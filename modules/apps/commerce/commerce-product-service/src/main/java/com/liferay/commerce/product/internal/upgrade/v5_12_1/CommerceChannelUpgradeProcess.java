/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v5_12_1;

import com.liferay.account.settings.AccountEntryGroupSettings;
import com.liferay.commerce.configuration.CommerceAccountGroupServiceConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.util.AccountEntryAllowedTypesUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Stefano Motta
 */
public class CommerceChannelUpgradeProcess extends UpgradeProcess {

	public CommerceChannelUpgradeProcess(
		AccountEntryGroupSettings accountEntryGroupSettings,
		ConfigurationProvider configurationProvider) {

		_accountEntryGroupSettings = accountEntryGroupSettings;
		_configurationProvider = configurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CommerceChannel.siteGroupId, Group_.groupId from ",
					"CommerceChannel inner join Group_ on ",
					"CommerceChannel.commerceChannelId = Group_.classPK and ",
					"Group_.classNameId = ?"))) {

			preparedStatement.setLong(
				1,
				ClassNameLocalServiceUtil.getClassNameId(
					CommerceChannel.class));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long siteGroupId = resultSet.getLong(1);

					if (siteGroupId == 0) {
						continue;
					}

					long groupId = resultSet.getLong(2);

					_accountEntryGroupSettings.setAllowedTypes(
						siteGroupId, _getAllowedTypes(groupId));
				}
			}
		}
	}

	private String[] _getAllowedTypes(long commerceChannelGroupId)
		throws ConfigurationException {

		CommerceAccountGroupServiceConfiguration
			commerceAccountGroupServiceConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountGroupServiceConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannelGroupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT));

		return AccountEntryAllowedTypesUtil.getAllowedTypes(
			commerceAccountGroupServiceConfiguration.commerceSiteType());
	}

	private final AccountEntryGroupSettings _accountEntryGroupSettings;
	private final ConfigurationProvider _configurationProvider;

}