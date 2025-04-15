/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display;

import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Preston Crary
 */
public class DisplayContextImpl<T> implements DisplayContext<T> {

	public DisplayContextImpl(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		ClassNameLocalService classNameLocalService,
		CTDisplayRendererRegistry ctDisplayRendererRegistry, long ctEntryId,
		Locale locale, T model, String type) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_classNameLocalService = classNameLocalService;
		_ctDisplayRendererRegistry = ctDisplayRendererRegistry;
		_ctEntryId = ctEntryId;
		_locale = locale;
		_model = model;
		_type = type;
	}

	@Override
	public String getDownloadURL(String key, long size, String title) {
		if (_ctEntryId <= 0) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(13);

		sb.append(themeDisplay.getPortalURL());
		sb.append(Portal.PATH_MODULE);
		sb.append("/change_tracking/documents/");
		sb.append(_ctEntryId);
		sb.append(StringPool.SLASH);
		sb.append(_type);

		if (Validator.isNotNull(key)) {
			sb.append(StringPool.SLASH);
			sb.append(String.valueOf(key));
		}

		if (size > 0) {
			sb.append("?size=");
			sb.append(String.valueOf(size));
		}

		if (Validator.isNotNull(title)) {
			if (size > 0) {
				sb.append(StringPool.AMPERSAND);
			}
			else {
				sb.append(StringPool.QUESTION);
			}

			sb.append("title=");
			sb.append(URLCodec.encodeURL(title));
		}

		return sb.toString();
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public T getModel() {
		return _model;
	}

	@Override
	public void render(BaseModel<?> baseModel, Locale locale) throws Exception {
		_render(baseModel, locale, false);
	}

	@Override
	public String renderPreview(BaseModel<?> baseModel, Locale locale)
		throws Exception {

		return _render(baseModel, locale, true);
	}

	private <M extends BaseModel<?>> String _render(
			M baseModel, Locale locale, boolean preview)
		throws Exception {

		if (baseModel == _model) {
			throw new IllegalArgumentException();
		}

		CTDisplayRenderer<M> ctDisplayRenderer =
			_ctDisplayRendererRegistry.getCTDisplayRenderer(
				_classNameLocalService.getClassNameId(
					baseModel.getModelClassName()));

		DisplayContext<M> displayContext = new DisplayContextImpl<>(
			_httpServletRequest, _httpServletResponse, _classNameLocalService,
			_ctDisplayRendererRegistry, _ctEntryId, locale, baseModel, _type);

		String result = null;

		if (preview) {
			result = ctDisplayRenderer.renderPreview(displayContext);
		}
		else {
			ctDisplayRenderer.render(displayContext);
		}

		return result;
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CTDisplayRendererRegistry _ctDisplayRendererRegistry;
	private final long _ctEntryId;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final Locale _locale;
	private final T _model;
	private final String _type;

}