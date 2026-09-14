/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectFieldService}.
 *
 * @author Marco Leo
 * @see ObjectFieldService
 * @generated
 */
public class ObjectFieldServiceWrapper
	implements ObjectFieldService, ServiceWrapper<ObjectFieldService> {

	public ObjectFieldServiceWrapper() {
		this(null);
	}

	public ObjectFieldServiceWrapper(ObjectFieldService objectFieldService) {
		_objectFieldService = objectFieldService;
	}

	@Override
	public com.liferay.object.model.ObjectField addCustomObjectField(
			String externalReferenceCode, long listTypeDefinitionId,
			long objectDefinitionId, String businessType, String dbType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean indexed, boolean indexedAsKeyword, String indexedLanguageId,
			java.util.Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			java.util.List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFieldService.addCustomObjectField(
			externalReferenceCode, listTypeDefinitionId, objectDefinitionId,
			businessType, dbType, descriptionMap, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	@Override
	public com.liferay.object.model.ObjectField deleteObjectField(
			long objectFieldId)
		throws Exception {

		return _objectFieldService.deleteObjectField(objectFieldId);
	}

	@Override
	public com.liferay.object.model.ObjectField getObjectField(
			long objectFieldId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFieldService.getObjectField(objectFieldId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectFieldService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectField updateObjectField(
			String externalReferenceCode, long objectFieldId,
			long listTypeDefinitionId, String businessType, String dbType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean indexed, boolean indexedAsKeyword, String indexedLanguageId,
			java.util.Map<java.util.Locale, String> labelMap, boolean localized,
			String name, String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			java.util.List<com.liferay.object.model.ObjectFieldSetting>
				objectFieldSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFieldService.updateObjectField(
			externalReferenceCode, objectFieldId, listTypeDefinitionId,
			businessType, dbType, descriptionMap, indexed, indexedAsKeyword,
			indexedLanguageId, labelMap, localized, name, readOnly,
			readOnlyConditionExpression, required, state, objectFieldSettings);
	}

	@Override
	public ObjectFieldService getWrappedService() {
		return _objectFieldService;
	}

	@Override
	public void setWrappedService(ObjectFieldService objectFieldService) {
		_objectFieldService = objectFieldService;
	}

	private ObjectFieldService _objectFieldService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-77866930