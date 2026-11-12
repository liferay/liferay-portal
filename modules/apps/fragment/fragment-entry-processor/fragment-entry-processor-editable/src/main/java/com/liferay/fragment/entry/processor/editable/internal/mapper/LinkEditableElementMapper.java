/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable.internal.mapper;

import com.liferay.fragment.entry.processor.editable.mapper.EditableElementMapper;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.helper.LayoutReferenceResolver;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Objects;

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

		boolean reuseExistingLink = false;

		if ((firstChildElement != null) && processEditableTag &&
			StringUtil.equalsIgnoreCase(firstChildElement.tagName(), "a")) {

			linkElement = firstChildElement;
			reuseExistingLink = true;
		}

		String target = configJSONObject.getString("target");

		if (Validator.isNotNull(target)) {
			if (StringUtil.equalsIgnoreCase(target, "_parent") ||
				StringUtil.equalsIgnoreCase(target, "_top")) {

				target = "_self";
			}

			if (StringUtil.equalsIgnoreCase(target, "_blank")) {
				linkElement.attr("rel", "noopener noreferrer");
			}

			linkElement.attr("target", target);
		}

		if (Validator.isNull(href)) {
			return;
		}

		boolean empty = false;

		if (element.childNodeSize() == 0) {
			empty = true;
		}

		if (!Objects.equals(
				HtmlUtil.escapeHREF(href), HtmlUtil.escapeAttribute(href))) {

			href = StringUtil.replaceFirst(href, CharPool.COLON, "%3a");
		}

		linkElement.attr("href", href);

		if (nofollow) {
			linkElement.attr("rel", "nofollow");
		}

		Element parentElement = element.parent();

		int index = element.siblingIndex();

		if (!reuseExistingLink) {
			_replaceLinkContent(element, empty, linkElement);
		}

		if (((linkElement != element) || processEditableTag) && !empty &&
			(linkElement.parent() != element)) {

			element.empty();

			element.appendChild(linkElement);
		}
		else if ((linkElement != element) && empty) {
			if (element.parent() == parentElement) {
				element.replaceWith(linkElement);
			}
			else {
				parentElement.insertChildren(index, linkElement);
			}
		}
	}

	private Object _getMappedLayoutValue(
			JSONObject jsonObject,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		if (!_isMappedLayout(jsonObject)) {
			return StringPool.BLANK;
		}

		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (layoutJSONObject == null) {
			return StringPool.POUND;
		}

		Layout layout = _layoutReferenceResolver.resolve(
			fragmentEntryProcessorContext.getCompanyId(), layoutJSONObject,
			fragmentEntryProcessorContext.getScopeGroupId());

		if (layout == null) {
			return StringPool.POUND;
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

		return _portal.getLayoutRelativeURL(layout, themeDisplay);
	}

	private boolean _isMappedLayout(JSONObject jsonObject) {
		return jsonObject.has("layout");
	}

	private void _replaceLinkContent(
		Element element, boolean empty, Element linkElement) {

		if (linkElement == element) {
			return;
		}

		linkElement.empty();

		if (empty) {
			linkElement.appendChild(element);
		}
		else {
			linkElement.appendChildren(element.childNodes());
		}
	}

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private LayoutReferenceResolver _layoutReferenceResolver;

	@Reference
	private Portal _portal;

}