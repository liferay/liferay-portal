/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.util;

import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class FaroPaginationUtilTest {

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

}