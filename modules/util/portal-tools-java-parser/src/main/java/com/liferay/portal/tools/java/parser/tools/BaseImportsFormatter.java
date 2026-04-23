/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.tools;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author André de Oliveira
 * @author Hugo Huijser
 */
public abstract class BaseImportsFormatter implements ImportsFormatter {

	@Override
	public String format(String content, Pattern importPattern)
		throws IOException {

		return doFormat(content, importPattern, null, null);
	}

	@Override
	public String format(String content, String packagePath, String className)
		throws IOException {

		return doFormat(content, null, packagePath, className);
	}

	protected abstract ImportPackage createImportPackage(String line);

	protected ImportPackage createJavaImportPackage(String line) {
		Matcher matcher = _javaImportPattern.matcher(line);

		if (!matcher.find()) {
			return null;
		}

		boolean isStatic = false;

		if (Validator.isNotNull(matcher.group(1))) {
			isStatic = true;
		}

		String importString = matcher.group(2);

		return new ImportPackage(importString, isStatic, line);
	}

	protected abstract String doFormat(
			String content, Pattern importPattern, String packagePath,
			String className)
		throws IOException;

	protected String sortAndGroupImports(String imports) throws IOException {
		if (imports.contains("/*") || imports.contains("*/") ||
			imports.contains("\n//")) {

			return imports + "\n";
		}

		Set<ImportPackage> importPackages = new TreeSet<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(imports))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				ImportPackage importPackage = createImportPackage(line);

				if (importPackage != null) {
					importPackages.add(importPackage);
				}
			}

			StringBundler sb = new StringBundler(3 * importPackages.size());

			ImportPackage previousImportPackage = null;

			for (ImportPackage importPackage : importPackages) {
				if ((previousImportPackage != null) &&
					!importPackage.isGroupedWith(previousImportPackage)) {

					sb.append("\n");
				}

				sb.append(importPackage.getLine());
				sb.append("\n");

				previousImportPackage = importPackage;
			}

			return sb.toString();
		}
	}

	protected String stripUnusedImports(
			String imports, String content, String packagePath,
			String className, String classNameExceptionRegex)
		throws IOException {

		Set<String> classes = ClassUtil.getClasses(
			new UnsyncStringReader(content), className);

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(imports))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				int x = line.indexOf("import ");

				if (x == -1) {
					continue;
				}

				int y = line.lastIndexOf(CharPool.PERIOD);

				String importPackage = line.substring(x + 7, y);

				if (importPackage.equals(packagePath) ||
					importPackage.equals("java.lang")) {

					continue;
				}

				String importClass = line.substring(y + 1, line.length() - 1);

				if (importClass.matches(classNameExceptionRegex) ||
					classes.contains(importClass)) {

					sb.append(line);
					sb.append("\n");
				}
			}

			return sb.toString();
		}
	}

	private static final Pattern _javaImportPattern = Pattern.compile(
		"import( static)? ([^;]+);");

}