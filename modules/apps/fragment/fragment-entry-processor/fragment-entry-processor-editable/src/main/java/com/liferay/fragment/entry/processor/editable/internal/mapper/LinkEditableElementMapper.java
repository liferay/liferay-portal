/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.mapper;

import com.liferay.fragment.entry.processor.editable.mapper.EditableElementMapper;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(property = "type=link", service = EditableElementMapper.class)
public class LinkEditableElementMapper implements EditableElementMapper {

	@Override
	public void map(
			Element element, JSONObject configJSONObject,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		boolean nofollow = false;
		String href = null;

		JSONObject hrefJSONObject = configJSONObject.getJSONObject("href");

		if (_fragmentEntryProcessorHelper.isMapped(configJSONObject) ||
			_fragmentEntryProcessorHelper.isMappedCollection(
				configJSONObject) ||
			_fragmentEntryProcessorHelper.isMappedDisplayPage(
				configJSONObject)) {

			Object fieldValue = _fragmentEntryProcessorHelper.getFieldValue(
				configJSONObject, new HashMap<>(),
				fragmentEntryProcessorContext);

			if (fieldValue instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)fieldValue;

				nofollow = jsonObject.getBoolean("nofollow");
				href = jsonObject.getString("url");
			}
			else {
				href = GetterUtil.getString(fieldValue);
			}
		}
		else if (_isMappedLayout(configJSONObject)) {
			href = GetterUtil.getString(
				_getMappedLayoutValue(
					configJSONObject, fragmentEntryProcessorContext));
		}
		else if (hrefJSONObject != null) {
			String languageId = LocaleUtil.toLanguageId(
				fragmentEntryProcessorContext.getLocale());

			if (!hrefJSONObject.has(languageId)) {
				languageId = LocaleUtil.toLanguageId(
					LocaleUtil.getSiteDefault());
			}

			href = hrefJSONObject.getString(languageId);
		}
		else {
			href = configJSONObject.getString("href");
		}

		if (Validator.isNull(href)) {
			return;
		}

		String prefix = configJSONObject.getString("prefix");

		if (Validator.isNotNull(prefix)) {
			href = prefix + href;
		}

		Element linkElement = new Element("a");

		Elements elements = element.children();

		Element firstChildElement = elements.first();

		boolean processEditableTag = false;

		if (StringUtil.equalsIgnoreCase(element.tagName(), "a")) {
			linkElement = element;
		}
		else if (StringUtil.equalsIgnoreCase(
					element.tagName(), "lfr-editable")) {

			processEditableTag = true;
		}

		boolean replaceLink = false;

		if ((firstChildElement != null) && processEditableTag &&
			StringUtil.equalsIgnoreCase(firstChildElement.tagName(), "a")) {

			linkElement = firstChildElement;
			replaceLink = true;
		}

		String target = configJSONObject.getString("target");

		if (Validator.isNotNull(target)) {
			if (StringUtil.equalsIgnoreCase(target, "_parent") ||
				StringUtil.equalsIgnoreCase(target, "_top")) {

				target = "_self";
			}

			linkElement.attr("target", target);
		}

		if (Validator.isNull(href)) {
			return;
		}

		linkElement.attr("href", href);

		if (nofollow) {
			linkElement.attr("rel", "nofollow");
		}

		_replaceLinkContent(
			element, firstChildElement, linkElement, replaceLink);

		if (((linkElement != element) || processEditableTag) &&
			Validator.isNotNull(element.html())) {

			element.html(linkElement.outerHtml());
		}
		else if ((linkElement != element) && Validator.isNull(element.html())) {
			element.replaceWith(linkElement);
		}
	}

	private Object _getMappedLayoutValue(
			JSONObject jsonObject,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		if (!_isMappedLayout(jsonObject)) {
			return StringPool.BLANK;
		}

		HttpServletRequest httpServletRequest =
			fragmentEntryProcessorContext.getHttpServletRequest();

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		long groupId = layoutJSONObject.getLong("groupId");

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return StringPool.POUND;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			groupId, layoutJSONObject.getBoolean("privateLayout"),
			layoutJSONObject.getLong("layoutId"));

		if (layout == null) {
			return StringPool.POUND;
		}

		return _portal.getLayoutRelativeURL(layout, themeDisplay);
	}

	private boolean _isMappedLayout(JSONObject jsonObject) {
		return jsonObject.has("layout");
	}

	private void _replaceLinkContent(
		Element element, Element firstChildElement, Element linkElement,
		boolean replaceLink) {

		if (replaceLink && Validator.isNull(firstChildElement.html())) {
			linkElement.html(firstChildElement.outerHtml());
		}
		else if (replaceLink && Validator.isNotNull(firstChildElement.html())) {
			linkElement.html(firstChildElement.html());
		}
		else if (Validator.isNull(element.html())) {
			linkElement.html(element.outerHtml());
		}
		else {
			linkElement.html(element.html());
		}
	}

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}