/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class FullyQualifiedNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {
			TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF, TokenTypes.INTERFACE_DEF
		};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		DetailAST parentDetailAST = detailAST.getParent();

		if (parentDetailAST != null) {
			return;
		}

		List<DetailAST> nameDetailASTs = getAllChildTokens(
			detailAST, true, TokenTypes.IDENT);

		for (DetailAST nameDetailAST : nameDetailASTs) {
			String name = nameDetailAST.getText();

			String fullyQualifiedName = _getFullyQualifiedName(
				nameDetailAST, name);

			if ((fullyQualifiedName == null) ||
				fullyQualifiedName.matches(".*\\.upgrade\\.v[0-9_]+\\.\\w+")) {

				continue;
			}

			if (!_isFullyQualifiedNameRequired(
					detailAST, fullyQualifiedName, name, nameDetailASTs)) {

				log(
					nameDetailAST, _MSG_USE_IMPORT_STATEMENT,
					fullyQualifiedName);
			}
		}
	}

	private boolean _containsImport(DetailAST rootDetailAST, String className) {
		DetailAST siblingDetailAST = rootDetailAST;

		while (true) {
			siblingDetailAST = siblingDetailAST.getPreviousSibling();

			if (siblingDetailAST == null) {
				return false;
			}

			if ((siblingDetailAST.getType() != TokenTypes.IMPORT) &&
				(siblingDetailAST.getType() != TokenTypes.STATIC_IMPORT)) {

				continue;
			}

			DetailAST dotDetailAST = siblingDetailAST.findFirstToken(
				TokenTypes.DOT);

			if (dotDetailAST == null) {
				continue;
			}

			FullIdent fullIdent = FullIdent.createFullIdent(dotDetailAST);

			String importName = fullIdent.getText();

			if (((siblingDetailAST.getType() == TokenTypes.IMPORT) &&
				 importName.endsWith("." + className)) ||
				((siblingDetailAST.getType() == TokenTypes.STATIC_IMPORT) &&
				 importName.contains("." + className + "."))) {

				return true;
			}
		}
	}

	private String _getFullyQualifiedName(
		DetailAST nameDetailAST, String name) {

		if (!name.matches("[A-Z][A-Za-z0-9]+")) {
			return null;
		}

		DetailAST parentDetailAST = nameDetailAST.getParent();

		if (parentDetailAST.getType() != TokenTypes.DOT) {
			return null;
		}

		FullIdent fullIdent = FullIdent.createFullIdent(parentDetailAST);

		String fullyQualifiedName = fullIdent.getText();

		if (fullyQualifiedName.matches("([a-z]\\w*\\.){2,}[A-Z]\\w*")) {
			return fullyQualifiedName;
		}

		return null;
	}

	private boolean _isFullyQualifiedNameRequired(
		DetailAST rootDetailAST, String fullyQualifiedName, String className,
		List<DetailAST> nameDetailASTs) {

		if (_containsImport(rootDetailAST, className)) {
			return true;
		}

		for (DetailAST nameDetailAST : nameDetailASTs) {
			String name = nameDetailAST.getText();

			if (!name.equals(className)) {
				continue;
			}

			String curFullyQualifiedName = _getFullyQualifiedName(
				nameDetailAST, name);

			if ((curFullyQualifiedName == null) ||
				!curFullyQualifiedName.equals(fullyQualifiedName)) {

				return true;
			}
		}

		return false;
	}

	private static final String _MSG_USE_IMPORT_STATEMENT =
		"import.statement.use";

}