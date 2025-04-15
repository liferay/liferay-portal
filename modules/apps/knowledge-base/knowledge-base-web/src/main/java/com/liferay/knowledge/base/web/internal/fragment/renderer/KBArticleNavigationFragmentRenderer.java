/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.fragment.renderer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.web.internal.display.context.KBArticleNavigationFragmentDisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = FragmentRenderer.class)
public class KBArticleNavigationFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "menu-display";
	}

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		return JSONUtil.put(
			"fieldSets",
			JSONUtil.putAll(
				JSONUtil.put(
					"fields",
					JSONUtil.putAll(
						JSONUtil.put(
							"label", "item"
						).put(
							"name", "itemSelector"
						).put(
							"type", "itemSelector"
						).put(
							"typeOptions",
							JSONUtil.put("itemType", KBArticle.class.getName())
						),
						JSONUtil.put(
							"defaultValue", String.valueOf(_MAX_NESTING_LEVEL)
						).put(
							"label", "maximum-nesting-level"
						).put(
							"name", "maxNestingLevel"
						).put(
							"type", "text"
						).put(
							"typeOptions",
							JSONUtil.put(
								"validation",
								JSONUtil.put(
									"min", 1
								).put(
									"type", "number"
								))
						))))
		).toString();
	}

	@Override
	public String getIcon() {
		return "hierarchy";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "knowledge-base-navigation");
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			KBArticle kbArticle = _getKBArticle(
				httpServletRequest, fragmentRendererContext);

			if ((kbArticle == null) && fragmentRendererContext.isEditMode()) {
				_printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-navigation-tree-for-the-displayed-knowledge-base-" +
						"article-will-be-shown-here");
			}

			if (kbArticle == null) {
				return;
			}

			PrintWriter printWriter = httpServletResponse.getWriter();

			String fragmentElementId =
				fragmentRendererContext.getFragmentElementId();

			printWriter.write("<div id=\"" + fragmentElementId + "\">");

			_writeCss(fragmentElementId, printWriter);

			httpServletRequest.setAttribute(
				KBArticleNavigationFragmentDisplayContext.class.getName(),
				new KBArticleNavigationFragmentDisplayContext(
					_infoItemFriendlyURLProvider, kbArticle,
					_getMaxNestingLevel(fragmentRendererContext)));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/navigation/view.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);

			printWriter.write("</div>");
		}
		catch (ServletException servletException) {
			_log.error(
				"KB article navigation fragment can not be rendered",
				servletException);
		}
	}

	private Object _getInfoItem(InfoItemReference infoItemReference) {
		if (infoItemReference == null) {
			return null;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, infoItemReference.getClassName(),
				infoItemIdentifier.getInfoItemServiceFilter());

		try {
			return infoItemObjectProvider.getInfoItem(infoItemIdentifier);
		}
		catch (NoSuchInfoItemException noSuchInfoItemException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchInfoItemException);
			}
		}

		return null;
	}

	private KBArticle _getKBArticle(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return null;
		}

		AssetRenderer<KBArticle> assetRenderer =
			(AssetRenderer<KBArticle>)assetEntry.getAssetRenderer();

		return assetRenderer.getAssetObject();
	}

	private KBArticle _getKBArticle(
		HttpServletRequest httpServletRequest,
		FragmentRendererContext fragmentRendererContext) {

		try {
			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			JSONObject jsonObject =
				(JSONObject)_fragmentEntryConfigurationParser.getFieldValue(
					getConfiguration(fragmentRendererContext),
					fragmentEntryLink.getEditableValues(),
					fragmentRendererContext.getLocale(), "itemSelector");

			if ((jsonObject != null) && jsonObject.has("className") &&
				jsonObject.has("classPK") &&
				Objects.equals(
					jsonObject.get("className"), KBArticle.class.getName())) {

				return _kbArticleService.fetchLatestKBArticle(
					jsonObject.getLong("classPK"),
					WorkflowConstants.STATUS_ANY);
			}

			InfoItemReference infoItemReference =
				fragmentRendererContext.getContextInfoItemReference();

			if (infoItemReference != null) {
				if (Objects.equals(
						infoItemReference.getClassName(),
						KBArticle.class.getName())) {

					return (KBArticle)_getInfoItem(infoItemReference);
				}
			}
			else {
				return _getKBArticle(
					(AssetEntry)httpServletRequest.getAttribute(
						WebKeys.LAYOUT_ASSET_ENTRY));
			}

			return null;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private int _getMaxNestingLevel(
		FragmentRendererContext fragmentRendererContext) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		return GetterUtil.getInteger(
			_fragmentEntryConfigurationParser.getFieldValue(
				getConfiguration(fragmentRendererContext),
				fragmentEntryLink.getEditableValues(),
				fragmentRendererContext.getLocale(), "maxNestingLevel"),
			_MAX_NESTING_LEVEL);
	}

	private void _printPortletMessageInfo(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String message) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write(
				StringBundler.concat(
					"<div class=\"portlet-msg-info\">",
					_language.get(httpServletRequest, message), "</div>"));
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private void _writeCss(String fragmentElementId, PrintWriter printWriter)
		throws IOException {

		printWriter.write(
			StringUtil.replace(
				StringUtil.read(
					getClass(),
					"/com/liferay/knowledge/base/web/internal/fragment" +
						"/renderer/dependencies/styles.tmpl"),
				"${", "}",
				HashMapBuilder.put(
					"fragmentElementId", fragmentElementId
				).build()));
	}

	private static final int _MAX_NESTING_LEVEL = 3;

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleNavigationFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference(
		target = "(item.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private InfoItemFriendlyURLProvider<KBArticle> _infoItemFriendlyURLProvider;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.knowledge.base.web)"
	)
	private ServletContext _servletContext;

}