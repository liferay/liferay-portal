/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {textWidth} from '../imaging/overlayShapes';
import {TextOverlay} from '../state/types';

function editorBackground(color: string): string {
	const value = color.replace('#', '');

	if (value.length === 6) {
		const [r, g, b] = [0, 2, 4].map((index) =>
			Number.parseInt(value.slice(index, index + 2), 16)
		);

		if ((0.299 * r + 0.587 * g + 0.114 * b) / 255 > 0.55) {
			return 'rgba(20, 21, 31, 0.92)';
		}
	}

	return 'rgba(255, 255, 255, 0.92)';
}

interface Props {
	bounds: {height: number; width: number; x: number; y: number};
	draft: string;
	onCancel: () => void;
	onChange: (draft: string) => void;
	onCommit: () => void;
	overlay: TextOverlay;
}

export function OverlayTextEditor({
	bounds,
	draft,
	onCancel,
	onChange,
	onCommit,
	overlay,
}: Props) {
	return (
		<foreignObject
			height={overlay.fontSize * 1.5}
			width={
				textWidth(draft || ' ', overlay.fontFamily, overlay.fontSize) +
				overlay.fontSize * 1.2
			}
			x={bounds.x - overlay.fontSize * 0.25}
			y={bounds.y - overlay.fontSize * 0.15}
		>
			<input
				aria-label={Liferay.Language.get('text')}
				autoFocus
				className="overlay-text-editor"
				onBlur={onCommit}
				onChange={(event) => onChange(event.target.value)}
				onKeyDown={(event) => {
					event.stopPropagation();

					if (event.key === 'Enter') {
						onCommit();
					}
					else if (event.key === 'Escape') {
						onCancel();
					}
				}}
				style={{
					background: editorBackground(overlay.color),
					color: overlay.color,
					fontFamily: overlay.fontFamily,
					fontSize: overlay.fontSize,
				}}
				value={draft}
			/>
		</foreignObject>
	);
}
