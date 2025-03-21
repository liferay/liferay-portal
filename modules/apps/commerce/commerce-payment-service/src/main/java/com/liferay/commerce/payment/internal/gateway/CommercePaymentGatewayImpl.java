/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.internal.gateway;

import com.liferay.commerce.payment.audit.CommercePaymentEntryAuditType;
import com.liferay.commerce.payment.audit.CommercePaymentEntryAuditTypeRegistry;
import com.liferay.commerce.payment.configuration.CommercePaymentEntryAuditConfiguration;
import com.liferay.commerce.payment.constants.CommercePaymentEntryAuditConstants;
import com.liferay.commerce.payment.gateway.CommercePaymentGateway;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryAuditLocalService;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.payment.util.CommercePaymentHelper;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.persistence.OptimisticLockException;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(service = CommercePaymentGateway.class)
public class CommercePaymentGatewayImpl implements CommercePaymentGateway {

	@Override
	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public CommercePaymentEntry authorize(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentHelper.getCommercePaymentIntegration(
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getPaymentIntegrationKey());

		CommercePaymentEntry authorizedCommercePaymentEntry =
			commercePaymentIntegration.authorize(
				httpServletRequest, commercePaymentEntry);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		if (StringUtil.equals(
				commercePaymentEntry.getErrorMessages(),
				authorizedCommercePaymentEntry.getErrorMessages()) &&
			StringUtil.equals(
				commercePaymentEntry.getPayload(),
				authorizedCommercePaymentEntry.getPayload()) &&
			(commercePaymentEntry.getPaymentStatus() ==
				authorizedCommercePaymentEntry.getPaymentStatus()) &&
			StringUtil.equals(
				commercePaymentEntry.getRedirectURL(),
				authorizedCommercePaymentEntry.getRedirectURL()) &&
			StringUtil.equals(
				commercePaymentEntry.getTransactionCode(),
				authorizedCommercePaymentEntry.getTransactionCode())) {

			return commercePaymentEntry;
		}

		User currentUser = _portal.getUser(httpServletRequest);

		PermissionThreadLocal.setPermissionChecker(
			_defaultPermissionCheckerFactory.create(currentUser));

		try {
			commercePaymentEntry =
				_commercePaymentEntryLocalService.updateCommercePaymentEntry(
					commercePaymentEntry.getExternalReferenceCode(),
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentEntry.getCommerceChannelId(),
					commercePaymentEntry.getAmount(),
					commercePaymentEntry.getCallbackURL(),
					commercePaymentEntry.getCancelURL(),
					commercePaymentEntry.getCurrencyCode(),
					authorizedCommercePaymentEntry.getErrorMessages(),
					commercePaymentEntry.getLanguageId(),
					commercePaymentEntry.getNote(),
					commercePaymentEntry.getPayload(),
					commercePaymentEntry.getPaymentIntegrationKey(),
					commercePaymentEntry.getPaymentIntegrationType(),
					authorizedCommercePaymentEntry.getPaymentStatus(),
					commercePaymentEntry.getReasonKey(),
					authorizedCommercePaymentEntry.getRedirectURL(),
					authorizedCommercePaymentEntry.getTransactionCode(),
					commercePaymentEntry.getType());
		}
		catch (Exception exception) {
			_logOptimisticLockException(exception);
		}

		CommercePaymentEntryAuditConfiguration
			commercePaymentEntryAuditConfiguration =
				_getCommercePaymentEntryAuditConfiguration(
					commercePaymentEntry.getCompanyId());

		if (commercePaymentEntryAuditConfiguration.enabled()) {
			CommercePaymentEntryAuditType commercePaymentEntryAuditType =
				_commercePaymentEntryAuditTypeRegistry.
					getCommercePaymentEntryAuditType(
						CommercePaymentEntryAuditConstants.
							TYPE_AUTHORIZE_PAYMENT);

			_commercePaymentEntryAuditLocalService.addCommercePaymentEntryAudit(
				currentUser.getUserId(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCurrencyCode(),
				commercePaymentEntryAuditType.getType(),
				commercePaymentEntryAuditType.getLog(
					HashMapBuilder.<String, Object>put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_NAME_ID,
						commercePaymentEntry.getClassNameId()
					).put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_PK,
						String.valueOf(commercePaymentEntry.getClassPK())
					).build()),
				_createServiceContext(currentUser));
		}

		return commercePaymentEntry;
	}

	@Override
	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public CommercePaymentEntry cancel(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentHelper.getCommercePaymentIntegration(
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getPaymentIntegrationKey());

		CommercePaymentEntry cancelledCommercePaymentEntry =
			commercePaymentIntegration.cancel(
				httpServletRequest, commercePaymentEntry);

		User currentUser = _portal.getUser(httpServletRequest);

		PermissionThreadLocal.setPermissionChecker(
			_defaultPermissionCheckerFactory.create(currentUser));

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry.getExternalReferenceCode(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCallbackURL(),
				commercePaymentEntry.getCancelURL(),
				commercePaymentEntry.getCurrencyCode(),
				cancelledCommercePaymentEntry.getErrorMessages(),
				commercePaymentEntry.getLanguageId(),
				commercePaymentEntry.getNote(),
				commercePaymentEntry.getPayload(),
				commercePaymentEntry.getPaymentIntegrationKey(),
				commercePaymentEntry.getPaymentIntegrationType(),
				cancelledCommercePaymentEntry.getPaymentStatus(),
				commercePaymentEntry.getReasonKey(),
				cancelledCommercePaymentEntry.getRedirectURL(),
				cancelledCommercePaymentEntry.getTransactionCode(),
				commercePaymentEntry.getType());

		CommercePaymentEntryAuditConfiguration
			commercePaymentEntryAuditConfiguration =
				_getCommercePaymentEntryAuditConfiguration(
					commercePaymentEntry.getCompanyId());

		if (commercePaymentEntryAuditConfiguration.enabled()) {
			CommercePaymentEntryAuditType commercePaymentEntryAuditType =
				_commercePaymentEntryAuditTypeRegistry.
					getCommercePaymentEntryAuditType(
						CommercePaymentEntryAuditConstants.TYPE_CANCEL_PAYMENT);

			_commercePaymentEntryAuditLocalService.addCommercePaymentEntryAudit(
				currentUser.getUserId(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCurrencyCode(),
				commercePaymentEntryAuditType.getType(),
				commercePaymentEntryAuditType.getLog(
					HashMapBuilder.<String, Object>put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_NAME_ID,
						commercePaymentEntry.getClassNameId()
					).put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_PK,
						String.valueOf(commercePaymentEntry.getClassPK())
					).build()),
				_createServiceContext(currentUser));
		}

		return commercePaymentEntry;
	}

	@Override
	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public CommercePaymentEntry capture(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		// LPD-20381 Get transaction code before it is captured by the commerce
		// payment integration

		String transactionCode = commercePaymentEntry.getTransactionCode();

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentHelper.getCommercePaymentIntegration(
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getPaymentIntegrationKey());

		CommercePaymentEntry capturedCommercePaymentEntry =
			commercePaymentIntegration.capture(
				httpServletRequest, commercePaymentEntry);

		commercePaymentEntry =
			_commercePaymentEntryLocalService.fetchCommercePaymentEntry(
				commercePaymentEntry.getCommercePaymentEntryId());

		if (StringUtil.equals(
				commercePaymentEntry.getErrorMessages(),
				capturedCommercePaymentEntry.getErrorMessages()) &&
			StringUtil.equals(
				commercePaymentEntry.getPayload(),
				capturedCommercePaymentEntry.getPayload()) &&
			(commercePaymentEntry.getPaymentStatus() ==
				capturedCommercePaymentEntry.getPaymentStatus()) &&
			StringUtil.equals(
				commercePaymentEntry.getRedirectURL(),
				capturedCommercePaymentEntry.getRedirectURL()) &&
			StringUtil.equals(
				transactionCode,
				capturedCommercePaymentEntry.getTransactionCode())) {

			return commercePaymentEntry;
		}

		User currentUser = _portal.getUser(httpServletRequest);

		PermissionThreadLocal.setPermissionChecker(
			_defaultPermissionCheckerFactory.create(currentUser));

		try {
			commercePaymentEntry =
				_commercePaymentEntryLocalService.updateCommercePaymentEntry(
					commercePaymentEntry.getExternalReferenceCode(),
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentEntry.getCommerceChannelId(),
					commercePaymentEntry.getAmount(),
					commercePaymentEntry.getCallbackURL(),
					commercePaymentEntry.getCancelURL(),
					commercePaymentEntry.getCurrencyCode(),
					capturedCommercePaymentEntry.getErrorMessages(),
					commercePaymentEntry.getLanguageId(),
					commercePaymentEntry.getNote(),
					capturedCommercePaymentEntry.getPayload(),
					commercePaymentEntry.getPaymentIntegrationKey(),
					commercePaymentEntry.getPaymentIntegrationType(),
					capturedCommercePaymentEntry.getPaymentStatus(),
					commercePaymentEntry.getReasonKey(),
					capturedCommercePaymentEntry.getRedirectURL(),
					capturedCommercePaymentEntry.getTransactionCode(),
					commercePaymentEntry.getType());
		}
		catch (Exception exception) {
			_logOptimisticLockException(exception);
		}

		CommercePaymentEntryAuditConfiguration
			commercePaymentEntryAuditConfiguration =
				_getCommercePaymentEntryAuditConfiguration(
					commercePaymentEntry.getCompanyId());

		if (commercePaymentEntryAuditConfiguration.enabled()) {
			CommercePaymentEntryAuditType commercePaymentEntryAuditType =
				_commercePaymentEntryAuditTypeRegistry.
					getCommercePaymentEntryAuditType(
						CommercePaymentEntryAuditConstants.
							TYPE_CAPTURE_PAYMENT);

			_commercePaymentEntryAuditLocalService.addCommercePaymentEntryAudit(
				currentUser.getUserId(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCurrencyCode(),
				commercePaymentEntryAuditType.getType(),
				commercePaymentEntryAuditType.getLog(
					HashMapBuilder.<String, Object>put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_NAME_ID,
						commercePaymentEntry.getClassNameId()
					).put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_PK,
						String.valueOf(commercePaymentEntry.getClassPK())
					).build()),
				_createServiceContext(currentUser));
		}

		return commercePaymentEntry;
	}

	@Override
	@Transactional(
		propagation = Propagation.REQUIRED, readOnly = false,
		rollbackFor = Exception.class
	)
	public CommercePaymentEntry refund(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentHelper.getCommercePaymentIntegration(
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getPaymentIntegrationKey());

		CommercePaymentEntry refundedCommercePaymentEntry =
			commercePaymentIntegration.refund(
				httpServletRequest, commercePaymentEntry);

		User currentUser = _portal.getUser(httpServletRequest);

		PermissionThreadLocal.setPermissionChecker(
			_defaultPermissionCheckerFactory.create(currentUser));

		commercePaymentEntry =
			_commercePaymentEntryLocalService.updateCommercePaymentEntry(
				commercePaymentEntry.getExternalReferenceCode(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCallbackURL(),
				commercePaymentEntry.getCancelURL(),
				commercePaymentEntry.getCurrencyCode(),
				refundedCommercePaymentEntry.getErrorMessages(),
				commercePaymentEntry.getLanguageId(),
				commercePaymentEntry.getNote(),
				commercePaymentEntry.getPayload(),
				commercePaymentEntry.getPaymentIntegrationKey(),
				commercePaymentEntry.getPaymentIntegrationType(),
				refundedCommercePaymentEntry.getPaymentStatus(),
				commercePaymentEntry.getReasonKey(),
				refundedCommercePaymentEntry.getRedirectURL(),
				refundedCommercePaymentEntry.getTransactionCode(),
				commercePaymentEntry.getType());

		CommercePaymentEntryAuditConfiguration
			commercePaymentEntryAuditConfiguration =
				_getCommercePaymentEntryAuditConfiguration(
					commercePaymentEntry.getCompanyId());

		if (commercePaymentEntryAuditConfiguration.enabled()) {
			CommercePaymentEntryAuditType commercePaymentEntryAuditType =
				_commercePaymentEntryAuditTypeRegistry.
					getCommercePaymentEntryAuditType(
						CommercePaymentEntryAuditConstants.TYPE_REFUND_PAYMENT);

			_commercePaymentEntryAuditLocalService.addCommercePaymentEntryAudit(
				currentUser.getUserId(),
				commercePaymentEntry.getCommercePaymentEntryId(),
				commercePaymentEntry.getAmount(),
				commercePaymentEntry.getCurrencyCode(),
				commercePaymentEntryAuditType.getType(),
				commercePaymentEntryAuditType.getLog(
					HashMapBuilder.<String, Object>put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_NAME_ID,
						commercePaymentEntry.getClassNameId()
					).put(
						CommercePaymentEntryAuditConstants.FIELD_CLASS_PK,
						String.valueOf(commercePaymentEntry.getClassPK())
					).build()),
				_createServiceContext(currentUser));
		}

		return commercePaymentEntry;
	}

	@Override
	public CommercePaymentEntry setUpPayment(
			HttpServletRequest httpServletRequest,
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		CommercePaymentIntegration commercePaymentIntegration =
			_commercePaymentHelper.getCommercePaymentIntegration(
				commercePaymentEntry.getCommerceChannelId(),
				commercePaymentEntry.getPaymentIntegrationKey());

		return commercePaymentIntegration.setUpPayment(
			httpServletRequest, commercePaymentEntry);
	}

	private ServiceContext _createServiceContext(User user) {
		return new ServiceContext() {
			{
				setCompanyId(user.getCompanyId());
				setUserId(user.getUserId());
			}
		};
	}

	private CommercePaymentEntryAuditConfiguration
			_getCommercePaymentEntryAuditConfiguration(long companyId)
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			CommercePaymentEntryAuditConfiguration.class, companyId);
	}

	private void _logOptimisticLockException(Exception exception) {
		if (!(exception.getCause() instanceof SystemException)) {
			return;
		}

		Throwable throwable = exception.getCause();

		if (!(throwable instanceof ORMException)) {
			return;
		}

		throwable = throwable.getCause();

		if (!(throwable instanceof OptimisticLockException) ||
			!_log.isDebugEnabled()) {

			return;
		}

		_log.debug("Ignore duplicate calls. See LPD-28950.", exception);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommercePaymentGatewayImpl.class);

	@Reference
	private CommercePaymentEntryAuditLocalService
		_commercePaymentEntryAuditLocalService;

	@Reference
	private CommercePaymentEntryAuditTypeRegistry
		_commercePaymentEntryAuditTypeRegistry;

	@Reference
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@Reference
	private CommercePaymentHelper _commercePaymentHelper;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference
	private Portal _portal;

}