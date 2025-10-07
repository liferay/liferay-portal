/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSEOSettings;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPageSettings;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ThumbnailUtil;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "dto.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry",
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
					() -> {
						User user = _userLocalService.fetchUser(
							layoutUtilityPageEntry.getUserId());

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
				setThumbnail(
					() ->
						ThumbnailUtil.getPortletFileEntryItemExternalReference(
							layoutUtilityPageEntry.getPreviewFileEntryId()));
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
		if (_internalToExternalValuesMap.containsKey(type)) {
			return _internalToExternalValuesMap.get(type);
		}

		throw new UnsupportedOperationException();
	}

	private static final Map<String, UtilityPage.Type>
		_internalToExternalValuesMap = HashMapBuilder.put(
			LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY,
			UtilityPage.Type.COOKIE_POLICY
		).put(
			LayoutUtilityPageEntryConstants.TYPE_CREATE_ACCOUNT,
			UtilityPage.Type.CREATE_ACCOUNT
		).put(
			LayoutUtilityPageEntryConstants.TYPE_FORGOT_PASSWORD,
			UtilityPage.Type.FORGOT_PASSWORD
		).put(
			LayoutUtilityPageEntryConstants.TYPE_LOGIN, UtilityPage.Type.LOGIN
		).put(
			LayoutUtilityPageEntryConstants.TYPE_SC_INTERNAL_SERVER_ERROR,
			UtilityPage.Type.ERROR_CODE500
		).put(
			LayoutUtilityPageEntryConstants.TYPE_SC_NOT_FOUND,
			UtilityPage.Type.ERROR_CODE404
		).put(
			LayoutUtilityPageEntryConstants.TYPE_STATUS, UtilityPage.Type.ERROR
		).put(
			LayoutUtilityPageEntryConstants.TYPE_TERMS_OF_USE,
			UtilityPage.Type.TERMS_OF_USE
		).build();

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private UserLocalService _userLocalService;

}