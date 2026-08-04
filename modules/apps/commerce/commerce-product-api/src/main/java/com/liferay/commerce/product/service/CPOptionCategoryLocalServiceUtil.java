/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionCategory;
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
 * Provides the local service utility for CPOptionCategory. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPOptionCategoryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPOptionCategoryLocalService
 * @generated
 */
public class CPOptionCategoryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPOptionCategoryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp option category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was added
	 */
	public static CPOptionCategory addCPOptionCategory(
		CPOptionCategory cpOptionCategory) {

		return getService().addCPOptionCategory(cpOptionCategory);
	}

	public static CPOptionCategory addCPOptionCategory(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPOptionCategory(
			externalReferenceCode, userId, titleMap, descriptionMap, priority,
			key, serviceContext);
	}

	public static CPOptionCategory addOrUpdateCPOptionCategory(
			String externalReferenceCode, long userId, long cpOptionCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPOptionCategory(
			externalReferenceCode, userId, cpOptionCategoryId, titleMap,
			descriptionMap, priority, key, serviceContext);
	}

	/**
	 * Creates a new cp option category with the primary key. Does not add the cp option category to the database.
	 *
	 * @param CPOptionCategoryId the primary key for the new cp option category
	 * @return the new cp option category
	 */
	public static CPOptionCategory createCPOptionCategory(
		long CPOptionCategoryId) {

		return getService().createCPOptionCategory(CPOptionCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCPOptionCategories(long companyId)
		throws PortalException {

		getService().deleteCPOptionCategories(companyId);
	}

	/**
	 * Deletes the cp option category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was removed
	 * @throws PortalException
	 */
	public static CPOptionCategory deleteCPOptionCategory(
			CPOptionCategory cpOptionCategory)
		throws PortalException {

		return getService().deleteCPOptionCategory(cpOptionCategory);
	}

	/**
	 * Deletes the cp option category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPOptionCategoryId the primary key of the cp option category
	 * @return the cp option category that was removed
	 * @throws PortalException if a cp option category with the primary key could not be found
	 */
	public static CPOptionCategory deleteCPOptionCategory(
			long CPOptionCategoryId)
		throws PortalException {

		return getService().deleteCPOptionCategory(CPOptionCategoryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
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

	public static CPOptionCategory fetchCPOptionCategory(
		long CPOptionCategoryId) {

		return getService().fetchCPOptionCategory(CPOptionCategoryId);
	}

	public static CPOptionCategory fetchCPOptionCategory(
		long companyId, String key) {

		return getService().fetchCPOptionCategory(companyId, key);
	}

	public static CPOptionCategory fetchCPOptionCategoryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCPOptionCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option category with the matching UUID and company.
	 *
	 * @param uuid the cp option category's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option category, or <code>null</code> if a matching cp option category could not be found
	 */
	public static CPOptionCategory fetchCPOptionCategoryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCPOptionCategoryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cp option categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp option categories
	 * @param end the upper bound of the range of cp option categories (not inclusive)
	 * @return the range of cp option categories
	 */
	public static List<CPOptionCategory> getCPOptionCategories(
		int start, int end) {

		return getService().getCPOptionCategories(start, end);
	}

	public static List<CPOptionCategory> getCPOptionCategories(
		long companyId, int start, int end) {

		return getService().getCPOptionCategories(companyId, start, end);
	}

	/**
	 * Returns the number of cp option categories.
	 *
	 * @return the number of cp option categories
	 */
	public static int getCPOptionCategoriesCount() {
		return getService().getCPOptionCategoriesCount();
	}

	/**
	 * Returns the cp option category with the primary key.
	 *
	 * @param CPOptionCategoryId the primary key of the cp option category
	 * @return the cp option category
	 * @throws PortalException if a cp option category with the primary key could not be found
	 */
	public static CPOptionCategory getCPOptionCategory(long CPOptionCategoryId)
		throws PortalException {

		return getService().getCPOptionCategory(CPOptionCategoryId);
	}

	public static CPOptionCategory getCPOptionCategory(
			long companyId, String key)
		throws PortalException {

		return getService().getCPOptionCategory(companyId, key);
	}

	public static CPOptionCategory getCPOptionCategoryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPOptionCategoryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option category with the matching UUID and company.
	 *
	 * @param uuid the cp option category's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option category
	 * @throws PortalException if a matching cp option category could not be found
	 */
	public static CPOptionCategory getCPOptionCategoryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCPOptionCategoryByUuidAndCompanyId(
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

	public static CPOptionCategory getOrAddEmptyCPOptionCategory(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCPOptionCategory(
			externalReferenceCode, companyId, userId);
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
		<CPOptionCategory> searchCPOptionCategories(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPOptionCategories(
			companyId, keywords, start, end, sort);
	}

	/**
	 * Updates the cp option category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionCategory the cp option category
	 * @return the cp option category that was updated
	 */
	public static CPOptionCategory updateCPOptionCategory(
		CPOptionCategory cpOptionCategory) {

		return getService().updateCPOptionCategory(cpOptionCategory);
	}

	public static CPOptionCategory updateCPOptionCategory(
			String externalReferenceCode, long cpOptionCategoryId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap, double priority,
			String key)
		throws PortalException {

		return getService().updateCPOptionCategory(
			externalReferenceCode, cpOptionCategoryId, titleMap, descriptionMap,
			priority, key);
	}

	public static CPOptionCategoryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPOptionCategoryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPOptionCategoryLocalServiceUtil.class,
			CPOptionCategoryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:771474907