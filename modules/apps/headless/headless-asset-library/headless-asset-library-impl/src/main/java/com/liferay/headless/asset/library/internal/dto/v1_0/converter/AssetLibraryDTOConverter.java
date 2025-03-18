/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.MimeTypeLimit;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.internal.resource.v1_0.BaseAssetLibraryResourceImpl;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "dto.class.name=com.liferay.depot.model.DepotEntry",
	service = DTOConverter.class
)
public class AssetLibraryDTOConverter
	implements DTOConverter<DepotEntry, AssetLibrary> {

	@Override
	public String getContentType() {
		return AssetLibrary.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-delivery", BaseAssetLibraryResourceImpl.class,
			"getAssetLibrary", uriInfo, classPK);
	}

	@Override
	public AssetLibrary toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		return toDTO(
			dtoConverterContext,
			_depotEntryLocalService.getGroupDepotEntry(
				(Long)dtoConverterContext.getId()));
	}

	@Override
	public AssetLibrary toDTO(
			DTOConverterContext dtoConverterContext, DepotEntry depotEntry)
		throws Exception {

		Group group = depotEntry.getGroup();

		return new AssetLibrary() {
			{
				setDateCreated(depotEntry::getCreateDate);
				setDateModified(depotEntry::getModifiedDate);
				setDescription(
					() -> group.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getDescriptionMap()));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setId(group::getGroupId);
				setName(() -> group.getName(dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getNameMap()));
				setNumberOfUserAccounts(
					() -> _userLocalService.getGroupUsersCount(
						group.getGroupId()));
				setNumberOfUserGroups(
					() -> _userGroupLocalService.getGroupUserGroupsCount(
						group.getGroupId()));
				setSettings(() -> _toSettings(group));
			}
		};
	}

	private Settings _toSettings(Group group) {
		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		return new Settings() {
			{
				setAutoTaggingEnabled(
					() -> {
						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.equals("autoTaggingEnabled")) {
								return true;
							}
						}

						return false;
					});
				setAvailableLanguageIds(group::getAvailableLanguageIds);
				setDefaultLanguageId(group::getDefaultLanguageId);
				setLogoColor(
					() -> {
						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.equals("logoColor")) {
								return entry.getKey();
							}
						}

						// see https://liferay.atlassian.net/browse/LPD-39975

						return "color-0";
					});
				setMimeTypeLimits(
					() -> new MimeTypeLimit[0] // TODO
				);
				setSharingEnabled(
					() -> {
						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.equals("sharingEnabled")) {
								return true;
							}
						}

						return false;
					});
				setUseCustomLanguages(
					() -> {
						for (Map.Entry<String, String> entry :
								unicodeProperties.entrySet()) {

							String key = entry.getKey();

							if (key.equals("useCustomLanguages")) {
								return true;
							}
						}

						return false;
					});
			}
		};
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}