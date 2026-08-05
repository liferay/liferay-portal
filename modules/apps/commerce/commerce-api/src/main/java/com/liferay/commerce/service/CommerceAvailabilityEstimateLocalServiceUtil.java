/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceAvailabilityEstimate. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceAvailabilityEstimateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateLocalService
 * @generated
 */
public class CommerceAvailabilityEstimateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceAvailabilityEstimateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce availability estimate to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was added
	 */
	public static CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		return getService().addCommerceAvailabilityEstimate(
			commerceAvailabilityEstimate);
	}

	public static CommerceAvailabilityEstimate addCommerceAvailabilityEstimate(
			String externalReferenceCode,
			Map<java.util.Locale, String> titleMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceAvailabilityEstimate(
			externalReferenceCode, titleMap, priority, serviceContext);
	}

	/**
	 * Creates a new commerce availability estimate with the primary key. Does not add the commerce availability estimate to the database.
	 *
	 * @param commerceAvailabilityEstimateId the primary key for the new commerce availability estimate
	 * @return the new commerce availability estimate
	 */
	public static CommerceAvailabilityEstimate
		createCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId) {

		return getService().createCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
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
	 * Deletes the commerce availability estimate from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws PortalException
	 */
	public static CommerceAvailabilityEstimate
			deleteCommerceAvailabilityEstimate(
				CommerceAvailabilityEstimate commerceAvailabilityEstimate)
		throws PortalException {

		return getService().deleteCommerceAvailabilityEstimate(
			commerceAvailabilityEstimate);
	}

	/**
	 * Deletes the commerce availability estimate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws PortalException if a commerce availability estimate with the primary key could not be found
	 */
	public static CommerceAvailabilityEstimate
			deleteCommerceAvailabilityEstimate(
				long commerceAvailabilityEstimateId)
		throws PortalException {

		return getService().deleteCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	public static void deleteCommerceAvailabilityEstimates(long companyId)
		throws PortalException {

		getService().deleteCommerceAvailabilityEstimates(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
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

	public static CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimate(long commerceAvailabilityEstimateId) {

		return getService().fetchCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	public static CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimateByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce availability estimate with the matching UUID and company.
	 *
	 * @param uuid the commerce availability estimate's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimateByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceAvailabilityEstimateByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce availability estimate with the primary key.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate
	 * @throws PortalException if a commerce availability estimate with the primary key could not be found
	 */
	public static CommerceAvailabilityEstimate getCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId)
		throws PortalException {

		return getService().getCommerceAvailabilityEstimate(
			commerceAvailabilityEstimateId);
	}

	public static CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce availability estimate with the matching UUID and company.
	 *
	 * @param uuid the commerce availability estimate's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce availability estimate
	 * @throws PortalException if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimateByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceAvailabilityEstimateByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate>
		getCommerceAvailabilityEstimates(int start, int end) {

		return getService().getCommerceAvailabilityEstimates(start, end);
	}

	public static List<CommerceAvailabilityEstimate>
		getCommerceAvailabilityEstimates(
			long companyId, int start, int end,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getService().getCommerceAvailabilityEstimates(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce availability estimates.
	 *
	 * @return the number of commerce availability estimates
	 */
	public static int getCommerceAvailabilityEstimatesCount() {
		return getService().getCommerceAvailabilityEstimatesCount();
	}

	public static int getCommerceAvailabilityEstimatesCount(long companyId) {
		return getService().getCommerceAvailabilityEstimatesCount(companyId);
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

	public static CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommerceAvailabilityEstimate(
			externalReferenceCode, companyId, userId);
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
	 * Updates the commerce availability estimate in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was updated
	 */
	public static CommerceAvailabilityEstimate
		updateCommerceAvailabilityEstimate(
			CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		return getService().updateCommerceAvailabilityEstimate(
			commerceAvailabilityEstimate);
	}

	public static CommerceAvailabilityEstimate
			updateCommerceAvailabilityEstimate(
				String externalReferenceCode,
				long commerceAvailabilityEstimateId,
				Map<java.util.Locale, String> titleMap, double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceAvailabilityEstimate(
			externalReferenceCode, commerceAvailabilityEstimateId, titleMap,
			priority, serviceContext);
	}

	public static CommerceAvailabilityEstimateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceAvailabilityEstimateLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceAvailabilityEstimateLocalServiceUtil.class,
			CommerceAvailabilityEstimateLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1594943584