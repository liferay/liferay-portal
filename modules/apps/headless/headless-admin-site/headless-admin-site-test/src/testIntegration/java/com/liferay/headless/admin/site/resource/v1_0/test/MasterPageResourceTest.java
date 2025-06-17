/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.Scope;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.MasterPageResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.sites.kernel.util.Sites;

import java.util.ArrayList;
import java.util.List;
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

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeMasterPage()
		throws Exception {

		MasterPage postMasterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postMasterPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		masterPageResource.deleteSiteSiteByExternalReferenceCodeMasterPage(
			testGroup.getExternalReferenceCode(),
			postMasterPage.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postMasterPage.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				masterPageResource.
					deleteSiteSiteByExternalReferenceCodeMasterPage(
						testGroup.getExternalReferenceCode(),
						postMasterPage.getExternalReferenceCode()));

		MasterPage liveGroupMasterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				masterPageResource.
					deleteSiteSiteByExternalReferenceCodeMasterPage(
						testGroup.getExternalReferenceCode(),
						liveGroupMasterPage.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteMasterPagePermissionsPage() throws Exception {
		super.testGetSiteMasterPagePermissionsPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPage()
		throws Exception {

		MasterPage masterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		_testGetSiteSiteByExternalReferenceCodeMasterPage(masterPage);
		_testGetSiteSiteByExternalReferenceCodeMasterPageWithNestedFields(
			masterPage);

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				masterPageResource.getSiteSiteByExternalReferenceCodeMasterPage(
					testGroup.getExternalReferenceCode(),
					RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteSiteByExternalReferenceCodeMasterPage(masterPage);
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagesPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodeMasterPagesPage();

		_testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSearch();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortDateTime()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortDouble()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortInteger()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortString()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSortString();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodeMasterPage()
		throws Exception {

		MasterPage masterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		_updateLayoutPageTemplateEntryStatus(
			masterPage.getExternalReferenceCode());

		_testPatchSiteSiteByExternalReferenceCodeMasterPage(
			Boolean.TRUE, null,
			_getMasterPage(
				Boolean.TRUE, masterPage.getExternalReferenceCode(), null));

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPatchSiteSiteByExternalReferenceCodeMasterPage(
			Boolean.TRUE, fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPatchSiteSiteByExternalReferenceCodeMasterPage(
			Boolean.FALSE, fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		_testPatchSiteSiteByExternalReferenceCodeMasterPage(
			Boolean.FALSE, null,
			_getMasterPage(
				Boolean.FALSE, masterPage.getExternalReferenceCode(),
				StringPool.BLANK));

		_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				masterPageResource.
					patchSiteSiteByExternalReferenceCodeMasterPage(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(), randomMasterPage()));

		MasterPage liveGroupMasterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				masterPageResource.
					patchSiteSiteByExternalReferenceCodeMasterPage(
						testGroup.getExternalReferenceCode(),
						liveGroupMasterPage.getExternalReferenceCode(),
						_getMasterPage(
							null,
							liveGroupMasterPage.getExternalReferenceCode(),
							null)));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeMasterPage()
		throws Exception {

		super.testPostSiteSiteByExternalReferenceCodeMasterPage();

		MasterPage masterPage = randomMasterPage();

		masterPage.setKey(StringPool.BLANK);

		MasterPage postMasterPage =
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
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

		postMasterPage =
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(), masterPage);

		Assert.assertEquals(masterPage.getKey(), postMasterPage.getKey());

		_assertThumbnailItemExternalReference(
			fileEntry.getExternalReferenceCode(),
			postMasterPage.getThumbnail());

		_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications();
		_testPostSiteSiteByExternalReferenceCodeMasterPageWithSiteTemplatePageSpecification();
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeMasterPagePageSpecification()
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage masterPage =
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(), randomMasterPage());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					masterPage.getExternalReferenceCode(),
					testGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.
			testPostSiteSiteByExternalReferenceCodePageSpecification(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				masterPage.getPageSpecifications(), serviceContext,
				contentPageSpecification ->
					masterPageResource.
						postSiteSiteByExternalReferenceCodeMasterPagePageSpecification(
							testGroup.getExternalReferenceCode(),
							masterPage.getExternalReferenceCode(),
							contentPageSpecification));

		_assertPostSiteSiteByExternalReferenceCodeMasterPagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteSiteByExternalReferenceCodeMasterPagePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext));
	}

	@Ignore
	@Override
	@Test
	public void testPutSiteMasterPagePermissionsPage() throws Exception {
		super.testPutSiteMasterPagePermissionsPage();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodeMasterPage()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeMasterPage(
			null, randomMasterPage());

		MasterPage masterPage =
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				randomMasterPage());

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		_testPutSiteSiteByExternalReferenceCodeMasterPage(
			fileEntry.getExternalReferenceCode(),
			_getMasterPage(
				null, masterPage.getExternalReferenceCode(),
				fileEntry.getExternalReferenceCode()));

		_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
					testGroup.getExternalReferenceCode(),
					masterPage.getExternalReferenceCode(),
					_getMasterPage(
						null, masterPage.getExternalReferenceCode(), null)));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "keywordItemExternalReferences", "name",
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

		masterPage.setKeywordItemExternalReferences(
			_randomKeywordItemExternalReferences());
		masterPage.setMarkedAsDefault(Boolean.FALSE);
		masterPage.setTaxonomyCategoryItemExternalReferences(
			_randomTaxonomyCategoryItemExternalReferences());

		return masterPage;
	}

	@Ignore
	@Override
	@Test
	protected MasterPage testGetSiteMasterPagePermissionsPage_addMasterPage()
		throws Exception {

		return super.testGetSiteMasterPagePermissionsPage_addMasterPage();
	}

	@Override
	protected MasterPage
			testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
				String siteExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		return masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
			siteExternalReferenceCode, masterPage);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodeMasterPagesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodeMasterPagesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected MasterPage
			testPostSiteSiteByExternalReferenceCodeMasterPage_addMasterPage(
				MasterPage masterPage)
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);
	}

	@Ignore
	@Override
	@Test
	protected MasterPage testPutSiteMasterPagePermissionsPage_addMasterPage()
		throws Exception {

		return super.testPutSiteMasterPagePermissionsPage_addMasterPage();
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

	private void
			_assertPostSiteSiteByExternalReferenceCodeMasterPagePageSpecificationProblemException(
				LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				masterPageResource.
					postSiteSiteByExternalReferenceCodeMasterPagePageSpecification(
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

	private MasterPage _getMasterPage(String name) throws Exception {
		MasterPage masterPage = randomMasterPage();

		masterPage.setName(name);

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

	private Scope _getScope(long groupId, long scopeGroupId) throws Exception {
		if (groupId == scopeGroupId) {
			return null;
		}

		Group group = _groupLocalService.getGroup(scopeGroupId);

		return new Scope() {
			{
				setExternalReferenceCode(group::getExternalReferenceCode);
				setType(
					() -> {
						if (group.getType() == GroupConstants.TYPE_DEPOT) {
							return Scope.Type.ASSET_LIBRARY;
						}

						return Scope.Type.SITE;
					});
			}
		};
	}

	private List<AssetCategory> _randomAssetCategories(
			AssetVocabulary assetVocabulary, ServiceContext serviceContext)
		throws Exception {

		List<AssetCategory> assetCategories = new ArrayList<>();

		for (int i = 0; i < RandomTestUtil.randomInt(1, 3); i++) {
			assetCategories.add(
				_assetCategoryLocalService.addCategory(
					RandomTestUtil.randomString(), TestPropsValues.getUserId(),
					assetVocabulary.getGroupId(), 0,
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomLocaleStringMap(),
					assetVocabulary.getVocabularyId(), null, serviceContext));
		}

		return assetCategories;
	}

	private List<AssetCategory> _randomAssetCategories(
			ServiceContext serviceContext)
		throws Exception {

		List<AssetCategory> assetCategories = new ArrayList<>();

		for (int i = 0; i < RandomTestUtil.randomInt(1, 3); i++) {
			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.addVocabulary(
					TestPropsValues.getUserId(), testGroup.getGroupId(),
					RandomTestUtil.randomString(), serviceContext);

			assetCategories = ListUtil.concat(
				assetCategories,
				_randomAssetCategories(assetVocabulary, serviceContext));
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), testCompany.getGroupId(),
				RandomTestUtil.randomString(), serviceContext);

		return ListUtil.concat(
			assetCategories,
			_randomAssetCategories(assetVocabulary, serviceContext));
	}

	private ItemExternalReference[] _randomKeywordItemExternalReferences()
		throws Exception {

		int length = RandomTestUtil.randomInt(1, 3);

		ItemExternalReference[] itemExternalReferences =
			new ItemExternalReference[length];

		for (int i = 0; i < length; i++) {
			ItemExternalReference itemExternalReference =
				new ItemExternalReference();

			AssetTag assetTag = _assetTagLocalService.addTag(
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				TestPropsValues.getUserId(), testGroup.getGroupId(),
				StringUtil.toLowerCase(RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext(
					testGroup, TestPropsValues.getUserId()));

			itemExternalReference.setExternalReferenceCode(
				assetTag.getExternalReferenceCode());

			itemExternalReferences[i] = itemExternalReference;
		}

		return itemExternalReferences;
	}

	private ItemExternalReference[]
			_randomTaxonomyCategoryItemExternalReferences()
		throws Exception {

		List<AssetCategory> assetCategories = _randomAssetCategories(
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		return TransformUtil.unsafeTransformToArray(
			assetCategories,
			assetCategory -> new ItemExternalReference() {
				{
					setExternalReferenceCode(
						assetCategory.getExternalReferenceCode());
					setScope(
						() -> _getScope(
							testGroup.getGroupId(),
							assetCategory.getGroupId()));
				}
			},
			ItemExternalReference.class);
	}

	private void _testGetSiteSiteByExternalReferenceCodeMasterPage(
			MasterPage masterPage)
		throws Exception {

		MasterPage getMasterPage =
			masterPageResource.getSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode());

		assertEquals(masterPage, getMasterPage);
		assertValid(getMasterPage);
	}

	private List<MasterPage>
			_testGetSiteSiteByExternalReferenceCodeMasterPagesPage(
				int count, String search)
		throws Exception {

		Page<MasterPage> masterPagePage =
			masterPageResource.
				getSiteSiteByExternalReferenceCodeMasterPagesPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		List<MasterPage> masterPages =
			(List<MasterPage>)masterPagePage.getItems();

		Assert.assertEquals(masterPages.toString(), count, masterPages.size());

		return masterPages;
	}

	private void _testGetSiteSiteByExternalReferenceCodeMasterPagesPageWithSearch()
		throws Exception {

		String search = RandomTestUtil.randomString();

		Page<MasterPage> masterPagePage =
			masterPageResource.
				getSiteSiteByExternalReferenceCodeMasterPagesPage(
					testGroup.getExternalReferenceCode(), search, null, null,
					null, null);

		int totalCount = GetterUtil.getInteger(masterPagePage.getTotalCount());

		testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());

		testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());

		MasterPage masterPage = _getMasterPage(search);

		testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), masterPage);

		testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(), randomMasterPage());

		List<MasterPage> masterPages =
			_testGetSiteSiteByExternalReferenceCodeMasterPagesPage(
				totalCount + 1, search);

		String name = null;

		for (MasterPage curMasterPage : masterPages) {
			if (!Objects.equals(
					curMasterPage.getExternalReferenceCode(),
					masterPage.getExternalReferenceCode())) {

				continue;
			}

			name = curMasterPage.getName();

			break;
		}

		Assert.assertEquals(search, name);

		testGetSiteSiteByExternalReferenceCodeMasterPagesPage_addMasterPage(
			testGroup.getExternalReferenceCode(),
			_getMasterPage(
				RandomTestUtil.randomString() + search +
					RandomTestUtil.randomString()));

		_testGetSiteSiteByExternalReferenceCodeMasterPagesPage(
			totalCount + 2, search);
	}

	private void
			_testGetSiteSiteByExternalReferenceCodeMasterPageWithNestedFields(
				MasterPage masterPage)
		throws Exception {

		MasterPageResource masterPageResource = _getMasterPageResource();

		MasterPage getMasterPage =
			masterPageResource.getSiteSiteByExternalReferenceCodeMasterPage(
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

	private void _testPatchSiteSiteByExternalReferenceCodeMasterPage(
			Boolean expectedMarkedAsDefault,
			String expectedExternalReferenceCode, MasterPage masterPage)
		throws Exception {

		MasterPage patchMasterPage =
			masterPageResource.patchSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage);

		assertEquals(masterPage, patchMasterPage);
		assertValid(patchMasterPage);

		Assert.assertEquals(
			expectedMarkedAsDefault, patchMasterPage.getMarkedAsDefault());

		_assertThumbnailItemExternalReference(
			expectedExternalReferenceCode, patchMasterPage.getThumbnail());
	}

	private void _testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications()
		throws Exception {

		_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
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

		MasterPage postMasterPage =
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(), masterPage);

		_assertPageSpecifications(
			postMasterPage, draftContentPageSpecification,
			publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			masterPageResource.patchSiteSiteByExternalReferenceCodeMasterPage(
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

	private void _testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications()
		throws Exception {

		_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
	}

	private void
			_testPostSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
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
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteSiteByExternalReferenceCodeMasterPageWithSiteTemplatePageSpecification()
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

		MasterPage postMasterPage =
			masterPageResource.postSiteSiteByExternalReferenceCodeMasterPage(
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

	private void _testPutSiteSiteByExternalReferenceCodeMasterPage(
			String expectedThumbnailExternalReferenceCode,
			MasterPage masterPage)
		throws Exception {

		masterPage.setMarkedAsDefault(Boolean.TRUE);

		_assertProblemException(
			"CONFLICT", "The default master page must be published first.",
			() ->
				masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
					testGroup.getExternalReferenceCode(),
					masterPage.getExternalReferenceCode(), masterPage));

		masterPage.setMarkedAsDefault(Boolean.FALSE);

		MasterPage putMasterPage =
			masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage);

		assertEquals(masterPage, putMasterPage);
		assertValid(putMasterPage);

		_assertThumbnailItemExternalReference(
			expectedThumbnailExternalReferenceCode,
			putMasterPage.getThumbnail());
	}

	private void _testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPutSiteSiteByExternalReferenceCodeMasterPageWithPageSpecifications(
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
			masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
				testGroup.getExternalReferenceCode(),
				masterPage.getExternalReferenceCode(), masterPage),
			draftContentPageSpecification, publishedContentPageSpecification);
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
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

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