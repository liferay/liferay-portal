/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service;

import com.liferay.layout.content.model.LayoutContentVersion;
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
 * Provides the local service utility for LayoutContentVersion. This utility wraps
 * <code>com.liferay.layout.content.service.impl.LayoutContentVersionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionLocalService
 * @generated
 */
public class LayoutContentVersionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.layout.content.service.impl.LayoutContentVersionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout content version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was added
	 */
	public static LayoutContentVersion addLayoutContentVersion(
		LayoutContentVersion layoutContentVersion) {

		return getService().addLayoutContentVersion(layoutContentVersion);
	}

	public static LayoutContentVersion addLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<java.util.Locale, String> nameMap, long plid, int status)
		throws PortalException {

		return getService().addLayoutContentVersion(
			externalReferenceCode, userId, data, nameMap, plid, status);
	}

	public static LayoutContentVersion addOrUpdateLayoutContentVersion(
			String externalReferenceCode, long userId, String data,
			Map<java.util.Locale, String> nameMap, long plid, int status)
		throws PortalException {

		return getService().addOrUpdateLayoutContentVersion(
			externalReferenceCode, userId, data, nameMap, plid, status);
	}

	/**
	 * Creates a new layout content version with the primary key. Does not add the layout content version to the database.
	 *
	 * @param layoutContentVersionId the primary key for the new layout content version
	 * @return the new layout content version
	 */
	public static LayoutContentVersion createLayoutContentVersion(
		long layoutContentVersionId) {

		return getService().createLayoutContentVersion(layoutContentVersionId);
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
	 * Deletes the layout content version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was removed
	 */
	public static LayoutContentVersion deleteLayoutContentVersion(
		LayoutContentVersion layoutContentVersion) {

		return getService().deleteLayoutContentVersion(layoutContentVersion);
	}

	/**
	 * Deletes the layout content version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version that was removed
	 * @throws PortalException if a layout content version with the primary key could not be found
	 */
	public static LayoutContentVersion deleteLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		return getService().deleteLayoutContentVersion(layoutContentVersionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
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

	public static LayoutContentVersion fetchLayoutContentVersion(
		long layoutContentVersionId) {

		return getService().fetchLayoutContentVersion(layoutContentVersionId);
	}

	public static LayoutContentVersion
		fetchLayoutContentVersionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchLayoutContentVersionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static long getLatestApprovedLayoutContentVersionId(long plid) {
		return getService().getLatestApprovedLayoutContentVersionId(plid);
	}

	/**
	 * Returns the layout content version with the primary key.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version
	 * @throws PortalException if a layout content version with the primary key could not be found
	 */
	public static LayoutContentVersion getLayoutContentVersion(
			long layoutContentVersionId)
		throws PortalException {

		return getService().getLayoutContentVersion(layoutContentVersionId);
	}

	public static LayoutContentVersion
			getLayoutContentVersionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getLayoutContentVersionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns a range of all the layout content versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @return the range of layout content versions
	 */
	public static List<LayoutContentVersion> getLayoutContentVersions(
		int start, int end) {

		return getService().getLayoutContentVersions(start, end);
	}

	public static List<LayoutContentVersion> getLayoutContentVersions(long plid)
		throws PortalException {

		return getService().getLayoutContentVersions(plid);
	}

	/**
	 * Returns the number of layout content versions.
	 *
	 * @return the number of layout content versions
	 */
	public static int getLayoutContentVersionsCount() {
		return getService().getLayoutContentVersionsCount();
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
	 * Updates the layout content version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersion the layout content version
	 * @return the layout content version that was updated
	 */
	public static LayoutContentVersion updateLayoutContentVersion(
		LayoutContentVersion layoutContentVersion) {

		return getService().updateLayoutContentVersion(layoutContentVersion);
	}

	public static LayoutContentVersion updateLayoutContentVersion(
			long layoutContentVersionId, Map<java.util.Locale, String> nameMap)
		throws PortalException {

		return getService().updateLayoutContentVersion(
			layoutContentVersionId, nameMap);
	}

	public static LayoutContentVersionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LayoutContentVersionLocalService>
		_serviceSnapshot = new Snapshot<>(
			LayoutContentVersionLocalServiceUtil.class,
			LayoutContentVersionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:59137004