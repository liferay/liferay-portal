/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.security.permission.SharingPermission;
import com.liferay.sharing.service.base.SharingEntryServiceBaseImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the remote service for adding and updating sharing entries. This
 * service checks permissions to ensure that a user can share a resource with
 * other users. If the permission check is successful, this service invokes the
 * local service {@link SharingEntryLocalServiceImpl} to perform the operation
 * in the database. The permission check is done using the interface {@code
 * SharingPermissionChecker} for the respective class name ID.
 *
 * @author Sergio González
 */
@Component(
	property = {
		"json.web.service.context.name=sharing",
		"json.web.service.context.path=SharingEntry"
	},
	service = AopService.class
)
public class SharingEntryServiceImpl extends SharingEntryServiceBaseImpl {

	/**
	 * Adds a new sharing entry in the database or updates an existing one.
	 *
	 * @param  toUserId the ID of the user the resource is shared with
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the primary key of the resource
	 * @param  groupId the primary key of the resource's group
	 * @param  shareable whether the user specified by {@code toUserId} can
	 *         share the resource
	 * @param  sharingEntryActions the sharing entry actions
	 * @param  expirationDate the date when the sharing entry expires
	 * @param  serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the user does not have permission to share the
	 *         resource, if the sharing entry actions are invalid (e.g., empty
	 *         don't contain {@code SharingEntryAction#VIEW}, or contain a
	 *         {@code null} value), if the to/from user IDs are the same, or if
	 *         the expiration date is a past value
	 */
	@Override
	public SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long toUserGroupId, long toUserId,
			long classNameId, long classPK, long groupId, boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.fetchByTUG_TU_C_C(
			toUserGroupId, toUserId, classNameId, classPK);

		if (sharingEntry == null) {
			return sharingEntryService.addSharingEntry(
				externalReferenceCode, toUserGroupId, toUserId, classNameId,
				classPK, groupId, shareable, sharingEntryActions,
				expirationDate, serviceContext);
		}

		return sharingEntryService.updateSharingEntry(
			sharingEntry.getSharingEntryId(), sharingEntryActions, shareable,
			expirationDate, serviceContext);
	}

	/**
	 * Adds a new sharing entry in the database.
	 *
	 * @param  toUserId the ID of the user the resource is shared with
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the primary key of the resource
	 * @param  groupId the primary key of the resource's group
	 * @param  shareable whether the user specified by {@code toUserId} can
	 *         share the resource
	 * @param  sharingEntryActions the sharing entry actions
	 * @param  expirationDate the date when the sharing entry expires
	 * @param  serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the user does not have permission to share the
	 *         resource, if a sharing entry already exists for the to/from user
	 *         IDs, if the sharing entry actions are invalid (e.g., empty, do
	 *         not contain {@code SharingEntryAction#VIEW}, or contain a {@code
	 *         null} value), if the to/from user IDs are the same, or if the
	 *         expiration date is a past value
	 */
	@Override
	public SharingEntry addSharingEntry(
			String externalReferenceCode, long toUserGroupId, long toUserId,
			long classNameId, long classPK, long groupId, boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException {

		sharingPermission.check(
			getPermissionChecker(), classNameId, classPK, groupId,
			sharingEntryActions);

		return sharingEntryLocalService.addSharingEntry(
			externalReferenceCode, getUserId(), toUserGroupId, toUserId,
			classNameId, classPK, groupId, shareable, sharingEntryActions,
			expirationDate, serviceContext);
	}

	@Override
	public SharingEntry deleteSharingEntry(
			long toUserGroupId, long toUserId, long classNameId, long classPK)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.findByTUG_TU_C_C(
			toUserGroupId, toUserId, classNameId, classPK);

		sharingPermission.checkManageCollaboratorsPermission(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), sharingEntry.getGroupId());

		return sharingEntryLocalService.deleteSharingEntry(sharingEntry);
	}

	@Override
	public SharingEntry deleteSharingEntry(
			long sharingEntryId, ServiceContext serviceContext)
		throws PortalException {

		return deleteSharingEntry(
			sharingEntryLocalService.getSharingEntry(sharingEntryId));
	}

	@Override
	public SharingEntry deleteSharingEntry(SharingEntry sharingEntry)
		throws PortalException {

		sharingPermission.checkManageCollaboratorsPermission(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), sharingEntry.getGroupId());

		return sharingEntryLocalService.deleteSharingEntry(sharingEntry);
	}

	@Override
	public SharingEntry deleteSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return deleteSharingEntry(
			sharingEntryLocalService.getSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public SharingEntry fetchSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SharingEntry sharingEntry =
			sharingEntryLocalService.fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId);

		if (sharingEntry != null) {
			sharingPermission.check(
				getPermissionChecker(), sharingEntry.getClassNameId(),
				sharingEntry.getClassPK(), groupId,
				Collections.singletonList(SharingEntryAction.VIEW));
		}

		return sharingEntry;
	}

	@Override
	public List<SharingEntry> getSharingEntries(
			long classNameId, long classPK, long groupId, int start, int end,
			OrderByComparator<SharingEntry> orderByComparator)
		throws PortalException {

		sharingPermission.checkSharePermission(
			getPermissionChecker(), classNameId, classPK, groupId);

		return sharingEntryLocalService.getSharingEntries(
			classNameId, classPK, start, end, orderByComparator);
	}

	@Override
	public SharingEntry getSharingEntry(long sharingEntryId)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryLocalService.getSharingEntry(
			sharingEntryId);

		sharingPermission.check(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), sharingEntry.getGroupId(),
			Collections.singletonList(SharingEntryAction.VIEW));

		return sharingEntry;
	}

	@Override
	public SharingEntry getSharingEntry(
			long toUserGroupId, long toUserId, long classNameId, long classPK)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.findByTUG_TU_C_C(
			toUserGroupId, toUserId, classNameId, classPK);

		sharingPermission.check(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), sharingEntry.getGroupId(),
			Collections.singletonList(SharingEntryAction.VIEW));

		return sharingEntry;
	}

	@Override
	public SharingEntry getSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		SharingEntry sharingEntry =
			sharingEntryLocalService.getSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId);

		sharingPermission.check(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), groupId,
			Collections.singletonList(SharingEntryAction.VIEW));

		return sharingEntry;
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param  sharingEntryId the primary key of the sharing entry
	 * @param  sharingEntryActions the sharing entry actions
	 * @param  shareable whether the user the resource is shared with can also
	 *         share it
	 * @param  expirationDate the date when the sharing entry expires
	 * @param  serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry does not exist, if the
	 *         sharing entry actions are invalid (e.g., empty, don't contain
	 *         {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 *         value), or if the expiration date is a past value
	 */
	@Override
	public SharingEntry updateSharingEntry(
			long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.findByPrimaryKey(
			sharingEntryId);

		sharingPermission.checkManageCollaboratorsPermission(
			getPermissionChecker(), sharingEntry.getClassNameId(),
			sharingEntry.getClassPK(), sharingEntry.getGroupId());

		return sharingEntryLocalService.updateSharingEntry(
			getUserId(), sharingEntryId, sharingEntryActions, shareable,
			expirationDate, serviceContext);
	}

	@Reference
	protected SharingPermission sharingPermission;

}