/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutModel;
import com.liferay.portal.kernel.model.LayoutReference;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Plugin;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.PluginSettingLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.util.Digester;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.service.base.LayoutServiceBaseImpl;

import jakarta.portlet.PortletPreferences;

import java.io.InputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Provides the remote service for accessing, adding, deleting, exporting,
 * importing, scheduling publishing of, and updating layouts. Its methods
 * include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 * @author Tibor Lipusz
 */
public class LayoutServiceImpl extends LayoutServiceBaseImpl {

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
	 * @param  externalReferenceCode the layout's external reference code
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  classNameId the class name ID of the entity
	 * @param  classPK the primary key of the entity
	 * @param  localeNamesMap the layout's locales and localized names
	 * @param  localeTitlesMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 *         #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  system whether the layout is system
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be used to specify whether site
	 *         administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, long classNameId, long classPK,
			Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, boolean system, Map<Locale, String> friendlyURLMap,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			GroupPermissionUtil.check(
				permissionChecker, groupId, ActionKeys.ADD_LAYOUT);
		}
		else {
			LayoutPermissionUtil.check(
				permissionChecker, groupId, privateLayout, parentLayoutId,
				ActionKeys.ADD_LAYOUT);
		}

		Layout layout = layoutLocalService.addLayout(
			externalReferenceCode, getUserId(), groupId, privateLayout,
			parentLayoutId, classNameId, classPK, localeNamesMap,
			localeTitlesMap, descriptionMap, keywordsMap, robotsMap, type,
			typeSettings, hidden, system, friendlyURLMap, masterLayoutPlid,
			serviceContext);

		checkLayoutTypeSettings(layout, StringPool.BLANK, typeSettings);

		return layout;
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
	 * @param  externalReferenceCode the layout's external reference code
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  localeNamesMap the layout's locales and localized names
	 * @param  localeTitlesMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 *         #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be used to specify whether site
	 *         administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, Map<Locale, String> friendlyURLMap,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException {

		return addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId, 0, 0,
			localeNamesMap, localeTitlesMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, false, friendlyURLMap,
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
	 * @param  externalReferenceCode the layout's external reference code
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  localeNamesMap the layout's locales and localized names
	 * @param  localeTitlesMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 *         #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be used to specify whether site
	 *         administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return addLayout(
			externalReferenceCode, groupId, privateLayout, parentLayoutId, 0, 0,
			localeNamesMap, localeTitlesMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, false, friendlyURLMap, 0,
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
	 * @param  externalReferenceCode the layout's external reference code
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  name the layout's locales and localized names
	 * @param  title the layout's locales and localized titles
	 * @param  description the layout's locales and localized descriptions
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURL the layout's locales and localized friendly URLs. To
	 *         see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can specify the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be used to specify whether site
	 *         administrators can modify this page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long groupId, boolean privateLayout,
			long parentLayoutId, String name, String title, String description,
			String type, boolean hidden, String friendlyURL,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			GroupPermissionUtil.check(
				permissionChecker, groupId, ActionKeys.ADD_LAYOUT);
		}
		else {
			LayoutPermissionUtil.check(
				permissionChecker, groupId, privateLayout, parentLayoutId,
				ActionKeys.ADD_LAYOUT);
		}

		return layoutLocalService.addLayout(
			externalReferenceCode, getUserId(), groupId, privateLayout,
			parentLayoutId, name, title, description, type, hidden, friendlyURL,
			serviceContext);
	}

	@Override
	public FileEntry addTempFileEntry(
			long groupId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.EXPORT_IMPORT_LAYOUTS);

		return TempFileEntryUtil.addTempFileEntry(
			groupId, getUserId(),
			DigesterUtil.digestHex(Digester.SHA_256, folderName), fileName,
			inputStream, mimeType);
	}

	@Override
	public Layout copyLayout(
			long groupId, boolean privateLayout,
			Map<Locale, String> localeNamesMap, boolean hidden, boolean system,
			boolean copyPermissions, long sourcePlid,
			ServiceContext serviceContext)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		Layout sourceLayout = layoutLocalService.getLayout(sourcePlid);

		long parentLayoutId = sourceLayout.getParentLayoutId();

		if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			GroupPermissionUtil.check(
				permissionChecker, groupId, ActionKeys.ADD_LAYOUT);
		}
		else {
			LayoutPermissionUtil.check(
				permissionChecker, groupId, privateLayout, parentLayoutId,
				ActionKeys.ADD_LAYOUT);
		}

		return layoutLocalService.copyLayout(
			getUserId(), groupId, privateLayout, localeNamesMap, hidden, system,
			copyPermissions, sourcePlid, serviceContext);
	}

	/**
	 * Deletes the layout with the primary key, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteLayout(
			long groupId, boolean privateLayout, long layoutId,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutPermissionUtil.check(
			getPermissionChecker(), groupId, privateLayout, layoutId,
			ActionKeys.DELETE);

		layoutLocalService.deleteLayout(
			groupId, privateLayout, layoutId, serviceContext);
	}

	/**
	 * Deletes the layout with the plid, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param  plid the primary key of the layout
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteLayout(long plid, ServiceContext serviceContext)
		throws PortalException {

		LayoutPermissionUtil.check(
			getPermissionChecker(), plid, ActionKeys.DELETE);

		layoutLocalService.deleteLayout(plid, serviceContext);
	}

	@Override
	public void deleteLayout(String externalReferenceCode, long groupId)
		throws PortalException {

		Layout layout = getLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.DELETE);

		layoutLocalService.deleteLayout(layout);
	}

	@Override
	public void deleteTempFileEntry(
			long groupId, String folderName, String fileName)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.EXPORT_IMPORT_LAYOUTS);

		TempFileEntryUtil.deleteTempFileEntry(
			groupId, getUserId(),
			DigesterUtil.digestHex(Digester.SHA_256, folderName), fileName);
	}

	@Override
	public Layout fetchFirstLayout(
		long groupId, boolean privateLayout, boolean published) {

		// Ensure that virtual layouts are merged. See LPS-42222.

		List<Layout> layouts = layoutLocalService.getLayouts(
			groupId, privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			true, 0, 1);

		if (layouts.isEmpty()) {
			return null;
		}

		Layout layout = layouts.get(0);

		if ((!published || layout.isPublished()) &&
			_hasViewPermission(layout)) {

			return layout;
		}

		return _getFirstLayout(groupId, privateLayout, published);
	}

	@Override
	public Layout fetchLayout(
			long groupId, boolean privateLayout, long layoutId)
		throws PortalException {

		Layout layout = layoutPersistence.fetchByG_P_L(
			groupId, privateLayout, layoutId);

		if (layout != null) {
			LayoutPermissionUtil.check(
				getPermissionChecker(), layout, ActionKeys.VIEW);
		}

		return layout;
	}

	@Override
	public Layout fetchLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Layout layout = layoutLocalService.fetchLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);

		if (layout != null) {
			LayoutPermissionUtil.check(
				getPermissionChecker(), layout, ActionKeys.VIEW);
		}

		return layout;
	}

	@Override
	public long fetchLayoutPlid(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		Layout layout = layoutLocalService.fetchLayout(
			uuid, groupId, privateLayout);

		if (layout != null) {
			LayoutPermissionUtil.check(
				getPermissionChecker(), layout, ActionKeys.VIEW);

			return layout.getPlid();
		}

		return 0;
	}

	/**
	 * Returns all the ancestor layouts of the layout.
	 *
	 * @param  plid the primary key of the layout
	 * @return the ancestor layouts of the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Layout> getAncestorLayouts(long plid) throws PortalException {
		Layout layout = layoutLocalService.getLayout(plid);

		return filterLayouts(layout.getAncestors());
	}

	/**
	 * Returns the control panel layout's plid.
	 *
	 * @return the control panel layout's plid
	 * @throws PortalException if a portal exception is occured
	 */
	@Override
	public long getControlPanelLayoutPlid() throws PortalException {
		Group group = _groupLocalService.fetchGroup(
			CompanyThreadLocal.getCompanyId(), GroupConstants.CONTROL_PANEL);

		List<Layout> layouts = layoutLocalService.getLayouts(
			group.getGroupId(), true);

		if (ListUtil.isEmpty(layouts)) {
			throw new NoSuchLayoutException(
				"Unable to get control panel layout");
		}

		Layout layout = layouts.get(0);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		return layout.getPlid();
	}

	/**
	 * Returns primary key of the matching default layout for the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the primary key of the default layout for the group; {@link
	 *         LayoutConstants#DEFAULT_PLID}) otherwise
	 */
	@Override
	public long getDefaultPlid(long groupId, boolean privateLayout) {
		return layoutLocalService.getDefaultPlid(groupId, privateLayout);
	}

	/**
	 * Returns the primary key of the default layout for the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  scopeGroupId the primary key of the scope group. See {@link
	 *         ServiceContext#getScopeGroupId()}.
	 * @param  privateLayout whether the layout is private to the group
	 * @param  portletId the primary key of the portlet
	 * @return Returns the primary key of the default layout group; {@link
	 *         LayoutConstants#DEFAULT_PLID} otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public long getDefaultPlid(
			long groupId, long scopeGroupId, boolean privateLayout,
			String portletId)
		throws PortalException {

		if (groupId <= 0) {
			return LayoutConstants.DEFAULT_PLID;
		}

		String scopeGroupLayoutUuid = null;

		Group scopeGroup = _groupLocalService.getGroup(scopeGroupId);

		if (scopeGroup.isLayout()) {
			Layout scopeGroupLayout = layoutLocalService.getLayout(
				scopeGroup.getClassPK());

			scopeGroupLayoutUuid = scopeGroupLayout.getUuid();
		}

		Map<Long, PortletPreferences> jxPortletPreferencesMap =
			PortletPreferencesFactoryUtil.getPortletSetupMap(
				scopeGroup.getCompanyId(), groupId,
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, portletId, privateLayout);

		for (Map.Entry<Long, PortletPreferences> entry :
				jxPortletPreferencesMap.entrySet()) {

			long plid = entry.getKey();

			Layout layout = null;

			try {
				layout = layoutLocalService.getLayout(plid);
			}
			catch (NoSuchLayoutException noSuchLayoutException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutException);
				}

				continue;
			}

			if (!_hasViewPermission(layout) || !layout.isTypePortlet()) {
				continue;
			}

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (!layoutTypePortlet.hasPortletId(portletId)) {
				continue;
			}

			PortletPreferences jxPortletPreferences = entry.getValue();

			String scopeType = GetterUtil.getString(
				jxPortletPreferences.getValue("lfrScopeType", null));

			if (scopeGroup.isLayout()) {
				String scopeLayoutUuid = GetterUtil.getString(
					jxPortletPreferences.getValue("lfrScopeLayoutUuid", null));

				if (Validator.isNotNull(scopeType) &&
					Validator.isNotNull(scopeLayoutUuid) &&
					scopeLayoutUuid.equals(scopeGroupLayoutUuid)) {

					return layout.getPlid();
				}
			}
			else if (scopeGroup.isCompany()) {
				if (Validator.isNotNull(scopeType) &&
					scopeType.equals("company")) {

					return layout.getPlid();
				}
			}
			else {
				if (Validator.isNull(scopeType)) {
					return layout.getPlid();
				}
			}
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	@Override
	@ThreadLocalCachable
	public long getDefaultPlid(
			long groupId, long scopeGroupId, String portletId)
		throws PortalException {

		long plid = getDefaultPlid(groupId, scopeGroupId, false, portletId);

		if (plid == 0) {
			plid = getDefaultPlid(groupId, scopeGroupId, true, portletId);
		}

		return plid;
	}

	@Override
	public Layout getLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		Layout layout = layoutLocalService.getLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		return layout;
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param  uuid the layout's UUID
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout getLayoutByUuidAndGroupId(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		Layout layout = layoutLocalService.getLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		return layout;
	}

	/**
	 * Returns the name of the layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  languageId the primary key of the language. For more information
	 *         See {@link Locale}.
	 * @return the layout's name
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public String getLayoutName(
			long groupId, boolean privateLayout, long layoutId,
			String languageId)
		throws PortalException {

		Layout layout = layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		return layout.getName(languageId);
	}

	/**
	 * Returns the layout's plid that matches the parameters.
	 *
	 * @param  uuid the layout's UUID
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the matching layout's plid
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public long getLayoutPlid(String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		Layout layout = layoutLocalService.getLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		return layout.getPlid();
	}

	/**
	 * Returns the layout references for all the layouts that belong to the
	 * company and belong to the portlet that matches the preferences.
	 *
	 * @param  companyId the primary key of the company
	 * @param  portletId the primary key of the portlet
	 * @param  preferencesKey the portlet's preference key
	 * @param  preferencesValue the portlet's preference value
	 * @return the layout references of the matching layouts
	 */
	@Override
	public LayoutReference[] getLayoutReferences(
		long companyId, String portletId, String preferencesKey,
		String preferencesValue) {

		LayoutReference[] layoutReferences = layoutLocalService.getLayouts(
			companyId, portletId, preferencesKey, preferencesValue);

		List<LayoutReference> filteredLayoutReferences = new ArrayList<>(
			layoutReferences.length);

		for (LayoutReference layoutReference : layoutReferences) {
			try {
				LayoutModel layoutModel = layoutReference.getLayoutModel();

				if (LayoutPermissionUtil.contains(
						getPermissionChecker(), layoutModel.getPlid(),
						ActionKeys.VIEW)) {

					filteredLayoutReferences.add(layoutReference);
				}
			}
			catch (PortalException portalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return filteredLayoutReferences.toArray(new LayoutReference[0]);
	}

	@Override
	public List<Layout> getLayouts(long groupId, boolean privateLayout) {
		return layoutPersistence.filterFindByG_P(groupId, privateLayout);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId)
		throws PortalException {

		return filterLayouts(
			layoutLocalService.getLayouts(
				groupId, privateLayout, parentLayoutId));
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean incomplete, int start, int end)
		throws PortalException {

		if (incomplete) {
			return filterLayouts(
				layoutLocalService.getLayouts(
					groupId, privateLayout, parentLayoutId, incomplete, start,
					end));
		}

		Group group = _groupLocalService.getGroup(groupId);

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayout);

		if (!MergeLayoutPrototypesThreadLocal.isInProgress() &&
			(layoutSet.isLayoutSetPrototypeLinkActive() || group.isUser() ||
			 (group.isUserGroup() &&
			  (parentLayoutId != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)))) {

			return filterLayouts(
				layoutLocalService.getLayouts(
					groupId, privateLayout, parentLayoutId, incomplete, start,
					end));
		}

		return layoutPersistence.filterFindByG_P_P(
			groupId, privateLayout, parentLayoutId, start, end, null);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long parentLayoutId, int start,
			int end)
		throws PortalException {

		return filterLayouts(
			layoutLocalService.getLayouts(
				groupId, privateLayout, parentLayoutId),
			start, end);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		return filterLayouts(
			layoutLocalService.getLayouts(groupId, privateLayout, type));
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type, int start,
			int end)
		throws PortalException {

		return layoutPersistence.filterFindByG_P_T(
			groupId, privateLayout, type, start, end);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return layoutPersistence.filterFindByG_P(
				groupId, privateLayout, start, end, orderByComparator);
		}

		return layoutLocalService.getLayouts(
			groupId, getUserId(), privateLayout, keywords, types, start, end,
			orderByComparator);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return layoutPersistence.filterFindByG_P_ST(
				groupId, privateLayout, statuses, start, end,
				orderByComparator);
		}

		return layoutLocalService.getLayouts(
			groupId, getUserId(), privateLayout, keywords, types, statuses,
			start, end, orderByComparator);
	}

	@Override
	public List<Layout> getLayouts(long groupId, String type) {
		return layoutPersistence.filterFindByG_T(groupId, type);
	}

	@Override
	public List<Layout> getLayouts(
		long groupId, String type, int start, int end) {

		return layoutPersistence.filterFindByG_T(groupId, type, start, end);
	}

	@Override
	public int getLayoutsCount(long groupId, boolean privateLayout) {
		return layoutPersistence.filterCountByG_P(groupId, privateLayout);
	}

	@Override
	public int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId) {

		try {
			List<Layout> layouts = getLayouts(
				groupId, privateLayout, parentLayoutId);

			return layouts.size();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return 0;
	}

	@Override
	public int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return layoutPersistence.filterCountByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority);
	}

	@Override
	public int getLayoutsCount(
		long groupId, boolean privateLayout, String type) {

		return layoutPersistence.filterCountByG_P_T(
			groupId, privateLayout, type);
	}

	@Override
	public int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return layoutPersistence.filterCountByG_P(groupId, privateLayout);
		}

		return layoutLocalService.getLayoutsCount(
			groupId, getUserId(), privateLayout, keywords, types);
	}

	@Override
	public int getLayoutsCount(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int[] statuses)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return layoutPersistence.filterCountByG_P_ST(
				groupId, privateLayout, statuses);
		}

		return layoutLocalService.getLayoutsCount(
			groupId, getUserId(), privateLayout, keywords, types, statuses);
	}

	@Override
	public int getLayoutsCount(long groupId, String type) {
		return layoutPersistence.filterCountByG_T(groupId, type);
	}

	@Override
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		GroupPermissionUtil.check(
			getPermissionChecker(), group, ActionKeys.EXPORT_IMPORT_LAYOUTS);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					group.getCtCollectionId())) {

			return TempFileEntryUtil.getTempFileNames(
				groupId, getUserId(),
				DigesterUtil.digestHex(Digester.SHA_256, folderName));
		}
	}

	/**
	 * Returns <code>true</code> if there is a matching layout with the UUID,
	 * group, and privacy.
	 *
	 * @param  uuid the layout's UUID
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return <code>true</code> if the layout is found; <code>false</code>
	 *         otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public boolean hasLayout(String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return layoutLocalService.hasLayout(uuid, groupId, privateLayout);
	}

	@Override
	public boolean hasPortletId(long plid, String portletId)
		throws PortalException {

		Layout layout = layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return false;
		}

		LayoutPermissionUtil.check(
			getPermissionChecker(), layout, ActionKeys.VIEW);

		LayoutType layoutType = layout.getLayoutType();

		if (layoutType instanceof LayoutTypePortlet) {
			LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)layoutType;

			if (layoutTypePortlet.hasPortletId(portletId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Layout publishLayout(long plid) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Schedules a range of layouts to be published.
	 *
	 * @param  sourceGroupId the primary key of the source group
	 * @param  targetGroupId the primary key of the target group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutIds the layouts considered for publishing, specified by the
	 *         layout IDs
	 * @param  parameterMap the mapping of parameters indicating which
	 *         information will be used. See {@link
	 *         com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys}.
	 * @param  groupName the group name (optionally {@link
	 *         DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 *         DestinationNames}.
	 * @param  cronText the cron text. See {@link
	 *        com.liferay.portal.kernel.scheduler.CronTextUtil#getCronText}
	 * @param  schedulerStartDate the scheduler start date
	 * @param  schedulerEndDate the scheduler end date
	 * @param  description the scheduler description
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void schedulePublishToLive(
			long sourceGroupId, long targetGroupId, boolean privateLayout,
			long[] layoutIds, Map<String, String[]> parameterMap,
			String groupName, String cronText, Date schedulerStartDate,
			Date schedulerEndDate, String description)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), targetGroupId, ActionKeys.PUBLISH_STAGING);

		User user = _userPersistence.findByPrimaryKey(getUserId());

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, sourceGroupId, targetGroupId, privateLayout,
					layoutIds, parameterMap);

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					getUserId(), description,
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_LOCAL,
					publishLayoutLocalSettingsMap);

		long companyId = exportImportConfiguration.getCompanyId();

		Trigger trigger = TriggerFactoryUtil.createTrigger(
			StringBundler.concat(
				PortalUUIDUtil.generate(), StringPool.AT, companyId),
			groupName, schedulerStartDate, schedulerEndDate, cronText,
			TimeZone.getTimeZone(
				MapUtil.getString(parameterMap, "timeZoneId")));

		Message message = new Message();

		message.put("companyId", companyId);

		message.setPayload(
			exportImportConfiguration.getExportImportConfigurationId());

		SchedulerEngineHelperUtil.schedule(
			trigger, StorageType.PERSISTED, description,
			DestinationNames.LAYOUTS_LOCAL_PUBLISHER, message);
	}

	/**
	 * Schedules a range of layouts to be stored.
	 *
	 * @param  sourceGroupId the primary key of the source group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutIdMap the layouts considered for publishing, specified by
	 *         the layout IDs and booleans indicating whether they have children
	 * @param  parameterMap the mapping of parameters indicating which
	 *         information will be used. See {@link
	 *         com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys}.
	 * @param  remoteAddress the remote address
	 * @param  remotePort the remote port
	 * @param  remotePathContext the remote path context
	 * @param  secureConnection whether the connection is secure
	 * @param  remoteGroupId the primary key of the remote group
	 * @param  remotePrivateLayout whether remote group's layout is private
	 * @param  startDate the start date
	 * @param  endDate the end date
	 * @param  groupName the group name. Optionally {@link
	 *         DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 *         DestinationNames}.
	 * @param  cronText the cron text. See {@link
	 *         com.liferay.portal.kernel.scheduler.CronTextUtil#getCronText}
	 * @param  schedulerStartDate the scheduler start date
	 * @param  schedulerEndDate the scheduler end date
	 * @param  description the scheduler description
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void schedulePublishToRemote(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			boolean remotePrivateLayout, Date startDate, Date endDate,
			String groupName, String cronText, Date schedulerStartDate,
			Date schedulerEndDate, String description)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), sourceGroupId, ActionKeys.PUBLISH_STAGING);

		User user = _userPersistence.findByPrimaryKey(getUserId());

		Map<String, Serializable> publishLayoutRemoteSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutRemoteSettingsMap(
					getUserId(), sourceGroupId, privateLayout, layoutIdMap,
					parameterMap, remoteAddress, remotePort, remotePathContext,
					secureConnection, remoteGroupId, remotePrivateLayout,
					user.getLocale(), user.getTimeZone());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					getUserId(), description,
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_REMOTE,
					publishLayoutRemoteSettingsMap);

		long companyId = exportImportConfiguration.getCompanyId();

		Trigger trigger = TriggerFactoryUtil.createTrigger(
			StringBundler.concat(
				PortalUUIDUtil.generate(), StringPool.AT, companyId),
			groupName, schedulerStartDate, schedulerEndDate, cronText,
			TimeZone.getTimeZone(
				MapUtil.getString(parameterMap, "timeZoneId")));

		Message message = new Message();

		message.put("companyId", companyId);

		message.setPayload(
			exportImportConfiguration.getExportImportConfigurationId());

		SchedulerEngineHelperUtil.schedule(
			trigger, StorageType.PERSISTED, description,
			DestinationNames.LAYOUTS_REMOTE_PUBLISHER, message);
	}

	/**
	 * Sets the layouts for the group, replacing and prioritizing all layouts of
	 * the parent layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @param  layoutIds the primary keys of the layouts
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void setLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			long[] layoutIds, ServiceContext serviceContext)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.UPDATE);

		layoutLocalService.setLayouts(
			groupId, privateLayout, parentLayoutId, layoutIds, serviceContext);
	}

	/**
	 * Deletes the job from the scheduler's queue.
	 *
	 * @param  groupId the primary key of the group
	 * @param  jobName the job name
	 * @param  groupName the group name (optionally {@link
	 *         DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 *         DestinationNames}.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unschedulePublishToLive(
			long groupId, String jobName, String groupName)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.PUBLISH_STAGING);

		SchedulerEngineHelperUtil.delete(
			jobName, groupName, StorageType.PERSISTED);
	}

	/**
	 * Deletes the job from the scheduler's persistent queue.
	 *
	 * @param  groupId the primary key of the group
	 * @param  jobName the job name
	 * @param  groupName the group name (optionally {@link
	 *         DestinationNames#LAYOUTS_LOCAL_PUBLISHER}). See {@link
	 *         DestinationNames}.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void unschedulePublishToRemote(
			long groupId, String jobName, String groupName)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.PUBLISH_STAGING);

		SchedulerEngineHelperUtil.delete(
			jobName, groupName, StorageType.PERSISTED);
	}

	@Override
	public Layout updateIconImage(long plid, byte[] bytes)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updateIconImage(plid, bytes);
	}

	/**
	 * Updates the layout with additional parameters.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  parentLayoutId the layout ID of the layout's new parent layout
	 * @param  localeNamesMap the layout's locales and localized names
	 * @param  localeTitlesMap the layout's locales and localized titles
	 * @param  descriptionMap the locales and localized descriptions to merge
	 *         (optionally <code>null</code>)
	 * @param  keywordsMap the locales and localized keywords to merge
	 *         (optionally <code>null</code>)
	 * @param  robotsMap the locales and localized robots to merge (optionally
	 *         <code>null</code>)
	 * @param  type the layout's new type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET})
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  hasIconImage if the layout has a custom icon image
	 * @param  iconBytes the byte array of the layout's new icon image
	 * @param  styleBookEntryId the primary key of the style book entry
	 * @param  faviconFileEntryId the file entry ID of the layout's new favicon
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date and expando bridge attributes for the layout.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, Map<Locale, String> localeNamesMap,
			Map<Locale, String> localeTitlesMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, boolean hidden,
			Map<Locale, String> friendlyURLMap, boolean hasIconImage,
			byte[] iconBytes, long styleBookEntryId, long faviconFileEntryId,
			long masterLayoutPlid, ServiceContext serviceContext)
		throws PortalException {

		Layout layout = layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), layout);

		Layout updatedLayout = layoutLocalService.updateLayout(
			groupId, privateLayout, layoutId, parentLayoutId, localeNamesMap,
			localeTitlesMap, descriptionMap, keywordsMap, robotsMap, type,
			hidden, friendlyURLMap, hasIconImage, iconBytes, styleBookEntryId,
			faviconFileEntryId, masterLayoutPlid, serviceContext);

		if (!(layout.getLayoutType() instanceof LayoutTypePortlet)) {
			checkLayoutTypeSettings(
				layout, StringPool.BLANK, updatedLayout.getTypeSettings());
		}

		return updatedLayout;
	}

	/**
	 * Updates the layout replacing its type settings.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link com.liferay.portal.kernel.util.UnicodeProperties
	 *         #fastLoad(String)}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings)
		throws PortalException {

		Layout layout = layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), layout);

		checkLayoutTypeSettings(layout, layout.getTypeSettings(), typeSettings);

		return layoutLocalService.updateLayout(
			groupId, privateLayout, layoutId, typeSettings);
	}

	/**
	 * Updates the look and feel of the layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  themeId the primary key of the layout's new theme
	 * @param  colorSchemeId the primary key of the layout's new color scheme
	 * @param  css the layout's new CSS
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLookAndFeel(
			long groupId, boolean privateLayout, long layoutId, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(),
			layoutLocalService.getLayout(groupId, privateLayout, layoutId));

		if (Validator.isNotNull(themeId)) {
			_pluginSettingLocalService.checkPermission(
				getUserId(), themeId, Plugin.TYPE_THEME);
		}

		return layoutLocalService.updateLookAndFeel(
			groupId, privateLayout, layoutId, themeId, colorSchemeId, css);
	}

	/**
	 * Updates the name of the layout matching the group, layout ID, and
	 * privacy.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  name the layout's new name
	 * @param  languageId the primary key of the language. For more information
	 *         see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateName(
			long groupId, boolean privateLayout, long layoutId, String name,
			String languageId)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(),
			layoutLocalService.getLayout(groupId, privateLayout, layoutId));

		return layoutLocalService.updateName(
			groupId, privateLayout, layoutId, name, languageId);
	}

	/**
	 * Updates the name of the layout matching the primary key.
	 *
	 * @param  plid the primary key of the layout
	 * @param  name the name to be assigned
	 * @param  languageId the primary key of the language. For more information
	 *         see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateName(long plid, String name, String languageId)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updateName(plid, name, languageId);
	}

	/**
	 * Updates the parent layout ID of the layout matching the group, layout ID,
	 * and privacy.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  parentLayoutId the layout ID to be assigned to the parent layout
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateParentLayoutId(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(),
			layoutLocalService.getLayout(groupId, privateLayout, layoutId));

		return layoutLocalService.updateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);
	}

	/**
	 * Updates the parent layout ID of the layout matching the primary key. If a
	 * layout matching the parent primary key is found, the layout ID of that
	 * layout is assigned, otherwise {@link
	 * LayoutConstants#DEFAULT_PARENT_LAYOUT_ID} is assigned.
	 *
	 * @param  plid the primary key of the layout
	 * @param  parentPlid the primary key of the parent layout
	 * @return the layout matching the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateParentLayoutId(long plid, long parentPlid)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updateParentLayoutId(plid, parentPlid);
	}

	/**
	 * Updates the parent layout ID and priority of the layout.
	 *
	 * @param  plid the primary key of the layout
	 * @param  parentPlid the primary key of the parent layout
	 * @param  priority the layout's new priority
	 * @return the layout matching the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateParentLayoutIdAndPriority(
			long plid, long parentPlid, int priority)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updateParentLayoutIdAndPriority(
			plid, parentPlid, priority);
	}

	/**
	 * Updates the priority of the layout matching the group, layout ID, and
	 * privacy.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId, int priority)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(),
			layoutLocalService.getLayout(groupId, privateLayout, layoutId));

		return layoutLocalService.updatePriority(
			groupId, privateLayout, layoutId, priority);
	}

	/**
	 * Updates the priority of the layout matching the group, layout ID, and
	 * privacy, setting the layout's priority based on the priorities of the
	 * next and previous layouts.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  nextLayoutId the layout ID of the next layout
	 * @param  previousLayoutId the layout ID of the previous layout
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updatePriority(
			long groupId, boolean privateLayout, long layoutId,
			long nextLayoutId, long previousLayoutId)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(),
			layoutLocalService.getLayout(groupId, privateLayout, layoutId));

		return layoutLocalService.updatePriority(
			groupId, privateLayout, layoutId, nextLayoutId, previousLayoutId);
	}

	/**
	 * Updates the priority of the layout matching the primary key.
	 *
	 * @param  plid the primary key of the layout
	 * @param  priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updatePriority(long plid, int priority)
		throws PortalException {

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updatePriority(plid, priority);
	}

	@Override
	public Layout updateType(long plid, String type) throws PortalException {
		LayoutPermissionUtil.checkLayoutUpdatePermission(
			getPermissionChecker(), plid);

		return layoutLocalService.updateType(plid, type);
	}

	protected void checkLayoutTypeSettings(
			Layout layout, String originalTypeSettings, String newTypeSettings)
		throws PortalException {

		if (!(layout.getLayoutType() instanceof LayoutTypePortlet)) {
			return;
		}

		List<String> originalPortletIds = getPortletIds(
			layout, originalTypeSettings);
		List<String> newPortletIds = getPortletIds(layout, newTypeSettings);

		for (String portletId : newPortletIds) {
			if (originalPortletIds.contains(portletId)) {
				continue;
			}

			PortletPermissionUtil.check(
				getPermissionChecker(), layout.getPlid(), portletId,
				ActionKeys.ADD_TO_PAGE);
		}
	}

	protected List<Layout> filterLayouts(List<Layout> layouts) {
		return TransformUtil.transform(
			layouts,
			layout -> {
				if (_hasViewPermission(layout)) {
					return layout;
				}

				return null;
			});
	}

	protected List<Layout> filterLayouts(
			List<Layout> layouts, int start, int end)
		throws PortalException {

		List<Layout> filteredLayouts = filterLayouts(layouts);

		if (filteredLayouts.size() < end) {
			end = filteredLayouts.size();
		}

		if (end <= start) {
			return Collections.emptyList();
		}

		return filteredLayouts.subList(start, end);
	}

	protected List<String> getPortletIds(Layout layout, String typeSettings) {
		if (Validator.isBlank(typeSettings)) {
			return Collections.emptyList();
		}

		Layout clonedLayout = (Layout)layout.clone();

		clonedLayout.setType(LayoutConstants.TYPE_PORTLET);
		clonedLayout.setTypeSettings(typeSettings);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)clonedLayout.getLayoutType();

		return layoutTypePortlet.getPortletIds();
	}

	private Layout _getFirstLayout(
		long groupId, boolean privateLayout, boolean published) {

		boolean hasNext = true;

		int start = 1;
		int end = 0;
		int interval = 5;

		while (hasNext) {
			end = start + interval;

			List<Layout> layouts = layoutLocalService.getLayouts(
				groupId, privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, true, start, end);

			for (Layout layout : layouts) {
				if ((!published || layout.isPublished()) &&
					_hasViewPermission(layout)) {

					return layout;
				}
			}

			start = start + interval;

			if (layouts.size() < interval) {
				hasNext = false;
			}
		}

		return null;
	}

	private boolean _hasViewPermission(Layout layout) {
		try {
			if (LayoutPermissionUtil.contains(
					getPermissionChecker(), layout, ActionKeys.VIEW)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutServiceImpl.class);

	@BeanReference(type = ExportImportConfigurationLocalService.class)
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = LayoutSetLocalService.class)
	private LayoutSetLocalService _layoutSetLocalService;

	@BeanReference(type = PluginSettingLocalService.class)
	private PluginSettingLocalService _pluginSettingLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

}