/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import java.util.List;

/**
 * Provides the remote service utility for KaleoDefinitionVersion. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionVersionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersionService
 * @generated
 */
public class KaleoDefinitionVersionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionVersionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void deleteKaleoDefinitionVersions(
			long companyId, String name)
		throws PortalException {

		getService().deleteKaleoDefinitionVersions(companyId, name);
	}

	public static KaleoDefinitionVersion getKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws PortalException {

		return getService().getKaleoDefinitionVersion(companyId, name, version);
	}

	public static List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
			long companyId, String name)
		throws PortalException {

		return getService().getKaleoDefinitionVersions(companyId, name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static KaleoDefinitionVersionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoDefinitionVersionService>
		_serviceSnapshot = new Snapshot<>(
			KaleoDefinitionVersionServiceUtil.class,
			KaleoDefinitionVersionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:670341265