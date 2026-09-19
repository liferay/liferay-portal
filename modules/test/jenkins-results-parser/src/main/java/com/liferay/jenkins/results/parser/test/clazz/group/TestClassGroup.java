/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Peter Yoo
 */
public interface TestClassGroup {

	public Job getJob();

	public List<File> getTestClassFiles();

	public List<TestClass> getTestClasses();

	public boolean hasTestClasses();

	public static enum GroupingStrategy {

		DEFAULT("default"),
		TEST_TASK_AVERAGE_DURATION("test_task_average_duration"),
		TEST_TASK_AVERAGE_TOTAL_DURATION("test_task_average_total_duration"),
		TEST_TASK_LONGEST_DURATION("test_task_longest_duration");

		public static GroupingStrategy getByString(String string) {
			return _groupingStrategies.get(string);
		}

		public static boolean isValid(String string) {
			return _groupingStrategies.containsKey(string);
		}

		@Override
		public String toString() {
			return _string;
		}

		private GroupingStrategy(String string) {
			_string = string;
		}

		private static final Map<String, GroupingStrategy> _groupingStrategies =
			new HashMap<>();

		static {
			for (GroupingStrategy groupingStrategy : values()) {
				_groupingStrategies.put(
					groupingStrategy.toString(), groupingStrategy);
			}
		}

		private final String _string;

	}

}