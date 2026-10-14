/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service;

import com.liferay.data.engine.model.DEDataDefinitionFieldLink;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DEDataDefinitionFieldLink. This utility wraps
 * <code>com.liferay.data.engine.service.impl.DEDataDefinitionFieldLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DEDataDefinitionFieldLinkLocalService
 * @generated
 */
public class DEDataDefinitionFieldLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.data.engine.service.impl.DEDataDefinitionFieldLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the de data definition field link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was added
	 */
	public static DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return getService().addDEDataDefinitionFieldLink(
			deDataDefinitionFieldLink);
	}

	public static DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName)
		throws PortalException {

		return getService().addDEDataDefinitionFieldLink(
			groupId, classNameId, classPK, ddmStructureId, fieldName);
	}

	public static DEDataDefinitionFieldLink addDEDataDefinitionFieldLink(
			long groupId, long classNameId, long classPK, long ddmStructureId,
			String fieldName,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDEDataDefinitionFieldLink(
			groupId, classNameId, classPK, ddmStructureId, fieldName,
			serviceContext);
	}

	/**
	 * Creates a new de data definition field link with the primary key. Does not add the de data definition field link to the database.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key for the new de data definition field link
	 * @return the new de data definition field link
	 */
	public static DEDataDefinitionFieldLink createDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId) {

		return getService().createDEDataDefinitionFieldLink(
			deDataDefinitionFieldLinkId);
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
	 * Deletes the de data definition field link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was removed
	 */
	public static DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return getService().deleteDEDataDefinitionFieldLink(
			deDataDefinitionFieldLink);
	}

	/**
	 * Deletes the de data definition field link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link that was removed
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	public static DEDataDefinitionFieldLink deleteDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws PortalException {

		return getService().deleteDEDataDefinitionFieldLink(
			deDataDefinitionFieldLinkId);
	}

	public static void deleteDEDataDefinitionFieldLinks(long ddmStructureId) {
		getService().deleteDEDataDefinitionFieldLinks(ddmStructureId);
	}

	public static void deleteDEDataDefinitionFieldLinks(
		long classNameId, long classPK) {

		getService().deleteDEDataDefinitionFieldLinks(classNameId, classPK);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#deleteDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	public static void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName) {

		getService().deleteDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldName);
	}

	public static void deleteDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		getService().deleteDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldNames);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
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

	public static DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLink(
		long deDataDefinitionFieldLinkId) {

		return getService().fetchDEDataDefinitionFieldLink(
			deDataDefinitionFieldLinkId);
	}

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link, or <code>null</code> if a matching de data definition field link could not be found
	 */
	public static DEDataDefinitionFieldLink
		fetchDEDataDefinitionFieldLinkByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchDEDataDefinitionFieldLinkByUuidAndGroupId(
			uuid, groupId);
	}

	public static DEDataDefinitionFieldLink fetchDEDataDefinitionFieldLinks(
		long classNameId, long classPK, long ddmStructureId, String fieldName) {

		return getService().fetchDEDataDefinitionFieldLinks(
			classNameId, classPK, ddmStructureId, fieldName);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<Long> getClassPKS(
		long classNameId, long ddmStructureId) {

		return getService().getClassPKS(classNameId, ddmStructureId);
	}

	/**
	 * Returns the de data definition field link with the primary key.
	 *
	 * @param deDataDefinitionFieldLinkId the primary key of the de data definition field link
	 * @return the de data definition field link
	 * @throws PortalException if a de data definition field link with the primary key could not be found
	 */
	public static DEDataDefinitionFieldLink getDEDataDefinitionFieldLink(
			long deDataDefinitionFieldLinkId)
		throws PortalException {

		return getService().getDEDataDefinitionFieldLink(
			deDataDefinitionFieldLinkId);
	}

	/**
	 * Returns the de data definition field link matching the UUID and group.
	 *
	 * @param uuid the de data definition field link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data definition field link
	 * @throws PortalException if a matching de data definition field link could not be found
	 */
	public static DEDataDefinitionFieldLink
			getDEDataDefinitionFieldLinkByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getDEDataDefinitionFieldLinkByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the de data definition field links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataDefinitionFieldLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @return the range of de data definition field links
	 */
	public static List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		int start, int end) {

		return getService().getDEDataDefinitionFieldLinks(start, end);
	}

	public static List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId) {

		return getService().getDEDataDefinitionFieldLinks(ddmStructureId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 DEDataDefinitionFieldLinkLocalServiceImpl#getDEDataDefinitionFieldLinks(
	 long, long, String[])}
	 */
	@Deprecated
	public static List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String fieldName) {

		return getService().getDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldName);
	}

	public static List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long classNameId, long ddmStructureId, String[] fieldNames) {

		return getService().getDEDataDefinitionFieldLinks(
			classNameId, ddmStructureId, fieldNames);
	}

	public static List<DEDataDefinitionFieldLink> getDEDataDefinitionFieldLinks(
		long ddmStructureId, String[] fieldNames) {

		return getService().getDEDataDefinitionFieldLinks(
			ddmStructureId, fieldNames);
	}

	public static List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
			long classNameId, long classPK) {

		return getService().
			getDEDataDefinitionFieldLinksByClassNameIdAndClassPK(
				classNameId, classPK);
	}

	/**
	 * Returns all the de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @return the matching de data definition field links, or an empty list if no matches were found
	 */
	public static List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of de data definition field links matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data definition field links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of de data definition field links
	 * @param end the upper bound of the range of de data definition field links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching de data definition field links, or an empty list if no matches were found
	 */
	public static List<DEDataDefinitionFieldLink>
		getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DEDataDefinitionFieldLink> orderByComparator) {

		return getService().getDEDataDefinitionFieldLinksByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of de data definition field links.
	 *
	 * @return the number of de data definition field links
	 */
	public static int getDEDataDefinitionFieldLinksCount() {
		return getService().getDEDataDefinitionFieldLinksCount();
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

	/**
	 * Updates the de data definition field link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataDefinitionFieldLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataDefinitionFieldLink the de data definition field link
	 * @return the de data definition field link that was updated
	 */
	public static DEDataDefinitionFieldLink updateDEDataDefinitionFieldLink(
		DEDataDefinitionFieldLink deDataDefinitionFieldLink) {

		return getService().updateDEDataDefinitionFieldLink(
			deDataDefinitionFieldLink);
	}

	public static DEDataDefinitionFieldLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DEDataDefinitionFieldLinkLocalService>
		_serviceSnapshot = new Snapshot<>(
			DEDataDefinitionFieldLinkLocalServiceUtil.class,
			DEDataDefinitionFieldLinkLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:277304681