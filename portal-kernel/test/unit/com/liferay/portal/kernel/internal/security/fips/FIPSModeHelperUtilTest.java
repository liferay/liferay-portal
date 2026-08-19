/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.fips.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Caio Farias
 */
public class FIPSModeHelperUtilTest {

	@Test
	public void testGetJGroupsProfileSecurityElements() throws Exception {
		Path path = Files.createTempFile(null, ".xml");

		try {
			String channelPropertiesXML = StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_AUTH,
				FIPSModeTestUtil.XML_SYM_ENCRYPT, "</config>");

			Files.write(
				path, channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

			Map<String, Element> securityElements =
				FIPSModeHelperUtil.getJGroupsProfileSecurityElements(
					String.valueOf(path));

			Assert.assertEquals(
				List.of("AUTH", "SYM_ENCRYPT"),
				List.copyOf(securityElements.keySet()));

			Element authElement = securityElements.get("AUTH");

			Assert.assertEquals(
				FIPSModeTestUtil.AUTH_CLASS_NAME,
				authElement.getAttribute("auth_class"));

			Files.write(path, "<config><AUTH".getBytes(StandardCharsets.UTF_8));

			FIPSModeTestUtil.assertSecurityException(
				"Unable to parse the JGroups channel properties",
				() -> FIPSModeHelperUtil.getJGroupsProfileSecurityElements(
					String.valueOf(path)));
		}
		finally {
			Files.delete(path);
		}

		FIPSModeTestUtil.assertSecurityException(
			"Unable to read the JGroups channel properties",
			() -> FIPSModeHelperUtil.getJGroupsProfileSecurityElements(
				RandomTestUtil.randomString()));
	}

	@Test
	public void testGetSecurityElements() {
		Document document1 = ReflectionTestUtil.invoke(
			FIPSModeHelperUtil.class, "_toDocument",
			new Class<?>[] {String.class},
			StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_ASYM_ENCRYPT,
				FIPSModeTestUtil.XML_AUTH, "</config>"));

		Map<String, Element> securityElements = ReflectionTestUtil.invoke(
			FIPSModeHelperUtil.class, "_getSecurityElements",
			new Class<?>[] {Document.class}, document1);

		Assert.assertEquals(
			List.of("ASYM_ENCRYPT", "AUTH"),
			List.copyOf(securityElements.keySet()));

		Element asymEncryptElement = securityElements.get("ASYM_ENCRYPT");

		Assert.assertEquals(
			"2048", asymEncryptElement.getAttribute("asym_keylength"));
		Assert.assertEquals(
			FIPSModeTestUtil.TRANSFORMATION_SYM,
			asymEncryptElement.getAttribute("sym_algorithm"));

		Element authElement = securityElements.get("AUTH");

		Assert.assertEquals(
			FIPSModeTestUtil.AUTH_CLASS_NAME,
			authElement.getAttribute("auth_class"));

		Document document2 = ReflectionTestUtil.invoke(
			FIPSModeHelperUtil.class, "_toDocument",
			new Class<?>[] {String.class}, "<config><UDP /></config>");

		Assert.assertEquals(
			Collections.emptyMap(),
			ReflectionTestUtil.invoke(
				FIPSModeHelperUtil.class, "_getSecurityElements",
				new Class<?>[] {Document.class}, document2));

		Document document3 = ReflectionTestUtil.invoke(
			FIPSModeHelperUtil.class, "_toDocument",
			new Class<?>[] {String.class},
			StringBundler.concat(
				"<config><!--", FIPSModeTestUtil.XML_SYM_ENCRYPT, "-->",
				FIPSModeTestUtil.XML_ASYM_ENCRYPT, "</config>"));

		securityElements = ReflectionTestUtil.invoke(
			FIPSModeHelperUtil.class, "_getSecurityElements",
			new Class<?>[] {Document.class}, document3);

		Assert.assertEquals(
			List.of("ASYM_ENCRYPT"), List.copyOf(securityElements.keySet()));
	}

}