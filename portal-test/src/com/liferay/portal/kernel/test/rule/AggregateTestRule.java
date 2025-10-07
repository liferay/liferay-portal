/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.portal.test.rule.InitializeKernelUtilTestRule;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 */
public class AggregateTestRule implements TestRule {

	public AggregateTestRule(boolean sort, TestRule... testRules) {
		if (testRules == null) {
			throw new NullPointerException("Test rules is null");
		}

		if (testRules.length < 2) {
			throw new IllegalArgumentException(
				"Rule number " + testRules.length + " is less than 2");
		}

		_testRules = testRules;

		if (sort) {
			Arrays.sort(_testRules, _testRuleComparator);
		}
	}

	public AggregateTestRule(TestRule... testRules) {
		this(true, testRules);
	}

	@Override
	public Statement apply(Statement statement, Description description) {
		for (int i = _testRules.length - 1; i >= 0; i--) {
			TestRule testRule = _testRules[i];

			if (!_skippedTestRules.contains(testRule)) {
				statement = testRule.apply(statement, description);
			}
		}

		return statement;
	}

	public void skipTestRule(TestRule testRule) {
		_skippedTestRules.add(testRule);
	}

	private static final String[] _ORDERED_RULE_CLASS_NAMES = {
		TimeoutTestRule.class.getName(), HeapDumpTestRule.class.getName(),
		CodeCoverageAssertor.class.getName(), NewEnvTestRule.class.getName(),
		AssumeTestRule.class.getName(),
		"com.liferay.exportimport.test.rule.LazyReferencingTestRule",
		"com.liferay.portal.test.rule.LiferayIntegrationTestRule",
		LiferayUnitTestRule.class.getName(),
		"com.liferay.portal.test.rule.PersistenceTestRule",
		"com.liferay.portal.test.rule.TransactionalTestRule",
		SynchronousDestinationTestRule.class.getName(),
		"com.liferay.portal.test.rule.SynchronousMailTestRule",
		"com.liferay.document.library.webdav.test.rule." +
			"WebDAVEnvironmentConfigClassTestRule",
		"com.liferay.portal.test.rule.PermissionCheckerMethodTestRule",
		InitializeKernelUtilTestRule.class.getName(),
		"com.liferay.portal.search.test.rule.logging.ExpectedLogMethodTestRule",
		"com.liferay.portal.security.script.management.test.rule." +
			"ScriptManagementConfigurationTestRule"
	};

	private static final Comparator<TestRule> _testRuleComparator =
		new Comparator<TestRule>() {

			@Override
			public int compare(TestRule testRule1, TestRule testRule2) {
				return _getIndex(testRule1.getClass()) -
					_getIndex(testRule2.getClass());
			}

			private int _getIndex(Class<?> testRuleClass) {
				Set<String> testRuleClassNames = new HashSet<>();

				while (TestRule.class.isAssignableFrom(testRuleClass)) {
					testRuleClassNames.add(testRuleClass.getName());

					testRuleClass = testRuleClass.getSuperclass();
				}

				for (int i = 0; i < _ORDERED_RULE_CLASS_NAMES.length; i++) {
					if (testRuleClassNames.contains(
							_ORDERED_RULE_CLASS_NAMES[i])) {

						return i;
					}
				}

				throw new IllegalArgumentException(
					"Unknown test rule class : " + testRuleClass);
			}

		};

	private final Set<TestRule> _skippedTestRules = new HashSet<>();
	private final TestRule[] _testRules;

}