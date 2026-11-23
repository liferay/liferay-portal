/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for KaleoDefinitionVersion. This utility wraps
 * <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersionLocalService
 * @generated
 */
public class KaleoDefinitionVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the kaleo definition version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was added
	 */
	public static KaleoDefinitionVersion addKaleoDefinitionVersion(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return getService().addKaleoDefinitionVersion(kaleoDefinitionVersion);
	}

	public static KaleoDefinitionVersion addKaleoDefinitionVersion(
			long kaleoDefinitionId, String name, String title,
			String description, String content, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKaleoDefinitionVersion(
			kaleoDefinitionId, name, title, description, content, version,
			serviceContext);
	}

	/**
	 * Creates a new kaleo definition version with the primary key. Does not add the kaleo definition version to the database.
	 *
	 * @param kaleoDefinitionVersionId the primary key for the new kaleo definition version
	 * @return the new kaleo definition version
	 */
	public static KaleoDefinitionVersion createKaleoDefinitionVersion(
		long kaleoDefinitionVersionId) {

		return getService().createKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
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
	 * Deletes the kaleo definition version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was removed
	 * @throws PortalException
	 */
	public static KaleoDefinitionVersion deleteKaleoDefinitionVersion(
			KaleoDefinitionVersion kaleoDefinitionVersion)
		throws PortalException {

		return getService().deleteKaleoDefinitionVersion(
			kaleoDefinitionVersion);
	}

	/**
	 * Deletes the kaleo definition version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version that was removed
	 * @throws PortalException if a kaleo definition version with the primary key could not be found
	 */
	public static KaleoDefinitionVersion deleteKaleoDefinitionVersion(
			long kaleoDefinitionVersionId)
		throws PortalException {

		return getService().deleteKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	public static void deleteKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws PortalException {

		getService().deleteKaleoDefinitionVersion(companyId, name, version);
	}

	public static void deleteKaleoDefinitionVersions(
			com.liferay.portal.workflow.kaleo.model.KaleoDefinition
				kaleoDefinition)
		throws PortalException {

		getService().deleteKaleoDefinitionVersions(kaleoDefinition);
	}

	public static void deleteKaleoDefinitionVersions(
			List<KaleoDefinitionVersion> kaleoDefinitionVersions)
		throws PortalException {

		getService().deleteKaleoDefinitionVersions(kaleoDefinitionVersions);
	}

	public static void deleteKaleoDefinitionVersions(
			long companyId, String name)
		throws PortalException {

		getService().deleteKaleoDefinitionVersions(companyId, name);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
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

	public static KaleoDefinitionVersion fetchKaleoDefinitionVersion(
		long kaleoDefinitionVersionId) {

		return getService().fetchKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	public static KaleoDefinitionVersion fetchKaleoDefinitionVersion(
		long companyId, String name, String version) {

		return getService().fetchKaleoDefinitionVersion(
			companyId, name, version);
	}

	public static KaleoDefinitionVersion fetchLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return getService().fetchLatestKaleoDefinitionVersion(companyId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static KaleoDefinitionVersion getFirstKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return getService().getFirstKaleoDefinitionVersion(companyId, name);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo definition version with the primary key.
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version
	 * @throws PortalException if a kaleo definition version with the primary key could not be found
	 */
	public static KaleoDefinitionVersion getKaleoDefinitionVersion(
			long kaleoDefinitionVersionId)
		throws PortalException {

		return getService().getKaleoDefinitionVersion(kaleoDefinitionVersionId);
	}

	public static KaleoDefinitionVersion getKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws PortalException {

		return getService().getKaleoDefinitionVersion(companyId, name, version);
	}

	/**
	 * Returns a range of all the kaleo definition versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo definition versions
	 * @param end the upper bound of the range of kaleo definition versions (not inclusive)
	 * @return the range of kaleo definition versions
	 */
	public static List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		int start, int end) {

		return getService().getKaleoDefinitionVersions(start, end);
	}

	public static List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
			long companyId, String name)
		throws PortalException {

		return getService().getKaleoDefinitionVersions(companyId, name);
	}

	public static List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		long companyId, String name, int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return getService().getKaleoDefinitionVersions(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo definition versions.
	 *
	 * @return the number of kaleo definition versions
	 */
	public static int getKaleoDefinitionVersionsCount() {
		return getService().getKaleoDefinitionVersionsCount();
	}

	public static int getKaleoDefinitionVersionsCount(
		long companyId, String name) {

		return getService().getKaleoDefinitionVersionsCount(companyId, name);
	}

	public static KaleoDefinitionVersion[]
			getKaleoDefinitionVersionsPrevAndNext(
				long companyId, String name, String version)
		throws PortalException {

		return getService().getKaleoDefinitionVersionsPrevAndNext(
			companyId, name, version);
	}

	public static KaleoDefinitionVersion getLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws PortalException {

		return getService().getLatestKaleoDefinitionVersion(companyId, name);
	}

	public static List<KaleoDefinitionVersion> getLatestKaleoDefinitionVersions(
		long companyId, String keywords, int status, java.util.Locale locale,
		int start, int end,
		OrderByComparator<KaleoDefinitionVersion> orderByComparator) {

		return getService().getLatestKaleoDefinitionVersions(
			companyId, keywords, status, locale, start, end, orderByComparator);
	}

	public static int getLatestKaleoDefinitionVersionsCount(
		long companyId, String keywords, int status) {

		return getService().getLatestKaleoDefinitionVersionsCount(
			companyId, keywords, status);
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
	 * Updates the kaleo definition version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was updated
	 */
	public static KaleoDefinitionVersion updateKaleoDefinitionVersion(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return getService().updateKaleoDefinitionVersion(
			kaleoDefinitionVersion);
	}

	public static KaleoDefinitionVersionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KaleoDefinitionVersionLocalService>
		_serviceSnapshot = new Snapshot<>(
			KaleoDefinitionVersionLocalServiceUtil.class,
			KaleoDefinitionVersionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:334459637