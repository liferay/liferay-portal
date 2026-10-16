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
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KaleoInstance. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceLocalService
 * @generated
 */
public class KaleoInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was added
	 */
	public static KaleoInstance addKaleoInstance(KaleoInstance kaleoInstance) {
		return getService().addKaleoInstance(kaleoInstance);
	}

	public static KaleoInstance addKaleoInstance(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoInstance(
			kaleoDefinitionId, kaleoDefinitionVersionId, kaleoDefinitionName,
			kaleoDefinitionVersion, workflowContext, serviceContext);
	}

	public static KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			boolean waitForCompletion)
		throws PortalException {

		return getService().addKaleoInstance(
			kaleoDefinitionName, kaleoDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	public static KaleoInstance completeKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		return getService().completeKaleoInstance(kaleoInstanceId);
	}

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	public static KaleoInstance createKaleoInstance(long kaleoInstanceId) {
		return getService().createKaleoInstance(kaleoInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoInstances(long companyId) {
		getService().deleteCompanyKaleoInstances(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoInstances(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoInstances(
			kaleoDefinitionVersionId);
	}

	/**
	 * Deletes the kaleo instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was removed
	 */
	public static KaleoInstance deleteKaleoInstance(
		KaleoInstance kaleoInstance) {

		return getService().deleteKaleoInstance(kaleoInstance);
	}

	/**
	 * Deletes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	public static KaleoInstance deleteKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		return getService().deleteKaleoInstance(kaleoInstanceId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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

	public static KaleoInstance fetchKaleoInstance(long kaleoInstanceId) {
		return getService().fetchKaleoInstance(kaleoInstanceId);
	}

	public static KaleoInstance fetchKaleoInstance(
		long kaleoInstanceId, long companyId, long userId) {

		return getService().fetchKaleoInstance(
			kaleoInstanceId, companyId, userId);
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

	public static int getKaleoDefinitionKaleoInstancesCount(
		long kaleoDefinitionId, boolean completed) {

		return getService().getKaleoDefinitionKaleoInstancesCount(
			kaleoDefinitionId, completed);
	}

	/**
	 * Returns the kaleo instance with the primary key.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	public static KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws PortalException {

		return getService().getKaleoInstance(kaleoInstanceId);
	}

	/**
	 * Returns a range of all the kaleo instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of kaleo instances
	 */
	public static List<KaleoInstance> getKaleoInstances(int start, int end) {
		return getService().getKaleoInstances(start, end);
	}

	public static List<KaleoInstance> getKaleoInstances(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, int start, int end,
			OrderByComparator<KaleoInstance> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getKaleoInstances(
			userId, assetClassName, assetClassPK, completed, start, end,
			orderByComparator, serviceContext);
	}

	public static List<KaleoInstance> getKaleoInstances(
			Long userId, String[] assetClassNames, Boolean completed, int start,
			int end, OrderByComparator<KaleoInstance> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getKaleoInstances(
			userId, assetClassNames, completed, start, end, orderByComparator,
			serviceContext);
	}

	public static List<KaleoInstance> getKaleoInstances(
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			boolean completed, int start, int end,
			OrderByComparator<KaleoInstance> orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getKaleoInstances(
			kaleoDefinitionName, kaleoDefinitionVersion, completed, start, end,
			orderByComparator, serviceContext);
	}

	/**
	 * Returns the number of kaleo instances.
	 *
	 * @return the number of kaleo instances
	 */
	public static int getKaleoInstancesCount() {
		return getService().getKaleoInstancesCount();
	}

	public static int getKaleoInstancesCount(
		long kaleoDefinitionVersionId, boolean completed) {

		return getService().getKaleoInstancesCount(
			kaleoDefinitionVersionId, completed);
	}

	public static int getKaleoInstancesCount(
		Long userId, String assetClassName, Long assetClassPK,
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstancesCount(
			userId, assetClassName, assetClassPK, completed, serviceContext);
	}

	public static int getKaleoInstancesCount(
		Long userId, String[] assetClassNames, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstancesCount(
			userId, assetClassNames, completed, serviceContext);
	}

	public static int getKaleoInstancesCount(
		String kaleoDefinitionName, int kaleoDefinitionVersion,
		boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().getKaleoInstancesCount(
			kaleoDefinitionName, kaleoDefinitionVersion, completed,
			serviceContext);
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

	public static List<KaleoInstance> search(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed, int start, int end,
		OrderByComparator<KaleoInstance> orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().search(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, start, end,
			orderByComparator, serviceContext);
	}

	public static int searchCount(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().searchCount(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, serviceContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<KaleoInstance> searchKaleoInstances(
				Long userId, Boolean active, String assetClassName,
				String assetTitle, String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				OrderByComparator<KaleoInstance> orderByComparator,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
			throws PortalException {

		return getService().searchKaleoInstances(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed,
			searchByActiveWorkflowHandlers, start, end, orderByComparator,
			serviceContext);
	}

	public static KaleoInstance updateActive(
			long userId, long kaleoInstanceId, boolean active)
		throws PortalException {

		return getService().updateActive(userId, kaleoInstanceId, active);
	}

	/**
	 * Updates the kaleo instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was updated
	 */
	public static KaleoInstance updateKaleoInstance(
		KaleoInstance kaleoInstance) {

		return getService().updateKaleoInstance(kaleoInstance);
	}

	public static KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, long rootKaleoInstanceTokenId)
		throws PortalException {

		return getService().updateKaleoInstance(
			kaleoInstanceId, rootKaleoInstanceTokenId);
	}

	public static KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateKaleoInstance(
			kaleoInstanceId, workflowContext);
	}

	public static KaleoInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoInstanceLocalService> _serviceSnapshot =
		new Snapshot<>(
			KaleoInstanceLocalServiceUtil.class,
			KaleoInstanceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1636303169