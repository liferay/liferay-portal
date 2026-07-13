/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.security.permission.resource.util;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PermissionServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Carolina Barbosa
 */
public class ObjectDefinitionResourcePermissionUtil {

	public static void populateResourceActions(
			ObjectActionLocalService objectActionLocalService,
			List<ObjectAction> objectActions, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			List<ObjectField> objectFields,
			PortletLocalService portletLocalService,
			ResourceActions resourceActions)
		throws Exception {

		Document document = _readDocument(
			objectActionLocalService, objectActions, objectDefinition,
			objectFieldLocalService, objectFields);

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				objectDefinition.getCompanyId())) {

			resourceActions.populateModelResources(document);

			Portlet portlet = portletLocalService.getPortletById(
				objectDefinition.getCompanyId(),
				objectDefinition.getPortletId());

			if (portlet != null) {
				resourceActions.populatePortletResource(
					portlet,
					ObjectDefinitionResourcePermissionUtil.class.
						getClassLoader(),
					document);
			}

			_objectDefinitionResourceActionDocumentsMap.put(
				_getKey(objectDefinition), document);
		}
	}

	public static void removeResourceActions(
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			ResourceActions resourceActions)
		throws Exception {

		Document document = _objectDefinitionResourceActionDocumentsMap.remove(
			_getKey(objectDefinition));

		if (document == null) {
			document = _readDocument(
				objectActionLocalService, null, objectDefinition,
				objectFieldLocalService, null);
		}

		resourceActions.removeModelResources(document);

		resourceActions.removePortletResources(document);
	}

	public static void updateResourcePermissions(
			long companyId, long groupId, String permissionName, long primKey,
			ServiceContext serviceContext)
		throws PortalException {

		ModelPermissions modelPermissions =
			serviceContext.getModelPermissions();

		if (modelPermissions == null) {
			return;
		}

		PermissionServiceUtil.checkPermission(groupId, permissionName, primKey);

		Collection<String> roleNames = modelPermissions.getRoleNames();

		for (ResourcePermission resourcePermission :
				ResourcePermissionLocalServiceUtil.getResourcePermissions(
					companyId, permissionName,
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(primKey))) {

			Role role = RoleLocalServiceUtil.fetchRole(
				resourcePermission.getRoleId());

			if ((role == null) || roleNames.contains(role.getName())) {
				continue;
			}

			for (ResourceAction resourceAction :
					ResourceActionLocalServiceUtil.getResourceActions(
						permissionName)) {

				ResourcePermissionLocalServiceUtil.removeResourcePermission(
					companyId, permissionName,
					ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(primKey),
					role.getRoleId(), resourceAction.getActionId());
			}
		}

		ResourcePermissionLocalServiceUtil.updateResourcePermissions(
			companyId, groupId, permissionName, String.valueOf(primKey),
			modelPermissions);
	}

	private static Serializable _getKey(ObjectDefinition objectDefinition) {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return objectDefinition.getObjectDefinitionId() + StringPool.AT +
				objectDefinition.getCompanyId();
		}

		return objectDefinition.getObjectDefinitionId();
	}

	private static String _getObjectActionPermissionKeys(
		ObjectActionLocalService objectActionLocalService,
		List<ObjectAction> objectActions, long objectDefinitionId) {

		if (objectActionLocalService == null) {
			return null;
		}

		String objectActionPermissionKeys = StringPool.BLANK;

		if (objectActions == null) {
			objectActions = objectActionLocalService.getObjectActions(
				objectDefinitionId,
				ObjectActionTriggerConstants.KEY_STANDALONE);
		}

		for (ObjectAction objectAction : objectActions) {
			objectActionPermissionKeys = StringBundler.concat(
				objectActionPermissionKeys, "<action-key>",
				objectAction.getName(), "</action-key>");
		}

		return objectActionPermissionKeys;
	}

	private static String _getObjectFieldPermissionKeys(
			long objectDefinitionId,
			ObjectFieldLocalService objectFieldLocalService,
			List<ObjectField> objectFields)
		throws Exception {

		if (objectFieldLocalService == null) {
			return null;
		}

		if (objectFields == null) {
			objectFields =
				objectFieldLocalService.getObjectFieldsByBusinessType(
					objectDefinitionId,
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT);
		}

		String objectFieldPermissionKeys = StringPool.BLANK;

		for (ObjectField objectField : objectFields) {
			if (!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

				continue;
			}

			objectFieldLocalService.addOrUpdateObjectFieldPLOEntries(
				objectField);

			objectFieldPermissionKeys = StringBundler.concat(
				objectFieldPermissionKeys, "<action-key>",
				objectField.getAttachmentDownloadActionKey(), "</action-key>");
		}

		return objectFieldPermissionKeys;
	}

	private static String _getPermissionsGuestUnsupported(
		ObjectDefinition objectDefinition) {

		if (!objectDefinition.isEnableComments()) {
			return StringPool.BLANK;
		}

		return "<action-key>DELETE_DISCUSSION</action-key>" +
			"<action-key>UPDATE_DISCUSSION</action-key>";
	}

	private static String _getPermissionsSupports(
		ObjectDefinition objectDefinition) {

		String permissionsSupports = StringPool.BLANK;

		if (objectDefinition.isEnableComments()) {
			permissionsSupports = StringBundler.concat(
				"<action-key>ADD_DISCUSSION</action-key>",
				"<action-key>DELETE_DISCUSSION</action-key>",
				"<action-key>UPDATE_DISCUSSION</action-key>");
		}

		if (objectDefinition.isEnableObjectEntryHistory()) {
			permissionsSupports = StringBundler.concat(
				permissionsSupports, "<action-key>",
				ObjectActionKeys.OBJECT_ENTRY_HISTORY, "</action-key>");
		}

		if (objectDefinition.isEnableObjectEntrySubscription() &&
			FeatureFlagManagerUtil.isEnabled("LPD-17564")) {

			permissionsSupports = StringBundler.concat(
				permissionsSupports, "<action-key>", ActionKeys.SUBSCRIBE,
				"</action-key>");
		}

		return permissionsSupports;
	}

	private static Document _readDocument(
			ObjectActionLocalService objectActionLocalService,
			List<ObjectAction> objectActions, ObjectDefinition objectDefinition,
			ObjectFieldLocalService objectFieldLocalService,
			List<ObjectField> objectFields)
		throws Exception {

		String objectActionPermissionKeys = _getObjectActionPermissionKeys(
			objectActionLocalService, objectActions,
			objectDefinition.getObjectDefinitionId());

		String objectFieldPermissionKeys = StringPool.BLANK;

		if (FeatureFlagManagerUtil.isEnabled(
				objectDefinition.getCompanyId(), "LPD-17564")) {

			objectFieldPermissionKeys = _getObjectFieldPermissionKeys(
				objectDefinition.getObjectDefinitionId(),
				objectFieldLocalService, objectFields);
		}

		String resourceActionsFileName =
			"resource-actions/resource-actions.xml.tpl";

		if (!StringUtil.equals(
				objectDefinition.getStorageType(),
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT)) {

			resourceActionsFileName =
				"resource-actions/resource-actions-nondefault-storage-type." +
					"xml.tpl";
		}

		return SAXReaderUtil.read(
			StringUtil.replace(
				StringUtil.read(
					ObjectDefinitionResourcePermissionUtil.class.
						getClassLoader(),
					resourceActionsFileName),
				new String[] {
					"[$MODEL_NAME$]", "[$PERMISSIONS_GUEST_UNSUPPORTED$]",
					"[$PERMISSIONS_SUPPORTS$]", "[$PORTLET_NAME$]",
					"[$RESOURCE_NAME$]"
				},
				new String[] {
					objectDefinition.getClassName(),
					_getPermissionsGuestUnsupported(objectDefinition) +
						objectActionPermissionKeys,
					_getPermissionsSupports(objectDefinition) +
						objectActionPermissionKeys + objectFieldPermissionKeys,
					objectDefinition.getPortletId(),
					objectDefinition.getResourceName()
				}));
	}

	private static final Map<Serializable, Document>
		_objectDefinitionResourceActionDocumentsMap = new ConcurrentHashMap<>();

}