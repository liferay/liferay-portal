/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.render.PortletRenderParts;
import com.liferay.portal.kernel.portlet.render.PortletRenderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = PortletRegistry.class)
public class PortletRegistryImpl implements PortletRegistry {

	@Override
	public List<String> getFragmentEntryLinkPortletIds(
		Document document, FragmentEntryLink fragmentEntryLink) {

		List<String> portletIds = new ArrayList<>();

		if (fragmentEntryLink.isTypePortlet()) {
			JSONObject jsonObject =
				fragmentEntryLink.getEditableValuesJSONObject();

			if (jsonObject == null) {
				return portletIds;
			}

			String portletId = jsonObject.getString("portletId");

			if (Validator.isNotNull(portletId)) {
				String instanceId = jsonObject.getString("instanceId");

				if (Objects.equals(instanceId, "0")) {
					instanceId = StringPool.BLANK;
				}

				portletIds.add(PortletIdCodec.encode(portletId, instanceId));
			}

			return portletIds;
		}

		String html = fragmentEntryLink.getHtml();

		_addRuntimeTagPortletIds(fragmentEntryLink, html, portletIds);
		_addWidgetTagPortletIds(document, fragmentEntryLink, html, portletIds);

		return portletIds;
	}

	@Override
	public List<String> getFragmentEntryLinkPortletIds(
		FragmentEntryLink fragmentEntryLink) {

		return getFragmentEntryLinkPortletIds(null, fragmentEntryLink);
	}

	@Override
	public List<String> getPortletAliases() {
		return new ArrayList<>(_aliasPortletNames.keySet());
	}

	@Override
	public String getPortletName(String alias) {
		return _aliasPortletNames.get(alias);
	}

	@Override
	public void registerAlias(String alias, String portletName) {
		_aliasPortletNames.put(alias, portletName);
	}

	@Override
	public void unregisterAlias(String alias) {
		_aliasPortletNames.remove(alias);
	}

	@Override
	public void writePortletPaths(
			FragmentEntryLink fragmentEntryLink,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortalException {

		List<String> fragmentEntryLinkPortletIds =
			getFragmentEntryLinkPortletIds(fragmentEntryLink);

		if (ListUtil.isEmpty(fragmentEntryLinkPortletIds)) {
			return;
		}

		Set<Portlet> portlets = new HashSet<>();

		for (String fragmentEntryLinkPortletId : fragmentEntryLinkPortletIds) {
			Portlet portlet = _portletLocalService.getPortletById(
				fragmentEntryLinkPortletId);

			if ((portlet == null) || !portlet.isActive() ||
				portlet.isUndeployedPortlet()) {

				continue;
			}

			portlets.add(portlet);
		}

		for (Portlet portlet : portlets) {
			try {
				PortletRenderParts portletRenderParts =
					PortletRenderUtil.getPortletRenderParts(
						httpServletRequest, StringPool.BLANK, portlet);

				PortletRenderUtil.writeHeaderPaths(
					httpServletResponse, portletRenderParts);

				PortletRenderUtil.writeFooterPaths(
					httpServletResponse, portletRenderParts);
			}
			catch (Exception exception) {
				_log.error(
					"Unable to write portlet paths " + portlet.getPortletId(),
					exception);
			}
		}
	}

	private void _addRuntimeTagPortletIds(
		FragmentEntryLink fragmentEntryLink, String html,
		List<String> portletIds) {

		int index = html.indexOf(_LIFERAY_PORTLET_MACRO);

		while (index != -1) {
			int contentIndex = index + _LIFERAY_PORTLET_MACRO.length();

			if (!html.startsWith(".runtime", contentIndex) &&
				!html.startsWith("[\"runtime\"]", contentIndex) &&
				!html.startsWith("['runtime']", contentIndex)) {

				index = html.indexOf(_LIFERAY_PORTLET_MACRO, index + 1);

				continue;
			}

			int endIndex = _getMacroEndIndex(html, contentIndex);

			if (endIndex == -1) {
				endIndex = html.indexOf("/]", contentIndex);
			}

			if (endIndex == -1) {
				break;
			}

			String macro = html.substring(index, endIndex);

			index = html.indexOf(_LIFERAY_PORTLET_MACRO, endIndex);

			String portletName = _getAttributeValue("portletName", macro);

			if (Validator.isNull(portletName)) {
				continue;
			}

			String instanceId = _getAttributeValue("instanceId", macro);

			String portletId = PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName),
				StringUtil.replace(
					instanceId, "fragmentEntryLinkNamespace",
					fragmentEntryLink.getNamespace()));

			portletIds.add(_portal.getJsSafePortletId(portletId));
		}
	}

	private void _addWidgetTagPortletIds(
		Document document, FragmentEntryLink fragmentEntryLink, String html,
		List<String> portletIds) {

		if (!html.contains("lfr-widget-")) {
			return;
		}

		if (document == null) {
			document = _getDocument(html);
		}

		for (Element element : document.select("*")) {
			String tagName = element.tagName();

			if (!StringUtil.startsWith(tagName, "lfr-widget-")) {
				continue;
			}

			String alias = StringUtil.removeSubstring(tagName, "lfr-widget-");

			String portletName = getPortletName(alias);

			if (Validator.isNull(portletName)) {
				continue;
			}

			String portletId = PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName),
				fragmentEntryLink.getNamespace() + element.attr("id"));

			portletIds.add(_portal.getJsSafePortletId(portletId));
		}
	}

	private String _getAttributeValue(String attributeName, String string) {
		int index = 0;

		while (index < string.length()) {
			if (Character.isWhitespace(string.charAt(index))) {
				index++;

				continue;
			}

			int startIndex = index;

			while ((index < string.length()) &&
				   (string.charAt(index) != CharPool.EQUAL) &&
				   !Character.isWhitespace(string.charAt(index))) {

				index++;
			}

			String name = string.substring(startIndex, index);

			while ((index < string.length()) &&
				   Character.isWhitespace(string.charAt(index))) {

				index++;
			}

			if ((index >= string.length()) ||
				(string.charAt(index) != CharPool.EQUAL)) {

				continue;
			}

			index++;

			while ((index < string.length()) &&
				   Character.isWhitespace(string.charAt(index))) {

				index++;
			}

			String value = null;

			if ((index < string.length()) &&
				((string.charAt(index) == CharPool.APOSTROPHE) ||
				 (string.charAt(index) == CharPool.QUOTE))) {

				startIndex = index + 1;

				index = _getQuotedStringEndIndex(
					startIndex, string.charAt(index), string);

				value = string.substring(startIndex, index);

				index++;
			}
			else {
				startIndex = index;

				while ((index < string.length()) &&
					   !Character.isWhitespace(string.charAt(index))) {

					index++;
				}

				value = string.substring(startIndex, index);
			}

			if (name.equals(attributeName)) {
				return value;
			}
		}

		return null;
	}

	private Document _getDocument(String html) {
		Document document = Jsoup.parseBodyFragment(html);

		Document.OutputSettings outputSettings = new Document.OutputSettings();

		outputSettings.prettyPrint(false);

		document.outputSettings(outputSettings);

		return document;
	}

	private int _getMacroEndIndex(String html, int index) {
		while (index < html.length()) {
			char c = html.charAt(index);

			if ((c == CharPool.APOSTROPHE) || (c == CharPool.QUOTE)) {
				index = _getQuotedStringEndIndex(index + 1, c, html);
			}
			else if ((c == CharPool.FORWARD_SLASH) &&
					 html.startsWith(StringPool.CLOSE_BRACKET, index + 1)) {

				return index;
			}

			index++;
		}

		return -1;
	}

	private int _getQuotedStringEndIndex(int index, char quote, String string) {
		while (index < string.length()) {
			char c = string.charAt(index);

			if (c == quote) {
				return index;
			}

			if (c == CharPool.BACK_SLASH) {
				index++;
			}

			index++;
		}

		return string.length();
	}

	private static final String _LIFERAY_PORTLET_MACRO = "[@liferay_portlet";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRegistryImpl.class);

	private final Map<String, String> _aliasPortletNames =
		new ConcurrentHashMap<>();

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}