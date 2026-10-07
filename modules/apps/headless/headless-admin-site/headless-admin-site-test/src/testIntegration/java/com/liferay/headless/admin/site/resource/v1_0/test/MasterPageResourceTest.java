/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.client.custom.field.CustomField;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.MasterPageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.AssetTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FileEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageExperiencesTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailHttpServer;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.URLFetchSecurityCompanyConfigurationUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class MasterPageResourceTest extends BaseMasterPageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseMasterPageResourceTestCase.setUpClass();

		_thumbnailHttpServer = ThumbnailHttpServer.start(
			MasterPageResourceTest.class);

		_thumbnail1Base64 = _thumbnailHttpServer.getThumbnail1Base64();
		_thumbnail1Bytes = _thumbnailHttpServer.getThumbnail1Bytes();
		_thumbnail1URL = _thumbnailHttpServer.getThumbnail1URL();
		_thumbnail2Base64 = _thumbnailHttpServer.getThumbnail2Base64();
		_thumbnail2Bytes = _thumbnailHttpServer.getThumbnail2Bytes();
		_thumbnail2URL = _thumbnailHttpServer.getThumbnail2URL();
	}

	@AfterClass
	public static void tearDownClass() {
		if (_thumbnailHttpServer != null) {
			_thumbnailHttpServer.stop();
		}
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteMasterPage() throws Exception {
		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postMasterPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		masterPageResource.deleteSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postMasterPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> masterPageResource.deleteSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				postMasterPage.getExternalReferenceCode()));

		MasterPage liveGroupMasterPage =
			testGetSiteMasterPagesPage_addMasterPage(
				irrelevantGroup.getExternalReferenceCode(),
				_randomMasterPage(irrelevantGroup));

		_enableLocalStaging(irrelevantGroup);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> masterPageResource.deleteSiteMasterPage(
				irrelevantGroup.getExternalReferenceCode(),
				liveGroupMasterPage.getExternalReferenceCode()));
	}

	@Override
	@Test
	@TestInfo("LPD-84480")
	public void testGetSiteMasterPage() throws Exception {
		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_testGetSiteMasterPage(masterPage);
		_testGetSiteMasterPageWithNestedFields(masterPage);

		_testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> masterPageResource.getSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteMasterPage(masterPage);
	}

	@Override
	@Test
	public void testGetSiteMasterPagesPage() throws Exception {
		super.testGetSiteMasterPagesPage();

		MasterPage randomMasterPage = randomMasterPage();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		randomMasterPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage);

		MasterPageResource masterPageResource = _getMasterPageResource();

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
					testGroup.getExternalReferenceCode(),
					postMasterPage.getExternalReferenceCode());

				ThumbnailURLReference thumbnailURLReference =
					getMasterPage.getThumbnailURLReference();

				Assert.assertNotNull(thumbnailURLReference);

				return thumbnailURLReference.getUrl();
			});

		Page<MasterPage> page = masterPageResource.getSiteMasterPagesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		for (MasterPage masterPage : page.getItems()) {
			if (StringUtil.equals(
					masterPage.getExternalReferenceCode(),
					postMasterPage.getExternalReferenceCode())) {

				ThumbnailURLReference thumbnail =
					masterPage.getThumbnailURLReference();

				_assertThumbnailURLReference(
					_thumbnail1Bytes, thumbnail.getExternalReferenceCode(),
					masterPage);
			}
			else {
				Assert.assertNull(masterPage.getThumbnailURLReference());
			}
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteMasterPagesPageWithSortDateTime() throws Exception {
		super.testGetSiteMasterPagesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteMasterPagesPageWithSortDouble() throws Exception {
		super.testGetSiteMasterPagesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteMasterPagesPageWithSortInteger() throws Exception {
		super.testGetSiteMasterPagesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteMasterPagesPageWithSortString() throws Exception {
		super.testGetSiteMasterPagesPageWithSortString();
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPatchSiteMasterPage() throws Exception {
		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_updateLayoutPageTemplateEntryStatus(
			masterPage.getExternalReferenceCode());

		String thumbnailURL = RandomTestUtil.randomString();

		_testPatchSiteMasterPage(
			Boolean.TRUE,
			_getMasterPage(
				Boolean.TRUE, masterPage.getExternalReferenceCode(), null,
				thumbnailURL));

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		_testPatchSiteMasterPage(
			Boolean.TRUE,
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode(), thumbnailURL));

		fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		_testPatchSiteMasterPage(
			Boolean.FALSE,
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode(), thumbnailURL));

		_testPatchSiteMasterPage(
			Boolean.FALSE,
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				StringPool.BLANK, StringPool.BLANK));

		_testPatchSiteMasterPageWithPageSpecifications();
		_testPatchSiteMasterPageWithThumbnail();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> masterPageResource.patchSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomMasterPage()));

		MasterPage liveGroupMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> masterPageResource.patchSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				liveGroupMasterPage.getExternalReferenceCode(),
				_getMasterPage(
					null, liveGroupMasterPage.getExternalReferenceCode(), null,
					thumbnailURL)));
	}

	@Override
	@Test
	@TestInfo({"LPD-92443", "LPD-107149"})
	public void testPostSiteMasterPage() throws Exception {
		super.testPostSiteMasterPage();

		MasterPage masterPage = randomMasterPage();

		masterPage.setKey(StringPool.BLANK);

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		Assert.assertTrue(Validator.isNotNull(postMasterPage.getKey()));
		Assert.assertFalse(postMasterPage.getMarkedAsDefault());
		Assert.assertNull(postMasterPage.getThumbnailURLReference());

		_testPostSiteMasterPageWithDropZonePageElement();
		_testPostSiteMasterPageWithPageSpecifications();
		_testPostSiteMasterPageWithSiteTemplatePageSpecification();
		_testPostSiteMasterPageWithThumbnail();
		_testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64();
		_testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64();
		_testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeNullAndURL();
		_testPostSiteMasterPageWithThumbnailURLReferenceFileBase64();
		_testPostSiteMasterPageWithThumbnailURLReferenceFileBase64AndURL();
		_testPostSiteMasterPageWithThumbnailURLReferenceNonexistingProblemException();
		_testPostSiteMasterPageWithThumbnailURLReferenceURL();
		_testPostSiteMasterPageWithThumbnailURLReferenceURLFetchSecurity();
		_testPostSiteMasterPageWithThumbnailURLReferenceURLUnsupportedProtocolProblemException();
	}

	@Override
	@Test
	public void testPostSiteMasterPagePageSpecification() throws Exception {
		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage masterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			masterPage.getPageSpecifications(), serviceContext,
			contentPageSpecification ->
				masterPageResource.postSiteMasterPagePageSpecification(
					testGroup.getExternalReferenceCode(),
					masterPage.getExternalReferenceCode(),
					contentPageSpecification));

		_assertPostSiteMasterPagePageSpecificationProblemException(
			"The external reference code does not point to a master page",
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteMasterPagePageSpecificationProblemException(
			"The external reference code does not point to a master page",
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext));
	}

	@Override
	@Test
	@TestInfo({"LPD-86817", "LPD-92443"})
	public void testPutSiteMasterPage() throws Exception {
		_testPutSiteMasterPage(randomMasterPage());

		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String thumbnailURL = RandomTestUtil.randomString();

		_testPutSiteMasterPage(
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode(), thumbnailURL));

		_testPutSiteMasterPageWithDropZonePageElement();
		_testPutSiteMasterPageWithPageSpecifications();
		_testPutSiteMasterPageWithThumbnail();
		_testPutSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPutSiteMasterPageWithThumbnailURLReferenceFileBase64();
		_testPutSiteMasterPageWithThumbnailURLReferenceURL();

		_enableLocalStaging();

		_assertStagingGroupMasterPageThumbnail(fileEntry, masterPage);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(),
				_getMasterPage(
					null, masterPage.getExternalReferenceCode(), null,
					thumbnailURL)));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "keywords", "name",
			"taxonomyCategoryBriefs"
		};
	}

	@Override
	protected MasterPage randomIrrelevantMasterPage() throws Exception {
		return new MasterPage() {
			{
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected MasterPage randomMasterPage() throws Exception {
		return _randomMasterPage(testGroup);
	}

	@Override
	protected MasterPage testGetSiteMasterPagesPage_addMasterPage(
			String siteExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		return masterPageResource.postSiteMasterPage(
			siteExternalReferenceCode, masterPage);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteMasterPagesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected MasterPage testPostSiteMasterPage_addMasterPage(
			MasterPage masterPage)
		throws Exception {

		return testGetSiteMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);
	}

	private void _assertContentPageSpecificationPageElements(
		ContentPageSpecification contentPageSpecification, int count) {

		PageExperience[] pageExperiences =
			contentPageSpecification.getPageExperiences();

		Assert.assertEquals(
			Arrays.toString(pageExperiences), 1, pageExperiences.length);

		PageExperience pageExperience = pageExperiences[0];

		PageElement[] pageElements = pageExperience.getPageElements();

		Assert.assertEquals(
			Arrays.toString(pageElements), count, pageElements.length);
	}

	private void _assertPageElements(
		PageElement[] expectedPageElements, MasterPage masterPage) {

		for (PageSpecification pageSpecification :
				masterPage.getPageSpecifications()) {

			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			for (PageExperience pageExperience :
					contentPageSpecification.getPageExperiences()) {

				Assert.assertArrayEquals(
					expectedPageElements, pageExperience.getPageElements());
			}
		}
	}

	private void _assertPageSpecifications(
		int expectedDraftLayoutPageElementsCount,
		int expectedPublishedLayoutPageElementsCount,
		PageSpecification[] pageSpecifications) {

		Assert.assertEquals(
			Arrays.toString(pageSpecifications), 2, pageSpecifications.length);

		_assertContentPageSpecificationPageElements(
			(ContentPageSpecification)pageSpecifications[0],
			expectedPublishedLayoutPageElementsCount);
		_assertContentPageSpecificationPageElements(
			(ContentPageSpecification)pageSpecifications[1],
			expectedDraftLayoutPageElementsCount);
	}

	private void _assertPageSpecifications(
			MasterPage masterPage,
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		PageSpecification.Status status = PageSpecification.Status.APPROVED;

		if (!Objects.equals(
				WorkflowConstants.STATUS_APPROVED,
				layoutPageTemplateEntry.getStatus())) {

			status = PageSpecification.Status.DRAFT;
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			masterPage.getPageSpecifications(), layout, status);
	}

	private void _assertPostSiteMasterPagePageSpecificationProblemException(
			String expectedTitle,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", expectedTitle,
			() -> masterPageResource.postSiteMasterPagePageSpecification(
				testGroup.getExternalReferenceCode(),
				layoutPageTemplateEntry.getExternalReferenceCode(),
				new ContentPageSpecification() {
					{
						setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
					}
				}));
	}

	private void _assertProblemException(
			String expectedStatus, String expectedTitle,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(expectedStatus, problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _assertStagingGroupMasterPageThumbnail(
			FileEntry liveGroupFileEntry, MasterPage masterPage)
		throws Exception {

		Group stagingGroup = testGroup.getStagingGroup();

		FileEntry stagingGroupFileEntry =
			_portletFileRepository.getPortletFileEntryByExternalReferenceCode(
				liveGroupFileEntry.getExternalReferenceCode(),
				stagingGroup.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPage.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		Assert.assertEquals(
			stagingGroupFileEntry.getFileEntryId(),
			layoutPageTemplateEntry.getPreviewFileEntryId());
	}

	private void _assertThumbnailFileEntryId(
			Boolean defaultValue, String masterPageExternalReferenceCode,
			String thumbnailExternalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplate =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPageExternalReferenceCode, testGroup.getGroupId());

		long fileEntryId = 0;

		if (!defaultValue) {
			FileEntry fileEntry =
				_portletFileRepository.
					getPortletFileEntryByExternalReferenceCode(
						thumbnailExternalReferenceCode, testGroup.getGroupId());

			fileEntryId = fileEntry.getFileEntryId();
		}

		Assert.assertEquals(
			layoutPageTemplate.getPreviewFileEntryId(), fileEntryId);
	}

	private void _assertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			MasterPage masterPage)
		throws Exception {

		Assert.assertNotNull(expectedExternalReferenceCode);

		ThumbnailURLReference thumbnailURLReference =
			masterPage.getThumbnailURLReference();

		Assert.assertEquals(
			expectedExternalReferenceCode,
			thumbnailURLReference.getExternalReferenceCode());

		_assertThumbnailFileEntryId(
			false, masterPage.getExternalReferenceCode(),
			expectedExternalReferenceCode);

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					expectedExternalReferenceCode, testGroup.getGroupId());

		try (InputStream inputStream = fileEntry.getContentStream()) {
			Assert.assertArrayEquals(
				expectedBytes, StreamUtil.toByteArray(inputStream));
		}

		MasterPageResource masterPageResource = _getMasterPageResource();

		URL url = new URL(
			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
				() -> {
					MasterPage getMasterPage =
						masterPageResource.getSiteMasterPage(
							testGroup.getExternalReferenceCode(),
							masterPage.getExternalReferenceCode());

					ThumbnailURLReference getThumbnailURLReference =
						getMasterPage.getThumbnailURLReference();

					Assert.assertNotNull(getThumbnailURLReference);

					return getThumbnailURLReference.getUrl();
				}));

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		Assert.assertEquals(
			HttpURLConnection.HTTP_OK, httpURLConnection.getResponseCode());

		String contentType = httpURLConnection.getContentType();

		Assert.assertTrue(contentType.startsWith("image/"));
	}

	private void _enableLocalStaging() throws Exception {
		_enableLocalStaging(testGroup);
	}

	private void _enableLocalStaging(Group group) throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.OFF)) {

			_stagingLocalService.enableLocalStaging(
				TestPropsValues.getUserId(), group, true, false,
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));
		}

		Assert.assertTrue(group.hasStagingGroup());
	}

	private PageElement _getContainerPageElement() {
		PageElement pageElement = new PageElement();

		pageElement.setExternalReferenceCode(
			() -> StringUtil.toLowerCase(RandomTestUtil.randomString()));

		ContainerPageElementDefinition containerPageElementDefinition =
			new ContainerPageElementDefinition();

		containerPageElementDefinition.setIndexed(Boolean.FALSE);
		containerPageElementDefinition.setLayout(
			() -> new com.liferay.headless.admin.site.client.dto.v1_0.Layout() {
				{
					setContentDisplay(ContentDisplay.FLEX_ROW);
				}
			});
		containerPageElementDefinition.setType(
			PageElementDefinition.Type.CONTAINER);

		pageElement.setPageElementDefinition(containerPageElementDefinition);

		pageElement.setPageElements(new PageElement[0]);
		pageElement.setParentExternalReferenceCode(StringPool.BLANK);
		pageElement.setPosition(1);

		return pageElement;
	}

	private MasterPage _getMasterPage(
			Boolean markedAsDefault, String masterPageExternalReferenceCode,
			String thumbnailExternalReferenceCode, String thumbnailURL)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		masterPage.setExternalReferenceCode(masterPageExternalReferenceCode);
		masterPage.setMarkedAsDefault(markedAsDefault);

		if (thumbnailExternalReferenceCode != null) {
			masterPage.setThumbnailURLReference(
				() -> new ThumbnailURLReference() {
					{
						setExternalReferenceCode(
							thumbnailExternalReferenceCode);
						setUrl(() -> thumbnailURL);
					}
				});
		}

		return masterPage;
	}

	private MasterPage _getMasterPage(PageElement[] pageElements)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(),
				PageSpecification.Status.APPROVED);

		draftContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				pageElements, RandomTestUtil.randomString()));

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.setExternalReferenceCode(
			masterPage.getExternalReferenceCode());
		publishedContentPageSpecification.setPageExperiences(
			PageExperiencesTestUtil.getDefaultPageExperiences(
				pageElements, masterPage.getExternalReferenceCode()));

		masterPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		return masterPage;
	}

	private MasterPageResource _getMasterPageResource() throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return MasterPageResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "pageSpecifications,thumbnailURLReference"
		).build();
	}

	private MasterPage _postMasterPageWithPageSpecificationsWithCustomFields()
		throws Exception {

		MasterPage randomMasterPage = randomMasterPage();

		PageSpecification[] pageSpecifications =
			PageSpecificationsTestUtil.getContentPageSpecifications(
				RandomTestUtil.randomString(), testGroup.getGroupId());

		randomMasterPage.setPageSpecifications(pageSpecifications);

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage);

		PageSpecificationsTestUtil.assertCustomFields(
			TransformUtil.transform(
				pageSpecifications,
				pageSpecification -> pageSpecification.getCustomFields(),
				CustomField[].class),
			testGroup.getGroupId(), postMasterPage.getPageSpecifications());

		return postMasterPage;
	}

	private MasterPage _postSiteMasterPageAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage postMasterPage =
			URLFetchSecurityCompanyConfigurationUtil.
				swapURLLocalNetworkAccessEnabled(
					true,
					() -> masterPageResource.postSiteMasterPage(
						testGroup.getExternalReferenceCode(), masterPage));

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference postThumbnailURLReference =
				postMasterPage.getThumbnailURLReference();

			expectedExternalReferenceCode =
				postThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, postMasterPage);

		return postMasterPage;
	}

	private MasterPage _putSiteMasterPageAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			MasterPage masterPage, ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage putMasterPage =
			URLFetchSecurityCompanyConfigurationUtil.
				swapURLLocalNetworkAccessEnabled(
					true,
					() -> masterPageResource.putSiteMasterPage(
						testGroup.getExternalReferenceCode(),
						masterPage.getExternalReferenceCode(), masterPage));

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference putThumbnailURLReference =
				putMasterPage.getThumbnailURLReference();

			expectedExternalReferenceCode =
				putThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, putMasterPage);

		return putMasterPage;
	}

	private MasterPage _randomMasterPage(Group group) throws Exception {
		MasterPage masterPage = super.randomMasterPage();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		masterPage.setKeywords(AssetTestUtil.randomKeywords(serviceContext));

		masterPage.setMarkedAsDefault(Boolean.FALSE);
		masterPage.setTaxonomyCategoryBriefs(
			AssetTestUtil.randomTaxonomyCategoryBriefs(
				testCompany.getGroupId(), serviceContext));

		return masterPage;
	}

	private MasterPage _randomMasterPage(
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		return masterPage;
	}

	private void _testGetSiteMasterPage(MasterPage masterPage)
		throws Exception {

		MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode());

		assertEquals(masterPage, getMasterPage);
		assertValid(getMasterPage);
	}

	private void _testGetSiteMasterPageWithNestedFields(MasterPage masterPage)
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage getMasterPage = masterPageResource.getSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode());

		assertEquals(masterPage, getMasterPage);
		assertValid(getMasterPage);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			getMasterPage.getPageSpecifications());
	}

	private void _testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink()
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				ServiceContextTestUtil.getServiceContext(
					testGroup, TestPropsValues.getUserId()),
				WorkflowConstants.STATUS_APPROVED);

		MasterPageResource masterPageResource = _getMasterPageResource();

		_testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink(
			1, 1, layoutPageTemplateEntry.getExternalReferenceCode(),
			masterPageResource, testGroup.getExternalReferenceCode());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntryLink fragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", draftLayout,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

		_testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink(
			2, 1, layoutPageTemplateEntry.getExternalReferenceCode(),
			masterPageResource, testGroup.getExternalReferenceCode());

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);

		_testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink(
			1, 1, layoutPageTemplateEntry.getExternalReferenceCode(),
			masterPageResource, testGroup.getExternalReferenceCode(),
			fragmentEntryLink.getFragmentEntryLinkId());
	}

	private void
			_testGetSiteMasterPageWithNestedFieldsAndMissingFragmentEntryLink(
				int expectedDraftLayoutPageElementsCount,
				int expectedPublishedLayoutPageElementsCount,
				String masterPageExternalReferenceCode,
				MasterPageResource masterPageResource,
				String siteExternalReferenceCode,
				long... missingFragmentEntryLinkIds)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.dto.v1_0.converter." +
					"FragmentInstancePageElementDefinitionDTOConverter",
				LoggerTestUtil.WARN)) {

			MasterPage masterPage = masterPageResource.getSiteMasterPage(
				siteExternalReferenceCode, masterPageExternalReferenceCode);

			_assertPageSpecifications(
				expectedDraftLayoutPageElementsCount,
				expectedPublishedLayoutPageElementsCount,
				masterPage.getPageSpecifications());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(
				logEntries.toString(), missingFragmentEntryLinkIds.length,
				logEntries.size());

			for (int i = 0; i < missingFragmentEntryLinkIds.length; i++) {
				LogEntry logEntry = logEntries.get(i);

				Assert.assertEquals(
					"No fragment entry link exists with ID " +
						missingFragmentEntryLinkIds[i],
					logEntry.getMessage());
			}
		}
	}

	private void _testPatchSiteMasterPage(
			Boolean expectedMarkedAsDefault, MasterPage masterPage)
		throws Exception {

		MasterPage patchMasterPage = masterPageResource.patchSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode(), masterPage);

		assertEquals(masterPage, patchMasterPage);
		assertValid(patchMasterPage);

		Assert.assertEquals(
			expectedMarkedAsDefault, patchMasterPage.getMarkedAsDefault());
	}

	private void _testPatchSiteMasterPageWithPageSpecifications()
		throws Exception {

		_testPatchSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPatchSiteMasterPageWithPageSpecificationsWithCustomFields();
	}

	private void _testPatchSiteMasterPageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		masterPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		_assertPageSpecifications(
			postMasterPage, draftContentPageSpecification,
			publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			masterPageResource.patchSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(),
				new MasterPage() {
					{
						setPageSpecifications(
							() -> new PageSpecification[] {
								publishedContentPageSpecification,
								draftContentPageSpecification
							});
					}
				}),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPatchSiteMasterPageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			MasterPage postMasterPage =
				_postMasterPageWithPageSpecificationsWithCustomFields();

			MasterPageResource masterPageResource = _getMasterPageResource();

			PageSpecification[] patchPageSpecifications =
				PageSpecificationsTestUtil.getPatchPageSpecifications(
					postMasterPage.getPageSpecifications(),
					testGroup.getGroupId());

			MasterPage patchMasterPage = masterPageResource.patchSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				postMasterPage.getExternalReferenceCode(),
				new MasterPage() {
					{
						setExternalReferenceCode(
							postMasterPage::getExternalReferenceCode);
						setPageSpecifications(patchPageSpecifications);
					}
				});

			PageSpecificationsTestUtil.assertCustomFields(
				TransformUtil.transform(
					patchPageSpecifications,
					pageSpecification -> pageSpecification.getCustomFields(),
					CustomField[].class),
				testGroup.getGroupId(),
				patchMasterPage.getPageSpecifications());
		}
	}

	private void _testPatchSiteMasterPageWithThumbnail() throws Exception {
		MasterPage masterPage1 = randomMasterPage();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		masterPage1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage1);

		Assert.assertEquals(masterPage1.getKey(), postMasterPage.getKey());

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postMasterPage);

		FileEntry newFileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		masterPage1.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				newFileEntry, null));

		MasterPage patchMasterPage = masterPageResource.patchSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage1.getExternalReferenceCode(), masterPage1);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, newFileEntry.getExternalReferenceCode(),
			patchMasterPage);

		MasterPage masterPage2 = randomMasterPage();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		masterPage2.setThumbnailURLReference(thumbnailURLReference);

		try {
			masterPageResource.patchSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage1.getExternalReferenceCode(), masterPage2);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl() +
						" because of restricted host invalid.example.test",
				problem.getTitle());
		}
	}

	private void _testPostMasterPageThumbnailURLReferenceProblemException(
			String expectedTitle, String externalReferenceCode, String url)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(url);

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		MasterPageResource masterPageResource = _getMasterPageResource();

		try {
			masterPageResource.postSiteMasterPage(
				testGroup.getExternalReferenceCode(), masterPage);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void _testPostSiteMasterPageWithDropZonePageElement()
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		PageElement[] pageElements = {
			PageElementsTestUtil.getDropZonePageElement(
				RandomTestUtil.randomString(), testGroup.getGroupId())
		};

		MasterPage masterPage = _getMasterPage(pageElements);

		_assertPageElements(
			pageElements,
			masterPageResource.postSiteMasterPage(
				testGroup.getExternalReferenceCode(), masterPage));
	}

	private void _testPostSiteMasterPageWithPageSpecifications()
		throws Exception {

		_testPostSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
		_testPostSiteMasterPageWithPageSpecificationsWithCustomFields();
	}

	private void _testPostSiteMasterPageWithPageSpecifications(
			PageSpecification.Status draftLayoutStatus,
			PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), publishedLayoutStatus);

		masterPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		MasterPageResource masterPageResource = _getMasterPageResource();

		_assertPageSpecifications(
			masterPageResource.postSiteMasterPage(
				testGroup.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteMasterPageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			_postMasterPageWithPageSpecificationsWithCustomFields();
		}
	}

	private void _testPostSiteMasterPageWithSiteTemplatePageSpecification()
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		Group group = GroupTestUtil.addGroup();

		LayoutSetPrototype layoutSetPrototype =
			LayoutTestUtil.addLayoutSetPrototype(RandomTestUtil.randomString());

		MergeLayoutPrototypesThreadLocal.clearMergeComplete();

		_sites.updateLayoutSetPrototypesLinks(
			group, 0, layoutSetPrototype.getLayoutSetPrototypeId(), true, true);

		MasterPage masterPage = super.randomMasterPage();

		masterPage.setMarkedAsDefault(Boolean.FALSE);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(),
				PageSpecification.Status.APPROVED);

		Layout layout =
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(
					ServiceContextTestUtil.getServiceContext(
						layoutSetPrototype.getGroupId()));

		Layout draftLayout = layout.fetchDraftLayout();

		draftContentPageSpecification.
			setSiteTemplatePageSpecificationExternalReferenceCode(
				draftLayout.getExternalReferenceCode());

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.
			setSiteTemplatePageSpecificationExternalReferenceCode(
				layout.getExternalReferenceCode());

		masterPage.setPageSpecifications(
			() -> new PageSpecification[] {
				draftContentPageSpecification, publishedContentPageSpecification
			});

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			group.getExternalReferenceCode(), masterPage);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					postMasterPage.getExternalReferenceCode(),
					group.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			postMasterPage.getPageSpecifications(),
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			PageSpecification.Status.APPROVED);
	}

	private void _testPostSiteMasterPageWithThumbnail() throws Exception {
		MasterPage masterPage = randomMasterPage();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		masterPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		Assert.assertEquals(masterPage.getKey(), postMasterPage.getKey());

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postMasterPage);

		masterPage = randomMasterPage();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		try {
			testPostSiteMasterPage_addMasterPage(masterPage);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl() +
						" because of restricted host invalid.example.test",
				problem.getTitle());
		}
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(StringPool.BLANK);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeNullAndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceFileBase64()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceFileBase64AndURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);
		thumbnailURLReference.setUrl(_thumbnail2URL);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceNonexistingProblemException()
		throws Exception {

		_testPostMasterPageThumbnailURLReferenceProblemException(
			"Unable to resolve file", RandomTestUtil.randomString(), null);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceURLFetchSecurity()
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		ThumbnailURLReferenceTestUtil.testThumbnailURLReferenceURLFetchSecurity(
			(thumbnailURLReference, masterPage) -> _assertThumbnailURLReference(
				_thumbnail1URL.equals(thumbnailURLReference.getUrl()) ?
					_thumbnail1Bytes : _thumbnail2Bytes,
				thumbnailURLReference.getExternalReferenceCode(), masterPage),
			_randomMasterPage(
				ThumbnailURLReferenceTestUtil.getThumbnailURLReference(
					_thumbnail1URL)),
			_randomMasterPage(
				ThumbnailURLReferenceTestUtil.getThumbnailURLReference(
					_thumbnail2URL)),
			MasterPage::getThumbnailURLReference,
			masterPage -> masterPageResource.postSiteMasterPage(
				testGroup.getExternalReferenceCode(), masterPage));
	}

	private void _testPostSiteMasterPageWithThumbnailURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostMasterPageThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			RandomTestUtil.randomString(), url);
	}

	private void _testPutSiteMasterPage(MasterPage masterPage)
		throws Exception {

		masterPage.setMarkedAsDefault(Boolean.TRUE);

		_assertProblemException(
			"CONFLICT", "The default master page must be published first.",
			() -> masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage));

		masterPage.setMarkedAsDefault(Boolean.FALSE);

		MasterPage putMasterPage = masterPageResource.putSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode(), masterPage);

		assertEquals(masterPage, putMasterPage);
		assertValid(putMasterPage);
	}

	private void _testPutSiteMasterPageWithDropZonePageElement()
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		PageElement[] pageElements = {
			PageElementsTestUtil.getDropZonePageElement(
				RandomTestUtil.randomString(), testGroup.getGroupId())
		};

		MasterPage masterPage = _getMasterPage(pageElements);

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		postMasterPage.setDateModified(new Date());

		_assertPageElements(
			pageElements,
			masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				postMasterPage.getExternalReferenceCode(), masterPage));
	}

	private void _testPutSiteMasterPageWithPageSpecifications()
		throws Exception {

		_testPutSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPutSiteMasterPageWithPageSpecificationsWithCustomFieldsAndPageElements();
	}

	private void _testPutSiteMasterPageWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		masterPage.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		MasterPageResource masterPageResource = _getMasterPageResource();

		_assertPageSpecifications(
			masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPutSiteMasterPageWithPageSpecificationsWithCustomFieldsAndPageElements()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			MasterPage postMasterPage =
				_postMasterPageWithPageSpecificationsWithCustomFields();

			MasterPageResource masterPageResource = _getMasterPageResource();

			MasterPage putMasterPage = postMasterPage;

			PageElement[] pageElements = {
				PageElementsTestUtil.getDropZonePageElement(
					RandomTestUtil.randomString(), testGroup.getGroupId()),
				_getContainerPageElement()
			};

			putMasterPage.setPageSpecifications(
				() -> TransformUtil.transform(
					putMasterPage.getPageSpecifications(),
					pageSpecification -> {
						pageSpecification.setCustomFields(
							PageSpecificationsTestUtil.getCustomFields());

						ContentPageSpecification contentPageSpecification =
							(ContentPageSpecification)pageSpecification;

						for (PageExperience pageExperience :
								contentPageSpecification.getPageExperiences()) {

							pageExperience.setPageElements(pageElements);
						}

						return contentPageSpecification;
					},
					PageSpecification.class));

			MasterPage updateMasterPage = masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				postMasterPage.getExternalReferenceCode(), putMasterPage);

			PageSpecificationsTestUtil.assertCustomFields(
				TransformUtil.transform(
					putMasterPage.getPageSpecifications(),
					pageSpecification -> pageSpecification.getCustomFields(),
					CustomField[].class),
				testGroup.getGroupId(),
				updateMasterPage.getPageSpecifications());

			_assertPageElements(pageElements, updateMasterPage);
		}
	}

	private void _testPutSiteMasterPageWithThumbnail() throws Exception {
		MasterPage masterPage = randomMasterPage();

		masterPage.setExternalReferenceCode(RandomTestUtil.randomString());

		FileEntry fileEntry1 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		masterPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry1, null));

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage putMasterPage = masterPageResource.putSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode(), masterPage);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry1.getExternalReferenceCode(),
			putMasterPage);

		FileEntry fileEntry2 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		putMasterPage.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry2, null));

		putMasterPage = masterPageResource.putSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			putMasterPage.getExternalReferenceCode(), putMasterPage);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry2.getExternalReferenceCode(),
			putMasterPage);

		putMasterPage.setThumbnailURLReference(() -> null);

		putMasterPage = masterPageResource.putSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			putMasterPage.getExternalReferenceCode(), putMasterPage);

		_assertThumbnailFileEntryId(
			true, putMasterPage.getExternalReferenceCode(), null);

		masterPage = randomMasterPage();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		masterPage.setThumbnailURLReference(thumbnailURLReference);

		try {
			masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				putMasterPage.getExternalReferenceCode(), masterPage);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl() +
						" because of restricted host invalid.example.test",
				problem.getTitle());
		}
	}

	private void _testPutSiteMasterPageWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		Assert.assertNull(postMasterPage.getThumbnailURLReference());

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_putSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, postMasterPage,
			thumbnailURLReference);
	}

	private void _testPutSiteMasterPageWithThumbnailURLReferenceFileBase64()
		throws Exception {

		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		Assert.assertNull(postMasterPage.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setFileBase64(_thumbnail1Base64);

		MasterPage putMasterPage =
			_putSiteMasterPageAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postMasterPage, thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setFileBase64(_thumbnail2Base64);

		_putSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putMasterPage, thumbnailURLReference2);
	}

	private void _testPutSiteMasterPageWithThumbnailURLReferenceURL()
		throws Exception {

		MasterPage postMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		Assert.assertNull(postMasterPage.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setUrl(_thumbnail1URL);

		MasterPage putMasterPage =
			_putSiteMasterPageAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postMasterPage, thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setUrl(_thumbnail2URL);

		_putSiteMasterPageAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putMasterPage, thumbnailURLReference2);
	}

	private void _updateLayoutPageTemplateEntryStatus(
			String externalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, testGroup.getGroupId());

		_layoutPageTemplateEntryLocalService.updateStatus(
			TestPropsValues.getUserId(),
			layoutPageTemplateEntry.getLayoutPageTemplateEntryId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	private static String _thumbnail1Base64;
	private static byte[] _thumbnail1Bytes;
	private static String _thumbnail1URL;
	private static String _thumbnail2Base64;
	private static byte[] _thumbnail2Bytes;
	private static String _thumbnail2URL;
	private static ThumbnailHttpServer _thumbnailHttpServer;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private Sites _sites;

	@Inject
	private StagingLocalService _stagingLocalService;

}