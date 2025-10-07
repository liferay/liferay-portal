/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for ObjectEntryVersion. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectEntryVersionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Marco Leo
 * @see ObjectEntryVersionService
 * @generated
 */
public class ObjectEntryVersionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryVersionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectEntryVersion deleteObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return getService().deleteObjectEntryVersion(objectEntryId, version);
	}

	public static ObjectEntryVersion expireObjectEntryVersion(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			int version)
		throws PortalException {

		return getService().expireObjectEntryVersion(
			objectEntry, serviceContext, version);
	}

	public static void expireObjectEntryVersions(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		getService().expireObjectEntryVersions(objectEntry, serviceContext);
	}

	public static ObjectEntryVersion getObjectEntryVersion(
			long objectEntryId, int version)
		throws PortalException {

		return getService().getObjectEntryVersion(objectEntryId, version);
	}

	public static List<ObjectEntryVersion> getObjectEntryVersions(
			long objectEntryId, int start, int end)
		throws PortalException {

		return getService().getObjectEntryVersions(objectEntryId, start, end);
	}

	public static int getObjectEntryVersionsCount(long objectEntryId)
		throws PortalException {

		return getService().getObjectEntryVersionsCount(objectEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ObjectEntryVersionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectEntryVersionService> _serviceSnapshot =
		new Snapshot<>(
			ObjectEntryVersionServiceUtil.class,
			ObjectEntryVersionService.class);

}