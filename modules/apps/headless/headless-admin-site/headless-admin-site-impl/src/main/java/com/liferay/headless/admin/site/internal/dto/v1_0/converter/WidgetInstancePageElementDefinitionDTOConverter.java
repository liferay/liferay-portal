/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.admin.site.dto.v1_0.PageElementDefinition;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.dto.v1_0.WidgetInstancePageElementDefinition;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentViewportUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ImageValueUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.WidgetInstanceUtil;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = DTOConverter.class)
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

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		if (fragmentEntryLink == null) {
			throw new UnsupportedOperationException();
		}

		WidgetInstancePageElementDefinition
			widgetInstancePageElementDefinition =
				new WidgetInstancePageElementDefinition();

		widgetInstancePageElementDefinition.setBackgroundImageValue(
			() -> ImageValueUtil.toBackgroundImageValue(
				companyId, dtoConverterContext, _infoItemServiceRegistry,
				fragmentStyledLayoutStructureItem.
					getBackgroundImageJSONObject(),
				scopeGroupId));
		widgetInstancePageElementDefinition.setCssClasses(
			() -> {
				Set<String> cssClasses =
					fragmentStyledLayoutStructureItem.getCssClasses();

				if (SetUtil.isEmpty(cssClasses)) {
					return null;
				}

				return ArrayUtil.toStringArray(cssClasses);
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
		widgetInstancePageElementDefinition.setType(
			() -> PageElementDefinition.Type.WIDGET);
		widgetInstancePageElementDefinition.setWidgetInstance(
			() -> _getWidgetInstance(fragmentEntryLink));
		widgetInstancePageElementDefinition.
			setWidgetInstanceExternalReferenceCode(
				fragmentEntryLink::getExternalReferenceCode);

		return widgetInstancePageElementDefinition;
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

		return WidgetInstanceUtil.getWidgetInstance(
			instanceId, fragmentEntryLink.getPlid(), portletId);
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}