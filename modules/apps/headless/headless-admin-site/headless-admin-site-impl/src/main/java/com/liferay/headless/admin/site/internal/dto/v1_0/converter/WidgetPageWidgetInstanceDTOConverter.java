/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.headless.admin.site.dto.v1_0.GeneralConfig;
import com.liferay.headless.admin.site.dto.v1_0.WidgetLookAndFeelConfig;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = DTOConverter.class)
public class WidgetPageWidgetInstanceDTOConverter
	implements DTOConverter<Layout, WidgetPageWidgetInstance> {

	@Override
	public String getContentType() {
		return WidgetPageWidgetInstance.class.getSimpleName();
	}

	@Override
	public WidgetPageWidgetInstance toDTO(
			DTOConverterContext dtoConverterContext, Layout layout)
		throws Exception {

		String portletId = GetterUtil.getString(
			dtoConverterContext.getAttribute("portletId"), null);

		if ((portletId == null) || !layout.isTypePortlet()) {
			return null;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(portletId)) {
			return null;
		}

		return new WidgetPageWidgetInstance() {
			{
				setExternalReferenceCode(() -> portletId);
				setParentSectionId(
					() -> LayoutUtil.getParentSectionId(layout, portletId));
				setPosition(() -> LayoutUtil.getPosition(layout, portletId));
				setWidgetConfig(
					() -> {
						Map<String, Object> portletConfigurationMap =
							_portletPreferencesPortletConfigurationExporter.
								getPortletConfiguration(
									layout.getPlid(), portletId);

						Map<String, Object> widgetConfig = new HashMap<>();

						for (Map.Entry<String, Object> entry :
								portletConfigurationMap.entrySet()) {

							String key = entry.getKey();

							if (_excludePreferencesNames.contains(key) ||
								key.startsWith("portletSetupTitle_")) {

								continue;
							}

							widgetConfig.put(key, entry.getValue());
						}

						if (MapUtil.isEmpty(widgetConfig)) {
							return null;
						}

						return widgetConfig;
					});
				setWidgetInstanceId(
					() -> PortletIdCodec.decodeInstanceId(portletId));
				setWidgetLookAndFeelConfig(
					() -> _getWidgetLookAndFeelConfig(layout, portletId));
				setWidgetName(
					() -> PortletIdCodec.decodePortletName(portletId));
				setWidgetPermissions(
					() -> {
						Map<String, String[]> permissionsMap =
							_portletPermissionsExporter.getPortletPermissions(
								layout.getPlid(), portletId);

						if (MapUtil.isEmpty(permissionsMap)) {
							return null;
						}

						return TransformUtil.transformToArray(
							permissionsMap.entrySet(),
							entry -> new WidgetPermission() {
								{
									setActionIds(entry::getValue);
									setRoleName(entry::getKey);
								}
							},
							WidgetPermission.class);
					});
			}
		};
	}

	@Override
	public WidgetPageWidgetInstance toDTO(Layout layout) {
		return null;
	}

	private Map<String, String> _getCustomTitleMap(
		long groupId, PortletPreferences portletPreferences) {

		Map<Locale, String> map = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales(groupId)) {
			String portletSetupTitle = portletPreferences.getValue(
				"portletSetupTitle_" + LocaleUtil.toLanguageId(locale), null);

			if (Validator.isNotNull(portletSetupTitle)) {
				map.put(locale, portletSetupTitle);
			}
		}

		if (MapUtil.isEmpty(map)) {
			return null;
		}

		return LocalizedMapUtil.getI18nMap(map);
	}

	private WidgetLookAndFeelConfig _getWidgetLookAndFeelConfig(
		Layout layout, String portletId) {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getStrictLayoutPortletSetup(
				layout, portletId);

		return new WidgetLookAndFeelConfig() {
			{
				setGeneralConfig(
					() -> new GeneralConfig() {
						{
							setApplicationDecorator(
								() -> {
									String value = GetterUtil.getString(
										portletPreferences.getValue(
											"portletSetupPortletDecoratorId",
											null),
										null);

									if (value == null) {
										return null;
									}

									return ApplicationDecorator.create(
										StringUtil.upperCaseFirstLetter(value));
								});
							setCustomTitle_i18n(
								() -> _getCustomTitleMap(
									layout.getGroupId(), portletPreferences));
							setUseCustomTitle(
								() -> {
									Object value = portletPreferences.getValue(
										"portletSetupUseCustomTitle", null);

									if (value == null) {
										return null;
									}

									return GetterUtil.getBoolean(value);
								});
						}
					});
			}
		};
	}

	private static final Collection<String> _excludePreferencesNames =
		ListUtil.fromArray(
			"portletSetupUseCustomTitle", "portletSetupPortletDecoratorId",
			"portletSetupCss");

	@Reference
	private PortletPermissionsExporter _portletPermissionsExporter;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

}