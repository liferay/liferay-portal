/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.sharing.model.SharingEntry;

import java.util.List;

/**
 * Provides the remote service utility for SharingEntry. This utility wraps
 * <code>com.liferay.sharing.service.impl.SharingEntryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see SharingEntryService
 * @generated
 */
public class SharingEntryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.sharing.service.impl.SharingEntryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a new sharing entry in the database or updates an existing one.
	 *
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the user does not have permission to share the
	 resource, if the sharing entry actions are invalid (e.g., empty
	 don't contain {@code SharingEntryAction#VIEW}, or contain a
	 {@code null} value), if the to/from user IDs are the same, or if
	 the expiration date is a past value
	 */
	public static SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long toUserGroupId, long toUserId,
			long classNameId, long classPK, long groupId, boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateSharingEntry(
			externalReferenceCode, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

	/**
	 * Adds a new sharing entry in the database.
	 *
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the user does not have permission to share the
	 resource, if a sharing entry already exists for the to/from user
	 IDs, if the sharing entry actions are invalid (e.g., empty, do
	 not contain {@code SharingEntryAction#VIEW}, or contain a {@code
	 null} value), if the to/from user IDs are the same, or if the
	 expiration date is a past value
	 */
	public static SharingEntry addSharingEntry(
			String externalReferenceCode, long toUserGroupId, long toUserId,
			long classNameId, long classPK, long groupId, boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSharingEntry(
			externalReferenceCode, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

	public static SharingEntry deleteSharingEntry(
			long toUserGroupId, long toUserId, long classNameId, long classPK)
		throws PortalException {

		return getService().deleteSharingEntry(
			toUserGroupId, toUserId, classNameId, classPK);
	}

	public static SharingEntry deleteSharingEntry(
			long sharingEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().deleteSharingEntry(sharingEntryId, serviceContext);
	}

	public static SharingEntry deleteSharingEntry(SharingEntry sharingEntry)
		throws PortalException {

		return getService().deleteSharingEntry(sharingEntry);
	}

	public static SharingEntry deleteSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static SharingEntry fetchSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchSharingEntryByExternalReferenceCode(
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

	public static List<SharingEntry> getSharingEntries(
			long classNameId, long classPK, long groupId, int start, int end,
			OrderByComparator<SharingEntry> orderByComparator)
		throws PortalException {

		return getService().getSharingEntries(
			classNameId, classPK, groupId, start, end, orderByComparator);
	}

	public static SharingEntry getSharingEntry(long sharingEntryId)
		throws PortalException {

		return getService().getSharingEntry(sharingEntryId);
	}

	public static SharingEntry getSharingEntry(
			long toUserGroupId, long toUserId, long classNameId, long classPK)
		throws PortalException {

		return getService().getSharingEntry(
			toUserGroupId, toUserId, classNameId, classPK);
	}

	public static SharingEntry getSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @param sharingEntryActions the sharing entry actions
	 * @param shareable whether the user the resource is shared with can also
	 share it
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry does not exist, if the
	 sharing entry actions are invalid (e.g., empty, don't contain
	 {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 value), or if the expiration date is a past value
	 */
	public static SharingEntry updateSharingEntry(
			long sharingEntryId,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			boolean shareable, java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSharingEntry(
			sharingEntryId, sharingEntryActions, shareable, expirationDate,
			serviceContext);
	}

	public static SharingEntryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SharingEntryService> _serviceSnapshot =
		new Snapshot<>(
			SharingEntryServiceUtil.class, SharingEntryService.class);

}