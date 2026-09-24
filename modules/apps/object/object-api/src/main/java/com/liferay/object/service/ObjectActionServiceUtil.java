/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Map;

/**
 * Provides the remote service utility for ObjectAction. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectActionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectActionService
 * @generated
 */
public class ObjectActionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectActionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectAction addObjectAction(
			String externalReferenceCode, long objectDefinitionId,
			boolean active, String conditionExpression,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> errorMessageMap,
			Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties,
			boolean system)
		throws PortalException {

		return getService().addObjectAction(
			externalReferenceCode, objectDefinitionId, active,
			conditionExpression, descriptionMap, errorMessageMap, labelMap,
			name, objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	public static ObjectAction deleteObjectAction(long objectActionId)
		throws PortalException {

		return getService().deleteObjectAction(objectActionId);
	}

	public static ObjectAction getObjectAction(long objectActionId)
		throws PortalException {

		return getService().getObjectAction(objectActionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ObjectAction updateObjectAction(
			String externalReferenceCode, long objectActionId, boolean active,
			String conditionExpression,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> errorMessageMap,
			Map<java.util.Locale, String> labelMap, String name,
			String objectActionExecutorKey, String objectActionTriggerKey,
			com.liferay.portal.kernel.util.UnicodeProperties
				parametersUnicodeProperties)
		throws PortalException {

		return getService().updateObjectAction(
			externalReferenceCode, objectActionId, active, conditionExpression,
			descriptionMap, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties);
	}

	public static ObjectActionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectActionService> _serviceSnapshot =
		new Snapshot<>(
			ObjectActionServiceUtil.class, ObjectActionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-855011659