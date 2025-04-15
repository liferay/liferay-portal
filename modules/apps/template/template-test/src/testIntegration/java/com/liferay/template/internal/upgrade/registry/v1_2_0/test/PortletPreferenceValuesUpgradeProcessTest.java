/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.internal.upgrade.registry.v1_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.navigation.language.constants.SiteNavigationLanguagePortletKeys;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class PortletPreferenceValuesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		String defaultPreferences = StringBundler.concat(
			"<portlet-preferences><preference><name>displayStyleGroupId",
			"</name><value>", _group.getGroupId(),
			"</value></preference></portlet-preferences>");

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				_group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE,
				_portletLocalService.fetchPortletById(
					_group.getCompanyId(),
					SiteNavigationLanguagePortletKeys.SITE_NAVIGATION_LANGUAGE),
				defaultPreferences);

		_runUpgrade();

		portletPreferences =
			_portletPreferencesLocalService.getPortletPreferences(
				portletPreferences.getPortletPreferencesId());

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			PortletPreferenceValueLocalServiceUtil.getPreferences(
				portletPreferences);

		Assert.assertNotNull(
			jxPortletPreferences.getValue("displayStyleGroupKey", null));
		Assert.assertEquals(
			_group.getGroupKey(),
			GetterUtil.getString(
				jxPortletPreferences.getValue("displayStyleGroupKey", null)));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 2, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.template.internal.upgrade.registry.TemplateEntryUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}