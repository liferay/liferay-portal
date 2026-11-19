/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service;

import com.liferay.json.storage.model.JSONStorageEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for JSONStorageEntry. This utility wraps
 * <code>com.liferay.json.storage.service.impl.JSONStorageEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Preston Crary
 * @see JSONStorageEntryLocalService
 * @generated
 */
public class JSONStorageEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.json.storage.service.impl.JSONStorageEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		getService().addJSONStorageEntries(
			companyId, classNameId, classPK, json);
	}

	/**
	 * Adds the json storage entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JSONStorageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param jsonStorageEntry the json storage entry
	 * @return the json storage entry that was added
	 */
	public static JSONStorageEntry addJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return getService().addJSONStorageEntry(jsonStorageEntry);
	}

	/**
	 * Creates a new json storage entry with the primary key. Does not add the json storage entry to the database.
	 *
	 * @param jsonStorageEntryId the primary key for the new json storage entry
	 * @return the new json storage entry
	 */
	public static JSONStorageEntry createJSONStorageEntry(
		long jsonStorageEntryId) {

		return getService().createJSONStorageEntry(jsonStorageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteJSONStorageEntries(
		long classNameId, long classPK) {

		getService().deleteJSONStorageEntries(classNameId, classPK);
	}

	/**
	 * Deletes the json storage entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JSONStorageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param jsonStorageEntry the json storage entry
	 * @return the json storage entry that was removed
	 */
	public static JSONStorageEntry deleteJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return getService().deleteJSONStorageEntry(jsonStorageEntry);
	}

	/**
	 * Deletes the json storage entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JSONStorageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry that was removed
	 * @throws PortalException if a json storage entry with the primary key could not be found
	 */
	public static JSONStorageEntry deleteJSONStorageEntry(
			long jsonStorageEntryId)
		throws PortalException {

		return getService().deleteJSONStorageEntry(jsonStorageEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.json.storage.model.impl.JSONStorageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.json.storage.model.impl.JSONStorageEntryModelImpl</code>.
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

	public static JSONStorageEntry fetchJSONStorageEntry(
		long jsonStorageEntryId) {

		return getService().fetchJSONStorageEntry(jsonStorageEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<Long> getClassPKs(
		long companyId, long classNameId, int start, int end) {

		return getService().getClassPKs(companyId, classNameId, start, end);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static String getJSON(long classNameId, long classPK) {
		return getService().getJSON(classNameId, classPK);
	}

	public static com.liferay.portal.kernel.json.JSONArray getJSONArray(
		long classNameId, long classPK) {

		return getService().getJSONArray(classNameId, classPK);
	}

	public static com.liferay.portal.kernel.json.JSONObject getJSONObject(
		long classNameId, long classPK) {

		return getService().getJSONObject(classNameId, classPK);
	}

	public static com.liferay.portal.kernel.json.JSONSerializable
		getJSONSerializable(long classNameId, long classPK) {

		return getService().getJSONSerializable(classNameId, classPK);
	}

	/**
	 * Returns a range of all the json storage entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.json.storage.model.impl.JSONStorageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of json storage entries
	 * @param end the upper bound of the range of json storage entries (not inclusive)
	 * @return the range of json storage entries
	 */
	public static List<JSONStorageEntry> getJSONStorageEntries(
		int start, int end) {

		return getService().getJSONStorageEntries(start, end);
	}

	/**
	 * Returns the number of json storage entries.
	 *
	 * @return the number of json storage entries
	 */
	public static int getJSONStorageEntriesCount() {
		return getService().getJSONStorageEntriesCount();
	}

	/**
	 * Returns the json storage entry with the primary key.
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry
	 * @throws PortalException if a json storage entry with the primary key could not be found
	 */
	public static JSONStorageEntry getJSONStorageEntry(long jsonStorageEntryId)
		throws PortalException {

		return getService().getJSONStorageEntry(jsonStorageEntryId);
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

	public static void updateJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		getService().updateJSONStorageEntries(
			companyId, classNameId, classPK, json);
	}

	/**
	 * Updates the json storage entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect JSONStorageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param jsonStorageEntry the json storage entry
	 * @return the json storage entry that was updated
	 */
	public static JSONStorageEntry updateJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return getService().updateJSONStorageEntry(jsonStorageEntry);
	}

	public static JSONStorageEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<JSONStorageEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			JSONStorageEntryLocalServiceUtil.class,
			JSONStorageEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-757413519