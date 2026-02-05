/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ClassSubtypeReference;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateOpenGraphSettings;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateSEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateSettings;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.SitemapSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ThumbnailUtil;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "dto.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry",
	service = DTOConverter.class
)
public class DisplayPageTemplateDTOConverter
	implements DTOConverter<LayoutPageTemplateEntry, DisplayPageTemplate> {

	@Override
	public String getContentType() {
		return DisplayPageTemplate.class.getSimpleName();
	}

	@Override
	public DisplayPageTemplate toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		return new DisplayPageTemplate() {
			{
				setContentTypeReference(
					() -> new ClassSubtypeReference() {
						{
							setClassName(layoutPageTemplateEntry::getClassName);
							setSubTypeExternalReference(
								() -> _getSubtypeItemExternalReference(
									layoutPageTemplateEntry));
						}
					});
				setCreator(
					() -> {
						User user = _userLocalService.fetchUser(
							layoutPageTemplateEntry.getUserId());

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
				setDateCreated(layoutPageTemplateEntry::getCreateDate);
				setDateModified(layoutPageTemplateEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setDisplayPageTemplateSettings(
					() -> new DisplayPageTemplateSettings() {
						{
							setOpenGraphSettings(
								() -> _getDisplayPageTemplateOpenGraphSettings(
									layout));
							setSeoSettings(
								() -> _getDisplayPageTemplateSEOSettings(
									layout));
						}
					});
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layout.getFriendlyURLMap()));
				setKey(layoutPageTemplateEntry::getLayoutPageTemplateEntryKey);
				setMarkedAsDefault(layoutPageTemplateEntry::isDefaultTemplate);
				setName(layoutPageTemplateEntry::getName);
				setParentFolder(
					() -> {
						LayoutPageTemplateCollection
							layoutPageTemplateCollection =
								_layoutPageTemplateCollectionLocalService.
									fetchLayoutPageTemplateCollection(
										layoutPageTemplateEntry.
											getLayoutPageTemplateCollectionId());

						if (layoutPageTemplateCollection == null) {
							return null;
						}

						return _displayPageTemplateFolderDTOConverter.toDTO(
							dtoConverterContext, layoutPageTemplateCollection);
					});
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnail",
						fieldName ->
							ThumbnailUtil.
								getPortletFileEntryThumbnailURLReference(
									layoutPageTemplateEntry.
										getPreviewFileEntryId())));
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	private DisplayPageTemplateOpenGraphSettings
		_getDisplayPageTemplateOpenGraphSettings(Layout layout) {

		return new DisplayPageTemplateOpenGraphSettings() {
			{
				setDescriptionTemplate(
					() -> layout.getTypeSettingsProperty(
						"mapped-openGraphDescription"));
				setImageAltTemplate(
					() -> layout.getTypeSettingsProperty(
						"mapped-openGraphImageAlt"));
				setImageTemplate(
					() -> layout.getTypeSettingsProperty(
						"mapped-openGraphImage"));
				setTitleTemplate(
					() -> layout.getTypeSettingsProperty(
						"mapped-openGraphTitle"));
			}
		};
	}

	private DisplayPageTemplateSEOSettings _getDisplayPageTemplateSEOSettings(
		Layout layout) {

		return new DisplayPageTemplateSEOSettings() {
			{
				setDescriptionTemplate(
					() -> layout.getTypeSettingsProperty("mapped-description"));
				setHtmlTitleTemplate(
					() -> layout.getTypeSettingsProperty("mapped-title"));
				setRobots_i18n(
					() -> {
						Map<Locale, String> robotsMap = layout.getRobotsMap();

						if (robotsMap.isEmpty()) {
							return null;
						}

						return LocalizedMapUtil.getI18nMap(true, robotsMap);
					});
				setSitemapSettings(() -> _getSitemapSettings(layout));
			}
		};
	}

	private SitemapSettings _getSitemapSettings(Layout layout) {
		return new SitemapSettings() {
			{
				setChangeFrequency(
					() -> ChangeFrequency.create(
						StringUtil.upperCaseFirstLetter(
							layout.getTypeSettingsProperty(
								LayoutTypePortletConstants.
									SITEMAP_CHANGEFREQ))));
				setInclude(
					() -> {
						String include = GetterUtil.getString(
							layout.getTypeSettingsProperty(
								LayoutTypePortletConstants.SITEMAP_INCLUDE));

						if (Validator.isNull(include)) {
							return null;
						}

						return Objects.equals(include, "1");
					});
				setPagePriority(
					() -> {
						double pagePriority = GetterUtil.getDouble(
							layout.getTypeSettingsProperty(
								LayoutTypePortletConstants.SITEMAP_PRIORITY),
							-1L);

						if (pagePriority == -1L) {
							return null;
						}

						return pagePriority;
					});
			}
		};
	}

	private ItemExternalReference _getSubtypeItemExternalReference(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		if (Validator.isNotNull(layoutPageTemplateEntry.getClassTypeKey())) {
			return new ItemExternalReference() {
				{
					setExternalReferenceCode(
						layoutPageTemplateEntry::getClassTypeKey);
				}
			};
		}

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class,
				layoutPageTemplateEntry.getClassName());

		if (infoItemFormVariationsProvider == null) {
			return null;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				layoutPageTemplateEntry.getGroupId(),
				String.valueOf(layoutPageTemplateEntry.getClassTypeId()));

		if (infoItemFormVariation == null) {
			return null;
		}

		return new ItemExternalReference() {
			{
				setExternalReferenceCode(
					infoItemFormVariation::getExternalReferenceCode);
			}
		};
	}

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.DisplayPageTemplateFolderDTOConverter)"
	)
	private DTOConverter
		<LayoutPageTemplateCollection, DisplayPageTemplateFolder>
			_displayPageTemplateFolderDTOConverter;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}