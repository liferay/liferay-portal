/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.theme;

import com.liferay.petra.string.StringBundler;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ThemeUtilTest {

	@Test
	public void testReplaceBodyContent() {
		String bodyContent = "<div class=\"preview\">Preview</div>";
		String bodyTag = "<body class=\"controls-visible signed-in\">";
		String head =
			"<!DOCTYPE html>\n\n<html dir=\"ltr\" lang=\"en-US\">\n\n<head>\n" +
				"\t<title>Home</title>\n</head>\n\n";

		Assert.assertEquals(
			StringBundler.concat(
				head, bodyTag, bodyContent, "</body>\n\n</html>"),
			ThemeUtil.replaceBodyContent(
				StringBundler.concat(
					head, bodyTag, "\n\n<div id=\"wrapper\"></div>\n\n",
					"</body>\n\n</html>"),
				bodyContent));

		Assert.assertEquals(
			bodyContent,
			ThemeUtil.replaceBodyContent(
				"<div id=\"wrapper\"></div>", bodyContent));
	}

}