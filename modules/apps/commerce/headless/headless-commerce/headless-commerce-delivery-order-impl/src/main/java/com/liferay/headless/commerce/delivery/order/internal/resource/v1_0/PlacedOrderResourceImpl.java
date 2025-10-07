/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.resource.v1_0;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryService;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStepRegistry;
import com.liferay.headless.commerce.core.util.ExpandoUtil;
import com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.internal.odata.entity.v1_0.PlacedOrderEntityModel;
import com.liferay.headless.commerce.delivery.order.resource.v1_0.PlacedOrderResource;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.MultivaluedMap;

import java.security.Key;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 * @author Gianmarco Brunialti Masera
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/placed-order.properties",
	scope = ServiceScope.PROTOTYPE, service = PlacedOrderResource.class
)
public class PlacedOrderResourceImpl extends BasePlacedOrderResourceImpl {

	@Override
	public Page<PlacedOrder> getChannelAccountPlacedOrdersPage(
			Long accountId, Long channelId, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		return SearchUtil.search(
			null,
			booleanQuery -> {
			},
			filter, CommerceOrder.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceAccountIds", new long[] {accountId});
				searchContext.setAttribute("negateOrderStatuses", Boolean.TRUE);
				searchContext.setAttribute(
					"orderStatuses",
					new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(
					new long[] {commerceChannel.getGroupId()});

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setUserId(0);
			},
			sorts,
			document -> _toPlacedOrder(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public Page<PlacedOrder>
			getChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, String search,
				Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		AccountEntry accountEntry =
			_accountEntryService.getAccountEntryByExternalReferenceCode(
				accountExternalReferenceCode, contextCompany.getCompanyId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					channelExternalReferenceCode,
					contextCompany.getCompanyId());

		return getChannelAccountPlacedOrdersPage(
			accountEntry.getAccountEntryId(),
			commerceChannel.getCommerceChannelId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Page<PlacedOrder> getChannelByExternalReferenceCodePlacedOrdersPage(
			String externalReferenceCode, String search, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.
				getCommerceChannelByExternalReferenceCode(
					externalReferenceCode, contextCompany.getCompanyId());

		return getChannelPlacedOrdersPage(
			commerceChannel.getCommerceChannelId(), search, filter, pagination,
			sorts);
	}

	@Override
	public Page<PlacedOrder> getChannelPlacedOrdersPage(
			Long channelId, String search, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannel(channelId);

		return SearchUtil.search(
			null,
			booleanQuery -> {
			},
			filter, CommerceOrder.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setAttribute(
					"commerceAccountIds",
					_getCommerceAccountIds(commerceChannel.getGroupId()));
				searchContext.setAttribute("negateOrderStatuses", Boolean.TRUE);
				searchContext.setAttribute(
					"orderStatuses",
					new int[] {CommerceOrderConstants.ORDER_STATUS_OPEN});
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(
					new long[] {commerceChannel.getGroupId()});

				if (Validator.isNotNull(search)) {
					searchContext.setKeywords(search);
				}

				searchContext.setUserId(0);
			},
			sorts,
			document -> _toPlacedOrder(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK))));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public PlacedOrder getPlacedOrder(Long placedOrderId) throws Exception {
		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (commerceOrder.isOpen()) {
			throw new NoSuchOrderException();
		}

		return _toPlacedOrder(commerceOrder.getCommerceOrderId());
	}

	@Override
	public PlacedOrder getPlacedOrderByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return getPlacedOrder(commerceOrder.getCommerceOrderId());
	}

	@Override
	public String getPlacedOrderByExternalReferenceCodePaymentURL(
			String externalReferenceCode, String callbackURL)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		return getPlacedOrderPaymentURL(
			commerceOrder.getCommerceOrderId(), callbackURL);
	}

	@Override
	public String getPlacedOrderPaymentURL(
			Long placedOrderId, String callbackURL)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (commerceOrder.isOpen()) {
			throw new NoSuchOrderException();
		}

		_initThemeDisplay(commerceOrder);

		StringBundler sb = new StringBundler(14);

		sb.append(_portal.getPortalURL(contextHttpServletRequest));
		sb.append(_portal.getPathModule());
		sb.append(CharPool.SLASH);
		sb.append(CommercePaymentMethodConstants.SERVLET_PATH);
		sb.append("?groupId=");
		sb.append(commerceOrder.getGroupId());
		sb.append(StringPool.AMPERSAND);

		if (commerceOrder.isGuestOrder()) {
			sb.append("guestToken=");

			Key key = contextCompany.getKeyObj();

			sb.append(
				_encryptor.encrypt(
					key, String.valueOf(commerceOrder.getCommerceOrderId())));

			sb.append(StringPool.AMPERSAND);
		}

		sb.append("nextStep=");

		if (Validator.isNotNull(callbackURL)) {
			sb.append(callbackURL);
		}
		else {
			sb.append(
				URLCodec.encodeURL(
					_getPlacedOrderConfirmationCheckoutStepURL(commerceOrder)));
		}

		sb.append("&uuid=");
		sb.append(commerceOrder.getUuid());

		return sb.toString();
	}

	@Override
	public PlacedOrder patchPlacedOrder(
			Long placedOrderId, PlacedOrder placedOrder)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			placedOrderId);

		if (commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to patch an open order");
		}

		_updateOrder(commerceOrder, placedOrder);

		return _toPlacedOrder(commerceOrder.getCommerceOrderId());
	}

	@Override
	public PlacedOrder patchPlacedOrderByExternalReferenceCode(
			String externalReferenceCode, PlacedOrder placedOrder)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		if (commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to patch an open order");
		}

		_updateOrder(commerceOrder, placedOrder);

		return _toPlacedOrder(commerceOrder.getCommerceOrderId());
	}

	private long[] _getCommerceAccountIds(long groupId) throws PortalException {
		PortletResourcePermission portletResourcePermission =
			_commerceOrderModelResourcePermission.
				getPortletResourcePermission();

		if (portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), groupId,
				CommerceOrderActionKeys.MANAGE_ALL_ACCOUNTS)) {

			return null;
		}

		return _commerceAccountHelper.getUserCommerceAccountIds(
			contextUser.getUserId(), groupId);
	}

	private String _getPlacedOrderConfirmationCheckoutStepURL(
			CommerceOrder commerceOrder)
		throws Exception {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				contextHttpServletRequest,
				CommercePortletKeys.COMMERCE_CHECKOUT,
				PortletProvider.Action.VIEW)
		).setParameter(
			"checkoutStepName",
			() -> {
				CommerceCheckoutStep commerceCheckoutStep =
					_commerceCheckoutStepRegistry.getCommerceCheckoutStep(
						"order-confirmation");

				return commerceCheckoutStep.getName();
			}
		).setParameter(
			"commerceOrderUuid", commerceOrder.getUuid()
		).buildString();
	}

	private void _initThemeDisplay(CommerceOrder commerceOrder)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			return;
		}

		ServicePreAction servicePreAction = new ServicePreAction();

		HttpServletResponse httpServletResponse =
			new DummyHttpServletResponse();

		servicePreAction.servicePre(
			contextHttpServletRequest, httpServletResponse, false);

		ThemeServicePreAction themeServicePreAction =
			new ThemeServicePreAction();

		themeServicePreAction.run(
			contextHttpServletRequest, httpServletResponse);

		themeDisplay = (ThemeDisplay)contextHttpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		themeDisplay.setScopeGroupId(commerceChannel.getSiteGroupId());
	}

	private PlacedOrder _toPlacedOrder(long commerceOrderId) throws Exception {
		return _placedOrderDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, commerceOrderId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private void _updateOrder(
			CommerceOrder commerceOrder, PlacedOrder placedOrder)
		throws Exception {

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceOrder.getGroupId(),
			null, commerceOrder.getCommerceOrderId(),
			contextCompany.getCompanyId());

		_commerceOrderEngine.updateCommerceOrder(
			commerceOrder.getExternalReferenceCode(),
			commerceOrder.getCommerceOrderId(),
			commerceOrder.getBillingAddressId(),
			commerceOrder.getCommerceShippingMethodId(),
			commerceOrder.getShippingAddressId(),
			commerceOrder.getAdvanceStatus(),
			commerceOrder.getCommercePaymentMethodKey(),
			GetterUtil.getString(
				placedOrder.getName(), commerceOrder.getName()),
			GetterUtil.get(
				placedOrder.getPurchaseOrderNumber(),
				commerceOrder.getPurchaseOrderNumber()),
			commerceOrder.getShippingAmount(),
			commerceOrder.getShippingOptionName(),
			commerceOrder.getShippingWithTaxAmount(),
			commerceOrder.getSubtotal(),
			commerceOrder.getSubtotalWithTaxAmount(),
			commerceOrder.getTaxAmount(), commerceOrder.getTotal(),
			commerceOrder.getTotalDiscountAmount(),
			commerceOrder.getTotalWithTaxAmount(), commerceContext, false);

		commerceOrder = _commerceOrderService.updatePrintedNote(
			commerceOrder.getCommerceOrderId(),
			GetterUtil.get(
				placedOrder.getPrintedNote(), commerceOrder.getPrintedNote()));

		Map<String, ?> customFields = placedOrder.getCustomFields();

		if ((customFields != null) && !customFields.isEmpty()) {
			ExpandoUtil.updateExpando(
				contextCompany.getCompanyId(), CommerceOrder.class,
				commerceOrder.getPrimaryKey(), customFields);
		}
	}

	private static final EntityModel _entityModel =
		new PlacedOrderEntityModel();

	@Reference
	private AccountEntryService _accountEntryService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepRegistry _commerceCheckoutStepRegistry;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private Encryptor _encryptor;

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.order.internal.dto.v1_0.converter.PlacedOrderDTOConverter)"
	)
	private DTOConverter<CommerceOrder, PlacedOrder> _placedOrderDTOConverter;

	@Reference
	private Portal _portal;

}