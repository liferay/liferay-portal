/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AudiencesEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryService
 * @generated
 */
public class AudiencesEntryServiceWrapper
	implements AudiencesEntryService, ServiceWrapper<AudiencesEntryService> {

	public AudiencesEntryServiceWrapper() {
		this(null);
	}

	public AudiencesEntryServiceWrapper(
		AudiencesEntryService audiencesEntryService) {

		_audiencesEntryService = audiencesEntryService;
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.addAudiencesEntry(
			externalReferenceCode, json, name);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry deleteAudiencesEntry(
			long audiencesEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.deleteAudiencesEntry(audiencesEntryId);
	}

	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntry>
			getAudiencesEntries(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.audiences.model.AudiencesEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.getAudiencesEntries(
			companyId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<com.liferay.audiences.model.AudiencesEntry>
			getAudiencesEntries(
				long companyId, String name, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.audiences.model.AudiencesEntry>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.getAudiencesEntries(
			companyId, name, start, end, orderByComparator);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.getAudiencesEntriesCount(companyId);
	}

	@Override
	public int getAudiencesEntriesCount(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.getAudiencesEntriesCount(companyId, name);
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry getAudiencesEntry(
			long audiencesEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.getAudiencesEntry(audiencesEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _audiencesEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.audiences.model.AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _audiencesEntryService.updateAudiencesEntry(
			audiencesEntryId, externalReferenceCode, json, name);
	}

	@Override
	public AudiencesEntryService getWrappedService() {
		return _audiencesEntryService;
	}

	@Override
	public void setWrappedService(AudiencesEntryService audiencesEntryService) {
		_audiencesEntryService = audiencesEntryService;
	}

	private AudiencesEntryService _audiencesEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2051514838