/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for LayoutFriendlyURL. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutFriendlyURLLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutFriendlyURLLocalService
 * @generated
 */
public class LayoutFriendlyURLLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutFriendlyURLLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout friendly url to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was added
	 */
	public static LayoutFriendlyURL addLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return getService().addLayoutFriendlyURL(layoutFriendlyURL);
	}

	public static LayoutFriendlyURL addLayoutFriendlyURL(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout, String friendlyURL, String languageId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayoutFriendlyURL(
			userId, companyId, groupId, plid, privateLayout, friendlyURL,
			languageId, serviceContext);
	}

	/**
	 * Creates a new layout friendly url with the primary key. Does not add the layout friendly url to the database.
	 *
	 * @param layoutFriendlyURLId the primary key for the new layout friendly url
	 * @return the new layout friendly url
	 */
	public static LayoutFriendlyURL createLayoutFriendlyURL(
		long layoutFriendlyURLId) {

		return getService().createLayoutFriendlyURL(layoutFriendlyURLId);
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
	 * Deletes the layout friendly url from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was removed
	 */
	public static LayoutFriendlyURL deleteLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return getService().deleteLayoutFriendlyURL(layoutFriendlyURL);
	}

	/**
	 * Deletes the layout friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url that was removed
	 * @throws PortalException if a layout friendly url with the primary key could not be found
	 */
	public static LayoutFriendlyURL deleteLayoutFriendlyURL(
			long layoutFriendlyURLId)
		throws PortalException {

		return getService().deleteLayoutFriendlyURL(layoutFriendlyURLId);
	}

	public static void deleteLayoutFriendlyURLs(long plid) {
		getService().deleteLayoutFriendlyURLs(plid);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
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

	public static LayoutFriendlyURL fetchFirstLayoutFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getService().fetchFirstLayoutFriendlyURL(
			groupId, privateLayout, friendlyURL);
	}

	public static LayoutFriendlyURL fetchLayoutFriendlyURL(
		long layoutFriendlyURLId) {

		return getService().fetchLayoutFriendlyURL(layoutFriendlyURLId);
	}

	public static LayoutFriendlyURL fetchLayoutFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId) {

		return getService().fetchLayoutFriendlyURL(
			groupId, privateLayout, friendlyURL, languageId);
	}

	public static LayoutFriendlyURL fetchLayoutFriendlyURL(
		long plid, String languageId, boolean useDefault) {

		return getService().fetchLayoutFriendlyURL(
			plid, languageId, useDefault);
	}

	/**
	 * Returns the layout friendly url matching the UUID and group.
	 *
	 * @param uuid the layout friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchLayoutFriendlyURLByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchLayoutFriendlyURLByUuidAndGroupId(
			uuid, groupId);
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
	 * Returns the layout friendly url with the primary key.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url
	 * @throws PortalException if a layout friendly url with the primary key could not be found
	 */
	public static LayoutFriendlyURL getLayoutFriendlyURL(
			long layoutFriendlyURLId)
		throws PortalException {

		return getService().getLayoutFriendlyURL(layoutFriendlyURLId);
	}

	public static LayoutFriendlyURL getLayoutFriendlyURL(
			long plid, String languageId)
		throws PortalException {

		return getService().getLayoutFriendlyURL(plid, languageId);
	}

	public static LayoutFriendlyURL getLayoutFriendlyURL(
			long plid, String languageId, boolean useDefault)
		throws PortalException {

		return getService().getLayoutFriendlyURL(plid, languageId, useDefault);
	}

	/**
	 * Returns the layout friendly url matching the UUID and group.
	 *
	 * @param uuid the layout friendly url's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout friendly url
	 * @throws PortalException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL getLayoutFriendlyURLByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getLayoutFriendlyURLByUuidAndGroupId(uuid, groupId);
	}

	public static Map<Long, String> getLayoutFriendlyURLs(
		com.liferay.portal.kernel.model.Group siteGroup,
		List<com.liferay.portal.kernel.model.Layout> layouts,
		String languageId) {

		return getService().getLayoutFriendlyURLs(
			siteGroup, layouts, languageId);
	}

	/**
	 * Returns a range of all the layout friendly urls.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of layout friendly urls
	 */
	public static List<LayoutFriendlyURL> getLayoutFriendlyURLs(
		int start, int end) {

		return getService().getLayoutFriendlyURLs(start, end);
	}

	public static List<LayoutFriendlyURL> getLayoutFriendlyURLs(long plid) {
		return getService().getLayoutFriendlyURLs(plid);
	}

	public static List<LayoutFriendlyURL> getLayoutFriendlyURLs(
		long plid, String friendlyURL, int start, int end) {

		return getService().getLayoutFriendlyURLs(
			plid, friendlyURL, start, end);
	}

	/**
	 * Returns all the layout friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout friendly urls
	 * @param companyId the primary key of the company
	 * @return the matching layout friendly urls, or an empty list if no matches were found
	 */
	public static List<LayoutFriendlyURL>
		getLayoutFriendlyURLsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getLayoutFriendlyURLsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of layout friendly urls matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout friendly urls
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout friendly urls, or an empty list if no matches were found
	 */
	public static List<LayoutFriendlyURL>
		getLayoutFriendlyURLsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getService().getLayoutFriendlyURLsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layout friendly urls.
	 *
	 * @return the number of layout friendly urls
	 */
	public static int getLayoutFriendlyURLsCount() {
		return getService().getLayoutFriendlyURLsCount();
	}

	public static int getLayoutFriendlyURLsCount(
		long companyId, String friendlyURL) {

		return getService().getLayoutFriendlyURLsCount(companyId, friendlyURL);
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
	 * Updates the layout friendly url in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutFriendlyURLLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutFriendlyURL the layout friendly url
	 * @return the layout friendly url that was updated
	 */
	public static LayoutFriendlyURL updateLayoutFriendlyURL(
		LayoutFriendlyURL layoutFriendlyURL) {

		return getService().updateLayoutFriendlyURL(layoutFriendlyURL);
	}

	public static LayoutFriendlyURL updateLayoutFriendlyURL(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout, String friendlyURL, String languageId,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutFriendlyURL(
			userId, companyId, groupId, plid, privateLayout, friendlyURL,
			languageId, serviceContext);
	}

	public static List<LayoutFriendlyURL> updateLayoutFriendlyURLs(
			long userId, long companyId, long groupId, long plid,
			boolean privateLayout, Map<java.util.Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayoutFriendlyURLs(
			userId, companyId, groupId, plid, privateLayout, friendlyURLMap,
			serviceContext);
	}

	public static LayoutFriendlyURLLocalService getService() {
		return _service;
	}

	public static void setService(LayoutFriendlyURLLocalService service) {
		_service = service;
	}

	private static volatile LayoutFriendlyURLLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1477020219