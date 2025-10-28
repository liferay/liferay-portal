/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OpenIdConnectUserLocalService}.
 *
 * @author Arthur Chan
 * @see OpenIdConnectUserLocalService
 * @generated
 */
public class OpenIdConnectUserLocalServiceWrapper
	implements OpenIdConnectUserLocalService,
			   ServiceWrapper<OpenIdConnectUserLocalService> {

	public OpenIdConnectUserLocalServiceWrapper() {
		this(null);
	}

	public OpenIdConnectUserLocalServiceWrapper(
		OpenIdConnectUserLocalService openIdConnectUserLocalService) {

		_openIdConnectUserLocalService = openIdConnectUserLocalService;
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser addOpenIdConnectUser(
				long userId, String issuer, String subject)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.addOpenIdConnectUser(
			userId, issuer, subject);
	}

	/**
	 * Adds the open ID connect user to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was added
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser addOpenIdConnectUser(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectUser openIdConnectUser) {

		return _openIdConnectUserLocalService.addOpenIdConnectUser(
			openIdConnectUser);
	}

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser createOpenIdConnectUser(long openIdConnectUserId) {

		return _openIdConnectUserLocalService.createOpenIdConnectUser(
			openIdConnectUserId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws PortalException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser deleteOpenIdConnectUser(long openIdConnectUserId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.deleteOpenIdConnectUser(
			openIdConnectUserId);
	}

	/**
	 * Deletes the open ID connect user from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was removed
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser deleteOpenIdConnectUser(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectUser openIdConnectUser) {

		return _openIdConnectUserLocalService.deleteOpenIdConnectUser(
			openIdConnectUser);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _openIdConnectUserLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _openIdConnectUserLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _openIdConnectUserLocalService.dynamicQuery();
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

		return _openIdConnectUserLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
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

		return _openIdConnectUserLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
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

		return _openIdConnectUserLocalService.dynamicQuery(
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

		return _openIdConnectUserLocalService.dynamicQueryCount(dynamicQuery);
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

		return _openIdConnectUserLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser fetchOpenIdConnectUser(long openIdConnectUserId) {

		return _openIdConnectUserLocalService.fetchOpenIdConnectUser(
			openIdConnectUserId);
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser fetchOpenIdConnectUser(
			long companyId, String issuer, String subject) {

		return _openIdConnectUserLocalService.fetchOpenIdConnectUser(
			companyId, issuer, subject);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _openIdConnectUserLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _openIdConnectUserLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the open ID connect user with the primary key.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws PortalException if a open ID connect user with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser getOpenIdConnectUser(long openIdConnectUserId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.getOpenIdConnectUser(
			openIdConnectUserId);
	}

	/**
	 * Returns a range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of open ID connect users
	 */
	@Override
	public java.util.List
		<com.liferay.portal.security.sso.openid.connect.persistence.model.
			OpenIdConnectUser> getOpenIdConnectUsers(int start, int end) {

		return _openIdConnectUserLocalService.getOpenIdConnectUsers(start, end);
	}

	/**
	 * Returns the number of open ID connect users.
	 *
	 * @return the number of open ID connect users
	 */
	@Override
	public int getOpenIdConnectUsersCount() {
		return _openIdConnectUserLocalService.getOpenIdConnectUsersCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _openIdConnectUserLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectUserLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the open ID connect user in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectUserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectUser the open ID connect user
	 * @return the open ID connect user that was updated
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectUser updateOpenIdConnectUser(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectUser openIdConnectUser) {

		return _openIdConnectUserLocalService.updateOpenIdConnectUser(
			openIdConnectUser);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _openIdConnectUserLocalService.getBasePersistence();
	}

	@Override
	public OpenIdConnectUserLocalService getWrappedService() {
		return _openIdConnectUserLocalService;
	}

	@Override
	public void setWrappedService(
		OpenIdConnectUserLocalService openIdConnectUserLocalService) {

		_openIdConnectUserLocalService = openIdConnectUserLocalService;
	}

	private OpenIdConnectUserLocalService _openIdConnectUserLocalService;

}