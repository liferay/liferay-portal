/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPDefinitionLinkLocalService}.
 *
 * @author Marco Leo
 * @see CPDefinitionLinkLocalService
 * @generated
 */
public class CPDefinitionLinkLocalServiceWrapper
	implements CPDefinitionLinkLocalService,
			   ServiceWrapper<CPDefinitionLinkLocalService> {

	public CPDefinitionLinkLocalServiceWrapper() {
		this(null);
	}

	public CPDefinitionLinkLocalServiceWrapper(
		CPDefinitionLinkLocalService cpDefinitionLinkLocalService) {

		_cpDefinitionLinkLocalService = cpDefinitionLinkLocalService;
	}

	/**
	 * Adds the cp definition link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was added
	 */
	@Override
	public CPDefinitionLink addCPDefinitionLink(
		CPDefinitionLink cpDefinitionLink) {

		return _cpDefinitionLinkLocalService.addCPDefinitionLink(
			cpDefinitionLink);
	}

	@Override
	public CPDefinitionLink addCPDefinitionLinkByCProductId(
			long cpDefinitionId, long cProductId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.addCPDefinitionLinkByCProductId(
			cpDefinitionId, cProductId, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, priority,
			type, serviceContext);
	}

	@Override
	public void checkCPDefinitionLinks()
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLinkLocalService.checkCPDefinitionLinks();
	}

	/**
	 * Creates a new cp definition link with the primary key. Does not add the cp definition link to the database.
	 *
	 * @param CPDefinitionLinkId the primary key for the new cp definition link
	 * @return the new cp definition link
	 */
	@Override
	public CPDefinitionLink createCPDefinitionLink(long CPDefinitionLinkId) {
		return _cpDefinitionLinkLocalService.createCPDefinitionLink(
			CPDefinitionLinkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp definition link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was removed
	 * @throws PortalException
	 */
	@Override
	public CPDefinitionLink deleteCPDefinitionLink(
			CPDefinitionLink cpDefinitionLink)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.deleteCPDefinitionLink(
			cpDefinitionLink);
	}

	/**
	 * Deletes the cp definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link that was removed
	 * @throws PortalException if a cp definition link with the primary key could not be found
	 */
	@Override
	public CPDefinitionLink deleteCPDefinitionLink(long CPDefinitionLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.deleteCPDefinitionLink(
			CPDefinitionLinkId);
	}

	@Override
	public void deleteCPDefinitionLinksByCPDefinitionId(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLinkLocalService.deleteCPDefinitionLinksByCPDefinitionId(
			cpDefinitionId);
	}

	@Override
	public void deleteCPDefinitionLinksByCProductId(long cProductId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLinkLocalService.deleteCPDefinitionLinksByCProductId(
			cProductId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpDefinitionLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpDefinitionLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpDefinitionLinkLocalService.dynamicQuery();
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

		return _cpDefinitionLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
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

		return _cpDefinitionLinkLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
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

		return _cpDefinitionLinkLocalService.dynamicQuery(
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

		return _cpDefinitionLinkLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpDefinitionLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPDefinitionLink fetchCPDefinitionLink(long CPDefinitionLinkId) {
		return _cpDefinitionLinkLocalService.fetchCPDefinitionLink(
			CPDefinitionLinkId);
	}

	/**
	 * Returns the cp definition link matching the UUID and group.
	 *
	 * @param uuid the cp definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink fetchCPDefinitionLinkByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpDefinitionLinkLocalService.
			fetchCPDefinitionLinkByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpDefinitionLinkLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp definition link with the primary key.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link
	 * @throws PortalException if a cp definition link with the primary key could not be found
	 */
	@Override
	public CPDefinitionLink getCPDefinitionLink(long CPDefinitionLinkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.getCPDefinitionLink(
			CPDefinitionLinkId);
	}

	/**
	 * Returns the cp definition link matching the UUID and group.
	 *
	 * @param uuid the cp definition link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp definition link
	 * @throws PortalException if a matching cp definition link could not be found
	 */
	@Override
	public CPDefinitionLink getCPDefinitionLinkByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.
			getCPDefinitionLinkByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp definition links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of cp definition links
	 */
	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		int start, int end) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(start, end);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, status);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int start, int end) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, start, end);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, int status, int start, int end) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, status, start, end);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, type);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, type, status);
	}

	@Override
	public java.util.List<CPDefinitionLink> getCPDefinitionLinks(
		long cpDefinitionId, String type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionLink>
			orderByComparator) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinks(
			cpDefinitionId, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition links
	 * @param companyId the primary key of the company
	 * @return the matching cp definition links, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionLink>
		getCPDefinitionLinksByUuidAndCompanyId(String uuid, long companyId) {

		return _cpDefinitionLinkLocalService.
			getCPDefinitionLinksByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp definition links matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp definition links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp definition links, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPDefinitionLink>
		getCPDefinitionLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPDefinitionLink>
				orderByComparator) {

		return _cpDefinitionLinkLocalService.
			getCPDefinitionLinksByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp definition links.
	 *
	 * @return the number of cp definition links
	 */
	@Override
	public int getCPDefinitionLinksCount() {
		return _cpDefinitionLinkLocalService.getCPDefinitionLinksCount();
	}

	@Override
	public int getCPDefinitionLinksCount(long cpDefinitionId, int status) {
		return _cpDefinitionLinkLocalService.getCPDefinitionLinksCount(
			cpDefinitionId, status);
	}

	@Override
	public int getCPDefinitionLinksCount(long cpDefinitionId, String type) {
		return _cpDefinitionLinkLocalService.getCPDefinitionLinksCount(
			cpDefinitionId, type);
	}

	@Override
	public int getCPDefinitionLinksCount(
		long cpDefinitionId, String type, int status) {

		return _cpDefinitionLinkLocalService.getCPDefinitionLinksCount(
			cpDefinitionId, type, status);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpDefinitionLinkLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpDefinitionLinkLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpDefinitionLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<CPDefinitionLink> getReverseCPDefinitionLinks(
		long cProductId, String type, int status) {

		return _cpDefinitionLinkLocalService.getReverseCPDefinitionLinks(
			cProductId, type, status);
	}

	/**
	 * Updates the cp definition link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPDefinitionLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpDefinitionLink the cp definition link
	 * @return the cp definition link that was updated
	 */
	@Override
	public CPDefinitionLink updateCPDefinitionLink(
		CPDefinitionLink cpDefinitionLink) {

		return _cpDefinitionLinkLocalService.updateCPDefinitionLink(
			cpDefinitionLink);
	}

	@Override
	public CPDefinitionLink updateCPDefinitionLink(
			long userId, long cpDefinitionLinkId, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, double priority,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.updateCPDefinitionLink(
			userId, cpDefinitionLinkId, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire, priority,
			serviceContext);
	}

	@Override
	public void updateCPDefinitionLinkCProductIds(
			long cpDefinitionId, long[] cProductIds, String type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpDefinitionLinkLocalService.updateCPDefinitionLinkCProductIds(
			cpDefinitionId, cProductIds, type, serviceContext);
	}

	@Override
	public CPDefinitionLink updateStatus(
			long userId, long cpDefinitionLinkId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpDefinitionLinkLocalService.updateStatus(
			userId, cpDefinitionLinkId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpDefinitionLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPDefinitionLink> getCTPersistence() {
		return _cpDefinitionLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<CPDefinitionLink> getModelClass() {
		return _cpDefinitionLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPDefinitionLink>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpDefinitionLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPDefinitionLinkLocalService getWrappedService() {
		return _cpDefinitionLinkLocalService;
	}

	@Override
	public void setWrappedService(
		CPDefinitionLinkLocalService cpDefinitionLinkLocalService) {

		_cpDefinitionLinkLocalService = cpDefinitionLinkLocalService;
	}

	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1039222721