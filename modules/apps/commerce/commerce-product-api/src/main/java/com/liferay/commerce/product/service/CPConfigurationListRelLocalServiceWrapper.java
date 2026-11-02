/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPConfigurationListRelLocalService}.
 *
 * @author Marco Leo
 * @see CPConfigurationListRelLocalService
 * @generated
 */
public class CPConfigurationListRelLocalServiceWrapper
	implements CPConfigurationListRelLocalService,
			   ServiceWrapper<CPConfigurationListRelLocalService> {

	public CPConfigurationListRelLocalServiceWrapper() {
		this(null);
	}

	public CPConfigurationListRelLocalServiceWrapper(
		CPConfigurationListRelLocalService cpConfigurationListRelLocalService) {

		_cpConfigurationListRelLocalService =
			cpConfigurationListRelLocalService;
	}

	/**
	 * Adds the cp configuration list rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was added
	 */
	@Override
	public CPConfigurationListRel addCPConfigurationListRel(
		CPConfigurationListRel cpConfigurationListRel) {

		return _cpConfigurationListRelLocalService.addCPConfigurationListRel(
			cpConfigurationListRel);
	}

	@Override
	public CPConfigurationListRel addCPConfigurationListRel(
			long userId, String className, long classPK,
			long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.addCPConfigurationListRel(
			userId, className, classPK, cpConfigurationListId);
	}

	/**
	 * Creates a new cp configuration list rel with the primary key. Does not add the cp configuration list rel to the database.
	 *
	 * @param CPConfigurationListRelId the primary key for the new cp configuration list rel
	 * @return the new cp configuration list rel
	 */
	@Override
	public CPConfigurationListRel createCPConfigurationListRel(
		long CPConfigurationListRelId) {

		return _cpConfigurationListRelLocalService.createCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp configuration list rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was removed
	 * @throws PortalException
	 */
	@Override
	public CPConfigurationListRel deleteCPConfigurationListRel(
			CPConfigurationListRel cpConfigurationListRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
			cpConfigurationListRel);
	}

	/**
	 * Deletes the cp configuration list rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel that was removed
	 * @throws PortalException if a cp configuration list rel with the primary key could not be found
	 */
	@Override
	public CPConfigurationListRel deleteCPConfigurationListRel(
			long CPConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.deleteCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	@Override
	public void deleteCPConfigurationListRels(long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			cpConfigurationListId);
	}

	@Override
	public void deleteCPConfigurationListRels(
			String className, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationListRelLocalService.deleteCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpConfigurationListRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpConfigurationListRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpConfigurationListRelLocalService.dynamicQuery();
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

		return _cpConfigurationListRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
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

		return _cpConfigurationListRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
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

		return _cpConfigurationListRelLocalService.dynamicQuery(
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

		return _cpConfigurationListRelLocalService.dynamicQueryCount(
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

		return _cpConfigurationListRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPConfigurationListRel fetchCPConfigurationListRel(
		long CPConfigurationListRelId) {

		return _cpConfigurationListRelLocalService.fetchCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	@Override
	public CPConfigurationListRel fetchCPConfigurationListRel(
		String className, long classPK, long cpConfigurationListId) {

		return _cpConfigurationListRelLocalService.fetchCPConfigurationListRel(
			className, classPK, cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel>
		getAccountEntryCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return _cpConfigurationListRelLocalService.
			getAccountEntryCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return _cpConfigurationListRelLocalService.
			getAccountEntryCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public java.util.List<CPConfigurationListRel>
		getAccountGroupCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return _cpConfigurationListRelLocalService.
			getAccountGroupCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return _cpConfigurationListRelLocalService.
			getAccountGroupCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpConfigurationListRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CPConfigurationListRel>
		getCommerceOrderTypeCPConfigurationListRels(
			long cpConfigurationListId, String keywords, int start, int end) {

		return _cpConfigurationListRelLocalService.
			getCommerceOrderTypeCPConfigurationListRels(
				cpConfigurationListId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCPConfigurationListRelsCount(
		long cpConfigurationListId, String keywords) {

		return _cpConfigurationListRelLocalService.
			getCommerceOrderTypeCPConfigurationListRelsCount(
				cpConfigurationListId, keywords);
	}

	/**
	 * Returns the cp configuration list rel with the primary key.
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel
	 * @throws PortalException if a cp configuration list rel with the primary key could not be found
	 */
	@Override
	public CPConfigurationListRel getCPConfigurationListRel(
			long CPConfigurationListRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRel(
			CPConfigurationListRelId);
	}

	/**
	 * Returns a range of all the cp configuration list rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration list rels
	 * @param end the upper bound of the range of cp configuration list rels (not inclusive)
	 * @return the range of cp configuration list rels
	 */
	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
		int start, int end) {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRels(
			start, end);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId) {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRels(
			cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
		long cpConfigurationListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationListRel>
			orderByComparator) {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRels(
			cpConfigurationListId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId) {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRels(
			className, cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationListRel> getCPConfigurationListRels(
		String className, long cpConfigurationListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationListRel>
			orderByComparator) {

		return _cpConfigurationListRelLocalService.getCPConfigurationListRels(
			className, cpConfigurationListId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration list rels.
	 *
	 * @return the number of cp configuration list rels
	 */
	@Override
	public int getCPConfigurationListRelsCount() {
		return _cpConfigurationListRelLocalService.
			getCPConfigurationListRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpConfigurationListRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpConfigurationListRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the cp configuration list rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationListRel the cp configuration list rel
	 * @return the cp configuration list rel that was updated
	 */
	@Override
	public CPConfigurationListRel updateCPConfigurationListRel(
		CPConfigurationListRel cpConfigurationListRel) {

		return _cpConfigurationListRelLocalService.updateCPConfigurationListRel(
			cpConfigurationListRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpConfigurationListRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPConfigurationListRel> getCTPersistence() {
		return _cpConfigurationListRelLocalService.getCTPersistence();
	}

	@Override
	public Class<CPConfigurationListRel> getModelClass() {
		return _cpConfigurationListRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPConfigurationListRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpConfigurationListRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPConfigurationListRelLocalService getWrappedService() {
		return _cpConfigurationListRelLocalService;
	}

	@Override
	public void setWrappedService(
		CPConfigurationListRelLocalService cpConfigurationListRelLocalService) {

		_cpConfigurationListRelLocalService =
			cpConfigurationListRelLocalService;
	}

	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1811420849