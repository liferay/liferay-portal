/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.ExceptionMapperUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = "path=/cms/update-structure", service = StrutsAction.class
)
public class UpdateStructureStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		try {
			JSONArray deletedObjectRelationshipsJSONArray =
				_jsonFactory.createJSONArray(
					ParamUtil.getString(
						httpServletRequest, "deletedObjectRelationships"));
			String[] deletedRepeatableGroupsERCs = ParamUtil.getStringValues(
				httpServletRequest, "deletedRepeatableGroupsERCs");
			String objectDefinitionJSON = ParamUtil.getString(
				httpServletRequest, "objectDefinition");
			JSONArray objectRelationshipsJSONArray =
				_jsonFactory.createJSONArray(
					ParamUtil.getString(
						httpServletRequest, "objectRelationships"));
			JSONArray repeatableGroupObjectDefinitionsJSONArray =
				_jsonFactory.createJSONArray(
					ParamUtil.getString(
						httpServletRequest,
						"repeatableGroupObjectDefinitions"));

			_updateStructure(
				deletedObjectRelationshipsJSONArray,
				deletedRepeatableGroupsERCs, httpServletRequest,
				objectDefinitionJSON, objectRelationshipsJSONArray,
				repeatableGroupObjectDefinitionsJSONArray);
		}
		catch (Exception exception) {
			jsonObject = _jsonFactory.createJSONObject();

			jsonObject.put(
				"error",
				_language.get(
					httpServletRequest.getLocale(),
					"an-unexpected-error-occurred"));

			Throwable throwable = exception;

			while (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}

			Class<?> clazz = throwable.getClass();

			jsonObject.put(
				"type", ExceptionMapperUtil.getType(clazz.getName()));

			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());

		return null;
	}

	private void _deleteRelationships(long objectDefinitionId)
		throws Exception {

		for (com.liferay.object.model.ObjectRelationship
				serviceBuilderObjectRelationship :
					_objectRelationshipLocalService.
						getObjectRelationshipsByObjectDefinitionId2(
							objectDefinitionId, true)) {

			if (serviceBuilderObjectRelationship.isReverse()) {
				continue;
			}

			if (serviceBuilderObjectRelationship.isEdge()) {
				serviceBuilderObjectRelationship =
					_objectRelationshipLocalService.updateObjectRelationship(
						serviceBuilderObjectRelationship.
							getExternalReferenceCode(),
						serviceBuilderObjectRelationship.
							getObjectRelationshipId(),
						serviceBuilderObjectRelationship.
							getParameterObjectFieldId(),
						serviceBuilderObjectRelationship.getDeletionType(),
						false, serviceBuilderObjectRelationship.getLabelMap(),
						null);
			}

			_objectRelationshipLocalService.deleteObjectRelationship(
				serviceBuilderObjectRelationship);
		}
	}

	private ObjectDefinitionResource _getObjectDefinitionResource(User user) {
		ObjectDefinitionResource.Builder builder =
			_objectDefinitionResourceFactory.create();

		builder.user(user);

		return builder.build();
	}

	private List<ObjectDefinition> _getObjectDefinitions(
		JSONArray objectDefinitionsJSONArray) {

		List<ObjectDefinition> objectDefinitions = new ArrayList<>();

		for (int i = 0; i < objectDefinitionsJSONArray.length(); i++) {
			ObjectDefinition objectDefinition = ObjectDefinition.toDTO(
				String.valueOf(objectDefinitionsJSONArray.getJSONObject(i)));

			objectDefinitions.add(objectDefinition);
		}

		return objectDefinitions;
	}

	private ObjectRelationshipResource _getObjectRelationshipResource(
		User user) {

		ObjectRelationshipResource.Builder builder =
			_objectRelationshipResourceFactory.create();

		builder.user(user);

		return builder.build();
	}

	private List<ObjectRelationship> _getObjectRelationships(
		JSONArray objectRelationshipsJSONArray) {

		List<ObjectRelationship> objectRelationships = new ArrayList<>();

		for (int i = 0; i < objectRelationshipsJSONArray.length(); i++) {
			ObjectRelationship objectRelationship = ObjectRelationship.toDTO(
				String.valueOf(objectRelationshipsJSONArray.getJSONObject(i)));

			objectRelationships.add(objectRelationship);
		}

		return objectRelationships;
	}

	private void _updateObjectRelationships(
			ObjectDefinition objectDefinition, long objectDefinitionId,
			ObjectDefinitionResource objectDefinitionResource)
		throws Exception {

		ObjectDefinition existingObjectDefinition =
			objectDefinitionResource.getObjectDefinition(objectDefinitionId);

		ObjectRelationship[] existingObjectRelationships =
			existingObjectDefinition.getObjectRelationships();

		if (ArrayUtil.isEmpty(existingObjectRelationships)) {
			return;
		}

		Map<String, ObjectRelationship> objectRelationshipsMap =
			new LinkedHashMap<>();

		ObjectRelationship[] objectRelationships =
			objectDefinition.getObjectRelationships();

		if (objectRelationships != null) {
			for (ObjectRelationship objectRelationship : objectRelationships) {
				objectRelationshipsMap.put(
					objectRelationship.getName(), objectRelationship);
			}
		}

		for (ObjectRelationship existingObjectRelationship :
				existingObjectRelationships) {

			objectRelationshipsMap.putIfAbsent(
				existingObjectRelationship.getName(),
				existingObjectRelationship);
		}

		Collection<ObjectRelationship> objectRelationshipsCollection =
			objectRelationshipsMap.values();

		objectDefinition.setObjectRelationships(
			() -> objectRelationshipsCollection.toArray(
				new ObjectRelationship[0]));
	}

	private void _updateStructure(
			JSONArray deletedObjectRelationshipsJSONArray,
			String[] deletedRepeatableGroupsERCs,
			HttpServletRequest httpServletRequest, String objectDefinitionJSON,
			JSONArray objectRelationshipsJSONArray,
			JSONArray repeatableGroupObjectDefinitionsJSONArray)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONObject objectDefinitionJSONObject = _jsonFactory.createJSONObject(
			objectDefinitionJSON);

		Callable<Void> callable = new UpdateStructureCallable(
			themeDisplay.getCompanyId(), deletedObjectRelationshipsJSONArray,
			deletedRepeatableGroupsERCs,
			ObjectDefinition.toDTO(objectDefinitionJSON),
			objectDefinitionJSONObject.getLong("id"),
			_getObjectRelationships(objectRelationshipsJSONArray),
			_getObjectDefinitions(repeatableGroupObjectDefinitionsJSONArray),
			themeDisplay.getUser());

		try {
			TransactionInvokerUtil.invoke(_transactionConfig, callable);
		}
		catch (Throwable throwable) {
			if (throwable instanceof Exception) {
				throw (Exception)throwable;
			}

			throw new Exception(throwable);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateStructureStrutsAction.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ObjectRelationshipResource.Factory
		_objectRelationshipResourceFactory;

	private class UpdateStructureCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			if (!JSONUtil.isEmpty(_deletedObjectRelationshipsJSONArray)) {
				for (int i = 0;
					 i < _deletedObjectRelationshipsJSONArray.length(); i++) {

					JSONObject jsonObject =
						_deletedObjectRelationshipsJSONArray.getJSONObject(i);

					String objectDefinitionERC = jsonObject.getString(
						"objectDefinitionERC");

					com.liferay.object.model.ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.
							fetchObjectDefinitionByExternalReferenceCode(
								objectDefinitionERC, _companyId);

					if (objectDefinition == null) {
						continue;
					}

					com.liferay.object.model.ObjectRelationship
						serviceBuilderObjectRelationship =
							_objectRelationshipLocalService.
								getObjectRelationshipByExternalReferenceCode(
									jsonObject.getString(
										"objectRelationshipERC"),
									_companyId,
									objectDefinition.getObjectDefinitionId());

					if (serviceBuilderObjectRelationship.isEdge()) {
						serviceBuilderObjectRelationship =
							_objectRelationshipLocalService.
								updateObjectRelationship(
									serviceBuilderObjectRelationship.
										getExternalReferenceCode(),
									serviceBuilderObjectRelationship.
										getObjectRelationshipId(),
									serviceBuilderObjectRelationship.
										getParameterObjectFieldId(),
									serviceBuilderObjectRelationship.
										getDeletionType(),
									false,
									serviceBuilderObjectRelationship.
										getLabelMap(),
									null);
					}

					_objectRelationshipLocalService.deleteObjectRelationship(
						serviceBuilderObjectRelationship);
				}
			}

			if (ArrayUtil.isNotEmpty(_deletedRepeatableGroupsERCs)) {
				for (String repeatableGroupERC : _deletedRepeatableGroupsERCs) {
					com.liferay.object.model.ObjectDefinition objectDefinition =
						_objectDefinitionLocalService.
							getObjectDefinitionByExternalReferenceCode(
								repeatableGroupERC, _companyId);

					_objectDefinitionService.deleteObjectDefinition(
						objectDefinition.getObjectDefinitionId());
				}
			}

			ObjectDefinitionResource objectDefinitionResource =
				_getObjectDefinitionResource(_user);

			if (ListUtil.isNotEmpty(_repeatableGroupObjectDefinitions)) {
				for (ObjectDefinition objectDefinition :
						_repeatableGroupObjectDefinitions) {

					objectDefinitionResource.
						putObjectDefinitionByExternalReferenceCode(
							objectDefinition.getExternalReferenceCode(),
							objectDefinition);
				}
			}

			_updateObjectRelationships(
				_objectDefinition, _objectDefinitionId,
				objectDefinitionResource);

			objectDefinitionResource.putObjectDefinition(
				_objectDefinitionId, _objectDefinition);

			com.liferay.object.model.ObjectDefinition
				serviceBuilderObjectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByExternalReferenceCode(
							_objectDefinition.getExternalReferenceCode(),
							_companyId);

			if (serviceBuilderObjectDefinition != null) {
				_deleteRelationships(
					serviceBuilderObjectDefinition.getObjectDefinitionId());
			}

			if (ListUtil.isNotEmpty(_objectRelationships)) {
				ObjectRelationshipResource objectRelationshipResource =
					_getObjectRelationshipResource(_user);

				for (ObjectRelationship objectRelationship :
						_objectRelationships) {

					objectRelationshipResource.
						postObjectDefinitionByExternalReferenceCodeObjectRelationship(
							objectRelationship.
								getObjectDefinitionExternalReferenceCode1(),
							objectRelationship);
				}
			}

			return null;
		}

		private UpdateStructureCallable(
			long companyId, JSONArray deletedObjectRelationshipsJSONArray,
			String[] deletedRepeatableGroupsERCs,
			ObjectDefinition objectDefinition, long objectDefinitionId,
			List<ObjectRelationship> objectRelationships,
			List<ObjectDefinition> repeatableGroupObjectDefinitions,
			User user) {

			_companyId = companyId;
			_deletedObjectRelationshipsJSONArray =
				deletedObjectRelationshipsJSONArray;
			_deletedRepeatableGroupsERCs = deletedRepeatableGroupsERCs;
			_objectDefinition = objectDefinition;
			_objectDefinitionId = objectDefinitionId;
			_objectRelationships = objectRelationships;
			_repeatableGroupObjectDefinitions =
				repeatableGroupObjectDefinitions;
			_user = user;
		}

		private final long _companyId;
		private final JSONArray _deletedObjectRelationshipsJSONArray;
		private final String[] _deletedRepeatableGroupsERCs;
		private final ObjectDefinition _objectDefinition;
		private final long _objectDefinitionId;
		private final List<ObjectRelationship> _objectRelationships;
		private final List<ObjectDefinition> _repeatableGroupObjectDefinitions;
		private final User _user;

	}

}