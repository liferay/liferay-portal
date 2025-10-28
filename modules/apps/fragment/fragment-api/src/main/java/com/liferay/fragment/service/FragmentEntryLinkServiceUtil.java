/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * Provides the remote service utility for FragmentEntryLink. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentEntryLinkServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkService
 * @generated
 */
public class FragmentEntryLinkServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryLinkServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static FragmentEntryLink addFragmentEntryLink(
			String externalReferenceCode, long groupId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long segmentsExperienceId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position,
			String rendererKey, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentEntryLink(
			externalReferenceCode, groupId, originalFragmentEntryLinkERC,
			fragmentEntryERC, fragmentEntryScopeERC, segmentsExperienceId, plid,
			css, html, js, configuration, editableValues, namespace, position,
			rendererKey, type, serviceContext);
	}

	public static FragmentEntryLink deleteFragmentEntryLink(
			long fragmentEntryLinkId)
		throws PortalException {

		return getService().deleteFragmentEntryLink(fragmentEntryLinkId);
	}

	public static FragmentEntryLink deleteFragmentEntryLink(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentEntryLink(
			externalReferenceCode, groupId);
	}

	public static FragmentEntryLink getFragmentEntryLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentEntryLinkByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static FragmentEntryLink updateDeleted(
			long fragmentEntryLinkId, boolean deleted)
		throws PortalException {

		return getService().updateDeleted(fragmentEntryLinkId, deleted);
	}

	public static FragmentEntryLink updateFragmentEntryLink(
			long fragmentEntryLinkId, String editableValues)
		throws PortalException {

		return getService().updateFragmentEntryLink(
			fragmentEntryLinkId, editableValues);
	}

	public static FragmentEntryLink updateFragmentEntryLink(
			long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		return getService().updateFragmentEntryLink(
			fragmentEntryLinkId, editableValues, updateClassedModel);
	}

	public static FragmentEntryLinkService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentEntryLinkService> _serviceSnapshot =
		new Snapshot<>(
			FragmentEntryLinkServiceUtil.class, FragmentEntryLinkService.class);

}