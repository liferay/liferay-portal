/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.layout.content.exception.LayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.LayoutContentVersionNameException;
import com.liferay.layout.content.exception.RequiredLayoutContentVersionException;
import com.liferay.layout.content.exception.UnsupportedLayoutLayoutContentVersionException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionPreview;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.content.service.LayoutContentVersionPreviewLocalService;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.renderer.LayoutPreviewRenderer;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.PortletServlet;
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
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.spring.aop.AopInvocationHandler;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
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
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES"}, defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class LayoutContentVersionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = layout.fetchDraftLayout();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo({"LPD-103233", "LPD-103846"})
	public void testAddLayoutContentVersion() throws Exception {
		_addSegmentsExperiences(2);

		Map<SegmentsExperience, JSONObject> segmentsExperienceJSONObjectsMap =
			_getRandomSegmentsExperienceLocalizedContentMap();

		_addFragmentEntryLinksToLayout(segmentsExperienceJSONObjectsMap);

		String data = RandomTestUtil.randomString();

		LayoutContentVersion draftLayoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				data, RandomTestUtil.randomLocaleStringMap(),
				_draftLayout.getPlid(), WorkflowConstants.STATUS_DRAFT);

		Assert.assertNotNull(draftLayoutContentVersion.getDataHash());
		Assert.assertEquals(
			_draftLayout.getPlid(), draftLayoutContentVersion.getPlid());
		Assert.assertEquals(1, draftLayoutContentVersion.getVersion());
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			draftLayoutContentVersion.getStatus());

		_assertLayoutContentVersionPreviews(
			draftLayoutContentVersion, segmentsExperienceJSONObjectsMap);

		try (SafeCloseable safeCloseable =
				_swapLayoutPreviewRendererWithSafeCloseable()) {

			LayoutContentVersion approvedLayoutContentVersion =
				_layoutContentVersionLocalService.addLayoutContentVersion(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					data, RandomTestUtil.randomLocaleStringMap(),
					_draftLayout.getPlid(), WorkflowConstants.STATUS_APPROVED);

			Assert.assertNotEquals(
				draftLayoutContentVersion.getLayoutContentVersionId(),
				approvedLayoutContentVersion.getLayoutContentVersionId());
			Assert.assertEquals(2, approvedLayoutContentVersion.getVersion());
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				approvedLayoutContentVersion.getStatus());

			_assertPreviewErrorLayoutContentVersionPreviews(
				approvedLayoutContentVersion,
				segmentsExperienceJSONObjectsMap.keySet());
		}

		_testAddLayoutContentVersionWithExternalReferenceCodeTooLong();
		_testAddLayoutContentVersionWithNullExternalReferenceCode();
		_testAddLayoutContentVersionWithNullNameMap();
		_testAddLayoutContentVersionWithUnsupportedLayout();
	}

	@Test
	public void testAddOrUpdateLayoutContentVersion() throws Exception {
		String data = RandomTestUtil.randomString();

		LayoutContentVersion layoutContentVersion1 =
			_layoutContentVersionLocalService.addOrUpdateLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				data, RandomTestUtil.randomLocaleStringMap(),
				_draftLayout.getPlid(), WorkflowConstants.STATUS_DRAFT);
		LayoutContentVersion layoutContentVersion2 =
			_layoutContentVersionLocalService.addOrUpdateLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				data, RandomTestUtil.randomLocaleStringMap(),
				_draftLayout.getPlid(), WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			layoutContentVersion1.getLayoutContentVersionId(),
			layoutContentVersion2.getLayoutContentVersionId());

		data = RandomTestUtil.randomString();

		LayoutContentVersion layoutContentVersion3 =
			_layoutContentVersionLocalService.addOrUpdateLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				data, RandomTestUtil.randomLocaleStringMap(),
				_draftLayout.getPlid(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertNotEquals(
			layoutContentVersion2.getLayoutContentVersionId(),
			layoutContentVersion3.getLayoutContentVersionId());
	}

	@Test
	@TestInfo("LPD-90030")
	public void testDeleteLayoutContentVersion() throws Exception {
		LayoutContentVersion approvedLayoutContentVersion =
			_addLayoutContentVersion(WorkflowConstants.STATUS_APPROVED);

		LayoutContentVersionPreview approvedLayoutContentVersionPreview =
			_addLayoutContentVersionPreview(
				approvedLayoutContentVersion.getLayoutContentVersionId());

		LayoutContentVersion latestApprovedLayoutContentVersion =
			_addLayoutContentVersion(WorkflowConstants.STATUS_APPROVED);

		LayoutContentVersionPreview latestApprovedLayoutContentVersionPreview =
			_addLayoutContentVersionPreview(
				latestApprovedLayoutContentVersion.getLayoutContentVersionId());

		LayoutContentVersion draftLayoutContentVersion =
			_addLayoutContentVersion(WorkflowConstants.STATUS_DRAFT);

		LayoutContentVersionPreview draftLayoutContentVersionPreview =
			_addLayoutContentVersionPreview(
				draftLayoutContentVersion.getLayoutContentVersionId());

		_layoutContentVersionLocalService.deleteLayoutContentVersion(
			approvedLayoutContentVersion.getLayoutContentVersionId());

		Assert.assertNull(
			_layoutContentVersionLocalService.fetchLayoutContentVersion(
				approvedLayoutContentVersion.getLayoutContentVersionId()));
		Assert.assertNull(
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					approvedLayoutContentVersionPreview.
						getLayoutContentVersionPreviewId()));

		Assert.assertThrows(
			RequiredLayoutContentVersionException.class,
			() -> _layoutContentVersionLocalService.deleteLayoutContentVersion(
				latestApprovedLayoutContentVersion.
					getLayoutContentVersionId()));

		Assert.assertNotNull(
			_layoutContentVersionLocalService.fetchLayoutContentVersion(
				latestApprovedLayoutContentVersion.
					getLayoutContentVersionId()));
		Assert.assertNotNull(
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					latestApprovedLayoutContentVersionPreview.
						getLayoutContentVersionPreviewId()));

		_layoutContentVersionLocalService.deleteLayoutContentVersion(
			draftLayoutContentVersion.getLayoutContentVersionId());

		Assert.assertNull(
			_layoutContentVersionLocalService.fetchLayoutContentVersion(
				draftLayoutContentVersion.getLayoutContentVersionId()));
		Assert.assertNull(
			_layoutContentVersionPreviewLocalService.
				fetchLayoutContentVersionPreview(
					draftLayoutContentVersionPreview.
						getLayoutContentVersionPreviewId()));
	}

	@Test
	public void testGetLayoutContentVersions() throws Exception {
		_layoutContentVersionLocalService.addLayoutContentVersion(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT);
		_layoutContentVersionLocalService.addLayoutContentVersion(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT);

		List<LayoutContentVersion> layoutContentVersions =
			_layoutContentVersionLocalService.getLayoutContentVersions(
				_draftLayout.getPlid());

		Assert.assertEquals(
			layoutContentVersions.toString(), 2, layoutContentVersions.size());
	}

	@Test
	public void testUpdateLayoutContentVersion() throws Exception {
		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
				WorkflowConstants.STATUS_DRAFT);

		Locale locale = LocaleUtil.getDefault();
		String name = RandomTestUtil.randomString();

		LayoutContentVersion updatedLayoutContentVersion =
			_layoutContentVersionLocalService.updateLayoutContentVersion(
				layoutContentVersion.getLayoutContentVersionId(),
				HashMapBuilder.put(
					locale, name
				).build());

		Assert.assertEquals(name, updatedLayoutContentVersion.getName(locale));

		String expectedMessage = "Name is null";

		_assertPortalException(
			expectedMessage, LayoutContentVersionNameException.class,
			() -> _layoutContentVersionLocalService.updateLayoutContentVersion(
				layoutContentVersion.getLayoutContentVersionId(),
				new HashMap<>()));
		_assertPortalException(
			expectedMessage, LayoutContentVersionNameException.class,
			() -> _layoutContentVersionLocalService.updateLayoutContentVersion(
				layoutContentVersion.getLayoutContentVersionId(), null));
	}

	private void _addFragmentEntryLinksToLayout(
			Map<SegmentsExperience, JSONObject>
				segmentsExperienceJSONObjectsMap)
		throws Exception {

		JSONObject defaultValueJSONObject = JSONUtil.put(
			"defaultValue", "Heading Example");

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		for (Map.Entry<SegmentsExperience, JSONObject> entry :
				segmentsExperienceJSONObjectsMap.entrySet()) {

			SegmentsExperience segmentsExperience = entry.getKey();

			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.merge(
							entry.getValue(), defaultValueJSONObject))
				).toString(),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(), null,
				fragmentEntry.getHtml(), fragmentEntry.getJs(), _draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0, segmentsExperience.getSegmentsExperienceId());
		}
	}

	private LayoutContentVersion _addLayoutContentVersion(int status)
		throws Exception {

		return _layoutContentVersionLocalService.addLayoutContentVersion(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
			status);
	}

	private LayoutContentVersionPreview _addLayoutContentVersionPreview(
			long layoutContentVersionId)
		throws Exception {

		return _layoutContentVersionPreviewLocalService.
			addLayoutContentVersionPreview(
				TestPropsValues.getUserId(), layoutContentVersionId,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString());
	}

	private void _addSegmentsExperiences(int count) throws Exception {
		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		MVCActionCommand mvcActionCommand =
			ContentLayoutTestUtil.getMVCActionCommand(
				"/layout_content_page_editor/add_segments_experience");

		for (int i = 0; i < count; i++) {
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				ContentLayoutTestUtil.getMockLiferayPortletActionRequest(
					company, _group, _draftLayout);

			mockLiferayPortletActionRequest.setParameter(
				"groupId", String.valueOf(_draftLayout.getGroupId()));
			mockLiferayPortletActionRequest.setParameter(
				"name", RandomTestUtil.randomString());
			mockLiferayPortletActionRequest.setParameter(
				"plid", String.valueOf(_draftLayout.getPlid()));

			HttpServletRequest httpServletRequest =
				new MockHttpServletRequest();

			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY,
				mockLiferayPortletActionRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
			httpServletRequest.setAttribute(
				WebKeys.USER_ID, TestPropsValues.getUserId());

			mockLiferayPortletActionRequest.setAttribute(
				PortletServlet.PORTLET_SERVLET_REQUEST, httpServletRequest);

			ReflectionTestUtil.invoke(
				mvcActionCommand, "doTransactionalCommand",
				new Class<?>[] {ActionRequest.class, ActionResponse.class},
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());
		}
	}

	private void _assertLayoutContentVersionPreviews(
			LayoutContentVersion layoutContentVersion,
			Map<SegmentsExperience, JSONObject>
				segmentsExperienceJSONObjectsMap)
		throws Exception {

		for (Map.Entry<SegmentsExperience, JSONObject> entry :
				segmentsExperienceJSONObjectsMap.entrySet()) {

			SegmentsExperience segmentsExperience = entry.getKey();
			JSONObject jsonObject = entry.getValue();

			for (String languageId : jsonObject.keySet()) {
				LayoutContentVersionPreview layoutContentVersionPreview =
					_layoutContentVersionPreviewLocalService.
						fetchLayoutContentVersionPreview(
							layoutContentVersion.getLayoutContentVersionId(),
							languageId,
							segmentsExperience.getExternalReferenceCode());

				String html = layoutContentVersionPreview.getHtml();

				Assert.assertTrue(html, html.contains("/company_logo"));
				Assert.assertTrue(
					html, html.contains(jsonObject.getString(languageId)));
			}
		}

		List<LayoutContentVersionPreview> layoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					layoutContentVersion.getLayoutContentVersionId());

		Assert.assertEquals(
			layoutContentVersionPreviews.toString(),
			segmentsExperienceJSONObjectsMap.size() * 2,
			layoutContentVersionPreviews.size());
	}

	private <T extends PortalException> void _assertPortalException(
		String expectedMessage, Class<T> portalExceptionClass,
		ThrowingRunnable runnable) {

		PortalException portalException = Assert.assertThrows(
			portalExceptionClass, runnable);

		Assert.assertEquals(expectedMessage, portalException.getMessage());
	}

	private void _assertPreviewErrorLayoutContentVersionPreviews(
			LayoutContentVersion layoutContentVersion,
			Set<SegmentsExperience> segmentsExperiences)
		throws Exception {

		HashMap<String, String> map = HashMapBuilder.put(
			LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
			_getPreviewErrorMessage(LocaleUtil.SPAIN)
		).put(
			LocaleUtil.toLanguageId(LocaleUtil.US),
			_getPreviewErrorMessage(LocaleUtil.US)
		).build();

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				LayoutContentVersionPreview layoutContentVersionPreview =
					_layoutContentVersionPreviewLocalService.
						fetchLayoutContentVersionPreview(
							layoutContentVersion.getLayoutContentVersionId(),
							entry.getKey(),
							segmentsExperience.getExternalReferenceCode());

				String html = layoutContentVersionPreview.getHtml();

				Assert.assertTrue(html, html.contains(entry.getValue()));
			}
		}

		List<LayoutContentVersionPreview> layoutContentVersionPreviews =
			_layoutContentVersionPreviewLocalService.
				getLayoutContentVersionPreviews(
					layoutContentVersion.getLayoutContentVersionId());

		Assert.assertEquals(
			layoutContentVersionPreviews.toString(),
			segmentsExperiences.size() * 2,
			layoutContentVersionPreviews.size());
	}

	private String _getPreviewErrorMessage(Locale locale) {
		return _language.get(
			locale,
			"this-preview-is-not-available.-an-error-occurred-while-" +
				"generating-the-preview-when-this-version-was-created");
	}

	private Map<SegmentsExperience, JSONObject>
		_getRandomSegmentsExperienceLocalizedContentMap() {

		Map<SegmentsExperience, JSONObject> map = new HashMap<>();

		for (SegmentsExperience segmentsExperience :
				_segmentsExperienceLocalService.getSegmentsExperiences(
					_group.getGroupId(), _draftLayout.getPlid())) {

			map.put(
				segmentsExperience,
				JSONUtil.put(
					LocaleUtil.toLanguageId(LocaleUtil.SPAIN),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toLanguageId(LocaleUtil.US),
					RandomTestUtil.randomString()
				));
		}

		return map;
	}

	private SafeCloseable _swapLayoutPreviewRendererWithSafeCloseable() {
		AopInvocationHandler aopInvocationHandler =
			(AopInvocationHandler)ProxyUtil.getInvocationHandler(
				_layoutContentVersionLocalService);

		Object layoutContentVersionLocalServiceImpl =
			aopInvocationHandler.getTarget();

		LayoutPreviewRenderer originalLayoutPreviewRenderer =
			ReflectionTestUtil.getAndSetFieldValue(
				layoutContentVersionLocalServiceImpl, "_layoutPreviewRenderer",
				(layout, locale, segmentsExperienceId, serviceContext) -> {
					throw new Exception();
				});

		return () -> ReflectionTestUtil.setFieldValue(
			layoutContentVersionLocalServiceImpl, "_layoutPreviewRenderer",
			originalLayoutPreviewRenderer);
	}

	private void _testAddLayoutContentVersionWithExternalReferenceCodeTooLong() {
		int maxLength = ModelHintsUtil.getMaxLength(
			LayoutContentVersion.class.getName(), "externalReferenceCode");

		_assertPortalException(
			"External reference code must be less than " + maxLength +
				" characters",
			LayoutContentVersionExternalReferenceCodeException.class,
			() -> _layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(maxLength + 1),
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
				WorkflowConstants.STATUS_DRAFT));
	}

	private void _testAddLayoutContentVersionWithNullExternalReferenceCode()
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				null, TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), _draftLayout.getPlid(),
				WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			_draftLayout.getExternalReferenceCode() + "_v_" +
				layoutContentVersion.getVersion(),
			layoutContentVersion.getExternalReferenceCode());
	}

	private void _testAddLayoutContentVersionWithNullNameMap()
		throws Exception {

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(), null, _draftLayout.getPlid(),
				WorkflowConstants.STATUS_DRAFT);

		Assert.assertEquals(
			_draftLayout.getNameMap(), layoutContentVersion.getNameMap());
	}

	private void _testAddLayoutContentVersionWithUnsupportedLayout()
		throws Exception {

		_testAddLayoutContentVersionWithUnsupportedLayout(
			_draftLayout.getClassPK());

		Layout portletLayout = LayoutTestUtil.addTypePortletLayout(_group);

		_testAddLayoutContentVersionWithUnsupportedLayout(
			portletLayout.getPlid());

		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				_group.getGroupId());

		_testAddLayoutContentVersionWithUnsupportedLayout(
			displayPageLayoutPageTemplateEntry.getPlid());

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				_group.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		_testAddLayoutContentVersionWithUnsupportedLayout(
			masterLayoutPageTemplateEntry.getPlid());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0, 0,
				false, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), null,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_testAddLayoutContentVersionWithUnsupportedLayout(
			layoutUtilityPageEntry.getPlid());
	}

	private void _testAddLayoutContentVersionWithUnsupportedLayout(long plid) {
		Assert.assertThrows(
			UnsupportedLayoutLayoutContentVersionException.class,
			() -> _layoutContentVersionLocalService.addLayoutContentVersion(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), plid,
				WorkflowConstants.STATUS_DRAFT));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}