/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Feliphe Marinho
 */
public abstract class BaseObjectDefinitionsDisplayContext {

	public String getAPIURL() {
		return "/o/object-admin/v1.0/object-definitions/" +
			getObjectDefinitionId() + getAPIURI();
	}

	public CreationMenu getCreationMenu() throws PortalException {
		CreationMenu creationMenu = new CreationMenu();

		if (!hasUpdateObjectDefinitionPermission()) {
			return creationMenu;
		}

		creationMenu.addDropdownItem(
			getCreationMenuDropdownItemUnsafeConsumer());

		return creationMenu;
	}

	public ObjectDefinition getObjectDefinition() {
		HttpServletRequest httpServletRequest =
			objectRequestHelper.getRequest();

		return (ObjectDefinition)httpServletRequest.getAttribute(
			ObjectWebKeys.OBJECT_DEFINITION);
	}

	public String getObjectDefinitionExternalReferenceCode() {
		ObjectDefinition objectDefinition = getObjectDefinition();

		return objectDefinition.getExternalReferenceCode();
	}

	public long getObjectDefinitionId() {
		ObjectDefinition objectDefinition = getObjectDefinition();

		return objectDefinition.getObjectDefinitionId();
	}

	public String getObjectFolderName() throws PortalException {
		ObjectDefinition objectDefinition = getObjectDefinition();

		ObjectFolder objectFolder = _objectFolderLocalService.getObjectFolder(
			objectDefinition.getObjectFolderId());

		return ParamUtil.getString(
			objectRequestHelper.getRequest(), "objectFolderName",
			objectFolder.getName());
	}

	public PortletURL getPortletURL() throws PortletException {
		return PortletURLUtil.clone(
			PortletURLUtil.getCurrent(
				objectRequestHelper.getLiferayPortletRequest(),
				objectRequestHelper.getLiferayPortletResponse()),
			objectRequestHelper.getLiferayPortletResponse());
	}

	public boolean hasUpdateObjectDefinitionPermission()
		throws PortalException {

		return objectDefinitionModelResourcePermission.contains(
			objectRequestHelper.getPermissionChecker(), getObjectDefinitionId(),
			ActionKeys.UPDATE);
	}

	protected BaseObjectDefinitionsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectFolderLocalService objectFolderLocalService) {

		this.objectDefinitionModelResourcePermission =
			objectDefinitionModelResourcePermission;
		_objectFolderLocalService = objectFolderLocalService;

		objectRequestHelper = new ObjectRequestHelper(httpServletRequest);
	}

	protected String getAPIURI() {
		return StringPool.BLANK;
	}

	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
		};
	}

	protected final ModelResourcePermission<ObjectDefinition>
		objectDefinitionModelResourcePermission;
	protected final ObjectRequestHelper objectRequestHelper;

	private final ObjectFolderLocalService _objectFolderLocalService;

}