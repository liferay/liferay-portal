/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsDetailsDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Renan Vasconcelos
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class DetailsObjectDefinitionsScreenNavigationEntry
	extends BaseObjectDefinitionsScreenNavigationEntry {

	@Override
	public String getCategoryKey() {
		return ObjectDefinitionsScreenNavigationEntryConstants.
			CATEGORY_KEY_DETAILS;
	}

	@Override
	public String getJspPath() {
		return "/object_definitions/object_definition/details.jsp";
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ObjectDefinitionsDetailsDisplayContext(
				_configurationProvider, httpServletRequest,
				_objectDefinitionModelResourcePermission,
				_objectEntryManagerRegistry, _objectFolderLocalService,
				_objectScopeProviderRegistry));

		super.render(httpServletRequest, httpServletResponse);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

}