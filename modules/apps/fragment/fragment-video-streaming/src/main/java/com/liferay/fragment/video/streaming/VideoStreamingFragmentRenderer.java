/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.video.streaming;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.fragment.video.streaming.constants.VideoStreamingWebKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ambrín Chaudhary
 */
@Component(service = FragmentRenderer.class)
public class VideoStreamingFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
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
						_createFieldJSONObject("url", "url", "text"),
						_createFieldJSONObject("subtitle", "subtitles", "text"),
						_createFieldJSONObject(
							"autoplay", "autoplay", "checkbox"),
						_createFieldJSONObject("loop", "loop", "checkbox"),
						_createFieldJSONObject("mute", "mute", "checkbox"),
						_createFieldJSONObject("width", "videoWidth", "text"),
						_createFieldJSONObject("height", "videoHeight", "text"))
				).put(
					"label",
					_language.format(
						fragmentRendererContext.getLocale(), "x-options",
						"video-streaming", true)
				))
		).toString();
	}

	@Override
	public String getIcon() {
		return "video";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "video-streaming");
	}

	@Override
	public boolean hasViewPermission(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest) {

		return FeatureFlagManagerUtil.isEnabled("LPS-200108");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return FeatureFlagManagerUtil.isEnabled("LPS-200108");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/page.jsp");

			FragmentEntryLink fragmentEntryLink =
				fragmentRendererContext.getFragmentEntryLink();

			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_AUTOPLAY,
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "autoplay")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_LOOP,
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "loop")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_MUTED,
				GetterUtil.getBoolean(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "mute")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_SOURCE_URL,
				GetterUtil.getString(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "url")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_SUBTITLES,
				GetterUtil.getString(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "subtitles")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_VIDEO_HEIGHT,
				GetterUtil.getString(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "videoHeight")));
			httpServletRequest.setAttribute(
				VideoStreamingWebKeys.VIDEO_STREAMING_VIDEO_WIDTH,
				GetterUtil.getString(
					_fragmentEntryConfigurationParser.getFieldValue(
						getConfiguration(fragmentRendererContext),
						fragmentEntryLink.getEditableValues(),
						fragmentRendererContext.getLocale(), "videoWidth")));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render video streaming fragment", exception);
		}
	}

	private JSONObject _createFieldJSONObject(
		String label, String name, String type) {

		return JSONUtil.put(
			"label", label
		).put(
			"name", name
		).put(
			"type", type
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VideoStreamingFragmentRenderer.class);

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.video.streaming)"
	)
	private ServletContext _servletContext;

}