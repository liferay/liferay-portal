/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePaymentEntryAudit. This utility wraps
 * <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryAuditLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryAuditLocalService
 * @generated
 */
public class CommercePaymentEntryAuditLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryAuditLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce payment entry audit to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was added
	 */
	public static CommercePaymentEntryAudit addCommercePaymentEntryAudit(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		return getService().addCommercePaymentEntryAudit(
			commercePaymentEntryAudit);
	}

	public static CommercePaymentEntryAudit addCommercePaymentEntryAudit(
			long userId, long commercePaymentEntryId,
			java.math.BigDecimal amount, String currencyCode, String logType,
			String logTypeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePaymentEntryAudit(
			userId, commercePaymentEntryId, amount, currencyCode, logType,
			logTypeSettings, serviceContext);
	}

	/**
	 * Creates a new commerce payment entry audit with the primary key. Does not add the commerce payment entry audit to the database.
	 *
	 * @param commercePaymentEntryAuditId the primary key for the new commerce payment entry audit
	 * @return the new commerce payment entry audit
	 */
	public static CommercePaymentEntryAudit createCommercePaymentEntryAudit(
		long commercePaymentEntryAuditId) {

		return getService().createCommercePaymentEntryAudit(
			commercePaymentEntryAuditId);
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
	 * Deletes the commerce payment entry audit from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 */
	public static CommercePaymentEntryAudit deleteCommercePaymentEntryAudit(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		return getService().deleteCommercePaymentEntryAudit(
			commercePaymentEntryAudit);
	}

	/**
	 * Deletes the commerce payment entry audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 * @throws PortalException if a commerce payment entry audit with the primary key could not be found
	 */
	public static CommercePaymentEntryAudit deleteCommercePaymentEntryAudit(
			long commercePaymentEntryAuditId)
		throws PortalException {

		return getService().deleteCommercePaymentEntryAudit(
			commercePaymentEntryAuditId);
	}

	public static void deleteCommercePaymentEntryAudits(
			long commercePaymentEntryId)
		throws PortalException {

		getService().deleteCommercePaymentEntryAudits(commercePaymentEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
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

	public static CommercePaymentEntryAudit fetchCommercePaymentEntryAudit(
		long commercePaymentEntryAuditId) {

		return getService().fetchCommercePaymentEntryAudit(
			commercePaymentEntryAuditId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce payment entry audit with the primary key.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit
	 * @throws PortalException if a commerce payment entry audit with the primary key could not be found
	 */
	public static CommercePaymentEntryAudit getCommercePaymentEntryAudit(
			long commercePaymentEntryAuditId)
		throws PortalException {

		return getService().getCommercePaymentEntryAudit(
			commercePaymentEntryAuditId);
	}

	/**
	 * Returns a range of all the commerce payment entry audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @return the range of commerce payment entry audits
	 */
	public static List<CommercePaymentEntryAudit> getCommercePaymentEntryAudits(
		int start, int end) {

		return getService().getCommercePaymentEntryAudits(start, end);
	}

	/**
	 * Returns the number of commerce payment entry audits.
	 *
	 * @return the number of commerce payment entry audits
	 */
	public static int getCommercePaymentEntryAuditsCount() {
		return getService().getCommercePaymentEntryAuditsCount();
	}

	public static int getCommercePaymentEntryAuditsCount(
		long commercePaymentEntryId) {

		return getService().getCommercePaymentEntryAuditsCount(
			commercePaymentEntryId);
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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePaymentEntryAudit> searchCommercePaymentEntryAudits(
			long companyId, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			String orderByField, boolean reverse) {

		return getService().searchCommercePaymentEntryAudits(
			companyId, keywords, params, start, end, orderByField, reverse);
	}

	/**
	 * Updates the commerce payment entry audit in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was updated
	 */
	public static CommercePaymentEntryAudit updateCommercePaymentEntryAudit(
		CommercePaymentEntryAudit commercePaymentEntryAudit) {

		return getService().updateCommercePaymentEntryAudit(
			commercePaymentEntryAudit);
	}

	public static CommercePaymentEntryAuditLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePaymentEntryAuditLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePaymentEntryAuditLocalServiceUtil.class,
			CommercePaymentEntryAuditLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-192407678