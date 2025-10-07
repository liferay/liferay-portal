/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.RelatedInfoItem;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.RelatedInfoItemProvider;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_info_item_one_to_many_relationships"
	},
	service = MVCResourceCommand.class
)
public class GetInfoItemOneToManyRelationshipsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long classNameId = ParamUtil.getLong(resourceRequest, "classNameId");

		ClassName className = _classNameLocalService.fetchClassName(
			classNameId);

		if ((className == null) ||
			!FeatureFlagManagerUtil.isEnabled("LPD-60546")) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		RelatedInfoItemProvider<?> relatedInfoItemProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				RelatedInfoItemProvider.class, className.getValue());

		if (relatedInfoItemProvider == null) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONArray());

			return;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		for (RelatedInfoItem relatedInfoItem :
				relatedInfoItemProvider.getRelatedInfoItems()) {

			InfoItemDetailsProvider<?> infoItemDetailsProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemDetailsProvider.class,
					relatedInfoItem.getClassName());

			InfoItemClassDetails infoItemClassDetails =
				infoItemDetailsProvider.getInfoItemClassDetails();

			jsonArray.put(
				JSONUtil.put(
					"classNameId",
					_classNameLocalService.getClassNameId(
						infoItemClassDetails.getClassName())
				).put(
					"label",
					infoItemClassDetails.getLabel(themeDisplay.getLocale())
				).put(
					"name", relatedInfoItem.getName()
				));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonArray);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

}