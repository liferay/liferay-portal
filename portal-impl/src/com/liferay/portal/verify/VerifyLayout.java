/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Jorge Avalos
 */
public class VerifyLayout extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		_verifyLayoutFriendlyURL();
	}

	private String _getReservedLayoutFriendlyURLS() {
		String reservedLayoutFriendlyURLS = "";

		for (int i = 0; i < PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS.length;
			 i++) {

			String likeClause = PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS[i];

			if (PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS[i].contains(
					StringPool.STAR)) {

				likeClause = StringUtil.replace(
					likeClause, CharPool.STAR, CharPool.PERCENT);
			}

			if (PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS[i].contains("_")) {
				likeClause = StringUtil.replace(
					likeClause, CharPool.UNDERLINE, "!_");
			}

			if (reservedLayoutFriendlyURLS.isEmpty()) {
				reservedLayoutFriendlyURLS += StringBundler.concat(
					"LIKE '/", likeClause, "' ");
			}
			else {
				reservedLayoutFriendlyURLS += StringBundler.concat(
					"OR friendlyURL LIKE '/", likeClause, "' ");
			}

			if (likeClause.contains("!_")) {
				reservedLayoutFriendlyURLS += "ESCAPE \'!\'";
			}
		}

		return reservedLayoutFriendlyURLS;
	}

	private void _verifyLayoutFriendlyURL() throws Exception {
		if (PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS.length == 0) {
			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select plid, friendlyURL from LayoutFriendlyURL where " +
					"friendlyURL " + _getReservedLayoutFriendlyURLS());
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long plid = resultSet.getLong(1);
				String friendlyURL = resultSet.getString(2);

				_log.error(
					StringBundler.concat(
						"Detected reserved friendly URL \"", friendlyURL,
						"\" Update the friendly URL for the layout with PLID ",
						plid, "."));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(VerifyLayout.class);

}