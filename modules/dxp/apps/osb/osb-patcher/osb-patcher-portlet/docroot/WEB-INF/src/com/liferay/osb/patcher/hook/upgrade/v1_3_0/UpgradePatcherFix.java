/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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