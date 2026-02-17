/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.struts;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Stefano Motta
 */
public class EditRoomStrutsActionTest {

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
			_groupService.getGroup(Mockito.anyLong())
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
			_portal.getGroupFriendlyURL(
				Mockito.any(), Mockito.any(), Mockito.anyBoolean(),
				Mockito.anyBoolean())
		).thenReturn(
			"/web/" + randomFriendlyURL
		);
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
			_editRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(200, mockHttpServletResponse.getStatus());

		mockHttpServletRequest.setParameter(
			"siteId", String.valueOf(RandomTestUtil.randomLong()));

		mockHttpServletResponse = new MockHttpServletResponse();

		Assert.assertNull(
			_editRoomStrutsAction.execute(
				mockHttpServletRequest, mockHttpServletResponse));

		Assert.assertEquals(302, mockHttpServletResponse.getStatus());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			redirectedURL, redirectedURL,
			StringBundler.concat(
				"/web/", _group.getFriendlyURL(), "?p_l_back_url=",
				_group.getFriendlyURL(), "&p_l_mode=edit"));
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockHttpServletRequest;
	}

	@InjectMocks
	private EditRoomStrutsAction _editRoomStrutsAction;

	private final Group _group = Mockito.mock(Group.class);
	private final GroupService _groupService = Mockito.mock(GroupService.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}