/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service;

import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ClientExtensionEntryRel. This utility wraps
 * <code>com.liferay.client.extension.service.impl.ClientExtensionEntryRelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ClientExtensionEntryRelLocalService
 * @generated
 */
public class ClientExtensionEntryRelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.client.extension.service.impl.ClientExtensionEntryRelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the client extension entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was added
	 */
	public static ClientExtensionEntryRel addClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return getService().addClientExtensionEntryRel(clientExtensionEntryRel);
	}

	public static ClientExtensionEntryRel addClientExtensionEntryRel(
			long userId, long groupId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addClientExtensionEntryRel(
			userId, groupId, classNameId, classPK, cetExternalReferenceCode,
			type, typeSettings, serviceContext);
	}

	/**
	 * Creates a new client extension entry rel with the primary key. Does not add the client extension entry rel to the database.
	 *
	 * @param clientExtensionEntryRelId the primary key for the new client extension entry rel
	 * @return the new client extension entry rel
	 */
	public static ClientExtensionEntryRel createClientExtensionEntryRel(
		long clientExtensionEntryRelId) {

		return getService().createClientExtensionEntryRel(
			clientExtensionEntryRelId);
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
	 * Deletes the client extension entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was removed
	 */
	public static ClientExtensionEntryRel deleteClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return getService().deleteClientExtensionEntryRel(
			clientExtensionEntryRel);
	}

	/**
	 * Deletes the client extension entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel that was removed
	 * @throws PortalException if a client extension entry rel with the primary key could not be found
	 */
	public static ClientExtensionEntryRel deleteClientExtensionEntryRel(
			long clientExtensionEntryRelId)
		throws PortalException {

		return getService().deleteClientExtensionEntryRel(
			clientExtensionEntryRelId);
	}

	public static void deleteClientExtensionEntryRels(
		long classNameId, long classPK) {

		getService().deleteClientExtensionEntryRels(classNameId, classPK);
	}

	public static void deleteClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		getService().deleteClientExtensionEntryRels(classNameId, classPK, type);
	}

	public static void deleteClientExtensionEntryRels(
		long companyId, String cetExternalReferenceCode) {

		getService().deleteClientExtensionEntryRels(
			companyId, cetExternalReferenceCode);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
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

	public static ClientExtensionEntryRel fetchClientExtensionEntryRel(
		long clientExtensionEntryRelId) {

		return getService().fetchClientExtensionEntryRel(
			clientExtensionEntryRelId);
	}

	public static ClientExtensionEntryRel fetchClientExtensionEntryRel(
		long classNameId, long classPK, String type) {

		return getService().fetchClientExtensionEntryRel(
			classNameId, classPK, type);
	}

	public static ClientExtensionEntryRel
		fetchClientExtensionEntryRelByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchClientExtensionEntryRelByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the client extension entry rel matching the UUID and group.
	 *
	 * @param uuid the client extension entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	public static ClientExtensionEntryRel
		fetchClientExtensionEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchClientExtensionEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the client extension entry rel with the primary key.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel
	 * @throws PortalException if a client extension entry rel with the primary key could not be found
	 */
	public static ClientExtensionEntryRel getClientExtensionEntryRel(
			long clientExtensionEntryRelId)
		throws PortalException {

		return getService().getClientExtensionEntryRel(
			clientExtensionEntryRelId);
	}

	public static ClientExtensionEntryRel
			getClientExtensionEntryRelByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getClientExtensionEntryRelByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the client extension entry rel matching the UUID and group.
	 *
	 * @param uuid the client extension entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching client extension entry rel
	 * @throws PortalException if a matching client extension entry rel could not be found
	 */
	public static ClientExtensionEntryRel
			getClientExtensionEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getClientExtensionEntryRelByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the client extension entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.client.extension.model.impl.ClientExtensionEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @return the range of client extension entry rels
	 */
	public static List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		int start, int end) {

		return getService().getClientExtensionEntryRels(start, end);
	}

	public static List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK) {

		return getService().getClientExtensionEntryRels(classNameId, classPK);
	}

	public static List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		return getService().getClientExtensionEntryRels(
			classNameId, classPK, type);
	}

	public static List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		String type) {

		return getService().getClientExtensionEntryRels(type);
	}

	/**
	 * Returns all the client extension entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the client extension entry rels
	 * @param companyId the primary key of the company
	 * @return the matching client extension entry rels, or an empty list if no matches were found
	 */
	public static List<ClientExtensionEntryRel>
		getClientExtensionEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getClientExtensionEntryRelsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of client extension entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the client extension entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of client extension entry rels
	 * @param end the upper bound of the range of client extension entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching client extension entry rels, or an empty list if no matches were found
	 */
	public static List<ClientExtensionEntryRel>
		getClientExtensionEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<ClientExtensionEntryRel> orderByComparator) {

		return getService().getClientExtensionEntryRelsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of client extension entry rels.
	 *
	 * @return the number of client extension entry rels
	 */
	public static int getClientExtensionEntryRelsCount() {
		return getService().getClientExtensionEntryRelsCount();
	}

	public static int getClientExtensionEntryRelsCount(
		long classNameId, long classPK, String type) {

		return getService().getClientExtensionEntryRelsCount(
			classNameId, classPK, type);
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
	 * Updates the client extension entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ClientExtensionEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param clientExtensionEntryRel the client extension entry rel
	 * @return the client extension entry rel that was updated
	 */
	public static ClientExtensionEntryRel updateClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return getService().updateClientExtensionEntryRel(
			clientExtensionEntryRel);
	}

	public static ClientExtensionEntryRel updateClientExtensionEntryRel(
			long clientExtensionEntryRelId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings)
		throws PortalException {

		return getService().updateClientExtensionEntryRel(
			clientExtensionEntryRelId, classNameId, classPK,
			cetExternalReferenceCode, type, typeSettings);
	}

	public static ClientExtensionEntryRelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ClientExtensionEntryRelLocalService>
		_serviceSnapshot = new Snapshot<>(
			ClientExtensionEntryRelLocalServiceUtil.class,
			ClientExtensionEntryRelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-688586136