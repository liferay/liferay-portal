/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.job.property;

import java.nio.file.PathMatcher;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Hashimoto
 */
public interface GlobJobProperty extends JobProperty {

	public Map<String, List<String>> getGlobTestClassMethodNamesMap();

	public List<PathMatcher> getPathMatchers();

	public List<String> getRelativeGlobs();

}