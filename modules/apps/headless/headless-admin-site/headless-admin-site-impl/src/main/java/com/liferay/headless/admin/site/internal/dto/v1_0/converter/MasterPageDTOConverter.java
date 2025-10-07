/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.AssetUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ThumbnailUtil;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
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
				setExternalReferenceCode(
					layoutPageTemplateEntry::getExternalReferenceCode);
				setKey(layoutPageTemplateEntry::getLayoutPageTemplateEntryKey);
				setKeywords(
					() -> AssetUtil.getKeywords(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid()));
				setMarkedAsDefault(layoutPageTemplateEntry::isDefaultTemplate);
				setName(layoutPageTemplateEntry::getName);
				setTaxonomyCategoryItemExternalReferences(
					() -> AssetUtil.getTaxonomyCategoryItemExternalReferences(
						Layout.class.getName(),
						layoutPageTemplateEntry.getPlid(),
						layoutPageTemplateEntry.getGroupId()));
				setThumbnail(
					() ->
						ThumbnailUtil.getPortletFileEntryItemExternalReference(
							layoutPageTemplateEntry.getPreviewFileEntryId()));
				setUuid(layoutPageTemplateEntry::getUuid);
			}
		};
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}