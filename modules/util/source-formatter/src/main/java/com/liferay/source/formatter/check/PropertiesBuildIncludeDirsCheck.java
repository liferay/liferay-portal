/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class PropertiesBuildIncludeDirsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/build.properties")) {
			return content;
		}

		Matcher matcher = _pattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		StringBundler sb = new StringBundler();

		sb.append(matcher.group(1));
		sb.append("#build.include.dirs=\\");
		sb.append(StringPool.NEW_LINE);

		Set<String> buildIncludeDirs = _getBuildIncludeDirs(absolutePath);

		for (String buildIncludeDir : buildIncludeDirs) {
			sb.append(matcher.group(1));
			sb.append(StringPool.POUND);
			sb.append(StringPool.FOUR_SPACES);
			sb.append(buildIncludeDir);
			sb.append(",\\");
			sb.append(StringPool.NEW_LINE);
		}

		if (!buildIncludeDirs.isEmpty()) {
			sb.setIndex(sb.index() - 2);
		}

		return StringUtil.replaceFirst(content, matcher.group(), sb.toString());
	}

	private Set<String> _getBuildIncludeDirs(String absolutePath)
		throws IOException {

		Set<String> buildIncludeDirs = new TreeSet<>();

		File modulesDir = new File(getPortalDir(), "modules");

		List<String> buildExcludeModuleNames = getAttributeValues(
			_BUILD_EXCLUDE_MODULE_NAMES_KEY, absolutePath);

		List<String> skipModuleNames = ListUtil.concat(
			buildExcludeModuleNames,
			_getIgnoredModuleNames(SourceUtil.getRootDirName(absolutePath)));

		Files.walkFileTree(
			modulesDir.toPath(), EnumSet.noneOf(FileVisitOption.class), 15,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (ArrayUtil.contains(_SKIP_DIR_NAMES, dirName) ||
						dirName.contains("-test-")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					String moduleDirName = _getModuleDirName(
						dirPath, skipModuleNames);

					if (moduleDirName == null) {
						return FileVisitResult.CONTINUE;
					}

					if (buildIncludeDirs.contains(moduleDirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (Files.exists(dirPath.resolve(".lfrbuild-portal")) ||
						Files.exists(dirPath.resolve("ci-merge"))) {

						buildIncludeDirs.add(moduleDirName);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return buildIncludeDirs;
	}

	private List<String> _getIgnoredModuleNames(String rootDirName)
		throws IOException {

		List<String> ignoredModuleNames = new ArrayList<>();

		File file = new File(rootDirName + "/.gitignore");

		if (!file.exists()) {
			return ignoredModuleNames;
		}

		Matcher matcher = _ignoredModuleNamePattern.matcher(
			FileUtil.read(file));

		while (matcher.find()) {
			ignoredModuleNames.add(matcher.group());
		}

		return ignoredModuleNames;
	}

	private String _getModuleDirName(Path dirPath, List<String> skipModuleNames)
		throws IOException {

		String absolutePath = SourceUtil.getAbsolutePath(dirPath);

		int x = absolutePath.indexOf("/modules/");

		if (x == -1) {
			return null;
		}

		for (String skipModuleName : skipModuleNames) {
			if (absolutePath.contains(skipModuleName + "/") ||
				absolutePath.endsWith(skipModuleName)) {

				return null;
			}
		}

		String[] directoryNames = StringUtil.split(
			absolutePath.substring(x + 9), CharPool.SLASH);

		if (directoryNames.length < 2) {
			return null;
		}

		List<String> buildIncludeCategoryNames = getAttributeValues(
			_BUILD_INCLUDE_CATEGORY_NAMES_KEY, absolutePath);

		for (int i = 0; i < (directoryNames.length - 1); i++) {
			if (buildIncludeCategoryNames.contains(directoryNames[i])) {
				return StringUtil.merge(
					ArrayUtil.subset(directoryNames, 0, i + 2),
					StringPool.SLASH);
			}
		}

		return null;
	}

	private static final String _BUILD_EXCLUDE_MODULE_NAMES_KEY =
		"buildExcludeModuleNames";

	private static final String _BUILD_INCLUDE_CATEGORY_NAMES_KEY =
		"buildIncludeCategoryNames";

	private static final String[] _SKIP_DIR_NAMES = {
		".git", ".gradle", ".idea", ".m2", ".releng", ".settings", "bin",
		"build", "classes", "dependencies", "node_modules",
		"node_modules_cache", "private", "sql", "src", "test", "test-classes",
		"test-coverage", "test-results", "tmp"
	};

	private static final Pattern _ignoredModuleNamePattern = Pattern.compile(
		"^/modules/([^/\n]+/)*\\w+$", Pattern.MULTILINE);
	private static final Pattern _pattern = Pattern.compile(
		"([^\\S\\n]*)#build\\.include\\.dirs=\\\\(\\s*#    .*)*");

}