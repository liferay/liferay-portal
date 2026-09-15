/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nathaly Gomes
 */
@Component(service = ModelListener.class)
public class ObjectRelationshipModelListener
	extends BaseModelListener<ObjectRelationship> {

	@Override
	public void onAfterCreate(ObjectRelationship objectRelationship) {
		ToolSetUtil.clearOpenAPIJSONObjectCache(
			objectRelationship.getCompanyId());
	}

	@Override
	public void onAfterRemove(ObjectRelationship objectRelationship) {
		ToolSetUtil.clearOpenAPIJSONObjectCache(
			objectRelationship.getCompanyId());
	}

	@Override
	public void onAfterUpdate(
		ObjectRelationship originalObjectRelationship,
		ObjectRelationship objectRelationship) {

		ToolSetUtil.clearOpenAPIJSONObjectCache(
			objectRelationship.getCompanyId());
	}

}