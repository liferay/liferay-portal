/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.product.content.contributor;

import com.liferay.commerce.product.constants.CPContentContributorConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPSubscriptionInfo;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPContentContributor;
import com.liferay.commerce.product.util.CPSubscriptionType;
import com.liferay.commerce.product.util.CPSubscriptionTypeRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.DELIVERY_SUBSCRIPTION_INFO,
	service = CPContentContributor.class
)
public class DeliverySubscriptionInfoCPContentContributor
	implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.DELIVERY_SUBSCRIPTION_INFO;
	}

	@Override
	public JSONObject getValue(
			CPInstance cpInstance, HttpServletRequest httpServletRequest)
		throws PortalException {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		if (cpInstance == null) {
			return jsonObject;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
				_portal.getScopeGroupId(httpServletRequest));

		if (commerceChannel == null) {
			return jsonObject;
		}

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		if (cpDefinition.isDeliverySubscriptionEnabled()) {
			jsonObject.put(
				CPContentContributorConstants.DELIVERY_SUBSCRIPTION_INFO,
				_getSubscriptionInfo(
					cpInstance.getCPSubscriptionInfo(), httpServletRequest));
		}

		return jsonObject;
	}

	private String _getPeriodKey(long count, String period) {
		if (count != 1) {
			return StringUtil.toLowerCase(TextFormatter.formatPlural(period));
		}

		return period;
	}

	private String _getSubscriptionInfo(
		CPSubscriptionInfo cpSubscriptionInfo,
		HttpServletRequest httpServletRequest) {

		if (cpSubscriptionInfo == null) {
			return StringPool.BLANK;
		}

		long maxDeliverySubscriptionCycles =
			cpSubscriptionInfo.getDeliveryMaxSubscriptionCycles();
		int deliverySubscriptionLength =
			cpSubscriptionInfo.getDeliverySubscriptionLength();

		String period = StringPool.BLANK;

		CPSubscriptionType cpSubscriptionType =
			_cpSubscriptionTypeRegistry.getCPSubscriptionType(
				cpSubscriptionInfo.getDeliverySubscriptionType());

		if (cpSubscriptionType != null) {
			period = cpSubscriptionType.getLabel(
				_portal.getLocale(httpServletRequest));
		}

		StringBundler sb = new StringBundler(
			(maxDeliverySubscriptionCycles > 0) ? 6 : 3);

		sb.append(_language.get(httpServletRequest, "delivery-subscription"));
		sb.append(StringPool.OPEN_PARENTHESIS);

		String deliverySubscriptionPeriodKey = _getPeriodKey(
			deliverySubscriptionLength, period);

		String deliverySubscriptionMessage = _language.format(
			httpServletRequest, "every-x-x",
			new Object[] {
				deliverySubscriptionLength, deliverySubscriptionPeriodKey
			},
			true);

		sb.append(deliverySubscriptionMessage);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		if (maxDeliverySubscriptionCycles > 0) {
			long totalLength =
				deliverySubscriptionLength * maxDeliverySubscriptionCycles;

			sb.append(StringPool.SPACE);

			String deliveryDurationPeriodKey = _getPeriodKey(
				totalLength, period);

			String deliveryDurationMessage = _language.format(
				httpServletRequest, "duration-x-x",
				new Object[] {totalLength, deliveryDurationPeriodKey}, true);

			sb.append(deliveryDurationMessage);

			sb.append(StringPool.SPACE);
		}

		return sb.toString();
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPSubscriptionTypeRegistry _cpSubscriptionTypeRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}