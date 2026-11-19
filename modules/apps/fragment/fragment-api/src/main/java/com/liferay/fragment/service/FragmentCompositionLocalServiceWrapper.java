/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentComposition;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link FragmentCompositionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCompositionLocalService
 * @generated
 */
public class FragmentCompositionLocalServiceWrapper
	implements FragmentCompositionLocalService,
			   ServiceWrapper<FragmentCompositionLocalService> {

	public FragmentCompositionLocalServiceWrapper() {
		this(null);
	}

	public FragmentCompositionLocalServiceWrapper(
		FragmentCompositionLocalService fragmentCompositionLocalService) {

		_fragmentCompositionLocalService = fragmentCompositionLocalService;
	}

	/**
	 * Adds the fragment composition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was added
	 */
	@Override
	public FragmentComposition addFragmentComposition(
		FragmentComposition fragmentComposition) {

		return _fragmentCompositionLocalService.addFragmentComposition(
			fragmentComposition);
	}

	@Override
	public FragmentComposition addFragmentComposition(
			String externalReferenceCode, long userId, long groupId,
			long fragmentCollectionId, String fragmentCompositionKey,
			String name, String description, String data,
			long previewFileEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.addFragmentComposition(
			externalReferenceCode, userId, groupId, fragmentCollectionId,
			fragmentCompositionKey, name, description, data, previewFileEntryId,
			status, serviceContext);
	}

	/**
	 * Creates a new fragment composition with the primary key. Does not add the fragment composition to the database.
	 *
	 * @param fragmentCompositionId the primary key for the new fragment composition
	 * @return the new fragment composition
	 */
	@Override
	public FragmentComposition createFragmentComposition(
		long fragmentCompositionId) {

		return _fragmentCompositionLocalService.createFragmentComposition(
			fragmentCompositionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the fragment composition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was removed
	 * @throws PortalException
	 */
	@Override
	public FragmentComposition deleteFragmentComposition(
			FragmentComposition fragmentComposition)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.deleteFragmentComposition(
			fragmentComposition);
	}

	/**
	 * Deletes the fragment composition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition that was removed
	 * @throws PortalException if a fragment composition with the primary key could not be found
	 */
	@Override
	public FragmentComposition deleteFragmentComposition(
			long fragmentCompositionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.deleteFragmentComposition(
			fragmentCompositionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _fragmentCompositionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _fragmentCompositionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _fragmentCompositionLocalService.dynamicQuery();
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

		return _fragmentCompositionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
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

		return _fragmentCompositionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
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

		return _fragmentCompositionLocalService.dynamicQuery(
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

		return _fragmentCompositionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _fragmentCompositionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public FragmentComposition fetchFragmentComposition(
		long fragmentCompositionId) {

		return _fragmentCompositionLocalService.fetchFragmentComposition(
			fragmentCompositionId);
	}

	@Override
	public FragmentComposition fetchFragmentComposition(
		long groupId, String fragmentCompositionKey) {

		return _fragmentCompositionLocalService.fetchFragmentComposition(
			groupId, fragmentCompositionKey);
	}

	@Override
	public FragmentComposition fetchFragmentCompositionByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _fragmentCompositionLocalService.
			fetchFragmentCompositionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment composition matching the UUID and group.
	 *
	 * @param uuid the fragment composition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment composition, or <code>null</code> if a matching fragment composition could not be found
	 */
	@Override
	public FragmentComposition fetchFragmentCompositionByUuidAndGroupId(
		String uuid, long groupId) {

		return _fragmentCompositionLocalService.
			fetchFragmentCompositionByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public String generateFragmentCompositionKey(long groupId, String name) {
		return _fragmentCompositionLocalService.generateFragmentCompositionKey(
			groupId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _fragmentCompositionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _fragmentCompositionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns the fragment composition with the primary key.
	 *
	 * @param fragmentCompositionId the primary key of the fragment composition
	 * @return the fragment composition
	 * @throws PortalException if a fragment composition with the primary key could not be found
	 */
	@Override
	public FragmentComposition getFragmentComposition(
			long fragmentCompositionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.getFragmentComposition(
			fragmentCompositionId);
	}

	@Override
	public FragmentComposition getFragmentCompositionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.
			getFragmentCompositionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment composition matching the UUID and group.
	 *
	 * @param uuid the fragment composition's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment composition
	 * @throws PortalException if a matching fragment composition could not be found
	 */
	@Override
	public FragmentComposition getFragmentCompositionByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.
			getFragmentCompositionByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the fragment compositions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentCompositionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @return the range of fragment compositions
	 */
	@Override
	public java.util.List<FragmentComposition> getFragmentCompositions(
		int start, int end) {

		return _fragmentCompositionLocalService.getFragmentCompositions(
			start, end);
	}

	@Override
	public java.util.List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId) {

		return _fragmentCompositionLocalService.getFragmentCompositions(
			fragmentCollectionId);
	}

	@Override
	public java.util.List<FragmentComposition> getFragmentCompositions(
		long fragmentCollectionId, int start, int end) {

		return _fragmentCompositionLocalService.getFragmentCompositions(
			fragmentCollectionId, start, end);
	}

	@Override
	public java.util.List<FragmentComposition> getFragmentCompositions(
		long groupId, long fragmentCollectionId, int status) {

		return _fragmentCompositionLocalService.getFragmentCompositions(
			groupId, fragmentCollectionId, status);
	}

	/**
	 * Returns all the fragment compositions matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment compositions
	 * @param companyId the primary key of the company
	 * @return the matching fragment compositions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<FragmentComposition>
		getFragmentCompositionsByUuidAndCompanyId(String uuid, long companyId) {

		return _fragmentCompositionLocalService.
			getFragmentCompositionsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of fragment compositions matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment compositions
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of fragment compositions
	 * @param end the upper bound of the range of fragment compositions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching fragment compositions, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<FragmentComposition>
		getFragmentCompositionsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<FragmentComposition> orderByComparator) {

		return _fragmentCompositionLocalService.
			getFragmentCompositionsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment compositions.
	 *
	 * @return the number of fragment compositions
	 */
	@Override
	public int getFragmentCompositionsCount() {
		return _fragmentCompositionLocalService.getFragmentCompositionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _fragmentCompositionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _fragmentCompositionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public String getUniqueFragmentCompositionName(
		long groupId, long fragmentCollectionId, String name) {

		return _fragmentCompositionLocalService.
			getUniqueFragmentCompositionName(
				groupId, fragmentCollectionId, name);
	}

	@Override
	public FragmentComposition moveFragmentComposition(
			long fragmentCompositionId, long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.moveFragmentComposition(
			fragmentCompositionId, fragmentCollectionId);
	}

	/**
	 * Updates the fragment composition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentCompositionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentComposition the fragment composition
	 * @return the fragment composition that was updated
	 */
	@Override
	public FragmentComposition updateFragmentComposition(
		FragmentComposition fragmentComposition) {

		return _fragmentCompositionLocalService.updateFragmentComposition(
			fragmentComposition);
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.updateFragmentComposition(
			fragmentCompositionId, previewFileEntryId);
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long userId, long fragmentCompositionId, long fragmentCollectionId,
			String name, String description, String data,
			long previewFileEntryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.updateFragmentComposition(
			userId, fragmentCompositionId, fragmentCollectionId, name,
			description, data, previewFileEntryId, status);
	}

	@Override
	public FragmentComposition updateFragmentComposition(
			long fragmentCompositionId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCompositionLocalService.updateFragmentComposition(
			fragmentCompositionId, name);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _fragmentCompositionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<FragmentComposition> getCTPersistence() {
		return _fragmentCompositionLocalService.getCTPersistence();
	}

	@Override
	public Class<FragmentComposition> getModelClass() {
		return _fragmentCompositionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentComposition>, R, E>
				updateUnsafeFunction)
		throws E {

		return _fragmentCompositionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public FragmentCompositionLocalService getWrappedService() {
		return _fragmentCompositionLocalService;
	}

	@Override
	public void setWrappedService(
		FragmentCompositionLocalService fragmentCompositionLocalService) {

		_fragmentCompositionLocalService = fragmentCompositionLocalService;
	}

	private FragmentCompositionLocalService _fragmentCompositionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2014393275