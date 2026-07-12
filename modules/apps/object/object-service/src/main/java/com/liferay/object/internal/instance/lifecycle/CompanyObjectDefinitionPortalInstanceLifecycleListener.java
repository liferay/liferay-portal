/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.instance.lifecycle;

import com.liferay.asset.list.model.AssetListEntryTable;
import com.liferay.fragment.model.FragmentCompositionTable;
import com.liferay.fragment.model.FragmentEntryLinkTable;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelTable;
import com.liferay.object.definition.security.permission.resource.util.ObjectDefinitionResourcePermissionUtil;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.jdbc.CurrentConnection;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassNameTable;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PortletPreferencesTable;
import com.liferay.portal.kernel.model.PortletTable;
import com.liferay.portal.kernel.model.ResourceActionTable;
import com.liferay.portal.kernel.model.ResourcePermissionTable;
import com.liferay.portal.kernel.model.UserNotificationEventTable;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceTable;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceTokenTable;
import com.liferay.portal.workflow.kaleo.model.KaleoLogTable;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceTokenTable;
import com.liferay.site.navigation.model.SiteNavigationMenuItemTable;
import com.liferay.template.model.TemplateEntryTable;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author István András Dézsi
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CompanyObjectDefinitionPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstancePreregistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			!PortalInstances.isCompanyInCopyProcess()) {

			return;
		}

		Connection connection = _currentConnection.getConnection(
			InfrastructureUtil.getDataSource());

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(
					company.getCompanyId(),
					WorkflowConstants.STATUS_APPROVED)) {

			if (objectDefinition.isUnmodifiableSystemObject()) {
				continue;
			}

			String className = objectDefinition.getClassName();

			objectDefinition = _objectDefinitionLocalService.updateClassName(
				objectDefinition.getObjectDefinitionId());

			if (StringUtil.equals(objectDefinition.getClassName(), className)) {
				continue;
			}

			try (AutoCloseable autoCloseable = _disableAutoCommit(connection)) {
				_executeUpdates(
					_classNameColumnNamesMap, company.getCompanyId(),
					connection, objectDefinition.getClassName(), className);
				_executeUpdates(
					_portletIdColumnNamesMap, company.getCompanyId(),
					connection,
					ObjectDefinitionUtil.getPortletId(
						objectDefinition.getClassName()),
					ObjectDefinitionUtil.getPortletId(className));

				connection.commit();
			}

			_ploEntryLocalService.deletePLOEntries(
				company.getCompanyId(), "model.resource." + className);

			_objectDefinitionLocalService.addOrUpdateObjectDefinitionPLOEntries(
				objectDefinition);
		}
	}

	@Override
	public void portalInstancePreunregistered(Company company)
		throws Exception {

		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return;
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				company.getCompanyId(), WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (!objectDefinition.isUnmodifiableSystemObject()) {

				// A partition company is deleted by dropping its schema, so
				// its object definitions are never individually deleted and
				// nothing else removes their resource actions. Evict them
				// from the JVM global maps in ResourceActionsImpl while the
				// partition is still readable, or the portlet to root model
				// resource bindings leak and a later company reusing a random
				// portlet name collides with them. See LPS-135983.

				try {
					ObjectDefinitionResourcePermissionUtil.
						removeResourceActions(
							_objectActionLocalService, objectDefinition,
							_objectFieldLocalService, _resourceActions);
				}
				catch (Exception exception) {
					_log.error(exception);
				}
			}

			_objectDefinitionLocalService.undeployObjectDefinition(
				objectDefinition);
		}
	}

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		if (!PropsValues.DATABASE_PARTITION_ENABLED ||
			!PortalInstances.isCompanyInCopyProcess()) {

			return;
		}

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				company.getCompanyId(), WorkflowConstants.STATUS_APPROVED);

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (objectDefinition.isActive()) {
				_objectDefinitionLocalService.deployObjectDefinition(
					objectDefinition);
			}
			else {
				_objectDefinitionLocalService.deployInactiveObjectDefinition(
					objectDefinition);
			}
		}
	}

	private AutoCloseable _disableAutoCommit(Connection connection)
		throws Exception {

		boolean autoCommit = connection.getAutoCommit();

		connection.setAutoCommit(false);

		return () -> connection.setAutoCommit(autoCommit);
	}

	private void _executeUpdates(
			Map<String, String> columnNamesMap, long companyId,
			Connection connection, String newData, String oldData)
		throws Exception {

		for (Map.Entry<String, String> entry : columnNamesMap.entrySet()) {
			String[] columnNames = StringUtil.split(entry.getValue());

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						SQLTransformer.transform(
							_getUpdateSQL(
								columnNames, companyId, entry.getKey())))) {

				int parameterIndex = 1;

				for (int i = 0; i < columnNames.length; i++) {
					preparedStatement.setString(parameterIndex++, oldData);
					preparedStatement.setString(parameterIndex++, newData);
				}

				for (int i = 0; i < columnNames.length; i++) {
					preparedStatement.setString(
						parameterIndex++, "%" + oldData + "%");
				}

				preparedStatement.executeUpdate();
			}
		}
	}

	private String _getUpdateSQL(
		String[] columnNames, long companyId, String tableName) {

		StringBundler sb = new StringBundler();

		sb.append("update ");
		sb.append(PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX);
		sb.append(companyId);
		sb.append(StringPool.PERIOD);
		sb.append(tableName);
		sb.append(" set ");

		for (int i = 0; i < columnNames.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(columnNames[i]);
			sb.append(" = replace(");
			sb.append(columnNames[i]);
			sb.append(", ?, ?)");
		}

		sb.append(" where ");

		for (int i = 0; i < columnNames.length; i++) {
			if (i > 0) {
				sb.append(" or ");
			}

			sb.append(columnNames[i]);
			sb.append(" like ?");
		}

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyObjectDefinitionPortalInstanceLifecycleListener.class);

	private final Map<String, String> _classNameColumnNamesMap =
		HashMapBuilder.put(
			AssetListEntryTable.INSTANCE.getTableName(),
			AssetListEntryTable.INSTANCE.assetEntryType.getName()
		).put(
			ClassNameTable.INSTANCE.getTableName(),
			ClassNameTable.INSTANCE.value.getName()
		).put(
			FragmentCompositionTable.INSTANCE.getTableName(),
			FragmentCompositionTable.INSTANCE.data.getName()
		).put(
			FragmentEntryLinkTable.INSTANCE.getTableName(),
			FragmentEntryLinkTable.INSTANCE.editableValues.getName()
		).put(
			KaleoInstanceTable.INSTANCE.getTableName(),
			KaleoInstanceTable.INSTANCE.className.getName() + StringPool.COMMA +
				KaleoInstanceTable.INSTANCE.workflowContext.getName()
		).put(
			KaleoInstanceTokenTable.INSTANCE.getTableName(),
			KaleoInstanceTokenTable.INSTANCE.className.getName()
		).put(
			KaleoLogTable.INSTANCE.getTableName(),
			KaleoLogTable.INSTANCE.workflowContext.getName()
		).put(
			KaleoTaskInstanceTokenTable.INSTANCE.getTableName(),
			KaleoTaskInstanceTokenTable.INSTANCE.className.getName() +
				StringPool.COMMA +
					KaleoTaskInstanceTokenTable.INSTANCE.workflowContext.
						getName()
		).put(
			LayoutPageTemplateStructureRelTable.INSTANCE.getTableName(),
			LayoutPageTemplateStructureRelTable.INSTANCE.data.getName()
		).put(
			ResourceActionTable.INSTANCE.getTableName(),
			ResourceActionTable.INSTANCE.name.getName()
		).put(
			ResourcePermissionTable.INSTANCE.getTableName(),
			ResourcePermissionTable.INSTANCE.name.getName() + StringPool.COMMA +
				ResourcePermissionTable.INSTANCE.primKey.getName()
		).put(
			SiteNavigationMenuItemTable.INSTANCE.getTableName(),
			SiteNavigationMenuItemTable.INSTANCE.type.getName() +
				StringPool.COMMA +
					SiteNavigationMenuItemTable.INSTANCE.typeSettings.getName()
		).put(
			TemplateEntryTable.INSTANCE.getTableName(),
			TemplateEntryTable.INSTANCE.infoItemClassName.getName()
		).put(
			UserNotificationEventTable.INSTANCE.getTableName(),
			UserNotificationEventTable.INSTANCE.payload.getName()
		).put(
			"Configuration_", "dictionary"
		).build();

	@Reference
	private CurrentConnection _currentConnection;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	private final Map<String, String> _portletIdColumnNamesMap =
		HashMapBuilder.put(
			FragmentCompositionTable.INSTANCE.getTableName(),
			FragmentCompositionTable.INSTANCE.data.getName()
		).put(
			FragmentEntryLinkTable.INSTANCE.getTableName(),
			FragmentEntryLinkTable.INSTANCE.editableValues.getName()
		).put(
			LayoutPageTemplateStructureRelTable.INSTANCE.getTableName(),
			LayoutPageTemplateStructureRelTable.INSTANCE.data.getName()
		).put(
			PortletTable.INSTANCE.getTableName(),
			PortletTable.INSTANCE.portletId.getName()
		).put(
			PortletPreferencesTable.INSTANCE.getTableName(),
			PortletPreferencesTable.INSTANCE.portletId.getName()
		).put(
			ResourceActionTable.INSTANCE.getTableName(),
			ResourceActionTable.INSTANCE.name.getName()
		).put(
			ResourcePermissionTable.INSTANCE.getTableName(),
			ResourcePermissionTable.INSTANCE.name.getName() + StringPool.COMMA +
				ResourcePermissionTable.INSTANCE.primKey.getName()
		).build();

	@Reference
	private ResourceActions _resourceActions;

}