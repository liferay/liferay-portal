/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.workflow.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManagerUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class LayoutWorkflowHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_serviceContext.setRequest(_getHttpServletRequest());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), Layout.class.getName(), 0, 0,
			"Single Approver@1");
	}

	@After
	public void tearDown() throws Exception {
		List<WorkflowInstance> workflowInstances =
			WorkflowInstanceManagerUtil.getWorkflowInstances(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				new String[] {Layout.class.getName()}, false, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		for (WorkflowInstance workflowInstance : workflowInstances) {
			WorkflowInstanceManagerUtil.deleteWorkflowInstance(
				TestPropsValues.getCompanyId(),
				workflowInstance.getWorkflowInstanceId());
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetURLViewInContext() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		MockHttpServletRequest mockHttpServletRequest =
			(MockHttpServletRequest)_serviceContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setURLCurrent(RandomTestUtil.randomString());

		_assertGetURLViewInContext(
			layout.getPlid(),
			draftLayout.getFriendlyURL(_portal.getSiteDefaultLocale(_group)),
			mockHttpServletRequest,
			HashMapBuilder.put(
				"p_l_back_url", themeDisplay.getURLCurrent()
			).build());

		themeDisplay.setDoAsUserId(RandomTestUtil.randomString());
		themeDisplay.setDoAsUserLanguageId(RandomTestUtil.randomString());

		_assertGetURLViewInContext(
			layout.getPlid(),
			draftLayout.getFriendlyURL(_portal.getSiteDefaultLocale(_group)),
			mockHttpServletRequest,
			HashMapBuilder.put(
				"doAsUserId", themeDisplay.getDoAsUserId()
			).put(
				"doAsUserLanguageId", themeDisplay.getDoAsUserLanguageId()
			).put(
				"p_l_back_url", themeDisplay.getURLCurrent()
			).build());
	}

	@Test
	public void testWorkflowHandlerContentLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, layout.getStatus());

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", draftLayout, segmentsExperienceId);

		String keywords = fragmentEntryLink.getHtml();

		Assert.assertTrue(keywords, Validator.isNotNull(keywords));

		IndexerFixture<Layout> layoutIndexerFixture = new IndexerFixture<>(
			Layout.class);

		layoutIndexerFixture.searchNoOne(keywords);

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				Layout.class.getName());

		Assert.assertNotNull(
			workflowHandler.getWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				layout.getPlid()));

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			TestPropsValues.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId(), Layout.class.getName(),
			layout.getPlid(), layout, _serviceContext, Collections.emptyMap());

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, layout.getStatus());

		layoutIndexerFixture.searchNoOne(keywords);

		workflowHandler.updateStatus(
			WorkflowConstants.STATUS_APPROVED,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK,
				String.valueOf(layout.getPlid())
			).put(
				WorkflowConstants.CONTEXT_USER_ID,
				String.valueOf(TestPropsValues.getUserId())
			).put(
				"serviceContext", _serviceContext
			).build());

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		Locale locale = LocaleUtil.getSiteDefault();

		Document document = layoutIndexerFixture.searchOnlyOne(
			keywords, locale);

		Assert.assertNotNull(document);

		String content = document.get(
			Field.getLocalizedName(locale, Field.CONTENT));

		Assert.assertTrue(
			content, StringUtil.contains(content, keywords, StringPool.BLANK));

		Assert.assertEquals(
			document.get(Field.ENTRY_CLASS_PK),
			String.valueOf(layout.getPlid()));
	}

	@Test
	public void testWorkflowHandlerContentLayoutWithSegmentsExperiences()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		String languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		String defaultExperienceHeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			defaultExperienceHeadingText);

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), SegmentsEntryConstants.ID_DEFAULT,
				draftLayout.getPlid());

		String experience1HeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			segmentsExperience1.getSegmentsExperienceId(),
			experience1HeadingText);

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), SegmentsEntryConstants.ID_DEFAULT,
				draftLayout.getPlid());

		String experience2HeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			segmentsExperience2.getSegmentsExperienceId(),
			experience2HeadingText);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, layout.getStatus());

		_approveUserWorkflowTasks();

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			defaultExperienceHeadingText, layoutPageTemplateStructure);

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getGroupId(),
				segmentsExperience1.getSegmentsExperienceKey(),
				layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId, segmentsExperience1.getSegmentsExperienceId(),
			experience1HeadingText, layoutPageTemplateStructure);

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getGroupId(),
				segmentsExperience2.getSegmentsExperienceKey(),
				layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId, segmentsExperience2.getSegmentsExperienceId(),
			experience2HeadingText, layoutPageTemplateStructure);
	}

	@Test
	public void testWorkflowHandlerContentLayoutWithSegmentsExperiencesWithoutPrincipalThreadLocalUser()
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertNotNull(draftLayout);

		String languageId = LocaleUtil.toLanguageId(
			_portal.getSiteDefaultLocale(_group));

		String defaultExperienceHeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()),
			defaultExperienceHeadingText);

		SegmentsExperience segmentsExperience1 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), SegmentsEntryConstants.ID_DEFAULT,
				draftLayout.getPlid());

		String experience1HeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			segmentsExperience1.getSegmentsExperienceId(),
			experience1HeadingText);

		SegmentsExperience segmentsExperience2 =
			SegmentsTestUtil.addSegmentsExperience(
				_group.getGroupId(), SegmentsEntryConstants.ID_DEFAULT,
				draftLayout.getPlid());

		String experience2HeadingText = RandomTestUtil.randomString();

		_addHeadingFragmentToLayout(
			draftLayout, languageId,
			segmentsExperience2.getSegmentsExperienceId(),
			experience2HeadingText);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, layout.getStatus());

		long originalUserId = PrincipalThreadLocal.getUserId();

		try {
			PrincipalThreadLocal.setName(0);

			_approveUserWorkflowTasks();
		}
		finally {
			PrincipalThreadLocal.setName(originalUserId);
		}

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					_group.getGroupId(), layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid()),
			defaultExperienceHeadingText, layoutPageTemplateStructure);

		segmentsExperience1 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience1.getGroupId(),
				segmentsExperience1.getSegmentsExperienceKey(),
				layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId, segmentsExperience1.getSegmentsExperienceId(),
			experience1HeadingText, layoutPageTemplateStructure);

		segmentsExperience2 =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperience2.getGroupId(),
				segmentsExperience2.getSegmentsExperienceKey(),
				layout.getPlid());

		_assertSegmentExperienceFragmentEntryLink(
			languageId, segmentsExperience2.getSegmentsExperienceId(),
			experience2HeadingText, layoutPageTemplateStructure);
	}

	@Test
	public void testWorkflowHandlerDeleteContentLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Assert.assertEquals(WorkflowConstants.STATUS_DRAFT, layout.getStatus());

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				Layout.class.getName());

		Assert.assertNotNull(
			workflowHandler.getWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				layout.getPlid()));

		WorkflowHandlerRegistryUtil.startWorkflowInstance(
			TestPropsValues.getCompanyId(), _group.getGroupId(),
			TestPropsValues.getUserId(), Layout.class.getName(),
			layout.getPlid(), layout, _serviceContext, Collections.emptyMap());

		layout = _layoutLocalService.getLayout(layout.getPlid());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, layout.getStatus());

		Assert.assertNotNull(
			WorkflowInstanceLinkLocalServiceUtil.fetchWorkflowInstanceLink(
				layout.getCompanyId(), layout.getGroupId(),
				Layout.class.getName(), layout.getPlid()));

		_layoutLocalService.deleteLayout(layout.getPlid());

		Assert.assertNull(
			WorkflowInstanceLinkLocalServiceUtil.fetchWorkflowInstanceLink(
				layout.getCompanyId(), layout.getGroupId(),
				Layout.class.getName(), layout.getPlid()));

		Assert.assertNull(_layoutLocalService.fetchLayout(layout.getPlid()));
	}

	@Test
	public void testWorkflowHandlerWidgetLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), StringPool.BLANK);

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				Layout.class.getName());

		Assert.assertNull(
			workflowHandler.getWorkflowDefinitionLink(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				layout.getPlid()));

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, layout.getStatus());
	}

	private void _addHeadingFragmentToLayout(
			Layout layout, String languageId, long segmentsExperienceId,
			String text)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		Assert.assertNotNull(fragmentEntry);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("element-text", JSONUtil.put(languageId, text))
			).put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put("headingLevel", "h1")
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getFragmentEntryId(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), layout, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), null, 0, segmentsExperienceId);
	}

	private void _approveUserWorkflowTasks() throws PortalException {
		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksBySubmittingUser(
					_group.getCompanyId(), TestPropsValues.getUserId(), false,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			workflowTask = _workflowTaskManager.assignWorkflowTaskToUser(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				StringPool.BLANK, null, null);

			Assert.assertEquals(
				TestPropsValues.getUserId(), workflowTask.getAssigneeUserId());

			workflowTask = _workflowTaskManager.completeWorkflowTask(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);

			Assert.assertTrue(workflowTask.isCompleted());
		}
	}

	private void _assertGetURLViewInContext(
		long classPK, String expectedFriendlyURL,
		MockHttpServletRequest mockHttpServletRequest,
		Map<String, String> parameterMap) {

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(
				Layout.class.getName());

		String url = workflowHandler.getURLViewInContext(
			classPK,
			new MockLiferayPortletRenderRequest(mockHttpServletRequest),
			new MockLiferayPortletRenderResponse(), null);

		Assert.assertEquals(
			StringBundler.concat(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				_group.getFriendlyURL(), expectedFriendlyURL),
			HttpComponentsUtil.getPath(url));

		Map<String, String[]> currentParameterMap =
			HttpComponentsUtil.getParameterMap(
				HttpComponentsUtil.getQueryString(url));

		Assert.assertEquals(
			currentParameterMap.toString(), parameterMap.size(),
			currentParameterMap.size());

		for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
			String[] values = currentParameterMap.get(entry.getKey());

			Assert.assertEquals(values.toString(), 1, values.length);
			Assert.assertEquals(entry.getValue(), values[0]);
		}
	}

	private void _assertSegmentExperienceFragmentEntryLink(
			String languageId, long segmentsExperienceId,
			String experienceHeadingText,
			LayoutPageTemplateStructure layoutPageTemplateStructure)
		throws Exception {

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		Map<Long, LayoutStructureItem> fragmentLayoutStructureItems =
			layoutStructure.getFragmentLayoutStructureItems();

		Assert.assertEquals(
			fragmentLayoutStructureItems.toString(), 1,
			fragmentLayoutStructureItems.size());

		Set<Long> keySet = fragmentLayoutStructureItems.keySet();

		Iterator<Long> iterator = keySet.iterator();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				iterator.next());

		Assert.assertNotNull(fragmentEntryLink);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			fragmentEntryLink.getEditableValues());

		JSONObject editableJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		Assert.assertNotNull(editableJSONObject);

		JSONObject textJSONObject = editableJSONObject.getJSONObject(
			"element-text");

		Assert.assertNotNull(textJSONObject);

		Assert.assertEquals(
			experienceHeadingText, textJSONObject.getString(languageId));
	}

	private HttpServletRequest _getHttpServletRequest() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			company, _group, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}