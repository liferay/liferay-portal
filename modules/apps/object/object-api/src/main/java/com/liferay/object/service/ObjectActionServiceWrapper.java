/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectActionService}.
 *
 * @author Marco Leo
 * @see ObjectActionService
 * @generated
 */
public class ObjectActionServiceWrapper
	implements ObjectActionService, ServiceWrapper<ObjectActionService> {

	public ObjectActionServiceWrapper() {
		this(null);
	}

	public ObjectActionServiceWrapper(ObjectActionService objectActionService) {
		_objectActionService = objectActionService;
	}

	@Override
	public com.liferay.object.model.ObjectAction addObjectAction(
			String externalReferenceCode, long objectDefinitionId,
			boolean active, String conditionExpression,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> errorMessageMap,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties,
			boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionService.addObjectAction(
			externalReferenceCode, objectDefinitionId, active,
			conditionExpression, descriptionMap, errorMessageMap, labelMap,
			name, objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	@Override
	public com.liferay.object.model.ObjectAction deleteObjectAction(
			long objectActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionService.deleteObjectAction(objectActionId);
	}

	@Override
	public com.liferay.object.model.ObjectAction getObjectAction(
			long objectActionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionService.getObjectAction(objectActionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectActionService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectAction updateObjectAction(
			String externalReferenceCode, long objectActionId, boolean active,
			String conditionExpression,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<java.util.Locale, String> errorMessageMap,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectActionService.updateObjectAction(
			externalReferenceCode, objectActionId, active, conditionExpression,
			descriptionMap, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties);
	}

	@Override
	public ObjectActionService getWrappedService() {
		return _objectActionService;
	}

	@Override
	public void setWrappedService(ObjectActionService objectActionService) {
		_objectActionService = objectActionService;
	}

	private ObjectActionService _objectActionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1001408135