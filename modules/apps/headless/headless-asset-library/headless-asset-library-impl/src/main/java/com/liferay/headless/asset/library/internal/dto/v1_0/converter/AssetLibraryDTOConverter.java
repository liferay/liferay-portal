/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.dto.v1_0.converter;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.dto.v1_0.MimeTypeLimit;
import com.liferay.headless.asset.library.dto.v1_0.Settings;
import com.liferay.headless.asset.library.internal.resource.v1_0.BaseAssetLibraryResourceImpl;
import com.liferay.headless.asset.library.internal.util.AssetLibraryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.sharing.constants.SharingConfigurationConstants;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;

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
			"headless-asset-library", BaseAssetLibraryResourceImpl.class,
			"getAssetLibrary", uriInfo, classPK);
	}

	@Override
	public AssetLibrary toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		return toDTO(
			dtoConverterContext,
			_depotEntryLocalService.getDepotEntry(
				(Long)dtoConverterContext.getId()));
	}

	@Override
	public AssetLibrary toDTO(
			DTOConverterContext dtoConverterContext, DepotEntry depotEntry)
		throws Exception {

		Group group = depotEntry.getGroup();

		return new AssetLibrary() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(group::getGroupKey);
				setCreatorUserId(group::getCreatorUserId);
				setDateCreated(depotEntry::getCreateDate);
				setDateModified(
					() -> GetterUtil.getObject(
						depotEntry.getModifiedDate(),
						depotEntry::getCreateDate));
				setDescription(
					() -> group.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getDescriptionMap()));
				setExternalReferenceCode(group::getExternalReferenceCode);
				setId(depotEntry::getDepotEntryId);
				setName(() -> group.getName(dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						group.getNameMap()));
				setNumberOfSites(
					() -> NestedFieldsSupplier.supply(
						"numberOfSites",
						nestedField ->
							_depotEntryGroupRelLocalService.
								getDepotEntryGroupRelsCount(depotEntry)));
				setNumberOfUserAccounts(
					() -> NestedFieldsSupplier.supply(
						"numberOfUserAccounts",
						nestedField -> _userLocalService.getGroupUsersCount(
							group.getGroupId())));
				setNumberOfUserGroups(
					() -> NestedFieldsSupplier.supply(
						"numberOfUserGroups",
						nestedField ->
							_userGroupLocalService.getGroupUserGroupsCount(
								group.getGroupId())));
				setSettings(() -> _toSettings(group));
				setSiteId(group::getGroupId);
				setType(
					() -> AssetLibraryUtil.getAssetLibraryType(
						depotEntry.getType()));
			}
		};
	}

	private MimeTypeLimit[] _getMimeTypeLimits(long groupId) {
		Map<String, Long> groupMimeTypeSizeLimit =
			_dlSizeLimitConfigurationProvider.getGroupMimeTypeSizeLimit(
				groupId);

		return TransformUtil.transformToArray(
			groupMimeTypeSizeLimit.entrySet(),
			entry -> new MimeTypeLimit() {
				{
					setMaximumSize(
						() -> GetterUtil.getInteger(entry.getValue()));
					setMimeType(entry::getKey);
				}
			},
			MimeTypeLimit.class);
	}

	private Settings _toSettings(Group group) {
		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		return new Settings() {
			{
				setAutoTaggingEnabled(
					() -> GetterUtil.getBoolean(
						unicodeProperties.get("autoTaggingEnabled")));
				setAvailableLanguageIds(
					() -> GetterUtil.getStringValues(
						StringUtil.split(unicodeProperties.get("locales"))));
				setDefaultLanguageId(
					() -> GetterUtil.getString(
						unicodeProperties.get("languageId")));
				setLogoColor(
					() -> GetterUtil.get(
						unicodeProperties.get("logoColor"), "outline-0"));
				setMimeTypeLimits(() -> _getMimeTypeLimits(group.getGroupId()));
				setSharingEnabled(
					() -> GetterUtil.getBoolean(
						unicodeProperties.get("sharingEnabled"),
						SharingConfigurationConstants.SHARING_ENABLED_DEFAULT));
				setTrashEnabled(
					() -> GetterUtil.getBoolean(
						unicodeProperties.get("trashEnabled")));
				setTrashEntriesMaxAge(
					() -> GetterUtil.getInteger(
						unicodeProperties.getProperty("trashEntriesMaxAge")));
				setUseCustomLanguages(
					() -> !GetterUtil.getBoolean(
						unicodeProperties.get("inheritLocales")));
			}
		};
	}

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLSizeLimitConfigurationProvider _dlSizeLimitConfigurationProvider;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}