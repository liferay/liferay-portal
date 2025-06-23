/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.ClassSubtypeReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateOpenGraphSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSEOSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.DisplayPageTemplateSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
 * @author Lourdes Fernández Besada
 */
@FeatureFlag("LPD-35443")
@RunWith(Arquillian.class)
public class DisplayPageTemplateResourceTest
	extends BaseDisplayPageTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		displayPageTemplateResource.
			deleteSiteSiteByExternalReferenceCodeDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplate.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateResource.
					deleteSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						postDisplayPageTemplate.getExternalReferenceCode()));

		DisplayPageTemplate liveGroupDisplayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateResource.
					deleteSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						liveGroupDisplayPageTemplate.
							getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatePermissionsPage()
		throws Exception {

		super.testGetSiteDisplayPageTemplatePermissionsPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			displayPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(_isPublished(layout));

		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNestedFields(
			displayPageTemplate);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class},
			layout.fetchDraftLayout(), layout);

		Assert.assertTrue(_isPublished(layout));

		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNestedFields(
			displayPageTemplate);

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateResource.
					getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			displayPageTemplate);
		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNestedFields(
			displayPageTemplate);
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage();

		_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithNestedFields();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDateTime()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDouble()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortInteger()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortString()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithSortString();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		super.
			testGraphQLGetSiteSiteByExternalReferenceCodeDisplayPageTemplate();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		DisplayPageTemplate expectedDisplayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			expectedDisplayPageTemplate, new DisplayPageTemplate());

		Assert.assertNull(expectedDisplayPageTemplate.getParentFolder());

		ClassSubtypeReference contentTypeReference =
			expectedDisplayPageTemplate.getContentTypeReference();

		String className = contentTypeReference.getClassName();

		if (className.equals(AssetCategory.class.getName())) {
			className = "com.liferay.journal.model.JournalArticle";
		}

		expectedDisplayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(className));
		expectedDisplayPageTemplate.setMarkedAsDefault(Boolean.FALSE);
		expectedDisplayPageTemplate.setParentFolder(
			new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						_getLayoutPageTemplateCollectionExternalReferenceCode(
							testGroup.getGroupId()));
				}
			});

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setContentTypeReference(
						expectedDisplayPageTemplate.getContentTypeReference());
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
					setParentFolder(
						expectedDisplayPageTemplate.getParentFolder());
				}
			});

		_updateLayoutPageTemplateEntryStatus(
			expectedDisplayPageTemplate.getExternalReferenceCode());

		expectedDisplayPageTemplate.setMarkedAsDefault(Boolean.TRUE);

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
				}
			});

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		expectedDisplayPageTemplate.setThumbnail(
			new ItemExternalReference() {
				{
					setClassName(FileEntry.class.getName());
					setExternalReferenceCode(
						fileEntry.getExternalReferenceCode());
				}
			});

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
					setThumbnail(expectedDisplayPageTemplate.getThumbnail());
				}
			});

		expectedDisplayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			expectedDisplayPageTemplate,
			new DisplayPageTemplate() {
				{
					setDisplayPageTemplateSettings(
						expectedDisplayPageTemplate.
							getDisplayPageTemplateSettings());
					setMarkedAsDefault(
						expectedDisplayPageTemplate.getMarkedAsDefault());
				}
			});

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications();

		_assertProblemException(
			"NOT_FOUND", null,
			() ->
				displayPageTemplateResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString(),
						randomDisplayPageTemplate()));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						expectedDisplayPageTemplate.getExternalReferenceCode(),
						expectedDisplayPageTemplate));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		super.testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate();

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithKey();
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications();
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithParentFolder();
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithThumbnail();
	}

	@Ignore
	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate()
		throws Exception {

		super.
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate();
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.
			testPostSiteSiteByExternalReferenceCodePageSpecification(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				displayPageTemplate.getPageSpecifications(), serviceContext,
				contentPageSpecification ->
					displayPageTemplateResource.
						postSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification(
							testGroup.getExternalReferenceCode(),
							displayPageTemplate.getExternalReferenceCode(),
							contentPageSpecification));

		_assertPostSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT));
	}

	@Ignore
	@Override
	@Test
	public void testPutSiteDisplayPageTemplatePermissionsPage()
		throws Exception {

		super.testPutSiteDisplayPageTemplatePermissionsPage();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateContentTypeReference();
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateMarkAsDefault();
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateSettings();
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateThumbnail();

		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate =
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			displayPageTemplate);

		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateResource.
					putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						displayPageTemplate.getExternalReferenceCode(),
						displayPageTemplate));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"displayPageTemplateSettings", "externalReferenceCode",
			"friendlyUrlPath_i18n", "name"
		};
	}

	@Override
	protected DisplayPageTemplate randomDisplayPageTemplate() throws Exception {
		DisplayPageTemplate displayPageTemplate =
			super.randomDisplayPageTemplate();

		displayPageTemplate.setContentTypeReference(
			_getRandomClassSubtypeReference());
		displayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());
		displayPageTemplate.setFriendlyUrlPath_i18n(
			() -> HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
				StringPool.FORWARD_SLASH +
					StringUtil.toLowerCase(RandomTestUtil.randomString())
			).build());
		displayPageTemplate.setMarkedAsDefault(Boolean.FALSE);

		return displayPageTemplate;
	}

	@Ignore
	@Override
	@Test
	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		return super.
			testGetSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();
	}

	@Override
	protected DisplayPageTemplate
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.
			postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode,
				displayPageTemplate);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			testGroup.getGroupId());
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			irrelevantGroup.getGroupId());
	}

	@Override
	protected DisplayPageTemplate
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.
			postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
				siteExternalReferenceCode, displayPageTemplate);
	}

	@Override
	protected DisplayPageTemplate
			testPostSiteSiteByExternalReferenceCodeDisplayPageTemplate_addDisplayPageTemplate(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
			testGroup.getExternalReferenceCode(), displayPageTemplate);
	}

	@Ignore
	@Override
	@Test
	protected DisplayPageTemplate
			testPutSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate()
		throws Exception {

		return super.
			testPutSiteDisplayPageTemplatePermissionsPage_addDisplayPageTemplate();
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

	private void _assertNestedFields(DisplayPageTemplate displayPageTemplate)
		throws Exception {

		FriendlyUrlHistory friendlyUrlHistory =
			displayPageTemplate.getFriendlyUrlHistory();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(friendlyUrlHistory.getFriendlyUrlPath_i18n()));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Map<Locale, String> friendlyURLMap = new HashMap<>();

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		if (_isPublished(layout)) {
			friendlyURLMap = layout.getFriendlyURLMap();
		}

		Assert.assertEquals(
			jsonObject.toString(), friendlyURLMap.size(), jsonObject.length());

		for (Map.Entry<Locale, String> entry : friendlyURLMap.entrySet()) {
			String key = LocaleUtil.toBCP47LanguageId(entry.getKey());

			JSONArray jsonArray = jsonObject.getJSONArray(key);

			Assert.assertEquals(jsonArray.toString(), 1, jsonArray.length());
			Assert.assertEquals(
				jsonArray.toString(), entry.getValue(), jsonArray.getString(0));
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, displayPageTemplate.getPageSpecifications());
	}

	private void _assertPageSpecifications(
			DisplayPageTemplate displayPageTemplate,
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			displayPageTemplate.getPageSpecifications(),
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			publishedContentPageSpecification.getStatus());
	}

	private void
			_assertPostSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecificationProblemException(
				LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification(
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

	private ClassSubtypeReference _getClassSubtypeReference(
		String classSubtypeReferenceClassName) {

		if (classSubtypeReferenceClassName.equals(
				AssetCategory.class.getName())) {

			return new ClassSubtypeReference() {
				{
					setClassName(classSubtypeReferenceClassName);
				}
			};
		}

		Assert.assertEquals(
			"com.liferay.journal.model.JournalArticle",
			classSubtypeReferenceClassName);

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				classSubtypeReferenceClassName);

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>(
			infoItemFormVariationsProvider.getInfoItemFormVariations(
				testGroup.getGroupId()));

		Assert.assertFalse(infoItemFormVariations.isEmpty());

		infoItemFormVariations.sort(
			Comparator.comparing(InfoItemFormVariation::getKey));

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariations.get(0);

		return new ClassSubtypeReference() {
			{
				setClassName(classSubtypeReferenceClassName);
				setSubTypeExternalReference(
					() -> new ItemExternalReference() {
						{
							setExternalReferenceCode(
								infoItemFormVariation::
									getExternalReferenceCode);
						}
					});
			}
		};
	}

	private DisplayPageTemplate _getDisplayPageTemplate(
		List<DisplayPageTemplate> displayPageTemplates,
		String externalReferenceCode) {

		for (DisplayPageTemplate displayPageTemplate : displayPageTemplates) {
			if (Objects.equals(
					displayPageTemplate.getExternalReferenceCode(),
					externalReferenceCode)) {

				return displayPageTemplate;
			}
		}

		return null;
	}

	private DisplayPageTemplateResource _getDisplayPageTemplateResource()
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return DisplayPageTemplateResource.builder(
		).authentication(
			user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "friendlyUrlHistory,pageSpecifications"
		).build();
	}

	private String _getLayoutPageTemplateCollectionExternalReferenceCode(
			long groupId)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), groupId,
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						groupId, TestPropsValues.getUserId()));

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	private ClassSubtypeReference _getRandomClassSubtypeReference() {
		if (RandomTestUtil.randomBoolean()) {
			return _getClassSubtypeReference(AssetCategory.class.getName());
		}

		return _getClassSubtypeReference(
			"com.liferay.journal.model.JournalArticle");
	}

	private boolean _isPublished(Layout layout) {
		Layout draftLayout = layout.fetchDraftLayout();

		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty("published"));
	}

	private DisplayPageTemplateSettings _randomDisplayPageTemplateSettings() {
		DisplayPageTemplateSettings displayPageTemplateSettings =
			new DisplayPageTemplateSettings();

		displayPageTemplateSettings.setOpenGraphSettings(
			new DisplayPageTemplateOpenGraphSettings() {
				{
					setDescriptionTemplate(RandomTestUtil.randomString());
					setImageAltTemplate(RandomTestUtil.randomString());
					setImageTemplate(RandomTestUtil.randomString());
					setTitleTemplate(RandomTestUtil.randomString());
				}
			});

		SitemapSettings randomSitemapSettings = new SitemapSettings() {
			{
				setChangeFrequency(
					RandomTestUtil.randomEnum(ChangeFrequency.class));
				setInclude(RandomTestUtil.randomBoolean());
				setPagePriority(RandomTestUtil.randomDouble());
			}
		};

		displayPageTemplateSettings.setSeoSettings(
			new DisplayPageTemplateSEOSettings() {
				{
					setDescriptionTemplate(RandomTestUtil.randomString());
					setHtmlTitleTemplate(RandomTestUtil.randomString());
					setRobots_i18n(
						LocalizedMapUtil.getI18nMap(
							RandomTestUtil.randomLocaleStringMap()));
					setSitemapSettings(randomSitemapSettings);
				}
			});

		return displayPageTemplateSettings;
	}

	private void _testGetSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode());

		assertEquals(displayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	private void _testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPageWithNestedFields()
		throws Exception {

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		long totalCount = page.getTotalCount();

		DisplayPageTemplate displayPageTemplate =
			testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage_addDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(_isPublished(layout));

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(_isPublished(layout));

		page =
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplatesPage(
					testGroup.getExternalReferenceCode(), null, null, null,
					null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));
	}

	private void
			_testGetSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNestedFields(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		_assertNestedFields(
			displayPageTemplateResource.
				getSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode()));
	}

	private void _testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			DisplayPageTemplate expectedDisplayPageTemplate,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate patchDisplayPageTemplate =
			displayPageTemplateResource.
				patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					expectedDisplayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		assertEquals(expectedDisplayPageTemplate, patchDisplayPageTemplate);
		assertValid(patchDisplayPageTemplate);

		Assert.assertEquals(
			expectedDisplayPageTemplate.getContentTypeReference(),
			patchDisplayPageTemplate.getContentTypeReference());
		Assert.assertEquals(
			expectedDisplayPageTemplate.getMarkedAsDefault(),
			patchDisplayPageTemplate.getMarkedAsDefault());

		DisplayPageTemplateFolder displayPageTemplateFolder1 =
			expectedDisplayPageTemplate.getParentFolder();
		DisplayPageTemplateFolder displayPageTemplateFolder2 =
			patchDisplayPageTemplate.getParentFolder();

		if ((displayPageTemplateFolder1 != null) &&
			(displayPageTemplateFolder2 != null)) {

			Assert.assertEquals(
				displayPageTemplateFolder1.getExternalReferenceCode(),
				displayPageTemplateFolder2.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				displayPageTemplateFolder1, displayPageTemplateFolder2);
		}

		Assert.assertEquals(
			expectedDisplayPageTemplate.getThumbnail(),
			patchDisplayPageTemplate.getThumbnail());
	}

	private void _testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
				PageSpecification.Status newDraftLayoutStatus,
				PageSpecification.Status newPublishedLayoutStatus,
				PageSpecification.Status oldDraftLayoutStatus,
				PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertPageSpecifications(
			postDisplayPageTemplate, draftContentPageSpecification,
			publishedContentPageSpecification);

		_assertPageSpecifications(
			displayPageTemplateResource.
				patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					new DisplayPageTemplate()),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.
				patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					new DisplayPageTemplate() {
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

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithKey()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setKey(StringPool.BLANK);

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertTrue(
			Validator.isNotNull(postDisplayPageTemplate.getKey()));

		displayPageTemplate = randomDisplayPageTemplate();

		postDisplayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getKey(), postDisplayPageTemplate.getKey());
	}

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNullPageSpecifications()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					randomDisplayPageTemplate());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			displayPageTemplate.getPageSpecifications());
	}

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithNullPageSpecifications();
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
	}

	private void
			_testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
				PageSpecification.Status draftLayoutStatus,
				PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		_assertPageSpecifications(
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithParentFolder()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), testGroup.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						testGroup, TestPropsValues.getUserId()));

		displayPageTemplate.setParentFolder(
			() -> new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						layoutPageTemplateCollection.
							getExternalReferenceCode());
				}
			});

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate);

		assertEquals(displayPageTemplate, postDisplayPageTemplate);
		assertValid(postDisplayPageTemplate);

		DisplayPageTemplateFolder displayPageTemplateFolder =
			displayPageTemplate.getParentFolder();
		DisplayPageTemplateFolder postDisplayPageTemplateFolder =
			postDisplayPageTemplate.getParentFolder();

		Assert.assertEquals(
			displayPageTemplateFolder.getExternalReferenceCode(),
			postDisplayPageTemplateFolder.getExternalReferenceCode());
	}

	private void _testPostSiteSiteByExternalReferenceCodeDisplayPageTemplateWithThumbnail()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry = _addPortletFileEntry(repository.getDlFolderId());

		displayPageTemplate.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(FileEntry.class.getName());
					setExternalReferenceCode(
						fileEntry.getExternalReferenceCode());
				}
			});

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertThumbnailItemExternalReference(
			fileEntry.getExternalReferenceCode(),
			postDisplayPageTemplate.getThumbnail());
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		assertEquals(displayPageTemplate, putDisplayPageTemplate);
		assertValid(putDisplayPageTemplate);

		Assert.assertNull(putDisplayPageTemplate.getParentFolder());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					null, TestPropsValues.getUserId(), testGroup.getGroupId(),
					LayoutPageTemplateConstants.
						PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
					null, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE,
					ServiceContextTestUtil.getServiceContext(
						testGroup, TestPropsValues.getUserId()));

		displayPageTemplate.setParentFolder(
			new DisplayPageTemplateFolder() {
				{
					setExternalReferenceCode(
						layoutPageTemplateCollection.
							getExternalReferenceCode());
				}
			});

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		assertEquals(displayPageTemplate, putDisplayPageTemplate);
		assertValid(putDisplayPageTemplate);

		DisplayPageTemplateFolder displayPageTemplateFolder =
			putDisplayPageTemplate.getParentFolder();

		Assert.assertEquals(
			layoutPageTemplateCollection.getExternalReferenceCode(),
			displayPageTemplateFolder.getExternalReferenceCode());
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateContentTypeReference()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(AssetCategory.class.getName()));

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getContentTypeReference(),
			putDisplayPageTemplate.getContentTypeReference());

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(
				"com.liferay.journal.model.JournalArticle"));

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getContentTypeReference(),
			putDisplayPageTemplate.getContentTypeReference());
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateMarkAsDefault()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.
				postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					randomDisplayPageTemplate());

		displayPageTemplate.setMarkedAsDefault(true);

		_assertProblemException(
			"CONFLICT",
			"The default display page template must be published first.",
			() ->
				displayPageTemplateResource.
					putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						displayPageTemplate.getExternalReferenceCode(),
						displayPageTemplate));
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateSettings()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		Assert.assertEquals(
			new DisplayPageTemplateSettings() {
				{
					setOpenGraphSettings(
						new DisplayPageTemplateOpenGraphSettings() {
							{
								setSeoSettings(
									new DisplayPageTemplateSEOSettings() {
										{
											setSitemapSettings(
												new SitemapSettings());
										}
									});
							}
						});
				}
			},
			putDisplayPageTemplate.getDisplayPageTemplateSettings());
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateThumbnail()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setExternalReferenceCode(
			RandomTestUtil.randomString());

		Repository repository = _portletFileRepository.addPortletRepository(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));

		FileEntry fileEntry1 = _addPortletFileEntry(repository.getDlFolderId());

		displayPageTemplate.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(FileEntry.class.getName());
					setExternalReferenceCode(
						fileEntry1.getExternalReferenceCode());
				}
			});

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate);

		_assertThumbnailItemExternalReference(
			fileEntry1.getExternalReferenceCode(),
			putDisplayPageTemplate.getThumbnail());

		FileEntry fileEntry2 = _addPortletFileEntry(repository.getDlFolderId());

		putDisplayPageTemplate.setThumbnail(
			() -> new ItemExternalReference() {
				{
					setClassName(FileEntry.class.getName());
					setExternalReferenceCode(
						fileEntry2.getExternalReferenceCode());
				}
			});

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					putDisplayPageTemplate.getExternalReferenceCode(),
					putDisplayPageTemplate);

		_assertThumbnailItemExternalReference(
			fileEntry2.getExternalReferenceCode(),
			putDisplayPageTemplate.getThumbnail());

		putDisplayPageTemplate.setThumbnail(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					putDisplayPageTemplate.getExternalReferenceCode(),
					putDisplayPageTemplate);

		_assertThumbnailItemExternalReference(
			null, putDisplayPageTemplate.getThumbnail());
	}

	private void _testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void
			_testPutSiteSiteByExternalReferenceCodeDisplayPageTemplateWithPageSpecifications(
				PageSpecification.Status newDraftLayoutStatus,
				PageSpecification.Status newPublishedLayoutStatus,
				PageSpecification.Status oldDraftLayoutStatus,
				PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				oldPublishedLayoutStatus);

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		_assertPageSpecifications(
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.
				putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
					testGroup.getExternalReferenceCode(),
					displayPageTemplate.getExternalReferenceCode(),
					displayPageTemplate),
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
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/layout_content_page_editor/publish_layout_page_template_entry"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private Portal _portal;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private UserLocalService _userLocalService;

}