/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.impl;

import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.base.AssetCategoryPropertyServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"json.web.service.context.name=asset",
		"json.web.service.context.path=AssetCategoryProperty"
	},
	service = AopService.class
)
public class AssetCategoryPropertyServiceImpl
	extends AssetCategoryPropertyServiceBaseImpl {

	@Override
	public AssetCategoryProperty addCategoryProperty(
			long entryId, String key, String value)
		throws PortalException {

		AssetCategoryPermission.check(
			getPermissionChecker(), entryId, ActionKeys.UPDATE);

		return assetCategoryPropertyLocalService.addCategoryProperty(
			getUserId(), entryId, key, value);
	}

	@Override
	public void deleteCategoryProperty(long categoryPropertyId)
		throws PortalException {

		AssetCategoryProperty assetCategoryProperty =
			assetCategoryPropertyPersistence.findByPrimaryKey(
				categoryPropertyId);

		AssetCategoryPermission.check(
			getPermissionChecker(), assetCategoryProperty.getCategoryId(),
			ActionKeys.UPDATE);

		assetCategoryPropertyLocalService.deleteCategoryProperty(
			categoryPropertyId);
	}

	@Override
	public List<AssetCategoryProperty> getCategoryProperties(long entryId) {
		try {
			if (AssetCategoryPermission.contains(
					getPermissionChecker(), entryId, ActionKeys.VIEW)) {

				return assetCategoryPropertyPersistence.findByCategoryId(
					entryId);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get asset category property for asset entry " +
						entryId,
					portalException);
			}
		}

		return new ArrayList<>();
	}

	@Override
	public List<AssetCategoryProperty> getCategoryPropertyValues(
		long companyId, String key) {

		return _filterAssetCategoryProperties(
			assetCategoryPropertyLocalService.getCategoryPropertyValues(
				companyId, key));
	}

	@Override
	public AssetCategoryProperty updateCategoryProperty(
			long userId, long categoryPropertyId, String key, String value)
		throws PortalException {

		AssetCategoryProperty assetCategoryProperty =
			assetCategoryPropertyPersistence.findByPrimaryKey(
				categoryPropertyId);

		AssetCategoryPermission.check(
			getPermissionChecker(), assetCategoryProperty.getCategoryId(),
			ActionKeys.UPDATE);

		return assetCategoryPropertyLocalService.updateCategoryProperty(
			userId, categoryPropertyId, key, value);
	}

	@Override
	public AssetCategoryProperty updateCategoryProperty(
			long categoryPropertyId, String key, String value)
		throws PortalException {

		return updateCategoryProperty(0, categoryPropertyId, key, value);
	}

	private List<AssetCategoryProperty> _filterAssetCategoryProperties(
		List<AssetCategoryProperty> assetCategoryProperties) {

		return TransformUtil.transform(
			assetCategoryProperties,
			assetCategoryProperty -> {
				try {
					if (AssetCategoryPermission.contains(
							getPermissionChecker(),
							assetCategoryProperty.getCategoryId(),
							ActionKeys.VIEW)) {

						return assetCategoryProperty;
					}
				}
				catch (PortalException portalException) {

					// LPS-52675

					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				return null;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetCategoryPropertyServiceImpl.class);

}