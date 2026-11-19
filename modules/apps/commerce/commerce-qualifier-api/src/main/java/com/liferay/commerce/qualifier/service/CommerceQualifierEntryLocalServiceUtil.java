/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service;

import com.liferay.commerce.qualifier.model.CommerceQualifierEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceQualifierEntry. This utility wraps
 * <code>com.liferay.commerce.qualifier.service.impl.CommerceQualifierEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Riccardo Alberti
 * @see CommerceQualifierEntryLocalService
 * @generated
 */
public class CommerceQualifierEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.qualifier.service.impl.CommerceQualifierEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce qualifier entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was added
	 */
	public static CommerceQualifierEntry addCommerceQualifierEntry(
		CommerceQualifierEntry commerceQualifierEntry) {

		return getService().addCommerceQualifierEntry(commerceQualifierEntry);
	}

	public static CommerceQualifierEntry addCommerceQualifierEntry(
			long userId, String sourceClassName, long sourceClassPK,
			String sourceCommerceQualifierMetadataKey, String targetClassName,
			long targetClassPK, String targetCommerceQualifierMetadataKey)
		throws PortalException {

		return getService().addCommerceQualifierEntry(
			userId, sourceClassName, sourceClassPK,
			sourceCommerceQualifierMetadataKey, targetClassName, targetClassPK,
			targetCommerceQualifierMetadataKey);
	}

	/**
	 * Creates a new commerce qualifier entry with the primary key. Does not add the commerce qualifier entry to the database.
	 *
	 * @param commerceQualifierEntryId the primary key for the new commerce qualifier entry
	 * @return the new commerce qualifier entry
	 */
	public static CommerceQualifierEntry createCommerceQualifierEntry(
		long commerceQualifierEntryId) {

		return getService().createCommerceQualifierEntry(
			commerceQualifierEntryId);
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
	 * Deletes the commerce qualifier entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws PortalException
	 */
	public static CommerceQualifierEntry deleteCommerceQualifierEntry(
			CommerceQualifierEntry commerceQualifierEntry)
		throws PortalException {

		return getService().deleteCommerceQualifierEntry(
			commerceQualifierEntry);
	}

	/**
	 * Deletes the commerce qualifier entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws PortalException if a commerce qualifier entry with the primary key could not be found
	 */
	public static CommerceQualifierEntry deleteCommerceQualifierEntry(
			long commerceQualifierEntryId)
		throws PortalException {

		return getService().deleteCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteSourceCommerceQualifierEntries(
			String sourceClassName, long sourceClassPK)
		throws PortalException {

		getService().deleteSourceCommerceQualifierEntries(
			sourceClassName, sourceClassPK);
	}

	public static void deleteTargetCommerceQualifierEntries(
			String targetClassName, long targetClassPK)
		throws PortalException {

		getService().deleteTargetCommerceQualifierEntries(
			targetClassName, targetClassPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
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

	public static CommerceQualifierEntry fetchCommerceQualifierEntry(
		long commerceQualifierEntryId) {

		return getService().fetchCommerceQualifierEntry(
			commerceQualifierEntryId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce qualifier entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @return the range of commerce qualifier entries
	 */
	public static List<CommerceQualifierEntry> getCommerceQualifierEntries(
		int start, int end) {

		return getService().getCommerceQualifierEntries(start, end);
	}

	/**
	 * Returns the number of commerce qualifier entries.
	 *
	 * @return the number of commerce qualifier entries
	 */
	public static int getCommerceQualifierEntriesCount() {
		return getService().getCommerceQualifierEntriesCount();
	}

	/**
	 * Returns the commerce qualifier entry with the primary key.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry
	 * @throws PortalException if a commerce qualifier entry with the primary key could not be found
	 */
	public static CommerceQualifierEntry getCommerceQualifierEntry(
			long commerceQualifierEntryId)
		throws PortalException {

		return getService().getCommerceQualifierEntry(commerceQualifierEntryId);
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

	public static List<CommerceQualifierEntry>
			getSourceCommerceQualifierEntries(
				long companyId, String sourceClassName, long sourceClassPK,
				String targetCommerceQualifierMetadataKey, String keywords,
				int start, int end)
		throws PortalException {

		return getService().getSourceCommerceQualifierEntries(
			companyId, sourceClassName, sourceClassPK,
			targetCommerceQualifierMetadataKey, keywords, start, end);
	}

	public static int getSourceCommerceQualifierEntriesCount(
			long companyId, String sourceClassName, long sourceClassPK,
			String targetCommerceQualifierMetadataKey, String keywords)
		throws PortalException {

		return getService().getSourceCommerceQualifierEntriesCount(
			companyId, sourceClassName, sourceClassPK,
			targetCommerceQualifierMetadataKey, keywords);
	}

	public static List<CommerceQualifierEntry>
			getTargetCommerceQualifierEntries(
				long companyId, String sourceCommerceQualifierMetadataKey,
				String targetClassName, long targetClassPK, String keywords,
				int start, int end)
		throws PortalException {

		return getService().getTargetCommerceQualifierEntries(
			companyId, sourceCommerceQualifierMetadataKey, targetClassName,
			targetClassPK, keywords, start, end);
	}

	public static int getTargetCommerceQualifierEntriesCount(
			long companyId, String sourceCommerceQualifierMetadataKey,
			String targetClassName, long targetClassPK, String keywords)
		throws PortalException {

		return getService().getTargetCommerceQualifierEntriesCount(
			companyId, sourceCommerceQualifierMetadataKey, targetClassName,
			targetClassPK, keywords);
	}

	/**
	 * Updates the commerce qualifier entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceQualifierEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceQualifierEntry the commerce qualifier entry
	 * @return the commerce qualifier entry that was updated
	 */
	public static CommerceQualifierEntry updateCommerceQualifierEntry(
		CommerceQualifierEntry commerceQualifierEntry) {

		return getService().updateCommerceQualifierEntry(
			commerceQualifierEntry);
	}

	public static CommerceQualifierEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceQualifierEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceQualifierEntryLocalServiceUtil.class,
			CommerceQualifierEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-865288249