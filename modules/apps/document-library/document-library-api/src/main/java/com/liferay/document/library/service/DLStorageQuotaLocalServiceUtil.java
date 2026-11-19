/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service;

import com.liferay.document.library.model.DLStorageQuota;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLStorageQuota. This utility wraps
 * <code>com.liferay.document.library.service.impl.DLStorageQuotaLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLStorageQuotaLocalService
 * @generated
 */
public class DLStorageQuotaLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.document.library.service.impl.DLStorageQuotaLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the dl storage quota to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was added
	 */
	public static DLStorageQuota addDLStorageQuota(
		DLStorageQuota dlStorageQuota) {

		return getService().addDLStorageQuota(dlStorageQuota);
	}

	/**
	 * Creates a new dl storage quota with the primary key. Does not add the dl storage quota to the database.
	 *
	 * @param dlStorageQuotaId the primary key for the new dl storage quota
	 * @return the new dl storage quota
	 */
	public static DLStorageQuota createDLStorageQuota(long dlStorageQuotaId) {
		return getService().createDLStorageQuota(dlStorageQuotaId);
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
	 * Deletes the dl storage quota from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was removed
	 */
	public static DLStorageQuota deleteDLStorageQuota(
		DLStorageQuota dlStorageQuota) {

		return getService().deleteDLStorageQuota(dlStorageQuota);
	}

	/**
	 * Deletes the dl storage quota with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota that was removed
	 * @throws PortalException if a dl storage quota with the primary key could not be found
	 */
	public static DLStorageQuota deleteDLStorageQuota(long dlStorageQuotaId)
		throws PortalException {

		return getService().deleteDLStorageQuota(dlStorageQuotaId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
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

	public static DLStorageQuota fetchDLStorageQuota(long dlStorageQuotaId) {
		return getService().fetchDLStorageQuota(dlStorageQuotaId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the dl storage quota with the primary key.
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota
	 * @throws PortalException if a dl storage quota with the primary key could not be found
	 */
	public static DLStorageQuota getDLStorageQuota(long dlStorageQuotaId)
		throws PortalException {

		return getService().getDLStorageQuota(dlStorageQuotaId);
	}

	/**
	 * Returns a range of all the dl storage quotas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dl storage quotas
	 * @param end the upper bound of the range of dl storage quotas (not inclusive)
	 * @return the range of dl storage quotas
	 */
	public static List<DLStorageQuota> getDLStorageQuotas(int start, int end) {
		return getService().getDLStorageQuotas(start, end);
	}

	/**
	 * Returns the number of dl storage quotas.
	 *
	 * @return the number of dl storage quotas
	 */
	public static int getDLStorageQuotasCount() {
		return getService().getDLStorageQuotasCount();
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

	public static void incrementStorageSize(long companyId, long increment) {
		getService().incrementStorageSize(companyId, increment);
	}

	/**
	 * Updates the dl storage quota in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was updated
	 */
	public static DLStorageQuota updateDLStorageQuota(
		DLStorageQuota dlStorageQuota) {

		return getService().updateDLStorageQuota(dlStorageQuota);
	}

	public static void updateStorageSize(long companyId) {
		getService().updateStorageSize(companyId);
	}

	public static void validateStorageQuota(long companyId, long increment)
		throws PortalException {

		getService().validateStorageQuota(companyId, increment);
	}

	public static DLStorageQuotaLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DLStorageQuotaLocalService> _serviceSnapshot =
		new Snapshot<>(
			DLStorageQuotaLocalServiceUtil.class,
			DLStorageQuotaLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1427792208