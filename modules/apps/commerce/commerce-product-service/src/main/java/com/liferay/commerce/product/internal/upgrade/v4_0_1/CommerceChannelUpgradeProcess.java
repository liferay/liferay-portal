/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.upgrade.v4_0_1;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Riccardo Ferrari
 */
public class CommerceChannelUpgradeProcess extends UpgradeProcess {

	public CommerceChannelUpgradeProcess(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select CommerceChannel.siteGroupId, Group_.groupId, ",
					"Group_.typeSettings from CommerceChannel inner join ",
					"Group_ on CommerceChannel.commerceChannelId = Group_.",
					"classPK and Group_.classNameId = ?"))) {

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

					_updateGroupTypeSetting(
						resultSet.getLong(2), siteGroupId,
						resultSet.getString(3));
				}
			}
		}
	}

	private void _updateGroupTypeSetting(
			long groupId, long siteGroupId, String typeSettings)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.create(
				true
			).fastLoad(
				typeSettings
			).setProperty(
				"siteGroupId", String.valueOf(siteGroupId)
			).build();

		_groupLocalService.updateGroup(
			groupId, typeSettingsUnicodeProperties.toString());
	}

	private final GroupLocalService _groupLocalService;

}