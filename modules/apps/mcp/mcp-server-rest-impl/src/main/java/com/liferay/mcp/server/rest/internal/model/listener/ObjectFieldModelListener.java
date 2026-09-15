/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.cache.MCPServerCacheManager;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ModelListener.class)
public class ObjectFieldModelListener extends BaseModelListener<ObjectField> {

	@Override
	public void onAfterCreate(ObjectField objectField) {
		_mcpServerCacheManager.clearOpenAPIJSONObjectCache(
			objectField.getCompanyId());
	}

	@Override
	public void onAfterRemove(ObjectField objectField) {
		_mcpServerCacheManager.clearOpenAPIJSONObjectCache(
			objectField.getCompanyId());
	}

	@Override
	public void onAfterUpdate(
		ObjectField originalObjectField, ObjectField objectField) {

		_mcpServerCacheManager.clearOpenAPIJSONObjectCache(
			objectField.getCompanyId());
	}

	@Reference
	private MCPServerCacheManager _mcpServerCacheManager;

}