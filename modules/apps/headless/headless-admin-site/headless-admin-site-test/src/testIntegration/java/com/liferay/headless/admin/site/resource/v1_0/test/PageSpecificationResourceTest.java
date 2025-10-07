/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.client.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.Settings;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutPageTemplateEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.LayoutUtilityPageEntryTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageExperiencesTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.PageSpecificationsTestUtil;
import com.liferay.headless.admin.site.resource.v1_0.test.util.SettingsTestUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Arrays;
import java.util.Collections;
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
public class PageSpecificationResourceTest
	extends BasePageSpecificationResourceTestCase {

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
	public void testDeleteSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		_testDeleteSitePageSpecification(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testDeleteSitePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		Layout layoutPageTemplateEntryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_assertProblemException(
			"NOT_FOUND",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layoutPageTemplateEntryLayout.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layoutPageTemplateEntry.getExternalReferenceCode()));
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
	}

	@Override
	@Test
	public void testGetSitePageSpecification() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_testGetSitePageSpecification(
			layout, layout.getExternalReferenceCode());

		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testGetSitePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testGetSitePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			layoutPageTemplateEntry.getExternalReferenceCode());
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
	}

	@Override
	@Test
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
	}

	@Ignore
	@Override
	@Test
	public void testPostSitePageSpecificationPublish() throws Exception {
		super.testPostSitePageSpecificationPublish();
	}

	@Override
	@Test
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
					pageSpecification1.getSettings(),
					pageSpecification2.getSettings());

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

	private Layout _addLayout(String type, ServiceContext serviceContext)
		throws Exception {

		return _layoutLocalService.addLayout(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 0,
			RandomTestUtil.randomLocaleStringMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), type, _getTypeSettings(), false, false,
			Collections.emptyMap(), _getMasterLayoutPlid(serviceContext),
			serviceContext);
	}

	private StyleBookEntry _addStyleBookEntry(ServiceContext serviceContext)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			false, null, RandomTestUtil.randomString(), null,
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

	private void _assertPutSiteContentPageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.getSitePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode());

		PageExperiencesTestUtil.modifyPageExperiences(
			contentPageSpecification.getPageExperiences());

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

	private long _getMasterLayoutPlid(ServiceContext serviceContext)
		throws Exception {

		if (RandomTestUtil.randomBoolean()) {
			return 0;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_APPROVED);

		return layoutPageTemplateEntry.getPlid();
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

	private long _getStyleBookEntryId(ServiceContext serviceContext)
		throws Exception {

		if (RandomTestUtil.randomBoolean()) {
			return 0;
		}

		StyleBookEntry styleBookEntry = _addStyleBookEntry(serviceContext);

		return styleBookEntry.getStyleBookEntryId();
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
				pageSpecification.getSettings());

			return;
		}

		pageSpecification.setSettings(
			() -> new Settings() {
				{
					setMasterPageItemExternalReference(
						() ->
							SettingsTestUtil.getMasterPageItemExternalReference(
								serviceContext));
					setStyleBookItemExternalReference(
						() ->
							SettingsTestUtil.getStyleBookItemExternalReference(
								serviceContext));
				}
			});
	}

	private void _testDeleteSitePageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		Layout draftLayout = layout.fetchDraftLayout();

		_assertDeleteSitePageSpecification(draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.deleteSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode()));

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertDeleteSitePageSpecification(draftLayout);
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
			layout, pageSpecification.getSettings());

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

		_assertProblemException(
			"BAD_REQUEST",
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
			contentPageSpecification.getPageExperiences());

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

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				new ContentPageSpecification() {
					{
						setPageExperiences(
							() -> ArrayUtil.append(
								contentPageSpecification.getPageExperiences(),
								new PageExperience()));
						setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
					}
				}));

		_modifySettings(
			contentPageSpecification, serviceContext, layout.isTypeUtility());

		_testPatchSitePageSpecification(
			contentPageSpecification,
			() -> _getContentPageSpecification(
				contentPageSpecification.getSettings()));

		contentPageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.patchSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(),
				contentPageSpecification));
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

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));
		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), pageSpecification));

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				layout.getExternalReferenceCode(), pageSpecification));

		_assertPutSiteContentPageSpecification(draftLayout, serviceContext);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			() -> pageSpecificationResource.putSitePageSpecification(
				testGroup.getExternalReferenceCode(),
				draftLayout.getExternalReferenceCode(), pageSpecification));

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertPutSiteContentPageSpecification(draftLayout, serviceContext);
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
			layout.getIconImage(), null, _getStyleBookEntryId(serviceContext),
			0, layout.getMasterLayoutPlid(), serviceContext);
	}

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private SegmentsExperienceService _segmentsExperienceService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}