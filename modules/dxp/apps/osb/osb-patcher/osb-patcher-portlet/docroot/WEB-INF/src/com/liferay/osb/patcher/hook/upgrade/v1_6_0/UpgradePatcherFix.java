/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.hook.upgrade.v1_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.List;

/**
 * @author Calvin Keum
 */
public class UpgradePatcherFix extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFix();
	}

	protected void updatePatcherFix() throws Exception {
		_removePatcherFixDuplicates();

		runSQL("alter_column_type OSB_PatcherFix patcherFixKey VARCHAR(75)");

		List<PatcherFix> patcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixs(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (PatcherFix patcherFix : patcherFixes) {
			StringBundler sb = new StringBundler(3);

			sb.append(PatcherFix.class.getName());
			sb.append(patcherFix.getPatcherProjectVersionId());
			sb.append(patcherFix.getName());

			patcherFix.setKey(
				DigesterUtil.digestHex(StringUtil.toUpperCase(sb.toString())));

			PatcherFixLocalServiceUtil.updatePatcherFix(patcherFix);
		}

		runSQL(
			"create unique index IX_8BFFC3A0 on OSB_PatcherFix " +
				"(patcherFixKey, patcherFixVersion)");
	}

	private void _removePatcherFixDuplicates() throws Exception {
		for (long patcherFixId : _PATCHER_FIX_IDS) {
			runSQL(
				"delete from OSB_PatcherFix where patcherFixId = " +
					patcherFixId);
		}

		for (long patcherFixRelId : _PATCHER_FIX_REL_IDS) {
			runSQL(
				"delete from OSB_PatcherFixRel where patcherFixId1 = " +
					patcherFixRelId);
		}
	}

	private static final long[] _PATCHER_FIX_IDS = {
		340243, 427375, 427377, 433728, 433732, 434893, 435223, 435270, 446725
	};

	private static final long[] _PATCHER_FIX_REL_IDS = {435223, 435270};

}