/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionValue;
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
 * Provides the local service utility for CPOptionValue. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPOptionValueLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPOptionValueLocalService
 * @generated
 */
public class CPOptionValueLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPOptionValueLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp option value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was added
	 */
	public static CPOptionValue addCPOptionValue(CPOptionValue cpOptionValue) {
		return getService().addCPOptionValue(cpOptionValue);
	}

	public static CPOptionValue addCPOptionValue(
			long cpOptionId, Map<java.util.Locale, String> nameMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPOptionValue(
			cpOptionId, nameMap, priority, key, serviceContext);
	}

	public static CPOptionValue addCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			Map<java.util.Locale, String> nameMap, double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	public static CPOptionValue addOrUpdateCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			Map<java.util.Locale, String> nameMap, double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	/**
	 * Creates a new cp option value with the primary key. Does not add the cp option value to the database.
	 *
	 * @param CPOptionValueId the primary key for the new cp option value
	 * @return the new cp option value
	 */
	public static CPOptionValue createCPOptionValue(long CPOptionValueId) {
		return getService().createCPOptionValue(CPOptionValueId);
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
	 * Deletes the cp option value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was removed
	 * @throws PortalException
	 */
	public static CPOptionValue deleteCPOptionValue(CPOptionValue cpOptionValue)
		throws PortalException {

		return getService().deleteCPOptionValue(cpOptionValue);
	}

	/**
	 * Deletes the cp option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value that was removed
	 * @throws PortalException if a cp option value with the primary key could not be found
	 */
	public static CPOptionValue deleteCPOptionValue(long CPOptionValueId)
		throws PortalException {

		return getService().deleteCPOptionValue(CPOptionValueId);
	}

	public static void deleteCPOptionValues(long cpOptionId)
		throws PortalException {

		getService().deleteCPOptionValues(cpOptionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
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

	public static CPOptionValue fetchCPOptionValue(long CPOptionValueId) {
		return getService().fetchCPOptionValue(CPOptionValueId);
	}

	public static CPOptionValue fetchCPOptionValueByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCPOptionValueByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option value with the matching UUID and company.
	 *
	 * @param uuid the cp option value's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchCPOptionValueByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCPOptionValueByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp option value with the primary key.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value
	 * @throws PortalException if a cp option value with the primary key could not be found
	 */
	public static CPOptionValue getCPOptionValue(long CPOptionValueId)
		throws PortalException {

		return getService().getCPOptionValue(CPOptionValueId);
	}

	public static CPOptionValue getCPOptionValue(long cpOptionId, String key)
		throws PortalException {

		return getService().getCPOptionValue(cpOptionId, key);
	}

	public static CPOptionValue getCPOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPOptionValueByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option value with the matching UUID and company.
	 *
	 * @param uuid the cp option value's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option value
	 * @throws PortalException if a matching cp option value could not be found
	 */
	public static CPOptionValue getCPOptionValueByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCPOptionValueByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp option values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of cp option values
	 */
	public static List<CPOptionValue> getCPOptionValues(int start, int end) {
		return getService().getCPOptionValues(start, end);
	}

	public static List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end) {

		return getService().getCPOptionValues(cpOptionId, start, end);
	}

	public static List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getService().getCPOptionValues(
			cpOptionId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp option values.
	 *
	 * @return the number of cp option values
	 */
	public static int getCPOptionValuesCount() {
		return getService().getCPOptionValuesCount();
	}

	public static int getCPOptionValuesCount(long cpOptionId) {
		return getService().getCPOptionValuesCount(cpOptionId);
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

	public static CPOptionValue getOrAddEmptyCPOptionValue(
			String externalReferenceCode, long cpOptionId, long companyId,
			long userId)
		throws PortalException {

		return getService().getOrAddEmptyCPOptionValue(
			externalReferenceCode, cpOptionId, companyId, userId);
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

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPOptionValue> searchCPOptionValues(
				long companyId, long cpOptionId, String keywords, int start,
				int end, com.liferay.portal.kernel.search.Sort[] sorts)
			throws PortalException {

		return getService().searchCPOptionValues(
			companyId, cpOptionId, keywords, start, end, sorts);
	}

	public static int searchCPOptionValuesCount(
			long companyId, long cpOptionId, String keywords)
		throws PortalException {

		return getService().searchCPOptionValuesCount(
			companyId, cpOptionId, keywords);
	}

	/**
	 * Updates the cp option value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was updated
	 */
	public static CPOptionValue updateCPOptionValue(
		CPOptionValue cpOptionValue) {

		return getService().updateCPOptionValue(cpOptionValue);
	}

	public static CPOptionValue updateCPOptionValue(
			long cpOptionValueId, Map<java.util.Locale, String> nameMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPOptionValue(
			cpOptionValueId, nameMap, priority, key, serviceContext);
	}

	public static CPOptionValueLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPOptionValueLocalService> _serviceSnapshot =
		new Snapshot<>(
			CPOptionValueLocalServiceUtil.class,
			CPOptionValueLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1863742299