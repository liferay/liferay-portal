/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.web.internal.handler.StyleBookEntryExceptionRequestHandlerUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @author Thiago Buarque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/add_style_book_entry"
	},
	service = MVCActionCommand.class
)
public class AddStyleBookEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			StyleBookEntry styleBookEntry = _addStyleBookEntry(actionRequest);

			String backURLTitle = ParamUtil.getString(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(actionRequest)),
				"p_l_back_url_title");
			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL",
				_getRedirectURL(
					actionRequest, backURLTitle, redirect, styleBookEntry));

			if (SessionErrors.contains(
					actionRequest, "styleBookEntryNameInvalid")) {

				addSuccessMessage(actionRequest, actionResponse);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			SessionErrors.add(actionRequest, "styleBookEntryNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			StyleBookEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	private StyleBookEntry _addStyleBookEntry(ActionRequest actionRequest)
		throws PortalException {

		String name = ParamUtil.getString(actionRequest, "name");
		String themeId = ParamUtil.getString(actionRequest, "themeId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		return _styleBookEntryService.addStyleBookEntry(
			null, serviceContext.getScopeGroupId(), name, StringPool.BLANK,
			themeId, serviceContext);
	}

	private String _getRedirectURL(
			ActionRequest actionRequest, String backURLTitle, String redirect,
			StyleBookEntry styleBookEntry)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				_portal.getHttpServletRequest(actionRequest),
				_groupLocalService.getGroup(styleBookEntry.getGroupId()),
				StyleBookPortletKeys.STYLE_BOOK, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/style_book/edit_style_book_entry"
		).setRedirect(
			redirect
		).setParameter(
			"p_l_back_url_title", backURLTitle
		).setParameter(
			"styleBookEntryId", styleBookEntry.getStyleBookEntryId()
		).buildString();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}