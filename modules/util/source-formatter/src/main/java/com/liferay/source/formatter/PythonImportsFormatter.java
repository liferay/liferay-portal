/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.BaseImportsFormatter;
import com.liferay.portal.tools.ImportPackage;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PythonImportsFormatter extends BaseImportsFormatter {

	protected static List<String> getImportsList(
		String content, Pattern importPattern) {

		List<String> importsList = new ArrayList<>();

		Matcher matcher = importPattern.matcher(content);

		while (matcher.find()) {
			importsList.add(matcher.group());
		}

		return importsList;
	}

	@Override
	protected ImportPackage createImportPackage(String line) {
		return _createPythonImportPackage(line);
	}

	@Override
	protected String doFormat(
			String content, Pattern importPattern, String packageDir,
			String className)
		throws IOException {

		List<String> importsList = getImportsList(content, importPattern);

		for (String imports : importsList) {
			content = doFormat(content, packageDir, className, imports);
		}

		return content;
	}

	protected String doFormat(
			String content, String packageDir, String className, String imports)
		throws IOException {

		if (Validator.isNull(imports)) {
			return content;
		}

		String newImports = _mergeImports(imports);

		newImports = sortAndGroupImports(newImports);

		newImports = _splitImports(newImports);

		if (!imports.equals(newImports)) {
			content = StringUtil.replaceFirst(content, imports, newImports);
		}

		return content;
	}

	private ImportPackage _createPythonImportPackage(String line) {
		Matcher matcher = _importPattern.matcher(line);

		if (!matcher.find()) {
			return null;
		}

		String packageName = matcher.group(2);

		return new PythonImportPackage(packageName, line);
	}

	private String _mergeImports(String imports) {
		imports = imports.replaceAll("\\\\\n", StringPool.BLANK);

		Map<String, List<String>> packageImportsMap = new TreeMap<>();

		Matcher matcher = _importPattern.matcher(imports);

		String indent = null;

		while (matcher.find()) {
			if (indent == null) {
				indent = matcher.group(1);
			}

			List<String> importNamesList = new ArrayList<>();

			String importNames = matcher.group(3);

			for (String importName : importNames.split(StringPool.COMMA)) {
				importNamesList.add(importName.trim());
			}

			String packageName = matcher.group(2);

			List<String> curImportNames = packageImportsMap.get(packageName);

			if (curImportNames != null) {
				importNamesList.addAll(curImportNames);
			}

			packageImportsMap.put(packageName, importNamesList);
		}

		if (MapUtil.isEmpty(packageImportsMap)) {
			return imports;
		}

		StringBundler sb = new StringBundler(packageImportsMap.size() * 6);

		for (Map.Entry<String, List<String>> entry :
				packageImportsMap.entrySet()) {

			sb.append(indent);
			sb.append("from ");
			sb.append(entry.getKey());
			sb.append(" import ");

			List<String> importNamesList = entry.getValue();

			Collections.sort(importNamesList);

			sb.append(
				ListUtil.toString(
					importNamesList, StringPool.BLANK,
					StringPool.COMMA_AND_SPACE));

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _splitImports(String imports) {
		String[] newImports = imports.split("\n");

		StringBundler sb = new StringBundler(newImports.length * 2);

		for (String newImport : newImports) {
			String indent = newImport.replaceFirst("(\t*).*", "$1");

			sb.append(newImport.replaceAll(", ", ", \\\\\n\t" + indent));

			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private static final Pattern _importPattern = Pattern.compile(
		"([ \t]*)from (.*) import (.*)");

}