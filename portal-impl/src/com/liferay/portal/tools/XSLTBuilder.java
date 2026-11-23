/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.ProcessingInstruction;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Brian Wing Shun Chan
 */
public class XSLTBuilder {

	public static void main(String[] args) throws IOException {
		ToolDependencies.wireBasic();

		if (args.length == 2) {
			String xmls = null;

			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in))) {

				xmls = bufferedReader.readLine();
			}

			new XSLTBuilder(StringUtil.split(xmls), args[0], args[1]);
		}
		else if (args.length == 3) {
			new XSLTBuilder(StringUtil.split(args[0]), args[1], args[2]);
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	public XSLTBuilder(String xml, String xsl, String html) {
		this(new String[] {xml}, xsl, html);
	}

	public XSLTBuilder(String[] xmls, String xsl, String html) {
		try {
			System.setProperty("line.separator", StringPool.NEW_LINE);

			String prefix = html.substring(
				0, html.lastIndexOf(CharPool.PERIOD));

			Document document = _combineAndSortXMLs(xmls, prefix + ".xsl");

			if (xmls.length > 1) {
				String completeXml = prefix + "-complete.xml";

				String completeContent = document.formattedString();

				Files.write(
					Paths.get(completeXml),
					completeContent.getBytes(StandardCharsets.UTF_8));
			}

			TransformerFactory transformerFactory =
				SecureXMLFactoryProviderUtil.newTransformerFactory();

			Transformer transformer = transformerFactory.newTransformer(
				new StreamSource(xsl));

			String xml = document.formattedString();

			transformer.transform(
				new StreamSource(new UnsyncStringReader(xml)),
				new StreamResult(new FileOutputStream(html)));
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private Document _combineAndSortXMLs(String[] xmls, String xsl)
		throws Exception {

		Map<String, Element> elementMap = new TreeMap<>();

		for (String xml : xmls) {
			Document document = SAXReaderUtil.read(new File(xml));

			List<Node> nodes = document.selectNodes("//file-name");

			for (Node node : nodes) {
				elementMap.put(node.getText(), node.getParent());
			}
		}

		Document document = SAXReaderUtil.createDocument();

		File xslFile = new File(xsl);

		if (xslFile.exists()) {
			ProcessingInstruction processingInstruction =
				SAXReaderUtil.createProcessingInstruction(
					"xml-stylesheet",
					HashMapBuilder.put(
						"href", xslFile.getName()
					).put(
						"type", "text/xsl"
					).build());

			document.add(processingInstruction);
		}

		Element versionsElement = document.addElement("versions");

		Element versionElement = versionsElement.addElement("version");

		Element librariesElement = versionElement.addElement("libraries");

		for (Element element : elementMap.values()) {
			librariesElement.add(element.detach());
		}

		return document;
	}

	private static final Log _log = LogFactoryUtil.getLog(XSLTBuilder.class);

}