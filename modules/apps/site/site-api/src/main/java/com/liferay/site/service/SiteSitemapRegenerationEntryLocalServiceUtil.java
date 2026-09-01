/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.model.SiteSitemapRegenerationEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SiteSitemapRegenerationEntry. This utility wraps
 * <code>com.liferay.site.service.impl.SiteSitemapRegenerationEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntryLocalService
 * @generated
 */
public class SiteSitemapRegenerationEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.site.service.impl.SiteSitemapRegenerationEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static SiteSitemapRegenerationEntry addSiteSitemapRegenerationEntry(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return getService().addSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntry);
	}

	public static SiteSitemapRegenerationEntry addSiteSitemapRegenerationEntry(
		String assetTypeKey, long companyId, long groupId) {

		return getService().addSiteSitemapRegenerationEntry(
			assetTypeKey, companyId, groupId);
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
	 * Creates a new site sitemap regeneration entry with the primary key. Does not add the site sitemap regeneration entry to the database.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key for the new site sitemap regeneration entry
	 * @return the new site sitemap regeneration entry
	 */
	public static SiteSitemapRegenerationEntry
		createSiteSitemapRegenerationEntry(
			long siteSitemapRegenerationEntryId) {

		return getService().createSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteSiteSitemapRegenerationEntries(long companyId) {
		getService().deleteSiteSitemapRegenerationEntries(companyId);
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
	public static SiteSitemapRegenerationEntry
			deleteSiteSitemapRegenerationEntry(
				long siteSitemapRegenerationEntryId)
		throws PortalException {

		return getService().deleteSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntryId);
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
	public static SiteSitemapRegenerationEntry
		deleteSiteSitemapRegenerationEntry(
			SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return getService().deleteSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
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

	public static SiteSitemapRegenerationEntry
		fetchSiteSitemapRegenerationEntry(long siteSitemapRegenerationEntryId) {

		return getService().fetchSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntryId);
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
	public static List<SiteSitemapRegenerationEntry>
		getSiteSitemapRegenerationEntries(int start, int end) {

		return getService().getSiteSitemapRegenerationEntries(start, end);
	}

	public static List<SiteSitemapRegenerationEntry>
		getSiteSitemapRegenerationEntries(long companyId) {

		return getService().getSiteSitemapRegenerationEntries(companyId);
	}

	/**
	 * Returns the number of site sitemap regeneration entries.
	 *
	 * @return the number of site sitemap regeneration entries
	 */
	public static int getSiteSitemapRegenerationEntriesCount() {
		return getService().getSiteSitemapRegenerationEntriesCount();
	}

	public static int getSiteSitemapRegenerationEntriesCount(long companyId) {
		return getService().getSiteSitemapRegenerationEntriesCount(companyId);
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry
	 * @throws PortalException if a site sitemap regeneration entry with the primary key could not be found
	 */
	public static SiteSitemapRegenerationEntry getSiteSitemapRegenerationEntry(
			long siteSitemapRegenerationEntryId)
		throws PortalException {

		return getService().getSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntryId);
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
	public static SiteSitemapRegenerationEntry
		updateSiteSitemapRegenerationEntry(
			SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return getService().updateSiteSitemapRegenerationEntry(
			siteSitemapRegenerationEntry);
	}

	public static SiteSitemapRegenerationEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SiteSitemapRegenerationEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			SiteSitemapRegenerationEntryLocalServiceUtil.class,
			SiteSitemapRegenerationEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1153821350