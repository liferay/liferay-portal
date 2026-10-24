/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.model.Definition;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Definition. This utility wraps
 * <code>com.liferay.portal.reports.engine.console.service.impl.DefinitionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DefinitionLocalService
 * @generated
 */
public class DefinitionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.reports.engine.console.service.impl.DefinitionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the definition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was added
	 */
	public static Definition addDefinition(Definition definition) {
		return getService().addDefinition(definition);
	}

	public static Definition addDefinition(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDefinition(
			userId, groupId, nameMap, descriptionMap, sourceId,
			reportParameters, fileName, inputStream, serviceContext);
	}

	/**
	 * Creates a new definition with the primary key. Does not add the definition to the database.
	 *
	 * @param definitionId the primary key for the new definition
	 * @return the new definition
	 */
	public static Definition createDefinition(long definitionId) {
		return getService().createDefinition(definitionId);
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
	 * Deletes the definition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was removed
	 * @throws PortalException
	 */
	public static Definition deleteDefinition(Definition definition)
		throws PortalException {

		return getService().deleteDefinition(definition);
	}

	/**
	 * Deletes the definition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definitionId the primary key of the definition
	 * @return the definition that was removed
	 * @throws PortalException if a definition with the primary key could not be found
	 */
	public static Definition deleteDefinition(long definitionId)
		throws PortalException {

		return getService().deleteDefinition(definitionId);
	}

	public static void deleteDefinitions(long groupId) throws PortalException {
		getService().deleteDefinitions(groupId);
	}

	public static void deleteDefinitionTemplates(
			long companyId, String attachmentsDirectory)
		throws PortalException {

		getService().deleteDefinitionTemplates(companyId, attachmentsDirectory);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
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

	public static Definition fetchDefinition(long definitionId) {
		return getService().fetchDefinition(definitionId);
	}

	/**
	 * Returns the definition matching the UUID and group.
	 *
	 * @param uuid the definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching definition, or <code>null</code> if a matching definition could not be found
	 */
	public static Definition fetchDefinitionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDefinitionByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static String[] getAttachmentsFileNames(Definition definition) {
		return getService().getAttachmentsFileNames(definition);
	}

	/**
	 * Returns the definition with the primary key.
	 *
	 * @param definitionId the primary key of the definition
	 * @return the definition
	 * @throws PortalException if a definition with the primary key could not be found
	 */
	public static Definition getDefinition(long definitionId)
		throws PortalException {

		return getService().getDefinition(definitionId);
	}

	/**
	 * Returns the definition matching the UUID and group.
	 *
	 * @param uuid the definition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching definition
	 * @throws PortalException if a matching definition could not be found
	 */
	public static Definition getDefinitionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDefinitionByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the definitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.DefinitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of definitions
	 * @param end the upper bound of the range of definitions (not inclusive)
	 * @return the range of definitions
	 */
	public static List<Definition> getDefinitions(int start, int end) {
		return getService().getDefinitions(start, end);
	}

	/**
	 * Returns all the definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the definitions
	 * @param companyId the primary key of the company
	 * @return the matching definitions, or an empty list if no matches were found
	 */
	public static List<Definition> getDefinitionsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDefinitionsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of definitions matching the UUID and company.
	 *
	 * @param uuid the UUID of the definitions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of definitions
	 * @param end the upper bound of the range of definitions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching definitions, or an empty list if no matches were found
	 */
	public static List<Definition> getDefinitionsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Definition> orderByComparator) {

		return getService().getDefinitionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of definitions.
	 *
	 * @return the number of definitions
	 */
	public static int getDefinitionsCount() {
		return getService().getDefinitionsCount();
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
	 * Updates the definition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DefinitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param definition the definition
	 * @return the definition that was updated
	 */
	public static Definition updateDefinition(Definition definition) {
		return getService().updateDefinition(definition);
	}

	public static Definition updateDefinition(
			long definitionId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, long sourceId,
			String reportParameters, String fileName, InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateDefinition(
			definitionId, nameMap, descriptionMap, sourceId, reportParameters,
			fileName, inputStream, serviceContext);
	}

	public static void updateDefinitionResources(
			Definition definition, String[] communityPermissions,
			String[] guestPermissions)
		throws PortalException {

		getService().updateDefinitionResources(
			definition, communityPermissions, guestPermissions);
	}

	public static DefinitionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DefinitionLocalService> _serviceSnapshot =
		new Snapshot<>(
			DefinitionLocalServiceUtil.class, DefinitionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1216053419