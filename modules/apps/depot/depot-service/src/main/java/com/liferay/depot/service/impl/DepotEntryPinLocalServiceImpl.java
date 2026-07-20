/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.impl;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.service.base.DepotEntryPinLocalServiceBaseImpl;
import com.liferay.depot.service.persistence.DepotEntryPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.depot.model.DepotEntryPin",
	service = AopService.class
)
public class DepotEntryPinLocalServiceImpl
	extends DepotEntryPinLocalServiceBaseImpl {

	@Override
	public DepotEntryPin addDepotEntryPin(long userId, long depotEntryId)
		throws PortalException {

		DepotEntryPin depotEntryPin = depotEntryPinPersistence.create(
			counterLocalService.increment());

		DepotEntry depotEntry = _depotEntryPersistence.findByPrimaryKey(
			depotEntryId);

		depotEntryPin.setGroupId(depotEntry.getGroupId());

		User user = _userLocalService.getUser(userId);

		depotEntryPin.setCompanyId(user.getCompanyId());

		depotEntryPin.setUserId(userId);
		depotEntryPin.setDepotEntryId(depotEntryId);

		return depotEntryPinPersistence.update(depotEntryPin);
	}

	@Override
	public void deleteDepotEntryDepotEntryPins(long depotEntryId) {
		depotEntryPinPersistence.removeByDepotEntryId(depotEntryId);
	}

	@Override
	public DepotEntryPin deleteDepotEntryPin(long userId, long depotEntryId)
		throws PortalException {

		return depotEntryPinPersistence.removeByU_D(userId, depotEntryId);
	}

	@Override
	public void deleteUserDepotEntryPins(long userId) {
		depotEntryPinPersistence.removeByUserId(userId);
	}

	@Override
	public DepotEntryPin fetchGroupDepotEntryPin(long groupId, long userId) {
		DepotEntry depotEntry = _depotEntryPersistence.fetchByGroupId(groupId);

		if (depotEntry == null) {
			return null;
		}

		return depotEntryPinPersistence.fetchByU_D(
			userId, depotEntry.getDepotEntryId());
	}

	@Override
	public List<DepotEntryPin> getDepotEntryDepotEntryPins(
		long depotEntryId, int start, int end) {

		return depotEntryPinPersistence.findByDepotEntryId(
			depotEntryId, start, end);
	}

	@Override
	public int getDepotEntryDepotEntryPinsCount(long depotEntryId) {
		return depotEntryPinPersistence.countByDepotEntryId(depotEntryId);
	}

	@Override
	public DepotEntryPin getDepotEntryPin(long userId, long depotEntryId)
		throws PortalException {

		return depotEntryPinPersistence.findByU_D(userId, depotEntryId);
	}

	@Override
	public List<DepotEntryPin> getUserDepotEntryPins(
		long userId, int start, int end) {

		return depotEntryPinPersistence.findByUserId(userId, start, end);
	}

	public int getUserDepotEntryPinsCount(long userId) {
		return depotEntryPinPersistence.countByUserId(userId);
	}

	@Reference
	private DepotEntryPersistence _depotEntryPersistence;

	@Reference
	private UserLocalService _userLocalService;

}