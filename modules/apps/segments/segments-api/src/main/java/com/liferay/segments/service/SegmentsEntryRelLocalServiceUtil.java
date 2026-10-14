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
import com.liferay.segments.model.SegmentsEntryRel;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SegmentsEntryRel. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryRelLocalService
 * @generated
 */
public class SegmentsEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SegmentsEntryRel addSegmentsEntryRel(
			long segmentsEntryId, long classNameId, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK, serviceContext);
	}

	/**
	 * Adds the segments entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRel the segments entry rel
	 * @return the segments entry rel that was added
	 */
	public static SegmentsEntryRel addSegmentsEntryRel(
		SegmentsEntryRel segmentsEntryRel) {

		return getService().addSegmentsEntryRel(segmentsEntryRel);
	}

	public static void addSegmentsEntryRels(
			long segmentsEntryId, long classNameId, long[] classPKs,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addSegmentsEntryRels(
			segmentsEntryId, classNameId, classPKs, serviceContext);
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
	 * Creates a new segments entry rel with the primary key. Does not add the segments entry rel to the database.
	 *
	 * @param segmentsEntryRelId the primary key for the new segments entry rel
	 * @return the new segments entry rel
	 */
	public static SegmentsEntryRel createSegmentsEntryRel(
		long segmentsEntryRelId) {

		return getService().createSegmentsEntryRel(segmentsEntryRelId);
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
	 * Deletes the segments entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRelId the primary key of the segments entry rel
	 * @return the segments entry rel that was removed
	 * @throws PortalException if a segments entry rel with the primary key could not be found
	 */
	public static SegmentsEntryRel deleteSegmentsEntryRel(
			long segmentsEntryRelId)
		throws PortalException {

		return getService().deleteSegmentsEntryRel(segmentsEntryRelId);
	}

	public static void deleteSegmentsEntryRel(
			long segmentsEntryId, long classNameId, long classPK)
		throws PortalException {

		getService().deleteSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK);
	}

	/**
	 * Deletes the segments entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRel the segments entry rel
	 * @return the segments entry rel that was removed
	 */
	public static SegmentsEntryRel deleteSegmentsEntryRel(
		SegmentsEntryRel segmentsEntryRel) {

		return getService().deleteSegmentsEntryRel(segmentsEntryRel);
	}

	public static void deleteSegmentsEntryRels(long segmentsEntryId) {
		getService().deleteSegmentsEntryRels(segmentsEntryId);
	}

	public static void deleteSegmentsEntryRels(long classNameId, long classPK) {
		getService().deleteSegmentsEntryRels(classNameId, classPK);
	}

	public static void deleteSegmentsEntryRels(
			long segmentsEntryId, long classNameId, long[] classPKs)
		throws PortalException {

		getService().deleteSegmentsEntryRels(
			segmentsEntryId, classNameId, classPKs);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRelModelImpl</code>.
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

	public static SegmentsEntryRel fetchSegmentsEntryRel(
		long segmentsEntryRelId) {

		return getService().fetchSegmentsEntryRel(segmentsEntryRelId);
	}

	public static SegmentsEntryRel fetchSegmentsEntryRel(
		long segmentsEntryId, long classNameId, long classPK) {

		return getService().fetchSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK);
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
	 * Returns the segments entry rel with the primary key.
	 *
	 * @param segmentsEntryRelId the primary key of the segments entry rel
	 * @return the segments entry rel
	 * @throws PortalException if a segments entry rel with the primary key could not be found
	 */
	public static SegmentsEntryRel getSegmentsEntryRel(long segmentsEntryRelId)
		throws PortalException {

		return getService().getSegmentsEntryRel(segmentsEntryRelId);
	}

	/**
	 * Returns a range of all the segments entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments entry rels
	 * @param end the upper bound of the range of segments entry rels (not inclusive)
	 * @return the range of segments entry rels
	 */
	public static List<SegmentsEntryRel> getSegmentsEntryRels(
		int start, int end) {

		return getService().getSegmentsEntryRels(start, end);
	}

	public static List<SegmentsEntryRel> getSegmentsEntryRels(
		long segmentsEntryId) {

		return getService().getSegmentsEntryRels(segmentsEntryId);
	}

	public static List<SegmentsEntryRel> getSegmentsEntryRels(
		long segmentsEntryId, int start, int end,
		OrderByComparator<SegmentsEntryRel> orderByComparator) {

		return getService().getSegmentsEntryRels(
			segmentsEntryId, start, end, orderByComparator);
	}

	public static List<SegmentsEntryRel> getSegmentsEntryRels(
		long classNameId, long classPK) {

		return getService().getSegmentsEntryRels(classNameId, classPK);
	}

	public static List<SegmentsEntryRel> getSegmentsEntryRels(
		long groupId, long classNameId, long classPK) {

		return getService().getSegmentsEntryRels(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of segments entry rels.
	 *
	 * @return the number of segments entry rels
	 */
	public static int getSegmentsEntryRelsCount() {
		return getService().getSegmentsEntryRelsCount();
	}

	public static int getSegmentsEntryRelsCount(long segmentsEntryId) {
		return getService().getSegmentsEntryRelsCount(segmentsEntryId);
	}

	public static int getSegmentsEntryRelsCount(
		long classNameId, long classPK) {

		return getService().getSegmentsEntryRelsCount(classNameId, classPK);
	}

	public static int getSegmentsEntryRelsCount(
		long groupId, long classNameId, long classPK) {

		return getService().getSegmentsEntryRelsCount(
			groupId, classNameId, classPK);
	}

	public static boolean hasSegmentsEntryRel(
		long segmentsEntryId, long classNameId, long classPK) {

		return getService().hasSegmentsEntryRel(
			segmentsEntryId, classNameId, classPK);
	}

	/**
	 * Updates the segments entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryRel the segments entry rel
	 * @return the segments entry rel that was updated
	 */
	public static SegmentsEntryRel updateSegmentsEntryRel(
		SegmentsEntryRel segmentsEntryRel) {

		return getService().updateSegmentsEntryRel(segmentsEntryRel);
	}

	public static SegmentsEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SegmentsEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			SegmentsEntryRelLocalServiceUtil.class,
			SegmentsEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:616206585