/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alberto Sousa
 */
public class ObjectEntryPortletURLUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_objectDefinition.getPortletId()
		).thenReturn(
			_OBJECT_DEFINITION_PORTLET_ID
		);
	}

	@Test
	@TestInfo("LPD-102111")
	public void testGetRelatedObjectEntryPortletURL() {
		_testGetRelatedObjectEntryPortletURL(
			_controlPanelPortletURL, _PORTLET_ID, true);
		_testGetRelatedObjectEntryPortletURL(_portletURL, _PORTLET_ID, false);
		_testGetRelatedObjectEntryPortletURL(
			_objectDefinitionControlPanelPortletURL, null, false);
	}

	private HttpServletRequest _getHttpServletRequest(
		boolean typeControlPanel) {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.isTypeControlPanel()
		).thenReturn(
			typeControlPanel
		);

		Mockito.when(
			themeDisplay.getLayout()
		).thenReturn(
			layout
		);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private void _testGetRelatedObjectEntryPortletURL(
		PortletURL expectedPortletURL, String portletId,
		boolean typeControlPanel) {

		HttpServletRequest httpServletRequest = _getHttpServletRequest(
			typeControlPanel);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			Mockito.mock(RequestBackedPortletURLFactory.class);

		Mockito.when(
			requestBackedPortletURLFactory.createPortletURL(
				portletId, PortletRequest.RENDER_PHASE)
		).thenReturn(
			_portletURL
		);

		try (MockedStatic<PortalUtil> portalUtilMockedStatic =
				Mockito.mockStatic(PortalUtil.class);
			MockedStatic<RequestBackedPortletURLFactoryUtil>
				requestBackedPortletURLFactoryUtilMockedStatic =
					Mockito.mockStatic(
						RequestBackedPortletURLFactoryUtil.class)) {

			portalUtilMockedStatic.when(
				() -> PortalUtil.getControlPanelPortletURL(
					httpServletRequest, _group, _PORTLET_ID, 0, 0,
					PortletRequest.RENDER_PHASE)
			).thenReturn(
				_controlPanelPortletURL
			);

			portalUtilMockedStatic.when(
				() -> PortalUtil.getControlPanelPortletURL(
					httpServletRequest, _group, _OBJECT_DEFINITION_PORTLET_ID,
					0, 0, PortletRequest.RENDER_PHASE)
			).thenReturn(
				_objectDefinitionControlPanelPortletURL
			);

			requestBackedPortletURLFactoryUtilMockedStatic.when(
				() -> RequestBackedPortletURLFactoryUtil.create(
					httpServletRequest)
			).thenReturn(
				requestBackedPortletURLFactory
			);

			Assert.assertEquals(
				expectedPortletURL,
				ObjectEntryPortletURLUtil.getRelatedObjectEntryPortletURL(
					_group, httpServletRequest, PortletRequest.RENDER_PHASE,
					_objectDefinition, portletId));
		}
	}

	private static final String _OBJECT_DEFINITION_PORTLET_ID =
		RandomTestUtil.randomString();

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private final PortletURL _controlPanelPortletURL = Mockito.mock(
		PortletURL.class);
	private final Group _group = Mockito.mock(Group.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final PortletURL _objectDefinitionControlPanelPortletURL =
		Mockito.mock(PortletURL.class);
	private final PortletURL _portletURL = Mockito.mock(PortletURL.class);

}