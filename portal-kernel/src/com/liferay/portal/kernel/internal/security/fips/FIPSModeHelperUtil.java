/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.xml.sax.InputSource;

/**
 * @author Caio Farias
 */
public class FIPSModeHelperUtil {

	public static Document readDocument(String channelPropertiesLocation) {
		try (InputStream inputStream = Files.newInputStream(
				Paths.get(channelPropertiesLocation))) {

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
				"http://xml.org/sax/features/external-parameter-entities",
				false);
			documentBuilderFactory.setXIncludeAware(false);

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			documentBuilder.setEntityResolver(
				(publicId, systemId) -> new InputSource(
					new StringReader(StringPool.BLANK)));

			return documentBuilder.parse(
				new InputSource(
					new StringReader(StringUtil.read(inputStream))));
		}
		catch (IOException ioException) {
			throw new SecurityException(
				StringBundler.concat(
					"Unable to read the cluster link channel properties \"",
					channelPropertiesLocation, "\" in FIPS mode"),
				ioException);
		}
		catch (Exception exception) {
			throw new SecurityException(
				StringBundler.concat(
					"Unable to parse the cluster link channel properties \"",
					channelPropertiesLocation, "\" in FIPS mode"),
				exception);
		}
	}

}