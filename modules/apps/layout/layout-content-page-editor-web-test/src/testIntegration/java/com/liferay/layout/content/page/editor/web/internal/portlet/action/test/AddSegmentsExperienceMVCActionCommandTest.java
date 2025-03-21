/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Sarai Díaz
 */
@RunWith(Arquillian.class)
public class AddSegmentsExperienceMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo("LPS-110227")
	public void testAddSegmentsExperienceWithPortlet() throws Exception {
		AssetVocabulary assetVocabulary1 = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(AssetCategory.class.getName()), 0,
			_portal.getClassNameId(PortletDisplayTemplate.class.getName()),
			TemplateConstants.LANG_TYPE_FTL,
			"<#if entries?has_content><#list entries as curVocabulary>" +
				"${curVocabulary.name}</#list></#if>",
			_portal.getSiteDefaultLocale(_group));

		String displayStyle =
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX +
				ddmTemplate.getTemplateKey();

		_setUpPortletPreferences(
			assetVocabulary1, _addPortletToLayout(), displayStyle);

		AssetVocabulary assetVocabulary2 = AssetTestUtil.addVocabulary(
			_group.getGroupId());

		long segmentsExperienceId = _addSegmentsExperience();

		_setUpPortletPreferences(
			assetVocabulary2, _getPortletId(segmentsExperienceId),
			displayStyle);

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		String html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		Assert.assertTrue(html.contains(assetVocabulary1.getName()));
		Assert.assertFalse(html.contains(assetVocabulary2.getName()));

		SegmentsExperience draftLayoutSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		SegmentsExperience publishLayoutSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				draftLayoutSegmentsExperience.getGroupId(),
				draftLayoutSegmentsExperience.getSegmentsExperienceKey(),
				_layout.getPlid());

		html = ContentLayoutTestUtil.getRenderLayoutHTML(
			_layout, _layoutServiceContextHelper, _layoutStructureProvider,
			publishLayoutSegmentsExperience.getSegmentsExperienceId());

		Assert.assertFalse(html.contains(assetVocabulary1.getName()));
		Assert.assertTrue(html.contains(assetVocabulary2.getName()));
	}

	@Test
	public void testAddSegmentsExperiment() throws Exception {
		FragmentEntryLink sourceFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", _draftLayout,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_draftLayout.getPlid()));

		String name = RandomTestUtil.randomString(10);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		JSONObject responseJSONObject = _addSegmentsExperience(
			name, segmentsEntry.getSegmentsEntryId());

		JSONObject segmentsExperienceJSONObject =
			responseJSONObject.getJSONObject("segmentsExperience");

		Assert.assertEquals(name, segmentsExperienceJSONObject.get("name"));
		Assert.assertEquals(
			segmentsEntry.getSegmentsEntryId(),
			GetterUtil.getLong(
				segmentsExperienceJSONObject.get("segmentsEntryId")));

		long segmentsExperienceId = GetterUtil.getLong(
			segmentsExperienceJSONObject.get("segmentsExperienceId"));

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.getSegmentsExperience(
				segmentsExperienceId);

		Assert.assertTrue(segmentsExperience.isActive());
		Assert.assertEquals(
			name, segmentsExperience.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			segmentsEntry.getSegmentsEntryId(),
			segmentsExperience.getSegmentsEntryId());
		Assert.assertEquals(
			segmentsExperienceId, segmentsExperience.getSegmentsExperienceId());

		_assertFragmentEntryLinks(
			responseJSONObject.getJSONObject("fragmentEntryLinks"),
			sourceFragmentEntryLink);
	}

	private String _addPortletToLayout() throws Exception {
		JSONObject processAddPortletJSONObject =
			ContentLayoutTestUtil.addPortletToLayout(
				_draftLayout,
				AssetCategoriesNavigationPortletKeys.
					ASSET_CATEGORIES_NAVIGATION);

		JSONObject fragmentEntryLinkJSONObject =
			processAddPortletJSONObject.getJSONObject("fragmentEntryLink");

		JSONObject editableValuesJSONObject =
			fragmentEntryLinkJSONObject.getJSONObject("editableValues");

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private long _addSegmentsExperience() throws Exception {
		JSONObject responseJSONObject = _addSegmentsExperience(
			RandomTestUtil.randomString(), 0);

		JSONObject segmentsExperienceJSONObject =
			responseJSONObject.getJSONObject("segmentsExperience");

		return GetterUtil.getLong(
			segmentsExperienceJSONObject.get("segmentsExperienceId"));
	}

	private JSONObject _addSegmentsExperience(String name, long segmentsEntryId)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_mvcActionCommand, "addSegmentsExperience",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			_getMockLiferayPortletActionRequest(name, segmentsEntryId),
			new MockLiferayPortletActionResponse());
	}

	private void _assertFragmentEntryLinks(
			JSONObject fragmentEntryLinksJSONObject,
			FragmentEntryLink sourceFragmentEntryLink)
		throws Exception {

		Assert.assertNotNull(fragmentEntryLinksJSONObject);
		Assert.assertEquals(
			fragmentEntryLinksJSONObject.toString(), 1,
			fragmentEntryLinksJSONObject.length());

		Iterator<String> iterator = fragmentEntryLinksJSONObject.keys();

		FragmentEntryLink targetFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				GetterUtil.getLong(iterator.next()));

		Assert.assertEquals(
			0, targetFragmentEntryLink.getOriginalFragmentEntryLinkId());

		Assert.assertEquals(
			sourceFragmentEntryLink.getFragmentEntryId(),
			targetFragmentEntryLink.getFragmentEntryId());
		Assert.assertEquals(
			sourceFragmentEntryLink.getHtml(),
			targetFragmentEntryLink.getHtml());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String name, long segmentsEntryId)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter("name", name);
		mockLiferayPortletActionRequest.addParameter(
			"plid", String.valueOf(_draftLayout.getPlid()));
		mockLiferayPortletActionRequest.addParameter(
			"segmentsEntryId", String.valueOf(segmentsEntryId));
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG, null);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.LAYOUT, _draftLayout);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		return mockLiferayPortletActionRequest;
	}

	private String _getPortletId(long segmentsExperienceId) throws Exception {
		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_group.getGroupId(), segmentsExperienceId,
					_draftLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		Assert.assertEquals(
			FragmentConstants.TYPE_PORTLET, fragmentEntryLink.getType());

		JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
			fragmentEntryLink.getEditableValues());

		return PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(_draftLayout);
		themeDisplay.setLayoutSet(_draftLayout.getLayoutSet());
		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)_draftLayout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPlid(_draftLayout.getPlid());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setServerName("localhost");
		themeDisplay.setServerPort(8080);
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private void _setUpPortletPreferences(
			AssetVocabulary assetVocabulary, String portletId,
			String displayStyle)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(_draftLayout, portletId);

		portletPreferences.setValue(
			"allAssetVocabularies", Boolean.FALSE.toString());
		portletPreferences.setValue(
			"assetVocabularyExternalReferenceCodes_" +
				_group.getExternalReferenceCode(),
			assetVocabulary.getExternalReferenceCode());
		portletPreferences.setValue(
			"assetVocabularyGroupExternalReferenceCodes",
			_group.getExternalReferenceCode());
		portletPreferences.setValue("displayStyle", displayStyle);
		portletPreferences.setValue(
			"displayStyleGroupExternalReferenceCode",
			_group.getExternalReferenceCode());

		portletPreferences.store();
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_segments_experience"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SegmentsExperienceService _segmentsExperienceService;

}