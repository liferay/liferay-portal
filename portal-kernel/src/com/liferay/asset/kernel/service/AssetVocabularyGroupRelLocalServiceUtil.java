/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.service;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetVocabularyGroupRel. This utility wraps
 * <code>com.liferay.portlet.asset.service.impl.AssetVocabularyGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetVocabularyGroupRelLocalService
 * @generated
 */
public class AssetVocabularyGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.asset.service.impl.AssetVocabularyGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset vocabulary group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was added
	 */
	public static AssetVocabularyGroupRel addAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return getService().addAssetVocabularyGroupRel(assetVocabularyGroupRel);
	}

	public static AssetVocabularyGroupRel addAssetVocabularyGroupRel(
			long groupId, long vocabularyId)
		throws PortalException {

		return getService().addAssetVocabularyGroupRel(groupId, vocabularyId);
	}

	/**
	 * Creates a new asset vocabulary group rel with the primary key. Does not add the asset vocabulary group rel to the database.
	 *
	 * @param assetVocabularyGroupRelId the primary key for the new asset vocabulary group rel
	 * @return the new asset vocabulary group rel
	 */
	public static AssetVocabularyGroupRel createAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId) {

		return getService().createAssetVocabularyGroupRel(
			assetVocabularyGroupRelId);
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
	 * Deletes the asset vocabulary group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 */
	public static AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return getService().deleteAssetVocabularyGroupRel(
			assetVocabularyGroupRel);
	}

	/**
	 * Deletes the asset vocabulary group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was removed
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	public static AssetVocabularyGroupRel deleteAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws PortalException {

		return getService().deleteAssetVocabularyGroupRel(
			assetVocabularyGroupRelId);
	}

	public static void deleteAssetVocabularyGroupRelsByGroupId(long groupId) {
		getService().deleteAssetVocabularyGroupRelsByGroupId(groupId);
	}

	public static void deleteAssetVocabularyGroupRelsByVocabularyId(
		long vocabularyId) {

		getService().deleteAssetVocabularyGroupRelsByVocabularyId(vocabularyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
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

	public static AssetVocabularyGroupRel fetchAssetVocabularyGroupRel(
		long assetVocabularyGroupRelId) {

		return getService().fetchAssetVocabularyGroupRel(
			assetVocabularyGroupRelId);
	}

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel, or <code>null</code> if a matching asset vocabulary group rel could not be found
	 */
	public static AssetVocabularyGroupRel
		fetchAssetVocabularyGroupRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchAssetVocabularyGroupRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the asset vocabulary group rel with the primary key.
	 *
	 * @param assetVocabularyGroupRelId the primary key of the asset vocabulary group rel
	 * @return the asset vocabulary group rel
	 * @throws PortalException if a asset vocabulary group rel with the primary key could not be found
	 */
	public static AssetVocabularyGroupRel getAssetVocabularyGroupRel(
			long assetVocabularyGroupRelId)
		throws PortalException {

		return getService().getAssetVocabularyGroupRel(
			assetVocabularyGroupRelId);
	}

	/**
	 * Returns the asset vocabulary group rel matching the UUID and group.
	 *
	 * @param uuid the asset vocabulary group rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching asset vocabulary group rel
	 * @throws PortalException if a matching asset vocabulary group rel could not be found
	 */
	public static AssetVocabularyGroupRel
			getAssetVocabularyGroupRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getAssetVocabularyGroupRelByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the asset vocabulary group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.asset.model.impl.AssetVocabularyGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @return the range of asset vocabulary group rels
	 */
	public static List<AssetVocabularyGroupRel> getAssetVocabularyGroupRels(
		int start, int end) {

		return getService().getAssetVocabularyGroupRels(start, end);
	}

	public static List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByGroupId(long groupId) {

		return getService().getAssetVocabularyGroupRelsByGroupId(groupId);
	}

	/**
	 * Returns all the asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @return the matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	public static List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getAssetVocabularyGroupRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of asset vocabulary group rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the asset vocabulary group rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of asset vocabulary group rels
	 * @param end the upper bound of the range of asset vocabulary group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching asset vocabulary group rels, or an empty list if no matches were found
	 */
	public static List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<AssetVocabularyGroupRel> orderByComparator) {

		return getService().getAssetVocabularyGroupRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	public static List<AssetVocabularyGroupRel>
		getAssetVocabularyGroupRelsByVocabularyId(long vocabularyId) {

		return getService().getAssetVocabularyGroupRelsByVocabularyId(
			vocabularyId);
	}

	/**
	 * Returns the number of asset vocabulary group rels.
	 *
	 * @return the number of asset vocabulary group rels
	 */
	public static int getAssetVocabularyGroupRelsCount() {
		return getService().getAssetVocabularyGroupRelsCount();
	}

	public static int getAssetVocabularyGroupRelsCount(long vocabularyId) {
		return getService().getAssetVocabularyGroupRelsCount(vocabularyId);
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

	public static void setAssetVocabularyGroupRels(
			long vocabularyId, long[] groupIds)
		throws PortalException {

		getService().setAssetVocabularyGroupRels(vocabularyId, groupIds);
	}

	/**
	 * Updates the asset vocabulary group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetVocabularyGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetVocabularyGroupRel the asset vocabulary group rel
	 * @return the asset vocabulary group rel that was updated
	 */
	public static AssetVocabularyGroupRel updateAssetVocabularyGroupRel(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		return getService().updateAssetVocabularyGroupRel(
			assetVocabularyGroupRel);
	}

	public static AssetVocabularyGroupRelLocalService getService() {
		return _service;
	}

	public static void setService(AssetVocabularyGroupRelLocalService service) {
		_service = service;
	}

	private static volatile AssetVocabularyGroupRelLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1546189820