/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.persistence;

import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the export import report entry service. This utility wraps <code>com.liferay.exportimport.report.service.persistence.impl.ExportImportReportEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Carlos Correa
 * @see ExportImportReportEntryPersistence
 * @generated
 */
public class ExportImportReportEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		ExportImportReportEntry exportImportReportEntry) {

		getPersistence().clearCache(exportImportReportEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, ExportImportReportEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<ExportImportReportEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<ExportImportReportEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<ExportImportReportEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static ExportImportReportEntry update(
		ExportImportReportEntry exportImportReportEntry) {

		return getPersistence().update(exportImportReportEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static ExportImportReportEntry update(
		ExportImportReportEntry exportImportReportEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(exportImportReportEntry, serviceContext);
	}

	/**
	 * Returns all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the matching export import report entries
	 */
	public static List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId) {

		return getPersistence().findByC_E(
			companyId, exportImportConfigurationId);
	}

	/**
	 * Returns a range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @return the range of matching export import report entries
	 */
	public static List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end) {

		return getPersistence().findByC_E(
			companyId, exportImportConfigurationId, start, end);
	}

	/**
	 * Returns an ordered range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching export import report entries
	 */
	public static List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return getPersistence().findByC_E(
			companyId, exportImportConfigurationId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching export import report entries
	 */
	public static List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_E(
			companyId, exportImportConfigurationId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	public static ExportImportReportEntry findByC_E_First(
			long companyId, long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws com.liferay.exportimport.report.exception.
			NoSuchExportImportReportEntryException {

		return getPersistence().findByC_E_First(
			companyId, exportImportConfigurationId, orderByComparator);
	}

	/**
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	public static ExportImportReportEntry fetchByC_E_First(
		long companyId, long exportImportConfigurationId,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return getPersistence().fetchByC_E_First(
			companyId, exportImportConfigurationId, orderByComparator);
	}

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	public static ExportImportReportEntry findByC_E_Last(
			long companyId, long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws com.liferay.exportimport.report.exception.
			NoSuchExportImportReportEntryException {

		return getPersistence().findByC_E_Last(
			companyId, exportImportConfigurationId, orderByComparator);
	}

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	public static ExportImportReportEntry fetchByC_E_Last(
		long companyId, long exportImportConfigurationId,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return getPersistence().fetchByC_E_Last(
			companyId, exportImportConfigurationId, orderByComparator);
	}

	/**
	 * Returns the export import report entries before and after the current export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param exportImportReportEntryId the primary key of the current export import report entry
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	public static ExportImportReportEntry[] findByC_E_PrevAndNext(
			long exportImportReportEntryId, long companyId,
			long exportImportConfigurationId,
			OrderByComparator<ExportImportReportEntry> orderByComparator)
		throws com.liferay.exportimport.report.exception.
			NoSuchExportImportReportEntryException {

		return getPersistence().findByC_E_PrevAndNext(
			exportImportReportEntryId, companyId, exportImportConfigurationId,
			orderByComparator);
	}

	/**
	 * Removes all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 */
	public static void removeByC_E(
		long companyId, long exportImportConfigurationId) {

		getPersistence().removeByC_E(companyId, exportImportConfigurationId);
	}

	/**
	 * Returns the number of export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the number of matching export import report entries
	 */
	public static int countByC_E(
		long companyId, long exportImportConfigurationId) {

		return getPersistence().countByC_E(
			companyId, exportImportConfigurationId);
	}

	/**
	 * Caches the export import report entry in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntry the export import report entry
	 */
	public static void cacheResult(
		ExportImportReportEntry exportImportReportEntry) {

		getPersistence().cacheResult(exportImportReportEntry);
	}

	/**
	 * Caches the export import report entries in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntries the export import report entries
	 */
	public static void cacheResult(
		List<ExportImportReportEntry> exportImportReportEntries) {

		getPersistence().cacheResult(exportImportReportEntries);
	}

	/**
	 * Creates a new export import report entry with the primary key. Does not add the export import report entry to the database.
	 *
	 * @param exportImportReportEntryId the primary key for the new export import report entry
	 * @return the new export import report entry
	 */
	public static ExportImportReportEntry create(
		long exportImportReportEntryId) {

		return getPersistence().create(exportImportReportEntryId);
	}

	/**
	 * Removes the export import report entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry that was removed
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	public static ExportImportReportEntry remove(long exportImportReportEntryId)
		throws com.liferay.exportimport.report.exception.
			NoSuchExportImportReportEntryException {

		return getPersistence().remove(exportImportReportEntryId);
	}

	public static ExportImportReportEntry updateImpl(
		ExportImportReportEntry exportImportReportEntry) {

		return getPersistence().updateImpl(exportImportReportEntry);
	}

	/**
	 * Returns the export import report entry with the primary key or throws a <code>NoSuchExportImportReportEntryException</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	public static ExportImportReportEntry findByPrimaryKey(
			long exportImportReportEntryId)
		throws com.liferay.exportimport.report.exception.
			NoSuchExportImportReportEntryException {

		return getPersistence().findByPrimaryKey(exportImportReportEntryId);
	}

	/**
	 * Returns the export import report entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry, or <code>null</code> if a export import report entry with the primary key could not be found
	 */
	public static ExportImportReportEntry fetchByPrimaryKey(
		long exportImportReportEntryId) {

		return getPersistence().fetchByPrimaryKey(exportImportReportEntryId);
	}

	/**
	 * Returns all the export import report entries.
	 *
	 * @return the export import report entries
	 */
	public static List<ExportImportReportEntry> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @return the range of export import report entries
	 */
	public static List<ExportImportReportEntry> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of export import report entries
	 */
	public static List<ExportImportReportEntry> findAll(
		int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the export import report entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExportImportReportEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of export import report entries
	 * @param end the upper bound of the range of export import report entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of export import report entries
	 */
	public static List<ExportImportReportEntry> findAll(
		int start, int end,
		OrderByComparator<ExportImportReportEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the export import report entries from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of export import report entries.
	 *
	 * @return the number of export import report entries
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static ExportImportReportEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		ExportImportReportEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile ExportImportReportEntryPersistence _persistence;

}