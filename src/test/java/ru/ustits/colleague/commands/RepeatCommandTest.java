package ru.ustits.colleague.commands;

import org.junit.Before;
import org.junit.Test;
import org.quartz.CronExpression;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.telegram.telegrambots.bots.AbsSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author ustits
 */
public class RepeatCommandTest {

  private static final String[] GOOD_ARGS = {"0-30", "1", "23", "?", "*", "1-5", "some", "text"};

  private RepeatCommand command;
  private Scheduler scheduler;

  @Before
  public void setUp() throws Exception {
    scheduler = mock(Scheduler.class);
    command = new RepeatCommand("random", scheduler);
  }

  @Test
  public void testBuildJob() throws Exception {
    final String text = "text";
    final AbsSender sender = mock(AbsSender.class);
    final Long chatId = 1L;
    final JobDetail job = command.buildJob(text, chatId, sender);
    assertThat(job.getJobDataMap())
            .containsEntry(RepeatCommand.MESSAGE_KEY, text)
            .containsEntry(RepeatCommand.SENDER_KEY, sender)
            .containsEntry(RepeatCommand.CHAT_KEY, chatId);
  }

  @Test
  public void testScheduleTask() throws Exception {
    final boolean result = command.scheduleTask(GOOD_ARGS, 1L, mock(AbsSender.class));
    assertThat(result).isTrue();
    verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
  }

  @Test
  public void testScheduleTaskWithNullArguments() throws Exception {
    final boolean result = command.scheduleTask(null, 1L, mock(AbsSender.class));
    assertThat(result).isFalse();
  }

  @Test
  public void testScheduleTaskWithNotEnoughArguments() throws Exception {
    final String[] arguments = {"one", "two"};
    final boolean result = command.scheduleTask(arguments, 1L, mock(AbsSender.class));
    assertThat(result).isFalse();
  }

  @Test
  public void testParseCron() throws Exception {
    final Optional<CronExpression> expression = command.parseCron(GOOD_ARGS);
    assertThat(expression).isPresent();
  }

  @Test
  public void testParseCronWithNotValidCron() throws Exception {
    final String[] arguments = GOOD_ARGS;
    arguments[0] = "error";
    final Optional<CronExpression> expression = command.parseCron(arguments);
    assertThat(expression).isNotPresent();
  }

  @Test
  public void testParseMessage() throws Exception {
    final Optional<String> text = command.parseMessage(GOOD_ARGS);
    assertThat(text).isPresent().contains("some text");
  }
}