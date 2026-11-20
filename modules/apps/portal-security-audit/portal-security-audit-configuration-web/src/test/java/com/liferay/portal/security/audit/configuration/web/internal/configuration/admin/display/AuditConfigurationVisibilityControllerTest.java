/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Christian Moura
 */
public class AuditConfigurationVisibilityControllerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@FeatureFlag("LPD-6417")
	@Test
	public void testIsVisible() {
		AuditConfigurationVisibilityController
			auditConfigurationVisibilityController =
				new AuditConfigurationVisibilityController();

		Assert.assertTrue(
			auditConfigurationVisibilityController.isVisible(
				ExtendedObjectClassDefinition.Scope.COMPANY,
				RandomTestUtil.randomLong()));
		Assert.assertTrue(
			auditConfigurationVisibilityController.isVisible(
				ExtendedObjectClassDefinition.Scope.SYSTEM,
				RandomTestUtil.randomLong()));

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			Assert.assertFalse(
				auditConfigurationVisibilityController.isVisible(
					ExtendedObjectClassDefinition.Scope.COMPANY,
					RandomTestUtil.randomLong()));
			Assert.assertTrue(
				auditConfigurationVisibilityController.isVisible(
					ExtendedObjectClassDefinition.Scope.SYSTEM,
					RandomTestUtil.randomLong()));
		}
	}

}