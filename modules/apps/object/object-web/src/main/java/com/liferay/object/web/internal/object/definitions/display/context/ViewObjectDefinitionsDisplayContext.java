/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Marco Leo
 * @author Gabriel Albuquerque
 */
public class ViewObjectDefinitionsDisplayContext {

	public ViewObjectDefinitionsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectFolderLocalService objectFolderLocalService) {

		_objectDefinitionModelResourcePermission =
			objectDefinitionModelResourcePermission;
		_objectEntryManagerRegistry = objectEntryManagerRegistry;
		_objectFolderLocalService = objectFolderLocalService;

		_objectRequestHelper = new ObjectRequestHelper(httpServletRequest);
	}

	public CreationMenu getCreationMenu() throws Exception {
		CreationMenu creationMenu = new CreationMenu();

		if (!_hasAddObjectDefinitionPermission()) {
			return creationMenu;
		}

		creationMenu.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("addObjectDefinition");
				dropdownItem.setLabel(
					LanguageUtil.get(
						_objectRequestHelper.getRequest(),
						"create-new-object"));
				dropdownItem.setTarget("event");
			});

		return creationMenu;
	}

	public String getEditObjectDefinitionURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_definition"
		).setParameter(
			"objectDefinitionId", "{id}"
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				getEditObjectDefinitionURL(), "view", "view",
				LanguageUtil.get(_objectRequestHelper.getRequest(), "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					_objectRequestHelper.getLiferayPortletResponse()
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_object_definition"
				).buildString(),
				"export", "export",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(),
					"export-object-definition"),
				"get", "exportObjectDefinition", null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					_objectRequestHelper.getLiferayPortletResponse()
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_bound_object_definitions"
				).buildString(),
				"export", "exportBoundObjectDefinitions",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(),
					"export-bound-object-definitions"),
				"get", "exportBoundObjectDefinitions", null),
			new FDSActionDropdownItem(
				getPermissionsURL(ObjectDefinition.class.getName()),
				"password-policies", "permissions",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(), "permissions"),
				"get", "permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				null, "trash", "deleteObjectDefinition",
				LanguageUtil.get(_objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", null));
	}

	public String getImportObjectDefinitionURL() throws Exception {
		return PortletURLBuilder.createActionURL(
			_objectRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/object_definitions/import_object_definition"
		).setRedirect(
			_objectRequestHelper.getCurrentURL()
		).buildString();
	}

	public String getImportObjectFolderURL() throws Exception {
		return PortletURLBuilder.createActionURL(
			_objectRequestHelper.getLiferayPortletResponse()
		).setActionName(
			"/object_definitions/import_object_folder"
		).setRedirect(
			_objectRequestHelper.getCurrentURL()
		).buildString();
	}

	public String getModelBuilderURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/view_model_builder"
		).buildString();
	}

	public String getPermissionsURL(String modelResource) throws Exception {
		PortletURL portletURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_objectRequestHelper.getRequest(),
				"com_liferay_portlet_configuration_web_portlet_" +
					"PortletConfigurationPortlet",
				ActionRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setRedirect(
			_objectRequestHelper.getCurrentURL()
		).setParameter(
			"modelResource", modelResource
		).setParameter(
			"modelResourceDescription", "{name}"
		).setParameter(
			"resourcePrimKey", "{id}"
		).buildPortletURL();

		try {
			portletURL.setWindowState(LiferayWindowState.POP_UP);
		}
		catch (WindowStateException windowStateException) {
			throw new PortalException(windowStateException);
		}

		return portletURL.toString();
	}

	public PortletURL getPortletURL() throws PortletException {
		return PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				_objectRequestHelper.getLiferayPortletRequest(),
				_objectRequestHelper.getLiferayPortletResponse()),
			_objectRequestHelper.getLiferayPortletResponse());
	}

	public JSONArray getStorageTypesJSONArray() throws Exception {
		return JSONUtil.toJSONArray(
			_objectEntryManagerRegistry.getObjectEntryManagers(
				_objectRequestHelper.getCompanyId()),
			objectEntryManager -> JSONUtil.put(
				"label",
				objectEntryManager.getStorageLabel(
					_objectRequestHelper.getLocale())
			).put(
				"value", objectEntryManager.getStorageType()
			));
	}

	private boolean _hasAddObjectDefinitionPermission() {
		PortletResourcePermission portletResourcePermission =
			_objectDefinitionModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			_objectRequestHelper.getPermissionChecker(), null,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);
	}

	private final ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;
	private final ObjectEntryManagerRegistry _objectEntryManagerRegistry;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final ObjectRequestHelper _objectRequestHelper;

}