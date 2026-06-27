/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ConfigurationTabPage} from '../pages/portal-workflow-kaleo-designer-web/ConfigurationTabPage';

const GLOBAL_PROCESS_BUILDER_URL =
	'/group/control_panel/manage?p_p_id=com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet';

/**
 * Opens the company-wide Process Builder configuration tab. The
 * `/group/control_panel` Process Builder lists every object-definition asset
 * type (Basic Web Content, Basic Document, custom CMS structures); the
 * site-scoped `/group/guest` one used by `ConfigurationTabPage.goTo` does not.
 * The configuration tab link is then clicked (rather than loading the
 * `_tab=configuration` URL directly) so the workflow-definition selects are
 * initialized before they are changed. A full page load is used so the
 * navigation escapes the page editor's unsaved draft state, which lets the
 * matching `unassignWorkflowFromStructure` in a `finally` always reach the
 * screen. The global applications menu is avoided because it hangs when the
 * current page lacks its chrome (e.g. the page editor).
 */
async function goToGlobalConfiguration(
	configurationTabPage: ConfigurationTabPage
) {
	await configurationTabPage.page.goto(GLOBAL_PROCESS_BUILDER_URL);

	await configurationTabPage.configurationTabLink.waitFor({state: 'visible'});

	await configurationTabPage.configurationTabLink.click({force: true});

	await configurationTabPage.page.waitForURL((url) =>
		url.href.includes('=configuration')
	);

	await configurationTabPage.page.waitForLoadState('networkidle');
}

export async function assignWorkflowToStructure(
	configurationTabPage: ConfigurationTabPage,
	workflowName: string,
	assetType: string
) {
	await goToGlobalConfiguration(configurationTabPage);

	await configurationTabPage.assignWorkflowToAssetType(
		workflowName,
		assetType
	);
}

export async function unassignWorkflowFromStructure(
	configurationTabPage: ConfigurationTabPage,
	assetType: string
) {
	await goToGlobalConfiguration(configurationTabPage);

	await configurationTabPage.unassignWorkflowFromAssetType(assetType);
}
