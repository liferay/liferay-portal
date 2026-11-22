/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for CommercePaymentEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CommercePaymentEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the commerce payment entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CommercePaymentEntryServiceUtil} if injection and service tracking are not available.
	 */
	public CommercePaymentEntry addCommercePaymentEntry(
			long classNameId, long classPK, long commerceChannelId,
			BigDecimal amount, String callbackURL, String cancelURL,
			String currencyCode, String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			String reasonKey, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException;

	public CommercePaymentEntry addOrUpdateCommercePaymentEntry(
			String externalReferenceCode, long classNameId, long classPK,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type, ServiceContext serviceContext)
		throws PortalException;

	public CommercePaymentEntry deleteCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry fetchCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry
			fetchCommercePaymentEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentEntry> getCommercePaymentEntries(
			long companyId, long classNameId, long classPK, int type, int start,
			int end, OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentEntry> getCommercePaymentEntries(
			long companyId, long classNameId, long classPK, int start, int end,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCommercePaymentEntriesCount(
			long companyId, long classNameId, long classPK, int type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CommercePaymentEntry getCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BigDecimal getRefundedAmount(
			long companyId, long classNameId, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CommercePaymentEntry> search(
			long companyId, long[] classNameIds, long[] classPKs,
			String[] currencyCodes, String keywords,
			String[] paymentMethodNames, int[] paymentStatuses,
			boolean excludeStatuses, int start, int end, Sort sort)
		throws PortalException;

	public CommercePaymentEntry updateCommercePaymentEntry(
			String externalReferenceCode, long commercePaymentEntryId,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type)
		throws PortalException;

	public CommercePaymentEntry updateExternalReferenceCode(
			long commercePaymentEntryId, String externalReferenceCode)
		throws PortalException;

	public CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws PortalException;

	public CommercePaymentEntry updateReasonKey(
			long commercePaymentEntryId, String reasonKey)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-972145700