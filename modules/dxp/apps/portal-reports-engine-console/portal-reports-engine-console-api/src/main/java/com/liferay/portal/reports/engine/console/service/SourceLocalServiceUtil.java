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
import com.liferay.portal.reports.engine.console.model.Source;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for Source. This utility wraps
 * <code>com.liferay.portal.reports.engine.console.service.impl.SourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SourceLocalService
 * @generated
 */
public class SourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.reports.engine.console.service.impl.SourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static Source addSource(
			long userId, long groupId, Map<java.util.Locale, String> nameMap,
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSource(
			userId, groupId, nameMap, driverClassName, driverUrl,
			driverUserName, driverPassword, serviceContext);
	}

	/**
	 * Adds the source to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was added
	 */
	public static Source addSource(Source source) {
		return getService().addSource(source);
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
	 * Creates a new source with the primary key. Does not add the source to the database.
	 *
	 * @param sourceId the primary key for the new source
	 * @return the new source
	 */
	public static Source createSource(long sourceId) {
		return getService().createSource(sourceId);
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
	 * Deletes the source with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sourceId the primary key of the source
	 * @return the source that was removed
	 * @throws PortalException if a source with the primary key could not be found
	 */
	public static Source deleteSource(long sourceId) throws PortalException {
		return getService().deleteSource(sourceId);
	}

	/**
	 * Deletes the source from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was removed
	 * @throws PortalException
	 */
	public static Source deleteSource(Source source) throws PortalException {
		return getService().deleteSource(source);
	}

	public static void deleteSources(long groupId) throws PortalException {
		getService().deleteSources(groupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
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

	public static Source fetchSource(long sourceId) {
		return getService().fetchSource(sourceId);
	}

	/**
	 * Returns the source matching the UUID and group.
	 *
	 * @param uuid the source's UUID
	 * @param groupId the primary key of the group
	 * @return the matching source, or <code>null</code> if a matching source could not be found
	 */
	public static Source fetchSourceByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchSourceByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static String[] getAttachmentsFileNames(Source source) {
		return getService().getAttachmentsFileNames(source);
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
	 * Returns the source with the primary key.
	 *
	 * @param sourceId the primary key of the source
	 * @return the source
	 * @throws PortalException if a source with the primary key could not be found
	 */
	public static Source getSource(long sourceId) throws PortalException {
		return getService().getSource(sourceId);
	}

	/**
	 * Returns the source matching the UUID and group.
	 *
	 * @param uuid the source's UUID
	 * @param groupId the primary key of the group
	 * @return the matching source
	 * @throws PortalException if a matching source could not be found
	 */
	public static Source getSourceByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getSourceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the sources.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.reports.engine.console.model.impl.SourceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sources
	 * @param end the upper bound of the range of sources (not inclusive)
	 * @return the range of sources
	 */
	public static List<Source> getSources(int start, int end) {
		return getService().getSources(start, end);
	}

	/**
	 * Returns all the sources matching the UUID and company.
	 *
	 * @param uuid the UUID of the sources
	 * @param companyId the primary key of the company
	 * @return the matching sources, or an empty list if no matches were found
	 */
	public static List<Source> getSourcesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getSourcesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of sources matching the UUID and company.
	 *
	 * @param uuid the UUID of the sources
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of sources
	 * @param end the upper bound of the range of sources (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching sources, or an empty list if no matches were found
	 */
	public static List<Source> getSourcesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Source> orderByComparator) {

		return getService().getSourcesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sources.
	 *
	 * @return the number of sources
	 */
	public static int getSourcesCount() {
		return getService().getSourcesCount();
	}

	public static Source updateSource(
			long sourceId, Map<java.util.Locale, String> nameMap,
			String driverClassName, String driverUrl, String driverUserName,
			String driverPassword,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSource(
			sourceId, nameMap, driverClassName, driverUrl, driverUserName,
			driverPassword, serviceContext);
	}

	/**
	 * Updates the source in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SourceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param source the source
	 * @return the source that was updated
	 */
	public static Source updateSource(Source source) {
		return getService().updateSource(source);
	}

	public static SourceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SourceLocalService> _serviceSnapshot =
		new Snapshot<>(SourceLocalServiceUtil.class, SourceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1160030405