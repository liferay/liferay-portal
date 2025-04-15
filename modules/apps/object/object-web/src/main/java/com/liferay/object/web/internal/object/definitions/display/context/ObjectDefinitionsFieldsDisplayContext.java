/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.object.admin.rest.dto.v1_0.util.ObjectFieldUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.web.internal.util.ObjectFieldBusinessTypeUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Marco Leo
 * @author Gabriel Albuquerque
 */
public class ObjectDefinitionsFieldsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsFieldsDisplayContext(
		HttpServletRequest httpServletRequest,
		ListTypeDefinitionService listTypeDefinitionService,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry,
		ObjectFolderLocalService objectFolderLocalService) {

		super(
			httpServletRequest, objectDefinitionModelResourcePermission,
			objectFolderLocalService);

		_listTypeDefinitionService = listTypeDefinitionService;
		_objectFieldBusinessTypeRegistry = objectFieldBusinessTypeRegistry;
	}

	public CreationMenu getCreationMenu(ObjectDefinition objectDefinition)
		throws PortalException {

		CreationMenu creationMenu = new CreationMenu();

		if (!hasUpdateObjectDefinitionPermission()) {
			return creationMenu;
		}

		creationMenu.addDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("addObjectField");
				dropdownItem.setLabel(
					LanguageUtil.get(
						objectRequestHelper.getRequest(), "add-object-field"));
				dropdownItem.setTarget("event");
			});

		return creationMenu;
	}

	public String getEditObjectFieldURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_field"
		).setParameter(
			"objectFieldId", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		boolean hasUpdatePermission = hasUpdateObjectDefinitionPermission();

		return Arrays.asList(
			new FDSActionDropdownItem(
				getEditObjectFieldURL(),
				hasUpdatePermission ? "pencil" : "view",
				hasUpdatePermission ? "edit" : "view",
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					hasUpdatePermission ? "edit" : "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				null, "trash", "deleteObjectField",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", null));
	}

	public String[] getForbiddenLastCharacters() {
		List<String> forbiddenLastCharacters = new ArrayList<>();

		for (String forbiddenLastCharacter :
				PropsValues.DL_CHAR_LAST_BLACKLIST) {

			if (forbiddenLastCharacter.startsWith(
					UnicodeFormatter.UNICODE_PREFIX)) {

				forbiddenLastCharacter = UnicodeFormatter.parseString(
					forbiddenLastCharacter);
			}

			forbiddenLastCharacters.add(forbiddenLastCharacter);
		}

		return forbiddenLastCharacters.toArray(new String[0]);
	}

	public List<Map<String, String>> getObjectFieldBusinessTypeMaps(
		Locale locale) {

		return ObjectFieldBusinessTypeUtil.getObjectFieldBusinessTypeMaps(
			locale,
			ListUtil.filter(
				_objectFieldBusinessTypeRegistry.getObjectFieldBusinessTypes(),
				objectFieldBusinessType ->
					objectFieldBusinessType.isVisible(getObjectDefinition()) &&
					!StringUtil.equals(
						objectFieldBusinessType.getName(),
						ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)));
	}

	public JSONObject getObjectFieldJSONObject(ObjectField objectField) {
		return ObjectFieldUtil.toJSONObject(
			_listTypeDefinitionService, objectField);
	}

	@Override
	protected String getAPIURI() {
		return "/object-fields";
	}

	private final ListTypeDefinitionService _listTypeDefinitionService;
	private final ObjectFieldBusinessTypeRegistry
		_objectFieldBusinessTypeRegistry;

}