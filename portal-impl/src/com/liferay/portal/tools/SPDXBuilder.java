/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.ProcessingInstruction;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Peter Shin
 */
public class SPDXBuilder {

	public static void main(String[] args) throws IOException {
		ToolDependencies.wireBasic();

		String xmls = null;

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in))) {

			xmls = bufferedReader.readLine();
		}

		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String spdxFileName = ArgumentsUtil.getString(
			arguments, "spdx.file", null);
		String licenseOverridePropertiesFileName = ArgumentsUtil.getString(
			arguments, "license.override.properties.file", null);

		new SPDXBuilder(
			StringUtil.split(xmls), spdxFileName,
			licenseOverridePropertiesFileName);
	}

	public SPDXBuilder(
		String[] xmls, String spdxFileName,
		String licenseOverridePropertiesFileName) {

		try {
			System.setProperty("line.separator", StringPool.NEW_LINE);

			File spdxFile = new File(spdxFileName);

			Properties licenseOverrideProperties = new Properties();

			if (Validator.isNotNull(licenseOverridePropertiesFileName)) {
				File licenseOverridePropertiesfile = new File(
					licenseOverridePropertiesFileName);

				if (licenseOverridePropertiesfile.exists()) {
					licenseOverrideProperties.load(
						new FileInputStream(licenseOverridePropertiesfile));
				}
			}

			Document document = _getDocument(
				xmls, spdxFile, licenseOverrideProperties);

			String xml = document.formattedString();

			_write(
				new File(spdxFile.getParentFile(), "versions-spdx.xml"), xml);

			_write(
				new File(spdxFile.getParentFile(), "versions-spdx.csv"),
				_toCSV(document));

			TransformerFactory transformerFactory =
				SecureXMLFactoryProviderUtil.newTransformerFactory();

			Transformer transformer = transformerFactory.newTransformer(
				new StreamSource(
					new File(spdxFile.getParentFile(), "versions.xsl")));

			File versionHtmlFile = new File(
				spdxFile.getParentFile(), "versions-spdx.html");

			transformer.transform(
				new StreamSource(new UnsyncStringReader(xml)),
				new StreamResult(new FileOutputStream(versionHtmlFile)));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Element> _createLibraryElements(
		Element packageElement, Properties licenseOverrideProperties) {

		String downloadLocation = packageElement.elementText(
			_getQName("downloadLocation"));
		String name = packageElement.elementText(_getQName("name"));
		String versionInfo = packageElement.elementText(
			_getQName("versionInfo"));

		Element hasFileElement = packageElement.element(_getQName("hasFile"));

		List<Element> fileElements = hasFileElement.elements(_getQName("File"));

		return TransformUtil.transform(
			fileElements,
			fileElement -> {
				String fileName = fileElement.elementText(
					_getQName("fileName"));

				String dirName = fileName.substring(
					0, fileName.indexOf('/') + 1);

				if (dirName.endsWith("portal/") ||
					dirName.endsWith("portal-ee/")) {

					fileName = fileName.substring(dirName.length());
				}

				Element libraryElement = SAXReaderUtil.createElement("library");

				Element fileNameElement = libraryElement.addElement(
					"file-name");

				fileNameElement.addText(fileName);

				Element versionElement = libraryElement.addElement("version");

				versionElement.addText(versionInfo);

				Element projectNameElement = libraryElement.addElement(
					"project-name");

				projectNameElement.addText(name);

				if ((downloadLocation != null) &&
					downloadLocation.startsWith("http")) {

					Element element = libraryElement.addElement("project-url");

					element.addText(downloadLocation);
				}

				String licenseName = _getLicenseName(
					packageElement, fileName, licenseOverrideProperties);

				if (licenseName == null) {
					return libraryElement;
				}

				Element licensesElement = libraryElement.addElement("licenses");

				Element licenseElement = licensesElement.addElement("license");

				Element element = licenseElement.addElement("license-name");

				element.addText(licenseName);

				String licenseURL = _getLicenseURL(
					packageElement, fileName, licenseOverrideProperties);

				if (licenseURL != null) {
					element = licenseElement.addElement("license-url");

					element.addText(licenseURL);
				}

				return libraryElement;
			});
	}

	private String _encode(Node node) {
		if (node == null) {
			return StringPool.BLANK;
		}

		String text = node.getText();

		text = StringUtil.trim(text.replaceAll("\\s+", " "));

		return CSVUtil.encode(text);
	}

	@SuppressWarnings("unchecked")
	private Document _getDocument(
			String[] xmls, File spdxFile, Properties licenseOverrideProperties)
		throws Exception {

		Comparator<String> comparator = String.CASE_INSENSITIVE_ORDER;

		Map<String, Element> libraryElementMap = new TreeMap<>(comparator);

		for (String xml : xmls) {
			Document xmlDocument = SAXReaderUtil.read(new File(xml));

			List<Node> fileNameNodes = xmlDocument.selectNodes("//file-name");

			for (Node fileNameNode : fileNameNodes) {
				Element libraryElement = fileNameNode.getParent();

				libraryElementMap.put(
					_getKey("portal", libraryElement), libraryElement);
			}
		}

		Document spdxDocument = SAXReaderUtil.read(spdxFile);

		Element spdxRootElement = spdxDocument.getRootElement();

		Element spdxDocumentElement = spdxRootElement.element(
			_getQName("SpdxDocument"));

		List<Element> elements = spdxDocumentElement.elements(
			_getQName("relationship"));

		for (Element element : elements) {
			Element relationshipElement = element.element(
				_getQName("Relationship"));

			Element relatedSPDXElement = relationshipElement.element(
				_getQName("relatedSpdxElement"));

			Element packageElement = relatedSPDXElement.element(
				_getQName("Package"));

			List<Element> libraryElements = _createLibraryElements(
				packageElement, licenseOverrideProperties);

			for (Element libraryElement : libraryElements) {
				libraryElementMap.put(
					_getKey("spdx", libraryElement), libraryElement);
			}
		}

		Document document = SAXReaderUtil.createDocument();

		ProcessingInstruction processingInstruction =
			SAXReaderUtil.createProcessingInstruction(
				"xml-stylesheet",
				HashMapBuilder.put(
					"href", "versions.xsl"
				).put(
					"type", "text/xsl"
				).build());

		document.add(processingInstruction);

		Element versionsElement = document.addElement("versions");

		Element versionElement = versionsElement.addElement("version");

		Element librariesElement = versionElement.addElement("libraries");

		for (Element libraryElement : libraryElementMap.values()) {
			librariesElement.add(libraryElement.detach());
		}

		return document;
	}

	private String _getKey(String type, Element libraryElement) {
		StringBundler sb = new StringBundler(5);

		sb.append(StringUtil.upperCase(type));
		sb.append(StringPool.COLON);

		Node fileNameNode = libraryElement.selectSingleNode("file-name");

		sb.append(fileNameNode.getText());

		sb.append(StringPool.COLON);

		Node versionNode = libraryElement.selectSingleNode("version");

		sb.append(versionNode.getText());

		return sb.toString();
	}

	private String _getLicenseName(
		Element packageElement, String fileName,
		Properties licenseOverrideProperties) {

		String key = "license.name[" + fileName + "]";

		if (licenseOverrideProperties.containsKey(key)) {
			return licenseOverrideProperties.getProperty(key);
		}

		Element licenseConcludedElement = packageElement.element(
			_getQName("licenseConcluded"));

		if (licenseConcludedElement == null) {
			return null;
		}

		String resource = licenseConcludedElement.attributeValue(
			_getQName("resource"));

		if (resource.startsWith("http://spdx.org/licenses/")) {
			return resource.substring(25);
		}

		if (resource.startsWith("LicenseRef-")) {
			return resource.substring(11);
		}

		return null;
	}

	private String _getLicenseURL(
		Element packageElement, String fileName, Properties licenseProperties) {

		String key = "license.url[" + fileName + "]";

		if (licenseProperties.containsKey(key)) {
			return licenseProperties.getProperty(key);
		}

		Element licenseConcludedElement = packageElement.element(
			_getQName("licenseConcluded"));

		String resource = licenseConcludedElement.attributeValue(
			_getQName("resource"));

		if (!resource.startsWith("http")) {
			return null;
		}

		return resource;
	}

	private QName _getQName(String name) {
		if (!_qNameMap.containsKey(name)) {
			QName qName = SAXReaderUtil.createQName(
				name,
				SAXReaderUtil.createNamespace(
					"spdx", "http://spdx.org/rdf/terms#"));

			if (Objects.equals(name, "resource")) {
				qName = SAXReaderUtil.createQName(
					name,
					SAXReaderUtil.createNamespace(
						"rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
			}

			_qNameMap.put(name, qName);
		}

		return _qNameMap.get(name);
	}

	@SuppressWarnings("unchecked")
	private String _toCSV(Document document) {
		StringBundler sb = new StringBundler();

		sb.append("File Name,Version,Project,License,Comments");

		List<Node> libraryNodes = document.selectNodes("//library");

		for (Node libraryNode : libraryNodes) {
			sb.append('\n');

			sb.append(_encode(libraryNode.selectSingleNode("file-name")));
			sb.append(',');
			sb.append(_encode(libraryNode.selectSingleNode("version")));
			sb.append(',');
			sb.append(_encode(libraryNode.selectSingleNode("project-name")));
			sb.append(',');

			String[] licenses = new String[0];

			List<Node> licenseNameNodes = libraryNode.selectNodes(
				"./licenses/license/license-name");

			if (!licenseNameNodes.isEmpty()) {
				String text = StringPool.BLANK;

				if (licenseNameNodes.get(0) != null) {
					Node node = licenseNameNodes.get(0);

					text = node.getText();

					text = StringUtil.trim(text.replaceAll("\\s+", " "));
				}

				licenses = ArrayUtil.append(licenses, text);
			}

			sb.append(CSVUtil.encode(licenses));

			sb.append(',');
			sb.append(_encode(libraryNode.selectSingleNode("comments")));
		}

		return sb.toString();
	}

	private void _write(File file, String s) throws Exception {
		Files.write(file.toPath(), s.getBytes(StandardCharsets.UTF_8));
	}

	private static final Log _log = LogFactoryUtil.getLog(SPDXBuilder.class);

	private static final Map<String, QName> _qNameMap = new HashMap<>();

}