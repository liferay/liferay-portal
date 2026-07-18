/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.service.base.ListTypeServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class ListTypeServiceImpl extends ListTypeServiceBaseImpl {

	@Override
	public ListType getListType(long listTypeId) throws PortalException {
		return listTypePersistence.findByPrimaryKey(listTypeId);
	}

	@Override
	public ListType getListType(long companyId, String name, String type) {
		return listTypePersistence.fetchByC_N_T(companyId, name, type);
	}

	@Override
	public long getListTypeId(long companyId, String name, String type) {
		return listTypeLocalService.getListTypeId(companyId, name, type);
	}

	@Override
	public List<ListType> getListTypes(long companyId, String type) {
		return listTypeLocalService.getListTypes(companyId, type);
	}

	@Override
	public void validate(long listTypeId, long classNameId, String type)
		throws PortalException {

		listTypeLocalService.validate(listTypeId, classNameId, type);
	}

	@Override
	public void validate(long listTypeId, String type) throws PortalException {
		listTypeLocalService.validate(listTypeId, type);
	}

}