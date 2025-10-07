/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {AppContextProvider} from './AppContext';
import {ClosableAlert} from './ClosableAlert';
import {Editor} from './Editor';
import Sidebar from './Sidebar';
import {PANEL_IDS} from './panelIds';

import './App.scss';

export default function App({
	editorAutocompleteData = {variables: {}},
	mode = 'text/plain',
	portletNamespace,
	propertiesViewURL,
	script: initialScript = '',
	showCacheableWarning = false,
	showTemplateWarning = false,
	showPropertiesPanel = false,
	templateVariableGroups = [],
}) {
	const [selectedSidebarPanelId, setSelectedSidebarPanelId] = useState(
		showPropertiesPanel ? PANEL_IDS.properties : PANEL_IDS.elements
	);

	return (
		<AppContextProvider
			portletNamespace={portletNamespace}
			propertiesViewURL={propertiesViewURL}
			templateVariableGroups={templateVariableGroups}
		>
			<div className="ddm_template_editor__App">
				<div
					className={classNames('ddm_template_editor__App-content', {
						'ddm_template_editor__App-content--sidebar-open':
							selectedSidebarPanelId,
					})}
				>
					<ClosableAlert
						message={Liferay.Language.get(
							'this-template-does-not-belong-to-this-site.-you-may-affect-other-sites-if-you-edit-this-template'
						)}
						visible={showTemplateWarning}
					/>

					<ClosableAlert
						id={`${portletNamespace}-cacheableWarningMessage`}
						message={Liferay.Language.get(
							'this-template-is-marked-as-cacheable.-avoid-using-code-that-uses-request-handling,-the-cms-query-api,-taglibs,-or-other-dynamic-features.-uncheck-the-cacheable-property-if-dynamic-behavior-is-needed'
						)}
						visible={showCacheableWarning}
					/>

					<Editor
						autocompleteData={editorAutocompleteData}
						initialScript={Liferay.Util.unescapeHTML(initialScript)}
						mode={mode}
					/>
				</div>

				<Sidebar
					selectedSidebarPanelId={selectedSidebarPanelId}
					setSelectedSidebarPanelId={setSelectedSidebarPanelId}
				/>
			</div>
		</AppContextProvider>
	);
}

App.propTypes = {
	editorAutocompleteData: PropTypes.object.isRequired,
	mode: PropTypes.oneOfType([
		PropTypes.string,
		PropTypes.shape({
			globalVars: PropTypes.bool.isRequired,
			name: PropTypes.string.isRequired,
		}),
	]),
	portletNamespace: PropTypes.string.isRequired,
	script: PropTypes.string.isRequired,
	showCacheableWarning: PropTypes.bool.isRequired,
	templateVariableGroups: PropTypes.any.isRequired,
};
