/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.change.tracking.model.CTRemote;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CTRemote. This utility wraps
 * <code>com.liferay.change.tracking.service.impl.CTRemoteServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see CTRemoteService
 * @generated
 */
public class CTRemoteServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.service.impl.CTRemoteServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CTRemote addCTRemote(
			String name, String description, String url, String clientId,
			String clientSecret)
		throws PortalException {

		return getService().addCTRemote(
			name, description, url, clientId, clientSecret);
	}

	public static CTRemote deleteCTRemote(CTRemote ctRemote)
		throws PortalException {

		return getService().deleteCTRemote(ctRemote);
	}

	public static CTRemote deleteCTRemote(long ctRemoteId)
		throws PortalException {

		return getService().deleteCTRemote(ctRemoteId);
	}

	public static List<CTRemote> getCTRemotes(
			String keywords, int start, int end,
			OrderByComparator<CTRemote> orderByComparator)
		throws PortalException {

		return getService().getCTRemotes(
			keywords, start, end, orderByComparator);
	}

	public static int getCTRemotesCount(String keywords)
		throws PortalException {

		return getService().getCTRemotesCount(keywords);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CTRemote updateCTRemote(
			long ctRemoteId, String name, String description, String url,
			String clientId, String clientSecret)
		throws PortalException {

		return getService().updateCTRemote(
			ctRemoteId, name, description, url, clientId, clientSecret);
	}

	public static CTRemoteService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTRemoteService> _serviceSnapshot =
		new Snapshot<>(CTRemoteServiceUtil.class, CTRemoteService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-269691912