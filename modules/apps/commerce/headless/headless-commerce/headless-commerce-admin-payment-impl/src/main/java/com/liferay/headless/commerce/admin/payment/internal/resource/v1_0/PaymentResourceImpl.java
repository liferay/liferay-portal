/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.resource.v1_0;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.payment.constants.CommercePaymentEntryActionKeys;
import com.liferay.commerce.payment.exception.NoSuchPaymentEntryException;
import com.liferay.commerce.payment.gateway.CommercePaymentGateway;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.internal.odata.entity.v1_0.PaymentEntityModel;
import com.liferay.headless.commerce.admin.payment.resource.v1_0.PaymentResource;
import com.liferay.headless.commerce.core.util.ActionUtil;
import com.liferay.headless.commerce.core.util.CommerceCurrencyUtil;
import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/payment.properties",
	scope = ServiceScope.PROTOTYPE, service = PaymentResource.class
)
public class PaymentResourceImpl extends BasePaymentResourceImpl {

	@Override
	public Response deletePayment(Long id) throws Exception {
		_commercePaymentEntryService.deleteCommercePaymentEntry(id);

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Response deletePaymentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePaymentEntry == null) {
			throw new NoSuchPaymentEntryException(
				"Unable to find payment with external reference code " +
					externalReferenceCode);
		}

		_commercePaymentEntryService.deleteCommercePaymentEntry(
			commercePaymentEntry.getCommercePaymentEntryId());

		Response.ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Payment getPayment(Long id) throws Exception {
		return _toPayment(
			GetterUtil.getLong(id), contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo,
			_getActions(
				_commercePaymentEntryService.getCommercePaymentEntry(
					GetterUtil.getLong(id))));
	}

	@Override
	public Payment getPaymentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePaymentEntry == null) {
			throw new NoSuchPaymentEntryException(
				"Unable to find payment with external reference code " +
					externalReferenceCode);
		}

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Page<Payment> getPaymentsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			null, booleanQuery -> booleanQuery.getPreBooleanFilter(), filter,
			CommercePaymentEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toPayment(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)),
				contextAcceptLanguage.getPreferredLocale(),
				contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
				contextUriInfo,
				_getActions(
					_commercePaymentEntryService.getCommercePaymentEntry(
						GetterUtil.getLong(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK)))))));
	}

	@Override
	public Payment patchPayment(Long id, Payment payment) throws Exception {
		CommercePaymentEntry commercePaymentEntry = _updatePayment(
			_commercePaymentEntryService.getCommercePaymentEntry(id), payment);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Payment patchPaymentByExternalReferenceCode(
			String externalReferenceCode, Payment payment)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePaymentEntry == null) {
			throw new NoSuchPaymentEntryException(
				"Unable to find payment with external reference code " +
					externalReferenceCode);
		}

		commercePaymentEntry = _updatePayment(commercePaymentEntry, payment);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Payment postPayment(Payment payment) throws Exception {
		CommercePaymentEntry commercePaymentEntry = _addOrUpdatePayment(
			payment.getExternalReferenceCode(), payment);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Payment postPaymentByExternalReferenceCodeRefund(
			String externalReferenceCode)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryService.
				fetchCommercePaymentEntryByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		if (commercePaymentEntry == null) {
			throw new NoSuchPaymentEntryException(
				"Unable to find payment with external reference code " +
					externalReferenceCode);
		}

		if (commercePaymentEntry.getType() !=
				CommercePaymentEntryConstants.TYPE_REFUND) {

			throw new UnsupportedOperationException();
		}

		commercePaymentEntry = _commercePaymentGateway.refund(
			contextHttpServletRequest, commercePaymentEntry);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Payment postPaymentRefund(Long id) throws Exception {
		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentEntryService.getCommercePaymentEntry(id);

		if (commercePaymentEntry.getType() !=
				CommercePaymentEntryConstants.TYPE_REFUND) {

			throw new UnsupportedOperationException();
		}

		commercePaymentEntry = _commercePaymentGateway.refund(
			contextHttpServletRequest, commercePaymentEntry);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	@Override
	public Payment putPaymentByExternalReferenceCode(
			String externalReferenceCode, Payment payment)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry = _addOrUpdatePayment(
			externalReferenceCode, payment);

		return _toPayment(
			commercePaymentEntry.getCommercePaymentEntryId(),
			contextAcceptLanguage.getPreferredLocale(),
			contextAcceptLanguage.isAcceptAllLanguages(), contextUser,
			contextUriInfo, _getActions(commercePaymentEntry));
	}

	private Map<String, String> _addAction(
			Class<?> clazz, CommercePaymentEntry commercePaymentEntry,
			String methodName, UriInfo uriInfo)
		throws Exception {

		if (!_portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				contextCompany.getGroupId(),
				CommercePaymentEntryActionKeys.ADD_REFUND) ||
			(commercePaymentEntry.getPaymentStatus() !=
				CommercePaymentEntryConstants.STATUS_COMPLETED) ||
			(commercePaymentEntry.getType() !=
				CommercePaymentEntryConstants.TYPE_PAYMENT)) {

			return null;
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();

				return uriBuilder.path(
					ActionUtil.getVersion(uriInfo)
				).path(
					clazz.getSuperclass(), methodName
				).toTemplate();
			}
		).put(
			"method",
			ActionUtil.getHttpMethodName(
				clazz, ActionUtil.getMethod(clazz, methodName))
		).build();
	}

	private Map<String, String> _addAction(
			String actionId, Class<?> clazz,
			CommercePaymentEntry commercePaymentEntry, String methodName,
			UriInfo uriInfo)
		throws Exception {

		if (!_commercePaymentEntryModelResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				commercePaymentEntry, actionId) ||
			(commercePaymentEntry.getType() !=
				CommercePaymentEntryConstants.TYPE_REFUND)) {

			return null;
		}

		return HashMapBuilder.put(
			"href",
			() -> {
				UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();

				return uriBuilder.path(
					ActionUtil.getVersion(uriInfo)
				).path(
					clazz.getSuperclass(), methodName
				).toTemplate();
			}
		).put(
			"method",
			ActionUtil.getHttpMethodName(
				clazz, ActionUtil.getMethod(clazz, methodName))
		).build();
	}

	private CommercePaymentEntry _addOrUpdatePayment(
			String externalReferenceCode, Payment payment)
		throws Exception {

		CommerceCurrency commerceCurrency =
			CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), payment.getCurrencyCode(),
				payment.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(payment.getCurrencyId()));

		return _commercePaymentEntryService.addOrUpdateCommercePaymentEntry(
			externalReferenceCode,
			GetterUtil.getLong(
				_classNameLocalService.getClassNameId(
					payment.getRelatedItemName())),
			GetterUtil.getLong(payment.getRelatedItemId()),
			GetterUtil.getLong(payment.getChannelId()), payment.getAmount(),
			payment.getCallbackURL(), payment.getCancelURL(),
			commerceCurrency.getCode(), payment.getErrorMessages(),
			payment.getLanguageId(), payment.getComment(), payment.getPayload(),
			payment.getPaymentIntegrationKey(),
			GetterUtil.getInteger(payment.getPaymentStatus()),
			GetterUtil.getInteger(
				payment.getPaymentStatus(),
				CommerceOrderPaymentConstants.STATUS_PENDING),
			payment.getReasonKey(), payment.getRedirectURL(),
			payment.getTransactionCode(),
			GetterUtil.getInteger(
				payment.getType(), CommercePaymentEntryConstants.TYPE_PAYMENT),
			_serviceContextHelper.getServiceContext(contextUser));
	}

	private Map<String, Map<String, String>> _getActions(
			CommercePaymentEntry commercePaymentEntry)
		throws Exception {

		if (contextUriInfo == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"create",
			_addAction(
				getClass(), commercePaymentEntry, "postPayment", contextUriInfo)
		).put(
			"delete",
			_addAction(
				ActionKeys.DELETE, getClass(), commercePaymentEntry,
				"deletePayment", contextUriInfo)
		).put(
			"get",
			_addAction(
				ActionKeys.VIEW, getClass(), commercePaymentEntry, "getPayment",
				contextUriInfo)
		).put(
			"update",
			_addAction(
				ActionKeys.UPDATE, getClass(), commercePaymentEntry,
				"patchPayment", contextUriInfo)
		).build();
	}

	private Payment _toPayment(
			long commercePaymentEntryId, Locale locale,
			boolean acceptAllLanguages, User contextUser,
			UriInfo contextUriInfo, Map<String, Map<String, String>> actions)
		throws Exception {

		return _paymentDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				acceptAllLanguages, actions, _dtoConverterRegistry,
				commercePaymentEntryId, locale, contextUriInfo, contextUser));
	}

	private CommercePaymentEntry _updatePayment(
			CommercePaymentEntry commercePaymentEntry, Payment payment)
		throws Exception {

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				contextCompany.getCompanyId(),
				commercePaymentEntry.getCurrencyCode());

		try {
			commerceCurrency = CommerceCurrencyUtil.getCommerceCurrency(
				contextCompany.getCompanyId(), payment.getCurrencyCode(),
				payment.getCurrencyExternalReferenceCode(),
				GetterUtil.getLong(payment.getCurrencyId()));
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchCurrencyException);
			}
		}

		return _commercePaymentEntryService.updateCommercePaymentEntry(
			GetterUtil.getString(
				payment.getExternalReferenceCode(),
				commercePaymentEntry.getExternalReferenceCode()),
			commercePaymentEntry.getCommercePaymentEntryId(),
			GetterUtil.getLong(
				payment.getChannelId(),
				commercePaymentEntry.getCommerceChannelId()),
			(BigDecimal)GetterUtil.getNumber(
				payment.getAmount(), commercePaymentEntry.getAmount()),
			GetterUtil.getString(
				payment.getCallbackURL(),
				commercePaymentEntry.getCallbackURL()),
			GetterUtil.getString(
				payment.getCancelURL(), commercePaymentEntry.getCancelURL()),
			commerceCurrency.getCode(),
			GetterUtil.getString(
				payment.getErrorMessages(),
				commercePaymentEntry.getErrorMessages()),
			GetterUtil.getString(
				payment.getLanguageId(), commercePaymentEntry.getLanguageId()),
			GetterUtil.getString(
				payment.getComment(), commercePaymentEntry.getNote()),
			GetterUtil.getString(
				payment.getPayload(), commercePaymentEntry.getPayload()),
			GetterUtil.getString(
				payment.getPaymentIntegrationKey(),
				commercePaymentEntry.getPaymentIntegrationKey()),
			GetterUtil.getInteger(
				payment.getPaymentIntegrationType(),
				commercePaymentEntry.getPaymentIntegrationType()),
			GetterUtil.getInteger(
				payment.getPaymentStatus(),
				commercePaymentEntry.getPaymentStatus()),
			GetterUtil.getString(
				payment.getReasonKey(), commercePaymentEntry.getReasonKey()),
			GetterUtil.getString(
				payment.getRedirectURL(),
				commercePaymentEntry.getRedirectURL()),
			GetterUtil.getString(
				payment.getTransactionCode(),
				commercePaymentEntry.getTransactionCode()),
			GetterUtil.getInteger(
				payment.getType(), commercePaymentEntry.getType()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PaymentResourceImpl.class);

	private static final EntityModel _entityModel = new PaymentEntityModel();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.payment.model.CommercePaymentEntry)"
	)
	private ModelResourcePermission<CommercePaymentEntry>
		_commercePaymentEntryModelResourcePermission;

	@Reference
	private CommercePaymentEntryService _commercePaymentEntryService;

	@Reference
	private CommercePaymentGateway _commercePaymentGateway;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.admin.payment.internal.dto.v1_0.converter.PaymentDTOConverter)"
	)
	private DTOConverter<CommercePaymentEntry, Payment> _paymentDTOConverter;

	@Reference(
		target = "(resource.name=" + CommercePaymentEntryConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}