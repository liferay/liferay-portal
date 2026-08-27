/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for ListTypeDefinition. This utility wraps
 * <code>com.liferay.list.type.service.impl.ListTypeDefinitionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeDefinitionService
 * @generated
 */
public class ListTypeDefinitionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.list.type.service.impl.ListTypeDefinitionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, Map<java.util.Locale, String> nameMap,
			boolean system,
			List<com.liferay.list.type.model.ListTypeEntry> listTypeEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addListTypeDefinition(
			externalReferenceCode, nameMap, system, listTypeEntries,
			serviceContext);
	}

	public static ListTypeDefinition deleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws PortalException {

		return getService().deleteListTypeDefinition(listTypeDefinition);
	}

	public static ListTypeDefinition deleteListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		return getService().deleteListTypeDefinition(listTypeDefinitionId);
	}

	public static ListTypeDefinition
			fetchListTypeDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchListTypeDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static ListTypeDefinition getListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		return getService().getListTypeDefinition(listTypeDefinitionId);
	}

	public static ListTypeDefinition
			getListTypeDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getListTypeDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<ListTypeDefinition> getListTypeDefinitions(
		int start, int end) {

		return getService().getListTypeDefinitions(start, end);
	}

	public static int getListTypeDefinitionsCount() {
		return getService().getListTypeDefinitionsCount();
	}

	public static ListTypeDefinition getOrAddEmptyListTypeDefinition(
			String externalReferenceCode, boolean system)
		throws PortalException {

		return getService().getOrAddEmptyListTypeDefinition(
			externalReferenceCode, system);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ListTypeDefinition updateListTypeDefinition(
			String externalReferenceCode, long listTypeDefinitionId,
			Map<java.util.Locale, String> nameMap,
			List<com.liferay.list.type.model.ListTypeEntry> listTypeEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateListTypeDefinition(
			externalReferenceCode, listTypeDefinitionId, nameMap,
			listTypeEntries, serviceContext);
	}

	public static ListTypeDefinitionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ListTypeDefinitionService> _serviceSnapshot =
		new Snapshot<>(
			ListTypeDefinitionServiceUtil.class,
			ListTypeDefinitionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1641408366