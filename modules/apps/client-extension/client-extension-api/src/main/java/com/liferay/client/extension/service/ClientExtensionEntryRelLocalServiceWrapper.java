/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.service;

import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link ClientExtensionEntryRelLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ClientExtensionEntryRelLocalService
 * @generated
 */
public class ClientExtensionEntryRelLocalServiceWrapper
	implements ClientExtensionEntryRelLocalService,
			   ServiceWrapper<ClientExtensionEntryRelLocalService> {

	public ClientExtensionEntryRelLocalServiceWrapper() {
		this(null);
	}

	public ClientExtensionEntryRelLocalServiceWrapper(
		ClientExtensionEntryRelLocalService
			clientExtensionEntryRelLocalService) {

		_clientExtensionEntryRelLocalService =
			clientExtensionEntryRelLocalService;
	}

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
	@Override
	public ClientExtensionEntryRel addClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			clientExtensionEntryRel);
	}

	@Override
	public ClientExtensionEntryRel addClientExtensionEntryRel(
			long userId, long groupId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.addClientExtensionEntryRel(
			userId, groupId, classNameId, classPK, cetExternalReferenceCode,
			type, typeSettings, serviceContext);
	}

	/**
	 * Creates a new client extension entry rel with the primary key. Does not add the client extension entry rel to the database.
	 *
	 * @param clientExtensionEntryRelId the primary key for the new client extension entry rel
	 * @return the new client extension entry rel
	 */
	@Override
	public ClientExtensionEntryRel createClientExtensionEntryRel(
		long clientExtensionEntryRelId) {

		return _clientExtensionEntryRelLocalService.
			createClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public ClientExtensionEntryRel deleteClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.
			deleteClientExtensionEntryRel(clientExtensionEntryRel);
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
	@Override
	public ClientExtensionEntryRel deleteClientExtensionEntryRel(
			long clientExtensionEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			deleteClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	@Override
	public void deleteClientExtensionEntryRels(long classNameId, long classPK) {
		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			classNameId, classPK);
	}

	@Override
	public void deleteClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			classNameId, classPK, type);
	}

	@Override
	public void deleteClientExtensionEntryRels(
		long companyId, String cetExternalReferenceCode) {

		_clientExtensionEntryRelLocalService.deleteClientExtensionEntryRels(
			companyId, cetExternalReferenceCode);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _clientExtensionEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _clientExtensionEntryRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _clientExtensionEntryRelLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _clientExtensionEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _clientExtensionEntryRelLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _clientExtensionEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public ClientExtensionEntryRel fetchClientExtensionEntryRel(
		long clientExtensionEntryRelId) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRel(clientExtensionEntryRelId);
	}

	@Override
	public ClientExtensionEntryRel fetchClientExtensionEntryRel(
		long classNameId, long classPK, String type) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRel(classNameId, classPK, type);
	}

	@Override
	public ClientExtensionEntryRel
		fetchClientExtensionEntryRelByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRelByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the client extension entry rel matching the UUID and group.
	 *
	 * @param uuid the client extension entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching client extension entry rel, or <code>null</code> if a matching client extension entry rel could not be found
	 */
	@Override
	public ClientExtensionEntryRel fetchClientExtensionEntryRelByUuidAndGroupId(
		String uuid, long groupId) {

		return _clientExtensionEntryRelLocalService.
			fetchClientExtensionEntryRelByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _clientExtensionEntryRelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the client extension entry rel with the primary key.
	 *
	 * @param clientExtensionEntryRelId the primary key of the client extension entry rel
	 * @return the client extension entry rel
	 * @throws PortalException if a client extension entry rel with the primary key could not be found
	 */
	@Override
	public ClientExtensionEntryRel getClientExtensionEntryRel(
			long clientExtensionEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRel(
			clientExtensionEntryRelId);
	}

	@Override
	public ClientExtensionEntryRel
			getClientExtensionEntryRelByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelByExternalReferenceCode(
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
	@Override
	public ClientExtensionEntryRel getClientExtensionEntryRelByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		int start, int end) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			start, end);
	}

	@Override
	public java.util.List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			classNameId, classPK);
	}

	@Override
	public java.util.List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		long classNameId, long classPK, String type) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			classNameId, classPK, type);
	}

	@Override
	public java.util.List<ClientExtensionEntryRel> getClientExtensionEntryRels(
		String type) {

		return _clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
			type);
	}

	/**
	 * Returns all the client extension entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the client extension entry rels
	 * @param companyId the primary key of the company
	 * @return the matching client extension entry rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<ClientExtensionEntryRel>
		getClientExtensionEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<ClientExtensionEntryRel>
		getClientExtensionEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<ClientExtensionEntryRel> orderByComparator) {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of client extension entry rels.
	 *
	 * @return the number of client extension entry rels
	 */
	@Override
	public int getClientExtensionEntryRelsCount() {
		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsCount();
	}

	@Override
	public int getClientExtensionEntryRelsCount(
		long classNameId, long classPK, String type) {

		return _clientExtensionEntryRelLocalService.
			getClientExtensionEntryRelsCount(classNameId, classPK, type);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _clientExtensionEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _clientExtensionEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _clientExtensionEntryRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
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
	@Override
	public ClientExtensionEntryRel updateClientExtensionEntryRel(
		ClientExtensionEntryRel clientExtensionEntryRel) {

		return _clientExtensionEntryRelLocalService.
			updateClientExtensionEntryRel(clientExtensionEntryRel);
	}

	@Override
	public ClientExtensionEntryRel updateClientExtensionEntryRel(
			long clientExtensionEntryRelId, long classNameId, long classPK,
			String cetExternalReferenceCode, String type, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _clientExtensionEntryRelLocalService.
			updateClientExtensionEntryRel(
				clientExtensionEntryRelId, classNameId, classPK,
				cetExternalReferenceCode, type, typeSettings);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _clientExtensionEntryRelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<ClientExtensionEntryRel> getCTPersistence() {
		return _clientExtensionEntryRelLocalService.getCTPersistence();
	}

	@Override
	public Class<ClientExtensionEntryRel> getModelClass() {
		return _clientExtensionEntryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ClientExtensionEntryRel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _clientExtensionEntryRelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public ClientExtensionEntryRelLocalService getWrappedService() {
		return _clientExtensionEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		ClientExtensionEntryRelLocalService
			clientExtensionEntryRelLocalService) {

		_clientExtensionEntryRelLocalService =
			clientExtensionEntryRelLocalService;
	}

	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:126017538