/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutReference;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Layout. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a layout with additional parameters.
	 *
	 * <p>
	 * This method handles the creation of the layout including its resources,
	 * metadata, and internal data structures. It is not necessary to make
	 * subsequent calls to any methods to setup default groups, resources, ...
	 * etc.
	 * </p>
	 *
	 * @param externalReferenceCode the layout's external reference code
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @param localeNamesMap the layout's locales and localized names
	 * @param localeTitlesMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param system whether the layout is system
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param masterLayoutPlid the primary key of the master layout
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be used to specify whether site
	 administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, long classNameId, long classPK,
			Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, boolean system, Map<Locale, String> friendlyURLMap,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a layout with additional parameters.
	 *
	 * <p>
	 * This method handles the creation of the layout including its resources,
	 * metadata, and internal data structures. It is not necessary to make
	 * subsequent calls to any methods to setup default groups, resources, ...
	 * etc.
	 * </p>
	 *
	 * @param externalReferenceCode the layout's external reference code
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param localeNamesMap the layout's locales and localized names
	 * @param localeTitlesMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param masterLayoutPlid the primary key of the master layout
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be used to specify whether site
	 administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, Map<Locale, String> friendlyURLMap,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a layout with additional parameters.
	 *
	 * <p>
	 * This method handles the creation of the layout including its resources,
	 * metadata, and internal data structures. It is not necessary to make
	 * subsequent calls to any methods to setup default groups, resources, ...
	 * etc.
	 * </p>
	 *
	 * @param externalReferenceCode the layout's external reference code
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param localeNamesMap the layout's locales and localized names
	 * @param localeTitlesMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be used to specify whether site
	 administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a layout with single entry maps for name, title, and description to
	 * the default locale.
	 *
	 * <p>
	 * This method handles the creation of the layout including its resources,
	 * metadata, and internal data structures. It is not necessary to make
	 * subsequent calls to any methods to setup default groups, resources, ...
	 * etc.
	 * </p>
	 *
	 * @param externalReferenceCode the layout's external reference code
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param name the layout's locales and localized names
	 * @param title the layout's locales and localized titles
	 * @param description the layout's locales and localized descriptions
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param hidden whether the layout is hidden
	 * @param friendlyURL the layout's locales and localized friendly URLs. To
	 see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can specify the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be used to specify whether site
	 administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, String name, String title, String description,
			String type, boolean hidden, String friendlyURL,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry addTempFileEntry(
			long groupId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	public Layout copyLayout(
			long groupId, boolean privateLayout,
			Map<Locale, String> localeNamesMap, boolean hidden, boolean system,
			boolean copyPermissions, long sourcePlid,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Deletes the layout with the primary key, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteLayout(
			long groupId, boolean privateLayout, long layoutId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Deletes the layout with the plid, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param plid the primary key of the layout
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteLayout(long plid, ServiceContext serviceContext)
		throws PortalException;

	public void deleteLayout(String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteTempFileEntry(
			long groupId, String folderName, String fileName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout fetchFirstLayout(
		long groupId, boolean privateLayout, boolean published);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout fetchLayout(
			long groupId, boolean privateLayout, long layoutId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout fetchLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long fetchLayoutPlid(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException;

	/**
	 * Returns all the ancestor layouts of the layout.
	 *
	 * @param plid the primary key of the layout
	 * @return the ancestor layouts of the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getAncestorLayouts(long plid) throws PortalException;

	/**
	 * Returns the control panel layout's plid.
	 *
	 * @return the control panel layout's plid
	 * @throws PortalException if a portal exception is occured
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getControlPanelLayoutPlid() throws PortalException;

	/**
	 * Returns primary key of the matching default layout for the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the primary key of the default layout for the group; {@link
	 LayoutConstants#DEFAULT_PLID}) otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getDefaultPlid(long groupId, boolean privateLayout);

	/**
	 * Returns the primary key of the default layout for the group.
	 *
	 * @param groupId the primary key of the group
	 * @param scopeGroupId the primary key of the scope group. See {@link
	 ServiceContext#getScopeGroupId()}.
	 * @param privateLayout whether the layout is private to the group
	 * @param portletId the primary key of the portlet
	 * @return Returns the primary key of the default layout group; {@link
	 LayoutConstants#DEFAULT_PLID} otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getDefaultPlid(
			long groupId, long scopeGroupId, boolean privateLayout,
			String portletId)
		throws PortalException;

	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getDefaultPlid(
			long groupId, long scopeGroupId, String portletId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout getLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout getLayoutByUuidAndGroupId(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException;

	/**
	 * Returns the name of the layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param languageId the primary key of the language. For more information
	 See {@link Locale}.
	 * @return the layout's name
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getLayoutName(
			long groupId, boolean privateLayout, long layoutId,
			String languageId)
		throws PortalException;

	/**
	 * Returns the layout's plid that matches the parameters.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout's plid
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getLayoutPlid(String uuid, long groupId, boolean privateLayout)
		throws PortalException;

	/**
	 * Returns the layout references for all the layouts that belong to the
	 * company and belong to the portlet that matches the preferences.
	 *
	 * @param companyId the primary key of the company
	 * @param portletId the primary key of the portlet
	 * @param preferencesKey the portlet's preference key
	 * @param preferencesValue the portlet's preference value
	 * @return the layout references of the matching layouts
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutReference[] getLayoutReferences(
		long companyId, String portletId, String preferencesKey,
		String preferencesValue);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(long groupId, boolean privateLayout);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean incomplete, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(long groupId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Layout> getLayouts(
		long groupId, String type, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(long groupId, boolean privateLayout);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId, int priority);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(
		long groupId, boolean privateLayout, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutsCount(long groupId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Layout getOrAddEmptyLayout(
			String externalReferenceCode, long groupId,
			ServiceContext serviceContext)
		throws Exception;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException;

	/**
	 * Returns <code>true</code> if there is a matching layout with the UUID,
	 * group, and privacy.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return <code>true</code> if the layout is found; <code>false</code>
	 otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasLayout(String uuid, long groupId, boolean privateLayout)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPortletId(long plid, String portletId)
		throws PortalException;

	public Layout publishLayout(long plid) throws Exception;

	/**
	 * Schedules a range of layouts to be published.
	 *
	 * @param sourceGroupId the primary key of the source group
	 * @param targetGroupId the primary key of the target group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutIds the layouts considered for publishing, specified by the
	 layout IDs
	 * @param parameterMap the mapping of parameters indicating which
	 information will be used. See {@link
	 com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys}.
	 * @param groupName the group name (optionally {@link
	 DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 DestinationNames}.
	 * @param cronText the cron text. See {@link
	 com.liferay.portal.kernel.scheduler.CronTextUtil#getCronText}
	 * @param schedulerStartDate the scheduler start date
	 * @param schedulerEndDate the scheduler end date
	 * @param description the scheduler description
	 * @throws PortalException if a portal exception occurred
	 */
	public void schedulePublishToLive(
			long sourceGroupId, long targetGroupId, boolean privateLayout,
			long[] layoutIds, Map<String, String[]> parameterMap,
			String groupName, String cronText, Date schedulerStartDate,
			Date schedulerEndDate, String description)
		throws PortalException;

	/**
	 * Schedules a range of layouts to be stored.
	 *
	 * @param sourceGroupId the primary key of the source group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutIdMap the layouts considered for publishing, specified by
	 the layout IDs and booleans indicating whether they have children
	 * @param parameterMap the mapping of parameters indicating which
	 information will be used. See {@link
	 com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys}.
	 * @param remoteAddress the remote address
	 * @param remotePort the remote port
	 * @param remotePathContext the remote path context
	 * @param secureConnection whether the connection is secure
	 * @param remoteGroupId the primary key of the remote group
	 * @param remotePrivateLayout whether remote group's layout is private
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param groupName the group name. Optionally {@link
	 DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 DestinationNames}.
	 * @param cronText the cron text. See {@link
	 com.liferay.portal.kernel.scheduler.CronTextUtil#getCronText}
	 * @param schedulerStartDate the scheduler start date
	 * @param schedulerEndDate the scheduler end date
	 * @param description the scheduler description
	 * @throws PortalException if a portal exception occurred
	 */
	public void schedulePublishToRemote(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			boolean remotePrivateLayout, Date startDate, Date endDate,
			String groupName, String cronText, Date schedulerStartDate,
			Date schedulerEndDate, String description)
		throws PortalException;

	/**
	 * Sets the layouts for the group, replacing and prioritizing all layouts of
	 * the parent layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @param layoutIds the primary keys of the layouts
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public void setLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			long[] layoutIds, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Deletes the job from the scheduler's queue.
	 *
	 * @param groupId the primary key of the group
	 * @param jobName the job name
	 * @param groupName the group name (optionally {@link
	 DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 DestinationNames}.
	 * @throws PortalException if a portal exception occurred
	 */
	public void unschedulePublishToLive(
			long groupId, String jobName, String groupName)
		throws PortalException;

	/**
	 * Deletes the job from the scheduler's persistent queue.
	 *
	 * @param groupId the primary key of the group
	 * @param jobName the job name
	 * @param groupName the group name (optionally {@link
	 DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 DestinationNames}.
	 * @throws PortalException if a portal exception occurred
	 */
	public void unschedulePublishToRemote(
			long groupId, String jobName, String groupName)
		throws PortalException;

	public Layout updateIconImage(long plid, byte[] bytes)
		throws PortalException;

	/**
	 * Updates the layout with additional parameters.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param parentLayoutId the layout ID of the layout's new parent layout
	 * @param localeNamesMap the layout's locales and localized names
	 * @param localeTitlesMap the layout's locales and localized titles
	 * @param descriptionMap the locales and localized descriptions to merge
	 (optionally <code>null</code>)
	 * @param keywordsMap the locales and localized keywords to merge
	 (optionally <code>null</code>)
	 * @param robotsMap the locales and localized robots to merge (optionally
	 <code>null</code>)
	 * @param type the layout's new type (optionally {@link
	 LayoutConstants#TYPE_PORTLET})
	 * @param hidden whether the layout is hidden
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param hasIconImage if the layout has a custom icon image
	 * @param iconBytes the byte array of the layout's new icon image
	 * @param styleBookEntryERC the external reference code of the style book entry
	 * @param faviconFileEntryId the file entry ID of the layout's new favicon
	 * @param masterLayoutPlid the primary key of the master layout
	 * @param serviceContext the service context to be applied. Can set the
	 modification date and expando bridge attributes for the layout.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, boolean hidden,
			Map<Locale, String> friendlyURLMap, boolean hasIconImage,
			byte[] iconBytes, String styleBookEntryERC, long faviconFileEntryId,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the layout replacing its type settings.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 #fastLoad(String)}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings)
		throws PortalException;

	/**
	 * Updates the look and feel of the layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param themeId the primary key of the layout's new theme
	 * @param colorSchemeId the primary key of the layout's new color scheme
	 * @param css the layout's new CSS
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateLookAndFeel(
			long groupId, boolean privateLayout, long layoutId, String themeId,
			String colorSchemeId, String css)
		throws PortalException;

	/**
	 * Updates the name of the layout matching the group, layout ID, and
	 * privacy.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param name the layout's new name
	 * @param languageId the primary key of the language. For more information
	 see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateName(
			long groupId, boolean privateLayout, long layoutId, String name,
			String languageId)
		throws PortalException;

	/**
	 * Updates the name of the layout matching the primary key.
	 *
	 * @param plid the primary key of the layout
	 * @param name the name to be assigned
	 * @param languageId the primary key of the language. For more information
	 see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateName(long plid, String name, String languageId)
		throws PortalException;

	/**
	 * Updates the parent layout ID of the layout matching the group, layout ID,
	 * and privacy.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param parentLayoutId the layout ID to be assigned to the parent layout
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateParentLayoutId(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId)
		throws PortalException;

	/**
	 * Updates the parent layout ID of the layout matching the primary key. If a
	 * layout matching the parent primary key is found, the layout ID of that
	 * layout is assigned, otherwise {@link
	 * LayoutConstants#DEFAULT_PARENT_LAYOUT_ID} is assigned.
	 *
	 * @param plid the primary key of the layout
	 * @param parentPlid the primary key of the parent layout
	 * @return the layout matching the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateParentLayoutId(long plid, long parentPlid)
		throws PortalException;

	/**
	 * Updates the parent layout ID and priority of the layout.
	 *
	 * @param plid the primary key of the layout
	 * @param parentPlid the primary key of the parent layout
	 * @param priority the layout's new priority
	 * @return the layout matching the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updateParentLayoutIdAndPriority(
			long plid, long parentPlid, int priority)
		throws PortalException;

	/**
	 * Updates the priority of the layout matching the group, layout ID, and
	 * privacy.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId, int priority)
		throws PortalException;

	/**
	 * Updates the priority of the layout matching the group, layout ID, and
	 * privacy, setting the layout's priority based on the priorities of the
	 * next and previous layouts.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param nextLayoutId the layout ID of the next layout
	 * @param previousLayoutId the layout ID of the previous layout
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId,
			long nextLayoutId, long previousLayoutId)
		throws PortalException;

	/**
	 * Updates the priority of the layout matching the primary key.
	 *
	 * @param plid the primary key of the layout
	 * @param priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public Layout updatePriority(long plid, int priority)
		throws PortalException;

	public Layout updateType(long plid, String type) throws PortalException;

}