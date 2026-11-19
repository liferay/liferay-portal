/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTRemoteLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTRemoteLocalService
 * @generated
 */
public class CTRemoteLocalServiceWrapper
	implements CTRemoteLocalService, ServiceWrapper<CTRemoteLocalService> {

	public CTRemoteLocalServiceWrapper() {
		this(null);
	}

	public CTRemoteLocalServiceWrapper(
		CTRemoteLocalService ctRemoteLocalService) {

		_ctRemoteLocalService = ctRemoteLocalService;
	}

	/**
	 * Adds the ct remote to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTRemoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctRemote the ct remote
	 * @return the ct remote that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote addCTRemote(
		com.liferay.change.tracking.model.CTRemote ctRemote) {

		return _ctRemoteLocalService.addCTRemote(ctRemote);
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote addCTRemote(
			long userId, String name, String description, String url,
			String clientId, String clientSecret)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.addCTRemote(
			userId, name, description, url, clientId, clientSecret);
	}

	/**
	 * Creates a new ct remote with the primary key. Does not add the ct remote to the database.
	 *
	 * @param ctRemoteId the primary key for the new ct remote
	 * @return the new ct remote
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote createCTRemote(
		long ctRemoteId) {

		return _ctRemoteLocalService.createCTRemote(ctRemoteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ct remote from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTRemoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctRemote the ct remote
	 * @return the ct remote that was removed
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote deleteCTRemote(
		com.liferay.change.tracking.model.CTRemote ctRemote) {

		return _ctRemoteLocalService.deleteCTRemote(ctRemote);
	}

	/**
	 * Deletes the ct remote with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTRemoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctRemoteId the primary key of the ct remote
	 * @return the ct remote that was removed
	 * @throws PortalException if a ct remote with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote deleteCTRemote(
			long ctRemoteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.deleteCTRemote(ctRemoteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctRemoteLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctRemoteLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctRemoteLocalService.dynamicQuery();
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

		return _ctRemoteLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTRemoteModelImpl</code>.
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

		return _ctRemoteLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTRemoteModelImpl</code>.
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

		return _ctRemoteLocalService.dynamicQuery(
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

		return _ctRemoteLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctRemoteLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote fetchCTRemote(
		long ctRemoteId) {

		return _ctRemoteLocalService.fetchCTRemote(ctRemoteId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctRemoteLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ct remote with the primary key.
	 *
	 * @param ctRemoteId the primary key of the ct remote
	 * @return the ct remote
	 * @throws PortalException if a ct remote with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote getCTRemote(
			long ctRemoteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.getCTRemote(ctRemoteId);
	}

	/**
	 * Returns a range of all the ct remotes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTRemoteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct remotes
	 * @param end the upper bound of the range of ct remotes (not inclusive)
	 * @return the range of ct remotes
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTRemote>
		getCTRemotes(int start, int end) {

		return _ctRemoteLocalService.getCTRemotes(start, end);
	}

	@Override
	public java.util.List<com.liferay.change.tracking.model.CTRemote>
		getCTRemotes(long companyId) {

		return _ctRemoteLocalService.getCTRemotes(companyId);
	}

	/**
	 * Returns the number of ct remotes.
	 *
	 * @return the number of ct remotes
	 */
	@Override
	public int getCTRemotesCount() {
		return _ctRemoteLocalService.getCTRemotesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctRemoteLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctRemoteLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the ct remote in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTRemoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctRemote the ct remote
	 * @return the ct remote that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTRemote updateCTRemote(
		com.liferay.change.tracking.model.CTRemote ctRemote) {

		return _ctRemoteLocalService.updateCTRemote(ctRemote);
	}

	@Override
	public com.liferay.change.tracking.model.CTRemote updateCTRemote(
			long ctRemoteId, String name, String description, String url,
			String clientId, String clientSecret)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctRemoteLocalService.updateCTRemote(
			ctRemoteId, name, description, url, clientId, clientSecret);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctRemoteLocalService.getBasePersistence();
	}

	@Override
	public CTRemoteLocalService getWrappedService() {
		return _ctRemoteLocalService;
	}

	@Override
	public void setWrappedService(CTRemoteLocalService ctRemoteLocalService) {
		_ctRemoteLocalService = ctRemoteLocalService;
	}

	private CTRemoteLocalService _ctRemoteLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:670551806