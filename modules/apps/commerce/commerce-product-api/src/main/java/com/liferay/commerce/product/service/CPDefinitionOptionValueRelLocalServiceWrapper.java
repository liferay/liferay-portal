/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDefinitionOptionValueRelLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelLocalService
 * @generated
 */
public class CPDefinitionOptionValueRelLocalServiceWrapper
	implements CPDefinitionOptionValueRelLocalService,
			   ServiceWrapper<CPDefinitionOptionValueRelLocalService> {

	public CPDefinitionOptionValueRelLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionOptionValueRelLocalServiceWrapper(
		CPDefinitionOptionValueRelLocalService
			cpDefinitionOptionValueRelLocalService) {

		_cpDefinitionOptionValueRelLocalService =
			cpDefinitionOptionValueRelLocalService;
	}

	/**
	 * Adds the cp definition option value rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionValueRel the cp definition option value rel
	 * @return the cp definition option value rel that was added
	 */
	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId,
			com.liferay.commerce.product.model.CPOptionValue cpOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpOptionValue, serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean preselected, java.math.BigDecimal deltaPrice,
			double priority, java.math.BigDecimal quantity,
			String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpInstanceId, key, nameMap,
				preselected, deltaPrice, priority, quantity, unitOfMeasureKey,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			java.util.Map<java.util.Locale, String> nameMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			addCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, key, nameMap, priority,
				serviceContext);
	}

	/**
	 * Creates a new cp definition option value rel with the primary key. Does not add the cp definition option value rel to the database.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key for the new cp definition option value rel
	 * @return the new cp definition option value rel
	 */
	@Override
	public CPDefinitionOptionValueRel createCPDefinitionOptionValueRel(
		long CPDefinitionOptionValueRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			createCPDefinitionOptionValueRel(CPDefinitionOptionValueRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp definition option value rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionValueRel the cp definition option value rel
	 * @return the cp definition option value rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			deleteCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
	}

	/**
	 * Deletes the cp definition option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel that was removed
	 * @throws PortalException if a cp definition option value rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long CPDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			deleteCPDefinitionOptionValueRel(CPDefinitionOptionValueRelId);
	}

	@Override
	public void deleteCPDefinitionOptionValueRels(long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionOptionValueRelLocalService.
			deleteCPDefinitionOptionValueRels(cpDefinitionOptionRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionOptionValueRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionOptionValueRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionOptionValueRelLocalService.dynamicQuery();
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

		return _cpDefinitionOptionValueRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
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

		return _cpDefinitionOptionValueRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
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

		return _cpDefinitionOptionValueRelLocalService.dynamicQuery(
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

		return _cpDefinitionOptionValueRelLocalService.dynamicQueryCount(
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

		return _cpDefinitionOptionValueRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long CPDefinitionOptionValueRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchCPDefinitionOptionValueRel(CPDefinitionOptionValueRelId);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpInstanceId);
	}

	@Override
	public CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, String key) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchCPDefinitionOptionValueRel(cpDefinitionOptionRelId, key);
	}

	@Override
	public CPDefinitionOptionValueRel
		fetchCPDefinitionOptionValueRelByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchCPDefinitionOptionValueRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition option value rel matching the UUID and group.
	 *
	 * @param uuid the cp definition option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel
		fetchCPDefinitionOptionValueRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchCPDefinitionOptionValueRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public CPDefinitionOptionValueRel
		fetchPreselectedCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			fetchPreselectedCPDefinitionOptionValueRel(cpDefinitionOptionRelId);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		filterByCPInstanceOptionValueRels(
			java.util.List<CPDefinitionOptionValueRel>
				cpDefinitionOptionValueRels,
			java.util.List
				<com.liferay.commerce.product.model.CPInstanceOptionValueRel>
					cpInstanceOptionValueRels) {

		return _cpDefinitionOptionValueRelLocalService.
			filterByCPInstanceOptionValueRels(
				cpDefinitionOptionValueRels, cpInstanceOptionValueRels);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionOptionValueRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getApprovedCPInstanceCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			getApprovedCPInstanceCPDefinitionOptionValueRels(
				cpDefinitionOptionRelId);
	}

	/**
	 * Returns the cp definition option value rel with the primary key.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel
	 * @throws PortalException if a cp definition option value rel with the primary key could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			long CPDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRel(CPDefinitionOptionValueRelId);
	}

	@Override
	public CPDefinitionOptionValueRel
			getCPDefinitionOptionValueRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition option value rel matching the UUID and group.
	 *
	 * @param uuid the cp definition option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition option value rel
	 * @throws PortalException if a matching cp definition option value rel could not be found
	 */
	@Override
	public CPDefinitionOptionValueRel
			getCPDefinitionOptionValueRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp definition option value rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of cp definition option value rels
	 */
	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(int start, int end) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(cpDefinitionOptionRelId);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(cpDefinitionOptionRelId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(
				cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(long[] cpDefinitionOptionValueRelsId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(cpDefinitionOptionValueRelsId);
	}

	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(String key, int start, int end) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRels(key, start, end);
	}

	/**
	 * Returns all the cp definition option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option value rels
	 * @param companyId the primary key of the company
	 * @return the matching cp definition option value rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp definition option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option value rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition option value rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPDefinitionOptionValueRel> orderByComparator) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition option value rels.
	 *
	 * @return the number of cp definition option value rels
	 */
	@Override
	public int getCPDefinitionOptionValueRelsCount() {
		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelsCount();
	}

	@Override
	public int getCPDefinitionOptionValueRelsCount(
		long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			getCPDefinitionOptionValueRelsCount(cpDefinitionOptionRelId);
	}

	@Override
	public CPDefinitionOptionValueRel getCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			getCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionRelId, cpInstanceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionOptionValueRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionOptionValueRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionOptionValueRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean hasCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			hasCPDefinitionOptionValueRels(cpDefinitionOptionRelId);
	}

	@Override
	public boolean hasPreselectedCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId) {

		return _cpDefinitionOptionValueRelLocalService.
			hasPreselectedCPDefinitionOptionValueRel(cpDefinitionOptionRelId);
	}

	@Override
	public void importCPDefinitionOptionRels(
			long cpDefinitionOptionRelId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionOptionValueRelLocalService.importCPDefinitionOptionRels(
			cpDefinitionOptionRelId, serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel resetCPInstanceCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			resetCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId);
	}

	@Override
	public void resetCPInstanceCPDefinitionOptionValueRels(
			String cpInstanceUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionOptionValueRelLocalService.
			resetCPInstanceCPDefinitionOptionValueRels(cpInstanceUuid);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _cpDefinitionOptionValueRelLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionValueRel> searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			searchCPDefinitionOptionValueRels(
				companyId, groupId, cpDefinitionOptionRelId, keywords, start,
				end, sorts);
	}

	@Override
	public int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			searchCPDefinitionOptionValueRelsCount(
				companyId, groupId, cpDefinitionOptionRelId, keywords);
	}

	/**
	 * Updates the cp definition option value rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionOptionValueRel the cp definition option value rel
	 * @return the cp definition option value rel that was updated
	 */
	@Override
	public CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return _cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRel(cpDefinitionOptionValueRel);
	}

	@Override
	public CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			java.util.Map<java.util.Locale, String> nameMap,
			boolean preselected, java.math.BigDecimal price, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
				preselected, price, priority, quantity, unitOfMeasureKey,
				serviceContext);
	}

	@Override
	public CPDefinitionOptionValueRel
		updateCPDefinitionOptionValueRelPreselected(
			long cpDefinitionOptionValueRelId, boolean preselected) {

		return _cpDefinitionOptionValueRelLocalService.
			updateCPDefinitionOptionValueRelPreselected(
				cpDefinitionOptionValueRelId, preselected);
	}

	@Override
	public CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionOptionValueRelLocalService.
			updateExternalReferenceCode(
				cpDefinitionOptionValueRelId, externalReferenceCode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionOptionValueRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPDefinitionOptionValueRel> getCTPersistence() {
		return _cpDefinitionOptionValueRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CPDefinitionOptionValueRel> getModelClass() {
		return _cpDefinitionOptionValueRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDefinitionOptionValueRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpDefinitionOptionValueRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPDefinitionOptionValueRelLocalService getWrappedService() {
		return _cpDefinitionOptionValueRelLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionOptionValueRelLocalService
			cpDefinitionOptionValueRelLocalService) {

		_cpDefinitionOptionValueRelLocalService =
			cpDefinitionOptionValueRelLocalService;
	}

	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-988914469