/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import java.util.Objects;

/**
 * @author Alberto Chaparro
 */
public class SegmentsExperienceUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			StringBundler.concat(
				"select SegmentsExperience.ctCollectionId, ",
				"SegmentsExperience.externalReferenceCode, ",
				"SegmentsExperience.segmentsExperienceId, ",
				"Layout.externalReferenceCode from SegmentsExperience inner ",
				"join Layout on Layout.ctCollectionId = ",
				"SegmentsExperience.ctCollectionId and Layout.plid = ",
				"SegmentsExperience.plid where ",
				"SegmentsExperience.segmentsExperienceKey = '",
				SegmentsExperienceConstants.KEY_DEFAULT,
				"' and Layout.externalReferenceCode is not null"),
			"update SegmentsExperience set externalReferenceCode = ? where " +
				"ctCollectionId = ? and segmentsExperienceId = ?",
			resultSet -> new Object[] {
				resultSet.getLong(1), resultSet.getString(2),
				resultSet.getLong(3), resultSet.getString(4)
			},
			(values, preparedStatement) -> {
				String externalReferenceCode =
					values[3] +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT;

				if (Objects.equals(externalReferenceCode, values[1])) {
					return;
				}

				preparedStatement.setString(1, externalReferenceCode);
				preparedStatement.setLong(2, (long)values[0]);
				preparedStatement.setLong(3, (long)values[2]);

				preparedStatement.addBatch();
			},
			null);
	}

}