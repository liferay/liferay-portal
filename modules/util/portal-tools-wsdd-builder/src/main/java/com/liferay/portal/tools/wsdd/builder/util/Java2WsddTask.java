/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.wsdd.builder.util;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.xml.SAXReaderFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.axis.tools.ant.wsdl.Java2WsdlAntTask;
import org.apache.axis.tools.ant.wsdl.NamespaceMapping;
import org.apache.axis.tools.ant.wsdl.Wsdl2javaAntTask;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class Java2WsddTask {

	public static String documentToString(Node node) throws IOException {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		OutputFormat outputFormat = new OutputFormat(StringPool.TAB, true);

		outputFormat.setOmitEncoding(true);
		outputFormat.setPadText(true);
		outputFormat.setTrimText(true);

		XMLWriter xmlWriter = new XMLWriter(
			unsyncByteArrayOutputStream, outputFormat);

		xmlWriter.write(node);

		String content = StringUtil.trimTrailing(
			unsyncByteArrayOutputStream.toString(StringPool.UTF8));

		while (content.contains(" \n")) {
			content = StringUtil.replace(content, " \n", "\n");
		}

		return content;
	}

	public static String[] generateWsdd(
			String className, String classPath, String serviceName)
		throws Exception {

		// Create temp directory

		java.nio.file.Path tempDirPath = Files.createTempDirectory(
			Paths.get(SystemProperties.get(SystemProperties.TMP_DIR)), null);

		File tempDir = tempDirPath.toFile();

		tempDir.mkdir();

		// axis-java2wsdl

		String wsdlFileName = tempDir + "/service.wsdl";

		int pos = className.lastIndexOf(".");

		String packagePath = className.substring(0, pos);

		String[] packagePaths = StringUtil.split(packagePath, '.');

		String namespace = "urn:";

		for (int i = packagePaths.length - 1; i >= 0; i--) {
			namespace += packagePaths[i];

			if (i <= 0) {
				continue;
			}

			namespace += ".";
		}

		String location = "http://localhost/services/" + serviceName;

		String mappingPackage =
			packagePath.substring(0, packagePath.lastIndexOf(".")) + ".ws";

		Project project = AntUtil.getProject();

		Java2WsdlAntTask java2Wsdl = new Java2WsdlAntTask();

		NamespaceMapping mapping = new NamespaceMapping();

		mapping.setNamespace(namespace);
		mapping.setPackage(mappingPackage);

		java2Wsdl.setProject(project);
		java2Wsdl.setClassName(className);

		if (Validator.isNotNull(classPath)) {
			java2Wsdl.setClasspath(new Path(project, classPath));
		}

		java2Wsdl.setOutput(new File(wsdlFileName));
		java2Wsdl.setLocation(location);
		java2Wsdl.setNamespace(namespace);
		java2Wsdl.addMapping(mapping);

		java2Wsdl.execute();

		// axis-wsdl2java

		Wsdl2javaAntTask wsdl2Java = new Wsdl2javaAntTask();

		wsdl2Java.setProject(project);
		wsdl2Java.setURL(wsdlFileName);
		wsdl2Java.setOutput(tempDir);
		wsdl2Java.setServerSide(true);
		wsdl2Java.setTestCase(false);
		wsdl2Java.setVerbose(false);

		wsdl2Java.execute();

		// Get content

		String packagePathWithSlashes = StringUtil.replace(
			packagePath, CharPool.PERIOD, CharPool.SLASH);

		File deployFile = new File(
			StringBundler.concat(
				tempDir, "/", packagePathWithSlashes, "/deploy.wsdd"));

		String deployContent = new String(
			Files.readAllBytes(deployFile.toPath()));

		deployContent = StringUtil.replace(
			deployContent,
			StringBundler.concat(
				packagePath, ".", serviceName, "SoapBindingImpl"),
			className);

		deployContent = _format(deployContent);

		File undeployFile = new File(
			StringBundler.concat(
				tempDir, "/", packagePathWithSlashes, "/undeploy.wsdd"));

		String undeployContent = new String(
			Files.readAllBytes(undeployFile.toPath()));

		undeployContent = _format(undeployContent);

		// Delete temp directory

		DeleteTask.deleteDirectory(tempDir);

		return new String[] {deployContent, undeployContent};
	}

	private static void _addElements(
		Element element, Map<String, Element> elements) {

		for (Map.Entry<String, Element> entry : elements.entrySet()) {
			Element childElement = entry.getValue();

			element.add(childElement);
		}
	}

	private static String _format(String content) throws Exception {
		content = _stripComments(content);

		SAXReader saxReader = SAXReaderFactory.getSAXReader(null, false, false);

		Document document = saxReader.read(new UnsyncStringReader(content));

		Element rootElement = document.getRootElement();

		Element serviceElement = rootElement.element("service");

		Map<String, Element> arrayMappingElements = new TreeMap<>();
		Map<String, Element> typeMappingElements = new TreeMap<>();
		Map<String, Element> operationElements = new TreeMap<>();
		Map<String, Element> parameterElements = new TreeMap<>();

		List<Element> elements = serviceElement.elements();

		for (Element element : elements) {
			String elementName = element.getName();

			if (elementName.equals("arrayMapping")) {
				element.detach();

				arrayMappingElements.put(documentToString(element), element);
			}
			else if (elementName.equals("operation")) {
				element.detach();

				List<Element> parameters = element.elements("parameter");

				StringBundler sb = new StringBundler(
					(2 * parameters.size()) + 2);

				String name = element.attributeValue("name");

				sb.append(name);

				sb.append("_METHOD_");

				for (Element parameterElement : parameters) {
					String type = parameterElement.attributeValue("type");

					sb.append(type);

					sb.append("_PARAMETER_");
				}

				operationElements.put(sb.toString(), element);
			}
			else if (elementName.equals("parameter")) {
				element.detach();

				String name = element.attributeValue("name");

				if (name.equals("allowedMethods")) {
					Attribute valueAttribute = element.attribute("value");

					String[] values = StringUtil.split(
						valueAttribute.getValue(), CharPool.SPACE);

					Arrays.sort(values);

					valueAttribute.setValue(
						StringUtil.merge(values, StringPool.SPACE));
				}
				else if (name.equals("schemaUnqualified")) {
					Attribute valueAttribute = element.attribute("value");

					String[] values = StringUtil.split(
						valueAttribute.getValue());

					Arrays.sort(values);

					valueAttribute.setValue(StringUtil.merge(values));
				}

				parameterElements.put(name, element);
			}
			else if (elementName.equals("typeMapping")) {
				element.detach();

				typeMappingElements.put(documentToString(element), element);
			}
		}

		_addElements(serviceElement, arrayMappingElements);
		_addElements(serviceElement, typeMappingElements);
		_addElements(serviceElement, operationElements);
		_addElements(serviceElement, parameterElements);

		return StringUtil.replace(documentToString(document), "\"/>", "\" />");
	}

	private static String _stripComments(String text) {
		return StringUtil.stripBetween(text, "<!--", "-->");
	}

}