/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemActionDetailsProvider;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_info_item_action_error_message"
	},
	service = MVCResourceCommand.class
)
public class GetInfoItemActionErrorMessageMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		try {
			InfoItemActionDetailsProvider<Object>
				infoItemActionDetailsProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemActionDetailsProvider.class,
						_portal.fetchClassName(
							ParamUtil.getLong(resourceRequest, "classNameId")));

			if (infoItemActionDetailsProvider == null) {
				_writeErrorJSON(resourceRequest, resourceResponse);

				return;
			}

			Map<String, String> messageMap = new HashMap<>();

			Map<Locale, String> actionErrorMessageMap =
				infoItemActionDetailsProvider.getInfoItemActionErrorMessageMap(
					ParamUtil.getString(resourceRequest, "fieldId"));

			for (Map.Entry<Locale, String> entry :
					actionErrorMessageMap.entrySet()) {

				messageMap.put(
					_language.getLanguageId(entry.getKey()),
					_language.get(entry.getKey(), entry.getValue()));
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put("message", messageMap));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			_writeErrorJSON(resourceRequest, resourceResponse);
		}
	}

	private void _writeErrorJSON(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		Company company = _portal.getCompany(resourceRequest);

		Map<String, String> errorMap = new HashMap<>();

		for (Locale locale :
				_language.getCompanyAvailableLocales(company.getCompanyId())) {

			errorMap.put(
				_language.getLanguageId(locale),
				_language.get(locale, "your-request-failed-to-complete"));
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, JSONUtil.put("error", errorMap));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetInfoItemActionErrorMessageMVCResourceCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}