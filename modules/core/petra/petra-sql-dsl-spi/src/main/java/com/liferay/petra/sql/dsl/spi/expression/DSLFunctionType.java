/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.sql.dsl.spi.expression;

import com.liferay.petra.string.StringPool;

import java.util.Objects;

/**
 * @author Preston Crary
 */
public class DSLFunctionType {

	public static final DSLFunctionType ADDITION = new DSLFunctionType(" + ");

	public static final DSLFunctionType BITWISE_AND = new DSLFunctionType(
		"BITAND(", ")");

	public static final DSLFunctionType CAST_CLOB_TEXT = new DSLFunctionType(
		"CAST_CLOB_TEXT(", ")");

	public static final DSLFunctionType CAST_LONG = new DSLFunctionType(
		"CAST_LONG(", ")");

	public static final DSLFunctionType CAST_TEXT = new DSLFunctionType(
		"CAST_TEXT(", ")");

	public static final DSLFunctionType CONCAT = new DSLFunctionType(
		"CONCAT(", ")");

	public static final DSLFunctionType DIVISION = new DSLFunctionType(" / ");

	public static final DSLFunctionType FLOAT_DIVISION = new DSLFunctionType(
		"CAST_DECIMAL(", ") / ", StringPool.BLANK);

	public static final DSLFunctionType LOWER = new DSLFunctionType(
		"LOWER(", ")");

	public static final DSLFunctionType MULTIPLICATION = new DSLFunctionType(
		" * ");

	public static final DSLFunctionType SUBTRACTION = new DSLFunctionType(
		" - ");

	public static final DSLFunctionType WITH_PARENTHESES = new DSLFunctionType(
		"(", ")");

	public DSLFunctionType(String delimiter) {
		this("", delimiter, "");
	}

	public DSLFunctionType(String prefix, String postfix) {
		this(prefix, ", ", postfix);
	}

	public DSLFunctionType(String prefix, String delimiter, String postfix) {
		_prefix = Objects.requireNonNull(prefix);
		_delimiter = Objects.requireNonNull(delimiter);
		_postfix = Objects.requireNonNull(postfix);
	}

	public String getDelimiter() {
		return _delimiter;
	}

	public String getPostfix() {
		return _postfix;
	}

	public String getPrefix() {
		return _prefix;
	}

	private final String _delimiter;
	private final String _postfix;
	private final String _prefix;

}