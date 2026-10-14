/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTaxFixedRateAddressRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceTaxFixedRateAddressRelLocalService
 * @generated
 */
public class CommerceTaxFixedRateAddressRelLocalServiceWrapper
	implements CommerceTaxFixedRateAddressRelLocalService,
			   ServiceWrapper<CommerceTaxFixedRateAddressRelLocalService> {

	public CommerceTaxFixedRateAddressRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceTaxFixedRateAddressRelLocalServiceWrapper(
		CommerceTaxFixedRateAddressRelLocalService
			commerceTaxFixedRateAddressRelLocalService) {

		_commerceTaxFixedRateAddressRelLocalService =
			commerceTaxFixedRateAddressRelLocalService;
	}

	/**
	 * Adds the commerce tax fixed rate address rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was added
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
				com.liferay.commerce.tax.engine.fixed.model.
					CommerceTaxFixedRateAddressRel
						commerceTaxFixedRateAddressRel) {

		return _commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(commerceTaxFixedRateAddressRel);
	}

	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
					long userId, long groupId, long commerceTaxMethodId,
					long cpTaxCategoryId, long countryId, long regionId,
					String zip, double rate)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				userId, groupId, commerceTaxMethodId, cpTaxCategoryId,
				countryId, regionId, zip, rate);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel(
					long commerceTaxMethodId, long cpTaxCategoryId,
					long countryId, long regionId, String zip, double rate,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, cpTaxCategoryId, countryId, regionId, zip,
				rate, serviceContext);
	}

	/**
	 * Creates a new commerce tax fixed rate address rel with the primary key. Does not add the commerce tax fixed rate address rel to the database.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key for the new commerce tax fixed rate address rel
	 * @return the new commerce tax fixed rate address rel
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel createCommerceTaxFixedRateAddressRel(
				long commerceTaxFixedRateAddressRelId) {

		return _commerceTaxFixedRateAddressRelLocalService.
			createCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce tax fixed rate address rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel deleteCommerceTaxFixedRateAddressRel(
				com.liferay.commerce.tax.engine.fixed.model.
					CommerceTaxFixedRateAddressRel
						commerceTaxFixedRateAddressRel) {

		return _commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRel);
	}

	/**
	 * Deletes the commerce tax fixed rate address rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was removed
	 * @throws PortalException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel deleteCommerceTaxFixedRateAddressRel(
					long commerceTaxFixedRateAddressRelId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId);
	}

	@Override
	public void deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
		long commerceTaxMethodId) {

		_commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRelsByCommerceTaxMethodId(
				commerceTaxMethodId);
	}

	@Override
	public void deleteCommerceTaxFixedRateAddressRelsByCPTaxCategoryId(
		long cpTaxCategoryId) {

		_commerceTaxFixedRateAddressRelLocalService.
			deleteCommerceTaxFixedRateAddressRelsByCPTaxCategoryId(
				cpTaxCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTaxFixedRateAddressRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTaxFixedRateAddressRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTaxFixedRateAddressRelLocalService.dynamicQuery();
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

		return _commerceTaxFixedRateAddressRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
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

		return _commerceTaxFixedRateAddressRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
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

		return _commerceTaxFixedRateAddressRelLocalService.dynamicQuery(
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

		return _commerceTaxFixedRateAddressRelLocalService.dynamicQueryCount(
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

		return _commerceTaxFixedRateAddressRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
				long commerceTaxFixedRateAddressRelId) {

		return _commerceTaxFixedRateAddressRelLocalService.
			fetchCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId);
	}

	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
				long commerceTaxMethodId, long cpTaxCategoryId, long countryId,
				long regionId, String zip) {

		return _commerceTaxFixedRateAddressRelLocalService.
			fetchCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, cpTaxCategoryId, countryId, regionId, zip);
	}

	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel fetchCommerceTaxFixedRateAddressRel(
				long commerceTaxMethodId, long countryId, long regionId,
				String zip) {

		return _commerceTaxFixedRateAddressRelLocalService.
			fetchCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, regionId, zip);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTaxFixedRateAddressRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax fixed rate address rel with the primary key.
	 *
	 * @param commerceTaxFixedRateAddressRelId the primary key of the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel
	 * @throws PortalException if a commerce tax fixed rate address rel with the primary key could not be found
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel getCommerceTaxFixedRateAddressRel(
					long commerceTaxFixedRateAddressRelId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRel(commerceTaxFixedRateAddressRelId);
	}

	/**
	 * Returns a range of all the commerce tax fixed rate address rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.engine.fixed.model.impl.CommerceTaxFixedRateAddressRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax fixed rate address rels
	 * @param end the upper bound of the range of commerce tax fixed rate address rels (not inclusive)
	 * @return the range of commerce tax fixed rate address rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel> getCommerceTaxFixedRateAddressRels(
				int start, int end) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRels(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel> getCommerceTaxFixedRateAddressRels(
				long cpTaxCategoryId, int start, int end) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRels(cpTaxCategoryId, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel> getCommerceTaxFixedRateAddressRels(
				long cpTaxCategoryId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.tax.engine.fixed.model.
						CommerceTaxFixedRateAddressRel> orderByComparator) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRels(
				cpTaxCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tax fixed rate address rels.
	 *
	 * @return the number of commerce tax fixed rate address rels
	 */
	@Override
	public int getCommerceTaxFixedRateAddressRelsCount() {
		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRelsCount();
	}

	@Override
	public int getCommerceTaxFixedRateAddressRelsCount(long cpTaxCategoryId) {
		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxFixedRateAddressRelsCount(cpTaxCategoryId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel>
				getCommerceTaxMethodFixedRateAddressRels(
					long commerceTaxMethodId, int start, int end) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxMethodFixedRateAddressRels(
				commerceTaxMethodId, start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel>
				getCommerceTaxMethodFixedRateAddressRels(
					long commerceTaxMethodId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.tax.engine.fixed.model.
							CommerceTaxFixedRateAddressRel> orderByComparator) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxMethodFixedRateAddressRels(
				commerceTaxMethodId, start, end, orderByComparator);
	}

	@Override
	public int getCommerceTaxMethodFixedRateAddressRelsCount(
		long commerceTaxMethodId) {

		return _commerceTaxFixedRateAddressRelLocalService.
			getCommerceTaxMethodFixedRateAddressRelsCount(commerceTaxMethodId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTaxFixedRateAddressRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTaxFixedRateAddressRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce tax fixed rate address rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxFixedRateAddressRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxFixedRateAddressRel the commerce tax fixed rate address rel
	 * @return the commerce tax fixed rate address rel that was updated
	 */
	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel updateCommerceTaxFixedRateAddressRel(
				com.liferay.commerce.tax.engine.fixed.model.
					CommerceTaxFixedRateAddressRel
						commerceTaxFixedRateAddressRel) {

		return _commerceTaxFixedRateAddressRelLocalService.
			updateCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRel);
	}

	@Override
	public
		com.liferay.commerce.tax.engine.fixed.model.
			CommerceTaxFixedRateAddressRel updateCommerceTaxFixedRateAddressRel(
					long commerceTaxFixedRateAddressRelId, long countryId,
					long regionId, String zip, double rate)
				throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTaxFixedRateAddressRelLocalService.
			updateCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRelId, countryId, regionId, zip,
				rate);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTaxFixedRateAddressRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceTaxFixedRateAddressRelLocalService getWrappedService() {
		return _commerceTaxFixedRateAddressRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTaxFixedRateAddressRelLocalService
			commerceTaxFixedRateAddressRelLocalService) {

		_commerceTaxFixedRateAddressRelLocalService =
			commerceTaxFixedRateAddressRelLocalService;
	}

	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1107239931