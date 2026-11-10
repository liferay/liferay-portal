/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceNotificationQueueEntryLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationQueueEntryLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class CommerceNotificationQueueEntryLocalServiceWrapper
	implements CommerceNotificationQueueEntryLocalService,
			   ServiceWrapper<CommerceNotificationQueueEntryLocalService> {

	public CommerceNotificationQueueEntryLocalServiceWrapper() {
		this(null);
	}

	public CommerceNotificationQueueEntryLocalServiceWrapper(
		CommerceNotificationQueueEntryLocalService
			commerceNotificationQueueEntryLocalService) {

		_commerceNotificationQueueEntryLocalService =
			commerceNotificationQueueEntryLocalService;
	}

	/**
	 * Adds the commerce notification queue entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was added
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
			addCommerceNotificationQueueEntry(
				com.liferay.commerce.notification.model.
					CommerceNotificationQueueEntry
						commerceNotificationQueueEntry) {

		return _commerceNotificationQueueEntryLocalService.
			addCommerceNotificationQueueEntry(commerceNotificationQueueEntry);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), this method will be replaced
	 */
	@Deprecated
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				addCommerceNotificationQueueEntry(
					long userId, long groupId,
					long commerceNotificationTemplateId, String from,
					String fromName, String to, String toName, String cc,
					String bcc, String subject, String body, double priority)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			addCommerceNotificationQueueEntry(
				userId, groupId, commerceNotificationTemplateId, from, fromName,
				to, toName, cc, bcc, subject, body, priority);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				addCommerceNotificationQueueEntry(
					long userId, long groupId, String className, long classPK,
					long commerceNotificationTemplateId, String from,
					String fromName, String to, String toName, String cc,
					String bcc, String subject, String body, double priority)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			addCommerceNotificationQueueEntry(
				userId, groupId, className, classPK,
				commerceNotificationTemplateId, from, fromName, to, toName, cc,
				bcc, subject, body, priority);
	}

	/**
	 * Creates a new commerce notification queue entry with the primary key. Does not add the commerce notification queue entry to the database.
	 *
	 * @param commerceNotificationQueueEntryId the primary key for the new commerce notification queue entry
	 * @return the new commerce notification queue entry
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
			createCommerceNotificationQueueEntry(
				long commerceNotificationQueueEntryId) {

		return _commerceNotificationQueueEntryLocalService.
			createCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				deleteCommerceNotificationQueue(
					com.liferay.commerce.notification.model.
						CommerceNotificationQueueEntry
							commerceNotificationQueueEntry)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueue(commerceNotificationQueueEntry);
	}

	@Override
	public void deleteCommerceNotificationQueueEntries(java.util.Date sentDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntries(sentDate);
	}

	@Override
	public void deleteCommerceNotificationQueueEntries(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntries(groupId);
	}

	/**
	 * Deletes the commerce notification queue entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was removed
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
			deleteCommerceNotificationQueueEntry(
				com.liferay.commerce.notification.model.
					CommerceNotificationQueueEntry
						commerceNotificationQueueEntry) {

		return _commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntry(
				commerceNotificationQueueEntry);
	}

	/**
	 * Deletes the commerce notification queue entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry that was removed
	 * @throws PortalException if a commerce notification queue entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				deleteCommerceNotificationQueueEntry(
					long commerceNotificationQueueEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			deleteCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceNotificationQueueEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceNotificationQueueEntryLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceNotificationQueueEntryLocalService.dynamicQuery();
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

		return _commerceNotificationQueueEntryLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
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

		return _commerceNotificationQueueEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
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

		return _commerceNotificationQueueEntryLocalService.dynamicQuery(
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

		return _commerceNotificationQueueEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _commerceNotificationQueueEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
			fetchCommerceNotificationQueueEntry(
				long commerceNotificationQueueEntryId) {

		return _commerceNotificationQueueEntryLocalService.
			fetchCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceNotificationQueueEntryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationQueueEntry>
			getCommerceNotificationQueueEntries(boolean sent) {

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntries(sent);
	}

	/**
	 * Returns a range of all the commerce notification queue entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationQueueEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification queue entries
	 * @param end the upper bound of the range of commerce notification queue entries (not inclusive)
	 * @return the range of commerce notification queue entries
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationQueueEntry>
			getCommerceNotificationQueueEntries(int start, int end) {

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntries(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.CommerceNotificationQueueEntry>
			getCommerceNotificationQueueEntries(
				long groupId, String className, long classPK, boolean sent,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.notification.model.
						CommerceNotificationQueueEntry> orderByComparator) {

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntries(
				groupId, className, classPK, sent, start, end,
				orderByComparator);
	}

	/**
	 * Returns the number of commerce notification queue entries.
	 *
	 * @return the number of commerce notification queue entries
	 */
	@Override
	public int getCommerceNotificationQueueEntriesCount() {
		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntriesCount();
	}

	@Override
	public int getCommerceNotificationQueueEntriesCount(
		long groupId, String className, long classPK, boolean sent) {

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntriesCount(
				groupId, className, classPK, sent);
	}

	/**
	 * Returns the commerce notification queue entry with the primary key.
	 *
	 * @param commerceNotificationQueueEntryId the primary key of the commerce notification queue entry
	 * @return the commerce notification queue entry
	 * @throws PortalException if a commerce notification queue entry with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				getCommerceNotificationQueueEntry(
					long commerceNotificationQueueEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			getCommerceNotificationQueueEntry(commerceNotificationQueueEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceNotificationQueueEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceNotificationQueueEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				resendCommerceNotificationQueueEntry(
					long commerceNotificationQueueEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.
			resendCommerceNotificationQueueEntry(
				commerceNotificationQueueEntryId);
	}

	@Override
	public void sendCommerceNotificationQueueEntries() throws Exception {
		_commerceNotificationQueueEntryLocalService.
			sendCommerceNotificationQueueEntries();
	}

	@Override
	public void updateCommerceNotificationQueueEntriesTemplateIds(
		long commerceNotificationTemplateId) {

		_commerceNotificationQueueEntryLocalService.
			updateCommerceNotificationQueueEntriesTemplateIds(
				commerceNotificationTemplateId);
	}

	/**
	 * Updates the commerce notification queue entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationQueueEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationQueueEntry the commerce notification queue entry
	 * @return the commerce notification queue entry that was updated
	 */
	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
			updateCommerceNotificationQueueEntry(
				com.liferay.commerce.notification.model.
					CommerceNotificationQueueEntry
						commerceNotificationQueueEntry) {

		return _commerceNotificationQueueEntryLocalService.
			updateCommerceNotificationQueueEntry(
				commerceNotificationQueueEntry);
	}

	@Override
	public
		com.liferay.commerce.notification.model.CommerceNotificationQueueEntry
				updateSent(long commerceNotificationQueueEntryId, boolean sent)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationQueueEntryLocalService.updateSent(
			commerceNotificationQueueEntryId, sent);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceNotificationQueueEntryLocalService.getBasePersistence();
	}

	@Override
	public CommerceNotificationQueueEntryLocalService getWrappedService() {
		return _commerceNotificationQueueEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceNotificationQueueEntryLocalService
			commerceNotificationQueueEntryLocalService) {

		_commerceNotificationQueueEntryLocalService =
			commerceNotificationQueueEntryLocalService;
	}

	private CommerceNotificationQueueEntryLocalService
		_commerceNotificationQueueEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:890959480