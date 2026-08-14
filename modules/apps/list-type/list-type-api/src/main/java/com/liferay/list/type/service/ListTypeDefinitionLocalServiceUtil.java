/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service;

import com.liferay.list.type.model.ListTypeDefinition;
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
 * Provides the local service utility for ListTypeDefinition. This utility wraps
 * <code>com.liferay.list.type.service.impl.ListTypeDefinitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Gabriel Albuquerque
 * @see ListTypeDefinitionLocalService
 * @generated
 */
public class ListTypeDefinitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.list.type.service.impl.ListTypeDefinitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the list type definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeDefinition the list type definition
	 * @return the list type definition that was added
	 */
	public static ListTypeDefinition addListTypeDefinition(
		ListTypeDefinition listTypeDefinition) {

		return getService().addListTypeDefinition(listTypeDefinition);
	}

	public static ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, long userId, boolean system)
		throws PortalException {

		return getService().addListTypeDefinition(
			externalReferenceCode, userId, system);
	}

	public static ListTypeDefinition addListTypeDefinition(
			String externalReferenceCode, long userId,
			Map<java.util.Locale, String> nameMap, boolean system,
			List<com.liferay.list.type.model.ListTypeEntry> listTypeEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addListTypeDefinition(
			externalReferenceCode, userId, nameMap, system, listTypeEntries,
			serviceContext);
	}

	/**
	 * Creates a new list type definition with the primary key. Does not add the list type definition to the database.
	 *
	 * @param listTypeDefinitionId the primary key for the new list type definition
	 * @return the new list type definition
	 */
	public static ListTypeDefinition createListTypeDefinition(
		long listTypeDefinitionId) {

		return getService().createListTypeDefinition(listTypeDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCompanyListTypeDefinitions(long companyId)
		throws PortalException {

		getService().deleteCompanyListTypeDefinitions(companyId);
	}

	/**
	 * Deletes the list type definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeDefinition the list type definition
	 * @return the list type definition that was removed
	 * @throws PortalException
	 */
	public static ListTypeDefinition deleteListTypeDefinition(
			ListTypeDefinition listTypeDefinition)
		throws PortalException {

		return getService().deleteListTypeDefinition(listTypeDefinition);
	}

	/**
	 * Deletes the list type definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeDefinitionId the primary key of the list type definition
	 * @return the list type definition that was removed
	 * @throws PortalException if a list type definition with the primary key could not be found
	 */
	public static ListTypeDefinition deleteListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		return getService().deleteListTypeDefinition(listTypeDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeDefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeDefinitionModelImpl</code>.
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

	public static ListTypeDefinition fetchListTypeDefinition(
		long listTypeDefinitionId) {

		return getService().fetchListTypeDefinition(listTypeDefinitionId);
	}

	public static ListTypeDefinition
		fetchListTypeDefinitionByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchListTypeDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the list type definition with the matching UUID and company.
	 *
	 * @param uuid the list type definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type definition, or <code>null</code> if a matching list type definition could not be found
	 */
	public static ListTypeDefinition fetchListTypeDefinitionByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchListTypeDefinitionByUuidAndCompanyId(
			uuid, companyId);
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

	/**
	 * Returns the list type definition with the primary key.
	 *
	 * @param listTypeDefinitionId the primary key of the list type definition
	 * @return the list type definition
	 * @throws PortalException if a list type definition with the primary key could not be found
	 */
	public static ListTypeDefinition getListTypeDefinition(
			long listTypeDefinitionId)
		throws PortalException {

		return getService().getListTypeDefinition(listTypeDefinitionId);
	}

	public static ListTypeDefinition
			getListTypeDefinitionByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getListTypeDefinitionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the list type definition with the matching UUID and company.
	 *
	 * @param uuid the list type definition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching list type definition
	 * @throws PortalException if a matching list type definition could not be found
	 */
	public static ListTypeDefinition getListTypeDefinitionByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getListTypeDefinitionByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the list type definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.list.type.model.impl.ListTypeDefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of list type definitions
	 * @param end the upper bound of the range of list type definitions (not inclusive)
	 * @return the range of list type definitions
	 */
	public static List<ListTypeDefinition> getListTypeDefinitions(
		int start, int end) {

		return getService().getListTypeDefinitions(start, end);
	}

	/**
	 * Returns the number of list type definitions.
	 *
	 * @return the number of list type definitions
	 */
	public static int getListTypeDefinitionsCount() {
		return getService().getListTypeDefinitionsCount();
	}

	public static ListTypeDefinition getOrAddEmptyListTypeDefinition(
			String externalReferenceCode, long companyId, long userId,
			boolean system)
		throws PortalException {

		return getService().getOrAddEmptyListTypeDefinition(
			externalReferenceCode, companyId, userId, system);
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
	 * Updates the list type definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ListTypeDefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param listTypeDefinition the list type definition
	 * @return the list type definition that was updated
	 */
	public static ListTypeDefinition updateListTypeDefinition(
		ListTypeDefinition listTypeDefinition) {

		return getService().updateListTypeDefinition(listTypeDefinition);
	}

	public static ListTypeDefinition updateListTypeDefinition(
			String externalReferenceCode, long listTypeDefinitionId,
			long userId, Map<java.util.Locale, String> nameMap,
			List<com.liferay.list.type.model.ListTypeEntry> listTypeEntries,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateListTypeDefinition(
			externalReferenceCode, listTypeDefinitionId, userId, nameMap,
			listTypeEntries, serviceContext);
	}

	public static void updateUserId(
			long companyId, long oldUserId, long newUserId)
		throws PortalException {

		getService().updateUserId(companyId, oldUserId, newUserId);
	}

	public static ListTypeDefinitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ListTypeDefinitionLocalService>
		_serviceSnapshot = new Snapshot<>(
			ListTypeDefinitionLocalServiceUtil.class,
			ListTypeDefinitionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1354570347