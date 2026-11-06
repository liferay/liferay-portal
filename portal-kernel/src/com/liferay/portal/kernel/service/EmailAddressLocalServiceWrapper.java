/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link EmailAddressLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see EmailAddressLocalService
 * @generated
 */
public class EmailAddressLocalServiceWrapper
	implements EmailAddressLocalService,
			   ServiceWrapper<EmailAddressLocalService> {

	public EmailAddressLocalServiceWrapper() {
		this(null);
	}

	public EmailAddressLocalServiceWrapper(
		EmailAddressLocalService emailAddressLocalService) {

		_emailAddressLocalService = emailAddressLocalService;
	}

	/**
	 * Adds the email address to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EmailAddressLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @return the email address that was added
	 */
	@Override
	public EmailAddress addEmailAddress(EmailAddress emailAddress) {
		return _emailAddressLocalService.addEmailAddress(emailAddress);
	}

	@Override
	public EmailAddress addEmailAddress(
			String externalReferenceCode, long userId, String className,
			long classPK, String address, long listTypeId, boolean primary,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.addEmailAddress(
			externalReferenceCode, userId, className, classPK, address,
			listTypeId, primary, serviceContext);
	}

	/**
	 * Creates a new email address with the primary key. Does not add the email address to the database.
	 *
	 * @param emailAddressId the primary key for the new email address
	 * @return the new email address
	 */
	@Override
	public EmailAddress createEmailAddress(long emailAddressId) {
		return _emailAddressLocalService.createEmailAddress(emailAddressId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the email address from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EmailAddressLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @return the email address that was removed
	 */
	@Override
	public EmailAddress deleteEmailAddress(EmailAddress emailAddress) {
		return _emailAddressLocalService.deleteEmailAddress(emailAddress);
	}

	/**
	 * Deletes the email address with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EmailAddressLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param emailAddressId the primary key of the email address
	 * @return the email address that was removed
	 * @throws PortalException if a email address with the primary key could not be found
	 */
	@Override
	public EmailAddress deleteEmailAddress(long emailAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.deleteEmailAddress(emailAddressId);
	}

	@Override
	public void deleteEmailAddresses(
		long companyId, String className, long classPK) {

		_emailAddressLocalService.deleteEmailAddresses(
			companyId, className, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _emailAddressLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _emailAddressLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _emailAddressLocalService.dynamicQuery();
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

		return _emailAddressLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.EmailAddressModelImpl</code>.
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

		return _emailAddressLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.EmailAddressModelImpl</code>.
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

		return _emailAddressLocalService.dynamicQuery(
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

		return _emailAddressLocalService.dynamicQueryCount(dynamicQuery);
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

		return _emailAddressLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public EmailAddress fetchEmailAddress(long emailAddressId) {
		return _emailAddressLocalService.fetchEmailAddress(emailAddressId);
	}

	@Override
	public EmailAddress fetchEmailAddressByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _emailAddressLocalService.
			fetchEmailAddressByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the email address with the matching UUID and company.
	 *
	 * @param uuid the email address's UUID
	 * @param companyId the primary key of the company
	 * @return the matching email address, or <code>null</code> if a matching email address could not be found
	 */
	@Override
	public EmailAddress fetchEmailAddressByUuidAndCompanyId(
		String uuid, long companyId) {

		return _emailAddressLocalService.fetchEmailAddressByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _emailAddressLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the email address with the primary key.
	 *
	 * @param emailAddressId the primary key of the email address
	 * @return the email address
	 * @throws PortalException if a email address with the primary key could not be found
	 */
	@Override
	public EmailAddress getEmailAddress(long emailAddressId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.getEmailAddress(emailAddressId);
	}

	@Override
	public EmailAddress getEmailAddressByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.getEmailAddressByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the email address with the matching UUID and company.
	 *
	 * @param uuid the email address's UUID
	 * @param companyId the primary key of the company
	 * @return the matching email address
	 * @throws PortalException if a matching email address could not be found
	 */
	@Override
	public EmailAddress getEmailAddressByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.getEmailAddressByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the email addresses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.EmailAddressModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of email addresses
	 * @param end the upper bound of the range of email addresses (not inclusive)
	 * @return the range of email addresses
	 */
	@Override
	public java.util.List<EmailAddress> getEmailAddresses(int start, int end) {
		return _emailAddressLocalService.getEmailAddresses(start, end);
	}

	@Override
	public java.util.List<EmailAddress> getEmailAddresses(
		long companyId, String className, long classPK) {

		return _emailAddressLocalService.getEmailAddresses(
			companyId, className, classPK);
	}

	/**
	 * Returns the number of email addresses.
	 *
	 * @return the number of email addresses
	 */
	@Override
	public int getEmailAddressesCount() {
		return _emailAddressLocalService.getEmailAddressesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _emailAddressLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _emailAddressLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _emailAddressLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the email address in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect EmailAddressLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @return the email address that was updated
	 */
	@Override
	public EmailAddress updateEmailAddress(EmailAddress emailAddress) {
		return _emailAddressLocalService.updateEmailAddress(emailAddress);
	}

	@Override
	public EmailAddress updateEmailAddress(
			String externalReferenceCode, long emailAddressId, String address,
			long listTypeId, boolean primary)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _emailAddressLocalService.updateEmailAddress(
			externalReferenceCode, emailAddressId, address, listTypeId,
			primary);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _emailAddressLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<EmailAddress> getCTPersistence() {
		return _emailAddressLocalService.getCTPersistence();
	}

	@Override
	public Class<EmailAddress> getModelClass() {
		return _emailAddressLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<EmailAddress>, R, E>
				updateUnsafeFunction)
		throws E {

		return _emailAddressLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public EmailAddressLocalService getWrappedService() {
		return _emailAddressLocalService;
	}

	@Override
	public void setWrappedService(
		EmailAddressLocalService emailAddressLocalService) {

		_emailAddressLocalService = emailAddressLocalService;
	}

	private EmailAddressLocalService _emailAddressLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1050194158