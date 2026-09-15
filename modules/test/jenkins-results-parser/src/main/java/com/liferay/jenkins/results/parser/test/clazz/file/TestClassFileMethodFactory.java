/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.util.List;

/**
 * @author Michael Hashimoto
 */
public class TestClassFileMethodFactory {

	public static TestClassFileMethod newTestClassFileMethod(
		String name, List<String> parentNames, TestClassFile testClassFile) {

		if (testClassFile instanceof JSTestClassFile) {
			return new JSTestClassFileMethod(name, parentNames, testClassFile);
		}

		return null;
	}

}