package ru.ustits.colleague.repeats.commands;

import ru.ustits.colleague.repeats.tools.CronBuilder;

/**
 * @author ustits
 */
public final class WeekendsParser extends RepeatParserWrapper {

  private static final String WEEKENDS = "1,7";

  public WeekendsParser() {
    super(new DailyParser());
  }

  public WeekendsParser(final DailyParser innerStrategy) {
    super(innerStrategy);
  }

  @Override
  public String transformCron(final String cron) {
    final String processedCron = super.transformCron(cron);
    return CronBuilder.builder(processedCron)
            .withDayOfWeek(WEEKENDS)
            .build();
  }

}
