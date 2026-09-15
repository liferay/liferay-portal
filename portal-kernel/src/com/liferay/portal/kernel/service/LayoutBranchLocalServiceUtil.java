/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutBranch. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutBranchLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutBranchLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutBranchLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout branch to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was added
	 */
	public static LayoutBranch addLayoutBranch(LayoutBranch layoutBranch) {
		return getService().addLayoutBranch(layoutBranch);
	}

	public static LayoutBranch addLayoutBranch(
			long layoutSetBranchId, long plid, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutBranch(
			layoutSetBranchId, plid, name, description, master, serviceContext);
	}

	public static LayoutBranch addLayoutBranch(
			long layoutRevisionId, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutBranch(
			layoutRevisionId, name, description, master, serviceContext);
	}

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	public static LayoutBranch createLayoutBranch(long layoutBranchId) {
		return getService().createLayoutBranch(layoutBranchId);
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
	 * Deletes the layout branch from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was removed
	 */
	public static LayoutBranch deleteLayoutBranch(LayoutBranch layoutBranch) {
		return getService().deleteLayoutBranch(layoutBranch);
	}

	/**
	 * Deletes the layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch that was removed
	 * @throws PortalException if a layout branch with the primary key could not be found
	 */
	public static LayoutBranch deleteLayoutBranch(long layoutBranchId)
		throws PortalException {

		return getService().deleteLayoutBranch(layoutBranchId);
	}

	public static void deleteLayoutBranchesByPlid(long plid)
		throws PortalException {

		getService().deleteLayoutBranchesByPlid(plid);
	}

	public static void deleteLayoutSetBranchLayoutBranches(
			long layoutSetBranchId)
		throws PortalException {

		getService().deleteLayoutSetBranchLayoutBranches(layoutSetBranchId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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

	public static LayoutBranch fetchLayoutBranch(long layoutBranchId) {
		return getService().fetchLayoutBranch(layoutBranchId);
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
	 * Returns the layout branch with the primary key.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws PortalException if a layout branch with the primary key could not be found
	 */
	public static LayoutBranch getLayoutBranch(long layoutBranchId)
		throws PortalException {

		return getService().getLayoutBranch(layoutBranchId);
	}

	public static List<LayoutBranch> getLayoutBranches(
		long layoutSetBranchId, long plid, int start, int end,
		OrderByComparator<LayoutBranch> orderByComparator) {

		return getService().getLayoutBranches(
			layoutSetBranchId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layout branches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of layout branches
	 */
	public static List<LayoutBranch> getLayoutBranchs(int start, int end) {
		return getService().getLayoutBranchs(start, end);
	}

	/**
	 * Returns the number of layout branches.
	 *
	 * @return the number of layout branches
	 */
	public static int getLayoutBranchsCount() {
		return getService().getLayoutBranchsCount();
	}

	public static List<LayoutBranch> getLayoutSetBranchLayoutBranches(
		long layoutSetBranchId) {

		return getService().getLayoutSetBranchLayoutBranches(layoutSetBranchId);
	}

	public static LayoutBranch getMasterLayoutBranch(
			long layoutSetBranchId, long plid)
		throws PortalException {

		return getService().getMasterLayoutBranch(layoutSetBranchId, plid);
	}

	public static LayoutBranch getMasterLayoutBranch(
			long layoutSetBranchId, long plid, ServiceContext serviceContext)
		throws PortalException {

		return getService().getMasterLayoutBranch(
			layoutSetBranchId, plid, serviceContext);
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
	 * Updates the layout branch in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was updated
	 */
	public static LayoutBranch updateLayoutBranch(LayoutBranch layoutBranch) {
		return getService().updateLayoutBranch(layoutBranch);
	}

	public static LayoutBranch updateLayoutBranch(
			long layoutBranchId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutBranch(
			layoutBranchId, name, description, serviceContext);
	}

	public static LayoutBranchLocalService getService() {
		return _service;
	}

	public static void setService(LayoutBranchLocalService service) {
		_service = service;
	}

	private static volatile LayoutBranchLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1577161337