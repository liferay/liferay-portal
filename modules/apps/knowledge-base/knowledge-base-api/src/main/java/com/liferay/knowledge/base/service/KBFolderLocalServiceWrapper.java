/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link KBFolderLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KBFolderLocalService
 * @generated
 */
public class KBFolderLocalServiceWrapper
	implements KBFolderLocalService, ServiceWrapper<KBFolderLocalService> {

	public KBFolderLocalServiceWrapper() {
		this(null);
	}

	public KBFolderLocalServiceWrapper(
		KBFolderLocalService kbFolderLocalService) {

		_kbFolderLocalService = kbFolderLocalService;
	}

	/**
	 * Adds the kb folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbFolder the kb folder
	 * @return the kb folder that was added
	 */
	@Override
	public KBFolder addKBFolder(KBFolder kbFolder) {
		return _kbFolderLocalService.addKBFolder(kbFolder);
	}

	@Override
	public KBFolder addKBFolder(
			String externalReferenceCode, long userId, long groupId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.addKBFolder(
			externalReferenceCode, userId, groupId, parentResourceClassNameId,
			parentResourcePrimKey, name, description, serviceContext);
	}

	/**
	 * Creates a new kb folder with the primary key. Does not add the kb folder to the database.
	 *
	 * @param kbFolderId the primary key for the new kb folder
	 * @return the new kb folder
	 */
	@Override
	public KBFolder createKBFolder(long kbFolderId) {
		return _kbFolderLocalService.createKBFolder(kbFolderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the kb folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbFolder the kb folder
	 * @return the kb folder that was removed
	 * @throws PortalException
	 */
	@Override
	public KBFolder deleteKBFolder(KBFolder kbFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.deleteKBFolder(kbFolder);
	}

	@Override
	public KBFolder deleteKBFolder(
			KBFolder kbFolder, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.deleteKBFolder(
			kbFolder, includeTrashedEntries);
	}

	/**
	 * Deletes the kb folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder that was removed
	 * @throws PortalException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder deleteKBFolder(long kbFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.deleteKBFolder(kbFolderId);
	}

	@Override
	public KBFolder deleteKBFolder(
			long kbFolderId, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.deleteKBFolder(
			kbFolderId, includeTrashedEntries);
	}

	@Override
	public void deleteKBFolders(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kbFolderLocalService.deleteKBFolders(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kbFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kbFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kbFolderLocalService.dynamicQuery();
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

		return _kbFolderLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBFolderModelImpl</code>.
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

		return _kbFolderLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBFolderModelImpl</code>.
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

		return _kbFolderLocalService.dynamicQuery(
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

		return _kbFolderLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kbFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KBFolder fetchFirstChildKBFolder(long groupId, long kbFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.fetchFirstChildKBFolder(
			groupId, kbFolderId);
	}

	@Override
	public KBFolder fetchFirstChildKBFolder(
			long groupId, long kbFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<KBFolder>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.fetchFirstChildKBFolder(
			groupId, kbFolderId, orderByComparator);
	}

	@Override
	public KBFolder fetchKBFolder(long kbFolderId) {
		return _kbFolderLocalService.fetchKBFolder(kbFolderId);
	}

	@Override
	public KBFolder fetchKBFolder(String uuid, long groupId) {
		return _kbFolderLocalService.fetchKBFolder(uuid, groupId);
	}

	@Override
	public KBFolder fetchKBFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _kbFolderLocalService.fetchKBFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public KBFolder fetchKBFolderByUrlTitle(
			long groupId, long parentKbFolderId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.fetchKBFolderByUrlTitle(
			groupId, parentKbFolderId, urlTitle);
	}

	/**
	 * Returns the kb folder matching the UUID and group.
	 *
	 * @param uuid the kb folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb folder, or <code>null</code> if a matching kb folder could not be found
	 */
	@Override
	public KBFolder fetchKBFolderByUuidAndGroupId(String uuid, long groupId) {
		return _kbFolderLocalService.fetchKBFolderByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kbFolderLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _kbFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kbFolderLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kb folder with the primary key.
	 *
	 * @param kbFolderId the primary key of the kb folder
	 * @return the kb folder
	 * @throws PortalException if a kb folder with the primary key could not be found
	 */
	@Override
	public KBFolder getKBFolder(long kbFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFolder(kbFolderId);
	}

	@Override
	public KBFolder getKBFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public KBFolder getKBFolderByUrlTitle(
			long groupId, long parentKbFolderId, String urlTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFolderByUrlTitle(
			groupId, parentKbFolderId, urlTitle);
	}

	/**
	 * Returns the kb folder matching the UUID and group.
	 *
	 * @param uuid the kb folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb folder
	 * @throws PortalException if a matching kb folder could not be found
	 */
	@Override
	public KBFolder getKBFolderByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the kb folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @return the range of kb folders
	 */
	@Override
	public java.util.List<KBFolder> getKBFolders(int start, int end) {
		return _kbFolderLocalService.getKBFolders(start, end);
	}

	@Override
	public java.util.List<KBFolder> getKBFolders(
			long groupId, long parentKBFolderId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFolders(
			groupId, parentKBFolderId, start, end);
	}

	@Override
	public java.util.List<Object> getKBFoldersAndKBArticles(
		long groupId, long parentResourcePrimKey, int status) {

		return _kbFolderLocalService.getKBFoldersAndKBArticles(
			groupId, parentResourcePrimKey, status);
	}

	@Override
	public java.util.List<Object> getKBFoldersAndKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<?> orderByComparator) {

		return _kbFolderLocalService.getKBFoldersAndKBArticles(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	@Override
	public int getKBFoldersAndKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		return _kbFolderLocalService.getKBFoldersAndKBArticlesCount(
			groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns all the kb folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb folders
	 * @param companyId the primary key of the company
	 * @return the matching kb folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBFolder> getKBFoldersByUuidAndCompanyId(
		String uuid, long companyId) {

		return _kbFolderLocalService.getKBFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of kb folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kb folders
	 * @param end the upper bound of the range of kb folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kb folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<KBFolder> getKBFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KBFolder>
			orderByComparator) {

		return _kbFolderLocalService.getKBFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb folders.
	 *
	 * @return the number of kb folders
	 */
	@Override
	public int getKBFoldersCount() {
		return _kbFolderLocalService.getKBFoldersCount();
	}

	@Override
	public int getKBFoldersCount(long groupId, long parentKBFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFoldersCount(
			groupId, parentKBFolderId);
	}

	@Override
	public int getKBFoldersCount(
			long groupId, long parentKBFolderId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getKBFoldersCount(
			groupId, parentKBFolderId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kbFolderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public KBFolder moveKBFolder(long kbFolderId, long parentKBFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.moveKBFolder(kbFolderId, parentKBFolderId);
	}

	@Override
	public KBFolder moveKBFolderFromTrash(
			long userId, long kbFolderId, long parentKBFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.moveKBFolderFromTrash(
			userId, kbFolderId, parentKBFolderId);
	}

	@Override
	public KBFolder moveKBFolderToTrash(long userId, long kbFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.moveKBFolderToTrash(userId, kbFolderId);
	}

	@Override
	public KBFolder restoreKBFolderFromTrash(long userId, long kbFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.restoreKBFolderFromTrash(
			userId, kbFolderId);
	}

	/**
	 * Updates the kb folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbFolder the kb folder
	 * @return the kb folder that was updated
	 */
	@Override
	public KBFolder updateKBFolder(KBFolder kbFolder) {
		return _kbFolderLocalService.updateKBFolder(kbFolder);
	}

	@Override
	public KBFolder updateKBFolder(
			long parentResourceClassNameId, long parentResourcePrimKey,
			long kbFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.updateKBFolder(
			parentResourceClassNameId, parentResourcePrimKey, kbFolderId, name,
			description, serviceContext);
	}

	@Override
	public KBFolder updateStatus(long userId, KBFolder kbFolder, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kbFolderLocalService.updateStatus(userId, kbFolder, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kbFolderLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KBFolder> getCTPersistence() {
		return _kbFolderLocalService.getCTPersistence();
	}

	@Override
	public Class<KBFolder> getModelClass() {
		return _kbFolderLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KBFolder>, R, E> updateUnsafeFunction)
		throws E {

		return _kbFolderLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KBFolderLocalService getWrappedService() {
		return _kbFolderLocalService;
	}

	@Override
	public void setWrappedService(KBFolderLocalService kbFolderLocalService) {
		_kbFolderLocalService = kbFolderLocalService;
	}

	private KBFolderLocalService _kbFolderLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1398529210