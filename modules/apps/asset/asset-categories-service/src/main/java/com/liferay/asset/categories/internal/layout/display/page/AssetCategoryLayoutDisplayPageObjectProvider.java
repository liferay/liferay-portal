/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.layout.display.page;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class AssetCategoryLayoutDisplayPageObjectProvider
	implements LayoutDisplayPageObjectProvider<AssetCategory> {

	public AssetCategoryLayoutDisplayPageObjectProvider(
		AssetCategory assetCategory,
		AssetVocabularyLocalService assetVocabularyLocalService,
		FriendlyURLEntryLocalService friendlyURLEntryLocalService,
		Portal portal) {

		_assetCategory = assetCategory;
		_assetVocabularyLocalService = assetVocabularyLocalService;
		_friendlyURLEntryLocalService = friendlyURLEntryLocalService;
		_portal = portal;
	}

	@Override
	public String getClassName() {
		return AssetCategory.class.getName();
	}

	@Override
	public long getClassNameId() {
		return _portal.getClassNameId(AssetCategory.class.getName());
	}

	@Override
	public long getClassPK() {
		return _assetCategory.getCategoryId();
	}

	@Override
	public long getClassTypeId() {
		return 0;
	}

	@Override
	public String getDescription(Locale locale) {
		return _assetCategory.getDescription(locale);
	}

	@Override
	public AssetCategory getDisplayObject() {
		return _assetCategory;
	}

	@Override
	public long getGroupId() {
		return _assetCategory.getGroupId();
	}

	@Override
	public String getKeywords(Locale locale) {
		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return _assetCategory.getTitle(locale);
	}

	@Override
	public String getURLTitle(Locale locale) {
		try {
			String urlTitle = _getUrlTitle(locale);

			if (urlTitle.length() <= Http.URL_MAXIMUM_LENGTH) {
				return urlTitle;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return String.valueOf(_assetCategory.getCategoryId());
	}

	private String _getUrlTitle(Locale locale) throws PortalException {
		List<AssetCategory> ancestorAssetCategories =
			_assetCategory.getAncestors();

		StringBundler sb = new StringBundler(
			(ancestorAssetCategories.size() * 2) + 3);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				_assetCategory.getVocabularyId());

		sb.append(HttpComponentsUtil.encodePath(assetVocabulary.getName()));

		Collections.reverse(ancestorAssetCategories);

		long classNameId = _portal.getClassNameId(AssetCategory.class);

		for (AssetCategory ancestorAssetCategory : ancestorAssetCategories) {
			sb.append(StringPool.SLASH);
			sb.append(
				_getUrlTitle(
					classNameId, ancestorAssetCategory.getCategoryId(),
					locale));
		}

		sb.append(StringPool.SLASH);
		sb.append(
			_getUrlTitle(classNameId, _assetCategory.getCategoryId(), locale));

		return sb.toString();
	}

	private String _getUrlTitle(long classNameId, long classPK, Locale locale) {
		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				classNameId, classPK);

		if (friendlyURLEntry == null) {
			return String.valueOf(classPK);
		}

		return GetterUtil.getObject(
			friendlyURLEntry.getUrlTitle(LocaleUtil.toLanguageId(locale)),
			friendlyURLEntry::getUrlTitle);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryLayoutDisplayPageObjectProvider.class);

	private final AssetCategory _assetCategory;
	private final AssetVocabularyLocalService _assetVocabularyLocalService;
	private final FriendlyURLEntryLocalService _friendlyURLEntryLocalService;
	private final Portal _portal;

}