/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DispatchTriggerLocalService}.
 *
 * @author Matija Petanjek
 * @see DispatchTriggerLocalService
 * @generated
 */
public class DispatchTriggerLocalServiceWrapper
	implements DispatchTriggerLocalService,
			   ServiceWrapper<DispatchTriggerLocalService> {

	public DispatchTriggerLocalServiceWrapper() {
		this(null);
	}

	public DispatchTriggerLocalServiceWrapper(
		DispatchTriggerLocalService dispatchTriggerLocalService) {

		_dispatchTriggerLocalService = dispatchTriggerLocalService;
	}

	/**
	 * Adds the dispatch trigger to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DispatchTriggerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dispatchTrigger the dispatch trigger
	 * @return the dispatch trigger that was added
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger addDispatchTrigger(
		com.liferay.dispatch.model.DispatchTrigger dispatchTrigger) {

		return _dispatchTriggerLocalService.addDispatchTrigger(dispatchTrigger);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			com.liferay.dispatch.executor.DispatchTaskExecutor
				dispatchTaskExecutor,
			String dispatchTaskExecutorType,
			com.liferay.portal.kernel.util.UnicodeProperties
				dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.addDispatchTrigger(
			externalReferenceCode, userId, dispatchTaskExecutor,
			dispatchTaskExecutorType, dispatchTaskSettingsUnicodeProperties,
			name, system);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			String dispatchTaskExecutorType,
			com.liferay.portal.kernel.util.UnicodeProperties
				dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.addDispatchTrigger(
			externalReferenceCode, userId, dispatchTaskExecutorType,
			dispatchTaskSettingsUnicodeProperties, name, system);
	}

	/**
	 * Creates a new dispatch trigger with the primary key. Does not add the dispatch trigger to the database.
	 *
	 * @param dispatchTriggerId the primary key for the new dispatch trigger
	 * @return the new dispatch trigger
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger createDispatchTrigger(
		long dispatchTriggerId) {

		return _dispatchTriggerLocalService.createDispatchTrigger(
			dispatchTriggerId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the dispatch trigger from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DispatchTriggerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dispatchTrigger the dispatch trigger
	 * @return the dispatch trigger that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger deleteDispatchTrigger(
			com.liferay.dispatch.model.DispatchTrigger dispatchTrigger)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.deleteDispatchTrigger(
			dispatchTrigger);
	}

	/**
	 * Deletes the dispatch trigger with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DispatchTriggerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger that was removed
	 * @throws PortalException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger deleteDispatchTrigger(
			long dispatchTriggerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.deleteDispatchTrigger(
			dispatchTriggerId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dispatchTriggerLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dispatchTriggerLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dispatchTriggerLocalService.dynamicQuery();
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

		return _dispatchTriggerLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dispatch.model.impl.DispatchTriggerModelImpl</code>.
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

		return _dispatchTriggerLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dispatch.model.impl.DispatchTriggerModelImpl</code>.
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

		return _dispatchTriggerLocalService.dynamicQuery(
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

		return _dispatchTriggerLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dispatchTriggerLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger fetchDispatchTrigger(
		long dispatchTriggerId) {

		return _dispatchTriggerLocalService.fetchDispatchTrigger(
			dispatchTriggerId);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger fetchDispatchTrigger(
		long companyId, String name) {

		return _dispatchTriggerLocalService.fetchDispatchTrigger(
			companyId, name);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger
		fetchDispatchTriggerByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _dispatchTriggerLocalService.
			fetchDispatchTriggerByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the dispatch trigger with the matching UUID and company.
	 *
	 * @param uuid the dispatch trigger's UUID
	 * @param companyId the primary key of the company
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger
		fetchDispatchTriggerByUuidAndCompanyId(String uuid, long companyId) {

		return _dispatchTriggerLocalService.
			fetchDispatchTriggerByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.Date fetchNextFireDate(long dispatchTriggerId) {
		return _dispatchTriggerLocalService.fetchNextFireDate(
			dispatchTriggerId);
	}

	@Override
	public java.util.Date fetchPreviousFireDate(long dispatchTriggerId) {
		return _dispatchTriggerLocalService.fetchPreviousFireDate(
			dispatchTriggerId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dispatchTriggerLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getActiveDispatchTriggers(
			java.util.List
				<com.liferay.dispatch.executor.DispatchTaskClusterMode>
					dispatchTaskClusterModes) {

		return _dispatchTriggerLocalService.getActiveDispatchTriggers(
			dispatchTaskClusterModes);
	}

	/**
	 * Returns the dispatch trigger with the primary key.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger
	 * @throws PortalException if a dispatch trigger with the primary key could not be found
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger getDispatchTrigger(
			long dispatchTriggerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.getDispatchTrigger(
			dispatchTriggerId);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger
			getDispatchTriggerByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.
			getDispatchTriggerByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the dispatch trigger with the matching UUID and company.
	 *
	 * @param uuid the dispatch trigger's UUID
	 * @param companyId the primary key of the company
	 * @return the matching dispatch trigger
	 * @throws PortalException if a matching dispatch trigger could not be found
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger
			getDispatchTriggerByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.
			getDispatchTriggerByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getDispatchTriggers(boolean active) {

		return _dispatchTriggerLocalService.getDispatchTriggers(active);
	}

	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getDispatchTriggers(
			boolean active,
			com.liferay.dispatch.executor.DispatchTaskClusterMode
				dispatchTaskClusterMode) {

		return _dispatchTriggerLocalService.getDispatchTriggers(
			active, dispatchTaskClusterMode);
	}

	/**
	 * Returns a range of all the dispatch triggers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dispatch.model.impl.DispatchTriggerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dispatch triggers
	 * @param end the upper bound of the range of dispatch triggers (not inclusive)
	 * @return the range of dispatch triggers
	 */
	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getDispatchTriggers(int start, int end) {

		return _dispatchTriggerLocalService.getDispatchTriggers(start, end);
	}

	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getDispatchTriggers(long companyId, int start, int end) {

		return _dispatchTriggerLocalService.getDispatchTriggers(
			companyId, start, end);
	}

	/**
	 * Returns the number of dispatch triggers.
	 *
	 * @return the number of dispatch triggers
	 */
	@Override
	public int getDispatchTriggersCount() {
		return _dispatchTriggerLocalService.getDispatchTriggersCount();
	}

	@Override
	public int getDispatchTriggersCount(long companyId) {
		return _dispatchTriggerLocalService.getDispatchTriggersCount(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dispatchTriggerLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dispatchTriggerLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.Date getNextFireDate(long dispatchTriggerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.getNextFireDate(dispatchTriggerId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dispatchTriggerLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.Date getPreviousFireDate(long dispatchTriggerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.getPreviousFireDate(
			dispatchTriggerId);
	}

	@Override
	public java.util.List<com.liferay.dispatch.model.DispatchTrigger>
		getUserDispatchTriggers(
			long companyId, long userId, int start, int end) {

		return _dispatchTriggerLocalService.getUserDispatchTriggers(
			companyId, userId, start, end);
	}

	@Override
	public int getUserDispatchTriggersCount(long companyId, long userId) {
		return _dispatchTriggerLocalService.getUserDispatchTriggersCount(
			companyId, userId);
	}

	/**
	 * Updates the dispatch trigger in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DispatchTriggerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dispatchTrigger the dispatch trigger
	 * @return the dispatch trigger that was updated
	 */
	@Override
	public com.liferay.dispatch.model.DispatchTrigger updateDispatchTrigger(
		com.liferay.dispatch.model.DispatchTrigger dispatchTrigger) {

		return _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTrigger);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId, boolean active, String cronExpression,
			com.liferay.dispatch.executor.DispatchTaskClusterMode
				dispatchTaskClusterMode,
			int endDateMonth, int endDateDay, int endDateYear, int endDateHour,
			int endDateMinute, boolean neverEnd, boolean overlapAllowed,
			int startDateMonth, int startDateDay, int startDateYear,
			int startDateHour, int startDateMinute, String timeZoneId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTriggerId, active, cronExpression, dispatchTaskClusterMode,
			endDateMonth, endDateDay, endDateYear, endDateHour, endDateMinute,
			neverEnd, overlapAllowed, startDateMonth, startDateDay,
			startDateYear, startDateHour, startDateMinute, timeZoneId);
	}

	@Override
	public com.liferay.dispatch.model.DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId,
			com.liferay.portal.kernel.util.UnicodeProperties
				taskSettingsUnicodeProperties,
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTriggerId, taskSettingsUnicodeProperties, name);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dispatchTriggerLocalService.getBasePersistence();
	}

	@Override
	public DispatchTriggerLocalService getWrappedService() {
		return _dispatchTriggerLocalService;
	}

	@Override
	public void setWrappedService(
		DispatchTriggerLocalService dispatchTriggerLocalService) {

		_dispatchTriggerLocalService = dispatchTriggerLocalService;
	}

	private DispatchTriggerLocalService _dispatchTriggerLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-594566932