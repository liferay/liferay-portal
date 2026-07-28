/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service;

import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FaroChannel. This utility wraps
 * <code>com.liferay.osb.faro.service.impl.FaroChannelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Matthew Kong
 * @see FaroChannelLocalService
 * @generated
 */
public class FaroChannelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.faro.service.impl.FaroChannelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static FaroChannel addFaroChannel(FaroChannel faroChannel) {
		return getService().addFaroChannel(faroChannel);
	}

	public static FaroChannel addFaroChannel(
			long userId, String name, String channelId, long workspaceGroupId)
		throws PortalException {

		return getService().addFaroChannel(
			userId, name, channelId, workspaceGroupId);
	}

	public static void addUsers(
			long companyId, String channelId, List<Long> invitedUserIds,
			long userId, long workspaceGroupId)
		throws PortalException {

		getService().addUsers(
			companyId, channelId, invitedUserIds, userId, workspaceGroupId);
	}

	/**
	 * Creates a new faro channel with the primary key. Does not add the faro channel to the database.
	 *
	 * @param faroChannelId the primary key for the new faro channel
	 * @return the new faro channel
	 */
	public static FaroChannel createFaroChannel(long faroChannelId) {
		return getService().createFaroChannel(faroChannelId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static FaroChannel deleteFaroChannel(FaroChannel faroChannel)
		throws PortalException {

		return getService().deleteFaroChannel(faroChannel);
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
	public static FaroChannel deleteFaroChannel(long faroChannelId)
		throws PortalException {

		return getService().deleteFaroChannel(faroChannelId);
	}

	public static FaroChannel deleteFaroChannel(
			String channelId, long workspaceGroupId)
		throws PortalException {

		return getService().deleteFaroChannel(channelId, workspaceGroupId);
	}

	public static void deleteFaroChannels(long workspaceGroupId)
		throws PortalException {

		getService().deleteFaroChannels(workspaceGroupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static FaroChannel fetchFaroChannel(long faroChannelId) {
		return getService().fetchFaroChannel(faroChannelId);
	}

	public static FaroChannel fetchFaroChannel(
		String channelId, long workspaceGroupId) {

		return getService().fetchFaroChannel(channelId, workspaceGroupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the faro channel with the primary key.
	 *
	 * @param faroChannelId the primary key of the faro channel
	 * @return the faro channel
	 * @throws PortalException if a faro channel with the primary key could not be found
	 */
	public static FaroChannel getFaroChannel(long faroChannelId)
		throws PortalException {

		return getService().getFaroChannel(faroChannelId);
	}

	public static FaroChannel getFaroChannel(
			String channelId, long workspaceGroupId)
		throws PortalException {

		return getService().getFaroChannel(channelId, workspaceGroupId);
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
	public static List<FaroChannel> getFaroChannels(int start, int end) {
		return getService().getFaroChannels(start, end);
	}

	/**
	 * Returns the number of faro channels.
	 *
	 * @return the number of faro channels
	 */
	public static int getFaroChannelsCount() {
		return getService().getFaroChannelsCount();
	}

	public static List<com.liferay.osb.faro.model.FaroUser> getFaroUsers(
			String channelId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId, int start, int end,
			OrderByComparator<com.liferay.osb.faro.model.FaroUser>
				orderByComparator)
		throws PortalException {

		return getService().getFaroUsers(
			channelId, available, query, statuses, workspaceGroupId, start, end,
			orderByComparator);
	}

	public static int getFaroUsersCount(
			String channelId, boolean available, String query,
			List<Integer> statuses, long workspaceGroupId)
		throws PortalException {

		return getService().getFaroUsersCount(
			channelId, available, query, statuses, workspaceGroupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static void removeUsers(
			String channelId, List<Long> userIds, long workspaceGroupId)
		throws PortalException {

		getService().removeUsers(channelId, userIds, workspaceGroupId);
	}

	public static List<FaroChannel> search(
		long groupId, String query, int start, int end,
		OrderByComparator<FaroChannel> orderByComparator) {

		return getService().search(
			groupId, query, start, end, orderByComparator);
	}

	public static int searchCount(long groupId, String query) {
		return getService().searchCount(groupId, query);
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
	public static FaroChannel updateFaroChannel(FaroChannel faroChannel) {
		return getService().updateFaroChannel(faroChannel);
	}

	public static FaroChannelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FaroChannelLocalService> _serviceSnapshot =
		new Snapshot<>(
			FaroChannelLocalServiceUtil.class, FaroChannelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:430529940