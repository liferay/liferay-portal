/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author André de Oliveira
 */
public class FieldValuesAssert {

	public static void assertFieldValue(
		String fieldName, Object fieldValue, SearchResponse searchResponse) {

		assertFieldValues(
			Collections.singletonMap(fieldName, fieldValue),
			Predicate.isEqual(fieldName), searchResponse);
	}

	public static void assertFieldValues(
		com.liferay.portal.kernel.search.Document document,
		Map<String, ?> expected, Predicate<String> keysPredicate,
		String message) {

		AssertUtils.assertEquals(
			message, _toStringValuesMap(expected),
			_filterOnKey(_toLegacyFieldValuesMap(document), keysPredicate));
	}

	public static void assertFieldValues(
		Map<String, ?> expected,
		com.liferay.portal.kernel.search.Document document, String message) {

		AssertUtils.assertEquals(
			message, _toStringValuesMap(expected),
			_toLegacyFieldValuesMap(document));
	}

	public static void assertFieldValues(
		Map<String, ?> expected, Predicate<String> keysPredicate,
		SearchResponse searchResponse) {

		Map<String, String> expectedFieldValuesMap = _toStringValuesMap(
			expected);

		List<Document> documents = searchResponse.getDocuments();

		if (documents.size() == 1) {
			Map<String, String> actualFieldValuesMap = _toFieldValuesMap(
				documents.get(0));

			Map<String, String> filteredFieldValuesMap = _filterOnKey(
				actualFieldValuesMap, keysPredicate);

			Map<String, Object> sourcesMap = _getSourcesMap(searchResponse);

			for (String key : filteredFieldValuesMap.keySet()) {
				if (!key.endsWith("_sortable") ||
					!sourcesMap.containsKey(key)) {

					continue;
				}

				filteredFieldValuesMap.put(
					key, _toObjectString(sourcesMap.get(key)));
			}

			AssertUtils.assertEquals(
				() -> StringBundler.concat(
					searchResponse.getRequestString(), "->",
					actualFieldValuesMap, "->", filteredFieldValuesMap),
				expectedFieldValuesMap, filteredFieldValuesMap);
		}
		else {
			AssertUtils.assertEquals(
				() -> StringBundler.concat(
					searchResponse.getRequestString(), "->", documents),
				expectedFieldValuesMap, documents);
		}
	}

	public static void assertFieldValues(
		Map<String, ?> expected, SearchResponse searchResponse) {

		assertFieldValues(expected, null, searchResponse);
	}

	public static void assertFieldValues(
		Map<String, ?> expected, String prefix,
		com.liferay.portal.kernel.search.Document document, String message) {

		AssertUtils.assertEquals(
			message, _toStringValuesMap(expected),
			_filterOnKey(
				_toLegacyFieldValuesMap(document),
				name -> name.startsWith(prefix)));
	}

	public static void assertFieldValues(
		String message, Document document, Map<?, ?> expected) {

		AssertUtils.assertEquals(
			message, expected, _toFieldValuesMap(document));
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static void assertFieldValues(
		String message, Document document, Predicate<String> keysPredicate,
		Map<?, ?> expected) {

		AssertUtils.assertEquals(
			message, expected,
			_filterOnKey(_toFieldValuesMap(document), keysPredicate));
	}

	private static void _addFields(
		Map<String, com.liferay.portal.kernel.search.Field> map,
		String parentKey,
		Collection<com.liferay.portal.kernel.search.Field> fields) {

		for (com.liferay.portal.kernel.search.Field field : fields) {
			String key = field.getName();

			if (Validator.isNull(key)) {
				_addFields(map, parentKey, field.getFields());

				continue;
			}

			if (parentKey != null) {
				key = StringBundler.concat(parentKey, StringPool.PERIOD, key);
			}

			if (field.getValues() != null) {
				map.put(key, field);
			}

			_addFields(map, key, field.getFields());
		}
	}

	private static Map<String, String> _filterOnKey(
		Map<String, String> map1, Predicate<String> predicate) {

		if (predicate == null) {
			return map1;
		}

		Map<String, String> map2 = new HashMap<>();

		for (Map.Entry<String, String> entry : map1.entrySet()) {
			if (!predicate.test(entry.getKey())) {
				continue;
			}

			map2.put(entry.getKey(), entry.getValue());
		}

		return map2;
	}

	private static Map<String, Object> _getSourcesMap(
		SearchResponse searchResponse) {

		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		SearchHit searchHit = searchHitsList.get(0);

		return searchHit.getSourcesMap();
	}

	private static <E> List<E> _sort(List<E> list) {
		ArrayList<E> sortedList = new ArrayList<>(list);

		try {
			Collections.sort(sortedList, null);
		}
		catch (ClassCastException | NullPointerException exception) {
			return list;
		}

		return sortedList;
	}

	private static String _toFieldString(Field field) {
		return _toListString(field.getValues());
	}

	private static Map<String, String> _toFieldValuesMap(Document document) {
		return _toStringValuesMap(
			document.getFields(), FieldValuesAssert::_toFieldString);
	}

	private static String _toLegacyFieldString(
		com.liferay.portal.kernel.search.Field field) {

		return _toListString(Arrays.asList(field.getValues()));
	}

	private static Map<String, String> _toLegacyFieldValuesMap(
		com.liferay.portal.kernel.search.Document document) {

		Map<String, com.liferay.portal.kernel.search.Field> map =
			new HashMap<>();

		Map<String, com.liferay.portal.kernel.search.Field> documentFields =
			document.getFields();

		_addFields(map, null, documentFields.values());

		return _toStringValuesMap(map, FieldValuesAssert::_toLegacyFieldString);
	}

	private static String _toListString(List<?> values) {
		if (values == null) {
			return null;
		}

		if (values.size() == 1) {
			return String.valueOf(values.get(0));
		}

		if (!values.isEmpty()) {
			return String.valueOf(_sort(values));
		}

		return "[]";
	}

	private static String _toObjectString(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof List) {
			return _toListString((List)value);
		}

		return String.valueOf(value);
	}

	private static Map<String, String> _toStringValuesMap(Map<String, ?> map) {
		return _toStringValuesMap(map, FieldValuesAssert::_toObjectString);
	}

	private static <T> Map<String, String> _toStringValuesMap(
		Map<String, T> map, Function<T, String> function) {

		Map<String, String> stringValues = new HashMap<>();

		for (Map.Entry<String, T> entry : map.entrySet()) {
			stringValues.put(entry.getKey(), function.apply(entry.getValue()));
		}

		return stringValues;
	}

}