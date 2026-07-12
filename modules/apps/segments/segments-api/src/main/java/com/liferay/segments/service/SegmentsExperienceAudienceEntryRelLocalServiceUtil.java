/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SegmentsExperienceAudienceEntryRel. This utility wraps
 * <code>com.liferay.segments.service.impl.SegmentsExperienceAudienceEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelLocalService
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsExperienceAudienceEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the segments experience audience entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was added
	 */
	public static SegmentsExperienceAudienceEntryRel
		addSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return getService().addSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRel);
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
	 * Creates a new segments experience audience entry rel with the primary key. Does not add the segments experience audience entry rel to the database.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key for the new segments experience audience entry rel
	 * @return the new segments experience audience entry rel
	 */
	public static SegmentsExperienceAudienceEntryRel
		createSegmentsExperienceAudienceEntryRel(
			long segmentsExperienceAudienceEntryRelId) {

		return getService().createSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRelId);
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
	 * Deletes the segments experience audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 * @throws PortalException if a segments experience audience entry rel with the primary key could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel
			deleteSegmentsExperienceAudienceEntryRel(
				long segmentsExperienceAudienceEntryRelId)
		throws PortalException {

		return getService().deleteSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Deletes the segments experience audience entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 */
	public static SegmentsExperienceAudienceEntryRel
		deleteSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return getService().deleteSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRel);
	}

	public static void
		deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
			long companyId, String audienceEntryERC) {

		getService().
			deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
				companyId, audienceEntryERC);
	}

	public static void
		deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
			long groupId, String segmentsExperienceERC) {

		getService().
			deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
				groupId, segmentsExperienceERC);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
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

	public static SegmentsExperienceAudienceEntryRel
		fetchSegmentsExperienceAudienceEntryRel(
			long segmentsExperienceAudienceEntryRelId) {

		return getService().fetchSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel matching the UUID and group.
	 *
	 * @param uuid the segments experience audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel
		fetchSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the segments experience audience entry rel with the primary key.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel
	 * @throws PortalException if a segments experience audience entry rel with the primary key could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel
			getSegmentsExperienceAudienceEntryRel(
				long segmentsExperienceAudienceEntryRelId)
		throws PortalException {

		return getService().getSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel matching the UUID and group.
	 *
	 * @param uuid the segments experience audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience audience entry rel
	 * @throws PortalException if a matching segments experience audience entry rel could not be found
	 */
	public static SegmentsExperienceAudienceEntryRel
			getSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of segments experience audience entry rels
	 */
	public static List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRels(int start, int end) {

		return getService().getSegmentsExperienceAudienceEntryRels(start, end);
	}

	public static List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRels(
			long groupId, String segmentsExperienceERC) {

		return getService().getSegmentsExperienceAudienceEntryRels(
			groupId, segmentsExperienceERC);
	}

	/**
	 * Returns all the segments experience audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experience audience entry rels
	 * @param companyId the primary key of the company
	 * @return the matching segments experience audience entry rels, or an empty list if no matches were found
	 */
	public static List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of segments experience audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experience audience entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching segments experience audience entry rels, or an empty list if no matches were found
	 */
	public static List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<SegmentsExperienceAudienceEntryRel>
				orderByComparator) {

		return getService().
			getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of segments experience audience entry rels.
	 *
	 * @return the number of segments experience audience entry rels
	 */
	public static int getSegmentsExperienceAudienceEntryRelsCount() {
		return getService().getSegmentsExperienceAudienceEntryRelsCount();
	}

	/**
	 * Updates the segments experience audience entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was updated
	 */
	public static SegmentsExperienceAudienceEntryRel
		updateSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return getService().updateSegmentsExperienceAudienceEntryRel(
			segmentsExperienceAudienceEntryRel);
	}

	public static List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long userId, long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws PortalException {

		return getService().updateSegmentsExperienceAudienceEntryRels(
			userId, groupId, audienceEntryERCs, segmentsExperienceERC);
	}

	public static SegmentsExperienceAudienceEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<SegmentsExperienceAudienceEntryRelLocalService> _serviceSnapshot =
			new Snapshot<>(
				SegmentsExperienceAudienceEntryRelLocalServiceUtil.class,
				SegmentsExperienceAudienceEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1478478896