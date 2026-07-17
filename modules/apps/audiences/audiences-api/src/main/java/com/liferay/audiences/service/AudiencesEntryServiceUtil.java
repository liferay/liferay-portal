/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for AudiencesEntry. This utility wraps
 * <code>com.liferay.audiences.service.impl.AudiencesEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryService
 * @generated
 */
public class AudiencesEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.audiences.service.impl.AudiencesEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static AudiencesEntry addAudiencesEntry(
			String externalReferenceCode, String json, String name)
		throws PortalException {

		return getService().addAudiencesEntry(
			externalReferenceCode, json, name);
	}

	public static AudiencesEntry deleteAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		return getService().deleteAudiencesEntry(audiencesEntryId);
	}

	public static List<AudiencesEntry> getAudiencesEntries(
			long companyId, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		return getService().getAudiencesEntries(
			companyId, start, end, orderByComparator);
	}

	public static List<AudiencesEntry> getAudiencesEntries(
			long companyId, String name, int start, int end,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws PortalException {

		return getService().getAudiencesEntries(
			companyId, name, start, end, orderByComparator);
	}

	public static int getAudiencesEntriesCount(long companyId)
		throws PortalException {

		return getService().getAudiencesEntriesCount(companyId);
	}

	public static int getAudiencesEntriesCount(long companyId, String name)
		throws PortalException {

		return getService().getAudiencesEntriesCount(companyId, name);
	}

	public static AudiencesEntry getAudiencesEntry(long audiencesEntryId)
		throws PortalException {

		return getService().getAudiencesEntry(audiencesEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static AudiencesEntry updateAudiencesEntry(
			long audiencesEntryId, String externalReferenceCode, String json,
			String name)
		throws PortalException {

		return getService().updateAudiencesEntry(
			audiencesEntryId, externalReferenceCode, json, name);
	}

	public static AudiencesEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AudiencesEntryService> _serviceSnapshot =
		new Snapshot<>(
			AudiencesEntryServiceUtil.class, AudiencesEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:136972504