/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.checkstyle.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class MVCCommandNameCheck extends BaseCheck {

	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.CLASS_DEF};
	}

	@Override
	protected void doVisitToken(DetailAST detailAST) {
		if ((detailAST.getParent() != null) ||
			AnnotationUtil.containsAnnotation(detailAST, "Deprecated")) {

			return;
		}

		String className = getName(detailAST);

		if (!className.endsWith("MVCActionCommand") &&
			!className.endsWith("MVCRenderCommand") &&
			!className.endsWith("MVCResourceCommand")) {

			return;
		}

		String absolutePath = getAbsolutePath();

		int pos = absolutePath.lastIndexOf("-web/");

		if (pos == -1) {
			return;
		}

		DetailAST annotationDetailAST = AnnotationUtil.getAnnotation(
			detailAST, "Component");

		if (annotationDetailAST == null) {
			return;
		}

		DetailAST propertyAnnotationMemberValuePairDetailAST =
			getAnnotationMemberValuePairDetailAST(
				annotationDetailAST, "property");

		if (propertyAnnotationMemberValuePairDetailAST == null) {
			return;
		}

		DetailAST annotationArrayInitDetailAST =
			propertyAnnotationMemberValuePairDetailAST.findFirstToken(
				TokenTypes.ANNOTATION_ARRAY_INIT);

		if (annotationArrayInitDetailAST == null) {
			return;
		}

		Map<String, Integer> mvcCommandNamesMap = _getMVCCommandNamesMap(
			annotationArrayInitDetailAST);

		if (mvcCommandNamesMap.isEmpty()) {
			return;
		}

		String modulePath = absolutePath.substring(0, pos);

		boolean validateActionName = false;

		if (mvcCommandNamesMap.size() == 1) {
			validateActionName = true;
		}

		for (Map.Entry<String, Integer> entry : mvcCommandNamesMap.entrySet()) {
			String mvcCommandName = entry.getKey();

			if (!_hasValidMVCCommandName(
					mvcCommandName, detailAST, annotationArrayInitDetailAST,
					className, modulePath, validateActionName)) {

				log(entry.getValue(), _MSG_INCORRECT_VALUE, mvcCommandName);
			}
		}
	}

	private String _getExpectedActionName(String className) {
		String name = StringUtil.removeSubstrings(
			className, "MVCActionCommand", "MVCRenderCommand",
			"MVCResourceCommand");

		name = StringUtil.replace(
			name, new String[] {"OAuth", "WebDAV", "WeDeploy"},
			new String[] {"Oauth", "Webdav", "Wedeploy"});

		Matcher matcher = _classNamePattern.matcher(className);

		while (matcher.find()) {
			String match = matcher.group(1);

			name = StringUtil.replaceFirst(
				name, match, StringUtil.lowerCase(match), matcher.start(1));
		}

		name = StringUtil.replace(
			TextFormatter.format(name, TextFormatter.K), CharPool.DASH,
			CharPool.UNDERLINE);

		return StringUtil.replace(name, "mfafido2", "mfa_fido2");
	}

	private Map<String, Integer> _getMVCCommandNamesMap(
		DetailAST annotationArrayInitDetailAST) {

		Map<String, Integer> mvcCommandNamesMap = new HashMap<>();

		List<DetailAST> expressionDetailASTs = getAllChildTokens(
			annotationArrayInitDetailAST, false, TokenTypes.EXPR);

		for (DetailAST expressionDetailAST : expressionDetailASTs) {
			DetailAST firstChildDetailAST = expressionDetailAST.getFirstChild();

			if (firstChildDetailAST.getType() != TokenTypes.STRING_LITERAL) {
				continue;
			}

			String value = firstChildDetailAST.getText();

			if (value.startsWith("\"mvc.command.name=") &&
				!value.equals("\"mvc.command.name=/\"")) {

				mvcCommandNamesMap.put(
					value.substring(18, value.length() - 1),
					expressionDetailAST.getLineNo());
			}
		}

		return mvcCommandNamesMap;
	}

	private String _getTrimmedPath(
		String[] pathArray, int abbreviatePathCount) {

		StringBundler sb = new StringBundler(pathArray.length);

		for (int i = 0; i < pathArray.length; i++) {
			String s = pathArray[i];

			if (i < abbreviatePathCount) {
				sb.append(s.charAt(0));
			}
			else {
				sb.append(s);
			}
		}

		return sb.toString();
	}

	private boolean _hasValidActionName(
		String actionName, String path, String className) {

		if (actionName.equals(_getExpectedActionName(className))) {
			return true;
		}

		String[] pathArray = StringUtil.split(path, CharPool.UNDERLINE);

		for (int i = 0; i < pathArray.length; i++) {
			String trimmedPath = _getTrimmedPath(pathArray, i);

			if (StringUtil.startsWith(className, trimmedPath) &&
				actionName.equals(
					_getExpectedActionName(
						className.substring(trimmedPath.length())))) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasValidMVCCommandName(
		String mvcCommandName, DetailAST classDefinitionDetailAST,
		DetailAST annotationArrayInitDetailAST, String className,
		String modulePath, boolean validateActionName) {

		if (!mvcCommandName.startsWith(StringPool.SLASH) &&
			!mvcCommandName.startsWith(StringPool.TILDE)) {

			return false;
		}

		char delimiter = mvcCommandName.charAt(0);

		String[] parts = StringUtil.split(
			mvcCommandName.substring(1), delimiter);

		if (parts.length != 2) {
			return false;
		}

		String actionName = parts[1];
		String path = parts[0];

		if ((!validateActionName ||
			 _hasValidActionName(actionName, path, className)) &&
			_hasValidPath(
				path, annotationArrayInitDetailAST, delimiter, modulePath)) {

			return true;
		}

		List<DetailAST> annotationDetailASTs = getAllChildTokens(
			classDefinitionDetailAST, true, TokenTypes.ANNOTATION);

		for (DetailAST annotationDetailAST : annotationDetailASTs) {
			if (!Objects.equals(getName(annotationDetailAST), "Reference")) {
				continue;
			}

			DetailAST targetAnnotationMemberValuePairDetailAST =
				getAnnotationMemberValuePairDetailAST(
					annotationDetailAST, "target");

			if (targetAnnotationMemberValuePairDetailAST == null) {
				continue;
			}

			FullIdent fullIdent = FullIdent.createFullIdentBelow(
				targetAnnotationMemberValuePairDetailAST.getLastChild());

			Matcher matcher = _componentNamePattern.matcher(
				fullIdent.getText());

			if (matcher.find()) {
				String expectedPath = StringUtil.replace(
					matcher.group(1), CharPool.PERIOD, CharPool.UNDERLINE);

				if (path.equals(expectedPath) &&
					actionName.equals(
						_getExpectedActionName(matcher.group(2)))) {

					return true;
				}
			}
		}

		return false;
	}

	private boolean _hasValidPath(
		String path, DetailAST annotationArrayInitDetailAST, char delimiter,
		String modulePath) {

		String moduleName = modulePath.substring(
			modulePath.lastIndexOf(delimiter) + 1);

		if (path.equals(StringUtil.replace(moduleName, '-', '_'))) {
			return true;
		}

		List<DetailAST> stringLiteralDetailASTs = getAllChildTokens(
			annotationArrayInitDetailAST, true, TokenTypes.STRING_LITERAL);

		for (DetailAST stringLiteralDetailAST : stringLiteralDetailASTs) {
			if (!Objects.equals(
					stringLiteralDetailAST.getText(),
					"\"jakarta.portlet.name=\"")) {

				continue;
			}

			for (String name :
					getNames(stringLiteralDetailAST.getParent(), true)) {

				if (StringUtil.equalsIgnoreCase(path, name)) {
					return true;
				}
			}
		}

		File file = new File(
			modulePath + "-web/src/main/resources/META-INF/resources/" + path);

		return file.exists();
	}

	private static final String _MSG_INCORRECT_VALUE = "value.incorrect";

	private static final Pattern _classNamePattern = Pattern.compile(
		"[A-Z]([A-Z]+)s([A-Z]|\\Z)");
	private static final Pattern _componentNamePattern = Pattern.compile(
		"^\"\\(component\\.name=com\\.liferay\\.(.*)\\.web\\.internal" +
			"\\..*\\.(\\w+MVC\\w+Command)\\)\"$");

}