/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
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
 * Provides the local service utility for CPDefinitionOptionRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelLocalService
 * @generated
 */
public class CPDefinitionOptionRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp definition option rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionRel the cp definition option rel
	 * @return the cp definition option rel that was added
	 */
	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return getService().addCPDefinitionOptionRel(cpDefinitionOptionRel);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, importOptionValue, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, priceType, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor,
			importOptionValue, priceType, typeSettings, serviceContext);
	}

	public static CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, serviceContext);
	}

	/**
	 * Creates a new cp definition option rel with the primary key. Does not add the cp definition option rel to the database.
	 *
	 * @param CPDefinitionOptionRelId the primary key for the new cp definition option rel
	 * @return the new cp definition option rel
	 */
	public static CPDefinitionOptionRel createCPDefinitionOptionRel(
		long CPDefinitionOptionRelId) {

		return getService().createCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
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
	 * Deletes the cp definition option rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionRel the cp definition option rel
	 * @return the cp definition option rel that was removed
	 * @throws PortalException
	 */
	public static CPDefinitionOptionRel deleteCPDefinitionOptionRel(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws PortalException {

		return getService().deleteCPDefinitionOptionRel(cpDefinitionOptionRel);
	}

	/**
	 * Deletes the cp definition option rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel that was removed
	 * @throws PortalException if a cp definition option rel with the primary key could not be found
	 */
	public static CPDefinitionOptionRel deleteCPDefinitionOptionRel(
			long CPDefinitionOptionRelId)
		throws PortalException {

		return getService().deleteCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
	}

	public static void deleteCPDefinitionOptionRels(long cpDefinitionId)
		throws PortalException {

		getService().deleteCPDefinitionOptionRels(cpDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
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

	public static CPDefinitionOptionRel fetchCPDefinitionOptionRel(
		long CPDefinitionOptionRelId) {

		return getService().fetchCPDefinitionOptionRel(CPDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel fetchCPDefinitionOptionRel(
		long cpDefinitionId, long cpOptionId) {

		return getService().fetchCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId);
	}

	public static CPDefinitionOptionRel
		fetchCPDefinitionOptionRelByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCPDefinitionOptionRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CPDefinitionOptionRel fetchCPDefinitionOptionRelByKey(
		long cpDefinitionId, String key) {

		return getService().fetchCPDefinitionOptionRelByKey(
			cpDefinitionId, key);
	}

	/**
	 * Returns the cp definition option rel matching the UUID and group.
	 *
	 * @param uuid the cp definition option rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel
		fetchCPDefinitionOptionRelByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchCPDefinitionOptionRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition option rel with the primary key.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel
	 * @throws PortalException if a cp definition option rel with the primary key could not be found
	 */
	public static CPDefinitionOptionRel getCPDefinitionOptionRel(
			long CPDefinitionOptionRelId)
		throws PortalException {

		return getService().getCPDefinitionOptionRel(CPDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPDefinitionOptionRelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition option rel matching the UUID and group.
	 *
	 * @param uuid the cp definition option rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition option rel
	 * @throws PortalException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel
			getCPDefinitionOptionRelByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getCPDefinitionOptionRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, boolean skuContributorsOnly,
				com.liferay.portal.kernel.json.JSONArray skuOptionJSONArray)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, skuContributorsOnly, skuOptionJSONArray);
	}

	public static Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, boolean skuContributorsOnly, String json)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, skuContributorsOnly, json);
	}

	public static Map<Long, List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, String json)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, json);
	}

	public static Map<String, List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				long cpInstanceId)
		throws PortalException {

		return getService().
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				cpInstanceId);
	}

	/**
	 * Returns a range of all the cp definition option rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		int start, int end) {

		return getService().getCPDefinitionOptionRels(start, end);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId) {

		return getService().getCPDefinitionOptionRels(cpDefinitionId);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, boolean skuContributor) {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, skuContributor);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, int start, int end) {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, start, end);
	}

	public static List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getService().getCPDefinitionOptionRels(
			cpDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option rels
	 * @param companyId the primary key of the company
	 * @return the matching cp definition option rels, or an empty list if no matches were found
	 */
	public static List<CPDefinitionOptionRel>
		getCPDefinitionOptionRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPDefinitionOptionRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of cp definition option rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition option rels, or an empty list if no matches were found
	 */
	public static List<CPDefinitionOptionRel>
		getCPDefinitionOptionRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getService().getCPDefinitionOptionRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition option rels.
	 *
	 * @return the number of cp definition option rels
	 */
	public static int getCPDefinitionOptionRelsCount() {
		return getService().getCPDefinitionOptionRelsCount();
	}

	public static int getCPDefinitionOptionRelsCount(long cpDefinitionId) {
		return getService().getCPDefinitionOptionRelsCount(cpDefinitionId);
	}

	public static int getCPDefinitionOptionRelsCount(
		long cpDefinitionId, boolean skuContributor) {

		return getService().getCPDefinitionOptionRelsCount(
			cpDefinitionId, skuContributor);
	}

	public static List<CPDefinitionOptionRel> getCPOptionCPDefinitionOptionRels(
		long cpOptionId) {

		return getService().getCPOptionCPDefinitionOptionRels(cpOptionId);
	}

	public static int getCPOptionCPDefinitionOptionRelsCount(long cpOptionId) {
		return getService().getCPOptionCPDefinitionOptionRelsCount(cpOptionId);
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

	public static boolean hasCPDefinitionPriceContributorCPDefinitionOptionRels(
		long cpDefinitionId) {

		return getService().
			hasCPDefinitionPriceContributorCPDefinitionOptionRels(
				cpDefinitionId);
	}

	public static boolean hasCPDefinitionRequiredCPDefinitionOptionRels(
		long cpDefinitionId) {

		return getService().hasCPDefinitionRequiredCPDefinitionOptionRels(
			cpDefinitionId);
	}

	public static boolean hasLinkedCPInstanceCPDefinitionOptionRels(
		long cpDefinitionId) {

		return getService().hasLinkedCPInstanceCPDefinitionOptionRels(
			cpDefinitionId);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionRel> searchCPDefinitionOptionRels(
				long companyId, long groupId, long cpDefinitionId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws PortalException {

		return getService().searchCPDefinitionOptionRels(
			companyId, groupId, cpDefinitionId, keywords, start, end, sorts);
	}

	public static int searchCPDefinitionOptionRelsCount(
			long companyId, long groupId, long cpDefinitionId, String keywords)
		throws PortalException {

		return getService().searchCPDefinitionOptionRelsCount(
			companyId, groupId, cpDefinitionId, keywords);
	}

	/**
	 * Updates the cp definition option rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionRel the cp definition option rel
	 * @return the cp definition option rel that was updated
	 */
	public static CPDefinitionOptionRel updateCPDefinitionOptionRel(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return getService().updateCPDefinitionOptionRel(cpDefinitionOptionRel);
	}

	public static CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, serviceContext);
	}

	public static CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, String priceType,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor, priceType,
			typeSettings, serviceContext);
	}

	public static CPDefinitionOptionRel updateExternalReferenceCode(
			long cpDefinitionOptionRelId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpDefinitionOptionRelId, externalReferenceCode);
	}

	public static CPDefinitionOptionRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionOptionRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionOptionRelLocalServiceUtil.class,
			CPDefinitionOptionRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-563344680