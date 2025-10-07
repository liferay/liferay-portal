/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service.impl;

import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupTable;
import com.liferay.portal.kernel.model.Users_UserGroupsTable;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.sharing.exception.DuplicateSharingEntryException;
import com.liferay.sharing.exception.InvalidSharingEntryActionException;
import com.liferay.sharing.exception.InvalidSharingEntryExpirationDateException;
import com.liferay.sharing.exception.InvalidSharingEntryUserAndUserGroupException;
import com.liferay.sharing.exception.InvalidSharingEntryUserException;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.model.SharingEntryTable;
import com.liferay.sharing.security.permission.SharingEntryAction;
import com.liferay.sharing.service.base.SharingEntryLocalServiceBaseImpl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the local service for accessing, adding, checking, deleting, and
 * updating sharing entries.
 *
 * <p>
 * This service does not perform any kind of permission checking and assumes
 * that the user has permission to share the resource via any sharing entry
 * action. Permission checks are done in {@link SharingEntryServiceImpl}.
 * </p>
 *
 * <p>
 * This service does, however, perform some validation and integrity checks to
 * ensure that the sharing entries are valid and consistent.
 * </p>
 *
 * @author Sergio González
 */
@Component(
	property = "model.class.name=com.liferay.sharing.model.SharingEntry",
	service = AopService.class
)
public class SharingEntryLocalServiceImpl
	extends SharingEntryLocalServiceBaseImpl {

	/**
	 * Adds a new sharing entry in the database or updates an existing one.
	 *
	 * @param  userId the ID of the user sharing the resource
	 * @param  toUserId the ID of the user the resource is shared with
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @param  groupId the primary key of the resource's group
	 * @param  shareable whether the user specified by {@code toUserId} can
	 *         share the resource
	 * @param  sharingEntryActions the sharing entry actions
	 * @param  expirationDate the date when the sharing entry expires
	 * @param  serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry actions are invalid (e.g.,
	 *         empty, don't contain {@code SharingEntryAction#VIEW}, or contain
	 *         a {@code null} value), if the to/from user IDs are the same, or
	 *         if the expiration date is a past value
	 * @review
	 */
	@Override
	public SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.fetchByTUG_TU_C_C(
			toUserGroupId, toUserId, classNameId, classPK);

		if (sharingEntry == null) {
			return sharingEntryLocalService.addSharingEntry(
				externalReferenceCode, userId, toUserGroupId, toUserId,
				classNameId, classPK, groupId, shareable, sharingEntryActions,
				expirationDate, serviceContext);
		}

		return sharingEntryLocalService.updateSharingEntry(
			userId, sharingEntry.getSharingEntryId(), sharingEntryActions,
			shareable, expirationDate, serviceContext);
	}

	/**
	 * Adds a new sharing entry in the database.
	 *
	 * @param  userId the ID of the user sharing the resource
	 * @param  toUserId the ID of the user the resource is shared with
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @param  groupId the primary key of the resource's group
	 * @param  shareable whether the user specified by {@code toUserId} can
	 *         share the resource
	 * @param  sharingEntryActions the sharing entry actions
	 * @param  expirationDate the date when the sharing entry expires
	 * @param  serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry already exists for the to/from
	 *         user IDs, if the sharing entry actions are invalid (e.g., empty,
	 *         don't contain {@code SharingEntryAction#VIEW}, or contain a
	 *         {@code null} value), if the to/from user IDs are the same, or if
	 *         the expiration date is a past value
	 * @review
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SharingEntry addSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException {

		_validateSharingEntryActions(sharingEntryActions);

		_validateUsersAndUserGroup(userId, toUserGroupId, toUserId);

		_validateExpirationDate(expirationDate);

		SharingEntry existingSharingEntry =
			sharingEntryPersistence.fetchByTUG_TU_C_C(
				toUserGroupId, toUserId, classNameId, classPK);

		if (existingSharingEntry != null) {
			throw new DuplicateSharingEntryException(
				StringBundler.concat(
					"A sharing entry already exists for user ", toUserId,
					" with classNameId ", classNameId, " and classPK ",
					classPK));
		}

		long sharingEntryId = counterLocalService.increment();

		SharingEntry sharingEntry = sharingEntryPersistence.create(
			sharingEntryId);

		sharingEntry.setUuid(serviceContext.getUuid());
		sharingEntry.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		sharingEntry.setExternalReferenceCode(externalReferenceCode);
		sharingEntry.setCompanyId(user.getCompanyId());
		sharingEntry.setUserId(user.getUserId());
		sharingEntry.setUserName(user.getFullName());

		sharingEntry.setToUserGroupId(toUserGroupId);
		sharingEntry.setToUserId(toUserId);
		sharingEntry.setClassNameId(classNameId);
		sharingEntry.setClassPK(classPK);
		sharingEntry.setShareable(shareable);
		sharingEntry.setActionIds(_getActionIds(sharingEntryActions));
		sharingEntry.setExpirationDate(expirationDate);

		SharingEntry newSharingEntry = sharingEntryPersistence.update(
			sharingEntry);

		String className = _portal.getClassName(classNameId);

		Indexer<Object> indexer = _indexerRegistry.getIndexer(className);

		if (indexer != null) {
			indexer.reindex(className, classPK);
		}

		return newSharingEntry;
	}

	@Override
	public void deleteCompanySharingEntries(long companyId, long classNameId) {
		List<SharingEntry> sharingEntries = sharingEntryPersistence.findByC_CN(
			companyId, classNameId);

		for (SharingEntry sharingEntry : sharingEntries) {
			sharingEntryLocalService.deleteSharingEntry(sharingEntry);
		}
	}

	/**
	 * Deletes the sharing entries whose expiration date is before the current
	 * date.
	 */
	@Override
	public void deleteExpiredEntries() {
		for (SharingEntry sharingEntry :
				sharingEntryPersistence.findByLtExpirationDate(
					DateUtil.newDate(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			sharingEntryLocalService.deleteSharingEntry(sharingEntry);
		}
	}

	/**
	 * Deletes the group's sharing entries.
	 *
	 * @param groupId the group's ID
	 */
	@Override
	public void deleteGroupSharingEntries(long groupId) {
		List<SharingEntry> sharingEntries =
			sharingEntryPersistence.findByGroupId(groupId);

		for (SharingEntry sharingEntry : sharingEntries) {
			sharingEntryLocalService.deleteSharingEntry(sharingEntry);
		}
	}

	/**
	 * Deletes the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 */
	@Override
	public void deleteSharingEntries(long classNameId, long classPK) {
		List<SharingEntry> sharingEntries = sharingEntryPersistence.findByC_C(
			classNameId, classPK);

		for (SharingEntry sharingEntry : sharingEntries) {
			sharingEntryLocalService.deleteSharingEntry(sharingEntry);
		}
	}

	/**
	 * Deletes the sharing entry that matches the sharing entry ID.
	 *
	 * @param  sharingEntryId the sharing entry's ID
	 * @return the deleted sharing entry
	 */
	@Override
	public SharingEntry deleteSharingEntry(long sharingEntryId)
		throws PortalException {

		return sharingEntryLocalService.deleteSharingEntry(
			getSharingEntry(sharingEntryId));
	}

	/**
	 * Deletes the sharing entry for the resource and users. The class name ID
	 * and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param  toUserId the ID of the user the resource is shared with
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @return the deleted sharing entry
	 */
	@Override
	public SharingEntry deleteSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.findByTUG_TU_C_C(
			0, toUserId, classNameId, classPK);

		return sharingEntryLocalService.deleteSharingEntry(sharingEntry);
	}

	/**
	 * Deletes the sharing entry.
	 *
	 * @param  sharingEntry the sharing entry to delete
	 * @return the deleted sharing entry
	 */
	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SharingEntry deleteSharingEntry(SharingEntry sharingEntry) {
		String className = sharingEntry.getClassName();
		long classPK = sharingEntry.getClassPK();

		SharingEntry deletedSharingEntry = sharingEntryPersistence.remove(
			sharingEntry);

		Indexer<Object> indexer = _indexerRegistry.getIndexer(className);

		if (indexer != null) {
			try {
				indexer.reindex(className, classPK);
			}
			catch (SearchException searchException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to index sharing entry for class name ",
							className, " and primary key ", classPK),
						searchException);
				}
			}
		}

		return deletedSharingEntry;
	}

	@Override
	public SharingEntry deleteSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return sharingEntryLocalService.deleteSharingEntry(
			getSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	/**
	 * Deletes the sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 */
	@Override
	public void deleteToUserSharingEntries(long toUserId) {
		List<SharingEntry> sharingEntries =
			sharingEntryPersistence.findByToUserId(toUserId);

		for (SharingEntry sharingEntry : sharingEntries) {
			sharingEntryLocalService.deleteSharingEntry(sharingEntry);
		}
	}

	/**
	 * Returns the sharing entry for the resource shared with the user or
	 * <code>null</code> if there's none. The class name ID and class primary
	 * key identify the resource's type and instance, respectively.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @return the sharing entry or <code>null</code> if none
	 * @review
	 */
	@Override
	public SharingEntry fetchSharingEntry(
		long toUserId, long classNameId, long classPK) {

		return sharingEntryPersistence.fetchByTUG_TU_C_C(
			0, toUserId, classNameId, classPK);
	}

	@Override
	public int getCompanySharingEntriesCount(long companyId, long classNameId) {
		return sharingEntryPersistence.countByC_CN(companyId, classNameId);
	}

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared by the user. The class name ID identifies the resource type.
	 *
	 * @param  fromUserId the user's ID
	 * @param  classNameId the class name ID of the resources
	 * @param  start the ordered range's lower bound
	 * @param  end the ordered range's upper bound (not inclusive)
	 * @param  orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Override
	public List<SharingEntry> getFromUserSharingEntries(
		long fromUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return sharingEntryFinder.findByUserId(
			fromUserId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared by
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param  fromUserId the user's ID
	 * @param  classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Override
	public int getFromUserSharingEntriesCount(
		long fromUserId, long classNameId) {

		return sharingEntryFinder.countByUserId(fromUserId, classNameId);
	}

	/**
	 * Returns the the group's sharing entries.
	 *
	 * @param  groupId the primary key of the group
	 * @return the sharing entries
	 */
	@Override
	public List<SharingEntry> getGroupSharingEntries(long groupId) {
		return sharingEntryPersistence.findByGroupId(groupId);
	}

	/**
	 * Returns the the group's sharing entries count.
	 *
	 * @param  groupId the primary key of the group
	 * @return the sharing entries count
	 */
	@Override
	public int getGroupSharingEntriesCount(long groupId) {
		return sharingEntryPersistence.countByGroupId(groupId);
	}

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @return the sharing entries
	 */
	@Override
	public List<SharingEntry> getSharingEntries(
		long classNameId, long classPK) {

		return sharingEntryPersistence.findByC_C(classNameId, classPK);
	}

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @param  start the range's lower bound
	 * @param  end the range's upper bound (not inclusive)
	 * @return the sharing entries
	 * @review
	 */
	@Override
	public List<SharingEntry> getSharingEntries(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return sharingEntryPersistence.findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns the resource's sharing entries count. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @return the sharing entries count
	 * @review
	 */
	@Override
	public int getSharingEntriesCount(long classNameId, long classPK) {
		return sharingEntryPersistence.countByC_C(classNameId, classPK);
	}

	/**
	 * Returns the sharing entry for the resource shared with the user. The
	 * class name ID and class primary key identify the resource's type and
	 * instance, respectively.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the resource
	 * @return the sharing entry
	 * @review
	 */
	@Override
	public SharingEntry getSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException {

		return sharingEntryPersistence.findByTUG_TU_C_C(
			0, toUserId, classNameId, classPK);
	}

	/**
	 * Returns the list of sharing entries for resources shared with the user.
	 *
	 * @param  toUserId the user's ID
	 * @return the list of sharing entries
	 */
	@Override
	public List<SharingEntry> getToUserSharingEntries(long toUserId) {
		return sharingEntryPersistence.findByToUserId(toUserId);
	}

	/**
	 * Returns the range of sharing entries for resources shared with the user.
	 *
	 * @param  toUserId the user's ID
	 * @param  start the range's lower bound
	 * @param  end the range's upper bound (not inclusive)
	 * @return the range of sharing entries
	 */
	@Override
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, int start, int end) {

		return sharingEntryPersistence.findByToUserId(toUserId, start, end);
	}

	/**
	 * Returns the list of sharing entries for the type of resource shared with
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the class name ID of the resources
	 * @return the list of sharing entries
	 */
	@Override
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId) {

		return sharingEntryPersistence.findByTU_C(toUserId, classNameId);
	}

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared with the user. The class name ID identifies the resource type.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the class name ID of the resources
	 * @param  start the ordered range's lower bound
	 * @param  end the ordered range's upper bound (not inclusive)
	 * @param  orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Override
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		if (classNameId > 0) {
			return sharingEntryPersistence.findByTU_C(
				toUserId, classNameId, start, end, orderByComparator);
		}

		return sharingEntryPersistence.findByToUserId(
			toUserId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for resources shared with the user.
	 *
	 * @param  toUserId the user's ID
	 * @return the number of sharing entries
	 */
	@Override
	public int getToUserSharingEntriesCount(long toUserId) {
		return sharingEntryPersistence.countByToUserId(toUserId);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared
	 * with the user. The class name ID identifies the resource type.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Override
	public int getToUserSharingEntriesCount(long toUserId, long classNameId) {
		if (classNameId > 0) {
			return sharingEntryPersistence.countByTU_C(toUserId, classNameId);
		}

		return sharingEntryPersistence.countByToUserId(toUserId);
	}

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with a user who can also share that resource. The class name
	 * ID and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the shared resource
	 * @param  sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 *         been shared with a user who can also share that resource; {@code
	 *         false} otherwise
	 */
	@Override
	public boolean hasShareableSharingPermission(
		long toUserId, long classNameId, long classPK,
		SharingEntryAction sharingEntryAction) {

		int sharingEntriesCount = _getSharingEntriesCount(
			classNameId, classPK, true, sharingEntryAction, toUserId);

		if (sharingEntriesCount > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with the user. The class name ID and class primary key
	 * identify the resource's type and instance, respectively.
	 *
	 * @param  toUserId the user's ID
	 * @param  classNameId the resource's class name ID
	 * @param  classPK the class primary key of the shared resource
	 * @param  sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 *         been shared with the user; {@code false} otherwise
	 */
	@Override
	public boolean hasSharingPermission(
		long toUserId, long classNameId, long classPK,
		SharingEntryAction sharingEntryAction) {

		int sharingEntriesCount = _getSharingEntriesCount(
			classNameId, classPK, null, sharingEntryAction, toUserId);

		if (sharingEntriesCount > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param      sharingEntryId the primary key of the sharing entry
	 * @param      sharingEntryActions the sharing entry actions
	 * @param      shareable whether the user the resource is shared with can
	 *             also share it
	 * @param      expirationDate the date when the sharing entry expires
	 * @param      serviceContext the service context
	 * @return     the sharing entry
	 * @throws     PortalException if the sharing entry does not exist, if the
	 *             sharing entry actions are invalid (e.g., empty, don't contain
	 *             {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 *             value), or if the expiration date is a past value
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 *             com.liferay.sharing.service.SharingEntryLocalService#updateSharingEntry(
	 *             long, long, Collection, boolean, Date, ServiceContext)}
	 * @review
	 */
	@Deprecated
	@Override
	public SharingEntry updateSharingEntry(
			long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException {

		return sharingEntryLocalService.updateSharingEntry(
			serviceContext.getUserId(), sharingEntryId, sharingEntryActions,
			shareable, expirationDate, serviceContext);
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param  userId the primary key of the user updating the sharing entry
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
	 * @review
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public SharingEntry updateSharingEntry(
			long userId, long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException {

		SharingEntry sharingEntry = sharingEntryPersistence.findByPrimaryKey(
			sharingEntryId);

		_validateSharingEntryActions(sharingEntryActions);

		_validateExpirationDate(expirationDate);

		sharingEntry.setUserId(userId);
		sharingEntry.setShareable(shareable);
		sharingEntry.setActionIds(_getActionIds(sharingEntryActions));
		sharingEntry.setExpirationDate(expirationDate);

		return sharingEntryPersistence.update(sharingEntry);
	}

	private long _getActionIds(
		Collection<SharingEntryAction> sharingEntryActions) {

		long actionIds = 0;

		for (SharingEntryAction sharingEntryAction : sharingEntryActions) {
			actionIds |= sharingEntryAction.getBitwiseValue();
		}

		return actionIds;
	}

	private int _getSharingEntriesCount(
		long classNameId, long classPK, Boolean shareable,
		SharingEntryAction sharingEntryAction, long toUserId) {

		return sharingEntryLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				SharingEntryTable.INSTANCE
			).where(
				DSLFunctionFactoryUtil.bitAnd(
					SharingEntryTable.INSTANCE.actionIds,
					sharingEntryAction.getBitwiseValue()
				).eq(
					sharingEntryAction.getBitwiseValue()
				).and(
					SharingEntryTable.INSTANCE.classNameId.eq(classNameId)
				).and(
					SharingEntryTable.INSTANCE.classPK.eq(classPK)
				).and(
					() -> {
						if (shareable == null) {
							return null;
						}

						return SharingEntryTable.INSTANCE.shareable.eq(
							shareable);
					}
				).and(
					SharingEntryTable.INSTANCE.toUserId.eq(
						toUserId
					).or(
						SharingEntryTable.INSTANCE.toUserGroupId.in(
							DSLQueryFactoryUtil.select(
								UserGroupTable.INSTANCE.userGroupId
							).from(
								UserGroupTable.INSTANCE
							).innerJoinON(
								Users_UserGroupsTable.INSTANCE,
								UserGroupTable.INSTANCE.userGroupId.eq(
									Users_UserGroupsTable.INSTANCE.userGroupId)
							).where(
								Users_UserGroupsTable.INSTANCE.userId.eq(
									toUserId)
							))
					).withParentheses()
				)
			));
	}

	private void _validateExpirationDate(Date expirationDate)
		throws InvalidSharingEntryExpirationDateException {

		if ((expirationDate != null) &&
			expirationDate.before(DateUtil.newDate())) {

			throw new InvalidSharingEntryExpirationDateException(
				"Expiration date is in the past");
		}
	}

	private void _validateSharingEntryActions(
			Collection<SharingEntryAction> sharedEntryActions)
		throws InvalidSharingEntryActionException {

		if (sharedEntryActions.isEmpty()) {
			throw new InvalidSharingEntryActionException(
				"Shared entry actions is empty");
		}

		for (SharingEntryAction curSharingEntryAction : sharedEntryActions) {
			if (curSharingEntryAction == null) {
				throw new InvalidSharingEntryActionException(
					"Shared entry actions contains a null value");
			}
		}

		if (!sharedEntryActions.contains(SharingEntryAction.VIEW)) {
			throw new InvalidSharingEntryActionException(
				"Shared entry actions must contain VIEW shared entry action");
		}
	}

	private void _validateUsersAndUserGroup(
			long fromUserId, long toUserGroupId, long toUserId)
		throws PortalException {

		if (toUserGroupId > 0) {
			_userGroupLocalService.getUserGroup(toUserGroupId);
		}

		if ((toUserGroupId > 0) && (toUserId > 0)) {
			throw new InvalidSharingEntryUserAndUserGroupException(
				"A sharing entry cannot be associated with a user and a user " +
					"group at the same time");
		}

		if ((toUserId > 0) && (fromUserId == toUserId)) {
			throw new InvalidSharingEntryUserException(
				"From user cannot be the same as to user");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharingEntryLocalServiceImpl.class);

	@Reference
	private IndexerRegistry _indexerRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}