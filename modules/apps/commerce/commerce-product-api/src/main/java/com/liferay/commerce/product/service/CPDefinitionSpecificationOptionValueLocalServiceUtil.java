/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
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
 * Provides the local service utility for CPDefinitionSpecificationOptionValue. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionSpecificationOptionValueLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDefinitionSpecificationOptionValueLocalService
 * @generated
 */
public class CPDefinitionSpecificationOptionValueLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionSpecificationOptionValueLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp definition specification option value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was added
	 */
	public static CPDefinitionSpecificationOptionValue
		addCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue) {

		return getService().addCPDefinitionSpecificationOptionValue(
			cpDefinitionSpecificationOptionValue);
	}

	public static CPDefinitionSpecificationOptionValue
			addCPDefinitionSpecificationOptionValue(
				String externalReferenceCode, long cpDefinitionId,
				long cpSpecificationOptionId, long cpOptionCategoryId,
				double priority, Map<java.util.Locale, String> valueMap,
				boolean visible,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionSpecificationOptionValue(
			externalReferenceCode, cpDefinitionId, cpSpecificationOptionId,
			cpOptionCategoryId, priority, valueMap, visible, serviceContext);
	}

	/**
	 * Creates a new cp definition specification option value with the primary key. Does not add the cp definition specification option value to the database.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key for the new cp definition specification option value
	 * @return the new cp definition specification option value
	 */
	public static CPDefinitionSpecificationOptionValue
		createCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId) {

		return getService().createCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValueId);
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
	 * Deletes the cp definition specification option value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException
	 */
	public static CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue)
		throws PortalException {

		return getService().deleteCPDefinitionSpecificationOptionValue(
			cpDefinitionSpecificationOptionValue);
	}

	public static CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				CPDefinitionSpecificationOptionValue
					cpDefinitionSpecificationOptionValue,
				boolean makeCopy)
		throws PortalException {

		return getService().deleteCPDefinitionSpecificationOptionValue(
			cpDefinitionSpecificationOptionValue, makeCopy);
	}

	/**
	 * Deletes the cp definition specification option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			deleteCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws PortalException {

		return getService().deleteCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValueId);
	}

	public static void deleteCPDefinitionSpecificationOptionValues(
			long cpDefinitionId)
		throws PortalException {

		getService().deleteCPDefinitionSpecificationOptionValues(
			cpDefinitionId);
	}

	public static void deleteCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, boolean makeCopy)
		throws PortalException {

		getService().deleteCPDefinitionSpecificationOptionValues(
			cpDefinitionId, makeCopy);
	}

	public static void deleteCPSpecificationOptionDefinitionValues(
			long cpSpecificationOptionId)
		throws PortalException {

		getService().deleteCPSpecificationOptionDefinitionValues(
			cpSpecificationOptionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
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

	public static CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long CPDefinitionSpecificationOptionValueId) {

		return getService().fetchCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValueId);
	}

	public static CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, long cpDefinitionSpecificationOptionValueId) {

		return getService().fetchCPDefinitionSpecificationOptionValue(
			cpDefinitionId, cpDefinitionSpecificationOptionValueId);
	}

	public static CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValue(
			long cpDefinitionId, String key) {

		return getService().fetchCPDefinitionSpecificationOptionValue(
			cpDefinitionId, key);
	}

	public static CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
		fetchCPDefinitionSpecificationOptionValueByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().
			fetchCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition specification option value with the primary key.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value
	 * @throws PortalException if a cp definition specification option value with the primary key could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValue(
				long CPDefinitionSpecificationOptionValueId)
		throws PortalException {

		return getService().getCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValueId);
	}

	public static CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getCPDefinitionSpecificationOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition specification option value matching the UUID and group.
	 *
	 * @param uuid the cp definition specification option value's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition specification option value
	 * @throws PortalException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			getCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().
			getCPDefinitionSpecificationOptionValueByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the cp definition specification option values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(int start, int end) {

		return getService().getCPDefinitionSpecificationOptionValues(
			start, end);
	}

	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(long cpSpecificationOptionId) {

		return getService().getCPDefinitionSpecificationOptionValues(
			cpSpecificationOptionId);
	}

	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, Boolean visible, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getService().getCPDefinitionSpecificationOptionValues(
			cpDefinitionId, visible, start, end, orderByComparator);
	}

	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpSpecificationOptionId, int start, int end) {

		return getService().getCPDefinitionSpecificationOptionValues(
			cpSpecificationOptionId, start, end);
	}

	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValues(
			long cpDefinitionId, long cpOptionCategoryId, Boolean visible) {

		return getService().getCPDefinitionSpecificationOptionValues(
			cpDefinitionId, cpOptionCategoryId, visible);
	}

	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByC_CSO(
			long cpDefinitionId, long cpSpecificationOptionId) {

		return getService().getCPDefinitionSpecificationOptionValuesByC_CSO(
			cpDefinitionId, cpSpecificationOptionId);
	}

	/**
	 * Returns all the cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @return the matching cp definition specification option values, or an empty list if no matches were found
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().
			getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of cp definition specification option values matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition specification option values
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition specification option values, or an empty list if no matches were found
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getService().
			getCPDefinitionSpecificationOptionValuesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition specification option values.
	 *
	 * @return the number of cp definition specification option values
	 */
	public static int getCPDefinitionSpecificationOptionValuesCount() {
		return getService().getCPDefinitionSpecificationOptionValuesCount();
	}

	public static int getCPDefinitionSpecificationOptionValuesCount(
		long cpDefinitionId, Boolean visible) {

		return getService().getCPDefinitionSpecificationOptionValuesCount(
			cpDefinitionId, visible);
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
	 * Updates the cp definition specification option value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionSpecificationOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionSpecificationOptionValue the cp definition specification option value
	 * @return the cp definition specification option value that was updated
	 */
	public static CPDefinitionSpecificationOptionValue
		updateCPDefinitionSpecificationOptionValue(
			CPDefinitionSpecificationOptionValue
				cpDefinitionSpecificationOptionValue) {

		return getService().updateCPDefinitionSpecificationOptionValue(
			cpDefinitionSpecificationOptionValue);
	}

	public static CPDefinitionSpecificationOptionValue
			updateCPDefinitionSpecificationOptionValue(
				String externalReferenceCode,
				long cpDefinitionSpecificationOptionValueId,
				long cpOptionCategoryId, String key, double priority,
				Map<java.util.Locale, String> valueMap, boolean visible,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionSpecificationOptionValue(
			externalReferenceCode, cpDefinitionSpecificationOptionValueId,
			cpOptionCategoryId, key, priority, valueMap, visible,
			serviceContext);
	}

	public static CPDefinitionSpecificationOptionValue updateCPOptionCategoryId(
			long cpDefinitionSpecificationOptionValueId,
			long cpOptionCategoryId)
		throws PortalException {

		return getService().updateCPOptionCategoryId(
			cpDefinitionSpecificationOptionValueId, cpOptionCategoryId);
	}

	public static CPDefinitionSpecificationOptionValueLocalService
		getService() {

		return _serviceSnapshot.get();
	}

	private static final Snapshot
		<CPDefinitionSpecificationOptionValueLocalService> _serviceSnapshot =
			new Snapshot<>(
				CPDefinitionSpecificationOptionValueLocalServiceUtil.class,
				CPDefinitionSpecificationOptionValueLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-516475299