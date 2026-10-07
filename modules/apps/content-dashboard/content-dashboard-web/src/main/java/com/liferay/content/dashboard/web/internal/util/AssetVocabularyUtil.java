/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.util;

import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Cristina González
 */
public class AssetVocabularyUtil {

	public static void addReservedAssetVocabulary(
			AssetVocabularyLocalService assetVocabularyLocalService,
			Collection<Long> classNameIds, Company company,
			String assetVocabularyName, int visibilityType)
		throws PortalException {

		if (assetVocabularyName == null) {
			throw new IllegalArgumentException(
				"assetVocabularyName should not be null");
		}

		AssetVocabulary assetVocabulary =
			assetVocabularyLocalService.fetchGroupVocabulary(
				company.getGroupId(),
				StringUtil.toLowerCase(assetVocabularyName));

		if (assetVocabulary == null) {
			_addAssetVocabulary(
				assetVocabularyLocalService, assetVocabularyName, classNameIds,
				company, visibilityType);

			return;
		}

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			_getAssetVocabularySettingsHelper(assetVocabulary, classNameIds);

		String settings = assetVocabularySettingsHelper.toString();

		if (Objects.equals(settings, assetVocabulary.getSettings()) &&
			(visibilityType == assetVocabulary.getVisibilityType())) {

			return;
		}

		assetVocabularyLocalService.updateVocabulary(
			assetVocabulary.getExternalReferenceCode(),
			assetVocabulary.getVocabularyId(), assetVocabulary.getTitleMap(),
			assetVocabulary.getDescriptionMap(),
			assetVocabularySettingsHelper.toString(), visibilityType);
	}

	private static void _addAssetVocabulary(
			AssetVocabularyLocalService assetVocabularyLocalService,
			String assetVocabularyName, Collection<Long> classNameIds,
			Company company, int visibilityType)
		throws PortalException {

		User user = company.getGuestUser();

		Map<Locale, String> titleMap = new HashMap<>();

		for (Locale locale :
				LanguageUtil.getCompanyAvailableLocales(
					company.getCompanyId())) {

			titleMap.put(locale, LanguageUtil.get(locale, assetVocabularyName));
		}

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setRegisteredClassNameIds(
			ArrayUtil.toLongArray(classNameIds));

		if (classNameIds != null) {
			long[] classTypePKs = new long[classNameIds.size()];
			boolean[] requireds = new boolean[classNameIds.size()];

			for (int i = 0; i < classNameIds.size(); i++) {
				classTypePKs[i] = AssetCategoryConstants.ALL_CLASS_TYPE_PK;
				requireds[i] = false;
			}

			assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
				ArrayUtil.toArray(classNameIds.toArray(new Long[0])),
				classTypePKs, requireds);
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		assetVocabularyLocalService.addVocabulary(
			_toExternalReferenceCode(assetVocabularyName), user.getUserId(),
			company.getGroupId(), assetVocabularyName, StringPool.BLANK,
			titleMap, Collections.emptyMap(),
			assetVocabularySettingsHelper.toString(), visibilityType,
			serviceContext);
	}

	private static AssetVocabularySettingsHelper
		_getAssetVocabularySettingsHelper(
			AssetVocabulary assetVocabulary, Collection<Long> classNameIds) {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper(assetVocabulary.getSettings());

		Set<Long> unregisteredClassNameIds = new LinkedHashSet<>(classNameIds);

		Set<Long> registeredClassNameIds = SetUtil.fromArray(
			assetVocabularySettingsHelper.getRegisteredClassNameIds());

		unregisteredClassNameIds.removeAll(registeredClassNameIds);

		assetVocabularySettingsHelper.setRegisteredClassNameIds(
			ArrayUtil.toLongArray(unregisteredClassNameIds));

		List<Long> newClassNameIds = new ArrayList<>();
		List<Long> newClassTypePKs = new ArrayList<>();
		List<Boolean> newDepotRequireds = new ArrayList<>();
		List<Boolean> newRequireds = new ArrayList<>();

		long[] selectedClassNameIds = assetVocabulary.getSelectedClassNameIds();
		long[] selectedClassTypePKs = assetVocabulary.getSelectedClassTypePKs();

		for (int i = 0; i < selectedClassNameIds.length; i++) {
			newClassNameIds.add(selectedClassNameIds[i]);
			newClassTypePKs.add(selectedClassTypePKs[i]);
			newDepotRequireds.add(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKDepotRequired(
						selectedClassNameIds[i], selectedClassTypePKs[i]));
			newRequireds.add(
				assetVocabularySettingsHelper.
					isClassNameIdAndClassTypePKRequired(
						selectedClassNameIds[i], selectedClassTypePKs[i]));
		}

		for (long unregisteredClassNameId : unregisteredClassNameIds) {
			if (newClassNameIds.contains(unregisteredClassNameId)) {
				continue;
			}

			newClassNameIds.add(unregisteredClassNameId);
			newClassTypePKs.add(AssetCategoryConstants.ALL_CLASS_TYPE_PK);
			newDepotRequireds.add(false);
			newRequireds.add(false);
		}

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			ArrayUtil.toArray(newClassNameIds.toArray(new Long[0])),
			ArrayUtil.toArray(newClassTypePKs.toArray(new Long[0])),
			ArrayUtil.toArray(newDepotRequireds.toArray(new Boolean[0])),
			ArrayUtil.toArray(newRequireds.toArray(new Boolean[0])));

		return assetVocabularySettingsHelper;
	}

	private static String _toExternalReferenceCode(String title) {
		return "L_" + TextFormatter.format(title, TextFormatter.A);
	}

}