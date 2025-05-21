/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PatcherFixPack. This utility wraps
 * <code>com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixPackLocalService
 * @generated
 */
public class PatcherFixPackLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherFixPackLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the patcher fix pack to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was added
	 */
	public static PatcherFixPack addPatcherFixPack(
		PatcherFixPack patcherFixPack) {

		return getService().addPatcherFixPack(patcherFixPack);
	}

	public static boolean addPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		return getService().addPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	public static boolean addPatcherFixPatcherFixPack(
		long patcherFixId, PatcherFixPack patcherFixPack) {

		return getService().addPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPack);
	}

	public static boolean addPatcherFixPatcherFixPacks(
		long patcherFixId, List<PatcherFixPack> patcherFixPacks) {

		return getService().addPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPacks);
	}

	public static boolean addPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		return getService().addPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
	}

	public static void clearPatcherFixPatcherFixPacks(long patcherFixId) {
		getService().clearPatcherFixPatcherFixPacks(patcherFixId);
	}

	/**
	 * Creates a new patcher fix pack with the primary key. Does not add the patcher fix pack to the database.
	 *
	 * @param patcherFixPackId the primary key for the new patcher fix pack
	 * @return the new patcher fix pack
	 */
	public static PatcherFixPack createPatcherFixPack(long patcherFixPackId) {
		return getService().createPatcherFixPack(patcherFixPackId);
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
	 * Deletes the patcher fix pack with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack that was removed
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack deletePatcherFixPack(long patcherFixPackId)
		throws PortalException {

		return getService().deletePatcherFixPack(patcherFixPackId);
	}

	/**
	 * Deletes the patcher fix pack from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was removed
	 */
	public static PatcherFixPack deletePatcherFixPack(
		PatcherFixPack patcherFixPack) {

		return getService().deletePatcherFixPack(patcherFixPack);
	}

	public static void deletePatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		getService().deletePatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	public static void deletePatcherFixPatcherFixPack(
		long patcherFixId, PatcherFixPack patcherFixPack) {

		getService().deletePatcherFixPatcherFixPack(
			patcherFixId, patcherFixPack);
	}

	public static void deletePatcherFixPatcherFixPacks(
		long patcherFixId, List<PatcherFixPack> patcherFixPacks) {

		getService().deletePatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPacks);
	}

	public static void deletePatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		getService().deletePatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
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

	public static PatcherFixPack fetchPatcherFixPack(long patcherFixPackId) {
		return getService().fetchPatcherFixPack(patcherFixPackId);
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

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Returns the patcher fix pack with the primary key.
	 *
	 * @param patcherFixPackId the primary key of the patcher fix pack
	 * @return the patcher fix pack
	 * @throws PortalException if a patcher fix pack with the primary key could not be found
	 */
	public static PatcherFixPack getPatcherFixPack(long patcherFixPackId)
		throws PortalException {

		return getService().getPatcherFixPack(patcherFixPackId);
	}

	/**
	 * Returns a range of all the patcher fix packs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixPackModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fix packs
	 * @param end the upper bound of the range of patcher fix packs (not inclusive)
	 * @return the range of patcher fix packs
	 */
	public static List<PatcherFixPack> getPatcherFixPacks(int start, int end) {
		return getService().getPatcherFixPacks(start, end);
	}

	/**
	 * Returns the number of patcher fix packs.
	 *
	 * @return the number of patcher fix packs
	 */
	public static int getPatcherFixPacksCount() {
		return getService().getPatcherFixPacksCount();
	}

	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId) {

		return getService().getPatcherFixPatcherFixPacks(patcherFixId);
	}

	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end) {

		return getService().getPatcherFixPatcherFixPacks(
			patcherFixId, start, end);
	}

	public static List<PatcherFixPack> getPatcherFixPatcherFixPacks(
		long patcherFixId, int start, int end,
		OrderByComparator<PatcherFixPack> orderByComparator) {

		return getService().getPatcherFixPatcherFixPacks(
			patcherFixId, start, end, orderByComparator);
	}

	public static int getPatcherFixPatcherFixPacksCount(long patcherFixId) {
		return getService().getPatcherFixPatcherFixPacksCount(patcherFixId);
	}

	/**
	 * Returns the patcherFixIds of the patcher fixes associated with the patcher fix pack.
	 *
	 * @param patcherFixPackId the patcherFixPackId of the patcher fix pack
	 * @return long[] the patcherFixIds of patcher fixes associated with the patcher fix pack
	 */
	public static long[] getPatcherFixPrimaryKeys(long patcherFixPackId) {
		return getService().getPatcherFixPrimaryKeys(patcherFixPackId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasPatcherFixPatcherFixPack(
		long patcherFixId, long patcherFixPackId) {

		return getService().hasPatcherFixPatcherFixPack(
			patcherFixId, patcherFixPackId);
	}

	public static boolean hasPatcherFixPatcherFixPacks(long patcherFixId) {
		return getService().hasPatcherFixPatcherFixPacks(patcherFixId);
	}

	public static void setPatcherFixPatcherFixPacks(
		long patcherFixId, long[] patcherFixPackIds) {

		getService().setPatcherFixPatcherFixPacks(
			patcherFixId, patcherFixPackIds);
	}

	/**
	 * Updates the patcher fix pack in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixPackLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixPack the patcher fix pack
	 * @return the patcher fix pack that was updated
	 */
	public static PatcherFixPack updatePatcherFixPack(
		PatcherFixPack patcherFixPack) {

		return getService().updatePatcherFixPack(patcherFixPack);
	}

	public static PatcherFixPackLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PatcherFixPackLocalService> _serviceSnapshot =
		new Snapshot<>(
			PatcherFixPackLocalServiceUtil.class,
			PatcherFixPackLocalService.class);

}