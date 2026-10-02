/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Georgel Pop
 */
public class LayoutPageTemplateDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpThemeDisplay();
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_layoutPageTemplateCollectionServiceUtilMockedStatic.close();
		_layoutPageTemplatePortletUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-107947")
	public void testGetLayoutPageTemplateCollectionId() {
		_testGetLayoutPageTemplateCollectionIdFromDefaultCollection();
		_testGetLayoutPageTemplateCollectionIdFromRequest();
		_testGetLayoutPageTemplateCollectionIdFromRequestAttribute();
		_testGetLayoutPageTemplateCollectionIdWithoutCollections();
	}

	@Test
	@TestInfo("LPD-104842")
	public void testIsHideCollectionsPanel() {
		_testIsHideCollectionsPanel(false);
		_testIsHideCollectionsPanel(true);
	}

	private LayoutPageTemplateCollection _createLayoutPageTemplateCollection(
		long layoutPageTemplateCollectionId) {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			Mockito.mock(LayoutPageTemplateCollection.class);

		Mockito.when(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionId()
		).thenReturn(
			layoutPageTemplateCollectionId
		);

		return layoutPageTemplateCollection;
	}

	private void _setUpDesignLibraryScope(boolean designLibraryScope) {
		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			designLibraryScope
		);
	}

	private void _setUpLayoutPageTemplateCollections(
		LayoutPageTemplateCollection... layoutPageTemplateCollections) {

		_layoutPageTemplateCollectionServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						_GROUP_ID, LayoutPageTemplateEntryTypeConstants.BASIC)
		).thenReturn(
			ListUtil.fromArray(layoutPageTemplateCollections)
		);
	}

	private void _setUpRequest(
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		Object layoutPageTemplateCollectionId) {

		Mockito.when(
			_httpServletRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.
					LAYOUT_PAGE_TEMPLATE_COLLECTION_ID)
		).thenReturn(
			layoutPageTemplateCollectionId
		);

		_layoutPageTemplatePortletUtilMockedStatic.when(
			() ->
				LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
					_httpServletRequest, _GROUP_ID)
		).thenReturn(
			layoutPageTemplateCollection
		);
	}

	private void _setUpThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		Mockito.when(
			themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);

		Mockito.when(
			themeDisplay.getScopeGroupId()
		).thenReturn(
			_GROUP_ID
		);
	}

	private void _testGetLayoutPageTemplateCollectionIdFromDefaultCollection() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		_setUpLayoutPageTemplateCollections(
			_createLayoutPageTemplateCollection(layoutPageTemplateCollectionId),
			_createLayoutPageTemplateCollection(RandomTestUtil.randomLong()));

		_setUpRequest(null, null);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId());
	}

	private void _testGetLayoutPageTemplateCollectionIdFromRequest() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		_setUpLayoutPageTemplateCollections(
			_createLayoutPageTemplateCollection(RandomTestUtil.randomLong()));
		_setUpRequest(
			_createLayoutPageTemplateCollection(layoutPageTemplateCollectionId),
			null);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId());
	}

	private void _testGetLayoutPageTemplateCollectionIdFromRequestAttribute() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		_setUpLayoutPageTemplateCollections(
			_createLayoutPageTemplateCollection(RandomTestUtil.randomLong()));
		_setUpRequest(
			_createLayoutPageTemplateCollection(RandomTestUtil.randomLong()),
			layoutPageTemplateCollectionId);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId());
	}

	private void _testGetLayoutPageTemplateCollectionIdWithoutCollections() {
		_setUpLayoutPageTemplateCollections();
		_setUpRequest(null, null);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			0,
			layoutPageTemplateDisplayContext.
				getLayoutPageTemplateCollectionId());
	}

	private void _testIsHideCollectionsPanel(boolean designLibraryScope) {
		_setUpDesignLibraryScope(designLibraryScope);

		LayoutPageTemplateDisplayContext layoutPageTemplateDisplayContext =
			new LayoutPageTemplateDisplayContext(
				_httpServletRequest, _renderRequest, _renderResponse);

		Assert.assertEquals(
			designLibraryScope,
			layoutPageTemplateDisplayContext.isHideCollectionsPanel());
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LayoutPageTemplateCollectionServiceUtil>
		_layoutPageTemplateCollectionServiceUtilMockedStatic =
			Mockito.mockStatic(LayoutPageTemplateCollectionServiceUtil.class);
	private final MockedStatic<LayoutPageTemplatePortletUtil>
		_layoutPageTemplatePortletUtilMockedStatic = Mockito.mockStatic(
			LayoutPageTemplatePortletUtil.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}