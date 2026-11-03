/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Calum Ragan
 */
public class BuildFactoryTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testBuildURLMultiPattern() {
		MultiPattern multiPattern = ReflectionTestUtil.getFieldValue(
			BuildFactory.class, "_buildURLMultiPattern");

		Matcher matcher = multiPattern.find(
			JenkinsResultsParserUtil.combine(
				"https://test-1-1.liferay.com/job/",
				"test-portal-acceptance-downstream(master)",
				"/buildWithParameters"));

		Assert.assertNull(matcher.group("queryString"));
	}

}