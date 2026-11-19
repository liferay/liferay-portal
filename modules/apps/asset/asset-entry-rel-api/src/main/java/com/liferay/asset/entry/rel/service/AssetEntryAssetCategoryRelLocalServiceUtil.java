/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.service;

import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AssetEntryAssetCategoryRel. This utility wraps
 * <code>com.liferay.asset.entry.rel.service.impl.AssetEntryAssetCategoryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AssetEntryAssetCategoryRelLocalService
 * @generated
 */
public class AssetEntryAssetCategoryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.asset.entry.rel.service.impl.AssetEntryAssetCategoryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the asset entry asset category rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was added
	 */
	public static AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return getService().addAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRel);
	}

	public static AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId) {

		return getService().addAssetEntryAssetCategoryRel(
			assetEntryId, assetCategoryId);
	}

	public static AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId, int priority) {

		return getService().addAssetEntryAssetCategoryRel(
			assetEntryId, assetCategoryId, priority);
	}

	/**
	 * Creates a new asset entry asset category rel with the primary key. Does not add the asset entry asset category rel to the database.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key for the new asset entry asset category rel
	 * @return the new asset entry asset category rel
	 */
	public static AssetEntryAssetCategoryRel createAssetEntryAssetCategoryRel(
		long assetEntryAssetCategoryRelId) {

		return getService().createAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRelId);
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
	 * Deletes the asset entry asset category rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was removed
	 */
	public static AssetEntryAssetCategoryRel deleteAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return getService().deleteAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRel);
	}

	/**
	 * Deletes the asset entry asset category rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel that was removed
	 * @throws PortalException if a asset entry asset category rel with the primary key could not be found
	 */
	public static AssetEntryAssetCategoryRel deleteAssetEntryAssetCategoryRel(
			long assetEntryAssetCategoryRelId)
		throws PortalException {

		return getService().deleteAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRelId);
	}

	public static void deleteAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId) {

		getService().deleteAssetEntryAssetCategoryRel(
			assetEntryId, assetCategoryId);
	}

	public static void deleteAssetEntryAssetCategoryRelByAssetCategoryId(
		long assetCategoryId) {

		getService().deleteAssetEntryAssetCategoryRelByAssetCategoryId(
			assetCategoryId);
	}

	public static void deleteAssetEntryAssetCategoryRelByAssetEntry(
		com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		getService().deleteAssetEntryAssetCategoryRelByAssetEntry(assetEntry);
	}

	public static void deleteAssetEntryAssetCategoryRelByAssetEntryId(
		long assetEntryId) {

		getService().deleteAssetEntryAssetCategoryRelByAssetEntryId(
			assetEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
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

	public static AssetEntryAssetCategoryRel fetchAssetEntryAssetCategoryRel(
		long assetEntryAssetCategoryRelId) {

		return getService().fetchAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static long[] getAssetCategoryPrimaryKeys(long assetEntryId) {
		return getService().getAssetCategoryPrimaryKeys(assetEntryId);
	}

	/**
	 * Returns the asset entry asset category rel with the primary key.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel
	 * @throws PortalException if a asset entry asset category rel with the primary key could not be found
	 */
	public static AssetEntryAssetCategoryRel getAssetEntryAssetCategoryRel(
			long assetEntryAssetCategoryRelId)
		throws PortalException {

		return getService().getAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRelId);
	}

	/**
	 * Returns a range of all the asset entry asset category rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset entry asset category rels
	 * @param end the upper bound of the range of asset entry asset category rels (not inclusive)
	 * @return the range of asset entry asset category rels
	 */
	public static List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRels(int start, int end) {

		return getService().getAssetEntryAssetCategoryRels(start, end);
	}

	public static List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetCategoryId(long assetCategoryId) {

		return getService().getAssetEntryAssetCategoryRelsByAssetCategoryId(
			assetCategoryId);
	}

	public static List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetEntryId(long assetEntryId) {

		return getService().getAssetEntryAssetCategoryRelsByAssetEntryId(
			assetEntryId);
	}

	public static List<AssetEntryAssetCategoryRel>
		getAssetEntryAssetCategoryRelsByAssetEntryId(
			long assetEntryId, int start, int end,
			OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator) {

		return getService().getAssetEntryAssetCategoryRelsByAssetEntryId(
			assetEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of asset entry asset category rels.
	 *
	 * @return the number of asset entry asset category rels
	 */
	public static int getAssetEntryAssetCategoryRelsCount() {
		return getService().getAssetEntryAssetCategoryRelsCount();
	}

	public static int getAssetEntryAssetCategoryRelsCount(long assetEntryId) {
		return getService().getAssetEntryAssetCategoryRelsCount(assetEntryId);
	}

	public static int getAssetEntryAssetCategoryRelsCountByAssetCategoryId(
		long assetCategoryId) {

		return getService().
			getAssetEntryAssetCategoryRelsCountByAssetCategoryId(
				assetCategoryId);
	}

	public static int getAssetEntryAssetCategoryRelsCountByClassNameId(
		long assetCategoryId, long classNameId) {

		return getService().getAssetEntryAssetCategoryRelsCountByClassNameId(
			assetCategoryId, classNameId);
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
	 * Updates the asset entry asset category rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetEntryAssetCategoryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetEntryAssetCategoryRel the asset entry asset category rel
	 * @return the asset entry asset category rel that was updated
	 */
	public static AssetEntryAssetCategoryRel updateAssetEntryAssetCategoryRel(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		return getService().updateAssetEntryAssetCategoryRel(
			assetEntryAssetCategoryRel);
	}

	public static AssetEntryAssetCategoryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AssetEntryAssetCategoryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AssetEntryAssetCategoryRelLocalServiceUtil.class,
			AssetEntryAssetCategoryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-830423819