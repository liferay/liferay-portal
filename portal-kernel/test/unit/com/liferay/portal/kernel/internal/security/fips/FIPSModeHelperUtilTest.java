/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.security.fips;

import com.liferay.portal.kernel.security.fips.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

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
		Document document = FIPSModeHelperUtil.readDocument(
			FIPSModeTestUtil.getChannelPropertiesLocation(
				"cluster-link-channel-properties.xml"));

		NodeList nodeList = document.getElementsByTagName("AUTH");

		Assert.assertEquals(1, nodeList.getLength());

		Element authElement = (Element)nodeList.item(0);

		Assert.assertEquals(
			FIPSModeTestUtil.AUTH_CLASS_NAME,
			authElement.getAttribute("auth_class"));

		FIPSModeTestUtil.assertSecurityException(
			"Unable to parse the cluster link channel properties",
			() -> FIPSModeHelperUtil.readDocument(
				FIPSModeTestUtil.getChannelPropertiesLocation(
					"cluster-link-channel-properties-doctype.xml")));
		FIPSModeTestUtil.assertSecurityException(
			"Unable to read the cluster link channel properties",
			() -> FIPSModeHelperUtil.readDocument(
				RandomTestUtil.randomString()));
	}

}