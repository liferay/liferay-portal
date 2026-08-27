/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ListTypeDefinitionService}.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeDefinitionService
 * @generated
 */
public class ListTypeDefinitionServiceWrapper
	implements ListTypeDefinitionService,
			   ServiceWrapper<ListTypeDefinitionService> {

	public ListTypeDefinitionServiceWrapper() {
		this(null);
	}

	public ListTypeDefinitionServiceWrapper(
		ListTypeDefinitionService listTypeDefinitionService) {

		_listTypeDefinitionService = listTypeDefinitionService;
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> nameMap, boolean system,
			java.util.List<com.liferay.list.type.model.ListTypeEntry>
				listTypeEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.addListTypeDefinition(
			externalReferenceCode, nameMap, system, listTypeEntries,
			serviceContext);
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			deleteListTypeDefinition(
				com.liferay.list.type.model.ListTypeDefinition
					listTypeDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.deleteListTypeDefinition(
			listTypeDefinition);
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			deleteListTypeDefinition(long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.deleteListTypeDefinition(
			listTypeDefinitionId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			fetchListTypeDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.
			fetchListTypeDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition getListTypeDefinition(
			long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.getListTypeDefinition(
			listTypeDefinitionId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			getListTypeDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.
			getListTypeDefinitionByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeDefinition>
		getListTypeDefinitions(int start, int end) {

		return _listTypeDefinitionService.getListTypeDefinitions(start, end);
	}

	@Override
	public int getListTypeDefinitionsCount() {
		return _listTypeDefinitionService.getListTypeDefinitionsCount();
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			getOrAddEmptyListTypeDefinition(
				String externalReferenceCode, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.getOrAddEmptyListTypeDefinition(
			externalReferenceCode, system);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _listTypeDefinitionService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.list.type.model.ListTypeDefinition
			updateListTypeDefinition(
				String externalReferenceCode, long listTypeDefinitionId,
				java.util.Map<java.util.Locale, String> nameMap,
				java.util.List<com.liferay.list.type.model.ListTypeEntry>
					listTypeEntries,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeDefinitionService.updateListTypeDefinition(
			externalReferenceCode, listTypeDefinitionId, nameMap,
			listTypeEntries, serviceContext);
	}

	@Override
	public ListTypeDefinitionService getWrappedService() {
		return _listTypeDefinitionService;
	}

	@Override
	public void setWrappedService(
		ListTypeDefinitionService listTypeDefinitionService) {

		_listTypeDefinitionService = listTypeDefinitionService;
	}

	private ListTypeDefinitionService _listTypeDefinitionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1396014755