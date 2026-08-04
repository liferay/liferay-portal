/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPMeasurementUnit;
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
 * Provides the local service utility for CPMeasurementUnit. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPMeasurementUnitLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPMeasurementUnitLocalService
 * @generated
 */
public class CPMeasurementUnitLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPMeasurementUnitLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPMeasurementUnit addCPMeasurementUnit(
		CPMeasurementUnit cpMeasurementUnit) {

		return getService().addCPMeasurementUnit(cpMeasurementUnit);
	}

	public static CPMeasurementUnit addCPMeasurementUnit(
			String externalReferenceCode, Map<java.util.Locale, String> nameMap,
			String key, double rate, boolean primary, double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPMeasurementUnit(
			externalReferenceCode, nameMap, key, rate, primary, priority, type,
			serviceContext);
	}

	/**
	 * Creates a new cp measurement unit with the primary key. Does not add the cp measurement unit to the database.
	 *
	 * @param CPMeasurementUnitId the primary key for the new cp measurement unit
	 * @return the new cp measurement unit
	 */
	public static CPMeasurementUnit createCPMeasurementUnit(
		long CPMeasurementUnitId) {

		return getService().createCPMeasurementUnit(CPMeasurementUnitId);
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
	public static CPMeasurementUnit deleteCPMeasurementUnit(
			CPMeasurementUnit cpMeasurementUnit)
		throws PortalException {

		return getService().deleteCPMeasurementUnit(cpMeasurementUnit);
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
	public static CPMeasurementUnit deleteCPMeasurementUnit(
			long CPMeasurementUnitId)
		throws PortalException {

		return getService().deleteCPMeasurementUnit(CPMeasurementUnitId);
	}

	public static void deleteCPMeasurementUnits(long companyId) {
		getService().deleteCPMeasurementUnits(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPMeasurementUnitModelImpl</code>.
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

	public static CPMeasurementUnit fetchCPMeasurementUnit(
		long CPMeasurementUnitId) {

		return getService().fetchCPMeasurementUnit(CPMeasurementUnitId);
	}

	public static CPMeasurementUnit fetchCPMeasurementUnit(
			long companyId, String key)
		throws PortalException {

		return getService().fetchCPMeasurementUnit(companyId, key);
	}

	public static CPMeasurementUnit
		fetchCPMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCPMeasurementUnitByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp measurement unit matching the UUID and group.
	 *
	 * @param uuid the cp measurement unit's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp measurement unit, or <code>null</code> if a matching cp measurement unit could not be found
	 */
	public static CPMeasurementUnit fetchCPMeasurementUnitByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCPMeasurementUnitByUuidAndGroupId(
			uuid, groupId);
	}

	public static CPMeasurementUnit fetchPrimaryCPMeasurementUnit(
		long companyId, int type) {

		return getService().fetchPrimaryCPMeasurementUnit(companyId, type);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp measurement unit with the primary key.
	 *
	 * @param CPMeasurementUnitId the primary key of the cp measurement unit
	 * @return the cp measurement unit
	 * @throws PortalException if a cp measurement unit with the primary key could not be found
	 */
	public static CPMeasurementUnit getCPMeasurementUnit(
			long CPMeasurementUnitId)
		throws PortalException {

		return getService().getCPMeasurementUnit(CPMeasurementUnitId);
	}

	public static CPMeasurementUnit getCPMeasurementUnit(
			long companyId, String key)
		throws PortalException {

		return getService().getCPMeasurementUnit(companyId, key);
	}

	public static CPMeasurementUnit getCPMeasurementUnitByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPMeasurementUnitByExternalReferenceCode(
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
	public static CPMeasurementUnit getCPMeasurementUnitByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCPMeasurementUnitByUuidAndGroupId(uuid, groupId);
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
	public static List<CPMeasurementUnit> getCPMeasurementUnits(
		int start, int end) {

		return getService().getCPMeasurementUnits(start, end);
	}

	public static List<CPMeasurementUnit> getCPMeasurementUnits(
			long companyId, int type, int start, int end,
			OrderByComparator<CPMeasurementUnit> orderByComparator)
		throws PortalException {

		return getService().getCPMeasurementUnits(
			companyId, type, start, end, orderByComparator);
	}

	public static List<CPMeasurementUnit> getCPMeasurementUnits(
		long companyId, int start, int end,
		OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return getService().getCPMeasurementUnits(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp measurement units matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp measurement units
	 * @param companyId the primary key of the company
	 * @return the matching cp measurement units, or an empty list if no matches were found
	 */
	public static List<CPMeasurementUnit>
		getCPMeasurementUnitsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getCPMeasurementUnitsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPMeasurementUnit>
		getCPMeasurementUnitsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPMeasurementUnit> orderByComparator) {

		return getService().getCPMeasurementUnitsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp measurement units.
	 *
	 * @return the number of cp measurement units
	 */
	public static int getCPMeasurementUnitsCount() {
		return getService().getCPMeasurementUnitsCount();
	}

	public static int getCPMeasurementUnitsCount(long companyId) {
		return getService().getCPMeasurementUnitsCount(companyId);
	}

	public static int getCPMeasurementUnitsCount(long companyId, int type) {
		return getService().getCPMeasurementUnitsCount(companyId, type);
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

	public static CPMeasurementUnit getOrAddEmptyCPMeasurementUnit(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCPMeasurementUnit(
			externalReferenceCode, companyId, userId);
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

	public static void importDefaultValues(
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().importDefaultValues(serviceContext);
	}

	public static CPMeasurementUnit setPrimary(
			long cpMeasurementUnitId, boolean primary)
		throws PortalException {

		return getService().setPrimary(cpMeasurementUnitId, primary);
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
	public static CPMeasurementUnit updateCPMeasurementUnit(
		CPMeasurementUnit cpMeasurementUnit) {

		return getService().updateCPMeasurementUnit(cpMeasurementUnit);
	}

	public static CPMeasurementUnit updateCPMeasurementUnit(
			String externalReferenceCode, long cpMeasurementUnitId,
			Map<java.util.Locale, String> nameMap, String key, double rate,
			boolean primary, double priority, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPMeasurementUnit(
			externalReferenceCode, cpMeasurementUnitId, nameMap, key, rate,
			primary, priority, type, serviceContext);
	}

	public static CPMeasurementUnitLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPMeasurementUnitLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPMeasurementUnitLocalServiceUtil.class,
			CPMeasurementUnitLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-512594067