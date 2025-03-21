/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_0_0;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portletdisplaytemplate.PortletDisplayTemplateManager;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eduardo García
 */
public class UpgradePortletDisplayTemplatePreferences
	extends BasePortletPreferencesUpgradeProcess {

	protected long getCompanyGroupId(long companyId) throws Exception {
		Long companyGroupId = _companyGroupIds.get(companyId);

		if (companyGroupId != null) {
			return companyGroupId;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId from Group_ where classNameId = ? and " +
					"classPK = ?")) {

			preparedStatement.setLong(
				1, PortalUtil.getClassNameId(Company.class));
			preparedStatement.setLong(2, companyId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					companyGroupId = resultSet.getLong("groupId");
				}
				else {
					companyGroupId = 0L;
				}

				_companyGroupIds.put(companyId, companyGroupId);

				return companyGroupId;
			}
		}
	}

	protected ObjectValuePair<Long, String> getTemplateGroupAndKey(
			long displayStyleGroupId, String displayStyle)
		throws Exception {

		String uuid = displayStyle.substring(DISPLAY_STYLE_PREFIX_6_2.length());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, templateKey from DDMTemplate where (groupId " +
					"= ? or groupId = ?) and uuid_ = ?")) {

			preparedStatement.setLong(1, displayStyleGroupId);
			preparedStatement.setLong(2, _companyGroupId);
			preparedStatement.setString(3, uuid);

			ObjectValuePair<Long, String> objectValuePair = null;

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");

					objectValuePair = new ObjectValuePair<>(
						groupId, resultSet.getString("templateKey"));

					if (groupId == displayStyleGroupId) {
						return objectValuePair;
					}
				}
			}

			return objectValuePair;
		}
	}

	@Override
	protected String getUpdatePortletPreferencesWhereClause() {
		return UPDATE_PORTLET_PREFERENCES_WHERE_CLAUSE;
	}

	protected void upgradeDisplayStyle(PortletPreferences portletPreferences)
		throws Exception {

		String displayStyle = GetterUtil.getString(
			portletPreferences.getValue("displayStyle", null));

		if (Validator.isNull(displayStyle) ||
			!displayStyle.startsWith(DISPLAY_STYLE_PREFIX_6_2)) {

			return;
		}

		long displayStyleGroupId = GetterUtil.getLong(
			portletPreferences.getValue("displayStyleGroupId", null));

		ObjectValuePair<Long, String> objectValuePair = getTemplateGroupAndKey(
			displayStyleGroupId, displayStyle);

		if (objectValuePair != null) {
			portletPreferences.setValue(
				"displayStyleGroupId",
				String.valueOf(objectValuePair.getKey()));
			portletPreferences.setValue(
				"displayStyle",
				PortletDisplayTemplateManager.DISPLAY_STYLE_PREFIX +
					objectValuePair.getValue());
		}
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		_companyGroupId = getCompanyGroupId(companyId);

		upgradeDisplayStyle(portletPreferences);

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	protected static final String DISPLAY_STYLE_PREFIX_6_2 = "ddmTemplate_";

	protected static final String UPDATE_PORTLET_PREFERENCES_WHERE_CLAUSE =
		"(preferences like '%" + DISPLAY_STYLE_PREFIX_6_2 + "%')";

	private long _companyGroupId = 0L;
	private final Map<Long, Long> _companyGroupIds = new HashMap<>();

}