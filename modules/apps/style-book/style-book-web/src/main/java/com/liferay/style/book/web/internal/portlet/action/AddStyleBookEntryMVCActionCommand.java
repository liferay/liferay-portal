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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
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

			JSONObject jsonObject = JSONUtil.put(
				"redirectURL", _getRedirectURL(actionResponse, styleBookEntry));

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
		ActionResponse actionResponse, StyleBookEntry styleBookEntry) {

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse)
		).setMVCRenderCommandName(
			"/style_book/edit_style_book_entry"
		).setParameter(
			"styleBookEntryId", styleBookEntry.getStyleBookEntryId()
		).buildString();
	}

	@Reference
	private Portal _portal;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}