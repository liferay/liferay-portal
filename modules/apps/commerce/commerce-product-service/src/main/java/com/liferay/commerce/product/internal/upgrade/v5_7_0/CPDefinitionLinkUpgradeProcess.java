/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v5_7_0;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Danny Situ
 */
public class CPDefinitionLinkUpgradeProcess extends UpgradeProcess {

	public CPDefinitionLinkUpgradeProcess(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CPDefinitionLink");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long cpDefinitionLinkId = resultSet.getLong(
					"CPDefinitionLinkId");

				AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
					"com.liferay.commerce.product.model.CPDefinitionLink",
					cpDefinitionLinkId);

				if (assetEntry != null) {
					continue;
				}

				long userId = resultSet.getLong("userId");
				long groupId = resultSet.getLong("groupId");
				Date date = new Date(System.currentTimeMillis());
				long cProductId = resultSet.getLong("CProductId");

				_assetEntryLocalService.updateEntry(
					userId, groupId, date, date,
					"com.liferay.commerce.product.model.CPDefinitionLink",
					cpDefinitionLinkId, null, 0, new long[0], new String[0],
					true, true, null, null, null, null, ContentTypes.TEXT_PLAIN,
					_getNameMapAsXML(cProductId),
					_getDescriptionMapAsXML(cProductId), null, null, null, 0, 0,
					null);
			}
		}
	}

	private long _getCPDefinitionId(long cProductId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select publishedCPDefinitionId from CProduct where " +
					"CProductId = " + cProductId);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getLong(1);
			}
		}

		return 0;
	}

	private String _getDefaultLanguageId(long cpDefinitionId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select defaultLanguageId from CPDefinition where " +
					"CPDefinitionId = ?")) {

			preparedStatement.setLong(1, cpDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					String defaultLanguageId = resultSet.getString(1);

					if (defaultLanguageId != null) {
						return defaultLanguageId;
					}
				}
			}
		}

		return StringPool.BLANK;
	}

	private String _getDescriptionMapAsXML(long cProductId) throws Exception {
		long cpDefinitionId = _getCPDefinitionId(cProductId);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CPDefinitionLocalization where CPDefinitionId " +
					"= ?")) {

			preparedStatement.setLong(1, cpDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Map<String, String> languageIdToNameMap = new HashMap<>();

				if (resultSet.next()) {
					languageIdToNameMap.put(
						resultSet.getString("languageId"),
						GetterUtil.getString(
							resultSet.getString("description")));
				}

				return LocalizationUtil.getXml(
					languageIdToNameMap, _getDefaultLanguageId(cpDefinitionId),
					"Description");
			}
		}
	}

	private String _getNameMapAsXML(long cProductId) throws Exception {
		long cpDefinitionId = _getCPDefinitionId(cProductId);

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CPDefinitionLocalization where CPDefinitionId " +
					"= ?")) {

			preparedStatement.setLong(1, cpDefinitionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Map<String, String> languageIdToNameMap = new HashMap<>();

				if (resultSet.next()) {
					languageIdToNameMap.put(
						resultSet.getString("languageId"),
						GetterUtil.getString(resultSet.getString("name")));
				}

				return LocalizationUtil.getXml(
					languageIdToNameMap, _getDefaultLanguageId(cpDefinitionId),
					"Name");
			}
		}
	}

	private final AssetEntryLocalService _assetEntryLocalService;

}