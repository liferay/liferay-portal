/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectViewService}.
 *
 * @author Marco Leo
 * @see ObjectViewService
 * @generated
 */
public class ObjectViewServiceWrapper
	implements ObjectViewService, ServiceWrapper<ObjectViewService> {

	public ObjectViewServiceWrapper() {
		this(null);
	}

	public ObjectViewServiceWrapper(ObjectViewService objectViewService) {
		_objectViewService = objectViewService;
	}

	@Override
	public com.liferay.object.model.ObjectView addObjectView(
			String externalReferenceCode, long objectDefinitionId,
			boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewService.addObjectView(
			externalReferenceCode, objectDefinitionId, defaultObjectView,
			nameMap, objectViewColumns, objectViewFilterColumns,
			objectViewSortColumns);
	}

	@Override
	public com.liferay.object.model.ObjectView deleteObjectView(
			long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewService.deleteObjectView(objectViewId);
	}

	@Override
	public com.liferay.object.model.ObjectView getObjectView(long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewService.getObjectView(objectViewId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectViewService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectView updateObjectView(
			String externalReferenceCode, long objectViewId,
			boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewService.updateObjectView(
			externalReferenceCode, objectViewId, defaultObjectView, nameMap,
			objectViewColumns, objectViewFilterColumns, objectViewSortColumns);
	}

	@Override
	public ObjectViewService getWrappedService() {
		return _objectViewService;
	}

	@Override
	public void setWrappedService(ObjectViewService objectViewService) {
		_objectViewService = objectViewService;
	}

	private ObjectViewService _objectViewService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1460932483