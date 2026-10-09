/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author Alan Huang
 */
public class PropertiesArchivedModulesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!fileName.endsWith("/test.properties")) {
			return content;
		}

		List<String> archivedModuleDirectoryNames =
			_getArchivedModuleDirectoryNames();

		if (archivedModuleDirectoryNames.isEmpty()) {
			return content;
		}

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (!key.contains("test.batch.class.names.includes")) {
				continue;
			}

			String value = properties.getProperty(key);

			if (Validator.isNull(value)) {
				continue;
			}

			List<String> propertyValues = ListUtil.fromString(
				value, StringPool.COMMA);

			for (String propertyValue : propertyValues) {
				String moduleName = StringPool.BLANK;

				if (propertyValue.startsWith("**/")) {
					int x = propertyValue.indexOf(CharPool.SLASH, 3);

					if (x == -1) {
						continue;
					}

					moduleName = propertyValue.substring(3, x);
				}
				else if (propertyValue.startsWith("apps/archived/")) {
					moduleName = propertyValue.replaceFirst(
						"apps/archived/(.+?)/.*", "$1");
				}

				if (!archivedModuleDirectoryNames.contains(moduleName)) {
					continue;
				}

				addMessage(
					fileName,
					StringBundler.concat(
						"Remove dependency \"", moduleName, "\" in property \"",
						key, "\", since it points to an archived module"));
			}
		}

		return content;
	}

	private synchronized List<String> _getArchivedModuleDirectoryNames()
		throws IOException {

		if (_archivedModuleDirectoryNames != null) {
			return _archivedModuleDirectoryNames;
		}

		_archivedModuleDirectoryNames = new ArrayList<>();

		File modulesDir = new File(getPortalDir(), "modules/apps/archived");

		if (!modulesDir.exists()) {
			return Collections.emptyList();
		}

		Files.walkFileTree(
			modulesDir.toPath(), EnumSet.noneOf(FileVisitOption.class), 15,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
					Path dirPath, BasicFileAttributes basicFileAttributes) {

					String moduleDirectoryName = _getModuleDirectoryName(
						dirPath);

					if (moduleDirectoryName == null) {
						return FileVisitResult.CONTINUE;
					}

					_archivedModuleDirectoryNames.add(moduleDirectoryName);

					return FileVisitResult.SKIP_SUBTREE;
				}

			});

		return _archivedModuleDirectoryNames;
	}

	private String _getModuleDirectoryName(Path dirPath) {
		String absolutePath = SourceUtil.getAbsolutePath(dirPath);

		int x = absolutePath.indexOf("/modules/apps/archived/");

		if (x == -1) {
			return null;
		}

		String directoryPath = absolutePath.substring(x + 23);

		if (!directoryPath.contains(StringPool.SLASH)) {
			return directoryPath;
		}

		return null;
	}

	private List<String> _archivedModuleDirectoryNames;

}