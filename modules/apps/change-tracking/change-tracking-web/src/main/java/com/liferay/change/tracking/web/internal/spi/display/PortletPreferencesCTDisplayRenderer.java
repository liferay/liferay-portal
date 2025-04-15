/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = CTDisplayRenderer.class)
public class PortletPreferencesCTDisplayRenderer
	extends BaseCTDisplayRenderer<PortletPreferences> {

	@Override
	public Class<PortletPreferences> getModelClass() {
		return PortletPreferences.class;
	}

	@Override
	public String getTitle(
		Locale locale, PortletPreferences portletPreferences) {

		List<String> arguments = new ArrayList<>(2);

		Portlet portlet = _portletLocalService.getPortletById(
			portletPreferences.getCompanyId(),
			portletPreferences.getPortletId());

		try {
			arguments.add(_portal.getPortletTitle(portlet, locale));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			arguments.add(portlet.getPortletName());
		}

		Layout layout = _layoutLocalService.fetchLayout(
			portletPreferences.getPlid());

		if (layout == null) {
			arguments.add(_language.get(locale, "control-panel"));
		}
		else {
			arguments.add(layout.getName(locale));
		}

		return _language.format(
			locale, "x-on-x-page", arguments.toArray(new String[0]), false);
	}

	@Override
	public String getTypeName(Locale locale) {
		return _language.get(locale, "widget");
	}

	@Override
	public boolean isHideable(PortletPreferences portletPreferences) {
		Portlet portlet = _portletLocalService.getPortletById(
			portletPreferences.getCompanyId(),
			portletPreferences.getPortletId());

		if (portlet.isSystem()) {
			return true;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			portletPreferences.getPlid());

		if ((layout == null) ||
			layout.isPortletEmbedded(
				portletPreferences.getPortletId(), layout.getGroupId()) ||
			layout.isSystem() || layout.isTypeControlPanel()) {

			return true;
		}

		return false;
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<PortletPreferences> displayBuilder) {

		PortletPreferences portletPreferences = displayBuilder.getModel();

		displayBuilder.display(
			"name",
			() -> {
				Portlet portlet = _portletLocalService.getPortletById(
					portletPreferences.getCompanyId(),
					portletPreferences.getPortletId());

				try {
					return _portal.getPortletTitle(
						portlet, displayBuilder.getLocale());
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}

					return portlet.getPortletName();
				}
			}
		).display(
			"preferences",
			() -> {
				jakarta.portlet.PortletPreferences jxPortletPreferences =
					_portletPreferenceValueLocalService.getPreferences(
						portletPreferences);

				Map<String, String[]> map = jxPortletPreferences.getMap();

				if (map.isEmpty()) {
					return _language.get(
						displayBuilder.getLocale(),
						"this-widget-has-not-configured-any-preferences");
				}

				StringBundler sb = new StringBundler();

				for (Map.Entry<String, String[]> entry : map.entrySet()) {
					sb.append(
						_language.get(
							displayBuilder.getLocale(),
							CamelCaseUtil.fromCamelCase(entry.getKey())));
					sb.append(": ");
					sb.append(StringUtil.merge(entry.getValue()));
					sb.append("\n");
				}

				return sb.toString();
			}
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesCTDisplayRenderer.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferenceValueLocalService
		_portletPreferenceValueLocalService;

}