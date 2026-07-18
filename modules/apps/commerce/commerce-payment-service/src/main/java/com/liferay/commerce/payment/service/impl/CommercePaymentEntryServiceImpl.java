/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.impl;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.payment.constants.CommercePaymentEntryActionKeys;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.base.CommercePaymentEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=commerce",
		"json.web.service.context.path=CommercePaymentEntry"
	},
	service = AopService.class
)
public class CommercePaymentEntryServiceImpl
	extends CommercePaymentEntryServiceBaseImpl {

	@Override
	public CommercePaymentEntry addCommercePaymentEntry(
			long classNameId, long classPK, long commerceChannelId,
			BigDecimal amount, String callbackURL, String cancelURL,
			String currencyCode, String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			String reasonKey, String transactionCode, int type,
			ServiceContext serviceContext)
		throws PortalException {

		String actionId = CommercePaymentEntryActionKeys.ADD_PAYMENT;

		if (type == CommercePaymentEntryConstants.TYPE_REFUND) {
			actionId = CommercePaymentEntryActionKeys.ADD_REFUND;
		}

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(), actionId);

		return commercePaymentEntryLocalService.addCommercePaymentEntry(
			getUserId(), classNameId, classPK, commerceChannelId, amount,
			callbackURL, cancelURL, currencyCode, languageId, note, payload,
			paymentIntegrationKey, paymentIntegrationType, reasonKey,
			transactionCode, type, serviceContext);
	}

	@Override
	public CommercePaymentEntry addOrUpdateCommercePaymentEntry(
			String externalReferenceCode, long classNameId, long classPK,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type, ServiceContext serviceContext)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryLocalService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, serviceContext.getCompanyId());

		if (commercePaymentEntry == null) {
			String actionId = CommercePaymentEntryActionKeys.ADD_PAYMENT;

			if (type == CommercePaymentEntryConstants.TYPE_REFUND) {
				actionId = CommercePaymentEntryActionKeys.ADD_REFUND;
			}

			_portletResourcePermission.check(
				getPermissionChecker(), serviceContext.getScopeGroupId(),
				actionId);
		}
		else {
			_commercePaymentEntryModelResourcePermission.check(
				getPermissionChecker(), commercePaymentEntry,
				ActionKeys.UPDATE);
		}

		return commercePaymentEntryLocalService.addOrUpdateCommercePaymentEntry(
			externalReferenceCode, getUserId(), classNameId, classPK,
			commerceChannelId, amount, callbackURL, cancelURL, currencyCode,
			errorMessages, languageId, note, payload, paymentIntegrationKey,
			paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
			transactionCode, type, serviceContext);
	}

	@Override
	public CommercePaymentEntry deleteCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.DELETE);

		return commercePaymentEntryLocalService.deleteCommercePaymentEntry(
			commercePaymentEntryId);
	}

	@Override
	public CommercePaymentEntry fetchCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryPersistence.fetchByPrimaryKey(
				commercePaymentEntryId);

		if (commercePaymentEntry != null) {
			_commercePaymentEntryModelResourcePermission.check(
				getPermissionChecker(), commercePaymentEntry, ActionKeys.VIEW);
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry
			fetchCommercePaymentEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		CommercePaymentEntry commercePaymentEntry =
			commercePaymentEntryLocalService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (commercePaymentEntry != null) {
			_commercePaymentEntryModelResourcePermission.check(
				getPermissionChecker(), commercePaymentEntry, ActionKeys.VIEW);
		}

		return commercePaymentEntry;
	}

	@Override
	public List<CommercePaymentEntry> getCommercePaymentEntries(
			long companyId, long classNameId, long classPK, int type, int start,
			int end, OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, CommercePaymentEntry.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntry.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return commercePaymentEntryPersistence.findByC_C_C_T(
			companyId, classNameId, classPK, type, start, end,
			orderByComparator);
	}

	@Override
	public List<CommercePaymentEntry> getCommercePaymentEntries(
			long companyId, long classNameId, long classPK, int start, int end,
			OrderByComparator<CommercePaymentEntry> orderByComparator)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, CommercePaymentEntry.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntry.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return commercePaymentEntryPersistence.findByC_C_C(
			companyId, classNameId, classPK, start, end, orderByComparator);
	}

	@Override
	public int getCommercePaymentEntriesCount(
			long companyId, long classNameId, long classPK, int type)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, CommercePaymentEntry.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntry.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return commercePaymentEntryPersistence.countByC_C_C_T(
			companyId, classNameId, classPK, type);
	}

	@Override
	public CommercePaymentEntry getCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.VIEW);

		return commercePaymentEntryPersistence.findByPrimaryKey(
			commercePaymentEntryId);
	}

	@Override
	public BigDecimal getRefundedAmount(
			long companyId, long classNameId, long classPK)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, CommercePaymentEntry.class.getName(), companyId,
				ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommercePaymentEntry.class.getName(), 0,
				ActionKeys.VIEW);
		}

		return commercePaymentEntryLocalService.getRefundedAmount(
			companyId, classNameId, classPK);
	}

	@Override
	public List<CommercePaymentEntry> search(
			long companyId, long[] classNameIds, long[] classPKs,
			String[] currencyCodes, String keywords,
			String[] paymentMethodNames, int[] paymentStatuses,
			boolean excludeStatuses, int start, int end, Sort sort)
		throws PortalException {

		BaseModelSearchResult<CommercePaymentEntry> baseModelSearchResult =
			commercePaymentEntryLocalService.searchCommercePaymentEntries(
				companyId, keywords,
				LinkedHashMapBuilder.<String, Object>put(
					"classNameIds", classNameIds
				).put(
					"classPKs", classPKs
				).put(
					"currencyCodes", currencyCodes
				).put(
					"paymentMethodNames", paymentMethodNames
				).put(
					"permissionUserId", getPermissionChecker().getUserId()
				).put(
					"paymentStatuses", paymentStatuses
				).put(
					"excludeStatuses", excludeStatuses
				).build(),
				start, end, sort);

		return baseModelSearchResult.getBaseModels();
	}

	@Override
	public CommercePaymentEntry updateCommercePaymentEntry(
			String externalReferenceCode, long commercePaymentEntryId,
			long commerceChannelId, BigDecimal amount, String callbackURL,
			String cancelURL, String currencyCode, String errorMessages,
			String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			int paymentStatus, String reasonKey, String redirectURL,
			String transactionCode, int type)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.UPDATE);

		return commercePaymentEntryLocalService.updateCommercePaymentEntry(
			externalReferenceCode, commercePaymentEntryId, commerceChannelId,
			amount, callbackURL, cancelURL, currencyCode, errorMessages,
			languageId, note, payload, paymentIntegrationKey,
			paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
			transactionCode, type);
	}

	@Override
	public CommercePaymentEntry updateExternalReferenceCode(
			long commercePaymentEntryId, String externalReferenceCode)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.UPDATE);

		return commercePaymentEntryLocalService.updateExternalReferenceCode(
			commercePaymentEntryId, externalReferenceCode);
	}

	@Override
	public CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.UPDATE);

		return commercePaymentEntryLocalService.updateNote(
			commercePaymentEntryId, note);
	}

	@Override
	public CommercePaymentEntry updateReasonKey(
			long commercePaymentEntryId, String reasonKey)
		throws PortalException {

		_commercePaymentEntryModelResourcePermission.check(
			getPermissionChecker(), commercePaymentEntryId, ActionKeys.UPDATE);

		return commercePaymentEntryLocalService.updateReasonKey(
			commercePaymentEntryId, reasonKey);
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry)"
	)
	private ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;

	@Reference(
		target = "(resource.name=" + CommercePaymentEntryConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}