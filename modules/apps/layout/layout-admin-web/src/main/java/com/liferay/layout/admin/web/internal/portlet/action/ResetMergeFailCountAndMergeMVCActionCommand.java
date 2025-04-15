/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.sites.kernel.util.Sites;

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
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/reset_merge_fail_count_and_merge"
	},
	service = MVCActionCommand.class
)
public class ResetMergeFailCountAndMergeMVCActionCommand
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

		long selPlid = ParamUtil.getLong(actionRequest, "selPlid");

		Layout selLayout = _layoutLocalService.getLayout(selPlid);

		_layoutSetPrototypeHelper.resetPrototype(selLayout);

		_sites.mergeLayoutPrototypeLayout(selLayout.getGroup(), selLayout);

		LayoutPrototype layoutPrototype =
			_layoutPrototypeService.getLayoutPrototype(layoutPrototypeId);

		if (layoutPrototype.getMergeFailCount() > 0) {
			SessionErrors.add(actionRequest, "resetMergeFailCountAndMerge");
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private LayoutPrototypeService _layoutPrototypeService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private Sites _sites;

}