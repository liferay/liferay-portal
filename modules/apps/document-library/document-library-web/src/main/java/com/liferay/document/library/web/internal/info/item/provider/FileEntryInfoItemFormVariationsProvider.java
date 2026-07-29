/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.info.item.provider.DDMStructureInfoItemFormVariationsProvider;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Jorge Ferrer
 */
@Component(service = InfoItemFormVariationsProvider.class)
public class FileEntryInfoItemFormVariationsProvider
	implements DDMStructureInfoItemFormVariationsProvider<FileEntry> {

	@Override
	public long getDDMStructureId(String formVariationKey) {
		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				GetterUtil.getLong(formVariationKey, -1));

		if (dlFileEntryType == null) {
			return 0;
		}

		return dlFileEntryType.getDataDefinitionId();
	}

	@Override
	public InfoItemFormVariation getInfoItemFormVariation(
		long groupId, String formVariationKey) {

		long dlFileEntryTypeId = GetterUtil.getLong(formVariationKey, -1);

		if (dlFileEntryTypeId < 0) {
			return null;
		}

		if (dlFileEntryTypeId ==
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT) {

			return _getBasicDocumentInfoItemFormVariation();
		}

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntryTypeId);

		if (dlFileEntryType == null) {
			return null;
		}

		return new InfoItemFormVariation(
			dlFileEntryType.getFileEntryTypeKey(), groupId,
			String.valueOf(dlFileEntryType.getFileEntryTypeId()),
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(
					dlFileEntryType.getDefaultLanguageId())
			).values(
				_localization.getLocalizationMap(
					dlFileEntryType.getName(), true)
			).build());
	}

	@Override
	public InfoItemFormVariation
		getInfoItemFormVariationByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		try {
			if (Objects.equals(
					externalReferenceCode,
					StringUtil.toUpperCase(
						DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT))) {

				return _getBasicDocumentInfoItemFormVariation();
			}

			DLFileEntryType dlFileEntryType =
				_getDLFileEntryTypeByFileEntryTypeKey(
					externalReferenceCode, groupId);

			if (dlFileEntryType != null) {
				return new InfoItemFormVariation(
					dlFileEntryType.getFileEntryTypeKey(),
					dlFileEntryType.getGroupId(),
					String.valueOf(dlFileEntryType.getFileEntryTypeId()),
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							dlFileEntryType.getDefaultLanguageId())
					).values(
						_localization.getLocalizationMap(
							dlFileEntryType.getName(), true)
					).build());
			}
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				"An unexpected error occurred", portalException);
		}

		return null;
	}

	@Override
	public String getInfoItemFormVariationClassName() {
		return DLFileEntryType.class.getName();
	}

	@Override
	public Collection<InfoItemFormVariation> getInfoItemFormVariations(
		long groupId) {

		try {
			return getInfoItemFormVariations(
				_getCurrentAndAncestorSiteGroupIds(groupId));
		}
		catch (PortalException portalException) {
			throw new RuntimeException(
				"An unexpected error occurred", portalException);
		}
	}

	@Override
	public Collection<InfoItemFormVariation> getInfoItemFormVariations(
		long[] groupIds) {

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>();

		infoItemFormVariations.add(_getBasicDocumentInfoItemFormVariation());

		for (DLFileEntryType dlFileEntryType :
				_dlFileEntryTypeLocalService.getFileEntryTypes(groupIds)) {

			infoItemFormVariations.add(
				new InfoItemFormVariation(
					dlFileEntryType.getFileEntryTypeKey(),
					dlFileEntryType.getGroupId(),
					String.valueOf(dlFileEntryType.getFileEntryTypeId()),
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							dlFileEntryType.getDefaultLanguageId())
					).values(
						_localization.getLocalizationMap(
							dlFileEntryType.getName(), true)
					).build()));
		}

		return infoItemFormVariations;
	}

	@Override
	public Collection<InfoItemFormVariation>
		getInfoItemFormVariationsByCompanyId(long companyId) {

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>();

		infoItemFormVariations.add(_getBasicDocumentInfoItemFormVariation());

		for (DLFileEntryType dlFileEntryType :
				_dlFileEntryTypeLocalService.getFileEntryTypesByCompanyId(
					companyId)) {

			infoItemFormVariations.add(
				new InfoItemFormVariation(
					dlFileEntryType.getFileEntryTypeKey(),
					dlFileEntryType.getGroupId(),
					String.valueOf(dlFileEntryType.getFileEntryTypeId()),
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							dlFileEntryType.getDefaultLanguageId())
					).values(
						_localization.getLocalizationMap(
							dlFileEntryType.getName(), true)
					).build()));
		}

		return infoItemFormVariations;
	}

	private InfoItemFormVariation _getBasicDocumentInfoItemFormVariation() {
		DLFileEntryType basicDocumentDLFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

		return new InfoItemFormVariation(
			basicDocumentDLFileEntryType.getFileEntryTypeKey(),
			basicDocumentDLFileEntryType.getGroupId(),
			String.valueOf(basicDocumentDLFileEntryType.getFileEntryTypeId()),
			InfoLocalizedValue.localize(
				FileEntryInfoItemFormVariationsProvider.class,
				DLFileEntryTypeConstants.NAME_BASIC_DOCUMENT));
	}

	private long[] _getCurrentAndAncestorSiteGroupIds(long groupId)
		throws PortalException {

		return SiteConnectedGroupGroupProviderUtil.
			getCurrentAndAncestorSiteAndDepotGroupIds(groupId, false, true);
	}

	private DLFileEntryType _getDLFileEntryTypeByFileEntryTypeKey(
			String fileEntryTypeKey, long groupId)
		throws PortalException {

		for (DLFileEntryType dlFileEntryType :
				_dlFileEntryTypeLocalService.getFileEntryTypes(
					_getCurrentAndAncestorSiteGroupIds(groupId))) {

			if (Objects.equals(
					fileEntryTypeKey, dlFileEntryType.getFileEntryTypeKey())) {

				return dlFileEntryType;
			}
		}

		return null;
	}

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private Localization _localization;

}