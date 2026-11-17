/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {
	hideProductMenuIfPresent,
	onProductMenuVisibleChange,
} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useEffect} from 'react';

import {ElementsSidebarPanel} from './ElementsSidebarPanel';
import PropertiesSidebarPanel from './PropertiesSidebarPanel';
import {PANEL_IDS} from './panelIds';

export default function Sidebar({
	selectedSidebarPanelId,
	setSelectedSidebarPanelId,
}) {
	useEffect(() => {
		if (selectedSidebarPanelId) {
			hideProductMenuIfPresent();
		}

		const handler = onProductMenuVisibleChange((visible) => {
			if (visible) {
				setSelectedSidebarPanelId(null);
			}
		});

		return () => {
			handler.removeListener();
		};
	}, [selectedSidebarPanelId, setSelectedSidebarPanelId]);

	return (
		<div className="ddm_template_editor__App-sidebar">
			<div
				className={classNames(
					'ddm_template_editor__App-sidebar-content',
					{
						open: selectedSidebarPanelId,
					}
				)}
			>
				<ElementsSidebarPanel
					className={classNames({
						'd-none': PANEL_IDS.elements !== selectedSidebarPanelId,
					})}
				/>

				<PropertiesSidebarPanel
					className={classNames({
						'd-none':
							PANEL_IDS.properties !== selectedSidebarPanelId,
					})}
				/>
			</div>

			<div className="ddm_template_editor__App-sidebar-buttons pt-3">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('elements')}
					aria-pressed={PANEL_IDS.elements === selectedSidebarPanelId}
					borderless
					className={classNames('mb-2', {
						active: PANEL_IDS.elements === selectedSidebarPanelId,
					})}
					data-tooltip-align="left"
					displayType="secondary"
					monospaced
					onClick={() => {
						setSelectedSidebarPanelId((selectedSidebarPanelId) =>
							selectedSidebarPanelId === PANEL_IDS.elements
								? null
								: PANEL_IDS.elements
						);
					}}
					outline
					symbol="list-ul"
					title={Liferay.Language.get('elements')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('properties')}
					aria-pressed={
						PANEL_IDS.properties === selectedSidebarPanelId
					}
					borderless
					className={classNames('mb-2', {
						active: PANEL_IDS.properties === selectedSidebarPanelId,
					})}
					data-tooltip-align="left"
					displayType="secondary"
					monospaced
					onClick={() => {
						setSelectedSidebarPanelId((selectedSidebarPanelId) =>
							selectedSidebarPanelId === PANEL_IDS.properties
								? null
								: PANEL_IDS.properties
						);
					}}
					outline
					symbol="cog"
					title={Liferay.Language.get('properties')}
				/>
			</div>
		</div>
	);
}

Sidebar.propTypes = {
	selectedSidebarPanelId: PropTypes.string,
	setSelectedSidebarPanelId: PropTypes.func.isRequired,
};
