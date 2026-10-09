/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.log.assertor;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Shuyang Zhou
 */
public class PortalLogAssertorTest {

	@BeforeClass
	public static void setUpClass() {
		Assume.assumeNotNull(System.getenv("JENKINS_HOME"));
	}

	@Test
	public void testScanOSGiLog() throws IOException {
		StringBundler sb = new StringBundler();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				Paths.get(System.getProperty("osgi.state.dir")), "*.log")) {

			for (Path path : directoryStream) {
				if (!Files.isRegularFile(path)) {
					continue;
				}

				sb.append("\nPortal log assert failure, OSGi log found: ");
				sb.append(path);
				sb.append(StringPool.COLON);
				sb.append(StringPool.NEW_LINE);
				sb.append(
					new String(
						Files.readAllBytes(path), Charset.defaultCharset()));
				sb.append(StringPool.NEW_LINE);
			}
		}

		Assert.assertTrue(sb.toString(), sb.index() == 0);
	}

	@Test
	public void testScanXMLLog() throws IOException {
		Files.walkFileTree(
			Paths.get(System.getProperty("liferay.log.dir")),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String pathString = StringUtil.toLowerCase(path.toString());

					if (pathString.endsWith(".xml")) {
						scanXMLLogFile(path);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	protected void scanXMLLogFile(Path path) throws IOException {
		String content = StringUtil.removeSubstring(
			new String(Files.readAllBytes(path), StringPool.UTF8), "log4j:");

		int index = content.lastIndexOf("</event>");

		if (index != -1) {
			content = content.substring(0, index + "</event>".length());
		}

		content = "<log4j>" + content + "</log4j>";

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(
				new InputSource(new UnsyncStringReader(content)));

			NodeList nodeList = document.getElementsByTagName("event");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				NamedNodeMap namedNodeMap = node.getAttributes();

				Node levelNode = namedNodeMap.getNamedItem("level");

				String levelString = levelNode.getNodeValue();

				if (!levelString.equals("ERROR") &&
					!levelString.equals("FATAL") &&
					!levelString.equals("WARN")) {

					continue;
				}

				NodeList childNodeList = node.getChildNodes();

				String message =
					"\nPortal log assert failure, see above log for more " +
						"information: \n";

				for (int j = 0; j < childNodeList.getLength(); j++) {
					Node childNode = childNodeList.item(j);

					String nodeName = childNode.getNodeName();

					if (nodeName.equals("message")) {
						message += childNode.getTextContent();
					}
					else if (nodeName.equals("throwable")) {
						message += "\n" + childNode.getTextContent();
					}
				}

				System.out.println(
					"Error detected, dumping the full log for reference:");

				Files.copy(
					Paths.get(
						StringUtil.replace(path.toString(), ".xml", ".log")),
					System.out);

				Assert.assertFalse(
					message,
					levelString.equals("ERROR") ||
					levelString.equals("FATAL") || levelString.equals("WARN"));
			}
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

}