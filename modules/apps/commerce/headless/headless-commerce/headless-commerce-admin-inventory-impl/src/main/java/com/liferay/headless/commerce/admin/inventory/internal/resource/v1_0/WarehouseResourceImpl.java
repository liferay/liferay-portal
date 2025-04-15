/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0;

import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseItem;
import com.liferay.headless.commerce.admin.inventory.internal.odata.entity.v1_0.WarehouseEntityModel;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/warehouse.properties",
	scope = ServiceScope.PROTOTYPE, service = WarehouseResource.class
)
public class WarehouseResourceImpl extends BaseWarehouseResourceImpl {

	@Override
	public void deleteWarehouseByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			throw new NoSuchInventoryWarehouseException(
				"Unable to find warehouse with external reference code " +
					externalReferenceCode);
		}

		_commerceInventoryWarehouseService.deleteCommerceInventoryWarehouse(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId());
	}

	@Override
	public void deleteWarehouseId(Long id) throws Exception {
		_commerceInventoryWarehouseService.deleteCommerceInventoryWarehouse(id);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		return _entityModel;
	}

	@Override
	public Warehouse getWarehouseByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			throw new NoSuchInventoryWarehouseException(
				"Unable to find warehouse with external reference code " +
					externalReferenceCode);
		}

		return _toWarehouse(commerceInventoryWarehouse);
	}

	@Override
	public Warehouse getWarehouseId(Long id) throws Exception {
		return _toWarehouse(GetterUtil.getLong(id));
	}

	@Override
	public Page<Warehouse> getWarehousesPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommerceInventoryWarehouse.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toWarehouse(
				_commerceInventoryWarehouseService.
					getCommerceInventoryWarehouse(
						GetterUtil.getLong(
							document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public Response patchWarehouseByExternalReferenceCode(
			String externalReferenceCode, Warehouse warehouse)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			throw new NoSuchInventoryWarehouseException(
				"Unable to find warehouse with external reference code " +
					externalReferenceCode);
		}

		_updateWarehouse(commerceInventoryWarehouse, warehouse);

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	@Override
	public Response patchWarehouseId(Long id, Warehouse warehouse)
		throws Exception {

		_updateWarehouse(
			_commerceInventoryWarehouseService.getCommerceInventoryWarehouse(
				id),
			warehouse);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Warehouse postWarehouse(Warehouse warehouse) throws Exception {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					warehouse.getExternalReferenceCode(),
					contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			commerceInventoryWarehouse =
				_commerceInventoryWarehouseService.
					addCommerceInventoryWarehouse(
						warehouse.getExternalReferenceCode(),
						LanguageUtils.getLocalizedMap(warehouse.getName()),
						LanguageUtils.getLocalizedMap(
							warehouse.getDescription()),
						GetterUtil.get(warehouse.getActive(), true),
						warehouse.getStreet1(), warehouse.getStreet2(),
						warehouse.getStreet3(), warehouse.getCity(),
						warehouse.getZip(), warehouse.getRegionISOCode(),
						warehouse.getCountryISOCode(),
						GetterUtil.get(warehouse.getLatitude(), 0D),
						GetterUtil.get(warehouse.getLongitude(), 0D),
						_serviceContextHelper.getServiceContext());
		}
		else {
			commerceInventoryWarehouse = _updateWarehouse(
				commerceInventoryWarehouse, warehouse);
		}

		// Update nested resources

		_updateNestedResources(warehouse, commerceInventoryWarehouse);

		return _toWarehouse(commerceInventoryWarehouse);
	}

	@Override
	public Warehouse putWarehouseByExternalReferenceCode(
			String externalReferenceCode, Warehouse warehouse)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			commerceInventoryWarehouse =
				_commerceInventoryWarehouseService.
					addCommerceInventoryWarehouse(
						externalReferenceCode,
						LanguageUtils.getLocalizedMap(warehouse.getName()),
						LanguageUtils.getLocalizedMap(
							warehouse.getDescription()),
						GetterUtil.get(warehouse.getActive(), true),
						warehouse.getStreet1(), warehouse.getStreet2(),
						warehouse.getStreet3(), warehouse.getCity(),
						warehouse.getZip(), warehouse.getRegionISOCode(),
						warehouse.getCountryISOCode(),
						GetterUtil.get(warehouse.getLatitude(), 0D),
						GetterUtil.get(warehouse.getLongitude(), 0D),
						_serviceContextHelper.getServiceContext());
		}
		else {
			commerceInventoryWarehouse =
				_commerceInventoryWarehouseService.
					updateCommerceInventoryWarehouse(
						commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId(),
						LanguageUtils.getLocalizedMap(warehouse.getName()),
						LanguageUtils.getLocalizedMap(
							warehouse.getDescription()),
						GetterUtil.getBoolean(warehouse.getActive()),
						GetterUtil.getString(warehouse.getStreet1()),
						GetterUtil.getString(warehouse.getStreet2()),
						GetterUtil.getString(warehouse.getStreet3()),
						GetterUtil.getString(warehouse.getCity()),
						GetterUtil.getString(warehouse.getZip()),
						GetterUtil.getString(warehouse.getRegionISOCode()),
						GetterUtil.getString(warehouse.getCountryISOCode()),
						GetterUtil.getDouble(warehouse.getLatitude()),
						GetterUtil.getDouble(warehouse.getLongitude()),
						commerceInventoryWarehouse.getMvccVersion(),
						_serviceContextHelper.getServiceContext());
		}

		// Update nested resources

		_updateNestedResources(warehouse, commerceInventoryWarehouse);

		return _toWarehouse(commerceInventoryWarehouse);
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceInventoryWarehouse commerceInventoryWarehouse)
		throws Exception {

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			addAction(
				"DELETE",
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				"deleteWarehouseId",
				_commerceInventoryWarehouseModelResourcePermission)
		).put(
			"get",
			addAction(
				"VIEW",
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				"getWarehousesPage",
				_commerceInventoryWarehouseModelResourcePermission)
		).put(
			"permissions",
			addAction(
				"PERMISSIONS",
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				"patchWarehouseId",
				_commerceInventoryWarehouseModelResourcePermission)
		).put(
			"update",
			addAction(
				"UPDATE",
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				"patchWarehouseId",
				_commerceInventoryWarehouseModelResourcePermission)
		).build();
	}

	private Warehouse _toWarehouse(
			CommerceInventoryWarehouse commerceInventoryWarehouse)
		throws Exception {

		return _warehouseDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceInventoryWarehouse), _dtoConverterRegistry,
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private Warehouse _toWarehouse(long commerceInventoryWarehouseId)
		throws Exception {

		return _toWarehouse(
			_commerceInventoryWarehouseService.getCommerceInventoryWarehouse(
				commerceInventoryWarehouseId));
	}

	private void _updateNestedResources(
			Warehouse warehouse,
			CommerceInventoryWarehouse commerceInventoryWarehouse)
		throws Exception {

		WarehouseItem[] warehouseItems = warehouse.getWarehouseItems();

		if (warehouseItems != null) {
			for (WarehouseItem warehouseItem : warehouseItems) {
				_commerceInventoryWarehouseItemService.
					addOrUpdateCommerceInventoryWarehouseItem(
						warehouseItem.getExternalReferenceCode(),
						commerceInventoryWarehouse.getCompanyId(),
						commerceInventoryWarehouse.
							getCommerceInventoryWarehouseId(),
						BigDecimalUtil.get(
							warehouseItem.getQuantity(), BigDecimal.ZERO),
						warehouseItem.getSku(),
						warehouseItem.getUnitOfMeasureKey());
			}
		}
	}

	private CommerceInventoryWarehouse _updateWarehouse(
			CommerceInventoryWarehouse commerceInventoryWarehouse,
			Warehouse warehouse)
		throws Exception {

		commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.updateCommerceInventoryWarehouse(
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				LanguageUtils.getLocalizedMap(warehouse.getName()),
				LanguageUtils.getLocalizedMap(warehouse.getDescription()),
				GetterUtil.get(
					warehouse.getActive(),
					commerceInventoryWarehouse.isActive()),
				GetterUtil.get(
					warehouse.getStreet1(),
					commerceInventoryWarehouse.getStreet1()),
				GetterUtil.get(
					warehouse.getStreet2(),
					commerceInventoryWarehouse.getStreet2()),
				GetterUtil.get(
					warehouse.getStreet3(),
					commerceInventoryWarehouse.getStreet3()),
				GetterUtil.get(
					warehouse.getCity(), commerceInventoryWarehouse.getCity()),
				GetterUtil.get(
					warehouse.getZip(), commerceInventoryWarehouse.getZip()),
				GetterUtil.get(
					warehouse.getRegionISOCode(),
					commerceInventoryWarehouse.getCommerceRegionCode()),
				GetterUtil.get(
					warehouse.getCountryISOCode(),
					commerceInventoryWarehouse.getCountryTwoLettersISOCode()),
				GetterUtil.get(
					warehouse.getLatitude(),
					commerceInventoryWarehouse.getLatitude()),
				GetterUtil.get(
					warehouse.getLongitude(),
					commerceInventoryWarehouse.getLongitude()),
				commerceInventoryWarehouse.getMvccVersion(),
				_serviceContextHelper.getServiceContext());

		// Update nested resources

		_updateNestedResources(warehouse, commerceInventoryWarehouse);

		return commerceInventoryWarehouse;
	}

	private static final EntityModel _entityModel = new WarehouseEntityModel();

	@Reference
	private CommerceInventoryWarehouseItemService
		_commerceInventoryWarehouseItemService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter.WarehouseDTOConverter)"
	)
	private DTOConverter<CommerceInventoryWarehouse, Warehouse>
		_warehouseDTOConverter;

}