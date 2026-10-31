/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.impl;

import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.base.CommerceInventoryWarehouseServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommerceInventoryWarehouse"
	},
	service = AopService.class
)
public class CommerceInventoryWarehouseServiceImpl
	extends CommerceInventoryWarehouseServiceBaseImpl {

	@Override
	public CommerceInventoryWarehouse addCommerceInventoryWarehouse(
			String externalReferenceCode, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String street1,
			String street2, String street3, String city, String zip,
			String commerceRegionCode, String commerceCountryCode,
			double latitude, double longitude, ServiceContext serviceContext)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.ADD_WAREHOUSE);

		return commerceInventoryWarehouseLocalService.
			addCommerceInventoryWarehouse(
				externalReferenceCode, nameMap, descriptionMap, active, street1,
				street2, street3, city, zip, commerceRegionCode,
				commerceCountryCode, latitude, longitude, serviceContext);
	}

	@Override
	public CommerceInventoryWarehouse deleteCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.DELETE);

		return commerceInventoryWarehouseLocalService.
			deleteCommerceInventoryWarehouse(commerceInventoryWarehouseId);
	}

	@Override
	public CommerceInventoryWarehouse fetchByCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
		throws PortalException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			commerceInventoryWarehousePersistence.fetchByPrimaryKey(
				commerceInventoryWarehouseId);

		if (commerceInventoryWarehouse != null) {
			_commerceInventoryWarehouseModelResourcePermission.check(
				getPermissionChecker(), commerceInventoryWarehouse,
				ActionKeys.VIEW);
		}

		return commerceInventoryWarehouse;
	}

	@Override
	public CommerceInventoryWarehouse
			fetchCommerceInventoryWarehouseByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			commerceInventoryWarehousePersistence.fetchByERC_C(
				externalReferenceCode, companyId);

		if (commerceInventoryWarehouse != null) {
			_commerceInventoryWarehouseModelResourcePermission.check(
				getPermissionChecker(), commerceInventoryWarehouse,
				ActionKeys.VIEW);
		}

		return commerceInventoryWarehouse;
	}

	@Override
	public CommerceInventoryWarehouse geolocateCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId, double latitude,
			double longitude)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryWarehouseLocalService.
			geolocateCommerceInventoryWarehouse(
				commerceInventoryWarehouseId, latitude, longitude);
	}

	@Override
	public CommerceInventoryWarehouse getCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.VIEW);

		return commerceInventoryWarehousePersistence.findByPrimaryKey(
			commerceInventoryWarehouseId);
	}

	@Override
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
			long companyId, boolean active, int start, int end,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehousePersistence.filterFindByC_A(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
			long companyId, boolean active, String commerceCountryCode,
			int start, int end,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehousePersistence.findByC_A_C(
			companyId, active, commerceCountryCode, start, end,
			orderByComparator);
	}

	@Override
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
			long companyId, int start, int end,
			OrderByComparator<CommerceInventoryWarehouse> orderByComparator)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(
				companyId, start, end, orderByComparator);
	}

	@Override
	public List<CommerceInventoryWarehouse> getCommerceInventoryWarehouses(
			long companyId, long accountEntryId, long groupId, boolean active)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehouses(
				companyId, accountEntryId, groupId, active);
	}

	@Override
	public int getCommerceInventoryWarehousesCount(long companyId)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehousePersistence.countByCompanyId(
			companyId);
	}

	@Override
	public int getCommerceInventoryWarehousesCount(
			long companyId, boolean active, String commerceCountryCode)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehouseLocalService.
			getCommerceInventoryWarehousesCount(
				companyId, active, commerceCountryCode);
	}

	@Override
	public List<CommerceInventoryWarehouse> search(
			long companyId, Boolean active, String commerceCountryCode,
			String keywords, int start, int end, Sort sort)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehouseLocalService.search(
			companyId, active, commerceCountryCode, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceInventoryWarehousesCount(
			long companyId, Boolean active, String commerceCountryCode,
			String keywords)
		throws PortalException {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		portletResourcePermission.check(
			getPermissionChecker(), null,
			CommerceInventoryActionKeys.VIEW_INVENTORIES);

		return commerceInventoryWarehouseLocalService.
			searchCommerceInventoryWarehousesCount(
				companyId, active, commerceCountryCode, keywords);
	}

	@Override
	public CommerceInventoryWarehouse setActive(
			long commerceInventoryWarehouseId, boolean active)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryWarehouseLocalService.setActive(
			commerceInventoryWarehouseId, active);
	}

	@Override
	public CommerceInventoryWarehouse updateCommerceInventoryWarehouse(
			long commerceInventoryWarehouseId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String street1,
			String street2, String street3, String city, String zip,
			String commerceRegionCode, String commerceCountryCode,
			double latitude, double longitude, long mvccVersion,
			ServiceContext serviceContext)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryWarehouseLocalService.
			updateCommerceInventoryWarehouse(
				commerceInventoryWarehouseId, nameMap, descriptionMap, active,
				street1, street2, street3, city, zip, commerceRegionCode,
				commerceCountryCode, latitude, longitude, mvccVersion,
				serviceContext);
	}

	@Override
	public CommerceInventoryWarehouse
			updateCommerceInventoryWarehouseExternalReferenceCode(
				String externalReferenceCode, long commerceInventoryWarehouseId)
		throws PortalException {

		_commerceInventoryWarehouseModelResourcePermission.check(
			getPermissionChecker(), commerceInventoryWarehouseId,
			ActionKeys.UPDATE);

		return commerceInventoryWarehouseLocalService.
			updateCommerceInventoryWarehouseExternalReferenceCode(
				externalReferenceCode, commerceInventoryWarehouseId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

}