/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.util;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;

/**
 * @author Brian I. Kim
 */
public class DSCommerceOrderAttachmentUtil {

	public static boolean isEnabled(CommerceOrder commerceOrder) {
		if (!FeatureFlagManagerUtil.isEnabled(
				commerceOrder.getCompanyId(), "LPD-69290")) {

			return false;
		}

		CommerceChannel commerceChannel =
			CommerceChannelLocalServiceUtil.fetchCommerceChannelByGroupClassPK(
				commerceOrder.getGroupId());

		if (commerceChannel == null) {
			return false;
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				commerceOrder.getCompanyId(), commerceChannel.getSiteGroupId());

		if (digitalSignatureConfiguration.embeddedViewEnabled() &&
			digitalSignatureConfiguration.enabled()) {

			return true;
		}

		return false;
	}

}