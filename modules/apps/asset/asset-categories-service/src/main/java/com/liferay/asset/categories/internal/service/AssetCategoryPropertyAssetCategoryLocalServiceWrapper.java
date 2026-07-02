/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.service;

import com.liferay.asset.categories.configuration.AssetCategoriesCompanyConfiguration;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.kernel.exception.AssetCategoryLimitException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceWrapper;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ServiceWrapper.class)
public class AssetCategoryPropertyAssetCategoryLocalServiceWrapper
	extends AssetCategoryLocalServiceWrapper {

	@Override
	public AssetCategory addCategory(
			String externalReferenceCode, long userId, long groupId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			boolean system, String[] categoryProperties,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		AssetCategoriesCompanyConfiguration
			assetCategoriesCompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					AssetCategoriesCompanyConfiguration.class,
					user.getCompanyId());

		int vocabularyCategoriesCount = super.getVocabularyCategoriesCount(
			vocabularyId);

		if (vocabularyCategoriesCount >=
				assetCategoriesCompanyConfiguration.
					maximumNumberOfCategoriesPerVocabulary()) {

			throw new AssetCategoryLimitException(
				"Unable to exceed maximum number of allowed asset categories " +
					"for asset vocabulary " + vocabularyId);
		}

		AssetCategory assetCategory = super.addCategory(
			externalReferenceCode, userId, groupId, parentCategoryId, titleMap,
			descriptionMap, vocabularyId, system, categoryProperties,
			serviceContext);

		if (categoryProperties == null) {
			return assetCategory;
		}

		// Properties

		for (String categoryProperty : categoryProperties) {
			String[] categoryPropertyArray = StringUtil.split(
				categoryProperty,
				AssetCategoryConstants.PROPERTY_KEY_VALUE_SEPARATOR);

			if (categoryPropertyArray.length <= 1) {
				categoryPropertyArray = StringUtil.split(
					categoryProperty, CharPool.COLON);
			}

			String key = StringPool.BLANK;
			String value = StringPool.BLANK;

			if (categoryPropertyArray.length > 1) {
				key = GetterUtil.getString(categoryPropertyArray[0]);
				value = GetterUtil.getString(categoryPropertyArray[1]);
			}

			if (Validator.isNotNull(key)) {
				_assetCategoryPropertyLocalService.addCategoryProperty(
					userId, assetCategory.getCategoryId(), key, value);
			}
		}

		return assetCategory;
	}

	@Override
	public AssetCategory deleteCategory(
			AssetCategory category, boolean skipRebuildTree)
		throws PortalException {

		_assetCategoryPropertyLocalService.deleteCategoryProperties(
			category.getCategoryId());

		return super.deleteCategory(category, skipRebuildTree);
	}

	@Override
	public AssetCategory mergeCategories(long fromCategoryId, long toCategoryId)
		throws PortalException {

		List<AssetCategoryProperty> assetCategoryProperties =
			_assetCategoryPropertyLocalService.getCategoryProperties(
				fromCategoryId);

		for (AssetCategoryProperty fromCategoryProperty :
				assetCategoryProperties) {

			AssetCategoryProperty toCategoryProperty =
				_assetCategoryPropertyLocalService.fetchCategoryProperty(
					toCategoryId, fromCategoryProperty.getKey());

			if (toCategoryProperty == null) {
				fromCategoryProperty.setCategoryId(toCategoryId);

				_assetCategoryPropertyLocalService.updateAssetCategoryProperty(
					fromCategoryProperty);
			}
		}

		return super.mergeCategories(fromCategoryId, toCategoryId);
	}

	@Override
	public AssetCategory updateCategory(
			String externalReferenceCode, long userId, long categoryId,
			long parentCategoryId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, long vocabularyId,
			String[] categoryProperties, ServiceContext serviceContext)
		throws PortalException {

		List<AssetCategoryProperty> assetCategoryProperties =
			_assetCategoryPropertyLocalService.getCategoryProperties(
				categoryId);

		assetCategoryProperties = ListUtil.copy(assetCategoryProperties);

		if (categoryProperties != null) {
			for (String categoryProperty : categoryProperties) {
				String[] categoryPropertyArray = StringUtil.split(
					categoryProperty,
					AssetCategoryConstants.PROPERTY_KEY_VALUE_SEPARATOR);

				if (categoryPropertyArray.length <= 1) {
					categoryPropertyArray = StringUtil.split(
						categoryProperty, CharPool.COLON);
				}

				String key = StringPool.BLANK;

				if (categoryPropertyArray.length > 0) {
					key = GetterUtil.getString(categoryPropertyArray[0]);
				}

				String value = StringPool.BLANK;

				if (categoryPropertyArray.length > 1) {
					value = GetterUtil.getString(categoryPropertyArray[1]);
				}

				if (Validator.isNull(key)) {
					continue;
				}

				boolean addCategoryProperty = true;

				AssetCategoryProperty oldCategoryProperty = null;

				Iterator<AssetCategoryProperty> iterator =
					assetCategoryProperties.iterator();

				while (iterator.hasNext()) {
					oldCategoryProperty = iterator.next();

					if ((categoryId == oldCategoryProperty.getCategoryId()) &&
						key.equals(oldCategoryProperty.getKey())) {

						addCategoryProperty = false;

						if (!value.equals(oldCategoryProperty.getValue())) {
							_assetCategoryPropertyLocalService.
								updateCategoryProperty(
									userId,
									oldCategoryProperty.getCategoryPropertyId(),
									key, value);
						}

						iterator.remove();

						break;
					}
				}

				if (addCategoryProperty) {
					_assetCategoryPropertyLocalService.addCategoryProperty(
						userId, categoryId, key, value);
				}
			}
		}

		for (AssetCategoryProperty categoryProperty : assetCategoryProperties) {
			_assetCategoryPropertyLocalService.deleteAssetCategoryProperty(
				categoryProperty);
		}

		return super.updateCategory(
			externalReferenceCode, userId, categoryId, parentCategoryId,
			titleMap, descriptionMap, vocabularyId, categoryProperties,
			serviceContext);
	}

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private UserLocalService _userLocalService;

}