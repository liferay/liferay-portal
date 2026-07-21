/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ListType;

import java.util.List;

/**
 * Provides the remote service utility for ListType. This utility wraps
 * <code>com.liferay.portal.service.impl.ListTypeServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see ListTypeService
 * @generated
 */
public class ListTypeServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.ListTypeServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ListType fetchListType(
		long companyId, String name, String type) {

		return getService().fetchListType(companyId, name, type);
	}

	public static ListType getListType(long listTypeId) throws PortalException {
		return getService().getListType(listTypeId);
	}

	public static ListType getListType(long companyId, String name, String type)
		throws PortalException {

		return getService().getListType(companyId, name, type);
	}

	public static long getListTypeId(long companyId, String name, String type)
		throws PortalException {

		return getService().getListTypeId(companyId, name, type);
	}

	public static List<ListType> getListTypes(long companyId, String type) {
		return getService().getListTypes(companyId, type);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static void validate(long listTypeId, long classNameId, String type)
		throws PortalException {

		getService().validate(listTypeId, classNameId, type);
	}

	public static void validate(long listTypeId, String type)
		throws PortalException {

		getService().validate(listTypeId, type);
	}

	public static ListTypeService getService() {
		return _service;
	}

	public static void setService(ListTypeService service) {
		_service = service;
	}

	private static volatile ListTypeService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1815735563