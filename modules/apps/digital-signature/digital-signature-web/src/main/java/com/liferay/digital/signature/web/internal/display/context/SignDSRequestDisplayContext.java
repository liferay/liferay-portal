/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.display.context;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.constants.DigitalSignatureConstants;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.key.secret.SecretResolverUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

/**
 * @author Danny Situ
 */
public class SignDSRequestDisplayContext {

	public SignDSRequestDisplayContext(
		DigitalSignatureConfiguration digitalSignatureConfiguration,
		DSRequest dsRequest, GroupLocalService groupLocalService, Portal portal,
		String recipientViewURL, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_digitalSignatureConfiguration = digitalSignatureConfiguration;
		_dsRequest = dsRequest;
		_groupLocalService = groupLocalService;
		_portal = portal;
		_recipientViewURL = recipientViewURL;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		_backURL = _portal.escapeRedirect(
			ParamUtil.getString(_renderRequest, "backURL"));

		if (Validator.isNotNull(_backURL)) {
			return _backURL;
		}

		Group group = _groupLocalService.fetchGroup(
			_dsRequest.getSiteGroupId());

		if (group != null) {
			_backURL = group.getDisplayURL(_themeDisplay);

			if (Validator.isNotNull(_backURL)) {
				return _backURL;
			}
		}

		_backURL = _themeDisplay.getURLHome();

		return _backURL;
	}

	public String getCssClass() {
		if (_themeDisplay.isStatePopUp()) {
			return "digital-signature-sign-container " +
				"digital-signature-sign-container-pop-up";
		}

		return "digital-signature-sign-container";
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"backURL", getBackURL()
		).put(
			"completeDSRecipientViewURL",
			PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/digital_signature/complete_ds_recipient_view"
			).setBackURL(
				getBackURL()
			).setParameter(
				"dsRequestId", _dsRequest.getDSRequestId()
			).buildString()
		).put(
			"containerId", _renderResponse.getNamespace() + "signContainer"
		).put(
			"integrationKey",
			SecretResolverUtil.resolve(
				_dsRequest.getCompanyId(),
				_digitalSignatureConfiguration.integrationKey())
		).put(
			"jsURL",
			DigitalSignatureConstants.getJSURL(
				_digitalSignatureConfiguration.environment())
		).put(
			"recipientViewURL", _recipientViewURL
		).build();
	}

	private String _backURL;
	private final DigitalSignatureConfiguration _digitalSignatureConfiguration;
	private final DSRequest _dsRequest;
	private final GroupLocalService _groupLocalService;
	private final Portal _portal;
	private final String _recipientViewURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}