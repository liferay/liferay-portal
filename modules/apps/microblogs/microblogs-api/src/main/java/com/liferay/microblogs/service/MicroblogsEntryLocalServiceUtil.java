/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service;

import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MicroblogsEntry. This utility wraps
 * <code>com.liferay.microblogs.service.impl.MicroblogsEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MicroblogsEntryLocalService
 * @generated
 */
public class MicroblogsEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.microblogs.service.impl.MicroblogsEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MicroblogsEntry addMicroblogsEntry(
			long userId, long creatorClassNameId, long creatorClassPK,
			String content, int type, long parentMicroblogsEntryId,
			int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMicroblogsEntry(
			userId, creatorClassNameId, creatorClassPK, content, type,
			parentMicroblogsEntryId, socialRelationType, serviceContext);
	}

	public static MicroblogsEntry addMicroblogsEntry(
			long userId, String content, int type, long parentMicroblogsEntryId,
			int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMicroblogsEntry(
			userId, content, type, parentMicroblogsEntryId, socialRelationType,
			serviceContext);
	}

	/**
	 * Adds the microblogs entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was added
	 */
	public static MicroblogsEntry addMicroblogsEntry(
		MicroblogsEntry microblogsEntry) {

		return getService().addMicroblogsEntry(microblogsEntry);
	}

	/**
	 * Creates a new microblogs entry with the primary key. Does not add the microblogs entry to the database.
	 *
	 * @param microblogsEntryId the primary key for the new microblogs entry
	 * @return the new microblogs entry
	 */
	public static MicroblogsEntry createMicroblogsEntry(
		long microblogsEntryId) {

		return getService().createMicroblogsEntry(microblogsEntryId);
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
	 * Deletes the microblogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws PortalException if a microblogs entry with the primary key could not be found
	 */
	public static MicroblogsEntry deleteMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		return getService().deleteMicroblogsEntry(microblogsEntryId);
	}

	/**
	 * Deletes the microblogs entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws PortalException
	 */
	public static MicroblogsEntry deleteMicroblogsEntry(
			MicroblogsEntry microblogsEntry)
		throws PortalException {

		return getService().deleteMicroblogsEntry(microblogsEntry);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteUserMicroblogsEntries(long userId)
		throws PortalException {

		getService().deleteUserMicroblogsEntries(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
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

	public static MicroblogsEntry fetchMicroblogsEntry(long microblogsEntryId) {
		return getService().fetchMicroblogsEntry(microblogsEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<MicroblogsEntry> getCompanyMicroblogsEntries(
		long companyId, int start, int end) {

		return getService().getCompanyMicroblogsEntries(companyId, start, end);
	}

	public static int getCompanyMicroblogsEntriesCount(long companyId) {
		return getService().getCompanyMicroblogsEntriesCount(companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the microblogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of microblogs entries
	 */
	public static List<MicroblogsEntry> getMicroblogsEntries(
		int start, int end) {

		return getService().getMicroblogsEntries(start, end);
	}

	/**
	 * Returns the number of microblogs entries.
	 *
	 * @return the number of microblogs entries
	 */
	public static int getMicroblogsEntriesCount() {
		return getService().getMicroblogsEntriesCount();
	}

	/**
	 * Returns the microblogs entry with the primary key.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry
	 * @throws PortalException if a microblogs entry with the primary key could not be found
	 */
	public static MicroblogsEntry getMicroblogsEntry(long microblogsEntryId)
		throws PortalException {

		return getService().getMicroblogsEntry(microblogsEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<MicroblogsEntry>
		getParentMicroblogsEntryMicroblogsEntries(
			int type, long parentMicroblogsEntryId, int start, int end) {

		return getService().getParentMicroblogsEntryMicroblogsEntries(
			type, parentMicroblogsEntryId, start, end);
	}

	public static List<MicroblogsEntry>
		getParentMicroblogsEntryMicroblogsEntries(
			int type, long parentMicroblogsEntryId, int start, int end,
			OrderByComparator<MicroblogsEntry> orderByComparator) {

		return getService().getParentMicroblogsEntryMicroblogsEntries(
			type, parentMicroblogsEntryId, start, end, orderByComparator);
	}

	public static int getParentMicroblogsEntryMicroblogsEntriesCount(
		int type, long parentMicroblogsEntryId) {

		return getService().getParentMicroblogsEntryMicroblogsEntriesCount(
			type, parentMicroblogsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<MicroblogsEntry> getUserMicroblogsEntries(
		long userId, int start, int end) {

		return getService().getUserMicroblogsEntries(userId, start, end);
	}

	public static List<MicroblogsEntry> getUserMicroblogsEntries(
		long userId, int type, int start, int end) {

		return getService().getUserMicroblogsEntries(userId, type, start, end);
	}

	public static int getUserMicroblogsEntriesCount(long userId) {
		return getService().getUserMicroblogsEntriesCount(userId);
	}

	public static int getUserMicroblogsEntriesCount(long userId, int type) {
		return getService().getUserMicroblogsEntriesCount(userId, type);
	}

	public static void updateAsset(
			MicroblogsEntry microblogsEntry, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(
			microblogsEntry, assetCategoryIds, assetTagNames);
	}

	public static MicroblogsEntry updateMicroblogsEntry(
			long microblogsEntryId, String content, int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateMicroblogsEntry(
			microblogsEntryId, content, socialRelationType, serviceContext);
	}

	/**
	 * Updates the microblogs entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was updated
	 */
	public static MicroblogsEntry updateMicroblogsEntry(
		MicroblogsEntry microblogsEntry) {

		return getService().updateMicroblogsEntry(microblogsEntry);
	}

	public static MicroblogsEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MicroblogsEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			MicroblogsEntryLocalServiceUtil.class,
			MicroblogsEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1020735312