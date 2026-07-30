/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v4_1_1;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.constants.SegmentsExperienceConstants;

/**
 * @author Alberto Chaparro
 */
public class SegmentsExperienceUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		processConcurrently(
			StringBundler.concat(
				"select SegmentsExperience.ctCollectionId, ",
				"SegmentsExperience.segmentsExperienceId, ",
				"Layout.externalReferenceCode from SegmentsExperience inner ",
				"join Layout on Layout.ctCollectionId = ",
				"SegmentsExperience.ctCollectionId and Layout.plid = ",
				"SegmentsExperience.plid where ",
				"SegmentsExperience.segmentsExperienceKey = '",
				SegmentsExperienceConstants.KEY_DEFAULT,
				"' and Layout.externalReferenceCode is not null and ",
				"(SegmentsExperience.externalReferenceCode is null or ",
				"SegmentsExperience.externalReferenceCode != ",
				"CONCAT(Layout.externalReferenceCode, '",
				LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT, "'))"),
			"update SegmentsExperience set externalReferenceCode = ? where " +
				"ctCollectionId = ? and segmentsExperienceId = ?",
			resultSet -> new Object[] {
				resultSet.getLong(1), resultSet.getLong(2),
				resultSet.getString(3)
			},
			(values, preparedStatement) -> {
				preparedStatement.setString(
					1,
					values[2] +
						LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT);
				preparedStatement.setLong(2, (long)values[0]);
				preparedStatement.setLong(3, (long)values[1]);

				preparedStatement.addBatch();
			},
			null);
	}

}