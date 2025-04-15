/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants;
import com.liferay.object.web.internal.object.definitions.display.context.ObjectDefinitionsFieldsDisplayContext;
import com.liferay.portal.kernel.model.User;
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
public class FieldsObjectDefinitionsScreenNavigationEntry
	extends BaseObjectDefinitionsScreenNavigationEntry {

	@Override
	public String getCategoryKey() {
		return ObjectDefinitionsScreenNavigationEntryConstants.
			CATEGORY_KEY_FIELDS;
	}

	@Override
	public String getJspPath() {
		return "/object_definitions/object_definition/fields.jsp";
	}

	@Override
	public boolean isVisible(User user, ObjectDefinition context) {
		return super.isVisible(user, context);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new ObjectDefinitionsFieldsDisplayContext(
				httpServletRequest, _listTypeDefinitionService,
				_objectDefinitionModelResourcePermission,
				_objectFieldBusinessTypeRegistry, _objectFolderLocalService));

		super.render(httpServletRequest, httpServletResponse);
	}

	@Reference
	private ListTypeDefinitionService _listTypeDefinitionService;

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectFieldBusinessTypeRegistry _objectFieldBusinessTypeRegistry;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

}