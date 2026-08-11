/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet;

import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.web.internal.object.entries.display.context.ViewObjectEntriesDisplayContext;
import com.liferay.object.web.internal.object.entries.frontend.data.set.filter.factory.ObjectFieldFDSFilterFactoryRegistry;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
public class ObjectEntriesPortlet extends MVCPortlet {

	public ObjectEntriesPortlet(
		ObjectActionLocalService objectActionLocalService,
		long objectDefinitionId,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldFDSFilterFactoryRegistry objectFieldFDSFilterFactoryRegistry,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		ObjectViewLocalService objectViewLocalService, Portal portal,
		PortletResourcePermission portletResourcePermission) {

		_objectActionLocalService = objectActionLocalService;
		_objectDefinitionId = objectDefinitionId;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldFDSFilterFactoryRegistry =
			objectFieldFDSFilterFactoryRegistry;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_objectViewLocalService = objectViewLocalService;
		_portal = portal;
		_portletResourcePermission = portletResourcePermission;
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				_objectDefinitionId);

		renderRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION,
			_getObjectDefinition(objectDefinition, renderRequest));
		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ViewObjectEntriesDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_objectActionLocalService, _objectFieldFDSFilterFactoryRegistry,
				_objectFieldLocalService,
				_objectScopeProviderRegistry.getObjectScopeProvider(
					objectDefinition.getScope()),
				_objectViewLocalService, _portletResourcePermission,
				objectDefinition.getRESTContextPath()));

		super.render(renderRequest, renderResponse);
	}

	private ObjectDefinition _getObjectDefinition(
		ObjectDefinition objectDefinition, RenderRequest renderRequest) {

		long objectDefinitionId = ParamUtil.getLong(
			renderRequest, "objectDefinitionId");

		if ((objectDefinitionId == 0) ||
			(objectDefinitionId == _objectDefinitionId)) {

			return objectDefinition;
		}

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				ParamUtil.getLong(renderRequest, "objectRelationshipId"));

		if ((objectRelationship == null) ||
			(objectRelationship.getObjectDefinitionId1() !=
				_objectDefinitionId) ||
			(objectRelationship.getObjectDefinitionId2() !=
				objectDefinitionId)) {

			return objectDefinition;
		}

		ObjectDefinition relatedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinitionId);

		if (relatedObjectDefinition == null) {
			return objectDefinition;
		}

		return relatedObjectDefinition;
	}

	private final ObjectActionLocalService _objectActionLocalService;
	private final long _objectDefinitionId;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldFDSFilterFactoryRegistry
		_objectFieldFDSFilterFactoryRegistry;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final ObjectViewLocalService _objectViewLocalService;
	private final Portal _portal;
	private final PortletResourcePermission _portletResourcePermission;

}