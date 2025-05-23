/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.base.PatcherFixPackLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherFixPackVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFixPack",
	service = AopService.class
)
public class PatcherFixPackLocalServiceImpl
	extends PatcherFixPackLocalServiceBaseImpl {

	@Override
	public PatcherFixPack fetchPatcherFixPack(
		long patcherProjectVersionId, String name) {

		return patcherFixPackPersistence.fetchByPFCI_N(
			patcherProjectVersionId, name);
	}

	@Override
	public PatcherFixPack fetchPatcherFixPackByPatcherBuildId(
		long patcherBuildId) {

		return patcherFixPackPersistence.fetchByPatcherBuildId(patcherBuildId);
	}

	@Override
	public PatcherFixPack getPatcherFixPack(
			long patcherProjectVersionId, String name)
		throws PortalException {

		return patcherFixPackPersistence.findByPFCI_N(
			patcherProjectVersionId, name);
	}

	@Override
	public PatcherFixPack getPatcherFixPackByPatcherBuildId(long patcherBuildId)
		throws PortalException {

		return patcherFixPackPersistence.findByPatcherBuildId(patcherBuildId);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacks(
		long patcherFixComponentId, int version) {

		return patcherFixPackPersistence.findByPFCI_V(
			patcherFixComponentId, version);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacks(
		long patcherFixComponentId, long patcherProjectVersionId, int version,
		boolean older) {

		if (older) {
			return patcherFixPackPersistence.findByPFCI_PPVI_LtV(
				patcherFixComponentId, patcherProjectVersionId, version,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherFixPackVersionComparator.getInstance(false));
		}

		return patcherFixPackPersistence.findByPFCI_PPVI_GtV(
			patcherFixComponentId, patcherProjectVersionId, version,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixPackVersionComparator.getInstance(true));
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacksByPatcherFixComponentId(
		long patcherFixComponentId) {

		return patcherFixPackPersistence.findByPatcherFixComponentId(
			patcherFixComponentId);
	}

	@Override
	public List<PatcherFixPack> getPatcherFixPacksByStatus(
		long patcherProjectVersionId, int status) {

		return patcherFixPackPersistence.findByPFCI_S(
			patcherProjectVersionId, status);
	}

}