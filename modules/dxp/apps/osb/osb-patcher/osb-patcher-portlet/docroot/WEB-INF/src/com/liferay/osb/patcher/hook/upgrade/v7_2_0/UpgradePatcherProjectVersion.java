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

package com.liferay.osb.patcher.hook.upgrade.v7_2_0;

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
		if (!tableHasColumn("OSB_PatcherProjectVersion", "hide")) {
			return;
		}

		try {
			StringBundler sb = new StringBundler(5);

			sb.append("update OSB_PatcherProjectVersion set hide = 1 where ");
			sb.append("committish like '%-private' and combinedBranch = 0 ");
			sb.append("and patcherProductVersionId in (");
			sb.append(StringUtil.merge(PUBLIC_MP_PRODUCT_VERSION_IDS));
			sb.append(")");

			runSQL(sb.toString());

			StringBundler sb2 = new StringBundler(8);

			sb2.append("update OSB_PatcherProjectVersion set hide = 1 where ");
			sb2.append("committish not like '%-private' and combinedBranch = ");
			sb2.append("0 and patcherProductVersionId in (");
			sb2.append(StringUtil.merge(PRIVATE_MP_PRODUCT_VERSION_IDS));
			sb2.append(")");

			runSQL(sb2.toString());
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private static final long[] PRIVATE_MP_PRODUCT_VERSION_IDS = {
		118007811, 118007812, 118007817, 118007843, 118007845, 118107331,
		118107337, 118107342, 118107350, 118107364, 118107370, 118107389,
		118107395, 118107408, 118107410, 118107415, 118107421, 118107422,
		118107428
	};

	private static final long[] PUBLIC_MP_PRODUCT_VERSION_IDS = {
		118007818, 118007823, 118007824, 118007830, 118007831, 118007836,
		118007837, 118007838, 118007844, 118007850, 118007851, 118007856,
		118107330, 118107336, 118107343, 118107344, 118107349, 118107351,
		118107356, 118107357, 118107362, 118107363, 118107369, 118107375,
		118107376, 118107377, 118107382, 118107383, 118107384, 118107390,
		118107397, 118107402, 118107403, 118107409, 118107416, 118107423,
		119478738, 119838027, 119839350, 119839816, 119923682, 119924817
	};

	private static Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProjectVersion.class);

}