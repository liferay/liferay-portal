/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for LayoutSetPrototype. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutSetPrototypeServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetPrototypeService
 * @generated
 */
public class LayoutSetPrototypeServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutSetPrototypeServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static LayoutSetPrototype addLayoutSetPrototype(
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			boolean layoutsUpdateable, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutSetPrototype(
			nameMap, descriptionMap, active, layoutsUpdateable, serviceContext);
	}

	public static LayoutSetPrototype addLayoutSetPrototype(
			String name, String description, boolean active,
			boolean layoutsUpdateable, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutSetPrototype(
			name, description, active, layoutsUpdateable, serviceContext);
	}

	public static void deleteLayoutSetPrototype(long layoutSetPrototypeId)
		throws PortalException {

		getService().deleteLayoutSetPrototype(layoutSetPrototypeId);
	}

	public static void deleteNondefaultLayoutSetPrototypes(long companyId)
		throws PortalException {

		getService().deleteNondefaultLayoutSetPrototypes(companyId);
	}

	public static LayoutSetPrototype fetchLayoutSetPrototype(
			long layoutSetPrototypeId)
		throws PortalException {

		return getService().fetchLayoutSetPrototype(layoutSetPrototypeId);
	}

	public static LayoutSetPrototype getLayoutSetPrototype(
			long layoutSetPrototypeId)
		throws PortalException {

		return getService().getLayoutSetPrototype(layoutSetPrototypeId);
	}

	public static List<LayoutSetPrototype> getLayoutSetPrototypes(
			long companyId)
		throws PortalException {

		return getService().getLayoutSetPrototypes(companyId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<LayoutSetPrototype> search(
			long companyId, Boolean active,
			OrderByComparator<LayoutSetPrototype> orderByComparator)
		throws PortalException {

		return getService().search(companyId, active, orderByComparator);
	}

	public static LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, boolean active,
			boolean layoutsUpdateable, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutSetPrototype(
			layoutSetPrototypeId, nameMap, descriptionMap, active,
			layoutsUpdateable, serviceContext);
	}

	public static LayoutSetPrototype updateLayoutSetPrototype(
			long layoutSetPrototypeId, String settings)
		throws PortalException {

		return getService().updateLayoutSetPrototype(
			layoutSetPrototypeId, settings);
	}

	public static LayoutSetPrototypeService getService() {
		return _service;
	}

	public static void setService(LayoutSetPrototypeService service) {
		_service = service;
	}

	private static volatile LayoutSetPrototypeService _service;

}