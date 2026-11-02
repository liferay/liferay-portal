/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectFilter;
import com.liferay.object.service.base.ObjectFilterLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectFilter",
	service = AopService.class
)
public class ObjectFilterLocalServiceImpl
	extends ObjectFilterLocalServiceBaseImpl {

	@Override
	public ObjectFilter addObjectFilter(
			long userId, long objectFieldId, String filterBy, String filterType,
			String json)
		throws PortalException {

		ObjectFilter objectFilter = objectFilterPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectFilter.setCompanyId(user.getCompanyId());
		objectFilter.setUserId(user.getUserId());
		objectFilter.setUserName(user.getFullName());

		objectFilter.setObjectFieldId(objectFieldId);
		objectFilter.setFilterBy(filterBy);
		objectFilter.setFilterType(filterType);
		objectFilter.setJSON(json);

		return objectFilterPersistence.update(objectFilter);
	}

	@Override
	public void deleteObjectFieldObjectFilter(long objectFieldId) {
		objectFilterPersistence.removeByObjectFieldId(objectFieldId);
	}

	@Reference
	private UserLocalService _userLocalService;

}