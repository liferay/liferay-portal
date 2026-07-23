/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.validator;

import com.liferay.asset.categories.thread.local.AssetVocabularyThreadLocal;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.validator.AssetEntryValidator;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(property = "model.class.name=*", service = AssetEntryValidator.class)
public class CardinalityAssetEntryValidator implements AssetEntryValidator {

	@Override
	public void validate(
			long groupId, String className, long classPK, long classTypePK,
			long[] categoryIds, String[] tagNames)
		throws PortalException {

		if (!_isCategorizable(groupId, className, classPK)) {
			return;
		}

		if (className.equals(Group.class.getName())) {
			groupId = classPK;
		}

		long classNameId = _classNameLocalService.getClassNameId(className);

		List<AssetVocabulary> assetVocabularies = new ArrayList<>(
			_assetVocabularyLocalService.getGroupsVocabularies(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(groupId)));

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if ((depotEntry != null) &&
			((depotEntry.getType() == DepotConstants.TYPE_PROJECT) ||
			 (depotEntry.getType() == DepotConstants.TYPE_SPACE))) {

			List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
				new ArrayList<>(
					_assetVocabularyGroupRelLocalService.
						getAssetVocabularyGroupRelsByGroupIdAndDepotEntryType(
							groupId, depotEntry.getType()));

			assetVocabularyGroupRels.addAll(
				_assetVocabularyGroupRelLocalService.
					getAssetVocabularyGroupRelsByGroupIdAndDepotEntryType(
						GroupConstants.ANY_PARENT_GROUP_ID,
						depotEntry.getType()));

			assetVocabularies.addAll(
				TransformUtil.transform(
					assetVocabularyGroupRels,
					assetVocabularyGroupRel ->
						_assetVocabularyLocalService.getVocabulary(
							assetVocabularyGroupRel.getVocabularyId())));
		}

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			validate(
				groupId, classNameId, classTypePK, categoryIds,
				assetVocabulary);
		}
	}

	protected void validate(
			long groupId, long classNameId, long classTypePK,
			long[] categoryIds, AssetVocabulary assetVocabulary)
		throws PortalException {

		if (AssetVocabularyThreadLocal.isSkipRequiredCategoryValidation() ||
			!assetVocabulary.isAssociatedToClassNameIdAndClassTypePK(
				classNameId, classTypePK)) {

			return;
		}

		if (assetVocabulary.isMissingRequiredCategory(
				classNameId, classTypePK, categoryIds, groupId)) {

			throw new AssetCategoryException(
				assetVocabulary, AssetCategoryException.AT_LEAST_ONE_CATEGORY);
		}

		if (!assetVocabulary.isMultiValued() &&
			assetVocabulary.hasMoreThanOneCategorySelected(categoryIds)) {

			throw new AssetCategoryException(
				assetVocabulary, AssetCategoryException.TOO_MANY_CATEGORIES);
		}
	}

	private boolean _isCategorizable(
			long groupId, String className, long classPK)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if ((assetRendererFactory == null) ||
			!assetRendererFactory.isCategorizable()) {

			return false;
		}

		Group group = _groupLocalService.getGroup(groupId);

		if (Validator.isNotNull(
				_assetEntryLocalService.fetchEntry(className, classPK)) &&
			group.isDepot()) {

			return true;
		}

		if (classPK != 0L) {
			try {
				AssetRenderer<?> assetRenderer =
					assetRendererFactory.getAssetRenderer(classPK);

				if ((assetRenderer == null) ||
					!assetRenderer.isCategorizable(groupId)) {

					return false;
				}
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Asset entry with class PK ", classPK,
							" and class name ", className,
							" is not categorizable"),
						portalException);
				}

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CardinalityAssetEntryValidator.class.getName());

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}