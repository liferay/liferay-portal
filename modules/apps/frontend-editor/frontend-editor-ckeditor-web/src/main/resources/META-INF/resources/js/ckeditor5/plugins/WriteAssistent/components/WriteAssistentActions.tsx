/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

interface ActionItem {
	disabled?: boolean;
	name: string;
	symbolLeft?: string;
	symbolRight?: string;
	type:
		| 'Improve Writing'
		| 'Fix Spelling Grammar'
		| 'Translate To'
		| 'Make Shorter'
		| 'Make Longer'
		| 'Generate Based On Title';
}

export default function WriteAssistentActions({
	containerRef,
	content,
}: {
	content: string;
	containerRef: HTMLElement;
}) {
	const [active, setActive] = useState(true);

	const actionsGroup = [
		{
			children: [
				{
					disabled: false,
					name: Liferay.Language.get('improve-writing'),
					symbolLeft: 'magic',
					type: 'Improve Writing',
				},
				{
					disabled: true,
					name: Liferay.Language.get('fix-spelling-grammar'),
					symbolLeft: 'check',
					type: 'Fix Spelling Grammar',
				},
				{
					disabled: true,
					name: Liferay.Language.get('translate-to'),
					symbolLeft: 'automatic-translate',
					symbolRight: 'angle-right-small',
					type: 'Translate To',
				},
			],
			name: Liferay.Language.get('suggested'),
		},
		{type: 'divider'},
		{
			children: [
				{
					disabled: true,
					name: Liferay.Language.get('make-shorter'),
					symbolLeft: 'bars',
					type: 'Make Shorter',
				},
				{
					disabled: true,
					name: Liferay.Language.get('make-longer'),
					symbolLeft: 'align-justify',
					type: 'Make Shorter',
				},
				{
					disabled: true,
					name: Liferay.Language.get('change-tone'),
					symbolRight: 'angle-right-small',
					type: 'Make Longer',
				},
			],
			name: Liferay.Language.get('edit'),
		},
		{type: 'divider'},
		{
			children: [
				{
					disabled: true,
					name: 'Title',
					type: 'Generate Based On Title',
				},
			],
			name: Liferay.Language.get('generate-based-on'),
		},
	];

	const alignRef = useRef<HTMLElement | null>(null);
	const menuElementRef = useRef<HTMLDivElement | null>(null);

	const handleChange = async (type: ActionItem['type']) => {
		await fetch(`/o/ai-hub/v1.0/tasks`, {
			body: JSON.stringify({
				context: {
					text: content,
				},
				type,
			}),
			headers: new Headers({
				'Accept': 'application/json',
				'Content-Type': 'application/json',
			}),
			method: 'POST',
		});
	};

	useEffect(() => {
		alignRef.current = containerRef ?? null;
	}, [containerRef]);

	return (
		<ClayDropDown.Menu
			active={active}
			alignElementRef={alignRef}
			onActiveChange={() => {
				setActive(!active);
			}}
			ref={menuElementRef}
		>
			<ClayDropDown.ItemList items={actionsGroup}>
				{(group: any) => (
					<ClayDropDown.Group<ActionItem>
						header={group.name}
						items={group.children}
						key={group.name}
					>
						{(child: ActionItem) => (
							<ClayDropDown.Item
								disabled={child.disabled}
								key={child.name}
								onClick={() => handleChange(child.type)}
								spritemap={
									Liferay.ThemeDisplay.getPathThemeImages() +
									'/clay/icons.svg'
								}
								symbolLeft={child.symbolLeft}
								symbolRight={child.symbolRight}
							>
								<span className="ml-4">{child.name}</span>
							</ClayDropDown.Item>
						)}
					</ClayDropDown.Group>
				)}
			</ClayDropDown.ItemList>
		</ClayDropDown.Menu>
	);
}
