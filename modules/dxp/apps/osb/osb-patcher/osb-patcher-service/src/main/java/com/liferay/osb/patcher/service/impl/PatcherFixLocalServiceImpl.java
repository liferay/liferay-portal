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
		Date modifiedDate, int[] type, boolean notified, int status) {

		return patcherFixPersistence.findByLtM_T_N_S(
			modifiedDate, type, notified, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, int type, boolean latestFix) {

		return patcherFixPersistence.findByP_T_L(
			patcherProjectVersionId, type, latestFix);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, int type, boolean latestFix, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return patcherFixPersistence.findByP_NotT_L(
				patcherProjectVersionId, type, latestFix);
		}

		return patcherFixPersistence.findByP_NotT_L_S(
			patcherProjectVersionId, type, latestFix, status);
	}

	@Override
	public List<PatcherFix> getPatcherFixes(
		long patcherProjectVersionId, String name, int type,
		boolean latestFix) {

		return patcherFixPersistence.findByP_N_NotT_L(
			patcherProjectVersionId, name, type, latestFix);
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
	public List<PatcherFix> getPatcherFixes(
		String key, int type, boolean latestFix) {

		return patcherFixPersistence.findByK_NotT_L(key, type, latestFix);
	}

	@Override
	public int getPatcherFixesCountByPatcherProjectVersionId(
		long patcherProjectVersionId) {

		return patcherFixPersistence.countByPatcherProjectVersionId(
			patcherProjectVersionId);
	}

}