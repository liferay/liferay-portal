/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersionPageExperience;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.PageSpecificationVersionResource;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-10622")
@RunWith(Arquillian.class)
public class PageSpecificationVersionResourceTest
	extends BasePageSpecificationVersionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_irrelevantGroupLayout = LayoutTestUtil.addTypeContentLayout(
			irrelevantGroup);
		_testGroupLayout = LayoutTestUtil.addTypeContentLayout(testGroup);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	@TestInfo("LPD-90029")
	public void testDeleteSiteSitePagePageSpecificationVersion()
		throws Exception {

		super.testDeleteSiteSitePagePageSpecificationVersion();

		_testDeleteSiteSitePagePageSpecificationVersionLatestApproved();
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationVersion() throws Exception {
		super.testGetSiteSitePagePageSpecificationVersion();

		_testGetSiteSitePagePageSpecificationVersionActions();
		_testGetSiteSitePagePageSpecificationVersionMismatchedSitePage();
		_testGetSiteSitePagePageSpecificationVersionPageSpecificationNestedField();
		_testGetSiteSitePagePageSpecificationVersionWithPageSpecificationVersionPageExperiences();
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationVersionsPage()
		throws Exception {

		super.testGetSiteSitePagePageSpecificationVersionsPage();
	}

	@Override
	@Test
	@TestInfo({"LPD-90200", "LPD-102622"})
	public void testPostSiteSitePagePageSpecificationVersionRestore()
		throws Exception {

		super.testPostSiteSitePagePageSpecificationVersionRestore();

		_testPostSiteSitePagePageSpecificationVersionRestore();
		_testPostSiteSitePagePageSpecificationVersionRestoreMismatchedSitePage();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"externalReferenceCode", "name"};
	}

	@Override
	protected PageSpecificationVersion
			testDeleteSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion(WorkflowConstants.STATUS_DRAFT);
	}

	@Override
	protected String
			testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		return _addPageSpecificationVersion(
			siteExternalReferenceCode, sitePageExternalReferenceCode,
			pageSpecificationVersion);
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSitePageExternalReferenceCode()
		throws Exception {

		return _irrelevantGroupLayout.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	private PageSpecificationVersion _addPageSpecificationVersion()
		throws Exception {

		return _addPageSpecificationVersion(WorkflowConstants.STATUS_APPROVED);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			Group group, Layout layout,
			PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		return _addPageSpecificationVersion(
			pageSpecificationVersion.getExternalReferenceCode(), group, layout,
			pageSpecificationVersion.getName(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(int status)
		throws Exception {

		return _addPageSpecificationVersion(
			RandomTestUtil.randomString(), testGroup, _testGroupLayout,
			RandomTestUtil.randomString(), status);
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			String externalReferenceCode, Group group, Layout layout,
			String name, int status)
		throws Exception {

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				externalReferenceCode, TestPropsValues.getUserId(),
				_layoutContentVersionDataProvider.getLayoutContentVersionData(
					draftLayout,
					ServiceContextTestUtil.getServiceContext(
						group.getGroupId())),
				HashMapBuilder.put(
					LocaleUtil.getSiteDefault(), name
				).build(),
				draftLayout.getPlid(), status);

		if (Validator.isNotNull(externalReferenceCode)) {
			Assert.assertEquals(
				externalReferenceCode,
				layoutContentVersion.getExternalReferenceCode());
		}

		return pageSpecificationVersionResource.
			getSiteSitePagePageSpecificationVersion(
				group.getExternalReferenceCode(),
				layout.getExternalReferenceCode(),
				layoutContentVersion.getExternalReferenceCode());
	}

	private PageSpecificationVersion _addPageSpecificationVersion(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			siteExternalReferenceCode, testCompany.getCompanyId());

		return _addPageSpecificationVersion(
			group,
			_layoutLocalService.getLayoutByExternalReferenceCode(
				sitePageExternalReferenceCode, group.getGroupId()),
			pageSpecificationVersion);
	}

	private List<SegmentsExperience> _addSegmentsExperiences(
			int count, Layout layout)
		throws Exception {

		List<SegmentsExperience> segmentsExperiences = new ArrayList<>();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(layout.getGroupId());

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		for (int i = 0; i < count; i++) {
			SegmentsExperience segmentsExperience =
				SegmentsTestUtil.addSegmentsExperience(
					layout.getGroupId(), layout.getPlid());

			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
				_layoutPageTemplateStructureRelLocalService.
					fetchLayoutPageTemplateStructureRel(
						layoutPageTemplateStructure.
							getLayoutPageTemplateStructureId(),
						segmentsExperience.getSegmentsExperienceId());

			if (layoutPageTemplateStructureRel == null) {
				_layoutPageTemplateStructureRelLocalService.
					addLayoutPageTemplateStructureRel(
						PrincipalThreadLocal.getUserId(), layout.getGroupId(),
						layoutPageTemplateStructure.
							getLayoutPageTemplateStructureId(),
						segmentsExperience.getSegmentsExperienceId(),
						layoutPageTemplateStructure.
							getDefaultSegmentsExperienceData(),
						serviceContext);
			}

			segmentsExperiences.add(segmentsExperience);
		}

		return segmentsExperiences;
	}

	private void _assertActionHref(
		PageSpecificationVersion pageSpecificationVersion, String... keys) {

		Map<String, Map<String, String>> actions =
			pageSpecificationVersion.getActions();

		String prefix = StringBundler.concat(
			"/sites/", testGroup.getExternalReferenceCode(), "/site-pages/",
			_testGroupLayout.getExternalReferenceCode(),
			"/page-specification-versions/",
			pageSpecificationVersion.getExternalReferenceCode());

		for (String key : keys) {
			Map<String, String> action = actions.get(key);

			String href = action.get("href");

			String content = prefix;

			if (key.equals("restore")) {
				content = prefix + "/restore";
			}

			Assert.assertTrue(key, href.contains(content));
		}
	}

	private void _assertFragmentEntryLinks(
		int count, long plid, long defaultSegmentsExperienceId) {

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					_testGroupLayout.getGroupId(), defaultSegmentsExperienceId,
					plid, false);

		Assert.assertEquals(
			fragmentEntryLinks.toString(), count, fragmentEntryLinks.size());
	}

	private void _assertPageExperiencePageElements(
		int count, ContentPageSpecification contentPageSpecification) {

		PageElement[] pageElements = _getDefaultPageExperiencePageElements(
			contentPageSpecification);

		Assert.assertEquals(
			Arrays.toString(pageElements), count, pageElements.length);
	}

	private void
			_assertPageSpecificationVersionMismatchedSitePageProblemException(
				PageSpecificationVersion pageSpecificationVersion,
				UnsafeBiConsumer<String, String, Exception> unsafeBiConsumer)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(testGroup);

		Problem.ProblemException problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() -> unsafeBiConsumer.accept(
				layout.getExternalReferenceCode(),
				pageSpecificationVersion.getExternalReferenceCode()));

		Problem problem = problemException.getProblem();

		Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		Assert.assertEquals(
			"The page specification version must belong to the site page",
			problem.getTitle());
	}

	private PageElement[] _getDefaultPageExperiencePageElements(
		ContentPageSpecification contentPageSpecification) {

		PageExperience[] pageExperiences =
			contentPageSpecification.getPageExperiences();

		PageExperience pageExperience = pageExperiences[0];

		return pageExperience.getPageElements();
	}

	private PageSpecificationVersionResource
			_getPageSpecificationVersionResource()
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return PageSpecificationVersionResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields",
			"pageSpecification,pageSpecificationVersionPageExperiences"
		).build();
	}

	private Map<String, SegmentsExperience> _getSegmentsExperiencesMap(
			int count, Layout layout)
		throws Exception {

		Map<String, SegmentsExperience> segmentsExperiencesMap =
			new HashMap<>();

		for (SegmentsExperience segmentsExperience :
				_addSegmentsExperiences(count, layout)) {

			segmentsExperiencesMap.put(
				segmentsExperience.getExternalReferenceCode(),
				segmentsExperience);
		}

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperience(
				layout.getPlid());

		segmentsExperiencesMap.put(
			segmentsExperience.getExternalReferenceCode(), segmentsExperience);

		return segmentsExperiencesMap;
	}

	private void _testDeleteSiteSitePagePageSpecificationVersionLatestApproved()
		throws Exception {

		PageSpecificationVersion pageSpecificationVersion =
			_addPageSpecificationVersion();

		Problem.ProblemException problemException = Assert.assertThrows(
			Problem.ProblemException.class,
			() ->
				pageSpecificationVersionResource.
					deleteSiteSitePagePageSpecificationVersion(
						testGroup.getExternalReferenceCode(),
						_testGroupLayout.getExternalReferenceCode(),
						pageSpecificationVersion.getExternalReferenceCode()));

		Problem problem = problemException.getProblem();

		Assert.assertEquals("CONFLICT", problem.getStatus());
		Assert.assertEquals(
			"The latest approved page specification version is required",
			problem.getTitle());
	}

	private void _testGetSiteSitePagePageSpecificationVersionActions()
		throws Exception {

		PageSpecificationVersion firstPageSpecificationVersion =
			_addPageSpecificationVersion();

		firstPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					firstPageSpecificationVersion.getExternalReferenceCode());

		Map<String, Map<String, String>> firstActions =
			firstPageSpecificationVersion.getActions();

		Assert.assertNull(firstActions.get("delete"));

		_assertActionHref(firstPageSpecificationVersion, "get", "restore");

		PageSpecificationVersion secondPageSpecificationVersion =
			_addPageSpecificationVersion();

		firstPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					firstPageSpecificationVersion.getExternalReferenceCode());

		_assertActionHref(
			firstPageSpecificationVersion, "delete", "get", "restore");

		secondPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					secondPageSpecificationVersion.getExternalReferenceCode());

		Map<String, Map<String, String>> secondActions =
			secondPageSpecificationVersion.getActions();

		Assert.assertNull(secondActions.get("delete"));

		_assertActionHref(secondPageSpecificationVersion, "get", "restore");
	}

	private void _testGetSiteSitePagePageSpecificationVersionMismatchedSitePage()
		throws Exception {

		_assertPageSpecificationVersionMismatchedSitePageProblemException(
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion(),
			(sitePageExternalReferenceCode,
			 pageSpecificationVersionExternalReferenceCode) ->
				pageSpecificationVersionResource.
					getSiteSitePagePageSpecificationVersion(
						testGroup.getExternalReferenceCode(),
						sitePageExternalReferenceCode,
						pageSpecificationVersionExternalReferenceCode));
	}

	private void _testGetSiteSitePagePageSpecificationVersionPageSpecificationNestedField()
		throws Exception {

		PageSpecificationVersion pageSpecificationVersion =
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		PageSpecificationVersion getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

		Assert.assertNull(getPageSpecificationVersion.getPageSpecification());

		PageSpecificationVersionResource pageSpecificationVersionResource =
			_getPageSpecificationVersionResource();

		getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

		LayoutContentVersion layoutContentVersion =
			_layoutContentVersionLocalService.
				getLayoutContentVersionByExternalReferenceCode(
					pageSpecificationVersion.getExternalReferenceCode(),
					testGroup.getGroupId());

		Assert.assertEquals(
			PageSpecification.toDTO(layoutContentVersion.getData()),
			getPageSpecificationVersion.getPageSpecification());
	}

	private void _testGetSiteSitePagePageSpecificationVersionWithPageSpecificationVersionPageExperiences()
		throws Exception {

		Map<String, SegmentsExperience> segmentsExperiencesMap =
			_getSegmentsExperiencesMap(3, _testGroupLayout.fetchDraftLayout());

		PageSpecificationVersion pageSpecificationVersion =
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		PageSpecificationVersionResource pageSpecificationVersionResource =
			_getPageSpecificationVersionResource();

		PageSpecificationVersion getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGroup.getExternalReferenceCode(),
					_testGroupLayout.getExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode());

		PageSpecificationVersionPageExperience[]
			pageSpecificationVersionPageExperiences =
				getPageSpecificationVersion.
					getPageSpecificationVersionPageExperiences();

		String[] expectedAvailablePreviewLanguageIds =
			TransformUtil.transformToArray(
				LanguageUtil.getAvailableLocales(testGroup.getGroupId()),
				locale -> LocaleUtil.toLanguageId(locale), String.class);

		for (PageSpecificationVersionPageExperience
				pageSpecificationVersionPageExperience :
					pageSpecificationVersionPageExperiences) {

			SegmentsExperience segmentsExperience = segmentsExperiencesMap.get(
				pageSpecificationVersionPageExperience.
					getExternalReferenceCode());

			Assert.assertEquals(
				segmentsExperience.getExternalReferenceCode(),
				pageSpecificationVersionPageExperience.
					getExternalReferenceCode());
			Assert.assertEquals(
				LocalizedMapUtil.getI18nMap(
					true, segmentsExperience.getNameMap()),
				pageSpecificationVersionPageExperience.getName_i18n());
			Assert.assertEquals(
				segmentsExperience.getPriority(),
				GetterUtil.getInteger(
					pageSpecificationVersionPageExperience.getPriority()));

			String[] availablePreviewLanguageIds =
				pageSpecificationVersionPageExperience.
					getAvailablePreviewLanguageIds();

			Assert.assertTrue(
				Arrays.toString(availablePreviewLanguageIds),
				ArrayUtil.containsAll(
					availablePreviewLanguageIds,
					expectedAvailablePreviewLanguageIds));
			Assert.assertEquals(
				Arrays.toString(availablePreviewLanguageIds),
				expectedAvailablePreviewLanguageIds.length,
				availablePreviewLanguageIds.length);
		}

		Assert.assertEquals(
			Arrays.toString(pageSpecificationVersionPageExperiences),
			segmentsExperiencesMap.size(),
			pageSpecificationVersionPageExperiences.length);
	}

	private void _testPostSiteSitePagePageSpecificationVersionRestore()
		throws Exception {

		Layout draftLayout = _testGroupLayout.fetchDraftLayout();

		long draftLayoutSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_testGroupLayout.getPlid());

		PageSpecificationVersionResource pageSpecificationVersionResource =
			_getPageSpecificationVersionResource();

		Map<String, PageSpecification> expectedPageSpecifications =
			new HashMap<>();

		int count = 3;

		for (int i = 1; i <= count; i++) {
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", draftLayout, draftLayoutSegmentsExperienceId);

			_assertFragmentEntryLinks(
				i, draftLayout.getPlid(), draftLayoutSegmentsExperienceId);

			ContentLayoutTestUtil.publishLayout(draftLayout, _testGroupLayout);

			_assertFragmentEntryLinks(
				i, _testGroupLayout.getPlid(), segmentsExperienceId);

			LayoutContentVersion layoutContentVersion =
				_layoutContentVersionLocalService.getLayoutContentVersion(
					_layoutContentVersionLocalService.
						getLatestApprovedLayoutContentVersionId(
							draftLayout.getPlid()));

			PageSpecificationVersion pageSpecificationVersion =
				pageSpecificationVersionResource.
					getSiteSitePagePageSpecificationVersion(
						testGroup.getExternalReferenceCode(),
						_testGroupLayout.getExternalReferenceCode(),
						layoutContentVersion.getExternalReferenceCode());

			PageSpecification pageSpecification =
				pageSpecificationVersion.getPageSpecification();

			pageSpecification.setStatus(PageSpecification.Status.DRAFT);

			_assertPageExperiencePageElements(
				i, (ContentPageSpecification)pageSpecification);

			expectedPageSpecifications.put(
				pageSpecificationVersion.getExternalReferenceCode(),
				pageSpecification);
		}

		_assertFragmentEntryLinks(
			count, _testGroupLayout.getPlid(), segmentsExperienceId);

		for (Map.Entry<String, PageSpecification> entry :
				expectedPageSpecifications.entrySet()) {

			PageSpecification restoredPageSpecification =
				pageSpecificationVersionResource.
					postSiteSitePagePageSpecificationVersionRestore(
						testGroup.getExternalReferenceCode(),
						_testGroupLayout.getExternalReferenceCode(),
						entry.getKey());

			PageSpecification pageSpecification = entry.getValue();

			Assert.assertEquals(pageSpecification, restoredPageSpecification);

			_assertFragmentEntryLinks(
				count, _testGroupLayout.getPlid(), segmentsExperienceId);

			PageElement[] pageElements = _getDefaultPageExperiencePageElements(
				(ContentPageSpecification)pageSpecification);

			_assertFragmentEntryLinks(
				pageElements.length, draftLayout.getPlid(),
				draftLayoutSegmentsExperienceId);
		}
	}

	private void _testPostSiteSitePagePageSpecificationVersionRestoreMismatchedSitePage()
		throws Exception {

		_assertPageSpecificationVersionMismatchedSitePageProblemException(
			_addPageSpecificationVersion(),
			(sitePageExternalReferenceCode,
			 pageSpecificationVersionExternalReferenceCode) ->
				pageSpecificationVersionResource.
					postSiteSitePagePageSpecificationVersionRestore(
						testGroup.getExternalReferenceCode(),
						sitePageExternalReferenceCode,
						pageSpecificationVersionExternalReferenceCode));
	}

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _irrelevantGroupLayout;

	@Inject
	private LayoutContentVersionDataProvider _layoutContentVersionDataProvider;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private Layout _testGroupLayout;

}