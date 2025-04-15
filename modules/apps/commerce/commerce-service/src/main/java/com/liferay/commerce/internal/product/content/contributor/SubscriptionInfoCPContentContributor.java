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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "commerce.product.content.contributor.name=" + CPContentContributorConstants.SUBSCRIPTION_INFO,
	service = CPContentContributor.class
)
public class SubscriptionInfoCPContentContributor
	implements CPContentContributor {

	@Override
	public String getName() {
		return CPContentContributorConstants.SUBSCRIPTION_INFO;
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

		if (cpDefinition.isSubscriptionEnabled()) {
			jsonObject.put(
				CPContentContributorConstants.SUBSCRIPTION_INFO,
				_getSubscriptionInfo(
					cpInstance.getCPSubscriptionInfo(), httpServletRequest));
		}

		return jsonObject;
	}

	private String _getPeriodKey(long count, String period) {
		if (count != 1) {
			return StringUtil.toLowerCase(period + CharPool.LOWER_CASE_S);
		}

		return period;
	}

	private String _getSubscriptionInfo(
		CPSubscriptionInfo cpSubscriptionInfo,
		HttpServletRequest httpServletRequest) {

		if (cpSubscriptionInfo == null) {
			return StringPool.BLANK;
		}

		long maxSubscriptionCycles =
			cpSubscriptionInfo.getMaxSubscriptionCycles();
		int subscriptionLength = cpSubscriptionInfo.getSubscriptionLength();

		String period = StringPool.BLANK;

		CPSubscriptionType cpSubscriptionType =
			_cpSubscriptionTypeRegistry.getCPSubscriptionType(
				cpSubscriptionInfo.getSubscriptionType());

		if (cpSubscriptionType != null) {
			period = cpSubscriptionType.getLabel(LocaleUtil.US);
		}

		StringBundler sb = new StringBundler(
			(maxSubscriptionCycles > 0) ? 6 : 3);

		sb.append(_language.get(httpServletRequest, "payment-subscription"));
		sb.append(StringPool.OPEN_PARENTHESIS);

		String subscriptionPeriodKey = _getPeriodKey(
			subscriptionLength, period);

		String subscriptionMessage = _language.format(
			httpServletRequest, "every-x-x",
			new Object[] {subscriptionLength, subscriptionPeriodKey}, true);

		sb.append(subscriptionMessage);

		sb.append(StringPool.CLOSE_PARENTHESIS);

		if (maxSubscriptionCycles > 0) {
			long totalLength = subscriptionLength * maxSubscriptionCycles;

			sb.append(StringPool.SPACE);

			String durationPeriodKey = _getPeriodKey(totalLength, period);

			String durationMessage = _language.format(
				httpServletRequest, "duration-x-x",
				new Object[] {totalLength, durationPeriodKey}, true);

			sb.append(durationMessage);

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