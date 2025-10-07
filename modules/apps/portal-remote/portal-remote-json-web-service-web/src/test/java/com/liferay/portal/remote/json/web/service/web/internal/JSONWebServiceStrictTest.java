/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.exception.NoSuchJSONWebServiceException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceStrictTest extends BaseJSONWebServiceTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testStrictHttpMethod() throws Exception {
		PropsUtil.set(PropsKeys.JSONWS_WEB_SERVICE_STRICT_HTTP_METHOD, "true");

		initPortalServices();

		registerActionClass(CamelFooService.class);

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/camelfoo/post/value/123");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (NoSuchJSONWebServiceException noSuchJSONWebServiceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchJSONWebServiceException);
			}
		}

		mockHttpServletRequest = createHttpRequest(
			"/camelfoo/post/value/123", HttpMethods.POST);

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertNotNull(jsonWebServiceAction);

		Assert.assertEquals("post 123", jsonWebServiceAction.invoke());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceStrictTest.class);

}