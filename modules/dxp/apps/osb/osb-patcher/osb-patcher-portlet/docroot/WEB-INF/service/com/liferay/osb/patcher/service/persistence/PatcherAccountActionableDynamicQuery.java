/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Calvin Keum
 * @generated
 */
public abstract class PatcherAccountActionableDynamicQuery
	extends BaseActionableDynamicQuery {
	public PatcherAccountActionableDynamicQuery() throws SystemException {
		setBaseLocalService(PatcherAccountLocalServiceUtil.getService());
		setClass(PatcherAccount.class);

		setClassLoader(com.liferay.osb.patcher.service.ClpSerializer.class.getClassLoader());

		setPrimaryKeyPropertyName("patcherAccountId");
	}
}