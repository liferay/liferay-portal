/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorCriterion;
import com.liferay.fragment.item.selector.FragmentEntryItemSelectorReturnType;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/select_default_input_fragment_entry"
	},
	service = MVCRenderCommand.class
)
public class SelectDefaultInputFragmentEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		FragmentEntryItemSelectorCriterion fragmentEntryItemSelectorCriterion =
			new FragmentEntryItemSelectorCriterion();

		fragmentEntryItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FragmentEntryItemSelectorReturnType());

		fragmentEntryItemSelectorCriterion.setInputTypes(
			new HashSet<>(
				Collections.singletonList(
					ParamUtil.getString(renderRequest, "inputType"))));

		fragmentEntryItemSelectorCriterion.setType(
			FragmentConstants.TYPE_INPUT);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(renderRequest);

		try {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						requestBackedPortletURLFactory,
						renderResponse.getNamespace() + "selectFragment",
						fragmentEntryItemSelectorCriterion)));
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return "/error.jsp";
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectDefaultInputFragmentEntryMVCRenderCommand.class);

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}