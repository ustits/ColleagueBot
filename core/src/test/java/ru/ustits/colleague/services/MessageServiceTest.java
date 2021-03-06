package ru.ustits.colleague.services;

import org.junit.Before;
import org.junit.Test;
import ru.ustits.colleague.RepositoryTest;
import ru.ustits.colleague.repositories.ChatsRepository;
import ru.ustits.colleague.repositories.MessageRepository;
import ru.ustits.colleague.repositories.UserRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author ustits
 */
public class MessageServiceTest extends RepositoryTest {

  private MessageService service;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    service = new MessageService(sql, mock(MessageRepository.class), mock(ChatsRepository.class),
            mock(UserRepository.class));
  }

  @Test
  public void testCount() {
    final Map<String, Integer> result = service.count(1L, false);
    assertThat(result).containsOnlyKeys("name1", "name2", "name3").containsValues(1);
  }

}