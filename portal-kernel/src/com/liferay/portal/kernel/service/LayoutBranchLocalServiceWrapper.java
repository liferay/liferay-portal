/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LayoutBranchLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutBranchLocalServiceWrapper
	implements LayoutBranchLocalService,
			   ServiceWrapper<LayoutBranchLocalService> {

	public LayoutBranchLocalServiceWrapper() {
		this(null);
	}

	public LayoutBranchLocalServiceWrapper(
		LayoutBranchLocalService layoutBranchLocalService) {

		_layoutBranchLocalService = layoutBranchLocalService;
	}

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
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
		com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return _layoutBranchLocalService.addLayoutBranch(layoutBranch);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
			long layoutSetBranchId, long plid, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.addLayoutBranch(
			layoutSetBranchId, plid, name, description, master, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
			long layoutRevisionId, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.addLayoutBranch(
			layoutRevisionId, name, description, master, serviceContext);
	}

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch createLayoutBranch(
		long layoutBranchId) {

		return _layoutBranchLocalService.createLayoutBranch(layoutBranchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch deleteLayoutBranch(
		com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return _layoutBranchLocalService.deleteLayoutBranch(layoutBranch);
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
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch deleteLayoutBranch(
			long layoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.deleteLayoutBranch(layoutBranchId);
	}

	@Override
	public void deleteLayoutBranchesByPlid(long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutBranchLocalService.deleteLayoutBranchesByPlid(plid);
	}

	@Override
	public void deleteLayoutSetBranchLayoutBranches(long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutBranchLocalService.deleteLayoutSetBranchLayoutBranches(
			layoutSetBranchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutBranchLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutBranchLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutBranchLocalService.dynamicQuery();
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

		return _layoutBranchLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _layoutBranchLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _layoutBranchLocalService.dynamicQuery(
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

		return _layoutBranchLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutBranchLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch fetchLayoutBranch(
		long layoutBranchId) {

		return _layoutBranchLocalService.fetchLayoutBranch(layoutBranchId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutBranchLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutBranchLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout branch with the primary key.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws PortalException if a layout branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch getLayoutBranch(
			long layoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.getLayoutBranch(layoutBranchId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutBranches(
			long layoutSetBranchId, long plid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.LayoutBranch>
					orderByComparator) {

		return _layoutBranchLocalService.getLayoutBranches(
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
	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutBranchs(int start, int end) {

		return _layoutBranchLocalService.getLayoutBranchs(start, end);
	}

	/**
	 * Returns the number of layout branches.
	 *
	 * @return the number of layout branches
	 */
	@Override
	public int getLayoutBranchsCount() {
		return _layoutBranchLocalService.getLayoutBranchsCount();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutSetBranchLayoutBranches(long layoutSetBranchId) {

		return _layoutBranchLocalService.getLayoutSetBranchLayoutBranches(
			layoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch getMasterLayoutBranch(
			long layoutSetBranchId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.getMasterLayoutBranch(
			layoutSetBranchId, plid);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch getMasterLayoutBranch(
			long layoutSetBranchId, long plid, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.getMasterLayoutBranch(
			layoutSetBranchId, plid, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutBranchLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.portal.kernel.model.LayoutBranch updateLayoutBranch(
		com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return _layoutBranchLocalService.updateLayoutBranch(layoutBranch);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutBranch updateLayoutBranch(
			long layoutBranchId, String name, String description,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutBranchLocalService.updateLayoutBranch(
			layoutBranchId, name, description, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutBranchLocalService.getBasePersistence();
	}

	@Override
	public LayoutBranchLocalService getWrappedService() {
		return _layoutBranchLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutBranchLocalService layoutBranchLocalService) {

		_layoutBranchLocalService = layoutBranchLocalService;
	}

	private LayoutBranchLocalService _layoutBranchLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:389016735