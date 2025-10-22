/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.orm.Type;

import java.io.Serializable;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.sql.Blob;
import java.sql.Clob;

import java.time.Duration;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;

/**
 * @author Brian Wing Shun Chan
 */
public class TypeTranslator {

	public static final BasicTypeReference<BigDecimal> BIG_DECIMAL =
		StandardBasicTypes.BIG_DECIMAL;

	public static final BasicTypeReference<BigInteger> BIG_INTEGER =
		StandardBasicTypes.BIG_INTEGER;

	public static final BasicTypeReference<byte[]> BINARY =
		StandardBasicTypes.BINARY;

	public static final BasicTypeReference<Blob> BLOB = StandardBasicTypes.BLOB;

	public static final BasicTypeReference<Boolean> BOOLEAN =
		new BasicTypeReference("boolean", Boolean.class, 16);

	public static final BasicTypeReference<Byte> BYTE = StandardBasicTypes.BYTE;

	public static final BasicTypeReference<Calendar> CALENDAR =
		StandardBasicTypes.CALENDAR;

	public static final BasicTypeReference<Calendar> CALENDAR_DATE =
		StandardBasicTypes.CALENDAR_DATE;

	public static final BasicTypeReference<char[]> CHAR_ARRAY =
		StandardBasicTypes.CHAR_ARRAY;

	public static final BasicTypeReference<Character> CHARACTER =
		StandardBasicTypes.CHARACTER;

	public static final BasicTypeReference<Character[]> CHARACTER_ARRAY =
		StandardBasicTypes.CHARACTER_ARRAY;

	@SuppressWarnings("rawtypes")
	public static final BasicTypeReference<Class> CLASS =
		StandardBasicTypes.CLASS;

	public static final BasicTypeReference<Clob> CLOB = StandardBasicTypes.CLOB;

	public static final BasicTypeReference<Currency> CURRENCY =
		StandardBasicTypes.CURRENCY;

	public static final BasicTypeReference<Date> DATE = StandardBasicTypes.DATE;

	public static final BasicTypeReference<Double> DOUBLE =
		StandardBasicTypes.DOUBLE;

	public static final BasicTypeReference<Duration> DURATION =
		StandardBasicTypes.DURATION;

	public static final BasicTypeReference<Float> FLOAT =
		StandardBasicTypes.FLOAT;

	public static final BasicTypeReference<byte[]> IMAGE =
		StandardBasicTypes.IMAGE;

	public static final BasicTypeReference<Integer> INTEGER =
		StandardBasicTypes.INTEGER;

	public static final BasicTypeReference<Locale> LOCALE =
		StandardBasicTypes.LOCALE;

	public static final BasicTypeReference<Long> LONG = StandardBasicTypes.LONG;

	public static final BasicTypeReference<byte[]> MATERIALIZED_BLOB =
		StandardBasicTypes.MATERIALIZED_BLOB;

	public static final BasicTypeReference<String> MATERIALIZED_CLOB =
		StandardBasicTypes.MATERIALIZED_CLOB;

	public static final BasicTypeReference<Boolean> NUMERIC_BOOLEAN =
		StandardBasicTypes.NUMERIC_BOOLEAN;

	public static final BasicTypeReference<Serializable> SERIALIZABLE =
		StandardBasicTypes.SERIALIZABLE;

	public static final BasicTypeReference<Short> SHORT =
		StandardBasicTypes.SHORT;

	public static final BasicTypeReference<String> STRING =
		StandardBasicTypes.STRING;

	public static final BasicTypeReference<String> TEXT =
		StandardBasicTypes.TEXT;

	public static final BasicTypeReference<Date> TIME = StandardBasicTypes.TIME;

	public static final BasicTypeReference<Date> TIMESTAMP =
		StandardBasicTypes.TIMESTAMP;

	public static final BasicTypeReference<TimeZone> TIMEZONE =
		StandardBasicTypes.TIMEZONE;

	public static final BasicTypeReference<Boolean> TRUE_FALSE =
		StandardBasicTypes.TRUE_FALSE;

	public static final BasicTypeReference<java.net.URL> URL =
		StandardBasicTypes.URL;

	public static final BasicTypeReference<UUID> UUID = StandardBasicTypes.UUID;

	public static final BasicTypeReference<UUID> UUID_BINARY =
		StandardBasicTypes.UUID_BINARY;

	public static final BasicTypeReference<UUID> UUID_CHAR =
		StandardBasicTypes.UUID_CHAR;

	public static final BasicTypeReference<Boolean> YES_NO =
		StandardBasicTypes.YES_NO;

	public static BasicTypeReference<?> translate(Type type) {
		if (type == Type.BIG_DECIMAL) {
			return BIG_DECIMAL;
		}
		else if (type == Type.BIG_INTEGER) {
			return BIG_INTEGER;
		}
		else if (type == Type.BINARY) {
			return BINARY;
		}
		else if (type == Type.BLOB) {
			return BLOB;
		}
		else if (type == Type.BOOLEAN) {
			return BOOLEAN;
		}
		else if (type == Type.BYTE) {
			return BYTE;
		}
		else if (type == Type.CALENDAR) {
			return CALENDAR;
		}
		else if (type == Type.CALENDAR_DATE) {
			return CALENDAR_DATE;
		}
		else if (type == Type.CHAR_ARRAY) {
			return CHAR_ARRAY;
		}
		else if (type == Type.CHARACTER) {
			return CHARACTER;
		}
		else if (type == Type.CHARACTER_ARRAY) {
			return CHARACTER_ARRAY;
		}
		else if (type == Type.CLASS) {
			return CLASS;
		}
		else if (type == Type.CLOB) {
			return CLOB;
		}
		else if (type == Type.CURRENCY) {
			return CURRENCY;
		}
		else if (type == Type.DATE) {
			return DATE;
		}
		else if (type == Type.DOUBLE) {
			return DOUBLE;
		}
		else if (type == Type.FLOAT) {
			return FLOAT;
		}
		else if (type == Type.IMAGE) {
			return IMAGE;
		}
		else if (type == Type.INTEGER) {
			return INTEGER;
		}
		else if (type == Type.LOCALE) {
			return LOCALE;
		}
		else if (type == Type.LONG) {
			return LONG;
		}
		else if (type == Type.MATERIALIZED_BLOB) {
			return MATERIALIZED_BLOB;
		}
		else if (type == Type.MATERIALIZED_CLOB) {
			return MATERIALIZED_CLOB;
		}
		else if (type == Type.NUMERIC_BOOLEAN) {
			return NUMERIC_BOOLEAN;
		}
		else if (type == Type.SERIALIZABLE) {
			return SERIALIZABLE;
		}
		else if (type == Type.SHORT) {
			return SHORT;
		}
		else if (type == Type.STRING) {
			return STRING;
		}
		else if (type == Type.TEXT) {
			return TEXT;
		}
		else if (type == Type.TIME) {
			return TIME;
		}
		else if (type == Type.TIMESTAMP) {
			return TIMESTAMP;
		}
		else if (type == Type.TIMEZONE) {
			return TIMEZONE;
		}
		else if (type == Type.TRUE_FALSE) {
			return TRUE_FALSE;
		}
		else if (type == Type.URL) {
			return URL;
		}
		else if (type == Type.UUID_BINARY) {
			return UUID_BINARY;
		}
		else if (type == Type.UUID_CHAR) {
			return UUID_CHAR;
		}
		else if (type == Type.YES_NO) {
			return YES_NO;
		}

		return null;
	}

}