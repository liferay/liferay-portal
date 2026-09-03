/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service;

import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DispatchTrigger. This utility wraps
 * <code>com.liferay.dispatch.service.impl.DispatchTriggerLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Matija Petanjek
 * @see DispatchTriggerLocalService
 * @generated
 */
public class DispatchTriggerLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dispatch.service.impl.DispatchTriggerLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static DispatchTrigger addDispatchTrigger(
		DispatchTrigger dispatchTrigger) {

		return getService().addDispatchTrigger(dispatchTrigger);
	}

	public static DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			com.liferay.dispatch.executor.DispatchTaskExecutor
				dispatchTaskExecutor,
			String dispatchTaskExecutorType,
			com.liferay.portal.kernel.util.UnicodeProperties
				dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws PortalException {

		return getService().addDispatchTrigger(
			externalReferenceCode, userId, dispatchTaskExecutor,
			dispatchTaskExecutorType, dispatchTaskSettingsUnicodeProperties,
			name, system);
	}

	public static DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			String dispatchTaskExecutorType,
			com.liferay.portal.kernel.util.UnicodeProperties
				dispatchTaskSettingsUnicodeProperties,
			String name, boolean system)
		throws PortalException {

		return getService().addDispatchTrigger(
			externalReferenceCode, userId, dispatchTaskExecutorType,
			dispatchTaskSettingsUnicodeProperties, name, system);
	}

	/**
	 * Creates a new dispatch trigger with the primary key. Does not add the dispatch trigger to the database.
	 *
	 * @param dispatchTriggerId the primary key for the new dispatch trigger
	 * @return the new dispatch trigger
	 */
	public static DispatchTrigger createDispatchTrigger(
		long dispatchTriggerId) {

		return getService().createDispatchTrigger(dispatchTriggerId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static DispatchTrigger deleteDispatchTrigger(
			DispatchTrigger dispatchTrigger)
		throws PortalException {

		return getService().deleteDispatchTrigger(dispatchTrigger);
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
	public static DispatchTrigger deleteDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		return getService().deleteDispatchTrigger(dispatchTriggerId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static DispatchTrigger fetchDispatchTrigger(long dispatchTriggerId) {
		return getService().fetchDispatchTrigger(dispatchTriggerId);
	}

	public static DispatchTrigger fetchDispatchTrigger(
		long companyId, String name) {

		return getService().fetchDispatchTrigger(companyId, name);
	}

	public static DispatchTrigger fetchDispatchTriggerByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchDispatchTriggerByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the dispatch trigger with the matching UUID and company.
	 *
	 * @param uuid the dispatch trigger's UUID
	 * @param companyId the primary key of the company
	 * @return the matching dispatch trigger, or <code>null</code> if a matching dispatch trigger could not be found
	 */
	public static DispatchTrigger fetchDispatchTriggerByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchDispatchTriggerByUuidAndCompanyId(
			uuid, companyId);
	}

	public static java.util.Date fetchNextFireDate(long dispatchTriggerId) {
		return getService().fetchNextFireDate(dispatchTriggerId);
	}

	public static java.util.Date fetchPreviousFireDate(long dispatchTriggerId) {
		return getService().fetchPreviousFireDate(dispatchTriggerId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<DispatchTrigger> getActiveDispatchTriggers(
		List<com.liferay.dispatch.executor.DispatchTaskClusterMode>
			dispatchTaskClusterModes) {

		return getService().getActiveDispatchTriggers(dispatchTaskClusterModes);
	}

	/**
	 * Returns the dispatch trigger with the primary key.
	 *
	 * @param dispatchTriggerId the primary key of the dispatch trigger
	 * @return the dispatch trigger
	 * @throws PortalException if a dispatch trigger with the primary key could not be found
	 */
	public static DispatchTrigger getDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		return getService().getDispatchTrigger(dispatchTriggerId);
	}

	public static DispatchTrigger getDispatchTriggerByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getDispatchTriggerByExternalReferenceCode(
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
	public static DispatchTrigger getDispatchTriggerByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getDispatchTriggerByUuidAndCompanyId(
			uuid, companyId);
	}

	public static List<DispatchTrigger> getDispatchTriggers(boolean active) {
		return getService().getDispatchTriggers(active);
	}

	public static List<DispatchTrigger> getDispatchTriggers(
		boolean active,
		com.liferay.dispatch.executor.DispatchTaskClusterMode
			dispatchTaskClusterMode) {

		return getService().getDispatchTriggers(
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
	public static List<DispatchTrigger> getDispatchTriggers(
		int start, int end) {

		return getService().getDispatchTriggers(start, end);
	}

	public static List<DispatchTrigger> getDispatchTriggers(
		long companyId, int start, int end) {

		return getService().getDispatchTriggers(companyId, start, end);
	}

	/**
	 * Returns the number of dispatch triggers.
	 *
	 * @return the number of dispatch triggers
	 */
	public static int getDispatchTriggersCount() {
		return getService().getDispatchTriggersCount();
	}

	public static int getDispatchTriggersCount(long companyId) {
		return getService().getDispatchTriggersCount(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static java.util.Date getNextFireDate(long dispatchTriggerId)
		throws PortalException {

		return getService().getNextFireDate(dispatchTriggerId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static java.util.Date getPreviousFireDate(long dispatchTriggerId)
		throws PortalException {

		return getService().getPreviousFireDate(dispatchTriggerId);
	}

	public static List<DispatchTrigger> getUserDispatchTriggers(
		long companyId, long userId, int start, int end) {

		return getService().getUserDispatchTriggers(
			companyId, userId, start, end);
	}

	public static int getUserDispatchTriggersCount(
		long companyId, long userId) {

		return getService().getUserDispatchTriggersCount(companyId, userId);
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
	public static DispatchTrigger updateDispatchTrigger(
		DispatchTrigger dispatchTrigger) {

		return getService().updateDispatchTrigger(dispatchTrigger);
	}

	public static DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId, boolean active, String cronExpression,
			com.liferay.dispatch.executor.DispatchTaskClusterMode
				dispatchTaskClusterMode,
			int endDateMonth, int endDateDay, int endDateYear, int endDateHour,
			int endDateMinute, boolean neverEnd, boolean overlapAllowed,
			int startDateMonth, int startDateDay, int startDateYear,
			int startDateHour, int startDateMinute, String timeZoneId)
		throws PortalException {

		return getService().updateDispatchTrigger(
			dispatchTriggerId, active, cronExpression, dispatchTaskClusterMode,
			endDateMonth, endDateDay, endDateYear, endDateHour, endDateMinute,
			neverEnd, overlapAllowed, startDateMonth, startDateDay,
			startDateYear, startDateHour, startDateMinute, timeZoneId);
	}

	public static DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId,
			com.liferay.portal.kernel.util.UnicodeProperties
				taskSettingsUnicodeProperties,
			String name)
		throws PortalException {

		return getService().updateDispatchTrigger(
			dispatchTriggerId, taskSettingsUnicodeProperties, name);
	}

	public static DispatchTriggerLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DispatchTriggerLocalService>
		_serviceSnapshot = new Snapshot<>(
			DispatchTriggerLocalServiceUtil.class,
			DispatchTriggerLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:53945700