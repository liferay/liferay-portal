/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.util;

import com.liferay.fragment.constants.FragmentEntryLinkConstants;
import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.HTMLInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Adolfo Pérez
 */
public class InfoFieldUtil {

	public static <E extends Throwable> void forEachInfoField(
			FragmentRendererController fragmentRendererController,
			Layout layout, long segmentsExperienceId,
			UnsafeTriConsumer
				<String, InfoField<?>,
				 UnsafeSupplier<JSONObject, JSONException>, E> consumer)
		throws E {

		if (!layout.isTypeContent()) {
			return;
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			FragmentEntryLinkLocalServiceUtil.
				getFragmentEntryLinksBySegmentsExperienceId(
					layout.getGroupId(), segmentsExperienceId,
					layout.getPlid());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (fragmentEntryLink.isTypePortlet()) {
				continue;
			}

			String defaultElementName =
				"defaultElementName" + StringUtil.randomId();

			Map<String, String> editableTypes =
				EditableFragmentEntryProcessorUtil.getEditableTypes(
					_getHtml(
						fragmentEntryLink, fragmentRendererController,
						defaultElementName));

			for (Map.Entry<String, String> entry : editableTypes.entrySet()) {
				String name = entry.getKey();
				String type = entry.getValue();

				if (!name.equals(defaultElementName) &&
					_isTextFieldType(type)) {

					consumer.accept(
						name,
						_getInfoField(
							fragmentEntryLink.getFragmentEntryLinkId(), name,
							type),
						() -> JSONFactoryUtil.createJSONObject(
							fragmentEntryLink.getEditableValues()));
				}
			}
		}
	}

	private static String _getHtml(
		FragmentEntryLink fragmentEntryLink,
		FragmentRendererController fragmentRendererController,
		String defaultElementName) {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return _renderHtml(fragmentEntryLink, defaultElementName);
		}

		HttpServletRequest httpServletRequest = serviceContext.getRequest();
		HttpServletResponse httpServletResponse = serviceContext.getResponse();

		if ((httpServletRequest == null) || (httpServletResponse == null)) {
			return _renderHtml(fragmentEntryLink, defaultElementName);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return _renderHtml(fragmentEntryLink, defaultElementName);
		}

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		defaultFragmentRendererContext.setLocale(themeDisplay.getLocale());
		defaultFragmentRendererContext.setMode(FragmentEntryLinkConstants.EDIT);

		return fragmentRendererController.render(
			defaultFragmentRendererContext, httpServletRequest,
			httpServletResponse);
	}

	private static InfoField<?> _getInfoField(
		long fragmentEntryLinkId, String name, String type) {

		return InfoField.builder(
		).infoFieldType(
			_getInfoFieldType(type)
		).namespace(
			FragmentEntryLink.class.getSimpleName()
		).name(
			fragmentEntryLinkId + StringPool.COLON + name
		).labelInfoLocalizedValue(
			InfoLocalizedValue.singleValue(name)
		).localizable(
			true
		).build();
	}

	private static InfoFieldType _getInfoFieldType(String type) {
		if (type.equals("html") || type.equals("rich-text")) {
			return HTMLInfoFieldType.INSTANCE;
		}

		return TextInfoFieldType.INSTANCE;
	}

	private static boolean _isTextFieldType(String type) {
		if (type.equals("html") || type.equals("link") ||
			type.equals("rich-text") || type.equals("text")) {

			return true;
		}

		return false;
	}

	private static String _renderHtml(
		FragmentEntryLink fragmentEntryLink, String defaultElementName) {

		Matcher matcher = _pattern.matcher(fragmentEntryLink.getHtml());

		return matcher.replaceAll(defaultElementName);
	}

	private static final Pattern _pattern = Pattern.compile("\\$\\{[^}]*\\}");

}