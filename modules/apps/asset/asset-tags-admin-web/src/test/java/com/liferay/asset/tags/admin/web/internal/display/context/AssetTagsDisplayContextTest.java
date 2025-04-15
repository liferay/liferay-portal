/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.admin.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class AssetTagsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_portletURLUtilMockedStatic.close();
		_stagingGroupHelperUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
		_setUpPortletURLUtil();
	}

	@Test
	public void testGetAssetTagActionDropdownItems() throws Exception {
		_testGetAssetTagActionDropdownItemsInLiveGroup();
		_testGetAssetTagActionDropdownItemsInStagingGroup();
	}

	private void _assertDropdownItem(
		DropdownItem dropdownItem, String icon, String label) {

		DropdownItemList dropdownItemList = (DropdownItemList)dropdownItem.get(
			"items");

		DropdownItem childDropdownItem = dropdownItemList.get(0);

		Assert.assertEquals(icon, childDropdownItem.get("icon"));
		Assert.assertEquals(label, childDropdownItem.get("label"));
	}

	private void _assertEmptyDropdownItemList(DropdownItem dropdownItem) {
		DropdownItemList dropdownItemList = (DropdownItemList)dropdownItem.get(
			"items");

		Assert.assertTrue(dropdownItemList.isEmpty());
	}

	private AssetTag _getAssetTag() {
		AssetTag assetTag = Mockito.mock(AssetTag.class);

		Mockito.when(
			assetTag.getTagId()
		).thenReturn(
			1L
		);

		return assetTag;
	}

	private AssetTagsDisplayContext _getAssetTagsDisplayContext(
		boolean liveGroup) {

		_setUpStagingGroupHelperUtil(liveGroup);

		Group group = Mockito.mock(Group.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			group
		);

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return new AssetTagsDisplayContext(
			httpServletRequest, Mockito.mock(RenderRequest.class),
			new MockLiferayPortletRenderResponse());
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(_language);
	}

	private void _setUpPortletURLUtil() throws Exception {
		_portletURLUtilMockedStatic = Mockito.mockStatic(PortletURLUtil.class);

		Mockito.when(
			PortletURLUtil.clone(
				Mockito.any(LiferayPortletURL.class), Mockito.anyString(),
				Mockito.any(LiferayPortletResponse.class))
		).thenReturn(
			new MockLiferayPortletURL()
		);
	}

	private void _setUpStagingGroupHelperUtil(boolean liveGroup) {
		StagingGroupHelper stagingGroupHelper = Mockito.mock(
			StagingGroupHelper.class);

		Mockito.when(
			stagingGroupHelper.isLocalLiveGroup(Mockito.any(Group.class))
		).thenReturn(
			liveGroup
		);

		Mockito.when(
			stagingGroupHelper.isRemoteStagingGroup(Mockito.any(Group.class))
		).thenReturn(
			liveGroup
		);

		_stagingGroupHelperUtilMockedStatic.when(
			StagingGroupHelperUtil::getStagingGroupHelper
		).thenReturn(
			stagingGroupHelper
		);
	}

	private void _testGetAssetTagActionDropdownItemsInLiveGroup() {
		AssetTagsDisplayContext assetTagsDisplayContext =
			_getAssetTagsDisplayContext(true);

		List<DropdownItem> assetTagActionDropdownItems =
			assetTagsDisplayContext.getAssetTagActionDropdownItems(
				_getAssetTag());

		Assert.assertEquals(
			assetTagActionDropdownItems.toString(), 3,
			assetTagActionDropdownItems.size());

		_assertEmptyDropdownItemList(assetTagActionDropdownItems.get(0));
		_assertEmptyDropdownItemList(assetTagActionDropdownItems.get(1));

		_assertDropdownItem(
			assetTagActionDropdownItems.get(2), "trash", "delete");
	}

	private void _testGetAssetTagActionDropdownItemsInStagingGroup() {
		AssetTagsDisplayContext assetTagsDisplayContext =
			_getAssetTagsDisplayContext(false);

		List<DropdownItem> assetTagActionDropdownItems =
			assetTagsDisplayContext.getAssetTagActionDropdownItems(
				_getAssetTag());

		Assert.assertEquals(
			assetTagActionDropdownItems.toString(), 3,
			assetTagActionDropdownItems.size());
		_assertDropdownItem(
			assetTagActionDropdownItems.get(0), "pencil", "edit");
		_assertDropdownItem(
			assetTagActionDropdownItems.get(1), "merge", "merge");
		_assertDropdownItem(
			assetTagActionDropdownItems.get(2), "trash", "delete");
	}

	private static MockedStatic<PortletURLUtil> _portletURLUtilMockedStatic;
	private static final MockedStatic<StagingGroupHelperUtil>
		_stagingGroupHelperUtilMockedStatic = Mockito.mockStatic(
			StagingGroupHelperUtil.class);

	private final Language _language = Mockito.mock(Language.class);

}