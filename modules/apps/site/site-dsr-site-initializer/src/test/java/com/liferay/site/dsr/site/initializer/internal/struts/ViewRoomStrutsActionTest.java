/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.struts;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Stefano Motta
 */
public class ViewRoomStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws PortalException {
		MockitoAnnotations.initMocks(this);

		String randomClassName = RandomTestUtil.randomString();

		Mockito.when(
			_group.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		).thenReturn(
			randomClassName
		);

		String randomFriendlyURL = RandomTestUtil.randomString();

		Mockito.when(
			_group.getFriendlyURL()
		).thenReturn(
			randomFriendlyURL
		);

		Mockito.when(
			_groupLocalService.getGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			randomClassName
		);

		Mockito.when(
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(Mockito.anyLong())
		).thenReturn(
			_objectEntry
		);

		Mockito.when(
			_objectEntry.getValues()
		).thenReturn(
			Collections.emptyMap()
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		_permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			_permissionChecker
		);

		_groupPermissionUtilMockedStatic.when(
			() -> GroupPermissionUtil.contains(
				Mockito.any(), Mockito.any(), Mockito.anyString())
		).thenReturn(
			true
		);

		Mockito.when(
			_portal.escapeRedirect(Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(0)
		);

		Mockito.when(
			_portal.getGroupFriendlyURL(
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.anyBoolean())
		).thenReturn(
			"/web/" + randomFriendlyURL
		);
	}

	@After
	public void tearDown() {
		_groupPermissionUtilMockedStatic.close();
		_permissionThreadLocalMockedStatic.close();
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testExecute() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setParameter(
			"siteId", String.valueOf(RandomTestUtil.randomLong()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setParameter(
			"siteId", String.valueOf(RandomTestUtil.randomLong()));

		mockHttpServletResponse = new MockHttpServletResponse();

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(302, mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			redirectedURL, redirectedURL, "/web/" + _group.getFriendlyURL());

		mockHttpServletRequest.setParameter("mode", "edit");
		mockHttpServletRequest.setParameter(
			"siteId", String.valueOf(RandomTestUtil.randomLong()));

		mockHttpServletResponse = new MockHttpServletResponse();

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(302, mockHttpServletResponse.getStatus());

		redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			redirectedURL, redirectedURL,
			StringBundler.concat(
				"/web/", _group.getFriendlyURL(), "?p_l_back_url=/web/",
				_group.getFriendlyURL(), "&p_l_mode=edit"));

		String referer = RandomTestUtil.randomString();

		mockHttpServletRequest.addHeader(HttpHeaders.REFERER, referer);

		mockHttpServletResponse = new MockHttpServletResponse();

		_groupPermissionUtilMockedStatic.when(
			() -> GroupPermissionUtil.contains(
				Mockito.any(), Mockito.any(), Mockito.anyString())
		).thenReturn(
			false
		);

		_portalUtilMockedStatic.when(
			() -> PortalUtil.getOriginalServletRequest(Mockito.any())
		).thenReturn(
			mockHttpServletRequest
		);

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(redirectedURL, redirectedURL, referer);

		Assert.assertTrue(
			SessionErrors.contains(
				mockHttpServletRequest,
				PrincipalException.MustHavePermission.class.getName()));

		_groupPermissionUtilMockedStatic.when(
			() -> GroupPermissionUtil.contains(
				Mockito.any(), Mockito.any(), Mockito.anyString())
		).thenReturn(
			true
		);

		String className = RandomTestUtil.randomString();

		Mockito.when(
			_group.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_objectEntry.getValues()
		).thenReturn(
			Collections.<String, Serializable>singletonMap(
				"roomStatus", WorkflowConstants.STATUS_INACTIVE)
		);

		Mockito.when(
			_permissionChecker.isCompanyAdmin()
		).thenReturn(
			false
		);

		Mockito.when(
			_permissionChecker.isGroupOwner(Mockito.anyLong())
		).thenReturn(
			false
		);

		mockHttpServletResponse = new MockHttpServletResponse();

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(
			referer, mockHttpServletResponse.getRedirectedUrl());

		Assert.assertTrue(
			SessionErrors.contains(
				mockHttpServletRequest,
				PrincipalException.MustHavePermission.class.getName()));

		Mockito.when(
			_permissionChecker.isCompanyAdmin()
		).thenReturn(
			true
		);

		mockHttpServletRequest.removeParameter("mode");

		mockHttpServletResponse = new MockHttpServletResponse();

		Assert.assertNull(
			_viewRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(
			"/web/" + _group.getFriendlyURL(),
			mockHttpServletResponse.getRedirectedUrl());
		Assert.assertEquals(302, mockHttpServletResponse.getStatus());
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockHttpServletRequest;
	}

	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final MockedStatic<GroupPermissionUtil>
		_groupPermissionUtilMockedStatic = Mockito.mockStatic(
			GroupPermissionUtil.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PermissionThreadLocal>
		_permissionThreadLocalMockedStatic = Mockito.mockStatic(
			PermissionThreadLocal.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

	@InjectMocks
	private ViewRoomStrutsAction _viewRoomStrutsAction;

}