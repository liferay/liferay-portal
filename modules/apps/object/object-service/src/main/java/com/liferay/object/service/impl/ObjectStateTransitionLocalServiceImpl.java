/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.model.ObjectStateTransitionModel;
import com.liferay.object.service.base.ObjectStateTransitionLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectStateTransition",
	service = AopService.class
)
public class ObjectStateTransitionLocalServiceImpl
	extends ObjectStateTransitionLocalServiceBaseImpl {

	@Override
	public ObjectStateTransition addObjectStateTransition(
			long userId, long objectStateFlowId, long sourceObjectStateId,
			long targetObjectStateId)
		throws PortalException {

		ObjectStateTransition objectStateTransition =
			createObjectStateTransition(counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectStateTransition.setCompanyId(user.getCompanyId());
		objectStateTransition.setUserId(user.getUserId());
		objectStateTransition.setUserName(user.getFullName());

		objectStateTransition.setObjectStateFlowId(objectStateFlowId);
		objectStateTransition.setSourceObjectStateId(sourceObjectStateId);
		objectStateTransition.setTargetObjectStateId(targetObjectStateId);

		return addObjectStateTransition(objectStateTransition);
	}

	@Override
	public void deleteObjectStateFlowObjectStateTransitions(
		long objectStateFlowId) {

		objectStateTransitionPersistence.removeByObjectStateFlowId(
			objectStateFlowId);
	}

	@Override
	public void deleteObjectStateObjectStateTransitions(long objectStateId) {
		objectStateTransitionPersistence.removeBySourceObjectStateId(
			objectStateId);

		objectStateTransitionPersistence.removeByTargetObjectStateId(
			objectStateId);
	}

	@Override
	public List<ObjectStateTransition> getObjectStateFlowObjectStateTransitions(
		long objectStateFlowId) {

		return objectStateTransitionPersistence.findByObjectStateFlowId(
			objectStateFlowId);
	}

	@Override
	public List<ObjectStateTransition> getObjectStateObjectStateTransitions(
		long objectStateId) {

		return objectStateTransitionPersistence.findBySourceObjectStateId(
			objectStateId);
	}

	@Override
	public void updateObjectStateTransitions(ObjectStateFlow objectStateFlow)
		throws PortalException {

		if (objectStateFlow == null) {
			return;
		}

		for (ObjectState objectState : objectStateFlow.getObjectStates()) {
			_updateObjectStateTransitions(
				objectState.getObjectStateId(),
				objectState.getObjectStateTransitions());
		}
	}

	private void _updateObjectStateTransitions(
			long objectStateId,
			List<ObjectStateTransition> objectStateTransitions)
		throws PortalException {

		List<ObjectStateTransition> persistedObjectStateTransitions =
			objectStateTransitionPersistence.findBySourceObjectStateId(
				objectStateId);

		if (persistedObjectStateTransitions.isEmpty()) {
			User user = _userLocalService.fetchUser(
				PrincipalThreadLocal.getUserId());

			for (ObjectStateTransition objectStateTransition :
					objectStateTransitions) {

				addObjectStateTransition(
					user.getUserId(),
					objectStateTransition.getObjectStateFlowId(),
					objectStateTransition.getSourceObjectStateId(),
					objectStateTransition.getTargetObjectStateId());
			}

			return;
		}

		if (objectStateTransitions.isEmpty()) {
			for (ObjectStateTransition objectStateTransition :
					persistedObjectStateTransitions) {

				objectStateTransitionPersistence.remove(
					objectStateTransition.getObjectStateTransitionId());
			}

			return;
		}

		List<Long> targetObjectStateIds = ListUtil.toList(
			objectStateTransitions,
			ObjectStateTransitionModel::getTargetObjectStateId);

		for (ObjectStateTransition objectStateTransition :
				persistedObjectStateTransitions) {

			if (targetObjectStateIds.contains(
					objectStateTransition.getTargetObjectStateId())) {

				continue;
			}

			objectStateTransitionPersistence.remove(
				objectStateTransition.getObjectStateTransitionId());
		}

		List<Long> persistedTargetObjectStateIds = ListUtil.toList(
			persistedObjectStateTransitions,
			ObjectStateTransitionModel::getTargetObjectStateId);

		for (ObjectStateTransition objectStateTransition :
				objectStateTransitions) {

			if (persistedTargetObjectStateIds.contains(
					objectStateTransition.getTargetObjectStateId())) {

				continue;
			}

			User user = _userLocalService.fetchUser(
				PrincipalThreadLocal.getUserId());

			if (user == null) {
				return;
			}

			addObjectStateTransition(
				user.getUserId(), objectStateTransition.getObjectStateFlowId(),
				objectStateTransition.getSourceObjectStateId(),
				objectStateTransition.getTargetObjectStateId());
		}
	}

	@Reference
	private UserLocalService _userLocalService;

}