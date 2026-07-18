/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.service.base.ObjectStateFlowLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectFieldSettingPersistence;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectStateFlow",
	service = AopService.class
)
public class ObjectStateFlowLocalServiceImpl
	extends ObjectStateFlowLocalServiceBaseImpl {

	@Override
	public ObjectStateFlow addDefaultObjectStateFlow(ObjectField objectField)
		throws PortalException {

		if (!objectField.isState()) {
			return null;
		}

		ObjectStateFlow objectStateFlow = _addObjectStateFlow(
			objectField.getUserId(), objectField.getObjectFieldId());

		List<ObjectState> objectStates = TransformUtil.transform(
			_listTypeEntryLocalService.getListTypeEntries(
				objectField.getListTypeDefinitionId()),
			listTypeEntry -> _objectStateLocalService.addObjectState(
				objectField.getUserId(), listTypeEntry.getListTypeEntryId(),
				objectStateFlow.getObjectStateFlowId()));

		for (ObjectState sourceObjectState : objectStates) {
			for (ObjectState targetObjectState : objectStates) {
				if (sourceObjectState.equals(targetObjectState)) {
					continue;
				}

				_objectStateTransitionLocalService.addObjectStateTransition(
					objectField.getUserId(),
					objectStateFlow.getObjectStateFlowId(),
					sourceObjectState.getObjectStateId(),
					targetObjectState.getObjectStateId());
			}
		}

		_addOrUpdateObjectFieldSetting(
			objectField.getUserId(), objectField.getObjectFieldId(),
			objectStateFlow.getObjectStateFlowId());

		return objectStateFlow;
	}

	@Override
	public ObjectStateFlow addObjectStateFlow(
			long userId, long objectFieldId, List<ObjectState> objectStates)
		throws PortalException {

		ObjectStateFlow objectStateFlow = _addObjectStateFlow(
			userId, objectFieldId);

		_addObjectStatesAndObjectStateTransitions(
			userId, objectStateFlow.getObjectStateFlowId(), objectStates);

		_addOrUpdateObjectFieldSetting(
			userId, objectFieldId, objectStateFlow.getObjectStateFlowId());

		return objectStateFlow;
	}

	@Override
	public void deleteObjectFieldObjectStateFlow(long objectFieldId)
		throws PortalException {

		ObjectStateFlow objectStateFlow =
			objectStateFlowPersistence.fetchByObjectFieldId(objectFieldId);

		if (objectStateFlow == null) {
			return;
		}

		objectStateFlowPersistence.remove(
			objectStateFlow.getObjectStateFlowId());

		_objectStateLocalService.deleteObjectStateFlowObjectStates(
			objectStateFlow.getObjectStateFlowId());

		_objectStateTransitionLocalService.
			deleteObjectStateFlowObjectStateTransitions(
				objectStateFlow.getObjectStateFlowId());

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingPersistence.fetchByOFI_N(
				objectFieldId, ObjectFieldSettingConstants.NAME_STATE_FLOW);

		if (objectFieldSetting != null) {
			_objectFieldSettingLocalService.deleteObjectFieldSetting(
				objectFieldSetting.getObjectFieldSettingId());
		}
	}

	@Override
	public ObjectStateFlow fetchObjectFieldObjectStateFlow(long objectFieldId) {
		return objectStateFlowPersistence.fetchByObjectFieldId(objectFieldId);
	}

	@Override
	public ObjectStateFlow updateDefaultObjectStateFlow(
			ObjectField newObjectField, ObjectField oldObjectField)
		throws PortalException {

		if (!oldObjectField.isState() && !newObjectField.isState()) {
			return null;
		}

		if (oldObjectField.isState() && !newObjectField.isState()) {
			deleteObjectFieldObjectStateFlow(oldObjectField.getObjectFieldId());

			return null;
		}

		if (!oldObjectField.isState() && newObjectField.isState()) {
			return addDefaultObjectStateFlow(newObjectField);
		}

		if (oldObjectField.getListTypeDefinitionId() ==
				newObjectField.getListTypeDefinitionId()) {

			return null;
		}

		deleteObjectFieldObjectStateFlow(oldObjectField.getObjectFieldId());

		return addDefaultObjectStateFlow(newObjectField);
	}

	@Override
	public void updateObjectStateFlow(
			long userId, long objectStateFlowId, List<ObjectState> objectStates)
		throws PortalException {

		_objectStateTransitionLocalService.
			deleteObjectStateFlowObjectStateTransitions(objectStateFlowId);

		_objectStateLocalService.deleteObjectStateFlowObjectStates(
			objectStateFlowId);

		_addObjectStatesAndObjectStateTransitions(
			userId, objectStateFlowId, objectStates);
	}

	private ObjectStateFlow _addObjectStateFlow(long userId, long objectFieldId)
		throws PortalException {

		ObjectStateFlow objectStateFlow = objectStateFlowPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectStateFlow.setCompanyId(user.getCompanyId());
		objectStateFlow.setUserId(user.getUserId());
		objectStateFlow.setUserName(user.getFullName());

		objectStateFlow.setObjectFieldId(objectFieldId);

		return objectStateFlowPersistence.update(objectStateFlow);
	}

	private void _addObjectStatesAndObjectStateTransitions(
			long userId, long objectStateFlowId, List<ObjectState> objectStates)
		throws PortalException {

		Map<Long, Long> objectStateIds = new HashMap<>();

		List<ObjectState> sourceObjectStates = TransformUtil.transform(
			objectStates,
			objectState -> {
				ObjectState sourceObjectState =
					_objectStateLocalService.addObjectState(
						userId, objectState.getListTypeEntryId(),
						objectStateFlowId);

				sourceObjectState.setObjectStateTransitions(
					objectState.getObjectStateTransitions());

				objectStateIds.put(
					sourceObjectState.getListTypeEntryId(),
					sourceObjectState.getObjectStateId());

				return sourceObjectState;
			});

		for (ObjectState sourceObjectState : sourceObjectStates) {
			for (ObjectStateTransition objectStateTransition :
					sourceObjectState.getObjectStateTransitions()) {

				_objectStateTransitionLocalService.addObjectStateTransition(
					userId, objectStateFlowId,
					sourceObjectState.getObjectStateId(),
					objectStateIds.get(
						objectStateTransition.
							getTargetObjectStateListTypeEntryId()));
			}
		}
	}

	private void _addOrUpdateObjectFieldSetting(
			long userId, long objectFieldId, long objectStateFlowId)
		throws PortalException {

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingPersistence.fetchByOFI_N(
				objectFieldId, ObjectFieldSettingConstants.NAME_STATE_FLOW);

		if (objectFieldSetting == null) {
			_objectFieldSettingLocalService.addObjectFieldSetting(
				userId, objectFieldId,
				ObjectFieldSettingConstants.NAME_STATE_FLOW,
				String.valueOf(objectStateFlowId));
		}
		else {
			_objectFieldSettingLocalService.updateObjectFieldSetting(
				objectFieldSetting.getObjectFieldSettingId(),
				String.valueOf(objectStateFlowId));
		}
	}

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Reference
	private ObjectFieldSettingPersistence _objectFieldSettingPersistence;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

	@Reference
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}