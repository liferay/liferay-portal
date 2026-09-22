/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.IconItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutsAdminDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(Mockito.anyLong())
		).thenReturn(
			_group
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
		_setUpPortalUtil();

		_layoutActionsHelper = Mockito.mock(LayoutActionsHelper.class);

		_layoutsAdminDisplayContext = new LayoutsAdminDisplayContext(
			null, _layoutActionsHelper, null, null, null,
			_liferayPortletRequest, null);
	}

	@Test
	public void testGetEditOrViewLayoutURLEditURL() throws Exception {
		Layout layout = _getContentLayout(true, false);

		Mockito.when(
			_layoutActionsHelper.isShowConfigureAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, Constants.EDIT);
	}

	@Test
	public void testGetEditOrViewLayoutURLViewURL() throws Exception {
		Layout layout = _getContentLayout(true, false);

		Mockito.when(
			_layoutActionsHelper.isShowViewLayoutAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, StringPool.BLANK);
	}

	@Test
	public void testGetEditOrViewLayoutURLViewURLWithLayoutUpdateableFalse()
		throws Exception {

		Layout layout = _getContentLayout(false, false);

		Mockito.when(
			_layoutActionsHelper.isShowConfigureAction(layout)
		).thenReturn(
			true
		);

		Mockito.when(
			_layoutActionsHelper.isShowViewLayoutAction(layout)
		).thenReturn(
			true
		);

		_assertGetEditOrViewLayoutURL(layout, StringPool.BLANK);
	}

	@Test
	public void testGetLayoutScreenNavigationPortletURL() {
		_liferayPortletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		LayoutsAdminDisplayContext layoutsAdminDisplayContext = Mockito.spy(
			new LayoutsAdminDisplayContext(
				null, _layoutActionsHelper, null, null, null,
				_liferayPortletRequest,
				new MockLiferayPortletActionResponse()));

		Mockito.doReturn(
			true
		).when(
			layoutsAdminDisplayContext
		).isPrivateLayout();

		Layout layout = _getContentLayout(true, true);

		String portletURL = String.valueOf(
			layoutsAdminDisplayContext.getLayoutScreenNavigationPortletURL(
				layout.getPlid()));

		Assert.assertTrue(
			portletURL,
			StringUtil.contains(portletURL, "param_privateLayout=true", ";"));
	}

	@Test
	@TestInfo({"LPD-89086", "LPD-105565"})
	public void testGetVerticalNavItemList() throws Exception {
		_testGetVerticalNavItemList();
		_testGetVerticalNavItemListWithDepotGroup();
	}

	private void _assertGetEditOrViewLayoutURL(Layout layout, String layoutMode)
		throws Exception {

		String friendlyURL = RandomTestUtil.randomString();

		if (Validator.isNull(layoutMode)) {
			Mockito.when(
				_portal.getLayoutFullURL(layout, null)
			).thenReturn(
				friendlyURL
			);
		}
		else {
			Mockito.when(
				_portal.getLayoutFullURL(layout.fetchDraftLayout(), null)
			).thenReturn(
				friendlyURL
			);
		}

		String url = _layoutsAdminDisplayContext.getEditOrViewLayoutURL(layout);

		Assert.assertTrue(url, StringUtil.startsWith(url, friendlyURL));
		Assert.assertEquals(
			url, layoutMode,
			HttpComponentsUtil.getParameter(url, "p_l_mode", false));

		String backURL = HttpComponentsUtil.decodeURL(
			HttpComponentsUtil.getParameter(url, "p_l_back_url", false));

		Assert.assertEquals(
			backURL, layout.getPlid(),
			GetterUtil.getLong(
				HttpComponentsUtil.getParameter(
					backURL, LayoutAdminPortletKeys.GROUP_PAGES + "_selPlid",
					false),
				-1));

		Assert.assertEquals(
			"pages",
			HttpComponentsUtil.getParameter(url, "p_l_back_url_title", false));
	}

	private void _assertVerticalNavItem(
		String expectedHref, List<IconItem> expectedIconItems,
		String expectedLabel, long expectedLayoutPageTemplateCollectionId,
		VerticalNavItem verticalNavItem) {

		Assert.assertEquals(expectedHref, verticalNavItem.get("href"));
		Assert.assertEquals(expectedIconItems, verticalNavItem.get("icons"));
		Assert.assertEquals(
			String.valueOf(expectedLayoutPageTemplateCollectionId),
			verticalNavItem.get("id"));
		Assert.assertEquals(expectedLabel, verticalNavItem.get("label"));
	}

	private void _assertVerticalNavItemList(
			int expectedVerticalNavItemsCount, boolean featureFlagEnabled,
			LayoutsAdminDisplayContext layoutsAdminDisplayContext,
			boolean showGlobalTemplates)
		throws Exception {

		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext = Mockito.mock(
				SelectLayoutPageTemplateEntryDisplayContext.class);

		Mockito.when(
			selectLayoutPageTemplateEntryDisplayContext.isShowGlobalTemplates()
		).thenReturn(
			showGlobalTemplates
		);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			_setUpFeatureFlagManagerUtil(
				featureFlagEnabled, featureFlagManagerUtilMockedStatic);

			VerticalNavItemList verticalNavItemList =
				layoutsAdminDisplayContext.getVerticalNavItemList(
					selectLayoutPageTemplateEntryDisplayContext);

			Assert.assertEquals(
				verticalNavItemList.toString(), expectedVerticalNavItemsCount,
				verticalNavItemList.size());
		}
	}

	private Layout _getContentLayout(
		boolean layoutUpdateable, boolean privateLayout) {

		Layout layout = Mockito.mock(Layout.class);

		Layout draftLayout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.fetchDraftLayout()
		).thenReturn(
			draftLayout
		);

		Mockito.when(
			layout.getPlid()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			layout.isPrivateLayout()
		).thenReturn(
			privateLayout
		);

		Mockito.when(
			layout.isLayoutUpdateable()
		).thenReturn(
			layoutUpdateable
		);

		Mockito.when(
			layout.isTypeContent()
		).thenReturn(
			true
		);

		return layout;
	}

	private LayoutPageTemplateCollection _getLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId, String name) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		Mockito.when(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).thenReturn(
			layoutPageTemplateCollectionId
		);

		Mockito.when(
			layoutPageTemplateCollection.getName()
		).thenReturn(
			name
		);

		return layoutPageTemplateCollection;
	}

	private LayoutsAdminDisplayContext _getLayoutsAdminDisplayContext() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			LocaleUtil.US
		).when(
			themeDisplay
		).getLocale();

		Mockito.doReturn(
			_group
		).when(
			themeDisplay
		).getScopeGroup();

		Mockito.doReturn(
			_group.getGroupId()
		).when(
			themeDisplay
		).getScopeGroupId();

		_liferayPortletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		LayoutsAdminDisplayContext layoutsAdminDisplayContext = Mockito.spy(
			new LayoutsAdminDisplayContext(
				null, _layoutActionsHelper, null, null, null,
				_liferayPortletRequest,
				new MockLiferayPortletActionResponse()));

		Mockito.doReturn(
			StringPool.BLANK
		).when(
			layoutsAdminDisplayContext
		).getSelectLayoutPageTemplateEntryURL(
			Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(),
			Mockito.anyBoolean()
		);

		Mockito.doReturn(
			0L
		).when(
			layoutsAdminDisplayContext
		).getSelPlid();

		Mockito.doReturn(
			false
		).when(
			layoutsAdminDisplayContext
		).isPrivateLayout();

		return layoutsAdminDisplayContext;
	}

	private void _setUpDesignLibraryGroup(
			long designLibraryGroupId, String title)
		throws Exception {

		Group designLibraryGroup = Mockito.mock(Group.class);

		String descriptiveName = RandomTestUtil.randomString();

		Mockito.when(
			designLibraryGroup.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			descriptiveName
		);

		Mockito.when(
			designLibraryGroup.getGroupId()
		).thenReturn(
			designLibraryGroupId
		);

		Mockito.when(
			designLibraryGroup.isDepot()
		).thenReturn(
			true
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroup(designLibraryGroupId)
		).thenReturn(
			designLibraryGroup
		);

		Mockito.when(
			_language.format(
				Mockito.any(HttpServletRequest.class),
				Mockito.eq("page-template-set-from-x-design-library"),
				Mockito.eq(descriptiveName))
		).thenReturn(
			title
		);
	}

	private void _setUpFeatureFlagManagerUtil(
		boolean featureFlagEnabled,
		MockedStatic<FeatureFlagManagerUtil>
			featureFlagManagerUtilMockedStatic) {

		featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(
				Mockito.anyLong(), Mockito.eq("LPD-76864"))
		).thenReturn(
			featureFlagEnabled
		);
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		_language = Mockito.mock(Language.class);

		Mockito.when(
			_language.get(
				Mockito.any(HttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(_language);
	}

	private void _setUpLayoutPageTemplateCollectionServiceUtil(
		long groupId, LayoutPageTemplateCollection layoutPageTemplateCollection,
		MockedStatic<LayoutPageTemplateCollectionServiceUtil>
			layoutPageTemplateCollectionServiceUtilMockedStatic) {

		layoutPageTemplateCollectionServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						Mockito.eq(groupId), Mockito.anyInt())
		).thenReturn(
			Collections.singletonList(layoutPageTemplateCollection)
		);
	}

	private void _setUpLayoutPageTemplateEntryServiceUtil(
		int layoutPageTemplateEntriesCount,
		MockedStatic<LayoutPageTemplateEntryServiceUtil>
			layoutPageTemplateEntryServiceUtilMockedStatic) {

		layoutPageTemplateEntryServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt())
		).thenReturn(
			layoutPageTemplateEntriesCount
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		_portal = Mockito.mock(Portal.class);

		_liferayPortletRequest = new MockLiferayPortletActionRequest();

		MockLiferayPortletURL liferayPortletURL = new MockLiferayPortletURL();

		liferayPortletURL.setPortletId(LayoutAdminPortletKeys.GROUP_PAGES);

		Mockito.when(
			_portal.getControlPanelPortletURL(
				_liferayPortletRequest, _group,
				LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
				PortletRequest.RENDER_PHASE)
		).thenReturn(
			liferayPortletURL
		);

		Mockito.when(
			_portal.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			new MockHttpServletRequest()
		);

		Mockito.when(
			_portal.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			(Answer<String[]>)invocationOnMock -> new String[] {
				invocationOnMock.getArgument(0, String.class), StringPool.BLANK
			}
		);

		portalUtil.setPortal(_portal);
	}

	private void _testGetVerticalNavItemList() throws Exception {
		LayoutsAdminDisplayContext layoutsAdminDisplayContext =
			_getLayoutsAdminDisplayContext();

		long layoutPageTemplateCollectionId1 = RandomTestUtil.randomLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_getLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId1, RandomTestUtil.randomString());

		long layoutPageTemplateCollectionId2 = RandomTestUtil.randomLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_getLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId2, RandomTestUtil.randomString());

		try (MockedStatic<LayoutPageTemplateCollectionServiceUtil>
				layoutPageTemplateCollectionServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateCollectionServiceUtil.class);
			MockedStatic<LayoutPageTemplateEntryServiceUtil>
				layoutPageTemplateEntryServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateEntryServiceUtil.class)) {

			layoutPageTemplateCollectionServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateCollectionServiceUtil.
						getLayoutPageTemplateCollections(
							Mockito.anyLong(), Mockito.anyInt())
			).thenReturn(
				Arrays.asList(
					layoutPageTemplateCollection1,
					layoutPageTemplateCollection2)
			);

			_setUpLayoutPageTemplateEntryServiceUtil(
				RandomTestUtil.randomInt(),
				layoutPageTemplateEntryServiceUtilMockedStatic);

			layoutPageTemplateEntryServiceUtilMockedStatic.when(
				() ->
					LayoutPageTemplateEntryServiceUtil.
						getLayoutPageTemplateEntriesCountByType(
							Mockito.anyLong(),
							Mockito.eq(layoutPageTemplateCollectionId1),
							Mockito.eq(
								LayoutPageTemplateEntryTypeConstants.BASIC))
			).thenReturn(
				RandomTestUtil.randomInt()
			);

			_assertVerticalNavItemList(
				2, false, layoutsAdminDisplayContext, false);
			_assertVerticalNavItemList(
				3, false, layoutsAdminDisplayContext, true);
			_assertVerticalNavItemList(
				3, true, layoutsAdminDisplayContext, false);
			_assertVerticalNavItemList(
				4, true, layoutsAdminDisplayContext, true);
		}
	}

	private void _testGetVerticalNavItemListWithDepotGroup() throws Exception {
		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			groupId
		);

		LayoutsAdminDisplayContext layoutsAdminDisplayContext =
			_getLayoutsAdminDisplayContext();

		long designLibraryGroupId = RandomTestUtil.randomLong();
		String title = RandomTestUtil.randomString();

		_setUpDesignLibraryGroup(designLibraryGroupId, title);

		String name = RandomTestUtil.randomString();

		long layoutPageTemplateCollectionId1 = RandomTestUtil.randomLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection1 =
			_getLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId1, name);

		long layoutPageTemplateCollectionId2 = RandomTestUtil.randomLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection2 =
			_getLayoutPageTemplateCollection(
				layoutPageTemplateCollectionId2, name);

		String selectLayoutPageTemplateEntryURL = RandomTestUtil.randomString();

		Mockito.doReturn(
			selectLayoutPageTemplateEntryURL
		).when(
			layoutsAdminDisplayContext
		).getSelectLayoutPageTemplateEntryURL(
			Mockito.eq(layoutPageTemplateCollectionId2), Mockito.anyLong(),
			Mockito.anyBoolean()
		);

		try (MockedStatic<DesignLibraryUtil> designLibraryUtilMockedStatic =
				Mockito.mockStatic(DesignLibraryUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<LayoutPageTemplateCollectionServiceUtil>
				layoutPageTemplateCollectionServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateCollectionServiceUtil.class);
			MockedStatic<LayoutPageTemplateEntryServiceUtil>
				layoutPageTemplateEntryServiceUtilMockedStatic =
					Mockito.mockStatic(
						LayoutPageTemplateEntryServiceUtil.class)) {

			designLibraryUtilMockedStatic.when(
				() -> DesignLibraryUtil.getConnectedDesignLibraryGroupIds(
					Mockito.anyLong(), Mockito.eq(groupId))
			).thenReturn(
				new long[] {designLibraryGroupId}
			);

			_setUpFeatureFlagManagerUtil(
				true, featureFlagManagerUtilMockedStatic);

			_setUpLayoutPageTemplateCollectionServiceUtil(
				groupId, layoutPageTemplateCollection1,
				layoutPageTemplateCollectionServiceUtilMockedStatic);
			_setUpLayoutPageTemplateCollectionServiceUtil(
				designLibraryGroupId, layoutPageTemplateCollection2,
				layoutPageTemplateCollectionServiceUtilMockedStatic);

			_setUpLayoutPageTemplateEntryServiceUtil(
				1, layoutPageTemplateEntryServiceUtilMockedStatic);

			VerticalNavItemList verticalNavItemList =
				layoutsAdminDisplayContext.getVerticalNavItemList(
					Mockito.mock(
						SelectLayoutPageTemplateEntryDisplayContext.class));

			Assert.assertEquals(
				verticalNavItemList.toString(), 3, verticalNavItemList.size());

			_assertVerticalNavItem(
				StringPool.BLANK, null, name, layoutPageTemplateCollectionId1,
				verticalNavItemList.get(1));
			_assertVerticalNavItem(
				selectLayoutPageTemplateEntryURL,
				Collections.singletonList(IconItem.of("books", title)), name,
				layoutPageTemplateCollectionId2, verticalNavItemList.get(2));
		}
	}

	private static final Group _group = Mockito.mock(Group.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

	private Language _language;
	private LayoutActionsHelper _layoutActionsHelper;
	private LayoutsAdminDisplayContext _layoutsAdminDisplayContext;
	private LiferayPortletRequest _liferayPortletRequest;
	private Portal _portal;

}