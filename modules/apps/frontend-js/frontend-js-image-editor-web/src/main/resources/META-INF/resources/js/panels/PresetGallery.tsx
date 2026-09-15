/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Carousel} from './Carousel';

export const PRESET_THUMB_BOX = {height: 48, width: 72, x: 0, y: 0};

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

interface ThumbProps {
	children?: React.ReactNode;
	filter?: string;
	thumbUrl: string;
}

export function PresetThumb({children, filter, thumbUrl}: ThumbProps) {
	return (
		<svg
			aria-hidden="true"
			className="editor-preset-thumb"
			height={PRESET_THUMB_BOX.height}
			viewBox={`0 0 ${PRESET_THUMB_BOX.width} ${PRESET_THUMB_BOX.height}`}
			width={PRESET_THUMB_BOX.width}
		>
			<image
				filter={filter}
				height={PRESET_THUMB_BOX.height}
				href={thumbUrl}
				preserveAspectRatio="xMidYMid slice"
				width={PRESET_THUMB_BOX.width}
			/>

			{children}
		</svg>
	);
}
