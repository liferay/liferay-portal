/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.service;

import com.liferay.commerce.machine.learning.forecast.alert.model.CommerceMLForecastAlertEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceMLForecastAlertEntry. This utility wraps
 * <code>com.liferay.commerce.machine.learning.forecast.alert.service.impl.CommerceMLForecastAlertEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Riccardo Ferrari
 * @see CommerceMLForecastAlertEntryLocalService
 * @generated
 */
public class CommerceMLForecastAlertEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.machine.learning.forecast.alert.service.impl.CommerceMLForecastAlertEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce ml forecast alert entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceMLForecastAlertEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceMLForecastAlertEntry the commerce ml forecast alert entry
	 * @return the commerce ml forecast alert entry that was added
	 */
	public static CommerceMLForecastAlertEntry addCommerceMLForecastAlertEntry(
		CommerceMLForecastAlertEntry commerceMLForecastAlertEntry) {

		return getService().addCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntry);
	}

	public static CommerceMLForecastAlertEntry
			addOrUpdateCommerceMLForecastAlertEntry(
				long companyId, long userId, long commerceAccountId,
				java.util.Date timestamp, float actual, float forecast,
				float relativeChange)
		throws PortalException {

		return getService().addOrUpdateCommerceMLForecastAlertEntry(
			companyId, userId, commerceAccountId, timestamp, actual, forecast,
			relativeChange);
	}

	/**
	 * Creates a new commerce ml forecast alert entry with the primary key. Does not add the commerce ml forecast alert entry to the database.
	 *
	 * @param commerceMLForecastAlertEntryId the primary key for the new commerce ml forecast alert entry
	 * @return the new commerce ml forecast alert entry
	 */
	public static CommerceMLForecastAlertEntry
		createCommerceMLForecastAlertEntry(
			long commerceMLForecastAlertEntryId) {

		return getService().createCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntryId);
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
	 * Deletes the commerce ml forecast alert entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceMLForecastAlertEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceMLForecastAlertEntry the commerce ml forecast alert entry
	 * @return the commerce ml forecast alert entry that was removed
	 */
	public static CommerceMLForecastAlertEntry
		deleteCommerceMLForecastAlertEntry(
			CommerceMLForecastAlertEntry commerceMLForecastAlertEntry) {

		return getService().deleteCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntry);
	}

	/**
	 * Deletes the commerce ml forecast alert entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceMLForecastAlertEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceMLForecastAlertEntryId the primary key of the commerce ml forecast alert entry
	 * @return the commerce ml forecast alert entry that was removed
	 * @throws PortalException if a commerce ml forecast alert entry with the primary key could not be found
	 */
	public static CommerceMLForecastAlertEntry
			deleteCommerceMLForecastAlertEntry(
				long commerceMLForecastAlertEntryId)
		throws PortalException {

		return getService().deleteCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.machine.learning.forecast.alert.model.impl.CommerceMLForecastAlertEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.machine.learning.forecast.alert.model.impl.CommerceMLForecastAlertEntryModelImpl</code>.
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

	public static CommerceMLForecastAlertEntry
		fetchCommerceMLForecastAlertEntry(long commerceMLForecastAlertEntryId) {

		return getService().fetchCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntryId);
	}

	/**
	 * Returns the commerce ml forecast alert entry with the matching UUID and company.
	 *
	 * @param uuid the commerce ml forecast alert entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce ml forecast alert entry, or <code>null</code> if a matching commerce ml forecast alert entry could not be found
	 */
	public static CommerceMLForecastAlertEntry
		fetchCommerceMLForecastAlertEntryByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommerceMLForecastAlertEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce ml forecast alert entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.machine.learning.forecast.alert.model.impl.CommerceMLForecastAlertEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce ml forecast alert entries
	 * @param end the upper bound of the range of commerce ml forecast alert entries (not inclusive)
	 * @return the range of commerce ml forecast alert entries
	 */
	public static List<CommerceMLForecastAlertEntry>
		getCommerceMLForecastAlertEntries(int start, int end) {

		return getService().getCommerceMLForecastAlertEntries(start, end);
	}

	/**
	 * Returns the number of commerce ml forecast alert entries.
	 *
	 * @return the number of commerce ml forecast alert entries
	 */
	public static int getCommerceMLForecastAlertEntriesCount() {
		return getService().getCommerceMLForecastAlertEntriesCount();
	}

	/**
	 * Returns the commerce ml forecast alert entry with the primary key.
	 *
	 * @param commerceMLForecastAlertEntryId the primary key of the commerce ml forecast alert entry
	 * @return the commerce ml forecast alert entry
	 * @throws PortalException if a commerce ml forecast alert entry with the primary key could not be found
	 */
	public static CommerceMLForecastAlertEntry getCommerceMLForecastAlertEntry(
			long commerceMLForecastAlertEntryId)
		throws PortalException {

		return getService().getCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntryId);
	}

	/**
	 * Returns the commerce ml forecast alert entry with the matching UUID and company.
	 *
	 * @param uuid the commerce ml forecast alert entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce ml forecast alert entry
	 * @throws PortalException if a matching commerce ml forecast alert entry could not be found
	 */
	public static CommerceMLForecastAlertEntry
			getCommerceMLForecastAlertEntryByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceMLForecastAlertEntryByUuidAndCompanyId(
			uuid, companyId);
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

	/**
	 * Updates the commerce ml forecast alert entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceMLForecastAlertEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceMLForecastAlertEntry the commerce ml forecast alert entry
	 * @return the commerce ml forecast alert entry that was updated
	 */
	public static CommerceMLForecastAlertEntry
		updateCommerceMLForecastAlertEntry(
			CommerceMLForecastAlertEntry commerceMLForecastAlertEntry) {

		return getService().updateCommerceMLForecastAlertEntry(
			commerceMLForecastAlertEntry);
	}

	public static CommerceMLForecastAlertEntry updateStatus(
			long userId, long commerceMLForecastAlertEntryId, int status)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceMLForecastAlertEntryId, status);
	}

	public static CommerceMLForecastAlertEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceMLForecastAlertEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceMLForecastAlertEntryLocalServiceUtil.class,
			CommerceMLForecastAlertEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:320114441