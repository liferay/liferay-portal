/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.flags.taglib.servlet.taglib.react.FlagsTag;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Tuple;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = FragmentRenderer.class)
public class ContentFlagsFragmentRenderer
	extends BaseContentFragmentRenderer implements FragmentRenderer {

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
						),
						JSONUtil.put(
							"label", "message"
						).put(
							"name", "message"
						).put(
							"type", "text"
						))
				).put(
					"label",
					_language.format(
						fragmentRendererContext.getLocale(), "x-options",
						"content-flags", true)
				))
		).toString();
	}

	@Override
	public String getIcon() {
		return "web-content";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "content-flags");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		FlagsTag flagsTag = new FlagsTag();

		Tuple displayObjectTuple = getDisplayObjectTuple(
			fragmentRendererContext, httpServletRequest);

		String className = GetterUtil.getString(
			displayObjectTuple.getObject(0));

		flagsTag.setClassName(className);

		long classPK = GetterUtil.getLong(displayObjectTuple.getObject(1));

		flagsTag.setClassPK(classPK);

		flagsTag.setReportedUserId(portal.getUserId(httpServletRequest));

		FragmentEntryLink fragmentEntryLink =
			fragmentRendererContext.getFragmentEntryLink();

		try {
			flagsTag.setMessage(
				_language.get(
					httpServletRequest,
					GetterUtil.getString(
						fragmentEntryConfigurationParser.getFieldValue(
							getConfiguration(fragmentRendererContext),
							fragmentEntryLink.getEditableValues(),
							fragmentRendererContext.getLocale(), "message"))));

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				(LayoutDisplayPageProvider<?>)httpServletRequest.getAttribute(
					LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER);

			if (layoutDisplayPageProvider != null) {
				LayoutDisplayPageObjectProvider<?>
					layoutDisplayPageObjectProvider =
						layoutDisplayPageProvider.
							getLayoutDisplayPageObjectProvider(
								new InfoItemReference(className, classPK));

				if (layoutDisplayPageObjectProvider != null) {
					flagsTag.setContentTitle(
						layoutDisplayPageObjectProvider.getTitle(
							fragmentRendererContext.getLocale()));
				}
			}

			flagsTag.doTag(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render content flags fragment", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentFlagsFragmentRenderer.class);

	@Reference
	private Language _language;

}