package ru.ustits.colleague.stats.commands;

import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import ru.ustits.colleague.repositories.StopWordRepository;
import ru.ustits.colleague.repositories.records.MessageRecord;
import ru.ustits.colleague.repositories.records.StopWordRecord;
import ru.ustits.colleague.services.MessageService;
import ru.ustits.colleague.stats.analysis.Cleanup;
import ru.ustits.colleague.stats.analysis.SimpleTokenizer;
import ru.ustits.colleague.stats.analysis.filters.MessageFilter;
import ru.ustits.colleague.stats.analysis.filters.NoUrlFilter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.toUnsignedLong;
import static ru.ustits.colleague.stats.analysis.filters.MessageFilter.MessageFilters.TWITTER;
import static ru.ustits.colleague.tools.ListUtils.count;
import static ru.ustits.colleague.tools.MapUtils.limit;
import static ru.ustits.colleague.tools.MapUtils.sortByValue;

/**
 * @author ustits
 */
@Log4j2
public final class WordStatsCmd extends StatsCommand {

  private static final int DEFAULT_STATS_LEN = 10;

  private final SimpleTokenizer tokenizer = new SimpleTokenizer();
  private final Cleanup cleanup = new Cleanup();
  private final StopWordRepository stopWordRepository;
  private final int statsLength;

  public WordStatsCmd(final String commandIdentifier, final MessageService service,
                      final StopWordRepository stopWordRepository) {
    this(commandIdentifier, service, stopWordRepository, DEFAULT_STATS_LEN);
  }

  public WordStatsCmd(final String commandIdentifier, final MessageService service,
                      final StopWordRepository stopWordRepository, final int statsLength) {
    super(commandIdentifier, service);
    this.stopWordRepository = stopWordRepository;
    this.statsLength = statsLength;
  }

  @Override
  public void execute(final AbsSender absSender, final User user, final Chat chat,
                      final String[] arguments) {
    final Long userId = toUnsignedLong(user.getId());
    final Long chatId = chat.getId();
    final List<MessageRecord> messages = getService().messagesForUser(userId, chatId);
    final List<String> tokens = tokenizer.tokenize(messages.stream()
            .map(MessageRecord::getText)
            .filter(MessageFilter.newInstance(TWITTER))
            .filter(new NoUrlFilter())
            .collect(Collectors.toList()));

    final List<StopWordRecord> stopWordRecords = Lists.newArrayList(stopWordRepository.findAll());
    final List<String> cleanedTokens;
    if (stopWordRecords.isEmpty()) {
      cleanedTokens = cleanup.clean(tokens);
    } else {
      final List<String> stopWords = stopWordRecords.stream().map(StopWordRecord::getWord)
              .collect(Collectors.toList());
      cleanedTokens = cleanup.clean(tokens, stopWords);
    }
    final int size = cleanedTokens.size();
    final Map<String, Integer> stats = limit(sortByValue(count(cleanedTokens)), statsLength);
    log.info("Mapped unique {} tokens", stats.size());
    final String text = buildText(stats, size);
    sendStats(text, chatId, absSender);
  }

  final String buildText(@NonNull final Map<String, Integer> stats, final int total) {
    if (stats.isEmpty()) {
      return NO_STAT_MESSAGE;
    }
    final StringBuilder builder = new StringBuilder();
    final DecimalFormat df = percentageFormat();
    stats.forEach((word, count) ->
            builder.append(word)
                    .append(": ")
                    .append(count)
                    .append(" (").append(df.format((float) count / total)).append(")")
                    .append("\n"));
    return builder.toString();
  }

  private DecimalFormat percentageFormat() {
    final DecimalFormat format = new DecimalFormat("#.##");
    final DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    format.setRoundingMode(RoundingMode.CEILING);
    format.setDecimalFormatSymbols(symbols);
    return format;
  }

}
