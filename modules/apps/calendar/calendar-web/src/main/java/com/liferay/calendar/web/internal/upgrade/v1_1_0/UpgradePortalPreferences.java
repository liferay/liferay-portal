/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.upgrade.v1_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.PortalPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Bryan Engler
 */
public class UpgradePortalPreferences extends PortalPreferencesUpgradeProcess {

	public UpgradePortalPreferences() {
		_preferenceNamesMap.put(
			_NAMESPACE_OLD_SESSION_CLICKS +
				"calendar-portlet-column-options-visible",
			_NAMESPACE_NEW_SESSION_CLICKS +
				"com.liferay.calendar.web_columnOptionsVisible");
		_preferenceNamesMap.put(
			_NAMESPACE_OLD_SESSION_CLICKS + "calendar-portlet-default-view",
			_NAMESPACE_NEW_SESSION_CLICKS +
				"com.liferay.calendar.web_defaultView");
		_preferenceNamesMap.put(
			_NAMESPACE_OLD_SESSION_CLICKS + "calendar-portlet-other-calendars",
			_NAMESPACE_NEW_SESSION_CLICKS +
				"com.liferay.calendar.web_otherCalendars");
	}

	@Override
	protected void doUpgrade() throws Exception {
		_populatePreferenceNamesMap();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select oldPortalPreferenceValue.portalPreferenceValueId, ",
					"oldPortalPreferenceValue.key_ as oldKey, ",
					"newPortalPreferenceValue.key_ as newKey from ",
					"PortalPreferenceValue oldPortalPreferenceValue inner ",
					"join PortalPreferenceValue newPortalPreferenceValue on ",
					"oldPortalPreferenceValue.companyId = ",
					"newPortalPreferenceValue.companyId and ",
					"oldPortalPreferenceValue.portalPreferencesId = ",
					"newPortalPreferenceValue.portalPreferencesId and ",
					"oldPortalPreferenceValue.index_ = ",
					"newPortalPreferenceValue.index_ where ",
					"oldPortalPreferenceValue.key_ like 'calendar-%' and ",
					"oldPortalPreferenceValue.namespace = ",
					"'com.liferay.portal.util.SessionClicks' and ",
					"newPortalPreferenceValue.key_ like ",
					"'com.liferay.calendar.web%' and ",
					"newPortalPreferenceValue.namespace = ",
					"'com.liferay.portal.kernel.util.SessionClicks'"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"delete from PortalPreferenceValue where " +
						"portalPreferenceValueId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				if (!StringUtil.equals(
						_preferenceNamesMap.get(
							_NAMESPACE_OLD_SESSION_CLICKS +
								resultSet.getString("oldKey")),
						_NAMESPACE_NEW_SESSION_CLICKS +
							resultSet.getString("newKey"))) {

					continue;
				}

				preparedStatement2.setLong(
					1, resultSet.getLong("portalPreferenceValueId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}

		super.doUpgrade();
	}

	@Override
	protected Map<String, String> getPreferenceNamesMap() {
		return _preferenceNamesMap;
	}

	private String _getNewPreferenceName(String preferenceName) {
		for (Pattern pattern : _oldPreferencePatterns) {
			Matcher matcher = pattern.matcher(preferenceName);

			if (matcher.matches()) {
				Matcher idMatcher = _idPattern.matcher(preferenceName);
				Matcher preferenceMatcher = _preferencePattern.matcher(
					preferenceName);

				idMatcher.find();
				preferenceMatcher.find();

				String id = idMatcher.group(0);
				String preference = preferenceMatcher.group(0);

				return StringUtil.replace(
					_newPreferencePatternsMap.get(preference), "{calendarId}",
					id);
			}
		}

		return null;
	}

	private void _populatePreferenceNamesMap() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select key_ from PortalPreferenceValue where namespace = ",
					"'com.liferay.portal.util.SessionClicks' and key_ like ",
					"'calendar-%'"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String preferenceName =
					_NAMESPACE_OLD_SESSION_CLICKS + resultSet.getString("key_");

				String newPreferenceName = null;

				if (!_preferenceNamesMap.containsKey(preferenceName)) {
					newPreferenceName = _getNewPreferenceName(preferenceName);
				}

				if (newPreferenceName == null) {
					continue;
				}

				_preferenceNamesMap.put(preferenceName, newPreferenceName);
			}
		}
	}

	private static final String _NAMESPACE_NEW_SESSION_CLICKS =
		"com.liferay.portal.kernel.util.SessionClicks#";

	private static final String _NAMESPACE_OLD_SESSION_CLICKS =
		"com.liferay.portal.util.SessionClicks#";

	private static final Pattern _idPattern = Pattern.compile("[0-9]+");
	private static final Map<String, String> _newPreferencePatternsMap =
		HashMapBuilder.put(
			"color",
			_NAMESPACE_NEW_SESSION_CLICKS +
				"com.liferay.calendar.web_calendar{calendarId}Color"
		).put(
			"visible",
			_NAMESPACE_NEW_SESSION_CLICKS +
				"com.liferay.calendar.web_calendar{calendarId}Visible"
		).build();
	private static final Pattern[] _oldPreferencePatterns = {
		Pattern.compile(
			_NAMESPACE_OLD_SESSION_CLICKS +
				"calendar-portlet-calendar-[0-9]+-color"),
		Pattern.compile(
			_NAMESPACE_OLD_SESSION_CLICKS +
				"calendar-portlet-calendar-[0-9]+-visible")
	};
	private static final Pattern _preferencePattern = Pattern.compile(
		"[a-z]+$");

	private final Map<String, String> _preferenceNamesMap = new HashMap<>();

}