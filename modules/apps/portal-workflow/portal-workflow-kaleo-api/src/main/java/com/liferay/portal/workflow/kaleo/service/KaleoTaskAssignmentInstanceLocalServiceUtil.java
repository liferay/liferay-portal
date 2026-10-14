/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoTaskAssignmentInstance. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskAssignmentInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskAssignmentInstanceLocalService
 * @generated
 */
public class KaleoTaskAssignmentInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskAssignmentInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo task assignment instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was added
	 */
	public static KaleoTaskAssignmentInstance addKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return getService().addKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstance);
	}

	public static KaleoTaskAssignmentInstance addKaleoTaskAssignmentInstance(
			long groupId,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoTaskAssignmentInstance(
			groupId, kaleoTaskInstanceToken, assigneeClassName, assigneeClassPK,
			serviceContext);
	}

	public static List<KaleoTaskAssignmentInstance> addTaskAssignmentInstances(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment>
					kaleoTaskAssignments,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addTaskAssignmentInstances(
			kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
			serviceContext);
	}

	public static KaleoTaskAssignmentInstance assignKaleoTaskAssignmentInstance(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().assignKaleoTaskAssignmentInstance(
			kaleoTaskInstanceToken, assigneeClassName, assigneeClassPK,
			serviceContext);
	}

	public static List<KaleoTaskAssignmentInstance>
			assignKaleoTaskAssignmentInstances(
				com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
					kaleoTaskInstanceToken,
				java.util.Collection
					<com.liferay.portal.workflow.kaleo.model.
						KaleoTaskAssignment> kaleoTaskAssignments,
				Map<String, Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().assignKaleoTaskAssignmentInstances(
			kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
			serviceContext);
	}

	public static KaleoTaskAssignmentInstance completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().completeKaleoTaskInstanceToken(
			kaleoTaskInstanceTokenId, serviceContext);
	}

	/**
	 * Creates a new kaleo task assignment instance with the primary key. Does not add the kaleo task assignment instance to the database.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key for the new kaleo task assignment instance
	 * @return the new kaleo task assignment instance
	 */
	public static KaleoTaskAssignmentInstance createKaleoTaskAssignmentInstance(
		long kaleoTaskAssignmentInstanceId) {

		return getService().createKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoTaskAssignmentInstances(
		long companyId) {

		getService().deleteCompanyKaleoTaskAssignmentInstances(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
		long kaleoDefinitionId) {

		getService().deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
			kaleoDefinitionId);
	}

	public static void deleteKaleoInstanceKaleoTaskAssignmentInstances(
		long kaleoInstanceId) {

		getService().deleteKaleoInstanceKaleoTaskAssignmentInstances(
			kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo task assignment instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 */
	public static KaleoTaskAssignmentInstance deleteKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return getService().deleteKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstance);
	}

	/**
	 * Deletes the kaleo task assignment instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 * @throws PortalException if a kaleo task assignment instance with the primary key could not be found
	 */
	public static KaleoTaskAssignmentInstance deleteKaleoTaskAssignmentInstance(
			long kaleoTaskAssignmentInstanceId)
		throws PortalException {

		return getService().deleteKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstanceId);
	}

	public static void deleteKaleoTaskAssignmentInstances(
		com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
			kaleoTaskInstanceToken) {

		getService().deleteKaleoTaskAssignmentInstances(kaleoTaskInstanceToken);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
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

	public static KaleoTaskAssignmentInstance
		fetchFirstKaleoTaskAssignmentInstance(
			long kaleoTaskInstanceTokenId,
			OrderByComparator<KaleoTaskAssignmentInstance> orderByComparator) {

		return getService().fetchFirstKaleoTaskAssignmentInstance(
			kaleoTaskInstanceTokenId, orderByComparator);
	}

	public static KaleoTaskAssignmentInstance fetchKaleoTaskAssignmentInstance(
		long kaleoTaskAssignmentInstanceId) {

		return getService().fetchKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstanceId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance
	 * @throws PortalException if a kaleo task assignment instance with the primary key could not be found
	 */
	public static KaleoTaskAssignmentInstance getKaleoTaskAssignmentInstance(
			long kaleoTaskAssignmentInstanceId)
		throws PortalException {

		return getService().getKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstanceId);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of kaleo task assignment instances
	 */
	public static List<KaleoTaskAssignmentInstance>
		getKaleoTaskAssignmentInstances(int start, int end) {

		return getService().getKaleoTaskAssignmentInstances(start, end);
	}

	public static List<KaleoTaskAssignmentInstance>
		getKaleoTaskAssignmentInstances(long kaleoTaskInstanceTokenId) {

		return getService().getKaleoTaskAssignmentInstances(
			kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns the number of kaleo task assignment instances.
	 *
	 * @return the number of kaleo task assignment instances
	 */
	public static int getKaleoTaskAssignmentInstancesCount() {
		return getService().getKaleoTaskAssignmentInstancesCount();
	}

	public static int getKaleoTaskAssignmentInstancesCount(
		long kaleoTaskInstanceTokenId) {

		return getService().getKaleoTaskAssignmentInstancesCount(
			kaleoTaskInstanceTokenId);
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

	/**
	 * Updates the kaleo task assignment instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was updated
	 */
	public static KaleoTaskAssignmentInstance updateKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return getService().updateKaleoTaskAssignmentInstance(
			kaleoTaskAssignmentInstance);
	}

	public static KaleoTaskAssignmentInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoTaskAssignmentInstanceLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoTaskAssignmentInstanceLocalServiceUtil.class,
			KaleoTaskAssignmentInstanceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-961660734