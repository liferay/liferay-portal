/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link PhoneLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see PhoneLocalService
 * @generated
 */
public class PhoneLocalServiceWrapper
	implements PhoneLocalService, ServiceWrapper<PhoneLocalService> {

	public PhoneLocalServiceWrapper() {
		this(null);
	}

	public PhoneLocalServiceWrapper(PhoneLocalService phoneLocalService) {
		_phoneLocalService = phoneLocalService;
	}

	/**
	 * Adds the phone to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PhoneLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param phone the phone
	 * @return the phone that was added
	 */
	@Override
	public Phone addPhone(Phone phone) {
		return _phoneLocalService.addPhone(phone);
	}

	@Override
	public Phone addPhone(
			String externalReferenceCode, long userId, String className,
			long classPK, String number, String extension, long listTypeId,
			boolean primary, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.addPhone(
			externalReferenceCode, userId, className, classPK, number,
			extension, listTypeId, primary, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new phone with the primary key. Does not add the phone to the database.
	 *
	 * @param phoneId the primary key for the new phone
	 * @return the new phone
	 */
	@Override
	public Phone createPhone(long phoneId) {
		return _phoneLocalService.createPhone(phoneId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the phone with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PhoneLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param phoneId the primary key of the phone
	 * @return the phone that was removed
	 * @throws PortalException if a phone with the primary key could not be found
	 */
	@Override
	public Phone deletePhone(long phoneId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.deletePhone(phoneId);
	}

	/**
	 * Deletes the phone from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PhoneLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param phone the phone
	 * @return the phone that was removed
	 */
	@Override
	public Phone deletePhone(Phone phone) {
		return _phoneLocalService.deletePhone(phone);
	}

	@Override
	public void deletePhones(long companyId, String className, long classPK) {
		_phoneLocalService.deletePhones(companyId, className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _phoneLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _phoneLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _phoneLocalService.dynamicQuery();
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

		return _phoneLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PhoneModelImpl</code>.
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

		return _phoneLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PhoneModelImpl</code>.
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

		return _phoneLocalService.dynamicQuery(
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

		return _phoneLocalService.dynamicQueryCount(dynamicQuery);
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

		return _phoneLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public Phone fetchPhone(long phoneId) {
		return _phoneLocalService.fetchPhone(phoneId);
	}

	@Override
	public Phone fetchPhoneByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _phoneLocalService.fetchPhoneByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the phone with the matching UUID and company.
	 *
	 * @param uuid the phone's UUID
	 * @param companyId the primary key of the company
	 * @return the matching phone, or <code>null</code> if a matching phone could not be found
	 */
	@Override
	public Phone fetchPhoneByUuidAndCompanyId(String uuid, long companyId) {
		return _phoneLocalService.fetchPhoneByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _phoneLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _phoneLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _phoneLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _phoneLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the phone with the primary key.
	 *
	 * @param phoneId the primary key of the phone
	 * @return the phone
	 * @throws PortalException if a phone with the primary key could not be found
	 */
	@Override
	public Phone getPhone(long phoneId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.getPhone(phoneId);
	}

	@Override
	public Phone getPhoneByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.getPhoneByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the phone with the matching UUID and company.
	 *
	 * @param uuid the phone's UUID
	 * @param companyId the primary key of the company
	 * @return the matching phone
	 * @throws PortalException if a matching phone could not be found
	 */
	@Override
	public Phone getPhoneByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.getPhoneByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the phones.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PhoneModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of phones
	 * @param end the upper bound of the range of phones (not inclusive)
	 * @return the range of phones
	 */
	@Override
	public java.util.List<Phone> getPhones(int start, int end) {
		return _phoneLocalService.getPhones(start, end);
	}

	@Override
	public java.util.List<Phone> getPhones(
		long companyId, String className, long classPK) {

		return _phoneLocalService.getPhones(companyId, className, classPK);
	}

	/**
	 * Returns the number of phones.
	 *
	 * @return the number of phones
	 */
	@Override
	public int getPhonesCount() {
		return _phoneLocalService.getPhonesCount();
	}

	/**
	 * Updates the phone in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect PhoneLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param phone the phone
	 * @return the phone that was updated
	 */
	@Override
	public Phone updatePhone(Phone phone) {
		return _phoneLocalService.updatePhone(phone);
	}

	@Override
	public Phone updatePhone(
			String externalReferenceCode, long phoneId, String number,
			String extension, long listTypeId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _phoneLocalService.updatePhone(
			externalReferenceCode, phoneId, number, extension, listTypeId,
			primary);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _phoneLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Phone> getCTPersistence() {
		return _phoneLocalService.getCTPersistence();
	}

	@Override
	public Class<Phone> getModelClass() {
		return _phoneLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Phone>, R, E> updateUnsafeFunction)
		throws E {

		return _phoneLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public PhoneLocalService getWrappedService() {
		return _phoneLocalService;
	}

	@Override
	public void setWrappedService(PhoneLocalService phoneLocalService) {
		_phoneLocalService = phoneLocalService;
	}

	private PhoneLocalService _phoneLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-313272064