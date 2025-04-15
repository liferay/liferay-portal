/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link PortletLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PortletLocalService
 * @generated
 */
public class PortletLocalServiceWrapper
	implements PortletLocalService, ServiceWrapper<PortletLocalService> {

	public PortletLocalServiceWrapper() {
		this(null);
	}

	public PortletLocalServiceWrapper(PortletLocalService portletLocalService) {
		_portletLocalService = portletLocalService;
	}

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
	@Override
	public com.liferay.portal.kernel.model.Portlet addPortlet(
		com.liferay.portal.kernel.model.Portlet portlet) {

		return _portletLocalService.addPortlet(portlet);
	}

	@Override
	public void addPortletCategory(long companyId, String categoryName) {
		_portletLocalService.addPortletCategory(companyId, categoryName);
	}

	@Override
	public void checkPortlet(com.liferay.portal.kernel.model.Portlet portlet)
		throws com.liferay.portal.kernel.exception.PortalException {

		_portletLocalService.checkPortlet(portlet);
	}

	@Override
	public void checkPortlets(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_portletLocalService.checkPortlets(companyId);
	}

	@Override
	public void clearCache() {
		_portletLocalService.clearCache();
	}

	@Override
	public void clearPortletsMap() {
		_portletLocalService.clearPortletsMap();
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet clonePortlet(
		String portletId) {

		return _portletLocalService.clonePortlet(portletId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new portlet with the primary key. Does not add the portlet to the database.
	 *
	 * @param id the primary key for the new portlet
	 * @return the new portlet
	 */
	@Override
	public com.liferay.portal.kernel.model.Portlet createPortlet(long id) {
		return _portletLocalService.createPortlet(id);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deletePersistedModel(persistedModel);
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
	@Override
	public com.liferay.portal.kernel.model.Portlet deletePortlet(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deletePortlet(id);
	}

	@Override
	public void deletePortlet(long companyId, String portletId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_portletLocalService.deletePortlet(companyId, portletId, plid);
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
	@Override
	public com.liferay.portal.kernel.model.Portlet deletePortlet(
		com.liferay.portal.kernel.model.Portlet portlet) {

		return _portletLocalService.deletePortlet(portlet);
	}

	@Override
	public void deletePortlets(long companyId, String[] portletIds, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_portletLocalService.deletePortlets(companyId, portletIds, plid);
	}

	@Override
	public void deployPortlet(com.liferay.portal.kernel.model.Portlet portlet)
		throws Exception {

		_portletLocalService.deployPortlet(portlet);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet deployRemotePortlet(
			long[] companyIds, com.liferay.portal.kernel.model.Portlet portlet,
			String[] categoryNames, boolean eagerDestroy, boolean clearCache)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deployRemotePortlet(
			companyIds, portlet, categoryNames, eagerDestroy, clearCache);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet deployRemotePortlet(
			com.liferay.portal.kernel.model.Portlet portlet,
			String categoryName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deployRemotePortlet(portlet, categoryName);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet deployRemotePortlet(
			com.liferay.portal.kernel.model.Portlet portlet,
			String[] categoryNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deployRemotePortlet(portlet, categoryNames);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet deployRemotePortlet(
			com.liferay.portal.kernel.model.Portlet portlet,
			String[] categoryNames, boolean eagerDestroy)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.deployRemotePortlet(
			portlet, categoryNames, eagerDestroy);
	}

	@Override
	public void destroyPortlet(
		com.liferay.portal.kernel.model.Portlet portlet) {

		_portletLocalService.destroyPortlet(portlet);
	}

	@Override
	public void destroyRemotePortlet(
		com.liferay.portal.kernel.model.Portlet portlet) {

		_portletLocalService.destroyRemotePortlet(portlet);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _portletLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _portletLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _portletLocalService.dynamicQuery();
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

		return _portletLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _portletLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _portletLocalService.dynamicQuery(
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

		return _portletLocalService.dynamicQueryCount(dynamicQuery);
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

		return _portletLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet fetchPortlet(long id) {
		return _portletLocalService.fetchPortlet(id);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet fetchPortletById(
		long companyId, String portletId) {

		return _portletLocalService.fetchPortletById(companyId, portletId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _portletLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List
		<com.liferay.expando.kernel.model.CustomAttributesDisplay>
			getCustomAttributesDisplays() {

		return _portletLocalService.getCustomAttributesDisplays();
	}

	@Override
	public com.liferay.portal.kernel.model.PortletCategory getEARDisplay(
		String xml) {

		return _portletLocalService.getEARDisplay(xml);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet>
		getFriendlyURLMapperPortlets() {

		return _portletLocalService.getFriendlyURLMapperPortlets();
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.portlet.FriendlyURLMapper>
		getFriendlyURLMappers() {

		return _portletLocalService.getFriendlyURLMappers();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _portletLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _portletLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the portlet with the primary key.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet
	 * @throws PortalException if a portlet with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Portlet getPortlet(long id)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _portletLocalService.getPortlet(id);
	}

	@Override
	public com.liferay.portal.kernel.model.PortletApp getPortletApp(
		String servletContextName) {

		return _portletLocalService.getPortletApp(servletContextName);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet getPortletById(
		long companyId, String portletId) {

		return _portletLocalService.getPortletById(companyId, portletId);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet getPortletById(
		String portletId) {

		return _portletLocalService.getPortletById(portletId);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet getPortletByStrutsPath(
		long companyId, String strutsPath) {

		return _portletLocalService.getPortletByStrutsPath(
			companyId, strutsPath);
	}

	@Override
	public com.liferay.portal.kernel.portlet.PortletFriendlyURLMapperMatch
		getPortletFriendlyURLMapperMatch(String url) {

		return _portletLocalService.getPortletFriendlyURLMapperMatch(url);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet>
		getPortlets() {

		return _portletLocalService.getPortlets();
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
	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet> getPortlets(
		int start, int end) {

		return _portletLocalService.getPortlets(start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet> getPortlets(
		long companyId) {

		return _portletLocalService.getPortlets(companyId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet> getPortlets(
		long companyId, boolean showSystem, boolean showPortal) {

		return _portletLocalService.getPortlets(
			companyId, showSystem, showPortal);
	}

	/**
	 * Returns the number of portlets.
	 *
	 * @return the number of portlets
	 */
	@Override
	public int getPortletsCount() {
		return _portletLocalService.getPortletsCount();
	}

	@Override
	public int getPortletsCount(long companyId) {
		return _portletLocalService.getPortletsCount(companyId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet>
		getScopablePortlets() {

		return _portletLocalService.getScopablePortlets();
	}

	@Override
	public com.liferay.portal.kernel.model.PortletCategory getWARDisplay(
		String servletContextName, String xml) {

		return _portletLocalService.getWARDisplay(servletContextName, xml);
	}

	@Override
	public boolean hasPortlet(long companyId, String portletId) {
		return _portletLocalService.hasPortlet(companyId, portletId);
	}

	@Override
	public void initEAR(
		jakarta.servlet.ServletContext servletContext, String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		_portletLocalService.initEAR(servletContext, xmls, pluginPackage);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.Portlet> initWAR(
		String servletContextName, jakarta.servlet.ServletContext servletContext,
		String[] xmls,
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		return _portletLocalService.initWAR(
			servletContextName, servletContext, xmls, pluginPackage);
	}

	@Override
	public java.util.Map<String, com.liferay.portal.kernel.model.Portlet>
		loadGetPortletsMap(long companyId) {

		return _portletLocalService.loadGetPortletsMap(companyId);
	}

	@Override
	public void removeCompanyPortletsPool(long companyId) {
		_portletLocalService.removeCompanyPortletsPool(companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Portlet updatePortlet(
		long companyId, String portletId, String roles, boolean active) {

		return _portletLocalService.updatePortlet(
			companyId, portletId, roles, active);
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
	@Override
	public com.liferay.portal.kernel.model.Portlet updatePortlet(
		com.liferay.portal.kernel.model.Portlet portlet) {

		return _portletLocalService.updatePortlet(portlet);
	}

	@Override
	public void visitPortlets(
		long companyId,
		java.util.function.Consumer<com.liferay.portal.kernel.model.Portlet>
			consumer) {

		_portletLocalService.visitPortlets(companyId, consumer);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _portletLocalService.getBasePersistence();
	}

	@Override
	public PortletLocalService getWrappedService() {
		return _portletLocalService;
	}

	@Override
	public void setWrappedService(PortletLocalService portletLocalService) {
		_portletLocalService = portletLocalService;
	}

	private PortletLocalService _portletLocalService;

}