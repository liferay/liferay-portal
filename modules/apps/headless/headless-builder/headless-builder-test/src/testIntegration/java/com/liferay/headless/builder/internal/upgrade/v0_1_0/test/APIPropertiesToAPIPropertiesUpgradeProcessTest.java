/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.upgrade.v0_1_0.test;

import com.liferay.headless.builder.test.BaseTestCase;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.apache.commons.lang.time.StopWatch;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Alejandro Tardín
 */
@FeatureFlag("LPS-178642")
public class APIPropertiesToAPIPropertiesUpgradeProcessTest
	extends BaseTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		_originalStopWatch = ReflectionTestUtil.getAndSetFieldValue(
			DBUpgrader.class, "_stopWatch", null);
	}

	@AfterClass
	public static void tearDownClass() {
		ReflectionTestUtil.setFieldValue(
			DBUpgrader.class, "_stopWatch", _originalStopWatch);
	}

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_API_PROPERTY", TestPropsValues.getCompanyId());

		_objectRelationshipLocalService.addObjectRelationship(
			"L_API_PROPERTIES_TO_API_PROPERTIES", TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			objectDefinition.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), true,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.headless.builder.internal.upgrade.v0_1_0." +
				"APIPropertiesToAPIPropertiesUpgradeProcess");

		String liferayMode = SystemProperties.get("liferay.mode");

		try {
			SystemProperties.clear("liferay.mode");

			StartupHelperUtil.setUpgrading(true);

			upgradeProcess.upgrade();
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);

			StartupHelperUtil.setUpgrading(false);
		}

		Assert.assertNull(
			_objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_API_PROPERTIES_TO_API_PROPERTIES",
					objectDefinition.getObjectDefinitionId()));
	}

	private static StopWatch _originalStopWatch;

	@Inject(
		filter = "component.name=com.liferay.headless.builder.internal.upgrade.registry.HeadlessBuilderUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}