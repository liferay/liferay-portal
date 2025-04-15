/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.service.taglib;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.internal.service.taglib.util.ClientExtensionDynamicIncludeUtil;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.GlobalCSSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = DynamicInclude.class)
public class ClientExtensionTopHeadDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PrintWriter printWriter = httpServletResponse.getWriter();

		List<ClientExtensionEntryRel> clientExtensionEntryRels =
			ClientExtensionDynamicIncludeUtil.getClientExtensionEntryRels(
				themeDisplay.getLayout(),
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS);

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			GlobalCSSCET globalCSSCET = (GlobalCSSCET)_cetManager.getCET(
				clientExtensionEntryRel.getCompanyId(),
				clientExtensionEntryRel.getCETExternalReferenceCode());

			if (globalCSSCET == null) {
				continue;
			}

			_writeStyleSheet(
				httpServletRequest, printWriter, globalCSSCET.getURL());
		}

		try {
			List<CET> cets = _cetManager.getCETs(
				themeDisplay.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_GLOBAL_CSS,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

			for (CET cet : cets) {
				GlobalCSSCET globalCSSCET = (GlobalCSSCET)cet;

				if (!StringUtil.equalsIgnoreCase(
						globalCSSCET.getScope(), "company")) {

					continue;
				}

				_writeStyleSheet(
					httpServletRequest, printWriter, globalCSSCET.getURL());
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to inject CSS client extensions for company " +
					themeDisplay.getCompanyId(),
				exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	private void _writeStyleSheet(
		HttpServletRequest httpServletRequest, PrintWriter printWriter,
		String url) {

		printWriter.print("<link data-senna-track=\"temporary\" href=\"");
		printWriter.print(url);
		printWriter.print(StringPool.QUOTE);
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.print(" rel=\"stylesheet\" type=\"text/css\" />");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionTopHeadDynamicInclude.class);

	@Reference
	private CETManager _cetManager;

}