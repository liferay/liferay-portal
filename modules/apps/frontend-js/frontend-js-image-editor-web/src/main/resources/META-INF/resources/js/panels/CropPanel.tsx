/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId} from '../chrome/instance';
import {EditorAction, clampCrop} from '../state/editorReducer';
import {CropRect} from '../state/types';

interface Props {
	bounds: {height: number; width: number};

	crop: CropRect;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
}

type Field = 'height' | 'width' | 'x' | 'y';

const FIELD_LABELS: Record<Field, string> = {
	height: Liferay.Language.get('height'),
	width: Liferay.Language.get('width'),
	x: Liferay.Language.get('x-position'),
	y: Liferay.Language.get('y-position'),
};

export function CropPanel({bounds, crop, dispatch, onAnnounce}: Props) {
	const eid = useEditorId();

	const [drafts, setDrafts] = useState<Record<Field, string>>({
		height: String(crop.height),
		width: String(crop.width),
		x: String(crop.x),
		y: String(crop.y),
	});

	useEffect(() => {
		setDrafts({
			height: String(crop.height),
			width: String(crop.width),
			x: String(crop.x),
			y: String(crop.y),
		});
	}, [crop]);

	const commit = (field: Field) => {
		const value = Number.parseInt(drafts[field], 10);

		if (Number.isNaN(value)) {
			setDrafts((previous) => ({
				...previous,
				[field]: String(crop[field]),
			}));

			return;
		}

		const requested: CropRect = {...crop, [field]: value};

		const next = clampCrop(requested, bounds);

		setDrafts({
			height: String(next.height),
			width: String(next.width),
			x: String(next.x),
			y: String(next.y),
		});

		const unchanged =
			next.height === crop.height &&
			next.width === crop.width &&
			next.x === crop.x &&
			next.y === crop.y;

		dispatch({crop: next, type: 'set-crop'});

		if (unchanged) {
			if (next[field] !== value) {
				onAnnounce(
					sub(
						Liferay.Language.get(
							'x-stays-at-x-the-crop-has-to-fit-inside-the-image'
						),
						FIELD_LABELS[field],
						next[field]
					)
				);
			}

			return;
		}

		onAnnounce(
			sub(
				Liferay.Language.get(
					'crop-set-to-x-x-y-x-width-x-height-x-pixels'
				),
				next.x,
				next.y,
				next.width,
				next.height
			)
		);
	};

	const stepField = (field: Field, direction: -1 | 1, large: boolean) => {
		const parsed = Number.parseInt(drafts[field], 10);

		const requested = {
			...crop,
			[field]:
				(Number.isNaN(parsed) ? crop[field] : parsed) +
				direction * (large ? 10 : 1),
		};

		const next = clampCrop(requested, bounds);

		setDrafts({
			height: String(next.height),
			width: String(next.width),
			x: String(next.x),
			y: String(next.y),
		});

		dispatch({crop: next, transient: true, type: 'set-crop'});
	};

	const renderField = (field: Field) => (
		<ClayForm.Group key={field} small>
			<label htmlFor={eid(`crop-${field}`)}>{FIELD_LABELS[field]}</label>

			<ClayInput
				id={eid(`crop-${field}`)}
				min={0}
				onBlur={() => commit(field)}
				onChange={(event) =>
					setDrafts((previous) => ({
						...previous,
						[field]: event.target.value,
					}))
				}
				onKeyDown={(event: React.KeyboardEvent) => {
					if (event.key === 'Enter') {
						event.preventDefault();
						commit(field);
					}
					else if (
						event.key === 'ArrowUp' ||
						event.key === 'ArrowDown'
					) {
						event.preventDefault();

						stepField(
							field,
							event.key === 'ArrowUp' ? 1 : -1,
							event.shiftKey
						);
					}
				}}
				sizing="sm"
				type="number"
				value={drafts[field]}
			/>
		</ClayForm.Group>
	);

	return (
		<EditorSection
			title={Liferay.Language.get('crop')}
			titleId={eid('crop-panel-title')}
		>
			<div className="editor-panel-grid">
				{renderField('x')}

				{renderField('y')}
			</div>

			<div className="editor-crop-size-row">
				{renderField('width')}

				{renderField('height')}
			</div>
		</EditorSection>
	);
}
