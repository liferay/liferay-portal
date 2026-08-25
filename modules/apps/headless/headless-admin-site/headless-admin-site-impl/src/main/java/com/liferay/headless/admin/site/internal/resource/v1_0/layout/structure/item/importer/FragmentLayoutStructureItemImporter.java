/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.headless.admin.site.dto.v1_0.BasicFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FormFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.FragmentInstance;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEditableElementUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEntryLinkUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEntryReference;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentEntryReferenceUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.util.FragmentConfigurationFieldValuesUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutStructureUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.PortletUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Eudaldo Alonso
 */
public class FragmentLayoutStructureItemImporter
	implements LayoutStructureItemImporter {

	@Override
	public LayoutStructureItem addLayoutStructureItem(
			LayoutStructure layoutStructure,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElement pageElement)
		throws Exception {

		PageElementDefinition pageElementDefinition =
			pageElement.getPageElementDefinition();

		FragmentInstance fragmentInstance = _getFragmentInstance(
			pageElementDefinition);

		if (fragmentInstance == null) {
			return null;
		}

		List<String> fragmentEntryLinkPortletIds = null;

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentInstance.getFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			fragmentEntryLink = _addFragmentEntryLink(
				fragmentInstance, layoutStructureItemImporterContext,
				pageElementDefinition);
		}
		else {
			PortletRegistry portletRegistry =
				_portletRegistryServiceTracker.getService();

			if (portletRegistry != null) {
				fragmentEntryLinkPortletIds =
					portletRegistry.getFragmentEntryLinkPortletIds(
						fragmentEntryLink);
			}

			fragmentEntryLink = _updateFragmentEntryLink(
				fragmentEntryLink, fragmentInstance,
				layoutStructureItemImporterContext, pageElementDefinition);
		}

		if (fragmentEntryLink == null) {
			return null;
		}

		Layout layout = layoutStructureItemImporterContext.getLayout();

		if (ArrayUtil.isNotEmpty(fragmentInstance.getWidgetInstances())) {
			for (WidgetInstance widgetInstance :
					fragmentInstance.getWidgetInstances()) {

				if (Validator.isNull(widgetInstance.getWidgetName())) {
					continue;
				}

				String portletId = PortletIdCodec.encode(
					widgetInstance.getWidgetName(),
					widgetInstance.getWidgetInstanceId());

				PortletUtil.importPortletPermissions(
					layout, portletId, widgetInstance.getWidgetName(),
					widgetInstance.getWidgetPermissions());
				PortletUtil.importPortletPreferences(
					layout, portletId, widgetInstance.getWidgetConfig());
			}
		}

		if (ListUtil.isNotEmpty(fragmentEntryLinkPortletIds)) {
			for (String fragmentEntryLinkPortletId :
					SetUtil.asymmetricDifference(
						fragmentEntryLinkPortletIds,
						_getPortletIds(
							fragmentInstance.getWidgetInstances()))) {

				PortletPreferencesLocalServiceUtil.deletePortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
					fragmentEntryLinkPortletId);
				ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
					layout.getCompanyId(), fragmentEntryLinkPortletId,
					ResourceConstants.SCOPE_INDIVIDUAL,
					PortletPermissionUtil.getPrimaryKey(
						layout.getPlid(), fragmentEntryLinkPortletId));
			}
		}

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.addFragmentStyledLayoutStructureItem(
					fragmentEntryLink.getFragmentEntryLinkId(),
					pageElement.getExternalReferenceCode(),
					LayoutStructureUtil.getParentExternalReferenceCode(
						pageElement, layoutStructure),
					pageElement.getPosition());

		JSONObject backgroundImageValueJSONObject =
			ImageValueUtil.toBackgroundImageValueJSONObject(
				fragmentInstance.getBackgroundImageValue(),
				layoutStructureItemImporterContext);

		if (backgroundImageValueJSONObject != null) {
			fragmentStyledLayoutStructureItem.updateItemConfig(
				JSONUtil.put(
					"backgroundImage", backgroundImageValueJSONObject));
		}

		fragmentStyledLayoutStructureItem.setCssClasses(
			SetUtil.fromArray(fragmentInstance.getCssClasses()));
		fragmentStyledLayoutStructureItem.setIndexed(
			GetterUtil.getBoolean(fragmentInstance.getIndexed(), true));
		fragmentStyledLayoutStructureItem.setName(fragmentInstance.getName());

		JSONObject fragmentViewportsJSONObject =
			FragmentViewportUtil.toFragmentViewportsJSONObject(
				fragmentInstance.getFragmentViewports());

		if (fragmentViewportsJSONObject != null) {
			fragmentStyledLayoutStructureItem.updateItemConfig(
				fragmentViewportsJSONObject);
		}

		return fragmentStyledLayoutStructureItem;
	}

	private FragmentEntryLink _addFragmentEntryLink(
			FragmentInstance fragmentInstance,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElementDefinition pageElementDefinition)
		throws Exception {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		FragmentEntryReference fragmentEntryReference =
			FragmentEntryReferenceUtil.getFragmentEntryReference(
				layoutStructureItemImporterContext.getCompanyId(),
				fragmentInstance.getFragmentReference(),
				layoutStructureItemImporterContext.getGroupId());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date createDate = serviceContext.getCreateDate();
		String uuid = serviceContext.getUuid();

		try {
			serviceContext.setCreateDate(fragmentInstance.getDatePropagated());
			serviceContext.setUuid(fragmentInstance.getUuid());

			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
					fragmentInstance.getFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getUserId(),
					layout.getGroupId(),
					_getOriginalFragmentEntryLinkERC(
						fragmentInstance, layoutStructureItemImporterContext),
					fragmentEntryReference.getFragmentEntryERC(),
					fragmentEntryReference.getFragmentEntryScopeERC(),
					layoutStructureItemImporterContext.
						getSegmentsExperienceId(),
					layout.getPlid(),
					GetterUtil.getString(fragmentInstance.getCss()),
					GetterUtil.getString(fragmentInstance.getHtml()),
					GetterUtil.getString(fragmentInstance.getJs()),
					GetterUtil.getString(fragmentInstance.getConfiguration()),
					_getEditableValues(
						fragmentInstance, layoutStructureItemImporterContext,
						pageElementDefinition),
					fragmentInstance.getNamespace(), 0,
					fragmentEntryReference.getRendererKey(),
					_getType(pageElementDefinition.getType()), serviceContext);

			FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry =
				layoutStructureItemImporterContext.
					getFragmentEntryProcessorRegistry();

			return FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
				fragmentEntryLink.getUserId(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				fragmentEntryProcessorRegistry.mergeDefaultEditableValues(
					fragmentEntryLink.getConfigurationJSONObject(),
					fragmentEntryLink.getEditableValuesJSONObject(),
					FragmentEntryLinkUtil.getProcessedHTML(
						fragmentEntryLink, fragmentEntryProcessorRegistry,
						layoutStructureItemImporterContext.getUser())),
				false);
		}
		finally {
			serviceContext.setCreateDate(createDate);
			serviceContext.setUuid(uuid);
		}
	}

	private String _getEditableValues(
			FragmentInstance fragmentInstance,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElementDefinition pageElementDefinition)
		throws Exception {

		return JSONUtil.put(
			FragmentEntryProcessorConstants.
				KEY_BACKGROUND_IMAGE_FRAGMENT_ENTRY_PROCESSOR,
			FragmentEditableElementUtil.
				getBackgroundImageFragmentEntryProcessorJSONObject(
					fragmentInstance.getFragmentEditableElements(),
					layoutStructureItemImporterContext)
		).put(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
			FragmentEditableElementUtil.
				getEditableFragmentEntryProcessorJSONObject(
					fragmentInstance.getFragmentEditableElements(),
					layoutStructureItemImporterContext)
		).put(
			FragmentEntryProcessorConstants.
				KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			FragmentConfigurationFieldValuesUtil.
				getFreeMarkerFragmentEntryProcessorJSONObject(
					pageElementDefinition, layoutStructureItemImporterContext)
		).toString();
	}

	private FragmentInstance _getFragmentInstance(
		PageElementDefinition pageElementDefinition) {

		if (pageElementDefinition instanceof
				BasicFragmentInstancePageElementDefinition) {

			BasicFragmentInstancePageElementDefinition
				basicFragmentInstancePageElementDefinition =
					(BasicFragmentInstancePageElementDefinition)
						pageElementDefinition;

			return basicFragmentInstancePageElementDefinition.
				getFragmentInstance();
		}

		if (pageElementDefinition instanceof
				FormFragmentInstancePageElementDefinition) {

			FormFragmentInstancePageElementDefinition
				formFragmentInstancePageElementDefinition =
					(FormFragmentInstancePageElementDefinition)
						pageElementDefinition;

			return formFragmentInstancePageElementDefinition.
				getFragmentInstance();
		}

		return null;
	}

	private String _getOriginalFragmentEntryLinkERC(
		FragmentInstance fragmentInstance,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		if (Validator.isNull(
				fragmentInstance.
					getDraftFragmentInstanceExternalReferenceCode())) {

			return null;
		}

		FragmentEntryLink fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.
				fetchFragmentEntryLinkByExternalReferenceCode(
					fragmentInstance.
						getDraftFragmentInstanceExternalReferenceCode(),
					layoutStructureItemImporterContext.getGroupId());

		if (fragmentEntryLink == null) {
			return null;
		}

		return fragmentEntryLink.getExternalReferenceCode();
	}

	private List<String> _getPortletIds(WidgetInstance[] widgetInstances) {
		List<String> portletIds = new ArrayList<>();

		if (ArrayUtil.isEmpty(widgetInstances)) {
			return portletIds;
		}

		for (WidgetInstance widgetInstance : widgetInstances) {
			portletIds.add(
				PortletIdCodec.encode(
					widgetInstance.getWidgetName(),
					widgetInstance.getWidgetInstanceId()));
		}

		return portletIds;
	}

	private int _getType(PageElementDefinition.Type pageElementDefinitionType) {
		int type = FragmentConstants.TYPE_COMPONENT;

		if (Objects.equals(
				PageElementDefinition.Type.FORM_FRAGMENT,
				pageElementDefinitionType)) {

			type = FragmentConstants.TYPE_INPUT;
		}

		return type;
	}

	private FragmentEntryLink _updateFragmentEntryLink(
			FragmentEntryLink fragmentEntryLink,
			FragmentInstance fragmentInstance,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			PageElementDefinition pageElementDefinition)
		throws Exception {

		Layout layout = layoutStructureItemImporterContext.getLayout();

		if ((fragmentEntryLink.getPlid() != layout.getPlid()) ||
			(fragmentEntryLink.getSegmentsExperienceId() !=
				layoutStructureItemImporterContext.getSegmentsExperienceId())) {

			throw new UnsupportedOperationException();
		}

		FragmentEntryReference fragmentEntryReference =
			FragmentEntryReferenceUtil.getFragmentEntryReference(
				layoutStructureItemImporterContext.getCompanyId(),
				fragmentInstance.getFragmentReference(),
				layoutStructureItemImporterContext.getGroupId());

		fragmentEntryLink.setOriginalFragmentEntryLinkERC(
			_getOriginalFragmentEntryLinkERC(
				fragmentInstance, layoutStructureItemImporterContext));
		fragmentEntryLink.setFragmentEntryERC(
			fragmentEntryReference.getFragmentEntryERC());
		fragmentEntryLink.setFragmentEntryScopeERC(
			fragmentEntryReference.getFragmentEntryScopeERC());
		fragmentEntryLink.setCss(
			GetterUtil.getString(fragmentInstance.getCss()));
		fragmentEntryLink.setHtml(
			GetterUtil.getString(fragmentInstance.getHtml()));
		fragmentEntryLink.setJs(GetterUtil.getString(fragmentInstance.getJs()));
		fragmentEntryLink.setConfiguration(
			GetterUtil.getString(fragmentInstance.getConfiguration()));
		fragmentEntryLink.setEditableValues(
			_getEditableValues(
				fragmentInstance, layoutStructureItemImporterContext,
				pageElementDefinition));
		fragmentEntryLink.setNamespace(fragmentInstance.getNamespace());
		fragmentEntryLink.setRendererKey(
			fragmentEntryReference.getRendererKey());
		fragmentEntryLink.setType(_getType(pageElementDefinition.getType()));
		fragmentEntryLink.setLastPropagationDate(
			fragmentInstance.getDatePropagated());

		fragmentEntryLink =
			FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
				fragmentEntryLink);

		FragmentEntryProcessorRegistry fragmentEntryProcessorRegistry =
			layoutStructureItemImporterContext.
				getFragmentEntryProcessorRegistry();

		return FragmentEntryLinkLocalServiceUtil.updateFragmentEntryLink(
			layoutStructureItemImporterContext.getUserId(),
			fragmentEntryLink.getFragmentEntryLinkId(),
			fragmentEntryProcessorRegistry.mergeDefaultEditableValues(
				fragmentEntryLink.getConfigurationJSONObject(),
				fragmentEntryLink.getEditableValuesJSONObject(),
				FragmentEntryLinkUtil.getProcessedHTML(
					fragmentEntryLink, fragmentEntryProcessorRegistry,
					layoutStructureItemImporterContext.getUser())),
			false);
	}

	private static final ServiceTracker<PortletRegistry, PortletRegistry>
		_portletRegistryServiceTracker = ServiceTrackerFactory.open(
			FrameworkUtil.getBundle(FragmentLayoutStructureItemImporter.class),
			PortletRegistry.class);

}