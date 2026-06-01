/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.helper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Eudaldo Alonso
 */
public class LayoutActionsHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpLayoutLocalServiceUtil();

		_layoutPermissionUtilMockedStatic.reset();
	}

	@After
	public void tearDown() {
		_featureFlagManagerUtilMockedStatic.close();
		_layoutLocalServiceUtilMockedStatic.close();
		_layoutPermissionUtilMockedStatic.close();
	}

	@Test
	public void testIsShowDeleteActionForLastPublicPageOnDefaultSite()
		throws PortalException {

		Layout layout = _getLayout(_getGroup());

		_setUpLayoutPermissionUtil(layout, ActionKeys.DELETE);

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(layoutActionsHelper.isShowDeleteAction(layout));
	}

	@Test
	@TestInfo("LPS-140136")
	public void testIsShowDiscardDraftAction() throws PortalException {
		Layout layout = _getLayout(_getGroup());

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(
			layoutActionsHelper.isShowDiscardDraftActions(layout));

		_setUpLayoutPermissionUtil(layout, ActionKeys.UPDATE);

		Assert.assertFalse(
			layoutActionsHelper.isShowDiscardDraftActions(layout));

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		Layout draftLayout = _getLayout(layout.getGroup());

		Mockito.when(
			layout.fetchDraftLayout()
		).thenReturn(
			draftLayout
		);

		Assert.assertFalse(
			layoutActionsHelper.isShowDiscardDraftActions(layout));

		Mockito.when(
			draftLayout.isDraft()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			layoutActionsHelper.isShowDiscardDraftActions(layout));

		_setUpLayoutPermissionUtil(layout);

		Assert.assertFalse(
			layoutActionsHelper.isShowDiscardDraftActions(layout));
	}

	@Test
	@TestInfo("LPS-140136")
	public void testIsShowPreviewDraftAction() throws PortalException {
		Layout layout = _getLayout(_getGroup());

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(
			layoutActionsHelper.isShowPreviewDraftActions(layout));

		_setUpPreviewDraftPermission(layout);

		Assert.assertFalse(
			layoutActionsHelper.isShowPreviewDraftActions(layout));

		Layout draftLayout = _getLayout(layout.getGroup());

		Mockito.when(
			layout.fetchDraftLayout()
		).thenReturn(
			draftLayout
		);

		Assert.assertTrue(
			layoutActionsHelper.isShowPreviewDraftActions(layout));

		Mockito.when(
			layout.isPublished()
		).thenReturn(
			true
		);

		Assert.assertFalse(
			layoutActionsHelper.isShowPreviewDraftActions(layout));

		Mockito.when(
			draftLayout.isDraft()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			layoutActionsHelper.isShowPreviewDraftActions(layout));

		_layoutPermissionUtilMockedStatic.reset();

		Assert.assertFalse(
			layoutActionsHelper.isShowPreviewDraftActions(layout));
	}

	@Test
	@TestInfo("LPD-90027")
	public void testIsShowViewHistoryAction() throws PortalException {
		_testIsShowViewHistoryActionWithFeatureFlagDisabled();
		_testIsShowViewHistoryActionWithLayoutTypeNotContent();
		_testIsShowViewHistoryActionWithoutUpdatePermission();
		_testIsShowViewHistoryActionWithUpdatePermission();
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isGuest()
		).thenReturn(
			true
		);

		return group;
	}

	private Layout _getLayout(Group group) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			layout.isPrivateLayout()
		).thenReturn(
			false
		);

		Mockito.when(
			layout.isRootLayout()
		).thenReturn(
			true
		);

		return layout;
	}

	private void _setUpFeatureFlag(boolean enabled) {
		_featureFlagManagerUtilMockedStatic.reset();

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.eq(_themeDisplay.getCompanyId()),
				Mockito.eq("LPD-10622"))
		).thenReturn(
			enabled
		);
	}

	private void _setUpLayoutLocalServiceUtil() {
		_layoutLocalServiceUtilMockedStatic.when(
			() -> LayoutLocalServiceUtil.getLayoutsCount(
				Mockito.any(), Mockito.anyBoolean(), Mockito.anyLong())
		).thenReturn(
			1
		);
	}

	private void _setUpLayoutPermissionUtil(
		Layout layout, String... actionIds) {

		_layoutPermissionUtilMockedStatic.when(
			() -> LayoutPermissionUtil.contains(
				Mockito.eq(_themeDisplay.getPermissionChecker()),
				Mockito.eq(layout), Mockito.anyString())
		).thenAnswer(
			(Answer<Boolean>)invocationOnMock -> ArrayUtil.contains(
				actionIds, invocationOnMock.getArgument(2, String.class))
		);
	}

	private void _setUpPreviewDraftPermission(Layout layout) {
		_layoutPermissionUtilMockedStatic.when(
			() -> LayoutPermissionUtil.containsLayoutPreviewDraftPermission(
				_themeDisplay.getPermissionChecker(), layout)
		).thenReturn(
			true
		);
	}

	private void _testIsShowViewHistoryActionWithFeatureFlagDisabled()
		throws PortalException {

		_setUpFeatureFlag(false);

		Layout layout = _getLayout(_getGroup());

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(layoutActionsHelper.isShowViewHistoryAction(layout));
	}

	private void _testIsShowViewHistoryActionWithLayoutTypeNotContent()
		throws PortalException {

		_setUpFeatureFlag(true);

		Layout layout = _getLayout(_getGroup());

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			false
		);

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(layoutActionsHelper.isShowViewHistoryAction(layout));
	}

	private void _testIsShowViewHistoryActionWithoutUpdatePermission()
		throws PortalException {

		_setUpFeatureFlag(true);

		Layout layout = _getLayout(_getGroup());

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertFalse(layoutActionsHelper.isShowViewHistoryAction(layout));
	}

	private void _testIsShowViewHistoryActionWithUpdatePermission()
		throws PortalException {

		_setUpFeatureFlag(true);

		Layout layout = _getLayout(_getGroup());

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		_setUpLayoutPermissionUtil(layout, ActionKeys.UPDATE);

		LayoutActionsHelper layoutActionsHelper = new LayoutActionsHelper(
			null, _themeDisplay, null);

		Assert.assertTrue(layoutActionsHelper.isShowViewHistoryAction(layout));
	}

	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);
	private final MockedStatic<LayoutLocalServiceUtil>
		_layoutLocalServiceUtilMockedStatic = Mockito.mockStatic(
			LayoutLocalServiceUtil.class);
	private final MockedStatic<LayoutPermissionUtil>
		_layoutPermissionUtilMockedStatic = Mockito.mockStatic(
			LayoutPermissionUtil.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}