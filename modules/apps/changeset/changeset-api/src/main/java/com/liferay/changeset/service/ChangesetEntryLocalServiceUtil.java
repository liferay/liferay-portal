/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service;

import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Set;

/**
 * Provides the local service utility for ChangesetEntry. This utility wraps
 * <code>com.liferay.changeset.service.impl.ChangesetEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ChangesetEntryLocalService
 * @generated
 */
public class ChangesetEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.changeset.service.impl.ChangesetEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the changeset entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was added
	 */
	public static ChangesetEntry addChangesetEntry(
		ChangesetEntry changesetEntry) {

		return getService().addChangesetEntry(changesetEntry);
	}

	public static ChangesetEntry addChangesetEntry(
			long userId, long changesetCollectionId,
			String classExternalReferenceCode, long classNameId, long classPK)
		throws PortalException {

		return getService().addChangesetEntry(
			userId, changesetCollectionId, classExternalReferenceCode,
			classNameId, classPK);
	}

	/**
	 * Creates a new changeset entry with the primary key. Does not add the changeset entry to the database.
	 *
	 * @param changesetEntryId the primary key for the new changeset entry
	 * @return the new changeset entry
	 */
	public static ChangesetEntry createChangesetEntry(long changesetEntryId) {
		return getService().createChangesetEntry(changesetEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteChangesetEntries(long changesetCollectionId) {
		getService().deleteChangesetEntries(changesetCollectionId);
	}

	public static void deleteChangesetEntries(Set<Long> changesetEntryIds) {
		getService().deleteChangesetEntries(changesetEntryIds);
	}

	/**
	 * Deletes the changeset entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was removed
	 */
	public static ChangesetEntry deleteChangesetEntry(
		ChangesetEntry changesetEntry) {

		return getService().deleteChangesetEntry(changesetEntry);
	}

	/**
	 * Deletes the changeset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry that was removed
	 * @throws PortalException if a changeset entry with the primary key could not be found
	 */
	public static ChangesetEntry deleteChangesetEntry(long changesetEntryId)
		throws PortalException {

		return getService().deleteChangesetEntry(changesetEntryId);
	}

	public static void deleteEntry(
		long changesetId, long classNameId, long classPK) {

		getService().deleteEntry(changesetId, classNameId, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
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

	public static ChangesetEntry fetchChangesetEntry(long changesetEntryId) {
		return getService().fetchChangesetEntry(changesetEntryId);
	}

	public static ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, long classNameId, long classPK) {

		return getService().fetchChangesetEntry(
			changesetCollectionId, classNameId, classPK);
	}

	public static ChangesetEntry fetchChangesetEntry(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return getService().fetchChangesetEntry(
			changesetCollectionId, classExternalReferenceCode, classNameId);
	}

	public static ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, long classNameId, long classPK)
		throws PortalException {

		return getService().fetchOrAddChangesetEntry(
			changesetCollectionId, classNameId, classPK);
	}

	public static ChangesetEntry fetchOrAddChangesetEntry(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId, long classPK)
		throws PortalException {

		return getService().fetchOrAddChangesetEntry(
			changesetCollectionId, classExternalReferenceCode, classNameId,
			classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the changeset entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.changeset.model.impl.ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @return the range of changeset entries
	 */
	public static List<ChangesetEntry> getChangesetEntries(int start, int end) {
		return getService().getChangesetEntries(start, end);
	}

	public static List<ChangesetEntry> getChangesetEntries(
		long changesetCollectionId, long classNameId) {

		return getService().getChangesetEntries(
			changesetCollectionId, classNameId);
	}

	/**
	 * Returns the number of changeset entries.
	 *
	 * @return the number of changeset entries
	 */
	public static int getChangesetEntriesCount() {
		return getService().getChangesetEntriesCount();
	}

	public static long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId) {

		return getService().getChangesetEntriesCount(
			changesetCollectionId, classNameId);
	}

	public static long getChangesetEntriesCount(
		long changesetCollectionId, long classNameId, Set<Long> classPKs) {

		return getService().getChangesetEntriesCount(
			changesetCollectionId, classNameId, classPKs);
	}

	/**
	 * Returns the changeset entry with the primary key.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry
	 * @throws PortalException if a changeset entry with the primary key could not be found
	 */
	public static ChangesetEntry getChangesetEntry(long changesetEntryId)
		throws PortalException {

		return getService().getChangesetEntry(changesetEntryId);
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
	 * Updates the changeset entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ChangesetEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param changesetEntry the changeset entry
	 * @return the changeset entry that was updated
	 */
	public static ChangesetEntry updateChangesetEntry(
		ChangesetEntry changesetEntry) {

		return getService().updateChangesetEntry(changesetEntry);
	}

	public static ChangesetEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ChangesetEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			ChangesetEntryLocalServiceUtil.class,
			ChangesetEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-183084480