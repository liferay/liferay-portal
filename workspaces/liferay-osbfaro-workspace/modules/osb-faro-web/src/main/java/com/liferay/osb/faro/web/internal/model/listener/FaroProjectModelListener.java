/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.listener;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.helper.WorkspaceSiteHelper;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ModelListener.class)
public class FaroProjectModelListener extends BaseModelListener<FaroProject> {

	@Override
	public void onAfterCreate(FaroProject faroProject) {
		if (!FeatureFlagManagerUtil.isEnabled(
				faroProject.getCompanyId(), "LPD-114293")) {

			return;
		}

		try {
			_workspaceSiteHelper.initialize(faroProject);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public void onAfterUpdate(
		FaroProject originalFaroProject, FaroProject faroProject) {

		if (!FeatureFlagManagerUtil.isEnabled(
				faroProject.getCompanyId(), "LPD-114293")) {

			return;
		}

		try {
			boolean ldpEnabled = FaroNavigationUtil.isLDPEnabled(faroProject);

			if (FaroNavigationUtil.isLDPEnabled(originalFaroProject) !=
					ldpEnabled) {

				_workspaceSiteHelper.updateLayoutPermissions(
					faroProject, ldpEnabled);

				if (ldpEnabled) {
					_workspaceSiteHelper.updateMasterLayouts(
						"LDP_FAVICON", faroProject.getGroupId());
				}
				else {
					_workspaceSiteHelper.updateMasterLayouts(
						"AC_FAVICON", faroProject.getGroupId());
				}
			}
			else if (originalFaroProject.isRecommendationsEnabled() !=
						faroProject.isRecommendationsEnabled()) {

				_workspaceSiteHelper.updateLayoutPermissions(
					faroProject, ldpEnabled);
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroProjectModelListener.class);

	@Reference
	private WorkspaceSiteHelper _workspaceSiteHelper;

}