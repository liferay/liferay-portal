/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service;

import com.liferay.commerce.inventory.model.CommerceInventoryAudit;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceInventoryAudit. This utility wraps
 * <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryAuditLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceInventoryAuditLocalService
 * @generated
 */
public class CommerceInventoryAuditLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.inventory.service.impl.CommerceInventoryAuditLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce inventory audit to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryAudit the commerce inventory audit
	 * @return the commerce inventory audit that was added
	 */
	public static CommerceInventoryAudit addCommerceInventoryAudit(
		CommerceInventoryAudit commerceInventoryAudit) {

		return getService().addCommerceInventoryAudit(commerceInventoryAudit);
	}

	public static CommerceInventoryAudit addCommerceInventoryAudit(
			long userId, String logType, String logTypeSettings,
			java.math.BigDecimal quantity, String sku, String unitOfMeasureKey)
		throws PortalException {

		return getService().addCommerceInventoryAudit(
			userId, logType, logTypeSettings, quantity, sku, unitOfMeasureKey);
	}

	public static void checkCommerceInventoryAudit(java.util.Date date) {
		getService().checkCommerceInventoryAudit(date);
	}

	/**
	 * Creates a new commerce inventory audit with the primary key. Does not add the commerce inventory audit to the database.
	 *
	 * @param commerceInventoryAuditId the primary key for the new commerce inventory audit
	 * @return the new commerce inventory audit
	 */
	public static CommerceInventoryAudit createCommerceInventoryAudit(
		long commerceInventoryAuditId) {

		return getService().createCommerceInventoryAudit(
			commerceInventoryAuditId);
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
	 * Deletes the commerce inventory audit from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryAudit the commerce inventory audit
	 * @return the commerce inventory audit that was removed
	 */
	public static CommerceInventoryAudit deleteCommerceInventoryAudit(
		CommerceInventoryAudit commerceInventoryAudit) {

		return getService().deleteCommerceInventoryAudit(
			commerceInventoryAudit);
	}

	/**
	 * Deletes the commerce inventory audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryAuditId the primary key of the commerce inventory audit
	 * @return the commerce inventory audit that was removed
	 * @throws PortalException if a commerce inventory audit with the primary key could not be found
	 */
	public static CommerceInventoryAudit deleteCommerceInventoryAudit(
			long commerceInventoryAuditId)
		throws PortalException {

		return getService().deleteCommerceInventoryAudit(
			commerceInventoryAuditId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditModelImpl</code>.
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

	public static CommerceInventoryAudit fetchCommerceInventoryAudit(
		long commerceInventoryAuditId) {

		return getService().fetchCommerceInventoryAudit(
			commerceInventoryAuditId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce inventory audit with the primary key.
	 *
	 * @param commerceInventoryAuditId the primary key of the commerce inventory audit
	 * @return the commerce inventory audit
	 * @throws PortalException if a commerce inventory audit with the primary key could not be found
	 */
	public static CommerceInventoryAudit getCommerceInventoryAudit(
			long commerceInventoryAuditId)
		throws PortalException {

		return getService().getCommerceInventoryAudit(commerceInventoryAuditId);
	}

	/**
	 * Returns a range of all the commerce inventory audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.inventory.model.impl.CommerceInventoryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce inventory audits
	 * @param end the upper bound of the range of commerce inventory audits (not inclusive)
	 * @return the range of commerce inventory audits
	 */
	public static List<CommerceInventoryAudit> getCommerceInventoryAudits(
		int start, int end) {

		return getService().getCommerceInventoryAudits(start, end);
	}

	/**
	 * Returns the number of commerce inventory audits.
	 *
	 * @return the number of commerce inventory audits
	 */
	public static int getCommerceInventoryAuditsCount() {
		return getService().getCommerceInventoryAuditsCount();
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
	 * Updates the commerce inventory audit in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceInventoryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceInventoryAudit the commerce inventory audit
	 * @return the commerce inventory audit that was updated
	 */
	public static CommerceInventoryAudit updateCommerceInventoryAudit(
		CommerceInventoryAudit commerceInventoryAudit) {

		return getService().updateCommerceInventoryAudit(
			commerceInventoryAudit);
	}

	public static CommerceInventoryAuditLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceInventoryAuditLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceInventoryAuditLocalServiceUtil.class,
			CommerceInventoryAuditLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1126155668