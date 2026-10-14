/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jürgen Kappler
 */
public class ContentEditorToolbarComponentSectionFragmentRendererTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		ReflectionTestUtil.setFieldValue(
			_contentEditorToolbarComponentSectionFragmentRenderer,
			"_layoutPageTemplateEntryLocalService",
			_layoutPageTemplateEntryLocalService);
		ReflectionTestUtil.setFieldValue(
			_contentEditorToolbarComponentSectionFragmentRenderer,
			"_objectDefinitionLocalService", _objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_contentEditorToolbarComponentSectionFragmentRenderer,
			"_objectFieldLocalService", _objectFieldLocalService);
		ReflectionTestUtil.setFieldValue(
			_contentEditorToolbarComponentSectionFragmentRenderer,
			"_workflowDefinitionLinkLocalService",
			_workflowDefinitionLinkLocalService);
		ReflectionTestUtil.setFieldValue(
			_contentEditorToolbarComponentSectionFragmentRenderer, "language",
			_language);
	}

	@Test
	public void testGetProps() {
		String redirect = "javascript:alert(1)";

		String escapedRedirect = RandomTestUtil.randomString();

		Mockito.when(
			_portal.escapeRedirect(redirect)
		).thenReturn(
			escapedRedirect
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("redirect", redirect);

		Map<String, Object> props =
			_contentEditorToolbarComponentSectionFragmentRenderer.getProps(
				null, mockHttpServletRequest);

		Assert.assertEquals(escapedRedirect, props.get("backURL"));
	}

	@Test
	public void testGetPropsObjectFields() throws Exception {
		Mockito.when(
			_layout.getPlid()
		).thenReturn(
			_PLID
		);

		Mockito.when(
			_layoutDisplayPageObjectProvider.getDisplayObject()
		).thenReturn(
			_objectEntry
		);

		Mockito.when(
			_layoutDisplayPageObjectProvider.getTitle(LocaleUtil.US)
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_layoutPageTemplateEntry.getLayoutPageTemplateEntryKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(_PLID)
		).thenReturn(
			_layoutPageTemplateEntry
		);

		Mockito.when(
			_metadataObjectField.isMetadata()
		).thenReturn(
			true
		);

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				_OBJECT_DEFINITION_ID)
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_objectEntry.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		String businessType = RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getBusinessType()
		).thenReturn(
			businessType
		);

		String label = RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getLabel(LocaleUtil.US)
		).thenReturn(
			label
		);

		String name = RandomTestUtil.randomString();

		Mockito.when(
			_objectField.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			_objectFieldLocalService.getObjectFields(_OBJECT_DEFINITION_ID)
		).thenReturn(
			List.of(_metadataObjectField, _objectField)
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Mockito.when(
			_portal.getOriginalServletRequest(mockHttpServletRequest)
		).thenReturn(
			mockHttpServletRequest
		);

		Mockito.when(
			_portal.getSiteDefaultLocale(Mockito.anyLong())
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_themeDisplay.getLayout()
		).thenReturn(
			_layout
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			_layoutDisplayPageObjectProvider);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		Map<String, Object> props =
			_contentEditorToolbarComponentSectionFragmentRenderer.getProps(
				null, mockHttpServletRequest);

		JSONArray jsonArray = (JSONArray)props.get("objectFields");

		Assert.assertEquals(1, jsonArray.length());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(businessType, jsonObject.getString("businessType"));
		Assert.assertEquals(label, jsonObject.getString("label"));
		Assert.assertEquals(name, jsonObject.getString("name"));
	}

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final long _PLID = RandomTestUtil.randomLong();

	private final ContentEditorToolbarComponentSectionFragmentRenderer
		_contentEditorToolbarComponentSectionFragmentRenderer =
			new ContentEditorToolbarComponentSectionFragmentRenderer();

	@Mock
	private Language _language;

	@Mock
	private Layout _layout;

	@Mock
	private LayoutDisplayPageObjectProvider<?> _layoutDisplayPageObjectProvider;

	@Mock
	private LayoutPageTemplateEntry _layoutPageTemplateEntry;

	@Mock
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Mock
	private ObjectField _metadataObjectField;

	@Mock
	private ObjectDefinition _objectDefinition;

	@Mock
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Mock
	private ObjectEntry _objectEntry;

	@Mock
	private ObjectField _objectField;

	@Mock
	private ObjectFieldLocalService _objectFieldLocalService;

	@Mock
	private Portal _portal;

	@Mock
	private ThemeDisplay _themeDisplay;

	@Mock
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}