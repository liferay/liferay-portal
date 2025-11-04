/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import React, {useEffect, useRef, useState} from 'react';

export default function WriteAssistentConfirmatinoAction({
	containerRef,
	handleAccept,
	handleDiscard,
}: {
	containerRef: HTMLElement;
	handleAccept: () => void;
	handleDiscard: () => void;
}) {
	const [active, setActive] = useState(true);

	const alignRef = useRef<HTMLElement | null>(null);

	const actions = [
		{
			disabled: false,
			name: Liferay.Language.get('accept'),
			onClick: handleAccept,
			symbolLeft: 'check',
		},
		{
			disabled: false,
			name: Liferay.Language.get('discard'),
			onClick: handleDiscard,
			symbolLeft: 'times',
		},
		{
			disabled: true,
			name: Liferay.Language.get('regenerate'),
			onClick: () => {},
			symbolLeft: 'reset',
		},
	];

	useEffect(() => {
		alignRef.current = containerRef ?? null;

		return () => {
			setActive(false);
		};
	}, [containerRef]);

	return (
		<ClayDropDown.Menu
			active={active}
			alignElementRef={alignRef}
			onActiveChange={setActive}
		>
			<ClayDropDown.ItemList items={actions}>
				{(item: any) => (
					<ClayDropDown.Item
						disabled={item.disabled}
						key={item.name}
						onClick={() => {
							item.onClick();
							setActive(false);
						}}
						spritemap={
							Liferay.ThemeDisplay.getPathThemeImages() +
							'/clay/icons.svg'
						}
						symbolLeft={item.symbolLeft}
					>
						<span className="ml-4">{item.name}</span>
					</ClayDropDown.Item>
				)}
			</ClayDropDown.ItemList>
		</ClayDropDown.Menu>
	);
}
