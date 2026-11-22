/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsEntryRole;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SegmentsEntryRole. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsEntryRoleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryRoleLocalService
 * @generated
 */
public class SegmentsEntryRoleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryRoleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SegmentsEntryRole addSegmentsEntryRole(
			long segmentsEntryId, long roleId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntryRole(
			segmentsEntryId, roleId, serviceContext);
	}

	/**
	 * Adds the segments entry role to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRole the segments entry role
	 * @return the segments entry role that was added
	 */
	public static SegmentsEntryRole addSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return getService().addSegmentsEntryRole(segmentsEntryRole);
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
	 * Creates a new segments entry role with the primary key. Does not add the segments entry role to the database.
	 *
	 * @param segmentsEntryRoleId the primary key for the new segments entry role
	 * @return the new segments entry role
	 */
	public static SegmentsEntryRole createSegmentsEntryRole(
		long segmentsEntryRoleId) {

		return getService().createSegmentsEntryRole(segmentsEntryRoleId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the segments entry role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role that was removed
	 * @throws PortalException if a segments entry role with the primary key could not be found
	 */
	public static SegmentsEntryRole deleteSegmentsEntryRole(
			long segmentsEntryRoleId)
		throws PortalException {

		return getService().deleteSegmentsEntryRole(segmentsEntryRoleId);
	}

	public static SegmentsEntryRole deleteSegmentsEntryRole(
			long segmentsEntryId, long roleId)
		throws PortalException {

		return getService().deleteSegmentsEntryRole(segmentsEntryId, roleId);
	}

	/**
	 * Deletes the segments entry role from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRole the segments entry role
	 * @return the segments entry role that was removed
	 */
	public static SegmentsEntryRole deleteSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return getService().deleteSegmentsEntryRole(segmentsEntryRole);
	}

	public static void deleteSegmentsEntryRoles(long segmentsEntryId)
		throws PortalException {

		getService().deleteSegmentsEntryRoles(segmentsEntryId);
	}

	public static void deleteSegmentsEntryRolesByRoleId(long roleId)
		throws PortalException {

		getService().deleteSegmentsEntryRolesByRoleId(roleId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRoleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRoleModelImpl</code>.
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

	public static SegmentsEntryRole fetchSegmentsEntryRole(
		long segmentsEntryRoleId) {

		return getService().fetchSegmentsEntryRole(segmentsEntryRoleId);
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
	 * Returns the segments entry role with the primary key.
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role
	 * @throws PortalException if a segments entry role with the primary key could not be found
	 */
	public static SegmentsEntryRole getSegmentsEntryRole(
			long segmentsEntryRoleId)
		throws PortalException {

		return getService().getSegmentsEntryRole(segmentsEntryRoleId);
	}

	/**
	 * Returns a range of all the segments entry roles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRoleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments entry roles
	 * @param end the upper bound of the range of segments entry roles (not inclusive)
	 * @return the range of segments entry roles
	 */
	public static List<SegmentsEntryRole> getSegmentsEntryRoles(
		int start, int end) {

		return getService().getSegmentsEntryRoles(start, end);
	}

	public static List<SegmentsEntryRole> getSegmentsEntryRoles(
		long segmentsEntryId) {

		return getService().getSegmentsEntryRoles(segmentsEntryId);
	}

	/**
	 * Returns the number of segments entry roles.
	 *
	 * @return the number of segments entry roles
	 */
	public static int getSegmentsEntryRolesCount() {
		return getService().getSegmentsEntryRolesCount();
	}

	public static int getSegmentsEntryRolesCountByRoleId(long roleId) {
		return getService().getSegmentsEntryRolesCountByRoleId(roleId);
	}

	public static boolean hasSegmentEntryRole(
		long segmentsEntryId, long roleId) {

		return getService().hasSegmentEntryRole(segmentsEntryId, roleId);
	}

	public static void setSegmentsEntrySiteRoles(
			long segmentsEntryId, long[] siteRoleIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().setSegmentsEntrySiteRoles(
			segmentsEntryId, siteRoleIds, serviceContext);
	}

	/**
	 * Updates the segments entry role in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRoleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRole the segments entry role
	 * @return the segments entry role that was updated
	 */
	public static SegmentsEntryRole updateSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return getService().updateSegmentsEntryRole(segmentsEntryRole);
	}

	public static SegmentsEntryRoleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SegmentsEntryRoleLocalService>
		_serviceSnapshot = new Snapshot<>(
			SegmentsEntryRoleLocalServiceUtil.class,
			SegmentsEntryRoleLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2056851552