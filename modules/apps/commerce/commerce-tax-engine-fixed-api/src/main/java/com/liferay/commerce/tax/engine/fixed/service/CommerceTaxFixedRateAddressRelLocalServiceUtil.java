/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service;

import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceTaxFixedRateAddressRel. This utility wraps
 * <code>com.liferay.commerce.tax.engine.fixed.service.impl.CommerceTaxFixedRateAddressRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateAddressRelLocalService
 * @generated
 */
public class CommerceTaxFixedRateAddressRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.tax.engine.fixed.service.impl.CommerceTaxFixedRateAddressRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce tax fixed rate address rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was added
	 */
	public static CommerceTaxFixedRateAddressRel
		addCommerceTaxFixedRateAddressRel(
			CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel) {

		return getService().addCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel);
	}

	public static CommerceTaxFixedRateAddressRel
			addCommerceTaxFixedRateAddressRel(
				long userId, long groupId, long commerceTaxMethodId,
				long cpTaxCategoryId, long countryId, long regionId, String zip,
				double rate)
		throws PortalException {

		return getService().addCommerceTaxFixedRateAddressRel(
			userId, groupId, commerceTaxMethodId, cpTaxCategoryId, countryId,
			regionId, zip, rate);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceTaxFixedRateAddressRel
			addCommerceTaxFixedRateAddressRel(
				long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
				long regionId, String zip, double rate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, cpTaxCategoryId, countryId, regionId, zip,
			rate, serviceContext);
	}

	/**
	 * Creates a new commerce tax fixed rate address rel with the primary key. Does not add the commerce tax fixed rate address rel to the database.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key for the new commerce tax fixed rate address rel
	 * @return the new commerce tax fixed rate address rel
	 */
	public static CommerceTaxFixedRateAddressRel
		createCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId) {

		return getService().createCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRelId);
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
	 * Deletes the commerce tax fixed rate address rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 */
	public static CommerceTaxFixedRateAddressRel
		deleteCommerceTaxFixedRateAddressRel(
			CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel) {

		return getService().deleteCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel);
	}

	/**
	 * Deletes the commerce tax fixed rate address rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 * @throws PortalException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	public static CommerceTaxFixedRateAddressRel
			deleteCommerceTaxFixedRateAddressRel(
				long commerceTaxFixedRateAddressRelId)
		throws PortalException {

		return getService().deleteCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRelId);
	}

	public static void
		deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
			long commerceTaxMethodId) {

		getService().deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
			commerceTaxMethodId);
	}

	public static void deleteCommerceTaxFixedRateAddressRelsByCPTaxCategoryId(
		long cpTaxCategoryId) {

		getService().deleteCommerceTaxFixedRateAddressRelsByCPTaxCategoryId(
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
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

	public static CommerceTaxFixedRateAddressRel
		fetchCommerceTaxFixedRateAddressRel(
			long commerceTaxFixedRateAddressRelId) {

		return getService().fetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRelId);
	}

	public static CommerceTaxFixedRateAddressRel
		fetchCommerceTaxFixedRateAddressRel(
			long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
			long regionId, String zip) {

		return getService().fetchCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, cpTaxCategoryId, countryId, regionId, zip);
	}

	public static CommerceTaxFixedRateAddressRel
		fetchCommerceTaxFixedRateAddressRel(
			long commerceTaxMethodId, long countryId, long regionId,
			String zip) {

		return getService().fetchCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, regionId, zip);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel
	 * @throws PortalException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	public static CommerceTaxFixedRateAddressRel
			getCommerceTaxFixedRateAddressRel(
				long commerceTaxFixedRateAddressRelId)
		throws PortalException {

		return getService().getCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRelId);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of commerce tax fixed rate address rels
	 */
	public static List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRels(int start, int end) {

		return getService().getCommerceTaxFixedRateAddressRels(start, end);
	}

	public static List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRels(
			long cpTaxCategoryId, int start, int end) {

		return getService().getCommerceTaxFixedRateAddressRels(
			cpTaxCategoryId, start, end);
	}

	public static List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxFixedRateAddressRels(
			long cpTaxCategoryId, int start, int end,
			OrderByComparator<CommerceTaxFixedRateAddressRel>
				orderByComparator) {

		return getService().getCommerceTaxFixedRateAddressRels(
			cpTaxCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tax fixed rate address rels.
	 *
	 * @return the number of commerce tax fixed rate address rels
	 */
	public static int getCommerceTaxFixedRateAddressRelsCount() {
		return getService().getCommerceTaxFixedRateAddressRelsCount();
	}

	public static int getCommerceTaxFixedRateAddressRelsCount(
		long cpTaxCategoryId) {

		return getService().getCommerceTaxFixedRateAddressRelsCount(
			cpTaxCategoryId);
	}

	public static List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxMethodFixedRateAddressRels(
			long commerceTaxMethodId, int start, int end) {

		return getService().getCommerceTaxMethodFixedRateAddressRels(
			commerceTaxMethodId, start, end);
	}

	public static List<CommerceTaxFixedRateAddressRel>
		getCommerceTaxMethodFixedRateAddressRels(
			long commerceTaxMethodId, int start, int end,
			OrderByComparator<CommerceTaxFixedRateAddressRel>
				orderByComparator) {

		return getService().getCommerceTaxMethodFixedRateAddressRels(
			commerceTaxMethodId, start, end, orderByComparator);
	}

	public static int getCommerceTaxMethodFixedRateAddressRelsCount(
		long commerceTaxMethodId) {

		return getService().getCommerceTaxMethodFixedRateAddressRelsCount(
			commerceTaxMethodId);
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
	 * Updates the commerce tax fixed rate address rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was updated
	 */
	public static CommerceTaxFixedRateAddressRel
		updateCommerceTaxFixedRateAddressRel(
			CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel) {

		return getService().updateCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel);
	}

	public static CommerceTaxFixedRateAddressRel
			updateCommerceTaxFixedRateAddressRel(
				long commerceTaxFixedRateAddressRelId, long countryId,
				long regionId, String zip, double rate)
		throws PortalException {

		return getService().updateCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRelId, countryId, regionId, zip, rate);
	}

	public static CommerceTaxFixedRateAddressRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTaxFixedRateAddressRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTaxFixedRateAddressRelLocalServiceUtil.class,
			CommerceTaxFixedRateAddressRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:392590069