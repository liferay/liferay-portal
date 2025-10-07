/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupWrapper;
import com.liferay.portal.kernel.util.LocaleUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Eduardo García
 */
public class GroupNameComparatorTest {

	@Before
	public void setUp() {
		setUpGroups();
	}

	@Test
	public void testCompareLocalized() {
		GroupNameComparator groupNameComparator = new GroupNameComparator(
			true, LocaleUtil.SPAIN);

		int value = groupNameComparator.compare(_group1, _group2);

		Assert.assertTrue(value < 0);
	}

	protected void setUpGroups() {
		_group1 = new GroupWrapper(null) {

			@Override
			public String getName(String languageId) {
				Assert.assertEquals("es_ES", languageId);

				return "Área";
			}

		};

		_group2 = new GroupWrapper(null) {

			@Override
			public String getName(String languageId) {
				Assert.assertEquals("es_ES", languageId);

				return "Zona";
			}

		};
	}

	private Group _group1;
	private Group _group2;

}