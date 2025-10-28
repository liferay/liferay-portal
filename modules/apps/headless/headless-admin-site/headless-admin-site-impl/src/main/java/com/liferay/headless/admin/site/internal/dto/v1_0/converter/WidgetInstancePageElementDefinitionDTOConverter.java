/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import jakarta.portlet.PortletPreferences;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "dto.class.name=com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem",
	service = DTOConverter.class
)
public class WidgetInstancePageElementDefinitionDTOConverter
	implements DTOConverter
		<FragmentStyledLayoutStructureItem,
		 WidgetInstancePageElementDefinition> {

	@Override
	public String getContentType() {
		return WidgetInstancePageElementDefinition.class.getSimpleName();
	}

	@Override
	public WidgetInstancePageElementDefinition toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem)
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			throw new UnsupportedOperationException();
		}

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				new WidgetInstancePageElementDefinition();

		widgetInstancePageElementDefinition.setCssClasses(
			() -> {
				Set<String> cssClasses =
					fragmentStyledLayoutStructureItem.getCssClasses();

				if (SetUtil.isEmpty(cssClasses)) {
					return null;
				}

				return ArrayUtil.toStringArray(cssClasses);
			});
		widgetInstancePageElementDefinition.setCustomCSS(
			() -> {
				String customCSS =
					fragmentStyledLayoutStructureItem.getCustomCSS();

				if (Validator.isNotNull(customCSS)) {
					return customCSS;
				}

				return null;
			});
		widgetInstancePageElementDefinition.
			setDraftWidgetInstanceExternalReferenceCode(
				() -> {
					FragmentEntryLink originalFragmentEntryLink =
						_fragmentEntryLinkLocalService.
							fetchFragmentEntryLinkByExternalReferenceCode(
								fragmentEntryLink.
									getOriginalFragmentEntryLinkERC(),
								fragmentEntryLink.getGroupId());

					if (originalFragmentEntryLink == null) {
						return null;
					}

					return originalFragmentEntryLink.getExternalReferenceCode();
				});
		widgetInstancePageElementDefinition.setFragmentViewports(
			() -> FragmentViewportUtil.toFragmentViewports(
				fragmentStyledLayoutStructureItem.getItemConfigJSONObject()));
		widgetInstancePageElementDefinition.setIndexed(
			fragmentStyledLayoutStructureItem::isIndexed);
		widgetInstancePageElementDefinition.setName(
			fragmentStyledLayoutStructureItem::getName);
		widgetInstancePageElementDefinition.setWidgetInstance(
			() -> _getWidgetInstance(fragmentEntryLink));
		widgetInstancePageElementDefinition.
			setWidgetInstanceExternalReferenceCode(
				fragmentEntryLink::getExternalReferenceCode);

		return widgetInstancePageElementDefinition;
	}

	private Map<String, Object> _getWidgetConfig(long plid, String portletId) {
		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return null;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			PortletIdCodec.decodePortletName(portletId));

		if (portlet == null) {
			return null;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.getLayoutPortletSetup(
				layout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId, portlet.getDefaultPreferences());

		if (portletPreferences == null) {
			return null;
		}

		Map<String, Object> portletConfigurationMap = new TreeMap<>();

		Map<String, String[]> portletPreferencesMap =
			portletPreferences.getMap();

		for (Map.Entry<String, String[]> entrySet :
				portletPreferencesMap.entrySet()) {

			String[] values = entrySet.getValue();

			if (values == null) {
				portletConfigurationMap.put(
					entrySet.getKey(), StringPool.BLANK);
			}
			else if (values.length == 1) {
				portletConfigurationMap.put(entrySet.getKey(), values[0]);
			}
			else {
				portletConfigurationMap.put(entrySet.getKey(), values);
			}
		}

		return portletConfigurationMap;
	}

	private WidgetInstance _getWidgetInstance(
		FragmentEntryLink fragmentEntryLink) {

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		if (JSONUtil.isEmpty(jsonObject) || !jsonObject.has("portletId")) {
			return null;
		}

		String instanceId = jsonObject.getString("instanceId", null);

		String portletId = PortletIdCodec.encode(
			jsonObject.getString("portletId"), instanceId);

		return new WidgetInstance() {
			{
				setWidgetConfig(
					() -> _getWidgetConfig(
						fragmentEntryLink.getPlid(), portletId));
				setWidgetInstanceId(() -> instanceId);
				setWidgetName(
					() -> PortletIdCodec.decodePortletName(portletId));
				setWidgetPermissions(
					() -> _getWidgetPermissions(
						fragmentEntryLink.getPlid(), portletId));
			}
		};
	}

	private WidgetPermission[] _getWidgetPermissions(
		long plid, String portletId) {

		Map<String, String[]> permissionsMap =
			_portletPermissionsExporter.getPortletPermissions(plid, portletId);

		if (MapUtil.isEmpty(permissionsMap)) {
			return new WidgetPermission[0];
		}

		return TransformUtil.transformToArray(
			permissionsMap.entrySet(),
			entry -> {
				if (ArrayUtil.isEmpty(entry.getValue())) {
					return null;
				}

				return new WidgetPermission() {
					{
						setActionIds(entry::getValue);
						setRoleName(entry::getKey);
					}
				};
			},
			WidgetPermission.class);
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPermissionsExporter _portletPermissionsExporter;

}