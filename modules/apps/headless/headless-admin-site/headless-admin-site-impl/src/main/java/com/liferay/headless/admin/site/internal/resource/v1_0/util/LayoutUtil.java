/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalServiceUtil;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.ClientExtension;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.FavIcon;
import com.liferay.headless.admin.site.dto.v1_0.FavIconClientExtension;
import com.liferay.headless.admin.site.dto.v1_0.FavIconItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.GeneralConfig;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.Scope;
import com.liferay.headless.admin.site.dto.v1_0.Settings;
import com.liferay.headless.admin.site.dto.v1_0.WidgetLookAndFeelConfig;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSection;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.importer.util.PortletPermissionsImporterUtil;
import com.liferay.layout.importer.util.PortletPreferencesPortletConfigurationImporterUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceServiceUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryServiceUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtil {

	public static Layout addContentLayout(
			CETManager cetManager, long groupId,
			InfoItemServiceRegistry infoItemServiceRegistry,
			PageSpecification[] pageSpecifications, long parentLayoutId,
			boolean privateLayout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			String type, UnicodeProperties typeSettingsUnicodeProperties,
			boolean hidden, boolean system, Map<Locale, String> friendlyURLMap,
			int status, ServiceContext serviceContext)
		throws Exception {

		if (typeSettingsUnicodeProperties == null) {
			typeSettingsUnicodeProperties = new UnicodeProperties();
		}

		if (pageSpecifications == null) {
			Layout layout = LayoutLocalServiceUtil.addLayout(
				GetterUtil.getString(
					serviceContext.getAttribute("layoutExternalReferenceCode"),
					null),
				serviceContext.getUserId(), groupId, privateLayout,
				parentLayoutId, 0, 0, nameMap, titleMap, descriptionMap,
				keywordsMap, robotsMap, type,
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

				LayoutPageTemplateEntry layoutPageTemplateEntry =
					LayoutPageTemplateEntryLocalServiceUtil.
						fetchLayoutPageTemplateEntryByExternalReferenceCode(
							itemExternalReference.getExternalReferenceCode(),
							groupId);

				if (layoutPageTemplateEntry == null) {
					throw new UnsupportedOperationException();
				}

				masterLayoutPlid = layoutPageTemplateEntry.getPlid();
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

		ServiceContextUtil.setLayoutSetPrototypeLayoutERC(
			groupId, publishedContentPageSpecification, serviceContext);

		ServiceContextUtil.setLayoutSetPrototypeLayoutERC(
			groupId, draftContentPageSpecification, serviceContext);

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
			serviceContext.getUserId(), groupId, privateLayout, parentLayoutId,
			0, 0, nameMap, titleMap, descriptionMap, keywordsMap, robotsMap,
			type, typeSettingsUnicodeProperties.toString(), hidden, system,
			friendlyURLMap, masterLayoutPlid, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		int draftLayoutStatus = WorkflowConstants.STATUS_APPROVED;

		if (Objects.equals(
				draftContentPageSpecification.getStatus(),
				PageSpecification.Status.DRAFT)) {

			draftLayoutStatus = WorkflowConstants.STATUS_DRAFT;
		}

		updateLayout(
			cetManager, infoItemServiceRegistry, draftLayout,
			draftLayout.getNameMap(), draftLayout.getTitleMap(),
			draftLayout.getDescriptionMap(), draftLayout.getKeywordsMap(),
			draftLayout.getRobotsMap(), draftLayout.getFriendlyURLMap(),
			draftContentPageSpecification, draftLayoutStatus, serviceContext);

		return updateLayout(
			cetManager, infoItemServiceRegistry, layout, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap, friendlyURLMap,
			publishedContentPageSpecification, status, serviceContext);
	}

	public static Layout addDraftToLayout(
			CETManager cetManager,
			ContentPageSpecification contentPageSpecification,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
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
			cetManager, infoItemServiceRegistry, draftLayout,
			layout.getNameMap(), layout.getTitleMap(),
			layout.getDescriptionMap(), draftLayout.getKeywordsMap(),
			draftLayout.getRobotsMap(), draftLayout.getFriendlyURLMap(),
			contentPageSpecification, WorkflowConstants.STATUS_DRAFT,
			serviceContext);
	}

	public static Layout addPortletLayout(
			CETManager cetManager, String externalReferenceCode,
			InfoItemServiceRegistry infoItemServiceRegistry, long groupId,
			long parentLayoutId, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			UnicodeProperties typeSettingsUnicodeProperties,
			boolean hiddenFromNavigation, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext,
			WidgetPageSpecification widgetPageSpecification)
		throws Exception {

		String typeSettings = null;

		if (typeSettingsUnicodeProperties != null) {
			typeSettings = typeSettingsUnicodeProperties.toString();
		}

		_setExpandoBridgeAttributes(widgetPageSpecification, serviceContext);

		Layout layout = LayoutServiceUtil.addLayout(
			externalReferenceCode, groupId, false, parentLayoutId, nameMap,
			titleMap, descriptionMap, keywordsMap, robotsMap,
			LayoutConstants.TYPE_PORTLET, typeSettings, hiddenFromNavigation,
			friendlyURLMap, 0, serviceContext);

		layout = updateLayout(
			cetManager, infoItemServiceRegistry, layout, layout.getNameMap(),
			layout.getTitleMap(), layout.getDescriptionMap(),
			layout.getKeywordsMap(), layout.getRobotsMap(),
			layout.getFriendlyURLMap(), widgetPageSpecification,
			layout.getStatus(), serviceContext);

		return updatePortletLayout(
			cetManager, layout, layout.getNameMap(), layout.getTitleMap(),
			layout.getDescriptionMap(), layout.getKeywordsMap(),
			layout.getRobotsMap(), layout.getFriendlyURLMap(),
			typeSettingsUnicodeProperties, serviceContext,
			widgetPageSpecification);
	}

	public static String getParentSectionId(Layout layout, String portletId) {
		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		return layoutTypePortlet.getColumn(portletId);
	}

	public static Integer getPosition(Layout layout, String portletId) {
		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		for (String columnId : layoutTypePortlet.getColumns()) {
			String columnValue = typeSettingsUnicodeProperties.getProperty(
				columnId, StringPool.BLANK);

			List<String> portletIds = ListUtil.fromString(
				columnValue, StringPool.COMMA);

			int position = portletIds.indexOf(portletId);

			if (position >= 0) {
				return position;
			}
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Position for portlet cannot be obtained since portlet ",
					portletId, " cannot be found in layout ",
					layout.getPlid()));
		}

		return null;
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
			CETManager cetManager,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, Map<Locale, String> friendlyURLMap,
			UnicodeProperties typeSettingsUnicodeProperties,
			PageSpecification[] pageSpecifications,
			ServiceContext serviceContext)
		throws Exception {

		if (!Objects.equals(
				typeSettingsUnicodeProperties,
				layout.getTypeSettingsProperties())) {

			layout = LayoutServiceUtil.updateLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), typeSettingsUnicodeProperties.toString());
		}

		if (pageSpecifications == null) {
			return _updateLayout(
				layout, nameMap, titleMap, descriptionMap, keywordsMap,
				robotsMap, layout.getStyleBookEntryId(),
				layout.getFaviconFileEntryId(), layout.getMasterLayoutPlid(),
				friendlyURLMap, serviceContext);
		}

		if (pageSpecifications.length != 2) {
			throw new UnsupportedOperationException();
		}

		ContentPageSpecification draftContentPageSpecification = null;
		ContentPageSpecification publishedContentPageSpecification =
			(ContentPageSpecification)pageSpecifications[0];

		if (Objects.equals(
				layout.getExternalReferenceCode(),
				publishedContentPageSpecification.getExternalReferenceCode())) {

			draftContentPageSpecification =
				(ContentPageSpecification)pageSpecifications[1];
		}
		else {
			draftContentPageSpecification = publishedContentPageSpecification;
			publishedContentPageSpecification =
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
			cetManager, infoItemServiceRegistry, draftLayout, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap,
			draftLayout.getFriendlyURLMap(), draftContentPageSpecification,
			draftLayoutStatus, serviceContext);

		return updateLayout(
			cetManager, infoItemServiceRegistry, layout, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap, friendlyURLMap,
			publishedContentPageSpecification, status, serviceContext);
	}

	public static Layout updateLayout(
			CETManager cetManager,
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, Map<Locale, String> friendlyURLMap,
			PageSpecification pageSpecification, int status,
			ServiceContext serviceContext)
		throws Exception {

		layout = _updateLayout(
			cetManager, layout, nameMap, titleMap, descriptionMap, keywordsMap,
			robotsMap, friendlyURLMap, pageSpecification, serviceContext);

		if (pageSpecification instanceof ContentPageSpecification) {
			ContentPageSpecification contentPageSpecification =
				(ContentPageSpecification)pageSpecification;

			_updatePageExperiences(
				infoItemServiceRegistry, layout,
				contentPageSpecification.getPageExperiences(), serviceContext);
		}

		if (status == layout.getStatus()) {
			return layout;
		}

		return LayoutLocalServiceUtil.updateStatus(
			serviceContext.getUserId(), layout.getPlid(), status,
			serviceContext);
	}

	public static Layout updatePortletLayout(
			CETManager cetManager, Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			Map<Locale, String> friendlyURLMap,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext,
			WidgetPageSpecification widgetPageSpecification)
		throws Exception {

		layout = _updateLayout(
			cetManager, layout, nameMap, titleMap, descriptionMap, keywordsMap,
			robotsMap, friendlyURLMap, widgetPageSpecification, serviceContext);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).fastLoad(
			layout.getTypeSettings()
		).build();

		if (typeSettingsUnicodeProperties != null) {
			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			layoutTypePortlet.removeCustomization(unicodeProperties);

			typeSettingsUnicodeProperties.setProperty(
				LayoutConstants.CUSTOMIZABLE_LAYOUT,
				String.valueOf(
					GetterUtil.getBoolean(
						typeSettingsUnicodeProperties.get(
							LayoutConstants.CUSTOMIZABLE_LAYOUT))));

			String layoutTemplateId = typeSettingsUnicodeProperties.getProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
				PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID);

			typeSettingsUnicodeProperties.setProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID,
				layoutTemplateId);

			if (!Objects.equals(
					layoutTemplateId,
					layoutTypePortlet.getLayoutTemplateId())) {

				layoutTypePortlet.setLayoutTemplateId(
					serviceContext.getUserId(), layoutTemplateId);
			}

			unicodeProperties.putAll(typeSettingsUnicodeProperties);

			layout.setTypeSettingsProperties(unicodeProperties);
		}

		return _updatePortletLayout(
			layout, serviceContext, widgetPageSpecification);
	}

	private static void _addPortletLookAndFeelToConfigurationMap(
		long groupId, Map<String, Object> map,
		WidgetLookAndFeelConfig widgetLookAndFeelConfig) {

		if ((widgetLookAndFeelConfig == null) ||
			(widgetLookAndFeelConfig.getGeneralConfig() == null)) {

			return;
		}

		GeneralConfig generalConfig =
			widgetLookAndFeelConfig.getGeneralConfig();

		if (generalConfig.getApplicationDecorator() != null) {
			map.put(
				"portletSetupPortletDecoratorId",
				StringUtil.lowerCase(
					generalConfig.getApplicationDecoratorAsString()));
		}

		Map<String, String> customTitleI18n =
			generalConfig.getCustomTitle_i18n();

		if (customTitleI18n != null) {
			Map<Locale, String> localizedMap = LocalizedMapUtil.getLocalizedMap(
				customTitleI18n);

			for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
				if (!localizedMap.containsKey(locale)) {
					continue;
				}

				map.put(
					"portletSetupTitle_" + LocaleUtil.toLanguageId(locale),
					localizedMap.get(locale));
			}
		}

		if (generalConfig.getUseCustomTitle() != null) {
			map.put(
				"portletSetupUseCustomTitle",
				Boolean.toString(generalConfig.getUseCustomTitle()));
		}
	}

	private static long _getFaviconFileEntryId(
			Settings settings, ServiceContext serviceContext)
		throws Exception {

		if ((settings == null) || (settings.getFavIcon() == null)) {
			return 0;
		}

		FavIcon favIcon = settings.getFavIcon();

		if (!(favIcon instanceof FavIconItemExternalReference)) {
			return 0;
		}

		FavIconItemExternalReference favIconItemExternalReference =
			(FavIconItemExternalReference)favIcon;

		long groupId = serviceContext.getScopeGroupId();

		Scope scope = favIconItemExternalReference.getScope();

		if (scope != null) {
			groupId = GroupUtil.getGroupId(
				true, true, serviceContext.getCompanyId(),
				scope.getExternalReferenceCode());
		}

		DLFileEntry dlFileEntry =
			DLFileEntryServiceUtil.fetchFileEntryByExternalReferenceCode(
				groupId,
				favIconItemExternalReference.getExternalReferenceCode());

		if (dlFileEntry == null) {
			throw new UnsupportedOperationException();
		}

		return dlFileEntry.getFileEntryId();
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

	private static void _importPortletConfiguration(
			Layout layout, String portletId,
			WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		Map<String, Object> configurationMap =
			widgetPageWidgetInstance.getWidgetConfig();

		if ((configurationMap != null) &&
			ListUtil.isNotEmpty(
				TransformUtil.transform(
					configurationMap.keySet(),
					key -> {
						if (_excludePreferencesNames.contains(key) ||
							key.startsWith("portletSetupTitle_")) {

							return key;
						}

						return null;
					}))) {

			throw new UnsupportedOperationException();
		}

		if (configurationMap == null) {
			configurationMap = new HashMap<>();
		}

		_addPortletLookAndFeelToConfigurationMap(
			layout.getGroupId(), configurationMap,
			widgetPageWidgetInstance.getWidgetLookAndFeelConfig());

		PortletPreferencesPortletConfigurationImporterUtil.
			importPortletConfiguration(
				layout.getPlid(), portletId, configurationMap);
	}

	private static void _setExpandoBridgeAttributes(
		PageSpecification pageSpecification, ServiceContext serviceContext) {

		if (pageSpecification == null) {
			serviceContext.setExpandoBridgeAttributes(null);
		}
		else {
			serviceContext.setExpandoBridgeAttributes(
				CustomFieldsUtil.toMap(
					Layout.class.getName(), serviceContext.getCompanyId(),
					pageSpecification.getCustomFields(), null));
		}
	}

	private static void _updateClientExtensionEntryRel(
			CETManager cetManager, long classNameId,
			ClientExtension clientExtension, Layout layout, String type,
			ServiceContext serviceContext)
		throws Exception {

		ClientExtension[] clientExtensions = null;

		if (clientExtension != null) {
			clientExtensions = new ClientExtension[] {clientExtension};
		}

		_updateClientExtensionEntryRels(
			cetManager, classNameId, clientExtensions, layout, type,
			serviceContext);
	}

	private static void _updateClientExtensionEntryRels(
			CETManager cetManager, long classNameId,
			ClientExtension[] clientExtensions, Layout layout, String type,
			ServiceContext serviceContext)
		throws Exception {

		ClientExtensionEntryRelLocalServiceUtil.deleteClientExtensionEntryRels(
			classNameId, layout.getPlid(), type);

		if (ArrayUtil.isEmpty(clientExtensions)) {
			return;
		}

		for (ClientExtension clientExtension : clientExtensions) {
			CET cet = cetManager.getCET(
				layout.getCompanyId(),
				clientExtension.getExternalReferenceCode());

			if (cet == null) {
				LogUtil.logOptionalReference(
					ClientExtension.class,
					clientExtension.getExternalReferenceCode(),
					layout.getCompanyId());
			}

			ClientExtensionEntryRelLocalServiceUtil.addClientExtensionEntryRel(
				serviceContext.getUserId(), layout.getGroupId(), classNameId,
				layout.getPlid(), clientExtension.getExternalReferenceCode(),
				type,
				UnicodePropertiesBuilder.create(
					clientExtension.getClientExtensionConfig(), true
				).buildString(),
				serviceContext);
		}
	}

	private static void _updateClientExtensions(
			CETManager cetManager, Layout layout, Settings settings,
			ServiceContext serviceContext)
		throws Exception {

		if (settings == null) {
			ClientExtensionEntryRelLocalServiceUtil.
				deleteClientExtensionEntryRels(
					PortalUtil.getClassNameId(Layout.class), layout.getPlid());

			return;
		}

		if (layout.isTypeUtility()) {
			if (Validator.isNotNull(settings.getFavIcon()) ||
				ArrayUtil.isNotEmpty(settings.getGlobalCSSClientExtensions()) ||
				ArrayUtil.isNotEmpty(settings.getGlobalJSClientExtensions()) ||
				Validator.isNotNull(settings.getThemeCSSClientExtension()) ||
				Validator.isNotNull(
					settings.getThemeSpritemapClientExtension())) {

				throw new UnsupportedOperationException();
			}

			return;
		}

		long classNameId = PortalUtil.getClassNameId(Layout.class);

		ClientExtension clientExtension = null;

		FavIcon favIcon = settings.getFavIcon();

		if (favIcon instanceof FavIconClientExtension) {
			FavIconClientExtension favIconClientExtension =
				(FavIconClientExtension)favIcon;

			clientExtension = new ClientExtension() {
				{
					setClientExtensionConfig(
						() ->
							favIconClientExtension.getClientExtensionConfig());
					setExternalReferenceCode(
						() ->
							favIconClientExtension.getExternalReferenceCode());
				}
			};
		}

		_updateClientExtensionEntryRels(
			cetManager, classNameId, settings.getGlobalCSSClientExtensions(),
			layout, ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
			serviceContext);
		_updateClientExtensionEntryRels(
			cetManager, classNameId, settings.getGlobalJSClientExtensions(),
			layout, ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
			serviceContext);
		_updateClientExtensionEntryRel(
			cetManager, classNameId, settings.getThemeCSSClientExtension(),
			layout, ClientExtensionEntryConstants.TYPE_THEME_CSS,
			serviceContext);
		_updateClientExtensionEntryRel(
			cetManager, classNameId, clientExtension, layout,
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON, serviceContext);
		_updateClientExtensionEntryRel(
			cetManager, classNameId,
			settings.getThemeSpritemapClientExtension(), layout,
			ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP, serviceContext);
	}

	private static Layout _updateLayout(
			CETManager cetManager, Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			Map<Locale, String> friendlyURLMap,
			PageSpecification pageSpecification, ServiceContext serviceContext)
		throws Exception {

		Settings settings = null;

		if (pageSpecification != null) {
			settings = pageSpecification.getSettings();
		}

		_updateClientExtensions(cetManager, layout, settings, serviceContext);

		layout = _updateLookAndFeel(layout, settings);

		_setExpandoBridgeAttributes(pageSpecification, serviceContext);

		return _updateLayout(
			layout, nameMap, titleMap, descriptionMap, keywordsMap, robotsMap,
			_getStyleBookEntryId(serviceContext.getScopeGroupId(), settings),
			_getFaviconFileEntryId(settings, serviceContext),
			_getMasterLayoutPlid(
				serviceContext.getScopeGroupId(), layout, settings),
			friendlyURLMap, serviceContext);
	}

	private static Layout _updateLayout(
			Layout layout, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			long styleBookEntryId, long faviconFileEntryId,
			long masterLayoutPlid, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws Exception {

		if (layout.isTypeAssetDisplay() || layout.isTypeUtility()) {
			serviceContext.setAttribute(
				"layout.instanceable.allowed", Boolean.TRUE);
		}

		return LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			GetterUtil.getLong(
				serviceContext.getAttribute("parentLayoutId"),
				layout.getParentLayoutId()),
			nameMap, titleMap, descriptionMap, keywordsMap, robotsMap,
			layout.getType(),
			GetterUtil.getBoolean(
				serviceContext.getAttribute("hidden"), layout.isHidden()),
			friendlyURLMap, layout.getIconImage(), null, styleBookEntryId,
			faviconFileEntryId, masterLayoutPlid, serviceContext);
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

			unicodeProperties.putAll(settings.getThemeSettings());
		}

		layout = LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			unicodeProperties.toString());

		String themeId = null;
		String colorSchemeId = null;
		String css = null;

		if (settings != null) {
			themeId = settings.getThemeName();
			colorSchemeId = settings.getColorSchemeName();
			css = settings.getCss();
		}

		return LayoutServiceUtil.updateLookAndFeel(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			themeId, colorSchemeId, css);
	}

	private static void _updatePageExperiences(
			InfoItemServiceRegistry infoItemServiceRegistry, Layout layout,
			PageExperience[] pageExperiences, ServiceContext serviceContext)
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
				infoItemServiceRegistry, layout, pageExperience,
				segmentsExperience, serviceContext);
		}
	}

	private static Layout _updatePortletLayout(
			Layout layout, ServiceContext serviceContext,
			WidgetPageSpecification widgetPageSpecification)
		throws Exception {

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		if (widgetPageSpecification == null) {
			return LayoutServiceUtil.updateLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId(), unicodeProperties.toString());
		}

		WidgetPageSection[] widgetPageSections =
			widgetPageSpecification.getWidgetPageSections();

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<String> columns = layoutTypePortlet.getColumns();

		if (widgetPageSections.length != columns.size()) {
			throw new UnsupportedOperationException();
		}

		List<String> portletIds = layoutTypePortlet.getPortletIds();

		boolean layoutCustomizable = GetterUtil.getBoolean(
			unicodeProperties.get(LayoutConstants.CUSTOMIZABLE_LAYOUT));

		for (WidgetPageSection widgetPageSection : widgetPageSections) {
			boolean customizable = GetterUtil.getBoolean(
				unicodeProperties.get(
					CustomizedPages.namespaceColumnId(
						widgetPageSection.getId())));

			if (!columns.contains(widgetPageSection.getId()) ||
				(!layoutCustomizable && customizable)) {

				throw new UnsupportedOperationException();
			}

			for (WidgetPageWidgetInstance widgetPageWidgetInstance :
					widgetPageSection.getWidgetPageWidgetInstances()) {

				String portletId = PortletIdCodec.encode(
					widgetPageWidgetInstance.getWidgetName(),
					widgetPageWidgetInstance.getWidgetInstanceId());

				if (!layoutTypePortlet.hasPortletId(portletId)) {
					layoutTypePortlet.addPortletId(
						serviceContext.getUserId(), portletId,
						widgetPageWidgetInstance.getParentSectionId(),
						widgetPageWidgetInstance.getPosition());
				}
				else if (!Objects.equals(
							widgetPageWidgetInstance.getParentSectionId(),
							getParentSectionId(layout, portletId)) ||
						 !Objects.equals(
							 widgetPageWidgetInstance.getPosition(),
							 getPosition(layout, portletId))) {

					layoutTypePortlet.movePortletId(
						serviceContext.getUserId(), portletId,
						widgetPageWidgetInstance.getParentSectionId(),
						widgetPageWidgetInstance.getPosition());
				}

				_importPortletConfiguration(
					layout, portletId, widgetPageWidgetInstance);

				PortletPermissionsImporterUtil.importPortletPermissions(
					layout.getPlid(), portletId, new HashSet<>(),
					TransformUtil.transform(
						ListUtil.fromArray(
							widgetPageWidgetInstance.getWidgetPermissions()),
						widgetPermission -> HashMapBuilder.<String, Object>put(
							"actionKeys",
							ListUtil.fromArray(widgetPermission.getActionIds())
						).put(
							"roleKey", widgetPermission.getRoleName()
						).build()));

				portletIds.remove(portletId);
			}
		}

		for (String portletId : portletIds) {
			layoutTypePortlet.removePortletId(
				serviceContext.getUserId(), portletId);
		}

		return LayoutServiceUtil.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			unicodeProperties.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(LayoutUtil.class);

	private static final Collection<String> _excludePreferencesNames =
		ListUtil.fromArray(
			"portletSetupUseCustomTitle", "portletSetupPortletDecoratorId",
			"portletSetupCss");

}