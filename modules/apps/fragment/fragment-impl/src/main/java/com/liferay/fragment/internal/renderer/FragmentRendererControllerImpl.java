/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.exception.FragmentEntryContentException;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.renderer.constants.FragmentRendererConstants;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.adaptive.media.LayoutAdaptiveMediaProcessor;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = FragmentRendererController.class)
public class FragmentRendererControllerImpl
	implements FragmentRendererController {

	@Override
	public String getConfiguration(
		FragmentRendererContext fragmentRendererContext) {

		FragmentRenderer fragmentRenderer = _getFragmentRenderer(
			fragmentRendererContext.getFragmentEntryLink());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				fragmentRenderer.getConfiguration(fragmentRendererContext));

			return _translateConfigurationFields(
				jsonObject, fragmentRendererContext.getLocale());
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to parse fragment entry link configuration",
				jsonException);
		}

		return StringPool.BLANK;
	}

	@Override
	public String render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		FragmentRenderer fragmentRenderer = _getFragmentRenderer(
			fragmentEntryLink);

		Locale currentLocale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(
			fragmentRendererContext.getLocale());

		try {
			fragmentRenderer.render(
				fragmentRendererContext, httpServletRequest,
				new PipingServletResponse(
					httpServletResponse, unsyncStringWriter));
		}
		catch (Exception exception) {
			Throwable throwable = exception;

			if (throwable.getCause() != null) {
				throwable = throwable.getCause();
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to render content of fragment entry ",
						fragmentEntryLink.getFragmentEntryId(),
						" with fragment entry link ",
						fragmentEntryLink.getFragmentEntryLinkId(),
						" and PLID ", fragmentEntryLink.getPlid(), ": ",
						throwable.getMessage()),
					exception);
			}

			SessionErrors.add(
				httpServletRequest, "fragmentEntryContentInvalid");

			if (fragmentRendererContext.isIndexMode() &&
				(throwable.getCause() instanceof TemplateException)) {

				return StringPool.BLANK;
			}

			return _getFragmentEntryContentExceptionMessage(
				exception, httpServletRequest);
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(currentLocale);
		}

		if (fragmentRendererContext.isEditMode()) {
			return _layoutAdaptiveMediaProcessor.processAdaptiveMediaContent(
				unsyncStringWriter.toString());
		}

		return unsyncStringWriter.toString();
	}

	private String _getFragmentEntryContentExceptionMessage(
		Exception exception, HttpServletRequest httpServletRequest) {

		StringBundler sb = new StringBundler(3);

		sb.append("<div class=\"alert alert-danger m-2\">");

		String errorMessage = "an-unexpected-error-occurred";

		Throwable throwable = exception.getCause();

		if (throwable instanceof FragmentEntryContentException) {
			FragmentEntryContentException fragmentEntryContentException =
				(FragmentEntryContentException)throwable;

			errorMessage = fragmentEntryContentException.getLocalizedMessage();
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String localizedErrorMessage = _language.get(
			themeDisplay.getLocale(), errorMessage);

		sb.append(localizedErrorMessage.replaceAll("\\n", "<br>"));

		sb.append("</div>");

		return sb.toString();
	}

	private FragmentRenderer _getFragmentRenderer(
		FragmentEntryLink fragmentEntryLink) {

		FragmentRenderer fragmentRenderer = null;

		if (Validator.isNotNull(fragmentEntryLink.getRendererKey())) {
			fragmentRenderer = _fragmentRendererRegistry.getFragmentRenderer(
				fragmentEntryLink.getRendererKey());
		}

		if ((fragmentRenderer == null) && fragmentEntryLink.isTypeReact()) {
			fragmentRenderer = _fragmentRendererRegistry.getFragmentRenderer(
				FragmentRendererConstants.
					FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY_REACT);
		}

		if (fragmentRenderer == null) {
			fragmentRenderer = _fragmentRendererRegistry.getFragmentRenderer(
				FragmentRendererConstants.FRAGMENT_ENTRY_FRAGMENT_RENDERER_KEY);
		}

		return fragmentRenderer;
	}

	private String _translateConfigurationFields(
		JSONObject jsonObject, Locale locale) {

		ResourceBundleLoader resourceBundleLoader =
			new AggregateResourceBundleLoader(
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader(),
				_fragmentCollectionContributorRegistry.
					getResourceBundleLoader());

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		return _fragmentEntryConfigurationParser.translateConfiguration(
			jsonObject, resourceBundle);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentRendererControllerImpl.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutAdaptiveMediaProcessor _layoutAdaptiveMediaProcessor;

}