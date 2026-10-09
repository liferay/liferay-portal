/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.BNDSettings;
import com.liferay.source.formatter.check.util.JavaSourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Seiphon Wang
 */
public class JavaModuleInheritableVariableAccessModifierCheck
	extends BaseJavaTermCheck {

	@Override
	public boolean isModuleSourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		if ((javaTerm.getParentJavaClass() != null) ||
			!fileContent.contains("@Reference")) {

			return javaTerm.getContent();
		}

		JavaClass javaClass = (JavaClass)javaTerm;

		String packageName = javaClass.getPackageName();

		if (!packageName.startsWith("com.liferay")) {
			return javaTerm.getContent();
		}

		Map<String, List<String>> osgiComponentFileNamesMap =
			_getOSGiComponentFileNamesMap();

		Set<String> superClassNames = osgiComponentFileNamesMap.keySet();

		if (!superClassNames.contains(
				packageName + "." + JavaSourceUtil.getClassName(fileName))) {

			return javaTerm.getContent();
		}

		List<JavaTerm> childJavaTerms = javaClass.getChildJavaTerms();

		for (JavaTerm childJavaTerm : childJavaTerms) {
			if (!childJavaTerm.isJavaVariable()) {
				continue;
			}

			JavaVariable javaVariable = (JavaVariable)childJavaTerm;

			String accessModifier = javaVariable.getAccessModifier();
			String variableContent = javaVariable.getContent();

			if (!accessModifier.equals("private") ||
				!variableContent.contains("@Reference")) {

				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"The access modifier of variable \"",
					javaVariable.getName(),
					"\" should be protected as the subclass has ",
					"\"-dsannotations-options: inherit\" in bnd.bnd"),
				javaVariable.getLineNumber());
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private synchronized Map<String, List<String>>
			_getOSGiComponentFileNamesMap()
		throws Exception {

		if (_osgiComponentFileNamesMap != null) {
			return _osgiComponentFileNamesMap;
		}

		_osgiComponentFileNamesMap = new HashMap<>();

		String moduleRootDirLocation = "modules/";

		List<String> fileNames = new ArrayList<>();

		for (int i = 0; i < getMaxDirLevel(); i++) {
			File file = new File(getBaseDirName() + moduleRootDirLocation);

			if (file.exists()) {
				fileNames = SourceFormatterUtil.matchFileContentsForFileNames(
					Arrays.asList("-E", "@Component\\("),
					file.getCanonicalPath(),
					new String[] {"apps/**/*.java", "dxp/apps/**/*.java"});

				break;
			}

			moduleRootDirLocation = "../" + moduleRootDirLocation;
		}

		for (String fileName : fileNames) {
			fileName = StringUtil.replace(
				fileName, CharPool.BACK_SLASH, CharPool.SLASH);

			if (fileName.contains("/src/test/java/") ||
				fileName.contains("/test/unit/")) {

				continue;
			}

			File file = new File(fileName);

			if (!file.exists()) {
				continue;
			}

			BNDSettings bndSettings = getBNDSettings(fileName);

			if (bndSettings == null) {
				continue;
			}

			String bndSettingsContent = bndSettings.getContent();

			if (!bndSettingsContent.contains(
					"-dsannotations-options: inherit")) {

				continue;
			}

			String className = JavaSourceUtil.getClassName(fileName);

			String superClassName = _getSuperClassName(
				FileUtil.read(file), className);

			if (superClassName == null) {
				continue;
			}

			List<String> subclassNames = _osgiComponentFileNamesMap.get(
				superClassName);

			if (subclassNames == null) {
				subclassNames = new ArrayList<>();
			}

			subclassNames.add(className);

			_osgiComponentFileNamesMap.put(superClassName, subclassNames);
		}

		return _osgiComponentFileNamesMap;
	}

	private String _getSuperClassName(String content, String className) {
		Pattern pattern = Pattern.compile(
			className + "\\s+extends\\s+(\\w+)\\W");

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		String superClassName = matcher.group(1);

		List<String> importNames = JavaSourceUtil.getImportNames(content);

		for (String importName : importNames) {
			if (importName.endsWith("." + superClassName)) {
				return importName;
			}
		}

		return JavaSourceUtil.getPackageName(content) + "." + superClassName;
	}

	private Map<String, List<String>> _osgiComponentFileNamesMap;

}