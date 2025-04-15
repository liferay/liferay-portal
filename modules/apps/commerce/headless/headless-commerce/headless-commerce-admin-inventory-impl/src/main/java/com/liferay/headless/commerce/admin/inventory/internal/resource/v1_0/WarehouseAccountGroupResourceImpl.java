/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.internal.resource.v1_0;

import com.liferay.account.exception.NoSuchEntryException;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupService;
import com.liferay.commerce.exception.NoSuchWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseService;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.Warehouse;
import com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccountGroup;
import com.liferay.headless.commerce.admin.inventory.resource.v1_0.WarehouseAccountGroupResource;
import com.liferay.headless.commerce.core.util.ActionUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/warehouse-account-group.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = WarehouseAccountGroupResource.class
)
public class WarehouseAccountGroupResourceImpl
	extends BaseWarehouseAccountGroupResourceImpl {

	@Override
	public void deleteWarehouseAccountGroup(Long id) throws Exception {
		_commerceInventoryWarehouseRelService.
			deleteCommerceInventoryWarehouseRel(id);
	}

	@Override
	public Page<WarehouseAccountGroup>
			getWarehouseByExternalReferenceCodeWarehouseAccountGroupsPage(
				String externalReferenceCode, Pagination pagination)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			throw new NoSuchWarehouseException(
				"Unable to find warehouse with external reference code " +
					externalReferenceCode);
		}

		List<CommerceInventoryWarehouseRel> commerceInventoryWarehouseRels =
			_commerceInventoryWarehouseRelService.
				getCommerceInventoryWarehouseRels(
					AccountGroup.class.getName(),
					commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					pagination.getStartPosition(), pagination.getEndPosition(),
					null);

		int totalCount =
			_commerceInventoryWarehouseRelService.
				getCommerceInventoryWarehouseRelsCount(
					AccountGroup.class.getName(),
					commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId());

		return Page.of(
			_toWarehouseAccountGroups(commerceInventoryWarehouseRels),
			pagination, totalCount);
	}

	@NestedField(
		parentClass = Warehouse.class, value = "warehouseAccountGroups"
	)
	@Override
	public Page<WarehouseAccountGroup> getWarehouseIdWarehouseAccountGroupsPage(
			Long id, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		List<CommerceInventoryWarehouseRel> commerceInventoryWarehouseRels =
			_commerceInventoryWarehouseRelService.
				getAccountGroupCommerceInventoryWarehouseRels(
					id, search, pagination.getStartPosition(),
					pagination.getEndPosition());

		int totalCount =
			_commerceInventoryWarehouseRelService.
				getAccountGroupCommerceInventoryWarehouseRelsCount(id, search);

		return Page.of(
			_toWarehouseAccountGroups(commerceInventoryWarehouseRels),
			pagination, totalCount);
	}

	@Override
	public WarehouseAccountGroup
			postWarehouseByExternalReferenceCodeWarehouseAccountGroup(
				String externalReferenceCode,
				WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_commerceInventoryWarehouseService.
				fetchCommerceInventoryWarehouseByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commerceInventoryWarehouse == null) {
			throw new NoSuchWarehouseException(
				"Unable to find warehouse with external reference code " +
					externalReferenceCode);
		}

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_addCommerceInventoryWarehouseRel(
				commerceInventoryWarehouse, warehouseAccountGroup);

		return _toWarehouseAccountGroup(
			commerceInventoryWarehouseRel.getCommerceInventoryWarehouseRelId());
	}

	@Override
	public WarehouseAccountGroup postWarehouseIdWarehouseAccountGroup(
			Long id, WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_addCommerceInventoryWarehouseRel(
				_commerceInventoryWarehouseService.
					getCommerceInventoryWarehouse(id),
				warehouseAccountGroup);

		return _toWarehouseAccountGroup(
			commerceInventoryWarehouseRel.getCommerceInventoryWarehouseRelId());
	}

	private Map<String, String> _addAction(
			Class<?> clazz,
			CommerceInventoryWarehouseRel commerceInventoryWarehouseRel,
			String methodName, UriInfo uriInfo)
		throws Exception {

		if (!_commerceInventoryWarehouseModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commerceInventoryWarehouseRel.getCommerceInventoryWarehouseId(),
				"UPDATE")) {

			return null;
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();

				return uriBuilder.path(
					ActionUtil.getVersion(uriInfo)
				).path(
					clazz.getSuperclass(), methodName
				).toTemplate();
			}
		).put(
			"method",
			ActionUtil.getHttpMethodName(
				clazz, ActionUtil.getMethod(clazz, methodName))
		).build();
	}

	private CommerceInventoryWarehouseRel _addCommerceInventoryWarehouseRel(
			CommerceInventoryWarehouse commerceInventoryWarehouse,
			WarehouseAccountGroup warehouseAccountGroup)
		throws Exception {

		AccountGroup accountGroup = null;

		if (Validator.isNull(
				warehouseAccountGroup.getAccountGroupExternalReferenceCode())) {

			accountGroup = _accountGroupService.getAccountGroup(
				warehouseAccountGroup.getAccountGroupId());
		}
		else {
			accountGroup =
				_accountGroupService.fetchAccountGroupByExternalReferenceCode(
					warehouseAccountGroup.
						getAccountGroupExternalReferenceCode(),
					commerceInventoryWarehouse.getCompanyId());

			if (accountGroup == null) {
				String externalReferenceCode =
					warehouseAccountGroup.
						getAccountGroupExternalReferenceCode();

				throw new NoSuchEntryException(
					"Unable to find account group with external reference " +
						"code " + externalReferenceCode);
			}
		}

		return _commerceInventoryWarehouseRelService.
			addCommerceInventoryWarehouseRel(
				AccountGroup.class.getName(), accountGroup.getAccountGroupId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId());
	}

	private Map<String, Map<String, String>> _getActions(
			CommerceInventoryWarehouseRel commerceInventoryWarehouseRel)
		throws Exception {

		if (contextUriInfo == null) {
			return null;
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_addAction(
				getClass(), commerceInventoryWarehouseRel,
				"deleteWarehouseAccountGroup", contextUriInfo)
		).build();
	}

	private WarehouseAccountGroup _toWarehouseAccountGroup(
			Long commerceInventoryWarehouseRelId)
		throws Exception {

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_commerceInventoryWarehouseRelService.
				getCommerceInventoryWarehouseRel(
					commerceInventoryWarehouseRelId);

		return _warehouseAccountGroupDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(commerceInventoryWarehouseRel),
				_dtoConverterRegistry, commerceInventoryWarehouseRelId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private List<WarehouseAccountGroup> _toWarehouseAccountGroups(
		List<CommerceInventoryWarehouseRel> commerceInventoryWarehouseRels) {

		return transform(
			commerceInventoryWarehouseRels,
			commerceInventoryWarehouseRel -> _toWarehouseAccountGroup(
				commerceInventoryWarehouseRel.
					getCommerceInventoryWarehouseRelId()));
	}

	@Reference
	private AccountGroupService _accountGroupService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

	@Reference
	private CommerceInventoryWarehouseRelService
		_commerceInventoryWarehouseRelService;

	@Reference
	private CommerceInventoryWarehouseService
		_commerceInventoryWarehouseService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.inventory.internal.dto.v1_0.converter.WarehouseAccountGroupDTOConverter)"
	)
	private DTOConverter<CommerceInventoryWarehouseRel, WarehouseAccountGroup>
		_warehouseAccountGroupDTOConverter;

}