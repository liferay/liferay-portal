/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.parser.comparator;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JavaTermComparator implements Comparator<JavaTerm> {

	public JavaTermComparator(String customSQLContent) {
		_customSQLContent = customSQLContent;
	}

	@Override
	public int compare(JavaTerm javaTerm1, JavaTerm javaTerm2) {
		int type1 = _getType(javaTerm1);
		int type2 = _getType(javaTerm2);

		if ((type1 == -1) || (type2 == -1)) {
			return 0;
		}

		if (type1 != type2) {
			return type1 - type2;
		}

		String name1 = javaTerm1.getName();
		String name2 = javaTerm2.getName();

		if (javaTerm1.isJavaVariable()) {
			if (StringUtil.isUpperCase(name1) &&
				!StringUtil.isLowerCase(name1) &&
				!StringUtil.isUpperCase(name2)) {

				return -1;
			}

			if (!StringUtil.isUpperCase(name1) &&
				StringUtil.isUpperCase(name2) &&
				!StringUtil.isLowerCase(name2)) {

				return 1;
			}

			if (javaTerm1.isPrivate() && javaTerm1.isStatic()) {
				if (name1.matches("_log(ger)?") &&
					!name2.matches("_log(ger)?")) {

					return -1;
				}

				if (!name1.matches("_log(ger)?") &&
					name2.matches("_log(ger)?")) {

					return 1;
				}

				if (name1.equals("_instance")) {
					return -1;
				}

				if (name2.equals("_instance")) {
					return 1;
				}
			}
		}

		if (Validator.isNotNull(_customSQLContent) &&
			(name1.compareToIgnoreCase(name2) != 0)) {

			int value = _compareFinderJavaTerms(javaTerm1, javaTerm2);

			if (value != 0) {
				return value;
			}
		}

		if (name1.compareTo(name2) != 0) {
			NaturalOrderStringComparator naturalOrderStringComparator =
				new NaturalOrderStringComparator(true, true);

			return naturalOrderStringComparator.compare(name1, name2);
		}

		return _compareParameterTypes(javaTerm1, javaTerm2);
	}

	private int _compareFinderJavaTerms(
		JavaTerm javaTerm1, JavaTerm javaTerm2) {

		Matcher matcher1 = _finderPattern.matcher(javaTerm1.getName());

		if (!matcher1.find()) {
			return 0;
		}

		Matcher matcher2 = _finderPattern.matcher(javaTerm2.getName());

		if (!matcher2.find()) {
			return 0;
		}

		String namePart1 = matcher1.group(1);
		String namePart2 = matcher2.group(1);

		if (!namePart1.equals(namePart2)) {
			return 0;
		}

		String customSQLKey1 = _getCustomSQLKey(javaTerm1);
		String customSQLKey2 = _getCustomSQLKey(javaTerm2);

		if ((customSQLKey1 == null) || (customSQLKey2 == null)) {
			return 0;
		}

		int startsWithWeight = StringUtil.startsWithWeight(
			customSQLKey1, customSQLKey2);

		if (startsWithWeight != 0) {
			String startKey = customSQLKey1.substring(0, startsWithWeight);

			if (!startKey.contains("By")) {
				NaturalOrderStringComparator comparator =
					new NaturalOrderStringComparator();

				return comparator.compare(customSQLKey1, customSQLKey2);
			}
		}

		int pos1 = _customSQLContent.indexOf(customSQLKey1);
		int pos2 = _customSQLContent.indexOf(customSQLKey2);

		if ((pos1 != -1) && (pos2 != -1)) {
			return pos1 - pos2;
		}

		int columnCount1 = StringUtil.count(
			javaTerm1.getName(), CharPool.UNDERLINE);
		int columnCount2 = StringUtil.count(
			javaTerm2.getName(), CharPool.UNDERLINE);

		return columnCount1 - columnCount2;
	}

	private int _compareParameterTypes(JavaTerm javaTerm1, JavaTerm javaTerm2) {
		JavaSignature javaSignature1 = javaTerm1.getSignature();

		if ((javaSignature1 == null) || (javaSignature1 == null)) {
			return 0;
		}

		JavaSignature javaSignature2 = javaTerm2.getSignature();

		List<JavaParameter> parameters1 = javaSignature1.getParameters();
		List<JavaParameter> parameters2 = javaSignature2.getParameters();

		if (parameters2.isEmpty()) {
			if (parameters1.isEmpty()) {
				return 0;
			}

			return 1;
		}

		if (parameters1.isEmpty()) {
			return -1;
		}

		for (int i = 0; i < parameters1.size(); i++) {
			if (parameters2.size() < (i + 1)) {
				return 1;
			}

			JavaParameter parameter1 = parameters1.get(i);
			JavaParameter parameter2 = parameters2.get(i);

			String parameterType1 = parameter1.getParameterType(false);
			String parameterType2 = parameter2.getParameterType(false);

			if ((parameters1.size() != parameters2.size()) &&
				(parameterType1.equals(parameterType2.concat("...")) ||
				 parameterType2.equals(parameterType1.concat("...")))) {

				continue;
			}

			if (parameterType1.compareToIgnoreCase(parameterType2) != 0) {
				return parameterType1.compareToIgnoreCase(parameterType2);
			}

			if (parameterType1.compareTo(parameterType2) != 0) {
				return -parameterType1.compareTo(parameterType2);
			}

			parameterType1 = parameter1.getParameterType(true);
			parameterType2 = parameter2.getParameterType(true);

			if (parameterType1.compareToIgnoreCase(parameterType2) != 0) {
				return parameterType1.compareToIgnoreCase(parameterType2);
			}

			if (parameterType1.compareTo(parameterType2) != 0) {
				return -parameterType1.compareTo(parameterType2);
			}
		}

		if (parameters1.size() == parameters2.size()) {
			return 0;
		}

		return -1;
	}

	private String _getCustomSQLKey(JavaTerm javaTerm) {
		JavaClass javaClass = javaTerm.getParentJavaClass();

		String javaClassName = javaClass.getName();

		String objectName = StringUtil.replaceLast(
			javaClassName, "Impl", StringPool.BLANK);

		String javaTermName = javaTerm.getName();

		if (javaTermName.matches("(COUNT|FIND|JOIN)_.*")) {
			Matcher matcher = _sqlKeyPattern.matcher(javaTerm.getContent());

			if (matcher.find()) {
				return objectName + StringPool.PERIOD + matcher.group(1);
			}

			return null;
		}

		String sqlKey = javaTermName.replaceFirst(
			"^(do|filter)", StringPool.BLANK);

		sqlKey =
			StringUtil.toLowerCase(sqlKey.substring(0, 1)) +
				sqlKey.substring(1);

		return objectName + StringPool.PERIOD + sqlKey;
	}

	private int _getType(JavaTerm javaTerm) {
		if (javaTerm.isJavaStaticBlock()) {
			return -1;
		}

		if (javaTerm.isPublic()) {
			if (javaTerm.isStatic()) {
				if (javaTerm.isJavaVariable()) {
					return 1;
				}

				if (javaTerm.isJavaMethod()) {
					return 2;
				}

				if (javaTerm.isJavaClass()) {
					return 6;
				}
			}
			else {
				if (javaTerm.isJavaConstructor()) {
					return 3;
				}

				if (javaTerm.isJavaMethod()) {
					return 4;
				}

				if (javaTerm.isJavaVariable()) {
					return 5;
				}

				if (javaTerm.isJavaClass()) {
					return 7;
				}
			}
		}

		if (javaTerm.isProtected()) {
			if (javaTerm.isStatic()) {
				if (javaTerm.isJavaMethod()) {
					return 8;
				}

				if (javaTerm.isJavaVariable()) {
					return 11;
				}

				if (javaTerm.isJavaClass()) {
					return 13;
				}
			}
			else {
				if (javaTerm.isJavaConstructor()) {
					return 9;
				}

				if (javaTerm.isJavaMethod()) {
					return 10;
				}

				if (javaTerm.isJavaVariable()) {
					return 12;
				}

				if (javaTerm.isJavaClass()) {
					return 14;
				}
			}
		}

		if (javaTerm.isPrivate()) {
			if (javaTerm.isStatic()) {
				if (javaTerm.isJavaMethod()) {
					return 15;
				}

				if (javaTerm.isJavaVariable()) {
					return 18;
				}

				if (javaTerm.isJavaClass()) {
					return 20;
				}
			}
			else {
				if (javaTerm.isJavaConstructor()) {
					return 16;
				}

				if (javaTerm.isJavaMethod()) {
					return 17;
				}

				if (javaTerm.isJavaVariable()) {
					return 19;
				}

				if (javaTerm.isJavaClass()) {
					return 21;
				}
			}
		}

		return -1;
	}

	private static final Pattern _finderPattern = Pattern.compile(
		"((COUNT|FIND|JOIN)_|(do|filter)?([Cc]ount|[Ff]ind)).*");
	private static final Pattern _sqlKeyPattern = Pattern.compile(
		"\"\\.([^\"]+)\";\n");

	private final String _customSQLContent;

}