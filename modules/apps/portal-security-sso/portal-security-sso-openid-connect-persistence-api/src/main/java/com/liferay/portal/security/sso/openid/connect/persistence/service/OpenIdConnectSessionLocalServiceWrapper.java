/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link OpenIdConnectSessionLocalService}.
 *
 * @author Arthur Chan
 * @see OpenIdConnectSessionLocalService
 * @generated
 */
public class OpenIdConnectSessionLocalServiceWrapper
	implements OpenIdConnectSessionLocalService,
			   ServiceWrapper<OpenIdConnectSessionLocalService> {

	public OpenIdConnectSessionLocalServiceWrapper() {
		this(null);
	}

	public OpenIdConnectSessionLocalServiceWrapper(
		OpenIdConnectSessionLocalService openIdConnectSessionLocalService) {

		_openIdConnectSessionLocalService = openIdConnectSessionLocalService;
	}

	/**
	 * Adds the open ID connect session to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectSession the open ID connect session
	 * @return the open ID connect session that was added
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession addOpenIdConnectSession(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectSession openIdConnectSession) {

		return _openIdConnectSessionLocalService.addOpenIdConnectSession(
			openIdConnectSession);
	}

	/**
	 * Creates a new open ID connect session with the primary key. Does not add the open ID connect session to the database.
	 *
	 * @param openIdConnectSessionId the primary key for the new open ID connect session
	 * @return the new open ID connect session
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession createOpenIdConnectSession(
			long openIdConnectSessionId) {

		return _openIdConnectSessionLocalService.createOpenIdConnectSession(
			openIdConnectSessionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectSessionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the open ID connect session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session that was removed
	 * @throws PortalException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession deleteOpenIdConnectSession(
				long openIdConnectSessionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectSessionLocalService.deleteOpenIdConnectSession(
			openIdConnectSessionId);
	}

	/**
	 * Deletes the open ID connect session from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectSession the open ID connect session
	 * @return the open ID connect session that was removed
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession deleteOpenIdConnectSession(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectSession openIdConnectSession) {

		return _openIdConnectSessionLocalService.deleteOpenIdConnectSession(
			openIdConnectSession);
	}

	@Override
	public void deleteOpenIdConnectSessions(long userId) {
		_openIdConnectSessionLocalService.deleteOpenIdConnectSessions(userId);
	}

	@Override
	public void deleteOpenIdConnectSessions(
		long companyId, String authServerWellKnownURI, String clientId) {

		_openIdConnectSessionLocalService.deleteOpenIdConnectSessions(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectSessionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _openIdConnectSessionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _openIdConnectSessionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _openIdConnectSessionLocalService.dynamicQuery();
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

		return _openIdConnectSessionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionModelImpl</code>.
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

		return _openIdConnectSessionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionModelImpl</code>.
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

		return _openIdConnectSessionLocalService.dynamicQuery(
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

		return _openIdConnectSessionLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _openIdConnectSessionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession fetchCurrentOpenIdConnectSession() {

		return _openIdConnectSessionLocalService.
			fetchCurrentOpenIdConnectSession();
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession fetchOpenIdConnectSession(
			long openIdConnectSessionId) {

		return _openIdConnectSessionLocalService.fetchOpenIdConnectSession(
			openIdConnectSessionId);
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession fetchOpenIdConnectSession(
			long userId, String authServerWellKnownURI, String clientId) {

		return _openIdConnectSessionLocalService.fetchOpenIdConnectSession(
			userId, authServerWellKnownURI, clientId);
	}

	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession fetchOpenIdConnectSession(
			String authServerWellKnownURI, String sid) {

		return _openIdConnectSessionLocalService.fetchOpenIdConnectSession(
			authServerWellKnownURI, sid);
	}

	@Override
	public java.util.List
		<com.liferay.portal.security.sso.openid.connect.persistence.model.
			OpenIdConnectSession>
				getAccessTokenExpirationDateOpenIdConnectSessions(
					java.util.Date ltAccessTokenExpirationDate, int start,
					int end) {

		return _openIdConnectSessionLocalService.
			getAccessTokenExpirationDateOpenIdConnectSessions(
				ltAccessTokenExpirationDate, start, end);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _openIdConnectSessionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _openIdConnectSessionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the open ID connect session with the primary key.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session
	 * @throws PortalException if a open ID connect session with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession getOpenIdConnectSession(
				long openIdConnectSessionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectSessionLocalService.getOpenIdConnectSession(
			openIdConnectSessionId);
	}

	/**
	 * Returns a range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.security.sso.openid.connect.persistence.model.impl.OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of open ID connect sessions
	 */
	@Override
	public java.util.List
		<com.liferay.portal.security.sso.openid.connect.persistence.model.
			OpenIdConnectSession> getOpenIdConnectSessions(int start, int end) {

		return _openIdConnectSessionLocalService.getOpenIdConnectSessions(
			start, end);
	}

	/**
	 * Returns the number of open ID connect sessions.
	 *
	 * @return the number of open ID connect sessions
	 */
	@Override
	public int getOpenIdConnectSessionsCount() {
		return _openIdConnectSessionLocalService.
			getOpenIdConnectSessionsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _openIdConnectSessionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _openIdConnectSessionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the open ID connect session in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect OpenIdConnectSessionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param openIdConnectSession the open ID connect session
	 * @return the open ID connect session that was updated
	 */
	@Override
	public com.liferay.portal.security.sso.openid.connect.persistence.model.
		OpenIdConnectSession updateOpenIdConnectSession(
			com.liferay.portal.security.sso.openid.connect.persistence.model.
				OpenIdConnectSession openIdConnectSession) {

		return _openIdConnectSessionLocalService.updateOpenIdConnectSession(
			openIdConnectSession);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _openIdConnectSessionLocalService.getBasePersistence();
	}

	@Override
	public OpenIdConnectSessionLocalService getWrappedService() {
		return _openIdConnectSessionLocalService;
	}

	@Override
	public void setWrappedService(
		OpenIdConnectSessionLocalService openIdConnectSessionLocalService) {

		_openIdConnectSessionLocalService = openIdConnectSessionLocalService;
	}

	private OpenIdConnectSessionLocalService _openIdConnectSessionLocalService;

}