/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.service.SegmentsEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
		"mvc.command.name=/segments/delete_segments_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteSegmentsEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] deleteSegmentsEntryIds = null;

		long segmentsEntryId = ParamUtil.getLong(
			actionRequest, "segmentsEntryId");

		if (segmentsEntryId > 0) {
			deleteSegmentsEntryIds = new long[] {segmentsEntryId};
		}
		else {
			deleteSegmentsEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");
		}

		for (long deleteSegmentsEntryId : deleteSegmentsEntryIds) {
			_segmentsEntryService.deleteSegmentsEntry(deleteSegmentsEntryId);
		}
	}

	@Reference
	private SegmentsEntryService _segmentsEntryService;

}