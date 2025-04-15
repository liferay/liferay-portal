/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Michael C. Han
 */
public class ServiceContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testJSONSerialization() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute("TestName", "TestValue");
		serviceContext.setHeaders(
			HashMapBuilder.put(
				"TestHeaderName", "TestHeaderValue"
			).build());
		serviceContext.setRequest(
			ProxyFactory.newDummyInstance(HttpServletRequest.class));

		String json = JSONFactoryUtil.serialize(serviceContext);

		ServiceContext deserializedServiceContext =
			(ServiceContext)JSONFactoryUtil.deserialize(json);

		Assert.assertEquals(
			deserializedServiceContext.getAttributes(),
			serviceContext.getAttributes());
		Assert.assertNull(deserializedServiceContext.getHeaders());
		Assert.assertNull(deserializedServiceContext.getRequest());
	}

}