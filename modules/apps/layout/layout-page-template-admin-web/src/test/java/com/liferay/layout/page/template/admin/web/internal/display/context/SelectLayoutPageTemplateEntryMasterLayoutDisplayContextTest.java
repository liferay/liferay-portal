/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.display.context;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

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
public class SelectLayoutPageTemplateEntryMasterLayoutDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	@After
	public void tearDown() {
		_designLibraryUtilMockedStatic.close();
		_layoutPageTemplateCollectionServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-109442")
	public void testGetAdditionalProps() {
		SelectLayoutPageTemplateEntryMasterLayoutDisplayContext
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext =
				new SelectLayoutPageTemplateEntryMasterLayoutDisplayContext(
					_httpServletRequest, _renderResponse);

		_testGetAdditionalProps(
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext);
		_testGetAdditionalPropsWithDesignLibrary(
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext);
		_testGetAdditionalPropsWithLayoutPageTemplateCollectionId(
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext);
	}

	private void _setUpLayoutPageTemplateCollectionServiceUtil(
		long layoutPageTemplateCollectionId,
		String layoutPageTemplateCollectionName) {

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_themeDisplay.getScopeGroupId()
		).thenReturn(
			groupId
		);

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
			layoutPageTemplateCollectionName
		);

		_layoutPageTemplateCollectionServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						groupId,
						LayoutPageTemplateCollectionTypeConstants.BASIC,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)
		).thenReturn(
			Collections.singletonList(layoutPageTemplateCollection)
		);
	}

	private void _setUpRenderResponse(String addPageTemplateSetURL) {
		Mockito.when(
			_liferayPortletURL.toString()
		).thenReturn(
			addPageTemplateSetURL
		);

		Mockito.when(
			_renderResponse.createResourceURL()
		).thenReturn(
			_liferayPortletURL
		);
	}

	private void _testGetAdditionalProps(
		SelectLayoutPageTemplateEntryMasterLayoutDisplayContext
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext) {

		Assert.assertEquals(
			Collections.emptyMap(),
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext.
				getAdditionalProps());
	}

	private void _testGetAdditionalPropsWithDesignLibrary(
		SelectLayoutPageTemplateEntryMasterLayoutDisplayContext
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext) {

		_designLibraryUtilMockedStatic.when(
			() -> DesignLibraryUtil.isDesignLibraryScope(_group)
		).thenReturn(
			true
		);

		long layoutPageTemplateCollectionId = RandomTestUtil.randomLong();
		String layoutPageTemplateCollectionName = RandomTestUtil.randomString();

		_setUpLayoutPageTemplateCollectionServiceUtil(
			layoutPageTemplateCollectionId, layoutPageTemplateCollectionName);

		String addPageTemplateSetURL = RandomTestUtil.randomString();

		_setUpRenderResponse(addPageTemplateSetURL);

		Map<String, Object> additionalProps =
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext.
				getAdditionalProps();

		Assert.assertEquals(
			addPageTemplateSetURL,
			additionalProps.get("addPageTemplateSetURL"));

		Mockito.verify(
			_liferayPortletURL
		).setCopyCurrentRenderParameters(
			false
		);

		Mockito.verify(
			_liferayPortletURL
		).setResourceID(
			"/layout_page_template_admin/add_layout_page_template_collection"
		);

		JSONArray pageTemplateSetsJSONArray = (JSONArray)additionalProps.get(
			"pageTemplateSets");

		Assert.assertEquals(
			pageTemplateSetsJSONArray.toString(), 1,
			pageTemplateSetsJSONArray.length());

		JSONObject pageTemplateSetJSONObject =
			pageTemplateSetsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			layoutPageTemplateCollectionId,
			pageTemplateSetJSONObject.getLong("id"));
		Assert.assertEquals(
			layoutPageTemplateCollectionName,
			pageTemplateSetJSONObject.getString("name"));
	}

	private void _testGetAdditionalPropsWithLayoutPageTemplateCollectionId(
		SelectLayoutPageTemplateEntryMasterLayoutDisplayContext
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext) {

		Mockito.when(
			_httpServletRequest.getParameter("layoutPageTemplateCollectionId")
		).thenReturn(
			String.valueOf(RandomTestUtil.randomLong())
		);

		Assert.assertEquals(
			Collections.emptyMap(),
			selectLayoutPageTemplateEntryMasterLayoutDisplayContext.
				getAdditionalProps());
	}

	private final MockedStatic<DesignLibraryUtil>
		_designLibraryUtilMockedStatic = Mockito.mockStatic(
			DesignLibraryUtil.class);
	private final Group _group = Mockito.mock(Group.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final MockedStatic<LayoutPageTemplateCollectionServiceUtil>
		_layoutPageTemplateCollectionServiceUtilMockedStatic =
			Mockito.mockStatic(LayoutPageTemplateCollectionServiceUtil.class);
	private final LiferayPortletURL _liferayPortletURL = Mockito.mock(
		LiferayPortletURL.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}