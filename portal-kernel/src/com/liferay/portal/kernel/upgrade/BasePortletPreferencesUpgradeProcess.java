/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.upgrade;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.simple.Element;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 */
public abstract class BasePortletPreferencesUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		updatePortletPreferences();
	}

	protected long getCompanyId(String sql, long primaryKey) throws Exception {
		long companyId = 0;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setLong(1, primaryKey);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					companyId = resultSet.getLong("companyId");
				}
			}
		}

		return companyId;
	}

	protected Object[] getGroup(long groupId) throws Exception {
		Object[] group = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select companyId from Group_ where groupId = ?")) {

			preparedStatement.setLong(1, groupId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long companyId = resultSet.getLong("companyId");

					group = new Object[] {groupId, companyId};
				}
			}
		}

		return group;
	}

	protected Object[] getLayout(long plid) throws Exception {
		Object[] layout = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, companyId, privateLayout, layoutId from " +
					"Layout where plid = ?")) {

			preparedStatement.setLong(1, plid);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					long companyId = resultSet.getLong("companyId");
					boolean privateLayout = resultSet.getBoolean(
						"privateLayout");
					long layoutId = resultSet.getLong("layoutId");

					layout = new Object[] {
						groupId, companyId, privateLayout, layoutId
					};
				}
			}
		}

		if (layout == null) {
			layout = getLayoutRevision(plid);
		}

		return layout;
	}

	protected Object[] getLayoutRevision(long layoutRevisionId)
		throws Exception {

		Object[] layoutRevision = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select groupId, companyId, privateLayout, layoutRevisionId " +
					"from LayoutRevision where layoutRevisionId = ?")) {

			preparedStatement.setLong(1, layoutRevisionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					long groupId = resultSet.getLong("groupId");
					long companyId = resultSet.getLong("companyId");
					boolean privateLayout = resultSet.getBoolean(
						"privateLayout");
					long layoutId = resultSet.getLong("layoutRevisionId");

					layoutRevision = new Object[] {
						groupId, companyId, privateLayout, layoutId
					};
				}
			}
		}

		return layoutRevision;
	}

	protected String getLayoutUuid(long plid, long layoutId) throws Exception {
		Object[] layout = getLayout(plid);

		if (layout == null) {
			return null;
		}

		String uuid = null;

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select uuid_ from Layout where groupId = ? and " +
					"privateLayout = ? and layoutId = ?")) {

			long groupId = (Long)layout[0];
			boolean privateLayout = (Boolean)layout[2];

			preparedStatement.setLong(1, groupId);
			preparedStatement.setBoolean(2, privateLayout);
			preparedStatement.setLong(3, layoutId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					uuid = resultSet.getString("uuid_");
				}
			}
		}

		return uuid;
	}

	protected String[] getPortletIds() {
		return new String[0];
	}

	protected String getUpdatePortletPreferencesWhereClause() {
		String[] portletIds = getPortletIds();

		if (portletIds.length == 0) {
			throw new IllegalArgumentException(
				"Subclasses must override getPortletIds or " +
					"getUpdatePortletPreferencesWhereClause");
		}

		StringBundler sb = new StringBundler((portletIds.length * 5) - 1);

		for (int i = 0; i < portletIds.length; i++) {
			String portletId = portletIds[i];

			sb.append("PortletPreferences.portletId ");

			if (portletId.contains(StringPool.PERCENT)) {
				sb.append(" like '");
				sb.append(portletId);
				sb.append("'");
			}
			else {
				sb.append(" = '");
				sb.append(portletId);
				sb.append("'");
			}

			if ((i + 1) < portletIds.length) {
				sb.append(" or ");
			}
		}

		return sb.toString();
	}

	protected void updatePortletPreferences() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			if (hasColumn("PortletPreferences", "preferences")) {
				_updatePortletPreferences();
			}
			else {
				_updatePortletPreferenceValues();
			}
		}
	}

	protected void upgradeMultiValuePreference(
			PortletPreferences portletPreferences, String key)
		throws ReadOnlyException {

		String value = portletPreferences.getValue(key, StringPool.BLANK);

		if (Validator.isNotNull(value)) {
			portletPreferences.setValues(key, StringUtil.split(value));
		}
	}

	protected abstract String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception;

	private void _deletePortletPreferences(
			long portletPreferencesId, boolean deletePortletPreferenceValue)
		throws Exception {

		runSQL(
			"delete from PortletPreferences where portletPreferencesId = " +
				portletPreferencesId);

		if (deletePortletPreferenceValue) {
			runSQL(
				"delete from PortletPreferenceValue where " +
					"portletPreferencesId = " + portletPreferencesId);
		}
	}

	private long _getCompanyId(int ownerType, long ownerId, long plid)
		throws Exception {

		long[] companyIds = PortalInstancePool.getCompanyIds();

		if (companyIds.length == 1) {
			return companyIds[0];
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				_getCompanyIdSelectSQL(ownerType, ownerId, plid))) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("companyId");
				}
			}
		}

		return 0;
	}

	private String _getCompanyIdSelectSQL(
		int ownerType, long ownerId, long plid) {

		String foreignColumnName = null;
		String foreignTableName = null;

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_ARCHIVED) {
			foreignColumnName = "portletItemId";
			foreignTableName = "PortletItem";
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			foreignColumnName = "companyId";
			foreignTableName = "Company";
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_GROUP) {
			foreignColumnName = "groupId";
			foreignTableName = "Group_";
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_LAYOUT) {
			foreignColumnName = "plid";
			foreignTableName = "Layout";
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_ORGANIZATION) {
			foreignColumnName = "organizationId";
			foreignTableName = "Organization_";
		}
		else if (ownerType == PortletKeys.PREFS_OWNER_TYPE_USER) {
			foreignColumnName = "userId";
			foreignTableName = "User_";
		}
		else {
			throw new IllegalArgumentException(
				"Invalid owner type: " + ownerType);
		}

		StringBundler sb = new StringBundler(8);

		sb.append("select companyId from ");
		sb.append(foreignTableName);
		sb.append(" where ");
		sb.append(foreignTableName);
		sb.append(".");
		sb.append(foreignColumnName);
		sb.append(" = ");

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_LAYOUT) {
			sb.append(plid);
		}
		else {
			sb.append(ownerId);
		}

		return sb.toString();
	}

	private Map<String, PreferenceValues> _getPreferenceValuesMap(
			PreparedStatement selectPreparedStatement)
		throws Exception {

		Map<String, PreferenceValues> preferenceValuesMap = new HashMap<>();

		try (ResultSet resultSet = selectPreparedStatement.executeQuery()) {
			while (resultSet.next()) {
				long portletPreferenceValueId = resultSet.getLong(
					"portletPreferenceValueId");
				String name = resultSet.getString("name");

				String value = resultSet.getString("smallValue");

				if (Validator.isBlank(value)) {
					value = resultSet.getString("largeValue");
				}

				boolean readOnly = resultSet.getBoolean("readOnly");

				PreferenceValues preferenceValues =
					preferenceValuesMap.computeIfAbsent(
						name, key -> new PreferenceValues());

				preferenceValues._portletPreferenceValueIds.add(
					portletPreferenceValueId);
				preferenceValues._readOnly |= readOnly;
				preferenceValues._values.add(value);
			}
		}

		return preferenceValuesMap;
	}

	private String _toXMLString(Map<String, PreferenceValues> preferenceMap) {
		if (preferenceMap.isEmpty()) {
			return PortletConstants.DEFAULT_PREFERENCES;
		}

		Element portletPreferencesElement = new Element(
			"portlet-preferences", false);

		for (Map.Entry<String, PreferenceValues> entry :
				preferenceMap.entrySet()) {

			Element preferenceElement = portletPreferencesElement.addElement(
				"preference");

			preferenceElement.addElement("name", entry.getKey());

			PreferenceValues preferenceValues = entry.getValue();

			for (String value : preferenceValues._values) {
				preferenceElement.addElement("value", value);
			}

			if (preferenceValues._readOnly) {
				preferenceElement.addElement("read-only", Boolean.TRUE);
			}
		}

		return portletPreferencesElement.toXMLString();
	}

	private void _updatePortletPreferences() throws Exception {
		StringBundler sb = new StringBundler(5);

		sb.append("select portletPreferencesId, companyId, ownerId, ");
		sb.append("ownerType, plid, portletId, preferences from ");
		sb.append("PortletPreferences");

		String whereClause = getUpdatePortletPreferencesWhereClause();

		if (Validator.isNotNull(whereClause)) {
			sb.append(" where ");
			sb.append(whereClause);
		}

		processConcurrently(
			sb.toString(),
			resultSet -> {
				long portletPreferencesId = resultSet.getLong(
					"portletPreferencesId");
				long companyId = resultSet.getLong("companyId");
				int ownerType = resultSet.getInt("ownerType");
				long plid = resultSet.getLong("plid");
				long ownerId = resultSet.getLong("ownerId");
				String portletId = resultSet.getString("portletId");
				String preferences = resultSet.getString("preferences");

				return new Object[] {
					portletPreferencesId, companyId, ownerType, plid, ownerId,
					portletId, preferences
				};
			},
			values -> _updatePortletPreferences(values),
			"Unable to update PortletPreferences");
	}

	private void _updatePortletPreferences(Object[] values) throws Exception {
		long portletPreferencesId = (Long)values[0];
		long companyId = (Long)values[1];
		int ownerType = (Integer)values[2];
		long plid = (Long)values[3];
		long ownerId = (Long)values[4];

		if (companyId <= 0) {
			companyId = _getCompanyId(ownerType, ownerId, plid);

			if (companyId <= 0) {
				_deletePortletPreferences(portletPreferencesId, false);

				return;
			}

			_updatePortletPreferencesCompanyId(
				companyId, portletPreferencesId, false);
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update PortletPreferences set preferences = ? where " +
					"portletPreferencesId = ?")) {

			String portletId = (String)values[5];
			String preferences = (String)values[6];

			String newPreferences = upgradePreferences(
				companyId, ownerId, ownerType, plid, portletId, preferences);

			if (!preferences.equals(newPreferences)) {
				preparedStatement.setString(1, newPreferences);
				preparedStatement.setLong(2, portletPreferencesId);

				preparedStatement.executeUpdate();
			}
		}
	}

	private void _updatePortletPreferencesCompanyId(
			long companyId, long portletPreferencesId,
			boolean updatePortletPreferenceValue)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"update PortletPreferences set companyId = ? where " +
					"portletPreferencesId = ?")) {

			preparedStatement.setLong(1, companyId);
			preparedStatement.setLong(2, portletPreferencesId);

			preparedStatement.executeUpdate();
		}

		if (updatePortletPreferenceValue) {
			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"update PortletPreferenceValue set companyId = ? " +
							"where portletPreferencesId = ?")) {

				preparedStatement.setLong(1, companyId);
				preparedStatement.setLong(2, portletPreferencesId);

				preparedStatement.executeUpdate();
			}
		}
	}

	private void _updatePortletPreferenceValues() throws Exception {
		StringBundler sb = new StringBundler(5);

		sb.append("select ctCollectionId, portletPreferencesId, companyId, ");
		sb.append("ownerId, ownerType, plid, portletId from ");
		sb.append("PortletPreferences");

		String whereClause = getUpdatePortletPreferencesWhereClause();

		if (Validator.isNotNull(whereClause)) {
			sb.append(" where ");
			sb.append(whereClause);
		}

		processConcurrently(
			sb.toString(),
			resultSet -> {
				long portletPreferencesId = resultSet.getLong(
					"portletPreferencesId");
				long companyId = resultSet.getLong("companyId");
				int ownerType = resultSet.getInt("ownerType");
				long plid = resultSet.getLong("plid");
				long ownerId = resultSet.getLong("ownerId");
				String portletId = resultSet.getString("portletId");
				long ctCollectionId = resultSet.getLong("ctCollectionId");

				return new Object[] {
					portletPreferencesId, companyId, ownerType, plid, ownerId,
					portletId, ctCollectionId
				};
			},
			values -> _updatePortletPreferenceValues(values),
			"Unable to update PortletPreferences and PortletPreferenceValue");
	}

	private void _updatePortletPreferenceValues(Object[] values)
		throws Exception {

		long portletPreferencesId = (Long)values[0];
		long companyId = (Long)values[1];
		int ownerType = (Integer)values[2];
		long plid = (Long)values[3];
		long ownerId = (Long)values[4];

		if (companyId <= 0) {
			companyId = _getCompanyId(ownerType, ownerId, plid);

			if (companyId <= 0) {
				_deletePortletPreferences(portletPreferencesId, true);

				return;
			}

			_updatePortletPreferencesCompanyId(
				companyId, portletPreferencesId, true);
		}

		String portletId = (String)values[5];
		long ctCollectionId = (Long)values[6];

		try (PreparedStatement preparedStatement1 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"select portletPreferenceValueId, largeValue, name, ",
						"readOnly, smallValue from PortletPreferenceValue ",
						"where portletPreferencesId = ? order by index_ asc"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"insert into PortletPreferenceValue (mvccVersion, ",
						"ctCollectionId, portletPreferenceValueId, companyId, ",
						"portletPreferencesId, index_, largeValue, name, ",
						"readOnly, smallValue) values (0, ?, ?, ?, ?, ?, ?, ",
						"?, ?, ?)"));
			PreparedStatement preparedStatement3 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					StringBundler.concat(
						"update PortletPreferenceValue set largeValue = ?, ",
						"readOnly = ?, smallValue = ? where ",
						"portletPreferenceValueId = ?"));
			PreparedStatement preparedStatement4 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"delete from PortletPreferenceValue where " +
						"portletPreferenceValueId = ?")) {

			preparedStatement1.setLong(1, portletPreferencesId);

			Map<String, PreferenceValues> preferenceValuesMap =
				_getPreferenceValuesMap(preparedStatement1);

			String preferences = _toXMLString(preferenceValuesMap);

			String newPreferences = upgradePreferences(
				companyId, ownerId, ownerType, plid, portletId, preferences);

			if (preferences.equals(newPreferences)) {
				return;
			}

			_upgradePortletPreferenceValues(
				preferenceValuesMap, ctCollectionId, portletPreferencesId,
				companyId, newPreferences, preparedStatement2,
				preparedStatement3, preparedStatement4);

			preparedStatement2.executeBatch();

			preparedStatement3.executeBatch();

			preparedStatement4.executeBatch();
		}
	}

	private void _upgradePortletPreferenceValues(
			Map<String, PreferenceValues> preferenceMap, long ctCollectionId,
			long portletPreferencesId, long companyId, String newPreferences,
			PreparedStatement insertPreparedStatement,
			PreparedStatement updatePreparedStatement,
			PreparedStatement deletePreparedStatement)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromDefaultXML(newPreferences);

		Map<String, Map.Entry<PreferenceValues, PreferenceValues>>
			preferenceEntries = new HashMap<>(preferenceMap.size());

		int newCount = 0;

		Map<String, String[]> newPreferenceMap = portletPreferences.getMap();

		for (Map.Entry<String, String[]> entry : newPreferenceMap.entrySet()) {
			String[] values = entry.getValue();

			if (values == null) {
				continue;
			}

			int size = 0;

			String name = entry.getKey();

			PreferenceValues preferenceValues = preferenceMap.remove(name);

			if (preferenceValues != null) {
				size = preferenceValues._values.size();
			}

			if (values.length > size) {
				newCount += values.length - size;
			}

			PreferenceValues newPreferenceValues = new PreferenceValues();

			newPreferenceValues._readOnly = portletPreferences.isReadOnly(
				entry.getKey());

			Collections.addAll(newPreferenceValues._values, values);

			preferenceEntries.put(
				name,
				new AbstractMap.SimpleImmutableEntry<>(
					preferenceValues, newPreferenceValues));
		}

		for (PreferenceValues portletPreferenceValues :
				preferenceMap.values()) {

			for (long portletPreferenceValueId :
					portletPreferenceValues._portletPreferenceValueIds) {

				deletePreparedStatement.setLong(1, portletPreferenceValueId);

				deletePreparedStatement.addBatch();
			}
		}

		long batchCounter = 0;

		if (newCount > 0) {
			batchCounter = increment(
				PortletPreferenceValue.class.getName(), newCount);

			batchCounter -= newCount;
		}

		int smallValueMaxLength = ModelHintsUtil.getMaxLength(
			PortletPreferenceValue.class.getName(), "smallValue");

		for (Map.Entry<String, Map.Entry<PreferenceValues, PreferenceValues>>
				entry : preferenceEntries.entrySet()) {

			Map.Entry<PreferenceValues, PreferenceValues>
				preferenceValuesEntry = entry.getValue();

			PreferenceValues oldPreferenceValues =
				preferenceValuesEntry.getKey();

			PreferenceValues newPreferenceValues =
				preferenceValuesEntry.getValue();

			List<String> newValues = newPreferenceValues._values;

			int oldSize = 0;

			if (oldPreferenceValues != null) {
				oldSize = oldPreferenceValues._values.size();
			}

			for (int i = 0; i < newValues.size(); i++) {
				String value = newValues.get(i);

				if (oldSize > i) {
					if (!Objects.equals(
							value, oldPreferenceValues._values.get(i)) ||
						(newPreferenceValues._readOnly !=
							oldPreferenceValues._readOnly)) {

						String largeValue = null;
						String smallValue = null;

						if ((value != null) &&
							(value.length() > smallValueMaxLength)) {

							largeValue = value;
						}
						else {
							smallValue = value;
						}

						updatePreparedStatement.setString(1, largeValue);
						updatePreparedStatement.setBoolean(
							2, newPreferenceValues._readOnly);
						updatePreparedStatement.setString(3, smallValue);
						updatePreparedStatement.setLong(
							4,
							oldPreferenceValues._portletPreferenceValueIds.get(
								i));

						updatePreparedStatement.addBatch();
					}
				}
				else {
					String largeValue = null;
					String smallValue = null;

					if ((value != null) &&
						(value.length() > smallValueMaxLength)) {

						largeValue = value;
					}
					else {
						smallValue = value;
					}

					insertPreparedStatement.setLong(1, ctCollectionId);
					insertPreparedStatement.setLong(2, ++batchCounter);
					insertPreparedStatement.setLong(3, companyId);
					insertPreparedStatement.setLong(4, portletPreferencesId);
					insertPreparedStatement.setInt(5, i);
					insertPreparedStatement.setString(6, largeValue);
					insertPreparedStatement.setString(7, entry.getKey());
					insertPreparedStatement.setBoolean(
						8, newPreferenceValues._readOnly);
					insertPreparedStatement.setString(9, smallValue);

					insertPreparedStatement.addBatch();
				}
			}

			for (int i = newValues.size(); i < oldSize; i++) {
				deletePreparedStatement.setLong(
					1, oldPreferenceValues._portletPreferenceValueIds.get(i));

				deletePreparedStatement.addBatch();
			}
		}
	}

	private static class PreferenceValues {

		private final List<Long> _portletPreferenceValueIds = new ArrayList<>();
		private boolean _readOnly;
		private final List<String> _values = new ArrayList<>();

	}

}