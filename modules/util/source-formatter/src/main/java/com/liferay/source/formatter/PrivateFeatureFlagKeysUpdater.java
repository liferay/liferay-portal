/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.source.formatter.check.PropertiesFeatureFlagsCheck;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PrivateFeatureFlagKeysUpdater {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String privateDirName = ArgumentsUtil.getString(
			arguments, "liferay.portal.private.dir", null);
		String sourceFormatterPropertiesFileName = ArgumentsUtil.getString(
			arguments, "source.formatter.properties.file", null);

		if ((privateDirName == null) ||
			(sourceFormatterPropertiesFileName == null)) {

			throw new IllegalArgumentException(
				"Arguments \"liferay.portal.private.dir\" and " +
					"\"source.formatter.properties.file\" are required");
		}

		File sourceFormatterPropertiesFile = new File(
			sourceFormatterPropertiesFileName);

		File portalDir = sourceFormatterPropertiesFile.getParentFile();

		File privateDir = new File(privateDirName);

		String portalDirCanonicalPath =
			portalDir.getCanonicalPath() + File.separator;
		String privateDirCanonicalPath =
			privateDir.getCanonicalPath() + File.separator;

		if (privateDirCanonicalPath.startsWith(portalDirCanonicalPath)) {
			throw new IllegalArgumentException(
				"Argument \"liferay.portal.private.dir\" must point to a " +
					"master-private checkout outside " +
						portalDir.getCanonicalPath());
		}

		List<String> fileNames = SourceFormatterUtil.filterFileNames(
			SourceFormatterUtil.scanForFileNames(
				privateDirName, PropertiesFeatureFlagsCheck.INCLUDES),
			PropertiesFeatureFlagsCheck.EXCLUDES,
			PropertiesFeatureFlagsCheck.INCLUDES, new SourceFormatterExcludes(),
			true);

		List<String> privateFeatureFlagKeys =
			PropertiesFeatureFlagsCheck.getFeatureFlagKeys(fileNames);

		String content = FileUtil.read(sourceFormatterPropertiesFile);

		List<String> additionalFeatureFlagKeys = _getAdditionalFeatureFlagKeys(
			content);

		additionalFeatureFlagKeys.removeAll(privateFeatureFlagKeys);

		String newContent = _replaceFeatureFlagKeys(
			content, "additionalFeatureFlagKeys", additionalFeatureFlagKeys);

		newContent = _replaceFeatureFlagKeys(
			newContent, "privateFeatureFlagKeys", privateFeatureFlagKeys);

		if (!StringUtil.equals(content, newContent)) {
			FileUtil.write(sourceFormatterPropertiesFile, newContent);
		}
	}

	private static List<String> _getAdditionalFeatureFlagKeys(String content) {
		Matcher matcher = _getMatcher(content, "additionalFeatureFlagKeys");

		String featureFlagKeysString = matcher.group(1);

		return ListUtil.fromArray(
			StringUtil.split(
				featureFlagKeysString.replaceAll(
					"[\\\\\\s]", StringPool.BLANK)));
	}

	private static Matcher _getMatcher(String content, String attributeName) {
		Pattern pattern = Pattern.compile(
			"source\\.check\\.PropertiesFeatureFlagsCheck\\." + attributeName +
				"=((\\\\\n +[^\\\\\n]+)*)");

		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher;
		}

		throw new IllegalStateException(
			"Unable to find " + attributeName +
				" in \"source-formatter.properties\"");
	}

	private static String _replaceFeatureFlagKeys(
		String content, String attributeName, List<String> featureFlagKeys) {

		Matcher matcher = _getMatcher(content, attributeName);

		StringBundler sb = new StringBundler((featureFlagKeys.size() * 3) + 2);

		sb.append(content.substring(0, matcher.start(1)));

		for (String featureFlagKey : featureFlagKeys) {
			sb.append("\\\n        ");
			sb.append(featureFlagKey);
			sb.append(StringPool.COMMA);
		}

		if (!featureFlagKeys.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append(content.substring(matcher.end(1)));

		return sb.toString();
	}

}