/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DepotEntryGroupRel. This utility wraps
 * <code>com.liferay.depot.service.impl.DepotEntryGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelLocalService
 * @generated
 */
public class DepotEntryGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.depot.service.impl.DepotEntryGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static DepotEntryGroupRel addDepotEntryGroupRel(
		boolean ddmStructuresAvailable, long depotEntryId, long toGroupId,
		boolean searchable) {

		return getService().addDepotEntryGroupRel(
			ddmStructuresAvailable, depotEntryId, toGroupId, searchable);
	}

	/**
	 * Adds the depot entry group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was added
	 */
	public static DepotEntryGroupRel addDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return getService().addDepotEntryGroupRel(depotEntryGroupRel);
	}

	public static DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId) {

		return getService().addDepotEntryGroupRel(depotEntryId, toGroupId);
	}

	public static DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId, boolean searchable) {

		return getService().addDepotEntryGroupRel(
			depotEntryId, toGroupId, searchable);
	}

	/**
	 * Creates a new depot entry group rel with the primary key. Does not add the depot entry group rel to the database.
	 *
	 * @param depotEntryGroupRelId the primary key for the new depot entry group rel
	 * @return the new depot entry group rel
	 */
	public static DepotEntryGroupRel createDepotEntryGroupRel(
		long depotEntryGroupRelId) {

		return getService().createDepotEntryGroupRel(depotEntryGroupRelId);
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
	 * Deletes the depot entry group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was removed
	 */
	public static DepotEntryGroupRel deleteDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return getService().deleteDepotEntryGroupRel(depotEntryGroupRel);
	}

	/**
	 * Deletes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws PortalException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws PortalException {

		return getService().deleteDepotEntryGroupRel(depotEntryGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteToGroupDepotEntryGroupRels(long toGroupId) {
		getService().deleteToGroupDepotEntryGroupRels(toGroupId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
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

	public static DepotEntryGroupRel fetchDepotEntryGroupRel(
		long depotEntryGroupRelId) {

		return getService().fetchDepotEntryGroupRel(depotEntryGroupRelId);
	}

	public static DepotEntryGroupRel
		fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId) {

		return getService().fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
			depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel matching the UUID and group.
	 *
	 * @param uuid the depot entry group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel fetchDepotEntryGroupRelByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDepotEntryGroupRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the depot entry group rel with the primary key.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws PortalException if a depot entry group rel with the primary key could not be found
	 */
	public static DepotEntryGroupRel getDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws PortalException {

		return getService().getDepotEntryGroupRel(depotEntryGroupRelId);
	}

	public static DepotEntryGroupRel
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				long depotEntryId, long toGroupId)
		throws PortalException {

		return getService().getDepotEntryGroupRelByDepotEntryIdToGroupId(
			depotEntryId, toGroupId);
	}

	/**
	 * Returns the depot entry group rel matching the UUID and group.
	 *
	 * @param uuid the depot entry group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching depot entry group rel
	 * @throws PortalException if a matching depot entry group rel could not be found
	 */
	public static DepotEntryGroupRel getDepotEntryGroupRelByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDepotEntryGroupRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static List<DepotEntryGroupRel> getDepotEntryGroupRels(
		com.liferay.depot.model.DepotEntry depotEntry) {

		return getService().getDepotEntryGroupRels(depotEntry);
	}

	/**
	 * Returns a range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.depot.model.impl.DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of depot entry group rels
	 */
	public static List<DepotEntryGroupRel> getDepotEntryGroupRels(
		int start, int end) {

		return getService().getDepotEntryGroupRels(start, end);
	}

	public static List<DepotEntryGroupRel> getDepotEntryGroupRels(
		long groupId, int start, int end) {

		return getService().getDepotEntryGroupRels(groupId, start, end);
	}

	/**
	 * Returns all the depot entry group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry group rels
	 * @param companyId the primary key of the company
	 * @return the matching depot entry group rels, or an empty list if no matches were found
	 */
	public static List<DepotEntryGroupRel>
		getDepotEntryGroupRelsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getDepotEntryGroupRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of depot entry group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the depot entry group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching depot entry group rels, or an empty list if no matches were found
	 */
	public static List<DepotEntryGroupRel>
		getDepotEntryGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DepotEntryGroupRel> orderByComparator) {

		return getService().getDepotEntryGroupRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of depot entry group rels.
	 *
	 * @return the number of depot entry group rels
	 */
	public static int getDepotEntryGroupRelsCount() {
		return getService().getDepotEntryGroupRelsCount();
	}

	public static int getDepotEntryGroupRelsCount(
		com.liferay.depot.model.DepotEntry depotEntry) {

		return getService().getDepotEntryGroupRelsCount(depotEntry);
	}

	public static int getDepotEntryGroupRelsCount(long groupId) {
		return getService().getDepotEntryGroupRelsCount(groupId);
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

	public static List<DepotEntryGroupRel> getSearchableDepotEntryGroupRels(
		long groupId, int start, int end) {

		return getService().getSearchableDepotEntryGroupRels(
			groupId, start, end);
	}

	public static int getSearchableDepotEntryGroupRelsCount(long groupId) {
		return getService().getSearchableDepotEntryGroupRelsCount(groupId);
	}

	public static DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws PortalException {

		return getService().updateDDMStructuresAvailable(
			depotEntryGroupRelId, ddmStructuresAvailable);
	}

	/**
	 * Updates the depot entry group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DepotEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 * @return the depot entry group rel that was updated
	 */
	public static DepotEntryGroupRel updateDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return getService().updateDepotEntryGroupRel(depotEntryGroupRel);
	}

	public static DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws PortalException {

		return getService().updateSearchable(depotEntryGroupRelId, searchable);
	}

	public static DepotEntryGroupRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DepotEntryGroupRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			DepotEntryGroupRelLocalServiceUtil.class,
			DepotEntryGroupRelLocalService.class);

}