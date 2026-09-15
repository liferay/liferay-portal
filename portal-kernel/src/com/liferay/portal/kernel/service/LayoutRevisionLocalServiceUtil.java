/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutRevision. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutRevisionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutRevisionLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutRevisionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutRevisionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout revision to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was added
	 */
	public static LayoutRevision addLayoutRevision(
		LayoutRevision layoutRevision) {

		return getService().addLayoutRevision(layoutRevision);
	}

	public static LayoutRevision addLayoutRevision(
			long userId, long layoutSetBranchId, long layoutBranchId,
			long parentLayoutRevisionId, boolean head, long plid,
			long portletPreferencesPlid, boolean privateLayout, String name,
			String title, String description, String keywords, String robots,
			String typeSettings, boolean iconImage, long iconImageId,
			String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutRevision(
			userId, layoutSetBranchId, layoutBranchId, parentLayoutRevisionId,
			head, plid, portletPreferencesPlid, privateLayout, name, title,
			description, keywords, robots, typeSettings, iconImage, iconImageId,
			themeId, colorSchemeId, css, serviceContext);
	}

	/**
	 * Creates a new layout revision with the primary key. Does not add the layout revision to the database.
	 *
	 * @param layoutRevisionId the primary key for the new layout revision
	 * @return the new layout revision
	 */
	public static LayoutRevision createLayoutRevision(long layoutRevisionId) {
		return getService().createLayoutRevision(layoutRevisionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteLayoutLayoutRevisions(long plid)
		throws PortalException {

		getService().deleteLayoutLayoutRevisions(plid);
	}

	/**
	 * Deletes the layout revision from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was removed
	 * @throws PortalException
	 */
	public static LayoutRevision deleteLayoutRevision(
			LayoutRevision layoutRevision)
		throws PortalException {

		return getService().deleteLayoutRevision(layoutRevision);
	}

	/**
	 * Deletes the layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision that was removed
	 * @throws PortalException if a layout revision with the primary key could not be found
	 */
	public static LayoutRevision deleteLayoutRevision(long layoutRevisionId)
		throws PortalException {

		return getService().deleteLayoutRevision(layoutRevisionId);
	}

	public static void deleteLayoutRevisions(long layoutSetBranchId, long plid)
		throws PortalException {

		getService().deleteLayoutRevisions(layoutSetBranchId, plid);
	}

	public static void deleteLayoutRevisions(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException {

		getService().deleteLayoutRevisions(
			layoutSetBranchId, layoutBranchId, plid);
	}

	public static void deleteLayoutSetBranchLayoutRevisions(
			long layoutSetBranchId)
		throws PortalException {

		getService().deleteLayoutSetBranchLayoutRevisions(layoutSetBranchId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
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

	public static LayoutRevision fetchLastLayoutRevision(
		long plid, boolean head) {

		return getService().fetchLastLayoutRevision(plid, head);
	}

	public static LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long plid) {

		return getService().fetchLatestLayoutRevision(layoutSetBranchId, plid);
	}

	public static LayoutRevision fetchLatestLayoutRevision(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return getService().fetchLatestLayoutRevision(
			layoutSetBranchId, layoutBranchId, plid);
	}

	public static LayoutRevision fetchLayoutRevision(long layoutRevisionId) {
		return getService().fetchLayoutRevision(layoutRevisionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevisionId, long plid) {

		return getService().getChildLayoutRevisions(
			layoutSetBranchId, parentLayoutRevisionId, plid);
	}

	public static List<LayoutRevision> getChildLayoutRevisions(
		long layoutSetBranchId, long parentLayoutRevision, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator) {

		return getService().getChildLayoutRevisions(
			layoutSetBranchId, parentLayoutRevision, plid, start, end,
			orderByComparator);
	}

	public static int getChildLayoutRevisionsCount(
		long layoutSetBranchId, long parentLayoutRevision, long plid) {

		return getService().getChildLayoutRevisionsCount(
			layoutSetBranchId, parentLayoutRevision, plid);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout revision with the primary key.
	 *
	 * @param layoutRevisionId the primary key of the layout revision
	 * @return the layout revision
	 * @throws PortalException if a layout revision with the primary key could not be found
	 */
	public static LayoutRevision getLayoutRevision(long layoutRevisionId)
		throws PortalException {

		return getService().getLayoutRevision(layoutRevisionId);
	}

	public static LayoutRevision getLayoutRevision(
			long layoutSetBranchId, long layoutBranchId, long plid)
		throws PortalException {

		return getService().getLayoutRevision(
			layoutSetBranchId, layoutBranchId, plid);
	}

	/**
	 * Returns a range of all the layout revisions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout revisions
	 * @param end the upper bound of the range of layout revisions (not inclusive)
	 * @return the range of layout revisions
	 */
	public static List<LayoutRevision> getLayoutRevisions(int start, int end) {
		return getService().getLayoutRevisions(start, end);
	}

	public static List<LayoutRevision> getLayoutRevisions(long plid) {
		return getService().getLayoutRevisions(plid);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head) {

		return getService().getLayoutRevisions(layoutSetBranchId, head);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, boolean head, int status) {

		return getService().getLayoutRevisions(layoutSetBranchId, head, status);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, int status) {

		return getService().getLayoutRevisions(layoutSetBranchId, status);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid) {

		return getService().getLayoutRevisions(layoutSetBranchId, plid);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, boolean head) {

		return getService().getLayoutRevisions(layoutSetBranchId, plid, head);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int status) {

		return getService().getLayoutRevisions(layoutSetBranchId, plid, status);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutRevision> orderByComparator) {

		return getService().getLayoutRevisions(
			layoutSetBranchId, plid, start, end, orderByComparator);
	}

	public static List<LayoutRevision> getLayoutRevisions(
		long layoutSetBranchId, long layoutBranchId, long plid, int start,
		int end, OrderByComparator<LayoutRevision> orderByComparator) {

		return getService().getLayoutRevisions(
			layoutSetBranchId, layoutBranchId, plid, start, end,
			orderByComparator);
	}

	public static List<LayoutRevision> getLayoutRevisionsByStatus(int status) {
		return getService().getLayoutRevisionsByStatus(status);
	}

	/**
	 * Returns the number of layout revisions.
	 *
	 * @return the number of layout revisions
	 */
	public static int getLayoutRevisionsCount() {
		return getService().getLayoutRevisionsCount();
	}

	public static int getLayoutRevisionsCount(long plid) {
		return getService().getLayoutRevisionsCount(plid);
	}

	public static int getLayoutRevisionsCount(
		long layoutSetBranchId, long layoutBranchId, long plid) {

		return getService().getLayoutRevisionsCount(
			layoutSetBranchId, layoutBranchId, plid);
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
	 * Updates the layout revision in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutRevision the layout revision
	 * @return the layout revision that was updated
	 */
	public static LayoutRevision updateLayoutRevision(
		LayoutRevision layoutRevision) {

		return getService().updateLayoutRevision(layoutRevision);
	}

	public static LayoutRevision updateLayoutRevision(
			long userId, long layoutRevisionId, long layoutBranchId,
			String name, String title, String description, String keywords,
			String robots, String typeSettings, boolean iconImage,
			long iconImageId, String themeId, String colorSchemeId, String css,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutRevision(
			userId, layoutRevisionId, layoutBranchId, name, title, description,
			keywords, robots, typeSettings, iconImage, iconImageId, themeId,
			colorSchemeId, css, serviceContext);
	}

	public static LayoutRevision updateStatus(
			long userId, long layoutRevisionId, int status,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, layoutRevisionId, status, serviceContext);
	}

	public static LayoutRevisionLocalService getService() {
		return _service;
	}

	public static void setService(LayoutRevisionLocalService service) {
		_service = service;
	}

	private static volatile LayoutRevisionLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1421977305