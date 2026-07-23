/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.permission.provider;

import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;

/**
 * @author Lourdes Fernández Besada
 */
public class ObjectEntryInfoPermissionProvider
	implements InfoPermissionProvider<ObjectEntry> {

	public ObjectEntryInfoPermissionProvider(
		ObjectDefinition objectDefinition,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		PortletLocalService portletLocalService,
		PortletResourcePermission portletResourcePermission) {

		_objectDefinition = objectDefinition;
		_objectDefinitionModelResourcePermission =
			objectDefinitionModelResourcePermission;
		_portletLocalService = portletLocalService;
		_portletResourcePermission = portletResourcePermission;
	}

	@Override
	public boolean hasAddPermission(
		long groupId, PermissionChecker permissionChecker) {

		return _portletResourcePermission.contains(
			permissionChecker, groupId, ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		if (_getActivePortlet() == null) {
			return false;
		}

		try {
			return _objectDefinitionModelResourcePermission.contains(
				permissionChecker, _objectDefinition.getObjectDefinitionId(),
				ActionKeys.VIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	@Override
	public boolean hasViewPermission(
		String formVariationKey, long groupId,
		PermissionChecker permissionChecker) {

		if (hasViewPermission(permissionChecker)) {
			return true;
		}

		Portlet portlet = _getActivePortlet();

		if (portlet == null) {
			return false;
		}

		try {
			return PortletPermissionUtil.contains(
				permissionChecker, groupId, null, portlet.getRootPortletId(),
				ActionKeys.VIEW);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private Portlet _getActivePortlet() {
		if (!_objectDefinition.isEnableFormContainer()) {
			return null;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			_objectDefinition.getCompanyId(), _objectDefinition.getPortletId());

		if ((portlet == null) || !portlet.isActive()) {
			return null;
		}

		return portlet;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryInfoPermissionProvider.class);

	private final ObjectDefinition _objectDefinition;
	private final ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;
	private final PortletLocalService _portletLocalService;
	private final PortletResourcePermission _portletResourcePermission;

}