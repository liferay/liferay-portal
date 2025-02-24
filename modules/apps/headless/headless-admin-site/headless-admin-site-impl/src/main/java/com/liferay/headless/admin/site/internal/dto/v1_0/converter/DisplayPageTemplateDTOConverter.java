/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ClassSubtypeReference;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
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
				setDateCreated(layoutPageTemplateEntry::getCreateDate);
				setDateModified(layoutPageTemplateEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
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
							layoutPageTemplateCollection);
					});
				setThumbnail(
					() -> {
						if (layoutPageTemplateEntry.getPreviewFileEntryId() <=
								0) {

							return null;
						}

						FileEntry fileEntry =
							_portletFileRepository.getPortletFileEntry(
								layoutPageTemplateEntry.
									getPreviewFileEntryId());

						if (fileEntry == null) {
							return null;
						}

						return new ItemExternalReference() {
							{
								setClassName(() -> FileEntry.class.getName());
								setExternalReferenceCode(
									fileEntry::getExternalReferenceCode);
							}
						};
					});
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	private ItemExternalReference _getSubtypeItemExternalReference(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

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
	private PortletFileRepository _portletFileRepository;

}