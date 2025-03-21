/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class SupportedPublicRenderParameterTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortalProvidedPRP() throws Exception {
		String prpName = "categoryId";

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.supported-public-render-parameter", prpName
			).build(),
			TEST_PORTLET_ID);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestPropsValues.getCompanyId(), TEST_PORTLET_ID);

		Assert.assertFalse(portlet.isUndeployedPortlet());

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		Assert.assertNotNull(publicRenderParameters);

		boolean found = false;

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			if (prpName.equals(publicRenderParameter.getIdentifier())) {
				found = true;
			}
		}

		Assert.assertTrue(found);
	}

	@Test
	public void testPortletProvidedPRP() throws Exception {
		String prpName = "myprp";

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.supported-public-render-parameter",
				prpName + ";http://some.uri.tld/space"
			).build(),
			TEST_PORTLET_ID);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestPropsValues.getCompanyId(), TEST_PORTLET_ID);

		Assert.assertFalse(portlet.isUndeployedPortlet());

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		Assert.assertNotNull(publicRenderParameters);

		boolean found = false;

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			if (prpName.equals(publicRenderParameter.getIdentifier())) {
				found = true;
			}
		}

		Assert.assertTrue(found);
	}

}