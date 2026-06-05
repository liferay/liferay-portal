/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.view.table;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.table.BaseTableFDSView;
import com.liferay.frontend.data.set.view.table.FDSTableSchema;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilder;
import com.liferay.frontend.data.set.view.table.FDSTableSchemaBuilderFactory;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Davyson Melo
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSView.class
)
public class IssueReportTableFDSView extends BaseTableFDSView {

	@Override
	public FDSTableSchema getFDSTableSchema(Locale locale) {
		FDSTableSchemaBuilder fdsTableSchemaBuilder =
			_fdsTableSchemaBuilderFactory.create();

		return fdsTableSchemaBuilder.add(
			"dateCreated", "date",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"date")
		).add(
			"aiHubAgentDefinitionsToAIHubReports", "agent-name",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"agentNamesTableCellRenderer")
		).add(
			"surface.name", "surface"
		).add(
			"feedback.name", "feedback-type"
		).add(
			"reason.name", "issue-type"
		).add(
			"level", "level",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"levelTableCellRenderer")
		).add(
			"userMessage", "user-message"
		).add(
			"creator", "user",
			fdsTableSchemaField -> fdsTableSchemaField.setContentRenderer(
				"userTableCellRenderer")
		).build();
	}

	@Reference
	private FDSTableSchemaBuilderFactory _fdsTableSchemaBuilderFactory;

}