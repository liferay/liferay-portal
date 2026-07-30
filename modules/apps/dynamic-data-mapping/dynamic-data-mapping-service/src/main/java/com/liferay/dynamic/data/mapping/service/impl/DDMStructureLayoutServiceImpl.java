/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.service.base.DDMStructureLayoutServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=ddm",
		"json.web.service.context.path=DDMStructureLayout"
	},
	service = AopService.class
)
public class DDMStructureLayoutServiceImpl
	extends DDMStructureLayoutServiceBaseImpl {

	@Override
	public List<DDMStructureLayout> getStructureLayouts(
			long groupId, int start, int end)
		throws PortalException {

		return ddmStructureLayoutLocalService.getStructureLayouts(
			groupId, start, end);
	}

	@Override
	public int getStructureLayoutsCount(long groupId) {
		return ddmStructureLayoutPersistence.countByGroupId(groupId);
	}

	@Override
	public List<DDMStructureLayout> search(
			long companyId, long[] groupIds, long classNameId, String keywords,
			int start, int end,
			OrderByComparator<DDMStructureLayout> orderByComparator)
		throws PortalException {

		return ddmStructureLayoutLocalService.search(
			companyId, groupIds, classNameId, keywords, start, end,
			orderByComparator);
	}

}