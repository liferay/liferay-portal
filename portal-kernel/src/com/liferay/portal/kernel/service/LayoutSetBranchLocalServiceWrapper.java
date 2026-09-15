/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LayoutSetBranchLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class LayoutSetBranchLocalServiceWrapper
	implements LayoutSetBranchLocalService,
			   ServiceWrapper<LayoutSetBranchLocalService> {

	public LayoutSetBranchLocalServiceWrapper() {
		this(null);
	}

	public LayoutSetBranchLocalServiceWrapper(
		LayoutSetBranchLocalService layoutSetBranchLocalService) {

		_layoutSetBranchLocalService = layoutSetBranchLocalService;
	}

	/**
	 * Adds the layout set branch to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetBranch the layout set branch
	 * @return the layout set branch that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch addLayoutSetBranch(
		com.liferay.portal.kernel.model.LayoutSetBranch layoutSetBranch) {

		return _layoutSetBranchLocalService.addLayoutSetBranch(layoutSetBranch);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch addLayoutSetBranch(
			long userId, long groupId, boolean privateLayout, String name,
			String description, boolean master, long copyLayoutSetBranchId,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.addLayoutSetBranch(
			userId, groupId, privateLayout, name, description, master,
			copyLayoutSetBranchId, serviceContext);
	}

	/**
	 * Creates a new layout set branch with the primary key. Does not add the layout set branch to the database.
	 *
	 * @param layoutSetBranchId the primary key for the new layout set branch
	 * @return the new layout set branch
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
		createLayoutSetBranch(long layoutSetBranchId) {

		return _layoutSetBranchLocalService.createLayoutSetBranch(
			layoutSetBranchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the layout set branch from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetBranch the layout set branch
	 * @return the layout set branch that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			deleteLayoutSetBranch(
				com.liferay.portal.kernel.model.LayoutSetBranch layoutSetBranch)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deleteLayoutSetBranch(
			layoutSetBranch);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			deleteLayoutSetBranch(
				com.liferay.portal.kernel.model.LayoutSetBranch layoutSetBranch,
				boolean includeMaster)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deleteLayoutSetBranch(
			layoutSetBranch, includeMaster);
	}

	/**
	 * Deletes the layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch that was removed
	 * @throws PortalException if a layout set branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			deleteLayoutSetBranch(long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deleteLayoutSetBranch(
			layoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			deleteLayoutSetBranch(
				long currentLayoutPlid,
				com.liferay.portal.kernel.model.LayoutSetBranch layoutSetBranch,
				boolean includeMaster)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deleteLayoutSetBranch(
			currentLayoutPlid, layoutSetBranch, includeMaster);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			deleteLayoutSetBranch(
				long currentLayoutPlid, long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deleteLayoutSetBranch(
			currentLayoutPlid, layoutSetBranchId);
	}

	@Override
	public void deleteLayoutSetBranches(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetBranchLocalService.deleteLayoutSetBranches(
			groupId, privateLayout);
	}

	@Override
	public void deleteLayoutSetBranches(
			long groupId, boolean privateLayout, boolean includeMaster)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutSetBranchLocalService.deleteLayoutSetBranches(
			groupId, privateLayout, includeMaster);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutSetBranchLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutSetBranchLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutSetBranchLocalService.dynamicQuery();
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

		return _layoutSetBranchLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
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

		return _layoutSetBranchLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
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

		return _layoutSetBranchLocalService.dynamicQuery(
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

		return _layoutSetBranchLocalService.dynamicQueryCount(dynamicQuery);
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

		return _layoutSetBranchLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch fetchLayoutSetBranch(
		long layoutSetBranchId) {

		return _layoutSetBranchLocalService.fetchLayoutSetBranch(
			layoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch fetchLayoutSetBranch(
		long groupId, boolean privateLayout, String name) {

		return _layoutSetBranchLocalService.fetchLayoutSetBranch(
			groupId, privateLayout, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutSetBranchLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutSetBranchLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout set branch with the primary key.
	 *
	 * @param layoutSetBranchId the primary key of the layout set branch
	 * @return the layout set branch
	 * @throws PortalException if a layout set branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch getLayoutSetBranch(
			long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.getLayoutSetBranch(
			layoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch getLayoutSetBranch(
			long groupId, boolean privateLayout, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.getLayoutSetBranch(
			groupId, privateLayout, name);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutSetBranch>
		getLayoutSetBranches(long groupId, boolean privateLayout) {

		return _layoutSetBranchLocalService.getLayoutSetBranches(
			groupId, privateLayout);
	}

	/**
	 * Returns a range of all the layout set branches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout set branches
	 * @param end the upper bound of the range of layout set branches (not inclusive)
	 * @return the range of layout set branches
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.LayoutSetBranch>
		getLayoutSetBranchs(int start, int end) {

		return _layoutSetBranchLocalService.getLayoutSetBranchs(start, end);
	}

	/**
	 * Returns the number of layout set branches.
	 *
	 * @return the number of layout set branches
	 */
	@Override
	public int getLayoutSetBranchsCount() {
		return _layoutSetBranchLocalService.getLayoutSetBranchsCount();
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			getMasterLayoutSetBranch(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.getMasterLayoutSetBranch(
			groupId, privateLayout);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutSetBranchLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			getUserLayoutSetBranch(
				long userId, long groupId, boolean privateLayout,
				long layoutSetId, long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.getUserLayoutSetBranch(
			userId, groupId, privateLayout, layoutSetId, layoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.mergeLayoutSetBranch(
			layoutSetBranchId, mergeLayoutSetBranchId, serviceContext);
	}

	/**
	 * Updates the layout set branch in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetBranch the layout set branch
	 * @return the layout set branch that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
		updateLayoutSetBranch(
			com.liferay.portal.kernel.model.LayoutSetBranch layoutSetBranch) {

		return _layoutSetBranchLocalService.updateLayoutSetBranch(
			layoutSetBranch);
	}

	@Override
	public com.liferay.portal.kernel.model.LayoutSetBranch
			updateLayoutSetBranch(
				long layoutSetBranchId, String name, String description,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutSetBranchLocalService.updateLayoutSetBranch(
			layoutSetBranchId, name, description, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutSetBranchLocalService.getBasePersistence();
	}

	@Override
	public LayoutSetBranchLocalService getWrappedService() {
		return _layoutSetBranchLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutSetBranchLocalService layoutSetBranchLocalService) {

		_layoutSetBranchLocalService = layoutSetBranchLocalService;
	}

	private LayoutSetBranchLocalService _layoutSetBranchLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1833154521