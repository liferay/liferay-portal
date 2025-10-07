/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SiteNavigationMenuItem. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SiteNavigationMenuItemLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SiteNavigationMenuItemLocalService
	extends BaseLocalService, CTService<SiteNavigationMenuItem>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.site.navigation.service.impl.SiteNavigationMenuItemLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the site navigation menu item local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SiteNavigationMenuItemLocalServiceUtil} if injection and service tracking are not available.
	 */
	public SiteNavigationMenuItem addOrUpdateSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings, ServiceContext serviceContext)
		throws Exception;

	/**
	 * Adds the site navigation menu item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SiteNavigationMenuItem addSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem);

	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException;

	public SiteNavigationMenuItem addSiteNavigationMenuItem(
			String externalReferenceCode, long userId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, String typeSettings, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new site navigation menu item with the primary key. Does not add the site navigation menu item to the database.
	 *
	 * @param siteNavigationMenuItemId the primary key for the new site navigation menu item
	 * @return the new site navigation menu item
	 */
	@Transactional(enabled = false)
	public SiteNavigationMenuItem createSiteNavigationMenuItem(
		long siteNavigationMenuItemId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the site navigation menu item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item that was removed
	 * @throws PortalException if a site navigation menu item with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException;

	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			long siteNavigationMenuItemId, boolean deleteChildren)
		throws PortalException;

	/**
	 * Deletes the site navigation menu item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem);

	public SiteNavigationMenuItem deleteSiteNavigationMenuItem(
			String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteSiteNavigationMenuItems(long siteNavigationMenuId);

	public void deleteSiteNavigationMenuItemsByGroupId(long groupId);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
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
	public SiteNavigationMenuItem fetchSiteNavigationMenuItem(
		long siteNavigationMenuItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SiteNavigationMenuItem
		fetchSiteNavigationMenuItemByExternalReferenceCode(
			String externalReferenceCode, long groupId);

	/**
	 * Returns the site navigation menu item matching the UUID and group.
	 *
	 * @param uuid the site navigation menu item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site navigation menu item, or <code>null</code> if a matching site navigation menu item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SiteNavigationMenuItem fetchSiteNavigationMenuItemByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getParentSiteNavigationMenuItemIds(
		long siteNavigationMenuId, String typeSettingsKeyword);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the site navigation menu item with the primary key.
	 *
	 * @param siteNavigationMenuItemId the primary key of the site navigation menu item
	 * @return the site navigation menu item
	 * @throws PortalException if a site navigation menu item with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SiteNavigationMenuItem getSiteNavigationMenuItem(
			long siteNavigationMenuItemId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SiteNavigationMenuItem
			getSiteNavigationMenuItemByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the site navigation menu item matching the UUID and group.
	 *
	 * @param uuid the site navigation menu item's UUID
	 * @param groupId the primary key of the group
	 * @return the matching site navigation menu item
	 * @throws PortalException if a matching site navigation menu item could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SiteNavigationMenuItem getSiteNavigationMenuItemByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the site navigation menu items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.navigation.model.impl.SiteNavigationMenuItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @return the range of site navigation menu items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId, long parentSiteNavigationMenuItemId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
		long siteNavigationMenuId,
		OrderByComparator<SiteNavigationMenuItem> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(String type);

	/**
	 * Returns all the site navigation menu items matching the UUID and company.
	 *
	 * @param uuid the UUID of the site navigation menu items
	 * @param companyId the primary key of the company
	 * @return the matching site navigation menu items, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of site navigation menu items matching the UUID and company.
	 *
	 * @param uuid the UUID of the site navigation menu items
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of site navigation menu items
	 * @param end the upper bound of the range of site navigation menu items (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching site navigation menu items, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SiteNavigationMenuItem>
		getSiteNavigationMenuItemsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<SiteNavigationMenuItem> orderByComparator);

	/**
	 * Returns the number of site navigation menu items.
	 *
	 * @return the number of site navigation menu items
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSiteNavigationMenuItemsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSiteNavigationMenuItemsCount(long siteNavigationMenuId);

	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long siteNavigationMenuItemId, long parentSiteNavigationMenuItemId,
			int order)
		throws PortalException;

	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, long groupId,
			long siteNavigationMenuId, long parentSiteNavigationMenuItemId,
			String type, int order, String typeSettings)
		throws PortalException;

	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
			long userId, long siteNavigationMenuItemId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the site navigation menu item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SiteNavigationMenuItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param siteNavigationMenuItem the site navigation menu item
	 * @return the site navigation menu item that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SiteNavigationMenuItem updateSiteNavigationMenuItem(
		SiteNavigationMenuItem siteNavigationMenuItem);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SiteNavigationMenuItem> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SiteNavigationMenuItem> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SiteNavigationMenuItem>, R, E>
				updateUnsafeFunction)
		throws E;

}