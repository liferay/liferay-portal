/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.CustomMetaTag;
import com.liferay.headless.admin.site.dto.v1_0.EmbeddedPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.LinkToPagePageSettings;
import com.liferay.headless.admin.site.dto.v1_0.LinkToURLPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSetPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageSettings;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.AssetUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.SitePageTypeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.NavigationSettingsUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.OpenGraphSettingsUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.SEOSettingsUtil;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.service.LayoutSEOEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
					() -> CreatorUtil.toCreator(
						layout.getUserId(), layout.getUserName()));
				setDateCreated(layout::getCreateDate);
				setDateModified(layout::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExternalReferenceCode(layout::getExternalReferenceCode);
				setFriendlyUrlPath_i18n(
					() -> {
						Map<Locale, String> friendlyURLMap =
							layout.getFriendlyURLMap();

						String layoutIdFriendlyURL =
							StringPool.SLASH + layout.getLayoutId();

						Map<String, String> i18nMap = new HashMap<>();

						for (Map.Entry<Locale, String> entry :
								friendlyURLMap.entrySet()) {

							String friendlyURL = entry.getValue();

							if (Objects.equals(
									friendlyURL, layoutIdFriendlyURL)) {

								continue;
							}

							i18nMap.put(
								LocaleUtil.toBCP47LanguageId(entry.getKey()),
								friendlyURL);
						}

						if (i18nMap.isEmpty()) {
							return null;
						}

						return i18nMap;
					});
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
				setTaxonomyCategoryBriefs(
					() -> AssetUtil.getTaxonomyCategoryBriefs(
						Layout.class.getName(), layout.getPlid(),
						layout.getGroupId()));
				setType(
					() -> SitePageTypeUtil.toExternalType(layout.getType()));
				setUuid(layout::getUuid);
			}
		};
	}

	private CustomMetaTag[] _getCustomMetaTags(LayoutSEOEntry layoutSEOEntry) {
		if (layoutSEOEntry == null) {
			return null;
		}

		List<LayoutSEOEntryCustomMetaTag> layoutSEOEntryCustomMetaTags =
			_layoutSEOEntryLocalService.getLayoutSEOEntryCustomMetaTags(
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

		if (customMetaTags.isEmpty()) {
			return null;
		}

		return customMetaTags.toArray(new CustomMetaTag[0]);
	}

	private PageSettings _getPageSettings(Layout layout) {
		SitePage.Type type = SitePageTypeUtil.toExternalType(layout.getType());

		if (type == SitePage.Type.CONTENT_PAGE) {
			return _toContentPageSettings(layout);
		}
		else if (type == SitePage.Type.EMBEDDED_PAGE) {
			return new EmbeddedPageSettings() {
				{
					setPageURL(
						() -> {
							UnicodeProperties typeSettingsUnicodeProperties =
								layout.getTypeSettingsProperties();

							return typeSettingsUnicodeProperties.getProperty(
								com.liferay.layout.admin.kernel.model.
									LayoutTypePortletConstants.EMBEDDED_LAYOUT_URL,
								StringPool.BLANK);
						});
					setType(() -> Type.EMBEDDED_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.LINK_TO_PAGE_PAGE) {
			return new LinkToPagePageSettings() {
				{
					setLinkToPageExternalReferenceCode(
						() -> {
							UnicodeProperties typeSettingsUnicodeProperties =
								layout.getTypeSettingsProperties();

							String linkToLayoutExternalReferenceCode =
								typeSettingsUnicodeProperties.get(
									"linkToLayoutExternalReferenceCode");

							if (Validator.isNotNull(
									linkToLayoutExternalReferenceCode)) {

								return linkToLayoutExternalReferenceCode;
							}

							long linkToLayoutId = GetterUtil.getLong(
								typeSettingsUnicodeProperties.get(
									"linkToLayoutId"));

							if (linkToLayoutId == 0) {
								return null;
							}

							Layout linkToLayout =
								_layoutLocalService.fetchLayout(
									layout.getGroupId(),
									layout.isPrivateLayout(), linkToLayoutId);

							if (linkToLayout == null) {
								return null;
							}

							return linkToLayout.getExternalReferenceCode();
						});
					setType(() -> Type.LINK_TO_PAGE_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.LINK_TO_URL_PAGE) {
			return new LinkToURLPageSettings() {
				{
					setPageURL(
						() -> {
							UnicodeProperties typeSettingsUnicodeProperties =
								layout.getTypeSettingsProperties();

							return typeSettingsUnicodeProperties.getProperty(
								"url", StringPool.BLANK);
						});
					setType(() -> Type.LINK_TO_URL_PAGE_SETTINGS);
				}
			};
		}
		else if (type == SitePage.Type.PAGE_SET_PAGE) {
			return new PageSetPageSettings() {
				{
					setType(() -> Type.PAGE_SET_PAGE_SETTINGS);
				}
			};
		}

		return _toWidgetPageSettings(layout);
	}

	private ContentPageSettings _toContentPageSettings(Layout layout) {
		ContentPageSettings contentPageSettings = new ContentPageSettings();

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		contentPageSettings.setCustomMetaTags(
			() -> _getCustomMetaTags(layoutSEOEntry));

		contentPageSettings.setDefaultAssetPublisherPortletId(
			() -> layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID));
		contentPageSettings.setOpenGraphSettings(
			() -> OpenGraphSettingsUtil.getOpenGraphSettings(
				_dlAppService, layoutSEOEntry));
		contentPageSettings.setSeoSettings(
			() -> SEOSettingsUtil.getSeoSettings(layout, layoutSEOEntry));

		contentPageSettings.setType(
			() -> PageSettings.Type.CONTENT_PAGE_SETTINGS);

		return contentPageSettings;
	}

	private PageSettings _toPageSettings(Layout layout) {
		PageSettings pageSettings = _getPageSettings(layout);

		pageSettings.setHiddenFromNavigation(layout::isHidden);
		pageSettings.setNavigationSettings(
			() -> NavigationSettingsUtil.toSitePageNavigationSettings(
				layout.getTypeSettingsProperties()));
		pageSettings.setPriority(layout::getPriority);

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

		LayoutSEOEntry layoutSEOEntry =
			_layoutSEOEntryLocalService.fetchLayoutSEOEntry(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getLayoutId());

		widgetPageSettings.setCustomMetaTags(
			() -> _getCustomMetaTags(layoutSEOEntry));

		widgetPageSettings.setDefaultAssetPublisherPortletId(
			() -> layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.DEFAULT_ASSET_PUBLISHER_PORTLET_ID));
		widgetPageSettings.setInheritChanges(
			() -> {
				if (Validator.isNull(
						layout.getPortletLayoutPageTemplateEntryERC())) {

					return null;
				}

				return layout.isPortletLayoutPageTemplateEntryLinkEnabled();
			});
		widgetPageSettings.setLayoutTemplateId(
			() -> layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID));
		widgetPageSettings.setOpenGraphSettings(
			() -> OpenGraphSettingsUtil.getOpenGraphSettings(
				_dlAppService, layoutSEOEntry));
		widgetPageSettings.setSeoSettings(
			() -> SEOSettingsUtil.getSeoSettings(layout, layoutSEOEntry));
		widgetPageSettings.setType(
			() -> PageSettings.Type.WIDGET_PAGE_SETTINGS);
		widgetPageSettings.setWidgetPageTemplateReference(
			() -> {
				if (Validator.isNull(
						layout.getPortletLayoutPageTemplateEntryERC())) {

					return null;
				}

				return new ItemExternalReference() {
					{
						setExternalReferenceCode(
							layout::getPortletLayoutPageTemplateEntryERC);
						setScope(
							() -> ItemScopeUtil.getItemScope(
								layout.getCompanyId(),
								layout.
									getPortletLayoutPageTemplateEntryScopeERC(),
								layout.getGroupId()));
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
	private LayoutSEOEntryLocalService _layoutSEOEntryLocalService;

}