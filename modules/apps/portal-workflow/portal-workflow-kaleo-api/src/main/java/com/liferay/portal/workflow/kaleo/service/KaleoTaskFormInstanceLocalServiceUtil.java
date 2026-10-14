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
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KaleoTaskFormInstance. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskFormInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskFormInstanceLocalService
 * @generated
 */
public class KaleoTaskFormInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoTaskFormInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo task form instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was added
	 */
	public static KaleoTaskFormInstance addKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return getService().addKaleoTaskFormInstance(kaleoTaskFormInstance);
	}

	public static KaleoTaskFormInstance addKaleoTaskFormInstance(
			long groupId, long kaleoTaskFormId, String formValues,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoTaskFormInstance(
			groupId, kaleoTaskFormId, formValues, kaleoTaskInstanceToken,
			serviceContext);
	}

	/**
	 * Creates a new kaleo task form instance with the primary key. Does not add the kaleo task form instance to the database.
	 *
	 * @param kaleoTaskFormInstanceId the primary key for the new kaleo task form instance
	 * @return the new kaleo task form instance
	 */
	public static KaleoTaskFormInstance createKaleoTaskFormInstance(
		long kaleoTaskFormInstanceId) {

		return getService().createKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyKaleoTaskFormInstances(long companyId) {
		getService().deleteCompanyKaleoTaskFormInstances(companyId);
	}

	public static void deleteKaleoDefinitionVersionKaleoTaskFormInstances(
		long kaleoDefinitionVersionId) {

		getService().deleteKaleoDefinitionVersionKaleoTaskFormInstances(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoInstanceKaleoTaskFormInstances(
		long kaleoInstanceId) {

		getService().deleteKaleoInstanceKaleoTaskFormInstances(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo task form instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 */
	public static KaleoTaskFormInstance deleteKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return getService().deleteKaleoTaskFormInstance(kaleoTaskFormInstance);
	}

	/**
	 * Deletes the kaleo task form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 * @throws PortalException if a kaleo task form instance with the primary key could not be found
	 */
	public static KaleoTaskFormInstance deleteKaleoTaskFormInstance(
			long kaleoTaskFormInstanceId)
		throws PortalException {

		return getService().deleteKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	public static void deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
		long kaleoTaskInstanceTokenId) {

		getService().deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
			kaleoTaskInstanceTokenId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
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

	public static KaleoTaskFormInstance fetchKaleoTaskFormInstance(
		long kaleoTaskFormInstanceId) {

		return getService().fetchKaleoTaskFormInstance(kaleoTaskFormInstanceId);
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
	 * Returns the kaleo task form instance with the primary key.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance
	 * @throws PortalException if a kaleo task form instance with the primary key could not be found
	 */
	public static KaleoTaskFormInstance getKaleoTaskFormInstance(
			long kaleoTaskFormInstanceId)
		throws PortalException {

		return getService().getKaleoTaskFormInstance(kaleoTaskFormInstanceId);
	}

	/**
	 * Returns a range of all the kaleo task form instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of kaleo task form instances
	 */
	public static List<KaleoTaskFormInstance> getKaleoTaskFormInstances(
		int start, int end) {

		return getService().getKaleoTaskFormInstances(start, end);
	}

	/**
	 * Returns the number of kaleo task form instances.
	 *
	 * @return the number of kaleo task form instances
	 */
	public static int getKaleoTaskFormInstancesCount() {
		return getService().getKaleoTaskFormInstancesCount();
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
	 * Updates the kaleo task form instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was updated
	 */
	public static KaleoTaskFormInstance updateKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return getService().updateKaleoTaskFormInstance(kaleoTaskFormInstance);
	}

	public static KaleoTaskFormInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoTaskFormInstanceLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoTaskFormInstanceLocalServiceUtil.class,
			KaleoTaskFormInstanceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1395864923