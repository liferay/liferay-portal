/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
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
 * Provides the local service utility for CPDefinitionOptionValueRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionValueRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelLocalService
 * @generated
 */
public class CPDefinitionOptionValueRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPDefinitionOptionValueRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel);
	}

	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId,
			com.liferay.commerce.product.model.CPOptionValue cpOptionValue,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpOptionValue, serviceContext);
	}

	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, long cpInstanceId, String key,
			Map<java.util.Locale, String> nameMap, boolean preselected,
			java.math.BigDecimal deltaPrice, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpInstanceId, key, nameMap, preselected,
			deltaPrice, priority, quantity, unitOfMeasureKey, serviceContext);
	}

	public static CPDefinitionOptionValueRel addCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId, String key,
			Map<java.util.Locale, String> nameMap, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, key, nameMap, priority, serviceContext);
	}

	/**
	 * Creates a new cp definition option value rel with the primary key. Does not add the cp definition option value rel to the database.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key for the new cp definition option value rel
	 * @return the new cp definition option value rel
	 */
	public static CPDefinitionOptionValueRel createCPDefinitionOptionValueRel(
		long CPDefinitionOptionValueRelId) {

		return getService().createCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRelId);
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
	public static CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel)
		throws PortalException {

		return getService().deleteCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel);
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
	public static CPDefinitionOptionValueRel deleteCPDefinitionOptionValueRel(
			long CPDefinitionOptionValueRelId)
		throws PortalException {

		return getService().deleteCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRelId);
	}

	public static void deleteCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId)
		throws PortalException {

		getService().deleteCPDefinitionOptionValueRels(cpDefinitionOptionRelId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
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

	public static CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long CPDefinitionOptionValueRelId) {

		return getService().fetchCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRelId);
	}

	public static CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		return getService().fetchCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpInstanceId);
	}

	public static CPDefinitionOptionValueRel fetchCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId, String key) {

		return getService().fetchCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, key);
	}

	public static CPDefinitionOptionValueRel
		fetchCPDefinitionOptionValueRelByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
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
	public static CPDefinitionOptionValueRel
		fetchCPDefinitionOptionValueRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCPDefinitionOptionValueRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static CPDefinitionOptionValueRel
		fetchPreselectedCPDefinitionOptionValueRel(
			long cpDefinitionOptionRelId) {

		return getService().fetchPreselectedCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId);
	}

	public static List<CPDefinitionOptionValueRel>
		filterByCPInstanceOptionValueRels(
			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels,
			List<com.liferay.commerce.product.model.CPInstanceOptionValueRel>
				cpInstanceOptionValueRels) {

		return getService().filterByCPInstanceOptionValueRels(
			cpDefinitionOptionValueRels, cpInstanceOptionValueRels);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CPDefinitionOptionValueRel>
		getApprovedCPInstanceCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId) {

		return getService().getApprovedCPInstanceCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId);
	}

	/**
	 * Returns the cp definition option value rel with the primary key.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel
	 * @throws PortalException if a cp definition option value rel with the primary key could not be found
	 */
	public static CPDefinitionOptionValueRel getCPDefinitionOptionValueRel(
			long CPDefinitionOptionValueRelId)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRel(
			CPDefinitionOptionValueRelId);
	}

	public static CPDefinitionOptionValueRel
			getCPDefinitionOptionValueRelByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
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
	public static CPDefinitionOptionValueRel
			getCPDefinitionOptionValueRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRelByUuidAndGroupId(
			uuid, groupId);
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
	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(int start, int end) {

		return getService().getCPDefinitionOptionValueRels(start, end);
	}

	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(long cpDefinitionOptionRelId) {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId);
	}

	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end) {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId, start, end);
	}

	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(
			long cpDefinitionOptionRelId, int start, int end,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId, start, end, orderByComparator);
	}

	public static List<CPDefinitionOptionValueRel>
			getCPDefinitionOptionValueRels(long[] cpDefinitionOptionValueRelsId)
		throws PortalException {

		return getService().getCPDefinitionOptionValueRels(
			cpDefinitionOptionValueRelsId);
	}

	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRels(String key, int start, int end) {

		return getService().getCPDefinitionOptionValueRels(key, start, end);
	}

	/**
	 * Returns all the cp definition option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition option value rels
	 * @param companyId the primary key of the company
	 * @return the matching cp definition option value rels, or an empty list if no matches were found
	 */
	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPDefinitionOptionValueRel>
		getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getService().getCPDefinitionOptionValueRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition option value rels.
	 *
	 * @return the number of cp definition option value rels
	 */
	public static int getCPDefinitionOptionValueRelsCount() {
		return getService().getCPDefinitionOptionValueRelsCount();
	}

	public static int getCPDefinitionOptionValueRelsCount(
		long cpDefinitionOptionRelId) {

		return getService().getCPDefinitionOptionValueRelsCount(
			cpDefinitionOptionRelId);
	}

	public static CPDefinitionOptionValueRel
			getCPInstanceCPDefinitionOptionValueRel(
				long cpDefinitionOptionRelId, long cpInstanceId)
		throws PortalException {

		return getService().getCPInstanceCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId, cpInstanceId);
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

	public static boolean hasCPDefinitionOptionValueRels(
		long cpDefinitionOptionRelId) {

		return getService().hasCPDefinitionOptionValueRels(
			cpDefinitionOptionRelId);
	}

	public static boolean hasPreselectedCPDefinitionOptionValueRel(
		long cpDefinitionOptionRelId) {

		return getService().hasPreselectedCPDefinitionOptionValueRel(
			cpDefinitionOptionRelId);
	}

	public static void importCPDefinitionOptionRels(
			long cpDefinitionOptionRelId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().importCPDefinitionOptionRels(
			cpDefinitionOptionRelId, serviceContext);
	}

	public static CPDefinitionOptionValueRel
			resetCPInstanceCPDefinitionOptionValueRel(
				long cpDefinitionOptionValueRelId)
		throws PortalException {

		return getService().resetCPInstanceCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId);
	}

	public static void resetCPInstanceCPDefinitionOptionValueRels(
			String cpInstanceUuid)
		throws PortalException {

		getService().resetCPInstanceCPDefinitionOptionValueRels(cpInstanceUuid);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPDefinitionOptionValueRel> searchCPDefinitionOptionValueRels(
				long companyId, long groupId, long cpDefinitionOptionRelId,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
			throws PortalException {

		return getService().searchCPDefinitionOptionValueRels(
			companyId, groupId, cpDefinitionOptionRelId, keywords, start, end,
			sorts);
	}

	public static int searchCPDefinitionOptionValueRelsCount(
			long companyId, long groupId, long cpDefinitionOptionRelId,
			String keywords)
		throws PortalException {

		return getService().searchCPDefinitionOptionValueRelsCount(
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
	public static CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return getService().updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRel);
	}

	public static CPDefinitionOptionValueRel updateCPDefinitionOptionValueRel(
			long cpDefinitionOptionValueRelId, long cpInstanceId, String key,
			Map<java.util.Locale, String> nameMap, boolean preselected,
			java.math.BigDecimal price, double priority,
			java.math.BigDecimal quantity, String unitOfMeasureKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId, cpInstanceId, key, nameMap,
			preselected, price, priority, quantity, unitOfMeasureKey,
			serviceContext);
	}

	public static CPDefinitionOptionValueRel
		updateCPDefinitionOptionValueRelPreselected(
			long cpDefinitionOptionValueRelId, boolean preselected) {

		return getService().updateCPDefinitionOptionValueRelPreselected(
			cpDefinitionOptionValueRelId, preselected);
	}

	public static CPDefinitionOptionValueRel updateExternalReferenceCode(
			long cpDefinitionOptionValueRelId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpDefinitionOptionValueRelId, externalReferenceCode);
	}

	public static CPDefinitionOptionValueRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPDefinitionOptionValueRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPDefinitionOptionValueRelLocalServiceUtil.class,
			CPDefinitionOptionValueRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1697854218