/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.organization.web.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.organization.constants.CommerceOrganizationPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
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
 * @author Fabio Monaco
 */
@RunWith(Arquillian.class)
public class CommerceOrganizationPortletPreferencesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = GroupTestUtil.addGroup();

		Organization organization = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		String defaultPreferences = StringBundler.concat(
			"<portlet-preferences><preference><name>rootOrganizationId",
			"</name><value>", organization.getOrganizationId(),
			"</value></preference></portlet-preferences>");

		PortletPreferences portletPreferences =
			_portletPreferencesLocalService.addPortletPreferences(
				group.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				CommerceOrganizationPortletKeys.COMMERCE_ORGANIZATION,
				_portletLocalService.fetchPortletById(
					group.getCompanyId(),
					CommerceOrganizationPortletKeys.COMMERCE_ORGANIZATION),
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
				"rootOrganizationExternalReferenceCode", null));
		Assert.assertEquals(
			organization.getExternalReferenceCode(),
			GetterUtil.getString(
				jxPortletPreferences.getValue(
					"rootOrganizationExternalReferenceCode", null)));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.commerce.organization.web.internal.upgrade.v1_0_1." +
			"CommerceOrganizationPortletPreferencesUpgradeProcess";

	@Inject
	private static OrganizationLocalService _organizationLocalService;

	@Inject
	private static PortletLocalService _portletLocalService;

	@Inject
	private static PortletPreferencesLocalService
		_portletPreferencesLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.commerce.organization.web.internal.upgrade.registry.CommerceOrganizationWebUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

}