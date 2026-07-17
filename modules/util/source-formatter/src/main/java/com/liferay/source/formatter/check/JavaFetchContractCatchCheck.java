/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaFetchContractCatchCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		String content = javaTerm.getContent();

		if (_isAllowedFileName(
				absolutePath,
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY, absolutePath))) {

			return content;
		}

		Matcher matcher = _catchPattern.matcher(content);

		while (matcher.find()) {
			String exceptionClassName = matcher.group(1);

			int x = exceptionClassName.lastIndexOf('.');

			String simpleClassName = exceptionClassName.substring(x + 1);

			// Only the Liferay checked not-found exceptions signal absence.
			// The JDK "NoSuch*Exception" classes (reflection, crypto,
			// collections) are unrelated and legitimately caught.

			if (!simpleClassName.equals("PortalException") &&
				(!simpleClassName.matches("NoSuch\\w*Exception") ||
				 _jdkNoSuchExceptionClassNames.contains(simpleClassName))) {

				continue;
			}

			String catchBlockBody = _getBlockBody(content, matcher.end());

			if ((catchBlockBody == null) ||
				!_isSwallowToSentinel(catchBlockBody)) {

				continue;
			}

			String tryBlockBody = _getTryBlockBody(content, matcher.start());

			if (tryBlockBody == null) {
				continue;
			}

			String lookupCall = _getSoleLookupCall(tryBlockBody);

			if (lookupCall == null) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Do not catch \"", simpleClassName,
					"\" around the lookup \"", lookupCall,
					"\" to signal a missing entity, call the null-tolerant ",
					"fetch sibling and check for null instead"),
				javaTerm.getLineNumber(matcher.start()));
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private String _getBlockBody(String content, int openCurlyBracePos) {
		int level = 1;

		for (int i = openCurlyBracePos; i < content.length(); i++) {
			char c = content.charAt(i);

			if (c == '{') {
				level++;
			}
			else if (c == '}') {
				level--;

				if (level == 0) {
					return content.substring(openCurlyBracePos, i);
				}
			}
		}

		return null;
	}

	private int _getMatchingOpenCurlyBracePos(
		String content, int closeCurlyBracePos) {

		int level = 1;

		for (int i = closeCurlyBracePos - 1; i >= 0; i--) {
			char c = content.charAt(i);

			if (c == '}') {
				level++;
			}
			else if (c == '{') {
				level--;

				if (level == 0) {
					return i;
				}
			}
		}

		return -1;
	}

	private String _getPrecedingWord(String content, int endPos) {
		int i = endPos - 1;

		while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
			i--;
		}

		int wordEndPos = i + 1;

		while ((i >= 0) && Character.isJavaIdentifierPart(content.charAt(i))) {
			i--;
		}

		return content.substring(i + 1, wordEndPos);
	}

	private String _getSoleLookupCall(String tryBlockBody) {
		Matcher matcher = _callPattern.matcher(tryBlockBody);

		if (!matcher.find()) {
			return null;
		}

		String receiver = matcher.group(1);
		String method = matcher.group(2);

		int callStartPos = matcher.start();
		int argsStartPos = matcher.end();

		if (matcher.find()) {
			return null;
		}

		String lowerCaseReceiver = StringUtil.toLowerCase(receiver);

		if (!lowerCaseReceiver.endsWith("localservice") &&
			!lowerCaseReceiver.endsWith("localserviceutil") &&
			!lowerCaseReceiver.endsWith("persistence")) {

			return null;
		}

		if ((!method.startsWith("get") || method.equals("get")) &&
			(!method.startsWith("findBy") || method.equals("findBy")) &&
			(!method.equals("remove") ||
			 !lowerCaseReceiver.endsWith("persistence"))) {

			return null;
		}

		int level = 1;
		int argsEndPos = -1;

		for (int i = argsStartPos; i < tryBlockBody.length(); i++) {
			char c = tryBlockBody.charAt(i);

			if (c == '(') {
				level++;
			}
			else if (c == ')') {
				level--;

				if (level == 0) {
					argsEndPos = i;

					break;
				}
			}
		}

		if (argsEndPos == -1) {
			return null;
		}

		// The arguments must not contain nested invocations and the rest of
		// the try block must not contain any other invocation or block, so
		// that the lookup is provably the only statement able to throw the
		// caught exception

		String args = tryBlockBody.substring(argsStartPos, argsEndPos);

		if (args.indexOf('(') != -1) {
			return null;
		}

		String remainder =
			tryBlockBody.substring(0, callStartPos) +
				tryBlockBody.substring(argsEndPos + 1);

		if ((remainder.indexOf('(') != -1) || (remainder.indexOf('{') != -1)) {
			return null;
		}

		return receiver + "." + method;
	}

	private String _getTryBlockBody(String content, int catchStartPos) {
		int i = catchStartPos - 1;

		while (true) {
			while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
				i--;
			}

			if ((i < 0) || (content.charAt(i) != '}')) {
				return null;
			}

			int openCurlyBracePos = _getMatchingOpenCurlyBracePos(content, i);

			if (openCurlyBracePos == -1) {
				return null;
			}

			int j = openCurlyBracePos - 1;

			while ((j >= 0) && Character.isWhitespace(content.charAt(j))) {
				j--;
			}

			if ((j >= 0) && (content.charAt(j) == ')')) {
				int level = 1;
				int k = j - 1;

				while (k >= 0) {
					char c = content.charAt(k);

					if (c == ')') {
						level++;
					}
					else if (c == '(') {
						level--;

						if (level == 0) {
							break;
						}
					}

					k--;
				}

				if (k < 0) {
					return null;
				}

				String word = _getPrecedingWord(content, k);

				if (word.equals("catch")) {
					i = content.lastIndexOf("catch", k) - 1;

					continue;
				}

				if (word.equals("try")) {
					return content.substring(openCurlyBracePos + 1, i);
				}

				return null;
			}

			String word = _getPrecedingWord(content, j + 1);

			if (word.equals("try")) {
				return content.substring(openCurlyBracePos + 1, i);
			}

			return null;
		}
	}

	private boolean _isAllowedFileName(
		String absolutePath, List<String> allowedFileNames) {

		for (String allowedFileName : allowedFileNames) {
			if (absolutePath.endsWith(allowedFileName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isSwallowToSentinel(String catchBlockBody) {
		String strippedBody = catchBlockBody;

		// Remove comments

		strippedBody = strippedBody.replaceAll("(?s)/\\*.*?\\*/", "");
		strippedBody = strippedBody.replaceAll("(?m)//.*$", "");

		// Remove the logging: an "if (_log.isXXXEnabled())" block and bare
		// "_log" statements

		strippedBody = strippedBody.replaceAll(
			"(?s)if \\(_log\\.is\\w+Enabled\\(\\)\\) \\{[^{}]*\\}", "");

		strippedBody = _stripLogStatements(strippedBody);

		strippedBody = strippedBody.trim();

		// A catch that does nothing but (optionally log and) return the
		// null/false absence sentinel is using the exception as control flow

		if (strippedBody.equals("return null;") ||
			strippedBody.equals("return false;")) {

			return true;
		}

		return false;
	}

	private String _stripLogStatements(String s) {
		while (true) {
			int x = s.indexOf("_log.");

			if (x == -1) {
				return s;
			}

			int y = s.indexOf('(', x);

			if (y == -1) {
				return s;
			}

			int level = 1;
			int i = y + 1;

			while ((i < s.length()) && (level > 0)) {
				char c = s.charAt(i);

				if (c == '(') {
					level++;
				}
				else if (c == ')') {
					level--;
				}

				i++;
			}

			while ((i < s.length()) &&
				   ((s.charAt(i) == ';') ||
					Character.isWhitespace(s.charAt(i)))) {

				i++;

				if (s.charAt(i - 1) == ';') {
					break;
				}
			}

			s = s.substring(0, x) + s.substring(i);
		}
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

	private static final Pattern _callPattern = Pattern.compile(
		"(\\w+)\\s*\\.\\s*(\\w+)\\s*\\(");
	private static final Pattern _catchPattern = Pattern.compile(
		"catch \\(([\\w.]+)\\s+\\w+\\)\\s*\\{");
	private static final Set<String> _jdkNoSuchExceptionClassNames =
		new HashSet<>(
			Arrays.asList(
				"NoSuchAlgorithmException", "NoSuchElementException",
				"NoSuchFieldException", "NoSuchFileException",
				"NoSuchMechanismException", "NoSuchMethodException",
				"NoSuchObjectException", "NoSuchPaddingException",
				"NoSuchProviderException"));

}