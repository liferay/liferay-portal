/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.iframe.web.internal.portlet;

import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.iframe.web.internal.configuration.IFramePortletInstanceConfiguration;
import com.liferay.iframe.web.internal.constants.IFramePortletKeys;
import com.liferay.iframe.web.internal.constants.IFrameWebKeys;
import com.liferay.iframe.web.internal.display.context.IFrameDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-iframe",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=IFrame",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + IFramePortletKeys.IFRAME,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class IFramePortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String src = null;

		try {
			src = _transformSrc(renderRequest, renderResponse);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		renderRequest.setAttribute(IFrameWebKeys.IFRAME_SRC, src);

		if (Validator.isNull(src) || src.equals(Http.HTTP_WITH_SLASH) ||
			src.equals(Http.HTTPS_WITH_SLASH)) {

			include("/portlet_not_setup.jsp", renderRequest, renderResponse);
		}
		else {
			super.doView(renderRequest, renderResponse);
		}
	}

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(_ALIAS, IFramePortletKeys.IFRAME);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	private String _transformSrc(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		IFrameDisplayContext iFrameDisplayContext = new IFrameDisplayContext(
			renderRequest);

		IFramePortletInstanceConfiguration iFramePortletInstanceConfiguration =
			iFrameDisplayContext.getIFramePortletInstanceConfiguration();

		String src = ParamUtil.getString(
			renderRequest, "src", iFramePortletInstanceConfiguration.src());

		if (!iFramePortletInstanceConfiguration.auth()) {
			return src;
		}

		String authType = iFrameDisplayContext.getAuthType();

		if (authType.equals("form")) {
			src = PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCPath(
				"/proxy.jsp"
			).buildString();
		}

		return src;
	}

	private static final String _ALIAS = "iframe";

	private static final Log _log = LogFactoryUtil.getLog(IFramePortlet.class);

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.iframe.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}