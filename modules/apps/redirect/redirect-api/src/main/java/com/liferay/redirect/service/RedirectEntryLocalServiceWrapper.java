/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link RedirectEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RedirectEntryLocalService
 * @generated
 */
public class RedirectEntryLocalServiceWrapper
	implements RedirectEntryLocalService,
			   ServiceWrapper<RedirectEntryLocalService> {

	public RedirectEntryLocalServiceWrapper() {
		this(null);
	}

	public RedirectEntryLocalServiceWrapper(
		RedirectEntryLocalService redirectEntryLocalService) {

		_redirectEntryLocalService = redirectEntryLocalService;
	}

	@Override
	public void addEntryResources(
			com.liferay.redirect.model.RedirectEntry entry,
			boolean addGroupPermissions, boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_redirectEntryLocalService.addEntryResources(
			entry, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addEntryResources(
			com.liferay.redirect.model.RedirectEntry entry,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_redirectEntryLocalService.addEntryResources(entry, modelPermissions);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, java.util.Date expirationDate,
			boolean permanent, String sourceURL,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.addRedirectEntry(
			groupId, destinationURL, expirationDate, permanent, sourceURL,
			serviceContext);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry addRedirectEntry(
			long groupId, String destinationURL, java.util.Date expirationDate,
			String groupBaseURL, boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.addRedirectEntry(
			groupId, destinationURL, expirationDate, groupBaseURL, permanent,
			sourceURL, updateChainedRedirectEntries, serviceContext);
	}

	/**
	 * Adds the redirect entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was added
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry addRedirectEntry(
		com.liferay.redirect.model.RedirectEntry redirectEntry) {

		return _redirectEntryLocalService.addRedirectEntry(redirectEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new redirect entry with the primary key. Does not add the redirect entry to the database.
	 *
	 * @param redirectEntryId the primary key for the new redirect entry
	 * @return the new redirect entry
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry createRedirectEntry(
		long redirectEntryId) {

		return _redirectEntryLocalService.createRedirectEntry(redirectEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRedirectEntries(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_redirectEntryLocalService.deleteRedirectEntries(groupId);
	}

	/**
	 * Deletes the redirect entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry that was removed
	 * @throws PortalException if a redirect entry with the primary key could not be found
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry deleteRedirectEntry(
			long redirectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.deleteRedirectEntry(redirectEntryId);
	}

	/**
	 * Deletes the redirect entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry deleteRedirectEntry(
			com.liferay.redirect.model.RedirectEntry redirectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.deleteRedirectEntry(redirectEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _redirectEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _redirectEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _redirectEntryLocalService.dynamicQuery();
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

		return _redirectEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
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

		return _redirectEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
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

		return _redirectEntryLocalService.dynamicQuery(
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

		return _redirectEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _redirectEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry fetchRedirectEntry(
		long redirectEntryId) {

		return _redirectEntryLocalService.fetchRedirectEntry(redirectEntryId);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry fetchRedirectEntry(
		long groupId, String sourceURL) {

		return _redirectEntryLocalService.fetchRedirectEntry(
			groupId, sourceURL);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry fetchRedirectEntry(
		long groupId, String sourceURL, boolean updateLastOccurrenceDate) {

		return _redirectEntryLocalService.fetchRedirectEntry(
			groupId, sourceURL, updateLastOccurrenceDate);
	}

	/**
	 * Returns the redirect entry matching the UUID and group.
	 *
	 * @param uuid the redirect entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching redirect entry, or <code>null</code> if a matching redirect entry could not be found
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry
		fetchRedirectEntryByUuidAndGroupId(String uuid, long groupId) {

		return _redirectEntryLocalService.fetchRedirectEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _redirectEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _redirectEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _redirectEntryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _redirectEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns a range of all the redirect entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.redirect.model.impl.RedirectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @return the range of redirect entries
	 */
	@Override
	public java.util.List<com.liferay.redirect.model.RedirectEntry>
		getRedirectEntries(int start, int end) {

		return _redirectEntryLocalService.getRedirectEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.redirect.model.RedirectEntry>
		getRedirectEntries(long groupId, String destinationURL) {

		return _redirectEntryLocalService.getRedirectEntries(
			groupId, destinationURL);
	}

	/**
	 * Returns all the redirect entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the redirect entries
	 * @param companyId the primary key of the company
	 * @return the matching redirect entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.redirect.model.RedirectEntry>
		getRedirectEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return _redirectEntryLocalService.getRedirectEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of redirect entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the redirect entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of redirect entries
	 * @param end the upper bound of the range of redirect entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching redirect entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.redirect.model.RedirectEntry>
		getRedirectEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.redirect.model.RedirectEntry> orderByComparator) {

		return _redirectEntryLocalService.getRedirectEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of redirect entries.
	 *
	 * @return the number of redirect entries
	 */
	@Override
	public int getRedirectEntriesCount() {
		return _redirectEntryLocalService.getRedirectEntriesCount();
	}

	@Override
	public int getRedirectEntriesCount(long groupId) {
		return _redirectEntryLocalService.getRedirectEntriesCount(groupId);
	}

	/**
	 * Returns the redirect entry with the primary key.
	 *
	 * @param redirectEntryId the primary key of the redirect entry
	 * @return the redirect entry
	 * @throws PortalException if a redirect entry with the primary key could not be found
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry getRedirectEntry(
			long redirectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.getRedirectEntry(redirectEntryId);
	}

	/**
	 * Returns the redirect entry matching the UUID and group.
	 *
	 * @param uuid the redirect entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching redirect entry
	 * @throws PortalException if a matching redirect entry could not be found
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry
			getRedirectEntryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.getRedirectEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL,
			java.util.Date expirationDate, boolean permanent, String sourceURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.updateRedirectEntry(
			redirectEntryId, destinationURL, expirationDate, permanent,
			sourceURL);
	}

	@Override
	public com.liferay.redirect.model.RedirectEntry updateRedirectEntry(
			long redirectEntryId, String destinationURL,
			java.util.Date expirationDate, String groupBaseURL,
			boolean permanent, String sourceURL,
			boolean updateChainedRedirectEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _redirectEntryLocalService.updateRedirectEntry(
			redirectEntryId, destinationURL, expirationDate, groupBaseURL,
			permanent, sourceURL, updateChainedRedirectEntries);
	}

	/**
	 * Updates the redirect entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RedirectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param redirectEntry the redirect entry
	 * @return the redirect entry that was updated
	 */
	@Override
	public com.liferay.redirect.model.RedirectEntry updateRedirectEntry(
		com.liferay.redirect.model.RedirectEntry redirectEntry) {

		return _redirectEntryLocalService.updateRedirectEntry(redirectEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _redirectEntryLocalService.getBasePersistence();
	}

	@Override
	public RedirectEntryLocalService getWrappedService() {
		return _redirectEntryLocalService;
	}

	@Override
	public void setWrappedService(
		RedirectEntryLocalService redirectEntryLocalService) {

		_redirectEntryLocalService = redirectEntryLocalService;
	}

	private RedirectEntryLocalService _redirectEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-742217886