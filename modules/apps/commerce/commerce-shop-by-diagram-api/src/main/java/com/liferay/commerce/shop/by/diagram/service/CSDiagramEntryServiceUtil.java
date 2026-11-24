/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.service;

import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for CSDiagramEntry. This utility wraps
 * <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntryService
 * @generated
 */
public class CSDiagramEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.shop.by.diagram.service.impl.CSDiagramEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static CSDiagramEntry addCSDiagramEntry(
			long cpDefinitionId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCSDiagramEntry(
			cpDefinitionId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	public static void deleteCSDiagramEntries(long cpDefinitionId)
		throws PortalException {

		getService().deleteCSDiagramEntries(cpDefinitionId);
	}

	public static void deleteCSDiagramEntry(long csDiagramEntryId)
		throws PortalException {

		getService().deleteCSDiagramEntry(csDiagramEntryId);
	}

	public static CSDiagramEntry fetchCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		return getService().fetchCSDiagramEntry(cpDefinitionId, sequence);
	}

	public static List<CSDiagramEntry> getCProductCSDiagramEntries(
			long cProductId, int start, int end,
			OrderByComparator<CSDiagramEntry> orderByComparator)
		throws PortalException {

		return getService().getCProductCSDiagramEntries(
			cProductId, start, end, orderByComparator);
	}

	public static int getCProductCSDiagramEntriesCount(long cProductId)
		throws PortalException {

		return getService().getCProductCSDiagramEntriesCount(cProductId);
	}

	public static List<CSDiagramEntry> getCSDiagramEntries(
			long cpDefinitionId, int start, int end)
		throws PortalException {

		return getService().getCSDiagramEntries(cpDefinitionId, start, end);
	}

	public static int getCSDiagramEntriesCount(long cpDefinitionId)
		throws PortalException {

		return getService().getCSDiagramEntriesCount(cpDefinitionId);
	}

	public static CSDiagramEntry getCSDiagramEntry(long csDiagramEntryId)
		throws PortalException {

		return getService().getCSDiagramEntry(csDiagramEntryId);
	}

	public static CSDiagramEntry getCSDiagramEntry(
			long cpDefinitionId, String sequence)
		throws PortalException {

		return getService().getCSDiagramEntry(cpDefinitionId, sequence);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static CSDiagramEntry updateCSDiagramEntry(
			long csDiagramEntryId, long cpInstanceId, long cProductId,
			boolean diagram, int quantity, String sequence, String sku,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCSDiagramEntry(
			csDiagramEntryId, cpInstanceId, cProductId, diagram, quantity,
			sequence, sku, serviceContext);
	}

	public static CSDiagramEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CSDiagramEntryService> _serviceSnapshot =
		new Snapshot<>(
			CSDiagramEntryServiceUtil.class, CSDiagramEntryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1956624005