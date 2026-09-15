/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

/**
 * @author Michael Hashimoto
 */
public interface TestClassFileMethod {

	public String getFullName();

	public String getFullName(String separator);

	public TestClassFile getTestClassFile();

	public boolean matches(String name);

	public boolean matchesPattern(String name);

}