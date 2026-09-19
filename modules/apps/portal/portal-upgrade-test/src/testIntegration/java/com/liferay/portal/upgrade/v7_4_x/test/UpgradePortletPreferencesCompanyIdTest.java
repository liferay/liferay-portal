/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CompanyProviderClassTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradePortletPreferencesCompanyId;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesCompanyIdTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule() {
			{
				skipTestRule(CompanyProviderClassTestRule.INSTANCE);
			}
		};

	@Before
	public void setUp() throws Exception {
		_testGroup = GroupTestUtil.addGroup();

		_testLayout = LayoutTestUtil.addTypePortletLayout(_testGroup);
	}

	@Test
	public void testUpgrade() throws Exception {
		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				CompanyConstants.SYSTEM, PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, _testLayout.getPlid(),
				"test", null,
				"<portlet-preferences><preference><name>testName</name>" +
					"<value>testValue1</value><value>testValue2</value>" +
						"</preference></portlet-preferences>");

		Assert.assertEquals(
			CompanyConstants.SYSTEM, portletPreferences.getCompanyId());

		List<PortletPreferenceValue> portletPreferenceValues =
			_getPortletPreferenceValues(
				portletPreferences.getPortletPreferencesId());

		assertCompanyIds(CompanyConstants.SYSTEM, portletPreferenceValues);

		UpgradeProcess upgradeProcess =
			new UpgradePortletPreferencesCompanyId();

		upgradeProcess.upgrade();

		CacheRegistryUtil.clear();

		portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				portletPreferences.getPortletPreferencesId());

		Assert.assertEquals(
			PortalUtil.getDefaultCompanyId(),
			portletPreferences.getCompanyId());

		portletPreferenceValues = _getPortletPreferenceValues(
			portletPreferences.getPortletPreferencesId());

		assertCompanyIds(
			PortalUtil.getDefaultCompanyId(), portletPreferenceValues);
	}

	protected void assertCompanyIds(
		long companyId, List<PortletPreferenceValue> portletPreferenceValues) {

		Assert.assertNotEquals(0, portletPreferenceValues.size());

		for (PortletPreferenceValue portletPreferenceValue :
				portletPreferenceValues) {

			Assert.assertEquals(
				companyId, portletPreferenceValue.getCompanyId());
		}
	}

	private List<PortletPreferenceValue> _getPortletPreferenceValues(
		long portletPreferencesId) {

		DynamicQuery dynamicQuery =
			_portletPreferenceValueLocalService.dynamicQuery();

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"portletPreferencesId", portletPreferencesId));

		return _portletPreferenceValueLocalService.dynamicQuery(dynamicQuery);
	}

	@Inject
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@DeleteAfterTestRun
	private Group _testGroup;

	@DeleteAfterTestRun
	private Layout _testLayout;

}