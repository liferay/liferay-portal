/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.internal.upgrade.v1_6_0;

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.sql.PreparedStatement;

import java.util.Map;

/**
 * @author Michele Vigilante
 */
public class CommerceCurrencyExternalReferenceCodeUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update CommerceCurrency set externalReferenceCode = ? " +
						"where code_ = ?")) {

			for (Map.Entry<String, String> entry :
					_externalReferenceCodes.entrySet()) {

				preparedStatement.setString(1, entry.getValue());
				preparedStatement.setString(2, entry.getKey());

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private static final Map<String, String> _externalReferenceCodes =
		HashMapBuilder.put(
			"AUD", "AUSTRALIAN_DOLLAR"
		).put(
			"BRL", "BRAZILIAN_REAL"
		).put(
			"CAD", "CANADIAN_DOLLAR"
		).put(
			"CNY", "CHINESE_YUAN_RENMINBI"
		).put(
			"EUR", "EURO"
		).put(
			"GBP", "BRITISH_POUND"
		).put(
			"HKD", "HONG_KONG_DOLLAR"
		).put(
			"INR", "INDIAN_RUPEE"
		).put(
			"JPY", "JAPANESE_YEN"
		).put(
			"USD", "US_DOLLAR"
		).build();

}