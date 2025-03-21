/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.processor;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.render.PortletRenderParts;
import com.liferay.portal.kernel.portlet.render.PortletRenderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			try {
				JSONObject jsonObject = _jsonFactory.createJSONObject(
					fragmentEntryLink.getEditableValues());

				String portletId = jsonObject.getString("portletId");

				if (Validator.isNotNull(portletId)) {
					String instanceId = jsonObject.getString("instanceId");

					if (Objects.equals(instanceId, "0")) {
						instanceId = StringPool.BLANK;
					}

					portletIds.add(
						PortletIdCodec.encode(portletId, instanceId));
				}
			}
			catch (PortalException portalException) {
				_log.error("Unable to get portlet IDs", portalException);
			}

			return portletIds;
		}

		String html = fragmentEntryLink.getHtml();

		if (!html.contains("@liferay_portlet") &&
			!html.contains("lfr-widget-")) {

			return portletIds;
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

		Matcher liferayPortletRuntimeMatcher =
			_liferayPortletRuntimePattern.matcher(fragmentEntryLink.getHtml());

		while (liferayPortletRuntimeMatcher.find()) {
			String portletName = _getAttributeValue(
				"portletName", liferayPortletRuntimeMatcher.group(2));

			if (Validator.isNull(portletName)) {
				continue;
			}

			String instanceId = _getAttributeValue(
				"instanceId", liferayPortletRuntimeMatcher.group(1));

			if (Validator.isNull(instanceId)) {
				instanceId = _getAttributeValue(
					"instanceId", liferayPortletRuntimeMatcher.group(3));
			}

			String portletId = PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName),
				StringUtil.replace(
					instanceId, "fragmentEntryLinkNamespace",
					fragmentEntryLink.getNamespace()));

			portletIds.add(_portal.getJsSafePortletId(portletId));
		}

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

	private String _getAttributeValue(String attributeName, String string) {
		String s = StringUtil.extractLast(
			string, attributeName + StringPool.EQUAL);

		if (Validator.isNull(s)) {
			return s;
		}

		if (s.startsWith(StringPool.QUOTE)) {
			return StringUtil.extractFirst(s.substring(1), StringPool.QUOTE);
		}

		String[] strings = s.split("\\s+");

		if (ArrayUtil.isNotEmpty(strings)) {
			return strings[0];
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

	private static final Log _log = LogFactoryUtil.getLog(
		PortletRegistryImpl.class);

	private static final Pattern _liferayPortletRuntimePattern =
		Pattern.compile(
			"\\[@liferay_portlet(?=\\.runtime|\\[\"runtime\"\\])([\\s\\S]*)?" +
				"(portletName=\"\\w+\")([\\s\\S]*)?\\/\\]");

	private final Map<String, String> _aliasPortletNames =
		new ConcurrentHashMap<>();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}