/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.parser.JavaClass;
import com.liferay.source.formatter.parser.JavaMethod;
import com.liferay.source.formatter.parser.JavaParameter;
import com.liferay.source.formatter.parser.JavaSignature;
import com.liferay.source.formatter.parser.JavaTerm;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Hugo Huijser
 */
public class JavaStagedModelDataHandlerCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, JavaTerm javaTerm,
			String fileContent)
		throws IOException {

		if (!Objects.equals(javaTerm.getName(), "doImportStagedModel")) {
			return javaTerm.getContent();
		}

		JavaClass javaClass = javaTerm.getParentJavaClass();

		List<String> extendedClassNames = javaClass.getExtendedClassNames();

		if (!extendedClassNames.contains("BaseStagedModelDataHandler")) {
			return javaTerm.getContent();
		}

		String javaMethodContent = javaTerm.getContent();

		if (javaMethodContent.contains("setMvccVersion(")) {
			return javaMethodContent;
		}

		JavaMethod javaMethod = (JavaMethod)javaTerm;

		JavaSignature javaSignature = javaMethod.getSignature();

		List<JavaParameter> javaParameters = javaSignature.getParameters();

		if (javaParameters.size() != 2) {
			return javaMethodContent;
		}

		JavaParameter stagedModelJavaParameter = javaParameters.get(1);

		String stagedModelType = stagedModelJavaParameter.getParameterType();

		String primaryKey = _getPrimaryKey(
			fileName, stagedModelType, javaClass.getImportNames());

		if (primaryKey == null) {
			return javaMethodContent;
		}

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(\n\t*)(\\w+)\\.\\s*set", primaryKey,
				"\\(\\s*(\\w+)\\.\\s*get", primaryKey, "\\(\\)"),
			Pattern.CASE_INSENSITIVE);

		Matcher matcher = pattern.matcher(javaMethodContent);

		if (!matcher.find()) {
			return javaMethodContent;
		}

		String existingVariableName = matcher.group(3);
		String importedVariableName = matcher.group(2);

		String existingVariableTypeName = getVariableTypeName(
			javaMethodContent, javaTerm, fileContent, fileName,
			existingVariableName);

		String importedVariableTypeName = getVariableTypeName(
			javaMethodContent, javaTerm, fileContent, fileName,
			importedVariableName);

		if ((existingVariableTypeName == null) ||
			(importedVariableTypeName == null)) {

			return javaMethodContent;
		}

		if (stagedModelType.equals(existingVariableTypeName) &&
			stagedModelType.equals(importedVariableTypeName)) {

			javaMethodContent = StringUtil.insert(
				javaMethodContent,
				StringBundler.concat(
					matcher.group(1), importedVariableName, ".setMvccVersion(",
					existingVariableName, ".getMvccVersion());"),
				matcher.start());
		}

		return javaMethodContent;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private String _getKernelPrimaryKey(
			String absolutePath, String stagedModelType,
			List<String> importNames)
		throws IOException {

		String packageName = null;

		for (String importName : importNames) {
			if (!importName.endsWith("." + stagedModelType)) {
				continue;
			}

			packageName = importName.substring(
				0, importName.length() - stagedModelType.length() - 1);
		}

		if (packageName == null) {
			return null;
		}

		List<String[]> kernelPrimaryKeysList = _getKernelPrimarykeysList(
			absolutePath);

		for (String[] primaryKeyArray : kernelPrimaryKeysList) {
			if (stagedModelType.equals(primaryKeyArray[0]) &&
				packageName.startsWith(primaryKeyArray[1])) {

				return primaryKeyArray[2];
			}
		}

		return null;
	}

	private synchronized List<String[]> _getKernelPrimarykeysList(
			String absolutePath)
		throws IOException {

		if (_kernelPrimaryKeysList != null) {
			return _kernelPrimaryKeysList;
		}

		_kernelPrimaryKeysList = new ArrayList<>();

		for (String serviceXMLFileName : _SERVICE_XML_FILE_NAMES) {
			String content = getPortalContent(serviceXMLFileName, absolutePath);

			if (Validator.isNull(content)) {
				continue;
			}

			Document document = SourceUtil.readXML(content);

			if (document == null) {
				continue;
			}

			Element rootElement = document.getRootElement();

			if (!GetterUtil.getBoolean(
					rootElement.attributeValue("mvcc-enabled"))) {

				continue;
			}

			String apiPackagePath = rootElement.attributeValue(
				"api-package-path");

			for (Element entityElement :
					(List<Element>)rootElement.elements("entity")) {

				for (Element columnElement :
						(List<Element>)entityElement.elements("column")) {

					if (!GetterUtil.getBoolean(
							columnElement.attributeValue("primary"))) {

						continue;
					}

					_kernelPrimaryKeysList.add(
						new String[] {
							entityElement.attributeValue("name"),
							apiPackagePath, columnElement.attributeValue("name")
						});
				}
			}
		}

		return _kernelPrimaryKeysList;
	}

	private String _getPrimaryKey(
			String serviceXMLFileName, String stagedModelType)
		throws IOException {

		File file = new File(serviceXMLFileName);

		if (!file.exists()) {
			return null;
		}

		String content = FileUtil.read(file);

		Document document = SourceUtil.readXML(content);

		if (document == null) {
			return null;
		}

		Element rootElement = document.getRootElement();

		for (Element entityElement :
				(List<Element>)rootElement.elements("entity")) {

			if (!stagedModelType.equals(entityElement.attributeValue("name"))) {
				continue;
			}

			if (!GetterUtil.getBoolean(
					rootElement.attributeValue("mvcc-enabled"))) {

				return StringPool.BLANK;
			}

			for (Element columnElement :
					(List<Element>)entityElement.elements("column")) {

				if (GetterUtil.getBoolean(
						columnElement.attributeValue("primary"))) {

					return columnElement.attributeValue("name");
				}
			}
		}

		return null;
	}

	private String _getPrimaryKey(
			String absolutePath, String stagedModelType,
			List<String> importNames)
		throws IOException {

		String primaryKey = null;

		int x = absolutePath.lastIndexOf("-service/");

		if (x != -1) {
			primaryKey = _getPrimaryKey(
				absolutePath.substring(0, x + 9) + "service.xml",
				stagedModelType);

			if (primaryKey != null) {
				return primaryKey;
			}
		}

		x = absolutePath.lastIndexOf("/modules/");

		if (x == -1) {
			return _getKernelPrimaryKey(
				absolutePath, stagedModelType, importNames);
		}

		Matcher matcher = _serviceDependencyPattern.matcher(
			getBuildGradleContent(absolutePath));

		while (matcher.find()) {
			String serviceXMLFileName = StringBundler.concat(
				absolutePath.substring(0, x + 8),
				StringUtil.replace(
					matcher.group(), CharPool.COLON, CharPool.SLASH),
				"/service.xml");

			primaryKey = _getPrimaryKey(serviceXMLFileName, stagedModelType);

			if (primaryKey != null) {
				return primaryKey;
			}
		}

		return _getKernelPrimaryKey(absolutePath, stagedModelType, importNames);
	}

	private static final String[] _SERVICE_XML_FILE_NAMES = {
		"portal-impl/src/com/liferay/counter/service.xml",
		"portal-impl/src/com/liferay/portal/service.xml",
		"portal-impl/src/com/liferay/portlet/announcements/service.xml",
		"portal-impl/src/com/liferay/portlet/asset/service.xml",
		"portal-impl/src/com/liferay/portlet/documentlibrary/service.xml",
		"portal-impl/src/com/liferay/portlet/expando/service.xml",
		"portal-impl/src/com/liferay/portlet/exportimport/service.xml",
		"portal-impl/src/com/liferay/portlet/ratings/service.xml",
		"portal-impl/src/com/liferay/portlet/social/service.xml"
	};

	private static final Pattern _serviceDependencyPattern = Pattern.compile(
		":apps:[\\w:-]+-service");

	private List<String[]> _kernelPrimaryKeysList;

}