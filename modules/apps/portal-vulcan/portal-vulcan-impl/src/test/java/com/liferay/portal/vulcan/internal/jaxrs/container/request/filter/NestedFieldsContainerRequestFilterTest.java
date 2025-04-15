/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.impl.PathSegmentImpl;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsContainerRequestFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testFilter() throws IOException {
		NestedFieldsContainerRequestFilter nestedFieldsContainerRequestFilter =
			new NestedFieldsContainerRequestFilter();

		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		UriInfo uriInfo = Mockito.mock(UriInfo.class);

		Mockito.when(
			containerRequestContext.getUriInfo()
		).thenReturn(
			uriInfo
		);

		List<PathSegment> pathSegments = new ArrayList<>();

		pathSegments.add(new PathSegmentImpl("v1.0"));
		pathSegments.add(new PathSegmentImpl("products"));

		Mockito.when(
			uriInfo.getPathSegments()
		).thenReturn(
			pathSegments
		);

		MultivaluedMap<String, String> queryParameters =
			new MultivaluedHashMap<>();

		Mockito.when(
			uriInfo.getQueryParameters()
		).thenReturn(
			queryParameters
		);

		nestedFieldsContainerRequestFilter.filter(containerRequestContext);

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		Assert.assertNotNull(nestedFieldsContext);

		queryParameters.putSingle("nestedFields", "skus,productOptions");

		nestedFieldsContainerRequestFilter.filter(containerRequestContext);

		nestedFieldsContext =
			NestedFieldsContextThreadLocal.getNestedFieldsContext();

		List<String> nestedFields = nestedFieldsContext.getNestedFields();

		Assert.assertEquals(nestedFields.toString(), 2, nestedFields.size());
	}

}