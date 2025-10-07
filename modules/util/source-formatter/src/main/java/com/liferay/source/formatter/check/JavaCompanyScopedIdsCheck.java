/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class JavaCompanyScopedIdsCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		String content = javaTerm.getContent();

		if (!absolutePath.contains("/upgrade/") ||
			absolutePath.contains("/test/") ||
			absolutePath.contains("/testIntegration/") ||
			!isUpgradeProcess(absolutePath, fileContent)) {

			return content;
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		if (!javaClass.hasAnnotation("Component") && !javaClass.isStatic()) {
			return content;
		}

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaVariable()) {
				continue;
			}

			JavaVariable javaVariable = (JavaVariable)childJavaTerm;

			String variableName = javaVariable.getName();

			if (StringUtil.containsIgnoreCase(
					variableName, "companyId", StringPool.BLANK)) {

				continue;
			}

			List<String> entityIds = _getEntityIds();

			if (!_containsEntityId(entityIds, variableName)) {
				continue;
			}

			String variableTypeName = getVariableTypeName(
				javaVariable.getContent(), childJavaTerm, fileContent, fileName,
				variableName, true, false);

			if (variableTypeName.equals("Set<Long>")) {
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "add",
					variableName);
			}
			else if (variableTypeName.startsWith("Map<Long")) {
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "compute",
					variableName);
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "computeIfAbsent",
					variableName);
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "computeIfPresent",
					variableName);
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "put",
					variableName);
				_checkCompanyScopedIds(
					fileName, content, entityIds, javaTerm, "putIfAbsent",
					variableName);
			}
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkCompanyScopedIds(
		String fileName, String content, List<String> entityIds,
		JavaTerm javaTerm, String methodName, String variableName) {

		int x = -1;

		while (true) {
			x = content.indexOf(
				StringBundler.concat(variableName, ".", methodName, "("),
				x + 1);

			if (x == -1) {
				break;
			}

			List<String> getParameterNames = JavaSourceUtil.getParameterNames(
				JavaSourceUtil.getMethodCall(content, x));

			String parameter = getParameterNames.get(0);

			if (parameter.contains("CompanyId") ||
				parameter.contains("companyId") ||
				!_containsEntityId(entityIds, parameter)) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					variableName, ".", methodName, ", ", variableName,
					" are not unique across companies so it is needed to make ",
					"the collection company scoped"),
				javaTerm.getLineNumber(x));
		}
	}

	private boolean _containsEntityId(List<String> entityIds, String name) {
		for (String entityId : entityIds) {
			if (StringUtil.containsIgnoreCase(
					name, entityId, StringPool.BLANK)) {

				return true;
			}
		}

		return false;
	}

	private synchronized List<String> _getEntityIds() throws Exception {
		if (_entityIds != null) {
			return _entityIds;
		}

		_entityIds = new ArrayList<>();

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

					if (getLevel(tableContent) != 0) {
						continue;
					}

					if (!tableContent.contains("\tcompanyId LONG")) {
						break;
					}

					List<String> primaryKeys = getPrimaryKeys(tableContent);

					if (!primaryKeys.isEmpty()) {
						_entityIds.add(primaryKeys.get(0));
					}

					break;
				}
			}
		}

		return _entityIds;
	}

	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\w+) \\(");
	private static List<String> _entityIds;

}