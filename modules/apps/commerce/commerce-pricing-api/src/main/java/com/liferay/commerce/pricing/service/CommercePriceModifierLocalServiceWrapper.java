/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service;

import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommercePriceModifierLocalService}.
 *
 * @author Riccardo Alberti
 * @see CommercePriceModifierLocalService
 * @generated
 */
public class CommercePriceModifierLocalServiceWrapper
	implements CommercePriceModifierLocalService,
			   ServiceWrapper<CommercePriceModifierLocalService> {

	public CommercePriceModifierLocalServiceWrapper() {
		this(null);
	}

	public CommercePriceModifierLocalServiceWrapper(
		CommercePriceModifierLocalService commercePriceModifierLocalService) {

		_commercePriceModifierLocalService = commercePriceModifierLocalService;
	}

	/**
	 * Adds the commerce price modifier to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifier the commerce price modifier
	 * @return the commerce price modifier that was added
	 */
	@Override
	public CommercePriceModifier addCommercePriceModifier(
		CommercePriceModifier commercePriceModifier) {

		return _commercePriceModifierLocalService.addCommercePriceModifier(
			commercePriceModifier);
	}

	@Override
	public CommercePriceModifier addCommercePriceModifier(
			String externalReferenceCode, long groupId,
			long commercePriceListId, String title, String target,
			java.math.BigDecimal modifierAmount, String modifierType,
			double priority, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.addCommercePriceModifier(
			externalReferenceCode, groupId, commercePriceListId, title, target,
			modifierAmount, modifierType, priority, active, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public CommercePriceModifier addOrUpdateCommercePriceModifier(
			String externalReferenceCode, long commercePriceModifierId,
			long groupId, long commercePriceListId, String title, String target,
			java.math.BigDecimal modifierAmount, String modifierType,
			double priority, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.
			addOrUpdateCommercePriceModifier(
				externalReferenceCode, commercePriceModifierId, groupId,
				commercePriceListId, title, target, modifierAmount,
				modifierType, priority, active, displayDateMonth,
				displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay,
				expirationDateYear, expirationDateHour, expirationDateMinute,
				neverExpire, serviceContext);
	}

	@Override
	public void checkCommercePriceModifiers()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierLocalService.checkCommercePriceModifiers();
	}

	/**
	 * Creates a new commerce price modifier with the primary key. Does not add the commerce price modifier to the database.
	 *
	 * @param commercePriceModifierId the primary key for the new commerce price modifier
	 * @return the new commerce price modifier
	 */
	@Override
	public CommercePriceModifier createCommercePriceModifier(
		long commercePriceModifierId) {

		return _commercePriceModifierLocalService.createCommercePriceModifier(
			commercePriceModifierId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce price modifier from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifier the commerce price modifier
	 * @return the commerce price modifier that was removed
	 * @throws PortalException
	 */
	@Override
	public CommercePriceModifier deleteCommercePriceModifier(
			CommercePriceModifier commercePriceModifier)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.deleteCommercePriceModifier(
			commercePriceModifier);
	}

	/**
	 * Deletes the commerce price modifier with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifierId the primary key of the commerce price modifier
	 * @return the commerce price modifier that was removed
	 * @throws PortalException if a commerce price modifier with the primary key could not be found
	 */
	@Override
	public CommercePriceModifier deleteCommercePriceModifier(
			long commercePriceModifierId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.deleteCommercePriceModifier(
			commercePriceModifierId);
	}

	@Override
	public void deleteCommercePriceModifiers(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierLocalService.deleteCommercePriceModifiers(
			companyId);
	}

	@Override
	public void deleteCommercePriceModifiersByCommercePriceListId(
			long commercePriceListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePriceModifierLocalService.
			deleteCommercePriceModifiersByCommercePriceListId(
				commercePriceListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePriceModifierLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePriceModifierLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePriceModifierLocalService.dynamicQuery();
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

		return _commercePriceModifierLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierModelImpl</code>.
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

		return _commercePriceModifierLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierModelImpl</code>.
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

		return _commercePriceModifierLocalService.dynamicQuery(
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

		return _commercePriceModifierLocalService.dynamicQueryCount(
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

		return _commercePriceModifierLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommercePriceModifier fetchCommercePriceModifier(
		long commercePriceModifierId) {

		return _commercePriceModifierLocalService.fetchCommercePriceModifier(
			commercePriceModifierId);
	}

	@Override
	public CommercePriceModifier
		fetchCommercePriceModifierByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commercePriceModifierLocalService.
			fetchCommercePriceModifierByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price modifier matching the UUID and group.
	 *
	 * @param uuid the commerce price modifier's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price modifier, or <code>null</code> if a matching commerce price modifier could not be found
	 */
	@Override
	public CommercePriceModifier fetchCommercePriceModifierByUuidAndGroupId(
		String uuid, long groupId) {

		return _commercePriceModifierLocalService.
			fetchCommercePriceModifierByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePriceModifierLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce price modifier with the primary key.
	 *
	 * @param commercePriceModifierId the primary key of the commerce price modifier
	 * @return the commerce price modifier
	 * @throws PortalException if a commerce price modifier with the primary key could not be found
	 */
	@Override
	public CommercePriceModifier getCommercePriceModifier(
			long commercePriceModifierId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.getCommercePriceModifier(
			commercePriceModifierId);
	}

	@Override
	public CommercePriceModifier
			getCommercePriceModifierByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.
			getCommercePriceModifierByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce price modifier matching the UUID and group.
	 *
	 * @param uuid the commerce price modifier's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce price modifier
	 * @throws PortalException if a matching commerce price modifier could not be found
	 */
	@Override
	public CommercePriceModifier getCommercePriceModifierByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.
			getCommercePriceModifierByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce price modifiers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.pricing.model.impl.CommercePriceModifierModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce price modifiers
	 * @param end the upper bound of the range of commerce price modifiers (not inclusive)
	 * @return the range of commerce price modifiers
	 */
	@Override
	public java.util.List<CommercePriceModifier> getCommercePriceModifiers(
		int start, int end) {

		return _commercePriceModifierLocalService.getCommercePriceModifiers(
			start, end);
	}

	@Override
	public java.util.List<CommercePriceModifier> getCommercePriceModifiers(
		long commercePriceListId) {

		return _commercePriceModifierLocalService.getCommercePriceModifiers(
			commercePriceListId);
	}

	@Override
	public java.util.List<CommercePriceModifier> getCommercePriceModifiers(
		long commercePriceListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CommercePriceModifier>
			orderByComparator) {

		return _commercePriceModifierLocalService.getCommercePriceModifiers(
			commercePriceListId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce price modifiers matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce price modifiers
	 * @param companyId the primary key of the company
	 * @return the matching commerce price modifiers, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CommercePriceModifier>
		getCommercePriceModifiersByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commercePriceModifierLocalService.
			getCommercePriceModifiersByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce price modifiers matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce price modifiers
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce price modifiers
	 * @param end the upper bound of the range of commerce price modifiers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce price modifiers, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CommercePriceModifier>
		getCommercePriceModifiersByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CommercePriceModifier> orderByComparator) {

		return _commercePriceModifierLocalService.
			getCommercePriceModifiersByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce price modifiers.
	 *
	 * @return the number of commerce price modifiers
	 */
	@Override
	public int getCommercePriceModifiersCount() {
		return _commercePriceModifierLocalService.
			getCommercePriceModifiersCount();
	}

	@Override
	public int getCommercePriceModifiersCount(long commercePriceListId) {
		return _commercePriceModifierLocalService.
			getCommercePriceModifiersCount(commercePriceListId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commercePriceModifierLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePriceModifierLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePriceModifierLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List<CommercePriceModifier>
		getQualifiedCommercePriceModifiers(
			long commercePriceListId, long cpDefinitionId) {

		return _commercePriceModifierLocalService.
			getQualifiedCommercePriceModifiers(
				commercePriceListId, cpDefinitionId);
	}

	/**
	 * Updates the commerce price modifier in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePriceModifierLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePriceModifier the commerce price modifier
	 * @return the commerce price modifier that was updated
	 */
	@Override
	public CommercePriceModifier updateCommercePriceModifier(
		CommercePriceModifier commercePriceModifier) {

		return _commercePriceModifierLocalService.updateCommercePriceModifier(
			commercePriceModifier);
	}

	@Override
	public CommercePriceModifier updateCommercePriceModifier(
			long commercePriceModifierId, long groupId,
			long commercePriceListId, String title, String target,
			java.math.BigDecimal modifierAmount, String modifierType,
			double priority, boolean active, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.updateCommercePriceModifier(
			commercePriceModifierId, groupId, commercePriceListId, title,
			target, modifierAmount, modifierType, priority, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, serviceContext);
	}

	@Override
	public CommercePriceModifier updateStatus(
			long userId, long commercePriceModifierId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePriceModifierLocalService.updateStatus(
			userId, commercePriceModifierId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePriceModifierLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommercePriceModifier> getCTPersistence() {
		return _commercePriceModifierLocalService.getCTPersistence();
	}

	@Override
	public Class<CommercePriceModifier> getModelClass() {
		return _commercePriceModifierLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommercePriceModifier>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commercePriceModifierLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommercePriceModifierLocalService getWrappedService() {
		return _commercePriceModifierLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePriceModifierLocalService commercePriceModifierLocalService) {

		_commercePriceModifierLocalService = commercePriceModifierLocalService;
	}

	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1628144873