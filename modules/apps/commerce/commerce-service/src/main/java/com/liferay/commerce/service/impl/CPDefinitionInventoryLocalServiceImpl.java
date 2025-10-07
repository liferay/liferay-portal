/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.impl;

import com.liferay.commerce.exception.CPDefinitionInventoryAllowedOrderQuantitiesException;
import com.liferay.commerce.exception.CPDefinitionInventoryMaxOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMinOrderQuantityException;
import com.liferay.commerce.exception.CPDefinitionInventoryMultipleOrderQuantityException;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.service.base.CPDefinitionInventoryLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.math.BigDecimal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Alec Sloan
 */
@Component(
	property = "model.class.name=com.liferay.commerce.model.CPDefinitionInventory",
	service = AopService.class
)
public class CPDefinitionInventoryLocalServiceImpl
	extends CPDefinitionInventoryLocalServiceBaseImpl {

	@Override
	public CPDefinitionInventory addCPDefinitionInventory(
			long userId, long cpDefinitionId,
			String cpDefinitionInventoryEngine, String lowStockActivity,
			boolean displayAvailability, boolean displayStockQuantity,
			BigDecimal minStockQuantity, boolean backOrders,
			BigDecimal minOrderQuantity, BigDecimal maxOrderQuantity,
			String allowedOrderQuantities, BigDecimal multipleOrderQuantity)
		throws PortalException {

		_validateOrderQuantity(
			minOrderQuantity, maxOrderQuantity, multipleOrderQuantity);
		_validateAllowedOrderQuantities(allowedOrderQuantities);

		User user = _userLocalService.getUser(userId);

		long cpDefinitionInventoryId = counterLocalService.increment();

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryPersistence.create(cpDefinitionInventoryId);

		CPDefinition cpDefinition = _cpDefinitionLocalService.getCPDefinition(
			cpDefinitionId);

		if (_cpDefinitionLocalService.isVersionable(cpDefinitionId)) {
			cpDefinition = _cpDefinitionLocalService.copyCPDefinition(
				cpDefinitionId);
		}

		cpDefinitionInventory.setGroupId(cpDefinition.getGroupId());
		cpDefinitionInventory.setCompanyId(user.getCompanyId());
		cpDefinitionInventory.setUserId(user.getUserId());
		cpDefinitionInventory.setUserName(user.getFullName());
		cpDefinitionInventory.setCPDefinitionId(
			cpDefinition.getCPDefinitionId());
		cpDefinitionInventory.setCPDefinitionInventoryEngine(
			cpDefinitionInventoryEngine);
		cpDefinitionInventory.setLowStockActivity(lowStockActivity);
		cpDefinitionInventory.setDisplayAvailability(displayAvailability);
		cpDefinitionInventory.setDisplayStockQuantity(displayStockQuantity);
		cpDefinitionInventory.setMinStockQuantity(minStockQuantity);
		cpDefinitionInventory.setBackOrders(backOrders);
		cpDefinitionInventory.setMinOrderQuantity(minOrderQuantity);
		cpDefinitionInventory.setMaxOrderQuantity(maxOrderQuantity);
		cpDefinitionInventory.setAllowedOrderQuantities(allowedOrderQuantities);
		cpDefinitionInventory.setMultipleOrderQuantity(multipleOrderQuantity);

		return cpDefinitionInventoryPersistence.update(cpDefinitionInventory);
	}

	@Override
	public void cloneCPDefinitionInventory(
		long cpDefinitionId, long newCPDefinitionId) {

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);

		if (cpDefinitionInventory != null) {
			CPDefinitionInventory newCPDefinitionInventory =
				(CPDefinitionInventory)cpDefinitionInventory.clone();

			newCPDefinitionInventory.setUuid(PortalUUIDUtil.generate());
			newCPDefinitionInventory.setCPDefinitionInventoryId(
				counterLocalService.increment());
			newCPDefinitionInventory.setCPDefinitionId(newCPDefinitionId);

			cpDefinitionInventoryLocalService.addCPDefinitionInventory(
				newCPDefinitionInventory);
		}
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CPDefinitionInventory deleteCPDefinitionInventory(
		CPDefinitionInventory cpDefinitionInventory) {

		if (_cpDefinitionLocalService.isVersionable(
				cpDefinitionInventory.getCPDefinitionId())) {

			try {
				CPDefinition newCPDefinition =
					_cpDefinitionLocalService.copyCPDefinition(
						cpDefinitionInventory.getCPDefinitionId());

				cpDefinitionInventory =
					cpDefinitionInventoryPersistence.findByCPDefinitionId(
						newCPDefinition.getCPDefinitionId());
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		return cpDefinitionInventoryPersistence.remove(cpDefinitionInventory);
	}

	@Override
	public CPDefinitionInventory deleteCPDefinitionInventory(
			long cpDefinitionInventoryId)
		throws PortalException {

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryPersistence.findByPrimaryKey(
				cpDefinitionInventoryId);

		return cpDefinitionInventoryLocalService.deleteCPDefinitionInventory(
			cpDefinitionInventory);
	}

	@Override
	public void deleteCPDefinitionInventoryByCPDefinitionId(
		long cpDefinitionId) {

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryLocalService.
				fetchCPDefinitionInventoryByCPDefinitionId(cpDefinitionId);

		if (cpDefinitionInventory != null) {
			cpDefinitionInventoryLocalService.deleteCPDefinitionInventory(
				cpDefinitionInventory);
		}
	}

	@Override
	public CPDefinitionInventory fetchCPDefinitionInventoryByCPDefinitionId(
		long cpDefinitionId) {

		return cpDefinitionInventoryPersistence.fetchByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public CPDefinitionInventory updateCPDefinitionInventory(
			long cpDefinitionInventoryId, String cpDefinitionInventoryEngine,
			String lowStockActivity, boolean displayAvailability,
			boolean displayStockQuantity, BigDecimal minStockQuantity,
			boolean backOrders, BigDecimal minOrderQuantity,
			BigDecimal maxOrderQuantity, String allowedOrderQuantities,
			BigDecimal multipleOrderQuantity)
		throws PortalException {

		_validateOrderQuantity(
			minOrderQuantity, maxOrderQuantity, multipleOrderQuantity);
		_validateAllowedOrderQuantities(allowedOrderQuantities);

		CPDefinitionInventory cpDefinitionInventory =
			cpDefinitionInventoryPersistence.findByPrimaryKey(
				cpDefinitionInventoryId);

		if (_cpDefinitionLocalService.isVersionable(
				cpDefinitionInventory.getCPDefinitionId())) {

			CPDefinition newCPDefinition =
				_cpDefinitionLocalService.copyCPDefinition(
					cpDefinitionInventory.getCPDefinitionId());

			cpDefinitionInventory =
				cpDefinitionInventoryPersistence.findByCPDefinitionId(
					newCPDefinition.getCPDefinitionId());
		}

		cpDefinitionInventory.setCPDefinitionInventoryEngine(
			cpDefinitionInventoryEngine);
		cpDefinitionInventory.setLowStockActivity(lowStockActivity);
		cpDefinitionInventory.setDisplayAvailability(displayAvailability);
		cpDefinitionInventory.setDisplayStockQuantity(displayStockQuantity);
		cpDefinitionInventory.setMinStockQuantity(minStockQuantity);
		cpDefinitionInventory.setBackOrders(backOrders);
		cpDefinitionInventory.setMinOrderQuantity(minOrderQuantity);
		cpDefinitionInventory.setMaxOrderQuantity(maxOrderQuantity);
		cpDefinitionInventory.setAllowedOrderQuantities(allowedOrderQuantities);
		cpDefinitionInventory.setMultipleOrderQuantity(multipleOrderQuantity);

		return cpDefinitionInventoryPersistence.update(cpDefinitionInventory);
	}

	private void _validateAllowedOrderQuantities(String allowedOrderQuantities)
		throws PortalException {

		if (Validator.isNull(allowedOrderQuantities)) {
			return;
		}

		Matcher matcher = _pattern.matcher(allowedOrderQuantities);

		if (!matcher.matches()) {
			throw new CPDefinitionInventoryAllowedOrderQuantitiesException();
		}
	}

	private void _validateOrderQuantity(
			BigDecimal minOrderQuantity, BigDecimal maxOrderQuantity,
			BigDecimal multipleOrderQuantity)
		throws CPDefinitionInventoryMaxOrderQuantityException,
			   CPDefinitionInventoryMinOrderQuantityException,
			   CPDefinitionInventoryMultipleOrderQuantityException {

		if (BigDecimalUtil.lte(minOrderQuantity, BigDecimal.ZERO)) {
			throw new CPDefinitionInventoryMinOrderQuantityException(
				"Minimum order quantity must be greater than 0");
		}

		if (BigDecimalUtil.lte(maxOrderQuantity, BigDecimal.ZERO)) {
			throw new CPDefinitionInventoryMaxOrderQuantityException(
				"Maximum order quantity must be greater than 0");
		}

		if (BigDecimalUtil.lte(multipleOrderQuantity, BigDecimal.ZERO)) {
			throw new CPDefinitionInventoryMultipleOrderQuantityException(
				"Multiple order quantity must be greater than 0");
		}
	}

	private static final Pattern _pattern = Pattern.compile(
		"^(\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?)" +
			"(\\s\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?)*$");

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}