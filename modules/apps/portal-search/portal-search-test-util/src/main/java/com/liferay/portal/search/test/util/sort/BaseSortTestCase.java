/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.sort;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactory;
import com.liferay.portal.search.internal.SortFactoryImpl;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelper;
import com.liferay.portal.search.test.util.indexing.DocumentCreationHelpers;

import java.util.Date;
import java.util.function.Function;

import org.junit.Test;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public abstract class BaseSortTestCase extends BaseIndexingTestCase {

	@Test
	public void testDefaultSorts() throws Exception {
		addDocuments(
			value -> document -> {
				document.addDate(
					Field.MODIFIED_DATE, new Date(value.longValue()));
				document.addNumber(Field.PRIORITY, value);
			},
			new double[] {1, 2, 3});

		SortFactory sortFactory = new SortFactoryImpl();

		assertOrder(
			sortFactory.getDefaultSorts(), Field.PRIORITY, "[3.0, 2.0, 1.0]");
	}

	@Test
	public void testPriorityField() throws Exception {
		testDoubleField(Field.PRIORITY);
	}

	@Test
	public void testPriorityFieldSortable() throws Exception {
		testDoubleFieldSortable(Field.PRIORITY);
	}

	@Test
	public void testScore() throws Exception {
		String fieldName = "testField";

		addDocuments(
			value -> DocumentCreationHelpers.singleText(fieldName, value),
			"alpha", "charlie");

		Query query = getScoredQuery(fieldName, "charlie");

		assertOrder(
			getScoreSortArray(Sort.CUSTOM_TYPE, false), fieldName,
			"[charlie, alpha]", query);
		assertOrder(
			getScoreSortArray(Sort.CUSTOM_TYPE, true), fieldName,
			"[alpha, charlie]", query);
		assertOrder(
			getScoreSortArray(Sort.SCORE_TYPE, false), fieldName,
			"[charlie, alpha]", query);
		assertOrder(
			getScoreSortArray(Sort.SCORE_TYPE, true), fieldName,
			"[alpha, charlie]", query);
	}

	protected void assertOrder(
		Sort[] sorts, String fieldName, String expected) {

		assertOrder(sorts, fieldName, expected, null);
	}

	protected void assertOrder(
		Sort[] sorts, String fieldName, String expected, Query query) {

		assertSearch(
			indexingTestHelper -> {
				indexingTestHelper.define(
					searchContext -> searchContext.setSorts(sorts));

				if (query != null) {
					indexingTestHelper.setQuery(query);
				}

				indexingTestHelper.search();

				indexingTestHelper.verify(
					hits -> DocumentsAssert.assertValues(
						indexingTestHelper.getRequestString(), hits.getDocs(),
						fieldName, expected));
			});
	}

	protected void assertOrder(
		String fieldName, int sortType, boolean reverse, String expected) {

		assertOrder(
			new Sort[] {new Sort(fieldName, sortType, reverse)}, fieldName,
			expected);
	}

	protected abstract String getScoreParameter();

	protected Sort[] getScoreSortArray(int type, boolean reverse) {
		return new Sort[] {new Sort(getScoreParameter(), type, reverse)};
	}

	protected Query getScoredQuery(String fieldName, String fieldValue) {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.addExactTerm(fieldName, fieldValue);

		booleanQuery.add(getDefaultQuery(), BooleanClauseOccur.SHOULD);

		return booleanQuery;
	}

	protected void testDoubleField(String fieldName) throws Exception {
		testDoubleField(
			fieldName,
			value -> DocumentCreationHelpers.singleNumber(fieldName, value));
	}

	protected void testDoubleField(
			String fieldName, Function<Double, DocumentCreationHelper> function)
		throws Exception {

		addDocuments(function, new double[] {10, 1, 40, 5.3});

		assertOrder(
			fieldName, Sort.DOUBLE_TYPE, false, "[1.0, 5.3, 10.0, 40.0]");
		assertOrder(
			fieldName, Sort.DOUBLE_TYPE, true, "[40.0, 10.0, 5.3, 1.0]");
	}

	protected void testDoubleFieldSortable(String fieldName) throws Exception {
		testDoubleField(
			fieldName,
			value -> DocumentCreationHelpers.singleNumberSortable(
				fieldName, value));
	}

}