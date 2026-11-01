/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.upgrade.v1_4_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortalUtil;

import java.sql.PreparedStatement;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			processConcurrently(
				StringBundler.concat(
					"select LayoutUtilityPageEntry.externalReferenceCode, ",
					"Layout.plid, Layout.classPK from LayoutUtilityPageEntry ",
					"inner join Layout on Layout.classNameId = ",
					PortalUtil.getClassNameId(Layout.class),
					" and Layout.classPK = LayoutUtilityPageEntry.plid where ",
					"LayoutUtilityPageEntry.externalReferenceCode like ",
					"'LFR-%'"),
				"update Layout set externalReferenceCode = ? where plid = ?",
				resultSet -> new Object[] {
					resultSet.getString("externalReferenceCode"),
					resultSet.getLong("plid"), resultSet.getLong("classPK")
				},
				(values, preparedStatement) -> {
					String externalReferenceCode = (String)values[0];
					long classPK = (Long)values[2];

					String layoutExternalReferenceCode =
						externalReferenceCode + "-layout";

					preparedStatement.setString(1, layoutExternalReferenceCode);

					preparedStatement.setLong(2, classPK);

					preparedStatement.addBatch();

					_updateSegmentsExperienceExternalReferenceCode(
						layoutExternalReferenceCode, classPK);

					long plid = (Long)values[1];

					String draftLayoutExternalReferenceCode =
						layoutExternalReferenceCode +
							LayoutConstants.
								EXTERNAL_REFERENCE_CODE_SUFFIX_DRAFT;

					preparedStatement.setString(
						1, draftLayoutExternalReferenceCode);

					preparedStatement.setLong(2, plid);

					preparedStatement.addBatch();

					_updateSegmentsExperienceExternalReferenceCode(
						draftLayoutExternalReferenceCode, plid);
				},
				null);
		}
	}

	private void _updateSegmentsExperienceExternalReferenceCode(
		String externalReferenceCode, long plid) {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update SegmentsExperience set externalReferenceCode = ? " +
					"where plid = ? and segmentsExperienceKey = 'DEFAULT'")) {

			preparedStatement.setString(
				1,
				externalReferenceCode +
					LayoutConstants.EXTERNAL_REFERENCE_CODE_SUFFIX_DEFAULT);
			preparedStatement.setLong(2, plid);

			preparedStatement.executeUpdate();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcess.class);

}