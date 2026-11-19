/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.osb.faro.engine.client.util.OrderByField;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class FaroPaginationUtilTest {

	@Test
	public void testPaginate() {
		List<String> items = List.of(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertEquals(
			Collections.emptyList(),
			FaroPaginationUtil.paginate(items, Pagination.of(2, 5)));
		Assert.assertEquals(
			items.subList(2, 4),
			FaroPaginationUtil.paginate(items, Pagination.of(2, 2)));
		Assert.assertSame(items, FaroPaginationUtil.paginate(items, null));
	}

	@Test
	public void testToOrderByFields() {
		Assert.assertNull(FaroPaginationUtil.toOrderByFields(null));
		Assert.assertNull(
			FaroPaginationUtil.toOrderByFields(new Sort[0], true));

		String fieldName1 = RandomTestUtil.randomString();
		String fieldName2 = RandomTestUtil.randomString();

		Sort[] sorts = {
			new Sort(fieldName1, Sort.STRING_TYPE, true),
			new Sort(null, Sort.STRING_TYPE, false),
			new Sort(fieldName2, Sort.STRING_TYPE, false)
		};

		List<OrderByField> orderByFields = FaroPaginationUtil.toOrderByFields(
			sorts);

		Assert.assertEquals(orderByFields.toString(), 2, orderByFields.size());

		_assertOrderByField(
			fieldName1, OrderByField.OrderBy.desc, orderByFields.get(0), false);
		_assertOrderByField(
			fieldName2, OrderByField.OrderBy.asc, orderByFields.get(1), false);

		orderByFields = FaroPaginationUtil.toOrderByFields(sorts, true);

		Assert.assertEquals(orderByFields.toString(), 2, orderByFields.size());

		_assertOrderByField(
			fieldName1, OrderByField.OrderBy.desc, orderByFields.get(0), true);
		_assertOrderByField(
			fieldName2, OrderByField.OrderBy.asc, orderByFields.get(1), true);
	}

	@Test
	public void testToSortString() {
		Assert.assertNull(FaroPaginationUtil.toSortString(null));
		Assert.assertNull(FaroPaginationUtil.toSortString(new Sort[0]));
		Assert.assertNull(
			FaroPaginationUtil.toSortString(
				new Sort[] {new Sort(null, Sort.STRING_TYPE, false)}));

		String fieldName = RandomTestUtil.randomString();

		Assert.assertEquals(
			fieldName + ":desc",
			FaroPaginationUtil.toSortString(
				new Sort[] {new Sort(fieldName, Sort.STRING_TYPE, true)}));
		Assert.assertEquals(
			fieldName + ":asc",
			FaroPaginationUtil.toSortString(
				new Sort[] {
					new Sort(fieldName, Sort.STRING_TYPE, false),
					new Sort(
						RandomTestUtil.randomString(), Sort.STRING_TYPE, true)
				}));
	}

	private void _assertOrderByField(
		String fieldName, OrderByField.OrderBy orderBy,
		OrderByField orderByField, boolean system) {

		Assert.assertEquals(fieldName, orderByField.getFieldName());
		Assert.assertEquals(orderBy, orderByField.getOrderBy());
		Assert.assertEquals(system, orderByField.isSystem());
	}

}