/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMDataProviderInstanceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderInstanceLocalService
 * @generated
 */
public class DDMDataProviderInstanceLocalServiceWrapper
	implements DDMDataProviderInstanceLocalService,
			   ServiceWrapper<DDMDataProviderInstanceLocalService> {

	public DDMDataProviderInstanceLocalServiceWrapper() {
		this(null);
	}

	public DDMDataProviderInstanceLocalServiceWrapper(
		DDMDataProviderInstanceLocalService
			ddmDataProviderInstanceLocalService) {

		_ddmDataProviderInstanceLocalService =
			ddmDataProviderInstanceLocalService;
	}

	@Override
	public DDMDataProviderInstance addDataProviderInstance(
			long userId, long groupId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.addDataProviderInstance(
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
	@Override
	public DDMDataProviderInstance addDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return _ddmDataProviderInstanceLocalService.addDDMDataProviderInstance(
			ddmDataProviderInstance);
	}

	/**
	 * Creates a new ddm data provider instance with the primary key. Does not add the ddm data provider instance to the database.
	 *
	 * @param dataProviderInstanceId the primary key for the new ddm data provider instance
	 * @return the new ddm data provider instance
	 */
	@Override
	public DDMDataProviderInstance createDDMDataProviderInstance(
		long dataProviderInstanceId) {

		return _ddmDataProviderInstanceLocalService.
			createDDMDataProviderInstance(dataProviderInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteDataProviderInstance(
			DDMDataProviderInstance dataProviderInstance)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			dataProviderInstance);
	}

	@Override
	public void deleteDataProviderInstance(long dataProviderInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstance(
			dataProviderInstanceId);
	}

	@Override
	public void deleteDataProviderInstances(long companyId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_ddmDataProviderInstanceLocalService.deleteDataProviderInstances(
			companyId, groupId);
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
	@Override
	public DDMDataProviderInstance deleteDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return _ddmDataProviderInstanceLocalService.
			deleteDDMDataProviderInstance(ddmDataProviderInstance);
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
	@Override
	public DDMDataProviderInstance deleteDDMDataProviderInstance(
			long dataProviderInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.
			deleteDDMDataProviderInstance(dataProviderInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmDataProviderInstanceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmDataProviderInstanceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmDataProviderInstanceLocalService.dynamicQuery();
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

		return _ddmDataProviderInstanceLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _ddmDataProviderInstanceLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _ddmDataProviderInstanceLocalService.dynamicQuery(
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

		return _ddmDataProviderInstanceLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _ddmDataProviderInstanceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMDataProviderInstance fetchDataProviderInstance(
		long dataProviderInstanceId) {

		return _ddmDataProviderInstanceLocalService.fetchDataProviderInstance(
			dataProviderInstanceId);
	}

	@Override
	public DDMDataProviderInstance fetchDataProviderInstanceByUuid(
		String uuid) {

		return _ddmDataProviderInstanceLocalService.
			fetchDataProviderInstanceByUuid(uuid);
	}

	@Override
	public DDMDataProviderInstance fetchDDMDataProviderInstance(
		long dataProviderInstanceId) {

		return _ddmDataProviderInstanceLocalService.
			fetchDDMDataProviderInstance(dataProviderInstanceId);
	}

	/**
	 * Returns the ddm data provider instance matching the UUID and group.
	 *
	 * @param uuid the ddm data provider instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm data provider instance, or <code>null</code> if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance fetchDDMDataProviderInstanceByUuidAndGroupId(
		String uuid, long groupId) {

		return _ddmDataProviderInstanceLocalService.
			fetchDDMDataProviderInstanceByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmDataProviderInstanceLocalService.getActionableDynamicQuery();
	}

	@Override
	public DDMDataProviderInstance getDataProviderInstance(
			long dataProviderInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.getDataProviderInstance(
			dataProviderInstanceId);
	}

	@Override
	public DDMDataProviderInstance getDataProviderInstanceByUuid(String uuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.
			getDataProviderInstanceByUuid(uuid);
	}

	@Override
	public java.util.List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds) {

		return _ddmDataProviderInstanceLocalService.getDataProviderInstances(
			groupIds);
	}

	@Override
	public java.util.List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end) {

		return _ddmDataProviderInstanceLocalService.getDataProviderInstances(
			groupIds, start, end);
	}

	@Override
	public java.util.List<DDMDataProviderInstance> getDataProviderInstances(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<DDMDataProviderInstance> orderByComparator) {

		return _ddmDataProviderInstanceLocalService.getDataProviderInstances(
			groupIds, start, end, orderByComparator);
	}

	/**
	 * Returns the ddm data provider instance with the primary key.
	 *
	 * @param dataProviderInstanceId the primary key of the ddm data provider instance
	 * @return the ddm data provider instance
	 * @throws PortalException if a ddm data provider instance with the primary key could not be found
	 */
	@Override
	public DDMDataProviderInstance getDDMDataProviderInstance(
			long dataProviderInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.getDDMDataProviderInstance(
			dataProviderInstanceId);
	}

	/**
	 * Returns the ddm data provider instance matching the UUID and group.
	 *
	 * @param uuid the ddm data provider instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm data provider instance
	 * @throws PortalException if a matching ddm data provider instance could not be found
	 */
	@Override
	public DDMDataProviderInstance getDDMDataProviderInstanceByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.
			getDDMDataProviderInstanceByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<DDMDataProviderInstance> getDDMDataProviderInstances(
		int start, int end) {

		return _ddmDataProviderInstanceLocalService.getDDMDataProviderInstances(
			start, end);
	}

	/**
	 * Returns all the ddm data provider instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm data provider instances
	 * @param companyId the primary key of the company
	 * @return the matching ddm data provider instances, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DDMDataProviderInstance>
		getDDMDataProviderInstancesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _ddmDataProviderInstanceLocalService.
			getDDMDataProviderInstancesByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<DDMDataProviderInstance>
		getDDMDataProviderInstancesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<DDMDataProviderInstance> orderByComparator) {

		return _ddmDataProviderInstanceLocalService.
			getDDMDataProviderInstancesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddm data provider instances.
	 *
	 * @return the number of ddm data provider instances
	 */
	@Override
	public int getDDMDataProviderInstancesCount() {
		return _ddmDataProviderInstanceLocalService.
			getDDMDataProviderInstancesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _ddmDataProviderInstanceLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmDataProviderInstanceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmDataProviderInstanceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public DDMDataProviderInstance updateDataProviderInstance(
			long userId, long dataProviderInstanceId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmDataProviderInstanceLocalService.updateDataProviderInstance(
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
	@Override
	public DDMDataProviderInstance updateDDMDataProviderInstance(
		DDMDataProviderInstance ddmDataProviderInstance) {

		return _ddmDataProviderInstanceLocalService.
			updateDDMDataProviderInstance(ddmDataProviderInstance);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmDataProviderInstanceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMDataProviderInstance> getCTPersistence() {
		return _ddmDataProviderInstanceLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMDataProviderInstance> getModelClass() {
		return _ddmDataProviderInstanceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMDataProviderInstance>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmDataProviderInstanceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMDataProviderInstanceLocalService getWrappedService() {
		return _ddmDataProviderInstanceLocalService;
	}

	@Override
	public void setWrappedService(
		DDMDataProviderInstanceLocalService
			ddmDataProviderInstanceLocalService) {

		_ddmDataProviderInstanceLocalService =
			ddmDataProviderInstanceLocalService;
	}

	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:112476418