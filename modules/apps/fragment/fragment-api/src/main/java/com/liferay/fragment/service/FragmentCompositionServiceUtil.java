/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for FragmentComposition. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentCompositionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCompositionService
 * @generated
 */
public class FragmentCompositionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCompositionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static FragmentComposition addFragmentComposition(
			String externalReferenceCode, long groupId,
			long fragmentCollectionId, String fragmentCompositionKey,
			String name, String description, String data,
			long previewFileEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentComposition(
			externalReferenceCode, groupId, fragmentCollectionId,
			fragmentCompositionKey, name, description, data, previewFileEntryId,
			status, serviceContext);
	}

	public static FragmentComposition deleteFragmentComposition(
			long fragmentCompositionId)
		throws PortalException {

		return getService().deleteFragmentComposition(fragmentCompositionId);
	}

	public static FragmentComposition deleteFragmentComposition(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentComposition(
			externalReferenceCode, groupId);
	}

	public static FragmentComposition fetchFragmentComposition(
		long fragmentCompositionId) {

		return getService().fetchFragmentComposition(fragmentCompositionId);
	}

	public static FragmentComposition fetchFragmentComposition(
		long groupId, String fragmentCompositionKey) {

		return getService().fetchFragmentComposition(
			groupId, fragmentCompositionKey);
	}

	public static FragmentComposition
			getFragmentCompositionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentCompositionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static List<FragmentComposition> getFragmentCompositions(
			long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentCompositions(fragmentCollectionId);
	}

	public static List<FragmentComposition> getFragmentCompositions(
			long fragmentCollectionId, int start, int end)
		throws PortalException {

		return getService().getFragmentCompositions(
			fragmentCollectionId, start, end);
	}

	public static List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, int status)
		throws PortalException {

		return getService().getFragmentCompositions(
			groupId, fragmentCollectionId, status);
	}

	public static List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, int start, int end,
			OrderByComparator<FragmentComposition> orderByComparator)
		throws PortalException {

		return getService().getFragmentCompositions(
			groupId, fragmentCollectionId, start, end, orderByComparator);
	}

	public static List<FragmentComposition> getFragmentCompositions(
			long groupId, long fragmentCollectionId, String name, int start,
			int end, OrderByComparator<FragmentComposition> orderByComparator)
		throws PortalException {

		return getService().getFragmentCompositions(
			groupId, fragmentCollectionId, name, start, end, orderByComparator);
	}

	public static int getFragmentCompositionsCount(long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentCompositionsCount(fragmentCollectionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static FragmentComposition moveFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId)
		throws PortalException {

		return getService().moveFragmentComposition(
			fragmentCompositionId, fragmentCollectionId);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long previewFileEntryId)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, previewFileEntryId);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId, String name,
			String description, String data, long previewFileEntryId,
			int status)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, fragmentCollectionId, name, description,
			data, previewFileEntryId, status);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, name);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name, String description,
			String data, long previewFileEntryId, int status)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, name, description, data, previewFileEntryId,
			status);
	}

	public static FragmentCompositionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentCompositionService> _serviceSnapshot =
		new Snapshot<>(
			FragmentCompositionServiceUtil.class,
			FragmentCompositionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:720490518