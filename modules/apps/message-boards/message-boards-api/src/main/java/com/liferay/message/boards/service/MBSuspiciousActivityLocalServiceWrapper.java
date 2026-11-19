/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBSuspiciousActivity;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link MBSuspiciousActivityLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MBSuspiciousActivityLocalService
 * @generated
 */
public class MBSuspiciousActivityLocalServiceWrapper
	implements MBSuspiciousActivityLocalService,
			   ServiceWrapper<MBSuspiciousActivityLocalService> {

	public MBSuspiciousActivityLocalServiceWrapper() {
		this(null);
	}

	public MBSuspiciousActivityLocalServiceWrapper(
		MBSuspiciousActivityLocalService mbSuspiciousActivityLocalService) {

		_mbSuspiciousActivityLocalService = mbSuspiciousActivityLocalService;
	}

	/**
	 * Adds the message boards suspicious activity to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was added
	 */
	@Override
	public MBSuspiciousActivity addMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return _mbSuspiciousActivityLocalService.addMBSuspiciousActivity(
			mbSuspiciousActivity);
	}

	@Override
	public MBSuspiciousActivity addOrUpdateMessageSuspiciousActivity(
			long userId, long messageId, String reason)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.
			addOrUpdateMessageSuspiciousActivity(userId, messageId, reason);
	}

	@Override
	public MBSuspiciousActivity addOrUpdateThreadSuspiciousActivity(
			long userId, long threadId, String reason)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.
			addOrUpdateThreadSuspiciousActivity(userId, threadId, reason);
	}

	/**
	 * Creates a new message boards suspicious activity with the primary key. Does not add the message boards suspicious activity to the database.
	 *
	 * @param suspiciousActivityId the primary key for the new message boards suspicious activity
	 * @return the new message boards suspicious activity
	 */
	@Override
	public MBSuspiciousActivity createMBSuspiciousActivity(
		long suspiciousActivityId) {

		return _mbSuspiciousActivityLocalService.createMBSuspiciousActivity(
			suspiciousActivityId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the message boards suspicious activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity that was removed
	 * @throws PortalException if a message boards suspicious activity with the primary key could not be found
	 */
	@Override
	public MBSuspiciousActivity deleteMBSuspiciousActivity(
			long suspiciousActivityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.deleteMBSuspiciousActivity(
			suspiciousActivityId);
	}

	/**
	 * Deletes the message boards suspicious activity from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was removed
	 */
	@Override
	public MBSuspiciousActivity deleteMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return _mbSuspiciousActivityLocalService.deleteMBSuspiciousActivity(
			mbSuspiciousActivity);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public MBSuspiciousActivity deleteSuspiciousActivity(
			long suspiciousActivityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.deleteSuspiciousActivity(
			suspiciousActivityId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mbSuspiciousActivityLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mbSuspiciousActivityLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mbSuspiciousActivityLocalService.dynamicQuery();
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

		return _mbSuspiciousActivityLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
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

		return _mbSuspiciousActivityLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
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

		return _mbSuspiciousActivityLocalService.dynamicQuery(
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

		return _mbSuspiciousActivityLocalService.dynamicQueryCount(
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

		return _mbSuspiciousActivityLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public MBSuspiciousActivity fetchMBSuspiciousActivity(
		long suspiciousActivityId) {

		return _mbSuspiciousActivityLocalService.fetchMBSuspiciousActivity(
			suspiciousActivityId);
	}

	/**
	 * Returns the message boards suspicious activity matching the UUID and group.
	 *
	 * @param uuid the message boards suspicious activity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards suspicious activity, or <code>null</code> if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity fetchMBSuspiciousActivityByUuidAndGroupId(
		String uuid, long groupId) {

		return _mbSuspiciousActivityLocalService.
			fetchMBSuspiciousActivityByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mbSuspiciousActivityLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _mbSuspiciousActivityLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mbSuspiciousActivityLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the message boards suspicious activities.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBSuspiciousActivityModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @return the range of message boards suspicious activities
	 */
	@Override
	public java.util.List<MBSuspiciousActivity> getMBSuspiciousActivities(
		int start, int end) {

		return _mbSuspiciousActivityLocalService.getMBSuspiciousActivities(
			start, end);
	}

	/**
	 * Returns all the message boards suspicious activities matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards suspicious activities
	 * @param companyId the primary key of the company
	 * @return the matching message boards suspicious activities, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBSuspiciousActivity>
		getMBSuspiciousActivitiesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _mbSuspiciousActivityLocalService.
			getMBSuspiciousActivitiesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of message boards suspicious activities matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards suspicious activities
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards suspicious activities
	 * @param end the upper bound of the range of message boards suspicious activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards suspicious activities, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBSuspiciousActivity>
		getMBSuspiciousActivitiesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<MBSuspiciousActivity> orderByComparator) {

		return _mbSuspiciousActivityLocalService.
			getMBSuspiciousActivitiesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards suspicious activities.
	 *
	 * @return the number of message boards suspicious activities
	 */
	@Override
	public int getMBSuspiciousActivitiesCount() {
		return _mbSuspiciousActivityLocalService.
			getMBSuspiciousActivitiesCount();
	}

	/**
	 * Returns the message boards suspicious activity with the primary key.
	 *
	 * @param suspiciousActivityId the primary key of the message boards suspicious activity
	 * @return the message boards suspicious activity
	 * @throws PortalException if a message boards suspicious activity with the primary key could not be found
	 */
	@Override
	public MBSuspiciousActivity getMBSuspiciousActivity(
			long suspiciousActivityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.getMBSuspiciousActivity(
			suspiciousActivityId);
	}

	/**
	 * Returns the message boards suspicious activity matching the UUID and group.
	 *
	 * @param uuid the message boards suspicious activity's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards suspicious activity
	 * @throws PortalException if a matching message boards suspicious activity could not be found
	 */
	@Override
	public MBSuspiciousActivity getMBSuspiciousActivityByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.
			getMBSuspiciousActivityByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mbSuspiciousActivityLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the message boards suspicious activity in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBSuspiciousActivityLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbSuspiciousActivity the message boards suspicious activity
	 * @return the message boards suspicious activity that was updated
	 */
	@Override
	public MBSuspiciousActivity updateMBSuspiciousActivity(
		MBSuspiciousActivity mbSuspiciousActivity) {

		return _mbSuspiciousActivityLocalService.updateMBSuspiciousActivity(
			mbSuspiciousActivity);
	}

	@Override
	public MBSuspiciousActivity updateValidated(long suspiciousActivityId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbSuspiciousActivityLocalService.updateValidated(
			suspiciousActivityId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mbSuspiciousActivityLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<MBSuspiciousActivity> getCTPersistence() {
		return _mbSuspiciousActivityLocalService.getCTPersistence();
	}

	@Override
	public Class<MBSuspiciousActivity> getModelClass() {
		return _mbSuspiciousActivityLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBSuspiciousActivity>, R, E>
				updateUnsafeFunction)
		throws E {

		return _mbSuspiciousActivityLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public MBSuspiciousActivityLocalService getWrappedService() {
		return _mbSuspiciousActivityLocalService;
	}

	@Override
	public void setWrappedService(
		MBSuspiciousActivityLocalService mbSuspiciousActivityLocalService) {

		_mbSuspiciousActivityLocalService = mbSuspiciousActivityLocalService;
	}

	private MBSuspiciousActivityLocalService _mbSuspiciousActivityLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:803444527