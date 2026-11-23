/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserIdMapper;
import com.liferay.portal.service.base.UserIdMapperLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 */
public class UserIdMapperLocalServiceImpl
	extends UserIdMapperLocalServiceBaseImpl {

	@Override
	public void deleteUserIdMappers(long userId) {
		userIdMapperPersistence.removeByUserId(userId);
	}

	@Override
	public UserIdMapper getUserIdMapperByExternalUserId(
			String type, String externalUserId)
		throws PortalException {

		return userIdMapperPersistence.findByT_E(type, externalUserId);
	}

	@Override
	public UserIdMapper updateUserIdMapper(
		long userId, String type, String description, String externalUserId) {

		UserIdMapper userIdMapper = userIdMapperPersistence.fetchByU_T(
			userId, type);

		if (userIdMapper == null) {
			long userIdMapperId = counterLocalService.increment();

			userIdMapper = userIdMapperPersistence.create(userIdMapperId);
		}

		userIdMapper.setUserId(userId);
		userIdMapper.setType(type);
		userIdMapper.setDescription(description);
		userIdMapper.setExternalUserId(externalUserId);

		return userIdMapperPersistence.update(userIdMapper);
	}

}