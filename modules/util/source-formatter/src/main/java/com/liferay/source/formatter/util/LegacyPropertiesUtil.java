/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.source.formatter.checkstyle.util.DetailASTUtil;

import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Hugo Huijser
 */
public class LegacyPropertiesUtil {

	public static List<LegacyProperty> getLegacyProperties(
			String fileName, String content)
		throws Exception {

		List<LegacyProperty> legacyProperties = new ArrayList<>();

		List<String> lines = _getLines(content);

		FileText fileText = new FileText(new File(fileName), lines);

		FileContents fileContents = new FileContents(fileText);

		DetailAST rootDetailAST = JavaParser.parse(fileContents);

		DetailAST nextSiblingDetailAST = rootDetailAST.getNextSibling();

		while (true) {
			if (nextSiblingDetailAST.getType() != TokenTypes.CLASS_DEF) {
				nextSiblingDetailAST = nextSiblingDetailAST.getNextSibling();

				continue;
			}

			List<DetailAST> variableDefinitionDetailASTs =
				DetailASTUtil.getAllChildTokens(
					nextSiblingDetailAST, true, TokenTypes.VARIABLE_DEF);

			for (DetailAST variableDefinitionDetailAST :
					variableDefinitionDetailASTs) {

				legacyProperties = _addLegacyProperties(
					legacyProperties, variableDefinitionDetailAST);
			}

			return legacyProperties;
		}
	}

	private static List<LegacyProperty> _addLegacyProperties(
		List<LegacyProperty> legacyProperties,
		DetailAST variableDefinitionDetailAST) {

		DetailAST assignDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.ASSIGN);

		if (assignDetailAST == null) {
			return legacyProperties;
		}

		DetailAST firstChildDetailAST = assignDetailAST.getFirstChild();

		if (firstChildDetailAST.getType() != TokenTypes.ARRAY_INIT) {
			return legacyProperties;
		}

		DetailAST identDetailAST = variableDefinitionDetailAST.findFirstToken(
			TokenTypes.IDENT);

		String variableName = identDetailAST.getText();

		if (!variableName.contains("_PORTAL_") &&
			!variableName.contains("_SYSTEM_")) {

			return legacyProperties;
		}

		List<DetailAST> arrayValueDetailASTs = _getArrayValueDetailASTs(
			firstChildDetailAST);

		for (DetailAST arrayValueDetailAST : arrayValueDetailASTs) {
			if (arrayValueDetailAST.getType() == TokenTypes.ARRAY_INIT) {
				legacyProperties = _addLegacyProperty(
					legacyProperties, variableName,
					_getArrayValueDetailASTs(arrayValueDetailAST));
			}
			else {
				legacyProperties = _addLegacyProperty(
					legacyProperties, variableName,
					Arrays.asList(arrayValueDetailAST));
			}
		}

		return legacyProperties;
	}

	private static List<LegacyProperty> _addLegacyProperty(
		List<LegacyProperty> legacyProperties, String variableName,
		List<DetailAST> detailASTs) {

		if (detailASTs.isEmpty()) {
			return legacyProperties;
		}

		String legacyPropertyName = _getStringValue(detailASTs.get(0));

		if (legacyPropertyName == null) {
			return legacyProperties;
		}

		String moduleName = null;
		String newPropertyName = null;

		if (detailASTs.size() > 1) {
			newPropertyName = _getStringValue(detailASTs.get(1));

			if (detailASTs.size() > 2) {
				moduleName = _getStringValue(detailASTs.get(2));
			}
		}

		legacyProperties.add(
			new LegacyProperty(
				legacyPropertyName, moduleName, newPropertyName, variableName));

		return legacyProperties;
	}

	private static List<DetailAST> _getArrayValueDetailASTs(
		DetailAST arrayInitDetailAST) {

		List<DetailAST> arrayValueDetailASTs = new ArrayList<>();

		DetailAST childDetailAST = arrayInitDetailAST.getFirstChild();

		while (true) {
			if (childDetailAST == null) {
				return arrayValueDetailASTs;
			}

			if ((childDetailAST.getType() != TokenTypes.COMMA) &&
				(childDetailAST.getType() != TokenTypes.RCURLY)) {

				arrayValueDetailASTs.add(childDetailAST);
			}

			childDetailAST = childDetailAST.getNextSibling();
		}
	}

	private static List<String> _getLines(String s) throws Exception {
		List<String> lines = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(s))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	private static String _getStringValue(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.EXPR) {
			detailAST = detailAST.getFirstChild();
		}

		if (detailAST.getType() == TokenTypes.STRING_LITERAL) {
			String text = detailAST.getText();

			return text.substring(1, text.length() - 1);
		}

		if (detailAST.getType() == TokenTypes.PLUS) {
			String left = _getStringValue(detailAST.getFirstChild());
			String right = _getStringValue(detailAST.getLastChild());

			if ((left != null) && (right != null)) {
				return left + right;
			}
		}

		return null;
	}

}