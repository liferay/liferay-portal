/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SharingEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SharingEntryLocalService
 * @generated
 */
public class SharingEntryLocalServiceWrapper
	implements ServiceWrapper<SharingEntryLocalService>,
			   SharingEntryLocalService {

	public SharingEntryLocalServiceWrapper() {
		this(null);
	}

	public SharingEntryLocalServiceWrapper(
		SharingEntryLocalService sharingEntryLocalService) {

		_sharingEntryLocalService = sharingEntryLocalService;
	}

	/**
	 * Adds a new sharing entry in the database or updates an existing one.
	 *
	 * @param userId the ID of the user sharing the resource
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry actions are invalid (e.g.,
	 empty, don't contain {@code SharingEntryAction#VIEW}, or contain
	 a {@code null} value), if the to/from user IDs are the same, or
	 if the expiration date is a past value
	 * @review
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.addOrUpdateSharingEntry(
			externalReferenceCode, userId, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

	/**
	 * Adds the sharing entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was added
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry addSharingEntry(
		com.liferay.sharing.model.SharingEntry sharingEntry) {

		return _sharingEntryLocalService.addSharingEntry(sharingEntry);
	}

	/**
	 * Adds a new sharing entry in the database.
	 *
	 * @param userId the ID of the user sharing the resource
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry already exists for the to/from
	 user IDs, if the sharing entry actions are invalid (e.g., empty,
	 don't contain {@code SharingEntryAction#VIEW}, or contain a
	 {@code null} value), if the to/from user IDs are the same, or if
	 the expiration date is a past value
	 * @review
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry addSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.addSharingEntry(
			externalReferenceCode, userId, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new sharing entry with the primary key. Does not add the sharing entry to the database.
	 *
	 * @param sharingEntryId the primary key for the new sharing entry
	 * @return the new sharing entry
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry createSharingEntry(
		long sharingEntryId) {

		return _sharingEntryLocalService.createSharingEntry(sharingEntryId);
	}

	@Override
	public void deleteCompanySharingEntries(long companyId, long classNameId) {
		_sharingEntryLocalService.deleteCompanySharingEntries(
			companyId, classNameId);
	}

	/**
	 * Deletes the sharing entries whose expiration date is before the current
	 * date.
	 */
	@Override
	public void deleteExpiredEntries() {
		_sharingEntryLocalService.deleteExpiredEntries();
	}

	/**
	 * Deletes the group's sharing entries.
	 *
	 * @param groupId the group's ID
	 */
	@Override
	public void deleteGroupSharingEntries(long groupId) {
		_sharingEntryLocalService.deleteGroupSharingEntries(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.deletePersistedModel(persistedModel);
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
		_sharingEntryLocalService.deleteSharingEntries(classNameId, classPK);
	}

	/**
	 * Deletes the sharing entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry that was removed
	 * @throws PortalException if a sharing entry with the primary key could not be found
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry deleteSharingEntry(
			long sharingEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.deleteSharingEntry(sharingEntryId);
	}

	/**
	 * Deletes the sharing entry for the resource and users. The class name ID
	 * and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the deleted sharing entry
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry deleteSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.deleteSharingEntry(
			toUserId, classNameId, classPK);
	}

	/**
	 * Deletes the sharing entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was removed
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry deleteSharingEntry(
		com.liferay.sharing.model.SharingEntry sharingEntry) {

		return _sharingEntryLocalService.deleteSharingEntry(sharingEntry);
	}

	@Override
	public com.liferay.sharing.model.SharingEntry
			deleteSharingEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.
			deleteSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Deletes the sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 */
	@Override
	public void deleteToUserSharingEntries(long toUserId) {
		_sharingEntryLocalService.deleteToUserSharingEntries(toUserId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sharingEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sharingEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sharingEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _sharingEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _sharingEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _sharingEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _sharingEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _sharingEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.sharing.model.SharingEntry fetchSharingEntry(
		long sharingEntryId) {

		return _sharingEntryLocalService.fetchSharingEntry(sharingEntryId);
	}

	/**
	 * Returns the sharing entry for the resource shared with the user or
	 * <code>null</code> if there's none. The class name ID and class primary
	 * key identify the resource's type and instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entry or <code>null</code> if none
	 * @review
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry fetchSharingEntry(
		long toUserId, long classNameId, long classPK) {

		return _sharingEntryLocalService.fetchSharingEntry(
			toUserId, classNameId, classPK);
	}

	@Override
	public com.liferay.sharing.model.SharingEntry
		fetchSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _sharingEntryLocalService.
			fetchSharingEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry
		fetchSharingEntryByUuidAndGroupId(String uuid, long groupId) {

		return _sharingEntryLocalService.fetchSharingEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sharingEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public int getCompanySharingEntriesCount(long companyId, long classNameId) {
		return _sharingEntryLocalService.getCompanySharingEntriesCount(
			companyId, classNameId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _sharingEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared by the user. The class name ID identifies the resource type.
	 *
	 * @param fromUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @param start the ordered range's lower bound
	 * @param end the ordered range's upper bound (not inclusive)
	 * @param orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getFromUserSharingEntries(
			long fromUserId, long classNameId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.sharing.model.SharingEntry> orderByComparator) {

		return _sharingEntryLocalService.getFromUserSharingEntries(
			fromUserId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared by
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param fromUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Override
	public int getFromUserSharingEntriesCount(
		long fromUserId, long classNameId) {

		return _sharingEntryLocalService.getFromUserSharingEntriesCount(
			fromUserId, classNameId);
	}

	/**
	 * Returns the the group's sharing entries.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getGroupSharingEntries(long groupId) {

		return _sharingEntryLocalService.getGroupSharingEntries(groupId);
	}

	/**
	 * Returns the the group's sharing entries count.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries count
	 */
	@Override
	public int getGroupSharingEntriesCount(long groupId) {
		return _sharingEntryLocalService.getGroupSharingEntriesCount(groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sharingEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sharingEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the sharing entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @return the range of sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getSharingEntries(int start, int end) {

		return _sharingEntryLocalService.getSharingEntries(start, end);
	}

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getSharingEntries(long classNameId, long classPK) {

		return _sharingEntryLocalService.getSharingEntries(
			classNameId, classPK);
	}

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param start the range's lower bound
	 * @param end the range's upper bound (not inclusive)
	 * @return the sharing entries
	 * @review
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getSharingEntries(
			long classNameId, long classPK, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.sharing.model.SharingEntry> orderByComparator) {

		return _sharingEntryLocalService.getSharingEntries(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the sharing entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the sharing entries
	 * @param companyId the primary key of the company
	 * @return the matching sharing entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getSharingEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return _sharingEntryLocalService.getSharingEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of sharing entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the sharing entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching sharing entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getSharingEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.sharing.model.SharingEntry> orderByComparator) {

		return _sharingEntryLocalService.getSharingEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries.
	 *
	 * @return the number of sharing entries
	 */
	@Override
	public int getSharingEntriesCount() {
		return _sharingEntryLocalService.getSharingEntriesCount();
	}

	/**
	 * Returns the resource's sharing entries count. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries count
	 * @review
	 */
	@Override
	public int getSharingEntriesCount(long classNameId, long classPK) {
		return _sharingEntryLocalService.getSharingEntriesCount(
			classNameId, classPK);
	}

	/**
	 * Returns the sharing entry with the primary key.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry with the primary key could not be found
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry getSharingEntry(
			long sharingEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.getSharingEntry(sharingEntryId);
	}

	/**
	 * Returns the sharing entry for the resource shared with the user. The
	 * class name ID and class primary key identify the resource's type and
	 * instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entry
	 * @review
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry getSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.getSharingEntry(
			toUserId, classNameId, classPK);
	}

	@Override
	public com.liferay.sharing.model.SharingEntry
			getSharingEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.getSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry
	 * @throws PortalException if a matching sharing entry could not be found
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry
			getSharingEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.getSharingEntryByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the list of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the list of sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getToUserSharingEntries(long toUserId) {

		return _sharingEntryLocalService.getToUserSharingEntries(toUserId);
	}

	/**
	 * Returns the range of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @param start the range's lower bound
	 * @param end the range's upper bound (not inclusive)
	 * @return the range of sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getToUserSharingEntries(long toUserId, int start, int end) {

		return _sharingEntryLocalService.getToUserSharingEntries(
			toUserId, start, end);
	}

	/**
	 * Returns the list of sharing entries for the type of resource shared with
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the list of sharing entries
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getToUserSharingEntries(long toUserId, long classNameId) {

		return _sharingEntryLocalService.getToUserSharingEntries(
			toUserId, classNameId);
	}

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared with the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @param start the ordered range's lower bound
	 * @param end the ordered range's upper bound (not inclusive)
	 * @param orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Override
	public java.util.List<com.liferay.sharing.model.SharingEntry>
		getToUserSharingEntries(
			long toUserId, long classNameId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.sharing.model.SharingEntry> orderByComparator) {

		return _sharingEntryLocalService.getToUserSharingEntries(
			toUserId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the number of sharing entries
	 */
	@Override
	public int getToUserSharingEntriesCount(long toUserId) {
		return _sharingEntryLocalService.getToUserSharingEntriesCount(toUserId);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared
	 * with the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Override
	public int getToUserSharingEntriesCount(long toUserId, long classNameId) {
		return _sharingEntryLocalService.getToUserSharingEntriesCount(
			toUserId, classNameId);
	}

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with a user who can also share that resource. The class name
	 * ID and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the shared resource
	 * @param sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 been shared with a user who can also share that resource; {@code
	 false} otherwise
	 */
	@Override
	public boolean hasShareableSharingPermission(
		long toUserId, long classNameId, long classPK,
		com.liferay.sharing.security.permission.SharingEntryAction
			sharingEntryAction) {

		return _sharingEntryLocalService.hasShareableSharingPermission(
			toUserId, classNameId, classPK, sharingEntryAction);
	}

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with the user. The class name ID and class primary key
	 * identify the resource's type and instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the shared resource
	 * @param sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 been shared with the user; {@code false} otherwise
	 */
	@Override
	public boolean hasSharingPermission(
		long toUserId, long classNameId, long classPK,
		com.liferay.sharing.security.permission.SharingEntryAction
			sharingEntryAction) {

		return _sharingEntryLocalService.hasSharingPermission(
			toUserId, classNameId, classPK, sharingEntryAction);
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @param sharingEntryActions the sharing entry actions
	 * @param shareable whether the user the resource is shared with can
	 also share it
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry does not exist, if the
	 sharing entry actions are invalid (e.g., empty, don't contain
	 {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 value), or if the expiration date is a past value
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 SharingEntryLocalService#updateSharingEntry(
	 long, long, Collection, boolean, Date, ServiceContext)}
	 * @review
	 */
	@Deprecated
	@Override
	public com.liferay.sharing.model.SharingEntry updateSharingEntry(
			long sharingEntryId,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			boolean shareable, java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.updateSharingEntry(
			sharingEntryId, sharingEntryActions, shareable, expirationDate,
			serviceContext);
	}

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param userId the primary key of the user updating the sharing entry
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
	 * @review
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry updateSharingEntry(
			long userId, long sharingEntryId,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			boolean shareable, java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharingEntryLocalService.updateSharingEntry(
			userId, sharingEntryId, sharingEntryActions, shareable,
			expirationDate, serviceContext);
	}

	/**
	 * Updates the sharing entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was updated
	 */
	@Override
	public com.liferay.sharing.model.SharingEntry updateSharingEntry(
		com.liferay.sharing.model.SharingEntry sharingEntry) {

		return _sharingEntryLocalService.updateSharingEntry(sharingEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sharingEntryLocalService.getBasePersistence();
	}

	@Override
	public SharingEntryLocalService getWrappedService() {
		return _sharingEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SharingEntryLocalService sharingEntryLocalService) {

		_sharingEntryLocalService = sharingEntryLocalService;
	}

	private SharingEntryLocalService _sharingEntryLocalService;

}