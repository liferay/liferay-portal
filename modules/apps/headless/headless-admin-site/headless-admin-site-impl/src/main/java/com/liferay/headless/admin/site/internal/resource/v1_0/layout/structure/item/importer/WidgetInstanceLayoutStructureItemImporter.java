/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.layout.importer.PortletPermissionsImporter;
import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mikel Lorza
 */
public class WidgetInstanceLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				(WidgetInstancePageElementDefinition)
					pageElement.getPageElementDefinition();

		if (widgetInstancePageElementDefinition == null) {
			return null;
		}

		WidgetInstance widgetInstance =
			widgetInstancePageElementDefinition.getWidgetInstance();

		if (widgetInstance == null) {
			throw new UnsupportedOperationException();
		}

		Layout layout = layoutStructureItemImporterContext.getLayout();

		String portletId = PortletIdCodec.encode(
			widgetInstance.getWidgetName(),
			widgetInstance.getWidgetInstanceId());

		_importPortletPreferences(
			layout, portletId, widgetInstance.getWidgetConfig());

		_importPortletPermissions(
			layout, portletId, widgetInstance.getWidgetName(),
			widgetInstance.getWidgetPermissions());

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					widgetInstancePageElementDefinition.
						getWidgetInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			fragmentEntryLink = _addFragmentEntryLink(
				widgetInstancePageElementDefinition.
					getDraftWidgetInstanceExternalReferenceCode(),
				widgetInstancePageElementDefinition.
					getWidgetInstanceExternalReferenceCode(),
				layoutStructureItemImporterContext, widgetInstance);
		}
		else {
			fragmentEntryLink = _updateFragmentEntryLink(
				widgetInstancePageElementDefinition.
					getDraftWidgetInstanceExternalReferenceCode(),
				fragmentEntryLink, layoutStructureItemImporterContext,
				widgetInstance);
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					pageElement.getExternalReferenceCode(),
					LayoutStructureUtil.getParentExternalReferenceCode(
						pageElement, layoutStructure),
					pageElement.getPosition());

		fragmentStyledLayoutStructureItem.setCssClasses(
			_getCssClasses(
				widgetInstancePageElementDefinition.getCssClasses()));
		fragmentStyledLayoutStructureItem.setCustomCSS(
			widgetInstancePageElementDefinition.getCustomCSS());
		fragmentStyledLayoutStructureItem.setFragmentEntryLinkId(
			fragmentEntryLink.getFragmentEntryLinkId());
		fragmentStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(
				widgetInstancePageElementDefinition.getIndexed()));
		fragmentStyledLayoutStructureItem.setName(
			widgetInstancePageElementDefinition.getName());

		JSONObject fragmentViewportsJSONObject =
			FragmentViewportUtil.toFragmentViewportsJSONObject(
				widgetInstancePageElementDefinition.getFragmentViewports());

		if (fragmentViewportsJSONObject != null) {
			fragmentStyledLayoutStructureItem.updateItemConfig(
				fragmentViewportsJSONObject);
		}

		return fragmentStyledLayoutStructureItem;
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String draftWidgetInstanceExternalReferenceCode,
			String externalReferenceCode,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			WidgetInstance widgetInstance)
		throws Exception {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		JSONObject editableValueJSONObject = _getEditableValuesJSONObject(
			null, widgetInstance);

		return FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
			externalReferenceCode,
			layoutStructureItemImporterContext.getUserId(),
			layoutStructureItemImporterContext.getGroupId(),
			draftWidgetInstanceExternalReferenceCode, null, null,
			layoutStructureItemImporterContext.getSegmentsExperienceId(),
			layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK,
			editableValueJSONObject.toString(), _getNamespace(widgetInstance),
			0, null, FragmentConstants.TYPE_PORTLET, new ServiceContext());
	}

	private LinkedHashSet<String> _getCssClasses(String[] cssClasses) {
		if (cssClasses == null) {
			return null;
		}

		return new LinkedHashSet<>(Arrays.asList(cssClasses));
	}

	private JSONObject _getEditableValuesJSONObject(
		FragmentEntryLink fragmentEntryLink, WidgetInstance widgetInstance) {

		JSONObject editableValuesJSONObject = null;

		FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry =
			_fragmentEntryProcessorRegistryServiceTracker.getService();

		if (fragmentEntryLink != null) {
			editableValuesJSONObject =
				fragmentEntryLink.getEditableValuesJSONObject();
		}
		else if (fragmentEntryProcessorRegistry != null) {
			editableValuesJSONObject =
				fragmentEntryProcessorRegistry.
					getDefaultEditableValuesJSONObject(StringPool.BLANK, null);
		}
		else {
			editableValuesJSONObject = JSONFactoryUtil.createJSONObject();
		}

		return editableValuesJSONObject.put(
			"instanceId",
			() -> {
				String instanceId = _getInstanceId(
					widgetInstance.getWidgetInstanceId(),
					widgetInstance.getWidgetName());

				if (Validator.isNull(instanceId)) {
					return null;
				}

				return instanceId;
			}
		).put(
			"portletId", widgetInstance.getWidgetName()
		);
	}

	private String _getInstanceId(String widgetInstanceId, String widgetName) {
		Portlet portlet = PortletLocalServiceUtil.getPortletById(widgetName);

		if (portlet.isInstanceable()) {
			return widgetInstanceId;
		}

		return StringPool.BLANK;
	}

	private String _getNamespace(WidgetInstance widgetInstance) {
		String namespace = null;

		if (Validator.isNull(widgetInstance.getWidgetInstanceId())) {
			namespace = StringUtil.randomId();
		}
		else {
			namespace = widgetInstance.getWidgetInstanceId();
		}

		return namespace;
	}

	private List<Map<String, Object>> _getWidgetPermissionsMaps(
		WidgetPermission[] widgetPermissions) {

		if (ArrayUtil.isEmpty(widgetPermissions)) {
			return new ArrayList<>();
		}

		List<Map<String, Object>> widgetPermissionsMaps = new ArrayList<>();

		for (WidgetPermission widgetPermission : widgetPermissions) {
			widgetPermissionsMaps.add(
				HashMapBuilder.<String, Object>put(
					"actionKeys", Arrays.asList(widgetPermission.getActionIds())
				).put(
					"roleKey", widgetPermission.getRoleName()
				).build());
		}

		return widgetPermissionsMaps;
	}

	private void _importPortletPermissions(
			Layout layout, String portletId, String portletName,
			WidgetPermission[] widgetPermissions)
		throws Exception {

		PortletPermissionsImporter portletPermissionsImporter =
			_portletPermissionsImporterServiceTracker.getService();

		if (portletPermissionsImporter == null) {
			return;
		}

		if ((widgetPermissions != null) && (widgetPermissions.length == 0)) {
			ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
				layout.getCompanyId(), portletName,
				ResourceConstants.SCOPE_INDIVIDUAL,
				PortletPermissionUtil.getPrimaryKey(
					layout.getPlid(), portletId));

			return;
		}

		portletPermissionsImporter.importPortletPermissions(
			layout.getPlid(), portletId, Collections.emptySet(),
			_getWidgetPermissionsMaps(widgetPermissions));
	}

	private void _importPortletPreferences(
			Layout layout, String portletId, Map<String, Object> widgetConfig)
		throws Exception {

		PortletPreferencesPortletConfigurationImporter
			portletPreferencesPortletConfigurationImporter =
				_portletPreferencesPortletConfigurationImporterServiceTracker.
					getService();

		if (portletPreferencesPortletConfigurationImporter == null) {
			return;
		}

		portletPreferencesPortletConfigurationImporter.
			importPortletConfiguration(
				layout.getPlid(), portletId, widgetConfig);
	}

	private FragmentEntryLink _updateFragmentEntryLink(
			String draftWidgetInstanceExternalReferenceCode,
			FragmentEntryLink fragmentEntryLink,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			WidgetInstance widgetInstance)
		throws Exception {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		String fragmentEntryLinkPortletId = PortletIdCodec.encode(
			editableValuesJSONObject.getString("portletId"),
			editableValuesJSONObject.getString("instanceId"));

		String widgetInstancePortletId = PortletIdCodec.encode(
			widgetInstance.getWidgetName(),
			widgetInstance.getWidgetInstanceId());

		if (!StringUtil.equals(
				fragmentEntryLinkPortletId, widgetInstancePortletId)) {

			Layout layout = layoutStructureItemImporterContext.getLayout();

			PortletPreferencesLocalServiceUtil.deletePortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				fragmentEntryLinkPortletId);

			ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
				layout.getCompanyId(),
				editableValuesJSONObject.getString("portletId"),
				ResourceConstants.SCOPE_INDIVIDUAL,
				PortletPermissionUtil.getPrimaryKey(
					layout.getPlid(), fragmentEntryLinkPortletId));
		}

		editableValuesJSONObject = _getEditableValuesJSONObject(
			fragmentEntryLink, widgetInstance);

		fragmentEntryLink.setOriginalFragmentEntryLinkERC(
			draftWidgetInstanceExternalReferenceCode);
		fragmentEntryLink.setEditableValues(
			editableValuesJSONObject.toString());

		return FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
			fragmentEntryLink);
	}

	private static final ServiceTracker
		<FragmentEntryProcessorRegistry, FragmentEntryProcessorRegistry>
			_fragmentEntryProcessorRegistryServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(
						WidgetInstanceLayoutStructureItemImporter.class),
					FragmentEntryProcessorRegistry.class);
	private static final ServiceTracker
		<PortletPermissionsImporter, PortletPermissionsImporter>
			_portletPermissionsImporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(
						WidgetInstanceLayoutStructureItemImporter.class),
					PortletPermissionsImporter.class);
	private static final ServiceTracker
		<PortletPreferencesPortletConfigurationImporter,
		 PortletPreferencesPortletConfigurationImporter>
			_portletPreferencesPortletConfigurationImporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(
						WidgetInstanceLayoutStructureItemImporter.class),
					PortletPreferencesPortletConfigurationImporter.class);

}