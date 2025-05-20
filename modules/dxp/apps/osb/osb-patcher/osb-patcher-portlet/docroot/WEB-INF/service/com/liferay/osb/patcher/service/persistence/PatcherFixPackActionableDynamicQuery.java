/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;

import com.liferay.portal.kernel.dao.orm.BaseActionableDynamicQuery;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Calvin Keum
 * @generated
 */
public abstract class PatcherFixPackActionableDynamicQuery
	extends BaseActionableDynamicQuery {
	public PatcherFixPackActionableDynamicQuery() throws SystemException {
		setBaseLocalService(PatcherFixPackLocalServiceUtil.getService());
		setClass(PatcherFixPack.class);

		setClassLoader(com.liferay.osb.patcher.service.ClpSerializer.class.getClassLoader());

		setPrimaryKeyPropertyName("patcherFixPackId");
	}
}