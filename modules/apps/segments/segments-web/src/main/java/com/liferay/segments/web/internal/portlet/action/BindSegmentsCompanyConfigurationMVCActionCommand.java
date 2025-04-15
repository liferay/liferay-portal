/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/instance_settings/bind_segments_company_configuration"
	},
	service = MVCActionCommand.class
)
public class BindSegmentsCompanyConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_segmentsConfigurationProvider.updateSegmentsCompanyConfiguration(
			_portal.getCompanyId(actionRequest),
			_getSegmentsCompanyConfiguration(actionRequest));
	}

	private SegmentsCompanyConfiguration _getSegmentsCompanyConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		boolean segmentationEnabled = _isSegmentationEnabled(actionRequest);

		boolean roleSegmentationEnabled = _isRoleSegmentationEnabled(
			actionRequest);

		return new SegmentsCompanyConfiguration() {

			@Override
			public boolean roleSegmentationEnabled() {
				return roleSegmentationEnabled;
			}

			@Override
			public boolean segmentationEnabled() {
				return segmentationEnabled;
			}

		};
	}

	private boolean _isRoleSegmentationEnabled(ActionRequest actionRequest)
		throws Exception {

		String roleSegmentationEnabledString = ParamUtil.getString(
			actionRequest, "roleSegmentationEnabled");

		if (Validator.isNotNull(roleSegmentationEnabledString) &&
			Objects.equals(roleSegmentationEnabledString, "on")) {

			return true;
		}
		else if (Validator.isNull(roleSegmentationEnabledString)) {
			return _segmentsConfigurationProvider.isRoleSegmentationEnabled(
				_portal.getCompanyId(actionRequest));
		}

		return false;
	}

	private boolean _isSegmentationEnabled(ActionRequest actionRequest)
		throws Exception {

		String segmentationEnabledString = ParamUtil.getString(
			actionRequest, "segmentationEnabled");

		if (Validator.isNotNull(segmentationEnabledString) &&
			Objects.equals(segmentationEnabledString, "on")) {

			return true;
		}
		else if (Validator.isNull(segmentationEnabledString)) {
			return _segmentsConfigurationProvider.isSegmentationEnabled(
				_portal.getCompanyId(actionRequest));
		}

		return false;
	}

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

}