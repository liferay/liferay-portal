/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.v1_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portlet.PortalPreferencesWrapper;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class FeatureFlagUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDoUpgrade() throws Exception {
		PortalPreferences portalPreferences = _getPortalPreferences(
			CompanyConstants.SYSTEM);

		portalPreferences.setValue(
			_OLD_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED, "false");

		_portalPreferencesLocalService.updatePreferences(
			CompanyConstants.SYSTEM, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portalPreferences);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		portalPreferences = _getPortalPreferences(CompanyConstants.SYSTEM);

		Assert.assertNull(
			portalPreferences.getValue(
				_OLD_NAMESPACE,
				FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED));
		Assert.assertTrue(
			GetterUtil.getBoolean(
				portalPreferences.getValue(
					FeatureFlagConstants.PREFERENCE_NAMESPACE,
					FeatureFlagConstants.
						PREFERENCE_KEY_DEPRECATION_PROCESSED)));
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private static final String _CLASS_NAME =
		"com.liferay.feature.flag.web.internal.upgrade.v1_0_1." +
			"FeatureFlagUpgradeProcess";

	private static final String _OLD_NAMESPACE =
		FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG;

	@Inject(
		filter = "(&(component.name=com.liferay.feature.flag.web.internal.upgrade.registry.FeatureFlagUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}