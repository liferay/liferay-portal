/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePricingClass;
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
 * Provides the local service utility for CommercePricingClass. This utility wraps
 * <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Riccardo Alberti
 * @see CommercePricingClassLocalService
 * @generated
 */
public class CommercePricingClassLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.pricing.service.impl.CommercePricingClassLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce pricing class to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was added
	 */
	public static CommercePricingClass addCommercePricingClass(
		CommercePricingClass commercePricingClass) {

		return getService().addCommercePricingClass(commercePricingClass);
	}

	public static CommercePricingClass addCommercePricingClass(
			long userId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePricingClass(
			userId, titleMap, descriptionMap, serviceContext);
	}

	public static CommercePricingClass addCommercePricingClass(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePricingClass(
			externalReferenceCode, userId, titleMap, descriptionMap,
			serviceContext);
	}

	public static CommercePricingClass addOrUpdateCommercePricingClass(
			String externalReferenceCode, long commercePricingClassId,
			long userId, Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePricingClass(
			externalReferenceCode, commercePricingClassId, userId, titleMap,
			descriptionMap, serviceContext);
	}

	/**
	 * Creates a new commerce pricing class with the primary key. Does not add the commerce pricing class to the database.
	 *
	 * @param commercePricingClassId the primary key for the new commerce pricing class
	 * @return the new commerce pricing class
	 */
	public static CommercePricingClass createCommercePricingClass(
		long commercePricingClassId) {

		return getService().createCommercePricingClass(commercePricingClassId);
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
	 * Deletes the commerce pricing class from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was removed
	 * @throws PortalException
	 */
	public static CommercePricingClass deleteCommercePricingClass(
			CommercePricingClass commercePricingClass)
		throws PortalException {

		return getService().deleteCommercePricingClass(commercePricingClass);
	}

	/**
	 * Deletes the commerce pricing class with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClassId the primary key of the commerce pricing class
	 * @return the commerce pricing class that was removed
	 * @throws PortalException if a commerce pricing class with the primary key could not be found
	 */
	public static CommercePricingClass deleteCommercePricingClass(
			long commercePricingClassId)
		throws PortalException {

		return getService().deleteCommercePricingClass(commercePricingClassId);
	}

	public static void deleteCommercePricingClasses(long companyId)
		throws PortalException {

		getService().deleteCommercePricingClasses(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
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

	public static CommercePricingClass fetchCommercePricingClass(
		long commercePricingClassId) {

		return getService().fetchCommercePricingClass(commercePricingClassId);
	}

	public static CommercePricingClass
		fetchCommercePricingClassByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommercePricingClassByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce pricing class with the matching UUID and company.
	 *
	 * @param uuid the commerce pricing class's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce pricing class, or <code>null</code> if a matching commerce pricing class could not be found
	 */
	public static CommercePricingClass
		fetchCommercePricingClassByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchCommercePricingClassByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce pricing class with the primary key.
	 *
	 * @param commercePricingClassId the primary key of the commerce pricing class
	 * @return the commerce pricing class
	 * @throws PortalException if a commerce pricing class with the primary key could not be found
	 */
	public static CommercePricingClass getCommercePricingClass(
			long commercePricingClassId)
		throws PortalException {

		return getService().getCommercePricingClass(commercePricingClassId);
	}

	public static long[] getCommercePricingClassByCPDefinition(
		long cpDefinitionId) {

		return getService().getCommercePricingClassByCPDefinition(
			cpDefinitionId);
	}

	public static CommercePricingClass
			getCommercePricingClassByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommercePricingClassByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce pricing class with the matching UUID and company.
	 *
	 * @param uuid the commerce pricing class's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce pricing class
	 * @throws PortalException if a matching commerce pricing class could not be found
	 */
	public static CommercePricingClass
			getCommercePricingClassByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getCommercePricingClassByUuidAndCompanyId(
			uuid, companyId);
	}

	public static int getCommercePricingClassCountByCPDefinitionId(
		long cpDefinitionId, String title) {

		return getService().getCommercePricingClassCountByCPDefinitionId(
			cpDefinitionId, title);
	}

	/**
	 * Returns a range of all the commerce pricing classes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePricingClassModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce pricing classes
	 * @param end the upper bound of the range of commerce pricing classes (not inclusive)
	 * @return the range of commerce pricing classes
	 */
	public static List<CommercePricingClass> getCommercePricingClasses(
		int start, int end) {

		return getService().getCommercePricingClasses(start, end);
	}

	public static List<CommercePricingClass> getCommercePricingClasses(
		long companyId, int start, int end,
		OrderByComparator<CommercePricingClass> orderByComparator) {

		return getService().getCommercePricingClasses(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce pricing classes.
	 *
	 * @return the number of commerce pricing classes
	 */
	public static int getCommercePricingClassesCount() {
		return getService().getCommercePricingClassesCount();
	}

	public static int getCommercePricingClassesCount(long companyId) {
		return getService().getCommercePricingClassesCount(companyId);
	}

	public static int getCommercePricingClassesCount(
		long cpDefinitionId, String title) {

		return getService().getCommercePricingClassesCount(
			cpDefinitionId, title);
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

	public static CommercePricingClass getOrAddEmptyCommercePricingClass(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommercePricingClass(
			externalReferenceCode, companyId, userId);
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

	public static List<CommercePricingClass> searchByCPDefinitionId(
		long cpDefinitionId, String title, int start, int end) {

		return getService().searchByCPDefinitionId(
			cpDefinitionId, title, start, end);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePricingClass> searchCommercePricingClasses(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommercePricingClasses(
			companyId, keywords, start, end, sort);
	}

	/**
	 * Updates the commerce pricing class in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePricingClassLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePricingClass the commerce pricing class
	 * @return the commerce pricing class that was updated
	 */
	public static CommercePricingClass updateCommercePricingClass(
		CommercePricingClass commercePricingClass) {

		return getService().updateCommercePricingClass(commercePricingClass);
	}

	public static CommercePricingClass updateCommercePricingClass(
			long commercePricingClassId, long userId,
			Map<java.util.Locale, String> titleMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommercePricingClass(
			commercePricingClassId, userId, titleMap, descriptionMap,
			serviceContext);
	}

	public static CommercePricingClass
			updateCommercePricingClassExternalReferenceCode(
				String externalReferenceCode, long commercePricingClassId)
		throws PortalException {

		return getService().updateCommercePricingClassExternalReferenceCode(
			externalReferenceCode, commercePricingClassId);
	}

	public static CommercePricingClassLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePricingClassLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePricingClassLocalServiceUtil.class,
			CommercePricingClassLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1773621989