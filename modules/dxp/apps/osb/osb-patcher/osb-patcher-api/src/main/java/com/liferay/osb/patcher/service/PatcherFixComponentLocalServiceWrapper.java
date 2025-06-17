/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherFixComponentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixComponentLocalService
 * @generated
 */
public class PatcherFixComponentLocalServiceWrapper
	implements PatcherFixComponentLocalService,
			   ServiceWrapper<PatcherFixComponentLocalService> {

	public PatcherFixComponentLocalServiceWrapper() {
		this(null);
	}

	public PatcherFixComponentLocalServiceWrapper(
		PatcherFixComponentLocalService patcherFixComponentLocalService) {

		_patcherFixComponentLocalService = patcherFixComponentLocalService;
	}

	/**
	 * Adds the patcher fix component to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixComponentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixComponent the patcher fix component
	 * @return the patcher fix component that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
		addPatcherFixComponent(
			com.liferay.osb.patcher.model.PatcherFixComponent
				patcherFixComponent) {

		return _patcherFixComponentLocalService.addPatcherFixComponent(
			patcherFixComponent);
	}

	/**
	 * Creates a new patcher fix component with the primary key. Does not add the patcher fix component to the database.
	 *
	 * @param patcherFixComponentId the primary key for the new patcher fix component
	 * @return the new patcher fix component
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
		createPatcherFixComponent(long patcherFixComponentId) {

		return _patcherFixComponentLocalService.createPatcherFixComponent(
			patcherFixComponentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixComponentLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the patcher fix component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixComponentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component that was removed
	 * @throws PortalException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
			deletePatcherFixComponent(long patcherFixComponentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixComponentLocalService.deletePatcherFixComponent(
			patcherFixComponentId);
	}

	/**
	 * Deletes the patcher fix component from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixComponentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixComponent the patcher fix component
	 * @return the patcher fix component that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
		deletePatcherFixComponent(
			com.liferay.osb.patcher.model.PatcherFixComponent
				patcherFixComponent) {

		return _patcherFixComponentLocalService.deletePatcherFixComponent(
			patcherFixComponent);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixComponentLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherFixComponentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherFixComponentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherFixComponentLocalService.dynamicQuery();
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

		return _patcherFixComponentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl</code>.
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

		return _patcherFixComponentLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl</code>.
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

		return _patcherFixComponentLocalService.dynamicQuery(
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

		return _patcherFixComponentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherFixComponentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
		fetchPatcherFixComponent(long patcherFixComponentId) {

		return _patcherFixComponentLocalService.fetchPatcherFixComponent(
			patcherFixComponentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherFixComponentLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherFixComponentLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherFixComponentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher fix component with the primary key.
	 *
	 * @param patcherFixComponentId the primary key of the patcher fix component
	 * @return the patcher fix component
	 * @throws PortalException if a patcher fix component with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
			getPatcherFixComponent(long patcherFixComponentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixComponentLocalService.getPatcherFixComponent(
			patcherFixComponentId);
	}

	/**
	 * Returns a range of all the patcher fix components.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixComponentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix components
	 * @param end the upper bound of the range of patcher fix components (not inclusive)
	 * @return the range of patcher fix components
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherFixComponent>
		getPatcherFixComponents(int start, int end) {

		return _patcherFixComponentLocalService.getPatcherFixComponents(
			start, end);
	}

	/**
	 * Returns the number of patcher fix components.
	 *
	 * @return the number of patcher fix components
	 */
	@Override
	public int getPatcherFixComponentsCount() {
		return _patcherFixComponentLocalService.getPatcherFixComponentsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherFixComponentLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the patcher fix component in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixComponentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixComponent the patcher fix component
	 * @return the patcher fix component that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherFixComponent
		updatePatcherFixComponent(
			com.liferay.osb.patcher.model.PatcherFixComponent
				patcherFixComponent) {

		return _patcherFixComponentLocalService.updatePatcherFixComponent(
			patcherFixComponent);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherFixComponentLocalService.getBasePersistence();
	}

	@Override
	public PatcherFixComponentLocalService getWrappedService() {
		return _patcherFixComponentLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherFixComponentLocalService patcherFixComponentLocalService) {

		_patcherFixComponentLocalService = patcherFixComponentLocalService;
	}

	private PatcherFixComponentLocalService _patcherFixComponentLocalService;

}