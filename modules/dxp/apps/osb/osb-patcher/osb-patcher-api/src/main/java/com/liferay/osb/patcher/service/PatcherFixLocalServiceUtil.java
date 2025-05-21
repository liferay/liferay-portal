/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for PatcherFix. This utility wraps
 * <code>com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherFixLocalService
 * @generated
 */
public class PatcherFixLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.osb.patcher.service.impl.PatcherFixLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static boolean addPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		return getService().addPatcherBuildPatcherFix(
			patcherBuildId, patcherFixId);
	}

	public static boolean addPatcherBuildPatcherFix(
		long patcherBuildId, PatcherFix patcherFix) {

		return getService().addPatcherBuildPatcherFix(
			patcherBuildId, patcherFix);
	}

	public static boolean addPatcherBuildPatcherFixes(
		long patcherBuildId, List<PatcherFix> patcherFixes) {

		return getService().addPatcherBuildPatcherFixes(
			patcherBuildId, patcherFixes);
	}

	public static boolean addPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		return getService().addPatcherBuildPatcherFixes(
			patcherBuildId, patcherFixIds);
	}

	/**
	 * Adds the patcher fix to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was added
	 */
	public static PatcherFix addPatcherFix(PatcherFix patcherFix) {
		return getService().addPatcherFix(patcherFix);
	}

	public static boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		return getService().addPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	public static boolean addPatcherFixPackPatcherFix(
		long patcherFixPackId, PatcherFix patcherFix) {

		return getService().addPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFix);
	}

	public static boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId, List<PatcherFix> patcherFixes) {

		return getService().addPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixes);
	}

	public static boolean addPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		return getService().addPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
	}

	public static void clearPatcherBuildPatcherFixes(long patcherBuildId) {
		getService().clearPatcherBuildPatcherFixes(patcherBuildId);
	}

	public static void clearPatcherFixPackPatcherFixes(long patcherFixPackId) {
		getService().clearPatcherFixPackPatcherFixes(patcherFixPackId);
	}

	/**
	 * Creates a new patcher fix with the primary key. Does not add the patcher fix to the database.
	 *
	 * @param patcherFixId the primary key for the new patcher fix
	 * @return the new patcher fix
	 */
	public static PatcherFix createPatcherFix(long patcherFixId) {
		return getService().createPatcherFix(patcherFixId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deletePatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		getService().deletePatcherBuildPatcherFix(patcherBuildId, patcherFixId);
	}

	public static void deletePatcherBuildPatcherFix(
		long patcherBuildId, PatcherFix patcherFix) {

		getService().deletePatcherBuildPatcherFix(patcherBuildId, patcherFix);
	}

	public static void deletePatcherBuildPatcherFixes(
		long patcherBuildId, List<PatcherFix> patcherFixes) {

		getService().deletePatcherBuildPatcherFixes(
			patcherBuildId, patcherFixes);
	}

	public static void deletePatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		getService().deletePatcherBuildPatcherFixes(
			patcherBuildId, patcherFixIds);
	}

	/**
	 * Deletes the patcher fix with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix that was removed
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix deletePatcherFix(long patcherFixId)
		throws PortalException {

		return getService().deletePatcherFix(patcherFixId);
	}

	/**
	 * Deletes the patcher fix from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was removed
	 */
	public static PatcherFix deletePatcherFix(PatcherFix patcherFix) {
		return getService().deletePatcherFix(patcherFix);
	}

	public static void deletePatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		getService().deletePatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	public static void deletePatcherFixPackPatcherFix(
		long patcherFixPackId, PatcherFix patcherFix) {

		getService().deletePatcherFixPackPatcherFix(
			patcherFixPackId, patcherFix);
	}

	public static void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId, List<PatcherFix> patcherFixes) {

		getService().deletePatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixes);
	}

	public static void deletePatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		getService().deletePatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
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

	public static PatcherFix fetchPatcherFix(long patcherFixId) {
		return getService().fetchPatcherFix(patcherFixId);
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

	public static List<PatcherFix> getPatcherBuildPatcherFixes(
		long patcherBuildId) {

		return getService().getPatcherBuildPatcherFixes(patcherBuildId);
	}

	public static List<PatcherFix> getPatcherBuildPatcherFixes(
		long patcherBuildId, int start, int end) {

		return getService().getPatcherBuildPatcherFixes(
			patcherBuildId, start, end);
	}

	public static List<PatcherFix> getPatcherBuildPatcherFixes(
		long patcherBuildId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getService().getPatcherBuildPatcherFixes(
			patcherBuildId, start, end, orderByComparator);
	}

	public static int getPatcherBuildPatcherFixesCount(long patcherBuildId) {
		return getService().getPatcherBuildPatcherFixesCount(patcherBuildId);
	}

	/**
	 * Returns the patcherBuildIds of the patcher builds associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherBuildIds of patcher builds associated with the patcher fix
	 */
	public static long[] getPatcherBuildPrimaryKeys(long patcherFixId) {
		return getService().getPatcherBuildPrimaryKeys(patcherFixId);
	}

	/**
	 * Returns the patcher fix with the primary key.
	 *
	 * @param patcherFixId the primary key of the patcher fix
	 * @return the patcher fix
	 * @throws PortalException if a patcher fix with the primary key could not be found
	 */
	public static PatcherFix getPatcherFix(long patcherFixId)
		throws PortalException {

		return getService().getPatcherFix(patcherFixId);
	}

	/**
	 * Returns a range of all the patcher fixes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.patcher.model.impl.PatcherFixModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of patcher fixes
	 * @param end the upper bound of the range of patcher fixes (not inclusive)
	 * @return the range of patcher fixes
	 */
	public static List<PatcherFix> getPatcherFixes(int start, int end) {
		return getService().getPatcherFixes(start, end);
	}

	/**
	 * Returns the number of patcher fixes.
	 *
	 * @return the number of patcher fixes
	 */
	public static int getPatcherFixesCount() {
		return getService().getPatcherFixesCount();
	}

	public static List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId) {

		return getService().getPatcherFixPackPatcherFixes(patcherFixPackId);
	}

	public static List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId, int start, int end) {

		return getService().getPatcherFixPackPatcherFixes(
			patcherFixPackId, start, end);
	}

	public static List<PatcherFix> getPatcherFixPackPatcherFixes(
		long patcherFixPackId, int start, int end,
		OrderByComparator<PatcherFix> orderByComparator) {

		return getService().getPatcherFixPackPatcherFixes(
			patcherFixPackId, start, end, orderByComparator);
	}

	public static int getPatcherFixPackPatcherFixesCount(
		long patcherFixPackId) {

		return getService().getPatcherFixPackPatcherFixesCount(
			patcherFixPackId);
	}

	/**
	 * Returns the patcherFixPackIds of the patcher fix packs associated with the patcher fix.
	 *
	 * @param patcherFixId the patcherFixId of the patcher fix
	 * @return long[] the patcherFixPackIds of patcher fix packs associated with the patcher fix
	 */
	public static long[] getPatcherFixPackPrimaryKeys(long patcherFixId) {
		return getService().getPatcherFixPackPrimaryKeys(patcherFixId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasPatcherBuildPatcherFix(
		long patcherBuildId, long patcherFixId) {

		return getService().hasPatcherBuildPatcherFix(
			patcherBuildId, patcherFixId);
	}

	public static boolean hasPatcherBuildPatcherFixes(long patcherBuildId) {
		return getService().hasPatcherBuildPatcherFixes(patcherBuildId);
	}

	public static boolean hasPatcherFixPackPatcherFix(
		long patcherFixPackId, long patcherFixId) {

		return getService().hasPatcherFixPackPatcherFix(
			patcherFixPackId, patcherFixId);
	}

	public static boolean hasPatcherFixPackPatcherFixes(long patcherFixPackId) {
		return getService().hasPatcherFixPackPatcherFixes(patcherFixPackId);
	}

	public static void setPatcherBuildPatcherFixes(
		long patcherBuildId, long[] patcherFixIds) {

		getService().setPatcherBuildPatcherFixes(patcherBuildId, patcherFixIds);
	}

	public static void setPatcherFixPackPatcherFixes(
		long patcherFixPackId, long[] patcherFixIds) {

		getService().setPatcherFixPackPatcherFixes(
			patcherFixPackId, patcherFixIds);
	}

	/**
	 * Updates the patcher fix in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PatcherFixLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param patcherFix the patcher fix
	 * @return the patcher fix that was updated
	 */
	public static PatcherFix updatePatcherFix(PatcherFix patcherFix) {
		return getService().updatePatcherFix(patcherFix);
	}

	public static PatcherFixLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<PatcherFixLocalService> _serviceSnapshot =
		new Snapshot<>(
			PatcherFixLocalServiceUtil.class, PatcherFixLocalService.class);

}