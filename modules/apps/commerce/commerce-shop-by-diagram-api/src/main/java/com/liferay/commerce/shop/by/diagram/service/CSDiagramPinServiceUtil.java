/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramPin;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;

/**
 * Provides the remote service utility for CSDiagramPin. This utility wraps
 * <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramPinServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramPinService
 * @generated
 */
public class CSDiagramPinServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramPinServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CSDiagramPin addCSDiagramPin(
			long cpDefinitionId, double positionX, double positionY,
			String sequence)
		throws PortalException {

		return getService().addCSDiagramPin(
			cpDefinitionId, positionX, positionY, sequence);
	}

	public static void deleteCSDiagramPin(long csDiagramPinId)
		throws PortalException {

		getService().deleteCSDiagramPin(csDiagramPinId);
	}

	public static void deleteCSDiagramPins(long cpDefinitionId)
		throws PortalException {

		getService().deleteCSDiagramPins(cpDefinitionId);
	}

	public static CSDiagramPin fetchCSDiagramPin(long csDiagramPinId) {
		return getService().fetchCSDiagramPin(csDiagramPinId);
	}

	public static CSDiagramPin getCSDiagramPin(long csDiagramPinId)
		throws PortalException {

		return getService().getCSDiagramPin(csDiagramPinId);
	}

	public static List<CSDiagramPin> getCSDiagramPins(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		return getService().getCSDiagramPins(cpDefinitionId, start, end);
	}

	public static int getCSDiagramPinsCount(long cpDefinitionId)
		throws PortalException {

		return getService().getCSDiagramPinsCount(cpDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CSDiagramPin updateCSDiagramPin(
			long csDiagramPinId, double positionX, double positionY,
			String sequence)
		throws PortalException {

		return getService().updateCSDiagramPin(
			csDiagramPinId, positionX, positionY, sequence);
	}

	public static CSDiagramPinService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CSDiagramPinService> _serviceSnapshot =
		new Snapshot<>(
			CSDiagramPinServiceUtil.class, CSDiagramPinService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-252441416