/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.search.opensearch2.internal.legacy.sort;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.opensearch.client.opensearch._types.FieldSort;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;

/**
 * @author Rodrigo Guedes de Souza
 */
public class SortTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testEntryClassNameField() {
		List<SortOptions> sortOptionsList = _sortTranslator.translateSorts(
			new Sort[] {new Sort(Field.ENTRY_CLASS_NAME, false)});

		_assertSort(Field.ENTRY_CLASS_NAME, SortOrder.Asc, sortOptionsList);
	}

	@Test
	public void testNondefaultSortableFieldAsc() {
		List<SortOptions> sortOptionsList = _sortTranslator.translateSorts(
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, false)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SortOrder.Asc, sortOptionsList);
	}

	@Test
	public void testNondefaultSortableFieldDesc() {
		List<SortOptions> sortOptionsList = _sortTranslator.translateSorts(
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, true)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SortOrder.Desc, sortOptionsList);
	}

	@Test
	public void testPriorityField() {
		List<SortOptions> sortOptionsList = _sortTranslator.translateSorts(
			new Sort[] {new Sort(Field.PRIORITY, false)});

		_assertSort(Field.PRIORITY, SortOrder.Asc, sortOptionsList);
	}

	@Test
	public void testWithoutSorts() {
		List<SortOptions> sortOptionsList = _sortTranslator.translateSorts(
			new Sort[0]);

		Assert.assertTrue(sortOptionsList.isEmpty());
	}

	private void _assertSort(
		String expectedFieldName, SortOrder expectedSortOrder,
		List<SortOptions> sortOptionsList) {

		SortOptions sortOptions = sortOptionsList.get(0);

		FieldSort fieldSort = sortOptions.field();

		Assert.assertEquals(expectedFieldName, fieldSort.field());
		Assert.assertEquals(expectedSortOrder, fieldSort.order());
	}

	private final SortTranslator _sortTranslator = new SortTranslator();

}