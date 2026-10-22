/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSettings;
import com.liferay.headless.admin.site.dto.v1_0.util.ThumbnailURLReferenceUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.layout.utility.page.converter.LayoutUtilityPageEntryTypeConverter;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry"
	},
	service = DTOConverter.class
)
public class UtilityPageDTOConverter
	implements DTOConverter<LayoutUtilityPageEntry, UtilityPage> {

	@Override
	public String getContentType() {
		return UtilityPage.class.getSimpleName();
	}

	@Override
	public UtilityPage toDTO(
			DTOConverterContext dtoConverterContext,
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws Exception {

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		return new UtilityPage() {
			{
				setCreator(
					() -> CreatorUtil.toCreator(
						layoutUtilityPageEntry.getUserId(),
						layoutUtilityPageEntry.getUserName()));
				setDateCreated(layoutUtilityPageEntry::getCreateDate);
				setDateModified(layoutUtilityPageEntry::getModifiedDate);
				setDatePublished(layout::getPublishDate);
				setExternalReferenceCode(
					layoutUtilityPageEntry::getExternalReferenceCode);
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						true, layout.getFriendlyURLMap()));
				setMarkedAsDefault(
					layoutUtilityPageEntry::isDefaultLayoutUtilityPageEntry);
				setName(layoutUtilityPageEntry::getName);
				setThumbnailURLReference(
					() -> NestedFieldsSupplier.supply(
						"thumbnailURLReference",
						fieldName ->
							ThumbnailURLReferenceUtil.
								getFileEntryThumbnailURLReference(
									layoutUtilityPageEntry.
										getPreviewFileEntryId())));
				setType(() -> _getType(layoutUtilityPageEntry.getType()));
				setUtilityPageSettings(
					() -> new UtilityPageSettings() {
						{
							setSeoSettings(
								() -> new UtilityPageSEOSettings() {
									{
										setDescription_i18n(
											() -> LocalizedMapUtil.getI18nMap(
												true,
												layout.getDescriptionMap()));
										setHtmlTitle_i18n(
											() -> LocalizedMapUtil.getI18nMap(
												true, layout.getTitleMap()));
									}
								});
						}
					});
				setUuid(layoutUtilityPageEntry::getUuid);
			}
		};
	}

	private UtilityPage.Type _getType(String type) {
		UtilityPage.Type utilityPageType = UtilityPage.Type.create(
			LayoutUtilityPageEntryTypeConverter.convertToExternalValue(type));

		if (utilityPageType != null) {
			return utilityPageType;
		}

		throw new UnsupportedOperationException();
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

}