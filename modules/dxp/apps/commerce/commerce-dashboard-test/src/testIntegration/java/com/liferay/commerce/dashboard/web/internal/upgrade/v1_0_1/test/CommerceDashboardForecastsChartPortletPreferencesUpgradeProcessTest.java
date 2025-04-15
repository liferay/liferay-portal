/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.dashboard.web.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class
	CommerceDashboardForecastsChartPortletPreferencesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateCommerceDashboardForecastsChartPortletPreferencesExternalReferenceCode()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		String portletId =
			"com_liferay_commerce_dashboard_web_internal_portlet_" +
				"CommerceDashboardForecastsChartPortlet";

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				serviceContext.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			serviceContext.getUserId(), group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			serviceContext);

		String defaultPreferences = StringBundler.concat(
			"<portlet-preferences><preference><name>assetCategoryIds",
			"</name><value>", assetCategory.getCategoryId(),
			"</value></preference></portlet-preferences>");

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId,
				_portletLocalService.fetchPortletById(
					group.getCompanyId(), portletId),
				defaultPreferences);

		_runUpgrade();

		portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				portletPreferences.getPortletPreferencesId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			PortletPreferenceValueLocalServiceUtil.getPreferences(
				portletPreferences);

		Assert.assertNotNull(
			jxPortletPreferences.getValue(
				"assetCategoryExternalReferenceCodes", null));
		Assert.assertEquals(
			assetCategory.getExternalReferenceCode(),
			GetterUtil.getString(
				jxPortletPreferences.getValue(
					"assetCategoryExternalReferenceCodes", null)));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.dashboard.web.internal.upgrade.v1_0_1." +
			"CommerceDashboardForecastsChartPortletPreferencesUpgradeProcess";

	@Inject
	private static AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private static AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private static PortletLocalService _portletLocalService;

	@Inject
	private static PortletPreferencesLocalService
		_portletPreferencesLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.dashboard.web.internal.upgrade.registry.CommerceDashboardWebUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}