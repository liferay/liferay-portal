/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.view.list;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;

import org.osgi.service.component.annotations.Component;

/**
 * @author João Victor Alves
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.TASK_DEFINITIONS,
	service = FDSView.class
)
public class TaskDefinitionListFDSView extends BaseListFDSView {

	@Override
	public String getDescription() {
		return "description";
	}

	@Override
	public String getTags() {
		return "tagsList";
	}

	@Override
	public String getTitle() {
		return "title";
	}

}