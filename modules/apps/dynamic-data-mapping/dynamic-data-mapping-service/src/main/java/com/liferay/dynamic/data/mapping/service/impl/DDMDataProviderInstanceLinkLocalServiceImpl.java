/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.service.base.DDMDataProviderInstanceLinkLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink",
	service = AopService.class
)
public class DDMDataProviderInstanceLinkLocalServiceImpl
	extends DDMDataProviderInstanceLinkLocalServiceBaseImpl {

	@Override
	public DDMDataProviderInstanceLink addDataProviderInstanceLink(
		long dataProviderInstanceId, long structureId) {

		long dataProviderInstanceLinkId = counterLocalService.increment();

		DDMDataProviderInstanceLink dataProviderInstanceLink =
			ddmDataProviderInstanceLinkPersistence.create(
				dataProviderInstanceLinkId);

		dataProviderInstanceLink.setDataProviderInstanceId(
			dataProviderInstanceId);
		dataProviderInstanceLink.setStructureId(structureId);

		return ddmDataProviderInstanceLinkPersistence.update(
			dataProviderInstanceLink);
	}

	@Override
	public void deleteDataProviderInstanceLink(
		DDMDataProviderInstanceLink dataProviderInstanceLink) {

		ddmDataProviderInstanceLinkPersistence.remove(dataProviderInstanceLink);
	}

	@Override
	public void deleteDataProviderInstanceLink(long dataProviderInstanceLinkId)
		throws PortalException {

		DDMDataProviderInstanceLink dataProviderInstanceLink =
			ddmDataProviderInstanceLinkPersistence.findByPrimaryKey(
				dataProviderInstanceLinkId);

		ddmDataProviderInstanceLinkPersistence.remove(dataProviderInstanceLink);
	}

	@Override
	public List<DDMDataProviderInstanceLink> getDataProviderInstanceLinks(
		long structureId) {

		return ddmDataProviderInstanceLinkPersistence.findByStructureId(
			structureId);
	}

}