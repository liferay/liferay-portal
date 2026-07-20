/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeListTypeType;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class UpgradeListTypeTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		for (String listTypeName : _listTypeNames) {
			_listTypeLocalService.addListType(
				CompanyThreadLocal.getCompanyId(), listTypeName,
				_OLD_LIST_TYPE_TYPE);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		try {
			_originalListType = _deleteListType(
				_listTypeNames.get(1), _NEW_LIST_TYPE_TYPE);

			UpgradeProcess upgradeProcess = new UpgradeListTypeType();

			upgradeProcess.upgrade();

			FinderCacheUtil.clearCache();

			for (String listTypeName : _listTypeNames) {
				ListType oldListType = _listTypeLocalService.fetchListType(
					CompanyThreadLocal.getCompanyId(), listTypeName,
					_OLD_LIST_TYPE_TYPE);

				Assert.assertNull(oldListType);

				ListType newListType = _listTypeLocalService.fetchListType(
					CompanyThreadLocal.getCompanyId(), listTypeName,
					_NEW_LIST_TYPE_TYPE);

				Assert.assertNotNull(newListType);
			}
		}
		finally {
			_deleteListType(_listTypeNames.get(1), _NEW_LIST_TYPE_TYPE);

			if (_originalListType != null) {
				_listTypeLocalService.addListType(_originalListType);
			}
		}
	}

	private ListType _deleteListType(String listTypeName, String listTypeType) {
		ListType listType = _listTypeLocalService.fetchListType(
			CompanyThreadLocal.getCompanyId(), listTypeName, listTypeType);

		if (listType != null) {
			_listTypeLocalService.deleteListType(listType);
		}

		return listType;
	}

	private static final String _NEW_LIST_TYPE_TYPE =
		"com.liferay.portal.kernel.model.Company.website";

	private static final String _OLD_LIST_TYPE_TYPE =
		"com.liferay.account.model.AccountEntry.address";

	private static final List<String> _listTypeNames = Arrays.asList(
		"intranet", "public");

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	private ListType _originalListType;

}