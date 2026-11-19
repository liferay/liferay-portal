/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service;

import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceTaxFixedRate. This utility wraps
 * <code>com.liferay.commerce.tax.engine.fixed.service.impl.CommerceTaxFixedRateLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateLocalService
 * @generated
 */
public class CommerceTaxFixedRateLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.tax.engine.fixed.service.impl.CommerceTaxFixedRateLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce tax fixed rate to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was added
	 */
	public static CommerceTaxFixedRate addCommerceTaxFixedRate(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		return getService().addCommerceTaxFixedRate(commerceTaxFixedRate);
	}

	public static CommerceTaxFixedRate addCommerceTaxFixedRate(
			long userId, long groupId, long commerceTaxMethodId,
			long cpTaxCategoryId, double rate)
		throws PortalException {

		return getService().addCommerceTaxFixedRate(
			userId, groupId, commerceTaxMethodId, cpTaxCategoryId, rate);
	}

	/**
	 * Creates a new commerce tax fixed rate with the primary key. Does not add the commerce tax fixed rate to the database.
	 *
	 * @param commerceTaxFixedRateId the primary key for the new commerce tax fixed rate
	 * @return the new commerce tax fixed rate
	 */
	public static CommerceTaxFixedRate createCommerceTaxFixedRate(
		long commerceTaxFixedRateId) {

		return getService().createCommerceTaxFixedRate(commerceTaxFixedRateId);
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
	 * Deletes the commerce tax fixed rate from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was removed
	 */
	public static CommerceTaxFixedRate deleteCommerceTaxFixedRate(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		return getService().deleteCommerceTaxFixedRate(commerceTaxFixedRate);
	}

	/**
	 * Deletes the commerce tax fixed rate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was removed
	 * @throws PortalException if a commerce tax fixed rate with the primary key could not be found
	 */
	public static CommerceTaxFixedRate deleteCommerceTaxFixedRate(
			long commerceTaxFixedRateId)
		throws PortalException {

		return getService().deleteCommerceTaxFixedRate(commerceTaxFixedRateId);
	}

	public static void deleteCommerceTaxFixedRateByCommerceTaxMethodId(
		long commerceTaxMethodId) {

		getService().deleteCommerceTaxFixedRateByCommerceTaxMethodId(
			commerceTaxMethodId);
	}

	public static void deleteCommerceTaxFixedRateByCPTaxCategoryId(
		long cpTaxCategoryId) {

		getService().deleteCommerceTaxFixedRateByCPTaxCategoryId(
			cpTaxCategoryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
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

	public static CommerceTaxFixedRate fetchCommerceTaxFixedRate(
		long commerceTaxFixedRateId) {

		return getService().fetchCommerceTaxFixedRate(commerceTaxFixedRateId);
	}

	public static CommerceTaxFixedRate fetchCommerceTaxFixedRate(
			long cpTaxCategoryId, long commerceTaxMethodId)
		throws PortalException {

		return getService().fetchCommerceTaxFixedRate(
			cpTaxCategoryId, commerceTaxMethodId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax fixed rate with the primary key.
	 *
	 * @param commerceTaxFixedRateId the primary key of the commerce tax fixed rate
	 * @return the commerce tax fixed rate
	 * @throws PortalException if a commerce tax fixed rate with the primary key could not be found
	 */
	public static CommerceTaxFixedRate getCommerceTaxFixedRate(
			long commerceTaxFixedRateId)
		throws PortalException {

		return getService().getCommerceTaxFixedRate(commerceTaxFixedRateId);
	}

	/**
	 * Returns a range of all the commerce tax fixed rates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax fixed rates
	 * @param end the upper bound of the range of commerce tax fixed rates (not inclusive)
	 * @return the range of commerce tax fixed rates
	 */
	public static List<CommerceTaxFixedRate> getCommerceTaxFixedRates(
		int start, int end) {

		return getService().getCommerceTaxFixedRates(start, end);
	}

	/**
	 * Returns the number of commerce tax fixed rates.
	 *
	 * @return the number of commerce tax fixed rates
	 */
	public static int getCommerceTaxFixedRatesCount() {
		return getService().getCommerceTaxFixedRatesCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * Updates the commerce tax fixed rate in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRate the commerce tax fixed rate
	 * @return the commerce tax fixed rate that was updated
	 */
	public static CommerceTaxFixedRate updateCommerceTaxFixedRate(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		return getService().updateCommerceTaxFixedRate(commerceTaxFixedRate);
	}

	public static CommerceTaxFixedRate updateCommerceTaxFixedRate(
			long commerceTaxFixedRateId, double rate)
		throws PortalException {

		return getService().updateCommerceTaxFixedRate(
			commerceTaxFixedRateId, rate);
	}

	public static CommerceTaxFixedRateLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTaxFixedRateLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTaxFixedRateLocalServiceUtil.class,
			CommerceTaxFixedRateLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:670734662