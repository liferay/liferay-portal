/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.language.override.model.PLOEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for PLOEntry. This utility wraps
 * <code>com.liferay.portal.language.override.service.impl.PLOEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Drew Brokke
 * @see PLOEntryLocalService
 * @generated
 */
public class PLOEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.language.override.service.impl.PLOEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static PLOEntry addOrUpdatePLOEntry(
			String externalReferenceCode, long companyId, long userId,
			String key, String languageId, String value)
		throws PortalException {

		return getService().addOrUpdatePLOEntry(
			externalReferenceCode, companyId, userId, key, languageId, value);
	}

	/**
	 * Adds the plo entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PLOEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ploEntry the plo entry
	 * @return the plo entry that was added
	 */
	public static PLOEntry addPLOEntry(PLOEntry ploEntry) {
		return getService().addPLOEntry(ploEntry);
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
	 * Creates a new plo entry with the primary key. Does not add the plo entry to the database.
	 *
	 * @param ploEntryId the primary key for the new plo entry
	 * @return the new plo entry
	 */
	public static PLOEntry createPLOEntry(long ploEntryId) {
		return getService().createPLOEntry(ploEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deletePLOEntries(long companyId, String key) {
		getService().deletePLOEntries(companyId, key);
	}

	/**
	 * Deletes the plo entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PLOEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ploEntryId the primary key of the plo entry
	 * @return the plo entry that was removed
	 * @throws PortalException if a plo entry with the primary key could not be found
	 */
	public static PLOEntry deletePLOEntry(long ploEntryId)
		throws PortalException {

		return getService().deletePLOEntry(ploEntryId);
	}

	public static PLOEntry deletePLOEntry(
		long companyId, String key, String languageId) {

		return getService().deletePLOEntry(companyId, key, languageId);
	}

	/**
	 * Deletes the plo entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PLOEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ploEntry the plo entry
	 * @return the plo entry that was removed
	 */
	public static PLOEntry deletePLOEntry(PLOEntry ploEntry) {
		return getService().deletePLOEntry(ploEntry);
	}

	public static PLOEntry deletePLOEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().deletePLOEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.language.override.model.impl.PLOEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.language.override.model.impl.PLOEntryModelImpl</code>.
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

	public static PLOEntry fetchPLOEntry(long ploEntryId) {
		return getService().fetchPLOEntry(ploEntryId);
	}

	public static PLOEntry fetchPLOEntry(
		long companyId, String key, String languageId) {

		return getService().fetchPLOEntry(companyId, key, languageId);
	}

	public static PLOEntry fetchPLOEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchPLOEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
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
	 * Returns a range of all the plo entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.language.override.model.impl.PLOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of plo entries
	 * @param end the upper bound of the range of plo entries (not inclusive)
	 * @return the range of plo entries
	 */
	public static List<PLOEntry> getPLOEntries(int start, int end) {
		return getService().getPLOEntries(start, end);
	}

	public static List<PLOEntry> getPLOEntries(long companyId) {
		return getService().getPLOEntries(companyId);
	}

	public static List<PLOEntry> getPLOEntries(
		long companyId, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator) {

		return getService().getPLOEntries(
			companyId, start, end, orderByComparator);
	}

	public static List<PLOEntry> getPLOEntries(
		long companyId, String languageId) {

		return getService().getPLOEntries(companyId, languageId);
	}

	public static List<PLOEntry> getPLOEntries(
		long companyId, String keywords, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator) {

		return getService().getPLOEntries(
			companyId, keywords, start, end, orderByComparator);
	}

	/**
	 * Returns the number of plo entries.
	 *
	 * @return the number of plo entries
	 */
	public static int getPLOEntriesCount() {
		return getService().getPLOEntriesCount();
	}

	public static int getPLOEntriesCount(long companyId) {
		return getService().getPLOEntriesCount(companyId);
	}

	public static int getPLOEntriesCount(long companyId, String keywords) {
		return getService().getPLOEntriesCount(companyId, keywords);
	}

	/**
	 * Returns the plo entry with the primary key.
	 *
	 * @param ploEntryId the primary key of the plo entry
	 * @return the plo entry
	 * @throws PortalException if a plo entry with the primary key could not be found
	 */
	public static PLOEntry getPLOEntry(long ploEntryId) throws PortalException {
		return getService().getPLOEntry(ploEntryId);
	}

	public static PLOEntry getPLOEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getPLOEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static void importPLOEntries(
			long companyId, long userId, String languageId,
			java.util.Properties properties)
		throws PortalException {

		getService().importPLOEntries(
			companyId, userId, languageId, properties);
	}

	public static void setPLOEntries(
			long companyId, long userId, String key,
			Map<java.util.Locale, String> localizationMap)
		throws PortalException {

		getService().setPLOEntries(companyId, userId, key, localizationMap);
	}

	/**
	 * Updates the plo entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PLOEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ploEntry the plo entry
	 * @return the plo entry that was updated
	 */
	public static PLOEntry updatePLOEntry(PLOEntry ploEntry) {
		return getService().updatePLOEntry(ploEntry);
	}

	public static PLOEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PLOEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			PLOEntryLocalServiceUtil.class, PLOEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2017307110