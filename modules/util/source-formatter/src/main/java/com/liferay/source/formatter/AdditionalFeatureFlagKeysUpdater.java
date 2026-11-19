/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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
public class AdditionalFeatureFlagKeysUpdater {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String privateDirName = ArgumentsUtil.getString(
			arguments, "private.dir", null);
		String sourceFormatterPropertiesFileName = ArgumentsUtil.getString(
			arguments, "source.formatter.properties.file", null);

		if ((privateDirName == null) ||
			(sourceFormatterPropertiesFileName == null)) {

			throw new IllegalArgumentException(
				"Arguments \"private.dir\" and " +
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
				StringBundler.concat(
					"Argument \"private.dir\" must point to a master-private ",
					"checkout outside ", portalDir.getCanonicalPath()));
		}

		List<String> fileNames = SourceFormatterUtil.filterFileNames(
			SourceFormatterUtil.scanForFileNames(
				privateDirName, PropertiesFeatureFlagsCheck.INCLUDES),
			PropertiesFeatureFlagsCheck.EXCLUDES,
			PropertiesFeatureFlagsCheck.INCLUDES, new SourceFormatterExcludes(),
			true);

		_updateAdditionalFeatureFlagKeys(
			PropertiesFeatureFlagsCheck.getFeatureFlagKeys(fileNames),
			sourceFormatterPropertiesFile);
	}

	private static void _updateAdditionalFeatureFlagKeys(
			List<String> additionalFeatureFlagKeys, File file)
		throws Exception {

		String content = FileUtil.read(file);

		Matcher matcher = _additionalFeatureFlagKeysPattern.matcher(content);

		if (!matcher.find()) {
			throw new IllegalStateException(
				"Unable to find additionalFeatureFlagKeys in " + file);
		}

		StringBundler sb = new StringBundler(
			(additionalFeatureFlagKeys.size() * 3) + 2);

		sb.append(content.substring(0, matcher.start(1)));

		for (String additionalFeatureFlagKey : additionalFeatureFlagKeys) {
			sb.append("\\\n        ");
			sb.append(additionalFeatureFlagKey);
			sb.append(StringPool.COMMA);
		}

		if (!additionalFeatureFlagKeys.isEmpty()) {
			sb.setIndex(sb.index() - 1);
		}

		sb.append(content.substring(matcher.end(1)));

		String newContent = sb.toString();

		if (!StringUtil.equals(content, newContent)) {
			FileUtil.write(file, newContent);
		}
	}

	private static final Pattern _additionalFeatureFlagKeysPattern =
		Pattern.compile(
			"source\\.check\\.PropertiesFeatureFlagsCheck\\." +
				"additionalFeatureFlagKeys=((\\\\\n +[^\\\\\n]+)*)");

}