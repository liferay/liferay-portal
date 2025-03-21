/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
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
 * Resets the number of failed merge attempts for the page template, which is
 * accessed from the action request's <code>layoutPrototypeId</code> param. Once
 * the counter is reset, the modified page template is merged back into its
 * linked page, which is accessed from the action request's <code>selPlid</code>
 * param.
 *
 * <p>
 * If the number of failed merge attempts is not equal to zero after the merge,
 * an error key is submitted into the {@link SessionErrors}.
 * </p>
 *
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/reset_layout_prototype_merge_fail_count_and_merge"
	},
	service = MVCActionCommand.class
)
public class ResetLayoutPrototypeMergeFailCountAndMergeMVCActionCommand
	extends BaseMVCActionCommand {

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