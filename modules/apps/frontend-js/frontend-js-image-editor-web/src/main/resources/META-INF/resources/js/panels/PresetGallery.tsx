/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Carousel} from './Carousel';

interface Props<T extends string> {
	idPrefix: string;

	items: T[];
	label: (item: T) => string;
	legend: string;
	onSelect: (item: T) => void;

	preview: (item: T) => React.ReactNode;

	selected: T;
}

export function PresetGallery<T extends string>({
	idPrefix,
	items,
	label,
	legend,
	onSelect,
	preview,
	selected,
}: Props<T>) {
	return (
		<fieldset>
			<legend className="sr-only">{legend}</legend>

			<Carousel className="editor-preset-grid" itemCount={items.length}>
				{items.map((item) => {
					const name = label(item);

					return (
						<div
							className="custom-control custom-radio editor-preset-option"
							key={item}
						>
							<input
								checked={selected === item}
								className="editor-preset-input sr-only"
								id={`${idPrefix}-${item}`}
								name={`${idPrefix}-preset`}
								onChange={() => onSelect(item)}
								type="radio"
								value={item}
							/>

							<label
								className="editor-preset-label"
								htmlFor={`${idPrefix}-${item}`}
							>
								<span className="editor-preset-card">
									{preview(item)}
								</span>

								<span className="editor-preset-name">
									{name}
								</span>
							</label>
						</div>
					);
				})}
			</Carousel>
		</fieldset>
	);
}
