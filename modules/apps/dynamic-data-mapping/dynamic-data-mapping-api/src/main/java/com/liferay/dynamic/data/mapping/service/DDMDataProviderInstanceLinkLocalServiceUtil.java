/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DDMDataProviderInstanceLink. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderInstanceLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMDataProviderInstanceLinkLocalService
 * @generated
 */
public class DDMDataProviderInstanceLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMDataProviderInstanceLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static DDMDataProviderInstanceLink addDataProviderInstanceLink(
		long dataProviderInstanceId, long structureId) {

		return getService().addDataProviderInstanceLink(
			dataProviderInstanceId, structureId);
	}

	/**
	 * Adds the ddm data provider instance link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was added
	 */
	public static DDMDataProviderInstanceLink addDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return getService().addDDMDataProviderInstanceLink(
			ddmDataProviderInstanceLink);
	}

	/**
	 * Creates a new ddm data provider instance link with the primary key. Does not add the ddm data provider instance link to the database.
	 *
	 * @param dataProviderInstanceLinkId the primary key for the new ddm data provider instance link
	 * @return the new ddm data provider instance link
	 */
	public static DDMDataProviderInstanceLink createDDMDataProviderInstanceLink(
		long dataProviderInstanceLinkId) {

		return getService().createDDMDataProviderInstanceLink(
			dataProviderInstanceLinkId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteDataProviderInstanceLink(
		DDMDataProviderInstanceLink dataProviderInstanceLink) {

		getService().deleteDataProviderInstanceLink(dataProviderInstanceLink);
	}

	public static void deleteDataProviderInstanceLink(
			long dataProviderInstanceLinkId)
		throws PortalException {

		getService().deleteDataProviderInstanceLink(dataProviderInstanceLinkId);
	}

	/**
	 * Deletes the ddm data provider instance link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was removed
	 */
	public static DDMDataProviderInstanceLink deleteDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return getService().deleteDDMDataProviderInstanceLink(
			ddmDataProviderInstanceLink);
	}

	/**
	 * Deletes the ddm data provider instance link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link that was removed
	 * @throws PortalException if a ddm data provider instance link with the primary key could not be found
	 */
	public static DDMDataProviderInstanceLink deleteDDMDataProviderInstanceLink(
			long dataProviderInstanceLinkId)
		throws PortalException {

		return getService().deleteDDMDataProviderInstanceLink(
			dataProviderInstanceLinkId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
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

	public static DDMDataProviderInstanceLink fetchDDMDataProviderInstanceLink(
		long dataProviderInstanceLinkId) {

		return getService().fetchDDMDataProviderInstanceLink(
			dataProviderInstanceLinkId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<DDMDataProviderInstanceLink>
		getDataProviderInstanceLinks(long structureId) {

		return getService().getDataProviderInstanceLinks(structureId);
	}

	/**
	 * Returns the ddm data provider instance link with the primary key.
	 *
	 * @param dataProviderInstanceLinkId the primary key of the ddm data provider instance link
	 * @return the ddm data provider instance link
	 * @throws PortalException if a ddm data provider instance link with the primary key could not be found
	 */
	public static DDMDataProviderInstanceLink getDDMDataProviderInstanceLink(
			long dataProviderInstanceLinkId)
		throws PortalException {

		return getService().getDDMDataProviderInstanceLink(
			dataProviderInstanceLinkId);
	}

	/**
	 * Returns a range of all the ddm data provider instance links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMDataProviderInstanceLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm data provider instance links
	 * @param end the upper bound of the range of ddm data provider instance links (not inclusive)
	 * @return the range of ddm data provider instance links
	 */
	public static List<DDMDataProviderInstanceLink>
		getDDMDataProviderInstanceLinks(int start, int end) {

		return getService().getDDMDataProviderInstanceLinks(start, end);
	}

	/**
	 * Returns the number of ddm data provider instance links.
	 *
	 * @return the number of ddm data provider instance links
	 */
	public static int getDDMDataProviderInstanceLinksCount() {
		return getService().getDDMDataProviderInstanceLinksCount();
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
	 * Updates the ddm data provider instance link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMDataProviderInstanceLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmDataProviderInstanceLink the ddm data provider instance link
	 * @return the ddm data provider instance link that was updated
	 */
	public static DDMDataProviderInstanceLink updateDDMDataProviderInstanceLink(
		DDMDataProviderInstanceLink ddmDataProviderInstanceLink) {

		return getService().updateDDMDataProviderInstanceLink(
			ddmDataProviderInstanceLink);
	}

	public static DDMDataProviderInstanceLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMDataProviderInstanceLinkLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMDataProviderInstanceLinkLocalServiceUtil.class,
			DDMDataProviderInstanceLinkLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1374613188