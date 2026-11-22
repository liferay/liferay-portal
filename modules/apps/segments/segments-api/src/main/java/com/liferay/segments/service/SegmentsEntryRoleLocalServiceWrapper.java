/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.model.SegmentsEntryRole;

/**
 * Provides a wrapper for {@link SegmentsEntryRoleLocalService}.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryRoleLocalService
 * @generated
 */
public class SegmentsEntryRoleLocalServiceWrapper
	implements SegmentsEntryRoleLocalService,
			   ServiceWrapper<SegmentsEntryRoleLocalService> {

	public SegmentsEntryRoleLocalServiceWrapper() {
		this(null);
	}

	public SegmentsEntryRoleLocalServiceWrapper(
		SegmentsEntryRoleLocalService segmentsEntryRoleLocalService) {

		_segmentsEntryRoleLocalService = segmentsEntryRoleLocalService;
	}

	@Override
	public SegmentsEntryRole addSegmentsEntryRole(
			long segmentsEntryId, long roleId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.addSegmentsEntryRole(
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
	@Override
	public SegmentsEntryRole addSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return _segmentsEntryRoleLocalService.addSegmentsEntryRole(
			segmentsEntryRole);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new segments entry role with the primary key. Does not add the segments entry role to the database.
	 *
	 * @param segmentsEntryRoleId the primary key for the new segments entry role
	 * @return the new segments entry role
	 */
	@Override
	public SegmentsEntryRole createSegmentsEntryRole(long segmentsEntryRoleId) {
		return _segmentsEntryRoleLocalService.createSegmentsEntryRole(
			segmentsEntryRoleId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public SegmentsEntryRole deleteSegmentsEntryRole(long segmentsEntryRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.deleteSegmentsEntryRole(
			segmentsEntryRoleId);
	}

	@Override
	public SegmentsEntryRole deleteSegmentsEntryRole(
			long segmentsEntryId, long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.deleteSegmentsEntryRole(
			segmentsEntryId, roleId);
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
	@Override
	public SegmentsEntryRole deleteSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return _segmentsEntryRoleLocalService.deleteSegmentsEntryRole(
			segmentsEntryRole);
	}

	@Override
	public void deleteSegmentsEntryRoles(long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryRoleLocalService.deleteSegmentsEntryRoles(
			segmentsEntryId);
	}

	@Override
	public void deleteSegmentsEntryRolesByRoleId(long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryRoleLocalService.deleteSegmentsEntryRolesByRoleId(roleId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _segmentsEntryRoleLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _segmentsEntryRoleLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _segmentsEntryRoleLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _segmentsEntryRoleLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _segmentsEntryRoleLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _segmentsEntryRoleLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _segmentsEntryRoleLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _segmentsEntryRoleLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public SegmentsEntryRole fetchSegmentsEntryRole(long segmentsEntryRoleId) {
		return _segmentsEntryRoleLocalService.fetchSegmentsEntryRole(
			segmentsEntryRoleId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _segmentsEntryRoleLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _segmentsEntryRoleLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _segmentsEntryRoleLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the segments entry role with the primary key.
	 *
	 * @param segmentsEntryRoleId the primary key of the segments entry role
	 * @return the segments entry role
	 * @throws PortalException if a segments entry role with the primary key could not be found
	 */
	@Override
	public SegmentsEntryRole getSegmentsEntryRole(long segmentsEntryRoleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsEntryRoleLocalService.getSegmentsEntryRole(
			segmentsEntryRoleId);
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
	@Override
	public java.util.List<SegmentsEntryRole> getSegmentsEntryRoles(
		int start, int end) {

		return _segmentsEntryRoleLocalService.getSegmentsEntryRoles(start, end);
	}

	@Override
	public java.util.List<SegmentsEntryRole> getSegmentsEntryRoles(
		long segmentsEntryId) {

		return _segmentsEntryRoleLocalService.getSegmentsEntryRoles(
			segmentsEntryId);
	}

	/**
	 * Returns the number of segments entry roles.
	 *
	 * @return the number of segments entry roles
	 */
	@Override
	public int getSegmentsEntryRolesCount() {
		return _segmentsEntryRoleLocalService.getSegmentsEntryRolesCount();
	}

	@Override
	public int getSegmentsEntryRolesCountByRoleId(long roleId) {
		return _segmentsEntryRoleLocalService.
			getSegmentsEntryRolesCountByRoleId(roleId);
	}

	@Override
	public boolean hasSegmentEntryRole(long segmentsEntryId, long roleId) {
		return _segmentsEntryRoleLocalService.hasSegmentEntryRole(
			segmentsEntryId, roleId);
	}

	@Override
	public void setSegmentsEntrySiteRoles(
			long segmentsEntryId, long[] siteRoleIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_segmentsEntryRoleLocalService.setSegmentsEntrySiteRoles(
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
	@Override
	public SegmentsEntryRole updateSegmentsEntryRole(
		SegmentsEntryRole segmentsEntryRole) {

		return _segmentsEntryRoleLocalService.updateSegmentsEntryRole(
			segmentsEntryRole);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _segmentsEntryRoleLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<SegmentsEntryRole> getCTPersistence() {
		return _segmentsEntryRoleLocalService.getCTPersistence();
	}

	@Override
	public Class<SegmentsEntryRole> getModelClass() {
		return _segmentsEntryRoleLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SegmentsEntryRole>, R, E>
				updateUnsafeFunction)
		throws E {

		return _segmentsEntryRoleLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public SegmentsEntryRoleLocalService getWrappedService() {
		return _segmentsEntryRoleLocalService;
	}

	@Override
	public void setWrappedService(
		SegmentsEntryRoleLocalService segmentsEntryRoleLocalService) {

		_segmentsEntryRoleLocalService = segmentsEntryRoleLocalService;
	}

	private SegmentsEntryRoleLocalService _segmentsEntryRoleLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1477159035