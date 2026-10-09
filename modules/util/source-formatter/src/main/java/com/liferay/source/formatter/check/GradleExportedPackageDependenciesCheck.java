/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
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
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Shin
 */
public class GradleExportedPackageDependenciesCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.contains("/modules/apps/")) {
			return content;
		}

		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		String moduleName = absolutePath.substring(y + 1, x);

		if (!moduleName.contains("test")) {
			for (String dependencies : _getDependenciesBlocks(content)) {
				content = _formatDependencies(content, dependencies);
			}
		}

		return content;
	}

	private String _formatDependencies(String content, String dependencies)
		throws IOException {

		int x = dependencies.indexOf("\n");
		int y = dependencies.lastIndexOf("\n");

		if (x == y) {
			return content;
		}

		dependencies = dependencies.substring(x, y + 1);

		StringBundler sb = new StringBundler();

		for (String line : StringUtil.splitLines(dependencies)) {
			String dependencyName = _getDependencyName(line);

			if (Objects.isNull(dependencyName) ||
				line.matches(".*\\s+testImplementation\\s+.*")) {

				sb.append(line);
				sb.append("\n");
			}
			else {
				if (_isValidBundleSymbolicName(
						dependencyName, _getDependencyVersion(line))) {

					sb.append(line);
					sb.append("\n");
				}
			}
		}

		return StringUtil.replace(
			content, StringUtil.trim(dependencies),
			StringUtil.trim(sb.toString()));
	}

	private List<String> _getDependenciesBlocks(String content) {
		List<String> dependenciesBlocks = new ArrayList<>();

		Matcher matcher = _dependenciesPattern.matcher(content);

		while (matcher.find()) {
			int y = matcher.start();

			while (true) {
				y = content.indexOf("}", y + 1);

				if (y == -1) {
					return dependenciesBlocks;
				}

				String dependencies = content.substring(
					matcher.start(2), y + 1);

				int level = getLevel(dependencies, "{", "}");

				if (level == 0) {
					if (!dependencies.contains("}\n")) {
						dependenciesBlocks.add(dependencies);
					}

					break;
				}
			}
		}

		return dependenciesBlocks;
	}

	private String _getDependencyName(String dependency) {
		Matcher matcher = _dependencyNamePattern.matcher(dependency);

		if (!matcher.find()) {
			return StringPool.BLANK;
		}

		return matcher.group(1);
	}

	private String _getDependencyVersion(String dependency) {
		Matcher matcher = _dependencyVersionPattern.matcher(dependency);

		if (!matcher.find()) {
			return StringPool.BLANK;
		}

		return matcher.group(1);
	}

	private synchronized Map<String, String>
			_getEmptyExportPackageBundleSymbolicMap()
		throws IOException {

		if (_emptyExportPackageBundleSymbolicMap != null) {
			return _emptyExportPackageBundleSymbolicMap;
		}

		File portalDir = getPortalDir();

		if (portalDir == null) {
			_emptyExportPackageBundleSymbolicMap = Collections.emptyMap();

			return _emptyExportPackageBundleSymbolicMap;
		}

		final List<File> files = new ArrayList<>();

		Files.walkFileTree(
			portalDir.toPath(), EnumSet.noneOf(FileVisitOption.class), 15,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String dirName = String.valueOf(dirPath.getFileName());

					if (ArrayUtil.contains(_SKIP_DIR_NAMES, dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					Path path = dirPath.resolve("bnd.bnd");

					if (Files.exists(path)) {
						files.add(path.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		_emptyExportPackageBundleSymbolicMap = new HashMap<>();

		for (File file : files) {
			String content = FileUtil.read(file);

			if (content.contains("Export-Package:")) {
				continue;
			}

			String bundleSymbolicName = BNDSourceUtil.getDefinitionValue(
				content, "Bundle-SymbolicName");

			if ((bundleSymbolicName == null) ||
				!bundleSymbolicName.startsWith("com.liferay.") ||
				bundleSymbolicName.equals("com.liferay.ant.bnd")) {

				continue;
			}

			String bundleVersion = BNDSourceUtil.getDefinitionValue(
				content, "Bundle-Version");

			_emptyExportPackageBundleSymbolicMap.put(
				bundleSymbolicName, bundleVersion);
		}

		return _emptyExportPackageBundleSymbolicMap;
	}

	private boolean _isValidBundleSymbolicName(
			String dependencyName, String dependencyVersion)
		throws IOException {

		Map<String, String> emptyExportPackageBundleSymbolicMap =
			_getEmptyExportPackageBundleSymbolicMap();

		Set<String> emptyExportPackageBundleSymbolicNames =
			emptyExportPackageBundleSymbolicMap.keySet();

		if (!dependencyName.startsWith("com.liferay.") ||
			!emptyExportPackageBundleSymbolicNames.contains(dependencyName) ||
			!dependencyVersion.equals(
				emptyExportPackageBundleSymbolicMap.get(dependencyName))) {

			return true;
		}

		return false;
	}

	private static final String[] _SKIP_DIR_NAMES = {
		".git", ".gradle", ".idea", ".m2", ".settings", "bin", "build",
		"classes", "dependencies", "node_modules", "node_modules_cache", "sql",
		"src", "test", "test-classes", "test-coverage", "test-results", "tmp"
	};

	private static final Pattern _dependenciesPattern = Pattern.compile(
		"(\n|\\A)(\t*)dependencies \\{\n");
	private static final Pattern _dependencyNamePattern = Pattern.compile(
		".*, name: \"([^\"]*)\".*");
	private static final Pattern _dependencyVersionPattern = Pattern.compile(
		".*, version: \"([^\"]*)\".*");

	private Map<String, String> _emptyExportPackageBundleSymbolicMap;

}