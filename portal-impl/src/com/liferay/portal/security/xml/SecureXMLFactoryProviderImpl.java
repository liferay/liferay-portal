/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.xml;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProvider;
import com.liferay.portal.kernel.util.PropsValues;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.XMLReader;

/**
 * @author Tomas Polesovsky
 */
public class SecureXMLFactoryProviderImpl implements SecureXMLFactoryProvider {

	@Override
	public DocumentBuilderFactory newDocumentBuilderFactory() {
		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		if (!PropsValues.XML_SECURITY_ENABLED) {
			return documentBuilderFactory;
		}

		try {
			documentBuilderFactory.setFeature(
				XMLConstants.FEATURE_SECURE_PROCESSING, true);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize safe document builder factory to " +
					"protect from XML Bomb attacks",
				exception);
		}

		try {
			documentBuilderFactory.setFeature(
				_FEATURES_DISALLOW_DOCTYPE_DECL, true);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize safe document builder factory to " +
					"protect from XML Bomb attacks",
				exception);
		}

		try {
			documentBuilderFactory.setExpandEntityReferences(false);
			documentBuilderFactory.setFeature(
				_FEATURES_EXTERNAL_GENERAL_ENTITIES, false);
			documentBuilderFactory.setFeature(
				_FEATURES_EXTERNAL_PARAMETER_ENTITIES, false);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize safe document builder factory to " +
					"protect from XXE attacks",
				exception);
		}

		return documentBuilderFactory;
	}

	@Override
	public TransformerFactory newTransformerFactory()
		throws TransformerFactoryConfigurationError {

		TransformerFactory transformerFactory =
			TransformerFactory.newInstance();

		if (!PropsValues.XML_SECURITY_ENABLED) {
			return transformerFactory;
		}

		transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		transformerFactory.setAttribute(
			XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		return transformerFactory;
	}

	@Override
	public XMLInputFactory newXMLInputFactory() {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

		if (!PropsValues.XML_SECURITY_ENABLED) {
			return xmlInputFactory;
		}

		xmlInputFactory.setProperty(
			XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
		xmlInputFactory.setProperty(
			XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);

		return xmlInputFactory;
	}

	@Override
	public XMLReader newXMLReader() {
		XMLReader xmlReader = null;

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				SecureXMLFactoryProviderImpl.class.getClassLoader())) {

			xmlReader = new SAXParser();
		}
		catch (RuntimeException runtimeException) {
			throw new SystemException(runtimeException);
		}

		if (!PropsValues.XML_SECURITY_ENABLED) {
			return xmlReader;
		}

		xmlReader = new StripDoctypeXMLReader(xmlReader);

		try {
			xmlReader.setFeature(_FEATURES_DISALLOW_DOCTYPE_DECL, true);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize safe SAX parser to protect from XML " +
					"Bomb attacks",
				exception);
		}

		try {
			xmlReader.setFeature(_FEATURES_EXTERNAL_GENERAL_ENTITIES, false);
			xmlReader.setFeature(_FEATURES_EXTERNAL_PARAMETER_ENTITIES, false);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize safe SAX parser to protect from XXE " +
					"attacks",
				exception);
		}

		return xmlReader;
	}

	private static final String _FEATURES_DISALLOW_DOCTYPE_DECL =
		"http://apache.org/xml/features/disallow-doctype-decl";

	private static final String _FEATURES_EXTERNAL_GENERAL_ENTITIES =
		"http://xml.org/sax/features/external-general-entities";

	private static final String _FEATURES_EXTERNAL_PARAMETER_ENTITIES =
		"http://xml.org/sax/features/external-parameter-entities";

	private static final Log _log = LogFactoryUtil.getLog(
		SecureXMLFactoryProviderImpl.class);

}