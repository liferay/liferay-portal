/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React from 'react';

import {
	BorderField,
	ColorField,
	NumberField,
	TextField,
} from '../chrome/fields';
import {useEditorId} from '../chrome/instance';
import {DEFAULT_BORDER_COLOR, overlayLabel} from '../imaging/overlayShapes';
import {EditorAction} from '../state/editorReducer';
import {patchFor} from '../state/overlayPatch';
import {
	ArrowOverlay,
	CircleOverlay,
	Overlay,
	ShapeOverlay,
	isBoxOverlay,
} from '../state/types';
import {FONT_FAMILIES} from './textFonts';

interface Props {
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onProportionalChange: (proportional: boolean) => void;
	overlay: Overlay;

	proportional: boolean;
}

export function LayerProperties({
	dispatch,
	onAnnounce,
	onProportionalChange,
	overlay,
	proportional,
}: Props) {
	const eid = useEditorId();

	const label = overlayLabel(overlay);

	const commitPatch = (patch: Partial<Overlay>) => {
		dispatch({id: overlay.id, patch, type: 'update-overlay'});

		onAnnounce(sub(Liferay.Language.get('x-updated'), label));
	};

	const previewPatch = (patch: Partial<Overlay>) =>
		dispatch({
			id: overlay.id,
			patch,
			transient: true,
			type: 'update-overlay',
		});

	const sizePatch = (
		side: 'height' | 'width',
		value: number
	): Partial<Overlay> => {
		if (!proportional || !isBoxOverlay(overlay)) {
			return {[side]: value};
		}

		const ratio = overlay.width / overlay.height;

		const other = Math.max(
			Math.round(side === 'width' ? value / ratio : value * ratio),
			1
		);

		return side === 'width'
			? {height: other, width: value}
			: {height: value, width: other};
	};

	const commitSize = (side: 'height' | 'width', value: number) =>
		commitPatch(sizePatch(side, value));

	return (
		<div
			aria-labelledby={eid('layer-properties-title')}
			className="editor-layer-properties"
			role="group"
		>
			<div
				className="editor-panel-subtitle"
				id={eid('layer-properties-title')}
			>
				{sub(Liferay.Language.get('selected-layer-x'), label)}
			</div>

			{overlay.kind === 'text' && (
				<TextField
					id={eid('layer-prop-text')}
					label={Liferay.Language.get('text')}
					onCommit={(text) => commitPatch({text})}
					value={overlay.text}
				/>
			)}

			<div className="editor-panel-grid">
				<ColorField
					fill
					id={eid('layer-prop-color')}
					label={
						overlay.kind === 'text'
							? Liferay.Language.get('text-color')
							: Liferay.Language.get('color')
					}
					onCommit={(color) => commitPatch({color})}
					onPreview={(color) => previewPatch({color})}
					value={overlay.color}
				/>

				<NumberField
					id={eid('layer-prop-x')}
					label={Liferay.Language.get('x-position')}
					min={-Infinity}
					onCommit={(x) => commitPatch({x})}
					onPreview={(x) => previewPatch({x})}
					value={Math.round(overlay.x)}
				/>

				<NumberField
					id={eid('layer-prop-y')}
					label={Liferay.Language.get('y-position')}
					min={-Infinity}
					onCommit={(y) => commitPatch({y})}
					onPreview={(y) => previewPatch({y})}
					value={Math.round(overlay.y)}
				/>

				<NumberField
					id={eid('layer-prop-opacity')}
					label={Liferay.Language.get('opacity')}
					max={100}
					min={0}
					onCommit={(opacity) => commitPatch({opacity})}
					onPreview={(opacity) => previewPatch({opacity})}
					suffix="%"
					value={overlay.opacity ?? 100}
				/>

				{overlay.kind === 'arrow' && (
					<ClayForm.Group small>
						<label htmlFor={eid('layer-prop-head')}>
							{Liferay.Language.get('arrow-head')}
						</label>

						<ClaySelectWithOption
							id={eid('layer-prop-head')}
							onChange={(event) =>
								commitPatch(
									patchFor(overlay)({
										head: event.target
											.value as ArrowOverlay['head'],
									})
								)
							}
							options={[
								{
									label: Liferay.Language.get('filled'),
									value: 'filled',
								},
								{
									label: Liferay.Language.get('open'),
									value: 'open',
								},
							]}
							sizing="sm"
							value={overlay.head}
						/>
					</ClayForm.Group>
				)}

				{overlay.kind === 'arrow' && (
					<NumberField
						id={eid('layer-prop-tip-x')}
						label={Liferay.Language.get('tip-x-position')}
						min={-Infinity}
						onCommit={(tipX) =>
							commitPatch({dx: Math.round(tipX - overlay.x)})
						}
						onPreview={(tipX) =>
							previewPatch({dx: Math.round(tipX - overlay.x)})
						}
						value={Math.round(overlay.x + overlay.dx)}
					/>
				)}

				{overlay.kind === 'arrow' && (
					<NumberField
						id={eid('layer-prop-tip-y')}
						label={Liferay.Language.get('tip-y-position')}
						min={-Infinity}
						onCommit={(tipY) =>
							commitPatch({dy: Math.round(tipY - overlay.y)})
						}
						onPreview={(tipY) =>
							previewPatch({dy: Math.round(tipY - overlay.y)})
						}
						value={Math.round(overlay.y + overlay.dy)}
					/>
				)}

				{overlay.kind === 'arrow' && (
					<NumberField
						id={eid('layer-prop-thickness')}
						label={Liferay.Language.get('thickness')}
						min={1}
						onCommit={(thickness) => commitPatch({thickness})}
						onPreview={(thickness) => previewPatch({thickness})}
						value={overlay.thickness}
					/>
				)}

				{overlay.kind === 'text' && (
					<ClayForm.Group small>
						<label htmlFor={eid('layer-prop-font-family')}>
							{Liferay.Language.get('font-family')}
						</label>

						<ClaySelectWithOption
							id={eid('layer-prop-font-family')}
							onChange={(event) =>
								commitPatch({
									fontFamily: event.target.value,
								})
							}
							options={FONT_FAMILIES}
							sizing="sm"
							value={overlay.fontFamily}
						/>
					</ClayForm.Group>
				)}

				{overlay.kind === 'text' && (
					<NumberField
						id={eid('layer-prop-font-size')}
						label={Liferay.Language.get('font-size')}
						min={8}
						onCommit={(fontSize) => commitPatch({fontSize})}
						onPreview={(fontSize) => previewPatch({fontSize})}
						value={overlay.fontSize}
					/>
				)}

				{isBoxOverlay(overlay) && (
					<div className="editor-crop-size-row editor-layer-size-row">
						<NumberField
							id={eid('layer-prop-width')}
							label={Liferay.Language.get('width')}
							onCommit={(width) => commitSize('width', width)}
							onPreview={(width) =>
								previewPatch(sizePatch('width', width))
							}
							value={overlay.width}
						/>

						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'lock-aspect-ratio'
							)}
							aria-pressed={proportional}
							borderless
							className="editor-aspect-lock"
							displayType="secondary"
							onClick={() => {
								onAnnounce(
									proportional
										? Liferay.Language.get(
												'aspect-ratio-unlocked'
											)
										: Liferay.Language.get(
												'aspect-ratio-locked'
											)
								);

								onProportionalChange(!proportional);
							}}
							size="xs"
							symbol={proportional ? 'lock' : 'unlock'}
							title={Liferay.Language.get('lock-aspect-ratio')}
						/>

						<NumberField
							id={eid('layer-prop-height')}
							label={Liferay.Language.get('height')}
							onCommit={(height) => commitSize('height', height)}
							onPreview={(height) =>
								previewPatch(sizePatch('height', height))
							}
							value={overlay.height}
						/>
					</div>
				)}

				{overlay.kind !== 'arrow' && (
					<NumberField
						id={eid('layer-prop-rotation')}
						label={Liferay.Language.get('rotation')}
						max={360}
						min={-360}
						onCommit={(rotation) => commitPatch({rotation})}
						onPreview={(rotation) => previewPatch({rotation})}
						suffix="°"
						value={overlay.rotation ?? 0}
					/>
				)}

				{hasBorder(overlay) && (
					<ClayForm.Group small>
						<label htmlFor={eid('layer-prop-shape-style')}>
							{Liferay.Language.get('style')}
						</label>

						<ClaySelectWithOption
							id={eid('layer-prop-shape-style')}
							onChange={(event) =>
								commitPatch(
									patchFor(overlay)({
										sketchSeed:
											event.target.value === 'sketchy'
												? Math.floor(
														Math.random() * 2 ** 31
													)
												: undefined,
									})
								)
							}
							options={[
								{
									label: Liferay.Language.get('clean'),
									value: 'clean',
								},
								{
									label: Liferay.Language.get('hand-drawn'),
									value: 'sketchy',
								},
							]}
							sizing="sm"
							value={
								overlay.sketchSeed === undefined
									? 'clean'
									: 'sketchy'
							}
						/>
					</ClayForm.Group>
				)}

				{hasBorder(overlay) && (
					<BorderField
						colorLabel={Liferay.Language.get('border-color')}
						colorValue={overlay.borderColor ?? DEFAULT_BORDER_COLOR}
						id={eid('layer-prop-border-width')}
						label={Liferay.Language.get('border')}
						onColorCommit={(borderColor) =>
							commitPatch({borderColor})
						}
						onColorPreview={(borderColor) =>
							previewPatch({borderColor})
						}
						onWidthCommit={(borderWidth) =>
							commitPatch({borderWidth})
						}
						onWidthPreview={(borderWidth) =>
							previewPatch({borderWidth})
						}
						widthLabel={Liferay.Language.get('border-width')}
						widthValue={overlay.borderWidth ?? 0}
					/>
				)}
			</div>
		</div>
	);
}

function hasBorder(overlay: Overlay): overlay is CircleOverlay | ShapeOverlay {
	return overlay.kind === 'circle' || overlay.kind === 'shape';
}
