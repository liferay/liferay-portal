/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.tld.formatter;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.xml.SAXReaderFactory;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class TLDFormatter {

	public static void main(String[] args) {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		String baseDirName = GetterUtil.getString(
			arguments.get("tld.base.dir"), TLDFormatterArgs.BASE_DIR_NAME);
		boolean plugin = GetterUtil.getBoolean(
			arguments.get("tld.plugin"), TLDFormatterArgs.PLUGIN);

		try {
			new TLDFormatter(baseDirName, plugin);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public TLDFormatter(String baseDirName, boolean plugin) throws Exception {
		_plugin = plugin;

		Files.walkFileTree(
			Paths.get(baseDirName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path file, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(file.getFileName());

					if (!fileName.endsWith(".tld") ||
						(!_plugin &&
						 fileName.equals("liferay-portlet-ext.tld"))) {

						return FileVisitResult.CONTINUE;
					}

					try {
						_formatTLD(file);
					}
					catch (IOException ioException) {
						throw ioException;
					}
					catch (Exception exception) {
						throw new IOException(exception);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public Set<String> getModifiedFileNames() {
		return _modifiedFileNames;
	}

	private void _formatTLD(Path file) throws Exception {
		System.setProperty(
			"javax.xml.parsers.SAXParserFactory",
			"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");

		String content = new String(
			Files.readAllBytes(file), StandardCharsets.UTF_8);

		SAXReader saxReader = _getSAXReader();

		Document document = saxReader.read(new UnsyncStringReader(content));

		Element root = document.getRootElement();

		_sortElements(root, "tag", "name");

		List<Element> tagElements = root.elements("tag");

		for (Element tagElement : tagElements) {
			_sortElements(tagElement, "attribute", "name");

			Element dynamicAttributesElement = tagElement.element(
				"dynamic-attributes");

			if (dynamicAttributesElement == null) {
				continue;
			}

			dynamicAttributesElement.detach();

			tagElement.add(dynamicAttributesElement);
		}

		String newContent = _toString(document);

		int x = newContent.indexOf("<tlib-version");
		int y = newContent.indexOf("</taglib>");

		newContent = newContent.substring(x, y);

		x = content.indexOf("<tlib-version");
		y = content.indexOf("</taglib>");

		newContent =
			content.substring(0, x) + newContent + content.substring(y);

		if (!content.equals(newContent)) {
			Files.write(file, newContent.getBytes(StandardCharsets.UTF_8));

			_modifiedFileNames.add(file.toString());

			System.out.println(file);
		}
	}

	private SAXReader _getSAXReader() {
		return SAXReaderFactory.getSAXReader(null, false, true);
	}

	private void _sortElements(
		Element parentElement, String name, String sortBy) {

		Map<String, Element> map = new TreeMap<>();

		List<Element> elements = parentElement.elements(name);

		for (Element element : elements) {
			map.put(element.elementText(sortBy), element);

			element.detach();
		}

		for (Map.Entry<String, Element> entry : map.entrySet()) {
			Element element = entry.getValue();

			parentElement.add(element);
		}
	}

	private String _toString(Node node) throws Exception {
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

	private final Set<String> _modifiedFileNames = new HashSet<>();
	private final boolean _plugin;

}