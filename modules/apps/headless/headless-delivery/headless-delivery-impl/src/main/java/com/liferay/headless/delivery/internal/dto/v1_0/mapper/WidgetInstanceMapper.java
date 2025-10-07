/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.mapper;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.headless.delivery.dto.v1_0.WidgetInstance;
import com.liferay.headless.delivery.dto.v1_0.WidgetPermission;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Jürgen Kappler
 * @author Javier de Arcos
 */
public class WidgetInstanceMapper {

	public WidgetInstanceMapper(
		LayoutLocalService layoutLocalService,
		PortletLocalService portletLocalService,
		PortletPermissionsExporter portletPermissionsExporter,
		PortletPreferencesPortletConfigurationExporter
			portletPreferencesPortletConfigurationExporter) {

		_layoutLocalService = layoutLocalService;
		_portletLocalService = portletLocalService;
		_portletPermissionsExporter = portletPermissionsExporter;
		_portletPreferencesPortletConfigurationExporter =
			portletPreferencesPortletConfigurationExporter;
	}

	public WidgetInstance getWidgetInstance(
		FragmentEntryLink fragmentEntryLink, String portletId) {

		if (Validator.isNull(portletId)) {
			return null;
		}

		return new WidgetInstance() {
			{
				setWidgetConfig(
					() -> _getWidgetConfig(
						fragmentEntryLink.getPlid(), portletId));
				setWidgetInstanceId(
					() -> _getWidgetInstanceId(fragmentEntryLink, portletId));
				setWidgetName(
					() -> PortletIdCodec.decodePortletName(portletId));
				setWidgetPermissions(
					() -> _getWidgetPermissions(
						fragmentEntryLink.getPlid(), portletId));
			}
		};
	}

	private Map<String, Object> _getWidgetConfig(long plid, String portletId) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return null;
		}

		String portletName = PortletIdCodec.decodePortletName(portletId);

		Portlet portlet = _portletLocalService.getPortletById(portletName);

		if (portlet == null) {
			return null;
		}

		return _portletPreferencesPortletConfigurationExporter.
			getPortletConfiguration(plid, portletId);
	}

	private String _getWidgetInstanceId(
		FragmentEntryLink fragmentEntryLink, String portletId) {

		String instanceId = PortletIdCodec.decodeInstanceId(portletId);

		if (Validator.isNull(instanceId)) {
			return null;
		}

		String namespace = fragmentEntryLink.getNamespace();

		if (instanceId.startsWith(namespace)) {
			instanceId = instanceId.substring(namespace.length());
		}

		if (Validator.isNull(instanceId)) {
			return null;
		}

		return instanceId;
	}

	private WidgetPermission[] _getWidgetPermissions(
		long plid, String portletId) {

		Map<String, String[]> permissionsMap =
			_portletPermissionsExporter.getPortletPermissions(plid, portletId);

		if (MapUtil.isEmpty(permissionsMap)) {
			return null;
		}

		return TransformUtil.transformToArray(
			permissionsMap.entrySet(),
			entry -> new WidgetPermission() {
				{
					setActionKeys(entry::getValue);
					setRoleKey(entry::getKey);
				}
			},
			WidgetPermission.class);
	}

	private final LayoutLocalService _layoutLocalService;
	private final PortletLocalService _portletLocalService;
	private final PortletPermissionsExporter _portletPermissionsExporter;
	private final PortletPreferencesPortletConfigurationExporter
		_portletPreferencesPortletConfigurationExporter;

}