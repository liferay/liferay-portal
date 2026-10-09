/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class BNDBundleCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/app.bnd")) {
			return content;
		}

		List<String> allowedFileNames = getAttributeValues(
			_ALLOWED_FILE_NAMES_KEY, absolutePath);

		for (String allowedFileName : allowedFileNames) {
			if (absolutePath.endsWith(allowedFileName)) {
				return content;
			}
		}

		if (!content.matches(
				"(?s).*Liferay-Releng-App-Title: " +
					Pattern.quote("${liferay.releng.app.title.prefix}") +
						" \\S+.*")) {

			String appTitle = _getAppTitle(absolutePath);

			content = BNDSourceUtil.updateInstruction(
				content, "Liferay-Releng-App-Title",
				"${liferay.releng.app.title.prefix} " + appTitle);
		}

		content = BNDSourceUtil.updateInstruction(
			content, "Liferay-Releng-Public", "${liferay.releng.public}");

		String liferayRelengRestartRequired = BNDSourceUtil.getDefinitionValue(
			content, "Liferay-Releng-Restart-Required");

		if (StringUtil.equals(liferayRelengRestartRequired, "false")) {
			if (!_isHotDeployOSGiAppIncludes(
					BNDSourceUtil.getModuleName(absolutePath))) {

				addMessage(
					fileName,
					"The \"Liferay-Releng-Restart-Required\" can only be set " +
						"to false if a POSHI tests exists");

				return content;
			}
		}
		else {
			content = BNDSourceUtil.updateInstruction(
				content, "Liferay-Releng-Restart-Required", "true");
		}

		content = BNDSourceUtil.updateInstruction(
			content, "Liferay-Releng-Support-Url", "http://www.liferay.com");
		content = BNDSourceUtil.updateInstruction(
			content, "Liferay-Releng-Supported", "${liferay.releng.supported}");

		for (String instruction : _REQUIRED_INSTRUCTIONS) {
			if (content.contains(instruction + ":")) {
				continue;
			}

			content = StringBundler.concat(content, "\n", instruction, ":");
		}

		return content;
	}

	private String _getAppTitle(String absolutePath) {
		int pos = absolutePath.lastIndexOf(StringPool.SLASH);

		if (pos == -1) {
			return StringPool.BLANK;
		}

		String dirName = absolutePath.substring(0, pos);

		pos = dirName.lastIndexOf(StringPool.SLASH);

		if (pos == -1) {
			return StringPool.BLANK;
		}

		String shortDirName = dirName.substring(pos + 1);

		if (shortDirName.startsWith("com-liferay-")) {
			shortDirName = StringUtil.replaceFirst(
				shortDirName, "com-liferay-", StringPool.BLANK);
		}

		return TextFormatter.format(shortDirName, TextFormatter.J);
	}

	private boolean _isHotDeployOSGiAppIncludes(String moduleName)
		throws IOException {

		String testcaseDirLocation =
			"/portal-web/test/functional/com/liferay/portalweb/tests";

		File file = new File(getPortalDir() + testcaseDirLocation);

		if (!file.exists()) {
			return false;
		}

		List<String> testcaseFileNames = SourceFormatterUtil.scanForFileNames(
			getPortalDir() + testcaseDirLocation, new String[0],
			new String[] {"**/*.testcase"}, new SourceFormatterExcludes(),
			true);

		for (String testcaseFileName : testcaseFileNames) {
			String content = FileUtil.read(new File(testcaseFileName));

			if (content.contains(
					StringBundler.concat(
						"property hot.deploy.osgi.app.includes = \"",
						moduleName, "\""))) {

				return true;
			}
		}

		return false;
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

	private static final String[] _REQUIRED_INSTRUCTIONS = {
		"Liferay-Releng-App-Description", "Liferay-Releng-App-Title",
		"Liferay-Releng-Bundle", "Liferay-Releng-Category",
		"Liferay-Releng-Demo-Url", "Liferay-Releng-Deprecated",
		"Liferay-Releng-Fix-Delivery-Method", "Liferay-Releng-Labs",
		"Liferay-Releng-Marketplace", "Liferay-Releng-Portal-Required",
		"Liferay-Releng-Public", "Liferay-Releng-Restart-Required",
		"Liferay-Releng-Support-Url", "Liferay-Releng-Supported"
	};

}