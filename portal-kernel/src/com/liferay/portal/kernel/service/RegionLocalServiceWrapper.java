/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link RegionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RegionLocalService
 * @generated
 */
public class RegionLocalServiceWrapper
	implements RegionLocalService, ServiceWrapper<RegionLocalService> {

	public RegionLocalServiceWrapper() {
		this(null);
	}

	public RegionLocalServiceWrapper(RegionLocalService regionLocalService) {
		_regionLocalService = regionLocalService;
	}

	/**
	 * Adds the region to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RegionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param region the region
	 * @return the region that was added
	 */
	@Override
	public Region addRegion(Region region) {
		return _regionLocalService.addRegion(region);
	}

	@Override
	public Region addRegion(
			String externalReferenceCode, long countryId, boolean active,
			String name, double position, String regionCode,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.addRegion(
			externalReferenceCode, countryId, active, name, position,
			regionCode, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.RegionLocalization
			addRegionLocalization(
				Region region, String languageId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.addRegionLocalization(
			region, languageId, title);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new region with the primary key. Does not add the region to the database.
	 *
	 * @param regionId the primary key for the new region
	 * @return the new region
	 */
	@Override
	public Region createRegion(long regionId) {
		return _regionLocalService.createRegion(regionId);
	}

	@Override
	public void deleteCountryRegions(long countryId) {
		_regionLocalService.deleteCountryRegions(countryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the region with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RegionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param regionId the primary key of the region
	 * @return the region that was removed
	 * @throws PortalException if a region with the primary key could not be found
	 */
	@Override
	public Region deleteRegion(long regionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.deleteRegion(regionId);
	}

	/**
	 * Deletes the region from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RegionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param region the region
	 * @return the region that was removed
	 */
	@Override
	public Region deleteRegion(Region region) {
		return _regionLocalService.deleteRegion(region);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _regionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _regionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _regionLocalService.dynamicQuery();
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

		return _regionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RegionModelImpl</code>.
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

		return _regionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RegionModelImpl</code>.
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

		return _regionLocalService.dynamicQuery(
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

		return _regionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _regionLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public Region fetchRegion(long regionId) {
		return _regionLocalService.fetchRegion(regionId);
	}

	@Override
	public Region fetchRegion(long countryId, String regionCode) {
		return _regionLocalService.fetchRegion(countryId, regionCode);
	}

	@Override
	public Region fetchRegionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _regionLocalService.fetchRegionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the region with the matching UUID and company.
	 *
	 * @param uuid the region's UUID
	 * @param companyId the primary key of the company
	 * @return the matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchRegionByUuidAndCompanyId(String uuid, long companyId) {
		return _regionLocalService.fetchRegionByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.RegionLocalization
		fetchRegionLocalization(long regionId, String languageId) {

		return _regionLocalService.fetchRegionLocalization(
			regionId, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _regionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _regionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _regionLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public Region getOrAddEmptyRegion(
			String externalReferenceCode, long companyId, long userId,
			long countryId, String regionCode, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getOrAddEmptyRegion(
			externalReferenceCode, companyId, userId, countryId, regionCode,
			name);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _regionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the region with the primary key.
	 *
	 * @param regionId the primary key of the region
	 * @return the region
	 * @throws PortalException if a region with the primary key could not be found
	 */
	@Override
	public Region getRegion(long regionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegion(regionId);
	}

	@Override
	public Region getRegion(long countryId, String regionCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegion(countryId, regionCode);
	}

	@Override
	public Region getRegionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the region with the matching UUID and company.
	 *
	 * @param uuid the region's UUID
	 * @param companyId the primary key of the company
	 * @return the matching region
	 * @throws PortalException if a matching region could not be found
	 */
	@Override
	public Region getRegionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.RegionLocalization
			getRegionLocalization(long regionId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegionLocalization(regionId, languageId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.RegionLocalization>
		getRegionLocalizations(long regionId) {

		return _regionLocalService.getRegionLocalizations(regionId);
	}

	/**
	 * Returns a range of all the regions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RegionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @return the range of regions
	 */
	@Override
	public java.util.List<Region> getRegions(int start, int end) {
		return _regionLocalService.getRegions(start, end);
	}

	@Override
	public java.util.List<Region> getRegions(long countryId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegions(countryId, active);
	}

	@Override
	public java.util.List<Region> getRegions(
		long countryId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Region>
			orderByComparator) {

		return _regionLocalService.getRegions(
			countryId, active, start, end, orderByComparator);
	}

	@Override
	public java.util.List<Region> getRegions(
		long countryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Region>
			orderByComparator) {

		return _regionLocalService.getRegions(
			countryId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<Region> getRegions(
			long companyId, String a2, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.getRegions(companyId, a2, active);
	}

	/**
	 * Returns the number of regions.
	 *
	 * @return the number of regions
	 */
	@Override
	public int getRegionsCount() {
		return _regionLocalService.getRegionsCount();
	}

	@Override
	public int getRegionsCount(long countryId) {
		return _regionLocalService.getRegionsCount(countryId);
	}

	@Override
	public int getRegionsCount(long countryId, boolean active) {
		return _regionLocalService.getRegionsCount(countryId, active);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<Region>
			searchRegions(
				long companyId, Boolean active, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<Region>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.searchRegions(
			companyId, active, keywords, params, start, end, orderByComparator);
	}

	@Override
	public Region updateActive(long regionId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.updateActive(regionId, active);
	}

	/**
	 * Updates the region in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RegionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param region the region
	 * @return the region that was updated
	 */
	@Override
	public Region updateRegion(Region region) {
		return _regionLocalService.updateRegion(region);
	}

	@Override
	public Region updateRegion(
			String externalReferenceCode, long regionId, boolean active,
			String name, double position, String regionCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.updateRegion(
			externalReferenceCode, regionId, active, name, position,
			regionCode);
	}

	@Override
	public com.liferay.portal.kernel.model.RegionLocalization
			updateRegionLocalization(
				Region region, String languageId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.updateRegionLocalization(
			region, languageId, title);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.RegionLocalization>
			updateRegionLocalizations(
				Region region, java.util.Map<String, String> titleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _regionLocalService.updateRegionLocalizations(region, titleMap);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _regionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Region> getCTPersistence() {
		return _regionLocalService.getCTPersistence();
	}

	@Override
	public Class<Region> getModelClass() {
		return _regionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Region>, R, E> updateUnsafeFunction)
		throws E {

		return _regionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public RegionLocalService getWrappedService() {
		return _regionLocalService;
	}

	@Override
	public void setWrappedService(RegionLocalService regionLocalService) {
		_regionLocalService = regionLocalService;
	}

	private RegionLocalService _regionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-896386275