/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.change.tracking.model.CTCollection;
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
 * Provides the local service utility for CTCollection. This utility wraps
 * <code>com.liferay.change.tracking.service.impl.CTCollectionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see CTCollectionLocalService
 * @generated
 */
public class CTCollectionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.service.impl.CTCollectionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ct collection to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was added
	 */
	public static CTCollection addCTCollection(CTCollection ctCollection) {
		return getService().addCTCollection(ctCollection);
	}

	public static CTCollection addCTCollection(
			String externalReferenceCode, long companyId, long userId,
			long ctRemoteId, String name, String description)
		throws PortalException {

		return getService().addCTCollection(
			externalReferenceCode, companyId, userId, ctRemoteId, name,
			description);
	}

	public static Map
		<Long, List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(CTCollection ctCollection)
			throws PortalException {

		return getService().checkConflicts(ctCollection);
	}

	public static Map
		<Long, List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(
					long companyId,
					List<com.liferay.change.tracking.model.CTEntry> ctEntries,
					long fromCTCollectionId, String fromCTCollectionName,
					long toCTCollectionId, String toCTCollectionName)
			throws PortalException {

		return getService().checkConflicts(
			companyId, ctEntries, fromCTCollectionId, fromCTCollectionName,
			toCTCollectionId, toCTCollectionName);
	}

	public static Map
		<Long, List<com.liferay.change.tracking.conflict.ConflictInfo>>
				checkConflicts(
					long companyId, long[] ctEntryIds, long fromCTCollectionId,
					String fromCTCollectionName, long toCTCollectionId,
					String toCTCollectionName)
			throws PortalException {

		return getService().checkConflicts(
			companyId, ctEntryIds, fromCTCollectionId, fromCTCollectionName,
			toCTCollectionId, toCTCollectionName);
	}

	/**
	 * Creates a new ct collection with the primary key. Does not add the ct collection to the database.
	 *
	 * @param ctCollectionId the primary key for the new ct collection
	 * @return the new ct collection
	 */
	public static CTCollection createCTCollection(long ctCollectionId) {
		return getService().createCTCollection(ctCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyCTCollections(long companyId)
		throws PortalException {

		getService().deleteCompanyCTCollections(companyId);
	}

	public static void deleteCTAutoResolutionInfo(long ctAutoResolutionInfoId) {
		getService().deleteCTAutoResolutionInfo(ctAutoResolutionInfoId);
	}

	/**
	 * Deletes the ct collection from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was removed
	 * @throws PortalException
	 */
	public static CTCollection deleteCTCollection(CTCollection ctCollection)
		throws PortalException {

		return getService().deleteCTCollection(ctCollection);
	}

	/**
	 * Deletes the ct collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection that was removed
	 * @throws PortalException if a ct collection with the primary key could not be found
	 */
	public static CTCollection deleteCTCollection(long ctCollectionId)
		throws PortalException {

		return getService().deleteCTCollection(ctCollectionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void discardCTEntry(
			long ctCollectionId,
			List<com.liferay.change.tracking.model.CTEntry> ctEntries,
			boolean force)
		throws PortalException {

		getService().discardCTEntry(ctCollectionId, ctEntries, force);
	}

	public static void discardCTEntry(
			long ctCollectionId, long modelClassNameId, long modelClassPK,
			boolean force)
		throws PortalException {

		getService().discardCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK, force);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
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

	public static CTCollection fetchCTCollection(long ctCollectionId) {
		return getService().fetchCTCollection(ctCollectionId);
	}

	public static CTCollection fetchCTCollectionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCTCollectionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct collection with the matching UUID and company.
	 *
	 * @param uuid the ct collection's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct collection, or <code>null</code> if a matching ct collection could not be found
	 */
	public static CTCollection fetchCTCollectionByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCTCollectionByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ct collection with the primary key.
	 *
	 * @param ctCollectionId the primary key of the ct collection
	 * @return the ct collection
	 * @throws PortalException if a ct collection with the primary key could not be found
	 */
	public static CTCollection getCTCollection(long ctCollectionId)
		throws PortalException {

		return getService().getCTCollection(ctCollectionId);
	}

	public static CTCollection getCTCollectionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCTCollectionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct collection with the matching UUID and company.
	 *
	 * @param uuid the ct collection's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct collection
	 * @throws PortalException if a matching ct collection could not be found
	 */
	public static CTCollection getCTCollectionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCTCollectionByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the ct collections.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct collections
	 * @param end the upper bound of the range of ct collections (not inclusive)
	 * @return the range of ct collections
	 */
	public static List<CTCollection> getCTCollections(int start, int end) {
		return getService().getCTCollections(start, end);
	}

	public static List<CTCollection> getCTCollections(
		long companyId, int status, int start, int end,
		OrderByComparator<CTCollection> orderByComparator) {

		return getService().getCTCollections(
			companyId, status, start, end, orderByComparator);
	}

	public static List<CTCollection> getCTCollections(
		long companyId, int[] statuses, int start, int end,
		OrderByComparator<CTCollection> orderByComparator) {

		return getService().getCTCollections(
			companyId, statuses, start, end, orderByComparator);
	}

	public static List<CTCollection> getCTCollections(
		long companyId, long userId, int[] statuses, int start, int end,
		OrderByComparator<CTCollection> orderByComparator) {

		return getService().getCTCollections(
			companyId, userId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ct collections.
	 *
	 * @return the number of ct collections
	 */
	public static int getCTCollectionsCount() {
		return getService().getCTCollectionsCount();
	}

	public static List<com.liferay.change.tracking.mapping.CTMappingTableInfo>
		getCTMappingTableInfos(long ctCollectionId) {

		return getService().getCTMappingTableInfos(ctCollectionId);
	}

	public static List<CTCollection> getExclusivePublishedCTCollections(
			long modelClassNameId, long modelClassPK)
		throws PortalException {

		return getService().getExclusivePublishedCTCollections(
			modelClassNameId, modelClassPK);
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

	public static List<com.liferay.change.tracking.model.CTEntry>
			getRelatedCTEntries(long ctCollectionId, long[] ctEntryIds)
		throws PortalException {

		return getService().getRelatedCTEntries(ctCollectionId, ctEntryIds);
	}

	public static Map<Long, List<com.liferay.change.tracking.model.CTEntry>>
			getRelatedCTEntriesMap(
				long ctCollectionId,
				List<com.liferay.change.tracking.model.CTEntry> ctEntries)
		throws PortalException {

		return getService().getRelatedCTEntriesMap(ctCollectionId, ctEntries);
	}

	public static Map<Long, List<com.liferay.change.tracking.model.CTEntry>>
			getRelatedCTEntriesMap(
				long ctCollectionId, long modelClassNameId, long modelClassPK)
		throws PortalException {

		return getService().getRelatedCTEntriesMap(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	public static Map<Long, List<com.liferay.change.tracking.model.CTEntry>>
			getRelatedCTEntriesMap(long ctCollectionId, long[] ctEntryIds)
		throws PortalException {

		return getService().getRelatedCTEntriesMap(ctCollectionId, ctEntryIds);
	}

	public static boolean hasUnapprovedChanges(long ctCollectionId)
		throws java.sql.SQLException {

		return getService().hasUnapprovedChanges(ctCollectionId);
	}

	public static boolean isCTEntryEnclosed(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return getService().isCTEntryEnclosed(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	public static void moveCTEntries(
			long fromCTCollectionId, long toCTCollectionId,
			List<com.liferay.change.tracking.model.CTEntry> ctEntries)
		throws PortalException {

		getService().moveCTEntries(
			fromCTCollectionId, toCTCollectionId, ctEntries);
	}

	public static void moveCTEntry(
			long fromCTCollectionId, long toCTCollectionId,
			long modelClassNameId, long modelClassPK)
		throws PortalException {

		getService().moveCTEntry(
			fromCTCollectionId, toCTCollectionId, modelClassNameId,
			modelClassPK);
	}

	public static CTCollection undoCTCollection(
			long ctCollectionId, long userId, String name, String description)
		throws PortalException {

		return getService().undoCTCollection(
			ctCollectionId, userId, name, description);
	}

	/**
	 * Updates the ct collection in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCollectionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCollection the ct collection
	 * @return the ct collection that was updated
	 */
	public static CTCollection updateCTCollection(CTCollection ctCollection) {
		return getService().updateCTCollection(ctCollection);
	}

	public static CTCollection updateCTCollection(
			long userId, long ctCollectionId, String name, String description)
		throws PortalException {

		return getService().updateCTCollection(
			userId, ctCollectionId, name, description);
	}

	public static CTCollectionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTCollectionLocalService> _serviceSnapshot =
		new Snapshot<>(
			CTCollectionLocalServiceUtil.class, CTCollectionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-955722249