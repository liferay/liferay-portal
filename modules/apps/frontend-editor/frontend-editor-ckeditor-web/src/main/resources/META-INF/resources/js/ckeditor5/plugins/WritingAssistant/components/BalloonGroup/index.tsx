/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon, {ClayIconSpriteContext} from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useState} from 'react';

import {EActionType, IAction} from '../../types';

function BalloonGroup({
	children,
	handleItemClick,
	header,
	key,
}: {
	children: IAction[];
	handleItemClick: (type: EActionType) => Promise<void>;
	header: string;
	key: string;
}) {
	const [isLoading, setIsLoading] = useState<{type: EActionType | ''}>({
		type: '',
	});

	const handleClick = async (child: IAction) => {
		if (child.disabled) {
			return;
		}
		setIsLoading({type: child.type});
		await handleItemClick(child.type);
	};

	return (
		<div className="balloon-group-container">
			<ClayIconSpriteContext.Provider value={Liferay.Icons.spritemap}>
				<ul className="list-unstyled">
					{header && (
						<li className="balloon-group-header">
							{header.toUpperCase()}
						</li>
					)}

					{children &&
						children.map((child) => {
							const loadingTest = isLoading.type === child.type;

							return (
								<li
									className={
										(child.disabled
											? 'balloon-group-item balloon-group-item-disabled'
											: 'balloon-group-item') +
										(loadingTest
											? ' balloon-group-item-loading'
											: '')
									}
									key={key}
									style={
										loadingTest
											? {
													opacity: 0.5,
													pointerEvents: 'none',
												}
											: {}
									}
								>
									{child.symbolLeft ? (
										<ClayIcon
											className="balloon-group-item-icon"
											height={17}
											style={{fill: '#6b6c7e'}}
											symbol={child.symbolLeft}
											width={17}
										/>
									) : (
										<span
											className="balloon-group-item-icon"
											style={{
												display: 'inline-block',
												width: 17,
											}}
										></span>
									)}

									<button
										className={
											child.disabled
												? 'balloon-group-item-button balloon-group-item-button-disabled'
												: 'balloon-group-item-button'
										}
										disabled={child.disabled || loadingTest}
										onClick={() => handleClick(child)}
									>
										{child.name}
									</button>

									<span className="balloon-group-item-icon-right">
										{loadingTest ? (
											<ClayLoadingIndicator className="mb-0 mt-0" />
										) : child.symbolRight ? (
											<ClayIcon
												className="balloon-group-item-icon"
												height={17}
												style={{fill: '#6b6c7e'}}
												symbol={child.symbolRight}
												width={17}
											/>
										) : null}
									</span>
								</li>
							);
						})}
				</ul>
			</ClayIconSpriteContext.Provider>
		</div>
	);
}

export default BalloonGroup;
