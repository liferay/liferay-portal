/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.cache.FragmentEntryLinkCache;
import com.liferay.fragment.configuration.FragmentJavaScriptConfiguration;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.input.template.parser.FragmentEntryInputTemplateNodeContextHelper;
import com.liferay.fragment.input.template.parser.InputTemplateNode;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DefaultFragmentEntryProcessorContext;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Pablo Molina
 */
@Component(service = FragmentRenderer.class)
public class FragmentEntryFragmentRenderer implements FragmentRenderer {

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
		return FragmentRendererConstants.FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY;
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

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write(
				_renderFragmentEntryLink(
					fragmentRendererContext, httpServletRequest,
					httpServletResponse));
		}
		catch (PortalException portalException) {
			throw new IOException(portalException);
		}
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
			fragmentEntryLink.setConfiguration(
				fragmentEntry.getConfiguration());
			fragmentEntryLink.setType(fragmentEntry.getType());
		}

		return fragmentEntryLink;
	}

	private String _getFragmentEntryName(FragmentEntryLink fragmentEntryLink) {
		FragmentEntry fragmentEntry = null;

		if (Validator.isNotNull(fragmentEntryLink.getRendererKey())) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryLink.getRendererKey());
		}

		if (fragmentEntry == null) {
			fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntryLink.getFragmentEntryId());
		}

		if (fragmentEntry == null) {
			return StringPool.BLANK;
		}

		return fragmentEntry.getName();
	}

	private JSONObject _getInputJSONObject(
		FragmentEntryLink fragmentEntryLink,
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		InputTemplateNode inputTemplateNode =
			_fragmentEntryInputTemplateNodeContextHelper.toInputTemplateNode(
				fragmentRendererContext.getAttributes(),
				_getFragmentEntryName(fragmentEntryLink), fragmentEntryLink,
				httpServletRequest, fragmentRendererContext.getInfoForm(),
				fragmentRendererContext.getLocale());

		return inputTemplateNode.toJSONObject();
	}

	private boolean _isCacheable(
		FragmentEntryLink fragmentEntryLink,
		FragmentRendererContext fragmentRendererContext) {

		if (!CTCollectionThreadLocal.isProductionMode() ||
			fragmentEntryLink.isTypeInput() ||
			!fragmentRendererContext.isViewMode() ||
			(fragmentRendererContext.getPreviewClassPK() > 0) ||
			!fragmentRendererContext.isUseCachedContent()) {

			return false;
		}

		if (fragmentEntryLink.getPlid() > 0) {
			Layout layout = _layoutLocalService.fetchLayout(
				fragmentEntryLink.getPlid());

			if (layout.isDraftLayout() || layout.isTypeAssetDisplay()) {
				return false;
			}
		}

		FragmentEntry fragmentEntry = null;

		if (Validator.isNotNull(fragmentEntryLink.getRendererKey())) {
			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryLink.getRendererKey());

			if (fragmentEntry == null) {
				return false;
			}
		}

		if (fragmentEntry == null) {
			fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
				fragmentEntryLink.getFragmentEntryId());
		}

		if (fragmentEntry == null) {
			return fragmentEntryLink.isCacheable();
		}

		return fragmentEntry.isCacheable();
	}

	private boolean _isJavaScriptModuleEnabled(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			FragmentJavaScriptConfiguration fragmentJavaScriptConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FragmentJavaScriptConfiguration.class,
					themeDisplay.getCompanyId());

			return fragmentJavaScriptConfiguration.javaScriptModuleEnabled();
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			return true;
		}
	}

	private String _renderFragmentEntry(
		String configuration, String css,
		FragmentRendererContext fragmentRendererContext, String html,
		HttpServletRequest httpServletRequest, String nonce) {

		StringBundler sb = new StringBundler(29);

		sb.append("<div id=\"");

		sb.append(fragmentRendererContext.getFragmentElementId());

		sb.append("\" >");
		sb.append(html);
		sb.append("</div>");

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		if (Validator.isNotNull(css)) {
			if (fragmentRendererContext.isEditMode() ||
				fragmentRendererContext.isIndexMode()) {

				sb.append("<style ");
				sb.append(nonce);
				sb.append(StringPool.GREATER_THAN);
				sb.append(css);
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
						cssLoaded = Objects.equals(cssSB.toString(), css);
					}
				}
				else {
					outputData = new OutputData();
				}

				if (!cssLoaded) {
					sb.append("<style ");
					sb.append(nonce);
					sb.append(StringPool.GREATER_THAN);
					sb.append(css);
					sb.append("</style>");

					outputData.addOutputKey(outputKey);

					outputData.setDataSB(
						outputKey, StringPool.BLANK, new StringBundler(css));

					httpServletRequest.setAttribute(
						WebKeys.OUTPUT_DATA, outputData);
				}
			}
		}

		if (Validator.isNotNull(fragmentEntryLink.getJs())) {
			boolean javaScriptModuleEnabled = _isJavaScriptModuleEnabled(
				httpServletRequest);

			if (javaScriptModuleEnabled) {
				sb.append("<script type=\"module\" ");
				sb.append(nonce);
				sb.append(StringPool.GREATER_THAN);
			}
			else {
				sb.append("<script>(function() {");
			}

			sb.append("const configuration = ");
			sb.append(configuration);
			sb.append("; const fragmentElement = document.querySelector('#");
			sb.append(fragmentRendererContext.getFragmentElementId());
			sb.append("'); const fragmentEntryLinkNamespace = '");
			sb.append(fragmentEntryLink.getNamespace());
			sb.append("'; const fragmentNamespace = '");
			sb.append(fragmentEntryLink.getNamespace());
			sb.append("'");

			if (fragmentEntryLink.isTypeInput()) {
				sb.append("; const input = ");
				sb.append(
					JSONUtil.toString(
						_getInputJSONObject(
							fragmentEntryLink, fragmentRendererContext,
							httpServletRequest)));
			}

			sb.append("; const layoutMode = '");
			sb.append(
				HtmlUtil.escapeJS(
					ParamUtil.getString(
						_portal.getOriginalServletRequest(httpServletRequest),
						"p_l_mode", Constants.VIEW)));
			sb.append("';");
			sb.append(fragmentEntryLink.getJs());

			if (javaScriptModuleEnabled) {
				sb.append(";</script>");
			}
			else {
				sb.append(";}());</script>");
			}
		}

		return sb.toString();
	}

	private String _renderFragmentEntryLink(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		String content = StringPool.BLANK;
		String nonce = _NONCE;

		FragmentEntryLink fragmentEntryLink = _getFragmentEntryLink(
			fragmentRendererContext);

		boolean cacheable = _isCacheable(
			fragmentEntryLink, fragmentRendererContext);

		if (cacheable) {
			content = _fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, fragmentRendererContext.getLocale());

			if (Validator.isNotNull(content)) {
				return StringUtil.replace(
					content, _NONCE,
					ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
						httpServletRequest));
			}
		}
		else {
			nonce = ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest);
		}

		DefaultFragmentEntryProcessorContext
			defaultFragmentEntryProcessorContext =
				new DefaultFragmentEntryProcessorContext(
					httpServletRequest, httpServletResponse,
					fragmentRendererContext.getMode(),
					fragmentRendererContext.getLocale());

		defaultFragmentEntryProcessorContext.setAttributes(
			fragmentRendererContext.getAttributes());
		defaultFragmentEntryProcessorContext.setContextInfoItemReference(
			fragmentRendererContext.getContextInfoItemReference());
		defaultFragmentEntryProcessorContext.setFragmentElementId(
			fragmentRendererContext.getFragmentElementId());
		defaultFragmentEntryProcessorContext.setInfoForm(
			fragmentRendererContext.getInfoForm());
		defaultFragmentEntryProcessorContext.setPreviewClassNameId(
			fragmentRendererContext.getPreviewClassNameId());
		defaultFragmentEntryProcessorContext.setPreviewClassPK(
			fragmentRendererContext.getPreviewClassPK());
		defaultFragmentEntryProcessorContext.setPreviewType(
			fragmentRendererContext.getPreviewType());
		defaultFragmentEntryProcessorContext.setPreviewVersion(
			fragmentRendererContext.getPreviewVersion());
		defaultFragmentEntryProcessorContext.setSegmentsEntryIds(
			fragmentRendererContext.getSegmentsEntryIds());

		String css = StringPool.BLANK;

		if (Validator.isNotNull(fragmentEntryLink.getCss())) {
			css = _fragmentEntryProcessorRegistry.processFragmentEntryLinkCSS(
				fragmentEntryLink, defaultFragmentEntryProcessorContext);
		}

		String html = StringPool.BLANK;

		if (Validator.isNotNull(fragmentEntryLink.getHtml()) ||
			Validator.isNotNull(fragmentEntryLink.getEditableValues())) {

			html = _fragmentEntryProcessorRegistry.processFragmentEntryLinkHTML(
				fragmentEntryLink, defaultFragmentEntryProcessorContext);
		}

		if (defaultFragmentEntryProcessorContext.isEditMode()) {
			html = _writePortletPaths(
				fragmentEntryLink, html, httpServletRequest,
				httpServletResponse);
		}

		JSONObject configurationJSONObject = _jsonFactory.createJSONObject();

		if (Validator.isNotNull(fragmentEntryLink.getConfiguration())) {
			configurationJSONObject =
				_fragmentEntryConfigurationParser.getConfigurationJSONObject(
					fragmentEntryLink.getConfiguration(),
					fragmentEntryLink.getEditableValues(),
					fragmentRendererContext.getLocale());
		}

		content = _renderFragmentEntry(
			configurationJSONObject.toString(), css, fragmentRendererContext,
			html, httpServletRequest, nonce);

		if (cacheable) {
			_fragmentEntryLinkCache.putFragmentEntryLinkContent(
				content, fragmentEntryLink,
				fragmentRendererContext.getLocale());

			content = StringUtil.replace(
				content, _NONCE,
				ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					httpServletRequest));
		}

		return content;
	}

	private String _writePortletPaths(
			FragmentEntryLink fragmentEntryLink, String html,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		_portletRegistry.writePortletPaths(
			fragmentEntryLink, httpServletRequest,
			new PipingServletResponse(httpServletResponse, unsyncStringWriter));

		unsyncStringWriter.append(html);

		return unsyncStringWriter.toString();
	}

	private static final String _NONCE = "data-lfr-nonce";

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryFragmentRenderer.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryInputTemplateNodeContextHelper
		_fragmentEntryInputTemplateNodeContextHelper;

	@Reference
	private FragmentEntryLinkCache _fragmentEntryLinkCache;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

}