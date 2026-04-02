/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.change.tracking.model.CTEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CTEntry. This utility wraps
 * <code>com.liferay.change.tracking.service.impl.CTEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see CTEntryLocalService
 * @generated
 */
public class CTEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.service.impl.CTEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ct entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was added
	 */
	public static CTEntry addCTEntry(CTEntry ctEntry) {
		return getService().addCTEntry(ctEntry);
	}

	public static CTEntry addCTEntry(
			String externalReferenceCode, long ctCollectionId,
			long modelClassNameId,
			com.liferay.portal.kernel.model.change.tracking.CTModel<?> ctModel,
			long userId, int changeType)
		throws PortalException {

		return getService().addCTEntry(
			externalReferenceCode, ctCollectionId, modelClassNameId, ctModel,
			userId, changeType);
	}

	/**
	 * Creates a new ct entry with the primary key. Does not add the ct entry to the database.
	 *
	 * @param ctEntryId the primary key for the new ct entry
	 * @return the new ct entry
	 */
	public static CTEntry createCTEntry(long ctEntryId) {
		return getService().createCTEntry(ctEntryId);
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
	 * Deletes the ct entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was removed
	 * @throws PortalException
	 */
	public static CTEntry deleteCTEntry(CTEntry ctEntry)
		throws PortalException {

		return getService().deleteCTEntry(ctEntry);
	}

	public static CTEntry deleteCTEntry(CTEntry ctEntry, boolean force)
		throws PortalException {

		return getService().deleteCTEntry(ctEntry, force);
	}

	/**
	 * Deletes the ct entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry that was removed
	 * @throws PortalException if a ct entry with the primary key could not be found
	 */
	public static CTEntry deleteCTEntry(long ctEntryId) throws PortalException {
		return getService().deleteCTEntry(ctEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
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

	public static CTEntry fetchCTEntry(long ctEntryId) {
		return getService().fetchCTEntry(ctEntryId);
	}

	public static CTEntry fetchCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return getService().fetchCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	public static CTEntry fetchCTEntryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCTEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct entry with the matching UUID and company.
	 *
	 * @param uuid the ct entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct entry, or <code>null</code> if a matching ct entry could not be found
	 */
	public static CTEntry fetchCTEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCTEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CTEntry> getCTCollectionCTEntries(long ctCollectionId) {
		return getService().getCTCollectionCTEntries(ctCollectionId);
	}

	public static List<CTEntry> getCTCollectionCTEntries(
		long ctCollectionId, int start, int end,
		OrderByComparator<CTEntry> orderByComparator) {

		return getService().getCTCollectionCTEntries(
			ctCollectionId, start, end, orderByComparator);
	}

	public static int getCTCollectionCTEntriesCount(long ctCollectionId) {
		return getService().getCTCollectionCTEntriesCount(ctCollectionId);
	}

	/**
	 * Returns a range of all the ct entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct entries
	 * @param end the upper bound of the range of ct entries (not inclusive)
	 * @return the range of ct entries
	 */
	public static List<CTEntry> getCTEntries(int start, int end) {
		return getService().getCTEntries(start, end);
	}

	public static List<CTEntry> getCTEntries(
		long ctCollectionId, long modelClassNameId) {

		return getService().getCTEntries(ctCollectionId, modelClassNameId);
	}

	public static List<CTEntry> getCTEntries(long[] ctEntryIds) {
		return getService().getCTEntries(ctEntryIds);
	}

	/**
	 * Returns the number of ct entries.
	 *
	 * @return the number of ct entries
	 */
	public static int getCTEntriesCount() {
		return getService().getCTEntriesCount();
	}

	/**
	 * Returns the ct entry with the primary key.
	 *
	 * @param ctEntryId the primary key of the ct entry
	 * @return the ct entry
	 * @throws PortalException if a ct entry with the primary key could not be found
	 */
	public static CTEntry getCTEntry(long ctEntryId) throws PortalException {
		return getService().getCTEntry(ctEntryId);
	}

	public static CTEntry getCTEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCTEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the ct entry with the matching UUID and company.
	 *
	 * @param uuid the ct entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching ct entry
	 * @throws PortalException if a matching ct entry could not be found
	 */
	public static CTEntry getCTEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCTEntryByUuidAndCompanyId(uuid, companyId);
	}

	public static long getCTRowCTCollectionId(CTEntry ctEntry)
		throws PortalException {

		return getService().getCTRowCTCollectionId(ctEntry);
	}

	public static List<Long> getExclusiveModelClassPKs(
		long ctCollectionId, long modelClassNameId) {

		return getService().getExclusiveModelClassPKs(
			ctCollectionId, modelClassNameId);
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

	public static boolean hasCTEntries(
		long ctCollectionId, long modelClassNameId) {

		return getService().hasCTEntries(ctCollectionId, modelClassNameId);
	}

	public static boolean hasCTEntry(
		long ctCollectionId, long modelClassNameId, long modelClassPK) {

		return getService().hasCTEntry(
			ctCollectionId, modelClassNameId, modelClassPK);
	}

	public static boolean hasUnpublishedCTEntries(
		long modelClassNameId, long modelClassPK, int changeType) {

		return getService().hasUnpublishedCTEntries(
			modelClassNameId, modelClassPK, changeType);
	}

	public static CTEntry updateChangeType(long ctEntryId, int changeType) {
		return getService().updateChangeType(ctEntryId, changeType);
	}

	/**
	 * Updates the ct entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctEntry the ct entry
	 * @return the ct entry that was updated
	 */
	public static CTEntry updateCTEntry(CTEntry ctEntry) {
		return getService().updateCTEntry(ctEntry);
	}

	public static CTEntry updateModelMvccVersion(
		long ctEntryId, long modelMvccVersion) {

		return getService().updateModelMvccVersion(ctEntryId, modelMvccVersion);
	}

	public static CTEntry updateUserId(long ctEntryId, long userId) {
		return getService().updateUserId(ctEntryId, userId);
	}

	public static CTEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			CTEntryLocalServiceUtil.class, CTEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1850296377