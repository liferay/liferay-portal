/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/edit_fragment_entry"
	},
	service = MVCRenderCommand.class
)
public class EditFragmentEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		long fragmentEntryId = ParamUtil.getLong(
			renderRequest, "fragmentEntryId");

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(fragmentEntryId);

		if (fragmentEntry == null) {
			String fragmentEntryKey = ParamUtil.getString(
				renderRequest, "fragmentEntryKey");

			fragmentEntry =
				_fragmentCollectionContributorRegistry.getFragmentEntry(
					fragmentEntryKey);
		}

		if (fragmentEntry != null) {
			return "/edit_fragment_entry.jsp";
		}

		return "/error.jsp";
	}

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

}