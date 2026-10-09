/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaCleanUpMethodVariablesCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		JavaClass javaClass = (JavaClass)javaTerm;

		String className = javaClass.getName();

		if (!className.endsWith("Tag")) {
			return javaTerm.getContent();
		}

		String cleanUpMethodContent = _getCleanUpMethodContent(javaClass);

		if (cleanUpMethodContent != null) {
			String newCleanUpMethodContent = _formatVariables(
				fileName, cleanUpMethodContent, javaClass);

			if (!cleanUpMethodContent.equals(newCleanUpMethodContent)) {
				return StringUtil.replace(
					javaTerm.getContent(), cleanUpMethodContent,
					newCleanUpMethodContent);
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _formatVariables(
		String fileName, String cleanUpMethodContent, JavaClass javaClass) {

		int previousPos = -1;

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isPrivate() || !javaTerm.isJavaVariable()) {
				continue;
			}

			String variableName = javaTerm.getName();

			Pattern pattern = Pattern.compile(
				"\tprivate\\s+(((final|static|transient|volatile)\\s+)*)" +
					"([\\s\\S]*?)" + variableName);

			String variableContent = javaTerm.getContent();

			Matcher matcher = pattern.matcher(variableContent);

			if (!matcher.find()) {
				return cleanUpMethodContent;
			}

			String modifierDefinition = StringUtil.trim(
				variableContent.substring(matcher.start(1), matcher.start(4)));

			boolean isFinal = false;

			if (modifierDefinition.contains("final")) {
				isFinal = true;
			}

			int pos = cleanUpMethodContent.indexOf(variableName + " =");

			if (pos == -1) {
				if (!isFinal && !javaTerm.isStatic()) {
					addMessage(
						fileName,
						"Variable \"" + variableName +
							"\" is missing in method \"cleanUp\"");
				}

				continue;
			}

			if (previousPos > pos) {
				return _sortVariables(cleanUpMethodContent, previousPos, pos);
			}

			previousPos = pos;

			if (isFinal) {
				continue;
			}

			String javaFieldType = StringUtil.trim(matcher.group(4));

			pattern = Pattern.compile(
				javaTerm.getName() + " =\\s+[a-z]\\w*\\.");

			matcher = pattern.matcher(cleanUpMethodContent);

			if (matcher.find()) {
				continue;
			}

			int x = variableContent.indexOf(javaTerm.getName());

			String setVariableCommand = variableContent.substring(x);

			if (!setVariableCommand.contains(" =")) {
				String defaultValue = _getDefaultValue(javaFieldType);

				setVariableCommand = StringUtil.replaceLast(
					setVariableCommand, CharPool.SEMICOLON,
					" = " + defaultValue + ";");
			}

			setVariableCommand = StringUtil.replace(
				setVariableCommand,
				new String[] {StringPool.TAB, StringPool.NEW_LINE},
				new String[] {StringPool.BLANK, StringPool.SPACE});

			String setVariableCommandRegex = StringUtil.replace(
				setVariableCommand,
				new String[] {
					StringPool.CLOSE_PARENTHESIS, StringPool.OPEN_PARENTHESIS,
					StringPool.SPACE, "0\\.0"
				},
				new String[] {"\\)", "\\(", "\\s*", "0(\\.0)?"});

			pattern = Pattern.compile(setVariableCommandRegex);

			matcher = pattern.matcher(cleanUpMethodContent);

			if (matcher.find()) {
				continue;
			}

			addMessage(
				fileName,
				"Initial value for \"" + variableName +
					"\" differs from value in cleanUp method");
		}

		return cleanUpMethodContent;
	}

	private String _getCleanUpMethodContent(JavaClass javaClass) {
		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod()) {
				continue;
			}

			String javaTermName = javaTerm.getName();

			if (javaTermName.equals("cleanUp")) {
				return javaTerm.getContent();
			}
		}

		return null;
	}

	private String _getDefaultValue(String type) {
		if (StringUtil.isLowerCase(type)) {
			return _defaultPrimitiveValues.get(type);
		}

		return "null";
	}

	private String _sortVariables(
		String cleanUpMethodContent, int previousPos, int pos) {

		int semiColonPos = cleanUpMethodContent.indexOf(";\n", pos);

		if ((semiColonPos == -1) || (semiColonPos > previousPos)) {
			return cleanUpMethodContent;
		}

		int previousSemiColonPos = cleanUpMethodContent.indexOf(
			";\n", previousPos);

		if (previousSemiColonPos == -1) {
			return cleanUpMethodContent;
		}

		String previousVariableSetter = cleanUpMethodContent.substring(
			previousPos, previousSemiColonPos + 1);
		String variableSetter = cleanUpMethodContent.substring(
			pos, semiColonPos + 1);

		String newCleanUpMethodContent = StringUtil.replaceFirst(
			cleanUpMethodContent, variableSetter, previousVariableSetter);

		return StringUtil.replaceLast(
			newCleanUpMethodContent, previousVariableSetter, variableSetter);
	}

	private static final Map<String, String> _defaultPrimitiveValues =
		MapUtil.fromArray(
			new String[] {
				"boolean", "false", "char", "'\\\\0'", "byte", "0", "double",
				"0\\.0", "float", "0\\.0", "int", "0", "long", "0", "short", "0"
			});

}