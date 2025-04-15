/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Gabriel Albuquerque
 */
public class ObjectDefinitionsViewsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsViewsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectFolderLocalService objectFolderLocalService) {

		super(
			httpServletRequest, objectDefinitionModelResourcePermission,
			objectFolderLocalService);

		_objectRequestHelper = new ObjectRequestHelper(httpServletRequest);
	}

	public String getEditObjectViewsURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_view"
		).setParameter(
			"objectViewId", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		boolean hasUpdatePermission = hasUpdateObjectDefinitionPermission();

		return Arrays.asList(
			new FDSActionDropdownItem(
				getEditObjectViewsURL(),
				hasUpdatePermission ? "pencil" : "view",
				hasUpdatePermission ? "edit" : "view",
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					hasUpdatePermission ? "edit" : "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-views/{id}/copy", "copy", "copy",
				LanguageUtil.get(objectRequestHelper.getRequest(), "duplicate"),
				"post", "copy", "async"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-views/{id}", "trash", "delete",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	@Override
	protected String getAPIURI() {
		return "/object-views";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref("addObjectView");
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(), "add-object-view"));
			dropdownItem.setTarget("event");
		};
	}

	private final ObjectRequestHelper _objectRequestHelper;

}