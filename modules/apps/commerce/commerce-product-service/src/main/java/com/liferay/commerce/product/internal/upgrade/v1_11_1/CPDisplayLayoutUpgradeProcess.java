/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v1_11_1;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Riccardo Ferrari
 */
public class CPDisplayLayoutUpgradeProcess extends UpgradeProcess {

	public CPDisplayLayoutUpgradeProcess(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (Statement s = connection.createStatement(
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			ResultSet resultSet = s.executeQuery(_SELECT_CPDISPLAYLAYOUT_SQL)) {

			while (resultSet.next()) {
				long groupId = resultSet.getLong("groupId");

				String layoutUuid = resultSet.getString("layoutUuid");

				Layout layout = _fetchLayout(groupId, layoutUuid);

				if (layout != null) {
					continue;
				}

				long cpDisplayLayoutId = resultSet.getLong("CPDisplayLayoutId");

				if (_log.isDebugEnabled()) {
					_log.debug(
						String.format(
							"Removing CPDisplayLayout entry id: %s",
							cpDisplayLayoutId));
				}

				runSQL(
					String.format(
						_DELETE_CPDISPLAYLAYOUT_SQL, cpDisplayLayoutId));
			}
		}
	}

	private Layout _fetchLayout(long groupId, String layoutUuid) {
		Layout layout = _layoutLocalService.fetchLayout(
			layoutUuid, groupId, false);

		if (layout != null) {
			return layout;
		}

		return _layoutLocalService.fetchLayout(layoutUuid, groupId, true);
	}

	private static final String _DELETE_CPDISPLAYLAYOUT_SQL =
		"DELETE FROM CPDisplayLayout where CPDisplayLayoutId = '%s'";

	private static final String _SELECT_CPDISPLAYLAYOUT_SQL =
		"SELECT CPDisplayLayoutId, groupId, layoutUuid FROM CPDisplayLayout";

	private static final Log _log = LogFactoryUtil.getLog(
		CPDisplayLayoutUpgradeProcess.class);

	private final LayoutLocalService _layoutLocalService;

}