/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.structure.util;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.PageElement;
import com.liferay.layout.importer.PortletPermissionsImporter;
import com.liferay.layout.internal.importer.LayoutStructureItemImporterContext;
import com.liferay.layout.internal.importer.helper.PortletConfigurationImporterHelper;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortletIdException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jürgen Kappler
 */
public class WidgetLayoutStructureItemImporter
	extends BaseLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	public WidgetLayoutStructureItemImporter(
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry,
		PortletConfigurationImporterHelper portletConfigurationImporterHelper,
		PortletLocalService portletLocalService,
		PortletPermissionsImporter portletPermissionsImporter,
		PortletRegistry portletRegistry) {

		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_fragmentEntryProcessorRegistry = fragmentEntryProcessorRegistry;
		_portletConfigurationImporterHelper =
			portletConfigurationImporterHelper;
		_portletLocalService = portletLocalService;
		_portletPermissionsImporter = portletPermissionsImporter;
		_portletRegistry = portletRegistry;
	}

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement, Set<String> warningMessages)
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			layoutStructureItemImporterContext.getLayout(), pageElement,
			layoutStructureItemImporterContext.getSegmentsExperienceId(),
			warningMessages);

		if (fragmentEntryLink == null) {
			return null;
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					layoutStructureItemImporterContext.getItemId(pageElement),
					layoutStructureItemImporterContext.getParentItemId(),
					layoutStructureItemImporterContext.getPosition());

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return fragmentStyledLayoutStructureItem;
		}

		if (definitionMap.containsKey("cssClasses")) {
			List<String> cssClasses = (List<String>)definitionMap.get(
				"cssClasses");

			fragmentStyledLayoutStructureItem.setCssClasses(
				new HashSet<>(cssClasses));
		}

		if (definitionMap.containsKey("customCSS")) {
			fragmentStyledLayoutStructureItem.setCustomCSS(
				String.valueOf(definitionMap.get("customCSS")));
		}

		if (definitionMap.containsKey("customCSSViewports")) {
			List<Map<String, Object>> customCSSViewports =
				(List<Map<String, Object>>)definitionMap.get(
					"customCSSViewports");

			for (Map<String, Object> customCSSViewport : customCSSViewports) {
				fragmentStyledLayoutStructureItem.setCustomCSSViewport(
					(String)customCSSViewport.get("id"),
					(String)customCSSViewport.get("customCSS"));
			}
		}

		Map<String, Object> fragmentStyleMap =
			(Map<String, Object>)definitionMap.get("fragmentStyle");

		if (fragmentStyleMap != null) {
			JSONObject jsonObject = JSONUtil.put(
				"styles",
				toStylesJSONObject(
					layoutStructureItemImporterContext, fragmentStyleMap));

			fragmentStyledLayoutStructureItem.updateItemConfig(jsonObject);
		}

		if (definitionMap.containsKey("fragmentViewports")) {
			List<Map<String, Object>> fragmentViewports =
				(List<Map<String, Object>>)definitionMap.get(
					"fragmentViewports");

			for (Map<String, Object> fragmentViewport : fragmentViewports) {
				JSONObject jsonObject = JSONUtil.put(
					(String)fragmentViewport.get("id"),
					toFragmentViewportStylesJSONObject(fragmentViewport));

				fragmentStyledLayoutStructureItem.updateItemConfig(jsonObject);
			}
		}

		if (definitionMap.containsKey("name")) {
			fragmentStyledLayoutStructureItem.setName(
				GetterUtil.getString(definitionMap.get("name")));
		}

		return fragmentStyledLayoutStructureItem;
	}

	@Override
	public PageElement.Type getPageElementType() {
		return PageElement.Type.WIDGET;
	}

	private FragmentEntryLink _addFragmentEntryLink(
			Layout layout, PageElement pageElement, long segmentsExperienceId,
			Set<String> warningMessages)
		throws Exception {

		Map<String, Object> definitionMap = getDefinitionMap(
			pageElement.getDefinition());

		if (definitionMap == null) {
			return null;
		}

		Map<String, Object> widgetInstance =
			(Map<String, Object>)definitionMap.get("widgetInstance");

		if (widgetInstance == null) {
			return null;
		}

		String widgetName = (String)widgetInstance.get("widgetName");

		if (Validator.isNull(widgetName)) {
			return null;
		}

		Portlet portlet = _portletLocalService.fetchPortletById(
			layout.getCompanyId(), widgetName);

		if (portlet == null) {
			return null;
		}

		String widgetInstanceId = (String)widgetInstance.get(
			"widgetInstanceId");

		JSONObject editableValueJSONObject =
			_fragmentEntryProcessorRegistry.getDefaultEditableValuesJSONObject(
				StringPool.BLANK, null);

		if (Validator.isNull(widgetInstanceId)) {
			widgetInstanceId = StringUtil.randomId();
		}

		widgetInstanceId = _getPortletInstanceId(
			layout, portlet, widgetInstanceId, segmentsExperienceId);

		editableValueJSONObject.put(
			"instanceId", widgetInstanceId
		).put(
			"portletId", widgetName
		);

		Map<String, Object> widgetConfigDefinitionMap =
			(Map<String, Object>)widgetInstance.get("widgetConfig");

		_portletConfigurationImporterHelper.importPortletConfiguration(
			layout.getPlid(),
			PortletIdCodec.encode(widgetName, widgetInstanceId),
			widgetConfigDefinitionMap);

		List<Map<String, Object>> widgetPermissionsMaps =
			(List<Map<String, Object>>)widgetInstance.get("widgetPermissions");

		_portletPermissionsImporter.importPortletPermissions(
			layout.getPlid(),
			PortletIdCodec.encode(widgetName, widgetInstanceId),
			warningMessages, widgetPermissionsMaps);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, serviceContext.getUserId(), layout.getGroupId(), 0, 0,
			segmentsExperienceId, layout.getPlid(), StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			editableValueJSONObject.toString(), widgetInstanceId, 0, null,
			FragmentConstants.TYPE_PORTLET, serviceContext);
	}

	private String _getPortletInstanceId(
			Layout layout, Portlet portlet, String portletInstanceId,
			long segmentsExperienceId)
		throws Exception {

		if (portlet.isInstanceable()) {
			return portletInstanceId;
		}

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksBySegmentsExperienceId(
						layout.getGroupId(), segmentsExperienceId,
						layout.getPlid(), false)) {

			for (String portletId :
					_portletRegistry.getFragmentEntryLinkPortletIds(
						null, fragmentEntryLink)) {

				if (Objects.equals(
						PortletIdCodec.decodePortletName(portletId),
						portlet.getPortletName())) {

					throw new PortletIdException(
						"Unable to add uninstanceable portlet more than once");
				}
			}
		}

		return StringPool.BLANK;
	}

	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final FragmentEntryProcessorRegistry
		_fragmentEntryProcessorRegistry;
	private final PortletConfigurationImporterHelper
		_portletConfigurationImporterHelper;
	private final PortletLocalService _portletLocalService;
	private final PortletPermissionsImporter _portletPermissionsImporter;
	private final PortletRegistry _portletRegistry;

}