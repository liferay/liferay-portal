/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service;

import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceTaxCategoryMapping. This utility wraps
 * <code>com.liferay.commerce.tax.service.impl.CommerceTaxCategoryMappingLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceTaxCategoryMappingLocalService
 * @generated
 */
public class CommerceTaxCategoryMappingLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.tax.service.impl.CommerceTaxCategoryMappingLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce tax category mapping to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was added
	 */
	public static CommerceTaxCategoryMapping addCommerceTaxCategoryMapping(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		return getService().addCommerceTaxCategoryMapping(
			commerceTaxCategoryMapping);
	}

	public static CommerceTaxCategoryMapping addCommerceTaxCategoryMapping(
			String externalReferenceCode, long userId, long groupId,
			long commerceTaxMethodId, long cpTaxCategoryId)
		throws PortalException {

		return getService().addCommerceTaxCategoryMapping(
			externalReferenceCode, userId, groupId, commerceTaxMethodId,
			cpTaxCategoryId);
	}

	/**
	 * Creates a new commerce tax category mapping with the primary key. Does not add the commerce tax category mapping to the database.
	 *
	 * @param commerceTaxCategoryMappingId the primary key for the new commerce tax category mapping
	 * @return the new commerce tax category mapping
	 */
	public static CommerceTaxCategoryMapping createCommerceTaxCategoryMapping(
		long commerceTaxCategoryMappingId) {

		return getService().createCommerceTaxCategoryMapping(
			commerceTaxCategoryMappingId);
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
	 * Deletes the commerce tax category mapping from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was removed
	 */
	public static CommerceTaxCategoryMapping deleteCommerceTaxCategoryMapping(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		return getService().deleteCommerceTaxCategoryMapping(
			commerceTaxCategoryMapping);
	}

	/**
	 * Deletes the commerce tax category mapping with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping that was removed
	 * @throws PortalException if a commerce tax category mapping with the primary key could not be found
	 */
	public static CommerceTaxCategoryMapping deleteCommerceTaxCategoryMapping(
			long commerceTaxCategoryMappingId)
		throws PortalException {

		return getService().deleteCommerceTaxCategoryMapping(
			commerceTaxCategoryMappingId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
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

	public static CommerceTaxCategoryMapping fetchCommerceTaxCategoryMapping(
		long commerceTaxCategoryMappingId) {

		return getService().fetchCommerceTaxCategoryMapping(
			commerceTaxCategoryMappingId);
	}

	public static CommerceTaxCategoryMapping fetchCommerceTaxCategoryMapping(
		long commerceTaxMethodId, long cpTaxCategoryId) {

		return getService().fetchCommerceTaxCategoryMapping(
			commerceTaxMethodId, cpTaxCategoryId);
	}

	public static CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMappingByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().
			fetchCommerceTaxCategoryMappingByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tax category mapping matching the UUID and group.
	 *
	 * @param uuid the commerce tax category mapping's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce tax category mapping, or <code>null</code> if a matching commerce tax category mapping could not be found
	 */
	public static CommerceTaxCategoryMapping
		fetchCommerceTaxCategoryMappingByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCommerceTaxCategoryMappingByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce tax category mapping with the primary key.
	 *
	 * @param commerceTaxCategoryMappingId the primary key of the commerce tax category mapping
	 * @return the commerce tax category mapping
	 * @throws PortalException if a commerce tax category mapping with the primary key could not be found
	 */
	public static CommerceTaxCategoryMapping getCommerceTaxCategoryMapping(
			long commerceTaxCategoryMappingId)
		throws PortalException {

		return getService().getCommerceTaxCategoryMapping(
			commerceTaxCategoryMappingId);
	}

	public static CommerceTaxCategoryMapping
			getCommerceTaxCategoryMappingByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().
			getCommerceTaxCategoryMappingByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce tax category mapping matching the UUID and group.
	 *
	 * @param uuid the commerce tax category mapping's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce tax category mapping
	 * @throws PortalException if a matching commerce tax category mapping could not be found
	 */
	public static CommerceTaxCategoryMapping
			getCommerceTaxCategoryMappingByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceTaxCategoryMappingByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce tax category mappings.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.tax.model.impl.CommerceTaxCategoryMappingModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @return the range of commerce tax category mappings
	 */
	public static List<CommerceTaxCategoryMapping>
		getCommerceTaxCategoryMappings(int start, int end) {

		return getService().getCommerceTaxCategoryMappings(start, end);
	}

	/**
	 * Returns all the commerce tax category mappings matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce tax category mappings
	 * @param companyId the primary key of the company
	 * @return the matching commerce tax category mappings, or an empty list if no matches were found
	 */
	public static List<CommerceTaxCategoryMapping>
		getCommerceTaxCategoryMappingsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCommerceTaxCategoryMappingsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of commerce tax category mappings matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce tax category mappings
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce tax category mappings
	 * @param end the upper bound of the range of commerce tax category mappings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce tax category mappings, or an empty list if no matches were found
	 */
	public static List<CommerceTaxCategoryMapping>
		getCommerceTaxCategoryMappingsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceTaxCategoryMapping> orderByComparator) {

		return getService().getCommerceTaxCategoryMappingsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce tax category mappings.
	 *
	 * @return the number of commerce tax category mappings
	 */
	public static int getCommerceTaxCategoryMappingsCount() {
		return getService().getCommerceTaxCategoryMappingsCount();
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

	/**
	 * Updates the commerce tax category mapping in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTaxCategoryMappingLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTaxCategoryMapping the commerce tax category mapping
	 * @return the commerce tax category mapping that was updated
	 */
	public static CommerceTaxCategoryMapping updateCommerceTaxCategoryMapping(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		return getService().updateCommerceTaxCategoryMapping(
			commerceTaxCategoryMapping);
	}

	public static CommerceTaxCategoryMapping updateExternalReferenceCode(
			long commerceTaxCategoryMappingId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commerceTaxCategoryMappingId, externalReferenceCode);
	}

	public static CommerceTaxCategoryMappingLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTaxCategoryMappingLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTaxCategoryMappingLocalServiceUtil.class,
			CommerceTaxCategoryMappingLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2013817705