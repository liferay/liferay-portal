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

package com.liferay.osb.patcher.hook.upgrade.v1_2_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.model.impl.PatcherFixRelBaseImpl;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixRel extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixRel();
	}

	protected void updatePatcherFixRel() throws Exception {
		if (hasTable("OSB_PatcherFixRel")) {
			return;
		}

		runSQL(PatcherFixRelBaseImpl.TABLE_SQL_CREATE);
	}

}