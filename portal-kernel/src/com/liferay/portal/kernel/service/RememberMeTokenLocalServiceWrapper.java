/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link RememberMeTokenLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RememberMeTokenLocalService
 * @generated
 */
public class RememberMeTokenLocalServiceWrapper
	implements RememberMeTokenLocalService,
			   ServiceWrapper<RememberMeTokenLocalService> {

	public RememberMeTokenLocalServiceWrapper() {
		this(null);
	}

	public RememberMeTokenLocalServiceWrapper(
		RememberMeTokenLocalService rememberMeTokenLocalService) {

		_rememberMeTokenLocalService = rememberMeTokenLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.RememberMeToken addRememberMeToken(
			long companyId, long userId, java.util.Date expirationDate,
			java.util.function.Consumer<String> valueConsumer)
		throws com.liferay.portal.kernel.exception.PwdEncryptorException {

		return _rememberMeTokenLocalService.addRememberMeToken(
			companyId, userId, expirationDate, valueConsumer);
	}

	/**
	 * Adds the remember me token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken addRememberMeToken(
		com.liferay.portal.kernel.model.RememberMeToken rememberMeToken) {

		return _rememberMeTokenLocalService.addRememberMeToken(rememberMeToken);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _rememberMeTokenLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new remember me token with the primary key. Does not add the remember me token to the database.
	 *
	 * @param rememberMeTokenId the primary key for the new remember me token
	 * @return the new remember me token
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken
		createRememberMeToken(long rememberMeTokenId) {

		return _rememberMeTokenLocalService.createRememberMeToken(
			rememberMeTokenId);
	}

	@Override
	public void deleteExpiredRememberMeTokens(java.util.Date expirationDate) {
		_rememberMeTokenLocalService.deleteExpiredRememberMeTokens(
			expirationDate);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _rememberMeTokenLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the remember me token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token that was removed
	 * @throws PortalException if a remember me token with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken
			deleteRememberMeToken(long rememberMeTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _rememberMeTokenLocalService.deleteRememberMeToken(
			rememberMeTokenId);
	}

	/**
	 * Deletes the remember me token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken
		deleteRememberMeToken(
			com.liferay.portal.kernel.model.RememberMeToken rememberMeToken) {

		return _rememberMeTokenLocalService.deleteRememberMeToken(
			rememberMeToken);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _rememberMeTokenLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _rememberMeTokenLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _rememberMeTokenLocalService.dynamicQuery();
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

		return _rememberMeTokenLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
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

		return _rememberMeTokenLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
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

		return _rememberMeTokenLocalService.dynamicQuery(
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

		return _rememberMeTokenLocalService.dynamicQueryCount(dynamicQuery);
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

		return _rememberMeTokenLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.RememberMeToken fetchRememberMeToken(
		long rememberMeTokenId) {

		return _rememberMeTokenLocalService.fetchRememberMeToken(
			rememberMeTokenId);
	}

	@Override
	public com.liferay.portal.kernel.model.RememberMeToken fetchRememberMeToken(
			long rememberMeTokenId, String value)
		throws com.liferay.portal.kernel.exception.PwdEncryptorException {

		return _rememberMeTokenLocalService.fetchRememberMeToken(
			rememberMeTokenId, value);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _rememberMeTokenLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _rememberMeTokenLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _rememberMeTokenLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _rememberMeTokenLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the remember me token with the primary key.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token
	 * @throws PortalException if a remember me token with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken getRememberMeToken(
			long rememberMeTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _rememberMeTokenLocalService.getRememberMeToken(
			rememberMeTokenId);
	}

	/**
	 * Returns a range of all the remember me tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @return the range of remember me tokens
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.RememberMeToken>
		getRememberMeTokens(int start, int end) {

		return _rememberMeTokenLocalService.getRememberMeTokens(start, end);
	}

	/**
	 * Returns the number of remember me tokens.
	 *
	 * @return the number of remember me tokens
	 */
	@Override
	public int getRememberMeTokensCount() {
		return _rememberMeTokenLocalService.getRememberMeTokensCount();
	}

	/**
	 * Updates the remember me token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RememberMeTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rememberMeToken the remember me token
	 * @return the remember me token that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.RememberMeToken
		updateRememberMeToken(
			com.liferay.portal.kernel.model.RememberMeToken rememberMeToken) {

		return _rememberMeTokenLocalService.updateRememberMeToken(
			rememberMeToken);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _rememberMeTokenLocalService.getBasePersistence();
	}

	@Override
	public RememberMeTokenLocalService getWrappedService() {
		return _rememberMeTokenLocalService;
	}

	@Override
	public void setWrappedService(
		RememberMeTokenLocalService rememberMeTokenLocalService) {

		_rememberMeTokenLocalService = rememberMeTokenLocalService;
	}

	private RememberMeTokenLocalService _rememberMeTokenLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:168792569