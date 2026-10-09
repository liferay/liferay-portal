/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.tools.GitUtil;
import com.liferay.portal.tools.java.parser.JavaParser;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaClassParser;
import com.liferay.source.formatter.parser.JavaConstructor;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.parser.JavaVariable;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class PortalJSONObjectUtil {

	public static void deleteTempPortalJSONObjectFile() throws Exception {
		File tempPortalJSONObjectFile = new File(
			StringBundler.concat(
				SystemProperties.get(SystemProperties.TMP_DIR), File.separator,
				GitUtil.getLatestCommitId(), ".json"));

		if (tempPortalJSONObjectFile.exists()) {
			tempPortalJSONObjectFile.delete();
		}
	}

	public static JSONObject getPortalJSONObject(String dirName)
		throws Exception {

		return getPortalJSONObject(
			dirName, new SourceFormatterExcludes(),
			SourceFormatterArgs.MAX_LINE_LENGTH);
	}

	public static JSONObject getPortalJSONObject(
			String dirName, SourceFormatterExcludes sourceFormatterExcludes,
			int maxLineLength)
		throws Exception {

		File tempPortalJSONObjectFile = new File(
			StringBundler.concat(
				SystemProperties.get(SystemProperties.TMP_DIR), File.separator,
				GitUtil.getLatestCommitId(), ".json"));

		if (tempPortalJSONObjectFile.exists()) {
			return new JSONObjectImpl(FileUtil.read(tempPortalJSONObjectFile));
		}

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		List<Future<Tuple>> futures = new ArrayList<>();

		JSONObject portalJSONObject = new JSONObjectImpl();
		JSONObject taglibsJSONObject = new JSONObjectImpl();
		JSONObject xmlDefinitionsJSONObject = new JSONObjectImpl();

		List<String> fileNames = SourceFormatterUtil.scanForFileNames(
			dirName, new String[0],
			new String[] {
				"**/*.dtd", "**/*.java", "**/resources/META-INF/*.tld",
				"**/resources/META-INF/**/*.tld", "**/src/META-INF/*.tld",
				"**/src/META-INF/**/*.tld"
			},
			sourceFormatterExcludes, true);

		for (String fileName : fileNames) {
			if (fileName.endsWith(".dtd")) {
				xmlDefinitionsJSONObject = _addXMLdefinition(
					xmlDefinitionsJSONObject, fileName);

				continue;
			}

			if (fileName.endsWith(".tld")) {
				taglibsJSONObject = _addTaglib(taglibsJSONObject, fileName);

				continue;
			}

			if (!fileName.contains("/com/liferay/")) {
				continue;
			}

			if (fileName.endsWith("/PreupgradeVerifyProperties.java")) {
				portalJSONObject.put(
					"legacyProperties",
					_getLegacyPropertiesJSONObject(fileName));
			}

			Future<Tuple> future = executorService.submit(
				new Callable<Tuple>() {

					@Override
					public Tuple call() throws Exception {
						return _getClassTuple(fileName, maxLineLength);
					}

				});

			futures.add(future);
		}

		JSONObject javaClassesJSONObject = new JSONObjectImpl();

		for (Future<Tuple> future : futures) {
			try {
				javaClassesJSONObject = _addJavaClassJSONObject(
					javaClassesJSONObject, future.get(1, TimeUnit.MINUTES));
			}
			catch (Exception exception) {
				future.cancel(true);
			}
		}

		executorService.shutdown();

		portalJSONObject.put(
			"javaClasses", javaClassesJSONObject
		).put(
			"taglibs", taglibsJSONObject
		).put(
			"xmlDefinitions", xmlDefinitionsJSONObject
		);

		FileUtil.write(
			tempPortalJSONObjectFile, JSONUtil.toString(portalJSONObject));

		return portalJSONObject;
	}

	public static JSONObject getPortalJSONObjectByVersion(String version)
		throws Exception {

		// TODO: Grab from URL (need to figure out what that URL is first)

		return new JSONObjectImpl();
	}

	private static JSONArray _addElementValues(
		JSONArray elementJSONArray, String s) {

		s = s.replaceAll("^\\s*\\(?(.+?)[?*+]?\\)?[?*+]?\\s*$", "$1");

		if (s.equals("#PCDATA")) {
			return elementJSONArray;
		}

		if (s.contains(StringPool.COMMA)) {
			String[] parts = StringUtil.split(s, CharPool.COMMA);

			for (String part : parts) {
				elementJSONArray = _addElementValues(elementJSONArray, part);
			}

			return elementJSONArray;
		}

		if (s.contains(StringPool.PIPE)) {
			String[] parts = StringUtil.split(s, CharPool.PIPE);

			for (String part : parts) {
				elementJSONArray = _addElementValues(elementJSONArray, part);
			}

			return elementJSONArray;
		}

		if (s.matches("[\\w-]+")) {
			elementJSONArray.put(s);
		}

		return elementJSONArray;
	}

	private static synchronized JSONObject _addJavaClassJSONObject(
		JSONObject javaClassesJSONObject, Tuple tuple) {

		String className = (String)tuple.getObject(0);

		JSONObject classJSONObject = (JSONObject)tuple.getObject(1);

		if (!javaClassesJSONObject.has(className)) {
			javaClassesJSONObject.put(className, classJSONObject);
		}
		else {
			javaClassesJSONObject.put(
				className,
				_mergeClassJSONObjects(
					classJSONObject,
					javaClassesJSONObject.getJSONObject(className)));
		}

		return javaClassesJSONObject;
	}

	private static JSONObject _addTaglib(
		JSONObject taglibsJSONObject, String fileName) {

		File tldFile = new File(fileName);

		Document document = null;

		try {
			document = SourceUtil.readXML(FileUtil.read(tldFile));
		}
		catch (Exception exception) {
			return taglibsJSONObject;
		}

		if (document == null) {
			return taglibsJSONObject;
		}

		Element rootElement = document.getRootElement();

		if (!Objects.equals(rootElement.getName(), "taglib")) {
			return taglibsJSONObject;
		}

		Element shortNameElement = rootElement.element("short-name");

		if (shortNameElement == null) {
			return taglibsJSONObject;
		}

		JSONObject taglibJSONObject = new JSONObjectImpl();

		List<Element> tagElements = rootElement.elements("tag");

		for (Element tagElement : tagElements) {
			Element tagClassElement = tagElement.element("tag-class");

			String tagClassName = tagClassElement.getStringValue();

			if (!tagClassName.startsWith("com.liferay")) {
				continue;
			}

			JSONObject tagJSONObject = new JSONObjectImpl();

			JSONArray tagAttributeNamesJSONArray = new JSONArrayImpl();

			List<Element> attributeElements = tagElement.elements("attribute");

			for (Element attributeElement : attributeElements) {
				JSONObject attributeJSONObject = new JSONObjectImpl();

				Element attributeDescriptionElement = attributeElement.element(
					"description");

				if (attributeDescriptionElement != null) {
					String description =
						attributeDescriptionElement.getStringValue();

					if (description.startsWith("Deprecated")) {
						attributeJSONObject.put("deprecated", true);
					}
				}

				Element attributeNameElement = attributeElement.element("name");

				attributeJSONObject.put(
					"name", attributeNameElement.getStringValue());

				tagAttributeNamesJSONArray.put(attributeJSONObject);
			}

			if (tagAttributeNamesJSONArray.length() > 0) {
				tagJSONObject.put("attributes", tagAttributeNamesJSONArray);
			}

			Element tagDescriptionElement = tagElement.element("description");

			if (tagDescriptionElement != null) {
				String description = tagDescriptionElement.getStringValue();

				if (description.startsWith("Deprecated")) {
					tagJSONObject.put("deprecated", true);
				}
			}

			Element tagNameElement = tagElement.element("name");

			taglibJSONObject.put(
				tagNameElement.getStringValue(), tagJSONObject);
		}

		taglibsJSONObject.put(
			shortNameElement.getStringValue(), taglibJSONObject);

		return taglibsJSONObject;
	}

	private static JSONObject _addXMLdefinition(
			JSONObject xmlDefinitionsJSONObject, String fileName)
		throws Exception {

		if (!fileName.matches(".*_[0-9]_[0-9]_[0-9]\\.dtd")) {
			return xmlDefinitionsJSONObject;
		}

		JSONObject xmlDefinitionJSONObject = new JSONObjectImpl();

		File dtdFile = new File(fileName);

		String content = FileUtil.read(dtdFile);

		Matcher matcher = _elementPattern.matcher(content);

		while (matcher.find()) {
			int x = content.indexOf(">", matcher.end());

			if (x == -1) {
				return xmlDefinitionJSONObject;
			}

			JSONArray elementJSONArray = _addElementValues(
				new JSONArrayImpl(), content.substring(matcher.end() - 1, x));

			if (elementJSONArray.length() <= 0) {
				continue;
			}

			xmlDefinitionJSONObject.put(matcher.group(1), elementJSONArray);
		}

		if (xmlDefinitionJSONObject.length() > 0) {
			int x = fileName.lastIndexOf(StringPool.SLASH);

			xmlDefinitionsJSONObject.put(
				fileName.substring(x + 1), xmlDefinitionJSONObject);
		}

		return xmlDefinitionsJSONObject;
	}

	private static Tuple _getClassTuple(String fileName, int maxLineLength)
		throws Exception {

		String content = JavaParser.parse(
			new File(fileName), maxLineLength, false);

		JavaClass javaClass = null;

		try {
			javaClass = JavaClassParser.parseJavaClass(fileName, content);
		}
		catch (Exception exception) {
			return null;
		}

		String className = javaClass.getName(true);

		if (!className.startsWith("com.liferay.")) {
			return null;
		}

		JSONObject classJSONObject = new JSONObjectImpl();

		JSONArray constructorsJSONArray = _getConstructorsJSONArray(javaClass);

		if (constructorsJSONArray.length() > 0) {
			classJSONObject.put("constructors", constructorsJSONArray);
		}

		if (javaClass.hasAnnotation("Deprecated")) {
			classJSONObject.put("deprecated", true);
		}

		JSONArray extendedClassesJSONArray = _getExtendedClassesJSONArray(
			javaClass);

		if (extendedClassesJSONArray.length() > 0) {
			classJSONObject.put("extendedClassNames", extendedClassesJSONArray);
		}

		JSONArray implementedClassesJSONArray = _getImplementedClassesJSONArray(
			javaClass);

		if (implementedClassesJSONArray.length() > 0) {
			classJSONObject.put(
				"implementedClassNames", implementedClassesJSONArray);
		}

		JSONArray methodsJSONArray = _getMethodsJSONArray(javaClass);

		if (methodsJSONArray.length() > 0) {
			classJSONObject.put("methods", methodsJSONArray);
		}

		JSONArray variablesJSONArray = _getVariablesJSONArray(javaClass);

		if (variablesJSONArray.length() > 0) {
			classJSONObject.put("variables", variablesJSONArray);
		}

		return new Tuple(javaClass.getName(true), classJSONObject);
	}

	private static JSONArray _getConstructorsJSONArray(JavaClass javaClass) {
		JSONArray constructorsJSONArray = new JSONArrayImpl();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaConstructor() ||
				childJavaTerm.isPrivate()) {

				continue;
			}

			JavaConstructor javaConstructor = (JavaConstructor)childJavaTerm;

			JSONObject constructorJSONObject = new JSONObjectImpl();

			constructorJSONObject.put(
				"accessModifier", javaConstructor.getAccessModifier());

			if (javaConstructor.hasAnnotation("Deprecated")) {
				constructorJSONObject.put("deprecated", true);
			}

			constructorJSONObject.put("name", javaConstructor.getName());

			if (javaConstructor.hasAnnotation("Override")) {
				constructorJSONObject.put("override", true);
			}

			JavaSignature javaSignature = null;

			try {
				javaSignature = javaConstructor.getSignature();
			}
			catch (Exception exception) {
				continue;
			}

			List<JavaParameter> parameters = javaSignature.getParameters();

			if (!parameters.isEmpty()) {
				JSONArray parametersJSONArray = new JSONArrayImpl();

				for (JavaParameter javaParameter : parameters) {
					parametersJSONArray.put(
						javaParameter.getParameterType(true));
				}

				constructorJSONObject.put("parameters", parametersJSONArray);
			}

			constructorsJSONArray.put(constructorJSONObject);
		}

		return constructorsJSONArray;
	}

	private static JSONArray _getExtendedClassesJSONArray(JavaClass javaClass) {
		JSONArray extendedClassesJSONArray = new JSONArrayImpl();

		for (String extendedClassName : javaClass.getExtendedClassNames(true)) {
			extendedClassesJSONArray.put(extendedClassName);
		}

		return extendedClassesJSONArray;
	}

	private static JSONArray _getImplementedClassesJSONArray(
		JavaClass javaClass) {

		JSONArray implementedClassesJSONArray = new JSONArrayImpl();

		for (String implementedClassName :
				javaClass.getImplementedClassNames(true)) {

			implementedClassesJSONArray.put(implementedClassName);
		}

		return implementedClassesJSONArray;
	}

	private static JSONObject _getLegacyPropertiesJSONObject(String fileName)
		throws Exception {

		JSONObject legacyPropertiesJSONObject = new JSONObjectImpl();

		List<LegacyProperty> legacyProperties =
			LegacyPropertiesUtil.getLegacyProperties(
				fileName, FileUtil.read(new File(fileName)));

		for (LegacyProperty legacyProperty : legacyProperties) {
			JSONObject legacyPropertyJSONObject = new JSONObjectImpl();

			legacyPropertyJSONObject.put(
				"moduleName", legacyProperty.getModuleName()
			).put(
				"newPropertyName", legacyProperty.getNewPropertyName()
			).put(
				"variableName", legacyProperty.getVariableName()
			);

			legacyPropertiesJSONObject.put(
				legacyProperty.getLegacyPropertyName(),
				legacyPropertyJSONObject);
		}

		return legacyPropertiesJSONObject;
	}

	private static JSONArray _getMethodsJSONArray(JavaClass javaClass) {
		JSONArray methodsJSONArray = new JSONArrayImpl();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaMethod() || childJavaTerm.isPrivate()) {
				continue;
			}

			JavaMethod javaMethod = (JavaMethod)childJavaTerm;

			JSONObject methodJSONObject = new JSONObjectImpl();

			methodJSONObject.put(
				"accessModifier", javaMethod.getAccessModifier());

			if (javaMethod.hasAnnotation("Deprecated")) {
				methodJSONObject.put("deprecated", true);
			}

			methodJSONObject.put("name", javaMethod.getName());

			if (javaMethod.hasAnnotation("Override")) {
				methodJSONObject.put("override", true);
			}

			JavaSignature javaSignature = null;

			try {
				javaSignature = javaMethod.getSignature();
			}
			catch (Exception exception) {
				continue;
			}

			List<JavaParameter> parameters = javaSignature.getParameters();

			if (!parameters.isEmpty()) {
				JSONArray parametersJSONArray = new JSONArrayImpl();

				for (JavaParameter javaParameter : parameters) {
					parametersJSONArray.put(
						javaParameter.getParameterType(true));
				}

				methodJSONObject.put("parameters", parametersJSONArray);
			}

			methodJSONObject.put(
				"returnType", javaSignature.getReturnType(true));

			methodsJSONArray.put(methodJSONObject);
		}

		return methodsJSONArray;
	}

	private static JSONArray _getVariablesJSONArray(JavaClass javaClass) {
		JSONArray variablesJSONArray = new JSONArrayImpl();

		for (JavaTerm childJavaTerm : javaClass.getChildJavaTerms()) {
			if (!childJavaTerm.isJavaVariable() || childJavaTerm.isPrivate()) {
				continue;
			}

			JavaVariable javaVariable = (JavaVariable)childJavaTerm;

			JSONObject variableJSONObject = new JSONObjectImpl();

			variableJSONObject.put(
				"accessModifier", javaVariable.getAccessModifier()
			).put(
				"deprecated",
				() -> {
					if (javaVariable.hasAnnotation("Deprecated")) {
						return true;
					}

					return null;
				}
			).put(
				"lineNumber", javaVariable.getLineNumber()
			).put(
				"name", javaVariable.getName()
			);

			variablesJSONArray.put(variableJSONObject);
		}

		return variablesJSONArray;
	}

	private static JSONObject _mergeClassJSONObjects(
		JSONObject classJSONObject1, JSONObject classJSONObject2) {

		JSONObject mergedJSONObject = _mergeJSONArrays(
			new JSONObjectImpl(), "constructors", classJSONObject1,
			classJSONObject2);

		if (classJSONObject1.has("deprecated") ||
			classJSONObject2.has("deprecated")) {

			mergedJSONObject.put("deprecated", true);
		}

		mergedJSONObject = _mergeJSONArrays(
			mergedJSONObject, "extendedClassNames", classJSONObject1,
			classJSONObject2);
		mergedJSONObject = _mergeJSONArrays(
			mergedJSONObject, "implementedClassNames", classJSONObject1,
			classJSONObject2);
		mergedJSONObject = _mergeJSONArrays(
			mergedJSONObject, "methods", classJSONObject1, classJSONObject2);
		mergedJSONObject = _mergeJSONArrays(
			mergedJSONObject, "variables", classJSONObject1, classJSONObject2);

		return mergedJSONObject;
	}

	private static JSONObject _mergeJSONArrays(
		JSONObject mergedJSONObject, String name, JSONObject classJSONObject1,
		JSONObject classJSONObject2) {

		JSONArray jsonArray1 = classJSONObject1.getJSONArray(name);
		JSONArray jsonArray2 = classJSONObject2.getJSONArray(name);

		if ((jsonArray1 == null) && (jsonArray2 == null)) {
			return mergedJSONObject;
		}

		if (jsonArray1 == null) {
			return mergedJSONObject.put(name, jsonArray2);
		}

		if (jsonArray2 == null) {
			return mergedJSONObject.put(name, jsonArray1);
		}

		for (int i = 0; i < jsonArray2.length(); i++) {
			jsonArray1.put(jsonArray2.get(i));
		}

		return mergedJSONObject.put(name, jsonArray1);
	}

	private static final Pattern _elementPattern = Pattern.compile(
		"<!ELEMENT ([\\w-]+) \\(");

}