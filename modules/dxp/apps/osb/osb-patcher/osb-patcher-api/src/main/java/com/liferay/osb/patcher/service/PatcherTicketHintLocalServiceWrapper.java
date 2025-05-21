/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PatcherTicketHintLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherTicketHintLocalService
 * @generated
 */
public class PatcherTicketHintLocalServiceWrapper
	implements PatcherTicketHintLocalService,
			   ServiceWrapper<PatcherTicketHintLocalService> {

	public PatcherTicketHintLocalServiceWrapper() {
		this(null);
	}

	public PatcherTicketHintLocalServiceWrapper(
		PatcherTicketHintLocalService patcherTicketHintLocalService) {

		_patcherTicketHintLocalService = patcherTicketHintLocalService;
	}

	/**
	 * Adds the patcher ticket hint to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherTicketHintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 * @return the patcher ticket hint that was added
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint addPatcherTicketHint(
		com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint) {

		return _patcherTicketHintLocalService.addPatcherTicketHint(
			patcherTicketHint);
	}

	/**
	 * Creates a new patcher ticket hint with the primary key. Does not add the patcher ticket hint to the database.
	 *
	 * @param patcherTicketHintId the primary key for the new patcher ticket hint
	 * @return the new patcher ticket hint
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint
		createPatcherTicketHint(long patcherTicketHintId) {

		return _patcherTicketHintLocalService.createPatcherTicketHint(
			patcherTicketHintId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherTicketHintLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the patcher ticket hint with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherTicketHintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 * @throws PortalException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint
			deletePatcherTicketHint(long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherTicketHintLocalService.deletePatcherTicketHint(
			patcherTicketHintId);
	}

	/**
	 * Deletes the patcher ticket hint from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherTicketHintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 * @return the patcher ticket hint that was removed
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint
		deletePatcherTicketHint(
			com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint) {

		return _patcherTicketHintLocalService.deletePatcherTicketHint(
			patcherTicketHint);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherTicketHintLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _patcherTicketHintLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _patcherTicketHintLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _patcherTicketHintLocalService.dynamicQuery();
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

		return _patcherTicketHintLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl</code>.
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

		return _patcherTicketHintLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl</code>.
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

		return _patcherTicketHintLocalService.dynamicQuery(
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

		return _patcherTicketHintLocalService.dynamicQueryCount(dynamicQuery);
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

		return _patcherTicketHintLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint
		fetchPatcherTicketHint(long patcherTicketHintId) {

		return _patcherTicketHintLocalService.fetchPatcherTicketHint(
			patcherTicketHintId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _patcherTicketHintLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _patcherTicketHintLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _patcherTicketHintLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher ticket hint with the primary key.
	 *
	 * @param patcherTicketHintId the primary key of the patcher ticket hint
	 * @return the patcher ticket hint
	 * @throws PortalException if a patcher ticket hint with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint getPatcherTicketHint(
			long patcherTicketHintId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherTicketHintLocalService.getPatcherTicketHint(
			patcherTicketHintId);
	}

	/**
	 * Returns a range of all the patcher ticket hints.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherTicketHintModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher ticket hints
	 * @param end the upper bound of the range of patcher ticket hints (not inclusive)
	 * @return the range of patcher ticket hints
	 */
	@Override
	public java.util.List<com.liferay.osb.patcher.model.PatcherTicketHint>
		getPatcherTicketHints(int start, int end) {

		return _patcherTicketHintLocalService.getPatcherTicketHints(start, end);
	}

	/**
	 * Returns the number of patcher ticket hints.
	 *
	 * @return the number of patcher ticket hints
	 */
	@Override
	public int getPatcherTicketHintsCount() {
		return _patcherTicketHintLocalService.getPatcherTicketHintsCount();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _patcherTicketHintLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the patcher ticket hint in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherTicketHintLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherTicketHint the patcher ticket hint
	 * @return the patcher ticket hint that was updated
	 */
	@Override
	public com.liferay.osb.patcher.model.PatcherTicketHint
		updatePatcherTicketHint(
			com.liferay.osb.patcher.model.PatcherTicketHint patcherTicketHint) {

		return _patcherTicketHintLocalService.updatePatcherTicketHint(
			patcherTicketHint);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _patcherTicketHintLocalService.getBasePersistence();
	}

	@Override
	public PatcherTicketHintLocalService getWrappedService() {
		return _patcherTicketHintLocalService;
	}

	@Override
	public void setWrappedService(
		PatcherTicketHintLocalService patcherTicketHintLocalService) {

		_patcherTicketHintLocalService = patcherTicketHintLocalService;
	}

	private PatcherTicketHintLocalService _patcherTicketHintLocalService;

}