/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for LayoutSet. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutSetLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutSetLocalService
 * @generated
 */
public class LayoutSetLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutSetLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout set to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was added
	 */
	public static LayoutSet addLayoutSet(LayoutSet layoutSet) {
		return getService().addLayoutSet(layoutSet);
	}

	public static LayoutSet addLayoutSet(long groupId, boolean privateLayout)
		throws PortalException {

		return getService().addLayoutSet(groupId, privateLayout);
	}

	/**
	 * Creates a new layout set with the primary key. Does not add the layout set to the database.
	 *
	 * @param layoutSetId the primary key for the new layout set
	 * @return the new layout set
	 */
	public static LayoutSet createLayoutSet(long layoutSetId) {
		return getService().createLayoutSet(layoutSetId);
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
	 * Deletes the layout set from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was removed
	 */
	public static LayoutSet deleteLayoutSet(LayoutSet layoutSet) {
		return getService().deleteLayoutSet(layoutSet);
	}

	/**
	 * Deletes the layout set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set that was removed
	 * @throws PortalException if a layout set with the primary key could not be found
	 */
	public static LayoutSet deleteLayoutSet(long layoutSetId)
		throws PortalException {

		return getService().deleteLayoutSet(layoutSetId);
	}

	public static void deleteLayoutSet(
			long groupId, boolean privateLayout, ServiceContext serviceContext)
		throws PortalException {

		getService().deleteLayoutSet(groupId, privateLayout, serviceContext);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
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

	public static LayoutSet fetchLayoutSet(long layoutSetId) {
		return getService().fetchLayoutSet(layoutSetId);
	}

	public static LayoutSet fetchLayoutSet(
		long groupId, boolean privateLayout) {

		return getService().fetchLayoutSet(groupId, privateLayout);
	}

	public static LayoutSet fetchLayoutSet(String virtualHostname) {
		return getService().fetchLayoutSet(virtualHostname);
	}

	public static LayoutSet fetchLayoutSetByLogoId(
		boolean privateLayout, long logoId) {

		return getService().fetchLayoutSetByLogoId(privateLayout, logoId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout set with the primary key.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set
	 * @throws PortalException if a layout set with the primary key could not be found
	 */
	public static LayoutSet getLayoutSet(long layoutSetId)
		throws PortalException {

		return getService().getLayoutSet(layoutSetId);
	}

	public static LayoutSet getLayoutSet(long groupId, boolean privateLayout)
		throws PortalException {

		return getService().getLayoutSet(groupId, privateLayout);
	}

	public static LayoutSet getLayoutSet(String virtualHostname)
		throws PortalException {

		return getService().getLayoutSet(virtualHostname);
	}

	/**
	 * Returns a range of all the layout sets.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @return the range of layout sets
	 */
	public static List<LayoutSet> getLayoutSets(int start, int end) {
		return getService().getLayoutSets(start, end);
	}

	public static List<LayoutSet> getLayoutSetsByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid) {

		return getService().getLayoutSetsByLayoutSetPrototypeUuid(
			layoutSetPrototypeUuid);
	}

	/**
	 * Returns the number of layout sets.
	 *
	 * @return the number of layout sets
	 */
	public static int getLayoutSetsCount() {
		return getService().getLayoutSetsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static int getPageCount(long groupId, boolean privateLayout) {
		return getService().getPageCount(groupId, privateLayout);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static LayoutSet updateFaviconFileEntryId(
			long groupId, boolean privateLayout, long faviconFileEntryId)
		throws PortalException {

		return getService().updateFaviconFileEntryId(
			groupId, privateLayout, faviconFileEntryId);
	}

	/**
	 * Updates the layout set in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutSetLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutSet the layout set
	 * @return the layout set that was updated
	 */
	public static LayoutSet updateLayoutSet(LayoutSet layoutSet) {
		return getService().updateLayoutSet(layoutSet);
	}

	public static void updateLayoutSetPrototypeLinkEnabled(
			long groupId, boolean mergeLayoutSetPrototype,
			boolean privateLayout, boolean layoutSetPrototypeLinkEnabled,
			String layoutSetPrototypeUuid)
		throws PortalException {

		getService().updateLayoutSetPrototypeLinkEnabled(
			groupId, mergeLayoutSetPrototype, privateLayout,
			layoutSetPrototypeLinkEnabled, layoutSetPrototypeUuid);
	}

	public static LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo, byte[] bytes)
		throws PortalException {

		return getService().updateLogo(groupId, privateLayout, hasLogo, bytes);
	}

	public static LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			java.io.File file)
		throws PortalException {

		return getService().updateLogo(groupId, privateLayout, hasLogo, file);
	}

	public static LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream)
		throws PortalException {

		return getService().updateLogo(
			groupId, privateLayout, hasLogo, inputStream);
	}

	public static LayoutSet updateLogo(
			long groupId, boolean privateLayout, boolean hasLogo,
			InputStream inputStream, boolean cleanUpStream)
		throws PortalException {

		return getService().updateLogo(
			groupId, privateLayout, hasLogo, inputStream, cleanUpStream);
	}

	public static LayoutSet updateLookAndFeel(
			long groupId, boolean privateLayout, String themeId,
			String colorSchemeId, String css)
		throws PortalException {

		return getService().updateLookAndFeel(
			groupId, privateLayout, themeId, colorSchemeId, css);
	}

	public static void updateLookAndFeel(
			long groupId, String themeId, String colorSchemeId, String css)
		throws PortalException {

		getService().updateLookAndFeel(groupId, themeId, colorSchemeId, css);
	}

	public static LayoutSet updateSettings(
			long groupId, boolean privateLayout, String settings)
		throws PortalException {

		return getService().updateSettings(groupId, privateLayout, settings);
	}

	public static LayoutSet updateVirtualHosts(
			long groupId, boolean privateLayout,
			java.util.TreeMap<String, String> virtualHostnames)
		throws PortalException {

		return getService().updateVirtualHosts(
			groupId, privateLayout, virtualHostnames);
	}

	public static LayoutSetLocalService getService() {
		return _service;
	}

	public static void setService(LayoutSetLocalService service) {
		_service = service;
	}

	private static volatile LayoutSetLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1926681041