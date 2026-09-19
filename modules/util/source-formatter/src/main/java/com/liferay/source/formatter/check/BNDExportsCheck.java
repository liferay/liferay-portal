/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.BNDSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class BNDExportsCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/bnd.bnd") ||
			absolutePath.contains("/third-party/")) {

			return content;
		}

		if (!absolutePath.contains("/testIntegration/")) {
			_checkExports(
				fileName, content, _exportContentsPattern, "-exportcontents");
			_checkExports(fileName, content, _exportsPattern, "Export-Package");
		}

		List<String> exportPackages = _getExportPackages(content);

		if (exportPackages.isEmpty()) {
			return content;
		}

		if (absolutePath.contains("/modules/apps/")) {
			_checkAllowedExportPackages(fileName, absolutePath, exportPackages);
		}

		_checkExportPackages(
			fileName, exportPackages, isModulesFile(absolutePath));

		return content;
	}

	private void _checkAllowedExportPackages(
		String fileName, String absolutePath, List<String> exportPackages) {

		if (fileName.endsWith("/test-bnd.bnd")) {
			return;
		}

		List<String> allowedExportPackageDirNames = getAttributeValues(
			_ALLOWED_EXPORT_PACKAGE_DIR_NAMES_KEY, absolutePath);

		for (String allowedExportPackageDirName :
				allowedExportPackageDirNames) {

			if (absolutePath.contains(allowedExportPackageDirName)) {
				return;
			}
		}

		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		String moduleName = absolutePath.substring(y + 1, x);

		if (moduleName.endsWith("-api") || moduleName.endsWith("-client") ||
			moduleName.endsWith("-spi") || moduleName.endsWith("-taglib") ||
			moduleName.contains("-taglib-") ||
			moduleName.endsWith("-test-util")) {

			return;
		}

		if (!moduleName.endsWith("-service")) {
			addMessage(
				fileName,
				"Exporting packages not allowed in module \"" + moduleName +
					"\"");

			return;
		}

		for (String exportPackage : exportPackages) {
			if (!exportPackage.endsWith(".http")) {
				addMessage(
					fileName,
					"Only allowed to export package \"*.http\" in module \"" +
						moduleName + "\"");

				return;
			}
		}
	}

	private void _checkExportPackage(
			String fileName, String srcDirLocation, String exportPackage,
			boolean modulesFile)
		throws IOException {

		String exportPackagePath = StringUtil.replace(
			exportPackage, CharPool.PERIOD, CharPool.SLASH);

		if (ArrayUtil.isNotEmpty(
				_getExportPackageResourcesFiles(
					srcDirLocation, exportPackagePath, modulesFile)) ||
			ArrayUtil.isNotEmpty(
				_getExportPackageSrcFiles(
					srcDirLocation, exportPackagePath, modulesFile))) {

			File packageinfoFile = null;

			if (modulesFile) {
				packageinfoFile = new File(
					StringBundler.concat(
						srcDirLocation, "main/resources/", exportPackagePath,
						"/packageinfo"));
			}
			else {
				packageinfoFile = new File(
					StringBundler.concat(
						srcDirLocation, exportPackagePath, "/packageinfo"));
			}

			if (!packageinfoFile.exists()) {
				addMessage(fileName, "Added packageinfo for " + exportPackage);

				FileUtil.write(packageinfoFile, "version 1.0.0");
			}
		}
		else if (exportPackage.startsWith("com.liferay.")) {
			int i = fileName.lastIndexOf("/");

			File importedFilesPropertiesFile = new File(
				fileName.substring(0, i) + "/imported-files.properties");

			if (!importedFilesPropertiesFile.exists()) {
				addMessage(
					fileName,
					"Unneeded/incorrect Export-Package: " + exportPackage);
			}
		}
	}

	private void _checkExportPackages(
			String fileName, List<String> exportPackages, boolean modulesFile)
		throws IOException {

		String srcDirLocation = _getSrcDirLocation(fileName, modulesFile);

		if (srcDirLocation == null) {
			return;
		}

		List<String> removedExportPackages = new ArrayList<>();

		for (String exportPackage : exportPackages) {
			if (exportPackage.startsWith(StringPool.EXCLAMATION)) {
				removedExportPackages.add(
					StringUtil.replace(
						exportPackage.substring(1), CharPool.PERIOD,
						CharPool.SLASH));
			}
			else if (exportPackage.endsWith(".*")) {
				_checkWildCardExportPackage(
					fileName, srcDirLocation, exportPackage,
					removedExportPackages);
			}
			else {
				_checkExportPackage(
					fileName, srcDirLocation, exportPackage, modulesFile);
			}
		}
	}

	private void _checkExports(
		String fileName, String content, Pattern pattern,
		String definitionKey) {

		String bundleSymbolicName = BNDSourceUtil.getDefinitionValue(
			content, "Bundle-SymbolicName");

		if ((bundleSymbolicName == null) ||
			bundleSymbolicName.startsWith(StringPool.DOLLAR) ||
			bundleSymbolicName.endsWith(".compat")) {

			return;
		}

		Matcher matcher = _apiOrServiceBundleSymbolicNamePattern.matcher(
			bundleSymbolicName);

		bundleSymbolicName = matcher.replaceAll(StringPool.BLANK);

		matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return;
		}

		String[] lines = StringUtil.splitLines(matcher.group(2));

		for (int i = 0; i < lines.length; i++) {
			String line = StringUtil.removeChar(
				StringUtil.trim(lines[i]), CharPool.BACK_SLASH);

			if (Validator.isNull(line) || !line.startsWith("com.liferay.") ||
				line.startsWith(bundleSymbolicName)) {

				continue;
			}

			if (bundleSymbolicName.endsWith(".test.util")) {
				String s = bundleSymbolicName.substring(
					0, bundleSymbolicName.length() - 10);

				if (line.startsWith(s + ".test.rule")) {
					continue;
				}
			}

			StringBundler sb = new StringBundler(6);

			sb.append(definitionKey);
			sb.append(" \"");
			sb.append(line);
			sb.append("\" should match Bundle-SymbolicName \"");
			sb.append(bundleSymbolicName);
			sb.append("\"");

			addMessage(
				fileName, sb.toString(),
				getLineNumber(content, matcher.start(2)) + i);
		}
	}

	private void _checkWildCardExportPackage(
			String fileName, String srcDirLocation,
			String wildCardExportPackage, List<String> removedExportPackages)
		throws IOException {

		String wildCardExportPackagePath = StringUtil.replace(
			wildCardExportPackage, CharPool.PERIOD, CharPool.SLASH);

		File exportPackageSrcDir = new File(
			StringBundler.concat(
				srcDirLocation, "/",
				StringUtil.removeSubstring(wildCardExportPackagePath, "/*")));

		if (!exportPackageSrcDir.exists()) {
			addMessage(
				fileName,
				"Unneeded/incorrect Export-Package: " + wildCardExportPackage);

			return;
		}

		Files.walkFileTree(
			exportPackageSrcDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					File dirFile = dirPath.toFile();

					File packageinfoFile = new File(dirFile, "packageinfo");

					if (packageinfoFile.exists()) {
						return FileVisitResult.CONTINUE;
					}

					File[] javaFiles = dirFile.listFiles(
						new FileFilter() {

							@Override
							public boolean accept(File file) {
								if (!file.isFile()) {
									return false;
								}

								String fileName = file.getName();

								return fileName.endsWith(".java");
							}

						});

					if (ArrayUtil.isEmpty(javaFiles)) {
						return FileVisitResult.CONTINUE;
					}

					String absolutePath = SourceUtil.getAbsolutePath(dirFile);

					for (String removedExportPackage : removedExportPackages) {
						if (!removedExportPackage.endsWith("/*")) {
							if (absolutePath.endsWith(removedExportPackage)) {
								return FileVisitResult.CONTINUE;
							}
						}
						else if (absolutePath.contains(
									StringUtil.removeSubstring(
										removedExportPackage, "/*"))) {

							return FileVisitResult.CONTINUE;
						}
					}

					addMessage(
						fileName, "Added packageinfo in " + absolutePath);

					FileUtil.write(packageinfoFile, "version 1.0.0");

					return FileVisitResult.CONTINUE;
				}

			});
	}

	private File[] _getExportPackageResourcesFiles(
		String srcDirLocation, String exportPackagePath, boolean modulesFile) {

		if (!modulesFile) {
			return new File[0];
		}

		File exportPackageResourcesDir = new File(
			StringBundler.concat(
				srcDirLocation, "/main/resources/", exportPackagePath));

		return exportPackageResourcesDir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					String fileName = file.getName();

					if (fileName.startsWith(".lfrbuild-") ||
						fileName.equals("packageinfo")) {

						return false;
					}

					return file.isFile();
				}

			});
	}

	private List<String> _getExportPackages(String content) {
		Matcher matcher = _exportsPattern.matcher(content);

		if (!matcher.find()) {
			return Collections.emptyList();
		}

		List<String> exportPackages = new ArrayList<>();

		for (String line : StringUtil.splitLines(matcher.group(3))) {
			line = StringUtil.trim(line);

			if (Validator.isNotNull(line) && !line.equals("\\") &&
				!line.contains(StringPool.SEMICOLON)) {

				exportPackages.add(StringUtil.removeSubstring(line, ",\\"));
			}
		}

		return exportPackages;
	}

	private File[] _getExportPackageSrcFiles(
		String srcDirLocation, String exportPackagePath, boolean modulesFile) {

		File exportPackageSrcDir = null;

		if (modulesFile) {
			exportPackageSrcDir = new File(
				StringBundler.concat(
					srcDirLocation, "/main/java/", exportPackagePath));
		}
		else {
			exportPackageSrcDir = new File(
				StringBundler.concat(srcDirLocation, "/", exportPackagePath));
		}

		return exportPackageSrcDir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.isFile();
				}

			});
	}

	private String _getSrcDirLocation(String fileName, boolean modulesFile) {
		int i = fileName.lastIndexOf("/");

		String srcDirLocation = fileName.substring(0, i) + "/src/";

		if (modulesFile) {
			File srcMainDir = new File(srcDirLocation + "main/");

			if (srcMainDir.exists()) {
				return srcDirLocation;
			}

			return null;
		}

		File comLiferayDir = new File(srcDirLocation + "com/liferay/");

		if (comLiferayDir.exists()) {
			return srcDirLocation;
		}

		return null;
	}

	private static final String _ALLOWED_EXPORT_PACKAGE_DIR_NAMES_KEY =
		"allowedExportPackageDirNames";

	private static final Pattern _apiOrServiceBundleSymbolicNamePattern =
		Pattern.compile("\\.(api|service)$");
	private static final Pattern _exportContentsPattern = Pattern.compile(
		"\n-exportcontents:(\\\\\n| )((.*?)(\n[^\t]|\\Z))",
		Pattern.DOTALL | Pattern.MULTILINE);
	private static final Pattern _exportsPattern = Pattern.compile(
		"\nExport-Package:(\\\\\n| )((.*?)(\n[^\t]|\\Z))",
		Pattern.DOTALL | Pattern.MULTILINE);

}