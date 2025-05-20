/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v1_3_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;

import java.util.List;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixs(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherFix patcherFix : patcherFixes) {
			if (patcherFix.getStatus() == _STATUS_FIX_PENDING_CONFLICT) {
				patcherFix.setType(_TYPE_GENERATED);

				PatcherFixLocalServiceUtil.updatePatcherFix(patcherFix);
			}
		}
	}

	private static final int _STATUS_FIX_PENDING_CONFLICT = 102;

	private static final int _TYPE_GENERATED = 2;

}