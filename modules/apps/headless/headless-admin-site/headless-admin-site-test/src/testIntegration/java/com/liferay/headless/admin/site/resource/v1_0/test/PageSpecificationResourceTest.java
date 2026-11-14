/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.LinkToURLPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.Settings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.resource.v1_0.PageSpecificationResource;
import com.liferay.headless.admin.site.client.scope.Scope;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageElementsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageExperiencesTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.ProblemExceptionTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.User;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsExperienceConstants;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-10622"), @FeatureFlag("LPD-57283"),
		@FeatureFlag("LPD-74328")
	}
)
@RunWith(Arquillian.class)
public class PageSpecificationResourceTest
	extends BasePageSpecificationResourceTestCase {

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
	@TestInfo({"LPD-97472", "LPD-107120"})
	public void testDeleteSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testDeleteSitePageSpecificationWithLockedLayout(serviceContext);
		_testDeleteSitePageSpecificationWithSupportedPageTypes(serviceContext);
		_testDeleteSitePageSpecificationWithUnsupportedLayoutType(
			serviceContext);
		_testDeleteSitePageSpecificationWithWidgetPage(serviceContext);
		_testDeleteSitePageSpecificationWithWidgetPageTemplate(serviceContext);
		_testDeleteSitePageSpecificationWithoutUpdatePermission(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext));

		_testDeleteSitePageSpecificationWithUnsupportedSite();
	}

	@Override
	@Test
	public void testGetSiteDisplayPageTemplatePageSpecificationsPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext);

		_testPageSpecificationsPage(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			serviceContext,
			() ->
				pageSpecificationResource.
					getSiteDisplayPageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				pageSpecificationResource.
					getSiteDisplayPageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						externalReferenceCode));

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The external reference code \"",
				masterLayoutPageTemplateEntry.getExternalReferenceCode(),
				"\" does not point to a display page template"),
			() ->
				pageSpecificationResource.
					getSiteDisplayPageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						masterLayoutPageTemplateEntry.
							getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteMasterPagePageSpecificationsPage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_DRAFT);

		_testPageSpecificationsPage(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			serviceContext,
			() ->
				pageSpecificationResource.
					getSiteMasterPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				pageSpecificationResource.
					getSiteMasterPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						externalReferenceCode));

		LayoutPageTemplateEntry basicLayoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The external reference code \"",
				basicLayoutPageTemplateEntry.getExternalReferenceCode(),
				"\" does not point to a master page"),
			() ->
				pageSpecificationResource.
					getSiteMasterPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						basicLayoutPageTemplateEntry.
							getExternalReferenceCode()));
	}

	@Override
	@Test
	@TestInfo("LPD-97472")
	public void testGetSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testGetSitePageSpecificationWithLayoutWithDraftLayout(serviceContext);
		_testGetSitePageSpecificationWithStyleBookEntryScopeERC(serviceContext);
		_testGetSitePageSpecificationWithUnsupportedLayoutType(serviceContext);
		_testGetSitePageSpecificationWithWidgetPage(serviceContext);
		_testGetSitePageSpecificationWithWidgetPageTemplate(serviceContext);

		_testGetSitePageSpecificationWithNonexistentPageSpecification();
	}

	@Override
	@Test
	public void testGetSitePageTemplatePageSpecificationsPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext);

		_testPageSpecificationsPage(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			serviceContext,
			() ->
				pageSpecificationResource.
					getSitePageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				pageSpecificationResource.
					getSitePageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						externalReferenceCode));

		LayoutPageTemplateEntry displayPageLayoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntry(serviceContext);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The external reference code \"",
				displayPageLayoutPageTemplateEntry.getExternalReferenceCode(),
				"\" does not point to a page template"),
			() ->
				pageSpecificationResource.
					getSitePageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						displayPageLayoutPageTemplateEntry.
							getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationVersionPageSpecification()
		throws Exception {

		super.testGetSiteSitePagePageSpecificationVersionPageSpecification();
	}

	@Override
	@Test
	public void testGetSiteSitePagePageSpecificationsPage() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		_testPageSpecificationsPage(
			layout, serviceContext,
			() ->
				pageSpecificationResource.getSiteSitePagePageSpecificationsPage(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				pageSpecificationResource.getSiteSitePagePageSpecificationsPage(
					testGroup.getExternalReferenceCode(),
					externalReferenceCode));

		Layout draftLayout = layout.fetchDraftLayout();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The external reference code \"",
				draftLayout.getExternalReferenceCode(),
				"\" does not point to a site page"),
			() ->
				pageSpecificationResource.getSiteSitePagePageSpecificationsPage(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteUtilityPagePageSpecificationsPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntry(
				serviceContext);

		_testPageSpecificationsPage(
			_layoutLocalService.getLayout(layoutUtilityPageEntry.getPlid()),
			serviceContext,
			() ->
				pageSpecificationResource.
					getSiteUtilityPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutUtilityPageEntry.getExternalReferenceCode()));

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() ->
				pageSpecificationResource.
					getSiteUtilityPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						externalReferenceCode));
	}

	@Override
	@Test
	@TestInfo("LPD-97472")
	public void testPatchSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
		_testPatchSitePageSpecification(
			_addLayout(LayoutConstants.TYPE_PORTLET, serviceContext),
			serviceContext);
		_testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPatchSitePageSpecificationWithMismatchedPageSpecification(
			serviceContext);

		ContentPageSpecification contentPageSpecification =
			new ContentPageSpecification();

		contentPageSpecification.setType(
			PageSpecification.Type.CONTENT_PAGE_SPECIFICATION);

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(), externalReferenceCode,
				contentPageSpecification));

		_testPatchSitePageSpecificationWithUnsupportedLayoutType(
			serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testPostSitePageSpecificationPublish() throws Exception {
		super.testPostSitePageSpecificationPublish();
	}

	@Override
	@Test
	@TestInfo({"LPD-95020", "LPD-97472"})
	public void testPutSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPutSitePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_testPutSitePageSpecification(
			layout, layout.getExternalReferenceCode(), serviceContext);

		_testPutSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPutSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPutSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testPutSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testPutSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			layoutPageTemplateEntry.getExternalReferenceCode(), serviceContext);

		_testPutSitePageSpecificationWithStyleBookEntryScopeERC(serviceContext);

		_testPutSitePageSpecificationWithInvalidCollectionPageElement(
			serviceContext);
		_testPutSitePageSpecificationWithInvalidMasterPageReference(
			serviceContext);
		_testPutSitePageSpecificationWithInvalidPageExperiences(serviceContext);
		_testPutSitePageSpecificationWithInvalidSettings(serviceContext);
		_testPutSitePageSpecificationWithUnsupportedLayoutType(serviceContext);
	}

	@Override
	protected boolean equals(
		PageSpecification pageSpecification1,
		PageSpecification pageSpecification2) {

		if (pageSpecification1 == pageSpecification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					additionalAssertFieldName, "externalReferenceCode")) {

				Assert.assertEquals(
					pageSpecification1.getExternalReferenceCode(),
					pageSpecification2.getExternalReferenceCode());

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "settings")) {
				SettingsTestUtil.assertSettings(
					SettingsTestUtil.getSettings(pageSpecification1),
					SettingsTestUtil.getSettings(pageSpecification2));

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "status")) {
				Assert.assertEquals(
					pageSpecification1.getStatus(),
					pageSpecification2.getStatus());

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "type")) {
				Assert.assertEquals(
					pageSpecification1.getType(), pageSpecification2.getType());

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "pageExperiences")) {
				if (!(pageSpecification1 instanceof ContentPageSpecification) ||
					!(pageSpecification2 instanceof ContentPageSpecification)) {

					continue;
				}

				_assertContentPageSpecification(
					(ContentPageSpecification)pageSpecification1,
					(ContentPageSpecification)pageSpecification2);

				continue;
			}

			if (Objects.equals(
					additionalAssertFieldName, "widgetPageSections")) {

				if (!(pageSpecification1 instanceof WidgetPageSpecification) ||
					!(pageSpecification2 instanceof WidgetPageSpecification)) {

					continue;
				}

				PageSpecificationsTestUtil.assertWidgetPageSpecification(
					(WidgetPageSpecification)pageSpecification1,
					(WidgetPageSpecification)pageSpecification2);

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"externalReferenceCode", "pageExperiences", "settings", "status",
			"type"
		};
	}

	@Override
	protected PageSpecification
			testGetSiteSitePagePageSpecificationVersionPageSpecification_addPageSpecification()
		throws Exception {

		Layout draftLayout = _testGroupLayout.fetchDraftLayout();

		_layoutContentVersion =
			_layoutContentVersionLocalService.addLayoutContentVersion(
				null, TestPropsValues.getUserId(),
				_layoutContentVersionDataProvider.getLayoutContentVersionData(
					draftLayout,
					ServiceContextTestUtil.getServiceContext(
						testGroup.getGroupId())),
				null, draftLayout.getPlid(), WorkflowConstants.STATUS_APPROVED);

		return PageSpecification.toDTO(_layoutContentVersion.getData());
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionPageSpecification_getPageSpecificationVersionExternalReferenceCode()
		throws Exception {

		return _layoutContentVersion.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetSiteSitePagePageSpecificationVersionPageSpecification_getSitePageExternalReferenceCode()
		throws Exception {

		return _testGroupLayout.getExternalReferenceCode();
	}

	private Layout _addLayout(String type, ServiceContext serviceContext)
		throws Exception {

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), type, _getTypeSettings(), false, false,
			Collections.emptyMap(),
			_getMasterLayoutPageTemplateEntryERC(serviceContext),
			serviceContext);
	}

	private StyleBookEntry _addStyleBookEntry(ServiceContext serviceContext)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			false, null, null, RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), serviceContext);
	}

	private void _assertContentPageSpecification(
		ContentPageSpecification expectedContentPageSpecification,
		ContentPageSpecification actualContentPageSpecification) {

		PageExperience[] expectedPageExperiences =
			expectedContentPageSpecification.getPageExperiences();
		PageExperience[] actualPageExperiences =
			actualContentPageSpecification.getPageExperiences();

		if (ArrayUtil.isEmpty(expectedPageExperiences)) {
			Assert.assertTrue(ArrayUtil.isEmpty(actualPageExperiences));

			return;
		}

		Assert.assertEquals(
			Arrays.toString(actualPageExperiences),
			expectedPageExperiences.length, actualPageExperiences.length);

		for (PageExperience curPageExperience : actualPageExperiences) {
			PageExperience pageExperience = _getPageExperience(
				curPageExperience.getExternalReferenceCode(),
				expectedPageExperiences);

			_assertPageElements(
				pageExperience.getPageElements(),
				curPageExperience.getPageElements());
		}
	}

	private void _assertContentPageSpecification(
			ContentPageSpecification contentPageSpecification, Layout layout)
		throws Exception {

		Assert.assertEquals(
			PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
			contentPageSpecification.getType());

		PageExperience[] pageExperiences =
			contentPageSpecification.getPageExperiences();

		Assert.assertEquals(
			Arrays.toString(pageExperiences),
			_segmentsExperienceService.getSegmentsExperiencesCount(
				layout.getGroupId(), layout.getPlid(), true),
			pageExperiences.length);
	}

	private void _assertDeleteSitePageSpecification(Layout draftLayout)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		boolean published = layout.isPublished();

		pageSpecificationResource.deleteSitePageSpecification(
			testGroup.getExternalReferenceCode(),
			draftLayout.getExternalReferenceCode());

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Assert.assertTrue(draftLayout.isApproved());

		layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		Assert.assertEquals(published, layout.isPublished());
	}

	private void _assertDeleteSitePageSpecificationProblemException(
			String pageSpecificationExternalReferenceCode)
		throws Exception {

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification does not have unpublished changes",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode));
	}

	private void _assertPageElements(
		PageElement[] expectedPageElements, PageElement[] actualPageElements) {

		if (ArrayUtil.isEmpty(expectedPageElements)) {
			Assert.assertTrue(ArrayUtil.isEmpty(actualPageElements));

			return;
		}

		Assert.assertTrue(ArrayUtil.isNotEmpty(actualPageElements));
		Assert.assertEquals(
			Arrays.toString(actualPageElements), expectedPageElements.length,
			actualPageElements.length);

		for (PageElement curPageElement : actualPageElements) {
			PageElement pageElement = _getPageElement(
				curPageElement.getExternalReferenceCode(),
				expectedPageElements);

			PageElementDefinition pageElementDefinition =
				pageElement.getPageElementDefinition();

			PageElementDefinition curPageElementDefinition =
				curPageElement.getPageElementDefinition();

			Assert.assertEquals(
				pageElementDefinition.getType(),
				curPageElementDefinition.getType());

			_assertPageElements(
				pageElement.getPageElements(),
				curPageElement.getPageElements());
			Assert.assertEquals(
				pageElement.getParentExternalReferenceCode(),
				curPageElement.getParentExternalReferenceCode());
			Assert.assertEquals(
				GetterUtil.getInteger(pageElement.getPosition()),
				GetterUtil.getInteger(curPageElement.getPosition()));
		}
	}

	private void _assertPageSpecification(
			Layout layout, PageSpecification pageSpecification)
		throws Exception {

		if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
			_assertContentPageSpecification(
				(ContentPageSpecification)pageSpecification, layout);
		}
		else {
			_assertWidgetPageSpecification(
				(WidgetPageSpecification)pageSpecification);
		}
	}

	private void _assertPageSpecificationsPage(
			Layout layout, Page<PageSpecification> page)
		throws Exception {

		Assert.assertEquals(2, page.getTotalCount());

		List<PageSpecification> pageSpecifications = ListUtil.fromCollection(
			page.getItems());

		_assertPageSpecification(layout, pageSpecifications.get(0));

		_assertPageSpecification(
			layout.fetchDraftLayout(), pageSpecifications.get(1));
	}

	private void _assertPutSiteContentPageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode());

		PageExperiencesTestUtil.modifyPageExperiences(
			contentPageSpecification.getPageExperiences(),
			testGroup.getGroupId());

		_modifySettings(
			contentPageSpecification, serviceContext, layout.isTypeUtility());

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		PageSpecification putPageSpecification =
			pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), contentPageSpecification);

		assertEquals(contentPageSpecification, putPageSpecification);
	}

	private void _assertWidgetPageSpecification(
		WidgetPageSpecification widgetPageSpecification) {

		Assert.assertEquals(
			PageSpecification.Type.WIDGET_PAGE_SPECIFICATION,
			widgetPageSpecification.getType());

		Assert.assertTrue(
			ArrayUtil.isNotEmpty(
				widgetPageSpecification.getWidgetPageSections()));
	}

	private ContentPageSpecification _getContentPageSpecification(
		Settings settings) {

		ContentPageSpecification contentPageSpecification =
			new ContentPageSpecification() {
				{
					setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
				}
			};

		contentPageSpecification.setSettings(settings);

		return contentPageSpecification;
	}

	private String _getMasterLayoutPageTemplateEntryERC(
			ServiceContext serviceContext)
		throws Exception {

		if (RandomTestUtil.randomBoolean()) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_APPROVED);

		return layoutPageTemplateEntry.getExternalReferenceCode();
	}

	private PageElement _getPageElement(
		String pageElementExternalReferenceCode, PageElement[] pageElements) {

		for (PageElement pageElement : pageElements) {
			if (!Objects.equals(
					pageElement.getExternalReferenceCode(),
					pageElementExternalReferenceCode)) {

				continue;
			}

			return pageElement;
		}

		return null;
	}

	private PageExperience _getPageExperience(
		String pageExperienceExternalReferenceCode,
		PageExperience[] pageExperiences) {

		for (PageExperience pageExperience : pageExperiences) {
			if (!Objects.equals(
					pageExperience.getExternalReferenceCode(),
					pageExperienceExternalReferenceCode)) {

				continue;
			}

			return pageExperience;
		}

		return null;
	}

	private String _getStyleBookEntryERC(ServiceContext serviceContext)
		throws Exception {

		if (RandomTestUtil.randomBoolean()) {
			return null;
		}

		StyleBookEntry styleBookEntry = _addStyleBookEntry(serviceContext);

		return styleBookEntry.getExternalReferenceCode();
	}

	private String _getTypeSettings() throws Exception {
		if (RandomTestUtil.randomBoolean()) {
			return StringPool.BLANK;
		}

		return UnicodePropertiesBuilder.put(
			"javascript", RandomTestUtil.randomString()
		).put(
			"lfr-theme:regular:show-maximize-minimize-application-links", true
		).buildString();
	}

	private boolean _isPublished(Layout draftLayout) {
		if (draftLayout == null) {
			return true;
		}

		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty("published"));
	}

	private void _modifySettings(
			PageSpecification pageSpecification, ServiceContext serviceContext,
			boolean typeUtility)
		throws Exception {

		if (!typeUtility) {
			SettingsTestUtil.modifySettings(
				FavIcon.FavIconType.CLIENT_EXTENSION, serviceContext,
				SettingsTestUtil.getSettings(pageSpecification));

			return;
		}

		if (!(pageSpecification instanceof ContentPageSpecification) &&
			!(pageSpecification instanceof WidgetPageSpecification)) {

			return;
		}

		Settings settings = new Settings() {
			{
				setMasterPageItemExternalReference(
					() -> SettingsTestUtil.getMasterPageItemExternalReference(
						false, serviceContext));
				setStyleBookItemExternalReference(
					() -> SettingsTestUtil.getStyleBookItemExternalReference(
						serviceContext));
			}
		};

		if (pageSpecification instanceof ContentPageSpecification) {
			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			contentPageSpecification.setSettings(() -> settings);

			return;
		}

		WidgetPageSpecification widgetPageSpecification =
			(WidgetPageSpecification)pageSpecification;

		widgetPageSpecification.setSettings(() -> settings);
	}

	private void _testDeleteSitePageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		_assertDeleteSitePageSpecificationProblemException(
			layout.getExternalReferenceCode());

		Layout draftLayout = layout.fetchDraftLayout();

		_assertDeleteSitePageSpecification(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertDeleteSitePageSpecificationProblemException(
			layout.getExternalReferenceCode());

		_assertDeleteSitePageSpecificationProblemException(
			draftLayout.getExternalReferenceCode());

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertDeleteSitePageSpecification(draftLayout);
	}

	private void _testDeleteSitePageSpecificationWithLockedLayout(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		User user = UserTestUtil.addUser(testGroup.getGroupId());

		_layoutLockManager.getLock(draftLayout, user.getUserId());

		try {
			ProblemExceptionTestUtil.assertProblemException(
				"CONFLICT", "The page is locked by another user",
				() -> pageSpecificationResource.deleteSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode()));
		}
		finally {
			_layoutLockManager.unlock(draftLayout, user.getUserId());
		}
	}

	private void _testDeleteSitePageSpecificationWithSupportedPageTypes(
			ServiceContext serviceContext)
		throws Exception {

		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
	}

	private void _testDeleteSitePageSpecificationWithUnsupportedLayoutType(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_EMBEDDED, serviceContext);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only content, widget, or asset display pages are supported",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));
	}

	private void _testDeleteSitePageSpecificationWithUnsupportedSite()
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST", "The site does not support this operation",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				companyGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()));
	}

	private void _testDeleteSitePageSpecificationWithWidgetPage(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_assertDeleteSitePageSpecificationProblemException(
			layout.getExternalReferenceCode());
	}

	private void _testDeleteSitePageSpecificationWithWidgetPageTemplate(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		_assertDeleteSitePageSpecificationProblemException(
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	private void _testDeleteSitePageSpecificationWithoutUpdatePermission(
			Layout layout)
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		PageSpecificationResource pageSpecificationResource =
			PageSpecificationResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		Layout draftLayout = layout.fetchDraftLayout();

		ProblemExceptionTestUtil.assertProblemException(
			"FORBIDDEN", "Forbidden",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode()));
	}

	private void _testGetSitePageSpecification(
			Layout layout, String pageSpecificationExternalReferenceCode)
		throws Exception {

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode);

		Assert.assertEquals(
			pageSpecificationExternalReferenceCode,
			pageSpecification.getExternalReferenceCode());

		SettingsTestUtil.assertPageSpecificationSetting(
			layout, SettingsTestUtil.getSettings(pageSpecification));

		if (layout.isDraftLayout()) {
			if (layout.isApproved()) {
				Assert.assertEquals(
					PageSpecification.Status.APPROVED,
					pageSpecification.getStatus());
			}
			else {
				Assert.assertEquals(
					PageSpecification.Status.DRAFT,
					pageSpecification.getStatus());
			}
		}
		else if (_isPublished(layout.fetchDraftLayout())) {
			Assert.assertEquals(
				PageSpecification.Status.APPROVED,
				pageSpecification.getStatus());
		}
		else {
			Assert.assertEquals(
				PageSpecification.Status.DRAFT, pageSpecification.getStatus());
		}

		if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
			_assertContentPageSpecification(
				(ContentPageSpecification)pageSpecification, layout);
		}
		else {
			_assertWidgetPageSpecification(
				(WidgetPageSpecification)pageSpecification);
		}
	}

	private void _testGetSitePageSpecificationWithLayoutWithDraftLayout(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());

		Layout draftLayout = layout.fetchDraftLayout();

		_testGetSitePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		draftLayout = _layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());
		_testGetSitePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());

		draftLayout = layout.fetchDraftLayout();

		_testGetSitePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		draftLayout = _layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());
		_testGetSitePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());
	}

	private void _testGetSitePageSpecificationWithLayoutWithDraftLayout(
			ServiceContext serviceContext)
		throws Exception {

		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
	}

	private void _testGetSitePageSpecificationWithNonexistentPageSpecification()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ProblemExceptionTestUtil.assertProblemException(
			"NOT_FOUND", null,
			() -> pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(), externalReferenceCode));
	}

	private void _testGetSitePageSpecificationWithStyleBookEntryScopeERC(
			ServiceContext serviceContext)
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), irrelevantGroup.getGroupId(),
				false, null, null, RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					irrelevantGroup.getGroupId(), TestPropsValues.getUserId()));

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setStyleBookEntryERC(
			styleBookEntry.getExternalReferenceCode());
		draftLayout.setStyleBookEntryScopeERC(
			irrelevantGroup.getExternalReferenceCode());

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode());

		Settings settings = SettingsTestUtil.getSettings(pageSpecification);

		ItemExternalReference itemExternalReference =
			settings.getStyleBookItemExternalReference();

		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			itemExternalReference.getExternalReferenceCode());

		Scope scope = itemExternalReference.getScope();

		Assert.assertEquals(
			irrelevantGroup.getExternalReferenceCode(),
			scope.getExternalReferenceCode());
	}

	private void _testGetSitePageSpecificationWithUnsupportedLayoutType(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_EMBEDDED, serviceContext);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only content, widget, or asset display pages are supported",
			() -> pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));
	}

	private void _testGetSitePageSpecificationWithWidgetPage(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());
	}

	private void _testGetSitePageSpecificationWithWidgetPageTemplate(
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testGetSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	private void _testPageSpecificationsPage(
			Layout layout, ServiceContext serviceContext,
			UnsafeSupplier<Page<PageSpecification>, Exception> unsafeSupplier)
		throws Exception {

		_assertPageSpecificationsPage(layout, unsafeSupplier.get());

		Layout draftLayout = layout.fetchDraftLayout();

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertPageSpecificationsPage(layout, unsafeSupplier.get());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertPageSpecificationsPage(layout, unsafeSupplier.get());

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertPageSpecificationsPage(layout, unsafeSupplier.get());
	}

	private void _testPatchSitePageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		WidgetPageSpecification widgetPageSpecification =
			(WidgetPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode());

		SettingsTestUtil.modifySettings(
			FavIcon.FavIconType.ITEM_EXTERNAL_REFERENCE, serviceContext,
			widgetPageSpecification.getSettings());

		_testPatchSitePageSpecification(
			widgetPageSpecification,
			() -> PageSpecificationsTestUtil.getWidgetPageSpecification(
				null, null, widgetPageSpecification.getSettings(), null,
				widgetPageSpecification.getWidgetPageSections()));

		widgetPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a widget page specification in " +
				"approved status",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), widgetPageSpecification));
	}

	private void _testPatchSitePageSpecification(
			PageSpecification pageSpecification,
			UnsafeSupplier<PageSpecification, Exception> unsafeSupplier)
		throws Exception {

		Assert.assertTrue(
			equals(
				pageSpecification,
				pageSpecificationResource.patchSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					pageSpecification.getExternalReferenceCode(),
					unsafeSupplier.get())));
	}

	private void _testPatchSitePageSpecificationWithLayoutWithDraftLayout(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		Layout draftLayout = _updateLayout(
			layout.fetchDraftLayout(), serviceContext);

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode());

		PageExperiencesTestUtil.modifyPageExperiences(
			contentPageSpecification.getPageExperiences(),
			testGroup.getGroupId());

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		_testPatchSitePageSpecification(
			contentPageSpecification,
			() -> new ContentPageSpecification() {
				{
					setPageExperiences(
						contentPageSpecification::getPageExperiences);
					setStatus(PageSpecification.Status.DRAFT);
					setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
				}
			});

		_modifySettings(
			contentPageSpecification, serviceContext, layout.isTypeUtility());

		_testPatchSitePageSpecification(
			contentPageSpecification,
			() -> _getContentPageSpecification(
				contentPageSpecification.getSettings()));

		contentPageSpecification.setStatus(PageSpecification.Status.APPROVED);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a content page specification in " +
				"draft status",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				contentPageSpecification));
	}

	private void _testPatchSitePageSpecificationWithMismatchedPageSpecification(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		WidgetPageSpecification widgetPageSpecification =
			new WidgetPageSpecification();

		widgetPageSpecification.setType(
			PageSpecification.Type.WIDGET_PAGE_SPECIFICATION);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification type does not match the page type",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				widgetPageSpecification));
	}

	private void _testPatchSitePageSpecificationWithUnsupportedLayoutType(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(LayoutConstants.TYPE_URL, serviceContext);

		LinkToURLPageSpecification linkToURLPageSpecification =
			new LinkToURLPageSpecification();

		linkToURLPageSpecification.setType(
			PageSpecification.Type.LINK_TO_URL_PAGE_SPECIFICATION);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only content, widget, or asset display pages are supported",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), linkToURLPageSpecification));

		WidgetPageSpecification widgetPageSpecification =
			new WidgetPageSpecification();

		widgetPageSpecification.setType(
			PageSpecification.Type.WIDGET_PAGE_SPECIFICATION);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only content, widget, or asset display pages are supported",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), widgetPageSpecification));
	}

	private void _testPutSitePageSpecification(
			Layout layout, String pageSpecificationExternalReferenceCode,
			ServiceContext serviceContext)
		throws Exception {

		_updateLayout(layout, serviceContext);

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode);

		_modifySettings(
			pageSpecification, serviceContext, layout.isTypeUtility());

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		PageSpecification putPageSpecification =
			pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				pageSpecificationExternalReferenceCode, pageSpecification);

		assertEquals(pageSpecification, putPageSpecification);
	}

	private void _testPutSitePageSpecificationWithInvalidCollectionPageElement(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode());

		PageElement pageElement = new PageElement();

		pageElement.setExternalReferenceCode(RandomTestUtil.randomString());
		pageElement.setPageElementDefinition(
			PageElementsTestUtil.getPageElementDefinition(
				false, PageElementDefinition.Type.COLLECTION_DISPLAY,
				testGroup.getGroupId()));
		pageElement.setPageElements(
			new PageElement[] {new PageElement(), new PageElement()});

		PageExperience[] pageExperiences =
			contentPageSpecification.getPageExperiences();

		PageExperience pageExperience = pageExperiences[0];

		pageExperience.setPageElements(new PageElement[] {pageElement});

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The collection display page element with the external ",
				"reference code \"", pageElement.getExternalReferenceCode(),
				"\" cannot have more than one child page element"),
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				contentPageSpecification));
	}

	private void _testPutSitePageSpecificationWithInvalidMasterPageReference(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode());

		Settings settings = SettingsTestUtil.getSettings(pageSpecification);

		LayoutPageTemplateEntry basicLayoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getBasicLayoutPageTemplateEntry(
				serviceContext);
		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setExternalReferenceCode(
			basicLayoutPageTemplateEntry.getExternalReferenceCode());

		settings.setMasterPageItemExternalReference(itemExternalReference);

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The external reference code \"",
				basicLayoutPageTemplateEntry.getExternalReferenceCode(),
				"\" does not point to a master page"),
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));

		itemExternalReference.setScope(
			new Scope() {
				{
					setExternalReferenceCode(
						irrelevantGroup.getExternalReferenceCode());
				}
			});

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The master page reference does not belong to the same scope as " +
				"the target page",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));
	}

	private void _testPutSitePageSpecificationWithInvalidPageExperiences(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only the default segments experience can use the key \"DEFAULT\"",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				PageSpecificationsTestUtil.getContentPageSpecification(
					draftLayout.getExternalReferenceCode(), null, null,
					new PageExperience[] {
						PageExperiencesTestUtil.getPageExperience(
							RandomTestUtil.randomString(),
							SegmentsExperienceConstants.KEY_DEFAULT, 0,
							RandomTestUtil.randomString()),
						PageExperiencesTestUtil.getPageExperience(
							RandomTestUtil.randomString(),
							SegmentsExperienceConstants.KEY_DEFAULT, 0,
							RandomTestUtil.randomString())
					},
					testGroup.getGroupId(), PageSpecification.Status.DRAFT)));

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The default page experience must have a priority of 0",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				PageSpecificationsTestUtil.getContentPageSpecification(
					draftLayout.getExternalReferenceCode(), null, null,
					new PageExperience[] {
						PageExperiencesTestUtil.getPageExperience(
							RandomTestUtil.randomString(),
							SegmentsExperienceConstants.KEY_DEFAULT, 1,
							RandomTestUtil.randomString())
					},
					testGroup.getGroupId(), PageSpecification.Status.DRAFT)));

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode());

		for (PageExperience pageExperience :
				contentPageSpecification.getPageExperiences()) {

			pageExperience.setName_i18n(Collections.<String, String>emptyMap());
		}

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"A name in the site's default language is required for each page " +
				"experience",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				contentPageSpecification));
	}

	private void _testPutSitePageSpecificationWithInvalidSettings(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode());

		Settings settings = SettingsTestUtil.getSettings(pageSpecification);

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		String key = RandomTestUtil.randomString();

		settings.setThemeSettings(
			Collections.singletonMap(key, RandomTestUtil.randomString()));

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			StringBundler.concat(
				"The theme setting \"", key,
				"\" must start with \"lfr-theme:\""),
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));
	}

	private void _testPutSitePageSpecificationWithLayoutWithDraftLayout(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		Layout draftLayout = _updateLayout(
			layout.fetchDraftLayout(), serviceContext);

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode());

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a content page specification in " +
				"draft status",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));
		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a content page specification in " +
				"draft status",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), pageSpecification));

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a content page specification in " +
				"draft status",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), pageSpecification));

		_assertPutSiteContentPageSpecification(draftLayout, serviceContext);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"The page specification must be a content page specification in " +
				"draft status",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertPutSiteContentPageSpecification(draftLayout, serviceContext);
	}

	private void _testPutSitePageSpecificationWithStyleBookEntryScopeERC(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		PageSpecification pageSpecification =
			pageSpecificationResource.getSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode());

		Settings settings = SettingsTestUtil.getSettings(pageSpecification);

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), irrelevantGroup.getGroupId(),
				false, null, null, RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					irrelevantGroup.getGroupId(), TestPropsValues.getUserId()));

		itemExternalReference.setExternalReferenceCode(
			styleBookEntry.getExternalReferenceCode());

		itemExternalReference.setScope(
			new Scope() {
				{
					setExternalReferenceCode(
						irrelevantGroup.getExternalReferenceCode());
				}
			});

		settings.setStyleBookItemExternalReference(itemExternalReference);

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		pageSpecificationResource.putSitePageSpecification(
			testGroup.getExternalReferenceCode(),
			draftLayout.getExternalReferenceCode(), pageSpecification);

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			draftLayout.getStyleBookEntryERC());
		Assert.assertEquals(
			irrelevantGroup.getExternalReferenceCode(),
			draftLayout.getStyleBookEntryScopeERC());

		Scope scope = itemExternalReference.getScope();

		scope.setExternalReferenceCode(RandomTestUtil.randomString());

		pageSpecificationResource.putSitePageSpecification(
			testGroup.getExternalReferenceCode(),
			draftLayout.getExternalReferenceCode(), pageSpecification);

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			scope.getExternalReferenceCode(),
			draftLayout.getStyleBookEntryScopeERC());
	}

	private void _testPutSitePageSpecificationWithUnsupportedLayoutType(
			ServiceContext serviceContext)
		throws Exception {

		Layout layout = _addLayout(LayoutConstants.TYPE_URL, serviceContext);

		WidgetPageSpecification widgetPageSpecification =
			new WidgetPageSpecification();

		widgetPageSpecification.setStatus(PageSpecification.Status.APPROVED);
		widgetPageSpecification.setType(
			PageSpecification.Type.WIDGET_PAGE_SPECIFICATION);

		ProblemExceptionTestUtil.assertProblemException(
			"BAD_REQUEST",
			"Only content, widget, or asset display pages are supported",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), widgetPageSpecification));
	}

	private Layout _updateLayout(Layout layout, ServiceContext serviceContext)
		throws Exception {

		if (RandomTestUtil.randomBoolean()) {
			layout = _layoutLocalService.updateLookAndFeel(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), "classic_WAR_classictheme", "01",
				RandomTestUtil.randomString());
		}

		return _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(), layout.getType(),
			layout.isHidden(), layout.getFriendlyURLMap(),
			layout.getIconImage(), null, _getStyleBookEntryERC(serviceContext),
			null, null, null, layout.getMasterLayoutPageTemplateEntryERC(),
			serviceContext);
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private LayoutContentVersion _layoutContentVersion;

	@Inject
	private LayoutContentVersionDataProvider _layoutContentVersionDataProvider;

	@Inject
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private SegmentsExperienceService _segmentsExperienceService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private Layout _testGroupLayout;

}