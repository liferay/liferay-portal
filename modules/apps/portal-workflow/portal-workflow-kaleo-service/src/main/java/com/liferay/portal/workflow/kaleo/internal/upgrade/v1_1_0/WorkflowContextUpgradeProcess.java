/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.kaleo.internal.upgrade.v1_3_0.util.WorkflowContextUpgradeUtil;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.AbstractSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;

import org.json.JSONObject;

/**
 * @author Jang Kim
 */
public class WorkflowContextUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateTable("KaleoInstance", "kaleoInstanceId");
		_updateTable("KaleoLog", "kaleoLogId");
		_updateTable("KaleoTaskInstanceToken", "kaleoTaskInstanceTokenId");
	}

	private JSONSerializer _getJSONSerializer() throws Exception {
		if (_jsonSerializer == null) {
			_jsonSerializer = new JSONSerializer();

			_jsonSerializer.registerDefaultSerializers();

			_jsonSerializer.registerSerializer(
				new PortletPreferencesIdsSerializer());
		}

		return _jsonSerializer;
	}

	private void _updateTable(String tableName, String fieldName)
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer(tableName);

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", fieldName, ", workflowContext from ", tableName,
					" where workflowContext is not null and workflowContext ",
					"not like '%serializable%'"));

			ResultSet resultSet = preparedStatement.executeQuery()) {

			JSONSerializer jsonSerializer = _getJSONSerializer();

			while (resultSet.next()) {
				String workflowContextJSON = resultSet.getString(
					"workflowContext");

				if (Validator.isNull(workflowContextJSON)) {
					continue;
				}

				long fieldValue = resultSet.getLong(fieldName);

				workflowContextJSON =
					WorkflowContextUpgradeUtil.renamePortalClassNames(
						workflowContextJSON);

				Map<String, Serializable> workflowContext =
					(Map<String, Serializable>)jsonSerializer.fromJSON(
						workflowContextJSON);

				workflowContext =
					WorkflowContextUpgradeUtil.renameEntryClassName(
						workflowContext);

				_updateWorkflowContext(
					tableName, fieldName, fieldValue,
					WorkflowContextUtil.convert(workflowContext));
			}
		}
	}

	private void _updateWorkflowContext(
			String tableName, String primaryKeyName, long primaryKeyValue,
			String workflowContext)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName, " set workflowContext = ? where ",
					primaryKeyName, " = ?"))) {

			preparedStatement.setString(1, workflowContext);
			preparedStatement.setLong(2, primaryKeyValue);

			preparedStatement.executeUpdate();
		}
	}

	private JSONSerializer _jsonSerializer;

	private static class PortletPreferencesIdsSerializer
		extends AbstractSerializer {

		@Override
		public Class<?>[] getJSONClasses() {
			return _JSON_CLASSES;
		}

		@Override
		public Class<?>[] getSerializableClasses() {
			return _SERIALIZABLE_CLASSES;
		}

		@Override
		public Object marshall(
				SerializerState serializerState, Object parentObject,
				Object object)
			throws MarshallException {

			throw new UnsupportedOperationException(
				"The marshall operation is unsupported");
		}

		@Override
		public ObjectMatch tryUnmarshall(
				SerializerState serializerState,
				@SuppressWarnings("rawtypes") Class clazz, Object object)
			throws UnmarshallException {

			JSONObject portletPreferencesIdsJSONObject = (JSONObject)object;

			ObjectMatch objectMatch = ObjectMatch.ROUGHLY_SIMILAR;

			if (portletPreferencesIdsJSONObject.has("companyId") &&
				portletPreferencesIdsJSONObject.has("ownerId") &&
				portletPreferencesIdsJSONObject.has("ownerType") &&
				portletPreferencesIdsJSONObject.has("plid") &&
				portletPreferencesIdsJSONObject.has("portletId")) {

				objectMatch = ObjectMatch.OKAY;
			}

			serializerState.setSerialized(object, objectMatch);

			return objectMatch;
		}

		@Override
		public Object unmarshall(
				SerializerState serializerState,
				@SuppressWarnings("rawtypes") Class clazz, Object object)
			throws UnmarshallException {

			JSONObject portletPreferencesIdsJSONObject = (JSONObject)object;

			long companyId = 0;

			try {
				companyId = portletPreferencesIdsJSONObject.getLong(
					"companyId");
			}
			catch (Exception exception) {
				throw new UnmarshallException(
					"companyId is undefined", exception);
			}

			long ownerId = 0;

			try {
				ownerId = portletPreferencesIdsJSONObject.getLong("ownerId");
			}
			catch (Exception exception) {
				throw new UnmarshallException(
					"ownerId is undefined", exception);
			}

			int ownerType = 0;

			try {
				ownerType = portletPreferencesIdsJSONObject.getInt("ownerType");
			}
			catch (Exception exception) {
				throw new UnmarshallException(
					"ownerType is undefined", exception);
			}

			long plid = 0;

			try {
				plid = portletPreferencesIdsJSONObject.getLong("plid");
			}
			catch (Exception exception) {
				throw new UnmarshallException("plid is undefined", exception);
			}

			String portletId = null;

			try {
				portletId = portletPreferencesIdsJSONObject.getString(
					"portletId");
			}
			catch (Exception exception) {
				throw new UnmarshallException(
					"portletId is undefined", exception);
			}

			PortletPreferencesIds portletPreferencesIds =
				new PortletPreferencesIds(
					companyId, ownerId, ownerType, plid, portletId);

			serializerState.setSerialized(object, portletPreferencesIds);

			return portletPreferencesIds;
		}

		private static final Class<?>[] _JSON_CLASSES = {JSONObject.class};

		private static final Class<?>[] _SERIALIZABLE_CLASSES = {
			PortletPreferencesIds.class
		};

	}

}