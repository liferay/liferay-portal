/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.impl;

import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.service.base.CTSChildLocalServiceBaseImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.change.tracking.sample.model.CTSChild",
	service = AopService.class
)
public class CTSChildLocalServiceImpl extends CTSChildLocalServiceBaseImpl {

	@Override
	public CTSChild addCTSChild(long companyId) {
		return addCTSChild(companyId, 0, 0, "");
	}

	@Override
	public CTSChild addCTSChild(
		long companyId, long ctsGrandParentId, long parentCTSChildId,
		String ctsParentName) {

		long ctsChildId = counterLocalService.increment(
			CTSChild.class.getName());

		CTSChild ctsChild = ctsChildPersistence.create(ctsChildId);

		ctsChild.setCompanyId(companyId);
		ctsChild.setCtsGrandParentId(ctsGrandParentId);
		ctsChild.setParentCTSChildId(parentCTSChildId);
		ctsChild.setCtsParentName(ctsParentName);
		ctsChild.setName(String.valueOf(ctsChildId));

		return ctsChildPersistence.update(ctsChild);
	}

	@Override
	public CTSChild deleteCTSChild(CTSChild ctsChild) {
		List<CTSChild> ctsChildren = ctsChildPersistence.findByC_P(
			ctsChild.getCompanyId(), ctsChild.getCtsChildId());

		for (CTSChild c : ctsChildren) {
			deleteCTSChild(c);
		}

		return ctsChildPersistence.remove(ctsChild);
	}

	@Override
	public void deleteCTSChildren(long companyId) {
		ctsChildPersistence.removeByCompanyId(companyId);
	}

	@Override
	public void deleteCTSChildrenByCTSGrandParentId(
		long companyId, long ctsGrandParentId) {

		List<CTSChild> ctsChildren = ctsChildPersistence.findByC_C(
			companyId, ctsGrandParentId);

		for (CTSChild ctsChild : ctsChildren) {
			deleteCTSChild(ctsChild);
		}
	}

	@Override
	public void deleteCTSChildrenByParentCTSChildId(
		long companyId, long parentCTSChildId) {

		List<CTSChild> ctsChildren = ctsChildPersistence.findByC_P(
			companyId, parentCTSChildId);

		for (CTSChild ctsChild : ctsChildren) {
			deleteCTSChild(ctsChild);
		}
	}

	@Override
	public List<CTSChild> getCTSChildren(long companyId) {
		return ctsChildPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<CTSChild> getCTSChildrenByCTSGrandParentId(
		long ctsGrandParentId) {

		CTSGrandParent ctsGrandParent =
			_ctsGrandParentPersistence.fetchByPrimaryKey(ctsGrandParentId);

		return ctsChildPersistence.findByC_C(
			ctsGrandParent.getCompanyId(), ctsGrandParentId);
	}

	@Override
	public List<CTSChild> getCTSChildrenByParentCTSChildId(
			long parentCTSChildId)
		throws PortalException {

		CTSChild ctsChild = ctsChildPersistence.findByPrimaryKey(
			parentCTSChildId);

		return ctsChildPersistence.findByC_P(
			ctsChild.getCompanyId(), ctsChild.getCtsChildId());
	}

	@Override
	public CTSChild updateCTSChild(CTSChild ctsChild) {
		ctsChild.setName(ctsChild.getName() + " Updated");

		return ctsChildPersistence.update(ctsChild);
	}

	@Reference
	private CTSGrandParentPersistence _ctsGrandParentPersistence;

}