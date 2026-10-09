/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaSystemEventAnnotationCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (fileName.endsWith("PortletDataHandler.java")) {
			_checkSystemEventAnnotations(fileName, content);
		}

		return content;
	}

	private void _checkSystemEventAnnotations(String fileName, String content)
		throws IOException {

		int pos = content.indexOf("setDeletionSystemEventStagedModelTypes");

		if (pos == -1) {
			return;
		}

		String deletionSystemEventStagedModelTypes = content.substring(
			pos, content.indexOf(");", pos));

		Matcher matcher = _stagedModelTypesPattern.matcher(
			deletionSystemEventStagedModelTypes);

		while (matcher.find()) {
			String stagedModelTypeClassName = matcher.group(1);

			pos = stagedModelTypeClassName.indexOf(".class");

			if (pos == -1) {
				pos = stagedModelTypeClassName.indexOf("Constants");
			}

			if (pos == -1) {
				return;
			}

			String className = stagedModelTypeClassName.substring(0, pos);

			Pattern packageNamePattern = Pattern.compile(
				"import (com\\.liferay\\.[a-zA-Z\\.]*)\\.model\\." + className +
					";");

			Matcher packageNameMatcher = packageNamePattern.matcher(content);

			if (!packageNameMatcher.find()) {
				return;
			}

			StringBundler sb = new StringBundler(5);

			sb.append(fileName.substring(0, fileName.indexOf("/src/") + 5));
			sb.append(
				StringUtil.replace(
					packageNameMatcher.group(1), CharPool.PERIOD,
					CharPool.SLASH));
			sb.append("/service/impl/");
			sb.append(className);
			sb.append("LocalServiceImpl.java");

			String localServiceImplFileName = sb.toString();

			String localServiceImplContent = FileUtil.read(
				new File(localServiceImplFileName));

			if (localServiceImplContent == null) {
				return;
			}

			if (localServiceImplContent.contains("@SystemEvent")) {
				continue;
			}

			addMessage(
				fileName,
				"Missing deletion system event \"" + localServiceImplFileName +
					"\", see LPS-46632");
		}
	}

	private static final Pattern _stagedModelTypesPattern = Pattern.compile(
		"StagedModelType\\(([a-zA-Z.]*(class|getClassName[\\(\\)]*))\\)");

}