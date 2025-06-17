/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.defaultpermissions.util.PortalDefaultPermissionsUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.LayoutJavaScriptException;
import com.liferay.portal.kernel.exception.LayoutNameException;
import com.liferay.portal.kernel.exception.MasterLayoutException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredLayoutException;
import com.liferay.portal.kernel.exception.SitemapChangeFrequencyException;
import com.liferay.portal.kernel.exception.SitemapIncludeException;
import com.liferay.portal.kernel.exception.SitemapPagePriorityException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lock.LockManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CustomizedPages;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutReference;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.PortalPreferenceValueTable;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.PortalPreferencesTable;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.SecureRandomUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutFriendlyURLPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPrototypePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntry;
import com.liferay.portal.kernel.systemevent.SystemEventHierarchyEntryThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.LayoutComparator;
import com.liferay.portal.kernel.util.comparator.LayoutPriorityComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.service.base.LayoutLocalServiceBaseImpl;
import com.liferay.portal.util.LayoutTypeControllerTracker;
import com.liferay.portal.util.PropsValues;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.sites.kernel.util.Sites;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * Provides the local service for accessing, adding, deleting, exporting,
 * importing, and updating layouts.
 *
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Joel Kozikowski
 * @author Charles May
 * @author Raymond Augé
 * @author Jorge Ferrer
 * @author Bruno Farache
 * @author Vilmos Papp
 * @author James Lefeu
 * @author Tibor Lipusz
 */
public class LayoutLocalServiceImpl extends LayoutLocalServiceBaseImpl {

	/**
	 * Returns the object counter's name.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether layout is private to the group
	 * @return the object counter's name
	 */
	public static String getCounterName(long groupId, boolean privateLayout) {
		return StringBundler.concat(
			Layout.class.getName(), StringPool.POUND, groupId, StringPool.POUND,
			privateLayout);
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  classNameId the class name ID of the entity
	 * @param  classPK the primary key of the entity
	 * @param  nameMap the layout's locales and localized names
	 * @param  titleMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link UnicodeProperties #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  system whether the layout is of system type
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be set to specify whether site
	 *         administrators can modify this page within their site. For
	 *         layouts that are created from a layout prototype, attributes
	 *         named <code>layoutPrototypeUuid</code> and
	 *         <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 *         provide the unique identifier of the source prototype and a
	 *         boolean to determine whether a link to it should be enabled to
	 *         activate propagation of changes made to the linked page in the
	 *         prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, long classNameId,
			long classPK, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			String type, String typeSettings, boolean hidden, boolean system,
			Map<Locale, String> friendlyURLMap, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout

		User user = _userPersistence.findByPrimaryKey(userId);
		long layoutId = getNextLayoutId(groupId, privateLayout);
		parentLayoutId = layoutLocalServiceHelper.getParentLayoutId(
			groupId, privateLayout, parentLayoutId);

		String name = nameMap.get(LocaleUtil.getSiteDefault());

		if (system &&
			(((Objects.equals(type, LayoutConstants.TYPE_ASSET_DISPLAY) ||
			   Objects.equals(type, LayoutConstants.TYPE_UTILITY)) &&
			  (classPK > 0) &&
			  (classNameId == _classNameLocalService.getClassNameId(
				  Layout.class.getName()))) ||
			 Objects.equals(type, LayoutConstants.TYPE_CONTENT))) {

			friendlyURLMap = _getDraftFriendlyURLMap(groupId, friendlyURLMap);
		}
		else {
			friendlyURLMap = layoutLocalServiceHelper.getFriendlyURLMap(
				groupId, privateLayout, layoutId, name, friendlyURLMap);
		}

		String friendlyURL = friendlyURLMap.get(LocaleUtil.getSiteDefault());

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		int priority = Integer.MAX_VALUE;

		if (!system) {
			priority = layoutLocalServiceHelper.getNextPriority(
				groupId, privateLayout, parentLayoutId, null, -1);
		}

		layoutLocalServiceHelper.validate(
			groupId, privateLayout, layoutId, parentLayoutId, classNameId,
			classPK, name, type, friendlyURLMap, serviceContext);

		Date date = new Date();

		Layout layout = layoutPersistence.create(
			layoutLocalServiceHelper.getUniquePlid());

		String uuid = serviceContext.getUuid();

		if (Validator.isNotNull(uuid)) {
			layout.setUuid(uuid);
		}

		layout.setExternalReferenceCode(externalReferenceCode);
		layout.setGroupId(groupId);
		layout.setCompanyId(user.getCompanyId());
		layout.setUserId(user.getUserId());
		layout.setUserName(user.getFullName());
		layout.setCreateDate(serviceContext.getCreateDate(date));
		layout.setModifiedDate(serviceContext.getModifiedDate(date));
		layout.setParentPlid(
			_getParentPlid(groupId, privateLayout, parentLayoutId));
		layout.setPrivateLayout(privateLayout);
		layout.setLayoutId(layoutId);
		layout.setParentLayoutId(parentLayoutId);
		layout.setClassNameId(classNameId);
		layout.setClassPK(classPK);
		layout.setNameMap(nameMap);
		layout.setTitleMap(titleMap);
		layout.setDescriptionMap(descriptionMap);
		layout.setKeywordsMap(keywordsMap);
		layout.setRobotsMap(robotsMap);
		layout.setType(type);

		boolean layoutUpdateable = ParamUtil.getBoolean(
			serviceContext, Sites.LAYOUT_UPDATEABLE, true);

		if (!layoutUpdateable) {
			typeSettingsUnicodeProperties.put(
				Sites.LAYOUT_UPDATEABLE, String.valueOf(layoutUpdateable));
		}

		if (privateLayout) {
			typeSettingsUnicodeProperties.put(
				"privateLayout", String.valueOf(privateLayout));
		}

		validateTypeSettingsProperties(layout, typeSettingsUnicodeProperties);

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		if (type.equals(LayoutConstants.TYPE_PORTLET)) {

			// LPS-106287 Calling layoutTypePortlet#setLayoutTemplateId modifies
			// typeSettingsProperties

			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (Validator.isNull(layoutTypePortlet.getLayoutTemplateId())) {
				layoutTypePortlet.setLayoutTemplateId(
					0, PropsValues.LAYOUT_DEFAULT_TEMPLATE_ID, false);
			}
		}

		layout.setHidden(hidden);
		layout.setSystem(system);
		layout.setFriendlyURL(friendlyURL);
		layout.setPriority(priority);
		layout.setMasterLayoutPlid(masterLayoutPlid);

		String layoutPrototypeUuid = ParamUtil.getString(
			serviceContext, "layoutPrototypeUuid");
		boolean layoutPrototypeLinkEnabled = ParamUtil.getBoolean(
			serviceContext, "layoutPrototypeLinkEnabled",
			PropsValues.LAYOUT_PROTOTYPE_LINK_ENABLED_DEFAULT);

		if (Validator.isNotNull(layoutPrototypeUuid)) {
			layout.setLayoutPrototypeUuid(layoutPrototypeUuid);
			layout.setLayoutPrototypeLinkEnabled(layoutPrototypeLinkEnabled);
		}

		String sourcePrototypeLayoutUuid = ParamUtil.getString(
			serviceContext, "sourcePrototypeLayoutUuid");

		if (Validator.isNotNull(sourcePrototypeLayoutUuid)) {
			layout.setSourcePrototypeLayoutUuid(sourcePrototypeLayoutUuid);
		}

		layout.setPublishDate(serviceContext.getModifiedDate(date));

		if (_workflowDefinitionLinkLocalService.hasWorkflowDefinitionLink(
				layout.getCompanyId(), layout.getGroupId(),
				Layout.class.getName()) &&
			(Objects.equals(type, LayoutConstants.TYPE_CONTENT) ||
			 Objects.equals(type, LayoutConstants.TYPE_UTILITY)) &&
			!system) {

			layout.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {
			layout.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		layout.setStatusByUserId(userId);
		layout.setStatusByUserName(user.getFullName());
		layout.setStatusDate(serviceContext.getCreateDate(date));
		layout.setExpandoBridgeAttributes(serviceContext);

		layout = layoutLocalService.updateLayout(layout);

		// Layout friendly URLs

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURLs(
			user.getUserId(), user.getCompanyId(), groupId, layout.getPlid(),
			privateLayout, friendlyURLMap, serviceContext);

		// Layout prototype

		if (Validator.isNotNull(layoutPrototypeUuid) &&
			!layoutPrototypeLinkEnabled) {

			_applyLayoutPrototype(
				layoutPrototypeUuid, layout, layoutPrototypeLinkEnabled);
		}

		// Resources

		boolean addGroupPermissions = true;

		Group group = _groupLocalService.getGroup(groupId);

		if (privateLayout && (group.isUser() || group.isUserGroup())) {
			addGroupPermissions = false;
		}

		boolean addGuestPermissions = false;

		if (!privateLayout || type.equals(LayoutConstants.TYPE_CONTROL_PANEL) ||
			group.isLayoutSetPrototype()) {

			addGuestPermissions = true;
		}

		_resourceLocalService.addResources(
			user.getCompanyId(), groupId, user.getUserId(),
			Layout.class.getName(), layout.getPlid(), false,
			addGroupPermissions, addGuestPermissions);

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_CONTENT)) {
			PortalDefaultPermissionsUtil.setModelDefaultPermissions(
				layout, layout.getCompanyId(), groupId, serviceContext);
		}

		// Group

		_groupLocalService.updateSite(groupId, true);

		layout.setLayoutSet(null);

		// Asset

		updateAsset(
			userId, layout, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		// Draft layout

		if (!layout.isDraftLayout() &&
			(layout.isTypeAssetDisplay() || layout.isTypeContent())) {

			serviceContext.setAttribute(
				"defaultSegmentsExperienceExternalReferenceCode",
				serviceContext.getAttribute(
					"draftLayoutDefaultSegmentsExperienceExternalReference" +
						"Code"));
			serviceContext.setAttribute(
				"sourcePrototypeLayoutUuid",
				serviceContext.getAttribute(
					"draftLayoutSourcePrototypeLayoutUuid"));
			serviceContext.setModifiedDate(date);

			addLayout(
				GetterUtil.getString(
					serviceContext.getAttribute(
						"draftLayoutExternalReferenceCode"),
					null),
				userId, groupId, privateLayout, parentLayoutId,
				_classNameLocalService.getClassNameId(Layout.class),
				layout.getPlid(), nameMap, titleMap, descriptionMap,
				keywordsMap, robotsMap, type, typeSettings, true, true,
				Collections.emptyMap(), layout.getMasterLayoutPlid(),
				serviceContext);
		}

		return layoutLocalService.getLayout(layout.getPlid());
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  nameMap the layout's locales and localized names
	 * @param  titleMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link UnicodeProperties #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  system whether the layout is of system type
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be set to specify whether site
	 *         administrators can modify this page within their site. For
	 *         layouts that are created from a layout prototype, attributes
	 *         named <code>layoutPrototypeUuid</code> and
	 *         <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 *         provide the unique identifier of the source prototype and a
	 *         boolean to determine whether a link to it should be enabled to
	 *         activate propagation of changes made to the linked page in the
	 *         prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, boolean system, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, 0, 0, nameMap, titleMap, descriptionMap,
			keywordsMap, robotsMap, type, typeSettings, hidden, system,
			friendlyURLMap, 0, serviceContext);
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param  nameMap the layout's locales and localized names
	 * @param  titleMap the layout's locales and localized titles
	 * @param  descriptionMap the layout's locales and localized descriptions
	 * @param  keywordsMap the layout's locales and localized keywords
	 * @param  robotsMap the layout's locales and localized robots
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link UnicodeProperties #fastLoad(String)}.
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURLMap the layout's locales and localized friendly URLs.
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date, modification
	 *         date, and expando bridge attributes for the layout. For layouts
	 *         that belong to a layout set prototype, an attribute named
	 *         <code>layoutUpdateable</code> can be set to specify whether site
	 *         administrators can modify this page within their site. For
	 *         layouts that are created from a layout prototype, attributes
	 *         named <code>layoutPrototypeUuid</code> and
	 *         <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 *         provide the unique identifier of the source prototype and a
	 *         boolean to determine whether a link to it should be enabled to
	 *         activate propagation of changes made to the linked page in the
	 *         prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId,
			Map<Locale, String> nameMap, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, Map<Locale, String> keywordsMap,
			Map<Locale, String> robotsMap, String type, String typeSettings,
			boolean hidden, Map<Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, nameMap, titleMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, false, friendlyURLMap,
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID}). The possible
	 *         values can be found in {@link LayoutConstants}.
	 * @param  name the layout's name (optionally {@link
	 *         PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_NAME} or {@link
	 *         PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_NAME}). The default values
	 *         can be overridden in <code>portal-ext.properties</code> by
	 *         specifying new values for the corresponding properties defined in
	 *         {@link PropsValues}
	 * @param  title the layout's title
	 * @param  description the layout's description
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  hidden whether the layout is hidden
	 * @param  system whether the layout is of system type
	 * @param  friendlyURL the friendly URL of the layout (optionally {@link
	 *         PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_FRIENDLY_URL} or {@link
	 *         PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_FRIENDLY_URL}). The
	 *         default values can be overridden in
	 *         <code>portal-ext.properties</code> by specifying new values for
	 *         the corresponding properties defined in {@link PropsValues}. To
	 *         see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date and modification
	 *         date for the layout. For layouts that belong to a layout set
	 *         prototype, an attribute named <code>layoutUpdateable</code> can
	 *         be set to specify whether site administrators can modify this
	 *         page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, String name,
			String title, String description, String type, boolean hidden,
			boolean system, String friendlyURL, ServiceContext serviceContext)
		throws PortalException {

		Locale locale = LocaleUtil.getSiteDefault();

		Map<Locale, String> nameMap = HashMapBuilder.put(
			locale, name
		).build();

		Map<Locale, String> titleMap = new HashMap<>();

		titleMap.put(locale, title);

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(locale, description);

		Map<Locale, String> friendlyURLMap = new HashMap<>();

		friendlyURLMap.put(LocaleUtil.getSiteDefault(), friendlyURL);

		return addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, nameMap, titleMap, descriptionMap,
			new HashMap<Locale, String>(), new HashMap<Locale, String>(), type,
			StringPool.BLANK, hidden, system, friendlyURLMap, serviceContext);
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
	 * @param  userId the primary key of the user
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout (optionally
	 *         {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID}). The possible
	 *         values can be found in {@link LayoutConstants}.
	 * @param  name the layout's name (optionally {@link
	 *         PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_NAME} or {@link
	 *         PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_NAME}). The default values
	 *         can be overridden in <code>portal-ext.properties</code> by
	 *         specifying new values for the corresponding properties defined in
	 *         {@link PropsValues}
	 * @param  title the layout's title
	 * @param  description the layout's description
	 * @param  type the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @param  hidden whether the layout is hidden
	 * @param  friendlyURL the friendly URL of the layout (optionally {@link
	 *         PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_FRIENDLY_URL} or {@link
	 *         PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_FRIENDLY_URL}). The
	 *         default values can be overridden in
	 *         <code>portal-ext.properties</code> by specifying new values for
	 *         the corresponding properties defined in {@link PropsValues}. To
	 *         see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  serviceContext the service context to be applied. Must set the
	 *         UUID for the layout. Can set the creation date and modification
	 *         date for the layout. For layouts that belong to a layout set
	 *         prototype, an attribute named <code>layoutUpdateable</code> can
	 *         be set to specify whether site administrators can modify this
	 *         page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, String name,
			String title, String description, String type, boolean hidden,
			String friendlyURL, ServiceContext serviceContext)
		throws PortalException {

		return addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, name, title, description, type, hidden, false,
			friendlyURL, serviceContext);
	}

	@Override
	public Layout copyLayout(
			long userId, long groupId, boolean privateLayout,
			Map<Locale, String> nameMap, boolean hidden, boolean system,
			boolean copyPermissions, long sourcePlid,
			ServiceContext serviceContext)
		throws PortalException {

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		UnicodeProperties typeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				httpServletRequest, "TypeSettingsProperties--");

		Layout sourceLayout = layoutLocalService.getLayout(sourcePlid);

		UnicodeProperties sourceUnicodeProperties =
			sourceLayout.getTypeSettingsProperties();

		sourceUnicodeProperties.putAll(typeSettingsUnicodeProperties);

		Layout targetLayout = layoutLocalService.addLayout(
			null, userId, groupId, privateLayout,
			sourceLayout.getParentLayoutId(), sourceLayout.getClassNameId(),
			sourceLayout.getClassPK(), nameMap, sourceLayout.getTitleMap(),
			sourceLayout.getDescriptionMap(), sourceLayout.getKeywordsMap(),
			sourceLayout.getRobotsMap(), sourceLayout.getType(),
			sourceUnicodeProperties.toString(), hidden, system, new HashMap<>(),
			sourceLayout.getMasterLayoutPlid(), serviceContext);

		if (copyPermissions) {
			_resourceLocalService.deleteResource(
				targetLayout.getCompanyId(), Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, targetLayout.getPlid());

			_resourceLocalService.copyModelResources(
				sourceLayout.getCompanyId(), Layout.class.getName(),
				sourceLayout.getPlid(), targetLayout.getPlid());
		}

		UnicodeProperties targetUnicodeProperties =
			targetLayout.getTypeSettingsProperties();

		targetUnicodeProperties.put("published", Boolean.FALSE.toString());

		return layoutLocalService.updateLayout(targetLayout);
	}

	@Override
	public Layout copyLayoutContent(Layout sourceLayout, Layout targetLayout)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	@Override
	public Layout copyLayoutContent(
			long sourceSegmentsExperienceId, Layout sourceLayout,
			long targetSegmentsExperienceId, Layout targetLayout)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	/**
	 * Deletes the layout, its child layouts, and its associated resources.
	 *
	 * @param  layout the layout
	 * @return the deleted layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout deleteLayout(Layout layout) throws PortalException {
		layoutLocalService.deleteLayout(layout, new ServiceContext());

		return layout;
	}

	/**
	 * Deletes the layout, its child layouts, and its associated resources.
	 *
	 * @param  layout the layout
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteLayout(Layout layout, ServiceContext serviceContext)
		throws PortalException {

		// First layout validation

		if (layout.getParentLayoutId() ==
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {

			List<Layout> rootLayouts = layoutPersistence.findByG_P_P(
				layout.getGroupId(), layout.isPrivateLayout(),
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, 0, 2);

			if (rootLayouts.size() > 1) {
				Layout firstLayout = rootLayouts.get(0);

				if (firstLayout.getLayoutId() == layout.getLayoutId()) {
					Layout secondLayout = rootLayouts.get(1);

					layoutLocalServiceHelper.validateFirstLayout(secondLayout);
				}
			}
		}

		// Child layouts

		List<Layout> childLayouts = layoutPersistence.findByG_P_P(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getLayoutId());

		for (Layout childLayout : childLayouts) {
			layoutLocalService.deleteLayout(childLayout, serviceContext);
		}

		LockManagerUtil.unlock(Layout.class.getName(), layout.getPlid());

		// Layout friendly URLs

		_layoutFriendlyURLLocalService.deleteLayoutFriendlyURLs(
			layout.getPlid());

		// Portlet preferences

		_portletPreferencesLocalService.deletePortletPreferencesByPlid(
			layout.getPlid());

		// Asset

		_assetEntryLocalService.deleteEntry(
			Layout.class.getName(), layout.getPlid());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			Layout.class.getName(), layout.getPlid());

		// Expando

		_expandoRowLocalService.deleteRows(
			layout.getCompanyId(),
			_classNameLocalService.getClassNameId(Layout.class.getName()),
			layout.getPlid());

		// Icon

		_imageLocalService.deleteImage(layout.getIconImageId());

		// Scope group

		Group scopeGroup = layout.getScopeGroup();

		if (scopeGroup != null) {
			_groupLocalService.deleteGroup(scopeGroup.getGroupId());
		}

		// Resources

		String primKey =
			layout.getPlid() + PortletConstants.LAYOUT_SEPARATOR + "%";

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionPersistence.findByC_LikeP(
				layout.getCompanyId(), primKey);

		for (ResourcePermission resourcePermission : resourcePermissions) {
			_resourcePermissionLocalService.deleteResourcePermission(
				resourcePermission);
		}

		_resourceLocalService.deleteResource(
			layout.getCompanyId(), Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, layout.getPlid());

		// Draft layout

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			layoutLocalService.deleteLayout(draftLayout);
		}

		// Layout

		layout = layoutPersistence.remove(layout);

		if (layout == null) {
			return;
		}

		// Portal preferences

		_resetPortalPreferences(layout);

		// System event

		SystemEventHierarchyEntry systemEventHierarchyEntry =
			SystemEventHierarchyEntryThreadLocal.peek();

		if ((systemEventHierarchyEntry != null) &&
			systemEventHierarchyEntry.hasTypedModel(
				Layout.class.getName(), layout.getPlid())) {

			systemEventHierarchyEntry.setExtraDataValue(
				"privateLayout", String.valueOf(layout.isPrivateLayout()));
		}

		// Workflow

		_workflowInstanceLinkLocalService.deleteWorkflowInstanceLink(
			layout.getCompanyId(), layout.getGroupId(), Layout.class.getName(),
			layout.getPlid());

		// Indexer

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(Layout.class);

		if (indexer != null) {
			indexer.delete(layout);
		}
	}

	/**
	 * Deletes the layout with the plid, also deleting the layout's child
	 * layouts, and associated resources.
	 *
	 * @param  plid the primary key of the layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout deleteLayout(long plid) throws PortalException {
		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		layoutLocalService.deleteLayout(layout, new ServiceContext());

		return layout;
	}

	/**
	 * Deletes the layout with the layout ID, also deleting the layout's child
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

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		layoutLocalService.deleteLayout(layout, serviceContext);
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

		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		layoutLocalService.deleteLayout(layout, serviceContext);
	}

	@Override
	public void deleteLayout(String externalReferenceCode, long groupId)
		throws PortalException {

		layoutLocalService.deleteLayout(
			getLayoutByExternalReferenceCode(externalReferenceCode, groupId));
	}

	/**
	 * Deletes the group's private or non-private layouts, also deleting the
	 * layouts' child layouts, and associated resources.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  serviceContext the service context to be applied. The parent
	 *         layout set's page count will be updated by default, unless an
	 *         attribute named <code>updatePageCount</code> is set to
	 *         <code>false</code>.
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void deleteLayouts(
			long groupId, boolean privateLayout, ServiceContext serviceContext)
		throws PortalException {

		// Layouts

		_deleteLayouts(groupId, privateLayout, false, serviceContext);
		_deleteLayouts(groupId, privateLayout, true, serviceContext);

		// Counter

		counterLocalService.reset(getCounterName(groupId, privateLayout));
	}

	@Override
	public Layout fetchDefaultLayout(long groupId, boolean privateLayout) {
		if (groupId > 0) {
			return layoutPersistence.fetchByG_P_First(
				groupId, privateLayout, null);
		}

		return null;
	}

	@Override
	public Layout fetchDraftLayout(long plid) {
		if (plid <= 0) {
			return null;
		}

		List<Layout> layouts = layoutPersistence.findByC_C(
			_classNameLocalService.getClassNameId(Layout.class), plid);

		if (layouts.isEmpty()) {
			return null;
		}

		if ((layouts.size() > 1) && _log.isWarnEnabled()) {
			_log.warn("More than one draft layout uses layout ID " + plid);
		}

		return layouts.get(layouts.size() - 1);
	}

	@Override
	public Layout fetchFirstLayout(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return layoutPersistence.fetchByG_P_P_First(
			groupId, privateLayout, parentLayoutId,
			new LayoutPriorityComparator());
	}

	@Override
	public Layout fetchFirstLayout(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return layoutPersistence.fetchByG_P_P_H_First(
			groupId, privateLayout, parentLayoutId, hidden,
			new LayoutPriorityComparator());
	}

	@Override
	public Layout fetchLayout(
		long groupId, boolean privateLayout, long layoutId) {

		return layoutPersistence.fetchByG_P_L(groupId, privateLayout, layoutId);
	}

	@Override
	public Layout fetchLayout(
		String uuid, long groupId, boolean privateLayout) {

		return layoutPersistence.fetchByUUID_G_P(uuid, groupId, privateLayout);
	}

	@Override
	public Layout fetchLayoutByFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL) {

		friendlyURL = layoutLocalServiceHelper.getFriendlyURL(friendlyURL);

		return layoutPersistence.fetchByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	@Override
	public Layout fetchLayoutByIconImageId(
		boolean privateLayout, long iconImageId) {

		if (iconImageId <= 0) {
			return null;
		}

		List<Layout> layouts = layoutPersistence.findByP_I(
			privateLayout, iconImageId);

		if (layouts.isEmpty()) {
			return null;
		}

		if ((layouts.size() > 1) && _log.isWarnEnabled()) {
			_log.warn("More than one layout uses icon image ID " + iconImageId);
		}

		return layouts.get(layouts.size() - 1);
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param  uuid the layout's UUID
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the matching layout, or <code>null</code> if a matching layout
	 *         could not be found
	 */
	@Override
	public Layout fetchLayoutByUuidAndGroupId(
		String uuid, long groupId, boolean privateLayout) {

		return layoutPersistence.fetchByUUID_G_P(uuid, groupId, privateLayout);
	}

	/**
	 * Returns all the layouts that match the type and belong to the group,
	 * including the ones marked as System.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  type the type of the layouts (optiona.lly {@link
	 *         LayoutConstants#TYPE_PORTLET})
	 * @return the matching layouts, or an empty list if no matches were found
	 */
	@Override
	public List<Layout> getAllLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		DynamicQuery dynamicQuery = layoutLocalService.dynamicQuery();

		Property groupIdProperty = PropertyFactoryUtil.forName("groupId");

		dynamicQuery.add(groupIdProperty.eq(groupId));

		Property privateLayoutProperty = PropertyFactoryUtil.forName(
			"privateLayout");

		dynamicQuery.add(privateLayoutProperty.eq(privateLayout));

		Property classNameIdProperty = PropertyFactoryUtil.forName(
			"classNameId");

		dynamicQuery.add(
			classNameIdProperty.ne(
				_classNameLocalService.getClassNameId(Layout.class.getName())));

		Property typeProperty = PropertyFactoryUtil.forName("type");

		dynamicQuery.add(typeProperty.eq(type));

		List<Layout> layouts = layoutLocalService.dynamicQuery(dynamicQuery);

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		if (!group.isUser()) {
			return layouts;
		}

		Set<Long> checkedPlids = new HashSet<>();

		Queue<Long> checkParentLayoutIds = new ArrayDeque<>();

		checkParentLayoutIds.add(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		LayoutSet layoutSet = _layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		while (!checkParentLayoutIds.isEmpty()) {
			long parentLayoutId = checkParentLayoutIds.poll();

			List<Layout> userGroupLayouts = _addUserGroupLayouts(
				group, layoutSet, Collections.emptyList(), parentLayoutId);

			for (Layout userGroupLayout : userGroupLayouts) {
				long userGroupPlid = userGroupLayout.getPlid();

				if (checkedPlids.add(userGroupPlid)) {
					layouts.add(userGroupLayout);

					checkParentLayoutIds.add(userGroupLayout.getLayoutId());
				}
			}
		}

		return layouts;
	}

	@Override
	public Layout getBrowsableLayout(Layout layout) {
		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		if (layoutTypeController.isBrowsable()) {
			return layout;
		}

		List<String> types = TransformUtil.transformToList(
			LayoutTypeControllerTracker.getTypes(),
			type -> {
				LayoutTypeController curLayoutTypeController =
					LayoutTypeControllerTracker.getLayoutTypeController(type);

				if ((curLayoutTypeController == null) ||
					!curLayoutTypeController.isBrowsable()) {

					return null;
				}

				return type;
			});

		ChildLayout browsableChildLayout = _getBrowsableChildLayout(
			types, layout.getGroupId(), layout.getLayoutId(),
			layout.isPrivateLayout());

		if (browsableChildLayout == null) {
			return fetchDefaultLayout(
				layout.getGroupId(), layout.isPrivateLayout());
		}

		return fetchLayout(browsableChildLayout.getPlid());
	}

	/**
	 * Returns the primary key of the default layout for the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the primary key of the default layout for the group (optionally
	 *         {@link LayoutConstants#DEFAULT_PLID})
	 */
	@Override
	public long getDefaultPlid(long groupId) {
		if (groupId > 0) {
			Layout layout = layoutPersistence.fetchByGroupId_First(
				groupId, null);

			if (layout != null) {
				return layout.getPlid();
			}
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	/**
	 * Returns primary key of the matching default layout for the group
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the primary key of the default layout for the group; {@link
	 *         LayoutConstants#DEFAULT_PLID}) otherwise
	 */
	@Override
	public long getDefaultPlid(long groupId, boolean privateLayout) {
		Layout layout = fetchDefaultLayout(groupId, privateLayout);

		if (layout != null) {
			return layout.getPlid();
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	/**
	 * Returns primary key of the default portlet layout for the group
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  portletId the primary key of the portlet
	 * @return the primary key of the default portlet layout for the group;
	 *         {@link LayoutConstants#DEFAULT_PLID} otherwise
	 * @throws PortalException
	 */
	@Override
	public long getDefaultPlid(
			long groupId, boolean privateLayout, String portletId)
		throws PortalException {

		if (groupId > 0) {
			List<Layout> layouts = layoutPersistence.findByG_P(
				groupId, privateLayout);

			for (Layout layout : layouts) {
				if (layout.isTypePortlet()) {
					LayoutTypePortlet layoutTypePortlet =
						(LayoutTypePortlet)layout.getLayoutType();

					if (layoutTypePortlet.hasPortletId(portletId)) {
						return layout.getPlid();
					}
				}
			}
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	/**
	 * Returns the layout for the friendly URL.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  friendlyURL the friendly URL of the layout
	 * @return the layout for the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout getFriendlyURLLayout(
			long groupId, boolean privateLayout, String friendlyURL)
		throws PortalException {

		if (Validator.isNull(friendlyURL)) {
			throw new NoSuchLayoutException(
				StringBundler.concat(
					"{groupId=", groupId, ", privateLayout=", privateLayout,
					"}"));
		}

		friendlyURL = layoutLocalServiceHelper.getFriendlyURL(friendlyURL);

		Layout layout = null;

		List<LayoutFriendlyURL> layoutFriendlyURLs =
			_layoutFriendlyURLPersistence.findByG_P_F(
				groupId, privateLayout, friendlyURL, 0, 1);

		if (!layoutFriendlyURLs.isEmpty()) {
			LayoutFriendlyURL layoutFriendlyURL = layoutFriendlyURLs.get(0);

			layout = layoutPersistence.findByPrimaryKey(
				layoutFriendlyURL.getPlid());
		}

		if ((layout == null) && friendlyURL.startsWith(StringPool.SLASH) &&
			Validator.isNumber(friendlyURL.substring(1))) {

			long layoutId = GetterUtil.getLong(friendlyURL.substring(1));

			layout = layoutPersistence.fetchByG_P_L(
				groupId, privateLayout, layoutId);
		}

		if (layout == null) {
			throw new NoSuchLayoutException(
				StringBundler.concat(
					"{groupId=", groupId, ", privateLayout=", privateLayout,
					", friendlyURL=", friendlyURL, "}"));
		}

		return layout;
	}

	@Override
	public Layout getLayout(long plid) throws PortalException {
		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		if (_mergeLayout(layout, plid)) {
			return layoutPersistence.findByPrimaryKey(plid);
		}

		return layout;
	}

	/**
	 * Returns the layout matching the layout ID, group, and privacy; throws a
	 * {@link NoSuchLayoutException} otherwise.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout getLayout(long groupId, boolean privateLayout, long layoutId)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		if (_mergeLayout(layout, groupId, privateLayout, layoutId)) {
			return layoutPersistence.findByG_P_L(
				groupId, privateLayout, layoutId);
		}

		return layout;
	}

	@Override
	public Layout getLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return layoutPersistence.findByERC_G(externalReferenceCode, groupId);
	}

	@Override
	public Layout getLayoutByFriendlyURL(
			long groupId, boolean privateLayout, String friendlyURL)
		throws PortalException {

		friendlyURL = layoutLocalServiceHelper.getFriendlyURL(friendlyURL);

		return layoutPersistence.findByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param  uuid the layout's UUID
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the matching layout
	 * @throws PortalException if a matching layout could not be found
	 */
	@Override
	public Layout getLayoutByUuidAndGroupId(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return layoutPersistence.findByUUID_G_P(uuid, groupId, privateLayout);
	}

	@Override
	public Map<Long, List<Layout>> getLayoutChildLayouts(
		List<Layout> parentLayouts) {

		Map<LayoutSet, List<Layout>> layoutsMap = new HashMap<>();

		for (Layout parentLayout : parentLayouts) {
			if (parentLayout instanceof VirtualLayout) {
				VirtualLayout virtualLayout = (VirtualLayout)parentLayout;

				Layout sourceLayout = virtualLayout.getSourceLayout();

				LayoutSet sourceLayoutSet = sourceLayout.getLayoutSet();

				List<Layout> layouts = layoutsMap.computeIfAbsent(
					sourceLayoutSet, key -> new ArrayList<>());

				layouts.add(sourceLayout);
			}
			else {
				List<Layout> layouts = layoutsMap.computeIfAbsent(
					parentLayout.getLayoutSet(), key -> new ArrayList<>());

				layouts.add(parentLayout);
			}
		}

		List<Layout> childLayouts = new ArrayList<>();

		for (Map.Entry<LayoutSet, List<Layout>> entry : layoutsMap.entrySet()) {
			List<Layout> newChildLayouts = _getChildLayouts(
				entry.getKey(),
				ListUtil.toLongArray(entry.getValue(), Layout::getLayoutId));

			childLayouts.addAll(newChildLayouts);
		}

		Map<Long, List<Layout>> layoutChildLayoutsMap = new HashMap<>();

		for (Layout childLayout : childLayouts) {
			List<Layout> layoutChildLayouts =
				layoutChildLayoutsMap.computeIfAbsent(
					childLayout.getParentPlid(),
					parentPlid -> new ArrayList<>());

			layoutChildLayouts.add(childLayout);
		}

		for (List<Layout> layoutChildLayouts : layoutChildLayoutsMap.values()) {
			layoutChildLayouts.sort(Comparator.comparing(Layout::getPriority));
		}

		return layoutChildLayoutsMap;
	}

	@Override
	public List<Layout> getLayouts(Collection<Serializable> primaryKeys) {
		Map<Serializable, Layout> layoutsMap =
			layoutPersistence.fetchByPrimaryKeys(
				SetUtil.fromCollection(primaryKeys));

		if (layoutsMap.isEmpty()) {
			return Collections.emptyList();
		}

		return new ArrayList<>(layoutsMap.values());
	}

	@Override
	public List<Layout> getLayouts(long companyId) {
		return layoutPersistence.findByCompanyId(companyId);
	}

	/**
	 * Returns all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(long groupId, boolean privateLayout) {
		return layoutPersistence.findByG_P(groupId, privateLayout);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return layoutPersistence.findByG_P(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return layoutPersistence.findByG_P_ST(
			groupId, privateLayout, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the layouts belonging to the group that are children of the
	 * parent layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getLayouts(
			groupId, privateLayout, parentLayoutId, false, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts belonging to the group that are
	 * children of the parent layout.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @param  incomplete whether the layout is incomplete
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean incomplete, int start, int end) {

		return getLayouts(
			groupId, privateLayout, parentLayoutId, incomplete, start, end,
			null);
	}

	/**
	 * Returns a range of all the layouts belonging to the group that are
	 * children of the parent layout.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean incomplete, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return layoutPersistence.findByG_P_P(
				groupId, privateLayout, parentLayoutId, start, end,
				orderByComparator);
		}

		try {
			Group group = _groupLocalService.getGroup(groupId);

			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				groupId, privateLayout);

			if (layoutSet.isLayoutSetPrototypeLinkActive() &&
				!_mergeLayouts(
					group, layoutSet, groupId, privateLayout, parentLayoutId,
					start, end, orderByComparator)) {

				return layoutPersistence.findByG_P_P(
					groupId, privateLayout, parentLayoutId, start, end,
					orderByComparator);
			}

			List<Layout> layouts = layoutPersistence.findByG_P_P(
				groupId, privateLayout, parentLayoutId, start, end,
				orderByComparator);

			return _injectVirtualLayouts(
				group, layoutSet, layouts, parentLayoutId);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	/**
	 * Returns all the layouts that match the layout IDs and belong to the
	 * group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutIds the layout IDs of the layouts
	 * @return the matching layouts, or an empty list if no matches were found
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, long[] layoutIds)
		throws PortalException {

		List<Layout> layouts = new ArrayList<>();

		for (long layoutId : layoutIds) {
			layouts.add(getLayout(groupId, privateLayout, layoutId));
		}

		return layouts;
	}

	/**
	 * Returns all the layouts that match the type and belong to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  type the type of the layouts (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET})
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		return getLayouts(groupId, privateLayout, new String[] {type});
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  keywords keywords
	 * @param  types layout types
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getLayouts(
			groupId, 0, privateLayout, keywords, types, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that match the type and belong to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  types the layout's type (optionally {@link
	 *         LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 *         in {@link LayoutConstants}.
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, boolean privateLayout, String[] types)
		throws PortalException {

		Group group = _groupPersistence.findByPrimaryKey(groupId);

		List<Layout> layouts = layoutPersistence.findByG_P_T(
			groupId, privateLayout, types);

		if (!group.isUser()) {
			return layouts;
		}

		layouts = new ArrayList<>(layouts);

		Set<Long> checkedPlids = new HashSet<>();

		Queue<Long> checkParentLayoutIds = new ArrayDeque<>();

		checkParentLayoutIds.add(LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

		LayoutSet layoutSet = _layoutSetPersistence.findByG_P(
			groupId, privateLayout);

		while (!checkParentLayoutIds.isEmpty()) {
			long parentLayoutId = checkParentLayoutIds.poll();

			List<Layout> userGroupLayouts = _addUserGroupLayouts(
				group, layoutSet, Collections.emptyList(), parentLayoutId);

			for (Layout userGroupLayout : userGroupLayouts) {
				long userGroupPlid = userGroupLayout.getPlid();

				if (checkedPlids.add(userGroupPlid)) {
					layouts.add(userGroupLayout);

					checkParentLayoutIds.add(userGroupLayout.getLayoutId());
				}
			}
		}

		return layouts;
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return layoutPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user
	 * @param  privateLayout whether the layout is private to the group
	 * @param  keywords keywords
	 * @param  types layout types
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getLayouts(
			groupId, userId, privateLayout, keywords, types, new int[0], start,
			end, orderByComparator);
	}

	@Override
	public List<Layout> getLayouts(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return search(
			groupId, userId, privateLayout, keywords, false, types, statuses,
			start, end, orderByComparator);
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
	public LayoutReference[] getLayouts(
		long companyId, String portletId, String preferencesKey,
		String preferencesValue) {

		List<LayoutReference> layoutReferences = layoutFinder.findByC_P_P(
			companyId, portletId, preferencesKey, preferencesValue);

		return layoutReferences.toArray(new LayoutReference[0]);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  keywords keywords
	 * @param  types layout types
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, String keywords, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getLayouts(
			groupId, keywords, types, new int[0], start, end,
			orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  keywords keywords
	 * @param  types layout types
	 * @param  start the lower bound of the range of layouts
	 * @param  end the upper bound of the range of layouts (not inclusive)
	 * @param  orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 *         found
	 */
	@Override
	public List<Layout> getLayouts(
			long groupId, String keywords, String[] types, int[] statuses,
			int start, int end, OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayouts(groupId, start, end, orderByComparator);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, null, keywords, types, statuses, start, end,
				orderByComparator));

		List<Document> documents = hits.toList();

		List<Layout> layouts = new ArrayList<>(documents.size());

		for (Document document : documents) {
			Layout layout = fetchLayout(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

			if (layout == null) {
				indexer.delete(
					GetterUtil.getLong(document.get(Field.COMPANY_ID)),
					document.getUID());

				continue;
			}

			layouts.add(layout);
		}

		return layouts;
	}

	@Override
	public List<Layout> getLayoutsByLayoutPrototypeUuid(
		String layoutPrototypeUuid) {

		return layoutPersistence.findByLayoutPrototypeUuid(layoutPrototypeUuid);
	}

	@Override
	public int getLayoutsByLayoutPrototypeUuidCount(
		String layoutPrototypeUuid) {

		return layoutPersistence.countByLayoutPrototypeUuid(
			layoutPrototypeUuid);
	}

	@Override
	public int getLayoutsCount(Group group, boolean privateLayout)
		throws PortalException {

		return getLayoutsCount(group, privateLayout, true);
	}

	@Override
	public int getLayoutsCount(
			Group group, boolean privateLayout, boolean includeUserGroups)
		throws PortalException {

		int count = layoutPersistence.countByG_P(
			group.getGroupId(), privateLayout);

		if (!group.isUser() || !includeUserGroups) {
			return count;
		}

		long[] userGroupIds = _userPersistence.getUserGroupPrimaryKeys(
			group.getClassPK());

		if (userGroupIds.length != 0) {
			long userGroupClassNameId = _classNameLocalService.getClassNameId(
				UserGroup.class);

			for (long userGroupId : userGroupIds) {
				Group userGroupGroup = _groupPersistence.findByC_C_C(
					group.getCompanyId(), userGroupClassNameId, userGroupId);

				count += layoutPersistence.countByG_P(
					userGroupGroup.getGroupId(), privateLayout);
			}
		}

		return count;
	}

	@Override
	public int getLayoutsCount(
		Group group, boolean privateLayout, long parentLayoutId) {

		return layoutPersistence.countByG_P_P(
			group.getGroupId(), privateLayout, parentLayoutId);
	}

	@Override
	public int getLayoutsCount(
		Group group, boolean privateLayout, long[] layoutIds) {

		if (ArrayUtil.isEmpty(layoutIds)) {
			return 0;
		}

		DynamicQuery dynamicQuery = dynamicQuery();

		Property groupIdProperty = PropertyFactoryUtil.forName("groupId");

		dynamicQuery.add(groupIdProperty.eq(group.getGroupId()));

		Property privateLayoutProperty = PropertyFactoryUtil.forName(
			"privateLayout");

		dynamicQuery.add(privateLayoutProperty.eq(privateLayout));

		Property layoutIdProperty = PropertyFactoryUtil.forName("layoutId");

		dynamicQuery.add(layoutIdProperty.in(layoutIds));

		return GetterUtil.getInteger(dynamicQueryCount(dynamicQuery));
	}

	@Override
	public int getLayoutsCount(
			Group group, boolean privateLayout, String keywords, String[] types)
		throws PortalException {

		return getLayoutsCount(
			group.getGroupId(), 0, privateLayout, keywords, types);
	}

	@Override
	public int getLayoutsCount(long groupId) {
		return layoutPersistence.countByGroupId(groupId);
	}

	@Override
	public int getLayoutsCount(long groupId, boolean privateLayout) {
		return layoutPersistence.countByG_P(groupId, privateLayout);
	}

	@Override
	public int getLayoutsCount(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return layoutPersistence.countByG_P_P(
			groupId, privateLayout, parentLayoutId);
	}

	@Override
	public int getLayoutsCount(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types)
		throws PortalException {

		return searchCount(
			groupId, userId, privateLayout, keywords, false, types);
	}

	@Override
	public int getLayoutsCount(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int[] statuses)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayoutsCount(groupId, privateLayout);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, userId, privateLayout, keywords, types, statuses,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		return hits.getLength();
	}

	@Override
	public int getLayoutsCount(long groupId, String keywords, String[] types)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayoutsCount(groupId);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, null, keywords, types, new int[0], QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		return hits.getLength();
	}

	@Override
	public int getLayoutsCount(
			long groupId, String keywords, String[] types, int[] statuses)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayoutsCount(groupId);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, null, keywords, types, statuses, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null));

		return hits.getLength();
	}

	@Override
	public int getLayoutsCount(User user, boolean privateLayout)
		throws PortalException {

		return getLayoutsCount(user, privateLayout, true);
	}

	@Override
	public int getLayoutsCount(
			User user, boolean privateLayout, boolean includeUserGroups)
		throws PortalException {

		Group group = _groupPersistence.findByC_C_C(
			user.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class),
			user.getUserId());

		return getLayoutsCount(group, privateLayout, includeUserGroups);
	}

	@Override
	public List<Layout> getMasterLayouts(long groupId, long masterLayoutPlid) {
		return layoutPersistence.findByG_MLP(groupId, masterLayoutPlid);
	}

	@Override
	public int getMasterLayoutsCount(long groupId, long masterLayoutPlid) {
		return layoutPersistence.countByG_MLP(groupId, masterLayoutPlid);
	}

	/**
	 * Returns the layout ID to use for the next layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the layout ID to use for the next layout
	 */
	@Override
	public long getNextLayoutId(long groupId, boolean privateLayout) {
		long nextLayoutId = counterLocalService.increment(
			getCounterName(groupId, privateLayout));

		if (nextLayoutId == 1) {
			Layout layout = layoutPersistence.fetchByG_P_First(
				groupId, privateLayout, LayoutComparator.getInstance(false));

			if (layout != null) {
				nextLayoutId = layout.getLayoutId() + 1;

				counterLocalService.reset(
					getCounterName(groupId, privateLayout), nextLayoutId);
			}
		}

		return nextLayoutId;
	}

	@Override
	public Layout getParentLayout(Layout layout) throws PortalException {
		Layout parentLayout = null;

		if (layout instanceof VirtualLayout) {
			VirtualLayout virtualLayout = (VirtualLayout)layout;

			Layout sourceLayout = virtualLayout.getSourceLayout();

			parentLayout = getLayout(
				sourceLayout.getGroupId(), sourceLayout.isPrivateLayout(),
				sourceLayout.getParentLayoutId());

			parentLayout = new VirtualLayout(parentLayout, layout.getGroup());
		}
		else {
			parentLayout = getLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId());
		}

		return parentLayout;
	}

	@Override
	public List<Layout> getPublishedLayouts(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		GroupByStep groupByStep = _getPublishedLayoutsGroupByStep(
			groupId, DSLQueryFactoryUtil.select(LayoutTable.INSTANCE));

		return dslQuery(
			groupByStep.orderBy(
				orderByStep -> {
					if (orderByComparator == null) {
						return orderByStep.orderBy(
							LayoutTable.INSTANCE.priority.ascending());
					}

					return orderByStep.orderBy(
						LayoutTable.INSTANCE, orderByComparator);
				}
			).limit(
				start, end
			));
	}

	@Override
	public int getPublishedLayoutsCount(long groupId) {
		return dslQueryCount(
			_getPublishedLayoutsGroupByStep(
				groupId, DSLQueryFactoryUtil.count()));
	}

	@Override
	public List<Layout> getScopeGroupLayouts(long parentGroupId)
		throws PortalException {

		if (PropsValues.LAYOUT_SCOPE_GROUP_FINDER_ENABLED) {
			return layoutFinder.findByScopeGroup(parentGroupId);
		}

		Group parentGroup = _groupPersistence.findByPrimaryKey(parentGroupId);

		if (PropsValues.LAYOUT_SCOPE_GROUP_FINDER_THRESHOLD >= 0) {
			int count = _groupLocalService.getGroupsCount(
				parentGroup.getCompanyId(), Layout.class.getName(),
				parentGroupId);

			if (count >= PropsValues.LAYOUT_SCOPE_GROUP_FINDER_THRESHOLD) {
				return layoutFinder.findByScopeGroup(parentGroupId);
			}
		}

		List<Group> groups = _groupLocalService.getGroups(
			parentGroup.getCompanyId(), Layout.class.getName(), parentGroupId);

		List<Layout> layouts = new ArrayList<>(groups.size());

		for (Group group : groups) {
			layouts.add(layoutPersistence.findByPrimaryKey(group.getClassPK()));
		}

		return layouts;
	}

	/**
	 * Returns all the layouts within scope of the group.
	 *
	 * @param  parentGroupId the primary key of the group's parent group
	 * @param  privateLayout whether the layout is private to the group
	 * @return the layouts within scope of the group
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public List<Layout> getScopeGroupLayouts(
			long parentGroupId, boolean privateLayout)
		throws PortalException {

		if (PropsValues.LAYOUT_SCOPE_GROUP_FINDER_ENABLED) {
			return layoutFinder.findByScopeGroup(parentGroupId, privateLayout);
		}

		Group parentGroup = _groupPersistence.findByPrimaryKey(parentGroupId);

		if (PropsValues.LAYOUT_SCOPE_GROUP_FINDER_THRESHOLD >= 0) {
			int count = _groupLocalService.getGroupsCount(
				parentGroup.getCompanyId(), Layout.class.getName(),
				parentGroupId);

			if (count >= PropsValues.LAYOUT_SCOPE_GROUP_FINDER_THRESHOLD) {
				return layoutFinder.findByScopeGroup(
					parentGroupId, privateLayout);
			}
		}

		List<Group> groups = _groupLocalService.getGroups(
			parentGroup.getCompanyId(), Layout.class.getName(), parentGroupId);

		List<Layout> layouts = new ArrayList<>(groups.size());

		for (Group group : groups) {
			Layout layout = layoutPersistence.findByPrimaryKey(
				group.getClassPK());

			if (layout.isPrivateLayout() == privateLayout) {
				layouts.add(layout);
			}
		}

		return layouts;
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

		try {
			getLayoutByUuidAndGroupId(uuid, groupId, privateLayout);

			return true;
		}
		catch (NoSuchLayoutException noSuchLayoutException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchLayoutException);
			}
		}

		return false;
	}

	@Override
	public boolean hasLayouts(Group group) throws PortalException {
		List<LayoutSet> groupLayoutSets = _layoutSetPersistence.findByGroupId(
			group.getGroupId());

		for (LayoutSet layoutSet : groupLayoutSets) {
			if (layoutSet.getPageCount() > 0) {
				return true;
			}
		}

		if (!group.isUser()) {
			return false;
		}

		long[] userGroupIds = _userPersistence.getUserGroupPrimaryKeys(
			group.getClassPK());

		if (userGroupIds.length != 0) {
			long userGroupClassNameId = _classNameLocalService.getClassNameId(
				UserGroup.class);

			for (long userGroupId : userGroupIds) {
				Group userGroupGroup = _groupPersistence.findByC_C_C(
					group.getCompanyId(), userGroupClassNameId, userGroupId);

				List<LayoutSet> userGroupGroupLayoutSets =
					_layoutSetPersistence.findByGroupId(
						userGroupGroup.getGroupId());

				for (LayoutSet layoutSet : userGroupGroupLayoutSets) {
					if (layoutSet.getPageCount() > 0) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean hasLayouts(Group group, boolean privateLayout)
		throws PortalException {

		return hasLayouts(group, privateLayout, true);
	}

	@Override
	public boolean hasLayouts(
			Group group, boolean privateLayout, boolean includeUserGroups)
		throws PortalException {

		LayoutSet layoutSet = _layoutSetPersistence.findByG_P(
			group.getGroupId(), privateLayout);

		if (layoutSet.getPageCount() > 0) {
			return true;
		}

		if (!group.isUser() || !includeUserGroups) {
			return false;
		}

		long[] userGroupIds = _userPersistence.getUserGroupPrimaryKeys(
			group.getClassPK());

		if (userGroupIds.length != 0) {
			long userGroupClassNameId = _classNameLocalService.getClassNameId(
				UserGroup.class);

			for (long userGroupId : userGroupIds) {
				Group userGroupGroup = _groupPersistence.findByC_C_C(
					group.getCompanyId(), userGroupClassNameId, userGroupId);

				layoutSet = _layoutSetPersistence.findByG_P(
					userGroupGroup.getGroupId(), privateLayout);

				if (layoutSet.getPageCount() > 0) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if the group has any layouts;
	 * <code>false</code> otherwise.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @return <code>true</code> if the group has any layouts;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasLayouts(
		long groupId, boolean privateLayout, long parentLayoutId) {

		int count = layoutPersistence.countByG_P_P(
			groupId, privateLayout, parentLayoutId);

		if (count > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasLayouts(User user, boolean privateLayout)
		throws PortalException {

		return hasLayouts(user, privateLayout, true);
	}

	@Override
	public boolean hasLayouts(
			User user, boolean privateLayout, boolean includeUserGroups)
		throws PortalException {

		return hasLayouts(user.getGroup(), privateLayout, includeUserGroups);
	}

	@Override
	public boolean hasLayoutSetPrototypeLayout(
			long layoutSetPrototypeId, String layoutUuid)
		throws PortalException {

		return layoutLocalServiceHelper.hasLayoutSetPrototypeLayout(
			_layoutSetPrototypeLocalService.getLayoutSetPrototype(
				layoutSetPrototypeId),
			layoutUuid);
	}

	@Override
	public boolean hasLayoutSetPrototypeLayout(
			String layoutSetPrototypeUuid, long companyId, String layoutUuid)
		throws PortalException {

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeLocalService.
				getLayoutSetPrototypeByUuidAndCompanyId(
					layoutSetPrototypeUuid, companyId);

		return layoutLocalServiceHelper.hasLayoutSetPrototypeLayout(
			layoutSetPrototype, layoutUuid);
	}

	@Override
	public List<Layout> search(
			long groupId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return search(
			groupId, 0, privateLayout, keywords, searchOnlyByTitle, types,
			start, end, orderByComparator);
	}

	@Override
	public List<Layout> search(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return search(
			groupId, userId, privateLayout, keywords, searchOnlyByTitle, types,
			new int[0], start, end, orderByComparator);
	}

	@Override
	public List<Layout> search(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int[] statuses,
			int start, int end, OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayouts(
				groupId, privateLayout, statuses, start, end,
				orderByComparator);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, userId, privateLayout, keywords, searchOnlyByTitle,
				types, statuses, start, end, orderByComparator));

		List<Document> documents = hits.toList();

		List<Layout> layouts = new ArrayList<>(documents.size());

		for (Document document : documents) {
			Layout layout = fetchLayout(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

			if (layout == null) {
				indexer.delete(
					GetterUtil.getLong(document.get(Field.COMPANY_ID)),
					document.getUID());

				continue;
			}

			layouts.add(layout);
		}

		return layouts;
	}

	@Override
	public int searchCount(
			Group group, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types)
		throws PortalException {

		return searchCount(
			group.getGroupId(), 0, privateLayout, keywords, searchOnlyByTitle,
			types);
	}

	@Override
	public int searchCount(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types)
		throws PortalException {

		if (Validator.isNull(keywords)) {
			return getLayoutsCount(groupId, privateLayout);
		}

		Indexer<Layout> indexer = IndexerRegistryUtil.getIndexer(
			Layout.class.getName());

		Hits hits = indexer.search(
			_buildSearchContext(
				groupId, userId, privateLayout, keywords, searchOnlyByTitle,
				types, new int[0], QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));

		return hits.getLength();
	}

	/**
	 * Sets the layouts for the group, replacing and prioritizing all layouts of
	 * the parent layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  parentLayoutId the layout ID of the parent layout
	 * @param  layoutIds the layout IDs of the layouts
	 * @param  serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public void setLayouts(
			long groupId, boolean privateLayout, long parentLayoutId,
			long[] layoutIds, ServiceContext serviceContext)
		throws PortalException {

		if (layoutIds == null) {
			return;
		}

		if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			if (layoutIds.length < 1) {
				throw new RequiredLayoutException(
					RequiredLayoutException.AT_LEAST_ONE);
			}

			Layout layout = layoutPersistence.findByG_P_L(
				groupId, privateLayout, layoutIds[0]);

			LayoutType layoutType = layout.getLayoutType();

			if (!layoutType.isFirstPageable()) {
				throw new RequiredLayoutException(
					RequiredLayoutException.FIRST_LAYOUT_TYPE);
			}
		}

		Set<Long> layoutIdsSet = new LinkedHashSet<>();

		for (long layoutId : layoutIds) {
			layoutIdsSet.add(layoutId);
		}

		Set<Long> newLayoutIdsSet = new HashSet<>();

		List<Layout> layouts = layoutPersistence.findByG_P_P(
			groupId, privateLayout, parentLayoutId);

		for (Layout layout : layouts) {
			if (!layoutIdsSet.contains(layout.getLayoutId())) {
				deleteLayout(layout, serviceContext);
			}
			else {
				newLayoutIdsSet.add(layout.getLayoutId());
			}
		}

		int priority = 0;

		for (long layoutId : layoutIdsSet) {
			Layout layout = layoutPersistence.findByG_P_L(
				groupId, privateLayout, layoutId);

			layout.setPriority(priority++);

			layoutLocalService.updateLayout(layout);
		}
	}

	@Override
	public void updateAsset(
			long userId, Layout layout, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		_assetEntryLocalService.updateEntry(
			userId, layout.getGroupId(), layout.getCreateDate(),
			layout.getModifiedDate(), Layout.class.getName(), layout.getPlid(),
			layout.getUuid(), 0, assetCategoryIds, assetTagNames, true, false,
			null, null, null, null, ContentTypes.TEXT_HTML,
			layout.getName(LocaleUtil.getDefault()), null, null, null, null, 0,
			0, null);
	}

	/**
	 * Updates the friendly URL of the layout.
	 *
	 * @param  userId the primary key of the user
	 * @param  plid the primary key of the layout
	 * @param  friendlyURL the friendly URL to be assigned
	 * @param  languageId the primary key of the language
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateFriendlyURL(
			long userId, long plid, String friendlyURL, String languageId)
		throws PortalException {

		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		friendlyURL = layoutLocalServiceHelper.getFriendlyURL(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			StringPool.BLANK, friendlyURL, languageId);

		layoutLocalServiceHelper.validateFriendlyURL(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			friendlyURL, languageId);

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURL(
			userId, layout.getCompanyId(), layout.getGroupId(),
			layout.getPlid(), layout.isPrivateLayout(), friendlyURL, languageId,
			new ServiceContext());

		layout.setModifiedDate(new Date());

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		if (languageId.equals(defaultLanguageId)) {
			layout.setFriendlyURL(friendlyURL);
		}

		return layoutLocalService.updateLayout(layout);
	}

	@Override
	public Layout updateIconImage(long plid, byte[] bytes)
		throws PortalException {

		Layout layout = layoutPersistence.fetchByPrimaryKey(plid);

		if (layout == null) {
			return null;
		}

		PortalUtil.updateImageId(
			layout, bytes != null, bytes, "iconImageId", 0, 0, 0);

		return layoutLocalService.updateLayout(layout);
	}

	/**
	 * Updates the layout replacing its draft publish date.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  publishDate the date when draft was last published
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			Date publishDate)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		layout.setPublishDate(publishDate);

		return layoutPersistence.update(layout);
	}

	/**
	 * Updates the layout replacing its entity class name ID and primary key.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  classNameId the class name ID of the entity
	 * @param  classPK the primary key of the entity
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long classNameId, long classPK)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		layout.setClassNameId(classNameId);
		layout.setClassPK(classPK);

		return layoutPersistence.update(layout);
	}

	/**
	 * Updates the layout.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  parentLayoutId the layout ID of the layout's new parent layout
	 * @param  nameMap the locales and localized names to merge (optionally
	 *         <code>null</code>)
	 * @param  titleMap the locales and localized titles to merge (optionally
	 *         <code>null</code>)
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
	 *         To see how the URL is normalized when accessed, see {@link
	 *         com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 *         String)}.
	 * @param  hasIconImage whether the icon image will be updated
	 * @param  iconBytes the byte array of the layout's new icon image
	 * @param  styleBookEntryId the primary key of the style book entrys
	 * @param  faviconFileEntryId the file entry ID of the layout's new favicon
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @param  serviceContext the service context to be applied. Can set the
	 *         modification date and expando bridge attributes for the layout.
	 *         For layouts that are linked to a layout prototype, attributes
	 *         named <code>layoutPrototypeUuid</code> and
	 *         <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 *         provide the unique identifier of the source prototype and a
	 *         boolean to determine whether a link to it should be enabled to
	 *         activate propagation of changes made to the linked page in the
	 *         prototype.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, Map<Locale, String> nameMap,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			Map<Locale, String> keywordsMap, Map<Locale, String> robotsMap,
			String type, boolean hidden, Map<Locale, String> friendlyURLMap,
			boolean hasIconImage, byte[] iconBytes, long styleBookEntryId,
			long faviconFileEntryId, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		// Layout

		parentLayoutId = layoutLocalServiceHelper.getParentLayoutId(
			groupId, privateLayout, parentLayoutId);

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		String name = nameMap.get(LocaleUtil.getSiteDefault());

		if (Validator.isNull(name) && nameMap.isEmpty()) {
			throw new LayoutNameException(
				"Name is required for layout PLID " + layout.getPlid(),
				LayoutNameException.REQUIRED);
		}
		else if (Validator.isNull(name)) {
			List<String> values = new ArrayList<>(nameMap.values());

			name = values.get(0);
		}

		friendlyURLMap = layoutLocalServiceHelper.getFriendlyURLMap(
			groupId, privateLayout, layoutId, name, friendlyURLMap);

		layoutLocalServiceHelper.validate(
			groupId, privateLayout, layoutId, parentLayoutId,
			layout.getClassNameId(), layout.getClassPK(), name, type,
			friendlyURLMap, serviceContext);

		layoutLocalServiceHelper.validateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);

		_validateMasterLayout(masterLayoutPlid, layout.getPlid());

		if (parentLayoutId != layout.getParentLayoutId()) {
			layout.setParentPlid(
				_getParentPlid(groupId, privateLayout, parentLayoutId));

			int priority = layoutLocalServiceHelper.getNextPriority(
				groupId, privateLayout, parentLayoutId,
				layout.getSourcePrototypeLayoutUuid(), -1);

			layout.setPriority(priority);
		}

		layout.setModifiedDate(serviceContext.getModifiedDate(new Date()));
		layout.setParentLayoutId(parentLayoutId);
		layout.setNameMap(nameMap);
		layout.setTitleMap(titleMap);
		layout.setDescriptionMap(descriptionMap);
		layout.setKeywordsMap(keywordsMap);
		layout.setRobotsMap(robotsMap);
		layout.setType(type);
		layout.setHidden(hidden);
		layout.setFriendlyURL(friendlyURLMap.get(LocaleUtil.getSiteDefault()));

		PortalUtil.updateImageId(
			layout, hasIconImage, iconBytes, "iconImageId", 0, 0, 0);

		layout.setStyleBookEntryId(styleBookEntryId);
		layout.setFaviconFileEntryId(faviconFileEntryId);
		layout.setMasterLayoutPlid(masterLayoutPlid);

		boolean layoutUpdateable = ParamUtil.getBoolean(
			serviceContext, Sites.LAYOUT_UPDATEABLE, true);

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Group group = layout.getGroup();

		if (!group.isLayoutPrototype()) {
			typeSettingsUnicodeProperties.put(
				Sites.LAYOUT_UPDATEABLE, String.valueOf(layoutUpdateable));
		}

		if (privateLayout) {
			typeSettingsUnicodeProperties.put(
				"privateLayout", String.valueOf(privateLayout));
		}

		layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		String layoutPrototypeUuid = ParamUtil.getString(
			serviceContext, "layoutPrototypeUuid");

		if (Validator.isNotNull(layoutPrototypeUuid)) {
			layout.setLayoutPrototypeUuid(layoutPrototypeUuid);

			boolean applyLayoutPrototype = ParamUtil.getBoolean(
				serviceContext, "applyLayoutPrototype");

			boolean layoutPrototypeLinkEnabled = ParamUtil.getBoolean(
				serviceContext, "layoutPrototypeLinkEnabled");

			layout.setLayoutPrototypeLinkEnabled(layoutPrototypeLinkEnabled);

			if (applyLayoutPrototype) {
				serviceContext.setAttribute(
					"applyLayoutPrototype", Boolean.FALSE);

				_applyLayoutPrototype(
					layoutPrototypeUuid, layout, layoutPrototypeLinkEnabled);

				layout = layoutPersistence.findByG_P_L(
					groupId, privateLayout, layoutId);
			}
		}

		layout.setExpandoBridgeAttributes(serviceContext);

		layout = layoutLocalService.updateLayout(layout);

		// Layout friendly URLs

		_layoutFriendlyURLLocalService.updateLayoutFriendlyURLs(
			serviceContext.getUserId(), layout.getCompanyId(),
			layout.getGroupId(), layout.getPlid(), layout.isPrivateLayout(),
			friendlyURLMap, serviceContext);

		// Asset

		updateAsset(
			serviceContext.getUserId(), layout,
			serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return layout;
	}

	/**
	 * Updates the layout replacing its type settings.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  typeSettings the settings to load the unicode properties object.
	 *         See {@link UnicodeProperties #fastLoad(String)}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings)
		throws PortalException {

		Date date = new Date();

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		validateTypeSettingsProperties(layout, typeSettingsUnicodeProperties);

		layout.setModifiedDate(date);
		layout.setTypeSettings(typeSettingsUnicodeProperties.toString());

		if (layout.isSystem() && (layout.getClassPK() > 0)) {
			layout.setPublishDate(date);
		}

		return layoutPersistence.update(layout);
	}

	@Override
	public Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings, byte[] iconBytes, String themeId,
			String colorSchemeId, long styleBookEntryId, String css,
			long faviconFileEntryId, long masterLayoutPlid)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		validateTypeSettingsProperties(layout, typeSettingsUnicodeProperties);

		_validateMasterLayout(masterLayoutPlid, layout.getPlid());

		layout.setModifiedDate(new Date());
		layout.setTypeSettings(typeSettingsUnicodeProperties.toString());
		layout.setThemeId(themeId);
		layout.setColorSchemeId(colorSchemeId);
		layout.setStyleBookEntryId(styleBookEntryId);
		layout.setCss(css);
		layout.setFaviconFileEntryId(faviconFileEntryId);
		layout.setMasterLayoutPlid(masterLayoutPlid);

		PortalUtil.updateImageId(
			layout, iconBytes != null, iconBytes, "iconImageId", 0, 0, 0);

		return layoutPersistence.update(layout);
	}

	@Override
	public void updateLayoutContent(
			String data, Layout layout, long segmentsExperienceId)
		throws Exception {

		throw new UnsupportedOperationException();
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

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		layout.setModifiedDate(new Date());
		layout.setThemeId(themeId);
		layout.setColorSchemeId(colorSchemeId);
		layout.setCss(css);

		return layoutPersistence.update(layout);
	}

	/**
	 * Updates the layout replacing its master layout plid.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  masterLayoutPlid the primary key of the master layout
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateMasterLayoutPlid(
			long groupId, boolean privateLayout, long layoutId,
			long masterLayoutPlid)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		_validateMasterLayout(masterLayoutPlid, layout.getPlid());

		layout.setMasterLayoutPlid(masterLayoutPlid);

		return layoutPersistence.update(layout);
	}

	/**
	 * Updates the name of the layout.
	 *
	 * @param  layout the layout to be updated
	 * @param  name the layout's new name
	 * @param  languageId the primary key of the language. For more information
	 *         see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateName(Layout layout, String name, String languageId)
		throws PortalException {

		Date date = new Date();

		layoutLocalServiceHelper.validateName(name, languageId);

		layout.setModifiedDate(date);
		layout.setName(name, LocaleUtil.fromLanguageId(languageId));

		layout = layoutPersistence.update(layout);

		Group group = layout.getGroup();

		if (group.isLayoutPrototype()) {
			LayoutPrototype layoutPrototype =
				_layoutPrototypeLocalService.getLayoutPrototype(
					group.getClassPK());

			layoutPrototype.setModifiedDate(date);
			layoutPrototype.setName(
				name, LocaleUtil.fromLanguageId(languageId));

			_layoutPrototypePersistence.update(layoutPrototype);
		}

		return layout;
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

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		return updateName(layout, name, languageId);
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

		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		return updateName(layout, name, languageId);
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

		parentLayoutId = layoutLocalServiceHelper.getParentLayoutId(
			groupId, privateLayout, parentLayoutId);

		layoutLocalServiceHelper.validateParentLayoutId(
			groupId, privateLayout, layoutId, parentLayoutId);

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		if (parentLayoutId != layout.getParentLayoutId()) {
			layout.setParentPlid(
				_getParentPlid(groupId, privateLayout, parentLayoutId));

			int priority = layoutLocalServiceHelper.getNextPriority(
				groupId, privateLayout, parentLayoutId,
				layout.getSourcePrototypeLayoutUuid(), -1);

			layout.setPriority(priority);
		}

		layout.setModifiedDate(new Date());
		layout.setParentLayoutId(parentLayoutId);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			updateParentLayoutId(
				groupId, privateLayout, draftLayout.getLayoutId(),
				parentLayoutId);
		}

		return layoutLocalService.updateLayout(layout);
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

		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		if (layout.getParentPlid() == parentPlid) {
			return layout;
		}

		long parentLayoutId = LayoutConstants.DEFAULT_PARENT_LAYOUT_ID;

		if (parentPlid > 0) {
			Layout parentLayout = layoutPersistence.fetchByPrimaryKey(
				parentPlid);

			if (parentLayout != null) {
				parentLayoutId = parentLayout.getLayoutId();
			}
		}

		parentLayoutId = layoutLocalServiceHelper.getParentLayoutId(
			layout.getGroupId(), layout.isPrivateLayout(), parentLayoutId);

		layoutLocalServiceHelper.validateParentLayoutId(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			parentLayoutId);

		if (parentLayoutId != layout.getParentLayoutId()) {
			int priority = layoutLocalServiceHelper.getNextPriority(
				layout.getGroupId(), layout.isPrivateLayout(), parentLayoutId,
				layout.getSourcePrototypeLayoutUuid(), -1);

			layout.setPriority(priority);
		}

		layout.setModifiedDate(new Date());
		layout.setParentPlid(parentPlid);
		layout.setParentLayoutId(parentLayoutId);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			updateParentLayoutId(draftLayout.getPlid(), parentPlid);
		}

		return layoutPersistence.update(layout);
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

		Layout layout = updateParentLayoutId(plid, parentPlid);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			layoutLocalService.updatePriority(draftLayout.getPlid(), priority);
		}

		return layoutLocalService.updatePriority(layout, priority);
	}

	/**
	 * Updates the priorities of the layouts.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @throws PortalException
	 */
	@Override
	public void updatePriorities(long groupId, boolean privateLayout)
		throws PortalException {

		List<Layout> layouts = layoutPersistence.findByG_P(
			groupId, privateLayout);

		for (Layout layout : layouts) {
			int nextPriority = layoutLocalServiceHelper.getNextPriority(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId(),
				layout.getSourcePrototypeLayoutUuid(), layout.getPriority());

			layout.setPriority(nextPriority);

			layout = layoutPersistence.update(layout);

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout != null) {
				draftLayout.setPriority(nextPriority);

				layoutPersistence.update(draftLayout);
			}
		}
	}

	/**
	 * Updates the priority of the layout.
	 *
	 * @param  layout the layout to be updated
	 * @param  priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updatePriority(Layout layout, int priority)
		throws PortalException {

		if (layout.getPriority() == priority) {
			return layout;
		}

		int oldPriority = layout.getPriority();

		int nextPriority = layoutLocalServiceHelper.getNextPriority(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getParentLayoutId(), layout.getSourcePrototypeLayoutUuid(),
			priority);

		if (oldPriority == nextPriority) {
			return layout;
		}

		layout.setModifiedDate(new Date());
		layout.setPriority(nextPriority);

		layout = layoutPersistence.update(layout);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			draftLayout.setPriority(nextPriority);

			draftLayout = layoutPersistence.update(draftLayout);
		}

		List<Layout> layouts = layoutPersistence.findByG_P_P(
			layout.getGroupId(), layout.isPrivateLayout(),
			layout.getParentLayoutId());

		boolean lessThan = false;

		if (oldPriority < nextPriority) {
			lessThan = true;
		}

		layouts = ListUtil.sort(
			layouts, new LayoutPriorityComparator(layout, lessThan));

		if (layout.getParentLayoutId() ==
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {

			Layout firstLayout = layouts.get(0);

			layoutLocalServiceHelper.validateFirstLayout(firstLayout);
		}

		int newPriority = LayoutConstants.FIRST_PRIORITY;

		for (Layout curLayout : layouts) {
			int curNextPriority = layoutLocalServiceHelper.getNextPriority(
				layout.getGroupId(), layout.isPrivateLayout(),
				layout.getParentLayoutId(),
				curLayout.getSourcePrototypeLayoutUuid(), newPriority++);

			if (curLayout.getPriority() == curNextPriority) {
				continue;
			}

			curLayout.setModifiedDate(layout.getModifiedDate());
			curLayout.setPriority(curNextPriority);

			curLayout = layoutPersistence.update(curLayout);

			draftLayout = curLayout.fetchDraftLayout();

			if (draftLayout != null) {
				draftLayout.setPriority(nextPriority);

				layoutPersistence.update(draftLayout);
			}

			if (curLayout.equals(layout)) {
				layout = curLayout;
			}
		}

		return layout;
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

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		return updatePriority(layout, priority);
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

		Layout layout = getLayout(groupId, privateLayout, layoutId);

		int priority = layout.getPriority();

		Layout nextLayout = null;

		if (nextLayoutId > 0) {
			nextLayout = getLayout(groupId, privateLayout, nextLayoutId);
		}

		Layout previousLayout = null;

		if (previousLayoutId > 0) {
			previousLayout = getLayout(
				groupId, privateLayout, previousLayoutId);
		}

		if ((nextLayout != null) && (priority > nextLayout.getPriority())) {
			priority = nextLayout.getPriority();
		}
		else if ((previousLayout != null) &&
				 (priority < previousLayout.getPriority())) {

			priority = previousLayout.getPriority();
		}

		return updatePriority(layout, priority);
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

		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		return updatePriority(layout, priority);
	}

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public Layout updateStatus(
			long userId, long plid, int status, ServiceContext serviceContext)
		throws PortalException {

		// Layout

		Layout layout = layoutLocalService.getLayout(plid);

		layout.setModifiedDate(new Date());
		layout.setStatus(status);

		User user = _userLocalService.getUser(userId);

		layout.setStatusByUserId(user.getUserId());
		layout.setStatusByUserName(user.getFullName());

		Date statusDate = new Date();

		if (serviceContext != null) {
			statusDate = serviceContext.getModifiedDate(statusDate);
		}

		layout.setStatusDate(statusDate);

		if (layout.isDraftLayout() &&
			(status == WorkflowConstants.STATUS_APPROVED) &&
			GetterUtil.getBoolean(
				serviceContext.getAttribute("published"), true)) {

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.put(
				"published", Boolean.TRUE.toString());
		}

		if ((layout.getClassPK() > 0) &&
			(layout.getClassNameId() == _classNameLocalService.getClassNameId(
				Layout.class.getName())) &&
			GetterUtil.getBoolean(
				layout.getTypeSettingsProperty("autogenerated")) &&
			layout.isTypeAssetDisplay() &&
			(status == WorkflowConstants.STATUS_DRAFT)) {

			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			typeSettingsUnicodeProperties.remove("autogenerated");
		}

		layout = layoutPersistence.update(layout);

		// Asset

		if (status == WorkflowConstants.STATUS_APPROVED) {
			_assetEntryLocalService.updateEntry(
				Layout.class.getName(), layout.getPlid(),
				layout.getStatusDate(), null, true, false);
		}

		return layout;
	}

	/**
	 * Updates the layout replacing its style book entry ID.
	 *
	 * @param  groupId the primary key of the group
	 * @param  privateLayout whether the layout is private to the group
	 * @param  layoutId the layout ID of the layout
	 * @param  styleBookEntryId the primary key of the style book entry
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public Layout updateStyleBookEntryId(
			long groupId, boolean privateLayout, long layoutId,
			long styleBookEntryId)
		throws PortalException {

		Layout layout = layoutPersistence.findByG_P_L(
			groupId, privateLayout, layoutId);

		layout.setStyleBookEntryId(styleBookEntryId);

		return layoutPersistence.update(layout);
	}

	@Override
	public Layout updateType(long plid, String type) throws PortalException {
		Layout layout = layoutPersistence.findByPrimaryKey(plid);

		layout.setType(type);

		if (Objects.equals(type, LayoutConstants.TYPE_CONTENT) ||
			Objects.equals(type, LayoutConstants.TYPE_UTILITY)) {

			layout.setLayoutPrototypeUuid(StringPool.BLANK);
			layout.setLayoutPrototypeLinkEnabled(false);
		}

		return layoutLocalService.updateLayout(layout);
	}

	protected void validateTypeSettingsProperties(
			Layout layout, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException {

		String sitemapChangeFrequency =
			typeSettingsUnicodeProperties.getProperty("sitemap-changefreq");

		if (Validator.isNotNull(sitemapChangeFrequency) &&
			!sitemapChangeFrequency.equals("always") &&
			!sitemapChangeFrequency.equals("hourly") &&
			!sitemapChangeFrequency.equals("daily") &&
			!sitemapChangeFrequency.equals("weekly") &&
			!sitemapChangeFrequency.equals("monthly") &&
			!sitemapChangeFrequency.equals("yearly") &&
			!sitemapChangeFrequency.equals("never")) {

			throw new SitemapChangeFrequencyException();
		}

		String sitemapInclude = typeSettingsUnicodeProperties.getProperty(
			LayoutTypePortletConstants.SITEMAP_INCLUDE);

		if (Validator.isNotNull(sitemapInclude) &&
			!sitemapInclude.equals("0") && !sitemapInclude.equals("1")) {

			throw new SitemapIncludeException();
		}

		String sitemapPriority = typeSettingsUnicodeProperties.getProperty(
			"sitemap-priority");

		if (Validator.isNotNull(sitemapPriority)) {
			try {
				double priority = Double.parseDouble(sitemapPriority);

				if ((priority < 0) || (priority > 1)) {
					throw new SitemapPagePriorityException();
				}
			}
			catch (NumberFormatException numberFormatException) {
				throw new SitemapPagePriorityException(numberFormatException);
			}
		}

		boolean enableJavaScript =
			PropsValues.
				FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_LAYOUT_JAVASCRIPT;

		if (enableJavaScript) {
			String javaScript = typeSettingsUnicodeProperties.getProperty(
				"javascript");

			if (Validator.isNotNull(javaScript) &&
				(javaScript.contains("<script") ||
				 javaScript.contains("</script>"))) {

				throw new LayoutJavaScriptException();
			}
		}
		else {
			UnicodeProperties layoutTypeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			String javaScript = layoutTypeSettingsUnicodeProperties.getProperty(
				"javascript");

			typeSettingsUnicodeProperties.setProperty("javascript", javaScript);
		}
	}

	@BeanReference(type = LayoutLocalServiceHelper.class)
	protected LayoutLocalServiceHelper layoutLocalServiceHelper;

	private List<Layout> _addChildUserGroupLayouts(
			Group group, List<Layout> layouts)
		throws PortalException {

		List<Layout> childLayouts = new ArrayList<>(layouts.size());

		for (Layout layout : layouts) {
			Group layoutGroup = layout.getGroup();

			if (layoutGroup.isUserGroup()) {
				childLayouts.add(new VirtualLayout(layout, group));
			}
			else {
				childLayouts.add(layout);
			}
		}

		return childLayouts;
	}

	private List<Layout> _addUserGroupLayouts(
			Group group, LayoutSet layoutSet, List<Layout> layouts,
			long parentLayoutId)
		throws PortalException {

		layouts = new ArrayList<>(layouts);

		List<UserGroup> userUserGroups =
			_userGroupLocalService.getUserUserGroups(group.getClassPK());

		for (UserGroup userGroup : userUserGroups) {
			Group userGroupGroup = userGroup.getGroup();

			List<Layout> userGroupLayouts = getLayouts(
				userGroupGroup.getGroupId(), layoutSet.isPrivateLayout(),
				parentLayoutId);

			for (Layout userGroupLayout : userGroupLayouts) {
				layouts.add(new VirtualLayout(userGroupLayout, group));
			}
		}

		return layouts;
	}

	private List<Layout> _addUserGroupLayouts(
			Group group, LayoutSet layoutSet, List<Layout> layouts,
			long[] parentLayoutIds)
		throws PortalException {

		boolean copied = false;

		List<UserGroup> userUserGroups =
			_userGroupLocalService.getUserUserGroups(group.getClassPK());

		for (UserGroup userGroup : userUserGroups) {
			Group userGroupGroup = userGroup.getGroup();

			List<Layout> userGroupLayouts = getLayouts(
				userGroupGroup.getGroupId(), layoutSet.isPrivateLayout(),
				parentLayoutIds);

			for (Layout userGroupLayout : userGroupLayouts) {
				if (!copied) {
					layouts = new ArrayList<>(layouts);

					copied = true;
				}

				layouts.add(new VirtualLayout(userGroupLayout, group));
			}
		}

		return layouts;
	}

	private void _applyLayoutPrototype(
			String layoutPrototypeUuid, Layout layout,
			boolean layoutPrototypeLinkEnabled)
		throws PortalException {

		LayoutPrototype layoutPrototype =
			_layoutPrototypeLocalService.getLayoutPrototypeByUuidAndCompanyId(
				layoutPrototypeUuid, layout.getCompanyId());

		try {
			Sites sites = _sitesSnapshot.get();

			sites.applyLayoutPrototype(
				layoutPrototype, layout, layoutPrototypeLinkEnabled);
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (SystemException systemException) {
			throw systemException;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private SearchContext _buildSearchContext(
			long groupId, Boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return _buildSearchContext(
			groupId, 0, privateLayout, keywords, types, statuses, start, end,
			orderByComparator);
	}

	private SearchContext _buildSearchContext(
			long groupId, long userId, Boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int[] statuses,
			int start, int end, OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		SearchContext searchContext = new SearchContext();

		if (ArrayUtil.isNotEmpty(statuses)) {
			searchContext.setAttribute(Field.STATUS, statuses);
		}

		searchContext.setAttribute(Field.TITLE, keywords);
		searchContext.setAttribute(Field.TYPE, types);
		searchContext.setAttribute("paginationType", "more");
		searchContext.setAttribute("searchOnlyByTitle", searchOnlyByTitle);

		if (privateLayout != null) {
			searchContext.setAttribute(
				"privateLayout", String.valueOf(privateLayout));
		}

		Group group = _groupLocalService.getGroup(groupId);

		searchContext.setCompanyId(group.getCompanyId());

		searchContext.setEnd(end);
		searchContext.setGroupIds(new long[] {groupId});
		searchContext.setIncludeInternalAssetCategories(true);
		searchContext.setKeywords(keywords);
		searchContext.setStart(start);

		if (orderByComparator != null) {
			searchContext.setSorts(_getSortFromComparator(orderByComparator));
		}

		if (userId > 0) {
			searchContext.setUserId(userId);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

		return searchContext;
	}

	private SearchContext _buildSearchContext(
			long groupId, long userId, Boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return _buildSearchContext(
			groupId, userId, privateLayout, keywords, false, types, statuses,
			start, end, orderByComparator);
	}

	private void _deleteLayouts(
			long groupId, boolean privateLayout, boolean system,
			ServiceContext serviceContext)
		throws PortalException {

		List<Layout> layouts = layoutPersistence.findByG_P_P_S(
			groupId, privateLayout, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			system, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			new LayoutPriorityComparator(false));

		for (Layout layout : layouts) {
			try {
				layoutLocalService.deleteLayout(layout, serviceContext);
			}
			catch (NoSuchLayoutException noSuchLayoutException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchLayoutException);
				}
			}
		}
	}

	private String _generateFriendlyURLUUID() {
		UUID uuid = new UUID(
			SecureRandomUtil.nextLong(), SecureRandomUtil.nextLong());

		return StringPool.SLASH + uuid.toString();
	}

	private ChildLayout _getBrowsableChildLayout(
		List<String> browsableTypes, long groupId, long parentLayoutId,
		boolean privateLayout) {

		for (ChildLayout childLayout :
				_getChildLayouts(groupId, parentLayoutId, privateLayout)) {

			if (browsableTypes.contains(childLayout.getType())) {
				return childLayout;
			}

			ChildLayout browsableChildLayout = _getBrowsableChildLayout(
				browsableTypes, groupId, childLayout.getLayoutId(),
				privateLayout);

			if (browsableChildLayout != null) {
				return browsableChildLayout;
			}
		}

		return null;
	}

	private List<Layout> _getChildLayouts(
		LayoutSet layoutSet, long[] parentLayoutIds) {

		if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return layoutPersistence.findByG_P_P(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				parentLayoutIds);
		}

		try {
			Group group = _groupPersistence.findByPrimaryKey(
				layoutSet.getGroupId());

			if (layoutSet.isLayoutSetPrototypeLinkActive() &&
				!_mergeLayouts(
					group, layoutSet, layoutSet.getGroupId(),
					layoutSet.isPrivateLayout(), parentLayoutIds)) {

				return layoutPersistence.findByG_P_P(
					layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
					parentLayoutIds);
			}

			List<Layout> layouts = layoutPersistence.findByG_P_P(
				layoutSet.getGroupId(), layoutSet.isPrivateLayout(),
				parentLayoutIds);

			return _injectVirtualLayouts(
				group, layoutSet, layouts, parentLayoutIds);
		}
		catch (PortalException portalException) {
			throw new SystemException(portalException);
		}
	}

	private List<ChildLayout> _getChildLayouts(
		long groupId, long parentLayoutId, boolean privateLayout) {

		List<Object[]> results = dslQuery(
			DSLQueryFactoryUtil.select(
				LayoutTable.INSTANCE.plid, LayoutTable.INSTANCE.layoutId,
				LayoutTable.INSTANCE.type
			).from(
				LayoutTable.INSTANCE
			).where(
				LayoutTable.INSTANCE.groupId.eq(
					groupId
				).and(
					LayoutTable.INSTANCE.privateLayout.eq(privateLayout)
				).and(
					LayoutTable.INSTANCE.parentLayoutId.eq(parentLayoutId)
				).and(
					LayoutTable.INSTANCE.system.eq(false)
				)
			).orderBy(
				orderByStep -> orderByStep.orderBy(
					LayoutTable.INSTANCE, new LayoutPriorityComparator())
			));

		return TransformUtil.transform(
			results,
			values -> new ChildLayout(
				GetterUtil.getLong(values[1]), GetterUtil.getLong(values[0]),
				GetterUtil.getString(values[2])));
	}

	private Map<Locale, String> _getDraftFriendlyURLMap(
		long groupId, Map<Locale, String> friendlyURLMap) {

		Map<Locale, String> newFriendlyURLMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
			String friendlyURL = friendlyURLMap.get(locale);

			if (Validator.isNotNull(friendlyURL)) {
				newFriendlyURLMap.put(locale, _generateFriendlyURLUUID());
			}
		}

		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		if (newFriendlyURLMap.isEmpty() ||
			Validator.isNull(newFriendlyURLMap.get(siteDefaultLocale))) {

			newFriendlyURLMap.put(
				siteDefaultLocale, _generateFriendlyURLUUID());
		}

		return newFriendlyURLMap;
	}

	private DSLQuery _getDraftLayoutsClassPKsDSLQuery(long groupId) {
		return DSLQueryFactoryUtil.select(
			LayoutTable.INSTANCE.classPK
		).from(
			LayoutTable.INSTANCE
		).where(
			LayoutTable.INSTANCE.groupId.eq(
				groupId
			).and(
				LayoutTable.INSTANCE.classNameId.eq(
					_classNameLocalService.getClassNameId(Layout.class))
			)
		);
	}

	private long _getParentPlid(
		long groupId, boolean privateLayout, long parentLayoutId) {

		if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
			return 0;
		}

		Layout parentLayout = layoutPersistence.fetchByG_P_L(
			groupId, privateLayout, parentLayoutId);

		if (parentLayout == null) {
			return 0;
		}

		return parentLayout.getPlid();
	}

	private GroupByStep _getPublishedLayoutsGroupByStep(
		long groupId, FromStep fromStep) {

		return fromStep.from(
			LayoutTable.INSTANCE
		).where(
			LayoutTable.INSTANCE.groupId.eq(
				groupId
			).and(
				LayoutTable.INSTANCE.system.eq(false)
			).and(
				LayoutTable.INSTANCE.type.notIn(
					new String[] {LayoutConstants.TYPE_CONTENT}
				).or(
					LayoutTable.INSTANCE.plid.in(
						DSLQueryFactoryUtil.select(
							LayoutTable.INSTANCE.classPK
						).from(
							LayoutTable.INSTANCE
						).where(
							LayoutTable.INSTANCE.groupId.eq(
								groupId
							).and(
								LayoutTable.INSTANCE.classNameId.eq(
									_classNameLocalService.getClassNameId(
										Layout.class)
								).and(
									LayoutTable.INSTANCE.typeSettings.like(
										"%published=true%")
								)
							)
						))
				).or(
					LayoutTable.INSTANCE.status.eq(
						WorkflowConstants.STATUS_APPROVED
					).and(
						LayoutTable.INSTANCE.hidden.eq(false)
					).and(
						LayoutTable.INSTANCE.plid.notIn(
							_getDraftLayoutsClassPKsDSLQuery(groupId))
					)
				).withParentheses()
			)
		);
	}

	private Sort _getSortFromComparator(
		OrderByComparator<Layout> orderByComparator) {

		String[] fields = orderByComparator.getOrderByFields();

		if (ArrayUtil.contains(fields, "score")) {
			return new Sort(null, Sort.SCORE_TYPE, false);
		}

		boolean reverse = !orderByComparator.isAscending();
		String field = fields[0];

		return new Sort(field, Sort.LONG_TYPE, reverse);
	}

	private List<Layout> _injectVirtualLayouts(
			Group group, LayoutSet layoutSet, List<Layout> layouts,
			long parentLayoutId)
		throws PortalException {

		if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return layouts;
		}

		if (group.isUser()) {
			_virtualLayoutTargetGroupId.set(group.getGroupId());

			if (parentLayoutId == LayoutConstants.DEFAULT_PARENT_LAYOUT_ID) {
				return _addUserGroupLayouts(
					group, layoutSet, layouts, parentLayoutId);
			}

			return _addChildUserGroupLayouts(group, layouts);
		}

		if (group.isUserGroup() &&
			(parentLayoutId != LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

			long targetGroupId = _virtualLayoutTargetGroupId.get();

			if (targetGroupId != GroupConstants.DEFAULT_LIVE_GROUP_ID) {
				Group targetGroup = _groupLocalService.getGroup(targetGroupId);

				return _addChildUserGroupLayouts(targetGroup, layouts);
			}
		}

		return layouts;
	}

	private List<Layout> _injectVirtualLayouts(
			Group group, LayoutSet layoutSet, List<Layout> layouts,
			long[] parentLayoutIds)
		throws PortalException {

		if (MergeLayoutPrototypesThreadLocal.isInProgress()) {
			return layouts;
		}

		if (group.isUser()) {
			_virtualLayoutTargetGroupId.set(group.getGroupId());

			if (ArrayUtil.contains(
					parentLayoutIds,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

				_addUserGroupLayouts(
					group, layoutSet, layouts, parentLayoutIds);

				if (parentLayoutIds.length == 1) {
					return layouts;
				}
			}

			return _addChildUserGroupLayouts(group, layouts);
		}

		if (group.isUserGroup()) {
			long targetGroupId = _virtualLayoutTargetGroupId.get();

			if (targetGroupId != GroupConstants.DEFAULT_LIVE_GROUP_ID) {
				Group targetGroup = _groupLocalService.getGroup(targetGroupId);

				return _addChildUserGroupLayouts(targetGroup, layouts);
			}
		}

		return layouts;
	}

	private boolean _mergeLayout(Layout layout, Object... arguments)
		throws PortalException {

		if (MergeLayoutPrototypesThreadLocal.isInProgress() ||
			StartupHelperUtil.isUpgrading()) {

			return false;
		}

		Group group = layout.getGroup();

		if (MergeLayoutPrototypesThreadLocal.isMergeComplete(
				"getLayout", arguments) &&
			!group.isUser()) {

			return false;
		}

		if (Validator.isNull(layout.getLayoutPrototypeUuid()) &&
			Validator.isNull(layout.getSourcePrototypeLayoutUuid())) {

			return false;
		}

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		LayoutSet layoutSet = layout.getLayoutSet();

		try {
			WorkflowThreadLocal.setEnabled(false);

			Sites sites = _sitesSnapshot.get();

			sites.mergeLayoutPrototypeLayout(group, layout);

			if (Validator.isNotNull(layout.getSourcePrototypeLayoutUuid())) {
				sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);
			}
		}
		catch (CTTransactionException | PortalException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setMergeComplete(
				"getLayout", arguments);
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}

		return true;
	}

	private boolean _mergeLayouts(
		Group group, LayoutSet layoutSet, Object... arguments) {

		if ((MergeLayoutPrototypesThreadLocal.isMergeComplete(
				"getLayouts", arguments) &&
			 !group.isUser()) ||
			StartupHelperUtil.isUpgrading()) {

			return false;
		}

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			Sites sites = _sitesSnapshot.get();

			if (sites.isLayoutSetMergeable(group, layoutSet)) {
				WorkflowThreadLocal.setEnabled(false);

				sites.mergeLayoutSetPrototypeLayouts(group, layoutSet);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to merge layouts for site template", exception);
			}
		}
		finally {
			MergeLayoutPrototypesThreadLocal.setMergeComplete(
				"getLayouts", arguments);
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}

		return true;
	}

	private void _resetPortalPreferences(Layout layout) {
		String namespace = CustomizedPages.namespacePlid(layout.getPlid());

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			PortalPreferencesTable.INSTANCE
		).from(
			PortalPreferencesTable.INSTANCE
		).innerJoinON(
			PortalPreferenceValueTable.INSTANCE,
			PortalPreferenceValueTable.INSTANCE.portalPreferencesId.eq(
				PortalPreferencesTable.INSTANCE.portalPreferencesId)
		).where(
			PortalPreferencesTable.INSTANCE.ownerType.eq(
				ResourceConstants.SCOPE_INDIVIDUAL
			).and(
				PortalPreferenceValueTable.INSTANCE.namespace.eq(namespace)
			)
		);

		for (PortalPreferences portalPreferencesModel :
				_portalPreferencesPersistence.<List<PortalPreferences>>dslQuery(
					dslQuery)) {

			com.liferay.portal.kernel.portlet.PortalPreferences
				portalPreferences =
					_portalPreferenceValueLocalService.getPortalPreferences(
						portalPreferencesModel, false);

			portalPreferences.resetValues(namespace);
		}
	}

	private void _validateMasterLayout(long masterLayoutPlid, long plid)
		throws PortalException {

		if (masterLayoutPlid == plid) {
			throw new MasterLayoutException(
				"Master page cannot point to itself");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutLocalServiceImpl.class);

	private static final Snapshot<Sites> _sitesSnapshot = new Snapshot<>(
		LayoutLocalServiceImpl.class, Sites.class);
	private static final ThreadLocal<Long> _virtualLayoutTargetGroupId =
		new CentralizedThreadLocal<>(
			LayoutLocalServiceImpl.class + "._virtualLayoutTargetGroupId",
			() -> GroupConstants.DEFAULT_LIVE_GROUP_ID);

	@BeanReference(type = AssetEntryLocalService.class)
	private AssetEntryLocalService _assetEntryLocalService;

	@BeanReference(type = ClassNameLocalService.class)
	private ClassNameLocalService _classNameLocalService;

	@BeanReference(type = ExpandoRowLocalService.class)
	private ExpandoRowLocalService _expandoRowLocalService;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = ImageLocalService.class)
	private ImageLocalService _imageLocalService;

	@BeanReference(type = LayoutFriendlyURLLocalService.class)
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@BeanReference(type = LayoutFriendlyURLPersistence.class)
	private LayoutFriendlyURLPersistence _layoutFriendlyURLPersistence;

	@BeanReference(type = LayoutPrototypeLocalService.class)
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@BeanReference(type = LayoutPrototypePersistence.class)
	private LayoutPrototypePersistence _layoutPrototypePersistence;

	@BeanReference(type = LayoutSetLocalService.class)
	private LayoutSetLocalService _layoutSetLocalService;

	@BeanReference(type = LayoutSetPersistence.class)
	private LayoutSetPersistence _layoutSetPersistence;

	@BeanReference(type = LayoutSetPrototypeLocalService.class)
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

	@BeanReference(type = PortalPreferencesPersistence.class)
	private PortalPreferencesPersistence _portalPreferencesPersistence;

	@BeanReference(type = PortalPreferenceValueLocalService.class)
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@BeanReference(type = PortletPreferencesLocalService.class)
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@BeanReference(type = RatingsStatsLocalService.class)
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

	@BeanReference(type = ResourcePermissionLocalService.class)
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@BeanReference(type = ResourcePermissionPersistence.class)
	private ResourcePermissionPersistence _resourcePermissionPersistence;

	@BeanReference(type = UserGroupLocalService.class)
	private UserGroupLocalService _userGroupLocalService;

	@BeanReference(type = UserLocalService.class)
	private UserLocalService _userLocalService;

	@BeanReference(type = UserPersistence.class)
	private UserPersistence _userPersistence;

	@BeanReference(type = WorkflowDefinitionLinkLocalService.class)
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@BeanReference(type = WorkflowInstanceLinkLocalService.class)
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	private class ChildLayout {

		public ChildLayout(long layoutId, long plid, String type) {
			_layoutId = layoutId;
			_plid = plid;
			_type = type;
		}

		public long getLayoutId() {
			return _layoutId;
		}

		public long getPlid() {
			return _plid;
		}

		public String getType() {
			return _type;
		}

		private final long _layoutId;
		private final long _plid;
		private final String _type;

	}

}