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
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
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
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_displayPageTemplateResource.setContextAcceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			});
		_displayPageTemplateResource.setContextUser(TestPropsValues.getUser());
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Override
	@Test
	public void testDeleteSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate postDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		displayPageTemplateResource.deleteSiteDisplayPageTemplate(
			testGroup.getExternalReferenceCode(),
			postDisplayPageTemplate.getExternalReferenceCode());

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					postDisplayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.deleteSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				postDisplayPageTemplate.getExternalReferenceCode()));

		DisplayPageTemplate liveGroupDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.deleteSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				liveGroupDisplayPageTemplate.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate displayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testGetSiteDisplayPageTemplate(displayPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					displayPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(_isPublished(layout));

		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);

		ReflectionTestUtil.invoke(
			_mvcActionCommand, "_publishLayoutPageTemplateEntry",
			new Class<?>[] {Layout.class, Layout.class, long.class},
			layout.fetchDraftLayout(), layout, TestPropsValues.getUserId());

		Assert.assertTrue(_isPublished(layout));

		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteDisplayPageTemplate(displayPageTemplate);
		_testGetSiteDisplayPageTemplateWithNestedFields(displayPageTemplate);
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPage() throws Exception {
		super.testGetSiteDisplayPageTemplatesPage();

		_testGetSiteDisplayPageTemplatesPageWithNestedFields();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortDateTime()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortDouble()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortInteger()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteDisplayPageTemplatesPageWithSortString()
		throws Exception {

		super.testGetSiteDisplayPageTemplatesPageWithSortString();
	}

	@Override
	@Test
	public void testPatchSiteDisplayPageTemplate() throws Exception {
		DisplayPageTemplate expectedDisplayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testPatchSiteDisplayPageTemplate(
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

		_testPatchSiteDisplayPageTemplate(
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

		_testPatchSiteDisplayPageTemplate(
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

		_testPatchSiteDisplayPageTemplate(
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

		_testPatchSiteDisplayPageTemplate(
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

		_testPatchSiteDisplayPageTemplateWithPageSpecifications();

		_assertProblemException(
			"NOT_FOUND", null,
			() -> displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString(), randomDisplayPageTemplate()));

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				expectedDisplayPageTemplate.getExternalReferenceCode(),
				expectedDisplayPageTemplate));
	}

	@Override
	@Test
	public void testPostSiteDisplayPageTemplate() throws Exception {
		super.testPostSiteDisplayPageTemplate();

		_testPostSiteDisplayPageTemplateWithKey();
		_testPostSiteDisplayPageTemplateWithPageSpecifications();
		_testPostSiteDisplayPageTemplateWithParentFolder();
		_testPostSiteDisplayPageTemplateWithThumbnail();
	}

	@Ignore
	@Override
	@Test
	public void testPostSiteDisplayPageTemplateFolderDisplayPageTemplate()
		throws Exception {

		super.testPostSiteDisplayPageTemplateFolderDisplayPageTemplate();
	}

	@Override
	@Test
	public void testPostSiteDisplayPageTemplatePageSpecification()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
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

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			displayPageTemplate.getPageSpecifications(), serviceContext,
			contentPageSpecification ->
				displayPageTemplateResource.
					postSiteDisplayPageTemplatePageSpecification(
						testGroup.getExternalReferenceCode(),
						displayPageTemplate.getExternalReferenceCode(),
						contentPageSpecification));

		_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext));

		_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT));
	}

	@Override
	@Test
	public void testPutSiteDisplayPageTemplate() throws Exception {
		_testPutSiteDisplayPageTemplateContentTypeReference();
		_testPutSiteDisplayPageTemplateMarkAsDefault();
		_testPutSiteDisplayPageTemplateSettings();
		_testPutSiteDisplayPageTemplateThumbnail();

		_testPutSiteDisplayPageTemplate(randomDisplayPageTemplate());

		DisplayPageTemplate displayPageTemplate =
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				randomDisplayPageTemplate());

		_testPutSiteDisplayPageTemplate(displayPageTemplate);

		_testPutSiteDisplayPageTemplateWithPageSpecifications();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> displayPageTemplateResource.putSiteDisplayPageTemplate(
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

	@Override
	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				String displayPageTemplateFolderExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.
			postSiteDisplayPageTemplateFolderDisplayPageTemplate(
				siteExternalReferenceCode,
				displayPageTemplateFolderExternalReferenceCode,
				displayPageTemplate);
	}

	@Override
	protected String
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_getDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			testGroup.getGroupId());
	}

	@Override
	protected String
			testGetSiteDisplayPageTemplateFolderDisplayPageTemplatesPage_getIrrelevantDisplayPageTemplateFolderExternalReferenceCode()
		throws Exception {

		return _getLayoutPageTemplateCollectionExternalReferenceCode(
			irrelevantGroup.getGroupId());
	}

	@Override
	protected DisplayPageTemplate
			testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
				String siteExternalReferenceCode,
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return displayPageTemplateResource.postSiteDisplayPageTemplate(
			siteExternalReferenceCode, displayPageTemplate);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSiteDisplayPageTemplatesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected DisplayPageTemplate
			testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
				DisplayPageTemplate displayPageTemplate)
		throws Exception {

		return testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
			testGroup.getExternalReferenceCode(), displayPageTemplate);
	}

	private static com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate
		_toDisplayPageTemplate(DisplayPageTemplate displayPageTemplate) {

		if (displayPageTemplate == null) {
			return null;
		}

		return com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate.
			toDTO(displayPageTemplate.toString());
	}

	private static DisplayPageTemplate _toDisplayPageTemplate(
		com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate
			displayPageTemplate) {

		if (displayPageTemplate == null) {
			return null;
		}

		return DisplayPageTemplate.toDTO(displayPageTemplate.toString());
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
			_assertPostSiteDisplayPageTemplatePageSpecificationProblemException(
				LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", null,
			() ->
				displayPageTemplateResource.
					postSiteDisplayPageTemplatePageSpecification(
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

	private void
			_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
				UnsafeFunction
					<DisplayPageTemplateFolder, DisplayPageTemplate, Exception>
						unsafeFunction)
		throws Exception {

		DisplayPageTemplateFolder displayPageTemplateFolder =
			new DisplayPageTemplateFolder() {
				{
					setDescription(RandomTestUtil::randomString);
					setExternalReferenceCode(RandomTestUtil::randomString);
					setName(RandomTestUtil::randomString);
				}
			};

		DisplayPageTemplateFolder parentDisplayPageTemplateFolder =
			new DisplayPageTemplateFolder() {
				{
					setDescription(RandomTestUtil::randomString);
					setExternalReferenceCode(RandomTestUtil::randomString);
					setName(RandomTestUtil::randomString);
				}
			};

		displayPageTemplateFolder.setParentDisplayPageTemplateFolder(
			parentDisplayPageTemplateFolder);
		displayPageTemplateFolder.
			setParentDisplayPageTemplateFolderExternalReferenceCode(
				parentDisplayPageTemplateFolder.getExternalReferenceCode());

		try {
			unsafeFunction.apply(displayPageTemplateFolder);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			if (_log.isDebugEnabled()) {
				_log.debug(unsupportedOperationException);
			}
		}

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					displayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));
		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					parentDisplayPageTemplateFolder.getExternalReferenceCode(),
					testGroup.getGroupId()));

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			DisplayPageTemplate displayPageTemplate = unsafeFunction.apply(
				displayPageTemplateFolder);

			DisplayPageTemplateFolder addedDisplayPageTemplateFolder =
				displayPageTemplate.getParentFolder();

			Assert.assertEquals(
				displayPageTemplateFolder.getExternalReferenceCode(),
				addedDisplayPageTemplateFolder.getExternalReferenceCode());
			Assert.assertEquals(
				parentDisplayPageTemplateFolder.getExternalReferenceCode(),
				addedDisplayPageTemplateFolder.
					getParentDisplayPageTemplateFolderExternalReferenceCode());

			Assert.assertNotNull(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						displayPageTemplateFolder.getExternalReferenceCode(),
						testGroup.getGroupId()));
			Assert.assertNotNull(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						parentDisplayPageTemplateFolder.
							getExternalReferenceCode(),
						testGroup.getGroupId()));
		}
	}

	private void _testGetSiteDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate getDisplayPageTemplate =
			displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode());

		assertEquals(displayPageTemplate, getDisplayPageTemplate);
		assertValid(getDisplayPageTemplate);
	}

	private void _testGetSiteDisplayPageTemplatesPageWithNestedFields()
		throws Exception {

		Page<DisplayPageTemplate> page =
			displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null);

		long totalCount = page.getTotalCount();

		DisplayPageTemplate displayPageTemplate =
			testGetSiteDisplayPageTemplatesPage_addDisplayPageTemplate(
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

		page = displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		Assert.assertTrue(_isPublished(layout));

		page = displayPageTemplateResource.getSiteDisplayPageTemplatesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		Assert.assertEquals(totalCount + 1, page.getTotalCount());

		_assertNestedFields(
			_getDisplayPageTemplate(
				(List<DisplayPageTemplate>)page.getItems(),
				displayPageTemplate.getExternalReferenceCode()));
	}

	private void _testGetSiteDisplayPageTemplateWithNestedFields(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		_assertNestedFields(
			displayPageTemplateResource.getSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode()));
	}

	private void _testPatchSiteDisplayPageTemplate(
			DisplayPageTemplate expectedDisplayPageTemplate,
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate patchDisplayPageTemplate =
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
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

	private void _testPatchSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void _testPatchSiteDisplayPageTemplateWithPageSpecifications(
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
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertPageSpecifications(
			postDisplayPageTemplate, draftContentPageSpecification,
			publishedContentPageSpecification);

		_assertPageSpecifications(
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				new DisplayPageTemplate()),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.patchSiteDisplayPageTemplate(
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

	private void _testPostSiteDisplayPageTemplateWithKey() throws Exception {
		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setKey(StringPool.BLANK);

		DisplayPageTemplate postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertTrue(
			Validator.isNotNull(postDisplayPageTemplate.getKey()));

		displayPageTemplate = randomDisplayPageTemplate();

		postDisplayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getKey(), postDisplayPageTemplate.getKey());
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
		_testPostSiteDisplayPageTemplateWithPageSpecificationsNull();
		_testPostSiteDisplayPageTemplateWithPageSpecificationsWithSettings();
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecifications(
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
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);
	}

	private void _testPostSiteDisplayPageTemplateWithPageSpecificationsNull()
		throws Exception {

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
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

	private void _testPostSiteDisplayPageTemplateWithPageSpecificationsWithSettings()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, PageSpecification.Status.DRAFT);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		draftContentPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.CLIENT_EXTENSION, serviceContext));

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				PageSpecification.Status.APPROVED);

		publishedContentPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE, serviceContext));

		displayPageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		DisplayPageTemplateResource displayPageTemplateResource =
			_getDisplayPageTemplateResource();

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.client.extension.type.internal.manager." +
					"CETManagerImpl",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.headless.admin.site.internal.util.LogUtil",
				LoggerTestUtil.WARN)) {

			_assertPageSpecifications(
				displayPageTemplateResource.postSiteDisplayPageTemplate(
					testGroup.getExternalReferenceCode(), displayPageTemplate),
				draftContentPageSpecification,
				publishedContentPageSpecification);

			List<LogEntry> logEntries = logCapture2.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 4, logEntries.size());

			for (LogEntry logEntry : logEntries) {
				String message = logEntry.getMessage();

				Assert.assertTrue(
					message,
					message.startsWith(
						"Optional reference generated for missing"));
			}
		}
	}

	private void _testPostSiteDisplayPageTemplateWithParentFolder()
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
			displayPageTemplateResource.postSiteDisplayPageTemplate(
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

		_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
			nonexistingDisplayPageTemplateFolder -> {
				DisplayPageTemplate randomDisplayPageTemplate =
					randomDisplayPageTemplate();

				randomDisplayPageTemplate.setParentFolder(
					nonexistingDisplayPageTemplateFolder);

				return _toDisplayPageTemplate(
					_displayPageTemplateResource.postSiteDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						_toDisplayPageTemplate(randomDisplayPageTemplate)));
			});
	}

	private void _testPostSiteDisplayPageTemplateWithThumbnail()
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
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(), displayPageTemplate);

		_assertThumbnailItemExternalReference(
			fileEntry.getExternalReferenceCode(),
			postDisplayPageTemplate.getThumbnail());
	}

	private void _testPutSiteDisplayPageTemplate(
			DisplayPageTemplate displayPageTemplate)
		throws Exception {

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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

		_testCreatingDisplayPageTemplateFolderWithLazyReferencingEnabled(
			nonexistingDisplayPageTemplateFolder -> {
				DisplayPageTemplate postDisplayPageTemplate =
					testPostSiteDisplayPageTemplate_addDisplayPageTemplate(
						randomDisplayPageTemplate());

				postDisplayPageTemplate.setParentFolder(
					nonexistingDisplayPageTemplateFolder);

				return _toDisplayPageTemplate(
					_displayPageTemplateResource.putSiteDisplayPageTemplate(
						testGroup.getExternalReferenceCode(),
						postDisplayPageTemplate.getExternalReferenceCode(),
						_toDisplayPageTemplate(postDisplayPageTemplate)));
			});
	}

	private void _testPutSiteDisplayPageTemplateContentTypeReference()
		throws Exception {

		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		displayPageTemplate.setContentTypeReference(
			_getClassSubtypeReference(AssetCategory.class.getName()));

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getContentTypeReference(),
			putDisplayPageTemplate.getContentTypeReference());
	}

	private void _testPutSiteDisplayPageTemplateMarkAsDefault()
		throws Exception {

		DisplayPageTemplate displayPageTemplate =
			displayPageTemplateResource.postSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				randomDisplayPageTemplate());

		displayPageTemplate.setMarkedAsDefault(true);

		_assertProblemException(
			"CONFLICT",
			"The default display page template must be published first.",
			() -> displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate));
	}

	private void _testPutSiteDisplayPageTemplateSettings() throws Exception {
		DisplayPageTemplate displayPageTemplate = randomDisplayPageTemplate();

		DisplayPageTemplate putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(
			_randomDisplayPageTemplateSettings());

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate);

		Assert.assertEquals(
			displayPageTemplate.getDisplayPageTemplateSettings(),
			putDisplayPageTemplate.getDisplayPageTemplateSettings());

		displayPageTemplate.setDisplayPageTemplateSettings(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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

	private void _testPutSiteDisplayPageTemplateThumbnail() throws Exception {
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
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		_assertThumbnailItemExternalReference(
			fileEntry2.getExternalReferenceCode(),
			putDisplayPageTemplate.getThumbnail());

		putDisplayPageTemplate.setThumbnail(() -> null);

		putDisplayPageTemplate =
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				putDisplayPageTemplate.getExternalReferenceCode(),
				putDisplayPageTemplate);

		_assertThumbnailItemExternalReference(
			null, putDisplayPageTemplate.getThumbnail());
	}

	private void _testPutSiteDisplayPageTemplateWithPageSpecifications()
		throws Exception {

		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteDisplayPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
	}

	private void _testPutSiteDisplayPageTemplateWithPageSpecifications(
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
			displayPageTemplateResource.putSiteDisplayPageTemplate(
				testGroup.getExternalReferenceCode(),
				displayPageTemplate.getExternalReferenceCode(),
				displayPageTemplate),
			draftContentPageSpecification, publishedContentPageSpecification);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);
		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			displayPageTemplateResource.putSiteDisplayPageTemplate(
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

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageTemplateResourceTest.class);

	@Inject
	private
		com.liferay.headless.admin.site.resource.v1_0.
			DisplayPageTemplateResource _displayPageTemplateResource;

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