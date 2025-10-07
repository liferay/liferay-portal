/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.impl;

import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.service.base.CTSGrandParentLocalServiceBaseImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSChildPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.sample.model.CTSGrandParent",
	service = AopService.class
)
public class CTSGrandParentLocalServiceImpl
	extends CTSGrandParentLocalServiceBaseImpl {

	@Override
	public CTSGrandParent addCTSGrandParent(long companyId) {
		return addCTSGrandParent(companyId, 0);
	}

	@Override
	public CTSGrandParent addCTSGrandParent(
		long companyId, long parentCTSGrandParentId) {

		long ctsCTSGrandParentId = counterLocalService.increment(
			CTSGrandParent.class.getName());

		CTSGrandParent ctsGrandParent = ctsGrandParentPersistence.create(
			ctsCTSGrandParentId);

		ctsGrandParent.setCompanyId(companyId);
		ctsGrandParent.setParentCTSGrandParentId(parentCTSGrandParentId);
		ctsGrandParent.setName(String.valueOf(ctsCTSGrandParentId));

		return ctsGrandParentPersistence.update(ctsGrandParent);
	}

	@Override
	public CTSGrandParent deleteCTSGrandParent(CTSGrandParent ctsGrandParent) {
		_ctsChildPersistence.removeByC_C(
			ctsGrandParent.getCompanyId(),
			ctsGrandParent.getCtsGrandParentId());

		return ctsGrandParentPersistence.remove(ctsGrandParent);
	}

	@Override
	public void deleteCTSGrandParents(long companyId) {
		List<CTSGrandParent> ctsGrandParents =
			ctsGrandParentPersistence.findByCompanyId(companyId);

		for (CTSGrandParent ctsGrandParent : ctsGrandParents) {
			deleteCTSGrandParent(ctsGrandParent);
		}
	}

	@Override
	public List<CTSGrandParent> getCTSGrandParents(long companyId) {
		return ctsGrandParentPersistence.findByCompanyId(companyId);
	}

	@Override
	public CTSGrandParent updateCTSGrandParent(long ctsCTSGrandParentId)
		throws PortalException {

		CTSGrandParent ctsGrandParent =
			ctsGrandParentPersistence.findByPrimaryKey(ctsCTSGrandParentId);

		ctsGrandParent.setName(ctsGrandParent.getName() + " Updated");

		return ctsGrandParentPersistence.update(ctsGrandParent);
	}

	@Reference
	private CTSChildPersistence _ctsChildPersistence;

}