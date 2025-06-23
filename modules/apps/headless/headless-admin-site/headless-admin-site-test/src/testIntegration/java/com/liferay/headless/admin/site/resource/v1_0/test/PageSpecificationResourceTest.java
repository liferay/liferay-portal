/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.site.client.dto.v1_0.ContainerPageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
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
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
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
import java.util.HashMap;
import java.util.List;
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
public class PageSpecificationResourceTest
	extends BasePageSpecificationResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testDeleteSiteSiteByExternalReferenceCodePageSpecification()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode()));

		_testDeleteSiteSiteByExternalReferenceCodePageSpecification(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
		_testDeleteSiteSiteByExternalReferenceCodePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSiteSiteByExternalReferenceCodePageSpecification(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testDeleteSiteSiteByExternalReferenceCodePageSpecification(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testDeleteSiteSiteByExternalReferenceCodePageSpecification(
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
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntryLayout.
							getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecificationsPage()
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
					getSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeMasterPagePageSpecificationsPage()
		throws Exception {

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
					getSiteSiteByExternalReferenceCodeMasterPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageSpecification()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode());

		_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			_layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid()),
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodePageTemplatePageSpecificationsPage()
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
					getSiteSiteByExternalReferenceCodePageTemplatePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutPageTemplateEntry.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeSitePagePageSpecificationsPage()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		Layout layout = _addLayout(
			LayoutConstants.TYPE_CONTENT, serviceContext);

		_testPageSpecificationsPage(
			layout, serviceContext,
			() ->
				pageSpecificationResource.
					getSiteSiteByExternalReferenceCodeSitePagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteSiteByExternalReferenceCodeUtilityPagePageSpecificationsPage()
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
					getSiteSiteByExternalReferenceCodeUtilityPagePageSpecificationsPage(
						testGroup.getExternalReferenceCode(),
						layoutUtilityPageEntry.getExternalReferenceCode()));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetSiteSiteByExternalReferenceCodePageSpecification()
		throws Exception {

		super.testGraphQLGetSiteSiteByExternalReferenceCodePageSpecification();
	}

	@Override
	@Test
	public void testPatchSiteSiteByExternalReferenceCodePageSpecification()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);
		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			_addLayout(LayoutConstants.TYPE_PORTLET, serviceContext),
			serviceContext);
		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testPostSiteSiteByExternalReferenceCodePageSpecificationPublish()
		throws Exception {

		super.testPostSiteSiteByExternalReferenceCodePageSpecificationPublish();
	}

	@Override
	@Test
	public void testPutSiteSiteByExternalReferenceCodePageSpecification()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			_addLayout(LayoutConstants.TYPE_CONTENT, serviceContext),
			serviceContext);

		Layout layout = _addLayout(
			LayoutConstants.TYPE_PORTLET, serviceContext);

		_testPutSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode(), serviceContext);

		_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getBasicLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getDisplayPageLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);
		_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutUtilityPageEntryTestUtil.getLayoutUtilityPageEntryLayout(
				serviceContext),
			serviceContext);
		_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
			LayoutPageTemplateEntryTestUtil.
				getMasterLayoutPageTemplateEntryLayout(serviceContext),
			serviceContext);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.
				getWidgetPageLayoutPageTemplateEntry(serviceContext);

		_testPutSiteSiteByExternalReferenceCodePageSpecification(
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
				_assertSettings(
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

				_assertWidgetPageSpecification(
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

	private void _assertDeleteSiteSiteByExternalReferenceCodePageSpecification(
			Layout draftLayout)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		boolean published = layout.isPublished();

		pageSpecificationResource.
			deleteSiteSiteByExternalReferenceCodePageSpecification(
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

	private void _assertPageSpecificationSetting(
			Layout layout, Settings settings)
		throws Exception {

		if (Validator.isNull(layout.getColorSchemeId()) ||
			Validator.isNull(layout.getThemeId())) {

			Assert.assertTrue(Validator.isNull(settings.getColorSchemeName()));
		}
		else {
			ColorScheme colorScheme = _themeLocalService.getColorScheme(
				layout.getCompanyId(), layout.getThemeId(),
				layout.getColorSchemeId());

			Assert.assertEquals(
				colorScheme.getName(), settings.getColorSchemeName());
		}

		if (Validator.isNull(layout.getCss())) {
			Assert.assertTrue(Validator.isNull(settings.getCss()));
		}
		else {
			Assert.assertEquals(layout.getCss(), settings.getCss());
		}

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		Assert.assertEquals(
			unicodeProperties.getProperty("javascript", null),
			settings.getJavascript());

		ItemExternalReference masterPageItemExternalReference =
			settings.getMasterPageItemExternalReference();

		if (layout.getMasterLayoutPlid() == 0) {
			Assert.assertNull(masterPageItemExternalReference);
		}
		else {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(
						layout.getMasterLayoutPlid());

			Assert.assertEquals(
				layoutPageTemplateEntry.getExternalReferenceCode(),
				masterPageItemExternalReference.getExternalReferenceCode());
		}

		ItemExternalReference styleBookItemExternalReference =
			settings.getStyleBookItemExternalReference();

		if (layout.getStyleBookEntryId() == 0) {
			Assert.assertNull(styleBookItemExternalReference);
		}
		else {
			StyleBookEntry styleBookEntry =
				_styleBookEntryLocalService.getStyleBookEntry(
					layout.getStyleBookEntryId());

			Assert.assertEquals(
				styleBookEntry.getExternalReferenceCode(),
				styleBookItemExternalReference.getExternalReferenceCode());
		}

		if (Validator.isNull(layout.getThemeId())) {
			Assert.assertTrue(Validator.isNull(settings.getThemeName()));
		}
		else {
			Theme theme = _themeLocalService.getTheme(
				layout.getCompanyId(), layout.getThemeId());

			Assert.assertEquals(theme.getName(), settings.getThemeName());
		}

		UnicodeProperties themeSettingsUnicodeProperties =
			_getThemeSettingsUnicodeProperties(unicodeProperties);

		if (themeSettingsUnicodeProperties.isEmpty()) {
			Assert.assertNull(settings.getThemeSettings());
		}
		else {
			Map<String, String> themeSettings = settings.getThemeSettings();

			Assert.assertEquals(
				MapUtil.toString(themeSettings),
				themeSettingsUnicodeProperties.size(), themeSettings.size());

			for (Map.Entry<String, String> entry :
					themeSettingsUnicodeProperties.entrySet()) {

				Assert.assertEquals(
					entry.getValue(), themeSettings.get(entry.getKey()));
			}
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

	private void
			_assertPutSiteSiteByExternalReferenceCodeContentPageSpecification(
				Layout layout, ServiceContext serviceContext)
		throws Exception {

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.
					getSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode());

		_modifyPageExperiences(contentPageSpecification.getPageExperiences());
		_modifySettings(serviceContext, contentPageSpecification.getSettings());

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		PageSpecification putPageSpecification =
			pageSpecificationResource.
				putSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode(),
					contentPageSpecification);

		equals(contentPageSpecification, putPageSpecification);
	}

	private void _assertSettings(
		Settings expectedSettings, Settings actualSettings) {

		if (expectedSettings == null) {
			Assert.assertNull(actualSettings);

			return;
		}

		Assert.assertEquals(
			expectedSettings.getColorSchemeName(),
			actualSettings.getColorSchemeName());
		Assert.assertEquals(expectedSettings.getCss(), actualSettings.getCss());
		Assert.assertEquals(
			expectedSettings.getJavascript(), actualSettings.getJavascript());

		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getMasterPageItemExternalReference(),
				actualSettings.getMasterPageItemExternalReference()));

		Assert.assertTrue(
			Objects.deepEquals(
				expectedSettings.getStyleBookItemExternalReference(),
				actualSettings.getStyleBookItemExternalReference()));

		Assert.assertEquals(
			expectedSettings.getThemeName(), actualSettings.getThemeName());

		Map<String, String> themeSettings = expectedSettings.getThemeSettings();
		Map<String, String> curThemeSettings =
			actualSettings.getThemeSettings();

		if (MapUtil.isEmpty(themeSettings)) {
			Assert.assertTrue(
				MapUtil.toString(curThemeSettings),
				MapUtil.isEmpty(curThemeSettings));

			return;
		}

		Assert.assertEquals(
			MapUtil.toString(curThemeSettings), themeSettings.size(),
			curThemeSettings.size());

		Assert.assertEquals(
			MapUtil.toString(curThemeSettings), themeSettings,
			curThemeSettings);
	}

	private void _assertWidgetPageSpecification(
		WidgetPageSpecification widgetPageSpecification) {

		Assert.assertEquals(
			PageSpecification.Type.WIDGET_PAGE_SPECIFICATION,
			widgetPageSpecification.getType());

		Assert.assertNull(widgetPageSpecification.getWidgetPageSections());
	}

	private void _assertWidgetPageSpecification(
		WidgetPageSpecification expectedWidgetPageSpecification,
		WidgetPageSpecification actualWidgetPageSpecification) {

		Assert.assertTrue(
			Objects.deepEquals(
				expectedWidgetPageSpecification.getWidgetPageSections(),
				actualWidgetPageSpecification.getWidgetPageSections()));
	}

	private Settings _getColorSchemeNameSettings(Settings settings) {
		if (settings.getColorSchemeName() != null) {
			settings.setColorSchemeName(() -> null);

			return new Settings() {
				{
					setColorSchemeName(() -> StringPool.BLANK);
				}
			};
		}

		settings.setColorSchemeName(() -> "01");
		settings.setThemeName(() -> "Classic");

		return new Settings() {
			{
				setColorSchemeName(() -> "01");
				setThemeName(() -> "Classic");
			}
		};
	}

	private ContentPageSpecification _getContentPageSpecification(
		Settings curSettings) {

		return new ContentPageSpecification() {
			{
				setSettings(() -> curSettings);
				setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
			}
		};
	}

	private Settings _getCssSettings(Settings settings) {
		if (settings.getCss() != null) {
			settings.setCss(() -> null);

			return new Settings() {
				{
					setCss(() -> StringPool.BLANK);
				}
			};
		}

		String curCss = RandomTestUtil.randomString();

		settings.setCss(() -> curCss);

		return new Settings() {
			{
				setCss(() -> curCss);
			}
		};
	}

	private Settings _getJavaScriptSettings(Settings settings) {
		if (settings.getJavascript() != null) {
			settings.setJavascript(() -> null);

			return new Settings() {
				{
					setJavascript(() -> StringPool.BLANK);
				}
			};
		}

		String javaScript = RandomTestUtil.randomString();

		settings.setJavascript(() -> javaScript);

		return new Settings() {
			{
				setJavascript(() -> javaScript);
			}
		};
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

	private Settings _getMasterPageItemExternalReferenceSettings(
			ServiceContext serviceContext, Settings settings)
		throws Exception {

		if (settings.getMasterPageItemExternalReference() != null) {
			settings.setMasterPageItemExternalReference(() -> null);

			return new Settings() {
				{
					setMasterPageItemExternalReference(
						() -> new ItemExternalReference() {
							{
								setExternalReferenceCode(
									() -> StringPool.BLANK);
							}
						});
				}
			};
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryTestUtil.getMasterLayoutPageTemplateEntry(
				serviceContext, WorkflowConstants.STATUS_APPROVED);

		ItemExternalReference itemExternalReference =
			new ItemExternalReference() {
				{
					setExternalReferenceCode(
						layoutPageTemplateEntry::getExternalReferenceCode);
				}
			};

		settings.setMasterPageItemExternalReference(
			() -> itemExternalReference);

		return new Settings() {
			{
				setMasterPageItemExternalReference(() -> itemExternalReference);
			}
		};
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

	private PageElement[] _getPageElements(
		int count, String curParentExternalReferenceCode) {

		PageElement[] pageElements = new PageElement[count];

		for (int i = 0; i < count; i++) {
			String curExternalReferenceCode = RandomTestUtil.randomString();

			int curPosition = i;

			pageElements[i] = new PageElement() {
				{
					setExternalReferenceCode(curExternalReferenceCode);
					setPageElementDefinition(
						() -> new ContainerPageElementDefinition() {
							{
								setIndexed(() -> Boolean.FALSE);
								setType(
									() -> PageElementDefinition.Type.CONTAINER);
							}
						});
					setPageElements(
						() -> {
							if (!RandomTestUtil.randomBoolean()) {
								return null;
							}

							return _getPageElements(
								RandomTestUtil.randomInt(1, 2),
								curExternalReferenceCode);
						});
					setParentExternalReferenceCode(
						() -> curParentExternalReferenceCode);
					setPosition(() -> curPosition);
				}
			};
		}

		return pageElements;
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

	private Settings _getStyleBookItemExternalReferenceSettings(
			ServiceContext serviceContext, Settings settings)
		throws Exception {

		if (settings.getStyleBookItemExternalReference() != null) {
			settings.setStyleBookItemExternalReference(() -> null);

			return new Settings() {
				{
					setStyleBookItemExternalReference(
						() -> new ItemExternalReference() {
							{
								setExternalReferenceCode(
									() -> StringPool.BLANK);
							}
						});
				}
			};
		}

		StyleBookEntry styleBookEntry = _addStyleBookEntry(serviceContext);

		ItemExternalReference itemExternalReference =
			new ItemExternalReference() {
				{
					setExternalReferenceCode(
						styleBookEntry::getExternalReferenceCode);
				}
			};

		settings.setStyleBookItemExternalReference(() -> itemExternalReference);

		return new Settings() {
			{
				setStyleBookItemExternalReference(() -> itemExternalReference);
			}
		};
	}

	private Settings _getThemeNameSettings(Settings settings) {
		if (settings.getThemeName() != null) {
			settings.setColorSchemeName(() -> null);
			settings.setThemeName(() -> null);

			return new Settings() {
				{
					setColorSchemeName(() -> StringPool.BLANK);
					setThemeName(() -> StringPool.BLANK);
				}
			};
		}

		settings.setThemeName(() -> "Classic");

		return new Settings() {
			{
				setThemeName(() -> "Classic");
			}
		};
	}

	private Settings _getThemeSettingsSettings(Settings settings) {
		if (settings.getThemeSettings() != null) {
			settings.setThemeSettings(() -> null);

			return new Settings() {
				{
					setThemeSettings(() -> new HashMap<>());
				}
			};
		}

		Map<String, String> map = TreeMapBuilder.put(
			"lfr-theme:" + RandomTestUtil.randomString(),
			RandomTestUtil.randomString()
		).put(
			"lfr-theme:" + RandomTestUtil.randomString(),
			RandomTestUtil.randomString()
		).build();

		settings.setThemeSettings(() -> map);

		return new Settings() {
			{
				setThemeSettings(() -> map);
			}
		};
	}

	private UnicodeProperties _getThemeSettingsUnicodeProperties(
		UnicodeProperties unicodeProperties) {

		UnicodeProperties themeSettingsUnicodeProperties =
			new UnicodeProperties();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			String key = entry.getKey();

			if (key.startsWith("lfr-theme:")) {
				themeSettingsUnicodeProperties.setProperty(
					key, entry.getValue());
			}
		}

		return themeSettingsUnicodeProperties;
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

	private WidgetPageSpecification _getWidgetPageSpecification(
		Settings curSettings) {

		return new WidgetPageSpecification() {
			{
				setSettings(() -> curSettings);
				setType(() -> Type.WIDGET_PAGE_SPECIFICATION);
			}
		};
	}

	private boolean _isPublished(Layout draftLayout) {
		if (draftLayout == null) {
			return true;
		}

		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty("published"));
	}

	private void _modifyPageExperiences(PageExperience[] pageExperiences) {
		for (PageExperience pageExperience : pageExperiences) {
			List<PageElement> dropZonePageElements =
				TransformUtil.transformToList(
					pageExperience.getPageElements(),
					pageElement -> {
						PageElementDefinition pageElementDefinition =
							pageElement.getPageElementDefinition();

						if (Objects.equals(
								pageElementDefinition.getType(),
								PageElementDefinition.Type.DROP_ZONE)) {

							return pageElement;
						}

						return null;
					});

			pageExperience.setPageElements(
				() -> {
					PageElement[] pageElements = _getPageElements(
						RandomTestUtil.randomInt(1, 3), StringPool.BLANK);

					if (ListUtil.isEmpty(dropZonePageElements)) {
						return pageElements;
					}

					for (int i = 0; i < dropZonePageElements.size(); i++) {
						PageElement pageElement = dropZonePageElements.get(i);

						pageElement.setPosition(pageElements.length + i);
					}

					return ArrayUtil.append(
						pageElements,
						dropZonePageElements.toArray(new PageElement[0]));
				});
		}
	}

	private void _modifySettings(
			ServiceContext serviceContext, Settings settings)
		throws Exception {

		if (Validator.isNotNull(settings.getJavascript())) {
			settings.setJavascript(() -> null);
		}
		else {
			settings.setJavascript(RandomTestUtil::randomString);
		}

		if (settings.getMasterPageItemExternalReference() != null) {
			settings.setMasterPageItemExternalReference(() -> null);
		}
		else {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryTestUtil.
					getMasterLayoutPageTemplateEntry(
						serviceContext, WorkflowConstants.STATUS_APPROVED);

			settings.setMasterPageItemExternalReference(
				() -> new ItemExternalReference() {
					{
						setExternalReferenceCode(
							layoutPageTemplateEntry::getExternalReferenceCode);
					}
				});
		}

		if (settings.getStyleBookItemExternalReference() != null) {
			settings.setStyleBookItemExternalReference(() -> null);
		}
		else {
			StyleBookEntry styleBookEntry = _addStyleBookEntry(serviceContext);

			settings.setStyleBookItemExternalReference(
				() -> new ItemExternalReference() {
					{
						setExternalReferenceCode(
							styleBookEntry::getExternalReferenceCode);
					}
				});
		}

		if (Validator.isNotNull(settings.getThemeName())) {
			settings.setColorSchemeName(() -> null);
			settings.setThemeName(() -> null);
		}
		else {
			if (RandomTestUtil.randomBoolean()) {
				settings.setColorSchemeName("01");
			}

			settings.setThemeName("Classic");
		}

		if (Validator.isNotNull(settings.getThemeSettings())) {
			settings.setThemeSettings(() -> null);
		}
		else {
			settings.setThemeSettings(
				() -> HashMapBuilder.put(
					"lfr-theme:regular:show-maximize-minimize-application-" +
						"links",
					"true"
				).build());
		}
	}

	private void _testDeleteSiteSiteByExternalReferenceCodePageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode()));

		Layout draftLayout = layout.fetchDraftLayout();

		_assertDeleteSiteSiteByExternalReferenceCodePageSpecification(
			draftLayout);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode()));

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode()));

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertDeleteSiteSiteByExternalReferenceCodePageSpecification(
			draftLayout);
	}

	private void _testGetSiteSiteByExternalReferenceCodePageSpecification(
			Layout layout, String pageSpecificationExternalReferenceCode)
		throws Exception {

		PageSpecification pageSpecification =
			pageSpecificationResource.
				getSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					pageSpecificationExternalReferenceCode);

		Assert.assertEquals(
			pageSpecificationExternalReferenceCode,
			pageSpecification.getExternalReferenceCode());

		_assertPageSpecificationSetting(
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

	private void
			_testGetSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
				Layout layout, ServiceContext serviceContext)
		throws Exception {

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode());

		Layout draftLayout = layout.fetchDraftLayout();

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		draftLayout = _layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode());
		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		layout = _layoutLocalService.getLayout(layout.getPlid());

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode());

		draftLayout = layout.fetchDraftLayout();

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			draftLayout, draftLayout.getExternalReferenceCode());

		draftLayout = _layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_testGetSiteSiteByExternalReferenceCodePageSpecification(
			layout, layout.getExternalReferenceCode());
		_testGetSiteSiteByExternalReferenceCodePageSpecification(
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

	private void _testPatchSiteSiteByExternalReferenceCodePageSpecification(
			Layout layout, ServiceContext serviceContext)
		throws Exception {

		PageSpecification pageSpecification =
			pageSpecificationResource.
				getSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					layout.getExternalReferenceCode());

		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithSettings(
			pageSpecification, serviceContext,
			settings -> _getWidgetPageSpecification(settings));

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					patchSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode(), pageSpecification));
	}

	private void _testPatchSiteSiteByExternalReferenceCodePageSpecification(
			PageSpecification pageSpecification,
			UnsafeSupplier<PageSpecification, Exception> unsafeSupplier)
		throws Exception {

		Assert.assertTrue(
			equals(
				pageSpecification,
				pageSpecificationResource.
					patchSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						pageSpecification.getExternalReferenceCode(),
						unsafeSupplier.get())));
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
				Layout layout, ServiceContext serviceContext)
		throws Exception {

		Layout draftLayout = _updateLayout(
			layout.fetchDraftLayout(), serviceContext);

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)
				pageSpecificationResource.
					getSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode());

		_modifyPageExperiences(contentPageSpecification.getPageExperiences());

		contentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
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
			() ->
				pageSpecificationResource.
					patchSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode(),
						new ContentPageSpecification() {
							{
								setPageExperiences(
									() -> ArrayUtil.append(
										contentPageSpecification.
											getPageExperiences(),
										new PageExperience()));
								setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
							}
						}));

		_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithSettings(
			contentPageSpecification, serviceContext,
			settings -> _getContentPageSpecification(settings));

		contentPageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					patchSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode(),
						contentPageSpecification));
	}

	private void
			_testPatchSiteSiteByExternalReferenceCodePageSpecificationWithSettings(
				PageSpecification pageSpecification,
				ServiceContext serviceContext,
				UnsafeFunction<Settings, PageSpecification, Exception>
					unsafeFunction)
		throws Exception {

		Settings settings = pageSpecification.getSettings();

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(_getColorSchemeNameSettings(settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(
				_getMasterPageItemExternalReferenceSettings(
					serviceContext, settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(_getCssSettings(settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(_getJavaScriptSettings(settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(
				_getStyleBookItemExternalReferenceSettings(
					serviceContext, settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(_getThemeNameSettings(settings)));

		_testPatchSiteSiteByExternalReferenceCodePageSpecification(
			pageSpecification,
			() -> unsafeFunction.apply(_getThemeSettingsSettings(settings)));
	}

	private void _testPutSiteSiteByExternalReferenceCodePageSpecification(
			Layout layout, String pageSpecificationExternalReferenceCode,
			ServiceContext serviceContext)
		throws Exception {

		layout = _updateLayout(layout, serviceContext);

		PageSpecification pageSpecification =
			pageSpecificationResource.
				getSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					pageSpecificationExternalReferenceCode);

		_modifySettings(serviceContext, pageSpecification.getSettings());

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		PageSpecification putPageSpecification =
			pageSpecificationResource.
				putSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					pageSpecificationExternalReferenceCode, pageSpecification);

		equals(pageSpecification, putPageSpecification);
	}

	private void
			_testPutSiteSiteByExternalReferenceCodePageSpecificationWithLayoutWithDraftLayout(
				Layout layout, ServiceContext serviceContext)
		throws Exception {

		Layout draftLayout = _updateLayout(
			layout.fetchDraftLayout(), serviceContext);

		PageSpecification pageSpecification =
			pageSpecificationResource.
				getSiteSiteByExternalReferenceCodePageSpecification(
					testGroup.getExternalReferenceCode(),
					draftLayout.getExternalReferenceCode());

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					putSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode(),
						pageSpecification));
		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					putSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode(), pageSpecification));

		pageSpecification.setStatus(PageSpecification.Status.DRAFT);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					putSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						layout.getExternalReferenceCode(), pageSpecification));

		_assertPutSiteSiteByExternalReferenceCodeContentPageSpecification(
			draftLayout, serviceContext);

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		pageSpecification.setStatus(PageSpecification.Status.APPROVED);

		_assertProblemException(
			"BAD_REQUEST",
			() ->
				pageSpecificationResource.
					putSiteSiteByExternalReferenceCodePageSpecification(
						testGroup.getExternalReferenceCode(),
						draftLayout.getExternalReferenceCode(),
						pageSpecification));

		_layoutLocalService.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);

		_assertPutSiteSiteByExternalReferenceCodeContentPageSpecification(
			draftLayout, serviceContext);
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

	@Inject
	private ThemeLocalService _themeLocalService;

}