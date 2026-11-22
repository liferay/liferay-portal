/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.oauth2.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SharepointOAuth2TokenEntry. This utility wraps
 * <code>com.liferay.sharepoint.rest.oauth2.service.impl.SharepointOAuth2TokenEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Adolfo Pérez
 * @see SharepointOAuth2TokenEntryLocalService
 * @generated
 */
public class SharepointOAuth2TokenEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.sharepoint.rest.oauth2.service.impl.SharepointOAuth2TokenEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static SharepointOAuth2TokenEntry addSharepointOAuth2TokenEntry(
			long userId, String configurationPid, String accessToken,
			String refreshToken, java.util.Date expirationDate)
		throws PortalException {

		return getService().addSharepointOAuth2TokenEntry(
			userId, configurationPid, accessToken, refreshToken,
			expirationDate);
	}

	/**
	 * Adds the sharepoint o auth2 token entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharepointOAuth2TokenEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharepointOAuth2TokenEntry the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry that was added
	 */
	public static SharepointOAuth2TokenEntry addSharepointOAuth2TokenEntry(
		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry) {

		return getService().addSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntry);
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
	 * Creates a new sharepoint o auth2 token entry with the primary key. Does not add the sharepoint o auth2 token entry to the database.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key for the new sharepoint o auth2 token entry
	 * @return the new sharepoint o auth2 token entry
	 */
	public static SharepointOAuth2TokenEntry createSharepointOAuth2TokenEntry(
		long sharepointOAuth2TokenEntryId) {

		return getService().createSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the sharepoint o auth2 token entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharepointOAuth2TokenEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry that was removed
	 * @throws PortalException if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	public static SharepointOAuth2TokenEntry deleteSharepointOAuth2TokenEntry(
			long sharepointOAuth2TokenEntryId)
		throws PortalException {

		return getService().deleteSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntryId);
	}

	public static void deleteSharepointOAuth2TokenEntry(
			long userId, String configurationPid)
		throws PortalException {

		getService().deleteSharepointOAuth2TokenEntry(userId, configurationPid);
	}

	/**
	 * Deletes the sharepoint o auth2 token entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharepointOAuth2TokenEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharepointOAuth2TokenEntry the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry that was removed
	 */
	public static SharepointOAuth2TokenEntry deleteSharepointOAuth2TokenEntry(
		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry) {

		return getService().deleteSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntry);
	}

	public static void deleteUserSharepointOAuth2TokenEntries(long userId) {
		getService().deleteUserSharepointOAuth2TokenEntries(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharepoint.rest.oauth2.model.impl.SharepointOAuth2TokenEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharepoint.rest.oauth2.model.impl.SharepointOAuth2TokenEntryModelImpl</code>.
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

	public static SharepointOAuth2TokenEntry fetchSharepointOAuth2TokenEntry(
		long sharepointOAuth2TokenEntryId) {

		return getService().fetchSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntryId);
	}

	public static SharepointOAuth2TokenEntry fetchSharepointOAuth2TokenEntry(
		long userId, String configurationPid) {

		return getService().fetchSharepointOAuth2TokenEntry(
			userId, configurationPid);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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

	/**
	 * Returns a range of all the sharepoint o auth2 token entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharepoint.rest.oauth2.model.impl.SharepointOAuth2TokenEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sharepoint o auth2 token entries
	 * @param end the upper bound of the range of sharepoint o auth2 token entries (not inclusive)
	 * @return the range of sharepoint o auth2 token entries
	 */
	public static List<SharepointOAuth2TokenEntry>
		getSharepointOAuth2TokenEntries(int start, int end) {

		return getService().getSharepointOAuth2TokenEntries(start, end);
	}

	/**
	 * Returns the number of sharepoint o auth2 token entries.
	 *
	 * @return the number of sharepoint o auth2 token entries
	 */
	public static int getSharepointOAuth2TokenEntriesCount() {
		return getService().getSharepointOAuth2TokenEntriesCount();
	}

	/**
	 * Returns the sharepoint o auth2 token entry with the primary key.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry
	 * @throws PortalException if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	public static SharepointOAuth2TokenEntry getSharepointOAuth2TokenEntry(
			long sharepointOAuth2TokenEntryId)
		throws PortalException {

		return getService().getSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntryId);
	}

	public static int getUserSharepointOAuth2TokenEntriesCount(long userId) {
		return getService().getUserSharepointOAuth2TokenEntriesCount(userId);
	}

	/**
	 * Updates the sharepoint o auth2 token entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharepointOAuth2TokenEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharepointOAuth2TokenEntry the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry that was updated
	 */
	public static SharepointOAuth2TokenEntry updateSharepointOAuth2TokenEntry(
		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry) {

		return getService().updateSharepointOAuth2TokenEntry(
			sharepointOAuth2TokenEntry);
	}

	public static SharepointOAuth2TokenEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SharepointOAuth2TokenEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			SharepointOAuth2TokenEntryLocalServiceUtil.class,
			SharepointOAuth2TokenEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1852000999