/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {EActionType, IActionGroup} from '../types';
import BalloonGroup from './BalloonGroup';

import '../../../../../css/ckeditor5/balloon.scss';

function TestBalloon({
	actionsGroup,
	handleActionClick,
}: {
	actionsGroup: IActionGroup[];
	handleActionClick: (type: EActionType) => Promise<void>;
}) {
	return (
		<div className="balloon-container">
			{actionsGroup.map((group: IActionGroup) => (
				<BalloonGroup
					handleItemClick={handleActionClick}
					header={group.name}
					key={group.name}
				>
					{group.children}
				</BalloonGroup>
			))}
		</div>
	);
}

export default TestBalloon;
