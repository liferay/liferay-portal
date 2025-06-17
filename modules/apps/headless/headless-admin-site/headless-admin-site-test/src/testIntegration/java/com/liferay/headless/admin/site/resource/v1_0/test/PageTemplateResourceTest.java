/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.client.resource.v1_0.PageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
public class PageTemplateResourceTest extends BasePageTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate pageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				randomPageTemplate());

		_testDeleteSiteSiteByExternalReferenceCodePageTemplate(
			testGroup, pageTemplate.getExternalReferenceCode());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageTemplateResource.
					deleteSiteSiteByExternalReferenceCodePageTemplate(
						testGroup.getExternalReferenceCode(),
						pageTemplate.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> {
				_postSiteSiteByExternalReferenceCodePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode());

				_testDeleteSiteSiteByExternalReferenceCodePageTemplate(
					group, widgetPageTemplate.getExternalReferenceCode());
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() ->
					pageTemplateResource.
						deleteSiteSiteByExternalReferenceCodePageTemplate(
							group.getExternalReferenceCode(),
							RandomTestUtil.randomString())));
	}

	@Ignore
	@Override
	@Test
	public void testGetSitePageTemplatePermissionsPage() throws Exception {
		super.testGetSitePageTemplatePermissionsPage();
	}

	@Override
	@Test
	@TestInfo("LPD-44414")
	public void testGetSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate pageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				randomPageTemplate());

		_testGetSiteSiteByExternalReferenceCodePageTemplate(pageTemplate);

		_testGetSiteSiteByExternalReferenceCodePageTemplateWithNestedFields();

		_assertProblemException(
			"NOT_FOUND",
			() ->
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplate(
						testGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString()));

		_enableLocalStaging();

		_testGetSiteSiteByExternalReferenceCodePageTemplate(pageTemplate);

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) ->
				_postSiteSiteByExternalReferenceCodePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode()));

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() ->
					pageTemplateResource.
						getSiteSiteByExternalReferenceCodePageTemplate(
							group.getExternalReferenceCode(),
							RandomTestUtil.randomString())));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage();
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPage()
		throws Exception {

		super.testGetSiteSiteByExternalReferenceCodePageTemplatesPage();

		long totalCount =
			_getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
				testGroup.getExternalReferenceCode());

		_enableLocalStaging();

		Assert.assertEquals(
			totalCount,
			_getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
				testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> {
				long curTotalCount =
					_getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
						group.getExternalReferenceCode());

				_postSiteSiteByExternalReferenceCodePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode());

				Assert.assertEquals(
					curTotalCount + 1,
					_getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
						group.getExternalReferenceCode()));
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() ->
					_getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
						group.getExternalReferenceCode())));
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDateTime()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDouble()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortInteger()
		throws Exception {

		super.
			testGetSiteSiteByExternalReferenceCodePageTemplatesPageWithSortInteger();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		ContentPageTemplate contentPageTemplate =
			(ContentPageTemplate)
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplate(
						testGroup.getExternalReferenceCode(),
						_getContentPageTemplate(testGroup));

		_testPatchSiteSiteByExternalReferenceCodePageTemplate(
			_getUpdatedContentPageTemplate(
				testGroup, contentPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		WidgetPageTemplate widgetPageTemplate =
			(WidgetPageTemplate)
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplate(
						testGroup.getExternalReferenceCode(),
						_getWidgetPageTemplate(testGroup));

		_testPatchSiteSiteByExternalReferenceCodePageTemplate(
			_getUpdatedWidgetPageTemplate(
				testGroup, widgetPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST",
			() -> _testPatchSiteSiteByExternalReferenceCodePageTemplate(
				contentPageTemplate, testGroup.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() -> _testPatchSiteSiteByExternalReferenceCodePageTemplate(
				widgetPageTemplate, testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, curWidgetPageTemplate) -> {
				_postSiteSiteByExternalReferenceCodePageTemplate(
					curWidgetPageTemplate, group.getExternalReferenceCode());

				_testPatchSiteSiteByExternalReferenceCodePageTemplate(
					_getUpdatedWidgetPageTemplate(
						group,
						curWidgetPageTemplate.getExternalReferenceCode()),
					group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST",
					() -> {
						ContentPageTemplate curContentPageTemplate =
							_getContentPageTemplate(group);

						pageTemplateResource.
							putSiteSiteByExternalReferenceCodePageTemplate(
								group.getExternalReferenceCode(),
								curContentPageTemplate.
									getExternalReferenceCode(),
								curContentPageTemplate);
					});
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() -> {
					PageTemplate pageTemplate = _getPageTemplate(group);

					pageTemplateResource.
						putSiteSiteByExternalReferenceCodePageTemplate(
							group.getExternalReferenceCode(),
							pageTemplate.getExternalReferenceCode(),
							pageTemplate);
				}));
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		_testPostSiteSiteByExternalReferenceCodePageTemplate();
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplatePageSpecification()
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate =
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplate(
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

		PageSpecificationsTestUtil.
			testPostSiteSiteByExternalReferenceCodePageSpecification(
				_layoutLocalService.getLayout(
					layoutPageTemplateEntry.getPlid()),
				pageTemplate.getPageSpecifications(), serviceContext,
				contentPageSpecification ->
					pageTemplateResource.
						postSiteSiteByExternalReferenceCodePageTemplatePageSpecification(
							testGroup.getExternalReferenceCode(),
							pageTemplate.getExternalReferenceCode(),
							contentPageSpecification));

		PageTemplate widgetPageTemplate =
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplate(
					testGroup.getExternalReferenceCode(),
					_getWidgetPageTemplate(testGroup));

		_assertPostSiteSiteByExternalReferenceCodePageTemplatePageSpecificationProblemException(
			widgetPageTemplate.getExternalReferenceCode());

		layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext);

		_assertPostSiteSiteByExternalReferenceCodePageTemplatePageSpecificationProblemException(
			layoutPageTemplateEntry.getExternalReferenceCode());

		layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT);

		_assertPostSiteSiteByExternalReferenceCodePageTemplatePageSpecificationProblemException(
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate()
		throws Exception {

		_testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate();
	}

	@Ignore
	@Override
	@Test
	public void testPutSitePageTemplatePermissionsPage() throws Exception {
		super.testPutSitePageTemplatePermissionsPage();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		ContentPageTemplate contentPageTemplate = _getContentPageTemplate(
			testGroup);

		_testPutSiteSiteByExternalReferenceCodePageTemplate(
			contentPageTemplate, testGroup.getExternalReferenceCode());

		_testPutSiteSiteByExternalReferenceCodePageTemplate(
			_getUpdatedContentPageTemplate(
				testGroup, contentPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		WidgetPageTemplate widgetPageTemplate = _getWidgetPageTemplate(
			testGroup);

		_testPutSiteSiteByExternalReferenceCodePageTemplate(
			widgetPageTemplate, testGroup.getExternalReferenceCode());

		_testPutSiteSiteByExternalReferenceCodePageTemplate(
			_getUpdatedWidgetPageTemplate(
				testGroup, widgetPageTemplate.getExternalReferenceCode()),
			testGroup.getExternalReferenceCode());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST",
			() -> _testPutSiteSiteByExternalReferenceCodePageTemplate(
				contentPageTemplate, testGroup.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() -> _testPutSiteSiteByExternalReferenceCodePageTemplate(
				widgetPageTemplate, testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, curWidgetPageTemplate) -> {
				_testPutSiteSiteByExternalReferenceCodePageTemplate(
					curWidgetPageTemplate, group.getExternalReferenceCode());

				_testPutSiteSiteByExternalReferenceCodePageTemplate(
					_getUpdatedWidgetPageTemplate(
						group,
						curWidgetPageTemplate.getExternalReferenceCode()),
					group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST",
					() -> {
						ContentPageTemplate curContentPageTemplate =
							_getContentPageTemplate(group);

						pageTemplateResource.
							putSiteSiteByExternalReferenceCodePageTemplate(
								group.getExternalReferenceCode(),
								curContentPageTemplate.
									getExternalReferenceCode(),
								curContentPageTemplate);
					});
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() -> {
					PageTemplate pageTemplate = _getPageTemplate(group);

					pageTemplateResource.
						putSiteSiteByExternalReferenceCodePageTemplate(
							group.getExternalReferenceCode(),
							pageTemplate.getExternalReferenceCode(),
							pageTemplate);
				}));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"active", "description_i18n", "externalReferenceCode", "name",
			"name_i18n", "pageTemplateSet"
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

	@Ignore
	@Override
	@Test
	protected PageTemplate
			testGetSitePageTemplatePermissionsPage_addPageTemplate()
		throws Exception {

		return super.testGetSitePageTemplatePermissionsPage_addPageTemplate();
	}

	@Override
	protected PageTemplate
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_addPageTemplate(
				String siteExternalReferenceCode,
				String pageTemplateSetExternalReferenceCode,
				PageTemplate pageTemplate)
		throws Exception {

		return pageTemplateResource.
			postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
				siteExternalReferenceCode, pageTemplateSetExternalReferenceCode,
				pageTemplate);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantPageTemplateSetExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getLayoutPageTemplateCollection(irrelevantGroup);

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getPageTemplateSetExternalReferenceCode()
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_getLayoutPageTemplateCollection(testGroup);

		return layoutPageTemplateCollection.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplateSetPageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected PageTemplate
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
				String siteExternalReferenceCode, PageTemplate pageTemplate)
		throws Exception {

		return pageTemplateResource.
			postSiteSiteByExternalReferenceCodePageTemplate(
				siteExternalReferenceCode, pageTemplate);
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSiteByExternalReferenceCodePageTemplatesPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Override
	protected PageTemplate
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				PageTemplate pageTemplate)
		throws Exception {

		return testGetSiteSiteByExternalReferenceCodePageTemplatesPage_addPageTemplate(
			testGroup.getExternalReferenceCode(), pageTemplate);
	}

	@Override
	protected PageTemplate
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				PageTemplate pageTemplate)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		return pageTemplateResource.
			postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplateSet.getExternalReferenceCode(), pageTemplate);
	}

	@Ignore
	@Override
	@Test
	protected PageTemplate
			testPutSitePageTemplatePermissionsPage_addPageTemplate()
		throws Exception {

		return super.testPutSitePageTemplatePermissionsPage_addPageTemplate();
	}

	private void
			_assertPostSiteSiteByExternalReferenceCodePageTemplatePageSpecificationProblemException(
				String pageTemplateExternalReferenceCode)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplatePageSpecification(
						testGroup.getExternalReferenceCode(),
						pageTemplateExternalReferenceCode,
						new ContentPageSpecification() {
							{
								setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
							}
						}));
	}

	private void _assertProblemException(
			String status, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals(status, problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private void _enableLocalStaging() throws Exception {
		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), testGroup, true, false,
			ServiceContextTestUtil.getServiceContext(
				testGroup, TestPropsValues.getUserId()));
	}

	private ContentPageTemplate _getContentPageTemplate(Group group)
		throws Exception {

		return new ContentPageTemplate() {
			{
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				pageTemplateSet = _getPageTemplateSet(group);
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
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "pageSpecifications"
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

	private long _getSiteSiteByExternalReferenceCodePageTemplatesPageTotalCount(
			String siteExternalReferenceCode)
		throws Exception {

		Page<PageTemplate> page =
			pageTemplateResource.
				getSiteSiteByExternalReferenceCodePageTemplatesPage(
					siteExternalReferenceCode, null, null, null, null, null);

		return page.getTotalCount();
	}

	private ContentPageTemplate _getUpdatedContentPageTemplate(
			Group group, String pageTemplateExternalReferenceCode)
		throws Exception {

		ContentPageTemplate contentPageTemplate =
			(ContentPageTemplate)
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplate(
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
			(WidgetPageTemplate)
				pageTemplateResource.
					getSiteSiteByExternalReferenceCodePageTemplate(
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

		return widgetPageTemplate;
	}

	private WidgetPageTemplate _getWidgetPageTemplate(Group group)
		throws Exception {

		return new WidgetPageTemplate() {
			{
				active = RandomTestUtil.randomBoolean();
				creatorExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
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

				name = StringUtil.toLowerCase(RandomTestUtil.randomString());

				name_i18n = HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()), name
				).build();

				pageTemplateSet = _getPageTemplateSet(group);
				type = PageTemplate.Type.WIDGET_PAGE_TEMPLATE;
				uuid = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	private void _postSiteSiteByExternalReferenceCodePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplate(
					siteExternalReferenceCode, pageTemplate));
	}

	private void
			_postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
				PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		PageTemplateSet pageTemplateSet = pageTemplate.getPageTemplateSet();

		assertEquals(
			pageTemplate,
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
					siteExternalReferenceCode,
					pageTemplateSet.getExternalReferenceCode(), pageTemplate));
	}

	private void _testDeleteSiteSiteByExternalReferenceCodePageTemplate(
			Group group, String pageTemplateExternalReferenceCode)
		throws Exception {

		Assert.assertNotNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, group.getGroupId()));

		pageTemplateResource.deleteSiteSiteByExternalReferenceCodePageTemplate(
			group.getExternalReferenceCode(),
			pageTemplateExternalReferenceCode);

		Assert.assertNull(
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					pageTemplateExternalReferenceCode, group.getGroupId()));

		_assertProblemException(
			"NOT_FOUND",
			() ->
				pageTemplateResource.
					deleteSiteSiteByExternalReferenceCodePageTemplate(
						group.getExternalReferenceCode(),
						pageTemplateExternalReferenceCode));
	}

	private void _testGetSiteSiteByExternalReferenceCodePageTemplate(
			PageTemplate pageTemplate)
		throws Exception {

		PageTemplate getPageTemplate =
			pageTemplateResource.getSiteSiteByExternalReferenceCodePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode());

		assertEquals(pageTemplate, getPageTemplate);
		assertValid(getPageTemplate);
	}

	private void _testGetSiteSiteByExternalReferenceCodePageTemplateWithNestedFields()
		throws Exception {

		PageTemplateResource pageTemplateResource = _getPageTemplateResource();

		PageTemplate pageTemplate =
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplate(
					testGroup.getExternalReferenceCode(),
					_getContentPageTemplate(testGroup));

		PageTemplate getPageTemplate =
			pageTemplateResource.getSiteSiteByExternalReferenceCodePageTemplate(
				testGroup.getExternalReferenceCode(),
				pageTemplate.getExternalReferenceCode());

		assertEquals(pageTemplate, getPageTemplate);
		assertValid(getPageTemplate);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				getLayoutPageTemplateEntryByExternalReferenceCode(
					getPageTemplate.getExternalReferenceCode(),
					testGroup.getGroupId());

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Assert.assertFalse(layout.isPublished());

		PageSpecificationsTestUtil.assertPageSpecifications(
			layout, getPageTemplate.getPageSpecifications());
	}

	private void _testPatchSiteSiteByExternalReferenceCodePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.
				patchSiteSiteByExternalReferenceCodePageTemplate(
					siteExternalReferenceCode,
					pageTemplate.getExternalReferenceCode(), pageTemplate));
	}

	private void _testPostSiteSiteByExternalReferenceCodePageTemplate()
		throws Exception {

		PageTemplate randomPageTemplate = randomPageTemplate();

		randomPageTemplate.setKey(StringPool.BLANK);

		PageTemplate postPageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplate_addPageTemplate(
				randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		Assert.assertTrue(Validator.isNotNull(postPageTemplate.getKey()));

		ContentPageTemplate contentPageTemplate = _getContentPageTemplate(
			testGroup);

		postPageTemplate =
			pageTemplateResource.
				postSiteSiteByExternalReferenceCodePageTemplate(
					testGroup.getExternalReferenceCode(), contentPageTemplate);

		Assert.assertEquals(
			contentPageTemplate.getKey(), postPageTemplate.getKey());

		_postSiteSiteByExternalReferenceCodePageTemplate(
			_getContentPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_postSiteSiteByExternalReferenceCodePageTemplate(
			_getWidgetPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_enableLocalStaging();

		_assertProblemException(
			"BAD_REQUEST",
			() -> _postSiteSiteByExternalReferenceCodePageTemplate(
				_getPageTemplate(testGroup),
				testGroup.getExternalReferenceCode()));

		_withCompanyGroupWidgetPageTemplate(
			(group, widgetPageTemplate) -> {
				_postSiteSiteByExternalReferenceCodePageTemplate(
					widgetPageTemplate, group.getExternalReferenceCode());

				_assertProblemException(
					"BAD_REQUEST",
					() ->
						pageTemplateResource.
							postSiteSiteByExternalReferenceCodePageTemplate(
								group.getExternalReferenceCode(),
								_getContentPageTemplate(group)));
			});

		_withDepotEntry(
			group -> _assertProblemException(
				"BAD_REQUEST",
				() ->
					pageTemplateResource.
						postSiteSiteByExternalReferenceCodePageTemplate(
							group.getExternalReferenceCode(),
							_getPageTemplate(group))));
	}

	private void _testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate()
		throws Exception {

		PageTemplate randomPageTemplate = randomPageTemplate();

		PageTemplate postPageTemplate =
			testPostSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate_addPageTemplate(
				randomPageTemplate);

		assertEquals(randomPageTemplate, postPageTemplate);
		assertValid(postPageTemplate);

		_postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
			_getContentPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());

		_postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
			_getWidgetPageTemplate(testGroup),
			testGroup.getExternalReferenceCode());
	}

	private void _testPutSiteSiteByExternalReferenceCodePageTemplate(
			PageTemplate pageTemplate, String siteExternalReferenceCode)
		throws Exception {

		assertEquals(
			pageTemplate,
			pageTemplateResource.putSiteSiteByExternalReferenceCodePageTemplate(
				siteExternalReferenceCode,
				pageTemplate.getExternalReferenceCode(), pageTemplate));
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
			new HashMap<>(), ServiceContextTestUtil.getServiceContext());

		try {
			unsafeConsumer.accept(depotEntry.getGroup());
		}
		finally {
			_depotEntryLocalService.deleteDepotEntry(depotEntry);
		}
	}

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
	private StagingLocalService _stagingLocalService;

}