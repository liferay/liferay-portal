/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service;

import com.liferay.audiences.model.AudiencesEntryGroupRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for AudiencesEntryGroupRel. This utility wraps
 * <code>com.liferay.audiences.service.impl.AudiencesEntryGroupRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRelLocalService
 * @generated
 */
public class AudiencesEntryGroupRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.audiences.service.impl.AudiencesEntryGroupRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the audiences entry group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was added
	 */
	public static AudiencesEntryGroupRel addAudiencesEntryGroupRel(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return getService().addAudiencesEntryGroupRel(audiencesEntryGroupRel);
	}

	/**
	 * Creates a new audiences entry group rel with the primary key. Does not add the audiences entry group rel to the database.
	 *
	 * @param audiencesEntryGroupRelId the primary key for the new audiences entry group rel
	 * @return the new audiences entry group rel
	 */
	public static AudiencesEntryGroupRel createAudiencesEntryGroupRel(
		long audiencesEntryGroupRelId) {

		return getService().createAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
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
	 * Deletes the audiences entry group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 */
	public static AudiencesEntryGroupRel deleteAudiencesEntryGroupRel(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return getService().deleteAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	/**
	 * Deletes the audiences entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel that was removed
	 * @throws PortalException if a audiences entry group rel with the primary key could not be found
	 */
	public static AudiencesEntryGroupRel deleteAudiencesEntryGroupRel(
			long audiencesEntryGroupRelId)
		throws PortalException {

		return getService().deleteAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	public static void deleteAudiencesEntryGroupRelsByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		getService().deleteAudiencesEntryGroupRelsByAudienceEntryERC(
			companyId, audienceEntryERC);
	}

	public static void deleteAudiencesEntryGroupRelsByGroupERC(
		long companyId, String groupERC) {

		getService().deleteAudiencesEntryGroupRelsByGroupERC(
			companyId, groupERC);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
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

	public static AudiencesEntryGroupRel fetchAudiencesEntryGroupRel(
		long audiencesEntryGroupRelId) {

		return getService().fetchAudiencesEntryGroupRel(
			audiencesEntryGroupRelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the audiences entry group rel with the primary key.
	 *
	 * @param audiencesEntryGroupRelId the primary key of the audiences entry group rel
	 * @return the audiences entry group rel
	 * @throws PortalException if a audiences entry group rel with the primary key could not be found
	 */
	public static AudiencesEntryGroupRel getAudiencesEntryGroupRel(
			long audiencesEntryGroupRelId)
		throws PortalException {

		return getService().getAudiencesEntryGroupRel(audiencesEntryGroupRelId);
	}

	/**
	 * Returns a range of all the audiences entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.audiences.model.impl.AudiencesEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of audiences entry group rels
	 * @param end the upper bound of the range of audiences entry group rels (not inclusive)
	 * @return the range of audiences entry group rels
	 */
	public static List<AudiencesEntryGroupRel> getAudiencesEntryGroupRels(
		int start, int end) {

		return getService().getAudiencesEntryGroupRels(start, end);
	}

	/**
	 * Returns the number of audiences entry group rels.
	 *
	 * @return the number of audiences entry group rels
	 */
	public static int getAudiencesEntryGroupRelsCount() {
		return getService().getAudiencesEntryGroupRelsCount();
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
	 * Updates the audiences entry group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AudiencesEntryGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param audiencesEntryGroupRel the audiences entry group rel
	 * @return the audiences entry group rel that was updated
	 */
	public static AudiencesEntryGroupRel updateAudiencesEntryGroupRel(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return getService().updateAudiencesEntryGroupRel(
			audiencesEntryGroupRel);
	}

	public static AudiencesEntryGroupRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<AudiencesEntryGroupRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			AudiencesEntryGroupRelLocalServiceUtil.class,
			AudiencesEntryGroupRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-83071795