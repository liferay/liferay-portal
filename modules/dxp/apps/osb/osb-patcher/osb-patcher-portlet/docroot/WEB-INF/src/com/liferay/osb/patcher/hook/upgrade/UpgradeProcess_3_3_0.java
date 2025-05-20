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

package com.liferay.osb.patcher.hook.upgrade;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.osb.patcher.hook.upgrade.v3_3_0.UpgradePatcherBuild;

/**
 * @author Johnny Duong
 */
public class UpgradeProcess_3_3_0 extends UpgradeProcess {

	@Override
	public int getThreshold() {
		return 330;
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(UpgradePatcherBuild.class);
	}

}