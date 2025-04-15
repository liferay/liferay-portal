/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.serializer.LayoutStructureItemJSONSerializer;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class AddFragmentEntryLinksMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_layout = layout.fetchDraftLayout();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddDropZoneFragmentEntryLinks() throws Exception {
		int numberOfFragmentEntryLinks = RandomTestUtil.randomInt(1, 3);

		_testAddFragmentEntryLinks(
			"<div><lfr-drop-zone></lfr-drop-zone></div>",
			numberOfFragmentEntryLinks);
	}

	@Test
	public void testAddFragmentEntryLinks() throws Exception {
		int numberOfFragmentEntryLinks = RandomTestUtil.randomInt(1, 3);

		_testAddFragmentEntryLinks(
			RandomTestUtil.randomString(), numberOfFragmentEntryLinks);
	}

	@Test
	@TestInfo("LPD-46069")
	public void testAddInputFragmentEntryLinks() throws Exception {
		FragmentComposition fragmentComposition = _addFragmentComposition(
			SetUtil.fromArray("text"), FragmentConstants.TYPE_INPUT,
			"<div></div>", 1);

		_testErrorMessage(fragmentComposition.getFragmentCompositionKey());

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "First Name",
						"firstName")));

		JSONObject jsonObject = ContentLayoutTestUtil.addFormToLayout(
			false,
			String.valueOf(
				_portal.getClassNameId(objectDefinition.getClassName())),
			"0", _layout, _layoutStructureProvider,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()));

		_testProcessAddFragmentEntryLinks(
			fragmentComposition.getFragmentCompositionKey(), 1,
			jsonObject.getString("addedItemId"), TestPropsValues.getUser());

		fragmentComposition = _addFragmentComposition(
			SetUtil.fromArray("localizationSelect"),
			FragmentConstants.TYPE_INPUT, "<div>localizationSelect</div>", 1);

		_testProcessAddFragmentEntryLinks(
			fragmentComposition.getFragmentCompositionKey(), 1, null,
			TestPropsValues.getUser());
	}

	private FragmentComposition _addFragmentComposition(
			Set<String> fieldTypes, int fragmentEntryType, String html,
			int numberOfFragmentEntryLinks)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				StringUtil.randomString(), StringPool.BLANK, serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK, html, StringPool.BLANK, false,
				StringPool.BLANK, null, 0, false, false, fragmentEntryType,
				JSONUtil.toString(JSONUtil.put("fieldTypes", fieldTypes)),
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = new LayoutStructure();

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerStyledLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		for (int i = 0; i < numberOfFragmentEntryLinks; i++) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.addFragmentEntryLink(
					null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
					fragmentEntry.getFragmentEntryId(),
					defaultSegmentsExperienceId, layout.getPlid(),
					fragmentEntry.getCss(), fragmentEntry.getHtml(),
					fragmentEntry.getJs(), fragmentEntry.getConfiguration(),
					"{}", StringPool.BLANK, 0, null, fragmentEntry.getType(),
					serviceContext);

			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLink.getFragmentEntryLinkId(),
				containerStyledLayoutStructureItem.getItemId(), -1);
		}

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				_group.getGroupId(), layout.getPlid(),
				defaultSegmentsExperienceId, layoutStructure.toString());

		String layoutStructureItemJSON =
			_layoutStructureItemJSONSerializer.toJSONString(
				layout, containerStyledLayoutStructureItem.getItemId(), false,
				false, defaultSegmentsExperienceId);

		return _fragmentCompositionLocalService.addFragmentComposition(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			fragmentCollection.getFragmentCollectionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), layoutStructureItemJSON, 0,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private FragmentComposition _addFragmentComposition(
			String html, int numberOfFragmentEntryLinks)
		throws Exception {

		return _addFragmentComposition(
			Collections.emptySet(), FragmentConstants.TYPE_COMPONENT, html,
			numberOfFragmentEntryLinks);
	}

	private void _assertFragmentEntryLinksContent(
		JSONObject fragmentEntryLinksJSONObject,
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest,
		MockLiferayPortletActionResponse mockLiferayPortletActionResponse) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			mockLiferayPortletActionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(mockLiferayPortletActionResponse);
		Locale locale = LocaleUtil.getMostRelevantLocale();

		for (String fragmentEntryLinkId :
				fragmentEntryLinksJSONObject.keySet()) {

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					Long.valueOf(fragmentEntryLinkId));

			Assert.assertNotNull(fragmentEntryLink);

			DefaultFragmentRendererContext defaultFragmentRendererContext =
				new DefaultFragmentRendererContext(fragmentEntryLink);

			defaultFragmentRendererContext.setLocale(locale);
			defaultFragmentRendererContext.setMode(
				FragmentEntryLinkConstants.EDIT);

			JSONObject fragmentEntryLinkJSONObject =
				fragmentEntryLinksJSONObject.getJSONObject(fragmentEntryLinkId);

			Assert.assertEquals(
				_removeUniqueId(
					_fragmentRendererController.render(
						defaultFragmentRendererContext, httpServletRequest,
						httpServletResponse)),
				_removeUniqueId(
					fragmentEntryLinkJSONObject.getString("content")));
		}
	}

	private void _assertLayoutData(JSONObject jsonObject) {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), _layout.getPlid());

		LayoutStructure updatedLayoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getDefaultSegmentsExperienceData());

		JSONObject updatedLayoutDataJSONObject =
			updatedLayoutStructure.toJSONObject();

		JSONObject layoutDataJSONObject = jsonObject.getJSONObject(
			"layoutData");

		Assert.assertEquals(
			updatedLayoutDataJSONObject.toString(),
			layoutDataJSONObject.toString());
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			String fragmentEntryKey, String parentItemId, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
				_company, _group, _layout);

		mockLiferayPortletActionRequest.addParameter(
			"fragmentEntryKey", fragmentEntryKey);

		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid());

		mockLiferayPortletActionRequest.addParameter(
			"segmentsExperienceId",
			String.valueOf(defaultSegmentsExperienceId));

		if (Validator.isNull(parentItemId)) {
			LayoutPageTemplateStructure layoutPageTemplateStructure =
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						_layout.getGroupId(), _layout.getPlid());

			LayoutStructure layoutStructure = LayoutStructure.of(
				layoutPageTemplateStructure.getData(
					defaultSegmentsExperienceId));

			parentItemId = layoutStructure.getMainItemId();
		}

		mockLiferayPortletActionRequest.addParameter(
			"parentItemId", parentItemId);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockLiferayPortletActionRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setRealUser(user);
		themeDisplay.setUser(user);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			mockLiferayPortletActionRequest);

		themeDisplay.setRequest(httpServletRequest);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private String _removeUniqueId(String content) {
		Document document = Jsoup.parseBodyFragment(content);

		Element bodyElement = document.body();

		Element element = bodyElement.firstElementSibling();

		element.attr("id", StringPool.BLANK);

		return element.outerHtml();
	}

	private void _testAddFragmentEntryLinks(
			String html, int numberOfFragmentEntryLinks)
		throws Exception {

		FragmentComposition fragmentComposition = _addFragmentComposition(
			html, numberOfFragmentEntryLinks);

		User user = UserTestUtil.addCompanyAdminUser(_company);

		try {
			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(
					_group, user.getUserId()));

			UserTestUtil.setUser(user);

			_layoutLockManager.getLock(_layout, user.getUserId());

			_testProcessAddFragmentEntryLinks(
				fragmentComposition.getFragmentCompositionKey(),
				numberOfFragmentEntryLinks, null, user);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();

			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	private void _testErrorMessage(String fragmentCompositionKey)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				fragmentCompositionKey, null, TestPropsValues.getUser());

		try {
			ReflectionTestUtil.invoke(
				_mvcActionCommand, "_processAddFragmentEntryLinks",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			Assert.fail();
		}
		catch (Exception exception) {
			JSONObject jsonObject = ReflectionTestUtil.invoke(
				_mvcActionCommand, "processException",
				new Class<?>[] {ActionRequest.class, Exception.class},
				mockLiferayPortletActionRequest, exception);

			Assert.assertEquals(jsonObject.toString(), 1, jsonObject.length());
			Assert.assertEquals(
				_language.get(
					_portal.getSiteDefaultLocale(_group),
					"this-form-component-can-only-be-placed-inside-a-mapped-" +
						"form-container"),
				jsonObject.getString("error"));
		}
	}

	private void _testProcessAddFragmentEntryLinks(
			String fragmentCompositionKey, int numberOfFragmentEntryLinks,
			String parentItemId, User user)
		throws Exception {

		List<FragmentEntryLink> originalFragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				fragmentCompositionKey, parentItemId, user);
		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		JSONObject jsonObject = ReflectionTestUtil.invoke(
			_mvcActionCommand, "_processAddFragmentEntryLinks",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				_group.getGroupId(), _layout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(),
			originalFragmentEntryLinks.size() + numberOfFragmentEntryLinks,
			fragmentEntryLinks.size());

		JSONObject fragmentEntryLinksJSONObject = jsonObject.getJSONObject(
			"fragmentEntryLinks");

		Assert.assertEquals(
			fragmentEntryLinksJSONObject.toString(), numberOfFragmentEntryLinks,
			fragmentEntryLinksJSONObject.length());

		_assertFragmentEntryLinksContent(
			fragmentEntryLinksJSONObject, mockLiferayPortletActionRequest,
			mockLiferayPortletActionResponse);

		_assertLayoutData(jsonObject);
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private FragmentRendererController _fragmentRendererController;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	private Layout _layout;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureItemJSONSerializer
		_layoutStructureItemJSONSerializer;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/add_fragment_entry_links"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}