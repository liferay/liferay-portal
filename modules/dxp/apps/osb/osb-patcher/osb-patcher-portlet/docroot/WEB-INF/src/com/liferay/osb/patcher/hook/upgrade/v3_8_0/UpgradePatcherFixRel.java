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

package com.liferay.osb.patcher.hook.upgrade.v3_8_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Eddie Olson
 */
public class UpgradePatcherFixRel extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePatcherFixRel();
	}

	protected void updatePatcherFixRel() throws Exception {
		runSQL(
			"delete from OSB_PatcherFixRel where childPatcherFixId in " +
				"(3035339, 3060674, 3060747, 3066188, 3289124)");
	}

}