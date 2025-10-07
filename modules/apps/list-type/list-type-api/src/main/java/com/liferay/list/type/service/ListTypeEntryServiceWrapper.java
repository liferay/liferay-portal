/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ListTypeEntryService}.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeEntryService
 * @generated
 */
public class ListTypeEntryServiceWrapper
	implements ListTypeEntryService, ServiceWrapper<ListTypeEntryService> {

	public ListTypeEntryServiceWrapper() {
		this(null);
	}

	public ListTypeEntryServiceWrapper(
		ListTypeEntryService listTypeEntryService) {

		_listTypeEntryService = listTypeEntryService;
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long listTypeDefinitionId, String key,
			java.util.Map<java.util.Locale, String> nameMap, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.addListTypeEntry(
			externalReferenceCode, listTypeDefinitionId, key, nameMap, system);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry deleteListTypeEntry(
			long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.deleteListTypeEntry(listTypeEntryId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry fetchListTypeEntry(
			long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.fetchListTypeEntry(
			listTypeDefinitionId, key);
	}

	@Override
	public java.util.List<com.liferay.list.type.model.ListTypeEntry>
			getListTypeEntries(long listTypeDefinitionId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.getListTypeEntries(
			listTypeDefinitionId, start, end);
	}

	@Override
	public int getListTypeEntriesCount(long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.getListTypeEntriesCount(
			listTypeDefinitionId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry getListTypeEntry(
			long listTypeEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.getListTypeEntry(listTypeEntryId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry
			getListTypeEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId,
				long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.getListTypeEntryByExternalReferenceCode(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry getOrAddEmptyListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.getOrAddEmptyListTypeEntry(
			userId, listTypeDefinitionId, key);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _listTypeEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.list.type.model.ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			java.util.Map<java.util.Locale, String> nameMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _listTypeEntryService.updateListTypeEntry(
			externalReferenceCode, listTypeEntryId, nameMap);
	}

	@Override
	public ListTypeEntryService getWrappedService() {
		return _listTypeEntryService;
	}

	@Override
	public void setWrappedService(ListTypeEntryService listTypeEntryService) {
		_listTypeEntryService = listTypeEntryService;
	}

	private ListTypeEntryService _listTypeEntryService;

}