package ru.ustits.colleague.commands;

import org.junit.Before;
import org.junit.Test;
import ru.ustits.colleague.repositories.TriggerRepository;
import ru.ustits.colleague.repositories.records.TriggerRecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static ru.ustits.colleague.RandomUtils.*;

/**
 * @author ustits
 */
public class DeleteTriggerCommandTest {

  private DeleteTriggerCommand command;
  private TriggerRepository repository;

  @Before
  public void setUp() throws Exception {
    repository = mock(TriggerRepository.class);
    command = new DeleteTriggerCommand(string(), repository);
  }

  @Test
  public void testDeleteTrigger() throws Exception {
    when(repository.fetchOne(anyString(), anyLong(), anyLong()))
            .thenReturn(randomRecord());
    final boolean result = command.deleteTrigger(string(), aLong(), aLong());
    assertThat(result).isTrue();
    verify(repository).fetchOne(anyString(), anyLong(), anyLong());
    verify(repository).delete(any(TriggerRecord.class));
  }

  @Test
  public void testDeleteTriggerThatNotExists() throws Exception {
    when(repository.fetchOne(anyString(), anyLong(), anyLong()))
            .thenReturn(null);
    final boolean result = command.deleteTrigger(string(), aLong(), aLong());
    assertThat(result).isFalse();
    verify(repository).fetchOne(anyString(), anyLong(), anyLong());
    verify(repository, never()).delete(any(TriggerRecord.class));
  }

  private TriggerRecord randomRecord() {
    return new TriggerRecord(anInt(), string(), string(), aLong(), aLong());
  }
}