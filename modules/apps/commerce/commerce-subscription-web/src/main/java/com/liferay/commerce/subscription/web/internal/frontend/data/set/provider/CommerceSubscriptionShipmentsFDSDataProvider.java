/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.web.internal.constants.CommerceSubscriptionFDSNames;
import com.liferay.commerce.subscription.web.internal.model.Label;
import com.liferay.commerce.subscription.web.internal.model.Link;
import com.liferay.commerce.subscription.web.internal.model.Shipment;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSubscriptionFDSNames.SUBSCRIPTION_SHIPMENTS,
	service = FDSDataProvider.class
)
public class CommerceSubscriptionShipmentsFDSDataProvider
	implements FDSDataProvider<Shipment> {

	@Override
	public List<Shipment> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			httpServletRequest, "commerceSubscriptionEntryId");

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		List<CommerceShipmentItem> commerceShipmentItems =
			_commerceShipmentItemService.
				getCommerceShipmentItemsByCommerceOrderItemId(
					commerceSubscriptionEntry.getCommerceOrderItemId());

		return TransformUtil.transform(
			commerceShipmentItems,
			commerceShipmentItem -> {
				CommerceShipment commerceShipment =
					_commerceShipmentService.getCommerceShipment(
						commerceShipmentItem.getCommerceShipmentId());

				CommerceOrderItem commerceOrderItem =
					_commerceOrderItemService.getCommerceOrderItem(
						commerceShipmentItem.getCommerceOrderItemId());

				CommerceAddress commerceAddress =
					_commerceAddressService.getCommerceAddress(
						commerceShipment.getCommerceAddressId());

				return new Shipment(
					dateTimeFormat.format(commerceShipment.getCreateDate()),
					new Link(
						String.valueOf(
							commerceShipment.getCommerceShipmentId()),
						_getEditShipmentURL(
							commerceShipment.getCommerceShipmentId(),
							httpServletRequest)),
					_getShipmentStatus(commerceShipment),
					new Link(
						String.valueOf(commerceOrderItem.getCommerceOrderId()),
						_getEditCommerceOrderURL(
							commerceOrderItem.getCommerceOrderId(),
							httpServletRequest)),
					StringBundler.concat(
						commerceAddress.getName(), CharPool.SPACE,
						commerceAddress.getStreet1()),
					new Link(commerceShipment.getTrackingNumber(), ""));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			httpServletRequest, "commerceSubscriptionEntryId");

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		return _commerceShipmentItemService.
			getCommerceShipmentItemsCountByCommerceOrderItemId(
				commerceSubscriptionEntry.getCommerceOrderItemId());
	}

	private String _getEditCommerceOrderURL(
			long commerceOrderId, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceOrder.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_open_order_content/edit_commerce_order"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceOrderId", commerceOrderId
		).buildString();
	}

	private String _getEditShipmentURL(
			long commerceShipmentId, HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest, CommerceShipment.class.getName(),
				PortletProvider.Action.MANAGE)
		).setMVCRenderCommandName(
			"/commerce_shipment/edit_commerce_shipment"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"commerceShipmentId", commerceShipmentId
		).buildString();
	}

	private Label _getShipmentStatus(CommerceShipment commerceShipment) {
		if (Objects.equals(
				commerceShipment.getStatus(),
				CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED)) {

			return new Label(
				CommerceShipmentConstants.getShipmentStatusLabel(
					CommerceShipmentConstants.SHIPMENT_STATUS_SHIPPED),
				Label.SUCCESS);
		}
		else if (Objects.equals(
					commerceShipment.getStatus(),
					CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED)) {

			return new Label(
				CommerceShipmentConstants.getShipmentStatusLabel(
					CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED),
				Label.INFO);
		}
		else if (Objects.equals(
					commerceShipment.getStatus(),
					CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING)) {

			return new Label(
				CommerceShipmentConstants.getShipmentStatusLabel(
					CommerceShipmentConstants.SHIPMENT_STATUS_PROCESSING),
				Label.INFO);
		}
		else if (Objects.equals(
					commerceShipment.getStatus(),
					CommerceShipmentConstants.
						SHIPMENT_STATUS_READY_TO_BE_SHIPPED)) {

			return new Label(
				CommerceShipmentConstants.getShipmentStatusLabel(
					CommerceShipmentConstants.
						SHIPMENT_STATUS_READY_TO_BE_SHIPPED),
				Label.INFO);
		}

		return null;
	}

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private CommerceShipmentService _commerceShipmentService;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private Portal _portal;

}