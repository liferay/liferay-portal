/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.model.listener;

import com.liferay.object.model.ObjectAction;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nathaly Gomes
 */
@Component(service = ModelListener.class)
public class ObjectActionModelListener extends BaseModelListener<ObjectAction> {

	@Override
	public void onAfterCreate(ObjectAction objectAction) {
		_objectDefinitionToolSetInvalidator.invalidate(
			objectAction.getObjectDefinitionId());
	}

	@Override
	public void onAfterRemove(ObjectAction objectAction) {
		_objectDefinitionToolSetInvalidator.invalidate(
			objectAction.getObjectDefinitionId());
	}

	@Override
	public void onAfterUpdate(
		ObjectAction originalObjectAction, ObjectAction objectAction) {

		_objectDefinitionToolSetInvalidator.invalidate(
			objectAction.getObjectDefinitionId());
	}

	@Reference
	private ObjectDefinitionToolSetInvalidator
		_objectDefinitionToolSetInvalidator;

}