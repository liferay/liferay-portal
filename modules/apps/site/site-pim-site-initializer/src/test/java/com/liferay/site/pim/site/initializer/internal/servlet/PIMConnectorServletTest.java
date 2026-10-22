/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.servlet;

import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.constants.DispatchScreenNavigationConstants;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorRegistry;
import com.liferay.site.pim.site.initializer.internal.util.PIMConnectorDispatchTriggerUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMConnectorServletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_dispatchTrigger.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_dispatchTrigger.getDispatchTriggerId()
		).thenReturn(
			_DISPATCH_TRIGGER_ID
		);

		Mockito.when(
			_groupLocalService.getGroup(
				_COMPANY_ID, GroupConstants.CONTROL_PANEL)
		).thenReturn(
			_group
		);

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			_objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"key", _KEY
			).build()
		);

		Mockito.when(
			_objectEntryService.getObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			_objectEntry
		);

		_permissionCheckerFactoryUtilMockedStatic.when(
			() -> PermissionCheckerFactoryUtil.create(_user)
		).thenReturn(
			Mockito.mock(PermissionChecker.class)
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			Mockito.mock(PIMConnector.class)
		);

		_pimConnectorDispatchTriggerUtilMockedStatic.when(
			() -> PIMConnectorDispatchTriggerUtil.getDispatchTrigger(
				_objectEntry)
		).thenReturn(
			_dispatchTrigger
		);

		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_groupLocalService", _groupLocalService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_messageBus", _messageBus);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_objectEntryService", _objectEntryService);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_pimConnectorRegistry",
			_pimConnectorRegistry);
		ReflectionTestUtil.setFieldValue(
			_pimConnectorServlet, "_portal", _portal);

		Mockito.when(
			_portal.getPortalURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_PORTAL_URL
		);

		Mockito.when(
			_portal.getPortletNamespace(DispatchPortletKeys.DISPATCH)
		).thenReturn(
			"_namespace_"
		);

		Mockito.when(
			_portal.getSiteAdminURL(
				Mockito.eq(_PORTAL_URL), Mockito.eq(_group),
				Mockito.eq(DispatchPortletKeys.DISPATCH), Mockito.anyMap())
		).thenReturn(
			_CONTROL_PANEL_URL
		);

		Mockito.when(
			_portal.getUser(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_user
		);
	}

	@After
	public void tearDown() {
		_authTokenUtilMockedStatic.close();
		_permissionCheckerFactoryUtilMockedStatic.close();
		_pimConnectorDispatchTriggerUtilMockedStatic.close();
	}

	@Test
	public void testService() throws Exception {
		_testServiceWithExecutePath();
		_testServiceWithGuestUser();
		_testServiceWithIncompleteMapping();
		_testServiceWithInvalidCSRFToken();
		_testServiceWithInvalidPath();
		_testServiceWithSchedulePath();
		_testServiceWithUnexpectedException();
	}

	private void _assertError(
			String errorMessage,
			MockHttpServletResponse mockHttpServletResponse, int status)
		throws Exception {

		Assert.assertEquals(
			JSONUtil.put(
				"error", errorMessage
			).toString(),
			mockHttpServletResponse.getContentAsString());
		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON,
			mockHttpServletResponse.getContentType());
		Assert.assertEquals(status, mockHttpServletResponse.getStatus());
	}

	private MockHttpServletResponse _service(String method, String pathInfo)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(method, StringPool.BLANK);

		mockHttpServletRequest.setParameter("backURL", _BACK_URL);
		mockHttpServletRequest.setParameter(
			"objectEntryId", String.valueOf(_OBJECT_ENTRY_ID));
		mockHttpServletRequest.setPathInfo(pathInfo);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_pimConnectorServlet.service(
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private void _testServiceWithExecutePath() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _service(
			HttpMethods.POST, "/execute");

		Assert.assertEquals(
			HttpServletResponse.SC_NO_CONTENT,
			mockHttpServletResponse.getStatus());

		ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(
			Message.class);

		Mockito.verify(
			_messageBus
		).sendMessage(
			Mockito.eq(DispatchConstants.EXECUTOR_DESTINATION_NAME),
			argumentCaptor.capture()
		);

		Message message = argumentCaptor.getValue();

		Assert.assertEquals(_COMPANY_ID, message.getLong("companyId"));
		Assert.assertEquals(
			JSONUtil.put(
				"dispatchTriggerId", _DISPATCH_TRIGGER_ID
			).toString(),
			message.getPayload());
	}

	private void _testServiceWithGuestUser() throws Exception {
		Mockito.clearInvocations(_messageBus);

		Mockito.when(
			_user.isGuestUser()
		).thenReturn(
			true
		);

		_assertError(
			"you-do-not-have-permission-to-access-the-requested-resource",
			_service(HttpMethods.POST, "/execute"),
			HttpServletResponse.SC_UNAUTHORIZED);

		Mockito.verifyNoInteractions(_messageBus);

		Mockito.when(
			_user.isGuestUser()
		).thenReturn(
			false
		);
	}

	private void _testServiceWithIncompleteMapping() throws Exception {
		Mockito.clearInvocations(_messageBus);

		PIMConnector pimConnector = Mockito.mock(PIMConnector.class);

		Mockito.when(
			pimConnector.getPIMConnectorChannelFields()
		).thenReturn(
			Collections.singletonList(
				new PIMConnectorChannelField("sku", false, "sku", true, "Text"))
		);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			pimConnector
		);

		try (MockedStatic<ObjectRelationshipLocalServiceUtil>
				objectRelationshipLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						ObjectRelationshipLocalServiceUtil.class)) {

			_assertError(
				"a-required-channel-field-is-not-mapped",
				_service(HttpMethods.POST, "/execute"),
				HttpServletResponse.SC_BAD_REQUEST);
		}

		Mockito.verifyNoInteractions(_messageBus);

		Mockito.when(
			_pimConnectorRegistry.getPIMConnector(_KEY)
		).thenReturn(
			Mockito.mock(PIMConnector.class)
		);
	}

	private void _testServiceWithInvalidCSRFToken() throws Exception {
		Mockito.clearInvocations(_messageBus);

		_authTokenUtilMockedStatic.when(
			() -> AuthTokenUtil.checkCSRFToken(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenThrow(
			new PrincipalException()
		);

		_assertError(
			"you-do-not-have-permission-to-access-the-requested-resource",
			_service(HttpMethods.POST, "/execute"),
			HttpServletResponse.SC_FORBIDDEN);

		Mockito.verifyNoInteractions(_messageBus);

		_authTokenUtilMockedStatic.reset();
	}

	private void _testServiceWithInvalidPath() throws Exception {
		Mockito.clearInvocations(_messageBus);

		_assertError(
			"the-requested-resource-could-not-be-found",
			_service(HttpMethods.POST, "/schedule"),
			HttpServletResponse.SC_NOT_FOUND);

		Mockito.verifyNoInteractions(_messageBus);
	}

	private void _testServiceWithSchedulePath() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _service(
			HttpMethods.GET, "/schedule");

		Assert.assertEquals(
			_CONTROL_PANEL_URL, mockHttpServletResponse.getRedirectedUrl());

		ArgumentCaptor<Map<String, String[]>> argumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			_portal
		).getSiteAdminURL(
			Mockito.eq(_PORTAL_URL), Mockito.eq(_group),
			Mockito.eq(DispatchPortletKeys.DISPATCH), argumentCaptor.capture()
		);

		Map<String, String[]> parameters = argumentCaptor.getValue();

		Assert.assertArrayEquals(
			new String[] {_BACK_URL}, parameters.get("_namespace_backURL"));
		Assert.assertArrayEquals(
			new String[] {String.valueOf(_DISPATCH_TRIGGER_ID)},
			parameters.get("_namespace_dispatchTriggerId"));
		Assert.assertArrayEquals(
			new String[] {"/dispatch/edit_dispatch_trigger"},
			parameters.get("_namespace_mvcRenderCommandName"));
		Assert.assertArrayEquals(
			new String[] {
				DispatchScreenNavigationConstants.CATEGORY_KEY_DISPATCH_TRIGGER
			},
			parameters.get("_namespace_screenNavigationCategoryKey"));
	}

	private void _testServiceWithUnexpectedException() throws Exception {
		Mockito.clearInvocations(_messageBus);

		Mockito.doThrow(
			new PortalException()
		).when(
			_objectEntryService
		).getObjectEntry(
			_OBJECT_ENTRY_ID
		);

		_assertError(
			"an-unexpected-error-occurred",
			_service(HttpMethods.POST, "/execute"),
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		Mockito.verifyNoInteractions(_messageBus);

		Mockito.doReturn(
			_objectEntry
		).when(
			_objectEntryService
		).getObjectEntry(
			_OBJECT_ENTRY_ID
		);
	}

	private static final String _BACK_URL = "/web/cms/connectors";

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _CONTROL_PANEL_URL =
		"http://www.liferay.com/group/control_panel/manage";

	private static final long _DISPATCH_TRIGGER_ID =
		RandomTestUtil.randomLong();

	private static final String _KEY = RandomTestUtil.randomString();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _PORTAL_URL = "http://www.liferay.com";

	private final MockedStatic<AuthTokenUtil> _authTokenUtilMockedStatic =
		Mockito.mockStatic(AuthTokenUtil.class);
	private final DispatchTrigger _dispatchTrigger = Mockito.mock(
		DispatchTrigger.class);
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final Language _language = Mockito.mock(Language.class);
	private final MessageBus _messageBus = Mockito.mock(MessageBus.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryService _objectEntryService = Mockito.mock(
		ObjectEntryService.class);
	private final MockedStatic<PermissionCheckerFactoryUtil>
		_permissionCheckerFactoryUtilMockedStatic = Mockito.mockStatic(
			PermissionCheckerFactoryUtil.class);
	private final MockedStatic<PIMConnectorDispatchTriggerUtil>
		_pimConnectorDispatchTriggerUtilMockedStatic = Mockito.mockStatic(
			PIMConnectorDispatchTriggerUtil.class);
	private final PIMConnectorRegistry _pimConnectorRegistry = Mockito.mock(
		PIMConnectorRegistry.class);
	private final PIMConnectorServlet _pimConnectorServlet =
		new PIMConnectorServlet();
	private final Portal _portal = Mockito.mock(Portal.class);
	private final User _user = Mockito.mock(User.class);

}