/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.provider;

import com.liferay.depot.util.SiteConnectedGroupGroupProviderUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = InfoItemFormVariationsProvider.class)
public class JournalArticleInfoItemFormVariationsProvider
	implements InfoItemFormVariationsProvider<JournalArticle> {

	@Override
	public InfoItemFormVariation getInfoItemFormVariation(
		long groupId, String formVariationKey) {

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			groupId, _portal.getClassNameId(JournalArticle.class.getName()),
			formVariationKey);

		if (ddmStructure == null) {
			ddmStructure = _ddmStructureLocalService.fetchStructure(
				GetterUtil.getLong(formVariationKey));
		}

		if (ddmStructure == null) {
			return null;
		}

		return new InfoItemFormVariation(
			ddmStructure.getStructureKey(), groupId,
			String.valueOf(ddmStructure.getStructureId()),
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId())
			).values(
				_localization.getLocalizationMap(ddmStructure.getName(), true)
			).build());
	}

	@Override
	public InfoItemFormVariation
		getInfoItemFormVariationByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			groupId, _portal.getClassNameId(JournalArticle.class.getName()),
			externalReferenceCode, true);

		if (ddmStructure == null) {
			return null;
		}

		return new InfoItemFormVariation(
			externalReferenceCode, groupId,
			String.valueOf(ddmStructure.getStructureId()),
			InfoLocalizedValue.<String>builder(
			).defaultLocale(
				LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId())
			).values(
				_localization.getLocalizationMap(ddmStructure.getName(), true)
			).build());
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

		for (DDMStructure ddmStructure :
				_ddmStructureLocalService.getStructures(
					groupIds,
					_portal.getClassNameId(JournalArticle.class.getName()))) {

			infoItemFormVariations.add(
				new InfoItemFormVariation(
					ddmStructure.getStructureKey(), ddmStructure.getGroupId(),
					String.valueOf(ddmStructure.getStructureId()),
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							ddmStructure.getDefaultLanguageId())
					).values(
						_localization.getLocalizationMap(
							ddmStructure.getName(), true)
					).build()));
		}

		return infoItemFormVariations;
	}

	@Override
	public Collection<InfoItemFormVariation>
		getInfoItemFormVariationsByCompanyId(long companyId) {

		List<InfoItemFormVariation> infoItemFormVariations = new ArrayList<>();

		for (DDMStructure ddmStructure :
				_ddmStructureLocalService.getClassStructures(
					companyId,
					_portal.getClassNameId(JournalArticle.class.getName()))) {

			infoItemFormVariations.add(
				new InfoItemFormVariation(
					ddmStructure.getStructureKey(), ddmStructure.getGroupId(),
					String.valueOf(ddmStructure.getStructureId()),
					InfoLocalizedValue.<String>builder(
					).defaultLocale(
						LocaleUtil.fromLanguageId(
							ddmStructure.getDefaultLanguageId())
					).values(
						_localization.getLocalizationMap(
							ddmStructure.getName(), true)
					).build()));
		}

		return infoItemFormVariations;
	}

	private long[] _getCurrentAndAncestorSiteGroupIds(long groupId)
		throws PortalException {

		return SiteConnectedGroupGroupProviderUtil.
			getCurrentAndAncestorSiteAndDepotGroupIds(groupId, false, true);
	}

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}