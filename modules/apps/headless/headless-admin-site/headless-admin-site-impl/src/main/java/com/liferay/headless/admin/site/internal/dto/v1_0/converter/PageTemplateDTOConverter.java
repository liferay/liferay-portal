/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.util.ThumbnailURLReferenceUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.AssetUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.NavigationSettingsUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutTypePortletConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "dto.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry",
	service = DTOConverter.class
)
public class PageTemplateDTOConverter
	implements DTOConverter<LayoutPageTemplateEntry, PageTemplate> {

	@Override
	public String getContentType() {
		return PageTemplate.class.getSimpleName();
	}

	@Override
	public PageTemplate toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		if (layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.BASIC) {

			return _getContentPageTemplate(
				dtoConverterContext, layoutPageTemplateEntry);
		}

		return _getWidgetPageTemplate(
			dtoConverterContext, layoutPageTemplateEntry);
	}

	private PageTemplate _getContentPageTemplate(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		return new ContentPageTemplate() {
			{
				setCreator(
					() -> CreatorUtil.toCreator(
						layoutPageTemplateEntry.getUserId(),
						layoutPageTemplateEntry.getUserName()));
				setDateCreated(layoutPageTemplateEntry::getCreateDate);
				setDateModified(layoutPageTemplateEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
				setKey(layoutPageTemplateEntry::getLayoutPageTemplateEntryKey);
				setKeywords(
					() -> AssetUtil.getKeywords(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid()));
				setName(layoutPageTemplateEntry::getName);
				setPageTemplateSet(
					() -> _getPageTemplateSet(
						dtoConverterContext, layoutPageTemplateEntry));
				setPageTemplateSettings(
					() -> new ContentPageTemplateSettings() {
						{
							setType(Type.CONTENT_PAGE_TEMPLATE_SETTINGS);
						}
					});
				setTaxonomyCategoryBriefs(
					() -> AssetUtil.getTaxonomyCategoryBriefs(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid(),
						layoutPageTemplateEntry.getGroupId()));
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnailURLReference",
						fieldName ->
							ThumbnailURLReferenceUtil.
								getFileEntryThumbnailURLReference(
									layoutPageTemplateEntry.
										getPreviewFileEntryId())));
				setType(() -> Type.CONTENT_PAGE_TEMPLATE);
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	private PageTemplateSet _getPageTemplateSet(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				fetchLayoutPageTemplateCollection(
					layoutPageTemplateEntry.
						getLayoutPageTemplateCollectionId());

		if (layoutPageTemplateCollection == null) {
			return null;
		}

		return _pageTemplateSetDTOConverter.toDTO(
			dtoConverterContext, layoutPageTemplateCollection);
	}

	private PageTemplate _getWidgetPageTemplate(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());
		LayoutPrototype layoutPrototype =
			_layoutPrototypeService.getLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());

		return new WidgetPageTemplate() {
			{
				setActive(layoutPrototype::isActive);
				setCreator(
					() -> CreatorUtil.toCreator(
						layoutPageTemplateEntry.getUserId(),
						layoutPageTemplateEntry.getUserName()));
				setDateCreated(layoutPageTemplateEntry::getCreateDate);
				setDateModified(layoutPageTemplateEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setDescription_i18n(
					() -> {
						if (MapUtil.isEmpty(
								layoutPrototype.getDescriptionMap())) {

							return null;
						}

						return LocalizedMapUtil.getI18nMap(
							true, layoutPrototype.getDescriptionMap());
					});
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layout.getFriendlyURLMap()));
				setKey(layoutPageTemplateEntry::getLayoutPageTemplateEntryKey);
				setKeywords(
					() -> AssetUtil.getKeywords(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid()));
				setName(layoutPageTemplateEntry::getName);
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layoutPrototype.getNameMap()));
				setPageTemplateSet(
					() -> _getPageTemplateSet(
						dtoConverterContext, layoutPageTemplateEntry));
				setPageTemplateSettings(
					() -> _getWidgetPageTemplateSettings(layout));
				setTaxonomyCategoryBriefs(
					() -> AssetUtil.getTaxonomyCategoryBriefs(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid(),
						layoutPageTemplateEntry.getGroupId()));
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnailURLReference",
						fieldName ->
							ThumbnailURLReferenceUtil.
								getFileEntryThumbnailURLReference(
									layoutPageTemplateEntry.
										getPreviewFileEntryId())));
				setType(() -> Type.WIDGET_PAGE_TEMPLATE);
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	private WidgetPageTemplateSettings _getWidgetPageTemplateSettings(
		Layout layout) {

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		return new WidgetPageTemplateSettings() {
			{
				setLayoutTemplateId(
					() -> GetterUtil.getString(
						unicodeProperties.getProperty(
							LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID),
						PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID));
				setNavigationSettings(
					() -> NavigationSettingsUtil.toNavigationSettings(
						unicodeProperties));
				setType(Type.WIDGET_PAGE_TEMPLATE_SETTINGS);
			}
		};
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageTemplateSetDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateCollection, PageTemplateSet>
		_pageTemplateSetDTOConverter;

}