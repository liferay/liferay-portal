/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.security.permission.resource.util;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Carolina Barbosa
 */
public class ObjectDefinitionResourcePermissionUtil {

	public static void populateResourceActions(
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition,
			PortletLocalService portletLocalService,
			ResourceActions resourceActions,
			List<ObjectAction> standaloneObjectActions)
		throws Exception {

		Document document = _readDocument(
			objectActionLocalService, objectDefinition,
			standaloneObjectActions);

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
				objectDefinition, document);
		}
	}

	public static void removeResourceActions(
			ObjectActionLocalService objectActionLocalService,
			ObjectDefinition objectDefinition, ResourceActions resourceActions)
		throws Exception {

		Document document = _objectDefinitionResourceActionDocumentsMap.remove(
			objectDefinition);

		if (document == null) {
			document = _readDocument(
				objectActionLocalService, objectDefinition, null);
		}

		resourceActions.removeModelResources(document);

		resourceActions.removePortletResources(document);
	}

	private static String _getObjectActionPermissionKeys(
		ObjectActionLocalService objectActionLocalService,
		long objectDefinitionId, List<ObjectAction> standaloneObjectActions) {

		String objectActionPermissionKeys = StringPool.BLANK;

		if (standaloneObjectActions == null) {
			standaloneObjectActions = objectActionLocalService.getObjectActions(
				objectDefinitionId,
				ObjectActionTriggerConstants.KEY_STANDALONE);
		}

		for (ObjectAction objectAction : standaloneObjectActions) {
			objectActionPermissionKeys = StringBundler.concat(
				objectActionPermissionKeys, "<action-key>",
				objectAction.getName(), "</action-key>");
		}

		return objectActionPermissionKeys;
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
			ObjectDefinition objectDefinition,
			List<ObjectAction> standaloneObjectActions)
		throws Exception {

		String objectActionPermissionKeys = _getObjectActionPermissionKeys(
			objectActionLocalService, objectDefinition.getObjectDefinitionId(),
			standaloneObjectActions);

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
						objectActionPermissionKeys,
					objectDefinition.getPortletId(),
					objectDefinition.getResourceName()
				}));
	}

	private static final Map<ObjectDefinition, Document>
		_objectDefinitionResourceActionDocumentsMap = new ConcurrentHashMap<>();

}