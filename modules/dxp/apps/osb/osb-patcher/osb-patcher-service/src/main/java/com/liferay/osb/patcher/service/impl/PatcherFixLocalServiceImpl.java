/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.impl;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.base.PatcherFixLocalServiceBaseImpl;
import com.liferay.osb.patcher.util.comparator.PatcherFixKeyVersionComparator;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.osb.patcher.model.PatcherFix",
	service = AopService.class
)
public class PatcherFixLocalServiceImpl extends PatcherFixLocalServiceBaseImpl {

	@Override
	public List<PatcherFix> getPatcherFixes(
		Date modifiedDate, boolean notified, int[] type, int status) {

		return patcherFixPersistence.findByLtM_N_T_S(
			modifiedDate, notified, type, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, int type) {

		return patcherFixPersistence.findByP_L_T(
			patcherProjectVersionId, latestFix, type);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, int type, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return patcherFixPersistence.findByP_L_NotT(
				patcherProjectVersionId, latestFix, type);
		}

		return patcherFixPersistence.findByP_L_NotT_S(
			patcherProjectVersionId, latestFix, type, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, boolean latestFix, String name,
		int type) {

		return patcherFixPersistence.findByP_L_N_NotT(
			patcherProjectVersionId, latestFix, name, type);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		String key, boolean latestFix, int type) {

		return patcherFixPersistence.findByK_L_NotT(key, latestFix, type);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		String key, double keyVersion, int type, boolean older) {

		if (older) {
			return patcherFixPersistence.findByK_LtKV_NotT(
				key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				PatcherFixKeyVersionComparator.getInstance(false));
		}

		return patcherFixPersistence.findByK_GtKV_NotT(
			key, keyVersion, type, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			PatcherFixKeyVersionComparator.getInstance(true));
	}

	@Override
	public int getPatcherFixesCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherFixPersistence.countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

}