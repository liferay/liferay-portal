/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.response.filter;

import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leslie Wong
 */
public class OriginContainerResponseFilterTest {

	@Test
	public void testFilter() {
		FaroThreadLocal.setOrigin(RandomTestUtil.randomString());

		OriginContainerResponseFilter originContainerResponseFilter =
			new OriginContainerResponseFilter();

		originContainerResponseFilter.filter(
			Mockito.mock(ContainerRequestContext.class),
			Mockito.mock(ContainerResponseContext.class));

		Assert.assertNull(FaroThreadLocal.getOrigin());
	}

}