/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Layout. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutLocalService
 * @generated
 */
public class LayoutLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layout the layout
	 * @return the layout that was added
	 */
	public static Layout addLayout(Layout layout) {
		return getService().addLayout(layout);
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
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @param nameMap the layout's locales and localized names
	 * @param titleMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link UnicodeProperties #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param system whether the layout is of system type
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param masterLayoutPlid the primary key of the master layout
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be set to specify whether site
	 administrators can modify this page within their site. For
	 layouts that are created from a layout prototype, attributes
	 named <code>layoutPrototypeUuid</code> and
	 <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 provide the unique identifier of the source prototype and a
	 boolean to determine whether a link to it should be enabled to
	 activate propagation of changes made to the linked page in the
	 prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, long classNameId,
			long classPK, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden, boolean system,
			Map<java.util.Locale, String> friendlyURLMap, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, classNameId, classPK, nameMap, titleMap,
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
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param nameMap the layout's locales and localized names
	 * @param titleMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link UnicodeProperties #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param system whether the layout is of system type
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be set to specify whether site
	 administrators can modify this page within their site. For
	 layouts that are created from a layout prototype, attributes
	 named <code>layoutPrototypeUuid</code> and
	 <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 provide the unique identifier of the source prototype and a
	 boolean to determine whether a link to it should be enabled to
	 activate propagation of changes made to the linked page in the
	 prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden, boolean system,
			Map<java.util.Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, nameMap, titleMap, descriptionMap, keywordsMap,
			robotsMap, type, typeSettings, hidden, system, friendlyURLMap,
			serviceContext);
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
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID})
	 * @param nameMap the layout's locales and localized names
	 * @param titleMap the layout's locales and localized titles
	 * @param descriptionMap the layout's locales and localized descriptions
	 * @param keywordsMap the layout's locales and localized keywords
	 * @param robotsMap the layout's locales and localized robots
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link UnicodeProperties #fastLoad(String)}.
	 * @param hidden whether the layout is hidden
	 * @param friendlyURLMap the layout's locales and localized friendly URLs.
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date, modification
	 date, and expando bridge attributes for the layout. For layouts
	 that belong to a layout set prototype, an attribute named
	 <code>layoutUpdateable</code> can be set to specify whether site
	 administrators can modify this page within their site. For
	 layouts that are created from a layout prototype, attributes
	 named <code>layoutPrototypeUuid</code> and
	 <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 provide the unique identifier of the source prototype and a
	 boolean to determine whether a link to it should be enabled to
	 activate propagation of changes made to the linked page in the
	 prototype.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			String typeSettings, boolean hidden,
			Map<java.util.Locale, String> friendlyURLMap,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, nameMap, titleMap, descriptionMap, keywordsMap,
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
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID}). The possible
	 values can be found in {@link LayoutConstants}.
	 * @param name the layout's name (optionally {@link
	 PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_NAME} or {@link
	 PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_NAME}). The default values
	 can be overridden in <code>portal-ext.properties</code> by
	 specifying new values for the corresponding properties defined in
	 {@link PropsValues}
	 * @param title the layout's title
	 * @param description the layout's description
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param hidden whether the layout is hidden
	 * @param system whether the layout is of system type
	 * @param friendlyURL the friendly URL of the layout (optionally {@link
	 PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_FRIENDLY_URL} or {@link
	 PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_FRIENDLY_URL}). The
	 default values can be overridden in
	 <code>portal-ext.properties</code> by specifying new values for
	 the corresponding properties defined in {@link PropsValues}. To
	 see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date and modification
	 date for the layout. For layouts that belong to a layout set
	 prototype, an attribute named <code>layoutUpdateable</code> can
	 be set to specify whether site administrators can modify this
	 page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, String name,
			String title, String description, String type, boolean hidden,
			boolean system, String friendlyURL, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, name, title, description, type, hidden, system,
			friendlyURL, serviceContext);
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
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout (optionally
	 {@link LayoutConstants#DEFAULT_PARENT_LAYOUT_ID}). The possible
	 values can be found in {@link LayoutConstants}.
	 * @param name the layout's name (optionally {@link
	 PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_NAME} or {@link
	 PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_NAME}). The default values
	 can be overridden in <code>portal-ext.properties</code> by
	 specifying new values for the corresponding properties defined in
	 {@link PropsValues}
	 * @param title the layout's title
	 * @param description the layout's description
	 * @param type the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @param hidden whether the layout is hidden
	 * @param friendlyURL the friendly URL of the layout (optionally {@link
	 PropsValues#DEFAULT_USER_PRIVATE_LAYOUT_FRIENDLY_URL} or {@link
	 PropsValues#DEFAULT_USER_PUBLIC_LAYOUT_FRIENDLY_URL}). The
	 default values can be overridden in
	 <code>portal-ext.properties</code> by specifying new values for
	 the corresponding properties defined in {@link PropsValues}. To
	 see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param serviceContext the service context to be applied. Must set the
	 UUID for the layout. Can set the creation date and modification
	 date for the layout. For layouts that belong to a layout set
	 prototype, an attribute named <code>layoutUpdateable</code> can
	 be set to specify whether site administrators can modify this
	 page within their site.
	 * @return the layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout addLayout(
			String externalReferenceCode, long userId, long groupId,
			boolean privateLayout, long parentLayoutId, String name,
			String title, String description, String type, boolean hidden,
			String friendlyURL, ServiceContext serviceContext)
		throws PortalException {

		return getService().addLayout(
			externalReferenceCode, userId, groupId, privateLayout,
			parentLayoutId, name, title, description, type, hidden, friendlyURL,
			serviceContext);
	}

	public static Layout copyLayout(
			long userId, long groupId, boolean privateLayout,
			Map<java.util.Locale, String> nameMap, boolean hidden,
			boolean system, boolean copyPermissions, long sourcePlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().copyLayout(
			userId, groupId, privateLayout, nameMap, hidden, system,
			copyPermissions, sourcePlid, serviceContext);
	}

	public static Layout copyLayoutContent(
			Layout sourceLayout, Layout targetLayout)
		throws Exception {

		return getService().copyLayoutContent(sourceLayout, targetLayout);
	}

	public static Layout copyLayoutContent(
			long sourceSegmentsExperienceId, Layout sourceLayout,
			long targetSegmentsExperienceId, Layout targetLayout)
		throws Exception {

		return getService().copyLayoutContent(
			sourceSegmentsExperienceId, sourceLayout,
			targetSegmentsExperienceId, targetLayout);
	}

	/**
	 * Creates a new layout with the primary key. Does not add the layout to the database.
	 *
	 * @param plid the primary key for the new layout
	 * @return the new layout
	 */
	public static Layout createLayout(long plid) {
		return getService().createLayout(plid);
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
	 * Deletes the layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layout the layout
	 * @return the layout that was removed
	 * @throws PortalException
	 */
	public static Layout deleteLayout(Layout layout) throws PortalException {
		return getService().deleteLayout(layout);
	}

	/**
	 * Deletes the layout, its child layouts, and its associated resources.
	 *
	 * @param layout the layout
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteLayout(
			Layout layout, ServiceContext serviceContext)
		throws PortalException {

		getService().deleteLayout(layout, serviceContext);
	}

	/**
	 * Deletes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param plid the primary key of the layout
	 * @return the layout that was removed
	 * @throws PortalException if a layout with the primary key could not be found
	 */
	public static Layout deleteLayout(long plid) throws PortalException {
		return getService().deleteLayout(plid);
	}

	/**
	 * Deletes the layout with the layout ID, also deleting the layout's child
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

	/**
	 * Deletes the group's private or non-private layouts, also deleting the
	 * layouts' child layouts, and associated resources.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param serviceContext the service context to be applied. The parent
	 layout set's page count will be updated by default, unless an
	 attribute named <code>updatePageCount</code> is set to
	 <code>false</code>.
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteLayouts(
			long groupId, boolean privateLayout, ServiceContext serviceContext)
		throws PortalException {

		getService().deleteLayouts(groupId, privateLayout, serviceContext);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutModelImpl</code>.
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

	public static Layout fetchDefaultLayout(
		long groupId, boolean privateLayout) {

		return getService().fetchDefaultLayout(groupId, privateLayout);
	}

	public static Layout fetchDraftLayout(long plid) {
		return getService().fetchDraftLayout(plid);
	}

	public static Map<Layout, Layout> fetchDraftLayouts(List<Layout> layouts) {
		return getService().fetchDraftLayouts(layouts);
	}

	public static Layout fetchFirstLayout(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getService().fetchFirstLayout(
			groupId, privateLayout, parentLayoutId);
	}

	public static Layout fetchFirstLayout(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		return getService().fetchFirstLayout(
			groupId, privateLayout, parentLayoutId, hidden);
	}

	public static Layout fetchLayout(long plid) {
		return getService().fetchLayout(plid);
	}

	public static Layout fetchLayout(
		long groupId, boolean privateLayout, long layoutId) {

		return getService().fetchLayout(groupId, privateLayout, layoutId);
	}

	public static Layout fetchLayout(
		String uuid, long groupId, boolean privateLayout) {

		return getService().fetchLayout(uuid, groupId, privateLayout);
	}

	public static Layout fetchLayoutByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static Layout fetchLayoutByFriendlyURL(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getService().fetchLayoutByFriendlyURL(
			groupId, privateLayout, friendlyURL);
	}

	public static Layout fetchLayoutByIconImageId(
		boolean privateLayout, long iconImageId) {

		return getService().fetchLayoutByIconImageId(
			privateLayout, iconImageId);
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	public static Layout fetchLayoutByUuidAndGroupId(
		String uuid, long groupId, boolean privateLayout) {

		return getService().fetchLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns all the layouts that match the type and belong to the group,
	 * including the ones marked as System.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param type the type of the layouts (optiona.lly {@link
	 LayoutConstants#TYPE_PORTLET})
	 * @return the matching layouts, or an empty list if no matches were found
	 */
	public static List<Layout> getAllLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		return getService().getAllLayouts(groupId, privateLayout, type);
	}

	public static Layout getBrowsableLayout(Layout layout) {
		return getService().getBrowsableLayout(layout);
	}

	/**
	 * Returns the primary key of the default layout for the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary key of the default layout for the group (optionally
	 {@link LayoutConstants#DEFAULT_PLID})
	 */
	public static long getDefaultPlid(long groupId) {
		return getService().getDefaultPlid(groupId);
	}

	/**
	 * Returns primary key of the matching default layout for the group
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
	 * Returns primary key of the default portlet layout for the group
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param portletId the primary key of the portlet
	 * @return the primary key of the default portlet layout for the group;
	 {@link LayoutConstants#DEFAULT_PLID} otherwise
	 * @throws PortalException
	 */
	public static long getDefaultPlid(
			long groupId, boolean privateLayout, String portletId)
		throws PortalException {

		return getService().getDefaultPlid(groupId, privateLayout, portletId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the layout for the friendly URL.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param friendlyURL the friendly URL of the layout
	 * @return the layout for the friendly URL
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout getFriendlyURLLayout(
			long groupId, boolean privateLayout, String friendlyURL)
		throws PortalException {

		return getService().getFriendlyURLLayout(
			groupId, privateLayout, friendlyURL);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout with the primary key.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout
	 * @throws PortalException if a layout with the primary key could not be found
	 */
	public static Layout getLayout(long plid) throws PortalException {
		return getService().getLayout(plid);
	}

	/**
	 * Returns the layout matching the layout ID, group, and privacy; throws a
	 * {@link NoSuchLayoutException} otherwise.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @return the matching layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout getLayout(
			long groupId, boolean privateLayout, long layoutId)
		throws PortalException {

		return getService().getLayout(groupId, privateLayout, layoutId);
	}

	public static Layout getLayoutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static Layout getLayoutByFriendlyURL(
			long groupId, boolean privateLayout, String friendlyURL)
		throws PortalException {

		return getService().getLayoutByFriendlyURL(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the layout matching the UUID, group, and privacy.
	 *
	 * @param uuid the layout's UUID
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layout
	 * @throws PortalException if a matching layout could not be found
	 */
	public static Layout getLayoutByUuidAndGroupId(
			String uuid, long groupId, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutByUuidAndGroupId(
			uuid, groupId, privateLayout);
	}

	public static Map<Long, List<Layout>> getLayoutChildLayouts(
		List<Layout> parentLayouts) {

		return getService().getLayoutChildLayouts(parentLayouts);
	}

	public static List<Layout> getLayouts(
		java.util.Collection<Serializable> primaryKeys) {

		return getService().getLayouts(primaryKeys);
	}

	/**
	 * Returns a range of all the layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of layouts
	 */
	public static List<Layout> getLayouts(int start, int end) {
		return getService().getLayouts(start, end);
	}

	public static List<Layout> getLayouts(long companyId) {
		return getService().getLayouts(companyId);
	}

	/**
	 * Returns all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(long groupId, boolean privateLayout) {
		return getService().getLayouts(groupId, privateLayout);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getLayouts(
			groupId, privateLayout, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getLayouts(
			groupId, privateLayout, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the layouts belonging to the group that are children of the
	 * parent layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getService().getLayouts(groupId, privateLayout, parentLayoutId);
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
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @param incomplete whether the layout is incomplete
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean incomplete, int start, int end) {

		return getService().getLayouts(
			groupId, privateLayout, parentLayoutId, incomplete, start, end);
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
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean incomplete, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getLayouts(
			groupId, privateLayout, parentLayoutId, incomplete, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that match the layout IDs and belong to the
	 * group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutIds the layout IDs of the layouts
	 * @return the matching layouts, or an empty list if no matches were found
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, long[] layoutIds)
		throws PortalException {

		return getService().getLayouts(groupId, privateLayout, layoutIds);
	}

	/**
	 * Returns all the layouts that match the type and belong to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param type the type of the layouts (optionally {@link
	 LayoutConstants#TYPE_PORTLET})
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String type)
		throws PortalException {

		return getService().getLayouts(groupId, privateLayout, type);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param keywords keywords
	 * @param types layout types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, privateLayout, keywords, types, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layouts that match the type and belong to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param types the layout's type (optionally {@link
	 LayoutConstants#TYPE_PORTLET}). The possible types can be found
	 in {@link LayoutConstants}.
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, boolean privateLayout, String[] types)
		throws PortalException {

		return getService().getLayouts(groupId, privateLayout, types);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getLayouts(groupId, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user
	 * @param privateLayout whether the layout is private to the group
	 * @param keywords keywords
	 * @param types layout types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, userId, privateLayout, keywords, types, start, end,
			orderByComparator);
	}

	public static List<Layout> getLayouts(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int[] statuses, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, userId, privateLayout, keywords, types, statuses, start,
			end, orderByComparator);
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
	public static com.liferay.portal.kernel.model.LayoutReference[] getLayouts(
		long companyId, String portletId, String preferencesKey,
		String preferencesValue) {

		return getService().getLayouts(
			companyId, portletId, preferencesKey, preferencesValue);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param keywords keywords
	 * @param types layout types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, String keywords, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, keywords, types, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layouts belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param keywords keywords
	 * @param types layout types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the layouts
	 * @return the matching layouts, or <code>null</code> if no matches were
	 found
	 */
	public static List<Layout> getLayouts(
			long groupId, String keywords, String[] types, int[] statuses,
			int start, int end, OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().getLayouts(
			groupId, keywords, types, statuses, start, end, orderByComparator);
	}

	public static List<Layout> getLayoutsByLayoutPrototypeUuid(
		String layoutPrototypeUuid) {

		return getService().getLayoutsByLayoutPrototypeUuid(
			layoutPrototypeUuid);
	}

	public static int getLayoutsByLayoutPrototypeUuidCount(
		String layoutPrototypeUuid) {

		return getService().getLayoutsByLayoutPrototypeUuidCount(
			layoutPrototypeUuid);
	}

	/**
	 * Returns all the layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the layouts
	 * @param companyId the primary key of the company
	 * @return the matching layouts, or an empty list if no matches were found
	 */
	public static List<Layout> getLayoutsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getLayoutsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of layouts matching the UUID and company.
	 *
	 * @param uuid the UUID of the layouts
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layouts, or an empty list if no matches were found
	 */
	public static List<Layout> getLayoutsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getLayoutsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of layouts.
	 *
	 * @return the number of layouts
	 */
	public static int getLayoutsCount() {
		return getService().getLayoutsCount();
	}

	public static int getLayoutsCount(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutsCount(group, privateLayout);
	}

	public static int getLayoutsCount(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout,
			boolean includeUserGroups)
		throws PortalException {

		return getService().getLayoutsCount(
			group, privateLayout, includeUserGroups);
	}

	public static int getLayoutsCount(
		com.liferay.portal.kernel.model.Group group, boolean privateLayout,
		long parentLayoutId) {

		return getService().getLayoutsCount(
			group, privateLayout, parentLayoutId);
	}

	public static int getLayoutsCount(
		com.liferay.portal.kernel.model.Group group, boolean privateLayout,
		long[] layoutIds) {

		return getService().getLayoutsCount(group, privateLayout, layoutIds);
	}

	public static int getLayoutsCount(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout,
			String keywords, String[] types)
		throws PortalException {

		return getService().getLayoutsCount(
			group, privateLayout, keywords, types);
	}

	public static int getLayoutsCount(long groupId) {
		return getService().getLayoutsCount(groupId);
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
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types)
		throws PortalException {

		return getService().getLayoutsCount(
			groupId, userId, privateLayout, keywords, types);
	}

	public static int getLayoutsCount(
			long groupId, long userId, boolean privateLayout, String keywords,
			String[] types, int[] statuses)
		throws PortalException {

		return getService().getLayoutsCount(
			groupId, userId, privateLayout, keywords, types, statuses);
	}

	public static int getLayoutsCount(
			long groupId, String keywords, String[] types)
		throws PortalException {

		return getService().getLayoutsCount(groupId, keywords, types);
	}

	public static int getLayoutsCount(
			long groupId, String keywords, String[] types, int[] statuses)
		throws PortalException {

		return getService().getLayoutsCount(groupId, keywords, types, statuses);
	}

	public static int getLayoutsCount(
			com.liferay.portal.kernel.model.User user, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutsCount(user, privateLayout);
	}

	public static int getLayoutsCount(
			com.liferay.portal.kernel.model.User user, boolean privateLayout,
			boolean includeUserGroups)
		throws PortalException {

		return getService().getLayoutsCount(
			user, privateLayout, includeUserGroups);
	}

	public static List<Layout> getMasterLayouts(
		long groupId, long masterLayoutPlid) {

		return getService().getMasterLayouts(groupId, masterLayoutPlid);
	}

	public static int getMasterLayoutsCount(
		long groupId, long masterLayoutPlid) {

		return getService().getMasterLayoutsCount(groupId, masterLayoutPlid);
	}

	/**
	 * Returns the layout ID to use for the next layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @return the layout ID to use for the next layout
	 */
	public static long getNextLayoutId(long groupId, boolean privateLayout) {
		return getService().getNextLayoutId(groupId, privateLayout);
	}

	public static Layout getOrAddEmptyLayout(
			String externalReferenceCode, long userId, long groupId,
			ServiceContext serviceContext)
		throws Exception {

		return getService().getOrAddEmptyLayout(
			externalReferenceCode, userId, groupId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static Layout getParentLayout(Layout layout) throws PortalException {
		return getService().getParentLayout(layout);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<Layout> getPublishedLayouts(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return getService().getPublishedLayouts(
			groupId, start, end, orderByComparator);
	}

	public static int getPublishedLayoutsCount(long groupId) {
		return getService().getPublishedLayoutsCount(groupId);
	}

	public static List<Layout> getScopeGroupLayouts(long parentGroupId)
		throws PortalException {

		return getService().getScopeGroupLayouts(parentGroupId);
	}

	/**
	 * Returns all the layouts within scope of the group.
	 *
	 * @param parentGroupId the primary key of the group's parent group
	 * @param privateLayout whether the layout is private to the group
	 * @return the layouts within scope of the group
	 * @throws PortalException if a portal exception occurred
	 */
	public static List<Layout> getScopeGroupLayouts(
			long parentGroupId, boolean privateLayout)
		throws PortalException {

		return getService().getScopeGroupLayouts(parentGroupId, privateLayout);
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

	public static boolean hasLayouts(
			com.liferay.portal.kernel.model.Group group)
		throws PortalException {

		return getService().hasLayouts(group);
	}

	public static boolean hasLayouts(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout)
		throws PortalException {

		return getService().hasLayouts(group, privateLayout);
	}

	public static boolean hasLayouts(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout,
			boolean includeUserGroups)
		throws PortalException {

		return getService().hasLayouts(group, privateLayout, includeUserGroups);
	}

	/**
	 * Returns <code>true</code> if the group has any layouts;
	 * <code>false</code> otherwise.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @return <code>true</code> if the group has any layouts;
	 <code>false</code> otherwise
	 */
	public static boolean hasLayouts(
		long groupId, boolean privateLayout, long parentLayoutId) {

		return getService().hasLayouts(groupId, privateLayout, parentLayoutId);
	}

	public static boolean hasLayouts(
			com.liferay.portal.kernel.model.User user, boolean privateLayout)
		throws PortalException {

		return getService().hasLayouts(user, privateLayout);
	}

	public static boolean hasLayouts(
			com.liferay.portal.kernel.model.User user, boolean privateLayout,
			boolean includeUserGroups)
		throws PortalException {

		return getService().hasLayouts(user, privateLayout, includeUserGroups);
	}

	public static boolean hasLayoutSetPrototypeLayout(
			long layoutSetPrototypeId, String layoutUuid)
		throws PortalException {

		return getService().hasLayoutSetPrototypeLayout(
			layoutSetPrototypeId, layoutUuid);
	}

	public static boolean hasLayoutSetPrototypeLayout(
			String layoutSetPrototypeUuid, long companyId, String layoutUuid)
		throws PortalException {

		return getService().hasLayoutSetPrototypeLayout(
			layoutSetPrototypeUuid, companyId, layoutUuid);
	}

	public static List<Layout> search(
			long groupId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().search(
			groupId, privateLayout, keywords, searchOnlyByTitle, types, start,
			end, orderByComparator);
	}

	public static List<Layout> search(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int start, int end,
			OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().search(
			groupId, userId, privateLayout, keywords, searchOnlyByTitle, types,
			start, end, orderByComparator);
	}

	public static List<Layout> search(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types, int[] statuses,
			int start, int end, OrderByComparator<Layout> orderByComparator)
		throws PortalException {

		return getService().search(
			groupId, userId, privateLayout, keywords, searchOnlyByTitle, types,
			statuses, start, end, orderByComparator);
	}

	public static int searchCount(
			com.liferay.portal.kernel.model.Group group, boolean privateLayout,
			String keywords, boolean searchOnlyByTitle, String[] types)
		throws PortalException {

		return getService().searchCount(
			group, privateLayout, keywords, searchOnlyByTitle, types);
	}

	public static int searchCount(
			long groupId, long userId, boolean privateLayout, String keywords,
			boolean searchOnlyByTitle, String[] types)
		throws PortalException {

		return getService().searchCount(
			groupId, userId, privateLayout, keywords, searchOnlyByTitle, types);
	}

	/**
	 * Sets the layouts for the group, replacing and prioritizing all layouts of
	 * the parent layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param parentLayoutId the layout ID of the parent layout
	 * @param layoutIds the layout IDs of the layouts
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

	public static void updateAsset(
			long userId, Layout layout, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(
			userId, layout, assetCategoryIds, assetTagNames);
	}

	/**
	 * Updates the friendly URL of the layout.
	 *
	 * @param userId the primary key of the user
	 * @param plid the primary key of the layout
	 * @param friendlyURL the friendly URL to be assigned
	 * @param languageId the primary key of the language
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateFriendlyURL(
			long userId, long plid, String friendlyURL, String languageId)
		throws PortalException {

		return getService().updateFriendlyURL(
			userId, plid, friendlyURL, languageId);
	}

	public static Layout updateIconImage(long plid, byte[] bytes)
		throws PortalException {

		return getService().updateIconImage(plid, bytes);
	}

	/**
	 * Updates the layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layout the layout
	 * @return the layout that was updated
	 */
	public static Layout updateLayout(Layout layout) {
		return getService().updateLayout(layout);
	}

	/**
	 * Updates the layout replacing its draft publish date.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param publishDate the date when draft was last published
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			java.util.Date publishDate)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, publishDate);
	}

	/**
	 * Updates the layout replacing its entity class name ID and primary key.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param classNameId the class name ID of the entity
	 * @param classPK the primary key of the entity
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long classNameId, long classPK)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, classNameId, classPK);
	}

	/**
	 * Updates the layout.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param parentLayoutId the layout ID of the layout's new parent layout
	 * @param nameMap the locales and localized names to merge (optionally
	 <code>null</code>)
	 * @param titleMap the locales and localized titles to merge (optionally
	 <code>null</code>)
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
	 To see how the URL is normalized when accessed, see {@link
	 com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil#normalize(
	 String)}.
	 * @param hasIconImage whether the icon image will be updated
	 * @param iconBytes the byte array of the layout's new icon image
	 * @param styleBookEntryId the primary key of the style book entrys
	 * @param faviconFileEntryId the file entry ID of the layout's new favicon
	 * @param masterLayoutPlid the primary key of the master layout
	 * @param serviceContext the service context to be applied. Can set the
	 modification date and expando bridge attributes for the layout.
	 For layouts that are linked to a layout prototype, attributes
	 named <code>layoutPrototypeUuid</code> and
	 <code>layoutPrototypeLinkedEnabled</code> can be specified to
	 provide the unique identifier of the source prototype and a
	 boolean to determine whether a link to it should be enabled to
	 activate propagation of changes made to the linked page in the
	 prototype.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			long parentLayoutId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			Map<java.util.Locale, String> keywordsMap,
			Map<java.util.Locale, String> robotsMap, String type,
			boolean hidden, Map<java.util.Locale, String> friendlyURLMap,
			boolean hasIconImage, byte[] iconBytes, long styleBookEntryId,
			long faviconFileEntryId, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, parentLayoutId, nameMap, titleMap,
			descriptionMap, keywordsMap, robotsMap, type, hidden,
			friendlyURLMap, hasIconImage, iconBytes, styleBookEntryId,
			faviconFileEntryId, masterLayoutPlid, serviceContext);
	}

	/**
	 * Updates the layout replacing its type settings.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param typeSettings the settings to load the unicode properties object.
	 See {@link UnicodeProperties #fastLoad(String)}.
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

	public static Layout updateLayout(
			long groupId, boolean privateLayout, long layoutId,
			String typeSettings, byte[] iconBytes, String themeId,
			String colorSchemeId, long styleBookEntryId, String css,
			long faviconFileEntryId, long masterLayoutPlid)
		throws PortalException {

		return getService().updateLayout(
			groupId, privateLayout, layoutId, typeSettings, iconBytes, themeId,
			colorSchemeId, styleBookEntryId, css, faviconFileEntryId,
			masterLayoutPlid);
	}

	public static void updateLayoutContent(
			String data, Layout layout, long segmentsExperienceId)
		throws Exception {

		getService().updateLayoutContent(data, layout, segmentsExperienceId);
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
	 * Updates the layout replacing its master layout plid.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param masterLayoutPlid the primary key of the master layout
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateMasterLayoutPlid(
			long groupId, boolean privateLayout, long layoutId,
			long masterLayoutPlid)
		throws PortalException {

		return getService().updateMasterLayoutPlid(
			groupId, privateLayout, layoutId, masterLayoutPlid);
	}

	/**
	 * Updates the name of the layout.
	 *
	 * @param layout the layout to be updated
	 * @param name the layout's new name
	 * @param languageId the primary key of the language. For more information
	 see {@link Locale}.
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateName(
			Layout layout, String name, String languageId)
		throws PortalException {

		return getService().updateName(layout, name, languageId);
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
	 * Updates the priorities of the layouts.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @throws PortalException
	 */
	public static void updatePriorities(long groupId, boolean privateLayout)
		throws PortalException {

		getService().updatePriorities(groupId, privateLayout);
	}

	/**
	 * Updates the priority of the layout.
	 *
	 * @param layout the layout to be updated
	 * @param priority the layout's new priority
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updatePriority(Layout layout, int priority)
		throws PortalException {

		return getService().updatePriority(layout, priority);
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

	public static Layout updateStatus(
			long userId, long plid, int status, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(userId, plid, status, serviceContext);
	}

	/**
	 * Updates the layout replacing its style book entry ID.
	 *
	 * @param groupId the primary key of the group
	 * @param privateLayout whether the layout is private to the group
	 * @param layoutId the layout ID of the layout
	 * @param styleBookEntryId the primary key of the style book entry
	 * @return the updated layout
	 * @throws PortalException if a portal exception occurred
	 */
	public static Layout updateStyleBookEntryId(
			long groupId, boolean privateLayout, long layoutId,
			long styleBookEntryId)
		throws PortalException {

		return getService().updateStyleBookEntryId(
			groupId, privateLayout, layoutId, styleBookEntryId);
	}

	public static Layout updateType(long plid, String type)
		throws PortalException {

		return getService().updateType(plid, type);
	}

	public static LayoutLocalService getService() {
		return _service;
	}

	public static void setService(LayoutLocalService service) {
		_service = service;
	}

	private static volatile LayoutLocalService _service;

}