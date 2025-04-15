/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.service.taglib;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.internal.service.taglib.util.ClientExtensionDynamicIncludeUtil;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.GlobalJSCET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
public abstract class BaseDynamicInclude implements DynamicInclude {

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
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS);

		for (ClientExtensionEntryRel clientExtensionEntryRel :
				clientExtensionEntryRels) {

			GlobalJSCET globalJSCET = (GlobalJSCET)cetManager.getCET(
				clientExtensionEntryRel.getCompanyId(),
				clientExtensionEntryRel.getCETExternalReferenceCode());

			if (globalJSCET == null) {
				continue;
			}

			UnicodeProperties typeSettingsUnicodeProperties =
				UnicodePropertiesBuilder.create(
					true
				).fastLoad(
					clientExtensionEntryRel.getTypeSettings()
				).build();

			if (!Objects.equals(
					typeSettingsUnicodeProperties.getProperty(
						"scriptLocation", StringPool.BLANK),
					getScriptLocation())) {

				continue;
			}

			_writeScript(
				globalJSCET, httpServletRequest,
				typeSettingsUnicodeProperties.getProperty(
					"loadType", StringPool.BLANK),
				printWriter);
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-30371")) {

			return;
		}

		try {
			List<CET> cets = cetManager.getCETs(
				themeDisplay.getCompanyId(), null,
				ClientExtensionEntryConstants.TYPE_GLOBAL_JS,
				Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS), null);

			for (CET cet : cets) {
				GlobalJSCET globalJSCET = (GlobalJSCET)cet;

				if (!StringUtil.equalsIgnoreCase(
						globalJSCET.getScope(), "company")) {

					continue;
				}

				String scriptLocation = globalJSCET.getScriptLocation();

				if (Validator.isNull(scriptLocation) ||
					!StringUtil.equalsIgnoreCase(
						scriptLocation, getScriptLocation())) {

					continue;
				}

				_writeScript(
					globalJSCET, httpServletRequest, null, printWriter);
			}
		}
		catch (Exception exception) {
			_log.error(
				"Unable to inject JavaScript client extensions for company " +
					themeDisplay.getCompanyId(),
				exception);
		}
	}

	protected abstract String getScriptLocation();

	@Reference
	protected CETManager cetManager;

	@Reference
	protected JSONFactory jsonFactory;

	private String _toScriptElementAttributes(
		String scriptElementAttributesJSON) {

		StringBuilder stringBuilder = new StringBuilder();

		try {
			JSONObject jsonObject = jsonFactory.createJSONObject(
				scriptElementAttributesJSON);

			Iterator<String> iterator = jsonObject.keys();

			if (!jsonObject.has("data-senna-track")) {
				stringBuilder.append("data-senna-track=\"temporary\" ");
			}

			if (!jsonObject.has("type")) {
				stringBuilder.append("type=\"text/javascript\" ");
			}

			while (iterator.hasNext()) {
				String key = iterator.next();

				if (key.equals("async") || key.equals("defer")) {
					continue;
				}

				Object value = jsonObject.get(key);

				if (value instanceof Boolean) {
					if (value == Boolean.FALSE) {
						continue;
					}

					stringBuilder.append(key);
				}
				else {
					stringBuilder.append(key);
					stringBuilder.append(StringPool.EQUAL);
					stringBuilder.append(StringPool.QUOTE);
					stringBuilder.append(
						HtmlUtil.escapeAttribute((String)value));
					stringBuilder.append(StringPool.QUOTE);
				}

				if (iterator.hasNext()) {
					stringBuilder.append(StringPool.SPACE);
				}
			}
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to parse script element attributes JSON: " +
					scriptElementAttributesJSON,
				jsonException);
		}

		return stringBuilder.toString();
	}

	private void _writeScript(
		GlobalJSCET globalJSCET, HttpServletRequest httpServletRequest,
		String loadType, PrintWriter printWriter) {

		printWriter.print("<script");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));

		if (Validator.isNotNull(loadType) &&
			!Objects.equals(loadType, "default")) {

			printWriter.print(StringPool.SPACE);
			printWriter.print(loadType);
		}

		printWriter.print(StringPool.SPACE);
		printWriter.print(
			_toScriptElementAttributes(
				globalJSCET.getScriptElementAttributesJSON()));
		printWriter.print(" src=\"");
		printWriter.print(globalJSCET.getURL());
		printWriter.print("\"></script>");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseDynamicInclude.class);

}