/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;

import org.junit.rules.TestRule;

/**
 * @author Matthew Tambara
 */
public class LiferayUnitTestRule extends AggregateTestRule {

	public static final LiferayUnitTestRule INSTANCE =
		new LiferayUnitTestRule();

	public LiferayUnitTestRule() {
		super(false, _getTestRules());
	}

	private static TestRule[] _getTestRules() {
		return new TestRule[] {
			InitializeKernelUtilTestRule.INSTANCE, FeatureFlagTestRule.INSTANCE,
			AspectJNewEnvTestRule.INSTANCE, NewEnvTestRule.INSTANCE
		};
	}

}