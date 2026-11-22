/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.model.SiteFriendlyURL;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SiteFriendlyURL. This utility wraps
 * <code>com.liferay.site.service.impl.SiteFriendlyURLLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SiteFriendlyURLLocalService
 * @generated
 */
public class SiteFriendlyURLLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.site.service.impl.SiteFriendlyURLLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the site friendly url to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteFriendlyURL the site friendly url
	 * @return the site friendly url that was added
	 */
	public static SiteFriendlyURL addSiteFriendlyURL(
		SiteFriendlyURL siteFriendlyURL) {

		return getService().addSiteFriendlyURL(siteFriendlyURL);
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
	 * Creates a new site friendly url with the primary key. Does not add the site friendly url to the database.
	 *
	 * @param siteFriendlyURLId the primary key for the new site friendly url
	 * @return the new site friendly url
	 */
	public static SiteFriendlyURL createSiteFriendlyURL(
		long siteFriendlyURLId) {

		return getService().createSiteFriendlyURL(siteFriendlyURLId);
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
	 * Deletes the site friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteFriendlyURLId the primary key of the site friendly url
	 * @return the site friendly url that was removed
	 * @throws PortalException if a site friendly url with the primary key could not be found
	 */
	public static SiteFriendlyURL deleteSiteFriendlyURL(long siteFriendlyURLId)
		throws PortalException {

		return getService().deleteSiteFriendlyURL(siteFriendlyURLId);
	}

	/**
	 * Deletes the site friendly url from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteFriendlyURL the site friendly url
	 * @return the site friendly url that was removed
	 */
	public static SiteFriendlyURL deleteSiteFriendlyURL(
		SiteFriendlyURL siteFriendlyURL) {

		return getService().deleteSiteFriendlyURL(siteFriendlyURL);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteFriendlyURLModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteFriendlyURLModelImpl</code>.
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

	public static SiteFriendlyURL fetchSiteFriendlyURL(long siteFriendlyURLId) {
		return getService().fetchSiteFriendlyURL(siteFriendlyURLId);
	}

	public static SiteFriendlyURL fetchSiteFriendlyURL(
		long companyId, long groupId, String languageId) {

		return getService().fetchSiteFriendlyURL(
			companyId, groupId, languageId);
	}

	public static SiteFriendlyURL fetchSiteFriendlyURLByFriendlyURL(
		long companyId, String friendlyURL) {

		return getService().fetchSiteFriendlyURLByFriendlyURL(
			companyId, friendlyURL);
	}

	/**
	 * Returns the site friendly url matching the UUID and group.
	 *
	 * @param uuid the site friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site friendly url, or <code>null</code> if a matching site friendly url could not be found
	 */
	public static SiteFriendlyURL fetchSiteFriendlyURLByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchSiteFriendlyURLByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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
	 * Returns the site friendly url with the primary key.
	 *
	 * @param siteFriendlyURLId the primary key of the site friendly url
	 * @return the site friendly url
	 * @throws PortalException if a site friendly url with the primary key could not be found
	 */
	public static SiteFriendlyURL getSiteFriendlyURL(long siteFriendlyURLId)
		throws PortalException {

		return getService().getSiteFriendlyURL(siteFriendlyURLId);
	}

	/**
	 * Returns the site friendly url matching the UUID and group.
	 *
	 * @param uuid the site friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site friendly url
	 * @throws PortalException if a matching site friendly url could not be found
	 */
	public static SiteFriendlyURL getSiteFriendlyURLByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getSiteFriendlyURLByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the site friendly urls.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site friendly urls
	 * @param end the upper bound of the range of site friendly urls (not inclusive)
	 * @return the range of site friendly urls
	 */
	public static List<SiteFriendlyURL> getSiteFriendlyURLs(
		int start, int end) {

		return getService().getSiteFriendlyURLs(start, end);
	}

	public static List<SiteFriendlyURL> getSiteFriendlyURLs(
		long companyId, long groupId) {

		return getService().getSiteFriendlyURLs(companyId, groupId);
	}

	/**
	 * Returns all the site friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the site friendly urls
	 * @param companyId the primary key of the company
	 * @return the matching site friendly urls, or an empty list if no matches were found
	 */
	public static List<SiteFriendlyURL> getSiteFriendlyURLsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getSiteFriendlyURLsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of site friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the site friendly urls
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of site friendly urls
	 * @param end the upper bound of the range of site friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching site friendly urls, or an empty list if no matches were found
	 */
	public static List<SiteFriendlyURL> getSiteFriendlyURLsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SiteFriendlyURL> orderByComparator) {

		return getService().getSiteFriendlyURLsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of site friendly urls.
	 *
	 * @return the number of site friendly urls
	 */
	public static int getSiteFriendlyURLsCount() {
		return getService().getSiteFriendlyURLsCount();
	}

	/**
	 * Updates the site friendly url in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteFriendlyURL the site friendly url
	 * @return the site friendly url that was updated
	 */
	public static SiteFriendlyURL updateSiteFriendlyURL(
		SiteFriendlyURL siteFriendlyURL) {

		return getService().updateSiteFriendlyURL(siteFriendlyURL);
	}

	public static SiteFriendlyURLLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SiteFriendlyURLLocalService>
		_serviceSnapshot = new Snapshot<>(
			SiteFriendlyURLLocalServiceUtil.class,
			SiteFriendlyURLLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:899427762