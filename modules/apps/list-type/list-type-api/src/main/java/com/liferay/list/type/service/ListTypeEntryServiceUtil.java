/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for ListTypeEntry. This utility wraps
 * <code>com.liferay.list.type.service.impl.ListTypeEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeEntryService
 * @generated
 */
public class ListTypeEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.list.type.service.impl.ListTypeEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ListTypeEntry addListTypeEntry(
			String externalReferenceCode, long listTypeDefinitionId, String key,
			Map<java.util.Locale, String> nameMap, boolean system)
		throws PortalException {

		return getService().addListTypeEntry(
			externalReferenceCode, listTypeDefinitionId, key, nameMap, system);
	}

	public static ListTypeEntry deleteListTypeEntry(long listTypeEntryId)
		throws PortalException {

		return getService().deleteListTypeEntry(listTypeEntryId);
	}

	public static ListTypeEntry fetchListTypeEntry(
			long listTypeDefinitionId, String key)
		throws PortalException {

		return getService().fetchListTypeEntry(listTypeDefinitionId, key);
	}

	public static List<ListTypeEntry> getListTypeEntries(
			long listTypeDefinitionId, int start, int end)
		throws PortalException {

		return getService().getListTypeEntries(
			listTypeDefinitionId, start, end);
	}

	public static int getListTypeEntriesCount(long listTypeDefinitionId)
		throws PortalException {

		return getService().getListTypeEntriesCount(listTypeDefinitionId);
	}

	public static ListTypeEntry getListTypeEntry(long listTypeEntryId)
		throws PortalException {

		return getService().getListTypeEntry(listTypeEntryId);
	}

	public static ListTypeEntry getListTypeEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long listTypeDefinitionId)
		throws PortalException {

		return getService().getListTypeEntryByExternalReferenceCode(
			externalReferenceCode, companyId, listTypeDefinitionId);
	}

	public static ListTypeEntry getOrAddEmptyListTypeEntry(
			long userId, long listTypeDefinitionId, String key)
		throws PortalException {

		return getService().getOrAddEmptyListTypeEntry(
			userId, listTypeDefinitionId, key);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static ListTypeEntry updateListTypeEntry(
			String externalReferenceCode, long listTypeEntryId,
			Map<java.util.Locale, String> nameMap)
		throws PortalException {

		return getService().updateListTypeEntry(
			externalReferenceCode, listTypeEntryId, nameMap);
	}

	public static ListTypeEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ListTypeEntryService> _serviceSnapshot =
		new Snapshot<>(
			ListTypeEntryServiceUtil.class, ListTypeEntryService.class);

}