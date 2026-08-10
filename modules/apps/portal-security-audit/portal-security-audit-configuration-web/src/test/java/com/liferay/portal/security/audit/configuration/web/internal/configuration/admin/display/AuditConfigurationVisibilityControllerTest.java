/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.configuration.web.internal.configuration.admin.display;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
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
		_testIsVisible(false, ExtendedObjectClassDefinition.Scope.COMPANY);
		_testIsVisible(false, ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	@FeatureFlag(enable = false, value = "LPD-6417")
	@Test
	public void testIsVisibleWhenFeatureFlagIsDisabled() {
		_testIsVisible(false, ExtendedObjectClassDefinition.Scope.COMPANY);
		_testIsVisible(true, ExtendedObjectClassDefinition.Scope.SYSTEM);
	}

	private void _testIsVisible(
		boolean expectedVisible, ExtendedObjectClassDefinition.Scope scope) {

		AuditConfigurationVisibilityController
			auditConfigurationVisibilityController =
				new AuditConfigurationVisibilityController();

		Assert.assertEquals(
			expectedVisible,
			auditConfigurationVisibilityController.isVisible(
				scope, RandomTestUtil.randomLong()));
	}

}