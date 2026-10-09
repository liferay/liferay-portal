/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.PortalJSONObjectUtil;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Hugo Huijser
 */
public class JavaMissingOverrideCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (javaClass.hasAnnotation("Deprecated") ||
			(javaClass.getParentJavaClass() != null)) {

			return javaClass.getContent();
		}

		Set<JavaMethod> javaMethods = new TreeSet<>(
			new Comparator<JavaTerm>() {

				public int compare(JavaTerm javaTerm1, JavaTerm javaTerm2) {
					return javaTerm2.getLineNumber() -
						javaTerm1.getLineNumber();
				}

			});

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod() || childJavaTerm.isStatic() ||
				childJavaTerm.hasAnnotation("Deprecated", "Override") ||
				Objects.equals(childJavaTerm.getName(), "main") ||
				!_hasSuperMethod(
					(JavaMethod)childJavaTerm, javaClass.getName(true),
					false)) {

				continue;
			}

			javaMethods.add((JavaMethod)childJavaTerm);
		}

		return _fixMissingOverrideAnnotations(javaClass, javaMethods);
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private boolean _equals(
		JavaMethod javaMethod, JSONObject methodJSONObject) {

		if (!Objects.equals(
				javaMethod.getAccessModifier(),
				methodJSONObject.getString("accessModifier")) ||
			!Objects.equals(
				javaMethod.getName(), methodJSONObject.getString("name"))) {

			return false;
		}

		JavaSignature javaSignature = javaMethod.getSignature();

		if (!Objects.equals(
				javaSignature.getReturnType(true),
				methodJSONObject.getString("returnType"))) {

			return false;
		}

		List<JavaParameter> javaParameters = javaSignature.getParameters();
		JSONArray parametersJSONArray = methodJSONObject.getJSONArray(
			"parameters");

		if (parametersJSONArray == null) {
			return javaParameters.isEmpty();
		}

		if (javaParameters.size() != parametersJSONArray.length()) {
			return false;
		}

		Iterator<String> iterator = parametersJSONArray.iterator();

		int i = 0;

		while (iterator.hasNext()) {
			JavaParameter javaParameter = javaParameters.get(i++);

			if (!Objects.equals(
					javaParameter.getParameterType(true), iterator.next())) {

				return false;
			}
		}

		return true;
	}

	private String _fixMissingOverrideAnnotations(
		JavaClass javaClass, Set<JavaMethod> javaMethods) {

		String content = javaClass.getContent();

		for (JavaMethod javaMethod : javaMethods) {
			int lineNumber =
				javaMethod.getLineNumber() - javaClass.getLineNumber();

			for (int i = lineNumber + 1;; i++) {
				String line = StringUtil.trim(getLine(content, i));

				if (line == null) {
					return content;
				}

				if (line.startsWith(javaMethod.getAccessModifier())) {
					content = StringUtil.replaceFirst(
						content, line, "@Override\n" + line,
						getLineStartPos(content, lineNumber));

					break;
				}
			}
		}

		return content;
	}

	private synchronized JSONObject _getJavaClassesJSONObject()
		throws Exception {

		if (_javaClassesJSONObject != null) {
			return _javaClassesJSONObject;
		}

		JSONObject portalJSONObject = PortalJSONObjectUtil.getPortalJSONObject(
			getBaseDirName(), getSourceFormatterExcludes(), getMaxLineLength());

		_javaClassesJSONObject = portalJSONObject.getJSONObject("javaClasses");

		return _javaClassesJSONObject;
	}

	private boolean _hasMethod(
		JSONObject classJSONObject, JavaMethod javaMethod) {

		JSONArray methodsJSONArray = classJSONObject.getJSONArray("methods");

		if (methodsJSONArray == null) {
			return false;
		}

		Iterator<JSONObject> iterator = methodsJSONArray.iterator();

		while (iterator.hasNext()) {
			if (_equals(javaMethod, iterator.next())) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasSuperMethod(
			JavaMethod javaMethod, JSONArray classNamesJSONArray)
		throws Exception {

		if (classNamesJSONArray == null) {
			return false;
		}

		Iterator<String> iterator = classNamesJSONArray.iterator();

		while (iterator.hasNext()) {
			if (_hasSuperMethod(javaMethod, iterator.next(), true)) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasSuperMethod(
			JavaMethod javaMethod, String className, boolean superClass)
		throws Exception {

		JSONObject javaClassesJSONObject = _getJavaClassesJSONObject();

		JSONObject classJSONObject = javaClassesJSONObject.getJSONObject(
			className);

		if (classJSONObject == null) {
			return false;
		}

		if ((superClass && _hasMethod(classJSONObject, javaMethod)) ||
			_hasSuperMethod(
				javaMethod,
				classJSONObject.getJSONArray("extendedClassNames")) ||
			_hasSuperMethod(
				javaMethod,
				classJSONObject.getJSONArray("implementedClassNames"))) {

			return true;
		}

		return false;
	}

	private JSONObject _javaClassesJSONObject;

}