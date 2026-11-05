/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DDMFormInstanceRecordVersion. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordVersionLocalService
 * @generated
 */
public class DDMFormInstanceRecordVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm form instance record version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecordVersion the ddm form instance record version
	 * @return the ddm form instance record version that was added
	 */
	public static DDMFormInstanceRecordVersion addDDMFormInstanceRecordVersion(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		return getService().addDDMFormInstanceRecordVersion(
			ddmFormInstanceRecordVersion);
	}

	/**
	 * Creates a new ddm form instance record version with the primary key. Does not add the ddm form instance record version to the database.
	 *
	 * @param formInstanceRecordVersionId the primary key for the new ddm form instance record version
	 * @return the new ddm form instance record version
	 */
	public static DDMFormInstanceRecordVersion
		createDDMFormInstanceRecordVersion(long formInstanceRecordVersionId) {

		return getService().createDDMFormInstanceRecordVersion(
			formInstanceRecordVersionId);
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
	 * Deletes the ddm form instance record version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecordVersion the ddm form instance record version
	 * @return the ddm form instance record version that was removed
	 * @throws PortalException
	 */
	public static DDMFormInstanceRecordVersion
			deleteDDMFormInstanceRecordVersion(
				DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion)
		throws PortalException {

		return getService().deleteDDMFormInstanceRecordVersion(
			ddmFormInstanceRecordVersion);
	}

	/**
	 * Deletes the ddm form instance record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version that was removed
	 * @throws PortalException if a ddm form instance record version with the primary key could not be found
	 */
	public static DDMFormInstanceRecordVersion
			deleteDDMFormInstanceRecordVersion(long formInstanceRecordVersionId)
		throws PortalException {

		return getService().deleteDDMFormInstanceRecordVersion(
			formInstanceRecordVersionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
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

	public static DDMFormInstanceRecordVersion
		fetchDDMFormInstanceRecordVersion(long formInstanceRecordVersionId) {

		return getService().fetchDDMFormInstanceRecordVersion(
			formInstanceRecordVersionId);
	}

	public static DDMFormInstanceRecordVersion
		fetchLatestFormInstanceRecordVersion(
			long userId, long formInstanceId, String formInstanceVersion,
			int status) {

		return getService().fetchLatestFormInstanceRecordVersion(
			userId, formInstanceId, formInstanceVersion, status);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm form instance record version with the primary key.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version
	 * @throws PortalException if a ddm form instance record version with the primary key could not be found
	 */
	public static DDMFormInstanceRecordVersion getDDMFormInstanceRecordVersion(
			long formInstanceRecordVersionId)
		throws PortalException {

		return getService().getDDMFormInstanceRecordVersion(
			formInstanceRecordVersionId);
	}

	/**
	 * Returns a range of all the ddm form instance record versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @return the range of ddm form instance record versions
	 */
	public static List<DDMFormInstanceRecordVersion>
		getDDMFormInstanceRecordVersions(int start, int end) {

		return getService().getDDMFormInstanceRecordVersions(start, end);
	}

	/**
	 * Returns the number of ddm form instance record versions.
	 *
	 * @return the number of ddm form instance record versions
	 */
	public static int getDDMFormInstanceRecordVersionsCount() {
		return getService().getDDMFormInstanceRecordVersionsCount();
	}

	public static DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
			long ddmFormInstanceRecordVersionId)
		throws PortalException {

		return getService().getFormInstanceRecordVersion(
			ddmFormInstanceRecordVersionId);
	}

	public static DDMFormInstanceRecordVersion getFormInstanceRecordVersion(
			long ddmFormInstanceId, String version)
		throws PortalException {

		return getService().getFormInstanceRecordVersion(
			ddmFormInstanceId, version);
	}

	public static List<DDMFormInstanceRecordVersion>
		getFormInstanceRecordVersions(
			long ddmFormInstanceRecordId, int start, int end,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return getService().getFormInstanceRecordVersions(
			ddmFormInstanceRecordId, start, end, orderByComparator);
	}

	public static List<DDMFormInstanceRecordVersion>
		getFormInstanceRecordVersions(long userId, long formInstanceId) {

		return getService().getFormInstanceRecordVersions(
			userId, formInstanceId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static DDMFormInstanceRecordVersion
			getLatestFormInstanceRecordVersion(long ddmFormInstanceId)
		throws PortalException {

		return getService().getLatestFormInstanceRecordVersion(
			ddmFormInstanceId);
	}

	public static DDMFormInstanceRecordVersion
			getLatestFormInstanceRecordVersion(
				long ddmFormInstanceRecordId, int status)
		throws PortalException {

		return getService().getLatestFormInstanceRecordVersion(
			ddmFormInstanceRecordId, status);
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
	 * Updates the ddm form instance record version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecordVersion the ddm form instance record version
	 * @return the ddm form instance record version that was updated
	 */
	public static DDMFormInstanceRecordVersion
		updateDDMFormInstanceRecordVersion(
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		return getService().updateDDMFormInstanceRecordVersion(
			ddmFormInstanceRecordVersion);
	}

	public static DDMFormInstanceRecordVersionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMFormInstanceRecordVersionLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMFormInstanceRecordVersionLocalServiceUtil.class,
			DDMFormInstanceRecordVersionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1259766418