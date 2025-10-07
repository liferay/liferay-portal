/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.model.impl.UserImpl;

import java.util.Objects;

/**
 * @author Preston Crary
 */
public class UserModelListener extends BaseModelListener<User> {

	@Override
	public void onAfterAddAssociation(
		Object classPK, String associationClassName,
		Object associationClassPK) {

		long userId = (long)classPK;

		PermissionCacheUtil.clearCache(userId);

		if (Objects.equals(UserGroup.class.getName(), associationClassName)) {
			EntityCacheUtil.removeResult(UserImpl.class, userId);
		}
	}

	@Override
	public void onAfterRemove(User user) {
		if (user != null) {
			PermissionCacheUtil.clearCache(user.getUserId());
		}
	}

	@Override
	public void onAfterRemoveAssociation(
		Object classPK, String associationClassName,
		Object associationClassPK) {

		long userId = (long)classPK;

		PermissionCacheUtil.clearCache(userId);

		if (Objects.equals(UserGroup.class.getName(), associationClassName)) {
			EntityCacheUtil.removeResult(UserImpl.class, userId);
		}
	}

}