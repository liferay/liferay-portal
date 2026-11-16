/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link CTRemoteService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTRemoteService
 * @generated
 */
public class CTRemoteServiceWrapper
	implements CTRemoteService, ServiceWrapper<CTRemoteService> {

	public CTRemoteServiceWrapper() {
		this(null);
	}

	public CTRemoteServiceWrapper(CTRemoteService ctRemoteService) {
		_ctRemoteService = ctRemoteService;
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote addCTRemote(
			String name, String description, String url, String clientId,
			String clientSecret)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.addCTRemote(
			name, description, url, clientId, clientSecret);
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote deleteCTRemote(
			com.liferay.change.tracking.model.CTRemote ctRemote)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.deleteCTRemote(ctRemote);
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote deleteCTRemote(
			long ctRemoteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.deleteCTRemote(ctRemoteId);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTRemote>
			getCTRemotes(
				String keywords, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.change.tracking.model.CTRemote>
						orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.getCTRemotes(
			keywords, start, end, orderByComparator);
	}

	@Override
	public int getCTRemotesCount(String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.getCTRemotesCount(keywords);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctRemoteService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote updateCTRemote(
			long ctRemoteId, String name, String description, String url,
			String clientId, String clientSecret)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteService.updateCTRemote(
			ctRemoteId, name, description, url, clientId, clientSecret);
	}

	@Override
	public CTRemoteService getWrappedService() {
		return _ctRemoteService;
	}

	@Override
	public void setWrappedService(CTRemoteService ctRemoteService) {
		_ctRemoteService = ctRemoteService;
	}

	private CTRemoteService _ctRemoteService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1422940584