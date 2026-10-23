/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link SystemEventLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SystemEventLocalService
 * @generated
 */
public class SystemEventLocalServiceWrapper
	implements ServiceWrapper<SystemEventLocalService>,
			   SystemEventLocalService {

	public SystemEventLocalServiceWrapper() {
		this(null);
	}

	public SystemEventLocalServiceWrapper(
		SystemEventLocalService systemEventLocalService) {

		_systemEventLocalService = systemEventLocalService;
	}

	@Override
	public SystemEvent addSystemEvent(
			long userId, long groupId, String classExternalReferenceCode,
			String className, long classPK, String classUuid,
			String referrerClassName, int type, String extraData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.addSystemEvent(
			userId, groupId, classExternalReferenceCode, className, classPK,
			classUuid, referrerClassName, type, extraData);
	}

	@Override
	public SystemEvent addSystemEvent(
			long companyId, String classExternalReferenceCode, String className,
			long classPK, String classUuid, String referrerClassName, int type,
			String extraData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.addSystemEvent(
			companyId, classExternalReferenceCode, className, classPK,
			classUuid, referrerClassName, type, extraData);
	}

	/**
	 * Adds the system event to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was added
	 */
	@Override
	public SystemEvent addSystemEvent(SystemEvent systemEvent) {
		return _systemEventLocalService.addSystemEvent(systemEvent);
	}

	@Override
	public void checkSystemEvents()
		throws com.liferay.portal.kernel.exception.PortalException {

		_systemEventLocalService.checkSystemEvents();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new system event with the primary key. Does not add the system event to the database.
	 *
	 * @param systemEventId the primary key for the new system event
	 * @return the new system event
	 */
	@Override
	public SystemEvent createSystemEvent(long systemEventId) {
		return _systemEventLocalService.createSystemEvent(systemEventId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the system event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEventId the primary key of the system event
	 * @return the system event that was removed
	 * @throws PortalException if a system event with the primary key could not be found
	 */
	@Override
	public SystemEvent deleteSystemEvent(long systemEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.deleteSystemEvent(systemEventId);
	}

	/**
	 * Deletes the system event from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was removed
	 */
	@Override
	public SystemEvent deleteSystemEvent(SystemEvent systemEvent) {
		return _systemEventLocalService.deleteSystemEvent(systemEvent);
	}

	@Override
	public void deleteSystemEvents(long groupId) {
		_systemEventLocalService.deleteSystemEvents(groupId);
	}

	@Override
	public void deleteSystemEvents(long groupId, long systemEventSetKey) {
		_systemEventLocalService.deleteSystemEvents(groupId, systemEventSetKey);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _systemEventLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _systemEventLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _systemEventLocalService.dynamicQuery();
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

		return _systemEventLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
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

		return _systemEventLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
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

		return _systemEventLocalService.dynamicQuery(
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

		return _systemEventLocalService.dynamicQueryCount(dynamicQuery);
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

		return _systemEventLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SystemEvent fetchSystemEvent(long systemEventId) {
		return _systemEventLocalService.fetchSystemEvent(systemEventId);
	}

	@Override
	public SystemEvent fetchSystemEvent(
		long groupId, long classNameId, long classPK, int type) {

		return _systemEventLocalService.fetchSystemEvent(
			groupId, classNameId, classPK, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _systemEventLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _systemEventLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _systemEventLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the system event with the primary key.
	 *
	 * @param systemEventId the primary key of the system event
	 * @return the system event
	 * @throws PortalException if a system event with the primary key could not be found
	 */
	@Override
	public SystemEvent getSystemEvent(long systemEventId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.getSystemEvent(systemEventId);
	}

	/**
	 * Returns a range of all the system events.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.SystemEventModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of system events
	 * @param end the upper bound of the range of system events (not inclusive)
	 * @return the range of system events
	 */
	@Override
	public java.util.List<SystemEvent> getSystemEvents(int start, int end) {
		return _systemEventLocalService.getSystemEvents(start, end);
	}

	@Override
	public java.util.List<SystemEvent> getSystemEvents(
		long groupId, long classNameId, long classPK, int type) {

		return _systemEventLocalService.getSystemEvents(
			groupId, classNameId, classPK, type);
	}

	/**
	 * Returns the number of system events.
	 *
	 * @return the number of system events
	 */
	@Override
	public int getSystemEventsCount() {
		return _systemEventLocalService.getSystemEventsCount();
	}

	/**
	 * Updates the system event in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SystemEventLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param systemEvent the system event
	 * @return the system event that was updated
	 */
	@Override
	public SystemEvent updateSystemEvent(SystemEvent systemEvent) {
		return _systemEventLocalService.updateSystemEvent(systemEvent);
	}

	@Override
	public boolean validateGroup(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _systemEventLocalService.validateGroup(groupId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _systemEventLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SystemEvent> getCTPersistence() {
		return _systemEventLocalService.getCTPersistence();
	}

	@Override
	public Class<SystemEvent> getModelClass() {
		return _systemEventLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SystemEvent>, R, E>
				updateUnsafeFunction)
		throws E {

		return _systemEventLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SystemEventLocalService getWrappedService() {
		return _systemEventLocalService;
	}

	@Override
	public void setWrappedService(
		SystemEventLocalService systemEventLocalService) {

		_systemEventLocalService = systemEventLocalService;
	}

	private SystemEventLocalService _systemEventLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:484107589