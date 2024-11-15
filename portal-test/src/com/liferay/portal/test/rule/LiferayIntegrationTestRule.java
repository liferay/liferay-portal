/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.rule;

import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CompanyProviderClassTestRule;
import com.liferay.portal.kernel.test.rule.DataGuardTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRunMethodTestRule;
import com.liferay.portal.kernel.test.rule.PortalRunModeClassTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.rule.TimeoutTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.rules.TestRule;

/**
 * @author Shuyang Zhou
 */
public class LiferayIntegrationTestRule extends AggregateTestRule {

	public LiferayIntegrationTestRule() {
		super(false, _getTestRules());
	}

	private static TestRule[] _getTestRules() {
		List<TestRule> testRules = new ArrayList<>();

		testRules.add(DBPartitionTestRule.INSTANCE);

		if (System.getenv("JENKINS_HOME") != null) {
			testRules.add(TimeoutTestRule.INSTANCE);
		}

		testRules.add(FeatureFlagTestRule.INSTANCE);
		testRules.add(LanguageIdTestRule.INSTANCE);
		testRules.add(PortalRunModeClassTestRule.INSTANCE);
		testRules.add(SynchronousDestinationTestRule.INSTANCE);
		testRules.add(DataGuardTestRule.INSTANCE);
		testRules.add(LogAssertionTestRule.INSTANCE);
		testRules.add(SybaseDumpTransactionLogTestRule.INSTANCE);
		testRules.add(ClearThreadLocalClassTestRule.INSTANCE);
		testRules.add(UniqueStringRandomizerBumperClassTestRule.INSTANCE);
		testRules.add(CompanyProviderClassTestRule.INSTANCE);
		testRules.add(DeleteAfterTestRunMethodTestRule.INSTANCE);
		testRules.add(InjectTestRule.INSTANCE);

		return testRules.toArray(new TestRule[0]);
	}

}