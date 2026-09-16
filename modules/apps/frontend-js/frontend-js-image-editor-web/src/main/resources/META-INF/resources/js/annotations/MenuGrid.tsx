/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useRef, useState} from 'react';

export interface GridChoice {
	art: React.ReactNode;

	id: string;
	label: string;
}

interface Props {
	choices: GridChoice[];

	columns: number;

	label: string;
	onChoose: (id: string) => void;
}

export function MenuGrid({choices, columns, label, onChoose}: Props) {
	const [active, setActive] = useState(0);

	const gridRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		gridRef.current
			?.querySelector<HTMLButtonElement>('button')
			?.focus({preventScroll: true});
	}, []);

	const rows: GridChoice[][] = [];

	for (let index = 0; index < choices.length; index += columns) {
		rows.push(choices.slice(index, index + columns));
	}

	const move = (index: number) => {
		const next = Math.min(Math.max(index, 0), choices.length - 1);

		setActive(next);

		gridRef.current
			?.querySelectorAll<HTMLButtonElement>('button')
			[next]?.focus();
	};

	const handleKeyDown = (event: React.KeyboardEvent) => {
		const index = Number(
			(event.target as Element)
				.closest('[data-cell]')
				?.getAttribute('data-cell')
		);

		if (Number.isNaN(index)) {
			return;
		}

		switch (event.key) {
			case 'ArrowDown':
				move(index + columns);
				break;

			case 'ArrowLeft':
				move(index - 1);
				break;

			case 'ArrowRight':
				move(index + 1);
				break;

			case 'ArrowUp':
				if (index < columns) {
					return;
				}

				move(index - columns);
				break;

			case 'End':
				move(choices.length - 1);
				break;

			case 'Home':
				move(0);
				break;

			default:
				return;
		}

		event.preventDefault();
		event.stopPropagation();
	};

	return (
		<div
			aria-label={label}
			className="editor-menu-grid"
			onKeyDown={handleKeyDown}
			ref={gridRef}
			role="grid"
		>
			{rows.map((row, rowIndex) => (
				<div key={rowIndex} role="row">
					{row.map((choice, columnIndex) => {
						const index = rowIndex * columns + columnIndex;

						return (
							<span key={choice.id} role="gridcell">
								<button
									aria-label={choice.label}
									className="btn btn-monospaced editor-menu-cell"
									data-cell={index}
									onClick={() => onChoose(choice.id)}
									tabIndex={active === index ? 0 : -1}
									title={choice.label}
									type="button"
								>
									{choice.art}
								</button>
							</span>
						);
					})}
				</div>
			))}
		</div>
	);
}
