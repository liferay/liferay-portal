/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClaySlider from '@clayui/slider';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {useEditorId} from '../chrome/instance';
import {EditorAction, clampCrop} from '../state/editorReducer';
import {CropRect} from '../state/types';

interface Props {
	angle: number;

	aspectLocked: boolean;

	bounds: {height: number; width: number};

	crop: CropRect;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onAspectLockedChange: (locked: boolean) => void;
	showStraighten: boolean;
}

type Field = 'height' | 'width' | 'x' | 'y';

const FIELD_LABELS: Record<Field, string> = {
	height: Liferay.Language.get('height'),
	width: Liferay.Language.get('width'),
	x: Liferay.Language.get('x-position'),
	y: Liferay.Language.get('y-position'),
};

export function CropPanel({
	angle,
	aspectLocked,
	bounds,
	crop,
	dispatch,
	onAnnounce,
	onAspectLockedChange,
	showStraighten,
}: Props) {
	const eid = useEditorId();

	const [drafts, setDrafts] = useState<Record<Field, string>>({
		height: String(crop.height),
		width: String(crop.width),
		x: String(crop.x),
		y: String(crop.y),
	});

	const angleGestureRef = useRef(false);

	const commitAngle = () => {
		if (!angleGestureRef.current) {
			return;
		}

		angleGestureRef.current = false;

		dispatch({angle, type: 'set-angle'});

		onAnnounce(
			sub(Liferay.Language.get('straighten-set-to-x-degrees'), angle)
		);
	};

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

		if (aspectLocked && crop.height > 0) {
			const aspect = crop.width / crop.height;

			if (field === 'width') {
				requested.height = Math.round(value / aspect);
			}
			else if (field === 'height') {
				requested.width = Math.round(value * aspect);
			}
		}

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
			title={Liferay.Language.get('crop-and-rotation')}
			titleId={eid('crop-panel-title')}
		>
			<div className="editor-panel-grid">
				{renderField('x')}

				{renderField('y')}
			</div>

			<div className="editor-crop-size-row">
				{renderField('width')}

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('lock-aspect-ratio')}
					aria-pressed={aspectLocked}
					borderless
					className="editor-aspect-lock"
					displayType="secondary"
					onClick={() => {
						onAnnounce(
							aspectLocked
								? Liferay.Language.get('aspect-ratio-unlocked')
								: Liferay.Language.get('aspect-ratio-locked')
						);

						onAspectLockedChange(!aspectLocked);
					}}
					size="xs"
					symbol={aspectLocked ? 'lock' : 'unlock'}
					title={Liferay.Language.get('lock-aspect-ratio')}
				/>

				{renderField('height')}
			</div>

			{showStraighten && (
				<ClayForm.Group small>
					<div className="editor-slider-row">
						<label htmlFor={eid('crop-angle')}>
							{Liferay.Language.get('straighten')}
						</label>

						<span
							aria-hidden="true"
							className="editor-slider-value"
						>
							{sub(Liferay.Language.get('x-degrees'), angle)}
						</span>

						{angle !== 0 && (
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'reset-the-straighten-angle'
								)}
								borderless
								className="editor-slider-reset"
								displayType="secondary"
								onClick={() => {
									dispatch({angle: 0, type: 'set-angle'});
									onAnnounce(
										sub(
											Liferay.Language.get(
												'straighten-set-to-x-degrees'
											),
											0
										)
									);
								}}
								size="xs"
								symbol="restore"
								title={Liferay.Language.get(
									'reset-the-straighten-angle'
								)}
							/>
						)}
					</div>

					<ClaySlider
						id={eid('crop-angle')}
						max={45}
						min={-45}
						onBlur={commitAngle}
						onChange={(next: number) => {
							angleGestureRef.current = true;

							dispatch({
								angle: next,
								transient: true,
								type: 'set-angle',
							});
						}}
						onKeyDown={(event: React.KeyboardEvent) => {

							// Shift steps by 10, as everywhere else.

							if (!event.shiftKey) {
								return;
							}

							const delta =
								event.key === 'ArrowRight' ||
								event.key === 'ArrowUp'
									? 10
									: event.key === 'ArrowLeft' ||
										  event.key === 'ArrowDown'
										? -10
										: 0;

							if (!delta) {
								return;
							}

							event.preventDefault();

							angleGestureRef.current = true;

							dispatch({
								angle: Math.max(
									-45,
									Math.min(45, angle + delta)
								),
								transient: true,
								type: 'set-angle',
							});
						}}
						onKeyUp={commitAngle}
						onPointerCancel={() => {
							if (angleGestureRef.current) {
								angleGestureRef.current = false;

								dispatch({type: 'cancel-gesture'});
							}
						}}
						onPointerUp={commitAngle}
						showTooltip={false}
						value={angle}
					/>
				</ClayForm.Group>
			)}
		</EditorSection>
	);
}
