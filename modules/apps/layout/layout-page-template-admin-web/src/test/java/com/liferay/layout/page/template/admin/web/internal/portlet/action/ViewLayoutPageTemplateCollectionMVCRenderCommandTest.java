/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.admin.web.internal.constants.LayoutPageTemplateAdminWebKeys;
import com.liferay.layout.page.template.admin.web.internal.util.LayoutPageTemplatePortletUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderResponse;

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
public class ViewLayoutPageTemplateCollectionMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_viewLayoutPageTemplateCollectionMVCRenderCommand, "portal",
			_portal);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_layoutPageTemplatePortletUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-104842")
	public void testRender() {
		_testRender();
		_testRenderWithoutLayoutPageTemplateCollection();
	}

	private LayoutPageTemplateCollection _mockLayoutPageTemplateCollection(
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

	private MockLiferayPortletRenderRequest _mockLiferayPortletRenderRequest(
		boolean designLibraryScope,
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			designLibraryScope
		);

		_layoutPageTemplatePortletUtilMockedStatic.when(
			() ->
				LayoutPageTemplatePortletUtil.fetchLayoutPageTemplateCollection(
					Mockito.any(), Mockito.anyLong())
		).thenReturn(
			layoutPageTemplateCollection
		);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		Mockito.when(
			_portal.getHttpServletRequest(mockLiferayPortletRenderRequest)
		).thenReturn(
			mockLiferayPortletRenderRequest.getHttpServletRequest()
		);

		return mockLiferayPortletRenderRequest;
	}

	private void _testRender() {
		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_mockLiferayPortletRenderRequest(
				true,
				_mockLayoutPageTemplateCollection(
					layoutPageTemplateCollectionId));

		Assert.assertEquals(
			"/view.jsp",
			_viewLayoutPageTemplateCollectionMVCRenderCommand.render(
				mockLiferayPortletRenderRequest,
				Mockito.mock(RenderResponse.class)));
		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			mockLiferayPortletRenderRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.
					LAYOUT_PAGE_TEMPLATE_COLLECTION_ID));
		Assert.assertFalse(
			(Boolean)mockLiferayPortletRenderRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.SHOW_COLLECTIONS_PANEL));
	}

	private void _testRenderWithoutLayoutPageTemplateCollection() {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_mockLiferayPortletRenderRequest(false, null);

		Assert.assertEquals(
			"/view.jsp",
			_viewLayoutPageTemplateCollectionMVCRenderCommand.render(
				mockLiferayPortletRenderRequest,
				Mockito.mock(RenderResponse.class)));
		Assert.assertNull(
			mockLiferayPortletRenderRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.
					LAYOUT_PAGE_TEMPLATE_COLLECTION_ID));
		Assert.assertTrue(
			(Boolean)mockLiferayPortletRenderRequest.getAttribute(
				LayoutPageTemplateAdminWebKeys.SHOW_COLLECTIONS_PANEL));
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final MockedStatic<LayoutPageTemplatePortletUtil>
		_layoutPageTemplatePortletUtilMockedStatic = Mockito.mockStatic(
			LayoutPageTemplatePortletUtil.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final ViewLayoutPageTemplateCollectionMVCRenderCommand
		_viewLayoutPageTemplateCollectionMVCRenderCommand =
			new ViewLayoutPageTemplateCollectionMVCRenderCommand();

}