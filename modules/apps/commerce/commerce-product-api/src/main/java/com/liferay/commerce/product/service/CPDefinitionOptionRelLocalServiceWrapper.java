/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDefinitionOptionRelLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelLocalService
 * @generated
 */
public class CPDefinitionOptionRelLocalServiceWrapper
	implements CPDefinitionOptionRelLocalService,
			   ServiceWrapper<CPDefinitionOptionRelLocalService> {

	public CPDefinitionOptionRelLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionOptionRelLocalServiceWrapper(
		CPDefinitionOptionRelLocalService cpDefinitionOptionRelLocalService) {

		_cpDefinitionOptionRelLocalService = cpDefinitionOptionRelLocalService;
	}

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
	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionOptionRel);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, importOptionValue, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, importOptionValue, priceType, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, boolean importOptionValue,
			String priceType, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor,
			importOptionValue, priceType, typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel addCPDefinitionOptionRel(
			long cpDefinitionId, long cpOptionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId, serviceContext);
	}

	/**
	 * Creates a new cp definition option rel with the primary key. Does not add the cp definition option rel to the database.
	 *
	 * @param CPDefinitionOptionRelId the primary key for the new cp definition option rel
	 * @return the new cp definition option rel
	 */
	@Override
	public CPDefinitionOptionRel createCPDefinitionOptionRel(
		long CPDefinitionOptionRelId) {

		return _cpDefinitionOptionRelLocalService.createCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public CPDefinitionOptionRel deleteCPDefinitionOptionRel(
			CPDefinitionOptionRel cpDefinitionOptionRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRel(
			cpDefinitionOptionRel);
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
	@Override
	public CPDefinitionOptionRel deleteCPDefinitionOptionRel(
			long CPDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
	}

	@Override
	public void deleteCPDefinitionOptionRels(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionOptionRelLocalService.deleteCPDefinitionOptionRels(
			cpDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionOptionRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionOptionRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionOptionRelLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _cpDefinitionOptionRelLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _cpDefinitionOptionRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _cpDefinitionOptionRelLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _cpDefinitionOptionRelLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _cpDefinitionOptionRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
		long CPDefinitionOptionRelId) {

		return _cpDefinitionOptionRelLocalService.fetchCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRel(
		long cpDefinitionId, long cpOptionId) {

		return _cpDefinitionOptionRelLocalService.fetchCPDefinitionOptionRel(
			cpDefinitionId, cpOptionId);
	}

	@Override
	public CPDefinitionOptionRel
		fetchCPDefinitionOptionRelByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpDefinitionOptionRelLocalService.
			fetchCPDefinitionOptionRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRelByKey(
		long cpDefinitionId, String key) {

		return _cpDefinitionOptionRelLocalService.
			fetchCPDefinitionOptionRelByKey(cpDefinitionId, key);
	}

	/**
	 * Returns the cp definition option rel matching the UUID and group.
	 *
	 * @param uuid the cp definition option rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	@Override
	public CPDefinitionOptionRel fetchCPDefinitionOptionRelByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpDefinitionOptionRelLocalService.
			fetchCPDefinitionOptionRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionOptionRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition option rel with the primary key.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel
	 * @throws PortalException if a cp definition option rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionRel getCPDefinitionOptionRel(
			long CPDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRel(
			CPDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionRel
			getCPDefinitionOptionRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelByExternalReferenceCode(
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
	@Override
	public CPDefinitionOptionRel getCPDefinitionOptionRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.Map<Long, java.util.List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, boolean skuContributorsOnly,
				com.liferay.portal.kernel.json.JSONArray skuOptionJSONArray)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, skuContributorsOnly, skuOptionJSONArray);
	}

	@Override
	public java.util.Map<Long, java.util.List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, boolean skuContributorsOnly, String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, skuContributorsOnly, json);
	}

	@Override
	public java.util.Map<Long, java.util.List<Long>>
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				long cpDefinitionId, String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelCPDefinitionOptionValueRelIds(
				cpDefinitionId, json);
	}

	@Override
	public java.util.Map<String, java.util.List<String>>
			getCPDefinitionOptionRelKeysCPDefinitionOptionValueRelKeys(
				long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
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
	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		int start, int end) {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId) {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			cpDefinitionId);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, boolean skuContributor) {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			cpDefinitionId, skuContributor);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, int start, int end) {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			cpDefinitionId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel> getCPDefinitionOptionRels(
		long cpDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionOptionRel>
			orderByComparator) {

		return _cpDefinitionOptionRelLocalService.getCPDefinitionOptionRels(
			cpDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option rels
	 * @param companyId the primary key of the company
	 * @return the matching cp definition option rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionOptionRel>
		getCPDefinitionOptionRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<CPDefinitionOptionRel>
		getCPDefinitionOptionRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionRel> orderByComparator) {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition option rels.
	 *
	 * @return the number of cp definition option rels
	 */
	@Override
	public int getCPDefinitionOptionRelsCount() {
		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelsCount();
	}

	@Override
	public int getCPDefinitionOptionRelsCount(long cpDefinitionId) {
		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelsCount(cpDefinitionId);
	}

	@Override
	public int getCPDefinitionOptionRelsCount(
		long cpDefinitionId, boolean skuContributor) {

		return _cpDefinitionOptionRelLocalService.
			getCPDefinitionOptionRelsCount(cpDefinitionId, skuContributor);
	}

	@Override
	public java.util.List<CPDefinitionOptionRel>
		getCPOptionCPDefinitionOptionRels(long cpOptionId) {

		return _cpDefinitionOptionRelLocalService.
			getCPOptionCPDefinitionOptionRels(cpOptionId);
	}

	@Override
	public int getCPOptionCPDefinitionOptionRelsCount(long cpOptionId) {
		return _cpDefinitionOptionRelLocalService.
			getCPOptionCPDefinitionOptionRelsCount(cpOptionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionOptionRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionOptionRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionOptionRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean hasCPDefinitionPriceContributorCPDefinitionOptionRels(
		long cpDefinitionId) {

		return _cpDefinitionOptionRelLocalService.
			hasCPDefinitionPriceContributorCPDefinitionOptionRels(
				cpDefinitionId);
	}

	@Override
	public boolean hasCPDefinitionRequiredCPDefinitionOptionRels(
		long cpDefinitionId) {

		return _cpDefinitionOptionRelLocalService.
			hasCPDefinitionRequiredCPDefinitionOptionRels(cpDefinitionId);
	}

	@Override
	public boolean hasLinkedCPInstanceCPDefinitionOptionRels(
		long cpDefinitionId) {

		return _cpDefinitionOptionRelLocalService.
			hasLinkedCPInstanceCPDefinitionOptionRels(cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _cpDefinitionOptionRelLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionRel> searchCPDefinitionOptionRels(
				long companyId, long groupId, long cpDefinitionId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.searchCPDefinitionOptionRels(
			companyId, groupId, cpDefinitionId, keywords, start, end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionRelsCount(
			long companyId, long groupId, long cpDefinitionId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.
			searchCPDefinitionOptionRelsCount(
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
	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return _cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRel);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, double priority, boolean facetable,
			boolean required, boolean skuContributor,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, priority, facetable, required,
			skuContributor, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateCPDefinitionOptionRel(
			long cpDefinitionOptionRelId, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, String infoItemServiceKey,
			double priority, boolean definedExternally, boolean facetable,
			boolean required, boolean skuContributor, String priceType,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.updateCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpOptionId, nameMap, descriptionMap,
			commerceOptionTypeKey, infoItemServiceKey, priority,
			definedExternally, facetable, required, skuContributor, priceType,
			typeSettings, serviceContext);
	}

	@Override
	public CPDefinitionOptionRel updateExternalReferenceCode(
			long cpDefinitionOptionRelId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionRelLocalService.updateExternalReferenceCode(
			cpDefinitionOptionRelId, externalReferenceCode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionOptionRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPDefinitionOptionRel> getCTPersistence() {
		return _cpDefinitionOptionRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CPDefinitionOptionRel> getModelClass() {
		return _cpDefinitionOptionRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDefinitionOptionRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpDefinitionOptionRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPDefinitionOptionRelLocalService getWrappedService() {
		return _cpDefinitionOptionRelLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionOptionRelLocalService cpDefinitionOptionRelLocalService) {

		_cpDefinitionOptionRelLocalService = cpDefinitionOptionRelLocalService;
	}

	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-300510455