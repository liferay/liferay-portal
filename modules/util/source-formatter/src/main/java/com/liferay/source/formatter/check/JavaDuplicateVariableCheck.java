/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class JavaDuplicateVariableCheck extends BaseJavaTermCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		JavaClass javaClass = (JavaClass)javaTerm;

		if (javaClass.isAnonymous() ||
			(javaClass.getParentJavaClass() != null)) {

			return javaTerm.getContent();
		}

		List<String> extendedClassNames = javaClass.getExtendedClassNames(true);

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if ((childJavaTerm instanceof JavaVariable) &&
				!childJavaTerm.isFinal() &&
				!childJavaTerm.hasAnnotation("Deprecated") &&
				(childJavaTerm.isPublic() || childJavaTerm.isProtected())) {

				_checkDuplicateVariable(
					fileName, absolutePath, extendedClassNames,
					(JavaVariable)childJavaTerm, javaClass.getPackageName());
			}
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private void _checkDuplicateVariable(
			String fileName, String absolutePath,
			List<String> extendedClassNames, JavaVariable javaVariable,
			String packageName)
		throws IOException {

		String variableName = javaVariable.getName();

		for (String extendedClassName : extendedClassNames) {
			Map<String, List<JavaVariable>> extendedJavaVariablesMap =
				_getJavaVariablesMap(absolutePath, extendedClassName);

			for (Map.Entry<String, List<JavaVariable>> entry :
					extendedJavaVariablesMap.entrySet()) {

				List<JavaVariable> javaVariables = entry.getValue();

				for (JavaVariable curJavaVariable : javaVariables) {
					if (!variableName.equals(curJavaVariable.getName())) {
						continue;
					}

					String fullyQualifiedClassName = entry.getKey();

					int pos = fullyQualifiedClassName.lastIndexOf(
						CharPool.PERIOD);

					String variablePackageName =
						fullyQualifiedClassName.substring(0, pos);

					if (curJavaVariable.isPublic() ||
						(curJavaVariable.isProtected() &&
						 packageName.equals(variablePackageName))) {

						String variableClassName =
							fullyQualifiedClassName.substring(pos + 1);

						addMessage(
							fileName,
							StringBundler.concat(
								javaVariable.getAccessModifier(),
								" variable \"", variableName,
								"\" already exists in extended class \"",
								variableClassName, "\""),
							javaVariable.getLineNumber());
					}
				}
			}
		}
	}

	private Map<String, List<JavaVariable>> _getJavaVariablesMap(
			String absolutePath, String fullyQualifiedClassName)
		throws IOException {

		Map<String, List<JavaVariable>> javaVariablesMap =
			_javaVariablesMap.get(fullyQualifiedClassName);

		if (javaVariablesMap != null) {
			return javaVariablesMap;
		}

		javaVariablesMap = new HashMap<>();

		File file = JavaSourceUtil.getJavaFile(
			fullyQualifiedClassName, _getRootDirName(absolutePath),
			getBundleSymbolicNamesMap(absolutePath));

		if (file != null) {
			try {
				JavaClass javaClass = JavaClassParser.parseJavaClass(
					SourceUtil.getAbsolutePath(file), FileUtil.read(file));

				javaVariablesMap.putAll(
					_getJavaVariablesMap(
						absolutePath, fullyQualifiedClassName, javaClass));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		_javaVariablesMap.put(fullyQualifiedClassName, javaVariablesMap);

		return javaVariablesMap;
	}

	private Map<String, List<JavaVariable>> _getJavaVariablesMap(
			String absolutePath, String fullyQualifiedName, JavaClass javaClass)
		throws IOException {

		Map<String, List<JavaVariable>> javaVariablesMap =
			HashMapBuilder.<String, List<JavaVariable>>put(
				fullyQualifiedName,
				() -> {
					List<JavaVariable> javaVariables = new ArrayList<>();

					for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
						if ((javaTerm instanceof JavaVariable) &&
							(javaTerm.isProtected() || javaTerm.isPublic())) {

							javaVariables.add((JavaVariable)javaTerm);
						}
					}

					return javaVariables;
				}
			).build();

		List<String> extendedClassNames = javaClass.getExtendedClassNames(true);

		for (String extendedClassName : extendedClassNames) {
			javaVariablesMap.putAll(
				_getJavaVariablesMap(absolutePath, extendedClassName));
		}

		return javaVariablesMap;
	}

	private synchronized String _getRootDirName(String absolutePath) {
		if (_rootDirName != null) {
			return _rootDirName;
		}

		_rootDirName = SourceUtil.getRootDirName(absolutePath);

		return _rootDirName;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaDuplicateVariableCheck.class);

	private final Map<String, Map<String, List<JavaVariable>>>
		_javaVariablesMap = new HashMap<>();
	private String _rootDirName;

}