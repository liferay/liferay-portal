/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.PortletFriendlyURLMapperMatch;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import jakarta.servlet.ServletContext;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Portlet. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see PortletLocalServiceUtil
 * @generated
 */
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Portlet"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface PortletLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the portlet local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link PortletLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public Portlet addPortlet(Portlet portlet);

	@Transactional(enabled = false)
	public void addPortletCategory(long companyId, String categoryName);

	public void checkPortlet(Portlet portlet) throws PortalException;

	public void checkPortlets(long companyId) throws PortalException;

	@Transactional(enabled = false)
	public void clearCache();

	@Clusterable
	@Transactional(enabled = false)
	public void clearPortletsMap();

	@Transactional(enabled = false)
	public Portlet clonePortlet(String portletId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new portlet with the primary key. Does not add the portlet to the database.
	 *
	 * @param id the primary key for the new portlet
	 * @return the new portlet
	 */
	@Transactional(enabled = false)
	public Portlet createPortlet(long id);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public Portlet deletePortlet(long id) throws PortalException;

	@CTAware
	public void deletePortlet(long companyId, String portletId, long plid)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public Portlet deletePortlet(Portlet portlet);

	@CTAware
	public void deletePortlets(long companyId, String[] portletIds, long plid)
		throws PortalException;

	@Transactional(enabled = false)
	public void deployPortlet(Portlet portlet) throws Exception;

	public Portlet deployRemotePortlet(
			long[] companyIds, Portlet portlet, String[] categoryNames,
			boolean eagerDestroy, boolean clearCache)
		throws PortalException;

	public Portlet deployRemotePortlet(Portlet portlet, String categoryName)
		throws PortalException;

	public Portlet deployRemotePortlet(Portlet portlet, String[] categoryNames)
		throws PortalException;

	public Portlet deployRemotePortlet(
			Portlet portlet, String[] categoryNames, boolean eagerDestroy)
		throws PortalException;

	@Transactional(enabled = false)
	public void destroyPortlet(Portlet portlet);

	@Transactional(enabled = false)
	public void destroyRemotePortlet(Portlet portlet);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Portlet fetchPortlet(long id);

	@Transactional(enabled = false)
	public Portlet fetchPortletById(long companyId, String portletId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(enabled = false)
	public List<CustomAttributesDisplay> getCustomAttributesDisplays();

	@Transactional(enabled = false)
	public PortletCategory getEARDisplay(String xml);

	@Transactional(enabled = false)
	public List<Portlet> getFriendlyURLMapperPortlets();

	@Transactional(enabled = false)
	public List<FriendlyURLMapper> getFriendlyURLMappers();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the portlet with the primary key.
	 *
	 * @param id the primary key of the portlet
	 * @return the portlet
	 * @throws PortalException if a portlet with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Portlet getPortlet(long id) throws PortalException;

	@Transactional(enabled = false)
	public PortletApp getPortletApp(String servletContextName);

	@Transactional(enabled = false)
	public Portlet getPortletById(long companyId, String portletId);

	@Transactional(enabled = false)
	public Portlet getPortletById(String portletId);

	@Transactional(enabled = false)
	public Portlet getPortletByStrutsPath(long companyId, String strutsPath);

	@Transactional(enabled = false)
	public PortletFriendlyURLMapperMatch getPortletFriendlyURLMapperMatch(
		String url);

	@Transactional(enabled = false)
	public List<Portlet> getPortlets();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Portlet> getPortlets(int start, int end);

	@Transactional(enabled = false)
	public List<Portlet> getPortlets(long companyId);

	@Transactional(enabled = false)
	public List<Portlet> getPortlets(
		long companyId, boolean showSystem, boolean showPortal);

	/**
	 * Returns the number of portlets.
	 *
	 * @return the number of portlets
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPortletsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPortletsCount(long companyId);

	@Transactional(enabled = false)
	public List<Portlet> getScopablePortlets();

	@Transactional(enabled = false)
	public PortletCategory getWARDisplay(String servletContextName, String xml);

	@Transactional(enabled = false)
	public boolean hasPortlet(long companyId, String portletId);

	@Transactional(enabled = false)
	public void initEAR(
		ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage);

	@Transactional(enabled = false)
	public List<Portlet> initWAR(
		String servletContextName, ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Portlet> loadGetPortletsMap(long companyId);

	@Clusterable
	@Transactional(enabled = false)
	public void removeCompanyPortletsPool(long companyId);

	public Portlet updatePortlet(
		long companyId, String portletId, String roles, boolean active);

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
	@Indexable(type = IndexableType.REINDEX)
	public Portlet updatePortlet(Portlet portlet);

	@Transactional(enabled = false)
	public void visitPortlets(long companyId, Consumer<Portlet> consumer);

}