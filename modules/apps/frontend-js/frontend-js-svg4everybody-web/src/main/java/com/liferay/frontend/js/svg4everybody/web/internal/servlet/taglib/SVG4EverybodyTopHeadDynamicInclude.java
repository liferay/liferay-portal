/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.svg4everybody.web.internal.servlet.taglib;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.ThemeSpritemapCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.BundleScriptAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryce Osterhaus
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MAX_VALUE,
	service = DynamicInclude.class
)
public class SVG4EverybodyTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		boolean cdnDynamicResourcesEnabled = true;

		try {
			cdnDynamicResourcesEnabled = _portal.isCDNDynamicResourcesEnabled(
				httpServletRequest);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to verify if CDN dynamic resources are enabled",
					portalException);
			}
		}

		boolean cdnHostEnabled = false;

		try {
			String cdnHost = _portal.getCDNHost(httpServletRequest);

			cdnHostEnabled = !cdnHost.isEmpty();
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get CDN host", portalException);
			}
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ThemeSpritemapCET themeSpritemapCET = _getThemeSpritemapCET(
			themeDisplay.getLayout());

		if (!cdnHostEnabled &&
			((themeSpritemapCET == null) ||
			 !themeSpritemapCET.isEnableSVG4Everybody())) {

			return;
		}

		PrintWriter printWriter = httpServletResponse.getWriter();

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		for (String jsFileName : _JS_FILE_NAMES) {
			printWriter.print("<script");
			printWriter.write(
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
			printWriter.print(" data-senna-track=\"permanent\" src=\"");

			BundleScriptAbsolutePortalURLBuilder
				bundleScriptAbsolutePortalURLBuilder =
					absolutePortalURLBuilder.forBundleScript(
						_bundleContext.getBundle(), jsFileName);

			if (!cdnDynamicResourcesEnabled) {
				bundleScriptAbsolutePortalURLBuilder.ignoreCDNHost();
			}

			printWriter.print(bundleScriptAbsolutePortalURLBuilder.build());

			printWriter.println("\" type=\"text/javascript\"></script>");
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/top_head.jsp#pre");
	}

	@Activate
	@Modified
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	private CET _getCET(
		long classNameId, long classPK, long companyId,
		List<ClientExtensionEntryRel> clientExtensionEntryRels) {

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			if ((clientExtensionEntryRel.getClassNameId() == classNameId) &&
				(clientExtensionEntryRel.getClassPK() == classPK)) {

				return _cetManager.getCET(
					companyId,
					clientExtensionEntryRel.getCETExternalReferenceCode());
			}
		}

		return null;
	}

	private ThemeSpritemapCET _getThemeSpritemapCET(Layout layout) {
		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			_clientExtensionEntryRelLocalService.getClientExtensionEntryRels(
				ClientExtensionEntryConstants.TYPE_THEME_SPRITEMAP);

		if (clientExtensionEntryRels.isEmpty()) {
			return null;
		}

		CET cet = _getCET(
			_portal.getClassNameId(Layout.class), layout.getPlid(),
			layout.getCompanyId(), clientExtensionEntryRels);

		if (cet == null) {
			cet = _getCET(
				_portal.getClassNameId(Layout.class),
				layout.getMasterLayoutPlid(), layout.getCompanyId(),
				clientExtensionEntryRels);
		}

		if (cet == null) {
			LayoutSet layoutSet = layout.getLayoutSet();

			cet = _getCET(
				_portal.getClassNameId(LayoutSet.class),
				layoutSet.getLayoutSetId(), layout.getCompanyId(),
				clientExtensionEntryRels);
		}

		if (cet != null) {
			return (ThemeSpritemapCET)cet;
		}

		return null;
	}

	private static final String[] _JS_FILE_NAMES = {"/index.js"};

	private static final Log _log = LogFactoryUtil.getLog(
		SVG4EverybodyTopHeadDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private volatile BundleContext _bundleContext;

	@Reference
	private CETManager _cetManager;

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	@Reference
	private Portal _portal;

}