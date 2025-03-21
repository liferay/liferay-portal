/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.ratings.taglib.servlet.taglib.RatingsTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = FragmentRenderer.class)
public class ContentRatingsFragmentRenderer
	extends BaseContentFragmentRenderer {

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
						JSONUtil.put(
							"label", "item"
						).put(
							"name", "itemSelector"
						).put(
							"type", "itemSelector"
						))
				).put(
					"label",
					_language.format(
						fragmentRendererContext.getLocale(), "x-options",
						"content-ratings", true)
				))
		).toString();
	}

	@Override
	public String getIcon() {
		return "web-content";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "content-ratings");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		RatingsTag ratingsTag = new RatingsTag();

		Tuple displayObjectTuple = getDisplayObjectTuple(
			fragmentRendererContext, httpServletRequest);

		ratingsTag.setClassName(
			GetterUtil.getString(displayObjectTuple.getObject(0)));
		ratingsTag.setClassPK(
			GetterUtil.getLong(displayObjectTuple.getObject(1)));

		ratingsTag.setInTrash(false);

		try {
			ratingsTag.doTag(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render content ratings fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentRatingsFragmentRenderer.class);

	@Reference
	private Language _language;

}