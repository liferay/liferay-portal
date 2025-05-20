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
import com.liferay.osb.patcher.hook.upgrade.v2_6_0.UpgradePatcherBuild;
import com.liferay.osb.patcher.hook.upgrade.v2_6_0.UpgradePatcherFix;
import com.liferay.osb.patcher.hook.upgrade.v2_6_0.UpgradePatcherPortalVersion;

/**
 * @author Johnny Duong
 * @author Eddie Olson
 */
public class UpgradeProcess_2_6_0 extends UpgradeProcess {

	@Override
	public int getThreshold() {
		return 260;
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(UpgradePatcherBuild.class);
		upgrade(UpgradePatcherFix.class);
		upgrade(UpgradePatcherPortalVersion.class);
	}

}