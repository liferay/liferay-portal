/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for ObjectField. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectFieldServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectFieldService
 * @generated
 */
public class ObjectFieldServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFieldServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectField addCustomObjectField(
			String externalReferenceCode, long listTypeDefinitionId,
			long objectDefinitionId, String businessType, String dbType,
			Map<java.util.Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().addCustomObjectField(
			externalReferenceCode, listTypeDefinitionId, objectDefinitionId,
			businessType, dbType, descriptionMap, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	public static ObjectField deleteObjectField(long objectFieldId)
		throws Exception {

		return getService().deleteObjectField(objectFieldId);
	}

	public static ObjectField getObjectField(long objectFieldId)
		throws PortalException {

		return getService().getObjectField(objectFieldId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ObjectField updateObjectField(
			String externalReferenceCode, long objectFieldId,
			long listTypeDefinitionId, String businessType, String dbType,
			Map<java.util.Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws PortalException {

		return getService().updateObjectField(
			externalReferenceCode, objectFieldId, listTypeDefinitionId,
			businessType, dbType, descriptionMap, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	public static ObjectFieldService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectFieldService> _serviceSnapshot =
		new Snapshot<>(ObjectFieldServiceUtil.class, ObjectFieldService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1699148342