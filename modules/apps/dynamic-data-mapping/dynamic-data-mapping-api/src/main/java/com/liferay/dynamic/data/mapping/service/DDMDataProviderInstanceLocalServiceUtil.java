/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for DDMDataProviderInstance. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderInstanceLocalService
 * @generated
 */
public class DDMDataProviderInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static DDMDataProviderInstance addDataProviderInstance(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDataProviderInstance(
			userId, groupId, nameMap, descriptionMap, ddmFormValues, type,
			serviceContext);
	}

	/**
	 * Adds the ddm data provider instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstance the ddm data provider instance
	 * @return the ddm data provider instance that was added
	 */
	public static DDMDataProviderInstance addDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return getService().addDDMDataProviderInstance(ddmDataProviderInstance);
	}

	/**
	 * Creates a new ddm data provider instance with the primary key. Does not add the ddm data provider instance to the database.
	 *
	 * @param dataProviderInstanceId the primary key for the new ddm data provider instance
	 * @return the new ddm data provider instance
	 */
	public static DDMDataProviderInstance createDDMDataProviderInstance(
		long dataProviderInstanceId) {

		return getService().createDDMDataProviderInstance(
			dataProviderInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteDataProviderInstance(
			DDMDataProviderInstance dataProviderInstance)
		throws PortalException {

		getService().deleteDataProviderInstance(dataProviderInstance);
	}

	public static void deleteDataProviderInstance(long dataProviderInstanceId)
		throws PortalException {

		getService().deleteDataProviderInstance(dataProviderInstanceId);
	}

	public static void deleteDataProviderInstances(long companyId, long groupId)
		throws PortalException {

		getService().deleteDataProviderInstances(companyId, groupId);
	}

	/**
	 * Deletes the ddm data provider instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstance the ddm data provider instance
	 * @return the ddm data provider instance that was removed
	 */
	public static DDMDataProviderInstance deleteDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return getService().deleteDDMDataProviderInstance(
			ddmDataProviderInstance);
	}

	/**
	 * Deletes the ddm data provider instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance that was removed
	 * @throws PortalException if a ddm data provider instance with the primary key could not be found
	 */
	public static DDMDataProviderInstance deleteDDMDataProviderInstance(
			long dataProviderInstanceId)
		throws PortalException {

		return getService().deleteDDMDataProviderInstance(
			dataProviderInstanceId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceModelImpl</code>.
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

	public static DDMDataProviderInstance fetchDataProviderInstance(
		long dataProviderInstanceId) {

		return getService().fetchDataProviderInstance(dataProviderInstanceId);
	}

	public static DDMDataProviderInstance fetchDataProviderInstanceByUuid(
		String uuid) {

		return getService().fetchDataProviderInstanceByUuid(uuid);
	}

	public static DDMDataProviderInstance fetchDDMDataProviderInstance(
		long dataProviderInstanceId) {

		return getService().fetchDDMDataProviderInstance(
			dataProviderInstanceId);
	}

	/**
	 * Returns the ddm data provider instance matching the UUID and group.
	 *
	 * @param uuid the ddm data provider instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	public static DDMDataProviderInstance
		fetchDDMDataProviderInstanceByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchDDMDataProviderInstanceByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static DDMDataProviderInstance getDataProviderInstance(
			long dataProviderInstanceId)
		throws PortalException {

		return getService().getDataProviderInstance(dataProviderInstanceId);
	}

	public static DDMDataProviderInstance getDataProviderInstanceByUuid(
			String uuid)
		throws PortalException {

		return getService().getDataProviderInstanceByUuid(uuid);
	}

	public static List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds) {

		return getService().getDataProviderInstances(groupIds);
	}

	public static List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end) {

		return getService().getDataProviderInstances(groupIds, start, end);
	}

	public static List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end,
		OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return getService().getDataProviderInstances(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm data provider instance with the primary key.
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance
	 * @throws PortalException if a ddm data provider instance with the primary key could not be found
	 */
	public static DDMDataProviderInstance getDDMDataProviderInstance(
			long dataProviderInstanceId)
		throws PortalException {

		return getService().getDDMDataProviderInstance(dataProviderInstanceId);
	}

	/**
	 * Returns the ddm data provider instance matching the UUID and group.
	 *
	 * @param uuid the ddm data provider instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm data provider instance
	 * @throws PortalException if a matching ddm data provider instance could not be found
	 */
	public static DDMDataProviderInstance
			getDDMDataProviderInstanceByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getDDMDataProviderInstanceByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the ddm data provider instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @return the range of ddm data provider instances
	 */
	public static List<DDMDataProviderInstance> getDDMDataProviderInstances(
		int start, int end) {

		return getService().getDDMDataProviderInstances(start, end);
	}

	/**
	 * Returns all the ddm data provider instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm data provider instances
	 * @param companyId the primary key of the company
	 * @return the matching ddm data provider instances, or an empty list if no matches were found
	 */
	public static List<DDMDataProviderInstance>
		getDDMDataProviderInstancesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getDDMDataProviderInstancesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of ddm data provider instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm data provider instances
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm data provider instances
	 * @param end the upper bound of the range of ddm data provider instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm data provider instances, or an empty list if no matches were found
	 */
	public static List<DDMDataProviderInstance>
		getDDMDataProviderInstancesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DDMDataProviderInstance> orderByComparator) {

		return getService().getDDMDataProviderInstancesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm data provider instances.
	 *
	 * @return the number of ddm data provider instances
	 */
	public static int getDDMDataProviderInstancesCount() {
		return getService().getDDMDataProviderInstancesCount();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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

	public static DDMDataProviderInstance updateDataProviderInstance(
			long userId, long dataProviderInstanceId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDataProviderInstance(
			userId, dataProviderInstanceId, nameMap, descriptionMap,
			ddmFormValues, serviceContext);
	}

	/**
	 * Updates the ddm data provider instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstance the ddm data provider instance
	 * @return the ddm data provider instance that was updated
	 */
	public static DDMDataProviderInstance updateDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return getService().updateDDMDataProviderInstance(
			ddmDataProviderInstance);
	}

	public static DDMDataProviderInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMDataProviderInstanceLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMDataProviderInstanceLocalServiceUtil.class,
			DDMDataProviderInstanceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-428885462