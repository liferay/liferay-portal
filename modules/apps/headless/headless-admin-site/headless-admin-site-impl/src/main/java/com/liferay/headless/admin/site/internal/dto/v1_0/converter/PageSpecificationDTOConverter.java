/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.exportimport.attachment.ExportImportAttachmentManager;
import com.liferay.headless.admin.site.dto.v1_0.ClientExtension;
import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.EmbeddedPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.FavIconClientExtension;
import com.liferay.headless.admin.site.dto.v1_0.FavIconItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.IconImageURL;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.LinkToPagePageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.LinkToURLPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageSetPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.Settings;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSection;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.segments.service.SegmentsExperienceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DTOConverter.class)
public class PageSpecificationDTOConverter
	implements DTOConverter<Layout, PageSpecification> {

	@Override
	public String getContentType() {
		return PageSpecification.class.getSimpleName();
	}

	@Override
	public PageSpecification toDTO(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws Exception {

		if (layout.isTypeAssetDisplay() || layout.isTypeContent()) {
			return _toContentPageSpecification(dtoConverterContext, layout);
		}

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_EMBEDDED)) {
			return _toPageSpecification(
				layout, EmbeddedPageSpecification::new,
				PageSpecification.Type.EMBEDDED_PAGE_SPECIFICATION);
		}

		if (Objects.equals(
				layout.getType(), LayoutConstants.TYPE_LINK_TO_LAYOUT)) {

			return _toPageSpecification(
				layout, LinkToPagePageSpecification::new,
				PageSpecification.Type.LINK_TO_PAGE_PAGE_SPECIFICATION);
		}

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_NODE)) {
			return _toPageSpecification(
				layout, PageSetPageSpecification::new,
				PageSpecification.Type.PAGE_SET_PAGE_SPECIFICATION);
		}

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_URL)) {
			return _toPageSpecification(
				layout, LinkToURLPageSpecification::new,
				PageSpecification.Type.LINK_TO_URL_PAGE_SPECIFICATION);
		}

		if (dtoConverterContext == null) {
			dtoConverterContext = new DefaultDTOConverterContext(
				null, null, null, null, null);
		}

		return _toWidgetPageSpecification(dtoConverterContext, layout);
	}

	private ClientExtension _getClientExtension(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		if (clientExtensionEntryRel == null) {
			return null;
		}

		return new ClientExtension() {
			{
				setClientExtensionConfig(
					() -> _getClientExtensionConfig(clientExtensionEntryRel));
				setExternalReferenceCode(
					clientExtensionEntryRel::getCETExternalReferenceCode);
			}
		};
	}

	private ClientExtension _getClientExtension(
		long classNameId, long classPK, String type) {

		return _getClientExtension(
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				classNameId, classPK, type));
	}

	private Map<String, String> _getClientExtensionConfig(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		if (clientExtensionEntryRel == null) {
			return null;
		}

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.fastLoad(
			clientExtensionEntryRel.getTypeSettings()
		).build();

		if (unicodeProperties.isEmpty()) {
			return null;
		}

		Map<String, String> clientExtensionConfig = new HashMap<>();

		for (Map.Entry<String, String> entry : unicodeProperties.entrySet()) {
			clientExtensionConfig.put(entry.getKey(), entry.getValue());
		}

		return clientExtensionConfig;
	}

	private ClientExtension[] _getClientExtensions(
		long classNameId, long classPK, String type) {

		ClientExtension[] clientExtensions = TransformUtil.transformToArray(
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				classNameId, classPK, type),
			clientExtensionEntryRel -> _getClientExtension(
				clientExtensionEntryRel),
			ClientExtension.class);

		if (ArrayUtil.isEmpty(clientExtensions)) {
			return null;
		}

		return clientExtensions;
	}

	private PageExperience[] _getPageExperiences(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws Exception {

		return TransformUtil.transformToArray(
			_segmentsExperienceService.getSegmentsExperiences(
				layout.getGroupId(), layout.getPlid(), true),
			segmentsExperience -> {
				LayoutPageTemplateStructure layoutPageTemplateStructure =
					_layoutPageTemplateStructureLocalService.
						fetchLayoutPageTemplateStructure(
							segmentsExperience.getGroupId(), layout.getPlid());

				LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
					_layoutPageTemplateStructureRelLocalService.
						fetchLayoutPageTemplateStructureRel(
							layoutPageTemplateStructure.
								getLayoutPageTemplateStructureId(),
							segmentsExperience.getSegmentsExperienceId());

				if (layoutPageTemplateStructureRel == null) {
					throw new UnsupportedOperationException();
				}

				return _pageExperienceDTOConverter.toDTO(
					dtoConverterContext, layoutPageTemplateStructureRel);
			},
			PageExperience.class);
	}

	private Settings _getSettings(Layout layout) throws Exception {
		long classNameId = _portal.getClassNameId(Layout.class.getName());
		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		return new Settings() {
			{
				setColorSchemeName(
					() -> {
						if (Validator.isNull(layout.getColorSchemeId())) {
							return null;
						}

						return layout.getColorSchemeId();
					});
				setCss(
					() -> {
						if (Validator.isNull(layout.getCss())) {
							return null;
						}

						return layout.getCss();
					});
				setFavIcon(
					() -> {
						ClientExtension clientExtension = _getClientExtension(
							classNameId, layout.getPlid(),
							ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

						if (clientExtension != null) {
							return new FavIconClientExtension() {
								{
									setClientExtensionConfig(
										clientExtension::
											getClientExtensionConfig);
									setExternalReferenceCode(
										clientExtension::
											getExternalReferenceCode);
									setFavIconType(
										() -> FavIconType.CLIENT_EXTENSION);
								}
							};
						}

						if (Validator.isNull(layout.getFaviconFileEntryERC())) {
							return null;
						}

						return new FavIconItemExternalReference() {
							{
								setClassName(FileEntry.class::getName);
								setExternalReferenceCode(
									layout::getFaviconFileEntryERC);
								setFavIconType(
									() -> FavIconType.ITEM_EXTERNAL_REFERENCE);
								setScope(
									() -> ItemScopeUtil.getItemScope(
										layout.getCompanyId(),
										layout.getFaviconFileEntryScopeERC(),
										layout.getGroupId()));
							}
						};
					});
				setGlobalCSSClientExtensions(
					() -> _getClientExtensions(
						classNameId, layout.getPlid(),
						ClientExtensionEntryConstants.TYPE_GLOBAL_CSS));
				setGlobalJSClientExtensions(
					() -> _getClientExtensions(
						classNameId, layout.getPlid(),
						ClientExtensionEntryConstants.TYPE_GLOBAL_JS));
				setIconImageURL(() -> _getIconImageURL(layout));
				setJavascript(
					() -> unicodeProperties.getProperty("javascript", null));
				setMasterPageItemExternalReference(
					() -> {
						if (Validator.isNull(
								layout.getMasterLayoutPageTemplateEntryERC())) {

							return null;
						}

						return new ItemExternalReference() {
							{
								setExternalReferenceCode(
									layout::
										getMasterLayoutPageTemplateEntryERC);
							}
						};
					});
				setStyleBookItemExternalReference(
					() -> {
						if (Validator.isNull(layout.getStyleBookEntryERC())) {
							return null;
						}

						return new ItemExternalReference() {
							{
								setExternalReferenceCode(
									layout::getStyleBookEntryERC);
							}
						};
					});
				setThemeCSSClientExtension(
					() -> _getClientExtension(
						classNameId, layout.getPlid(),
						ClientExtensionEntryConstants.TYPE_THEME_CSS));
				setThemeName(
					() -> {
						if (Validator.isNull(layout.getThemeId())) {
							return null;
						}

						return layout.getThemeId();
					});
				setThemeSettings(
					() -> {
						Map<String, String> themeSettings = new HashMap<>();

						for (String key : unicodeProperties.keySet()) {
							if (!key.startsWith("lfr-theme:")) {
								continue;
							}

							themeSettings.put(
								key, unicodeProperties.getProperty(key, null));
						}

						if (MapUtil.isEmpty(themeSettings)) {
							return null;
						}

						return themeSettings;
					});
				setThemeSpritemapClientExtension(
					() -> _getClientExtension(
						classNameId, layout.getPlid(),
						ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP));
			}

			private IconImageURL _getIconImageURL(Layout layout) {
				if (layout.getIconImageId() <= 0) {
					return null;
				}

				Image image = _imageLocalService.fetchImage(
					layout.getIconImageId());

				if (image == null) {
					return null;
				}

				return new IconImageURL() {
					{
						setUrl(
							() -> _exportImportAttachmentManager.getImageURL(
								image));
					}
				};
			}

		};
	}

	private String _getSiteTemplatePageSpecificationERC(Layout layout) {
		Layout layoutSetPrototypeLayout = layout.getLayoutSetPrototypeLayout();

		if (layoutSetPrototypeLayout == null) {
			return null;
		}

		return layoutSetPrototypeLayout.getExternalReferenceCode();
	}

	private WidgetPageSection[] _getWidgetPageSections(
		DTOConverterContext dtoConverterContext, Layout layout) {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();
		List<String> nestedColumnIds = StringUtil.split(
			layout.getTypeSettingsProperty(
				LayoutTypePortletConstants.NESTED_COLUMN_IDS));

		return TransformUtil.transformToArray(
			layoutTypePortlet.getColumns(),
			column -> {
				if (nestedColumnIds.contains(column)) {
					return null;
				}

				return new WidgetPageSection() {
					{
						setCustomizable(
							() -> layoutTypePortlet.isColumnCustomizable(
								column));
						setId(() -> column);
						setWidgetPageWidgetInstances(
							() -> _getWidgetPageWidgetInstances(
								column, dtoConverterContext, layout));
					}
				};
			},
			WidgetPageSection.class);
	}

	private WidgetPageWidgetInstance[] _getWidgetPageWidgetInstances(
		String column, DTOConverterContext dtoConverterContext, Layout layout) {

		return TransformUtil.transformToArray(
			StringUtil.split(layout.getTypeSettingsProperty(column)),
			portletId -> {
				dtoConverterContext.setAttribute("portletId", portletId);

				return _widgetPageWidgetInstanceDTOConverter.toDTO(
					dtoConverterContext, layout);
			},
			WidgetPageWidgetInstance.class);
	}

	private PageSpecification _toContentPageSpecification(
		DTOConverterContext dtoConverterContext, Layout layout) {

		return new ContentPageSpecification() {
			{
				setCustomFields(
					() -> {
						if (layout.isTypeUtility()) {
							return null;
						}

						return CustomFieldsUtil.toCustomFields(
							true, Layout.class.getName(), layout.getPlid(),
							layout.getCompanyId(), null);
					});
				setDraftContentPageSpecificationExternalReferenceCode(
					() -> {
						Layout draftLayout = layout.fetchDraftLayout();

						if (draftLayout == null) {
							return null;
						}

						return draftLayout.getExternalReferenceCode();
					});
				setExternalReferenceCode(layout::getExternalReferenceCode);
				setPageExperiences(
					() -> _getPageExperiences(dtoConverterContext, layout));
				setSettings(() -> _getSettings(layout));
				setSiteTemplatePageSpecificationExternalReferenceCode(
					() -> _getSiteTemplatePageSpecificationERC(layout));
				setStatus(
					() -> {
						if (layout.isDraftLayout()) {
							if (layout.isApproved()) {
								return Status.APPROVED;
							}

							return Status.DRAFT;
						}

						if (LayoutUtil.isPublished(layout)) {
							return Status.APPROVED;
						}

						return Status.DRAFT;
					});
				setType(() -> Type.CONTENT_PAGE_SPECIFICATION);
			}
		};
	}

	private PageSpecification _toPageSpecification(
			Layout layout,
			UnsafeSupplier<PageSpecification, Exception>
				pageSpecificationUnsafeSupplier,
			PageSpecification.Type type)
		throws Exception {

		PageSpecification pageSpecification =
			pageSpecificationUnsafeSupplier.get();

		pageSpecification.setCustomFields(
			() -> CustomFieldsUtil.toCustomFields(
				true, Layout.class.getName(), layout.getPlid(),
				layout.getCompanyId(), null));
		pageSpecification.setExternalReferenceCode(
			layout::getExternalReferenceCode);
		pageSpecification.setSiteTemplatePageSpecificationExternalReferenceCode(
			() -> _getSiteTemplatePageSpecificationERC(layout));
		pageSpecification.setStatus(() -> PageSpecification.Status.APPROVED);
		pageSpecification.setType(() -> type);

		return pageSpecification;
	}

	private PageSpecification _toWidgetPageSpecification(
		DTOConverterContext dtoConverterContext, Layout layout) {

		return new WidgetPageSpecification() {
			{
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						true, Layout.class.getName(), layout.getPlid(),
						layout.getCompanyId(), null));
				setExternalReferenceCode(
					() -> {
						LayoutPageTemplateEntry layoutPageTemplateEntry =
							_layoutPageTemplateEntryLocalService.
								fetchLayoutPageTemplateEntryByPlid(
									layout.getPlid());

						if ((layoutPageTemplateEntry == null) ||
							(layoutPageTemplateEntry.getType() !=
								LayoutPageTemplateEntryTypeConstants.
									WIDGET_PAGE)) {

							return layout.getExternalReferenceCode();
						}

						return layoutPageTemplateEntry.
							getExternalReferenceCode();
					});
				setSettings(() -> _getSettings(layout));
				setSiteTemplatePageSpecificationExternalReferenceCode(
					() -> _getSiteTemplatePageSpecificationERC(layout));
				setStatus(() -> Status.APPROVED);
				setType(() -> Type.WIDGET_PAGE_SPECIFICATION);
				setWidgetPageSections(
					() -> _getWidgetPageSections(dtoConverterContext, layout));
			}
		};
	}

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private ExportImportAttachmentManager _exportImportAttachmentManager;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.PageExperienceDTOConverter)"
	)
	private DTOConverter<LayoutPageTemplateStructureRel, PageExperience>
		_pageExperienceDTOConverter;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.WidgetPageWidgetInstanceDTOConverter)"
	)
	private DTOConverter<Layout, WidgetPageWidgetInstance>
		_widgetPageWidgetInstanceDTOConverter;

}