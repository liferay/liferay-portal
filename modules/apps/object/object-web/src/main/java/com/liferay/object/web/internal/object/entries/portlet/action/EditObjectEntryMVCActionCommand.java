/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.portlet.action;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Marco Leo
 */
public class EditObjectEntryMVCActionCommand extends BaseMVCActionCommand {

	public EditObjectEntryMVCActionCommand(
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryService objectEntryService,
		ObjectRelatedModelsProviderRegistry objectRelatedModelsProviderRegistry,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		Portal portal) {

		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectEntryService = objectEntryService;
		_objectRelatedModelsProviderRegistry =
			objectRelatedModelsProviderRegistry;
		_objectRelationshipLocalService = objectRelationshipLocalService;
		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_portal = portal;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
			_addOrUpdateObjectEntry(actionRequest, actionResponse);
		}
		else if (cmd.equals("deleteRelatedModels")) {
			_objectEntryService.deleteObjectEntry(
				ParamUtil.getLong(actionRequest, "relatedModelId"));
		}
		else if (cmd.equals("disassociateRelatedModels")) {
			long objectRelationshipId = ParamUtil.getLong(
				actionRequest, "objectRelationshipId");

			ObjectRelationship objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					objectRelationshipId);

			ObjectRelatedModelsProvider objectRelatedModelsProvider =
				_objectRelatedModelsProviderRegistry.
					getObjectRelatedModelsProvider(
						ParamUtil.getString(actionRequest, "className"),
						objectRelationship.getCompanyId(),
						objectRelationship.getType());

			objectRelatedModelsProvider.disassociateRelatedModels(
				PrincipalThreadLocal.getUserId(), objectRelationshipId,
				ParamUtil.getLong(actionRequest, "objectEntryId"),
				ParamUtil.getLong(actionRequest, "relatedModelId"));
		}
	}

	private void _addOrUpdateObjectEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			long objectEntryId = ParamUtil.getLong(
				actionRequest, "objectEntryId");

			long objectDefinitionId = ParamUtil.getLong(
				actionRequest, "objectDefinitionId");

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectDefinitionId);

			if (objectEntryId == 0) {
				_objectEntryService.addObjectEntry(
					_getGroupId(actionRequest, objectDefinition),
					objectDefinition.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null, _getValues(actionRequest),
					ServiceContextFactory.getInstance(
						objectDefinition.getClassName(), actionRequest));
			}
			else {
				_objectEntryService.updateObjectEntry(
					objectEntryId, _getValues(actionRequest),
					ServiceContextFactory.getInstance(
						objectDefinition.getClassName(), actionRequest));
			}
		}
		catch (Exception exception) {
			if (exception instanceof ObjectDefinitionScopeException ||
				exception instanceof ObjectEntryValuesException) {

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				throw exception;
			}
		}
	}

	private long _getGroupId(
			ActionRequest actionRequest, ObjectDefinition objectDefinition)
		throws Exception {

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(
				objectDefinition.getScope());

		return objectScopeProvider.getGroupId(
			_portal.getHttpServletRequest(actionRequest));
	}

	private Map<String, Serializable> _getValues(ActionRequest actionRequest) {
		String ddmFormValues = ParamUtil.getString(
			actionRequest, "ddmFormValues");

		Map<String, Serializable> ddmFormValuesMap =
			(Map<String, Serializable>)JSONFactoryUtil.looseDeserialize(
				ddmFormValues);

		for (Map.Entry<String, Serializable> entry :
				ddmFormValuesMap.entrySet()) {

			Serializable value = entry.getValue();

			if (value == null) {
				continue;
			}

			Class<?> clazz = value.getClass();

			if (clazz != ArrayList.class) {
				continue;
			}

			String valueString = value.toString();

			ddmFormValuesMap.put(
				entry.getKey(),
				valueString.replaceAll("\\[|\\]|\"", StringPool.BLANK));
		}

		return ddmFormValuesMap;
	}

	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectEntryService _objectEntryService;
	private final ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;
	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final Portal _portal;

}