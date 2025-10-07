/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.headless.admin.site.client.custom.field.CustomField;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.MasterPageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.AssetTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class MasterPageResourceTest extends BaseMasterPageResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		MasterPage liveGroupMasterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> masterPageResource.deleteSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				liveGroupMasterPage.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteMasterPage() throws Exception {
		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_testGetSiteMasterPage(masterPage);
		_testGetSiteMasterPageWithNestedFields(masterPage);

		_assertProblemException(
			"NOT_FOUND", null,
			() -> masterPageResource.getSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteMasterPage(masterPage);
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
	public void testPatchSiteMasterPage() throws Exception {
		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		_updateLayoutPageTemplateEntryStatus(
			masterPage.getExternalReferenceCode());

		_testPatchSiteMasterPage(
			Boolean.TRUE, null,
			_getMasterPage(
				Boolean.TRUE, masterPage.getExternalReferenceCode(), null));

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPatchSiteMasterPage(
			Boolean.TRUE, fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPatchSiteMasterPage(
			Boolean.FALSE, fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		_testPatchSiteMasterPage(
			Boolean.FALSE, null,
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				StringPool.BLANK));

		_testPatchSiteMasterPageWithPageSpecifications();

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
					null, liveGroupMasterPage.getExternalReferenceCode(),
					null)));
	}

	@Override
	@Test
	public void testPostSiteMasterPage() throws Exception {
		super.testPostSiteMasterPage();

		MasterPage masterPage = randomMasterPage();

		masterPage.setKey(StringPool.BLANK);

		MasterPage postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		Assert.assertTrue(Validator.isNotNull(postMasterPage.getKey()));
		Assert.assertFalse(postMasterPage.getMarkedAsDefault());
		Assert.assertNull(postMasterPage.getThumbnail());

		masterPage = randomMasterPage();

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		masterPage.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(FileEntry.class.getName());
					setExternalReferenceCode(
						fileEntry.getExternalReferenceCode());
				}
			});

		postMasterPage = masterPageResource.postSiteMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		Assert.assertEquals(masterPage.getKey(), postMasterPage.getKey());

		_assertThumbnailItemExternalReference(
			fileEntry.getExternalReferenceCode(),
			postMasterPage.getThumbnail());

		_testPostSiteMasterPageWithPageSpecifications();
		_testPostSiteMasterPageWithSiteTemplatePageSpecification();
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
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteMasterPagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext));
	}

	@Override
	@Test
	public void testPutSiteMasterPage() throws Exception {
		_testPutSiteMasterPage(null, randomMasterPage());

		MasterPage masterPage = testPostSiteMasterPage_addMasterPage(
			randomMasterPage());

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPutSiteMasterPage(
			fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		_testPutSiteMasterPageWithPageSpecifications();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> masterPageResource.putSiteMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(),
				_getMasterPage(
					null, masterPage.getExternalReferenceCode(), null)));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "keywords", "name",
			"taxonomyCategoryItemExternalReferences"
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
		MasterPage masterPage = super.randomMasterPage();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId());

		masterPage.setKeywords(AssetTestUtil.randomKeywords(serviceContext));

		masterPage.setMarkedAsDefault(Boolean.FALSE);
		masterPage.setTaxonomyCategoryItemExternalReferences(
			AssetTestUtil.randomTaxonomyCategoryItemExternalReferences(
				testCompany.getGroupId(), serviceContext));

		return masterPage;
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

	private FileEntry _addPortletFileEntry(long folderId) throws Exception {
		Class<?> clazz = getClass();

		return _portletFileRepository.addPortletFileEntry(
			null, testGroup.getGroupId(), TestPropsValues.getUserId(),
			LayoutPageTemplateEntry.class.getName(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			folderId, clazz.getResourceAsStream("dependencies/thumbnail.png"),
			RandomTestUtil.randomString(), ContentTypes.IMAGE_PNG, false);
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
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
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
			String status, String title,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertEquals(title, problem.getTitle());
		}
	}

	private void _assertThumbnailItemExternalReference(
		String expectedExternalReferenceCode,
		ItemExternalReference itemExternalReference) {

		if (expectedExternalReferenceCode != null) {
			Assert.assertEquals(
				FileEntry.class.getName(),
				itemExternalReference.getClassName());
			Assert.assertEquals(
				expectedExternalReferenceCode,
				itemExternalReference.getExternalReferenceCode());
		}
		else {
			Assert.assertNull(itemExternalReference);
		}
	}

	private void _enableLocalStaging() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), testGroup, true, false,
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));
	}

	private MasterPage _getMasterPage(
			Boolean markedAsDefault, String masterPageExternalReferenceCode,
			String thumbnailExternalReferenceCode)
		throws Exception {

		MasterPage masterPage = randomMasterPage();

		masterPage.setExternalReferenceCode(masterPageExternalReferenceCode);
		masterPage.setMarkedAsDefault(markedAsDefault);

		if (thumbnailExternalReferenceCode != null) {
			masterPage.setThumbnail(
				() -> new ItemExternalReference() {
					{
						setClassName(() -> FileEntry.class.getName());
						setExternalReferenceCode(
							thumbnailExternalReferenceCode);
					}
				});
		}

		return masterPage;
	}

	private MasterPageResource _getMasterPageResource() throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return MasterPageResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "pageSpecifications"
		).build();
	}

	private MasterPage _postMasterPageWithPageSpecificationsWithCustomFields()
		throws Exception {

		MasterPage randomMasterPage = randomMasterPage();

		PageSpecification[] pageSpecifications =
			PageSpecificationsTestUtil.getContentPageSpecifications(
				RandomTestUtil.randomString());

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

	private void _testPatchSiteMasterPage(
			Boolean expectedMarkedAsDefault,
			String expectedExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		MasterPage patchMasterPage = masterPageResource.patchSiteMasterPage(
			testGroup.getExternalReferenceCode(),
			masterPage.getExternalReferenceCode(), masterPage);

		assertEquals(masterPage, patchMasterPage);
		assertValid(patchMasterPage);

		Assert.assertEquals(
			expectedMarkedAsDefault, patchMasterPage.getMarkedAsDefault());

		_assertThumbnailItemExternalReference(
			expectedExternalReferenceCode, patchMasterPage.getThumbnail());
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
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

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
					postMasterPage.getPageSpecifications());

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
				null, draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedLayoutStatus);

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
				null, PageSpecification.Status.APPROVED);

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
				PageSpecification.Status.APPROVED);

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

	private void _testPutSiteMasterPage(
			String expectedThumbnailExternalReferenceCode,
			MasterPage masterPage)
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

		_assertThumbnailItemExternalReference(
			expectedThumbnailExternalReferenceCode,
			putMasterPage.getThumbnail());
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
		_testPutSiteMasterPageWithPageSpecificationsWithCustomFields();
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
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

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

	private void _testPutSiteMasterPageWithPageSpecificationsWithCustomFields()
		throws Exception {

		try (PageSpecificationsTestUtil.ExpandoTableAutocloseable
				expandoTableAutoCloseable =
					PageSpecificationsTestUtil.getExpandoTableAutoCloseable()) {

			MasterPage postMasterPage =
				_postMasterPageWithPageSpecificationsWithCustomFields();

			MasterPageResource masterPageResource = _getMasterPageResource();

			MasterPage putMasterPage = postMasterPage;

			putMasterPage.setPageSpecifications(
				() -> TransformUtil.transform(
					putMasterPage.getPageSpecifications(),
					pageSpecification -> {
						pageSpecification.setCustomFields(
							PageSpecificationsTestUtil.getCustomFields());

						return pageSpecification;
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
		}
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

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private Sites _sites;

	@Inject
	private StagingLocalService _stagingLocalService;

}