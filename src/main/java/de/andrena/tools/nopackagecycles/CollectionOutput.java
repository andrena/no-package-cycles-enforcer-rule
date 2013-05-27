package de.andrena.tools.nopackagecycles;

import java.util.Collection;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CollectionOutput {

	public static <T> String joinCollection(Collection<T> collection, final StringProvider<T> stringProvider,
			String separator) {
		final StringBuilder output = new StringBuilder();
		joinCollection(collection, output, new Appender<T>() {
			public void append(T value) {
				output.append(stringProvider.provide(value));
			}
		}, separator);
		return output.toString();
	}

	public static <T> String joinArray(T[] array, StringProvider<T> stringProvider, String separator) {
		@SuppressWarnings("unchecked")
		List<T> list = Arrays.asList(array);
		return joinCollection(list, stringProvider, separator);
	}

	public static <T> void joinCollection(Collection<T> collection, StringBuilder output, Appender<T> appender,
			String separator) {
		boolean first = true;
		for (T element : collection) {
			if (!first) {
				output.append(separator);
			}
			appender.append(element);
			first = false;
		}
	}

	public interface StringProvider<T> {
		String provide(T value);
	}

	public interface Appender<T> {
		void append(T value);
	}
}
