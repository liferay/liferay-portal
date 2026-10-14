/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.util;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.exception.DSCommerceOrderAttachmentException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.exception.DSRequestException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Brian I. Kim
 */
public class DSCommerceOrderAttachmentUtil {

	public static List<User> getAccountUsers(CommerceOrder commerceOrder)
		throws PortalException {

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		return ListUtil.filter(
			accountEntry.fetchUsers(),
			user ->
				(user.getType() == UserConstants.TYPE_REGULAR) &&
				user.isActive());
	}

	public static String getErrorMessage(
		Language language, Locale locale, PortalException portalException) {

		String key = _errorMessageKeys.get(portalException.getClass());

		if (Validator.isNull(key)) {
			return portalException.getMessage();
		}

		return language.get(locale, key);
	}

	public static JSONObject getUserJSONObject(User user) {
		return JSONUtil.put(
			"emailAddress", user.getEmailAddress()
		).put(
			"name", user.getFullName()
		).put(
			"userId", user.getUserId()
		);
	}

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

	private static final Map<Class<?>, String> _errorMessageKeys =
		HashMapBuilder.<Class<?>, String>put(
			DSCommerceOrderAttachmentException.MustHaveDSRequest.class,
			"no-signature-request-was-found-for-this-document"
		).put(
			DSCommerceOrderAttachmentException.MustHaveEmbeddedViewEnabled.
				class,
			"digital-signature-is-not-enabled-for-this-order"
		).put(
			DSCommerceOrderAttachmentException.MustHaveRecipient.class,
			"at-least-one-recipient-is-required"
		).put(
			DSCommerceOrderAttachmentException.MustHaveSupportedFileExtension.
				class,
			"this-file-extension-is-not-supported-by-docusign"
		).put(
			DSCommerceOrderAttachmentException.
				MustNotHaveMultipleCountersigners.class,
			"only-one-internal-countersigner-is-allowed"
		).put(
			DSRequestException.MustBeActive.class,
			"this-signature-request-is-no-longer-active"
		).put(
			DSRequestException.MustHaveEmbeddedViewEnabled.class,
			"digital-signature-is-not-enabled-for-this-order"
		).put(
			DSRequestException.MustHaveReplaceableFileEntry.class,
			"this-document-already-has-an-active-or-completed-signature-request"
		).put(
			DSRequestException.MustHaveValidRecipient.class,
			"one-or-more-recipients-cannot-sign-this-order"
		).build();

}