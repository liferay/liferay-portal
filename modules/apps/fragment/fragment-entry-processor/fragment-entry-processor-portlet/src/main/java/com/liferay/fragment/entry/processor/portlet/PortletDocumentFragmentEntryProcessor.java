/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.portlet;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DocumentFragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.renderer.FragmentPortletRenderer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.exportimport.staging.StagingAdvicesThreadLocal;

import jakarta.portlet.PortletPreferences;

import java.util.Arrays;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=3",
	service = DocumentFragmentEntryProcessor.class
)
public class PortletDocumentFragmentEntryProcessor
	implements DocumentFragmentEntryProcessor {

	@Override
	public void processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, Document document,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		String html = fragmentEntryLink.getHtml();

		if (!html.contains("lfr-widget-") &&
			!html.contains("@liferay_portlet")) {

			return;
		}

		Elements elements = document.getAllElements();

		for (Element element : elements) {
			String tagName = element.tagName();

			String portletName = _getPortletName(tagName);

			if (Validator.isNull(portletName)) {
				continue;
			}

			Portlet portlet = _portletLocalService.getPortletById(portletName);

			String instanceId = String.valueOf(CharPool.NUMBER_0);

			String id = element.attr("id");

			if (portlet.isInstanceable()) {
				instanceId = _getInstanceId(
					fragmentEntryLink.getNamespace(), id);
			}

			String portletHTML = StringPool.BLANK;

			String defaultPreferences = portlet.getDefaultPreferences();

			if (fragmentEntryProcessorContext.isPreviewMode()) {
				portletHTML = _fragmentPortletRenderer.renderPortlet(
					fragmentEntryLink,
					fragmentEntryProcessorContext.getHttpServletRequest(),
					fragmentEntryProcessorContext.getHttpServletResponse(),
					portletName, instanceId, defaultPreferences);
			}
			else {
				long plid = ParamUtil.getLong(
					fragmentEntryProcessorContext.getHttpServletRequest(),
					"p_l_id");

				boolean stagingAdvicesThreadLocalEnabled =
					StagingAdvicesThreadLocal.isEnabled();

				try {
					StagingAdvicesThreadLocal.setEnabled(false);

					defaultPreferences = _getPreferences(
						plid, portletName, fragmentEntryLink, id,
						portlet.getDefaultPreferences());
				}
				finally {
					StagingAdvicesThreadLocal.setEnabled(
						stagingAdvicesThreadLocalEnabled);
				}

				portletHTML = _fragmentPortletRenderer.renderPortlet(
					fragmentEntryLink,
					fragmentEntryProcessorContext.getHttpServletRequest(),
					fragmentEntryProcessorContext.getHttpServletResponse(),
					portletName, instanceId,
					_getPreferences(
						plid, portletName, fragmentEntryLink, id,
						defaultPreferences));
			}

			Element portletElement = new Element("div");

			portletElement.attr("class", "portlet");

			portletElement.html(portletHTML);

			element.replaceWith(portletElement);
		}
	}

	private boolean _comparePreferences(
		PortletPreferences currentPortletPreferences,
		PortletPreferences sourcePortletPreferences) {

		Map<String, String[]> currentPreferences =
			currentPortletPreferences.getMap();

		Map<String, String[]> sourcePreferences =
			sourcePortletPreferences.getMap();

		if (currentPreferences.size() != sourcePreferences.size()) {
			return false;
		}

		for (Map.Entry<String, String[]> entry :
				currentPreferences.entrySet()) {

			if (!Arrays.equals(
					sourcePreferences.get(entry.getKey()), entry.getValue())) {

				return false;
			}
		}

		return true;
	}

	private String _getInstanceId(String namespace, String id) {
		if (Validator.isNull(namespace)) {
			namespace = StringUtil.randomId();
		}

		return namespace + id;
	}

	private String _getPortletId(
		String portletName, String namespace, String id) {

		return PortletIdCodec.encode(
			PortletIdCodec.decodePortletName(portletName),
			PortletIdCodec.decodeUserId(portletName),
			_getInstanceId(namespace, id));
	}

	private String _getPortletName(String tagName) {
		if (!StringUtil.startsWith(tagName, "lfr-widget-")) {
			return StringPool.BLANK;
		}

		String alias = tagName.substring(11);

		return _portletRegistry.getPortletName(alias);
	}

	private String _getPreferences(
		long plid, String portletName, FragmentEntryLink fragmentEntryLink,
		String id, String defaultPreferences) {

		String defaultPortletId = _getPortletId(
			portletName, fragmentEntryLink.getNamespace(), id);

		PortletPreferences jxPortletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				fragmentEntryLink.getCompanyId(),
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
				fragmentEntryLink.getPlid(), defaultPortletId,
				defaultPreferences);

		String portletId = _getPortletId(
			portletName, fragmentEntryLink.getNamespace(), id);

		com.liferay.portal.kernel.model.PortletPreferences portletPreferences =
			_portletPreferencesLocalService.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId);

		if (portletPreferences != null) {
			jxPortletPreferences =
				PortletPreferencesFactoryUtil.getLayoutPortletSetup(
					fragmentEntryLink.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					fragmentEntryLink.getPlid(), portletId,
					PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));

			_updateLayoutPortletSetup(portletPreferences, jxPortletPreferences);
		}
		else {
			int count =
				_portletPreferencesLocalService.getPortletPreferencesCount(
					fragmentEntryLink.getCompanyId(),
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, portletId);

			if (count > 0) {
				jxPortletPreferences =
					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						fragmentEntryLink.getCompanyId(),
						PortletKeys.PREFS_OWNER_ID_DEFAULT,
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
						fragmentEntryLink.getPlid(), portletId,
						PortletPreferencesFactoryUtil.toXML(
							jxPortletPreferences));
			}
		}

		return PortletPreferencesFactoryUtil.toXML(jxPortletPreferences);
	}

	private void _updateLayoutPortletSetup(
		com.liferay.portal.kernel.model.PortletPreferences portletPreferences,
		PortletPreferences jxPortletPreferences) {

		PortletPreferences currentPortletPreferences =
			_portletPreferenceValueLocalService.getPreferences(
				portletPreferences);

		if (_comparePreferences(
				currentPortletPreferences, jxPortletPreferences)) {

			return;
		}

		_portletPreferencesLocalService.updatePreferences(
			portletPreferences.getOwnerId(), portletPreferences.getOwnerType(),
			portletPreferences.getPlid(), portletPreferences.getPortletId(),
			jxPortletPreferences);
	}

	@Reference
	private FragmentPortletRenderer _fragmentPortletRenderer;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

}