/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.util;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.tools.java.parser.Position;

import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.Token;

/**
 * @author Hugo Huijser
 */
public class DetailASTUtil {

	public static final int ALL_TYPES = -1;

	public static boolean equals(DetailAST detailAST1, DetailAST detailAST2) {
		return Objects.equals(detailAST1.toString(), detailAST2.toString());
	}

	public static List<DetailAST> getAllChildTokens(
		DetailAST detailAST, boolean recursive, int... tokenTypes) {

		return _getAllChildTokens(detailAST, recursive, null, tokenTypes);
	}

	public static String getBaseTypeName(DetailAST detailAST) {
		FullIdent fullIdent = FullIdent.createFullIdent(detailAST);

		String baseTypeName = fullIdent.getText();

		while (baseTypeName.endsWith("[]")) {
			baseTypeName = baseTypeName.substring(0, baseTypeName.length() - 2);
		}

		return baseTypeName;
	}

	public static DetailAST getClosingDetailAST(DetailAST detailAST) {
		if (detailAST.getType() == TokenTypes.RCURLY) {
			return null;
		}

		if (detailAST.getType() == TokenTypes.CASE_GROUP) {
			DetailAST slistDetailAST = detailAST.findFirstToken(
				TokenTypes.SLIST);

			if (slistDetailAST != null) {
				DetailAST previousSiblingDetailAST =
					slistDetailAST.getPreviousSibling();

				return previousSiblingDetailAST.findFirstToken(
					TokenTypes.COLON);
			}

			DetailAST lastChildDetailAST = detailAST.getLastChild();

			return lastChildDetailAST.findFirstToken(TokenTypes.COLON);
		}

		if (detailAST.getType() == TokenTypes.DO_WHILE) {
			while (detailAST != null) {
				if (detailAST.getType() == TokenTypes.SEMI) {
					return detailAST;
				}

				detailAST = detailAST.getNextSibling();
			}

			return null;
		}

		if (detailAST.getType() == TokenTypes.ENUM_CONSTANT_DEF) {
			while (TokenUtil.isOfType(
						detailAST, TokenTypes.COMMA,
						TokenTypes.ENUM_CONSTANT_DEF)) {

				detailAST = detailAST.getNextSibling();
			}

			if (detailAST.getType() == TokenTypes.SEMI) {
				return detailAST;
			}

			return null;
		}

		if (detailAST.getType() == TokenTypes.LITERAL_SWITCH) {
			return detailAST.findFirstToken(TokenTypes.LCURLY);
		}

		if (detailAST.getType() == TokenTypes.LABELED_STAT) {
			detailAST = detailAST.getFirstChild();

			detailAST = detailAST.getNextSibling();
		}

		DetailAST slistDetailAST = detailAST.findFirstToken(TokenTypes.SLIST);

		if (slistDetailAST != null) {
			return slistDetailAST;
		}

		DetailAST objBlockDetailAST = detailAST.findFirstToken(
			TokenTypes.OBJBLOCK);

		if (objBlockDetailAST != null) {
			return objBlockDetailAST.getFirstChild();
		}

		DetailAST emptyStatDetailAST = detailAST.findFirstToken(
			TokenTypes.EMPTY_STAT);

		if (emptyStatDetailAST != null) {
			return emptyStatDetailAST;
		}

		if (TokenUtil.isOfType(
				detailAST, TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_IF,
				TokenTypes.LITERAL_WHILE)) {

			return null;
		}

		if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			if (firstChildDetailAST.getType() == TokenTypes.LITERAL_IF) {
				return getClosingDetailAST(firstChildDetailAST);
			}

			return null;
		}

		DetailAST semiDetailAST = detailAST.findFirstToken(TokenTypes.SEMI);

		if (semiDetailAST != null) {
			return semiDetailAST;
		}

		DetailAST nextSiblingDetailAST = detailAST.getNextSibling();

		if ((nextSiblingDetailAST != null) &&
			(nextSiblingDetailAST.getType() == TokenTypes.SEMI)) {

			return nextSiblingDetailAST;
		}

		return null;
	}

	public static Position getEndPosition(
		DetailAST detailAST, FileContents fileContents) {

		if (detailAST.getType() == TokenTypes.LABELED_STAT) {
			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			return getEndPosition(
				firstChildDetailAST.getNextSibling(), fileContents);
		}

		DetailAST closingDetailAST = getClosingDetailAST(detailAST);

		if (closingDetailAST != null) {
			String s = closingDetailAST.getText();

			return new Position(
				closingDetailAST.getLineNo(),
				closingDetailAST.getColumnNo() + s.length());
		}

		DetailAST rparenDetailAST = null;

		if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
			DetailAST firstChildDetailAST = detailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.LITERAL_IF) {
				return new Position(
					detailAST.getLineNo(), detailAST.getColumnNo() + 4);
			}

			rparenDetailAST = firstChildDetailAST.findFirstToken(
				TokenTypes.RPAREN);
		}
		else if (TokenUtil.isOfType(
					detailAST, TokenTypes.LITERAL_FOR, TokenTypes.LITERAL_IF,
					TokenTypes.LITERAL_WHILE)) {

			rparenDetailAST = detailAST.findFirstToken(TokenTypes.RPAREN);
		}

		if (rparenDetailAST != null) {
			return new Position(
				rparenDetailAST.getLineNo(), rparenDetailAST.getColumnNo() + 1);
		}

		Position endPosition = null;

		String s = detailAST.getText();

		String line = fileContents.getLine(detailAST.getLineNo() - 1);

		if (line.contains(s)) {
			endPosition = new Position(
				detailAST.getLineNo(), detailAST.getColumnNo() + s.length());
		}

		for (DetailAST childDetailAST :
				getAllChildTokens(detailAST, true, ALL_TYPES)) {

			s = childDetailAST.getText();

			line = fileContents.getLine(childDetailAST.getLineNo() - 1);

			if (!line.contains(s)) {
				continue;
			}

			Position childDetailASTEndPosition = new Position(
				childDetailAST.getLineNo(),
				childDetailAST.getColumnNo() + s.length());

			if ((endPosition != null) &&
				(childDetailASTEndPosition.compareTo(endPosition) <= 0)) {

				continue;
			}

			endPosition = childDetailASTEndPosition;
		}

		return endPosition;
	}

	public static List<Token> getHiddenBefore(DetailAST detailAST) {
		DetailAstImpl detailAstImpl = (DetailAstImpl)detailAST;

		return detailAstImpl.getHiddenBefore();
	}

	public static Position getStartPosition(DetailAST detailAST) {
		Position startPosition = new Position(
			detailAST.getLineNo(), detailAST.getColumnNo());

		for (DetailAST childDetailAST :
				getAllChildTokens(detailAST, true, ALL_TYPES)) {

			Position childDetailASTStartPosition = new Position(
				childDetailAST.getLineNo(), childDetailAST.getColumnNo());

			if (childDetailASTStartPosition.compareTo(startPosition) >= 0) {
				continue;
			}

			startPosition = childDetailASTStartPosition;
		}

		return startPosition;
	}

	public static boolean hasParentWithTokenType(
		DetailAST detailAST, int... tokenTypes) {

		DetailAST parentDetailAST = detailAST.getParent();

		while (true) {
			if ((parentDetailAST == null) ||
				(parentDetailAST.getType() == TokenTypes.SLIST)) {

				return false;
			}

			if (ArrayUtil.contains(tokenTypes, parentDetailAST.getType())) {
				return true;
			}

			parentDetailAST = parentDetailAST.getParent();
		}
	}

	private static List<DetailAST> _getAllChildTokens(
		DetailAST detailAST, boolean recursive, List<DetailAST> list,
		int... tokenTypes) {

		if (list == null) {
			list = new ArrayList<>();
		}

		DetailAST childDetailAST = detailAST.getFirstChild();

		while (childDetailAST != null) {
			if (ArrayUtil.contains(tokenTypes, childDetailAST.getType()) ||
				ArrayUtil.contains(tokenTypes, ALL_TYPES)) {

				list.add(childDetailAST);
			}

			if (recursive) {
				list = _getAllChildTokens(
					childDetailAST, recursive, list, tokenTypes);
			}

			childDetailAST = childDetailAST.getNextSibling();
		}

		return list;
	}

}