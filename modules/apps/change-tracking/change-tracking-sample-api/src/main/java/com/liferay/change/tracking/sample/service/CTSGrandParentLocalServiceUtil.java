/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service;

import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CTSGrandParent. This utility wraps
 * <code>com.liferay.change.tracking.sample.service.impl.CTSGrandParentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParentLocalService
 * @generated
 */
public class CTSGrandParentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.sample.service.impl.CTSGrandParentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cts grand parent to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was added
	 */
	public static CTSGrandParent addCTSGrandParent(
		CTSGrandParent ctsGrandParent) {

		return getService().addCTSGrandParent(ctsGrandParent);
	}

	public static CTSGrandParent addCTSGrandParent(long companyId) {
		return getService().addCTSGrandParent(companyId);
	}

	public static CTSGrandParent addCTSGrandParent(
		long companyId, long parentCTSGrandParentId) {

		return getService().addCTSGrandParent(
			companyId, parentCTSGrandParentId);
	}

	/**
	 * Creates a new cts grand parent with the primary key. Does not add the cts grand parent to the database.
	 *
	 * @param ctsGrandParentId the primary key for the new cts grand parent
	 * @return the new cts grand parent
	 */
	public static CTSGrandParent createCTSGrandParent(long ctsGrandParentId) {
		return getService().createCTSGrandParent(ctsGrandParentId);
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
	 * Deletes the cts grand parent from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was removed
	 */
	public static CTSGrandParent deleteCTSGrandParent(
		CTSGrandParent ctsGrandParent) {

		return getService().deleteCTSGrandParent(ctsGrandParent);
	}

	/**
	 * Deletes the cts grand parent with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent that was removed
	 * @throws PortalException if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent deleteCTSGrandParent(long ctsGrandParentId)
		throws PortalException {

		return getService().deleteCTSGrandParent(ctsGrandParentId);
	}

	public static void deleteCTSGrandParents(long companyId) {
		getService().deleteCTSGrandParents(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
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

	public static CTSGrandParent fetchCTSGrandParent(long ctsGrandParentId) {
		return getService().fetchCTSGrandParent(ctsGrandParentId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cts grand parent with the primary key.
	 *
	 * @param ctsGrandParentId the primary key of the cts grand parent
	 * @return the cts grand parent
	 * @throws PortalException if a cts grand parent with the primary key could not be found
	 */
	public static CTSGrandParent getCTSGrandParent(long ctsGrandParentId)
		throws PortalException {

		return getService().getCTSGrandParent(ctsGrandParentId);
	}

	/**
	 * Returns a range of all the cts grand parents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSGrandParentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts grand parents
	 * @param end the upper bound of the range of cts grand parents (not inclusive)
	 * @return the range of cts grand parents
	 */
	public static List<CTSGrandParent> getCTSGrandParents(int start, int end) {
		return getService().getCTSGrandParents(start, end);
	}

	public static List<CTSGrandParent> getCTSGrandParents(long companyId) {
		return getService().getCTSGrandParents(companyId);
	}

	/**
	 * Returns the number of cts grand parents.
	 *
	 * @return the number of cts grand parents
	 */
	public static int getCTSGrandParentsCount() {
		return getService().getCTSGrandParentsCount();
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
	 * Updates the cts grand parent in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSGrandParentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsGrandParent the cts grand parent
	 * @return the cts grand parent that was updated
	 */
	public static CTSGrandParent updateCTSGrandParent(
		CTSGrandParent ctsGrandParent) {

		return getService().updateCTSGrandParent(ctsGrandParent);
	}

	public static CTSGrandParent updateCTSGrandParent(long ctsCTSGrandParentId)
		throws PortalException {

		return getService().updateCTSGrandParent(ctsCTSGrandParentId);
	}

	public static CTSGrandParentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTSGrandParentLocalService> _serviceSnapshot =
		new Snapshot<>(
			CTSGrandParentLocalServiceUtil.class,
			CTSGrandParentLocalService.class);

}