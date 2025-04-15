/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.list.type.display.context;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gabriel Albuquerque
 */
public class ViewListTypeEntriesDisplayContext {

	public ViewListTypeEntriesDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ListTypeDefinition>
			listTypeDefinitionModelResourcePermission) {

		_listTypeDefinitionModelResourcePermission =
			listTypeDefinitionModelResourcePermission;

		_objectRequestHelper = new ObjectRequestHelper(httpServletRequest);
	}

	public boolean hasUpdateListTypeDefinitionPermission()
		throws PortalException {

		return _listTypeDefinitionModelResourcePermission.contains(
			_objectRequestHelper.getPermissionChecker(),
			_getListTypeDefinitionId(), ActionKeys.UPDATE);
	}

	private long _getListTypeDefinitionId() {
		HttpServletRequest httpServletRequest =
			_objectRequestHelper.getRequest();

		ListTypeDefinition listTypeDefinition =
			(ListTypeDefinition)httpServletRequest.getAttribute(
				ObjectWebKeys.LIST_TYPE_DEFINITION);

		return listTypeDefinition.getListTypeDefinitionId();
	}

	private final ModelResourcePermission<ListTypeDefinition>
		_listTypeDefinitionModelResourcePermission;
	private final ObjectRequestHelper _objectRequestHelper;

}