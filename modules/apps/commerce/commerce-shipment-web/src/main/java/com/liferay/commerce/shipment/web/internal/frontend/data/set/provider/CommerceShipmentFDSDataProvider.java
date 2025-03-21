/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryModel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.frontend.model.Shipment;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.SHIPMENTS,
	service = FDSDataProvider.class
)
public class CommerceShipmentFDSDataProvider
	implements FDSDataProvider<Shipment> {

	@Override
	public List<Shipment> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.fetchCommerceOrder(
			commerceOrderId);

		List<CommerceShipment> commerceShipments;

		if (commerceOrder != null) {
			commerceShipments =
				_commerceShipmentService.getCommerceShipmentsByOrderId(
					commerceOrderId, fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition());
		}
		else {
			long companyId = _portal.getCompanyId(httpServletRequest);

			commerceShipments = _commerceShipmentService.getCommerceShipments(
				companyId, _getCommerceChannelGroupIds(companyId),
				_getAccountEntryIds(_portal.getUserId(httpServletRequest)),
				fdsKeywords.getKeywords(), null, false,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition());
		}

		User user = _portal.getUser(httpServletRequest);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM,
			_portal.getLocale(httpServletRequest), user.getTimeZone());

		Format dateFormat = FastDateFormatFactoryUtil.getDate(
			DateFormat.MEDIUM, _portal.getLocale(httpServletRequest),
			user.getTimeZone());

		return TransformUtil.transform(
			commerceShipments,
			commerceShipment -> {
				CommerceChannel commerceChannel =
					_commerceChannelLocalService.
						getCommerceChannelByOrderGroupId(
							commerceShipment.getGroupId());

				String expectedDate = null;

				if (commerceShipment.getExpectedDate() != null) {
					expectedDate = dateFormat.format(
						commerceShipment.getExpectedDate());
				}

				String shippingDate = null;

				if (commerceShipment.getShippingDate() != null) {
					shippingDate = dateFormat.format(
						commerceShipment.getShippingDate());
				}

				return new Shipment(
					commerceShipment.getAccountEntryName(),
					_getDescriptiveAddress(commerceShipment),
					commerceChannel.getName(),
					dateTimeFormat.format(commerceShipment.getCreateDate()),
					expectedDate, shippingDate,
					commerceShipment.getCommerceShipmentId(),
					new LabelField(
						CommerceShipmentConstants.getShipmentLabelStyle(
							commerceShipment.getStatus()),
						_language.get(
							httpServletRequest,
							CommerceShipmentConstants.getShipmentStatusLabel(
								commerceShipment.getStatus()))),
					commerceShipment.getTrackingNumber());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.fetchCommerceOrder(
			commerceOrderId);

		if (commerceOrder != null) {
			return _commerceShipmentService.getCommerceShipmentsCountByOrderId(
				commerceOrderId);
		}

		long companyId = _portal.getCompanyId(httpServletRequest);

		return _commerceShipmentService.getCommerceShipmentsCount(
			companyId, _getCommerceChannelGroupIds(companyId),
			_getAccountEntryIds(_portal.getUserId(httpServletRequest)),
			fdsKeywords.getKeywords(), null, false);
	}

	private long[] _getAccountEntryIds(long userId) throws PortalException {
		if (_portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(), null,
				CommerceActionKeys.MANAGE_ALL_ACCOUNTS)) {

			return null;
		}

		List<AccountEntry> accountEntries =
			_accountEntryLocalService.getUserAccountEntries(
				userId, AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
				StringPool.BLANK,
				_commerceAccountHelper.toAccountEntryTypes(
					CommerceChannelConstants.SITE_TYPE_B2X),
				WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		long[] accountEntriesIds = ListUtil.toLongArray(
			accountEntries, AccountEntryModel::getAccountEntryId);

		if (accountEntriesIds.length != 0) {
			return accountEntriesIds;
		}

		return null;
	}

	private long[] _getCommerceChannelGroupIds(long companyId)
		throws PortalException {

		return TransformUtil.transformToLongArray(
			_commerceChannelLocalService.search(companyId),
			CommerceChannel::getGroupId);
	}

	private String _getDescriptiveAddress(CommerceShipment commerceShipment)
		throws PortalException {

		CommerceAddress commerceAddress =
			commerceShipment.fetchCommerceAddress();

		if (commerceAddress == null) {
			return StringPool.BLANK;
		}

		Region region = commerceAddress.getRegion();

		StringBundler sb = new StringBundler((region == null) ? 5 : 7);

		sb.append(commerceAddress.getStreet1());
		sb.append(StringPool.SPACE);
		sb.append(commerceAddress.getCity());
		sb.append(StringPool.NEW_LINE);

		if (region != null) {
			sb.append(region.getRegionCode());
			sb.append(StringPool.SPACE);
		}

		sb.append(commerceAddress.getZip());

		return sb.toString();
	}

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceShipmentService _commerceShipmentService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceConstants.RESOURCE_NAME_COMMERCE_SHIPMENT + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}