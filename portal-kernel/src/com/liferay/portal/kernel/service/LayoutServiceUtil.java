/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;

import java.util.List;
import java.util.Map;

/**
 * Provides the remote service utility for Layout. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutService
 * @generated
 */
public class LayoutServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, long classNameId, long classPK,
			Map<java.util.Locale, String> localeNamesMap,
			Map<java.util.Locale, String> localeTitlesMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden, boolean system,
			Map<java.util.Locale, String> friendlyURLMap, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId,
			classNameId, classPK, localeNamesMap, localeTitlesMap,
			descriptionMap, keywordsMap, robotsMap, type, typeSettings, hidden,
			system, friendlyURLMap, masterLayoutPlid, serviceContext);
	}

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
	public static Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<java.util.Locale, String> localeNamesMap,
			Map<java.util.Locale, String> localeTitlesMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden,
			Map<java.util.Locale, String> friendlyURLMap, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId,
			localeNamesMap, localeTitlesMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, friendlyURLMap,
			masterLayoutPlid, serviceContext);
	}

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
	public static Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<java.util.Locale, String> localeNamesMap,
			Map<java.util.Locale, String> localeTitlesMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden,
			Map<java.util.Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId,
			localeNamesMap, localeTitlesMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, friendlyURLMap,
			serviceContext);
	}

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
	public static Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, String name, String title, String description,
			String type, boolean hidden, String friendlyURL,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId, name,
			title, description, type, hidden, friendlyURL, serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				long groupId, String folderName, String fileName,
				InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addTempFileEntry(
			groupId, folderName, fileName, inputStream, mimeType);
	}

	public static Layout copyLayout(
			long groupId, boolean privateLayout,
			Map<java.util.Locale, String> localeNamesMap, boolean hidden,
			boolean system, boolean copyPermissions, long sourcePlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().copyLayout(
			groupId, privateLayout, localeNamesMap, hidden, system,
			copyPermissions, sourcePlid, serviceContext);
	}

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
	public static void deleteLayout(
			long groupId, boolean privateLayout, long layoutId,
			ServiceContext serviceContext)
		throws PortalException {

		getService().deleteLayout(
			groupId, privateLayout, layoutId, serviceContext);
	}

	/**
	 * Deletes the layout with the plid, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param plid the primary key of the layout
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteLayout(long plid, ServiceContext serviceContext)
		throws PortalException {

		getService().deleteLayout(plid, serviceContext);
	}

	public static void deleteLayout(String externalReferenceCode, long groupId)
		throws PortalException {

		getService().deleteLayout(externalReferenceCode, groupId);
	}

	public static void deleteTempFileEntry(
			long groupId, String folderName, String fileName)
		throws PortalException {

		getService().deleteTempFileEntry(groupId, folderName, fileName);
	}

	public static Layout fetchFirstLayout(
		long groupId, boolean privateLayout, boolean published) {

		return getService().fetchFirstLayout(groupId, privateLayout, published);
	}

	public static Layout fetchLayout(
			long groupId, boolean privateLayout, long layoutId)
		throws PortalException {

		return getService().fetchLayout(groupId, privateLayout, layoutId);
	}

	public static Layout fetchLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static long fetchLayoutPlid(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return getService().fetchLayoutPlid(uuid, groupId, privateLayout);
	}

	/**
	 * Returns all the ancestor layouts of the layout.
	 *
	 * @param plid the primary key of the layout
	 * @return the ancestor layouts of the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Layout> getAncestorLayouts(long plid)
		throws PortalException {

		return getService().getAncestorLayouts(plid);
	}

	/**
	 * Returns the control panel layout's plid.
	 *
	 * @return the control panel layout's plid
	 * @throws PortalException if a portal exception is occured
	 */
	public static long getControlPanelLayoutPlid() throws PortalException {
		return getService().getControlPanelLayoutPlid();
	}

	/**
	 * Returns primary key of the matching default layout for the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the primary key of the default layout for the group; {@link
	 LayoutConstants#DEFAULT_PLID}) otherwise
	 */
	public static long getDefaultPlid(long groupId, boolean privateLayout) {
		return getService().getDefaultPlid(groupId, privateLayout);
	}

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
	public static long getDefaultPlid(
			long groupId, long scopeGroupId, boolean privateLayout,
			String portletId)
		throws PortalException {

		return getService().getDefaultPlid(
			groupId, scopeGroupId, privateLayout, portletId);
	}

	public static long getDefaultPlid(
			long groupId, long scopeGroupId, String portletId)
		throws PortalException {

		return getService().getDefaultPlid(groupId, scopeGroupId, portletId);
	}

	public static Layout getLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout getLayoutByUuidAndGroupId(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);
	}

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
	public static String getLayoutName(
			long groupId, boolean privateLayout, long layoutId,
			String languageId)
		throws PortalException {

		return getService().getLayoutName(
			groupId, privateLayout, layoutId, languageId);
	}

	/**
	 * Returns the layout's plid that matches the parameters.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout's plid
	 * @throws PortalException if a portal exception occurred
	 */
	public static long getLayoutPlid(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutPlid(uuid, groupId, privateLayout);
	}

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
	public static com.liferay.portal.kernel.model.LayoutReference[]
		getLayoutReferences(
			long companyId, String portletId, String preferencesKey,
			String preferencesValue) {

		return getService().getLayoutReferences(
			companyId, portletId, preferencesKey, preferencesValue);
	}

	public static List<Layout> getLayouts(long groupId, boolean privateLayout) {
		return getService().getLayouts(groupId, privateLayout);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId)
		throws PortalException {

		return getService().getLayouts(groupId, privateLayout, parentLayoutId);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean incomplete, int start, int end)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, parentLayoutId, incomplete, start, end);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId, int start,
			int end)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, parentLayoutId, start, end);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		return getService().getLayouts(groupId, privateLayout, type);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type, int start,
			int end)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, type, start, end);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, keywords, types, start, end,
			orderByComparator);
	}

	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, keywords, types, statuses, start, end,
			orderByComparator);
	}

	public static List<Layout> getLayouts(long groupId, String type) {
		return getService().getLayouts(groupId, type);
	}

	public static List<Layout> getLayouts(
		long groupId, String type, int start, int end) {

		return getService().getLayouts(groupId, type, start, end);
	}

	public static int getLayoutsCount(long groupId, boolean privateLayout) {
		return getService().getLayoutsCount(groupId, privateLayout);
	}

	public static int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getService().getLayoutsCount(
			groupId, privateLayout, parentLayoutId);
	}

	public static int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return getService().getLayoutsCount(
			groupId, privateLayout, parentLayoutId, priority);
	}

	public static int getLayoutsCount(
		long groupId, boolean privateLayout, String type) {

		return getService().getLayoutsCount(groupId, privateLayout, type);
	}

	public static int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types)
		throws PortalException {

		return getService().getLayoutsCount(
			groupId, privateLayout, keywords, types);
	}

	public static int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses)
		throws PortalException {

		return getService().getLayoutsCount(
			groupId, privateLayout, keywords, types, statuses);
	}

	public static int getLayoutsCount(long groupId, String type) {
		return getService().getLayoutsCount(groupId, type);
	}

	public static Layout getOrAddEmptyLayout(
			String externalReferenceCode, long groupId,
			ServiceContext serviceContext)
		throws Exception {

		return getService().getOrAddEmptyLayout(
			externalReferenceCode, groupId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		return getService().getTempFileNames(groupId, folderName);
	}

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
	public static boolean hasLayout(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return getService().hasLayout(uuid, groupId, privateLayout);
	}

	public static boolean hasPortletId(long plid, String portletId)
		throws PortalException {

		return getService().hasPortletId(plid, portletId);
	}

	public static Layout publishLayout(long plid) throws Exception {
		return getService().publishLayout(plid);
	}

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
	public static void schedulePublishToLive(
			long sourceGroupId, long targetGroupId, boolean privateLayout,
			long[] layoutIds, Map<String, String[]> parameterMap,
			String groupName, String cronText,
			java.util.Date schedulerStartDate, java.util.Date schedulerEndDate,
			String description)
		throws PortalException {

		getService().schedulePublishToLive(
			sourceGroupId, targetGroupId, privateLayout, layoutIds,
			parameterMap, groupName, cronText, schedulerStartDate,
			schedulerEndDate, description);
	}

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
	public static void schedulePublishToRemote(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			boolean remotePrivateLayout, java.util.Date startDate,
			java.util.Date endDate, String groupName, String cronText,
			java.util.Date schedulerStartDate, java.util.Date schedulerEndDate,
			String description)
		throws PortalException {

		getService().schedulePublishToRemote(
			sourceGroupId, privateLayout, layoutIdMap, parameterMap,
			remoteAddress, remotePort, remotePathContext, secureConnection,
			remoteGroupId, remotePrivateLayout, startDate, endDate, groupName,
			cronText, schedulerStartDate, schedulerEndDate, description);
	}

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
	public static void setLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			long[] layoutIds, ServiceContext serviceContext)
		throws PortalException {

		getService().setLayouts(
			groupId, privateLayout, parentLayoutId, layoutIds, serviceContext);
	}

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
	public static void unschedulePublishToLive(
			long groupId, String jobName, String groupName)
		throws PortalException {

		getService().unschedulePublishToLive(groupId, jobName, groupName);
	}

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
	public static void unschedulePublishToRemote(
			long groupId, String jobName, String groupName)
		throws PortalException {

		getService().unschedulePublishToRemote(groupId, jobName, groupName);
	}

	public static Layout updateIconImage(long plid, byte[] bytes)
		throws PortalException {

		return getService().updateIconImage(plid, bytes);
	}

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
	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, Map<java.util.Locale, String> localeNamesMap,
			Map<java.util.Locale, String> localeTitlesMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			boolean hidden, Map<java.util.Locale, String> friendlyURLMap,
			boolean hasIconImage, byte[] iconBytes, String styleBookEntryERC,
			long faviconFileEntryId, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, parentLayoutId, localeNamesMap,
			localeTitlesMap, descriptionMap, keywordsMap, robotsMap, type,
			hidden, friendlyURLMap, hasIconImage, iconBytes, styleBookEntryERC,
			faviconFileEntryId, masterLayoutPlid, serviceContext);
	}

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
	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, typeSettings);
	}

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
	public static Layout updateLookAndFeel(
			long groupId, boolean privateLayout, long layoutId, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		return getService().updateLookAndFeel(
			groupId, privateLayout, layoutId, themeId, colorSchemeId, css);
	}

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
	public static Layout updateName(
			long groupId, boolean privateLayout, long layoutId, String name,
			String languageId)
		throws PortalException {

		return getService().updateName(
			groupId, privateLayout, layoutId, name, languageId);
	}

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
	public static Layout updateName(long plid, String name, String languageId)
		throws PortalException {

		return getService().updateName(plid, name, languageId);
	}

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
	public static Layout updateParentLayoutId(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId)
		throws PortalException {

		return getService().updateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);
	}

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
	public static Layout updateParentLayoutId(long plid, long parentPlid)
		throws PortalException {

		return getService().updateParentLayoutId(plid, parentPlid);
	}

	/**
	 * Updates the parent layout ID and priority of the layout.
	 *
	 * @param plid the primary key of the layout
	 * @param parentPlid the primary key of the parent layout
	 * @param priority the layout's new priority
	 * @return the layout matching the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateParentLayoutIdAndPriority(
			long plid, long parentPlid, int priority)
		throws PortalException {

		return getService().updateParentLayoutIdAndPriority(
			plid, parentPlid, priority);
	}

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
	public static Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId, int priority)
		throws PortalException {

		return getService().updatePriority(
			groupId, privateLayout, layoutId, priority);
	}

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
	public static Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId,
			long nextLayoutId, long previousLayoutId)
		throws PortalException {

		return getService().updatePriority(
			groupId, privateLayout, layoutId, nextLayoutId, previousLayoutId);
	}

	/**
	 * Updates the priority of the layout matching the primary key.
	 *
	 * @param plid the primary key of the layout
	 * @param priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updatePriority(long plid, int priority)
		throws PortalException {

		return getService().updatePriority(plid, priority);
	}

	public static Layout updateType(long plid, String type)
		throws PortalException {

		return getService().updateType(plid, type);
	}

	public static LayoutService getService() {
		return _service;
	}

	public static void setService(LayoutService service) {
		_service = service;
	}

	private static volatile LayoutService _service;

}