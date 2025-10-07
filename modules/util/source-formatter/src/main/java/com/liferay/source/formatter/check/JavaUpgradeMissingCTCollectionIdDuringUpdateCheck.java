/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.poshi.core.util.ListUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.processor.SourceProcessor;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaUpgradeMissingCTCollectionIdDuringUpdateCheck
	extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		String className = JavaSourceUtil.getClassName(fileName);

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			className.startsWith("Base") ||
			!isUpgradeProcess(absolutePath, content)) {

			return content;
		}

		SourceProcessor sourceProcessor = getSourceProcessor();

		SourceFormatterArgs sourceFormatterArgs =
			sourceProcessor.getSourceFormatterArgs();

		for (String currentBranchRenamedFileName :
				sourceFormatterArgs.getCurrentBranchRenamedFileNames()) {

			if (absolutePath.endsWith(currentBranchRenamedFileName)) {
				return content;
			}
		}

		for (String currentBranchAddedFileNames :
				sourceFormatterArgs.getCurrentBranchAddedFileNames()) {

			if (absolutePath.endsWith(currentBranchAddedFileNames)) {
				for (String methodName : _METHOD_NAMES) {
					_checkMissingCTCollectionIdDuringUpdate(
						fileName, content, methodName);
				}

				return content;
			}
		}

		return content;
	}

	private void _checkMissingCTCollectionIdDuringUpdate(
			String fileName, String content, String methodName)
		throws Exception {

		int x = -1;

		while (true) {
			x = content.indexOf(methodName, x + 1);

			if (x == -1) {
				break;
			}

			List<String> getParameterNames = JavaSourceUtil.getParameterNames(
				JavaSourceUtil.getMethodCall(content, x));

			if (methodName.startsWith("AutoBatchPreparedStatementUtil.") &&
				(getParameterNames.size() != 2)) {

				continue;
			}

			String updateClause = null;

			if (methodName.startsWith("AutoBatchPreparedStatementUtil.")) {
				updateClause = getParameterNames.get(1);
			}
			else {
				updateClause = StringUtil.trim(
					JavaSourceUtil.getParameters(
						JavaSourceUtil.getMethodCall(content, x)));
			}

			if (!updateClause.startsWith("\"update")) {
				continue;
			}

			updateClause = updateClause.replaceAll("\n", "");
			updateClause = updateClause.replaceAll("\\s{2,}", " ");

			updateClause = StringUtil.removeSubstring(updateClause, "\" + \"");
			updateClause = StringUtil.removeSubstring(updateClause, "\", \"");

			String tableName = updateClause.replaceFirst(
				"\"update (\\w+).*", "$1");

			int y = updateClause.indexOf("where ");

			if (y == -1) {
				continue;
			}

			String whereClause = updateClause.substring(y);

			if (whereClause.contains("and ") || whereClause.contains("or ")) {
				continue;
			}

			String columnName = whereClause.replaceFirst(
				"where (\\w+).+", "$1");

			if (!columnName.endsWith("Id") ||
				columnName.equals("ctCollectionId")) {

				continue;
			}

			List<String> primaryKeys = _getPrimaryKeysMap(tableName);

			if (ListUtil.isEmpty(primaryKeys)) {
				continue;
			}

			if (primaryKeys.contains(columnName) &&
				primaryKeys.contains("ctCollectionId")) {

				addMessage(
					fileName,
					"Missing \"ctCollectionId\" in where clause during update",
					getLineNumber(content, x));
			}
		}
	}

	private synchronized List<String> _getPrimaryKeysMap(String tableName)
		throws Exception {

		if (_primaryKeysMap != null) {
			return _primaryKeysMap.get(tableName);
		}

		_primaryKeysMap = new HashMap<>();

		File portalDir = getPortalDir();

		List<String> fileNames = SourceFormatterUtil.scanForFileNames(
			portalDir.getCanonicalPath(),
			new String[] {
				"**/*-service/src/main/resources/META-INF/sql/tables.sql"
			});

		fileNames.add(portalDir + "/sql/portal-tables.sql");

		for (String fileName : fileNames) {
			String content = FileUtil.read(new File(fileName));

			Matcher matcher = _createTablePattern.matcher(content);

			while (matcher.find()) {
				int x = matcher.end();

				while (true) {
					x = content.indexOf(");", x + 1);

					if (x == -1) {
						continue;
					}

					String tableContent = content.substring(
						matcher.start(), x + 2);

					if (getLevel(tableContent) == 0) {
						_primaryKeysMap.put(
							matcher.group(1), getPrimaryKeys(tableContent));

						break;
					}
				}
			}
		}

		return _primaryKeysMap.get(tableName);
	}

	private static final String[] _METHOD_NAMES = {
		"AutoBatchPreparedStatementUtil.autoBatch(",
		"AutoBatchPreparedStatementUtil.concurrentAutoBatch(",
		"connection.prepareStatement(", "StringBundler.concat("
	};

	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\w+) \\(");

	private Map<String, List<String>> _primaryKeysMap;

}