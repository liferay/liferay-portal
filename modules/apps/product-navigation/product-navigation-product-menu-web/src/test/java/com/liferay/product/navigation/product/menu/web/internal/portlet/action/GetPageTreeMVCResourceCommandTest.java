/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class GetPageTreeMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDoServeResourceWithTheProductMenuHidden() throws Exception {
		GetPageTreeMVCResourceCommand getPageTreeMVCResourceCommand =
			new GetPageTreeMVCResourceCommand();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);
		Portal portal = Mockito.mock(Portal.class);
		ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);

		Mockito.when(
			portal.getHttpServletRequest(resourceRequest)
		).thenReturn(
			httpServletRequest
		);

		ReflectionTestUtil.setFieldValue(
			getPageTreeMVCResourceCommand, "_portal", portal);
		ReflectionTestUtil.setFieldValue(
			getPageTreeMVCResourceCommand,
			"_productNavigationProductMenuHelper",
			Mockito.mock(ProductNavigationProductMenuHelper.class));

		ResourceResponse resourceResponse = Mockito.mock(
			ResourceResponse.class);

		getPageTreeMVCResourceCommand.doServeResource(
			resourceRequest, resourceResponse);

		Mockito.verifyNoInteractions(resourceResponse);

		Mockito.verify(
			resourceRequest, Mockito.never()
		).setAttribute(
			Mockito.anyString(), Mockito.any()
		);
	}

}