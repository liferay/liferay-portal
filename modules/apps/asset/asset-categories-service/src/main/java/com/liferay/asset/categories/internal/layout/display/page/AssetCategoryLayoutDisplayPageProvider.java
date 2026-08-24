/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.layout.display.page;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutDisplayPageProvider.class)
public class AssetCategoryLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<AssetCategory> {

	@Override
	public String getClassName() {
		return AssetCategory.class.getName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_ASSET_CATEGORY;
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getLayoutDisplayPageObjectProvider(AssetCategory assetCategory) {

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _assetVocabularyLocalService,
			_friendlyURLEntryLocalService, _portal);
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		if (Validator.isNull(urlTitle)) {
			return null;
		}

		AssetCategory assetCategory = _fetchAssetCategory(groupId, urlTitle);

		if (assetCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _assetVocabularyLocalService,
			_friendlyURLEntryLocalService, _portal);
	}

	@Override
	public LayoutDisplayPageObjectProvider<AssetCategory>
		getParentLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				classPKInfoItemIdentifier.getClassPK());

		if (assetCategory == null) {
			return null;
		}

		AssetCategory parentCategory = assetCategory.getParentCategory();

		if (parentCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			parentCategory, _assetVocabularyLocalService,
			_friendlyURLEntryLocalService, _portal);
	}

	@Override
	public boolean inheritable() {
		return true;
	}

	@Override
	protected AssetCategoryLayoutDisplayPageObjectProvider
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return null;
		}

		AssetCategory assetCategory = null;

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			assetCategory = _assetCategoryLocalService.fetchAssetCategory(
				classPKInfoItemIdentifier.getClassPK());
		}
		else {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			assetCategory =
				_assetCategoryLocalService.
					fetchAssetCategoryByExternalReferenceCode(
						ercInfoItemIdentifier.getExternalReferenceCode(),
						groupId);
		}

		if (assetCategory == null) {
			return null;
		}

		return new AssetCategoryLayoutDisplayPageObjectProvider(
			assetCategory, _assetVocabularyLocalService,
			_friendlyURLEntryLocalService, _portal);
	}

	private AssetCategory _fetchAssetCategory(long groupId, String urlTitle) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if ((group != null) && !Validator.isNumber(urlTitle)) {
			return _fetchAssetCategoryByURLTitle(groupId, urlTitle);
		}

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(
				GetterUtil.getLong(urlTitle));

		if ((assetCategory == null) ||
			(assetCategory.getGroupId() != groupId)) {

			return null;
		}

		return assetCategory;
	}

	private AssetCategory _fetchAssetCategoryByURLTitle(
		long groupId, String urlTitle) {

		if (Validator.isNull(urlTitle)) {
			return null;
		}

		String[] parts = StringUtil.split(urlTitle, CharPool.SLASH);

		if (parts.length == 0) {
			return null;
		}

		AssetVocabulary assetVocabulary = _fetchAssetVocabulary(
			groupId, parts[0]);

		if (assetVocabulary == null) {
			return null;
		}

		long classPK = 0;
		long parentClassPK = assetVocabulary.getVocabularyId();

		for (int i = 1; i < parts.length; i++) {
			classPK = _getClassPK(groupId, parentClassPK, parts[i]);

			if (classPK == 0) {
				return null;
			}

			parentClassPK = classPK;
		}

		if (classPK == 0) {
			return null;
		}

		AssetCategory assetCategory =
			_assetCategoryLocalService.fetchAssetCategory(classPK);

		if ((assetCategory == null) ||
			(assetCategory.getGroupId() != groupId)) {

			return null;
		}

		return assetCategory;
	}

	private AssetVocabulary _fetchAssetVocabulary(long groupId, String name) {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchGroupVocabulary(groupId, name);

		if (assetVocabulary != null) {
			return assetVocabulary;
		}

		String decodedName = HttpComponentsUtil.decodePath(name);

		if (name.equals(decodedName)) {
			return null;
		}

		return _assetVocabularyLocalService.fetchGroupVocabulary(
			groupId, decodedName);
	}

	private long _getClassPK(
		long groupId, long parentClassPK, String urlTitle) {

		long classNameId = _portal.getClassNameId(AssetCategory.class);

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				groupId, classNameId, parentClassPK, urlTitle);

		if (friendlyURLEntry != null) {
			return friendlyURLEntry.getClassPK();
		}

		String decodedURLTitle = HttpComponentsUtil.decodePath(urlTitle);

		if (urlTitle.equals(decodedURLTitle)) {
			return 0;
		}

		friendlyURLEntry = _friendlyURLEntryLocalService.fetchFriendlyURLEntry(
			groupId, classNameId, parentClassPK, decodedURLTitle);

		if (friendlyURLEntry != null) {
			return friendlyURLEntry.getClassPK();
		}

		return 0;
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}