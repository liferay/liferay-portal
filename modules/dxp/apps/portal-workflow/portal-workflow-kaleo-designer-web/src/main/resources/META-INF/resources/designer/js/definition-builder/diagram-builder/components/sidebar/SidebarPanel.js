/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useId, useState} from 'react';

export default function SidebarPanel({children, panelTitle}) {
	const [panelCollapsed, setPanelCollapsed] = useState(false);

	const panelBodyId = useId();

	return (
		<div className="panel-group panel-group-flush">
			<div className="panel">
				<ClayButton
					aria-controls={panelBodyId}
					aria-expanded={!panelCollapsed}
					className="sheet-subtitle"
					displayType="unstyled"
					onClick={() => setPanelCollapsed(!panelCollapsed)}
				>
					<span>{panelTitle}</span>

					<ClayIcon
						symbol={panelCollapsed ? 'angle-right' : 'angle-down'}
					/>
				</ClayButton>

				<div
					className={classNames('panel-collapse', {
						collapse: panelCollapsed,
					})}
					id={panelBodyId}
				>
					<div className="panel-body">{children}</div>
				</div>
			</div>
		</div>
	);
}

SidebarPanel.propTypes = {
	children: PropTypes.any,
	panelTitle: PropTypes.string.isRequired,
};
