/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service.impl;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.commerce.shop.by.diagram.service.base.CSDiagramPinLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "model.class.name=com.liferay.commerce.shop.by.diagram.model.CSDiagramPin",
	service = AopService.class
)
public class CSDiagramPinLocalServiceImpl
	extends CSDiagramPinLocalServiceBaseImpl {

	@Override
	public CSDiagramPin addCSDiagramPin(
			long userId, long cpDefinitionId, double positionX,
			double positionY, String sequence)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long csDiagramPinId = counterLocalService.increment();

		CSDiagramPin csDiagramPin = csDiagramPinPersistence.create(
			csDiagramPinId);

		csDiagramPin.setCompanyId(user.getCompanyId());
		csDiagramPin.setUserId(user.getUserId());
		csDiagramPin.setUserName(user.getFullName());
		csDiagramPin.setCPDefinitionId(cpDefinitionId);
		csDiagramPin.setPositionX(positionX);
		csDiagramPin.setPositionY(positionY);
		csDiagramPin.setSequence(sequence);

		return csDiagramPinPersistence.update(csDiagramPin);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public CSDiagramPin deleteCSDiagramPin(CSDiagramPin csDiagramPin) {
		return csDiagramPinPersistence.remove(csDiagramPin);
	}

	@Override
	public void deleteCSDiagramPins(long cpDefinitionId) {
		List<CSDiagramPin> csDiagramPins =
			csDiagramPinPersistence.findByCPDefinitionId(cpDefinitionId);

		for (CSDiagramPin csDiagramPin : csDiagramPins) {
			csDiagramPinLocalService.deleteCSDiagramPin(csDiagramPin);
		}
	}

	@Override
	public List<CSDiagramPin> getCSDiagramPins(
		long cpDefinitionId, int start, int end) {

		return csDiagramPinPersistence.findByCPDefinitionId(
			cpDefinitionId, start, end);
	}

	@Override
	public int getCSDiagramPinsCount(long cpDefinitionId) {
		return csDiagramPinPersistence.countByCPDefinitionId(cpDefinitionId);
	}

	@Override
	public CSDiagramPin updateCSDiagramPin(
			long csDiagramPinId, double positionX, double positionY,
			String sequence)
		throws PortalException {

		CSDiagramPin csDiagramPin = csDiagramPinPersistence.findByPrimaryKey(
			csDiagramPinId);

		csDiagramPin.setPositionX(positionX);
		csDiagramPin.setPositionY(positionY);
		csDiagramPin.setSequence(sequence);

		return csDiagramPinPersistence.update(csDiagramPin);
	}

	@Reference
	private UserLocalService _userLocalService;

}