/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for PortletPreferences. This utility wraps
 * <code>com.liferay.portal.service.impl.PortletPreferencesLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferencesLocalService
 * @generated
 */
public class PortletPreferencesLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletPreferencesLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PortletPreferences addPortletPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, com.liferay.portal.kernel.model.Portlet portlet,
		String defaultPreferences) {

		return getService().addPortletPreferences(
			companyId, ownerId, ownerType, plid, portletId, portlet,
			defaultPreferences);
	}

	/**
	 * Adds the portlet preferences to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was added
	 */
	public static PortletPreferences addPortletPreferences(
		PortletPreferences portletPreferences) {

		return getService().addPortletPreferences(portletPreferences);
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
	 * Creates a new portlet preferences with the primary key. Does not add the portlet preferences to the database.
	 *
	 * @param portletPreferencesId the primary key for the new portlet preferences
	 * @return the new portlet preferences
	 */
	public static PortletPreferences createPortletPreferences(
		long portletPreferencesId) {

		return getService().createPortletPreferences(portletPreferencesId);
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
	 * Deletes the portlet preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences that was removed
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	public static PortletPreferences deletePortletPreferences(
			long portletPreferencesId)
		throws PortalException {

		return getService().deletePortletPreferences(portletPreferencesId);
	}

	public static void deletePortletPreferences(
		long ownerId, int ownerType, long plid) {

		getService().deletePortletPreferences(ownerId, ownerType, plid);
	}

	public static void deletePortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException {

		getService().deletePortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	/**
	 * Deletes the portlet preferences from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was removed
	 */
	public static PortletPreferences deletePortletPreferences(
		PortletPreferences portletPreferences) {

		return getService().deletePortletPreferences(portletPreferences);
	}

	public static void deletePortletPreferencesByOwnerId(long ownerId) {
		getService().deletePortletPreferencesByOwnerId(ownerId);
	}

	public static void deletePortletPreferencesByPlid(long plid) {
		getService().deletePortletPreferencesByPlid(plid);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
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

	public static PortletPreferences fetchPortletPreferences(
		long portletPreferencesId) {

		return getService().fetchPortletPreferences(portletPreferencesId);
	}

	public static PortletPreferences fetchPortletPreferences(
		long ownerId, int ownerType, long plid, String portletId) {

		return getService().fetchPortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	public static jakarta.portlet.PortletPreferences fetchPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return getService().fetchPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	public static jakarta.portlet.PortletPreferences fetchPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return getService().fetchPreferences(portletPreferencesIds);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static jakarta.portlet.PortletPreferences getDefaultPreferences(
		long companyId, String portletId) {

		return getService().getDefaultPreferences(companyId, portletId);
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

	public static com.liferay.portal.kernel.settings.Settings
		getPortletInstanceSettings(
			long companyId, long groupId, String portletId,
			com.liferay.portal.kernel.settings.PortletInstanceSettingsLocator
				portletInstanceSettingsLocator,
			com.liferay.portal.kernel.settings.Settings
				portalPreferencesSettings) {

		return getService().getPortletInstanceSettings(
			companyId, groupId, portletId, portletInstanceSettingsLocator,
			portalPreferencesSettings);
	}

	public static List<PortletPreferences> getPortletPreferences() {
		return getService().getPortletPreferences();
	}

	public static List<PortletPreferences> getPortletPreferences(
		int ownerType, long plid, String portletId) {

		return getService().getPortletPreferences(ownerType, plid, portletId);
	}

	/**
	 * Returns the portlet preferences with the primary key.
	 *
	 * @param portletPreferencesId the primary key of the portlet preferences
	 * @return the portlet preferences
	 * @throws PortalException if a portlet preferences with the primary key could not be found
	 */
	public static PortletPreferences getPortletPreferences(
			long portletPreferencesId)
		throws PortalException {

		return getService().getPortletPreferences(portletPreferencesId);
	}

	public static List<PortletPreferences> getPortletPreferences(
		long ownerId, int ownerType, long plid) {

		return getService().getPortletPreferences(ownerId, ownerType, plid);
	}

	public static PortletPreferences getPortletPreferences(
			long ownerId, int ownerType, long plid, String portletId)
		throws PortalException {

		return getService().getPortletPreferences(
			ownerId, ownerType, plid, portletId);
	}

	public static List<PortletPreferences> getPortletPreferences(
			long companyId, long ownerId, int ownerType, String portletId)
		throws PortalException {

		return getService().getPortletPreferences(
			companyId, ownerId, ownerType, portletId);
	}

	public static List<PortletPreferences> getPortletPreferences(
		long companyId, long groupId, long ownerId, int ownerType,
		String portletId, boolean privateLayout) {

		return getService().getPortletPreferences(
			companyId, groupId, ownerId, ownerType, portletId, privateLayout);
	}

	public static List<PortletPreferences> getPortletPreferences(
		long plid, String portletId) {

		return getService().getPortletPreferences(plid, portletId);
	}

	public static List<PortletPreferences> getPortletPreferencesByOwnerId(
		long ownerId) {

		return getService().getPortletPreferencesByOwnerId(ownerId);
	}

	public static List<PortletPreferences> getPortletPreferencesByPlid(
		long plid) {

		return getService().getPortletPreferencesByPlid(plid);
	}

	public static List<PortletPreferences> getPortletPreferencesByPortletId(
		String portletId) {

		return getService().getPortletPreferencesByPortletId(portletId);
	}

	public static long getPortletPreferencesCount(
		int ownerType, long plid, String portletId) {

		return getService().getPortletPreferencesCount(
			ownerType, plid, portletId);
	}

	public static long getPortletPreferencesCount(
		int ownerType, String portletId) {

		return getService().getPortletPreferencesCount(ownerType, portletId);
	}

	public static long getPortletPreferencesCount(
		long ownerId, int ownerType, long plid,
		com.liferay.portal.kernel.model.Portlet portlet,
		boolean excludeDefaultPreferences) {

		return getService().getPortletPreferencesCount(
			ownerId, ownerType, plid, portlet, excludeDefaultPreferences);
	}

	public static long getPortletPreferencesCount(
		long ownerId, int ownerType, String portletId,
		boolean excludeDefaultPreferences) {

		return getService().getPortletPreferencesCount(
			ownerId, ownerType, portletId, excludeDefaultPreferences);
	}

	public static int getPortletPreferencesCount(
		long companyId, long ownerId, int ownerType, String portletId) {

		return getService().getPortletPreferencesCount(
			companyId, ownerId, ownerType, portletId);
	}

	/**
	 * Returns a range of all the portlet preferenceses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet preferenceses
	 * @param end the upper bound of the range of portlet preferenceses (not inclusive)
	 * @return the range of portlet preferenceses
	 */
	public static List<PortletPreferences> getPortletPreferenceses(
		int start, int end) {

		return getService().getPortletPreferenceses(start, end);
	}

	/**
	 * Returns the number of portlet preferenceses.
	 *
	 * @return the number of portlet preferenceses
	 */
	public static int getPortletPreferencesesCount() {
		return getService().getPortletPreferencesesCount();
	}

	public static jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return getService().getPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	public static jakarta.portlet.PortletPreferences getPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String defaultPreferences) {

		return getService().getPreferences(
			companyId, ownerId, ownerType, plid, portletId, defaultPreferences);
	}

	public static jakarta.portlet.PortletPreferences getPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return getService().getPreferences(portletPreferencesIds);
	}

	public static Map<String, jakarta.portlet.PortletPreferences>
		getStrictPreferences(
			com.liferay.portal.kernel.model.Layout layout,
			List<com.liferay.portal.kernel.model.Portlet> portlets) {

		return getService().getStrictPreferences(layout, portlets);
	}

	public static jakarta.portlet.PortletPreferences getStrictPreferences(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId) {

		return getService().getStrictPreferences(
			companyId, ownerId, ownerType, plid, portletId);
	}

	public static jakarta.portlet.PortletPreferences getStrictPreferences(
		com.liferay.portal.kernel.model.PortletPreferencesIds
			portletPreferencesIds) {

		return getService().getStrictPreferences(portletPreferencesIds);
	}

	/**
	 * Updates the portlet preferences in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferencesLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferences the portlet preferences
	 * @return the portlet preferences that was updated
	 */
	public static PortletPreferences updatePortletPreferences(
		PortletPreferences portletPreferences) {

		return getService().updatePortletPreferences(portletPreferences);
	}

	public static PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId,
		jakarta.portlet.PortletPreferences portletPreferences) {

		return getService().updatePreferences(
			ownerId, ownerType, plid, portletId, portletPreferences);
	}

	public static PortletPreferences updatePreferences(
		long ownerId, int ownerType, long plid, String portletId, String xml) {

		return getService().updatePreferences(
			ownerId, ownerType, plid, portletId, xml);
	}

	public static PortletPreferencesLocalService getService() {
		return _service;
	}

	public static void setService(PortletPreferencesLocalService service) {
		_service = service;
	}

	private static volatile PortletPreferencesLocalService _service;

}