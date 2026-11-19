/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.entry;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Antonio Ortega
 */
public class SystemFDSEntryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetShowPagination() {
		SystemFDSEntry systemFDSEntry = Mockito.mock(
			SystemFDSEntry.class, Mockito.CALLS_REAL_METHODS);

		Assert.assertTrue(systemFDSEntry.getShowPagination());
	}

}