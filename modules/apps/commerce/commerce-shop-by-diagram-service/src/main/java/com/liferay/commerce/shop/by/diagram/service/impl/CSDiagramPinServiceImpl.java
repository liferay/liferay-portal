/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramPinServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CSDiagramPin"
	},
	service = AopService.class
)
public class CSDiagramPinServiceImpl extends CSDiagramPinServiceBaseImpl {

	@Override
	public CSDiagramPin addCSDiagramPin(
			long cpDefinitionId, double positionX, double positionY,
			String sequence)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramPinLocalService.addCSDiagramPin(
			getUserId(), cpDefinitionId, positionX, positionY, sequence);
	}

	@Override
	public void deleteCSDiagramPin(CSDiagramPin csDiagramPin)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramPin.getCPDefinitionId(),
			ActionKeys.UPDATE);

		csDiagramPinLocalService.deleteCSDiagramPin(csDiagramPin);
	}

	@Override
	public void deleteCSDiagramPins(long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		csDiagramPinLocalService.deleteCSDiagramPins(cpDefinitionId);
	}

	@Override
	public CSDiagramPin fetchCSDiagramPin(long csDiagramPinId) {
		return csDiagramPinPersistence.fetchByPrimaryKey(csDiagramPinId);
	}

	@Override
	public CSDiagramPin getCSDiagramPin(long csDiagramPinId)
		throws PortalException {

		CSDiagramPin csDiagramPin = csDiagramPinPersistence.findByPrimaryKey(
			csDiagramPinId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramPin.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramPin;
	}

	@Override
	public List<CSDiagramPin> getCSDiagramPins(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramPinPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramPinsCount(long cpDefinitionId)
		throws PortalException {

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), cpDefinitionId, ActionKeys.UPDATE);

		return csDiagramPinPersistence.countByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramPin updateCSDiagramPin(
			long csDiagramPinId, double positionX, double positionY,
			String sequence)
		throws PortalException {

		CSDiagramPin csDiagramPin = csDiagramPinPersistence.findByPrimaryKey(
			csDiagramPinId);

		_cpDefinitionModelResourcePermission.check(
			getPermissionChecker(), csDiagramPin.getCPDefinitionId(),
			ActionKeys.UPDATE);

		return csDiagramPinLocalService.updateCSDiagramPin(
			csDiagramPinId, positionX, positionY, sequence);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.product.model.CPDefinition)"
	)
	private ModelResourcePermission<CPDefinition>
		_cpDefinitionModelResourcePermission;

}