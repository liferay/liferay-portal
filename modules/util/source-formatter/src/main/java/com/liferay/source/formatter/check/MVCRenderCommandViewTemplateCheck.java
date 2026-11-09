/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class MVCRenderCommandViewTemplateCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		if (isExcludedPath(
				"mvc.render.command.view.template.excludes", absolutePath)) {

			return content;
		}

		int index = content.indexOf("\"mvc.command.name=/\"");

		if (index == -1) {
			return content;
		}

		Matcher classMatcher = _renderCommandClassPattern.matcher(content);

		if (!classMatcher.find()) {
			return content;
		}

		Matcher renderMatcher = _constantRenderPattern.matcher(content);

		if (!renderMatcher.find()) {
			return content;
		}

		String path = renderMatcher.group(1);

		addMessage(
			fileName,
			StringBundler.concat(
				"The MVC render command only maps \"/\" to \"", path,
				"\"; set \"jakarta.portlet.init-param.view-template=", path,
				"\" on the portlet and remove the command"),
			getLineNumber(content, index));

		return content;
	}

	private static final Pattern _constantRenderPattern = Pattern.compile(
		"\\bString render\\(\\s*RenderRequest \\w+,\\s*RenderResponse \\w+" +
			"\\)\\s*(?:throws PortletException\\s*)?\\{\\s*return " +
				"\"([^\"]*)\";\\s*\\}");
	private static final Pattern _renderCommandClassPattern = Pattern.compile(
		"\\bimplements\\b[^{]*\\bMVCRenderCommand\\b");

}