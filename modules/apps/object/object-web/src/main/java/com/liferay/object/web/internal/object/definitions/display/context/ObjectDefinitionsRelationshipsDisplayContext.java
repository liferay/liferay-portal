/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFieldService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.web.internal.display.context.helper.ObjectRequestHelper;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionsRelationshipsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsRelationshipsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectDefinitionService objectDefinitionService,
		ObjectFieldService objectFieldService,
		ObjectFolderLocalService objectFolderLocalService,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		super(
			httpServletRequest, objectDefinitionModelResourcePermission,
			objectFolderLocalService);

		_objectDefinitionService = objectDefinitionService;
		_objectFieldService = objectFieldService;
		_systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;

		_objectRequestHelper = new ObjectRequestHelper(httpServletRequest);
	}

	public String getEditObjectRelationshipURL() throws Exception {
		return PortletURLBuilder.create(
			getPortletURL()
		).setMVCRenderCommandName(
			"/object_definitions/edit_object_relationship"
		).setParameter(
			"objectRelationshipId", "{id}"
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		boolean hasUpdatePermission = hasUpdateObjectDefinitionPermission();

		return Arrays.asList(
			new FDSActionDropdownItem(
				getEditObjectRelationshipURL(),
				hasUpdatePermission ? "pencil" : "view",
				hasUpdatePermission ? "edit" : "view",
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					hasUpdatePermission ? "edit" : "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				null, "trash", "deleteObjectRelationship",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", null));
	}

	public JSONArray getObjectRelationshipDeletionTypesJSONArray() {
		return JSONUtil.putAll(
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(),
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE)
			).put(
				"value", ObjectRelationshipConstants.DELETION_TYPE_CASCADE
			)
		).put(
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(),
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)
			).put(
				"value", ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE
			)
		).put(
			JSONUtil.put(
				"label",
				LanguageUtil.get(
					_objectRequestHelper.getRequest(),
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)
			).put(
				"value", ObjectRelationshipConstants.DELETION_TYPE_PREVENT
			)
		);
	}

	public JSONObject getObjectRelationshipJSONObject(
			ObjectRelationship objectRelationship)
		throws PortalException {

		ObjectDefinition objectDefinition1 =
			_objectDefinitionService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());
		ObjectDefinition objectDefinition2 =
			_objectDefinitionService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		return JSONUtil.put(
			"deletionType", objectRelationship.getDeletionType()
		).put(
			"edge",
			FeatureFlagManagerUtil.isEnabled("LPD-34594") ?
				objectRelationship.isEdge() : null
		).put(
			"id", Long.valueOf(objectRelationship.getObjectRelationshipId())
		).put(
			"label", objectRelationship.getLabelMap()
		).put(
			"name", objectRelationship.getName()
		).put(
			"objectDefinitionExternalReferenceCode1",
			objectDefinition1.getExternalReferenceCode()
		).put(
			"objectDefinitionExternalReferenceCode2",
			objectDefinition2.getExternalReferenceCode()
		).put(
			"objectDefinitionId1",
			Long.valueOf(objectRelationship.getObjectDefinitionId1())
		).put(
			"objectDefinitionId2",
			Long.valueOf(objectRelationship.getObjectDefinitionId2())
		).put(
			"objectDefinitionName2", objectDefinition2.getShortName()
		).put(
			"parameterObjectFieldId",
			objectRelationship.getParameterObjectFieldId()
		).put(
			"parameterObjectFieldName",
			() -> {
				if (Validator.isNotNull(
						objectRelationship.getParameterObjectFieldId())) {

					ObjectField objectField =
						_objectFieldService.getObjectField(
							objectRelationship.getParameterObjectFieldId());

					return objectField.getName();
				}

				return StringPool.BLANK;
			}
		).put(
			"reverse", objectRelationship.isReverse()
		).put(
			"system", objectRelationship.isSystem()
		).put(
			"type", objectRelationship.getType()
		);
	}

	public Set<String> getObjectRelationshipTypes(
		ObjectDefinition objectDefinition) {

		return ObjectRelationshipUtil.getObjectRelationshipTypes(
			objectDefinition, _systemObjectDefinitionManagerRegistry);
	}

	public String getRESTContextPath(ObjectDefinition objectDefinition) {
		return ObjectRelationshipUtil.getRESTContextPath(
			objectDefinition, _systemObjectDefinitionManagerRegistry);
	}

	public boolean isParameterRequired(ObjectDefinition objectDefinition) {
		return ObjectRelationshipUtil.isParameterRequired(
			objectDefinition, _systemObjectDefinitionManagerRegistry);
	}

	@Override
	protected String getAPIURI() {
		return "/object-relationships";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref("addObjectRelationship");
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(),
					"add-object-relationship"));
			dropdownItem.setTarget("event");
		};
	}

	private final ObjectDefinitionService _objectDefinitionService;
	private final ObjectFieldService _objectFieldService;
	private final ObjectRequestHelper _objectRequestHelper;
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}