/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.BalancedListSplitter;

/**
 * @author Peter Yoo
 * @author Michael Hashimoto
 */
public class TestClassBalancedListSplitter
	extends BalancedListSplitter<TestClass> {

	public TestClassBalancedListSplitter(long maxListWeight) {
		super(maxListWeight);
	}

}