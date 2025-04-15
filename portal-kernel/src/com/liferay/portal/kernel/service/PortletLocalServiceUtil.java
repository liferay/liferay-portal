/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Portlet. This utility wraps
 * <code>com.liferay.portal.service.impl.PortletLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PortletLocalService
 * @generated
 */
public class PortletLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the portlet to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portlet the portlet
	 * @return the portlet that was added
	 */
	public static Portlet addPortlet(Portlet portlet) {
		return getService().addPortlet(portlet);
	}

	public static void addPortletCategory(long companyId, String categoryName) {
		getService().addPortletCategory(companyId, categoryName);
	}

	public static void checkPortlet(Portlet portlet) throws PortalException {
		getService().checkPortlet(portlet);
	}

	public static void checkPortlets(long companyId) throws PortalException {
		getService().checkPortlets(companyId);
	}

	public static void clearCache() {
		getService().clearCache();
	}

	public static void clearPortletsMap() {
		getService().clearPortletsMap();
	}

	public static Portlet clonePortlet(String portletId) {
		return getService().clonePortlet(portletId);
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
	 * Creates a new portlet with the primary key. Does not add the portlet to the database.
	 *
	 * @param id the primary key for the new portlet
	 * @return the new portlet
	 */
	public static Portlet createPortlet(long id) {
		return getService().createPortlet(id);
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
	 * Deletes the portlet with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet that was removed
	 * @throws PortalException if a portlet with the primary key could not be found
	 */
	public static Portlet deletePortlet(long id) throws PortalException {
		return getService().deletePortlet(id);
	}

	public static void deletePortlet(
			long companyId, String portletId, long plid)
		throws PortalException {

		getService().deletePortlet(companyId, portletId, plid);
	}

	/**
	 * Deletes the portlet from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portlet the portlet
	 * @return the portlet that was removed
	 */
	public static Portlet deletePortlet(Portlet portlet) {
		return getService().deletePortlet(portlet);
	}

	public static void deletePortlets(
			long companyId, String[] portletIds, long plid)
		throws PortalException {

		getService().deletePortlets(companyId, portletIds, plid);
	}

	public static void deployPortlet(Portlet portlet) throws Exception {
		getService().deployPortlet(portlet);
	}

	public static Portlet deployRemotePortlet(
			long[] companyIds, Portlet portlet, String[] categoryNames,
			boolean eagerDestroy, boolean clearCache)
		throws PortalException {

		return getService().deployRemotePortlet(
			companyIds, portlet, categoryNames, eagerDestroy, clearCache);
	}

	public static Portlet deployRemotePortlet(
			Portlet portlet, String categoryName)
		throws PortalException {

		return getService().deployRemotePortlet(portlet, categoryName);
	}

	public static Portlet deployRemotePortlet(
			Portlet portlet, String[] categoryNames)
		throws PortalException {

		return getService().deployRemotePortlet(portlet, categoryNames);
	}

	public static Portlet deployRemotePortlet(
			Portlet portlet, String[] categoryNames, boolean eagerDestroy)
		throws PortalException {

		return getService().deployRemotePortlet(
			portlet, categoryNames, eagerDestroy);
	}

	public static void destroyPortlet(Portlet portlet) {
		getService().destroyPortlet(portlet);
	}

	public static void destroyRemotePortlet(Portlet portlet) {
		getService().destroyRemotePortlet(portlet);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletModelImpl</code>.
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

	public static Portlet fetchPortlet(long id) {
		return getService().fetchPortlet(id);
	}

	public static Portlet fetchPortletById(long companyId, String portletId) {
		return getService().fetchPortletById(companyId, portletId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<com.liferay.expando.kernel.model.CustomAttributesDisplay>
		getCustomAttributesDisplays() {

		return getService().getCustomAttributesDisplays();
	}

	public static com.liferay.portal.kernel.model.PortletCategory getEARDisplay(
		String xml) {

		return getService().getEARDisplay(xml);
	}

	public static List<Portlet> getFriendlyURLMapperPortlets() {
		return getService().getFriendlyURLMapperPortlets();
	}

	public static List<com.liferay.portal.kernel.portlet.FriendlyURLMapper>
		getFriendlyURLMappers() {

		return getService().getFriendlyURLMappers();
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
	 * Returns the portlet with the primary key.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet
	 * @throws PortalException if a portlet with the primary key could not be found
	 */
	public static Portlet getPortlet(long id) throws PortalException {
		return getService().getPortlet(id);
	}

	public static com.liferay.portal.kernel.model.PortletApp getPortletApp(
		String servletContextName) {

		return getService().getPortletApp(servletContextName);
	}

	public static Portlet getPortletById(long companyId, String portletId) {
		return getService().getPortletById(companyId, portletId);
	}

	public static Portlet getPortletById(String portletId) {
		return getService().getPortletById(portletId);
	}

	public static Portlet getPortletByStrutsPath(
		long companyId, String strutsPath) {

		return getService().getPortletByStrutsPath(companyId, strutsPath);
	}

	public static
		com.liferay.portal.kernel.portlet.PortletFriendlyURLMapperMatch
			getPortletFriendlyURLMapperMatch(String url) {

		return getService().getPortletFriendlyURLMapperMatch(url);
	}

	public static List<Portlet> getPortlets() {
		return getService().getPortlets();
	}

	/**
	 * Returns a range of all the portlets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlets
	 * @param end the upper bound of the range of portlets (not inclusive)
	 * @return the range of portlets
	 */
	public static List<Portlet> getPortlets(int start, int end) {
		return getService().getPortlets(start, end);
	}

	public static List<Portlet> getPortlets(long companyId) {
		return getService().getPortlets(companyId);
	}

	public static List<Portlet> getPortlets(
		long companyId, boolean showSystem, boolean showPortal) {

		return getService().getPortlets(companyId, showSystem, showPortal);
	}

	/**
	 * Returns the number of portlets.
	 *
	 * @return the number of portlets
	 */
	public static int getPortletsCount() {
		return getService().getPortletsCount();
	}

	public static int getPortletsCount(long companyId) {
		return getService().getPortletsCount(companyId);
	}

	public static List<Portlet> getScopablePortlets() {
		return getService().getScopablePortlets();
	}

	public static com.liferay.portal.kernel.model.PortletCategory getWARDisplay(
		String servletContextName, String xml) {

		return getService().getWARDisplay(servletContextName, xml);
	}

	public static boolean hasPortlet(long companyId, String portletId) {
		return getService().hasPortlet(companyId, portletId);
	}

	public static void initEAR(
		jakarta.servlet.ServletContext servletContext, String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		getService().initEAR(servletContext, xmls, pluginPackage);
	}

	public static List<Portlet> initWAR(
		String servletContextName, jakarta.servlet.ServletContext servletContext,
		String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return getService().initWAR(
			servletContextName, servletContext, xmls, pluginPackage);
	}

	public static Map<String, Portlet> loadGetPortletsMap(long companyId) {
		return getService().loadGetPortletsMap(companyId);
	}

	public static void removeCompanyPortletsPool(long companyId) {
		getService().removeCompanyPortletsPool(companyId);
	}

	public static Portlet updatePortlet(
		long companyId, String portletId, String roles, boolean active) {

		return getService().updatePortlet(companyId, portletId, roles, active);
	}

	/**
	 * Updates the portlet in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portlet the portlet
	 * @return the portlet that was updated
	 */
	public static Portlet updatePortlet(Portlet portlet) {
		return getService().updatePortlet(portlet);
	}

	public static void visitPortlets(
		long companyId, java.util.function.Consumer<Portlet> consumer) {

		getService().visitPortlets(companyId, consumer);
	}

	public static PortletLocalService getService() {
		return _service;
	}

	public static void setService(PortletLocalService service) {
		_service = service;
	}

	private static volatile PortletLocalService _service;

}