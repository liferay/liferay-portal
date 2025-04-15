/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveWebKeys;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Cristina González
 */
public class OAuth2ControllerTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_oAuth2Manager = Mockito.mock(OAuth2Manager.class);

		Mockito.when(
			_oAuth2Manager.getAuthorizationURL(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			"authorizationURL"
		);

		_portal = Mockito.mock(Portal.class);

		Mockito.when(
			_portal.getPortalURL((PortletRequest)Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		_portletURLFactory = Mockito.mock(PortletURLFactory.class);

		_liferayPortletURL = Mockito.mock(LiferayPortletURL.class);

		Mockito.when(
			_liferayPortletURL.toString()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_portletURLFactory.create(
				Mockito.any(PortletRequest.class),
				Mockito.nullable(String.class), Mockito.anyLong(),
				Mockito.nullable(String.class))
		).thenReturn(
			_liferayPortletURL
		);

		Mockito.when(
			_portletURLFactory.create(
				Mockito.any(PortletRequest.class),
				Mockito.nullable(String.class), Mockito.nullable(String.class))
		).thenReturn(
			_liferayPortletURL
		);

		_oAuth2ControllerFactory = new OAuth2ControllerFactory();

		ReflectionTestUtil.setFieldValue(
			_oAuth2ControllerFactory, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_oAuth2ControllerFactory, "_oAuth2Manager", _oAuth2Manager);
		ReflectionTestUtil.setFieldValue(
			_oAuth2ControllerFactory, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_oAuth2ControllerFactory, "_portletURLFactory", _portletURLFactory);
	}

	@Test
	public void testJSONOAuth2ControllerWithAccessToken()
		throws PortalException, UnsupportedEncodingException {

		Mockito.when(
			_oAuth2Manager.hasAccessToken(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			true
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		JSONObject jsonObject = JSONUtil.put("key", "value");

		OAuth2Controller oAuth2Controller =
			_oAuth2ControllerFactory.getJSONOAuth2Controller(null);

		oAuth2Controller.execute(
			_getMockPortletRequest(mockHttpServletRequest),
			_getMockPortletResponse(mockHttpServletResponse),
			portletRequest -> jsonObject);

		Assert.assertNull(
			mockHttpServletRequest.getAttribute(WebKeys.REDIRECT));

		Assert.assertEquals(
			jsonObject.toString(),
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testJSONOAuth2ControllerWithoutAccessToken()
		throws PortalException, UnsupportedEncodingException {

		Mockito.when(
			_oAuth2Manager.hasAccessToken(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			false
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		OAuth2Controller oAuth2Controller =
			_oAuth2ControllerFactory.getJSONOAuth2Controller(
				portalResquest -> RandomTestUtil.randomString());

		oAuth2Controller.execute(
			_getMockPortletRequest(mockHttpServletRequest),
			_getMockPortletResponse(mockHttpServletResponse),
			portletRequest -> JSONFactoryUtil.createJSONObject());

		Assert.assertNull(
			mockHttpServletRequest.getAttribute(WebKeys.REDIRECT));
		Assert.assertEquals(
			JSONUtil.put(
				"redirectURL", "authorizationURL"
			).toString(),
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testRedirectingOAuth2ControllerWithAccessToken()
		throws PortalException {

		Mockito.when(
			_oAuth2Manager.hasAccessToken(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			true
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		JSONObject jsonObject1 = JSONUtil.put("key", "value");

		OAuth2Controller oAuth2Controller =
			_oAuth2ControllerFactory.getRedirectingOAuth2Controller();

		oAuth2Controller.execute(
			_getMockPortletRequest(mockHttpServletRequest),
			_getMockPortletResponse(mockHttpServletResponse),
			portletRequest -> jsonObject1);

		Assert.assertEquals(
			_liferayPortletURL.toString(),
			mockHttpServletRequest.getAttribute(WebKeys.REDIRECT));

		HttpSession httpSession = mockHttpServletRequest.getSession();

		Assert.assertNotNull(httpSession);

		JSONObject jsonObject2 = (JSONObject)httpSession.getAttribute(
			DLOpenerOneDriveWebKeys.
				DL_OPENER_ONE_DRIVE_REDIRECTING_OAUTH2_JSON_OBJECT);

		Assert.assertNotNull(jsonObject2);
		Assert.assertEquals(jsonObject1.get("key"), jsonObject2.get("key"));
	}

	@Test
	public void testRedirectingOAuth2ControllerWithoutAccessToken()
		throws PortalException {

		Mockito.when(
			_oAuth2Manager.hasAccessToken(Mockito.anyLong(), Mockito.anyLong())
		).thenReturn(
			false
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		OAuth2Controller oAuth2Controller =
			_oAuth2ControllerFactory.getRedirectingOAuth2Controller();

		oAuth2Controller.execute(
			_getMockPortletRequest(mockHttpServletRequest),
			_getMockPortletResponse(mockHttpServletResponse),
			portletRequest -> JSONFactoryUtil.createJSONObject());

		Assert.assertEquals(
			"authorizationURL",
			mockHttpServletRequest.getAttribute(WebKeys.REDIRECT));

		HttpSession httpSession = mockHttpServletRequest.getSession();

		Assert.assertNotNull(
			httpSession.getAttribute(
				DLOpenerOneDriveWebKeys.DL_OPENER_ONE_DRIVE_OAUTH2_STATE));
	}

	private PortletRequest _getMockPortletRequest(
		HttpServletRequest httpServletRequest) {

		Mockito.when(
			_portal.getCurrentURL(httpServletRequest)
		).thenReturn(
			"currentURL"
		);

		PortletRequest portletRequest = Mockito.mock(PortletRequest.class);

		Mockito.when(
			_portal.getHttpServletRequest(portletRequest)
		).thenReturn(
			httpServletRequest
		);

		Mockito.when(
			_portal.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			httpServletRequest
		);

		_portletURLFactory.create(
			portletRequest, _portal.getPortletId(portletRequest),
			PortletRequest.ACTION_PHASE);

		Mockito.when(
			portletRequest.getParameter(Mockito.anyString())
		).thenAnswer(
			invocation -> {
				Object[] arguments = invocation.getArguments();

				return httpServletRequest.getParameter(
					String.valueOf(arguments[0]));
			}
		);

		Mockito.when(
			portletRequest.getAttribute(Mockito.anyString())
		).thenAnswer(
			invocation -> {
				Object[] arguments = invocation.getArguments();

				return httpServletRequest.getAttribute(
					String.valueOf(arguments[0]));
			}
		);

		Mockito.doAnswer(
			answer -> {
				Object[] arguments = answer.getArguments();

				httpServletRequest.setAttribute(
					String.valueOf(arguments[0]), arguments[1]);

				return null;
			}
		).when(
			portletRequest
		).setAttribute(
			Mockito.anyString(), Mockito.any()
		);

		return portletRequest;
	}

	private PortletResponse _getMockPortletResponse(
		HttpServletResponse httpServletResponse) {

		PortletResponse portletResponse = Mockito.mock(PortletResponse.class);

		Mockito.when(
			_portal.getHttpServletResponse(portletResponse)
		).thenReturn(
			httpServletResponse
		);

		return portletResponse;
	}

	private static LiferayPortletURL _liferayPortletURL;
	private static OAuth2ControllerFactory _oAuth2ControllerFactory;
	private static OAuth2Manager _oAuth2Manager;
	private static Portal _portal;
	private static PortletURLFactory _portletURLFactory;

}