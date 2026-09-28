/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React from 'react';

function Sidebar({children, className, onClose, title, visible}) {
	return (
		<div
			className={getCN(className, 'sidebar', 'sidebar-light', {
				open: visible,
			})}
		>
			<div className="sidebar-header">
				<div className="component-title">
					<span className="text-truncate-inline">
						<span className="text-truncate">{title}</span>
					</span>
				</div>

				<span>
					<ClayButton
						aria-label={Liferay.Language.get('close')}
						borderless
						displayType="secondary"
						monospaced
						onClick={onClose}
						size="sm"
					>
						<ClayIcon symbol="times" />
					</ClayButton>
				</span>
			</div>

			{children}
		</div>
	);
}

export default React.memo(Sidebar);
