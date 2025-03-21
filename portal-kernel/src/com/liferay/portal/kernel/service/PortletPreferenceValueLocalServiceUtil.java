/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PortletPreferenceValue. This utility wraps
 * <code>com.liferay.portal.service.impl.PortletPreferenceValueLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PortletPreferenceValueLocalService
 * @generated
 */
public class PortletPreferenceValueLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PortletPreferenceValueLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the portlet preference value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was added
	 */
	public static PortletPreferenceValue addPortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return getService().addPortletPreferenceValue(portletPreferenceValue);
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
	 * Creates a new portlet preference value with the primary key. Does not add the portlet preference value to the database.
	 *
	 * @param portletPreferenceValueId the primary key for the new portlet preference value
	 * @return the new portlet preference value
	 */
	public static PortletPreferenceValue createPortletPreferenceValue(
		long portletPreferenceValueId) {

		return getService().createPortletPreferenceValue(
			portletPreferenceValueId);
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
	 * Deletes the portlet preference value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value that was removed
	 * @throws PortalException if a portlet preference value with the primary key could not be found
	 */
	public static PortletPreferenceValue deletePortletPreferenceValue(
			long portletPreferenceValueId)
		throws PortalException {

		return getService().deletePortletPreferenceValue(
			portletPreferenceValueId);
	}

	/**
	 * Deletes the portlet preference value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was removed
	 */
	public static PortletPreferenceValue deletePortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return getService().deletePortletPreferenceValue(
			portletPreferenceValue);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
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

	public static PortletPreferenceValue fetchPortletPreferenceValue(
		long portletPreferenceValueId) {

		return getService().fetchPortletPreferenceValue(
			portletPreferenceValueId);
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

	/**
	 * Returns the portlet preference value with the primary key.
	 *
	 * @param portletPreferenceValueId the primary key of the portlet preference value
	 * @return the portlet preference value
	 * @throws PortalException if a portlet preference value with the primary key could not be found
	 */
	public static PortletPreferenceValue getPortletPreferenceValue(
			long portletPreferenceValueId)
		throws PortalException {

		return getService().getPortletPreferenceValue(portletPreferenceValueId);
	}

	/**
	 * Returns a range of all the portlet preference values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PortletPreferenceValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of portlet preference values
	 * @param end the upper bound of the range of portlet preference values (not inclusive)
	 * @return the range of portlet preference values
	 */
	public static List<PortletPreferenceValue> getPortletPreferenceValues(
		int start, int end) {

		return getService().getPortletPreferenceValues(start, end);
	}

	/**
	 * Returns the number of portlet preference values.
	 *
	 * @return the number of portlet preference values
	 */
	public static int getPortletPreferenceValuesCount() {
		return getService().getPortletPreferenceValuesCount();
	}

	public static int getPortletPreferenceValuesCount(
		long companyId, String name, String smallValue) {

		return getService().getPortletPreferenceValuesCount(
			companyId, name, smallValue);
	}

	public static jakarta.portlet.PortletPreferences getPreferences(
		com.liferay.portal.kernel.model.PortletPreferences portletPreferences) {

		return getService().getPreferences(portletPreferences);
	}

	/**
	 * Updates the portlet preference value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PortletPreferenceValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param portletPreferenceValue the portlet preference value
	 * @return the portlet preference value that was updated
	 */
	public static PortletPreferenceValue updatePortletPreferenceValue(
		PortletPreferenceValue portletPreferenceValue) {

		return getService().updatePortletPreferenceValue(
			portletPreferenceValue);
	}

	public static PortletPreferenceValueLocalService getService() {
		return _service;
	}

	public static void setService(PortletPreferenceValueLocalService service) {
		_service = service;
	}

	private static volatile PortletPreferenceValueLocalService _service;

}