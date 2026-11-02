/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
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
 * Provides the local service utility for CPInstanceOptionValueRel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPInstanceOptionValueRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPInstanceOptionValueRelLocalService
 * @generated
 */
public class CPInstanceOptionValueRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPInstanceOptionValueRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPInstanceOptionValueRel addCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return getService().addCPInstanceOptionValueRel(
			cpInstanceOptionValueRel);
	}

	public static CPInstanceOptionValueRel addCPInstanceOptionValueRel(
			long groupId, long companyId, long userId,
			long cpDefinitionOptionRelId, long cpDefinitionOptionValueRelId,
			long cpInstanceId)
		throws PortalException {

		return getService().addCPInstanceOptionValueRel(
			groupId, companyId, userId, cpDefinitionOptionRelId,
			cpDefinitionOptionValueRelId, cpInstanceId);
	}

	/**
	 * Creates a new cp instance option value rel with the primary key. Does not add the cp instance option value rel to the database.
	 *
	 * @param CPInstanceOptionValueRelId the primary key for the new cp instance option value rel
	 * @return the new cp instance option value rel
	 */
	public static CPInstanceOptionValueRel createCPInstanceOptionValueRel(
		long CPInstanceOptionValueRelId) {

		return getService().createCPInstanceOptionValueRel(
			CPInstanceOptionValueRelId);
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
	 * Deletes the cp instance option value rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceOptionValueRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstanceOptionValueRel the cp instance option value rel
	 * @return the cp instance option value rel that was removed
	 */
	public static CPInstanceOptionValueRel deleteCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return getService().deleteCPInstanceOptionValueRel(
			cpInstanceOptionValueRel);
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
	public static CPInstanceOptionValueRel deleteCPInstanceOptionValueRel(
			long CPInstanceOptionValueRelId)
		throws PortalException {

		return getService().deleteCPInstanceOptionValueRel(
			CPInstanceOptionValueRelId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceOptionValueRelModelImpl</code>.
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

	public static CPInstanceOptionValueRel fetchCPInstanceOptionValueRel(
		long CPInstanceOptionValueRelId) {

		return getService().fetchCPInstanceOptionValueRel(
			CPInstanceOptionValueRelId);
	}

	/**
	 * Returns the cp instance option value rel matching the UUID and group.
	 *
	 * @param uuid the cp instance option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance option value rel, or <code>null</code> if a matching cp instance option value rel could not be found
	 */
	public static CPInstanceOptionValueRel
		fetchCPInstanceOptionValueRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCPInstanceOptionValueRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CPInstanceOptionValueRel>
		getCPDefinitionCPInstanceOptionValueRels(long cpDefinitionId) {

		return getService().getCPDefinitionCPInstanceOptionValueRels(
			cpDefinitionId);
	}

	public static List<CPInstanceOptionValueRel>
		getCPDefinitionOptionRelCPInstanceOptionValueRels(
			long cpDefinitionOptionRelId) {

		return getService().getCPDefinitionOptionRelCPInstanceOptionValueRels(
			cpDefinitionOptionRelId);
	}

	public static List<CPInstanceOptionValueRel>
		getCPInstanceCPInstanceOptionValueRels(long cpInstanceId) {

		return getService().getCPInstanceCPInstanceOptionValueRels(
			cpInstanceId);
	}

	/**
	 * Returns the cp instance option value rel with the primary key.
	 *
	 * @param CPInstanceOptionValueRelId the primary key of the cp instance option value rel
	 * @return the cp instance option value rel
	 * @throws PortalException if a cp instance option value rel with the primary key could not be found
	 */
	public static CPInstanceOptionValueRel getCPInstanceOptionValueRel(
			long CPInstanceOptionValueRelId)
		throws PortalException {

		return getService().getCPInstanceOptionValueRel(
			CPInstanceOptionValueRelId);
	}

	/**
	 * Returns the cp instance option value rel matching the UUID and group.
	 *
	 * @param uuid the cp instance option value rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance option value rel
	 * @throws PortalException if a matching cp instance option value rel could not be found
	 */
	public static CPInstanceOptionValueRel
			getCPInstanceOptionValueRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCPInstanceOptionValueRelByUuidAndGroupId(
			uuid, groupId);
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
	public static List<CPInstanceOptionValueRel> getCPInstanceOptionValueRels(
		int start, int end) {

		return getService().getCPInstanceOptionValueRels(start, end);
	}

	/**
	 * Returns all the cp instance option value rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instance option value rels
	 * @param companyId the primary key of the company
	 * @return the matching cp instance option value rels, or an empty list if no matches were found
	 */
	public static List<CPInstanceOptionValueRel>
		getCPInstanceOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPInstanceOptionValueRelsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPInstanceOptionValueRel>
		getCPInstanceOptionValueRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPInstanceOptionValueRel> orderByComparator) {

		return getService().getCPInstanceOptionValueRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp instance option value rels.
	 *
	 * @return the number of cp instance option value rels
	 */
	public static int getCPInstanceOptionValueRelsCount() {
		return getService().getCPInstanceOptionValueRelsCount();
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

	public static boolean hasCPInstanceCPDefinitionOptionRel(
		long cpDefinitionOptionRelId, long cpInstanceId) {

		return getService().hasCPInstanceCPDefinitionOptionRel(
			cpDefinitionOptionRelId, cpInstanceId);
	}

	public static boolean hasCPInstanceCPDefinitionOptionValueRel(
		long cpDefinitionOptionValueRelId, long cpInstanceId) {

		return getService().hasCPInstanceCPDefinitionOptionValueRel(
			cpDefinitionOptionValueRelId, cpInstanceId);
	}

	public static boolean hasCPInstanceOptionValueRel(long cpInstanceId) {
		return getService().hasCPInstanceOptionValueRel(cpInstanceId);
	}

	public static boolean matchesCPDefinitionOptionRels(
		long cpDefinitionId, long cpInstanceId) {

		return getService().matchesCPDefinitionOptionRels(
			cpDefinitionId, cpInstanceId);
	}

	public static boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels) {

		return getService().matchesCPInstanceOptionValueRels(
			cpInstanceId, cpInstanceOptionValueRels);
	}

	public static boolean matchesCPInstanceOptionValueRels(
		long cpInstanceId,
		Map<Long, List<Long>>
			cpDefinitionOptionRelIdsCPDefinitionOptionValueRelIds) {

		return getService().matchesCPInstanceOptionValueRels(
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
	public static CPInstanceOptionValueRel updateCPInstanceOptionValueRel(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		return getService().updateCPInstanceOptionValueRel(
			cpInstanceOptionValueRel);
	}

	public static void updateCPInstanceOptionValueRels(
			long groupId, long companyId, long userId, long cpInstanceId,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds)
		throws PortalException {

		getService().updateCPInstanceOptionValueRels(
			groupId, companyId, userId, cpInstanceId,
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds);
	}

	public static CPInstanceOptionValueRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPInstanceOptionValueRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPInstanceOptionValueRelLocalServiceUtil.class,
			CPInstanceOptionValueRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:88075732