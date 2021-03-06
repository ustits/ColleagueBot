package ru.ustits.colleague;

import java.lang.Long;
import java.util.Arrays;

import static io.qala.datagen.RandomShortApi.*;
import static io.qala.datagen.RandomValue.length;

/**
 * @author ustits
 */
public final class RandomUtils {

  private static final int RANDOM_BOUND = 10;
  private static final int MAX_ELEMENTS = 100;

  private RandomUtils() {
  }

  public static String[] values() {
    return length(RANDOM_BOUND).alphanumerics(MAX_ELEMENTS).toArray(new String[]{});
  }

  public static String[] values(final int bound) {
    final String[] values = values();
    return Arrays.copyOf(values, bound);
  }

  public static String[] valuesMoreThan(final int from) {
    final int bound = aPositiveInt(RANDOM_BOUND);
    final int finalSize = from + bound;
    final String[] values = new String[finalSize];
    for (int i = 0; i < from + bound; i++) {
      values[i] = string();
    }
    return values;
  }

  public static String string() {
    return string(RANDOM_BOUND);
  }

  public static String string(final int length) {
    return alphanumeric(length);
  }

  public static Long aLong() {
    return positiveLong();
  }

  public static Integer anInt() {
    return integer();
  }

  public static Integer anInt(final int bound) {
    return anInt() % bound;
  }

  public static Integer aPositiveInt(final int bound) {
    if (bound == 0) {
      return 1;
    }
    final int number = anInt(bound);
    if (number == 0) {
      return 1;
    }
    return number < 0 ? -number : number;
  }

  public static Integer aPositiveInt() {
    return aPositiveInt(RANDOM_BOUND);
  }

}
