/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link PLOEntryService}.
 *
 * @author Drew Brokke
 * @see PLOEntryService
 * @generated
 */
public class PLOEntryServiceWrapper
	implements PLOEntryService, ServiceWrapper<PLOEntryService> {

	public PLOEntryServiceWrapper() {
		this(null);
	}

	public PLOEntryServiceWrapper(PLOEntryService ploEntryService) {
		_ploEntryService = ploEntryService;
	}

	@Override
	public com.liferay.portal.language.override.model.PLOEntry
			addOrUpdatePLOEntry(
				String externalReferenceCode, long userId, String key,
				String languageId, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.addOrUpdatePLOEntry(
			externalReferenceCode, userId, key, languageId, value);
	}

	@Override
	public void deletePLOEntries(String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ploEntryService.deletePLOEntries(key);
	}

	@Override
	public com.liferay.portal.language.override.model.PLOEntry deletePLOEntry(
			String key, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.deletePLOEntry(key, languageId);
	}

	@Override
	public com.liferay.portal.language.override.model.PLOEntry
			deletePLOEntryByExternalReferenceCode(String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.deletePLOEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ploEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.portal.language.override.model.PLOEntry>
			getPLOEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntries();
	}

	@Override
	public java.util.List<com.liferay.portal.language.override.model.PLOEntry>
			getPLOEntries(
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.language.override.model.PLOEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntries(start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.portal.language.override.model.PLOEntry>
			getPLOEntries(
				String keywords, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.portal.language.override.model.PLOEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntries(
			keywords, start, end, orderByComparator);
	}

	@Override
	public int getPLOEntriesCount()
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntriesCount();
	}

	@Override
	public int getPLOEntriesCount(String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntriesCount(keywords);
	}

	@Override
	public com.liferay.portal.language.override.model.PLOEntry
			getPLOEntryByExternalReferenceCode(String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ploEntryService.getPLOEntryByExternalReferenceCode(
			externalReferenceCode);
	}

	@Override
	public void importPLOEntries(
			String languageId, java.util.Properties properties)
		throws com.liferay.portal.kernel.exception.PortalException,
			   java.io.IOException {

		_ploEntryService.importPLOEntries(languageId, properties);
	}

	@Override
	public void setPLOEntries(
			String key, java.util.Map<java.util.Locale, String> localizationMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ploEntryService.setPLOEntries(key, localizationMap);
	}

	@Override
	public PLOEntryService getWrappedService() {
		return _ploEntryService;
	}

	@Override
	public void setWrappedService(PLOEntryService ploEntryService) {
		_ploEntryService = ploEntryService;
	}

	private PLOEntryService _ploEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2080420053