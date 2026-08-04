/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.taglib;

import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class ChangeTrackingIndicatorDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpChangeTrackingIndicatorDynamicInclude();
		_setUpHttpServletRequest();
		_setUpHttpServletResponse();
		_setUpPortletPermissionUtil();
	}

	@After
	public void tearDown() {
		_portletPermissionUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-100937")
	public void testIncludeWithLayoutHistoryMode() throws Exception {
		_testInclude(1);

		Mockito.when(
			_httpServletRequest.getParameter("p_l_mode")
		).thenReturn(
			Constants.HISTORY
		);

		_testInclude(1);

		Mockito.when(
			_layout.isTypeContent()
		).thenReturn(
			true
		);

		_testInclude(1);

		Mockito.when(
			_layout.isDraftLayout()
		).thenReturn(
			true
		);

		_testInclude(0);
	}

	private void _setUpChangeTrackingIndicatorDynamicInclude() {
		_setUpCTSettingsConfigurationHelper();

		ReflectionTestUtil.setFieldValue(
			_changeTrackingIndicatorDynamicInclude,
			"_ctSettingsConfigurationHelper", _ctSettingsConfigurationHelper);
	}

	private void _setUpCTSettingsConfigurationHelper() {
		Mockito.when(
			_ctSettingsConfigurationHelper.isEnabled(Mockito.anyLong())
		).thenReturn(
			true
		);
	}

	private void _setUpHttpServletRequest() {
		_setUpThemeDisplay();

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);
	}

	private void _setUpHttpServletResponse() throws Exception {
		Mockito.when(
			_httpServletResponse.getWriter()
		).thenThrow(
			new IOException()
		);
	}

	private void _setUpPortletPermissionUtil() {
		_portletPermissionUtilMockedStatic.when(
			() -> PortletPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(),
				CTPortletKeys.PUBLICATIONS, ActionKeys.VIEW)
		).thenReturn(
			true
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getLayout()
		).thenReturn(
			_layout
		);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			permissionChecker
		);

		User user = Mockito.mock(User.class);

		Mockito.when(
			_themeDisplay.getUser()
		).thenReturn(
			user
		);
	}

	private void _testInclude(int expectedInvocations) throws Exception {
		Mockito.clearInvocations(_httpServletResponse);

		try {
			_changeTrackingIndicatorDynamicInclude.include(
				_httpServletRequest, _httpServletResponse, null);
		}
		catch (IOException ioException) {
			Assert.assertNotEquals(0, expectedInvocations);
		}

		Mockito.verify(
			_httpServletResponse, Mockito.times(expectedInvocations)
		).getWriter();
	}

	private final ChangeTrackingIndicatorDynamicInclude
		_changeTrackingIndicatorDynamicInclude =
			new ChangeTrackingIndicatorDynamicInclude();
	private final CTSettingsConfigurationHelper _ctSettingsConfigurationHelper =
		Mockito.mock(CTSettingsConfigurationHelper.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);
	private final Layout _layout = Mockito.mock(Layout.class);
	private final MockedStatic<PortletPermissionUtil>
		_portletPermissionUtilMockedStatic = Mockito.mockStatic(
			PortletPermissionUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}