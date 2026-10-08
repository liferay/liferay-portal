/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.upgrade.v5_0_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tejesh Boggavarapu
 */
@RunWith(Arquillian.class)
public class DeleteStaleOAuth2ScopeGrantsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)));

		_objectDefinitions.add(objectDefinition);

		long companyId = TestPropsValues.getCompanyId();
		String deletedObjectDefinitionApplicationName =
			"c_" + StringUtil.toLowerCase(RandomTestUtil.randomString());

		OAuth2ScopeGrant oAuth2ScopeGrant1 = _addOAuth2ScopeGrant(
			companyId, objectDefinition.getOSGiJaxRsName(),
			"com.liferay.object.rest.impl");
		OAuth2ScopeGrant oAuth2ScopeGrant2 = _addOAuth2ScopeGrant(
			companyId, deletedObjectDefinitionApplicationName,
			"com.liferay.object.rest.impl");
		OAuth2ScopeGrant oAuth2ScopeGrant3 = _addOAuth2ScopeGrant(
			companyId, deletedObjectDefinitionApplicationName,
			RandomTestUtil.randomString());
		OAuth2ScopeGrant oAuth2ScopeGrant4 = _addOAuth2ScopeGrant(
			RandomTestUtil.randomLong(), objectDefinition.getOSGiJaxRsName(),
			"com.liferay.object.rest.impl");

		long oAuth2AuthorizationId = RandomTestUtil.randomLong();

		_oAuth2ScopeGrantLocalService.addOAuth2AuthorizationOAuth2ScopeGrant(
			oAuth2AuthorizationId, oAuth2ScopeGrant1.getOAuth2ScopeGrantId());
		_oAuth2ScopeGrantLocalService.addOAuth2AuthorizationOAuth2ScopeGrant(
			oAuth2AuthorizationId, oAuth2ScopeGrant2.getOAuth2ScopeGrantId());

		_runUpgrade();

		Assert.assertNotNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				oAuth2ScopeGrant1.getOAuth2ScopeGrantId()));
		Assert.assertNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				oAuth2ScopeGrant2.getOAuth2ScopeGrantId()));
		Assert.assertNotNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				oAuth2ScopeGrant3.getOAuth2ScopeGrantId()));
		Assert.assertNull(
			_oAuth2ScopeGrantLocalService.fetchOAuth2ScopeGrant(
				oAuth2ScopeGrant4.getOAuth2ScopeGrantId()));

		Assert.assertArrayEquals(
			new long[] {oAuth2AuthorizationId},
			_oAuth2ScopeGrantLocalService.getOAuth2AuthorizationPrimaryKeys(
				oAuth2ScopeGrant1.getOAuth2ScopeGrantId()));
		Assert.assertArrayEquals(
			new long[0],
			_oAuth2ScopeGrantLocalService.getOAuth2AuthorizationPrimaryKeys(
				oAuth2ScopeGrant2.getOAuth2ScopeGrantId()));
	}

	private OAuth2ScopeGrant _addOAuth2ScopeGrant(
			long companyId, String applicationName, String bundleSymbolicName)
		throws Exception {

		OAuth2ScopeGrant oAuth2ScopeGrant =
			_oAuth2ScopeGrantLocalService.createOAuth2ScopeGrant(
				companyId, RandomTestUtil.randomLong(), applicationName,
				bundleSymbolicName, RandomTestUtil.randomString(),
				List.of(RandomTestUtil.randomString()));

		_oAuth2ScopeGrants.add(oAuth2ScopeGrant);

		return oAuth2ScopeGrant;
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(5, 0, 1));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		CacheRegistryUtil.clear();
	}

	@Inject
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@DeleteAfterTestRun
	private final List<OAuth2ScopeGrant> _oAuth2ScopeGrants = new ArrayList<>();

	@DeleteAfterTestRun
	private final List<ObjectDefinition> _objectDefinitions = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.oauth2.provider.internal.upgrade.registry.OAuth2ServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}