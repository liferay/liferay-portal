/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Calvin Keum
 * @generated
 */
public abstract class PatcherProductVersionActionableDynamicQuery
	extends BaseActionableDynamicQuery {
	public PatcherProductVersionActionableDynamicQuery()
		throws SystemException {
		setBaseLocalService(PatcherProductVersionLocalServiceUtil.getService());
		setClass(PatcherProductVersion.class);

		setClassLoader(com.liferay.osb.patcher.service.ClpSerializer.class.getClassLoader());

		setPrimaryKeyPropertyName("patcherProductVersionId");
	}
}