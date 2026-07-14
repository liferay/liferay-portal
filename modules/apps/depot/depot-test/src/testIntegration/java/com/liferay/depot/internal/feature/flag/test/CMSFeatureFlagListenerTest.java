/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Breier
 */
@RunWith(Arquillian.class)
public class CMSFeatureFlagListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testOnValueRestoresTitlesWhenDisabled() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Locale locale = LocaleUtil.getDefault();

		Map<String, Map<Locale, String>> titleMaps = new HashMap<>();

		for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
			Role role = _roleLocalService.getRole(companyId, name);

			titleMaps.put(name, role.getTitleMap());
		}

		try {
			_featureFlagListener.onValue(companyId, "LPD-17564", false);

			Map<String, String> titleMap = new HashMap<>();

			for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
				Role role = _roleLocalService.getRole(companyId, name);

				titleMap.put(name, role.getTitle(locale));
			}

			_featureFlagListener.onValue(companyId, "LPD-17564", true);

			_assertTitle(
				LanguageUtil.get(locale, "depot-administrator"),
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR);
			_assertTitle(
				LanguageUtil.get(locale, "depot-member"),
				DepotRolesConstants.ASSET_LIBRARY_MEMBER);
			_assertTitle(
				LanguageUtil.get(locale, "depot-owner"),
				DepotRolesConstants.ASSET_LIBRARY_OWNER);

			_featureFlagListener.onValue(companyId, "LPD-17564", false);

			for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
				_assertTitle(name, titleMap.get(name));
			}
		}
		finally {
			for (String name : DepotRolesConstants.DEPOT_ROLE_NAMES) {
				Role role = _roleLocalService.getRole(companyId, name);

				role.setTitleMap(titleMaps.get(name));

				_roleLocalService.updateRole(role);
			}
		}
	}

	private void _assertTitle(String expectedTitle, String name)
		throws Exception {

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), name);

		Assert.assertEquals(
			expectedTitle, role.getTitle(LocaleUtil.getDefault()));
	}

	@Inject(
		filter = "component.name=com.liferay.depot.internal.feature.flag.CMSFeatureFlagListener"
	)
	private FeatureFlagListener _featureFlagListener;

	@Inject
	private RoleLocalService _roleLocalService;

}