/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FragmentComposition. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentCompositionLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCompositionLocalService
 * @generated
 */
public class FragmentCompositionLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCompositionLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the fragment composition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was added
	 */
	public static FragmentComposition addFragmentComposition(
		FragmentComposition fragmentComposition) {

		return getService().addFragmentComposition(fragmentComposition);
	}

	public static FragmentComposition addFragmentComposition(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentCompositionKey,
			String name, String description, String data,
			long previewFileEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentComposition(
			externalReferenceCode, userId, groupId, fragmentCollectionId,
			fragmentCompositionKey, name, description, data, previewFileEntryId,
			status, serviceContext);
	}

	/**
	 * Creates a new fragment composition with the primary key. Does not add the fragment composition to the database.
	 *
	 * @param fragmentCompositionId the primary key for the new fragment composition
	 * @return the new fragment composition
	 */
	public static FragmentComposition createFragmentComposition(
		long fragmentCompositionId) {

		return getService().createFragmentComposition(fragmentCompositionId);
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
	 * Deletes the fragment composition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was removed
	 * @throws PortalException
	 */
	public static FragmentComposition deleteFragmentComposition(
			FragmentComposition fragmentComposition)
		throws PortalException {

		return getService().deleteFragmentComposition(fragmentComposition);
	}

	/**
	 * Deletes the fragment composition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition that was removed
	 * @throws PortalException if a fragment composition with the primary key could not be found
	 */
	public static FragmentComposition deleteFragmentComposition(
			long fragmentCompositionId)
		throws PortalException {

		return getService().deleteFragmentComposition(fragmentCompositionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
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

	public static FragmentComposition fetchFragmentComposition(
		long fragmentCompositionId) {

		return getService().fetchFragmentComposition(fragmentCompositionId);
	}

	public static FragmentComposition fetchFragmentComposition(
		long groupId, String fragmentCompositionKey) {

		return getService().fetchFragmentComposition(
			groupId, fragmentCompositionKey);
	}

	public static FragmentComposition
		fetchFragmentCompositionByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchFragmentCompositionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment composition matching the UUID and group.
	 *
	 * @param uuid the fragment composition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	public static FragmentComposition fetchFragmentCompositionByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchFragmentCompositionByUuidAndGroupId(
			uuid, groupId);
	}

	public static String generateFragmentCompositionKey(
		long groupId, String name) {

		return getService().generateFragmentCompositionKey(groupId, name);
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

	/**
	 * Returns the fragment composition with the primary key.
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition
	 * @throws PortalException if a fragment composition with the primary key could not be found
	 */
	public static FragmentComposition getFragmentComposition(
			long fragmentCompositionId)
		throws PortalException {

		return getService().getFragmentComposition(fragmentCompositionId);
	}

	public static FragmentComposition
			getFragmentCompositionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentCompositionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment composition matching the UUID and group.
	 *
	 * @param uuid the fragment composition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment composition
	 * @throws PortalException if a matching fragment composition could not be found
	 */
	public static FragmentComposition getFragmentCompositionByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFragmentCompositionByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the fragment compositions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of fragment compositions
	 */
	public static List<FragmentComposition> getFragmentCompositions(
		int start, int end) {

		return getService().getFragmentCompositions(start, end);
	}

	public static List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId) {

		return getService().getFragmentCompositions(fragmentCollectionId);
	}

	public static List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId, int start, int end) {

		return getService().getFragmentCompositions(
			fragmentCollectionId, start, end);
	}

	public static List<FragmentComposition> getFragmentCompositions(
		long groupId, long fragmentCollectionId, int status) {

		return getService().getFragmentCompositions(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns all the fragment compositions matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment compositions
	 * @param companyId the primary key of the company
	 * @return the matching fragment compositions, or an empty list if no matches were found
	 */
	public static List<FragmentComposition>
		getFragmentCompositionsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getFragmentCompositionsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of fragment compositions matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment compositions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching fragment compositions, or an empty list if no matches were found
	 */
	public static List<FragmentComposition>
		getFragmentCompositionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<FragmentComposition> orderByComparator) {

		return getService().getFragmentCompositionsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment compositions.
	 *
	 * @return the number of fragment compositions
	 */
	public static int getFragmentCompositionsCount() {
		return getService().getFragmentCompositionsCount();
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

	public static String getUniqueFragmentCompositionName(
		long groupId, long fragmentCollectionId, String name) {

		return getService().getUniqueFragmentCompositionName(
			groupId, fragmentCollectionId, name);
	}

	public static FragmentComposition moveFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId)
		throws PortalException {

		return getService().moveFragmentComposition(
			fragmentCompositionId, fragmentCollectionId);
	}

	/**
	 * Updates the fragment composition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was updated
	 */
	public static FragmentComposition updateFragmentComposition(
		FragmentComposition fragmentComposition) {

		return getService().updateFragmentComposition(fragmentComposition);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long previewFileEntryId)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, previewFileEntryId);
	}

	public static FragmentComposition updateFragmentComposition(
			long userId, long fragmentCompositionId, long fragmentCollectionId,
			String name, String description, String data,
			long previewFileEntryId, int status)
		throws PortalException {

		return getService().updateFragmentComposition(
			userId, fragmentCompositionId, fragmentCollectionId, name,
			description, data, previewFileEntryId, status);
	}

	public static FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name)
		throws PortalException {

		return getService().updateFragmentComposition(
			fragmentCompositionId, name);
	}

	public static FragmentCompositionLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentCompositionLocalService>
		_serviceSnapshot = new Snapshot<>(
			FragmentCompositionLocalServiceUtil.class,
			FragmentCompositionLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1220338202