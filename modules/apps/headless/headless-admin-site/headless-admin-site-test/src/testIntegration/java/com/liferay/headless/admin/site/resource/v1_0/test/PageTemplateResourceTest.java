/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplateSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.Creator;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationSettings;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.client.dto.v1_0.ThumbnailURLReference;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplateSettings;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.PageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.AssetTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.FileEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailHttpServer;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ThumbnailURLReferenceUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Arrays;
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
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-35443"), @FeatureFlag("LPD-76864")}
)
@RunWith(Arquillian.class)
public class PageTemplateResourceTest extends BasePageTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BasePageTemplateResourceTestCase.setUpClass();

		_thumbnailHttpServer = ThumbnailHttpServer.start(
			PageTemplateResourceTest.class);

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
	public void testDeleteSitePageTemplate() throws Exception {
		PageTemplate pageTemplate = testPostSitePageTemplate_addPageTemplate(
			randomPageTemplate());

		_testDeleteSitePageTemplate(
			testGroup, pageTemplate.getExternalReferenceCode());

		_enableLocalStaging(irrelevantGroup);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateResource.deleteSitePageTemplate(
				irrelevantGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> {
				_postSitePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode());

				_testDeleteSitePageTemplate(
					group, widgetPageTemplate.getExternalReferenceCode());
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> pageTemplateResource.deleteSitePageTemplate(
					group.getExternalReferenceCode(),
					RandomTestUtil.randomString())));
	}

	@Override
	@Test
	@TestInfo("LPD-44414")
	public void testGetSitePageTemplate() throws Exception {
		PageTemplate pageTemplate = testPostSitePageTemplate_addPageTemplate(
			randomPageTemplate());

		_testGetSitePageTemplate(pageTemplate);

		_testGetSitePageTemplateWithNestedFields(
			_getContentPageTemplate(testGroup));
		_testGetSitePageTemplateWithNestedFields(
			_getWidgetPageTemplate(testGroup));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> pageTemplateResource.getSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSitePageTemplate(pageTemplate);

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> _postSitePageTemplate(
				widgetPageTemplate, group.getExternalReferenceCode()));

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> pageTemplateResource.getSitePageTemplate(
					group.getExternalReferenceCode(),
					RandomTestUtil.randomString())));
	}

	@Override
	@Test
	public void testGetSitePageTemplateSetPageTemplatesPage() throws Exception {
		super.testGetSitePageTemplateSetPageTemplatesPage();
	}

	@Override
	@Test
	public void testGetSitePageTemplatesPage() throws Exception {
		super.testGetSitePageTemplatesPage();

		_testGetSitePageTemplatesPageWithThumbnailAsNestedField();

		long totalCount = _getSitePageTemplatesPageTotalCount(
			testGroup.getExternalReferenceCode());

		_enableLocalStaging();

		Assert.assertEquals(
			totalCount,
			_getSitePageTemplatesPageTotalCount(
				testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> {
				long curTotalCount = _getSitePageTemplatesPageTotalCount(
					group.getExternalReferenceCode());

				_postSitePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode());

				Assert.assertEquals(
					curTotalCount + 1,
					_getSitePageTemplatesPageTotalCount(
						group.getExternalReferenceCode()));
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> _getSitePageTemplatesPageTotalCount(
					group.getExternalReferenceCode())));
	}

	@Override
	@Test
	public void testGetSitePageTemplatesPageWithSortDateTime()
		throws Exception {

		super.testGetSitePageTemplatesPageWithSortDateTime();
	}

	@Override
	@Test
	public void testGetSitePageTemplatesPageWithSortDouble() throws Exception {
		super.testGetSitePageTemplatesPageWithSortDouble();
	}

	@Override
	@Test
	public void testGetSitePageTemplatesPageWithSortInteger() throws Exception {
		super.testGetSitePageTemplatesPageWithSortInteger();
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPatchSitePageTemplate() throws Exception {
		ContentPageTemplate contentPageTemplate =
			(ContentPageTemplate)pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				_getContentPageTemplate(testGroup));

		_testPatchSitePageTemplate(
			_getUpdatedContentPageTemplate(
				testGroup, contentPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		WidgetPageTemplate widgetPageTemplate =
			(WidgetPageTemplate)pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				_getWidgetPageTemplate(testGroup));

		_testPatchSitePageTemplate(
			_getUpdatedWidgetPageTemplate(
				testGroup, widgetPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		_testPatchSitePageTemplateWithPageSpecifications();
		_testPatchSitePageTemplateWithPageTemplateSet();
		_testPatchSitePageTemplateWithThumbnail();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPatchSitePageTemplate(
				contentPageTemplate, testGroup.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPatchSitePageTemplate(
				widgetPageTemplate, testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, curWidgetPageTemplate) -> {
				_postSitePageTemplate(
					curWidgetPageTemplate, group.getExternalReferenceCode());

				_testPatchSitePageTemplate(
					_getUpdatedWidgetPageTemplate(
						group,
						curWidgetPageTemplate.getExternalReferenceCode()),
					group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST", null,
					() -> {
						ContentPageTemplate curContentPageTemplate =
							_getContentPageTemplate(group);

						pageTemplateResource.putSitePageTemplate(
							group.getExternalReferenceCode(),
							curContentPageTemplate.getExternalReferenceCode(),
							curContentPageTemplate);
					});
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> {
					PageTemplate pageTemplate = _getPageTemplate(group);

					pageTemplateResource.putSitePageTemplate(
						group.getExternalReferenceCode(),
						pageTemplate.getExternalReferenceCode(), pageTemplate);
				}));
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPostSitePageTemplate() throws Exception {
		PageTemplate randomPageTemplate = randomPageTemplate();

		randomPageTemplate.setKey(StringPool.BLANK);

		PageTemplate postPageTemplate =
			testPostSitePageTemplate_addPageTemplate(randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		Assert.assertTrue(Validator.isNotNull(postPageTemplate.getKey()));

		ContentPageTemplate contentPageTemplate = _getContentPageTemplate(
			testGroup);

		postPageTemplate = pageTemplateResource.postSitePageTemplate(
			testGroup.getExternalReferenceCode(), contentPageTemplate);

		Assert.assertEquals(
			contentPageTemplate.getKey(), postPageTemplate.getKey());

		_postSitePageTemplate(
			_getContentPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_postSitePageTemplate(
			_getWidgetPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_testPostSitePageTemplateWithPageSpecifications();
		_testPostSitePageTemplateWithPageTemplateSet();
		_testPostSitePageTemplateWithThumbnail();
		_testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64();
		_testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64();
		_testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndURL();
		_testPostSitePageTemplateWithThumbnailURLReferenceFileBase64();
		_testPostSitePageTemplateWithThumbnailURLReferenceFileBase64AndURL();
		_testPostSitePageTemplateWithThumbnailURLReferenceNonexistingProblemException();
		_testPostSitePageTemplateWithThumbnailURLReferenceURL();
		_testPostSitePageTemplateWithThumbnailURLReferenceURLUnsupportedProtocolProblemException();
		_testPostSitePageTemplateWithWidgetPageTypeIsDeprecated();

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _postSitePageTemplate(
				_getPageTemplate(testGroup),
				testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, companyGroupWidgetPageTemplate) -> {
				_postSitePageTemplate(
					companyGroupWidgetPageTemplate,
					group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST", null,
					() -> pageTemplateResource.postSitePageTemplate(
						group.getExternalReferenceCode(),
						_getContentPageTemplate(group)));
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> pageTemplateResource.postSitePageTemplate(
					group.getExternalReferenceCode(),
					_getPageTemplate(group))));
	}

	@Override
	@Test
	public void testPostSitePageTemplatePageSpecification() throws Exception {
		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = pageTemplateResource.postSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			_getContentPageTemplate(testGroup));

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		PageSpecificationsTestUtil.testPostSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			pageTemplate.getPageSpecifications(), serviceContext,
			contentPageSpecification ->
				pageTemplateResource.postSitePageTemplatePageSpecification(
					testGroup.getExternalReferenceCode(),
					pageTemplate.getExternalReferenceCode(),
					contentPageSpecification));

		PageTemplate widgetPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				_getWidgetPageTemplate(testGroup));

		_assertPostSitePageTemplatePageSpecificationProblemException(
			widgetPageTemplate.getExternalReferenceCode());

		layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext);

		_assertPostSitePageTemplatePageSpecificationProblemException(
			layoutPageTemplateEntry.getExternalReferenceCode());

		layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT);

		_assertPostSitePageTemplatePageSpecificationProblemException(
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	@Override
	@Test
	public void testPostSitePageTemplateSetPageTemplate() throws Exception {
		_testPostSitePageTemplateSetPageTemplate();
	}

	@Override
	@Test
	@TestInfo("LPD-92443")
	public void testPutSitePageTemplate() throws Exception {
		ContentPageTemplate contentPageTemplate = _getContentPageTemplate(
			testGroup);

		_testPutSitePageTemplate(
			contentPageTemplate, testGroup.getExternalReferenceCode());

		_testPutSitePageTemplate(
			_getUpdatedContentPageTemplate(
				testGroup, contentPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		WidgetPageTemplate widgetPageTemplate = _getWidgetPageTemplate(
			testGroup);

		_testPutSitePageTemplate(
			widgetPageTemplate, testGroup.getExternalReferenceCode());

		_testPutSitePageTemplate(
			_getUpdatedWidgetPageTemplate(
				testGroup, widgetPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		WidgetPageTemplate expectedWidgetPageTemplate =
			_getUpdatedWidgetPageTemplate(
				testGroup, widgetPageTemplate.getExternalReferenceCode());

		WidgetPageTemplate putWidgetPageTemplate = new WidgetPageTemplate();

		BeanTestUtil.copyProperties(
			expectedWidgetPageTemplate, putWidgetPageTemplate);

		expectedWidgetPageTemplate.setPageTemplateSettings(
			() -> new WidgetPageTemplateSettings() {
				{
					setLayoutTemplateId(PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID);
					setNavigationSettings(
						new NavigationSettings() {
							{
								setTargetType(TargetType.SPECIFIC_FRAME);
							}
						});
					setType(Type.WIDGET_PAGE_TEMPLATE_SETTINGS);
				}
			});

		putWidgetPageTemplate.setPageTemplateSettings(() -> null);

		assertEquals(
			expectedWidgetPageTemplate,
			pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				putWidgetPageTemplate.getExternalReferenceCode(),
				putWidgetPageTemplate));

		_testPutSitePageTemplateWithPageSpecifications();
		_testPutSitePageTemplateWithPageTemplateSet();
		_testPutSitePageTemplateWithThumbnail();
		_testPutSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64();
		_testPutSitePageTemplateWithThumbnailURLReferenceFileBase64();
		_testPutSitePageTemplateWithThumbnailURLReferenceURL();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		contentPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			contentPageTemplate.getExternalReferenceCode(),
			contentPageTemplate);

		_enableLocalStaging();

		_assertStagingGroupPageTemplateThumbnail(
			fileEntry, contentPageTemplate);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPutSitePageTemplate(
				contentPageTemplate, testGroup.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _testPutSitePageTemplate(
				widgetPageTemplate, testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, curWidgetPageTemplate) -> {
				_testPutSitePageTemplate(
					curWidgetPageTemplate, group.getExternalReferenceCode());

				_testPutSitePageTemplate(
					_getUpdatedWidgetPageTemplate(
						group,
						curWidgetPageTemplate.getExternalReferenceCode()),
					group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST", null,
					() -> {
						ContentPageTemplate curContentPageTemplate =
							_getContentPageTemplate(group);

						pageTemplateResource.putSitePageTemplate(
							group.getExternalReferenceCode(),
							curContentPageTemplate.getExternalReferenceCode(),
							curContentPageTemplate);
					});
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST", null,
				() -> {
					PageTemplate pageTemplate = _getPageTemplate(group);

					pageTemplateResource.putSitePageTemplate(
						group.getExternalReferenceCode(),
						pageTemplate.getExternalReferenceCode(), pageTemplate);
				}));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "description_i18n", "externalReferenceCode", "keywords",
			"name", "name_i18n", "pageTemplateSet", "pageTemplateSettings",
			"taxonomyCategoryBriefs"
		};
	}

	@Override
	protected PageTemplate randomIrrelevantPageTemplate() throws Exception {
		return _getPageTemplate(irrelevantGroup);
	}

	@Override
	protected PageTemplate randomPageTemplate() throws Exception {
		return _getPageTemplate(testGroup);
	}

	@Override
	protected PageTemplate
			testGetSitePageTemplateSetPageTemplatesPage_addPageTemplate(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode,
				PageTemplate pageTemplate)
		throws Exception {

		return pageTemplateResource.postSitePageTemplateSetPageTemplate(
			siteExternalReferenceCode, pageTemplateSetExternalReferenceCode,
			pageTemplate);
	}

	@Override
	protected String
			testGetSitePageTemplateSetPageTemplatesPage_getIrrelevantPageTemplateSetExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getLayoutPageTemplateCollection(irrelevantGroup);

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSitePageTemplateSetPageTemplatesPage_getPageTemplateSetExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getLayoutPageTemplateCollection(testGroup);

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	@Override
	protected PageTemplate testGetSitePageTemplatesPage_addPageTemplate(
			String siteExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		return pageTemplateResource.postSitePageTemplate(
			siteExternalReferenceCode, pageTemplate);
	}

	@Override
	protected Map<String, Map<String, String>>
		testGetSitePageTemplatesPage_getExpectedActions(
			String siteExternalReferenceCode) {

		return new HashMap<>();
	}

	@Override
	protected PageTemplate testPostSitePageTemplate_addPageTemplate(
			PageTemplate pageTemplate)
		throws Exception {

		return testGetSitePageTemplatesPage_addPageTemplate(
			testGroup.getExternalReferenceCode(), pageTemplate);
	}

	@Override
	protected PageTemplate
			testPostSitePageTemplateSetPageTemplate_addPageTemplate(
				PageTemplate pageTemplate)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		return pageTemplateResource.postSitePageTemplateSetPageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplateSet.getExternalReferenceCode(), pageTemplate);
	}

	private void _assertPageSpecifications(
			ContentPageSpecification draftContentPageSpecification,
			ContentPageSpecification publishedContentPageSpecification,
			PageTemplate pageTemplate)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		PageSpecification.Status status = PageSpecification.Status.APPROVED;

		if (layout.isPublished()) {
			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				layoutPageTemplateEntry.getStatus());
		}
		else {
			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT,
				layoutPageTemplateEntry.getStatus());

			status = PageSpecification.Status.DRAFT;
		}

		PageSpecificationsTestUtil.assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			pageTemplate.getPageSpecifications(), layout, status);
	}

	private void _assertPostSitePageTemplatePageSpecificationProblemException(
			String pageTemplateExternalReferenceCode)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST", "The page template must be of type basic",
			() -> pageTemplateResource.postSitePageTemplatePageSpecification(
				testGroup.getExternalReferenceCode(),
				pageTemplateExternalReferenceCode,
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

	private void _assertStagingGroupPageTemplateThumbnail(
			FileEntry liveGroupFileEntry, PageTemplate pageTemplate)
		throws Exception {

		Group stagingGroup = testGroup.getStagingGroup();

		FileEntry stagingGroupFileEntry =
			_portletFileRepository.getPortletFileEntryByExternalReferenceCode(
				liveGroupFileEntry.getExternalReferenceCode(),
				stagingGroup.getGroupId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplate.getExternalReferenceCode(),
					stagingGroup.getGroupId());

		Assert.assertEquals(
			stagingGroupFileEntry.getFileEntryId(),
			layoutPageTemplateEntry.getPreviewFileEntryId());
	}

	private void _assertThumbnailFileEntryId(
			Boolean defaultValue, String pageTemplateExternalReferenceCode,
			String thumbnailExternalReferenceCode)
		throws Exception {

		LayoutPageTemplateEntry pageTemplateEntry =
			_layoutPageTemplateEntryService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, testGroup.getGroupId());

		long fileEntryId = 0;

		if (!defaultValue) {
			FileEntry fileEntry =
				_portletFileRepository.
					getPortletFileEntryByExternalReferenceCode(
						thumbnailExternalReferenceCode, testGroup.getGroupId());

			fileEntryId = fileEntry.getFileEntryId();
		}

		Assert.assertEquals(
			pageTemplateEntry.getPreviewFileEntryId(), fileEntryId);
	}

	private void _assertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			PageTemplate pageTemplate)
		throws Exception {

		Assert.assertNotNull(expectedExternalReferenceCode);

		ThumbnailURLReference thumbnailURLReference =
			pageTemplate.getThumbnailURLReference();

		Assert.assertEquals(
			expectedExternalReferenceCode,
			thumbnailURLReference.getExternalReferenceCode());

		_assertThumbnailFileEntryId(
			false, pageTemplate.getExternalReferenceCode(),
			expectedExternalReferenceCode);

		FileEntry fileEntry =
			PortletFileRepositoryUtil.
				fetchPortletFileEntryByExternalReferenceCode(
					expectedExternalReferenceCode, testGroup.getGroupId());

		try (InputStream inputStream = fileEntry.getContentStream()) {
			Assert.assertArrayEquals(
				expectedBytes, StreamUtil.toByteArray(inputStream));
		}

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		URL url = new URL(
			IdempotentRetryAssert.retryAssert(
				30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
				() -> {
					PageTemplate getPageTemplate =
						pageTemplateResource.getSitePageTemplate(
							testGroup.getExternalReferenceCode(),
							pageTemplate.getExternalReferenceCode());

					ThumbnailURLReference getThumbnailURLReference =
						getPageTemplate.getThumbnailURLReference();

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

	private ContentPageTemplate _getContentPageTemplate(Group group)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		return new ContentPageTemplate() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				keywords = AssetTestUtil.randomKeywords(serviceContext);
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				pageTemplateSet = _getPageTemplateSet(group);
				pageTemplateSettings = new ContentPageTemplateSettings() {
					{
						setType(Type.CONTENT_PAGE_TEMPLATE_SETTINGS);
					}
				};
				taxonomyCategoryBriefs =
					AssetTestUtil.randomTaxonomyCategoryBriefs(
						testCompany.getGroupId(), serviceContext);
				type = Type.CONTENT_PAGE_TEMPLATE;
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	private LayoutPageTemplateCollection _getLayoutPageTemplateCollection(
			Group group)
		throws Exception {

		return _layoutPageTemplateCollectionLocalService.
			addLayoutPageTemplateCollection(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				LayoutPageTemplateConstants.
					PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT,
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LayoutPageTemplateCollectionTypeConstants.BASIC,
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));
	}

	private PageTemplate _getPageTemplate(Group group) throws Exception {
		List<UnsafeSupplier<PageTemplate, Exception>> unsafeSuppliers =
			Arrays.asList(
				() -> _getContentPageTemplate(group),
				() -> _getWidgetPageTemplate(group));

		UnsafeSupplier<PageTemplate, Exception> unsafeSupplier =
			unsafeSuppliers.get(
				RandomTestUtil.randomInt(0, unsafeSuppliers.size() - 1));

		return unsafeSupplier.get();
	}

	private PageTemplateResource _getPageTemplateResource() throws Exception {
		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return PageTemplateResource.builder(
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

	private PageTemplateSet _getPageTemplateSet(Group group) throws Exception {
		if (group.isCompany() || group.isDepot()) {
			return null;
		}

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getLayoutPageTemplateCollection(group);

		return new PageTemplateSet() {
			{
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							layoutPageTemplateCollection.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
								setName(user::getFullName);
							}
						};
					});
				setDateCreated(layoutPageTemplateCollection::getCreateDate);
				setDateModified(layoutPageTemplateCollection::getModifiedDate);
				setDescription(layoutPageTemplateCollection::getDescription);
				setExternalReferenceCode(
					layoutPageTemplateCollection::getExternalReferenceCode);
				setKey(
					layoutPageTemplateCollection::
						getLayoutPageTemplateCollectionKey);
				setName(layoutPageTemplateCollection::getName);
			}
		};
	}

	private long _getSitePageTemplatesPageTotalCount(
			String siteExternalReferenceCode)
		throws Exception {

		Page<PageTemplate> page = pageTemplateResource.getSitePageTemplatesPage(
			siteExternalReferenceCode, null, null, null, null, null);

		return page.getTotalCount();
	}

	private ContentPageTemplate _getUpdatedContentPageTemplate(
			Group group, String pageTemplateExternalReferenceCode)
		throws Exception {

		ContentPageTemplate contentPageTemplate =
			(ContentPageTemplate)pageTemplateResource.getSitePageTemplate(
				group.getExternalReferenceCode(),
				pageTemplateExternalReferenceCode);

		contentPageTemplate.setName(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		contentPageTemplate.setPageTemplateSet(_getPageTemplateSet(group));

		return contentPageTemplate;
	}

	private WidgetPageTemplate _getUpdatedWidgetPageTemplate(
			Group group, String pageTemplateExternalReferenceCode)
		throws Exception {

		WidgetPageTemplate widgetPageTemplate =
			(WidgetPageTemplate)pageTemplateResource.getSitePageTemplate(
				group.getExternalReferenceCode(),
				pageTemplateExternalReferenceCode);

		widgetPageTemplate.setActive(RandomTestUtil.randomBoolean());
		widgetPageTemplate.setDescription_i18n(
			HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
				RandomTestUtil.randomString()
			).build());

		String name = StringUtil.toLowerCase(RandomTestUtil.randomString());

		widgetPageTemplate.setName(name);
		widgetPageTemplate.setName_i18n(
			HashMapBuilder.put(
				LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()), name
			).build());

		widgetPageTemplate.setPageTemplateSet(_getPageTemplateSet(group));
		widgetPageTemplate.setPageTemplateSettings(
			_getWidgetPageTemplateSettings());

		return widgetPageTemplate;
	}

	private WidgetPageTemplate _getWidgetPageTemplate(Group group)
		throws Exception {

		String randomName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		return new WidgetPageTemplate() {
			{
				active = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description_i18n = HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					RandomTestUtil.randomString()
				).build();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hiddenFromNavigation = RandomTestUtil.randomBoolean();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				keywords = AssetTestUtil.randomKeywords(serviceContext);
				name = randomName;
				name_i18n = HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					randomName
				).build();
				pageTemplateSet = _getPageTemplateSet(group);
				pageTemplateSettings = _getWidgetPageTemplateSettings();
				taxonomyCategoryBriefs =
					AssetTestUtil.randomTaxonomyCategoryBriefs(
						testCompany.getGroupId(), serviceContext);
				type = PageTemplate.Type.WIDGET_PAGE_TEMPLATE;
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	private WidgetPageTemplateSettings _getWidgetPageTemplateSettings() {
		return new WidgetPageTemplateSettings() {
			{
				setLayoutTemplateId(
					() -> {
						if (RandomTestUtil.randomBoolean()) {
							return "1_column";
						}

						return "2_columns_ii";
					});
				setNavigationSettings(
					new NavigationSettings() {
						{
							if (RandomTestUtil.randomBoolean()) {
								setTarget("_blank");
								setTargetType(() -> TargetType.NEW_TAB);
							}
							else {
								setTarget(RandomTestUtil::randomString);
								setTargetType(() -> TargetType.SPECIFIC_FRAME);
							}
						}
					});
				setType(Type.WIDGET_PAGE_TEMPLATE_SETTINGS);
			}
		};
	}

	private void _postSitePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.postSitePageTemplate(
				siteExternalReferenceCode, pageTemplate));
	}

	private PageTemplate _postSitePageTemplateAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		PageTemplate pageTemplate = randomPageTemplate();

		pageTemplate.setThumbnailURLReference(thumbnailURLReference);

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference postThumbnailURLReference =
				postPageTemplate.getThumbnailURLReference();

			expectedExternalReferenceCode =
				postThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, postPageTemplate);

		return postPageTemplate;
	}

	private void _postSitePageTemplateSetPageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		assertEquals(
			pageTemplate,
			pageTemplateResource.postSitePageTemplateSetPageTemplate(
				siteExternalReferenceCode,
				pageTemplateSet.getExternalReferenceCode(), pageTemplate));
	}

	private PageTemplate _putSitePageTemplateAndAssertThumbnailURLReference(
			byte[] expectedBytes, String expectedExternalReferenceCode,
			PageTemplate pageTemplate,
			ThumbnailURLReference thumbnailURLReference)
		throws Exception {

		pageTemplate.setThumbnailURLReference(thumbnailURLReference);

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(), pageTemplate);

		if (expectedExternalReferenceCode == null) {
			ThumbnailURLReference putThumbnailURLReference =
				putPageTemplate.getThumbnailURLReference();

			expectedExternalReferenceCode =
				putThumbnailURLReference.getExternalReferenceCode();
		}

		_assertThumbnailURLReference(
			expectedBytes, expectedExternalReferenceCode, putPageTemplate);

		return putPageTemplate;
	}

	private void _testCreatingPageTemplateSetWithLazyReferencingEnabled(
			UnsafeFunction<PageTemplateSet, PageTemplate, Exception>
				unsafeFunction)
		throws Exception {

		PageTemplateSet pageTemplateSet = new PageTemplateSet() {
			{
				setDescription(RandomTestUtil::randomString);
				setExternalReferenceCode(RandomTestUtil::randomString);
				setName(RandomTestUtil::randomString);
			}
		};

		try {
			unsafeFunction.apply(pageTemplateSet);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(problemException);
			}

			Problem problem = problemException.getProblem();

			Assert.assertEquals("IllegalArgumentException", problem.getType());
		}

		Assert.assertNull(
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollectionByExternalReferenceCode(
					pageTemplateSet.getExternalReferenceCode(),
					testGroup.getGroupId()));

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			PageTemplate pageTemplate = unsafeFunction.apply(pageTemplateSet);

			PageTemplateSet addedPageTemplateSet =
				pageTemplate.getPageTemplateSet();

			Assert.assertEquals(
				pageTemplateSet.getExternalReferenceCode(),
				addedPageTemplateSet.getExternalReferenceCode());

			Assert.assertNotNull(
				_layoutPageTemplateCollectionLocalService.
					fetchLayoutPageTemplateCollectionByExternalReferenceCode(
						pageTemplateSet.getExternalReferenceCode(),
						testGroup.getGroupId()));
		}
	}

	private void _testDeleteSitePageTemplate(
			Group group, String pageTemplateExternalReferenceCode)
		throws Exception {

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, group.getGroupId()));

		pageTemplateResource.deleteSitePageTemplate(
			group.getExternalReferenceCode(),
			pageTemplateExternalReferenceCode);

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, group.getGroupId()));

		_assertProblemException(
			"NOT_FOUND", null,
			() -> pageTemplateResource.deleteSitePageTemplate(
				group.getExternalReferenceCode(),
				pageTemplateExternalReferenceCode));
	}

	private void _testGetSitePageTemplate(PageTemplate pageTemplate)
		throws Exception {

		PageTemplate getPageTemplate = pageTemplateResource.getSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode());

		assertEquals(pageTemplate, getPageTemplate);
		assertValid(getPageTemplate);
	}

	private void _testGetSitePageTemplatesPageWithThumbnailAsNestedField()
		throws Exception {

		PageTemplate randomPageTemplate = randomPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		randomPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		PageTemplate postPageTemplate =
			testPostSitePageTemplate_addPageTemplate(randomPageTemplate);

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		IdempotentRetryAssert.retryAssert(
			30, TimeUnit.SECONDS, 500, TimeUnit.MILLISECONDS,
			() -> {
				PageTemplate getPageTemplate =
					pageTemplateResource.getSitePageTemplate(
						testGroup.getExternalReferenceCode(),
						postPageTemplate.getExternalReferenceCode());

				ThumbnailURLReference thumbnailURLReference =
					getPageTemplate.getThumbnailURLReference();

				Assert.assertNotNull(thumbnailURLReference);

				return thumbnailURLReference.getUrl();
			});

		Page<PageTemplate> page = pageTemplateResource.getSitePageTemplatesPage(
			testGroup.getExternalReferenceCode(), null, null, null, null, null);

		for (PageTemplate pageTemplate : page.getItems()) {
			if (StringUtil.equals(
					pageTemplate.getExternalReferenceCode(),
					postPageTemplate.getExternalReferenceCode())) {

				ThumbnailURLReference thumbnail =
					pageTemplate.getThumbnailURLReference();

				_assertThumbnailURLReference(
					_thumbnail1Bytes, thumbnail.getExternalReferenceCode(),
					pageTemplate);
			}
			else {
				Assert.assertNull(pageTemplate.getThumbnailURLReference());
			}
		}
	}

	private void _testGetSitePageTemplateWithNestedFields(
			PageTemplate pageTemplate)
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		PageTemplate getPageTemplate = pageTemplateResource.getSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			postPageTemplate.getExternalReferenceCode());

		assertEquals(postPageTemplate, getPageTemplate);
		assertValid(getPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					getPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		PageSpecificationsTestUtil.assertPageSpecifications(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			getPageTemplate.getPageSpecifications());
	}

	private void _testPatchSitePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.patchSitePageTemplate(
				siteExternalReferenceCode,
				pageTemplate.getExternalReferenceCode(), pageTemplate));
	}

	private void
			_testPatchSitePageTemplateContentPageTemplateWithPageSpecifications(
				PageSpecification.Status newDraftLayoutStatus,
				PageSpecification.Status newPublishedLayoutStatus,
				PageSpecification.Status oldDraftLayoutStatus,
				PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = _getContentPageTemplate(testGroup);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			postPageTemplate);

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);

		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			pageTemplateResource.patchSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				postPageTemplate.getExternalReferenceCode(),
				new ContentPageTemplate() {
					{
						setPageSpecifications(
							() -> new PageSpecification[] {
								publishedContentPageSpecification,
								draftContentPageSpecification
							});
						setType(Type.CONTENT_PAGE_TEMPLATE);
					}
				}));
	}

	private void _testPatchSitePageTemplateWidgetPageTemplateWithPageSpecifications()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = pageTemplateResource.getSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			layoutPageTemplateEntry.getExternalReferenceCode());

		WidgetPageTemplateSettings widgetPageTemplateSettings =
			(WidgetPageTemplateSettings)pageTemplate.getPageTemplateSettings();

		String layoutTemplateId = "1_column";

		if (Objects.equals(
				layoutTemplateId,
				widgetPageTemplateSettings.getLayoutTemplateId())) {

			layoutTemplateId = "2_columns_ii";
		}

		widgetPageTemplateSettings.setLayoutTemplateId(layoutTemplateId);

		PageSpecification[] patchPageSpecifications =
			PageSpecificationsTestUtil.getWidgetPageSpecifications(
				null, layoutTemplateId,
				pageTemplate.getExternalReferenceCode());

		pageTemplate = pageTemplateResource.patchSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(),
			new WidgetPageTemplate() {
				{
					setPageSpecifications(() -> patchPageSpecifications);
					setPageTemplateSettings(widgetPageTemplateSettings);
					setType(Type.WIDGET_PAGE_TEMPLATE);
				}
			});

		WidgetPageSpecification widgetPageSpecification =
			(WidgetPageSpecification)patchPageSpecifications[0];

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			pageTemplate.getPageSpecifications(), widgetPageSpecification);

		WidgetPageTemplateSettings patchWidgetPageTemplateSettings =
			(WidgetPageTemplateSettings)pageTemplate.getPageTemplateSettings();

		Assert.assertEquals(
			layoutTemplateId,
			patchWidgetPageTemplateSettings.getLayoutTemplateId());

		SettingsTestUtil.modifySettings(
			FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE, serviceContext,
			widgetPageSpecification.getSettings());

		pageTemplate = pageTemplateResource.patchSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(),
			new WidgetPageTemplate() {
				{
					setPageSpecifications(
						() -> new PageSpecification[] {
							PageSpecificationsTestUtil.
								getWidgetPageSpecification(
									null, null,
									widgetPageSpecification.getSettings(),
									PageSpecification.Status.APPROVED,
									widgetPageSpecification.
										getWidgetPageSections())
						});
					setType(Type.WIDGET_PAGE_TEMPLATE);
				}
			});

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		SettingsTestUtil.assertPageSpecificationSetting(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			widgetPageSpecification.getSettings());

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			pageTemplate.getPageSpecifications(), widgetPageSpecification);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private void _testPatchSitePageTemplateWithPageSpecifications()
		throws Exception {

		_testPatchSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPatchSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPatchSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPatchSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPatchSitePageTemplateWidgetPageTemplateWithPageSpecifications();
	}

	private void _testPatchSitePageTemplateWithPageTemplateSet()
		throws Exception {

		PageTemplate pageTemplate =
			testPostSitePageTemplateSetPageTemplate_addPageTemplate(
				randomPageTemplate());

		PageTemplate patchPageTemplate;

		if (pageTemplate.getType() == PageTemplate.Type.CONTENT_PAGE_TEMPLATE) {
			patchPageTemplate = new ContentPageTemplate() {
				{
					setPageTemplateSet(
						() -> new PageTemplateSet() {
							{
								setExternalReferenceCode(
									() -> StringPool.BLANK);
							}
						});
					setType(() -> Type.CONTENT_PAGE_TEMPLATE);
				}
			};
		}
		else {
			patchPageTemplate = new WidgetPageTemplate() {
				{
					setPageTemplateSet(
						() -> new PageTemplateSet() {
							{
								setExternalReferenceCode(
									() -> StringPool.BLANK);
							}
						});
					setType(() -> Type.WIDGET_PAGE_TEMPLATE);
				}
			};
		}

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateResource.patchSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), patchPageTemplate));

		_testCreatingPageTemplateSetWithLazyReferencingEnabled(
			pageTemplateSet -> {
				patchPageTemplate.setPageTemplateSet(pageTemplateSet);

				return pageTemplateResource.patchSitePageTemplate(
					testGroup.getExternalReferenceCode(),
					pageTemplate.getExternalReferenceCode(), patchPageTemplate);
			});
	}

	private void _testPatchSitePageTemplateWithThumbnail() throws Exception {
		PageTemplate pageTemplate = randomPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		pageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postPageTemplate);

		FileEntry newFileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		pageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				newFileEntry, RandomTestUtil.randomString()));

		PageTemplate patchPageTemplate =
			pageTemplateResource.patchSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, newFileEntry.getExternalReferenceCode(),
			patchPageTemplate);

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		pageTemplate.setThumbnailURLReference(thumbnailURLReference);

		try {
			pageTemplateResource.patchSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void _testPostPageTemplateThumbnailURLReferenceProblemException(
			String expectedTitle, String externalReferenceCode, String url)
		throws Exception {

		PageTemplate pageTemplate = randomPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(url);

		pageTemplate.setThumbnailURLReference(thumbnailURLReference);

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		try {
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(expectedTitle, problem.getTitle());
		}
	}

	private void
			_testPostSitePageTemplateContentPageTemplateWithPageSpecifications(
				PageSpecification.Status draftLayoutStatus,
				PageSpecification.Status publishedLayoutStatus)
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = _getContentPageTemplate(testGroup);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), draftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), publishedLayoutStatus);

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate));
	}

	private void _testPostSitePageTemplateSetPageTemplate() throws Exception {
		PageTemplate randomPageTemplate = randomPageTemplate();

		PageTemplate postPageTemplate =
			testPostSitePageTemplateSetPageTemplate_addPageTemplate(
				randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		_postSitePageTemplateSetPageTemplate(
			_getContentPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_postSitePageTemplateSetPageTemplate(
			_getWidgetPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());
	}

	private void _testPostSitePageTemplateWidgetPageTemplateWithPageSpecifications()
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = _getWidgetPageTemplate(testGroup);

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				PageSpecificationsTestUtil.getContentPageSpecification(
					null, testGroup.getGroupId(),
					PageSpecification.Status.APPROVED)
			});

		_assertProblemException(
			"BAD_REQUEST",
			"The page specification is invalid or has not been approved",
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		WidgetPageTemplateSettings widgetPageTemplateSettings =
			(WidgetPageTemplateSettings)pageTemplate.getPageTemplateSettings();

		WidgetPageSpecification widgetPageSpecification =
			PageSpecificationsTestUtil.getWidgetPageSpecification(
				null, pageTemplate.getExternalReferenceCode(), null,
				PageSpecification.Status.APPROVED,
				PageSpecificationsTestUtil.getWidgetPageSections(
					widgetPageTemplateSettings.getLayoutTemplateId()));

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				widgetPageSpecification, widgetPageSpecification
			});

		_assertProblemException(
			"BAD_REQUEST", "Exactly one page specification is required",
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		widgetPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {widgetPageSpecification});

		_assertProblemException(
			"BAD_REQUEST",
			"The page specification is invalid or has not been approved",
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		widgetPageSpecification.setExternalReferenceCode(
			RandomTestUtil::randomString);
		widgetPageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			"The provided external reference code does not point to a widget " +
				"page specification",
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		widgetPageSpecification.setExternalReferenceCode(
			pageTemplate.getExternalReferenceCode());

		widgetPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId())));

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			postPageTemplate.getPageSpecifications(), widgetPageSpecification);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			postPageTemplate.getExternalReferenceCode(),
			testGroup.getGroupId());
	}

	private void _testPostSitePageTemplateWithPageSpecifications()
		throws Exception {

		_testPostSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPostSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPostSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED);
		_testPostSitePageTemplateContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT);
		_testPostSitePageTemplateWidgetPageTemplateWithPageSpecifications();
	}

	private void _testPostSitePageTemplateWithPageTemplateSet()
		throws Exception {

		PageTemplate pageTemplate = randomPageTemplate();

		pageTemplate.setPageTemplateSet(() -> null);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		pageTemplate.setPageTemplateSet(
			() -> new PageTemplateSet() {
				{
					setExternalReferenceCode(() -> StringPool.BLANK);
				}
			});

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> _postSitePageTemplate(
				pageTemplate, testGroup.getExternalReferenceCode()));

		_testCreatingPageTemplateSetWithLazyReferencingEnabled(
			pageTemplateSet -> {
				pageTemplate.setPageTemplateSet(pageTemplateSet);

				return pageTemplateResource.postSitePageTemplate(
					testGroup.getExternalReferenceCode(), pageTemplate);
			});
	}

	private void _testPostSitePageTemplateWithThumbnail() throws Exception {
		PageTemplate pageTemplate = randomPageTemplate();

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		pageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry, null));

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate postPageTemplate =
			pageTemplateResource.postSitePageTemplate(
				testGroup.getExternalReferenceCode(), pageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry.getExternalReferenceCode(),
			postPageTemplate);

		pageTemplate = randomPageTemplate();

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		pageTemplate.setThumbnailURLReference(thumbnailURLReference);

		try {
			testPostSitePageTemplateSetPageTemplate_addPageTemplate(
				pageTemplate);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeEmptyAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(StringPool.BLANK);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndFileBase64()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeNullAndURL()
		throws Exception {

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, null, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceFileBase64()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceFileBase64AndURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail1Base64);
		thumbnailURLReference.setUrl(_thumbnail2URL);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceNonexistingProblemException()
		throws Exception {

		_testPostPageTemplateThumbnailURLReferenceProblemException(
			"Unable to resolve file", RandomTestUtil.randomString(), null);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceURL()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setUrl(_thumbnail1URL);

		_postSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, thumbnailURLReference);
	}

	private void _testPostSitePageTemplateWithThumbnailURLReferenceURLUnsupportedProtocolProblemException()
		throws Exception {

		String url =
			"ftp://invalid.example.test/" + RandomTestUtil.randomString();

		_testPostPageTemplateThumbnailURLReferenceProblemException(
			"Unable to download file from " + url +
				" because of unsupported protocol ftp",
			RandomTestUtil.randomString(), url);
	}

	private void _testPostSitePageTemplateWithWidgetPageTypeIsDeprecated()
		throws Exception {

		PropsUtil.set("feature.flag.LPD-76864", "false");

		try {
			HttpInvoker.HttpResponse httpResponse =
				pageTemplateResource.postSitePageTemplateHttpResponse(
					testGroup.getExternalReferenceCode(),
					_getWidgetPageTemplate(testGroup));

			String content = httpResponse.getContent();

			Assert.assertEquals(content, 400, httpResponse.getStatusCode());
			Assert.assertTrue(
				content,
				content.contains(
					"Feature flag LPD-76864 is disabled for company"));
		}
		finally {
			PropsUtil.set("feature.flag.LPD-76864", "true");
		}
	}

	private void _testPutSiteContentPageTemplateWithPageSpecifications(
			PageSpecification.Status newDraftLayoutStatus,
			PageSpecification.Status newPublishedLayoutStatus,
			PageSpecification.Status oldDraftLayoutStatus,
			PageSpecification.Status oldPublishedLayoutStatus)
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = _getContentPageTemplate(testGroup);

		ContentPageSpecification draftContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				null, testGroup.getGroupId(), oldDraftLayoutStatus);

		ContentPageSpecification publishedContentPageSpecification =
			PageSpecificationsTestUtil.getContentPageSpecification(
				draftContentPageSpecification.getExternalReferenceCode(),
				testGroup.getGroupId(), oldPublishedLayoutStatus);

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			});

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate));

		draftContentPageSpecification.setStatus(newDraftLayoutStatus);

		publishedContentPageSpecification.setStatus(newPublishedLayoutStatus);

		_assertPageSpecifications(
			draftContentPageSpecification, publishedContentPageSpecification,
			pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate));
	}

	private void _testPutSitePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.putSitePageTemplate(
				siteExternalReferenceCode,
				pageTemplate.getExternalReferenceCode(), pageTemplate));
	}

	private void _testPutSitePageTemplateWidgetPageTemplateWithPageSpecifications()
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate = _getWidgetPageTemplate(testGroup);

		WidgetPageTemplateSettings widgetPageTemplateSettings =
			(WidgetPageTemplateSettings)pageTemplate.getPageTemplateSettings();

		WidgetPageSpecification widgetPageSpecification =
			PageSpecificationsTestUtil.getWidgetPageSpecification(
				null, pageTemplate.getExternalReferenceCode(),
				SettingsTestUtil.getSettings(
					FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE,
					ServiceContextTestUtil.getServiceContext(
						testGroup.getGroupId(), TestPropsValues.getUserId())),
				PageSpecification.Status.APPROVED,
				PageSpecificationsTestUtil.getWidgetPageSections(
					widgetPageTemplateSettings.getLayoutTemplateId()));

		pageTemplate.setPageSpecifications(
			() -> new PageSpecification[] {widgetPageSpecification});

		PageTemplate putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(), pageTemplate);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			putPageTemplate.getPageSpecifications(), widgetPageSpecification);

		String layoutTemplateId = "1_column";

		if (Objects.equals(
				layoutTemplateId,
				widgetPageTemplateSettings.getLayoutTemplateId())) {

			layoutTemplateId = "2_columns_ii";
		}

		widgetPageTemplateSettings.setLayoutTemplateId(layoutTemplateId);
		widgetPageSpecification.setWidgetPageSections(
			PageSpecificationsTestUtil.getWidgetPageSections(layoutTemplateId));

		putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(), pageTemplate);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			putPageTemplate.getPageSpecifications(), widgetPageSpecification);

		WidgetPageTemplateSettings putWidgetPageTemplateSettings =
			(WidgetPageTemplateSettings)
				putPageTemplate.getPageTemplateSettings();

		Assert.assertEquals(
			layoutTemplateId,
			putWidgetPageTemplateSettings.getLayoutTemplateId());

		widgetPageSpecification.setSettings(
			SettingsTestUtil.getSettings(
				FavIcon.FavIconType.CLIENT_EXTENSION,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId())));

		putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(), pageTemplate);

		PageSpecificationsTestUtil.assertWidgetPageSpecifications(
			putPageTemplate.getPageSpecifications(), widgetPageSpecification);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			putPageTemplate.getExternalReferenceCode(), testGroup.getGroupId());
	}

	private void _testPutSitePageTemplateWithPageSpecifications()
		throws Exception {

		_testPutSiteContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED);
		_testPutSiteContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT,
			PageSpecification.Status.DRAFT);
		_testPutSiteContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED,
			PageSpecification.Status.APPROVED);
		_testPutSiteContentPageTemplateWithPageSpecifications(
			PageSpecification.Status.DRAFT, PageSpecification.Status.DRAFT,
			PageSpecification.Status.APPROVED, PageSpecification.Status.DRAFT);
		_testPutSitePageTemplateWidgetPageTemplateWithPageSpecifications();
	}

	private void _testPutSitePageTemplateWithPageTemplateSet()
		throws Exception {

		PageTemplate pageTemplate =
			testPostSitePageTemplateSetPageTemplate_addPageTemplate(
				randomPageTemplate());

		pageTemplate.setPageTemplateSet(() -> null);

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate));

		pageTemplate.setPageTemplateSet(
			() -> new PageTemplateSet() {
				{
					setExternalReferenceCode(() -> StringPool.BLANK);
				}
			});

		_assertProblemException(
			"BAD_REQUEST", null,
			() -> pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode(), pageTemplate));

		_testCreatingPageTemplateSetWithLazyReferencingEnabled(
			pageTemplateSet -> {
				pageTemplate.setPageTemplateSet(pageTemplateSet);

				return pageTemplateResource.putSitePageTemplate(
					testGroup.getExternalReferenceCode(),
					pageTemplate.getExternalReferenceCode(), pageTemplate);
			});
	}

	private void _testPutSitePageTemplateWithThumbnail() throws Exception {
		PageTemplate pageTemplate = randomPageTemplate();

		pageTemplate.setExternalReferenceCode(RandomTestUtil.randomString());

		FileEntry fileEntry1 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		pageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry1, null));

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			pageTemplate.getExternalReferenceCode(), pageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry1.getExternalReferenceCode(),
			putPageTemplate);

		FileEntry fileEntry2 = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		putPageTemplate.setThumbnailURLReference(
			() -> ThumbnailURLReferenceUtil.getThumbnailURLReference(
				fileEntry2, null));

		putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			putPageTemplate.getExternalReferenceCode(), putPageTemplate);

		_assertThumbnailURLReference(
			_thumbnail1Bytes, fileEntry2.getExternalReferenceCode(),
			putPageTemplate);

		putPageTemplate.setThumbnailURLReference(() -> null);

		putPageTemplate = pageTemplateResource.putSitePageTemplate(
			testGroup.getExternalReferenceCode(),
			putPageTemplate.getExternalReferenceCode(), putPageTemplate);

		_assertThumbnailFileEntryId(
			true, putPageTemplate.getExternalReferenceCode(), null);

		ThumbnailURLReference thumbnailURLReference =
			ThumbnailURLReferenceUtil.getRandomThumbnailURLReference();

		putPageTemplate.setThumbnailURLReference(thumbnailURLReference);

		try {
			pageTemplateResource.putSitePageTemplate(
				testGroup.getExternalReferenceCode(),
				putPageTemplate.getExternalReferenceCode(), putPageTemplate);
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertEquals(
				"Unable to download file from " +
					thumbnailURLReference.getUrl(),
				problem.getTitle());
		}
	}

	private void _testPutSitePageTemplateWithThumbnailURLReferenceExternalReferenceCodeAndFileBase64()
		throws Exception {

		PageTemplate postPageTemplate =
			testPostSitePageTemplate_addPageTemplate(randomPageTemplate());

		Assert.assertNull(postPageTemplate.getThumbnailURLReference());

		FileEntry fileEntry = FileEntryTestUtil.addPreviewFileEntry(
			testGroup, _portletFileRepository, getClass());

		String externalReferenceCode = fileEntry.getExternalReferenceCode();

		ThumbnailURLReference thumbnailURLReference =
			new ThumbnailURLReference();

		thumbnailURLReference.setExternalReferenceCode(externalReferenceCode);
		thumbnailURLReference.setFileBase64(_thumbnail2Base64);

		_putSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail1Bytes, externalReferenceCode, postPageTemplate,
			thumbnailURLReference);
	}

	private void _testPutSitePageTemplateWithThumbnailURLReferenceFileBase64()
		throws Exception {

		PageTemplate postPageTemplate =
			testPostSitePageTemplate_addPageTemplate(randomPageTemplate());

		Assert.assertNull(postPageTemplate.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setFileBase64(_thumbnail1Base64);

		PageTemplate putPageTemplate =
			_putSitePageTemplateAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postPageTemplate,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setFileBase64(_thumbnail2Base64);

		_putSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putPageTemplate, thumbnailURLReference2);
	}

	private void _testPutSitePageTemplateWithThumbnailURLReferenceURL()
		throws Exception {

		PageTemplate postPageTemplate =
			testPostSitePageTemplate_addPageTemplate(randomPageTemplate());

		Assert.assertNull(postPageTemplate.getThumbnailURLReference());

		ThumbnailURLReference thumbnailURLReference1 =
			new ThumbnailURLReference();

		thumbnailURLReference1.setUrl(_thumbnail1URL);

		PageTemplate putPageTemplate =
			_putSitePageTemplateAndAssertThumbnailURLReference(
				_thumbnail1Bytes, null, postPageTemplate,
				thumbnailURLReference1);

		ThumbnailURLReference thumbnailURLReference2 =
			new ThumbnailURLReference();

		thumbnailURLReference2.setUrl(_thumbnail2URL);

		_putSitePageTemplateAndAssertThumbnailURLReference(
			_thumbnail2Bytes, null, putPageTemplate, thumbnailURLReference2);
	}

	private void _withCompanyGroupWidgetPageTemplate(
			UnsafeBiConsumer<Group, WidgetPageTemplate, Exception>
				unsafeBiConsumer)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group group = company.getGroup();

		WidgetPageTemplate widgetPageTemplate = _getWidgetPageTemplate(group);

		try {
			unsafeBiConsumer.accept(group, widgetPageTemplate);
		}
		finally {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByExternalReferenceCode(
						widgetPageTemplate.getExternalReferenceCode(),
						group.getGroupId());

			if (layoutPageTemplateEntry != null) {
				_layoutPageTemplateEntryLocalService.
					deleteLayoutPageTemplateEntry(layoutPageTemplateEntry);
			}
		}
	}

	private void _withDepotEntry(
			UnsafeConsumer<Group, Exception> unsafeConsumer)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(), DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		try {
			unsafeConsumer.accept(depotEntry.getGroup());
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PageTemplateResourceTest.class);

	private static String _thumbnail1Base64;
	private static byte[] _thumbnail1Bytes;
	private static String _thumbnail1URL;
	private static String _thumbnail2Base64;
	private static byte[] _thumbnail2Bytes;
	private static String _thumbnail2URL;
	private static ThumbnailHttpServer _thumbnailHttpServer;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private UserLocalService _userLocalService;

}