/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

/**
 * @author Caio Farias
 */
public class FIPSModeHelperUtil {

	public static Map<String, Element> getJGroupsProfileSecurityElements(
		String channelPropertiesLocation) {

		String channelPropertiesXML;

		try {
			channelPropertiesXML = _readChannelPropertiesXML(
				channelPropertiesLocation);
		}
		catch (IOException ioException) {
			throw new SecurityException(
				StringBundler.concat(
					"Unable to read the JGroups channel properties \"",
					channelPropertiesLocation, "\" in FIPS mode"),
				ioException);
		}

		Document document;

		try {
			document = _toDocument(channelPropertiesXML);
		}
		catch (Exception exception) {
			throw new SecurityException(
				StringBundler.concat(
					"Unable to parse the JGroups channel properties \"",
					channelPropertiesLocation, "\" in FIPS mode"),
				exception);
		}

		return _getSecurityElements(document);
	}

	private static Map<String, Element> _getSecurityElements(
		Document document) {

		Map<String, Element> securityElements = new LinkedHashMap<>();

		NodeList nodeList = document.getElementsByTagName(StringPool.STAR);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);

			String tagName = element.getTagName();

			if (tagName.equals("AUTH") || tagName.contains("ENCRYPT")) {
				securityElements.put(tagName, element);
			}
		}

		return securityElements;
	}

	private static String _readChannelPropertiesXML(
			String channelPropertiesLocation)
		throws IOException {

		Path channelPropertiesPath = Paths.get(channelPropertiesLocation);

		if (Files.isRegularFile(channelPropertiesPath)) {
			try (InputStream inputStream = Files.newInputStream(
					channelPropertiesPath)) {

				return StringUtil.read(inputStream);
			}
		}

		Path jarPath = Paths.get(
			GetterUtil.getString(PropsValues.MODULE_FRAMEWORK_PORTAL_DIR),
			"com.liferay.portal.cluster.multiple.jar");

		URI uri = jarPath.toUri();

		try (URLClassLoader urlClassLoader = new URLClassLoader(
				new URL[] {uri.toURL()},
				FIPSModeHelperUtil.class.getClassLoader())) {

			return StringUtil.read(urlClassLoader, channelPropertiesLocation);
		}
	}

	private static Document _toDocument(String channelPropertiesXML)
		throws Exception {

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		documentBuilderFactory.setExpandEntityReferences(false);
		documentBuilderFactory.setFeature(
			XMLConstants.FEATURE_SECURE_PROCESSING, true);
		documentBuilderFactory.setFeature(
			"http://apache.org/xml/features/disallow-doctype-decl", true);
		documentBuilderFactory.setFeature(
			"http://xml.org/sax/features/external-general-entities", false);
		documentBuilderFactory.setFeature(
			"http://xml.org/sax/features/external-parameter-entities", false);
		documentBuilderFactory.setXIncludeAware(false);

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		documentBuilder.setEntityResolver(
			(publicId, systemId) -> new InputSource(
				new StringReader(StringPool.BLANK)));

		return documentBuilder.parse(
			new InputSource(new StringReader(channelPropertiesXML)));
	}

}