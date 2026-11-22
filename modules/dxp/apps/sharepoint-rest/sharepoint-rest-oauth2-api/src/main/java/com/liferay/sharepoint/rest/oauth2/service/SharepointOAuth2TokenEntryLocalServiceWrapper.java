/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.oauth2.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SharepointOAuth2TokenEntryLocalService}.
 *
 * @author Adolfo Pérez
 * @see SharepointOAuth2TokenEntryLocalService
 * @generated
 */
public class SharepointOAuth2TokenEntryLocalServiceWrapper
	implements ServiceWrapper<SharepointOAuth2TokenEntryLocalService>,
			   SharepointOAuth2TokenEntryLocalService {

	public SharepointOAuth2TokenEntryLocalServiceWrapper() {
		this(null);
	}

	public SharepointOAuth2TokenEntryLocalServiceWrapper(
		SharepointOAuth2TokenEntryLocalService
			sharepointOAuth2TokenEntryLocalService) {

		_sharepointOAuth2TokenEntryLocalService =
			sharepointOAuth2TokenEntryLocalService;
	}

	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
			addSharepointOAuth2TokenEntry(
				long userId, String configurationPid, String accessToken,
				String refreshToken, java.util.Date expirationDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.
			addSharepointOAuth2TokenEntry(
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
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		addSharepointOAuth2TokenEntry(
			com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
				sharepointOAuth2TokenEntry) {

		return _sharepointOAuth2TokenEntryLocalService.
			addSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new sharepoint o auth2 token entry with the primary key. Does not add the sharepoint o auth2 token entry to the database.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key for the new sharepoint o auth2 token entry
	 * @return the new sharepoint o auth2 token entry
	 */
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		createSharepointOAuth2TokenEntry(long sharepointOAuth2TokenEntryId) {

		return _sharepointOAuth2TokenEntryLocalService.
			createSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.deletePersistedModel(
			persistedModel);
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
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
			deleteSharepointOAuth2TokenEntry(long sharepointOAuth2TokenEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.
			deleteSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntryId);
	}

	@Override
	public void deleteSharepointOAuth2TokenEntry(
			long userId, String configurationPid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_sharepointOAuth2TokenEntryLocalService.
			deleteSharepointOAuth2TokenEntry(userId, configurationPid);
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
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		deleteSharepointOAuth2TokenEntry(
			com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
				sharepointOAuth2TokenEntry) {

		return _sharepointOAuth2TokenEntryLocalService.
			deleteSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntry);
	}

	@Override
	public void deleteUserSharepointOAuth2TokenEntries(long userId) {
		_sharepointOAuth2TokenEntryLocalService.
			deleteUserSharepointOAuth2TokenEntries(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _sharepointOAuth2TokenEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _sharepointOAuth2TokenEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _sharepointOAuth2TokenEntryLocalService.dynamicQuery();
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

		return _sharepointOAuth2TokenEntryLocalService.dynamicQuery(
			dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _sharepointOAuth2TokenEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _sharepointOAuth2TokenEntryLocalService.dynamicQuery(
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

		return _sharepointOAuth2TokenEntryLocalService.dynamicQueryCount(
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

		return _sharepointOAuth2TokenEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		fetchSharepointOAuth2TokenEntry(long sharepointOAuth2TokenEntryId) {

		return _sharepointOAuth2TokenEntryLocalService.
			fetchSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntryId);
	}

	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		fetchSharepointOAuth2TokenEntry(long userId, String configurationPid) {

		return _sharepointOAuth2TokenEntryLocalService.
			fetchSharepointOAuth2TokenEntry(userId, configurationPid);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _sharepointOAuth2TokenEntryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _sharepointOAuth2TokenEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _sharepointOAuth2TokenEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public java.util.List
		<com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry>
			getSharepointOAuth2TokenEntries(int start, int end) {

		return _sharepointOAuth2TokenEntryLocalService.
			getSharepointOAuth2TokenEntries(start, end);
	}

	/**
	 * Returns the number of sharepoint o auth2 token entries.
	 *
	 * @return the number of sharepoint o auth2 token entries
	 */
	@Override
	public int getSharepointOAuth2TokenEntriesCount() {
		return _sharepointOAuth2TokenEntryLocalService.
			getSharepointOAuth2TokenEntriesCount();
	}

	/**
	 * Returns the sharepoint o auth2 token entry with the primary key.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry
	 * @throws PortalException if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
			getSharepointOAuth2TokenEntry(long sharepointOAuth2TokenEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _sharepointOAuth2TokenEntryLocalService.
			getSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntryId);
	}

	@Override
	public int getUserSharepointOAuth2TokenEntriesCount(long userId) {
		return _sharepointOAuth2TokenEntryLocalService.
			getUserSharepointOAuth2TokenEntriesCount(userId);
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
	@Override
	public com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
		updateSharepointOAuth2TokenEntry(
			com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry
				sharepointOAuth2TokenEntry) {

		return _sharepointOAuth2TokenEntryLocalService.
			updateSharepointOAuth2TokenEntry(sharepointOAuth2TokenEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _sharepointOAuth2TokenEntryLocalService.getBasePersistence();
	}

	@Override
	public SharepointOAuth2TokenEntryLocalService getWrappedService() {
		return _sharepointOAuth2TokenEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SharepointOAuth2TokenEntryLocalService
			sharepointOAuth2TokenEntryLocalService) {

		_sharepointOAuth2TokenEntryLocalService =
			sharepointOAuth2TokenEntryLocalService;
	}

	private SharepointOAuth2TokenEntryLocalService
		_sharepointOAuth2TokenEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-238136631