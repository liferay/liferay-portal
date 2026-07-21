/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet;

import com.liferay.digital.signature.configuration.DigitalSignatureConfiguration;
import com.liferay.digital.signature.configuration.DigitalSignatureConfigurationUtil;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSRecipientManager;
import com.liferay.digital.signature.manager.DSRecipientViewDefinitionManager;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.digital.signature.model.DSRecipientViewDefinition;
import com.liferay.digital.signature.web.internal.constants.DigitalSignatureWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=digital-signature-sign",
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.friendly-url-mapping=digital_signature",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Sign Digital Signature",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.copy-request-parameters=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/sign_digital_signature/view.jsp",
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SignDSPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				StringBundler.concat(
					_portal.getPathMain(), "/portal/login?redirect=",
					URLCodec.encodeURL(_portal.getCurrentURL(renderRequest))));

			return;
		}

		DigitalSignatureConfiguration digitalSignatureConfiguration =
			DigitalSignatureConfigurationUtil.getDigitalSignatureConfiguration(
				themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId());

		if (digitalSignatureConfiguration.enabled() &&
			digitalSignatureConfiguration.enableEmbeddedView()) {

			_setDSSigningConfig(
				digitalSignatureConfiguration, renderRequest, renderResponse,
				themeDisplay);
		}

		super.render(renderRequest, renderResponse);
	}

	private String _getSigningURL(
			String appOriginURL, String dsEnvelopeId,
			HttpServletRequest httpServletRequest, String returnURL,
			ThemeDisplay themeDisplay)
		throws Exception {

		User user = themeDisplay.getUser();

		String emailAddress = user.getEmailAddress();

		DSRecipientViewDefinition dsRecipientViewDefinition =
			new DSRecipientViewDefinition();

		dsRecipientViewDefinition.setAdditionalProps(
			HashMapBuilder.<String, Object>put(
				"frameAncestors",
				new String[] {
					_portal.getPortalURL(httpServletRequest), appOriginURL
				}
			).put(
				"messageOrigins", new String[] {appOriginURL}
			).build());
		dsRecipientViewDefinition.setAuthenticationMethod("none");
		dsRecipientViewDefinition.setDSClientUserId(
			String.valueOf(user.getUserId()));
		dsRecipientViewDefinition.setEmailAddress(emailAddress);
		dsRecipientViewDefinition.setReturnURL(returnURL);
		dsRecipientViewDefinition.setUserName(user.getFullName());

		return _dsRecipientViewDefinitionManager.addDSRecipientViewDefinition(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			dsEnvelopeId, dsRecipientViewDefinition);
	}

	private void _setDSSigningConfig(
		DigitalSignatureConfiguration digitalSignatureConfiguration,
		RenderRequest renderRequest, RenderResponse renderResponse,
		ThemeDisplay themeDisplay) {

		String dsEnvelopeId = ParamUtil.getString(
			renderRequest, "dsEnvelopeId");

		if (Validator.isNull(dsEnvelopeId)) {
			return;
		}

		User user = themeDisplay.getUser();

		DSRecipient dsRecipient = _dsRecipientManager.getDSRecipient(
			themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(),
			dsEnvelopeId, user.getEmailAddress());

		if (dsRecipient == null) {
			return;
		}

		String status = dsRecipient.getStatus();

		renderRequest.setAttribute(
			DigitalSignatureWebKeys.DIGITAL_SIGNATURE_RECIPIENT_STATUS, status);

		if (StringUtil.equalsIgnoreCase("completed", status) ||
			StringUtil.equalsIgnoreCase("declined", status)) {

			return;
		}

		String appOriginURL = "https://apps-d.docusign.com";
		String jsURL = "https://js-d.docusign.com/bundle.js";

		if (Objects.equals(
				digitalSignatureConfiguration.environment(), "production")) {

			appOriginURL = "https://apps.docusign.com";
			jsURL = "https://js.docusign.com/bundle.js";
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		String backURL = ParamUtil.getString(renderRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = themeDisplay.getURLHome();
		}

		String returnURL = PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/digital_signature/complete_ds_recipient_view"
		).setBackURL(
			backURL
		).setParameter(
			"dsEnvelopeId", dsEnvelopeId
		).buildString();

		if (returnURL.startsWith("/")) {
			returnURL = _portal.getPortalURL(httpServletRequest) + returnURL;
		}

		try {
			renderRequest.setAttribute(
				DigitalSignatureWebKeys.DIGITAL_SIGNATURE_SIGNING_CONFIG,
				JSONUtil.put(
					"integrationKey",
					digitalSignatureConfiguration.integrationKey()
				).put(
					"jsURL", jsURL
				).put(
					"returnURL", returnURL
				).put(
					"signingURL",
					_getSigningURL(
						appOriginURL, dsEnvelopeId, httpServletRequest,
						returnURL, themeDisplay)
				));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to create signing view for envelope " + dsEnvelopeId,
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SignDSPortlet.class);

	@Reference
	private DSRecipientManager _dsRecipientManager;

	@Reference
	private DSRecipientViewDefinitionManager _dsRecipientViewDefinitionManager;

	@Reference
	private Portal _portal;

}