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

package com.liferay.osb.patcher.hook.upgrade.v6_6_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringBundler;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherProductVersion extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!tableHasColumn("OSB_PatcherProductVersion", "fixDeliveryMethod")) {
			return;
		}

		try {
			runSQL(
				"update OSB_PatcherProductVersion set fixDeliveryMethod = " +
					_TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20 + " where name = '" +
						_NAME_PRODUCT_VERSION_6X_PORTAL + "'");

			StringBundler sb = new StringBundler(8);

			sb.append("update OSB_PatcherProductVersion set ");
			sb.append("fixDeliveryMethod = ");
			sb.append(_TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30);
			sb.append(" where name in ('");
			sb.append(_NAME_PRODUCT_VERSION_70_PORTAL);
			sb.append("', '");
			sb.append(_NAME_PRODUCT_VERSION_71_PORTAL);
			sb.append("')");

			runSQL(sb.toString());
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final String _NAME_PRODUCT_VERSION_6X_PORTAL = "Portal 6.x";

	private static final String _NAME_PRODUCT_VERSION_70_PORTAL = "DXP 7.0 DE";

	private static final String _NAME_PRODUCT_VERSION_71_PORTAL = "DXP 7.1 DE";

	private static final String _TYPE_FIX_DELIVERY_METHOD_FIX_PACK_20 = "1";

	private static final String _TYPE_FIX_DELIVERY_METHOD_FIX_PACK_30 = "2";

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherProductVersion.class);

}