/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "dto.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry",
	service = DTOConverter.class
)
public class MasterPageDTOConverter
	implements DTOConverter<LayoutPageTemplateEntry, MasterPage> {

	@Override
	public String getContentType() {
		return MasterPage.class.getSimpleName();
	}

	@Override
	public MasterPage toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		return new MasterPage() {
			{
				setDateCreated(layoutPageTemplateEntry::getCreateDate);
				setDateModified(layoutPageTemplateEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
				setKey(layoutPageTemplateEntry::getLayoutPageTemplateEntryKey);
				setMarkedAsDefault(layoutPageTemplateEntry::isDefaultTemplate);
				setName(layoutPageTemplateEntry::getName);
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

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}