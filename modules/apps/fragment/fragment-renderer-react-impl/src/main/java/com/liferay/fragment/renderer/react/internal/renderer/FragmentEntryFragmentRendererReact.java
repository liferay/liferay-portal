/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.renderer.react.internal.renderer;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.renderer.react.internal.helper.FragmentEntryLinkJSModuleInitializerHelper;
import com.liferay.fragment.renderer.react.internal.util.FragmentEntryFragmentRendererReactUtil;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FragmentEntryFragmentRendererReact implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return StringPool.BLANK;
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		return fragmentEntryLink.getConfiguration();
	}

	@Override
	public String getKey() {
		return FragmentRendererConstants.
			FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY_REACT;
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_fragmentEntryLinkJSModuleInitializerHelper.ensureInitialized();

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
				fragmentRendererContext);

			JSONObject configurationJSONObject =
				_jsonFactory.createJSONObject();

			if (Validator.isNotNull(fragmentEntryLink.getConfiguration())) {
				configurationJSONObject =
					_fragmentEntryConfigurationParser.
						getConfigurationJSONObject(
							fragmentEntryLink.getConfiguration(),
							fragmentEntryLink.getEditableValues(),
							LocaleUtil.getMostRelevantLocale());
			}

			printWriter.write(
				_renderFragmentEntry(
					fragmentEntryLink,
					fragmentRendererContext.getFragmentElementId(),
					fragmentRendererContext,
					HashMapBuilder.<String, Object>put(
						"configuration", configurationJSONObject
					).build(),
					httpServletRequest));
		}
		catch (PortalException portalException) {
			throw new IOException(portalException);
		}
	}

	@Activate
	protected void activate() {
		_jsPackage = _npmResolver.getJSPackage();

		_fragmentEntryLinkJSModuleInitializerHelper.ensureInitialized();
	}

	private FragmentEntry _getContributedFragmentEntry(
		FragmentEntryLink fragmentEntryLink) {

		Map<String, FragmentEntry> fragmentCollectionContributorEntries =
			_fragmentCollectionContributorRegistry.getFragmentEntries();

		return fragmentCollectionContributorEntries.get(
			fragmentEntryLink.getRendererKey());
	}

	private FragmentEntryLink _getFragmentEntryLink(
		FragmentRendererContext fragmentRendererContext) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		FragmentEntry fragmentEntry = _getContributedFragmentEntry(
			fragmentEntryLink);

		if (fragmentEntry != null) {
			fragmentEntryLink.setCss(fragmentEntry.getCss());
			fragmentEntryLink.setHtml(fragmentEntry.getHtml());
			fragmentEntryLink.setJs(fragmentEntry.getJs());
			fragmentEntryLink.setType(fragmentEntry.getType());
		}

		return fragmentEntryLink;
	}

	private String _renderFragmentEntry(
			FragmentEntryLink fragmentEntryLink, String fragmentElementId,
			FragmentRendererContext fragmentRendererContext,
			Map<String, Object> data, HttpServletRequest httpServletRequest)
		throws IOException {

		StringBundler sb = new StringBundler(11);

		sb.append("<div id=\"");
		sb.append(fragmentElementId);
		sb.append("\" >");
		sb.append(fragmentEntryLink.getHtml());

		Writer writer = new CharArrayWriter();

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				ModuleNameUtil.getModuleResolvedId(
					_jsPackage,
					FragmentEntryFragmentRendererReactUtil.getModuleName(
						fragmentEntryLink)),
				"fragment" + fragmentEntryLink.getFragmentEntryLinkId(),
				Collections.emptyList(), true),
			data, httpServletRequest, writer);

		sb.append(writer.toString());

		sb.append("</div>");

		if (Validator.isNotNull(fragmentEntryLink.getCss())) {
			if (fragmentRendererContext.isEditMode() ||
				fragmentRendererContext.isIndexMode()) {

				sb.append("<style");
				sb.append(
					ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
						httpServletRequest));
				sb.append(StringPool.GREATER_THAN);
				sb.append(fragmentEntryLink.getCss());
				sb.append("</style>");
			}
			else {
				String outputKey =
					fragmentEntryLink.getFragmentEntryId() + "_CSS";

				OutputData outputData =
					(OutputData)httpServletRequest.getAttribute(
						WebKeys.OUTPUT_DATA);

				boolean cssLoaded = false;

				if (outputData != null) {
					Set<String> outputKeys = outputData.getOutputKeys();

					cssLoaded = outputKeys.contains(outputKey);

					StringBundler cssSB = outputData.getDataSB(
						outputKey, StringPool.BLANK);

					if (cssSB != null) {
						cssLoaded = Objects.equals(
							cssSB.toString(), fragmentEntryLink.getCss());
					}
				}
				else {
					outputData = new OutputData();
				}

				if (!cssLoaded) {
					sb.append("<style");
					sb.append(
						ContentSecurityPolicyNonceProviderUtil.
							getNonceAttribute(httpServletRequest));
					sb.append(StringPool.GREATER_THAN);
					sb.append(fragmentEntryLink.getCss());
					sb.append("</style>");

					outputData.addOutputKey(outputKey);

					outputData.setDataSB(
						outputKey, StringPool.BLANK,
						new StringBundler(fragmentEntryLink.getCss()));

					httpServletRequest.setAttribute(
						WebKeys.OUTPUT_DATA, outputData);
				}
			}
		}

		return sb.toString();
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkJSModuleInitializerHelper
		_fragmentEntryLinkJSModuleInitializerHelper;

	@Reference
	private JSONFactory _jsonFactory;

	private JSPackage _jsPackage;

	@Reference
	private NPMResolver _npmResolver;

	@Reference
	private ReactRenderer _reactRenderer;

}