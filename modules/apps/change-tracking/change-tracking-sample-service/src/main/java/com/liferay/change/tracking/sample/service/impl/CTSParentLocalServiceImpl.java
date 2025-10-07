/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.impl;

import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.change.tracking.sample.service.base.CTSParentLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.sample.model.CTSParent",
	service = AopService.class
)
public class CTSParentLocalServiceImpl extends CTSParentLocalServiceBaseImpl {

	@Override
	public CTSParent addCTSParent(long companyId, long ctsGrandParentId) {
		long ctsParentId = counterLocalService.increment(
			CTSParent.class.getName());

		CTSParent ctsParent = ctsParentPersistence.create(ctsParentId);

		ctsParent.setCompanyId(companyId);
		ctsParent.setCtsGrandParentId(ctsGrandParentId);
		ctsParent.setName(String.valueOf(ctsParentId));

		return ctsParentPersistence.update(ctsParent);
	}

	@Override
	public void deleteCTSParents(long companyId) {
		ctsParentPersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteCTSParentsByCTSGrandParentId(
		long companyId, long ctsGrandParentId) {

		ctsParentPersistence.removeByC_C(companyId, ctsGrandParentId);
	}

	@Override
	public List<CTSParent> getCTSParents(long companyId) {
		return ctsParentPersistence.findByCompanyId(companyId);
	}

	@Override
	public CTSParent updateCTSParent(long ctsParentId) throws PortalException {
		CTSParent ctsParent = ctsParentPersistence.findByPrimaryKey(
			ctsParentId);

		ctsParent.setName(ctsParent.getName() + " Updated");

		return ctsParentPersistence.update(ctsParent);
	}

}