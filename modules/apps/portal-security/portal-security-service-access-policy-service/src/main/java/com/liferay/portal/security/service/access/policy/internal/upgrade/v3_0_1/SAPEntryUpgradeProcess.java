/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.internal.upgrade.v3_0_1;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * @author Christopher Kian
 */
public class SAPEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		CompanyLocalServiceUtil.forEachCompanyId(
			companyId -> {
				try (PreparedStatement preparedStatement1 =
						connection.prepareStatement(
							StringBundler.concat(
								"select count(*) from SAPEntry where ",
								"companyId = ? and name = ",
								"'SYSTEM_REST_CLIENT_TEMPLATE_OBJECT'"))) {

					preparedStatement1.setLong(1, companyId);

					try (ResultSet resultSet =
							preparedStatement1.executeQuery()) {

						resultSet.next();

						int count = resultSet.getInt(1);

						if (count != 0) {
							return;
						}

						StringBuilder sb = new StringBuilder(5);

						sb.append("insert into SAPEntry (uuid_, sapEntryId, ");
						sb.append("companyId, userId, createDate, ");
						sb.append("modifiedDate, allowedServiceSignatures, ");
						sb.append("defaultSAPEntry, enabled, name, title) ");
						sb.append("values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

						PreparedStatement preparedStatement2 =
							connection.prepareStatement(sb.toString());

						preparedStatement2.setString(
							1, PortalUUIDUtil.generate());

						long sapEntryId = CounterLocalServiceUtil.increment();

						preparedStatement2.setLong(2, sapEntryId);

						preparedStatement2.setLong(3, companyId);
						preparedStatement2.setLong(
							4, UserLocalServiceUtil.getGuestUserId(companyId));

						Timestamp timestamp = new Timestamp(
							System.currentTimeMillis());

						preparedStatement2.setTimestamp(5, timestamp);
						preparedStatement2.setTimestamp(6, timestamp);

						preparedStatement2.setString(7, StringPool.STAR);
						preparedStatement2.setBoolean(8, false);
						preparedStatement2.setBoolean(9, true);
						preparedStatement2.setString(
							10, "SYSTEM_REST_CLIENT_TEMPLATE_OBJECT");

						String title =
							"System Service Access Policy for REST Client " +
								"Template Requests";

						preparedStatement2.setString(
							11,
							LocalizationUtil.updateLocalization(
								HashMapBuilder.put(
									LocaleUtil.fromLanguageId(
										UpgradeProcessUtil.getDefaultLanguageId(
											companyId)),
									title
								).build(),
								title, "Title",
								UpgradeProcessUtil.getDefaultLanguageId(
									companyId)));

						preparedStatement2.execute();

						Role guestRole = RoleLocalServiceUtil.getRole(
							companyId, RoleConstants.GUEST);

						ResourcePermissionLocalServiceUtil.
							setResourcePermissions(
								companyId, SAPEntry.class.getName(),
								ResourceConstants.SCOPE_INDIVIDUAL,
								String.valueOf(sapEntryId),
								guestRole.getRoleId(),
								new String[] {ActionKeys.VIEW});
					}
				}
			});
	}

}