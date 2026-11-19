/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPSpecificationOptionListTypeDefinitionRelLocalService}.
 *
 * @author Marco Leo
 * @see CPSpecificationOptionListTypeDefinitionRelLocalService
 * @generated
 */
public class CPSpecificationOptionListTypeDefinitionRelLocalServiceWrapper
	implements CPSpecificationOptionListTypeDefinitionRelLocalService,
			   ServiceWrapper
				   <CPSpecificationOptionListTypeDefinitionRelLocalService> {

	public CPSpecificationOptionListTypeDefinitionRelLocalServiceWrapper() {
		this(null);
	}

	public CPSpecificationOptionListTypeDefinitionRelLocalServiceWrapper(
		CPSpecificationOptionListTypeDefinitionRelLocalService
			cpSpecificationOptionListTypeDefinitionRelLocalService) {

		_cpSpecificationOptionListTypeDefinitionRelLocalService =
			cpSpecificationOptionListTypeDefinitionRelLocalService;
	}

	/**
	 * Adds the cp specification option list type definition rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was added
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		addCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			addCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionListTypeDefinitionRel);
	}

	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			addCPSpecificationOptionListTypeDefinitionRel(
				long cpSpecificationOptionId, long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			addCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionId, listTypeDefinitionId);
	}

	/**
	 * Creates a new cp specification option list type definition rel with the primary key. Does not add the cp specification option list type definition rel to the database.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key for the new cp specification option list type definition rel
	 * @return the new cp specification option list type definition rel
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		createCPSpecificationOptionListTypeDefinitionRel(
			long CPSpecificationOptionListTypeDefinitionRelId) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			createCPSpecificationOptionListTypeDefinitionRel(
				CPSpecificationOptionListTypeDefinitionRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cp specification option list type definition rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was removed
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		deleteCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			deleteCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionListTypeDefinitionRel);
	}

	/**
	 * Deletes the cp specification option list type definition rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was removed
	 * @throws PortalException if a cp specification option list type definition rel with the primary key could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			deleteCPSpecificationOptionListTypeDefinitionRel(
				long CPSpecificationOptionListTypeDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			deleteCPSpecificationOptionListTypeDefinitionRel(
				CPSpecificationOptionListTypeDefinitionRelId);
	}

	@Override
	public void deleteCPSpecificationOptionListTypeDefinitionRel(
			long cpSpecificationOptionId, long listTypeDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpSpecificationOptionListTypeDefinitionRelLocalService.
			deleteCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionId, listTypeDefinitionId);
	}

	@Override
	public void deleteCPSpecificationOptionListTypeDefinitionRels(
		long cpSpecificationOptionId) {

		_cpSpecificationOptionListTypeDefinitionRelLocalService.
			deleteCPSpecificationOptionListTypeDefinitionRels(
				cpSpecificationOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQuery();
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

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
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

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
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

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQuery(dynamicQuery, start, end, orderByComparator);
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

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		fetchCPSpecificationOptionListTypeDefinitionRel(
			long CPSpecificationOptionListTypeDefinitionRelId) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			fetchCPSpecificationOptionListTypeDefinitionRel(
				CPSpecificationOptionListTypeDefinitionRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the cp specification option list type definition rel with the primary key.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel
	 * @throws PortalException if a cp specification option list type definition rel with the primary key could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			getCPSpecificationOptionListTypeDefinitionRel(
				long CPSpecificationOptionListTypeDefinitionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCPSpecificationOptionListTypeDefinitionRel(
				CPSpecificationOptionListTypeDefinitionRelId);
	}

	/**
	 * Returns a range of all the cp specification option list type definition rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp specification option list type definition rels
	 * @param end the upper bound of the range of cp specification option list type definition rels (not inclusive)
	 * @return the range of cp specification option list type definition rels
	 */
	@Override
	public java.util.List<CPSpecificationOptionListTypeDefinitionRel>
		getCPSpecificationOptionListTypeDefinitionRels(int start, int end) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCPSpecificationOptionListTypeDefinitionRels(start, end);
	}

	@Override
	public java.util.List<CPSpecificationOptionListTypeDefinitionRel>
		getCPSpecificationOptionListTypeDefinitionRels(
			long cpSpecificationOptionId) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCPSpecificationOptionListTypeDefinitionRels(
				cpSpecificationOptionId);
	}

	/**
	 * Returns the number of cp specification option list type definition rels.
	 *
	 * @return the number of cp specification option list type definition rels
	 */
	@Override
	public int getCPSpecificationOptionListTypeDefinitionRelsCount() {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCPSpecificationOptionListTypeDefinitionRelsCount();
	}

	@Override
	public int getCPSpecificationOptionListTypeDefinitionRelsCount(
		long listTypeDefinitionId) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCPSpecificationOptionListTypeDefinitionRelsCount(
				listTypeDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasCPSpecificationOptionListTypeDefinitionRel(
		long cpSpecificationOptionId, long listTypeDefinitionId) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			hasCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionId, listTypeDefinitionId);
	}

	/**
	 * Updates the cp specification option list type definition rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPSpecificationOptionListTypeDefinitionRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpSpecificationOptionListTypeDefinitionRel the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was updated
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		updateCPSpecificationOptionListTypeDefinitionRel(
			CPSpecificationOptionListTypeDefinitionRel
				cpSpecificationOptionListTypeDefinitionRel) {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			updateCPSpecificationOptionListTypeDefinitionRel(
				cpSpecificationOptionListTypeDefinitionRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<CPSpecificationOptionListTypeDefinitionRel>
		getCTPersistence() {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getCTPersistence();
	}

	@Override
	public Class<CPSpecificationOptionListTypeDefinitionRel> getModelClass() {
		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<CPSpecificationOptionListTypeDefinitionRel>, R,
				 E> updateUnsafeFunction)
		throws E {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public CPSpecificationOptionListTypeDefinitionRelLocalService
		getWrappedService() {

		return _cpSpecificationOptionListTypeDefinitionRelLocalService;
	}

	@Override
	public void setWrappedService(
		CPSpecificationOptionListTypeDefinitionRelLocalService
			cpSpecificationOptionListTypeDefinitionRelLocalService) {

		_cpSpecificationOptionListTypeDefinitionRelLocalService =
			cpSpecificationOptionListTypeDefinitionRelLocalService;
	}

	private CPSpecificationOptionListTypeDefinitionRelLocalService
		_cpSpecificationOptionListTypeDefinitionRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1173324178