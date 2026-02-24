/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.tuning.web.application.list.constants.SearchTuningPanelCategoryKeys;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	property = {
		"panel.app.order:Integer=150",
		"panel.category.key=" + SearchTuningPanelCategoryKeys.CONTROL_PANEL_SEARCH_TUNING
	},
	service = PanelApp.class
)
public class ResultRankingsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "medal";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ResultRankingsPortletKeys.RESULT_RANKINGS;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference(
		target = "(jakarta.portlet.name=" + ResultRankingsPortletKeys.RESULT_RANKINGS + ")"
	)
	private Portlet _portlet;

}