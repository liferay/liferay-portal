/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link FaroChannelLocalService}.
 *
 * @author Matthew Kong
 * @see FaroChannelLocalService
 * @generated
 */
public class FaroChannelLocalServiceWrapper
	implements FaroChannelLocalService,
			   ServiceWrapper<FaroChannelLocalService> {

	public FaroChannelLocalServiceWrapper() {
		this(null);
	}

	public FaroChannelLocalServiceWrapper(
		FaroChannelLocalService faroChannelLocalService) {

		_faroChannelLocalService = faroChannelLocalService;
	}

	/**
	 * Adds the faro channel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroChannel the faro channel
	 * @return the faro channel that was added
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel addFaroChannel(
		com.liferay.osb.faro.model.FaroChannel faroChannel) {

		return _faroChannelLocalService.addFaroChannel(faroChannel);
	}

	@Override
	public com.liferay.osb.faro.model.FaroChannel addFaroChannel(
			long userId, String name, String channelId, long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.addFaroChannel(
			userId, name, channelId, workspaceGroupId);
	}

	@Override
	public void addUsers(
			long companyId, String channelId,
			java.util.List<Long> invitedUserIds, long userId,
			long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_faroChannelLocalService.addUsers(
			companyId, channelId, invitedUserIds, userId, workspaceGroupId);
	}

	/**
	 * Creates a new faro channel with the primary key. Does not add the faro channel to the database.
	 *
	 * @param faroChannelId the primary key for the new faro channel
	 * @return the new faro channel
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel createFaroChannel(
		long faroChannelId) {

		return _faroChannelLocalService.createFaroChannel(faroChannelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the faro channel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroChannel the faro channel
	 * @return the faro channel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel deleteFaroChannel(
			com.liferay.osb.faro.model.FaroChannel faroChannel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.deleteFaroChannel(faroChannel);
	}

	/**
	 * Deletes the faro channel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel that was removed
	 * @throws PortalException if a faro channel with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel deleteFaroChannel(
			long faroChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.deleteFaroChannel(faroChannelId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroChannel deleteFaroChannel(
			String channelId, long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.deleteFaroChannel(
			channelId, workspaceGroupId);
	}

	@Override
	public void deleteFaroChannels(long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_faroChannelLocalService.deleteFaroChannels(workspaceGroupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _faroChannelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _faroChannelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _faroChannelLocalService.dynamicQuery();
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

		return _faroChannelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroChannelModelImpl</code>.
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

		return _faroChannelLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroChannelModelImpl</code>.
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

		return _faroChannelLocalService.dynamicQuery(
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

		return _faroChannelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _faroChannelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.osb.faro.model.FaroChannel fetchFaroChannel(
		long faroChannelId) {

		return _faroChannelLocalService.fetchFaroChannel(faroChannelId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroChannel fetchFaroChannel(
		String channelId, long workspaceGroupId) {

		return _faroChannelLocalService.fetchFaroChannel(
			channelId, workspaceGroupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _faroChannelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the faro channel with the primary key.
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel
	 * @throws PortalException if a faro channel with the primary key could not be found
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel getFaroChannel(
			long faroChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.getFaroChannel(faroChannelId);
	}

	@Override
	public com.liferay.osb.faro.model.FaroChannel getFaroChannel(
			String channelId, long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.getFaroChannel(
			channelId, workspaceGroupId);
	}

	/**
	 * Returns a range of all the faro channels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroChannelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of faro channels
	 * @param end the upper bound of the range of faro channels (not inclusive)
	 * @return the range of faro channels
	 */
	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroChannel>
		getFaroChannels(int start, int end) {

		return _faroChannelLocalService.getFaroChannels(start, end);
	}

	/**
	 * Returns the number of faro channels.
	 *
	 * @return the number of faro channels
	 */
	@Override
	public int getFaroChannelsCount() {
		return _faroChannelLocalService.getFaroChannelsCount();
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroUser> getFaroUsers(
			String channelId, boolean available, String query,
			java.util.List<Integer> statuses, long workspaceGroupId, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.osb.faro.model.FaroUser> orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.getFaroUsers(
			channelId, available, query, statuses, workspaceGroupId, start, end,
			orderByComparator);
	}

	@Override
	public int getFaroUsersCount(
			String channelId, boolean available, String query,
			java.util.List<Integer> statuses, long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.getFaroUsersCount(
			channelId, available, query, statuses, workspaceGroupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _faroChannelLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _faroChannelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _faroChannelLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void removeUsers(
			String channelId, java.util.List<Long> userIds,
			long workspaceGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_faroChannelLocalService.removeUsers(
			channelId, userIds, workspaceGroupId);
	}

	@Override
	public java.util.List<com.liferay.osb.faro.model.FaroChannel> search(
		long groupId, String query, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.osb.faro.model.FaroChannel> orderByComparator) {

		return _faroChannelLocalService.search(
			groupId, query, start, end, orderByComparator);
	}

	@Override
	public int searchCount(long groupId, String query) {
		return _faroChannelLocalService.searchCount(groupId, query);
	}

	/**
	 * Updates the faro channel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FaroChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param faroChannel the faro channel
	 * @return the faro channel that was updated
	 */
	@Override
	public com.liferay.osb.faro.model.FaroChannel updateFaroChannel(
		com.liferay.osb.faro.model.FaroChannel faroChannel) {

		return _faroChannelLocalService.updateFaroChannel(faroChannel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _faroChannelLocalService.getBasePersistence();
	}

	@Override
	public FaroChannelLocalService getWrappedService() {
		return _faroChannelLocalService;
	}

	@Override
	public void setWrappedService(
		FaroChannelLocalService faroChannelLocalService) {

		_faroChannelLocalService = faroChannelLocalService;
	}

	private FaroChannelLocalService _faroChannelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1105062712