/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.checkstyle.util.DetailASTUtil;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class ExceptionVariableNameCheck extends VariableNameCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF, TokenTypes.STATIC_INIT,
			TokenTypes.VARIABLE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		Map<String, Set<ExceptionDefinition>> exceptionDefinitionsMap =
			_getExceptionDefinitionsMap(detailAST);

		for (Map.Entry<String, Set<ExceptionDefinition>> entry :
				exceptionDefinitionsMap.entrySet()) {

			String typeName = entry.getKey();

			Set<ExceptionDefinition> exceptionDefinitions = entry.getValue();

			for (ExceptionDefinition exceptionDefinition :
					exceptionDefinitions) {

				int counter = _getCounter(
					exceptionDefinition, exceptionDefinitions);

				String name = exceptionDefinition.getName();

				String expectedName = getExpectedVariableName(typeName);

				if (counter != 0) {
					expectedName += String.valueOf(counter);
				}

				if (!name.equals(expectedName)) {
					log(
						exceptionDefinition.getLineNumber(),
						MSG_RENAME_VARIABLE, name, expectedName);
				}
			}
		}
	}

	private int _getCounter(
		ExceptionDefinition exceptionDefinition,
		Set<ExceptionDefinition> exceptionDefinitionSet) {

		if (exceptionDefinitionSet.size() == 1) {
			return 0;
		}

		int precedingOverlappingExceptionCount = 0;

		for (ExceptionDefinition curExceptionDefinition :
				exceptionDefinitionSet) {

			if (exceptionDefinition.equals(curExceptionDefinition)) {
				if (precedingOverlappingExceptionCount > 0) {
					return precedingOverlappingExceptionCount + 1;
				}
			}
			else if (curExceptionDefinition.compareTo(exceptionDefinition) <
						0) {

				if (curExceptionDefinition.getEndRangeLineNumber() >=
						exceptionDefinition.getLineNumber()) {

					precedingOverlappingExceptionCount++;
				}
			}
			else if (exceptionDefinition.getEndRangeLineNumber() >=
						curExceptionDefinition.getLineNumber()) {

				return 1;
			}
		}

		return 0;
	}

	private int _getEndRangeLineNumber(DetailAST detailAST) {
		DetailAST slistDetailAST = null;

		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.SLIST) {
			slistDetailAST = parentDetailAST;
		}
		else {
			if (parentDetailAST.getType() != TokenTypes.LITERAL_CATCH) {
				parentDetailAST = parentDetailAST.getParent();
			}

			slistDetailAST = parentDetailAST.getLastChild();
		}

		if (slistDetailAST.getType() == TokenTypes.SLIST) {
			DetailAST lastChildDetailAST = slistDetailAST.getLastChild();

			return lastChildDetailAST.getLineNo();
		}

		return getEndLineNumber(parentDetailAST);
	}

	private Map<String, Set<ExceptionDefinition>> _getExceptionDefinitionsMap(
		DetailAST detailAST) {

		Map<String, Set<ExceptionDefinition>> exceptionDefinitionsMap =
			new HashMap<>();

		List<DetailAST> definitionDetailASTs = DetailASTUtil.getAllChildTokens(
			detailAST, true, TokenTypes.PARAMETER_DEF, TokenTypes.VARIABLE_DEF);

		for (DetailAST definitionDetailAST : definitionDetailASTs) {
			DetailAST parentDetailAST = definitionDetailAST.getParent();

			if (parentDetailAST.getType() == TokenTypes.OBJBLOCK) {
				continue;
			}

			String exceptionTypeName = _getExceptionTypeName(
				definitionDetailAST);

			if (exceptionTypeName == null) {
				continue;
			}

			DetailAST ancestorDetailAST = getParentWithTokenType(
				definitionDetailAST, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF,
				TokenTypes.STATIC_INIT, TokenTypes.VARIABLE_DEF);

			if (!equals(ancestorDetailAST, detailAST)) {
				continue;
			}

			Set<ExceptionDefinition> exceptionDefinitions =
				exceptionDefinitionsMap.get(exceptionTypeName);

			if (exceptionDefinitions == null) {
				exceptionDefinitions = new TreeSet<>();
			}

			DetailAST nameDetailAST = definitionDetailAST.findFirstToken(
				TokenTypes.IDENT);

			exceptionDefinitions.add(
				new ExceptionDefinition(
					nameDetailAST.getText(), definitionDetailAST.getLineNo(),
					definitionDetailAST.getColumnNo(),
					_getEndRangeLineNumber(definitionDetailAST)));

			exceptionDefinitionsMap.put(
				exceptionTypeName, exceptionDefinitions);
		}

		return exceptionDefinitionsMap;
	}

	private String _getExceptionTypeName(DetailAST definitionDetailAST) {
		DetailAST typeDetailAST = definitionDetailAST.findFirstToken(
			TokenTypes.TYPE);

		DetailAST parentDetailAST = definitionDetailAST.getParent();

		if (parentDetailAST.getType() == TokenTypes.LITERAL_CATCH) {
			DetailAST firstChildDetailAST = typeDetailAST.getFirstChild();

			if ((firstChildDetailAST != null) &&
				(firstChildDetailAST.getType() == TokenTypes.BOR)) {

				for (String name : getNames(firstChildDetailAST, true)) {
					if (name.endsWith("Exception")) {
						return "Exception";
					}
				}

				return null;
			}
		}

		String exceptionTypeName = getTypeName(typeDetailAST, false);

		String[] parts = StringUtil.split(exceptionTypeName, ".");

		if (exceptionTypeName.endsWith("Exception")) {
			return parts[parts.length - 1];
		}

		if (parentDetailAST.getType() != TokenTypes.LITERAL_CATCH) {
			return null;
		}

		if (parentDetailAST.getType() == TokenTypes.LITERAL_CATCH) {
			for (String part : parts) {
				if (part.endsWith("Exception")) {
					return part;
				}
			}
		}

		return null;
	}

	private class ExceptionDefinition
		implements Comparable<ExceptionDefinition> {

		public ExceptionDefinition(
			String name, int lineNumber, int linePosition,
			int endRangeLineNumber) {

			_name = name;
			_lineNumber = lineNumber;
			_linePosition = linePosition;
			_endRangeLineNumber = endRangeLineNumber;
		}

		@Override
		public int compareTo(ExceptionDefinition exceptionDefinition) {
			if (_lineNumber != exceptionDefinition.getLineNumber()) {
				return _lineNumber - exceptionDefinition.getLineNumber();
			}

			return _linePosition - exceptionDefinition.getLinePosition();
		}

		public int getEndRangeLineNumber() {
			return _endRangeLineNumber;
		}

		public int getLineNumber() {
			return _lineNumber;
		}

		public int getLinePosition() {
			return _linePosition;
		}

		public String getName() {
			return _name;
		}

		private final int _endRangeLineNumber;
		private final int _lineNumber;
		private final int _linePosition;
		private final String _name;

	}

}