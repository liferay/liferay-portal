/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.portal.kernel.search.Sort;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class FaroPaginationUtilTest {

	@Test
	public void testToSortStringReturnsNullWhenFieldNameIsNull() {
		Assert.assertNull(
			FaroPaginationUtil.toSortString(
				new Sort[] {new Sort(null, Sort.STRING_TYPE, false)}));
	}

	@Test
	public void testToSortStringReturnsNullWhenSortsAreEmpty() {
		Assert.assertNull(FaroPaginationUtil.toSortString(null));
		Assert.assertNull(FaroPaginationUtil.toSortString(new Sort[0]));
	}

	@Test
	public void testToSortStringUsesDescForReverseSort() {
		Assert.assertEquals(
			"lastActivityDate:desc",
			FaroPaginationUtil.toSortString(
				new Sort[] {
					new Sort("lastActivityDate", Sort.STRING_TYPE, true)
				}));
	}

	@Test
	public void testToSortStringUsesFirstSortOnly() {
		Assert.assertEquals(
			"lastActivityDate:asc",
			FaroPaginationUtil.toSortString(
				new Sort[] {
					new Sort("lastActivityDate", Sort.STRING_TYPE, false),
					new Sort("accountName", Sort.STRING_TYPE, true)
				}));
	}

}