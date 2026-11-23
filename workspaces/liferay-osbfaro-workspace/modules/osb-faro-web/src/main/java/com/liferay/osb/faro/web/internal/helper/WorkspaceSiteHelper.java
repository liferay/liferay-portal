/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.helper;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.web.internal.util.SiteInitializerUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.site.initializer.SiteInitializer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = WorkspaceSiteHelper.class)
public class WorkspaceSiteHelper {

	public void initialize(FaroProject faroProject) throws Exception {
		SiteInitializerUtil.initialize(
			_groupLocalService.getGroup(faroProject.getGroupId()),
			_siteInitializer);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(site.initializer.key=com.liferay.osb.faro.workspace.site.initializer)"
	)
	private SiteInitializer _siteInitializer;

}