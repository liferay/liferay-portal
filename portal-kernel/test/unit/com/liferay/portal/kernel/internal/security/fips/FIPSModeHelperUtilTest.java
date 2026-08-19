/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.fips.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Caio Farias
 */
public class FIPSModeHelperUtilTest {

	@Test
	public void testReadDocument() throws Exception {
		Path path = Files.createTempFile(null, ".xml");

		try {
			String channelPropertiesXML = StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_AUTH,
				FIPSModeTestUtil.XML_SYM_ENCRYPT, "</config>");

			Files.write(
				path, channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

			Document document = FIPSModeHelperUtil.readDocument(
				String.valueOf(path));

			NodeList nodeList = document.getElementsByTagName("AUTH");

			Assert.assertEquals(1, nodeList.getLength());

			Element authElement = (Element)nodeList.item(0);

			Assert.assertEquals(
				FIPSModeTestUtil.AUTH_CLASS_NAME,
				authElement.getAttribute("auth_class"));

			Files.write(path, "<config><AUTH".getBytes(StandardCharsets.UTF_8));

			FIPSModeTestUtil.assertSecurityException(
				"Unable to parse the cluster channel properties",
				() -> FIPSModeHelperUtil.readDocument(String.valueOf(path)));
		}
		finally {
			Files.delete(path);
		}

		FIPSModeTestUtil.assertSecurityException(
			"Unable to read the cluster channel properties",
			() -> FIPSModeHelperUtil.readDocument(
				RandomTestUtil.randomString()));
	}

}