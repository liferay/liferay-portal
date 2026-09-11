/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Region. This utility wraps
 * <code>com.liferay.portal.service.impl.RegionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RegionLocalService
 * @generated
 */
public class RegionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.RegionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static Region addRegion(Region region) {
		return getService().addRegion(region);
	}

	public static Region addRegion(
			String externalReferenceCode, long countryId, boolean active,
			String name, double position, String regionCode,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addRegion(
			externalReferenceCode, countryId, active, name, position,
			regionCode, serviceContext);
	}

	public static com.liferay.portal.kernel.model.RegionLocalization
			addRegionLocalization(
				Region region, String languageId, String title)
		throws PortalException {

		return getService().addRegionLocalization(region, languageId, title);
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
	 * Creates a new region with the primary key. Does not add the region to the database.
	 *
	 * @param regionId the primary key for the new region
	 * @return the new region
	 */
	public static Region createRegion(long regionId) {
		return getService().createRegion(regionId);
	}

	public static void deleteCountryRegions(long countryId) {
		getService().deleteCountryRegions(countryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	public static Region deleteRegion(long regionId) throws PortalException {
		return getService().deleteRegion(regionId);
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
	public static Region deleteRegion(Region region) {
		return getService().deleteRegion(region);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RegionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RegionModelImpl</code>.
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

	public static Region fetchRegion(long regionId) {
		return getService().fetchRegion(regionId);
	}

	public static Region fetchRegion(long countryId, String regionCode) {
		return getService().fetchRegion(countryId, regionCode);
	}

	public static Region fetchRegionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchRegionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the region with the matching UUID and company.
	 *
	 * @param uuid the region's UUID
	 * @param companyId the primary key of the company
	 * @return the matching region, or <code>null</code> if a matching region could not be found
	 */
	public static Region fetchRegionByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchRegionByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.model.RegionLocalization
		fetchRegionLocalization(long regionId, String languageId) {

		return getService().fetchRegionLocalization(regionId, languageId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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

	public static Region getOrAddEmptyRegion(
			String externalReferenceCode, long companyId, long userId,
			long countryId, String regionCode, String name)
		throws PortalException {

		return getService().getOrAddEmptyRegion(
			externalReferenceCode, companyId, userId, countryId, regionCode,
			name);
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
	 * Returns the region with the primary key.
	 *
	 * @param regionId the primary key of the region
	 * @return the region
	 * @throws PortalException if a region with the primary key could not be found
	 */
	public static Region getRegion(long regionId) throws PortalException {
		return getService().getRegion(regionId);
	}

	public static Region getRegion(long countryId, String regionCode)
		throws PortalException {

		return getService().getRegion(countryId, regionCode);
	}

	public static Region getRegionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getRegionByExternalReferenceCode(
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
	public static Region getRegionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getRegionByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.model.RegionLocalization
			getRegionLocalization(long regionId, String languageId)
		throws PortalException {

		return getService().getRegionLocalization(regionId, languageId);
	}

	public static List<com.liferay.portal.kernel.model.RegionLocalization>
		getRegionLocalizations(long regionId) {

		return getService().getRegionLocalizations(regionId);
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
	public static List<Region> getRegions(int start, int end) {
		return getService().getRegions(start, end);
	}

	public static List<Region> getRegions(long countryId, boolean active)
		throws PortalException {

		return getService().getRegions(countryId, active);
	}

	public static List<Region> getRegions(
		long countryId, boolean active, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return getService().getRegions(
			countryId, active, start, end, orderByComparator);
	}

	public static List<Region> getRegions(
		long countryId, int start, int end,
		OrderByComparator<Region> orderByComparator) {

		return getService().getRegions(
			countryId, start, end, orderByComparator);
	}

	public static List<Region> getRegions(
			long companyId, String a2, boolean active)
		throws PortalException {

		return getService().getRegions(companyId, a2, active);
	}

	/**
	 * Returns the number of regions.
	 *
	 * @return the number of regions
	 */
	public static int getRegionsCount() {
		return getService().getRegionsCount();
	}

	public static int getRegionsCount(long countryId) {
		return getService().getRegionsCount(countryId);
	}

	public static int getRegionsCount(long countryId, boolean active) {
		return getService().getRegionsCount(countryId, active);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult<Region>
			searchRegions(
				long companyId, Boolean active, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, OrderByComparator<Region> orderByComparator)
		throws PortalException {

		return getService().searchRegions(
			companyId, active, keywords, params, start, end, orderByComparator);
	}

	public static Region updateActive(long regionId, boolean active)
		throws PortalException {

		return getService().updateActive(regionId, active);
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
	public static Region updateRegion(Region region) {
		return getService().updateRegion(region);
	}

	public static Region updateRegion(
			String externalReferenceCode, long regionId, boolean active,
			String name, double position, String regionCode)
		throws PortalException {

		return getService().updateRegion(
			externalReferenceCode, regionId, active, name, position,
			regionCode);
	}

	public static com.liferay.portal.kernel.model.RegionLocalization
			updateRegionLocalization(
				Region region, String languageId, String title)
		throws PortalException {

		return getService().updateRegionLocalization(region, languageId, title);
	}

	public static List<com.liferay.portal.kernel.model.RegionLocalization>
			updateRegionLocalizations(
				Region region, Map<String, String> titleMap)
		throws PortalException {

		return getService().updateRegionLocalizations(region, titleMap);
	}

	public static RegionLocalService getService() {
		return _service;
	}

	public static void setService(RegionLocalService service) {
		_service = service;
	}

	private static volatile RegionLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1916021605