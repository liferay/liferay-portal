/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.service.persistence;

import com.liferay.exportimport.report.exception.NoSuchExportImportReportEntryException;
import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the export import report entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Carlos Correa
 * @see ExportImportReportEntryUtil
 * @generated
 */
@ProviderType
public interface ExportImportReportEntryPersistence
	extends BasePersistence<ExportImportReportEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ExportImportReportEntryUtil} to access the export import report entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the matching export import report entries
	 */
	public java.util.List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId);

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
	public java.util.List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end);

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
	public java.util.List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator);

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
	public java.util.List<ExportImportReportEntry> findByC_E(
		long companyId, long exportImportConfigurationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	public ExportImportReportEntry findByC_E_First(
			long companyId, long exportImportConfigurationId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException;

	/**
	 * Returns the first export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	public ExportImportReportEntry fetchByC_E_First(
		long companyId, long exportImportConfigurationId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator);

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry
	 * @throws NoSuchExportImportReportEntryException if a matching export import report entry could not be found
	 */
	public ExportImportReportEntry findByC_E_Last(
			long companyId, long exportImportConfigurationId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException;

	/**
	 * Returns the last export import report entry in the ordered set where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching export import report entry, or <code>null</code> if a matching export import report entry could not be found
	 */
	public ExportImportReportEntry fetchByC_E_Last(
		long companyId, long exportImportConfigurationId,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator);

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
	public ExportImportReportEntry[] findByC_E_PrevAndNext(
			long exportImportReportEntryId, long companyId,
			long exportImportConfigurationId,
			com.liferay.portal.kernel.util.OrderByComparator
				<ExportImportReportEntry> orderByComparator)
		throws NoSuchExportImportReportEntryException;

	/**
	 * Removes all the export import report entries where companyId = &#63; and exportImportConfigurationId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 */
	public void removeByC_E(long companyId, long exportImportConfigurationId);

	/**
	 * Returns the number of export import report entries where companyId = &#63; and exportImportConfigurationId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param exportImportConfigurationId the export import configuration ID
	 * @return the number of matching export import report entries
	 */
	public int countByC_E(long companyId, long exportImportConfigurationId);

	/**
	 * Caches the export import report entry in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntry the export import report entry
	 */
	public void cacheResult(ExportImportReportEntry exportImportReportEntry);

	/**
	 * Caches the export import report entries in the entity cache if it is enabled.
	 *
	 * @param exportImportReportEntries the export import report entries
	 */
	public void cacheResult(
		java.util.List<ExportImportReportEntry> exportImportReportEntries);

	/**
	 * Creates a new export import report entry with the primary key. Does not add the export import report entry to the database.
	 *
	 * @param exportImportReportEntryId the primary key for the new export import report entry
	 * @return the new export import report entry
	 */
	public ExportImportReportEntry create(long exportImportReportEntryId);

	/**
	 * Removes the export import report entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry that was removed
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	public ExportImportReportEntry remove(long exportImportReportEntryId)
		throws NoSuchExportImportReportEntryException;

	public ExportImportReportEntry updateImpl(
		ExportImportReportEntry exportImportReportEntry);

	/**
	 * Returns the export import report entry with the primary key or throws a <code>NoSuchExportImportReportEntryException</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry
	 * @throws NoSuchExportImportReportEntryException if a export import report entry with the primary key could not be found
	 */
	public ExportImportReportEntry findByPrimaryKey(
			long exportImportReportEntryId)
		throws NoSuchExportImportReportEntryException;

	/**
	 * Returns the export import report entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param exportImportReportEntryId the primary key of the export import report entry
	 * @return the export import report entry, or <code>null</code> if a export import report entry with the primary key could not be found
	 */
	public ExportImportReportEntry fetchByPrimaryKey(
		long exportImportReportEntryId);

	/**
	 * Returns all the export import report entries.
	 *
	 * @return the export import report entries
	 */
	public java.util.List<ExportImportReportEntry> findAll();

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
	public java.util.List<ExportImportReportEntry> findAll(int start, int end);

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
	public java.util.List<ExportImportReportEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator);

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
	public java.util.List<ExportImportReportEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<ExportImportReportEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the export import report entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of export import report entries.
	 *
	 * @return the number of export import report entries
	 */
	public int countAll();

}