/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPMeasurementUnitLocalService}.
 *
 * @author Marco Leo
 * @see CPMeasurementUnitLocalService
 * @generated
 */
public class CPMeasurementUnitLocalServiceWrapper
	implements CPMeasurementUnitLocalService,
			   ServiceWrapper<CPMeasurementUnitLocalService> {

	public CPMeasurementUnitLocalServiceWrapper() {
		this(null);
	}

	public CPMeasurementUnitLocalServiceWrapper(
		CPMeasurementUnitLocalService cpMeasurementUnitLocalService) {

		_cpMeasurementUnitLocalService = cpMeasurementUnitLocalService;
	}

	/**
	 * Adds the cp measurement unit to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPMeasurementUnitLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpMeasurementUnit the cp measurement unit
	 * @return the cp measurement unit that was added
	 */
	@Override
	public CPMeasurementUnit addCPMeasurementUnit(
		CPMeasurementUnit cpMeasurementUnit) {

		return _cpMeasurementUnitLocalService.addCPMeasurementUnit(
			cpMeasurementUnit);
	}

	@Override
	public CPMeasurementUnit addCPMeasurementUnit(
			String externalReferenceCode,
			java.util.Map<java.util.Locale, String> nameMap, String key,
			double rate, boolean primary, double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.addCPMeasurementUnit(
			externalReferenceCode, nameMap, key, rate, primary, priority, type,
			serviceContext);
	}

	/**
	 * Creates a new cp measurement unit with the primary key. Does not add the cp measurement unit to the database.
	 *
	 * @param CPMeasurementUnitId the primary key for the new cp measurement unit
	 * @return the new cp measurement unit
	 */
	@Override
	public CPMeasurementUnit createCPMeasurementUnit(long CPMeasurementUnitId) {
		return _cpMeasurementUnitLocalService.createCPMeasurementUnit(
			CPMeasurementUnitId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp measurement unit from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPMeasurementUnitLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpMeasurementUnit the cp measurement unit
	 * @return the cp measurement unit that was removed
	 * @throws PortalException
	 */
	@Override
	public CPMeasurementUnit deleteCPMeasurementUnit(
			CPMeasurementUnit cpMeasurementUnit)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.deleteCPMeasurementUnit(
			cpMeasurementUnit);
	}

	/**
	 * Deletes the cp measurement unit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPMeasurementUnitLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit that was removed
	 * @throws PortalException if a cp measurement unit with the primary key could not be found
	 */
	@Override
	public CPMeasurementUnit deleteCPMeasurementUnit(long CPMeasurementUnitId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.deleteCPMeasurementUnit(
			CPMeasurementUnitId);
	}

	@Override
	public void deleteCPMeasurementUnits(long companyId) {
		_cpMeasurementUnitLocalService.deleteCPMeasurementUnits(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpMeasurementUnitLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpMeasurementUnitLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpMeasurementUnitLocalService.dynamicQuery();
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

		return _cpMeasurementUnitLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl</code>.
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

		return _cpMeasurementUnitLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl</code>.
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

		return _cpMeasurementUnitLocalService.dynamicQuery(
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

		return _cpMeasurementUnitLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpMeasurementUnitLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPMeasurementUnit fetchCPMeasurementUnit(long CPMeasurementUnitId) {
		return _cpMeasurementUnitLocalService.fetchCPMeasurementUnit(
			CPMeasurementUnitId);
	}

	@Override
	public CPMeasurementUnit fetchCPMeasurementUnit(long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.fetchCPMeasurementUnit(
			companyId, key);
	}

	@Override
	public CPMeasurementUnit fetchCPMeasurementUnitByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpMeasurementUnitLocalService.
			fetchCPMeasurementUnitByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp measurement unit matching the UUID and group.
	 *
	 * @param uuid the cp measurement unit's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit fetchCPMeasurementUnitByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpMeasurementUnitLocalService.
			fetchCPMeasurementUnitByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public CPMeasurementUnit fetchPrimaryCPMeasurementUnit(
		long companyId, int type) {

		return _cpMeasurementUnitLocalService.fetchPrimaryCPMeasurementUnit(
			companyId, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpMeasurementUnitLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp measurement unit with the primary key.
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit
	 * @throws PortalException if a cp measurement unit with the primary key could not be found
	 */
	@Override
	public CPMeasurementUnit getCPMeasurementUnit(long CPMeasurementUnitId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.getCPMeasurementUnit(
			CPMeasurementUnitId);
	}

	@Override
	public CPMeasurementUnit getCPMeasurementUnit(long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.getCPMeasurementUnit(
			companyId, key);
	}

	@Override
	public CPMeasurementUnit getCPMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.
			getCPMeasurementUnitByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp measurement unit matching the UUID and group.
	 *
	 * @param uuid the cp measurement unit's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp measurement unit
	 * @throws PortalException if a matching cp measurement unit could not be found
	 */
	@Override
	public CPMeasurementUnit getCPMeasurementUnitByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.
			getCPMeasurementUnitByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp measurement units.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @return the range of cp measurement units
	 */
	@Override
	public java.util.List<CPMeasurementUnit> getCPMeasurementUnits(
		int start, int end) {

		return _cpMeasurementUnitLocalService.getCPMeasurementUnits(start, end);
	}

	@Override
	public java.util.List<CPMeasurementUnit> getCPMeasurementUnits(
			long companyId, int type, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPMeasurementUnit>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.getCPMeasurementUnits(
			companyId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPMeasurementUnit> getCPMeasurementUnits(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPMeasurementUnit>
			orderByComparator) {

		return _cpMeasurementUnitLocalService.getCPMeasurementUnits(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp measurement units matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp measurement units
	 * @param companyId the primary key of the company
	 * @return the matching cp measurement units, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPMeasurementUnit>
		getCPMeasurementUnitsByUuidAndCompanyId(String uuid, long companyId) {

		return _cpMeasurementUnitLocalService.
			getCPMeasurementUnitsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp measurement units matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp measurement units
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp measurement units
	 * @param end the upper bound of the range of cp measurement units (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp measurement units, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPMeasurementUnit>
		getCPMeasurementUnitsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPMeasurementUnit>
				orderByComparator) {

		return _cpMeasurementUnitLocalService.
			getCPMeasurementUnitsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp measurement units.
	 *
	 * @return the number of cp measurement units
	 */
	@Override
	public int getCPMeasurementUnitsCount() {
		return _cpMeasurementUnitLocalService.getCPMeasurementUnitsCount();
	}

	@Override
	public int getCPMeasurementUnitsCount(long companyId) {
		return _cpMeasurementUnitLocalService.getCPMeasurementUnitsCount(
			companyId);
	}

	@Override
	public int getCPMeasurementUnitsCount(long companyId, int type) {
		return _cpMeasurementUnitLocalService.getCPMeasurementUnitsCount(
			companyId, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpMeasurementUnitLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpMeasurementUnitLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CPMeasurementUnit getOrAddEmptyCPMeasurementUnit(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.getOrAddEmptyCPMeasurementUnit(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpMeasurementUnitLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void importDefaultValues(
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpMeasurementUnitLocalService.importDefaultValues(serviceContext);
	}

	@Override
	public CPMeasurementUnit setPrimary(
			long cpMeasurementUnitId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.setPrimary(
			cpMeasurementUnitId, primary);
	}

	/**
	 * Updates the cp measurement unit in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPMeasurementUnitLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpMeasurementUnit the cp measurement unit
	 * @return the cp measurement unit that was updated
	 */
	@Override
	public CPMeasurementUnit updateCPMeasurementUnit(
		CPMeasurementUnit cpMeasurementUnit) {

		return _cpMeasurementUnitLocalService.updateCPMeasurementUnit(
			cpMeasurementUnit);
	}

	@Override
	public CPMeasurementUnit updateCPMeasurementUnit(
			String externalReferenceCode, long cpMeasurementUnitId,
			java.util.Map<java.util.Locale, String> nameMap, String key,
			double rate, boolean primary, double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpMeasurementUnitLocalService.updateCPMeasurementUnit(
			externalReferenceCode, cpMeasurementUnitId, nameMap, key, rate,
			primary, priority, type, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpMeasurementUnitLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPMeasurementUnit> getCTPersistence() {
		return _cpMeasurementUnitLocalService.getCTPersistence();
	}

	@Override
	public Class<CPMeasurementUnit> getModelClass() {
		return _cpMeasurementUnitLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPMeasurementUnit>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpMeasurementUnitLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPMeasurementUnitLocalService getWrappedService() {
		return _cpMeasurementUnitLocalService;
	}

	@Override
	public void setWrappedService(
		CPMeasurementUnitLocalService cpMeasurementUnitLocalService) {

		_cpMeasurementUnitLocalService = cpMeasurementUnitLocalService;
	}

	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1363514882