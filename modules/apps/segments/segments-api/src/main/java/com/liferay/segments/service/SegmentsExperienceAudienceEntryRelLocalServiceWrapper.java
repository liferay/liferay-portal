/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;

/**
 * Provides a wrapper for {@link SegmentsExperienceAudienceEntryRelLocalService}.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceAudienceEntryRelLocalService
 * @generated
 */
public class SegmentsExperienceAudienceEntryRelLocalServiceWrapper
	implements SegmentsExperienceAudienceEntryRelLocalService,
			   ServiceWrapper<SegmentsExperienceAudienceEntryRelLocalService> {

	public SegmentsExperienceAudienceEntryRelLocalServiceWrapper() {
		this(null);
	}

	public SegmentsExperienceAudienceEntryRelLocalServiceWrapper(
		SegmentsExperienceAudienceEntryRelLocalService
			segmentsExperienceAudienceEntryRelLocalService) {

		_segmentsExperienceAudienceEntryRelLocalService =
			segmentsExperienceAudienceEntryRelLocalService;
	}

	/**
	 * Adds the segments experience audience entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was added
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		addSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			addSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRel);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new segments experience audience entry rel with the primary key. Does not add the segments experience audience entry rel to the database.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key for the new segments experience audience entry rel
	 * @return the new segments experience audience entry rel
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		createSegmentsExperienceAudienceEntryRel(
			long segmentsExperienceAudienceEntryRelId) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			createSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the segments experience audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 * @throws PortalException if a segments experience audience entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
			deleteSegmentsExperienceAudienceEntryRel(
				long segmentsExperienceAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			deleteSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Deletes the segments experience audience entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was removed
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		deleteSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			deleteSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRel);
	}

	@Override
	public void deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
		long companyId, String audienceEntryERC) {

		_segmentsExperienceAudienceEntryRelLocalService.
			deleteSegmentsExperienceAudienceEntryRelsByAudienceEntryERC(
				companyId, audienceEntryERC);
	}

	@Override
	public void
		deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
			long groupId, String segmentsExperienceERC) {

		_segmentsExperienceAudienceEntryRelLocalService.
			deleteSegmentsExperienceAudienceEntryRelsBySegmentsExperienceERC(
				groupId, segmentsExperienceERC);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _segmentsExperienceAudienceEntryRelLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _segmentsExperienceAudienceEntryRelLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _segmentsExperienceAudienceEntryRelLocalService.dynamicQuery();
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

		return _segmentsExperienceAudienceEntryRelLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
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

		return _segmentsExperienceAudienceEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
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

		return _segmentsExperienceAudienceEntryRelLocalService.dynamicQuery(
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

		return _segmentsExperienceAudienceEntryRelLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _segmentsExperienceAudienceEntryRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public SegmentsExperienceAudienceEntryRel
		fetchSegmentsExperienceAudienceEntryRel(
			long segmentsExperienceAudienceEntryRelId) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			fetchSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel matching the UUID and group.
	 *
	 * @param uuid the segments experience audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience audience entry rel, or <code>null</code> if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		fetchSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
			String uuid, long groupId) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			fetchSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _segmentsExperienceAudienceEntryRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the segments experience audience entry rel with the primary key.
	 *
	 * @param segmentsExperienceAudienceEntryRelId the primary key of the segments experience audience entry rel
	 * @return the segments experience audience entry rel
	 * @throws PortalException if a segments experience audience entry rel with the primary key could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
			getSegmentsExperienceAudienceEntryRel(
				long segmentsExperienceAudienceEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRelId);
	}

	/**
	 * Returns the segments experience audience entry rel matching the UUID and group.
	 *
	 * @param uuid the segments experience audience entry rel's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience audience entry rel
	 * @throws PortalException if a matching segments experience audience entry rel could not be found
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
			getSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRelByUuidAndGroupId(
				uuid, groupId);
	}

	/**
	 * Returns a range of all the segments experience audience entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @return the range of segments experience audience entry rels
	 */
	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRels(int start, int end) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRels(start, end);
	}

	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRels(
			long groupId, String segmentsExperienceERC) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRels(
				groupId, segmentsExperienceERC);
	}

	/**
	 * Returns all the segments experience audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experience audience entry rels
	 * @param companyId the primary key of the company
	 * @return the matching segments experience audience entry rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of segments experience audience entry rels matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experience audience entry rels
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of segments experience audience entry rels
	 * @param end the upper bound of the range of segments experience audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching segments experience audience entry rels, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
		getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<SegmentsExperienceAudienceEntryRel> orderByComparator) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRelsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of segments experience audience entry rels.
	 *
	 * @return the number of segments experience audience entry rels
	 */
	@Override
	public int getSegmentsExperienceAudienceEntryRelsCount() {
		return _segmentsExperienceAudienceEntryRelLocalService.
			getSegmentsExperienceAudienceEntryRelsCount();
	}

	/**
	 * Updates the segments experience audience entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceAudienceEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceAudienceEntryRel the segments experience audience entry rel
	 * @return the segments experience audience entry rel that was updated
	 */
	@Override
	public SegmentsExperienceAudienceEntryRel
		updateSegmentsExperienceAudienceEntryRel(
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel) {

		return _segmentsExperienceAudienceEntryRelLocalService.
			updateSegmentsExperienceAudienceEntryRel(
				segmentsExperienceAudienceEntryRel);
	}

	@Override
	public java.util.List<SegmentsExperienceAudienceEntryRel>
			updateSegmentsExperienceAudienceEntryRels(
				long userId, long groupId, String[] audienceEntryERCs,
				String segmentsExperienceERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _segmentsExperienceAudienceEntryRelLocalService.
			updateSegmentsExperienceAudienceEntryRels(
				userId, groupId, audienceEntryERCs, segmentsExperienceERC);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _segmentsExperienceAudienceEntryRelLocalService.
			getBasePersistence();
	}

	@Override
	public CTPersistence<SegmentsExperienceAudienceEntryRel>
		getCTPersistence() {

		return _segmentsExperienceAudienceEntryRelLocalService.
			getCTPersistence();
	}

	@Override
	public Class<SegmentsExperienceAudienceEntryRel> getModelClass() {
		return _segmentsExperienceAudienceEntryRelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction
				<CTPersistence<SegmentsExperienceAudienceEntryRel>, R, E>
					updateUnsafeFunction)
		throws E {

		return _segmentsExperienceAudienceEntryRelLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public SegmentsExperienceAudienceEntryRelLocalService getWrappedService() {
		return _segmentsExperienceAudienceEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		SegmentsExperienceAudienceEntryRelLocalService
			segmentsExperienceAudienceEntryRelLocalService) {

		_segmentsExperienceAudienceEntryRelLocalService =
			segmentsExperienceAudienceEntryRelLocalService;
	}

	private SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1823294634