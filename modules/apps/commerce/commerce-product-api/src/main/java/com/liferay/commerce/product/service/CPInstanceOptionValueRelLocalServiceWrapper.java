/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPInstanceOptionValueRelLocalService}.
 *
 * @author Marco Leo
 * @see CPInstanceOptionValueRelLocalService
 * @generated
 */
public class CPInstanceOptionValueRelLocalServiceWrapper
	implements CPInstanceOptionValueRelLocalService,
			   ServiceWrapper<CPInstanceOptionValueRelLocalService> {

	public CPInstanceOptionValueRelLocalServiceWrapper() {
		this(null);
	}

	public CPInstanceOptionValueRelLocalServiceWrapper(
		CPInstanceOptionValueRelLocalService
			cpInstanceOptionValueRelLocalService) {

		_cpInstanceOptionValueRelLocalService =
			cpInstanceOptionValueRelLocalService;
	}

	/**
	 * Adds the cp instance option value rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstanceOptionValueRel the cp instance option value rel
	 * @return the cp instance option value rel that was added
	 */
	@Override
	public CPInstanceOptionValueRel addCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return _cpInstanceOptionValueRelLocalService.
			addCPInstanceOptionValueRel(cpInstanceOptionValueRel);
	}

	@Override
	public CPInstanceOptionValueRel addCPInstanceOptionValueRel(
			long groupId, long companyId, long userId,
			long cpDefinitionOptionRelId, long cpDefinitionOptionValueRelId,
			long cpInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.
			addCPInstanceOptionValueRel(
				groupId, companyId, userId, cpDefinitionOptionRelId,
				cpDefinitionOptionValueRelId, cpInstanceId);
	}

	/**
	 * Creates a new cp instance option value rel with the primary key. Does not add the cp instance option value rel to the database.
	 *
	 * @param CPInstanceOptionValueRelId the primary key for the new cp instance option value rel
	 * @return the new cp instance option value rel
	 */
	@Override
	public CPInstanceOptionValueRel createCPInstanceOptionValueRel(
		long CPInstanceOptionValueRelId) {

		return _cpInstanceOptionValueRelLocalService.
			createCPInstanceOptionValueRel(CPInstanceOptionValueRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp instance option value rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstanceOptionValueRel the cp instance option value rel
	 * @return the cp instance option value rel that was removed
	 */
	@Override
	public CPInstanceOptionValueRel deleteCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return _cpInstanceOptionValueRelLocalService.
			deleteCPInstanceOptionValueRel(cpInstanceOptionValueRel);
	}

	/**
	 * Deletes the cp instance option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel that was removed
	 * @throws PortalException if a cp instance option value rel with the primary key could not be found
	 */
	@Override
	public CPInstanceOptionValueRel deleteCPInstanceOptionValueRel(
			long CPInstanceOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.
			deleteCPInstanceOptionValueRel(CPInstanceOptionValueRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpInstanceOptionValueRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpInstanceOptionValueRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpInstanceOptionValueRelLocalService.dynamicQuery();
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

		return _cpInstanceOptionValueRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl</code>.
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

		return _cpInstanceOptionValueRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl</code>.
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

		return _cpInstanceOptionValueRelLocalService.dynamicQuery(
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

		return _cpInstanceOptionValueRelLocalService.dynamicQueryCount(
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

		return _cpInstanceOptionValueRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPInstanceOptionValueRel fetchCPInstanceOptionValueRel(
		long CPInstanceOptionValueRelId) {

		return _cpInstanceOptionValueRelLocalService.
			fetchCPInstanceOptionValueRel(CPInstanceOptionValueRelId);
	}

	/**
	 * Returns the cp instance option value rel matching the UUID and group.
	 *
	 * @param uuid the cp instance option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel
		fetchCPInstanceOptionValueRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _cpInstanceOptionValueRelLocalService.
			fetchCPInstanceOptionValueRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpInstanceOptionValueRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPDefinitionCPInstanceOptionValueRels(long cpDefinitionId) {

		return _cpInstanceOptionValueRelLocalService.
			getCPDefinitionCPInstanceOptionValueRels(cpDefinitionId);
	}

	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPDefinitionOptionRelCPInstanceOptionValueRels(
			long cpDefinitionOptionRelId) {

		return _cpInstanceOptionValueRelLocalService.
			getCPDefinitionOptionRelCPInstanceOptionValueRels(
				cpDefinitionOptionRelId);
	}

	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPInstanceCPInstanceOptionValueRels(long cpInstanceId) {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceCPInstanceOptionValueRels(cpInstanceId);
	}

	/**
	 * Returns the cp instance option value rel with the primary key.
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel
	 * @throws PortalException if a cp instance option value rel with the primary key could not be found
	 */
	@Override
	public CPInstanceOptionValueRel getCPInstanceOptionValueRel(
			long CPInstanceOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRel(CPInstanceOptionValueRelId);
	}

	/**
	 * Returns the cp instance option value rel matching the UUID and group.
	 *
	 * @param uuid the cp instance option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance option value rel
	 * @throws PortalException if a matching cp instance option value rel could not be found
	 */
	@Override
	public CPInstanceOptionValueRel getCPInstanceOptionValueRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRelByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp instance option value rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @return the range of cp instance option value rels
	 */
	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPInstanceOptionValueRels(int start, int end) {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRels(start, end);
	}

	/**
	 * Returns all the cp instance option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instance option value rels
	 * @param companyId the primary key of the company
	 * @return the matching cp instance option value rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPInstanceOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRelsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp instance option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instance option value rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp instance option value rels
	 * @param end the upper bound of the range of cp instance option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp instance option value rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPInstanceOptionValueRel>
		getCPInstanceOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPInstanceOptionValueRel> orderByComparator) {

		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp instance option value rels.
	 *
	 * @return the number of cp instance option value rels
	 */
	@Override
	public int getCPInstanceOptionValueRelsCount() {
		return _cpInstanceOptionValueRelLocalService.
			getCPInstanceOptionValueRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpInstanceOptionValueRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpInstanceOptionValueRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpInstanceOptionValueRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceOptionValueRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean hasCPInstanceCPDefinitionOptionRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		return _cpInstanceOptionValueRelLocalService.
			hasCPInstanceCPDefinitionOptionRel(
				cpDefinitionOptionRelId, cpInstanceId);
	}

	@Override
	public boolean hasCPInstanceCPDefinitionOptionValueRel(
		long cpDefinitionOptionValueRelId, long cpInstanceId) {

		return _cpInstanceOptionValueRelLocalService.
			hasCPInstanceCPDefinitionOptionValueRel(
				cpDefinitionOptionValueRelId, cpInstanceId);
	}

	@Override
	public boolean hasCPInstanceOptionValueRel(long cpInstanceId) {
		return _cpInstanceOptionValueRelLocalService.
			hasCPInstanceOptionValueRel(cpInstanceId);
	}

	@Override
	public boolean matchesCPDefinitionOptionRels(
		long cpDefinitionId, long cpInstanceId) {

		return _cpInstanceOptionValueRelLocalService.
			matchesCPDefinitionOptionRels(cpDefinitionId, cpInstanceId);
	}

	@Override
	public boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		java.util.List<CPInstanceOptionValueRel> cpInstanceOptionValueRels) {

		return _cpInstanceOptionValueRelLocalService.
			matchesCPInstanceOptionValueRels(
				cpInstanceId, cpInstanceOptionValueRels);
	}

	@Override
	public boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		java.util.Map<Long, java.util.List<Long>>
			cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds) {

		return _cpInstanceOptionValueRelLocalService.
			matchesCPInstanceOptionValueRels(
				cpInstanceId,
				cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds);
	}

	/**
	 * Updates the cp instance option value rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstanceOptionValueRel the cp instance option value rel
	 * @return the cp instance option value rel that was updated
	 */
	@Override
	public CPInstanceOptionValueRel updateCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return _cpInstanceOptionValueRelLocalService.
			updateCPInstanceOptionValueRel(cpInstanceOptionValueRel);
	}

	@Override
	public void updateCPInstanceOptionValueRels(
			long groupId, long companyId, long userId, long cpInstanceId,
			java.util.Map<Long, java.util.List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceOptionValueRelLocalService.updateCPInstanceOptionValueRels(
			groupId, companyId, userId, cpInstanceId,
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpInstanceOptionValueRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPInstanceOptionValueRel> getCTPersistence() {
		return _cpInstanceOptionValueRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CPInstanceOptionValueRel> getModelClass() {
		return _cpInstanceOptionValueRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPInstanceOptionValueRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpInstanceOptionValueRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPInstanceOptionValueRelLocalService getWrappedService() {
		return _cpInstanceOptionValueRelLocalService;
	}

	@Override
	public void setWrappedService(
		CPInstanceOptionValueRelLocalService
			cpInstanceOptionValueRelLocalService) {

		_cpInstanceOptionValueRelLocalService =
			cpInstanceOptionValueRelLocalService;
	}

	private CPInstanceOptionValueRelLocalService
		_cpInstanceOptionValueRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2106294177