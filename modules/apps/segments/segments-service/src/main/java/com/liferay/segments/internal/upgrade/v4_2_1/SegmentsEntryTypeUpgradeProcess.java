/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_2_1;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.sql.PreparedStatement;

/**
 * @author Marcos Martins
 */
public class SegmentsEntryTypeUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateType(
			SegmentsEntryConstants.TYPE_BATCH,
			" and source = '" +
				SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND + "'");

		_updateType(SegmentsEntryConstants.TYPE_DEFAULT, "");
	}

	private void _updateType(String type, String whereClause) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update SegmentsEntry set type_ = ? where type_ is null" +
					whereClause)) {

			preparedStatement.setString(1, type);

			preparedStatement.executeUpdate();
		}
	}

}