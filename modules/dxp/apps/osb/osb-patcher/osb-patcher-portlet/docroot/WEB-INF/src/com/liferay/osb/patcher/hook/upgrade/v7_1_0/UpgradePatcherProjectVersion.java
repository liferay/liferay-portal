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

package com.liferay.osb.patcher.hook.upgrade.v7_1_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Johnny Duong
 */
public class UpgradePatcherProjectVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!tableHasColumn("OSB_PatcherProjectVersion", "combinedBranch")) {
			return;
		}

		try {
			StringBundler sb = new StringBundler(6);

			sb.append("update OSB_PatcherProjectVersion set combinedBranch = ");
			sb.append("1 where patcherProductVersionId = ");
			sb.append(_PATCHER_PRODUCT_VERSION_ID_PORTAL_6X);
			sb.append(" or patcherProjectVersionId in (");
			sb.append(StringUtil.merge(_PATCHER_PROJECT_VERSION_IDS_PRE_DE_28));
			sb.append(")");

			runSQL(sb.toString());
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final long _PATCHER_PRODUCT_VERSION_ID_PORTAL_6X = 101625503;

	private static final long[] _PATCHER_PROJECT_VERSION_IDS_PRE_DE_28 = {
		47145401, 46916878, 46916919, 46916944, 46916965, 46051356, 46916998,
		47173623, 50199234, 51523085, 53998128, 57117473, 58468695, 61225566,
		63872434, 64400369, 65107246, 65642048, 66257684, 66874566, 67478739,
		68175109, 69400748, 70023287, 70632186, 71100904, 72027665, 72374230
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProjectVersion.class);

}