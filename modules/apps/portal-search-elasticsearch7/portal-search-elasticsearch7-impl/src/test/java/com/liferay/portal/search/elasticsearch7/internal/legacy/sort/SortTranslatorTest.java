/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.portal.search.elasticsearch7.internal.legacy.sort;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

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
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_sortTranslator.translate(
			searchSourceBuilder,
			new Sort[] {new Sort(Field.ENTRY_CLASS_NAME, false)});

		_assertSort(
			Field.ENTRY_CLASS_NAME, SortOrder.ASC, searchSourceBuilder.sorts());
	}

	@Test
	public void testNondefaultSortableFieldAsc() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_sortTranslator.translate(
			searchSourceBuilder,
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, false)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SortOrder.ASC,
			searchSourceBuilder.sorts());
	}

	@Test
	public void testNondefaultSortableFieldDesc() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_sortTranslator.translate(
			searchSourceBuilder,
			new Sort[] {new Sort(Field.ROLE_ID, Sort.LONG_TYPE, true)});

		_assertSort(
			Field.ROLE_ID + "_sortable", SortOrder.DESC,
			searchSourceBuilder.sorts());
	}

	@Test
	public void testPriorityField() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_sortTranslator.translate(
			searchSourceBuilder, new Sort[] {new Sort(Field.PRIORITY, false)});

		_assertSort(Field.PRIORITY, SortOrder.ASC, searchSourceBuilder.sorts());
	}

	@Test
	public void testWithoutSorts() {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		_sortTranslator.translate(searchSourceBuilder, new Sort[0]);

		Assert.assertNull(searchSourceBuilder.sorts());
	}

	private void _assertSort(
		String expectedFieldName, SortOrder expectedSortOrder,
		List<SortBuilder<?>> sortBuilders) {

		FieldSortBuilder fieldSortBuilder = (FieldSortBuilder)sortBuilders.get(
			0);

		Assert.assertEquals(expectedFieldName, fieldSortBuilder.getFieldName());
		Assert.assertEquals(expectedSortOrder, fieldSortBuilder.order());
	}

	private final SortTranslator _sortTranslator = new SortTranslator();

}