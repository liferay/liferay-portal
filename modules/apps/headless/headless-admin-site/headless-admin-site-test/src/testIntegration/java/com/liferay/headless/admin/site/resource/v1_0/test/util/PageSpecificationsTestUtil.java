/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.headless.admin.site.client.custom.field.CustomField;
import com.liferay.headless.admin.site.client.custom.field.CustomValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.GeneralConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.Settings;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetLookAndFeelConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSection;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.problem.Problem;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.junit.Assert;

/**
 * @author Lourdes Fernández Besada
 */
public class PageSpecificationsTestUtil {

	public static void assertContentPageSpecification(
			PageSpecification pageSpecification, long plid)
		throws Exception {

		Layout layout = LayoutLocalServiceUtil.getLayout(plid);

		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			pageSpecification.getExternalReferenceCode());

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

		Assert.assertEquals(
			PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
			pageSpecification.getType());
	}

	public static void assertCustomFields(
			CustomField[][] expectedCustomFields, long groupId,
			PageSpecification[] pageSpecifications)
		throws Exception {

		Assert.assertEquals(
			Arrays.toString(pageSpecifications), expectedCustomFields.length,
			pageSpecifications.length);

		for (int i = 0; i < pageSpecifications.length; i++) {
			_assertCustomFields(
				_getExpectedCustomFields(expectedCustomFields[i]), groupId,
				pageSpecifications[i]);
		}
	}

	public static void assertPageSpecifications(
		ContentPageSpecification expectedDraftContentPageSpecification,
		ContentPageSpecification expectedPublishedContentPageSpecification,
		PageSpecification[] pageSpecifications, Layout layout,
		PageSpecification.Status status) {

		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		String expectedPublishedContentPageSpecificationExternalReferenceCode =
			expectedPublishedContentPageSpecification.
				getExternalReferenceCode();

		if (expectedPublishedContentPageSpecificationExternalReferenceCode ==
				null) {

			Assert.assertNotNull(layout.getExternalReferenceCode());
			Assert.assertNotNull(
				publishedContentPageSpecification.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				expectedPublishedContentPageSpecificationExternalReferenceCode,
				layout.getExternalReferenceCode());
			Assert.assertEquals(
				expectedPublishedContentPageSpecificationExternalReferenceCode,
				publishedContentPageSpecification.getExternalReferenceCode());
		}

		Assert.assertEquals(
			expectedPublishedContentPageSpecification.getStatus(),
			publishedContentPageSpecification.getStatus());

		ContentPageSpecification draftContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[1];

		Assert.assertNull(
			draftContentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode());
		Assert.assertEquals(
			draftContentPageSpecification.getExternalReferenceCode(),
			publishedContentPageSpecification.
				getDraftContentPageSpecificationExternalReferenceCode());

		if (expectedDraftContentPageSpecification.getExternalReferenceCode() ==
				null) {

			Assert.assertNotNull(
				draftContentPageSpecification.getExternalReferenceCode());
		}
		else {
			Assert.assertEquals(
				expectedDraftContentPageSpecification.
					getExternalReferenceCode(),
				draftContentPageSpecification.getExternalReferenceCode());
		}

		Assert.assertEquals(
			expectedDraftContentPageSpecification.getStatus(),
			draftContentPageSpecification.getStatus());

		Assert.assertEquals(
			status, publishedContentPageSpecification.getStatus());

		PageExperiencesTestUtil.assertPageExperiences(
			expectedPublishedContentPageSpecification.getPageExperiences(),
			layout, publishedContentPageSpecification.getPageExperiences());

		Assert.assertEquals(
			expectedPublishedContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode(),
			publishedContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());

		Layout draftLayout = layout.fetchDraftLayout();

		if (Objects.equals(PageSpecification.Status.APPROVED, status)) {
			Assert.assertTrue(_isPublished(draftLayout));
		}
		else {
			Assert.assertFalse(_isPublished(draftLayout));
		}

		Assert.assertEquals(
			draftContentPageSpecification.getExternalReferenceCode(),
			draftLayout.getExternalReferenceCode());

		PageExperiencesTestUtil.assertPageExperiences(
			expectedDraftContentPageSpecification.getPageExperiences(),
			draftLayout, draftContentPageSpecification.getPageExperiences());

		SettingsTestUtil.assertSettings(
			expectedDraftContentPageSpecification.getSettings(),
			draftContentPageSpecification.getSettings());
		SettingsTestUtil.assertSettings(
			expectedPublishedContentPageSpecification.getSettings(),
			publishedContentPageSpecification.getSettings());

		Assert.assertEquals(
			expectedDraftContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode(),
			draftContentPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode());

		if (Objects.equals(
				PageSpecification.Status.APPROVED,
				expectedDraftContentPageSpecification.getStatus())) {

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, draftLayout.getStatus());
		}
		else {
			Assert.assertEquals(
				WorkflowConstants.STATUS_DRAFT, draftLayout.getStatus());
		}
	}

	public static void assertPageSpecifications(
			Layout layout, PageSpecification[] pageSpecifications)
		throws Exception {

		Assert.assertTrue(ArrayUtil.isNotEmpty(pageSpecifications));

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			Assert.assertEquals(
				Arrays.toString(pageSpecifications), 1,
				pageSpecifications.length);

			PageSpecification pageSpecification = pageSpecifications[0];

			String externalReferenceCode = layout.getExternalReferenceCode();

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

			if ((layoutPageTemplateEntry != null) &&
				(layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

				externalReferenceCode =
					layoutPageTemplateEntry.getExternalReferenceCode();
			}

			Assert.assertEquals(
				externalReferenceCode,
				pageSpecification.getExternalReferenceCode());

			Assert.assertEquals(
				PageSpecification.Status.APPROVED,
				pageSpecification.getStatus());
			Assert.assertEquals(
				PageSpecification.Type.WIDGET_PAGE_SPECIFICATION,
				pageSpecification.getType());

			return;
		}

		Assert.assertEquals(
			Arrays.toString(pageSpecifications), 2, pageSpecifications.length);

		if (layout.isTypeUtility()) {
			for (PageSpecification pageSpecification : pageSpecifications) {
				Assert.assertNull(pageSpecification.getCustomFields());
			}
		}

		assertContentPageSpecification(pageSpecifications[0], layout.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		assertContentPageSpecification(
			pageSpecifications[1], draftLayout.getPlid());
	}

	public static void assertWidgetPageSpecification(
		WidgetPageSpecification expectedWidgetPageSpecification,
		WidgetPageSpecification actualWidgetPageSpecification) {

		SettingsTestUtil.assertSettings(
			expectedWidgetPageSpecification.getSettings(),
			actualWidgetPageSpecification.getSettings());

		Assert.assertArrayEquals(
			expectedWidgetPageSpecification.getWidgetPageSections(),
			actualWidgetPageSpecification.getWidgetPageSections());
	}

	public static void assertWidgetPageSpecifications(
		PageSpecification[] expectedPageSpecifications,
		PageSpecification[] actualPageSpecifications) {

		Assert.assertEquals(
			actualPageSpecifications.toString(),
			expectedPageSpecifications.length, actualPageSpecifications.length);
		Assert.assertEquals(
			actualPageSpecifications.toString(), 1,
			actualPageSpecifications.length);

		assertWidgetPageSpecification(
			(WidgetPageSpecification)expectedPageSpecifications[0],
			(WidgetPageSpecification)actualPageSpecifications[0]);
	}

	public static void assertWidgetPageSpecifications(
		PageSpecification[] pageSpecifications,
		WidgetPageSpecification widgetPageSpecification) {

		Assert.assertEquals(
			Arrays.toString(pageSpecifications), 1, pageSpecifications.length);

		assertWidgetPageSpecification(
			widgetPageSpecification,
			(WidgetPageSpecification)pageSpecifications[0]);
	}

	public static ContentPageSpecification getContentPageSpecification(
		String contentPageSpecificationExternalReferenceCode,
		CustomField[] customFields,
		String draftContentPageSpecificationExternalReferenceCode,
		PageExperience[] pageExperiences, PageSpecification.Status status) {

		ContentPageSpecification contentPageSpecification =
			new ContentPageSpecification() {
				{
					setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
				}
			};

		contentPageSpecification.setCustomFields(customFields);
		contentPageSpecification.
			setDraftContentPageSpecificationExternalReferenceCode(
				draftContentPageSpecificationExternalReferenceCode);
		contentPageSpecification.setExternalReferenceCode(
			contentPageSpecificationExternalReferenceCode);

		if (pageExperiences == null) {
			pageExperiences = PageExperiencesTestUtil.getPageExperiences(
				contentPageSpecificationExternalReferenceCode);
		}
		else {
			PageExperiencesTestUtil.modifyPageExperiences(pageExperiences);
		}

		contentPageSpecification.setPageExperiences(pageExperiences);
		contentPageSpecification.setStatus(status);

		return contentPageSpecification;
	}

	public static ContentPageSpecification getContentPageSpecification(
		String draftContentPageSpecificationExternalReferenceCode,
		PageSpecification.Status status) {

		return getContentPageSpecification(
			RandomTestUtil.randomString(), null,
			draftContentPageSpecificationExternalReferenceCode, null, status);
	}

	public static PageSpecification[] getContentPageSpecifications(
		String publishedPageSpecificationExternalReferenceCode) {

		return _getContentPageSpecifications(
			getCustomFields(), RandomTestUtil.randomString(), null,
			getCustomFields(), publishedPageSpecificationExternalReferenceCode,
			null);
	}

	public static CustomField[] getCustomFields() {
		return new CustomField[] {
			_getCustomField(_EXPANDO_ATTRIBUTE_NAMES[0], (String)null),
			_getCustomField(
				_EXPANDO_ATTRIBUTE_NAMES[1], RandomTestUtil.randomString())
		};
	}

	public static ExpandoTableAutocloseable getExpandoTableAutoCloseable()
		throws Exception {

		return new ExpandoTableAutocloseable();
	}

	public static PageSpecification[] getPageSpecifications(
		String externalReferenceCode, SitePage.Type type) {

		if (type == SitePage.Type.CONTENT_PAGE) {
			ContentPageSpecification draftContentPageSpecification =
				getContentPageSpecification(
					null, PageSpecification.Status.DRAFT);

			ContentPageSpecification publishedContentPageSpecification =
				getContentPageSpecification(
					draftContentPageSpecification.getExternalReferenceCode(),
					PageSpecification.Status.APPROVED);

			publishedContentPageSpecification.setExternalReferenceCode(
				externalReferenceCode);

			return new PageSpecification[] {
				publishedContentPageSpecification, draftContentPageSpecification
			};
		}

		return new PageSpecification[] {
			getWidgetPageSpecification(
				null, externalReferenceCode, null,
				PageSpecification.Status.APPROVED,
				new WidgetPageSection[] {
					new WidgetPageSection() {
						{
							setCustomizable(() -> Boolean.FALSE);
							setId(() -> "column-1");
							setWidgetPageWidgetInstances(
								() -> new WidgetPageWidgetInstance[0]);
						}
					}
				})
		};
	}

	public static PageSpecification[] getPatchPageSpecifications(
		PageSpecification[] pageSpecifications) {

		if (pageSpecifications.length == 2) {
			ContentPageSpecification draftContentPageSpecification = null;

			ContentPageSpecification publishedContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[0];

			String draftContentPageSpecificationExternalReferenceCode =
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode();

			if (draftContentPageSpecificationExternalReferenceCode != null) {
				draftContentPageSpecification =
					(ContentPageSpecification)pageSpecifications[1];
			}
			else {
				draftContentPageSpecification =
					publishedContentPageSpecification;
				publishedContentPageSpecification =
					(ContentPageSpecification)pageSpecifications[1];
			}

			return _getContentPageSpecifications(
				getCustomFields(),
				draftContentPageSpecification.getExternalReferenceCode(),
				draftContentPageSpecification.getPageExperiences(),
				getCustomFields(),
				publishedContentPageSpecification.getExternalReferenceCode(),
				publishedContentPageSpecification.getPageExperiences());
		}

		WidgetPageSpecification widgetPageSpecification =
			(WidgetPageSpecification)pageSpecifications[0];

		return new PageSpecification[] {
			getWidgetPageSpecification(
				getCustomFields(),
				widgetPageSpecification.getExternalReferenceCode(), null,
				PageSpecification.Status.APPROVED,
				widgetPageSpecification.getWidgetPageSections())
		};
	}

	public static WidgetPageSection[] getWidgetPageSections(
		String layoutTemplateId) {

		List<String> columns = new ArrayList<>();

		if (Objects.equals(layoutTemplateId, "1_column")) {
			columns.add("column-1");
		}
		else if (Objects.equals(layoutTemplateId, "2_columns_ii")) {
			columns.add("column-1");
			columns.add("column-2");
		}

		return TransformUtil.transformToArray(
			columns,
			column -> new WidgetPageSection() {
				{
					setCustomizable(() -> Boolean.FALSE);
					setId(() -> column);
					setWidgetPageWidgetInstances(
						() -> _getWidgetPageWidgetInstances(column));
				}
			},
			WidgetPageSection.class);
	}

	public static WidgetPageSpecification getWidgetPageSpecification(
		CustomField[] customFields, String externalReferenceCode,
		Settings settings, PageSpecification.Status status,
		WidgetPageSection[] widgetPageSections) {

		WidgetPageSpecification widgetPageSpecification =
			new WidgetPageSpecification() {
				{
					setType(() -> Type.WIDGET_PAGE_SPECIFICATION);
				}
			};

		widgetPageSpecification.setCustomFields(customFields);
		widgetPageSpecification.setExternalReferenceCode(externalReferenceCode);
		widgetPageSpecification.setSettings(settings);
		widgetPageSpecification.setStatus(status);
		widgetPageSpecification.setWidgetPageSections(widgetPageSections);

		return widgetPageSpecification;
	}

	public static PageSpecification[] getWidgetPageSpecifications(
		CustomField[] customFields, String layoutTemplateId,
		String publishedPageSpecificationExternalReferenceCode) {

		return new PageSpecification[] {
			getWidgetPageSpecification(
				customFields, publishedPageSpecificationExternalReferenceCode,
				new Settings(), PageSpecification.Status.APPROVED,
				getWidgetPageSections(layoutTemplateId))
		};
	}

	public static void testPostSitePageSpecification(
			Layout layout, PageSpecification[] pageSpecifications,
			ServiceContext serviceContext,
			UnsafeFunction
				<ContentPageSpecification, ContentPageSpecification, Exception>
					unsafeFunction)
		throws Exception {

		assertPageSpecifications(layout, pageSpecifications);

		Layout draftLayout = layout.fetchDraftLayout();

		Assert.assertFalse(_isPublished(draftLayout));

		assertPageSpecifications(layout, pageSpecifications);

		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		Assert.assertEquals(
			publishedContentPageSpecification.getStatus(),
			PageSpecification.Status.DRAFT);

		publishedContentPageSpecification.setExternalReferenceCode(
			layout.getExternalReferenceCode());

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		ContentPageSpecification draftContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[1];

		draftContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());
		draftContentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_DRAFT);

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));

		ContentLayoutTestUtil.publishLayout(layout.fetchDraftLayout(), layout);

		draftLayout = layout.fetchDraftLayout();

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		publishedContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());

		publishedContentPageSpecification.setStatus(
			PageSpecification.Status.APPROVED);

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		publishedContentPageSpecification.setExternalReferenceCode(
			layout.getExternalReferenceCode());

		_assertProblemException(
			() -> unsafeFunction.apply(publishedContentPageSpecification));

		draftContentPageSpecification.setExternalReferenceCode(
			draftLayout.getExternalReferenceCode());
		draftContentPageSpecification.setStatus(PageSpecification.Status.DRAFT);

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));

		draftLayout = LayoutLocalServiceUtil.updateStatus(
			TestPropsValues.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		draftContentPageSpecification.setExternalReferenceCode((String)null);
		draftContentPageSpecification.setStatus((PageSpecification.Status)null);

		assertContentPageSpecification(
			unsafeFunction.apply(draftContentPageSpecification),
			draftLayout.getPlid());

		draftLayout = LayoutLocalServiceUtil.getLayout(draftLayout.getPlid());

		Assert.assertEquals(
			draftLayout.getStatus(), WorkflowConstants.STATUS_DRAFT);

		_assertProblemException(
			() -> unsafeFunction.apply(draftContentPageSpecification));
	}

	public static class ExpandoTableAutocloseable implements AutoCloseable {

		public ExpandoTableAutocloseable() throws Exception {
			_originalPermissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			_expandoTable = ExpandoTableLocalServiceUtil.addDefaultTable(
				PortalUtil.getDefaultCompanyId(), Layout.class.getName());

			for (int i = 0; i < _EXPANDO_ATTRIBUTE_NAMES.length; i++) {
				ExpandoColumnLocalServiceUtil.addColumn(
					_expandoTable.getTableId(), _EXPANDO_ATTRIBUTE_NAMES[i],
					ExpandoColumnConstants.STRING,
					_EXPANDO_ATTRIBUTE_VALUES[i]);
			}
		}

		@Override
		public void close() throws Exception {
			try {
				if (_expandoTable != null) {
					ExpandoTableLocalServiceUtil.deleteTable(_expandoTable);
				}
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(
					_originalPermissionChecker);
			}
		}

		private final ExpandoTable _expandoTable;
		private final PermissionChecker _originalPermissionChecker;

	}

	private static void _assertCustomFields(
			CustomField[] expectedCustomFields, long groupId,
			PageSpecification pageSpecification)
		throws Exception {

		CustomField[] customFields = pageSpecification.getCustomFields();

		Assert.assertTrue(
			Arrays.toString(customFields) +
				" does not contain all custom fields in " +
					Arrays.toString(expectedCustomFields),
			ArrayUtil.containsAll(customFields, expectedCustomFields));

		Layout layout = LayoutLocalServiceUtil.getLayoutByExternalReferenceCode(
			pageSpecification.getExternalReferenceCode(), groupId);

		ExpandoBridge expandoBridge = layout.getExpandoBridge();

		Map<String, Serializable> attributes = expandoBridge.getAttributes();

		Assert.assertFalse(attributes.isEmpty());

		for (CustomField customField : expectedCustomFields) {
			CustomValue customValue = customField.getCustomValue();

			Assert.assertEquals(
				customValue.getData(), attributes.get(customField.getName()));
		}
	}

	private static void _assertProblemException(
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();
			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
			Assert.assertNull(problem.getTitle());
		}
	}

	private static String[] _getActionIds(String roleName) {
		if (Objects.equals(RoleConstants.GUEST, roleName)) {
			if (RandomTestUtil.randomBoolean()) {
				return null;
			}

			return new String[] {ActionKeys.VIEW};
		}

		int random = RandomTestUtil.randomInt(0, 3);

		if (random == 0) {
			return null;
		}

		if (random == 1) {
			return new String[] {ActionKeys.VIEW};
		}

		if (random == 2) {
			return new String[] {ActionKeys.CONFIGURATION, ActionKeys.VIEW};
		}

		return new String[] {
			ActionKeys.ADD_TO_PAGE, ActionKeys.CONFIGURATION, ActionKeys.VIEW
		};
	}

	private static ContentPageSpecification[] _getContentPageSpecifications(
		CustomField[] draftPageSpecificationCustomFields,
		String draftPageSpecificationExternalReferenceCode,
		PageExperience[] draftPageSpecificationPageExperiences,
		CustomField[] publishedPageSpecificationCustomFields,
		String publishedPageSpecificationExternalReferenceCode,
		PageExperience[] publishedPageSpecificationPageExperiences) {

		ContentPageSpecification draftContentPageSpecification =
			getContentPageSpecification(
				draftPageSpecificationExternalReferenceCode,
				draftPageSpecificationCustomFields, null,
				draftPageSpecificationPageExperiences,
				PageSpecification.Status.DRAFT);

		ContentPageSpecification publishedContentPageSpecification =
			getContentPageSpecification(
				publishedPageSpecificationExternalReferenceCode,
				publishedPageSpecificationCustomFields,
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedPageSpecificationPageExperiences,
				PageSpecification.Status.APPROVED);

		return new ContentPageSpecification[] {
			publishedContentPageSpecification, draftContentPageSpecification
		};
	}

	private static CustomField _getCustomField(
		String attributeName, CustomField[] customFields) {

		CustomField[] filteredCustomFields = ArrayUtil.filter(
			customFields,
			customField -> Objects.equals(
				customField.getName(), attributeName));

		if (ArrayUtil.isEmpty(filteredCustomFields)) {
			return null;
		}

		return filteredCustomFields[0];
	}

	private static CustomField _getCustomField(String curName, String curData) {
		return new CustomField() {
			{
				customValue = new CustomValue() {
					{
						data = curData;
						dataType = "Text";
					}
				};
				name = curName;
			}
		};
	}

	private static CustomField[] _getExpectedCustomFields(
		CustomField[] customFields) {

		CustomField[] expectedCustomFields =
			new CustomField[_EXPANDO_ATTRIBUTE_NAMES.length];

		for (int i = 0; i < _EXPANDO_ATTRIBUTE_NAMES.length; i++) {
			String attributeName = _EXPANDO_ATTRIBUTE_NAMES[i];

			CustomField customField = _getCustomField(
				attributeName, customFields);

			if (customField == null) {
				expectedCustomFields[i] = _getCustomField(
					attributeName,
					GetterUtil.getString(_EXPANDO_ATTRIBUTE_VALUES[i]));

				continue;
			}

			CustomValue customValue = customField.getCustomValue();

			if (Validator.isNull(customValue.getData())) {
				customValue.setData(
					GetterUtil.getString(_EXPANDO_ATTRIBUTE_VALUES[i]));
			}

			expectedCustomFields[i] = customField;
		}

		return expectedCustomFields;
	}

	private static GeneralConfig.ApplicationDecorator
		_getRandomApplicationDecorator() {

		int random = RandomTestUtil.randomInt(0, 3);

		if (random == 0) {
			return null;
		}

		if (random == 1) {
			return GeneralConfig.ApplicationDecorator.BAREBONE;
		}

		if (random == 2) {
			return GeneralConfig.ApplicationDecorator.BORDERLESS;
		}

		return GeneralConfig.ApplicationDecorator.DECORATE;
	}

	private static Map<String, Object> _getWidgetConfig() {
		Map<String, Object> map = new TreeMap<>();

		for (int i = 0; i < RandomTestUtil.randomInt(0, 3); i++) {
			map.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());
		}

		if (map.isEmpty()) {
			return null;
		}

		return map;
	}

	private static WidgetLookAndFeelConfig _getWidgetLookAndFeelConfig() {
		WidgetLookAndFeelConfig widgetLookAndFeelConfig =
			new WidgetLookAndFeelConfig();

		GeneralConfig generalConfig = new GeneralConfig();

		generalConfig.setApplicationDecorator(
			() -> _getRandomApplicationDecorator());

		generalConfig.setUseCustomTitle(RandomTestUtil.randomBoolean());

		if (generalConfig.getUseCustomTitle() &&
			RandomTestUtil.randomBoolean()) {

			generalConfig.setCustomTitle_i18n(
				() -> HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.SPAIN),
					RandomTestUtil.randomString()
				).put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.US),
					RandomTestUtil.randomString()
				).build());
		}

		widgetLookAndFeelConfig.setGeneralConfig(generalConfig);

		return widgetLookAndFeelConfig;
	}

	private static WidgetPageWidgetInstance[] _getWidgetPageWidgetInstances(
		String column) {

		List<WidgetPageWidgetInstance> widgetPageWidgetInstances =
			new ArrayList<>();

		for (int i = 0; i < RandomTestUtil.randomInt(0, 3); i++) {
			WidgetPageWidgetInstance widgetPageWidgetInstance =
				new WidgetPageWidgetInstance();

			String widgetName = AssetPublisherPortletKeys.ASSET_PUBLISHER;

			if (RandomTestUtil.randomBoolean()) {
				widgetName = SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU;
			}

			String widgetInstanceId = RandomTestUtil.randomString();

			widgetPageWidgetInstance.setExternalReferenceCode(
				PortletIdCodec.encode(widgetName, widgetInstanceId));

			widgetPageWidgetInstance.setParentSectionId(column);
			widgetPageWidgetInstance.setPosition(i);
			widgetPageWidgetInstance.setWidgetConfig(() -> _getWidgetConfig());
			widgetPageWidgetInstance.setWidgetInstanceId(widgetInstanceId);
			widgetPageWidgetInstance.setWidgetLookAndFeelConfig(
				() -> _getWidgetLookAndFeelConfig());
			widgetPageWidgetInstance.setWidgetName(widgetName);
			widgetPageWidgetInstance.setWidgetPermissions(
				() -> _getWidgetPermissions());

			widgetPageWidgetInstances.add(widgetPageWidgetInstance);
		}

		return widgetPageWidgetInstances.toArray(
			new WidgetPageWidgetInstance[0]);
	}

	private static WidgetPermission[] _getWidgetPermissions() {
		WidgetPermission[] widgetPermissions = TransformUtil.transformToArray(
			ListUtil.fromArray(
				RoleConstants.GUEST, RoleConstants.SITE_CONTENT_REVIEWER,
				RoleConstants.SITE_MEMBER),
			roleName -> {
				String[] actionIds = _getActionIds(roleName);

				if (actionIds == null) {
					return null;
				}

				WidgetPermission widgetPermission = new WidgetPermission();

				widgetPermission.setActionIds(actionIds);
				widgetPermission.setRoleName(roleName);

				return widgetPermission;
			},
			WidgetPermission.class);

		if (ArrayUtil.isEmpty(widgetPermissions)) {
			return null;
		}

		return widgetPermissions;
	}

	private static boolean _isPublished(Layout draftLayout) {
		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty(
				LayoutTypeSettingsConstants.KEY_PUBLISHED));
	}

	private static final String[] _EXPANDO_ATTRIBUTE_NAMES = {
		RandomTestUtil.randomString(), RandomTestUtil.randomString(),
		RandomTestUtil.randomString()
	};

	private static final String[] _EXPANDO_ATTRIBUTE_VALUES = {
		RandomTestUtil.randomString(), RandomTestUtil.randomString(), null
	};

}