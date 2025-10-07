/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.AssetUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.SitePageTypeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.NavigationSettingsUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.OpenGraphSettingsUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.SEOSettingsUtil;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = DTOConverter.class)
public class SitePageDTOConverter implements DTOConverter<Layout, SitePage> {

	@Override
	public String getContentType() {
		return SitePage.class.getSimpleName();
	}

	@Override
	public SitePage toDTO(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws Exception {

		return new SitePage() {
			{
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						layout.getAvailableLanguageIds()));
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							layout.getUserId());

						if (user == null) {
							return null;
						}

						return new Creator() {
							{
								setExternalReferenceCode(
									user::getExternalReferenceCode);
							}
						};
					});
				setDateCreated(layout::getCreateDate);
				setDateModified(layout::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExternalReferenceCode(layout::getExternalReferenceCode);
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layout.getFriendlyURLMap()));
				setKeywords(
					() -> AssetUtil.getKeywords(
						Layout.class.getName(), layout.getPlid()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layout.getNameMap()));
				setPageSettings(() -> _toPageSettings(layout));
				setParentSitePageExternalReferenceCode(
					() -> {
						if (layout.getParentLayoutId() == 0) {
							return null;
						}

						Layout parentLayout = _layoutLocalService.getLayout(
							layout.getGroupId(), layout.isPrivateLayout(),
							layout.getParentLayoutId());

						return parentLayout.getExternalReferenceCode();
					});
				setTaxonomyCategoryItemExternalReferences(
					() -> AssetUtil.getTaxonomyCategoryItemExternalReferences(
						Layout.class.getName(), layout.getPlid(),
						layout.getGroupId()));
				setType(
					() -> SitePageTypeUtil.toExternalType(layout.getType()));
				setUuid(layout::getUuid);
			}
		};
	}

	private CustomMetaTag[] _getCustomMetaTags(
		Layout layout, LayoutSEOEntryLocalService layoutSEOEntryLocalService) {

		LayoutSEOEntry layoutSEOEntry =
			layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		if (layoutSEOEntry == null) {
			return null;
		}

		List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
			layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
				layoutSEOEntry.getGroupId(),
				layoutSEOEntry.getLayoutSEOEntryId());

		List<CustomMetaTag> customMetaTags = new ArrayList<>();

		for (LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag :
				layoutSEOEntryCustomMetaTags) {

			customMetaTags.add(
				new CustomMetaTag() {
					{
						setKey(layoutSEOEntryCustomMetaTag::getProperty);
						setValue_i18n(
							() -> LocalizedMapUtil.getI18nMap(
								layoutSEOEntryCustomMetaTag.getContentMap()));
					}
				});
		}

		return customMetaTags.toArray(new CustomMetaTag[0]);
	}

	private PageSettings _getPageSettings(Layout layout) {
		SitePage.Type type = SitePageTypeUtil.toExternalType(layout.getType());

		if (type == SitePage.Type.CONTENT_PAGE) {
			return new ContentPageSettings();
		}

		return _toWidgetPageSettings(layout);
	}

	private PageSettings _toPageSettings(Layout layout) {
		PageSettings pageSettings = _getPageSettings(layout);

		pageSettings.setCustomMetaTags(
			() -> _getCustomMetaTags(layout, _layoutSEOEntryLocalService));
		pageSettings.setHiddenFromNavigation(layout::isHidden);
		pageSettings.setNavigationSettings(
			() -> NavigationSettingsUtil.toNavigationSettings(
				layout.getTypeSettingsProperties()));
		pageSettings.setOpenGraphSettings(
			() -> OpenGraphSettingsUtil.getOpenGraphSettings(
				_dlAppService, _layoutSEOEntryLocalService, layout));
		pageSettings.setQueryString(
			() -> {
				UnicodeProperties unicodeProperties =
					layout.getTypeSettingsProperties();

				return unicodeProperties.getProperty(
					com.liferay.layout.admin.kernel.model.
						LayoutTypePortletConstants.QUERY_STRING);
			});
		pageSettings.setPriority(layout::getPriority);
		pageSettings.setSeoSettings(
			() -> SEOSettingsUtil.getSeoSettings(
				_layoutSEOEntryLocalService, layout));

		return pageSettings;
	}

	private WidgetPageSettings _toWidgetPageSettings(Layout layout) {
		WidgetPageSettings widgetPageSettings = new WidgetPageSettings();

		widgetPageSettings.setCustomizable(layout::isCustomizable);
		widgetPageSettings.setCustomizableSectionIds(
			() -> {
				List<String> customizableSectionIds = new ArrayList<>();

				UnicodeProperties typeSettingsUnicodeProperties =
					UnicodePropertiesBuilder.fastLoad(
						layout.getTypeSettings()
					).build();

				typeSettingsUnicodeProperties.forEach(
					(key, value) -> {
						if (key.contains("-customizable") &&
							Objects.equals(value, "true")) {

							customizableSectionIds.add(
								key.substring(0, key.indexOf("-customizable")));
						}
					});

				List<String> sortedCustomizableSectionIds = ListUtil.sort(
					customizableSectionIds);

				return sortedCustomizableSectionIds.toArray(new String[0]);
			});
		widgetPageSettings.setLayoutTemplateId(
			() -> layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
		widgetPageSettings.setWidgetPageTemplateReference(
			() -> {
				if (layout.getLayoutPrototypeUuid() == null) {
					return null;
				}

				LayoutPrototype layoutPrototype =
					_layoutPrototypeLocalService.
						fetchLayoutPrototypeByUuidAndCompanyId(
							layout.getLayoutPrototypeUuid(),
							layout.getCompanyId());

				if (layoutPrototype == null) {
					return null;
				}

				LayoutPageTemplateEntry layoutPageTemplateEntry =
					_layoutPageTemplateEntryLocalService.
						fetchFirstLayoutPageTemplateEntry(
							layoutPrototype.getLayoutPrototypeId());

				if (layoutPageTemplateEntry == null) {
					return null;
				}

				widgetPageSettings.setInheritChanges(
					layout::isLayoutPrototypeLinkEnabled);

				return new ItemExternalReference() {
					{
						setExternalReferenceCode(
							layoutPageTemplateEntry::getExternalReferenceCode);
						setScope(
							() -> ScopeUtil.getScope(
								layout.getGroupId(),
								layoutPageTemplateEntry.getGroupId()));
					}
				};
			});

		return widgetPageSettings;
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

	@Reference
	private UserLocalService _userLocalService;

}