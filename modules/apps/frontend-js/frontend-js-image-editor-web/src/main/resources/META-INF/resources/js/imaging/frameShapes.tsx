/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {CropRect, Frame, FrameKind} from '../state/types';

export const FRAME_KINDS: FrameKind[] = [
	'none',
	'mat',
	'bevel',
	'line',
	'double',
	'dashed',
	'ticks',
	'corners',
	'inset',
	'polaroid',
];

function metrics(crop: CropRect, frame: Frame) {
	const unit = Math.min(crop.width, crop.height) / 100;

	const band = Math.max(frame.size * unit, 0.5);

	return {
		band,
		gap: frame.offset * unit,
		line: Math.max((frame.size * unit) / 4, 0.5),
	};
}

function insetRect(crop: CropRect, inset: number) {
	return {
		height: Math.max(crop.height - inset * 2, 0),
		width: Math.max(crop.width - inset * 2, 0),
		x: crop.x + inset,
		y: crop.y + inset,
	};
}

interface Props {
	crop: CropRect;
	frame: Frame;
}

export function FrameShape({crop, frame}: Props) {
	if (frame.kind === 'none') {
		return null;
	}

	return (
		<g className="editor-frame" pointerEvents="none">
			{frameNode(crop, frame)}
		</g>
	);
}

function frameNode(crop: CropRect, frame: Frame) {
	const {band, gap, line} = metrics(crop, frame);

	const common = {
		fill: 'none',
		stroke: frame.color,
	};

	switch (frame.kind) {
		case 'mat':
			return (
				<rect
					{...common}
					{...insetRect(crop, gap + band / 2)}
					strokeWidth={band}
				/>
			);

		case 'bevel':
			return (
				<rect
					{...common}
					{...insetRect(crop, gap + band / 2)}
					rx={band}
					strokeWidth={band}
				/>
			);

		case 'line':
			return (
				<rect
					{...common}
					{...insetRect(crop, gap + line / 2)}
					strokeWidth={line}
				/>
			);

		case 'double':
			return (
				<>
					<rect
						{...common}
						{...insetRect(crop, gap + line / 2)}
						strokeWidth={line}
					/>

					<rect
						{...common}
						{...insetRect(crop, gap + line * 3.5)}
						strokeWidth={line}
					/>
				</>
			);

		case 'dashed':
			return (
				<rect
					{...common}
					{...insetRect(crop, gap + line / 2)}
					strokeDasharray={`${line * 4} ${line * 3}`}
					strokeWidth={line}
				/>
			);

		case 'ticks': {
			const rect = insetRect(crop, gap + line / 2);

			const arm = Math.min(crop.width, crop.height) * 0.06;

			const midX = rect.x + rect.width / 2;
			const midY = rect.y + rect.height / 2;

			// A line with a cross tick at the middle of each side: the
			// registration marks of a print frame.

			return (
				<>
					<rect {...common} {...rect} strokeWidth={line} />

					<path
						{...common}
						d={[
							`M ${midX} ${rect.y - arm} V ${rect.y + arm}`,
							`M ${midX} ${rect.y + rect.height - arm} V ${
								rect.y + rect.height + arm
							}`,
							`M ${rect.x - arm} ${midY} H ${rect.x + arm}`,
							`M ${rect.x + rect.width - arm} ${midY} H ${
								rect.x + rect.width + arm
							}`,
						].join(' ')}
						strokeWidth={line}
					/>
				</>
			);
		}

		case 'corners': {
			const rect = insetRect(crop, gap + line / 2);

			const arm = Math.min(crop.width, crop.height) * 0.12;

			const right = rect.x + rect.width;
			const bottom = rect.y + rect.height;

			return (
				<path
					{...common}
					d={[
						`M ${rect.x} ${rect.y + arm} V ${rect.y} H ${
							rect.x + arm
						}`,
						`M ${right - arm} ${rect.y} H ${right} V ${
							rect.y + arm
						}`,
						`M ${right} ${bottom - arm} V ${bottom} H ${
							right - arm
						}`,
						`M ${rect.x + arm} ${bottom} H ${rect.x} V ${
							bottom - arm
						}`,
					].join(' ')}
					strokeWidth={line}
				/>
			);
		}

		case 'inset':
			return (
				<>
					<rect
						{...common}
						{...insetRect(crop, gap + band / 2)}
						strokeWidth={band}
					/>

					<rect
						{...common}
						{...insetRect(crop, gap + band + line)}
						strokeWidth={line}
					/>
				</>
			);

		case 'polaroid': {

			// A mat with a deep bottom margin, filled rather than stroked:
			// the even-odd rule punches the picture out of the card.

			const outer = insetRect(crop, gap);

			const inner = {
				height: Math.max(outer.height - band * 4, 0),
				width: Math.max(outer.width - band * 2, 0),
				x: outer.x + band,
				y: outer.y + band,
			};

			return (
				<path
					d={[
						`M ${outer.x} ${outer.y} h ${outer.width} v ${outer.height} h ${-outer.width} Z`,
						`M ${inner.x} ${inner.y} h ${inner.width} v ${inner.height} h ${-inner.width} Z`,
					].join(' ')}
					fill={frame.color}
					fillRule="evenodd"
				/>
			);
		}

		default:
			return null;
	}
}
