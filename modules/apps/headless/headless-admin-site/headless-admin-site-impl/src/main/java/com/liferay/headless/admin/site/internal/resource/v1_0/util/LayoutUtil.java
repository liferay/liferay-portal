/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.dto.v1_0.Settings;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.util.ColorSchemeFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceServiceUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryServiceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtil {

	public static Layout addContentLayout(
			long groupId, PageSpecification[] pageSpecifications,
			boolean privateLayout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> robotsMap, String type,
			UnicodeProperties typeSettingsUnicodeProperties, boolean hidden,
			boolean system, Map<Locale, String> friendlyURLMap, int status,
			ServiceContext serviceContext)
		throws Exception {

		if (typeSettingsUnicodeProperties == null) {
			typeSettingsUnicodeProperties = new UnicodeProperties();
		}

		if (pageSpecifications == null) {
			Layout layout = LayoutLocalServiceUtil.addLayout(
				null, serviceContext.getUserId(), groupId, privateLayout, 0, 0,
				0, nameMap, titleMap, descriptionMap, null, robotsMap, type,
				typeSettingsUnicodeProperties.toString(), hidden, system,
				friendlyURLMap, 0L, serviceContext);

			return LayoutLocalServiceUtil.updateStatus(
				serviceContext.getUserId(), layout.getPlid(), status,
				serviceContext);
		}

		if (pageSpecifications.length != 2) {
			throw new UnsupportedOperationException();
		}

		ContentPageSpecification draftContentPageSpecification = null;
		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		if (Validator.isNull(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode())) {

			draftContentPageSpecification = publishedContentPageSpecification;
			publishedContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}
		else {
			draftContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}

		if (Validator.isNull(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode()) ||
			!Objects.equals(
				draftContentPageSpecification.getExternalReferenceCode(),
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode())) {

			throw new UnsupportedOperationException();
		}

		Settings settings = publishedContentPageSpecification.getSettings();

		if ((settings == null) ||
			Validator.isNull(settings.getMasterPageItemExternalReference())) {

			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-footer", Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-header", Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-header-search",
				Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:wrap-widget-page-content",
				Boolean.FALSE.toString());
		}

		long masterLayoutPlid = 0;

		if ((settings != null) &&
			(settings.getMasterPageItemExternalReference() != null)) {

			if (Objects.equals(
					LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
					serviceContext.getAttribute(
						"layout.page.template.entry.type"))) {

				throw new UnsupportedOperationException();
			}

			ItemExternalReference itemExternalReference =
				settings.getMasterPageItemExternalReference();

			if (Validator.isNotNull(
					itemExternalReference.getExternalReferenceCode())) {

				if (itemExternalReference.getScope() != null) {
					throw new UnsupportedOperationException();
				}

				Layout masterLayout =
					LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						groupId);

				if (masterLayout == null) {
					throw new UnsupportedOperationException();
				}

				masterLayoutPlid = masterLayout.getPlid();
			}
		}

		serviceContext.setAttribute(
			"defaultSegmentsExperienceExternalReferenceCode",
			SegmentsExperienceUtil.
				getDefaultSegmentsExperienceExternalReferenceCode(
					publishedContentPageSpecification.getPageExperiences()));
		serviceContext.setAttribute(
			"draftLayoutDefaultSegmentsExperienceExternalReferenceCode",
			SegmentsExperienceUtil.
				getDefaultSegmentsExperienceExternalReferenceCode(
					draftContentPageSpecification.getPageExperiences()));
		serviceContext.setAttribute(
			"draftLayoutExternalReferenceCode",
			draftContentPageSpecification.getExternalReferenceCode());

		Layout prototypeLayout = _getLayoutPrototypeLayout(
			groupId, publishedContentPageSpecification, serviceContext);

		if (prototypeLayout != null) {
			serviceContext.setAttribute(
				"sourcePrototypeLayoutUuid", prototypeLayout.getUuid());

			Layout draftPrototypeLayout = _getLayoutPrototypeLayout(
				groupId, draftContentPageSpecification, serviceContext);

			if (draftPrototypeLayout != null) {
				serviceContext.setAttribute(
					"draftLayoutSourcePrototypeLayoutUuid",
					draftPrototypeLayout.getUuid());
			}
		}

		if (Objects.equals(
				publishedContentPageSpecification.getStatus(),
				PageSpecification.Status.APPROVED)) {

			serviceContext.setAttribute("published", Boolean.TRUE.toString());

			typeSettingsUnicodeProperties.setProperty(
				LayoutTypeSettingsConstants.KEY_PUBLISHED,
				Boolean.TRUE.toString());
		}
		else {
			serviceContext.setAttribute("published", Boolean.FALSE.toString());
		}

		Layout layout = LayoutLocalServiceUtil.addLayout(
			publishedContentPageSpecification.getExternalReferenceCode(),
			serviceContext.getUserId(), groupId, privateLayout, 0, 0, 0,
			nameMap, titleMap, descriptionMap, null, robotsMap, type,
			typeSettingsUnicodeProperties.toString(), hidden, system,
			friendlyURLMap, masterLayoutPlid, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		int draftLayoutStatus = WorkflowConstants.STATUS_APPROVED;

		if (Objects.equals(
				draftContentPageSpecification.getStatus(),
				PageSpecification.Status.DRAFT)) {

			draftLayoutStatus = WorkflowConstants.STATUS_DRAFT;
		}

		updateLayout(
			draftContentPageSpecification, draftLayout, nameMap, titleMap,
			descriptionMap, draftLayout.getRobotsMap(),
			draftLayout.getFriendlyURLMap(), draftLayoutStatus, serviceContext);

		return updateLayout(
			publishedContentPageSpecification, layout, nameMap, titleMap,
			descriptionMap, robotsMap, friendlyURLMap, status, serviceContext);
	}

	public static Layout addDraftToLayout(
			ContentPageSpecification contentPageSpecification, Layout layout,
			ServiceContext serviceContext)
		throws Exception {

		if ((Validator.isNotNull(contentPageSpecification.getStatus()) &&
			 !Objects.equals(
				 contentPageSpecification.getStatus(),
				 PageSpecification.Status.DRAFT)) ||
			layout.isDraftLayout()) {

			throw new UnsupportedOperationException();
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if ((Validator.isNotNull(
				contentPageSpecification.getExternalReferenceCode()) &&
			 !Objects.equals(
				 contentPageSpecification.getExternalReferenceCode(),
				 draftLayout.getExternalReferenceCode())) ||
			!Objects.equals(
				draftLayout.getStatus(), WorkflowConstants.STATUS_APPROVED)) {

			throw new UnsupportedOperationException();
		}

		return updateLayout(
			contentPageSpecification, draftLayout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			draftLayout.getRobotsMap(), draftLayout.getFriendlyURLMap(),
			WorkflowConstants.STATUS_DRAFT, serviceContext);
	}

	public static boolean isPublished(Layout layout) {
		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return true;
		}

		if (layout.isDraftLayout()) {
			return GetterUtil.getBoolean(
				layout.getTypeSettingsProperty(
					LayoutTypeSettingsConstants.KEY_PUBLISHED));
		}

		Layout draftLayout = layout.fetchDraftLayout();

		return GetterUtil.getBoolean(
			draftLayout.getTypeSettingsProperty(
				LayoutTypeSettingsConstants.KEY_PUBLISHED));
	}

	public static Layout updateContentLayout(
			Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> robotsMap, Map<Locale, String> friendlyURLMap,
			PageSpecification[] pageSpecifications,
			ServiceContext serviceContext)
		throws Exception {

		if (pageSpecifications == null) {
			return _updateLayout(
				layout, nameMap, titleMap, descriptionMap, robotsMap,
				layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
				layout.getMasterLayoutPlid(), friendlyURLMap, serviceContext);
		}

		if (pageSpecifications.length != 2) {
			throw new UnsupportedOperationException();
		}

		ContentPageSpecification draftContentPageSpecification = null;
		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		if (!Objects.equals(
				layout.getExternalReferenceCode(),
				publishedContentPageSpecification.getExternalReferenceCode())) {

			draftContentPageSpecification = publishedContentPageSpecification;
			publishedContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}
		else {
			draftContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (!Objects.equals(
				draftLayout.getExternalReferenceCode(),
				draftContentPageSpecification.getExternalReferenceCode()) ||
			!Objects.equals(
				layout.getExternalReferenceCode(),
				publishedContentPageSpecification.getExternalReferenceCode()) ||
			!Objects.equals(
				publishedContentPageSpecification.
					getDraftContentPageSpecificationExternalReferenceCode(),
				draftContentPageSpecification.getExternalReferenceCode())) {

			throw new UnsupportedOperationException();
		}

		int draftLayoutStatus = WorkflowConstants.STATUS_APPROVED;

		if (Objects.equals(
				draftContentPageSpecification.getStatus(),
				PageSpecification.Status.DRAFT)) {

			draftLayoutStatus = WorkflowConstants.STATUS_DRAFT;
		}

		int status = layout.getStatus();

		if (Objects.equals(
				publishedContentPageSpecification.getStatus(),
				PageSpecification.Status.APPROVED)) {

			serviceContext.setAttribute("published", Boolean.TRUE.toString());

			status = WorkflowConstants.STATUS_APPROVED;
		}

		updateLayout(
			draftContentPageSpecification, draftLayout, nameMap, titleMap,
			descriptionMap, robotsMap, draftLayout.getFriendlyURLMap(),
			draftLayoutStatus, serviceContext);

		return updateLayout(
			publishedContentPageSpecification, layout, nameMap, titleMap,
			descriptionMap, robotsMap, friendlyURLMap, status, serviceContext);
	}

	public static Layout updateContentLayout(
			Layout layout, UnicodeProperties typeSettingsUnicodeProperties,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> robotsMap,
			Map<Locale, String> friendlyURLMap,
			PageSpecification[] pageSpecifications,
			ServiceContext serviceContext)
		throws Exception {

		layout = LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		return updateContentLayout(
			layout, nameMap, titleMap, descriptionMap, robotsMap,
			friendlyURLMap, pageSpecifications, serviceContext);
	}

	public static Layout updateLayout(
			ContentPageSpecification contentPageSpecification, Layout layout,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> robotsMap,
			Map<Locale, String> friendlyURLMap, int status,
			ServiceContext serviceContext)
		throws Exception {

		updateLayout(
			layout, nameMap, titleMap, descriptionMap, robotsMap,
			friendlyURLMap, contentPageSpecification.getSettings(),
			serviceContext);

		_updatePageExperiences(
			layout, contentPageSpecification.getPageExperiences(),
			serviceContext);

		return LayoutLocalServiceUtil.updateStatus(
			serviceContext.getUserId(), layout.getPlid(), status,
			serviceContext);
	}

	public static Layout updateLayout(
			Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> robotsMap, Map<Locale, String> friendlyURLMap,
			Settings settings, ServiceContext serviceContext)
		throws Exception {

		layout = _updateLookAndFeel(layout, settings);

		return _updateLayout(
			layout, nameMap, titleMap, descriptionMap, robotsMap,
			_getStyleBookEntryId(serviceContext.getScopeGroupId(), settings),
			_getFaviconFileEntryId(settings, serviceContext),
			_getMasterLayoutPlid(
				serviceContext.getScopeGroupId(), layout, settings),
			friendlyURLMap, serviceContext);
	}

	private static long _getFaviconFileEntryId(
			Settings settings, ServiceContext serviceContext)
		throws Exception {

		if ((settings == null) || (settings.getFavIcon() == null) ||
			!(settings.getFavIcon() instanceof ItemExternalReference)) {

			return 0;
		}

		ItemExternalReference itemExternalReference =
			(ItemExternalReference)settings.getFavIcon();

		if (Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return 0;
		}

		long groupId = serviceContext.getScopeGroupId();

		Scope scope = itemExternalReference.getScope();

		if (scope != null) {
			groupId = GroupUtil.getGroupId(
				true, true, serviceContext.getCompanyId(),
				scope.getExternalReferenceCode());
		}

		DLFileEntry dlFileEntry =
			DLFileEntryServiceUtil.fetchFileEntryByExternalReferenceCode(
				groupId, itemExternalReference.getExternalReferenceCode());

		if (dlFileEntry == null) {
			throw new UnsupportedOperationException();
		}

		return dlFileEntry.getFileEntryId();
	}

	private static Layout _getLayoutPrototypeLayout(
			long groupId, PageSpecification pageSpecification,
			ServiceContext serviceContext)
		throws Exception {

		if (Validator.isNull(
				pageSpecification.
					getSiteTemplatePageSpecificationExternalReferenceCode())) {

			return null;
		}

		boolean privateLayout = Boolean.FALSE;

		int layoutPageTemplateEntryType = GetterUtil.getInteger(
			serviceContext.getAttribute("layout.page.template.entry.type"), -1);

		if (Objects.equals(
				LayoutPageTemplateEntryTypeConstants.BASIC,
				layoutPageTemplateEntryType) ||
			Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntryType) ||
			Objects.equals(
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE,
				layoutPageTemplateEntryType)) {

			privateLayout = Boolean.TRUE;
		}
		else if (Objects.equals(
					PageSpecification.Type.CONTENT_PAGE_SPECIFICATION,
					pageSpecification.getType())) {

			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			if (Validator.isNull(
					contentPageSpecification.
						getDraftContentPageSpecificationExternalReferenceCode())) {

				privateLayout = Boolean.TRUE;
			}
		}

		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(
			groupId, privateLayout);

		if (!layoutSet.isLayoutSetPrototypeLinkActive()) {
			return null;
		}

		LayoutSetPrototype layoutSetPrototype =
			LayoutSetPrototypeLocalServiceUtil.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSet.getLayoutSetPrototypeUuid(),
					layoutSet.getCompanyId());

		return LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
			pageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode(),
			layoutSetPrototype.getGroupId(), privateLayout);
	}

	private static long _getMasterLayoutPlid(
			long groupId, Layout layout, Settings settings)
		throws Exception {

		if (settings == null) {
			return 0;
		}

		ItemExternalReference itemExternalReference =
			settings.getMasterPageItemExternalReference();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return 0;
		}

		if (itemExternalReference.getScope() != null) {
			throw new UnsupportedOperationException();
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if ((layoutPageTemplateEntry != null) &&
			Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		layoutPageTemplateEntry =
			LayoutPageTemplateEntryServiceUtil.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(), groupId);

		if ((layoutPageTemplateEntry == null) ||
			!Objects.equals(
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				layoutPageTemplateEntry.getType())) {

			throw new UnsupportedOperationException();
		}

		return layoutPageTemplateEntry.getPlid();
	}

	private static long _getStyleBookEntryId(long groupId, Settings settings)
		throws Exception {

		if (settings == null) {
			return 0;
		}

		ItemExternalReference itemExternalReference =
			settings.getStyleBookItemExternalReference();

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return 0;
		}

		StyleBookEntry styleBookEntry =
			StyleBookEntryServiceUtil.getStyleBookEntryByExternalReferenceCode(
				itemExternalReference.getExternalReferenceCode(), groupId);

		return styleBookEntry.getStyleBookEntryId();
	}

	private static Layout _updateLayout(
			Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> robotsMap, long styleBookEntryId,
			long faviconFileEntryId, long masterLayoutPlid,
			Map<Locale, String> friendlyURLMap, ServiceContext serviceContext)
		throws Exception {

		if (layout.isTypeAssetDisplay() || layout.isTypeUtility()) {
			serviceContext.setAttribute(
				"layout.instanceable.allowed", Boolean.TRUE);
		}

		return LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), nameMap, titleMap, descriptionMap,
			layout.getKeywordsMap(), robotsMap, layout.getType(),
			layout.isHidden(), friendlyURLMap, layout.getIconImage(), null,
			styleBookEntryId, faviconFileEntryId, masterLayoutPlid,
			serviceContext);
	}

	private static Layout _updateLookAndFeel(Layout layout, Settings settings)
		throws Exception {

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		if (settings != null) {
			unicodeProperties.setProperty(
				"javascript", settings.getJavascript());
		}
		else {
			unicodeProperties.remove("javascript");
		}

		for (String key : ListUtil.fromCollection(unicodeProperties.keySet())) {
			if (key.startsWith("lfr-theme:")) {
				unicodeProperties.remove(key);
			}
		}

		if ((settings != null) &&
			MapUtil.isNotEmpty(settings.getThemeSettings())) {

			unicodeProperties.putAll(
				(Map<String, ? extends String>)settings.getThemeSettings());
		}

		layout = LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			unicodeProperties.toString());

		Theme theme = null;
		String themeId = null;

		if ((settings != null) &&
			Validator.isNotNull(settings.getThemeName())) {

			for (Theme curTheme :
					ThemeLocalServiceUtil.getThemes(layout.getCompanyId())) {

				if (!Objects.equals(
						curTheme.getName(), settings.getThemeName())) {

					continue;
				}

				theme = curTheme;
				themeId = curTheme.getThemeId();

				break;
			}

			if (themeId == null) {
				throw new UnsupportedOperationException();
			}
		}

		String colorSchemeId = null;

		if ((settings != null) &&
			Validator.isNotNull(settings.getColorSchemeName())) {

			if (theme == null) {
				throw new UnsupportedOperationException();
			}

			for (ColorScheme colorScheme : theme.getColorSchemes()) {
				if (!Objects.equals(
						colorScheme.getName(), settings.getColorSchemeName())) {

					continue;
				}

				colorSchemeId = colorScheme.getColorSchemeId();

				break;
			}

			if (colorSchemeId == null) {
				ColorScheme colorScheme =
					ColorSchemeFactoryUtil.getDefaultRegularColorScheme();

				if (Objects.equals(
						colorScheme.getName(), settings.getColorSchemeName())) {

					colorSchemeId = colorScheme.getColorSchemeId();
				}
			}

			if (colorSchemeId == null) {
				throw new UnsupportedOperationException();
			}
		}

		String css = null;

		if (settings != null) {
			css = settings.getCss();
		}

		return LayoutServiceUtil.updateLookAndFeel(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			themeId, colorSchemeId, css);
	}

	private static void _updatePageExperiences(
			Layout layout, PageExperience[] pageExperiences,
			ServiceContext serviceContext)
		throws Exception {

		List<SegmentsExperience> segmentsExperiences =
			SegmentsExperienceServiceUtil.getSegmentsExperiences(
				layout.getGroupId(), layout.getPlid(), true);

		if ((pageExperiences == null) ||
			(pageExperiences.length != segmentsExperiences.size())) {

			throw new UnsupportedOperationException();
		}

		Map<String, SegmentsExperience> segmentsExperiencesMap =
			new HashMap<>();

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			segmentsExperiencesMap.put(
				segmentsExperience.getExternalReferenceCode(),
				segmentsExperience);
		}

		for (PageExperience pageExperience : pageExperiences) {
			SegmentsExperience segmentsExperience = segmentsExperiencesMap.get(
				pageExperience.getExternalReferenceCode());

			if (segmentsExperience == null) {
				throw new UnsupportedOperationException();
			}

			SegmentsExperienceUtil.updateSegmentsExperience(
				layout, pageExperience, segmentsExperience, serviceContext);
		}
	}

}