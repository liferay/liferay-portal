/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Sandeep Soni
 */
public class ClassUtil {

	public static String getClassName(Object object) {
		if (object == null) {
			return null;
		}

		Class<?> clazz = object.getClass();

		return clazz.getName();
	}

	public static Set<String> getClasses(File file) throws IOException {
		String fileName = file.getName();

		if (fileName.endsWith(".java")) {
			fileName = fileName.substring(0, fileName.length() - 5);
		}

		return getClasses(
			new UnsyncBufferedReader(new FileReader(file)), fileName);
	}

	public static Set<String> getClasses(Reader reader, String className)
		throws IOException {

		Set<String> classes = new HashSet<>();

		StreamTokenizer st = new StreamTokenizer(reader);

		_setupParseTableForAnnotationProcessing(st);

		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.equals("class") || st.sval.equals("enum") ||
					st.sval.equals("interface") ||
					st.sval.equals("@interface")) {

					break;
				}
				else if (st.sval.startsWith("@")) {
					st.ordinaryChar(' ');
					st.wordChars('=', '=');
					st.wordChars('+', '+');
					st.wordChars('-', '-');

					String[] annotationClasses = _processAnnotation(
						st.sval, st);

					for (String annotationClass : annotationClasses) {
						classes.add(annotationClass);
					}

					_setupParseTableForAnnotationProcessing(st);
				}
			}
		}

		_setupParseTable(st);

		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			if (st.ttype == StreamTokenizer.TT_WORD) {
				int firstIndex = st.sval.indexOf('.');

				if (firstIndex >= 0) {
					classes.add(st.sval.substring(0, firstIndex));
				}

				int lastIndex = st.sval.lastIndexOf('.');

				if (lastIndex >= 0) {
					classes.add(st.sval.substring(lastIndex + 1));
				}

				if (firstIndex < 0) {
					classes.add(st.sval);
				}
			}
			else if ((st.ttype != StreamTokenizer.TT_NUMBER) &&
					 (st.ttype != StreamTokenizer.TT_EOL)) {

				if (Character.isUpperCase((char)st.ttype)) {
					classes.add(String.valueOf((char)st.ttype));
				}
			}
		}

		classes.remove(className);

		return classes;
	}

	public static boolean isSubclass(Class<?> a, Class<?> b) {
		if (a == b) {
			return true;
		}

		if ((a == null) || (b == null)) {
			return false;
		}

		for (Class<?> x = a; x != null; x = x.getSuperclass()) {
			if (x == b) {
				return true;
			}

			if (b.isInterface()) {
				Class<?>[] interfaceClasses = x.getInterfaces();

				for (Class<?> interfaceClass : interfaceClasses) {
					if (isSubclass(interfaceClass, b)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean isSubclass(Class<?> a, String s) {
		if ((a == null) || (s == null)) {
			return false;
		}

		String name = a.getName();

		if (name.equals(s)) {
			return true;
		}

		for (Class<?> x = a; x != null; x = x.getSuperclass()) {
			name = x.getName();

			if (name.equals(s)) {
				return true;
			}

			Class<?>[] interfaceClasses = x.getInterfaces();

			for (Class<?> interfaceClass : interfaceClasses) {
				if (isSubclass(interfaceClass, s)) {
					return true;
				}
			}
		}

		return false;
	}

	private static String[] _processAnnotation(String s, StreamTokenizer st)
		throws IOException {

		s = s.trim();

		List<String> tokens = new ArrayList<>();

		Matcher annotationNameMatcher = _annotationNamePattern.matcher(s);
		Matcher annotationParametersMatcher =
			_annotationParametersPattern.matcher(s);

		if (annotationNameMatcher.matches()) {
			tokens.add(annotationNameMatcher.group(1));
		}
		else if (annotationParametersMatcher.matches()) {
			tokens.add(annotationParametersMatcher.group(1));

			String annotationParameters = null;

			String trimmedString = s.trim();

			if (trimmedString.endsWith(")")) {
				annotationParameters = annotationParametersMatcher.group(3);
			}
			else {
				annotationParameters = s.substring(s.indexOf('('));

				while (st.nextToken() != StreamTokenizer.TT_EOF) {
					if (st.ttype != StreamTokenizer.TT_WORD) {
						continue;
					}

					annotationParameters += st.sval;

					String trimmedValue = StringUtil.trim(st.sval);

					if (!trimmedValue.endsWith(")")) {
						continue;
					}

					int closeParenthesesCount = StringUtil.count(
						annotationParameters, ')');
					int openParenthesesCount = StringUtil.count(
						annotationParameters, '(');

					if (closeParenthesesCount == openParenthesesCount) {
						break;
					}
				}
			}

			tokens = _processAnnotationParameters(annotationParameters, tokens);
		}

		return tokens.toArray(new String[0]);
	}

	private static List<String> _processAnnotationParameters(
			String s, List<String> tokens)
		throws IOException {

		StreamTokenizer st = new StreamTokenizer(new UnsyncStringReader(s));

		_setupParseTable(st);

		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			if (st.ttype == StreamTokenizer.TT_WORD) {
				if (st.sval.indexOf('.') >= 0) {
					tokens.add(st.sval.substring(0, st.sval.indexOf('.')));
				}
				else {
					tokens.add(st.sval);
				}
			}
		}

		return tokens;
	}

	private static void _setupParseTable(StreamTokenizer st) {
		st.resetSyntax();
		st.slashSlashComments(true);
		st.slashStarComments(true);
		st.wordChars('a', 'z');
		st.wordChars('A', 'Z');
		st.wordChars('.', '.');
		st.wordChars('0', '9');
		st.wordChars('_', '_');
		st.lowerCaseMode(false);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		st.quoteChar('\'');
		st.parseNumbers();
	}

	private static void _setupParseTableForAnnotationProcessing(
		StreamTokenizer st) {

		_setupParseTable(st);

		st.wordChars('@', '@');
		st.wordChars('(', '(');
		st.wordChars(')', ')');
		st.wordChars('{', '{');
		st.wordChars('}', '}');
		st.wordChars(',', ',');
	}

	private static final Pattern _annotationNamePattern = Pattern.compile(
		"@(\\w+)\\.?(\\w*)$");
	private static final Pattern _annotationParametersPattern = Pattern.compile(
		"@(\\w+)\\.?(\\w*)\\({0,1}\\{{0,1}([^)}]+)\\}{0,1}\\){0,1}");

}