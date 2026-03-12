/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManager;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class CompanyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeprecationFeatureFlags() throws Exception {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					CompanyConstants.SYSTEM,
					PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		PortalPreferences portalPreferences =
			portalPreferencesWrapper.getPortalPreferencesImpl();

		Assert.assertEquals(
			"true",
			portalPreferences.getValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE,
				FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED));

		Company company = CompanyTestUtil.addCompany();

		portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					company.getCompanyId(),
					PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		portalPreferences = portalPreferencesWrapper.getPortalPreferencesImpl();

		List<FeatureFlag> deprecationFeatureFlags =
			_featureFlagManager.getFeatureFlags(
				company.getCompanyId(),
				FeatureFlagType.DEPRECATION.getPredicate());

		for (FeatureFlag deprecationFeatureFlag : deprecationFeatureFlags) {
			Assert.assertEquals(
				"false",
				portalPreferences.getValue(
					FeatureFlagConstants.PREFERENCE_NAMESPACE,
					deprecationFeatureFlag.getKey()));
			Assert.assertFalse(deprecationFeatureFlag.isEnabled());
		}
	}

	@Inject
	private FeatureFlagManager _featureFlagManager;

	@Inject
	private PortalPreferencesLocalService _portalPreferencesLocalService;

}