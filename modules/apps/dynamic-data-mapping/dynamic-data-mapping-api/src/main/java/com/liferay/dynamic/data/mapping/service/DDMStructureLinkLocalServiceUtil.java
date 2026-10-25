/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructureLink;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DDMStructureLink. This utility wraps
 * <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureLinkLocalService
 * @generated
 */
public class DDMStructureLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMStructureLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the ddm structure link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was added
	 */
	public static DDMStructureLink addDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return getService().addDDMStructureLink(ddmStructureLink);
	}

	public static DDMStructureLink addStructureLink(
		long classNameId, long classPK, long structureId) {

		return getService().addStructureLink(classNameId, classPK, structureId);
	}

	/**
	 * Creates a new ddm structure link with the primary key. Does not add the ddm structure link to the database.
	 *
	 * @param structureLinkId the primary key for the new ddm structure link
	 * @return the new ddm structure link
	 */
	public static DDMStructureLink createDDMStructureLink(
		long structureLinkId) {

		return getService().createDDMStructureLink(structureLinkId);
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
	 * Deletes the ddm structure link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was removed
	 */
	public static DDMStructureLink deleteDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return getService().deleteDDMStructureLink(ddmStructureLink);
	}

	/**
	 * Deletes the ddm structure link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link that was removed
	 * @throws PortalException if a ddm structure link with the primary key could not be found
	 */
	public static DDMStructureLink deleteDDMStructureLink(long structureLinkId)
		throws PortalException {

		return getService().deleteDDMStructureLink(structureLinkId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteStructureLink(DDMStructureLink structureLink) {
		getService().deleteStructureLink(structureLink);
	}

	public static void deleteStructureLink(long structureLinkId)
		throws PortalException {

		getService().deleteStructureLink(structureLinkId);
	}

	public static void deleteStructureLink(
			long classNameId, long classPK, long structureId)
		throws PortalException {

		getService().deleteStructureLink(classNameId, classPK, structureId);
	}

	public static void deleteStructureLinks(long classNameId, long classPK) {
		getService().deleteStructureLinks(classNameId, classPK);
	}

	public static void deleteStructureStructureLinks(long structureId) {
		getService().deleteStructureStructureLinks(structureId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
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

	public static DDMStructureLink fetchDDMStructureLink(long structureLinkId) {
		return getService().fetchDDMStructureLink(structureLinkId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static List<DDMStructureLink> getClassNameStructureLinks(
		long classNameId) {

		return getService().getClassNameStructureLinks(classNameId);
	}

	/**
	 * Returns the ddm structure link with the primary key.
	 *
	 * @param structureLinkId the primary key of the ddm structure link
	 * @return the ddm structure link
	 * @throws PortalException if a ddm structure link with the primary key could not be found
	 */
	public static DDMStructureLink getDDMStructureLink(long structureLinkId)
		throws PortalException {

		return getService().getDDMStructureLink(structureLinkId);
	}

	/**
	 * Returns a range of all the ddm structure links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure links
	 * @param end the upper bound of the range of ddm structure links (not inclusive)
	 * @return the range of ddm structure links
	 */
	public static List<DDMStructureLink> getDDMStructureLinks(
		int start, int end) {

		return getService().getDDMStructureLinks(start, end);
	}

	/**
	 * Returns the number of ddm structure links.
	 *
	 * @return the number of ddm structure links
	 */
	public static int getDDMStructureLinksCount() {
		return getService().getDDMStructureLinksCount();
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

	public static DDMStructureLink getStructureLink(long structureLinkId)
		throws PortalException {

		return getService().getStructureLink(structureLinkId);
	}

	public static List<DDMStructureLink> getStructureLinks(long structureId) {
		return getService().getStructureLinks(structureId);
	}

	public static List<DDMStructureLink> getStructureLinks(
		long structureId, int start, int end) {

		return getService().getStructureLinks(structureId, start, end);
	}

	public static List<DDMStructureLink> getStructureLinks(
		long classNameId, long classPK) {

		return getService().getStructureLinks(classNameId, classPK);
	}

	public static List<DDMStructureLink> getStructureLinks(
		long classNameId, long classPK, int start, int end) {

		return getService().getStructureLinks(classNameId, classPK, start, end);
	}

	public static int getStructureLinksCount(long classNameId, long classPK) {
		return getService().getStructureLinksCount(classNameId, classPK);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getStructureLinkStructures(long classNameId, long classPK)
		throws PortalException {

		return getService().getStructureLinkStructures(classNameId, classPK);
	}

	public static List<com.liferay.dynamic.data.mapping.model.DDMStructure>
			getStructureLinkStructures(
				long classNameId, long classPK, int start, int end)
		throws PortalException {

		return getService().getStructureLinkStructures(
			classNameId, classPK, start, end);
	}

	public static DDMStructureLink getUniqueStructureLink(
			long classNameId, long classPK)
		throws PortalException {

		return getService().getUniqueStructureLink(classNameId, classPK);
	}

	/**
	 * Updates the ddm structure link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureLink the ddm structure link
	 * @return the ddm structure link that was updated
	 */
	public static DDMStructureLink updateDDMStructureLink(
		DDMStructureLink ddmStructureLink) {

		return getService().updateDDMStructureLink(ddmStructureLink);
	}

	public static DDMStructureLink updateStructureLink(
			long structureLinkId, long classNameId, long classPK,
			long structureId)
		throws PortalException {

		return getService().updateStructureLink(
			structureLinkId, classNameId, classPK, structureId);
	}

	public static DDMStructureLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDMStructureLinkLocalService>
		_serviceSnapshot = new Snapshot<>(
			DDMStructureLinkLocalServiceUtil.class,
			DDMStructureLinkLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:526579141