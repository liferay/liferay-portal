/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service;

import com.liferay.json.storage.model.JSONStorageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link JSONStorageEntryLocalService}.
 *
 * @author Preston Crary
 * @see JSONStorageEntryLocalService
 * @generated
 */
public class JSONStorageEntryLocalServiceWrapper
	implements JSONStorageEntryLocalService,
			   ServiceWrapper<JSONStorageEntryLocalService> {

	public JSONStorageEntryLocalServiceWrapper() {
		this(null);
	}

	public JSONStorageEntryLocalServiceWrapper(
		JSONStorageEntryLocalService jsonStorageEntryLocalService) {

		_jsonStorageEntryLocalService = jsonStorageEntryLocalService;
	}

	@Override
	public void addJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		_jsonStorageEntryLocalService.addJSONStorageEntries(
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
	@Override
	public JSONStorageEntry addJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return _jsonStorageEntryLocalService.addJSONStorageEntry(
			jsonStorageEntry);
	}

	/**
	 * Creates a new json storage entry with the primary key. Does not add the json storage entry to the database.
	 *
	 * @param jsonStorageEntryId the primary key for the new json storage entry
	 * @return the new json storage entry
	 */
	@Override
	public JSONStorageEntry createJSONStorageEntry(long jsonStorageEntryId) {
		return _jsonStorageEntryLocalService.createJSONStorageEntry(
			jsonStorageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _jsonStorageEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteJSONStorageEntries(long classNameId, long classPK) {
		_jsonStorageEntryLocalService.deleteJSONStorageEntries(
			classNameId, classPK);
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
	@Override
	public JSONStorageEntry deleteJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return _jsonStorageEntryLocalService.deleteJSONStorageEntry(
			jsonStorageEntry);
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
	@Override
	public JSONStorageEntry deleteJSONStorageEntry(long jsonStorageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _jsonStorageEntryLocalService.deleteJSONStorageEntry(
			jsonStorageEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _jsonStorageEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _jsonStorageEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _jsonStorageEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _jsonStorageEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _jsonStorageEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _jsonStorageEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _jsonStorageEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _jsonStorageEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _jsonStorageEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public JSONStorageEntry fetchJSONStorageEntry(long jsonStorageEntryId) {
		return _jsonStorageEntryLocalService.fetchJSONStorageEntry(
			jsonStorageEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _jsonStorageEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<Long> getClassPKs(
		long companyId, long classNameId, int start, int end) {

		return _jsonStorageEntryLocalService.getClassPKs(
			companyId, classNameId, start, end);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _jsonStorageEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public String getJSON(long classNameId, long classPK) {
		return _jsonStorageEntryLocalService.getJSON(classNameId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONArray getJSONArray(
		long classNameId, long classPK) {

		return _jsonStorageEntryLocalService.getJSONArray(classNameId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject getJSONObject(
		long classNameId, long classPK) {

		return _jsonStorageEntryLocalService.getJSONObject(
			classNameId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONSerializable getJSONSerializable(
		long classNameId, long classPK) {

		return _jsonStorageEntryLocalService.getJSONSerializable(
			classNameId, classPK);
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
	@Override
	public java.util.List<JSONStorageEntry> getJSONStorageEntries(
		int start, int end) {

		return _jsonStorageEntryLocalService.getJSONStorageEntries(start, end);
	}

	/**
	 * Returns the number of json storage entries.
	 *
	 * @return the number of json storage entries
	 */
	@Override
	public int getJSONStorageEntriesCount() {
		return _jsonStorageEntryLocalService.getJSONStorageEntriesCount();
	}

	/**
	 * Returns the json storage entry with the primary key.
	 *
	 * @param jsonStorageEntryId the primary key of the json storage entry
	 * @return the json storage entry
	 * @throws PortalException if a json storage entry with the primary key could not be found
	 */
	@Override
	public JSONStorageEntry getJSONStorageEntry(long jsonStorageEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _jsonStorageEntryLocalService.getJSONStorageEntry(
			jsonStorageEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _jsonStorageEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _jsonStorageEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void updateJSONStorageEntries(
		long companyId, long classNameId, long classPK, String json) {

		_jsonStorageEntryLocalService.updateJSONStorageEntries(
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
	@Override
	public JSONStorageEntry updateJSONStorageEntry(
		JSONStorageEntry jsonStorageEntry) {

		return _jsonStorageEntryLocalService.updateJSONStorageEntry(
			jsonStorageEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _jsonStorageEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<JSONStorageEntry> getCTPersistence() {
		return _jsonStorageEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<JSONStorageEntry> getModelClass() {
		return _jsonStorageEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<JSONStorageEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _jsonStorageEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public JSONStorageEntryLocalService getWrappedService() {
		return _jsonStorageEntryLocalService;
	}

	@Override
	public void setWrappedService(
		JSONStorageEntryLocalService jsonStorageEntryLocalService) {

		_jsonStorageEntryLocalService = jsonStorageEntryLocalService;
	}

	private JSONStorageEntryLocalService _jsonStorageEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-571562418