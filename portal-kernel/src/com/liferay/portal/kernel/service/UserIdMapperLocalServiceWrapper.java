/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UserIdMapperLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserIdMapperLocalService
 * @generated
 */
public class UserIdMapperLocalServiceWrapper
	implements ServiceWrapper<UserIdMapperLocalService>,
			   UserIdMapperLocalService {

	public UserIdMapperLocalServiceWrapper() {
		this(null);
	}

	public UserIdMapperLocalServiceWrapper(
		UserIdMapperLocalService userIdMapperLocalService) {

		_userIdMapperLocalService = userIdMapperLocalService;
	}

	/**
	 * Adds the user ID mapper to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserIdMapperLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userIdMapper the user ID mapper
	 * @return the user ID mapper that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper addUserIdMapper(
		com.liferay.portal.kernel.model.UserIdMapper userIdMapper) {

		return _userIdMapperLocalService.addUserIdMapper(userIdMapper);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user ID mapper with the primary key. Does not add the user ID mapper to the database.
	 *
	 * @param userIdMapperId the primary key for the new user ID mapper
	 * @return the new user ID mapper
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper createUserIdMapper(
		long userIdMapperId) {

		return _userIdMapperLocalService.createUserIdMapper(userIdMapperId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the user ID mapper with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserIdMapperLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userIdMapperId the primary key of the user ID mapper
	 * @return the user ID mapper that was removed
	 * @throws PortalException if a user ID mapper with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper deleteUserIdMapper(
			long userIdMapperId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.deleteUserIdMapper(userIdMapperId);
	}

	/**
	 * Deletes the user ID mapper from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserIdMapperLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userIdMapper the user ID mapper
	 * @return the user ID mapper that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper deleteUserIdMapper(
		com.liferay.portal.kernel.model.UserIdMapper userIdMapper) {

		return _userIdMapperLocalService.deleteUserIdMapper(userIdMapper);
	}

	@Override
	public void deleteUserIdMappers(long userId) {
		_userIdMapperLocalService.deleteUserIdMappers(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userIdMapperLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userIdMapperLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userIdMapperLocalService.dynamicQuery();
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

		return _userIdMapperLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserIdMapperModelImpl</code>.
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

		return _userIdMapperLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserIdMapperModelImpl</code>.
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

		return _userIdMapperLocalService.dynamicQuery(
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

		return _userIdMapperLocalService.dynamicQueryCount(dynamicQuery);
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

		return _userIdMapperLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.UserIdMapper fetchUserIdMapper(
		long userIdMapperId) {

		return _userIdMapperLocalService.fetchUserIdMapper(userIdMapperId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userIdMapperLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userIdMapperLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userIdMapperLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the user ID mapper with the primary key.
	 *
	 * @param userIdMapperId the primary key of the user ID mapper
	 * @return the user ID mapper
	 * @throws PortalException if a user ID mapper with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper getUserIdMapper(
			long userIdMapperId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.getUserIdMapper(userIdMapperId);
	}

	@Override
	public com.liferay.portal.kernel.model.UserIdMapper
			getUserIdMapperByExternalUserId(String type, String externalUserId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userIdMapperLocalService.getUserIdMapperByExternalUserId(
			type, externalUserId);
	}

	/**
	 * Returns a range of all the user ID mappers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserIdMapperModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user ID mappers
	 * @param end the upper bound of the range of user ID mappers (not inclusive)
	 * @return the range of user ID mappers
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserIdMapper>
		getUserIdMappers(int start, int end) {

		return _userIdMapperLocalService.getUserIdMappers(start, end);
	}

	/**
	 * Returns the number of user ID mappers.
	 *
	 * @return the number of user ID mappers
	 */
	@Override
	public int getUserIdMappersCount() {
		return _userIdMapperLocalService.getUserIdMappersCount();
	}

	@Override
	public com.liferay.portal.kernel.model.UserIdMapper updateUserIdMapper(
		long userId, String type, String description, String externalUserId) {

		return _userIdMapperLocalService.updateUserIdMapper(
			userId, type, description, externalUserId);
	}

	/**
	 * Updates the user ID mapper in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserIdMapperLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userIdMapper the user ID mapper
	 * @return the user ID mapper that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.UserIdMapper updateUserIdMapper(
		com.liferay.portal.kernel.model.UserIdMapper userIdMapper) {

		return _userIdMapperLocalService.updateUserIdMapper(userIdMapper);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userIdMapperLocalService.getBasePersistence();
	}

	@Override
	public UserIdMapperLocalService getWrappedService() {
		return _userIdMapperLocalService;
	}

	@Override
	public void setWrappedService(
		UserIdMapperLocalService userIdMapperLocalService) {

		_userIdMapperLocalService = userIdMapperLocalService;
	}

	private UserIdMapperLocalService _userIdMapperLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:343282351