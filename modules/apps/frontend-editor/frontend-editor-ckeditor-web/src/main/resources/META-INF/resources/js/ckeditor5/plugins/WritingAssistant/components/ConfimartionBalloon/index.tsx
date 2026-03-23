/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon, {ClayIconSpriteContext} from '@clayui/icon';
import React from 'react';

import '../../../../../../css/ckeditor5/balloon.scss';

function ConfirmationBalloon({actions}: {actions: any[]}) {
	const handleClick = (action: any) => {
		if (action.disabled) {
			return;
		}
		action.onClick();
	};

	const handleMouseDown = (event: React.MouseEvent<HTMLButtonElement>) => {
		event.stopPropagation();
	};

	return (
		<div className="confirmation-balloon-container">
			<ClayIconSpriteContext.Provider value={Liferay.Icons.spritemap}>
				<ul>
					{actions.map((action, index) => (
						<li
							className={
								action.disabled
									? 'confirmation-balloon-item confirmation-balloon-item-disabled'
									: 'confirmation-balloon-item'
							}
							key={index}
						>
							<ClayIcon
								height={17}
								style={{fill: '#6b6c7e'}}
								symbol={action.symbolLeft}
								width={17}
							/>

							<button
								className="confirmation-ballon-button"
								disabled={action.disabled}
								onClick={() => handleClick(action)}
								onMouseDown={handleMouseDown}
							>
								{action.name}
							</button>
						</li>
					))}
				</ul>
			</ClayIconSpriteContext.Provider>
		</div>
	);
}

export default ConfirmationBalloon;
