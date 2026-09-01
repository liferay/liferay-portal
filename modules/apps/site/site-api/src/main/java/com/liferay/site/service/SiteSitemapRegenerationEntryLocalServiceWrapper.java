/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link SiteSitemapRegenerationEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntryLocalService
 * @generated
 */
public class SiteSitemapRegenerationEntryLocalServiceWrapper
	implements ServiceWrapper<SiteSitemapRegenerationEntryLocalService>,
			   SiteSitemapRegenerationEntryLocalService {

	public SiteSitemapRegenerationEntryLocalServiceWrapper() {
		this(null);
	}

	public SiteSitemapRegenerationEntryLocalServiceWrapper(
		SiteSitemapRegenerationEntryLocalService
			siteSitemapRegenerationEntryLocalService) {

		_siteSitemapRegenerationEntryLocalService =
			siteSitemapRegenerationEntryLocalService;
	}

	/**
	 * Adds the site sitemap regeneration entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteSitemapRegenerationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteSitemapRegenerationEntry the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was added
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		addSiteSitemapRegenerationEntry(
			com.liferay.site.model.SiteSitemapRegenerationEntry
				siteSitemapRegenerationEntry) {

		return _siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(siteSitemapRegenerationEntry);
	}

	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		addSiteSitemapRegenerationEntry(
			String assetTypeKey, long companyId, long groupId) {

		return _siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(assetTypeKey, companyId, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteSitemapRegenerationEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new site sitemap regeneration entry with the primary key. Does not add the site sitemap regeneration entry to the database.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key for the new site sitemap regeneration entry
	 * @return the new site sitemap regeneration entry
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		createSiteSitemapRegenerationEntry(
			long siteSitemapRegenerationEntryId) {

		return _siteSitemapRegenerationEntryLocalService.
			createSiteSitemapRegenerationEntry(siteSitemapRegenerationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteSitemapRegenerationEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteSiteSitemapRegenerationEntries(long companyId) {
		_siteSitemapRegenerationEntryLocalService.
			deleteSiteSitemapRegenerationEntries(companyId);
	}

	/**
	 * Deletes the site sitemap regeneration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteSitemapRegenerationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was removed
	 * @throws PortalException if a site sitemap regeneration entry with the primary key could not be found
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
			deleteSiteSitemapRegenerationEntry(
				long siteSitemapRegenerationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteSitemapRegenerationEntryLocalService.
			deleteSiteSitemapRegenerationEntry(siteSitemapRegenerationEntryId);
	}

	/**
	 * Deletes the site sitemap regeneration entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteSitemapRegenerationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteSitemapRegenerationEntry the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was removed
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		deleteSiteSitemapRegenerationEntry(
			com.liferay.site.model.SiteSitemapRegenerationEntry
				siteSitemapRegenerationEntry) {

		return _siteSitemapRegenerationEntryLocalService.
			deleteSiteSitemapRegenerationEntry(siteSitemapRegenerationEntry);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _siteSitemapRegenerationEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _siteSitemapRegenerationEntryLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _siteSitemapRegenerationEntryLocalService.dynamicQuery();
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

		return _siteSitemapRegenerationEntryLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
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

		return _siteSitemapRegenerationEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
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

		return _siteSitemapRegenerationEntryLocalService.dynamicQuery(
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

		return _siteSitemapRegenerationEntryLocalService.dynamicQueryCount(
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

		return _siteSitemapRegenerationEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		fetchSiteSitemapRegenerationEntry(long siteSitemapRegenerationEntryId) {

		return _siteSitemapRegenerationEntryLocalService.
			fetchSiteSitemapRegenerationEntry(siteSitemapRegenerationEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _siteSitemapRegenerationEntryLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _siteSitemapRegenerationEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _siteSitemapRegenerationEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteSitemapRegenerationEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns a range of all the site sitemap regeneration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @return the range of site sitemap regeneration entries
	 */
	@Override
	public java.util.List<com.liferay.site.model.SiteSitemapRegenerationEntry>
		getSiteSitemapRegenerationEntries(int start, int end) {

		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.site.model.SiteSitemapRegenerationEntry>
		getSiteSitemapRegenerationEntries(long companyId) {

		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntries(companyId);
	}

	/**
	 * Returns the number of site sitemap regeneration entries.
	 *
	 * @return the number of site sitemap regeneration entries
	 */
	@Override
	public int getSiteSitemapRegenerationEntriesCount() {
		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntriesCount();
	}

	@Override
	public int getSiteSitemapRegenerationEntriesCount(long companyId) {
		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntriesCount(companyId);
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry
	 * @throws PortalException if a site sitemap regeneration entry with the primary key could not be found
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
			getSiteSitemapRegenerationEntry(long siteSitemapRegenerationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntry(siteSitemapRegenerationEntryId);
	}

	/**
	 * Updates the site sitemap regeneration entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteSitemapRegenerationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteSitemapRegenerationEntry the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was updated
	 */
	@Override
	public com.liferay.site.model.SiteSitemapRegenerationEntry
		updateSiteSitemapRegenerationEntry(
			com.liferay.site.model.SiteSitemapRegenerationEntry
				siteSitemapRegenerationEntry) {

		return _siteSitemapRegenerationEntryLocalService.
			updateSiteSitemapRegenerationEntry(siteSitemapRegenerationEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _siteSitemapRegenerationEntryLocalService.getBasePersistence();
	}

	@Override
	public SiteSitemapRegenerationEntryLocalService getWrappedService() {
		return _siteSitemapRegenerationEntryLocalService;
	}

	@Override
	public void setWrappedService(
		SiteSitemapRegenerationEntryLocalService
			siteSitemapRegenerationEntryLocalService) {

		_siteSitemapRegenerationEntryLocalService =
			siteSitemapRegenerationEntryLocalService;
	}

	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1841631120