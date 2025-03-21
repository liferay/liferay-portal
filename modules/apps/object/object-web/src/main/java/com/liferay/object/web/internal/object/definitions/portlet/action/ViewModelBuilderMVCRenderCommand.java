/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action;

import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFieldService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsDetailsDisplayContext;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsFieldsDisplayContext;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsRelationshipsDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Franca
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ObjectPortletKeys.OBJECT_DEFINITIONS,
		"mvc.command.name=/object_definitions/view_model_builder"
	},
	service = MVCRenderCommand.class
)
public class ViewModelBuilderMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITIONS_DETAILS_DISPLAY_CONTEXT,
			new ObjectDefinitionsDetailsDisplayContext(
				_configurationProvider,
				_portal.getHttpServletRequest(renderRequest),
				_objectDefinitionModelResourcePermission,
				_objectEntryManagerRegistry, _objectFolderLocalService,
				_objectScopeProviderRegistry));
		renderRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITIONS_FIELD_DISPLAY_CONTEXT,
			new ObjectDefinitionsFieldsDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_listTypeDefinitionService,
				_objectDefinitionModelResourcePermission,
				_objectFieldBusinessTypeRegistry, _objectFolderLocalService));
		renderRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITIONS_RELATIONSHIP_DISPLAY_CONTEXT,
			new ObjectDefinitionsRelationshipsDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_objectDefinitionModelResourcePermission,
				_objectDefinitionService, _objectFieldService,
				_objectFolderLocalService,
				_systemObjectDefinitionManagerRegistry));

		return "/object_folders/view_model_builder.jsp";
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFieldService _objectFieldService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}