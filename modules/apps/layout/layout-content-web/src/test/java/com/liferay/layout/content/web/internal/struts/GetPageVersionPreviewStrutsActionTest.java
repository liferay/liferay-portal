/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.web.internal.struts;

import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Lourdes Fernández Besada
 */
public class GetPageVersionPreviewStrutsActionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_getPageVersionPreviewStrutsAction,
			"_layoutContentVersionLocalService",
			_layoutContentVersionLocalService);
		ReflectionTestUtil.setFieldValue(
			_getPageVersionPreviewStrutsAction,
			"_layoutContentVersionPreviewLocalService",
			_layoutContentVersionPreviewLocalService);
		ReflectionTestUtil.setFieldValue(
			_getPageVersionPreviewStrutsAction, "_layoutLocalService",
			_layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			_getPageVersionPreviewStrutsAction, "_layoutPermission",
			_layoutPermission);

		_setUpMockHttpServletRequest();
		_setUpPermissionCheckerFactoryUtil();
	}

	@After
	public void tearDown() {
		_featureFlagManagerUtilMockedStatic.close();
		_permissionCheckerFactoryUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-103339")
	public void testExecute() throws Exception {
		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		Mockito.verifyNoInteractions(_layoutLocalService);

		_setUpLayoutContentVersion();

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		Mockito.verify(
			_layoutLocalService
		).fetchLayout(
			_layoutContentVersion.getPlid()
		);

		Mockito.when(
			_layoutLocalService.fetchLayout(_layoutContentVersion.getPlid())
		).thenReturn(
			_layout
		);

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		Mockito.verify(
			_layout
		).isDraftLayout();

		Mockito.when(
			_layout.isDraftLayout()
		).thenReturn(
			true
		);

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		Mockito.verify(
			_layout
		).isDraftLayout();

		Mockito.verify(
			_layout
		).isTypeContent();

		Mockito.when(
			_layout.isTypeContent()
		).thenReturn(
			true
		);

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		_featureFlagManagerUtilMockedStatic.verify(
			() -> FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPD-10622"));

		Mockito.verifyNoInteractions(_layoutPermission);

		_permissionCheckerFactoryUtilMockedStatic.verifyNoInteractions();

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPD-10622")
		).thenReturn(
			true
		);

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		_featureFlagManagerUtilMockedStatic.verify(
			() -> FeatureFlagManagerUtil.isEnabled(
				_themeDisplay.getCompanyId(), "LPD-10622"));

		_permissionCheckerFactoryUtilMockedStatic.verify(
			() -> PermissionCheckerFactoryUtil.create(
				_themeDisplay.getRealUser()));

		Mockito.verify(
			_layoutPermission
		).contains(
			_permissionChecker, _layout, ActionKeys.UPDATE
		);

		Mockito.when(
			_layoutPermission.contains(
				_permissionChecker, _layout, ActionKeys.UPDATE)
		).thenReturn(
			true
		);

		_testExecute(StringPool.BLANK, HttpServletResponse.SC_NOT_FOUND);

		Mockito.verify(
			_layoutPermission
		).contains(
			_permissionChecker, _layout, ActionKeys.UPDATE
		);

		Mockito.verify(
			_layoutContentVersionPreviewLocalService
		).fetchLayoutContentVersionPreview(
			_layoutContentVersion.getLayoutContentVersionId(),
			_mockHttpServletRequest.getParameter("languageId"),
			_mockHttpServletRequest.getParameter("segmentsExperienceERC")
		);

		_setUpLayoutContentVersionPreview();

		_testExecute(
			_layoutContentVersionPreview.getHtml(), HttpServletResponse.SC_OK);
	}

	private void _setUpLayoutContentVersion() {
		Mockito.when(
			_layoutContentVersion.getLayoutContentVersionId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_layoutContentVersion.getPlid()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_layoutContentVersionLocalService.
				fetchLayoutContentVersionByExternalReferenceCode(
					_mockHttpServletRequest.getParameter(
						"externalReferenceCode"),
					GetterUtil.getLong(
						_mockHttpServletRequest.getParameter("groupId")))
		).thenReturn(
			_layoutContentVersion
		);
	}

	private void _setUpLayoutContentVersionPreview() throws Exception {
		Mockito.when(
			_layoutContentVersionPreview.getHtml()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					_layoutContentVersion.getLayoutContentVersionId(),
					_mockHttpServletRequest.getParameter("languageId"),
					_mockHttpServletRequest.getParameter(
						"segmentsExperienceERC"))
		).thenReturn(
			_layoutContentVersionPreview
		);
	}

	private void _setUpMockHttpServletRequest() {
		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getRealUser()
		).thenReturn(
			Mockito.mock(User.class)
		);

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_mockHttpServletRequest.setParameter(
			"externalReferenceCode", RandomTestUtil.randomString());
		_mockHttpServletRequest.setParameter(
			"groupId", String.valueOf(RandomTestUtil.randomLong()));
		_mockHttpServletRequest.setParameter(
			"languageId", RandomTestUtil.randomString());
		_mockHttpServletRequest.setParameter(
			"segmentsExperienceERC", RandomTestUtil.randomString());
	}

	private void _setUpPermissionCheckerFactoryUtil() {
		_permissionCheckerFactoryUtilMockedStatic.when(
			() -> PermissionCheckerFactoryUtil.create(
				_themeDisplay.getRealUser())
		).thenReturn(
			_permissionChecker
		);
	}

	private void _testExecute(String content, int status) throws Exception {
		_featureFlagManagerUtilMockedStatic.clearInvocations();
		_permissionCheckerFactoryUtilMockedStatic.clearInvocations();

		Mockito.clearInvocations(_layout, _layoutPermission);

		_mockHttpServletResponse.reset();

		_getPageVersionPreviewStrutsAction.execute(
			_mockHttpServletRequest, _mockHttpServletResponse);

		Assert.assertEquals(
			content, _mockHttpServletResponse.getContentAsString());
		Assert.assertEquals(status, _mockHttpServletResponse.getStatus());
	}

	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private final GetPageVersionPreviewStrutsAction
		_getPageVersionPreviewStrutsAction =
			new GetPageVersionPreviewStrutsAction();
	private final Layout _layout = Mockito.mock(Layout.class);
	private final LayoutContentVersion _layoutContentVersion = Mockito.mock(
		LayoutContentVersion.class);
	private final LayoutContentVersionLocalService
		_layoutContentVersionLocalService = Mockito.mock(
			LayoutContentVersionLocalService.class);
	private final LayoutContentVersionPreview _layoutContentVersionPreview =
		Mockito.mock(LayoutContentVersionPreview.class);
	private final LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService = Mockito.mock(
			LayoutContentVersionPreviewLocalService.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final LayoutPermission _layoutPermission = Mockito.mock(
		LayoutPermission.class);
	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final MockHttpServletResponse _mockHttpServletResponse =
		new MockHttpServletResponse();
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<PermissionCheckerFactoryUtil>
		_permissionCheckerFactoryUtilMockedStatic = Mockito.mockStatic(
			PermissionCheckerFactoryUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}