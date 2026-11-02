/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service;

import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
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
 * Provides the local service utility for CommerceShippingFixedOption. This utility wraps
 * <code>com.liferay.commerce.shipping.engine.fixed.service.impl.CommerceShippingFixedOptionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceShippingFixedOptionLocalService
 * @generated
 */
public class CommerceShippingFixedOptionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.shipping.engine.fixed.service.impl.CommerceShippingFixedOptionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce shipping fixed option to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was added
	 */
	public static CommerceShippingFixedOption addCommerceShippingFixedOption(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		return getService().addCommerceShippingFixedOption(
			commerceShippingFixedOption);
	}

	public static CommerceShippingFixedOption addCommerceShippingFixedOption(
			long userId, long groupId, long commerceShippingMethodId,
			java.math.BigDecimal amount,
			Map<java.util.Locale, String> descriptionMap, String key,
			Map<java.util.Locale, String> nameMap, double priority)
		throws PortalException {

		return getService().addCommerceShippingFixedOption(
			userId, groupId, commerceShippingMethodId, amount, descriptionMap,
			key, nameMap, priority);
	}

	/**
	 * Creates a new commerce shipping fixed option with the primary key. Does not add the commerce shipping fixed option to the database.
	 *
	 * @param commerceShippingFixedOptionId the primary key for the new commerce shipping fixed option
	 * @return the new commerce shipping fixed option
	 */
	public static CommerceShippingFixedOption createCommerceShippingFixedOption(
		long commerceShippingFixedOptionId) {

		return getService().createCommerceShippingFixedOption(
			commerceShippingFixedOptionId);
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
	 * Deletes the commerce shipping fixed option from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was removed
	 */
	public static CommerceShippingFixedOption deleteCommerceShippingFixedOption(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		return getService().deleteCommerceShippingFixedOption(
			commerceShippingFixedOption);
	}

	/**
	 * Deletes the commerce shipping fixed option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was removed
	 * @throws PortalException if a commerce shipping fixed option with the primary key could not be found
	 */
	public static CommerceShippingFixedOption deleteCommerceShippingFixedOption(
			long commerceShippingFixedOptionId)
		throws PortalException {

		return getService().deleteCommerceShippingFixedOption(
			commerceShippingFixedOptionId);
	}

	public static void deleteCommerceShippingFixedOptions(
		long commerceShippingMethodId) {

		getService().deleteCommerceShippingFixedOptions(
			commerceShippingMethodId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
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

	public static CommerceShippingFixedOption fetchCommerceShippingFixedOption(
		long commerceShippingFixedOptionId) {

		return getService().fetchCommerceShippingFixedOption(
			commerceShippingFixedOptionId);
	}

	public static CommerceShippingFixedOption fetchCommerceShippingFixedOption(
		long companyId, String key) {

		return getService().fetchCommerceShippingFixedOption(companyId, key);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceShippingFixedOption>
		getCommerceOrderTypeCommerceShippingFixedOptions(
			long companyId, long commerceOrderTypeId,
			long commerceShippingMethodId) {

		return getService().getCommerceOrderTypeCommerceShippingFixedOptions(
			companyId, commerceOrderTypeId, commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping fixed option with the primary key.
	 *
	 * @param commerceShippingFixedOptionId the primary key of the commerce shipping fixed option
	 * @return the commerce shipping fixed option
	 * @throws PortalException if a commerce shipping fixed option with the primary key could not be found
	 */
	public static CommerceShippingFixedOption getCommerceShippingFixedOption(
			long commerceShippingFixedOptionId)
		throws PortalException {

		return getService().getCommerceShippingFixedOption(
			commerceShippingFixedOptionId);
	}

	/**
	 * Returns a range of all the commerce shipping fixed options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.shipping.engine.fixed.model.impl.CommerceShippingFixedOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce shipping fixed options
	 * @param end the upper bound of the range of commerce shipping fixed options (not inclusive)
	 * @return the range of commerce shipping fixed options
	 */
	public static List<CommerceShippingFixedOption>
		getCommerceShippingFixedOptions(int start, int end) {

		return getService().getCommerceShippingFixedOptions(start, end);
	}

	public static List<CommerceShippingFixedOption>
		getCommerceShippingFixedOptions(
			long commerceShippingMethodId, int start, int end,
			OrderByComparator<CommerceShippingFixedOption> orderByComparator) {

		return getService().getCommerceShippingFixedOptions(
			commerceShippingMethodId, start, end, orderByComparator);
	}

	public static List<CommerceShippingFixedOption>
			getCommerceShippingFixedOptions(
				long companyId, long groupId, long commerceShippingMethodId,
				String keywords, int start, int end)
		throws PortalException {

		return getService().getCommerceShippingFixedOptions(
			companyId, groupId, commerceShippingMethodId, keywords, start, end);
	}

	/**
	 * Returns the number of commerce shipping fixed options.
	 *
	 * @return the number of commerce shipping fixed options
	 */
	public static int getCommerceShippingFixedOptionsCount() {
		return getService().getCommerceShippingFixedOptionsCount();
	}

	public static long getCommerceShippingFixedOptionsCount(
			long companyId, long groupId, long commerceShippingMethodId,
			String keywords)
		throws PortalException {

		return getService().getCommerceShippingFixedOptionsCount(
			companyId, groupId, commerceShippingMethodId, keywords);
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

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceShippingFixedOption> searchCommerceShippingFixedOption(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCommerceShippingFixedOption(searchContext);
	}

	/**
	 * Updates the commerce shipping fixed option in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceShippingFixedOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceShippingFixedOption the commerce shipping fixed option
	 * @return the commerce shipping fixed option that was updated
	 */
	public static CommerceShippingFixedOption updateCommerceShippingFixedOption(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		return getService().updateCommerceShippingFixedOption(
			commerceShippingFixedOption);
	}

	public static CommerceShippingFixedOption updateCommerceShippingFixedOption(
			long commerceShippingFixedOptionId, java.math.BigDecimal amount,
			Map<java.util.Locale, String> descriptionMap, String key,
			Map<java.util.Locale, String> nameMap, double priority)
		throws PortalException {

		return getService().updateCommerceShippingFixedOption(
			commerceShippingFixedOptionId, amount, descriptionMap, key, nameMap,
			priority);
	}

	public static CommerceShippingFixedOptionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceShippingFixedOptionLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceShippingFixedOptionLocalServiceUtil.class,
			CommerceShippingFixedOptionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1677848335