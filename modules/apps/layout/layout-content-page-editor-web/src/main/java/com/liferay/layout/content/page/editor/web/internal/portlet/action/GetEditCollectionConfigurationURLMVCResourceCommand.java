/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.collection.provider.ConfigurableInfoCollectionProvider;
import com.liferay.info.collection.provider.InfoCollectionProvider;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.InfoFormUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_edit_collection_configuration_url"
	},
	service = MVCResourceCommand.class
)
public class GetEditCollectionConfigurationURLMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String collectionKey = ParamUtil.getString(
			resourceRequest, "collectionKey");

		if (Validator.isBlank(collectionKey)) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());

			return;
		}

		InfoCollectionProvider<?> infoCollectionProvider =
			_infoItemServiceRegistry.getInfoItemService(
				InfoCollectionProvider.class, collectionKey);

		if (infoCollectionProvider == null) {
			infoCollectionProvider =
				_infoItemServiceRegistry.getInfoItemService(
					RelatedInfoItemCollectionProvider.class, collectionKey);
		}

		if (!(infoCollectionProvider instanceof
				ConfigurableInfoCollectionProvider)) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurableInfoCollectionProvider<?>
			configurableInfoCollectionProvider =
				(ConfigurableInfoCollectionProvider<?>)infoCollectionProvider;

		JSONObject jsonObject = InfoFormUtil.getConfigurationJSONObject(
			configurableInfoCollectionProvider.getConfigurationInfoForm(),
			themeDisplay.getLocale());

		if (JSONUtil.isEmpty(jsonObject.getJSONArray("fieldSets"))) {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_jsonFactory.createJSONObject());

			return;
		}

		String redirect = HttpComponentsUtil.removeParameter(
			ParamUtil.getString(resourceRequest, "redirect"), "itemId");

		String itemId = ParamUtil.getString(resourceRequest, "itemId");

		redirect = HttpComponentsUtil.addParameter(redirect, "itemId", itemId);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"url",
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						resourceRequest,
						ContentPageEditorPortletKeys.
							CONTENT_PAGE_EDITOR_PORTLET,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/layout_content_page_editor/edit_collection_configuration"
				).setRedirect(
					redirect
				).setBackURL(
					redirect
				).setParameter(
					"backURLTitle",
					() -> {
						Layout previousLayout = themeDisplay.getLayout();

						return previousLayout.getName(themeDisplay.getLocale());
					}
				).setParameter(
					"collectionKey", collectionKey
				).setParameter(
					"itemId", itemId
				).setParameter(
					"plid", themeDisplay.getPlid()
				).setParameter(
					"segmentsExperienceId",
					ParamUtil.getLong(resourceRequest, "segmentsExperienceId")
				).setParameter(
					"type", ParamUtil.getLong(resourceRequest, "type")
				).buildString()));
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}