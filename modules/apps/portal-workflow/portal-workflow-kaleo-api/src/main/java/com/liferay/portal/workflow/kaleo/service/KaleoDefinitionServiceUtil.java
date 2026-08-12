/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;

import java.util.List;

/**
 * Provides the remote service utility for KaleoDefinition. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionService
 * @generated
 */
public class KaleoDefinitionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static KaleoDefinition addKaleoDefinition(
			String externalReferenceCode, String name, String title,
			String description, String content, String scope, boolean system,
			int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoDefinition(
			externalReferenceCode, name, title, description, content, scope,
			system, version, serviceContext);
	}

	public static KaleoDefinition getKaleoDefinition(long kaleoDefinitionId)
		throws PortalException {

		return getService().getKaleoDefinition(kaleoDefinitionId);
	}

	public static KaleoDefinition getKaleoDefinition(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getKaleoDefinition(
			externalReferenceCode, companyId);
	}

	public static KaleoDefinition getKaleoDefinition(
			String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getKaleoDefinition(name, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, boolean active, int start, int end,
			OrderByComparator<KaleoDefinition> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getScopeKaleoDefinitions(
			scope, active, start, end, orderByComparator, serviceContext);
	}

	public static List<KaleoDefinition> getScopeKaleoDefinitions(
			String scope, int start, int end,
			OrderByComparator<KaleoDefinition> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getScopeKaleoDefinitions(
			scope, start, end, orderByComparator, serviceContext);
	}

	public static KaleoDefinition updateKaleoDefinition(
			String externalReferenceCode, long kaleoDefinitionId, String title,
			String description, String content, boolean system,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKaleoDefinition(
			externalReferenceCode, kaleoDefinitionId, title, description,
			content, system, serviceContext);
	}

	public static KaleoDefinitionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoDefinitionService> _serviceSnapshot =
		new Snapshot<>(
			KaleoDefinitionServiceUtil.class, KaleoDefinitionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1585344898