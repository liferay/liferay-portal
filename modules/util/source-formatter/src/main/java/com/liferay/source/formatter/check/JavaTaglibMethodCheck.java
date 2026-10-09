/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.check.util.TaglibUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class JavaTaglibMethodCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	public void setAllFileNames(List<String> allFileNames) {
		_allFileNames = allFileNames;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws Exception {

		JavaClass javaClass = (JavaClass)javaTerm;

		String className = javaClass.getName();

		if (!className.endsWith("Tag")) {
			return javaTerm.getContent();
		}

		List<JavaClass> extendedTagJavaClasses = _getExtendedTagJavaClasses(
			javaClass, absolutePath);

		if (!_containsClass(
				extendedTagJavaClasses, "com.liferay.taglib.util.IncludeTag") ||
			(extendedTagJavaClasses == null)) {

			return javaTerm.getContent();
		}

		List<String> tagAttributeNames = _getTagAttributeNames(
			javaClass.getPackageName(), javaClass.getName());

		if (tagAttributeNames == null) {
			return javaTerm.getContent();
		}

		List<JavaMethod> javaMethods = _getJavaMethods(
			javaClass, extendedTagJavaClasses);

		for (String tagAttributeName : tagAttributeNames) {
			JavaMethod setterJavaMethod = _getJavaMethod(
				javaMethods, "set" + tagAttributeName);

			if (setterJavaMethod == null) {
				addMessage(
					fileName,
					"Missing getter/setter methods for attribute \"" +
						tagAttributeName + "\"");

				continue;
			}

			if (setterJavaMethod.hasAnnotation("Deprecated")) {
				continue;
			}

			JavaParameter javaParameter = _getJavaParameter(setterJavaMethod);

			if (javaParameter == null) {
				continue;
			}

			String expectedGetterMethodName = StringUtil.replaceFirst(
				setterJavaMethod.getName(), "set", "get");

			JavaMethod getterJavaMethod = _getJavaMethod(
				javaMethods, expectedGetterMethodName);

			if (getterJavaMethod != null) {
				continue;
			}

			if (StringUtil.equalsIgnoreCase(
					javaParameter.getParameterType(), "boolean")) {

				expectedGetterMethodName = StringUtil.replaceFirst(
					setterJavaMethod.getName(), "set", "is");

				getterJavaMethod = _getJavaMethod(
					javaMethods, expectedGetterMethodName);

				if (getterJavaMethod != null) {
					continue;
				}
			}

			JavaClass setterMethodJavaClass =
				setterJavaMethod.getParentJavaClass();

			if (Objects.equals(
					javaClass.getName(true),
					setterMethodJavaClass.getName(true))) {

				String getterMethodContent = _constructGetterMethodContent(
					setterJavaMethod.getContent(),
					javaParameter.getParameterType(), expectedGetterMethodName);

				if (getterMethodContent != null) {
					_extendedTagJavaClassesMap.clear();
					_javaMethodsMap.clear();

					return StringUtil.replaceLast(
						javaTerm.getContent(), CharPool.CLOSE_CURLY_BRACE,
						getterMethodContent + StringPool.CLOSE_CURLY_BRACE);
				}
			}

			addMessage(
				fileName,
				"Missing getter method for attribute \"" + tagAttributeName +
					"\"");
		}

		return javaTerm.getContent();
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_CLASS};
	}

	private String _constructGetterMethodContent(
		String setterMethodContent, String attributeType,
		String getterMethodName) {

		int x = setterMethodContent.indexOf(CharPool.OPEN_CURLY_BRACE);
		int y = setterMethodContent.lastIndexOf(CharPool.CLOSE_CURLY_BRACE);

		String setterMethodBody = StringUtil.trim(
			setterMethodContent.substring(x + 1, y));

		Matcher matcher = _setterMethodPattern.matcher(setterMethodBody);

		if (!matcher.find()) {
			return null;
		}

		StringBundler sb = new StringBundler(9);

		sb.append("public ");
		sb.append(attributeType);
		sb.append(StringPool.SPACE);
		sb.append(getterMethodName);
		sb.append("() {\n");
		sb.append("return _");
		sb.append(matcher.group(1));
		sb.append(";\n");
		sb.append("}\n");

		return sb.toString();
	}

	private boolean _containsClass(
		List<JavaClass> javaClasses, String className) {

		for (JavaClass javaClass : javaClasses) {
			if (className.equals(javaClass.getName(true))) {
				return true;
			}
		}

		return false;
	}

	private synchronized List<JavaClass> _getExtendedTagJavaClasses(
			JavaClass javaClass, String absolutePath)
		throws Exception {

		List<JavaClass> extendedTagJavaClasses = _extendedTagJavaClassesMap.get(
			absolutePath);

		if (extendedTagJavaClasses != null) {
			return extendedTagJavaClasses;
		}

		return _getExtendedTagJavaClasses(
			javaClass, absolutePath, _getUtilTaglibSrcDirName());
	}

	private List<JavaClass> _getExtendedTagJavaClasses(
			JavaClass javaClass, String absolutePath,
			String utilTaglibSrcDirName)
		throws Exception {

		List<String> extendedTagFileNames = TaglibUtil.getExtendedTagFileNames(
			javaClass, absolutePath, utilTaglibSrcDirName);

		if (extendedTagFileNames.isEmpty()) {
			_extendedTagJavaClassesMap.put(
				absolutePath, Collections.emptyList());

			return Collections.emptyList();
		}

		List<JavaClass> extendedTagJavaClasses = new ArrayList<>();

		for (String extendedTagFileName : extendedTagFileNames) {
			File extendedTagFile = new File(extendedTagFileName);

			if (!extendedTagFile.exists()) {
				continue;
			}

			String extendedTagContent = FileUtil.read(extendedTagFile);

			JavaClass extendedTagJavaClass = JavaClassParser.parseJavaClass(
				extendedTagFileName, extendedTagContent);

			extendedTagJavaClasses.add(extendedTagJavaClass);

			extendedTagJavaClasses.addAll(
				_getExtendedTagJavaClasses(
					extendedTagJavaClass, extendedTagFileName,
					utilTaglibSrcDirName));
		}

		_extendedTagJavaClassesMap.put(absolutePath, extendedTagJavaClasses);

		return extendedTagJavaClasses;
	}

	private JavaMethod _getJavaMethod(
		List<JavaMethod> javaMethods, String name) {

		for (JavaMethod javaMethod : javaMethods) {
			if (StringUtil.equalsIgnoreCase(javaMethod.getName(), name)) {
				return javaMethod;
			}
		}

		return null;
	}

	private synchronized List<JavaMethod> _getJavaMethods(JavaClass javaClass) {
		String className = javaClass.getName(true);

		List<JavaMethod> javaMethods = _javaMethodsMap.get(className);

		if (javaMethods != null) {
			return javaMethods;
		}

		javaMethods = new ArrayList<>();

		for (JavaTerm javaTerm : javaClass.getChildJavaTerms()) {
			if (!javaTerm.isJavaMethod()) {
				continue;
			}

			javaMethods.add((JavaMethod)javaTerm);
		}

		_javaMethodsMap.put(className, javaMethods);

		return javaMethods;
	}

	private List<JavaMethod> _getJavaMethods(
		JavaClass javaClass, List<JavaClass> extendedTagJavaClasses) {

		List<JavaMethod> javaMethods = new ArrayList<>();

		javaMethods.addAll(_getJavaMethods(javaClass));

		for (JavaClass extendedTagJavaClass : extendedTagJavaClasses) {
			javaMethods.addAll(_getJavaMethods(extendedTagJavaClass));
		}

		return javaMethods;
	}

	private JavaParameter _getJavaParameter(JavaMethod setterJavaMethod) {
		JavaSignature javaSignature = setterJavaMethod.getSignature();

		List<JavaParameter> javaParameters = javaSignature.getParameters();

		if (javaParameters.size() == 1) {
			return javaParameters.get(0);
		}

		return null;
	}

	private synchronized List<String> _getTagAttributeNames(
			String packageName, String className)
		throws Exception {

		String fullyQualifiedClassName = StringBundler.concat(
			packageName, StringPool.PERIOD, className);

		if (_tagAttributesMap != null) {
			return _tagAttributesMap.get(fullyQualifiedClassName);
		}

		_tagAttributesMap = new HashMap<>();

		List<String> tldFileNames = TaglibUtil.getTLDFileNames(
			getBaseDirName(), _allFileNames, getSourceFormatterExcludes(),
			isPortalSource(), getMaxDirLevel());

		if (tldFileNames.isEmpty()) {
			return _tagAttributesMap.get(fullyQualifiedClassName);
		}

		for (String tldFileName : tldFileNames) {
			File tldFile = new File(tldFileName);

			String content = FileUtil.read(tldFile);

			Document document = SourceUtil.readXML(content);

			if (document == null) {
				continue;
			}

			Element rootElement = document.getRootElement();

			List<Element> tagElements = rootElement.elements("tag");

			for (Element tagElement : tagElements) {
				Element tagClassElement = tagElement.element("tag-class");

				String tagClassName = tagClassElement.getStringValue();

				if (!tagClassName.startsWith("com.liferay")) {
					continue;
				}

				List<String> tagAttributeNames = new ArrayList<>();

				List<Element> attributeElements = tagElement.elements(
					"attribute");

				for (Element attributeElement : attributeElements) {
					Element attributeNameElement = attributeElement.element(
						"name");

					tagAttributeNames.add(
						attributeNameElement.getStringValue());
				}

				_tagAttributesMap.put(tagClassName, tagAttributeNames);
			}
		}

		return _tagAttributesMap.get(fullyQualifiedClassName);
	}

	private synchronized String _getUtilTaglibSrcDirName() {
		if (_utilTaglibSrcDirName != null) {
			return _utilTaglibSrcDirName;
		}

		_utilTaglibSrcDirName = TaglibUtil.getUtilTaglibSrcDirName(
			getBaseDirName(), getMaxDirLevel());

		return _utilTaglibSrcDirName;
	}

	private static final Pattern _setterMethodPattern = Pattern.compile(
		"^_(\\w+)\\s*=\\s*\\1;$");

	private List<String> _allFileNames;
	private final Map<String, List<JavaClass>> _extendedTagJavaClassesMap =
		new ConcurrentHashMap<>();
	private final Map<String, List<JavaMethod>> _javaMethodsMap =
		new ConcurrentHashMap<>();
	private Map<String, List<String>> _tagAttributesMap;
	private String _utilTaglibSrcDirName;

}