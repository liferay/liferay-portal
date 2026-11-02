/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.impl;

import com.liferay.asset.category.property.exception.CategoryPropertyKeyException;
import com.liferay.asset.category.property.exception.CategoryPropertyValueException;
import com.liferay.asset.category.property.exception.DuplicateCategoryPropertyException;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.model.AssetCategoryPropertyTable;
import com.liferay.asset.category.property.service.base.AssetCategoryPropertyLocalServiceBaseImpl;
import com.liferay.asset.kernel.model.AssetCategoryTable;
import com.liferay.asset.util.AssetHelper;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@Component(
	property = "model.class.name=com.liferay.asset.category.property.model.AssetCategoryProperty",
	service = AopService.class
)
public class AssetCategoryPropertyLocalServiceImpl
	extends AssetCategoryPropertyLocalServiceBaseImpl {

	@Override
	public AssetCategoryProperty addCategoryProperty(
			long userId, long categoryId, String key, String value)
		throws PortalException {

		_validate(key, value);

		if (_hasCategoryProperty(categoryId, key)) {
			throw new DuplicateCategoryPropertyException(
				"A category property already exists with the key " + key);
		}

		User user = _userLocalService.getUser(userId);

		long categoryPropertyId = counterLocalService.increment();

		AssetCategoryProperty categoryProperty =
			assetCategoryPropertyPersistence.create(categoryPropertyId);

		categoryProperty.setCompanyId(user.getCompanyId());
		categoryProperty.setUserId(user.getUserId());
		categoryProperty.setUserName(user.getFullName());
		categoryProperty.setCategoryId(categoryId);
		categoryProperty.setKey(key);
		categoryProperty.setValue(value);

		return assetCategoryPropertyPersistence.update(categoryProperty);
	}

	@Override
	public void deleteCategoryProperties(long entryId) {
		List<AssetCategoryProperty> assetCategoryProperties =
			assetCategoryPropertyPersistence.findByCategoryId(entryId);

		for (AssetCategoryProperty assetCategoryProperty :
				assetCategoryProperties) {

			deleteCategoryProperty(assetCategoryProperty);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public void deleteCategoryProperty(AssetCategoryProperty categoryProperty) {
		assetCategoryPropertyPersistence.remove(categoryProperty);
	}

	@Override
	public void deleteCategoryProperty(long categoryPropertyId)
		throws PortalException {

		AssetCategoryProperty categoryProperty =
			assetCategoryPropertyPersistence.findByPrimaryKey(
				categoryPropertyId);

		deleteCategoryProperty(categoryProperty);
	}

	@Override
	public AssetCategoryProperty fetchCategoryProperty(
		long categoryId, String key) {

		return assetCategoryPropertyPersistence.fetchByCA_K(categoryId, key);
	}

	@Override
	public List<AssetCategoryProperty> getCategoryProperties(long entryId) {
		return assetCategoryPropertyPersistence.findByCategoryId(entryId);
	}

	@Override
	public List<AssetCategoryProperty> getCategoryPropertyValues(
		long groupId, String key) {

		return assetCategoryPropertyPersistence.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				AssetCategoryPropertyTable.INSTANCE
			).from(
				AssetCategoryPropertyTable.INSTANCE
			).innerJoinON(
				AssetCategoryTable.INSTANCE,
				AssetCategoryTable.INSTANCE.categoryId.eq(
					AssetCategoryPropertyTable.INSTANCE.categoryId)
			).where(
				AssetCategoryTable.INSTANCE.groupId.eq(
					groupId
				).and(
					AssetCategoryPropertyTable.INSTANCE.key.eq(key)
				)
			).orderBy(
				AssetCategoryPropertyTable.INSTANCE.value.ascending()
			));
	}

	@Override
	public AssetCategoryProperty updateCategoryProperty(
			long userId, long categoryPropertyId, String key, String value)
		throws PortalException {

		AssetCategoryProperty categoryProperty =
			assetCategoryPropertyPersistence.findByPrimaryKey(
				categoryPropertyId);

		String categoryPropertyKey = categoryProperty.getKey();

		if (!categoryPropertyKey.equals(key) &&
			_hasCategoryProperty(categoryProperty.getCategoryId(), key)) {

			throw new DuplicateCategoryPropertyException(
				"A category property already exists with the key " + key);
		}

		_validate(key, value);

		if (userId != 0) {
			User user = _userLocalService.getUser(userId);

			categoryProperty.setUserId(userId);
			categoryProperty.setUserName(user.getFullName());
		}

		categoryProperty.setKey(key);
		categoryProperty.setValue(value);

		return assetCategoryPropertyPersistence.update(categoryProperty);
	}

	private boolean _hasCategoryProperty(long categoryId, String key) {
		AssetCategoryProperty categoryProperty =
			assetCategoryPropertyPersistence.fetchByCA_K(categoryId, key);

		if (categoryProperty != null) {
			return true;
		}

		return false;
	}

	private void _validate(String key, String value) throws PortalException {
		if (!_assetHelper.isValidWord(key)) {
			throw new CategoryPropertyKeyException("Invalid key " + key);
		}

		int keyMaxLength = ModelHintsUtil.getMaxLength(
			AssetCategoryProperty.class.getName(), "key");

		if (key.length() > keyMaxLength) {
			throw new CategoryPropertyKeyException(
				"Maximum length of key exceeded");
		}

		if (Validator.isBlank(value)) {
			throw new CategoryPropertyValueException(
				"Property value cannot be an empty string");
		}

		int valueMaxLength = ModelHintsUtil.getMaxLength(
			AssetCategoryProperty.class.getName(), "value");

		if (value.length() > valueMaxLength) {
			throw new CategoryPropertyValueException(
				"Maximum length of value exceeded");
		}
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private UserLocalService _userLocalService;

}