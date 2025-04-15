/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/reset_merge_fail_count"
	},
	service = MVCActionCommand.class
)
public class ResetMergeFailCountMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutPrototypeId");

		_layoutSetPrototypeHelper.setMergeFailCount(
			_layoutPrototypeLocalService.getLayoutPrototype(layoutPrototypeId),
			0);
	}

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

}