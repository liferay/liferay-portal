/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClaySelectWithOption} from '@clayui/form';
import {sub} from 'frontend-js-web';
import React from 'react';

import {EditorAction} from '../state/editorReducer';
import {RatioPreset} from '../state/types';
import {useEditorId} from './instance';

const RATIO_OPTIONS: Array<{label: string; value: RatioPreset}> = [
	{label: Liferay.Language.get('custom'), value: 'custom'},
	{label: Liferay.Language.get('original'), value: 'original'},
	{label: '1:1', value: '1:1'},
	{label: '4:3', value: '4:3'},
	{label: '16:9', value: '16:9'},
	{label: '3:4', value: '3:4'},
	{label: '9:16', value: '9:16'},
];

interface Props {
	canRedo: boolean;
	canUndo: boolean;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
	onCancel: () => void;
	onRedo: () => void;
	onSave: () => void;
	onShowShortcuts: () => void;
	onUndo: () => void;
	onZoom: (direction: -1 | 1) => void;
	onZoomFit: () => void;
	ratio: RatioPreset;

	ratios: RatioPreset[];

	saving: boolean;
	showRotate: boolean;
	zoom: number;
}

export function BottomBar({
	canRedo,
	canUndo,
	dispatch,
	onAnnounce,
	onCancel,
	onRedo,
	onSave,
	onShowShortcuts,
	onUndo,
	onZoom,
	onZoomFit,
	ratio,
	ratios,
	saving,
	showRotate,
	zoom,
}: Props) {
	const eid = useEditorId();

	return (
		<div className="editor-bottom-bar">
			<div className="editor-bar-first editor-bar-group">
				{!!ratios.length && (
					<>
						<label
							className="editor-ratio-label"
							htmlFor={eid('crop-ratio-select')}
						>
							{Liferay.Language.get('ratio')}
						</label>

						<ClaySelectWithOption
							className="editor-ratio-select"
							id={eid('crop-ratio-select')}
							onChange={(event) => {
								dispatch({
									ratio: event.target.value as RatioPreset,
									type: 'set-ratio',
								});
							}}
							options={RATIO_OPTIONS.filter(({value}) =>
								ratios.includes(value)
							)}
							sizing="sm"
							value={ratio}
						/>
					</>
				)}

				{showRotate && (
					<>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'rotate-90-degrees-clockwise'
							)}
							borderless
							className="editor-bar-button"
							displayType="secondary"
							onClick={() => {
								dispatch({type: 'rotate-90'});
								onAnnounce(
									Liferay.Language.get(
										'the-image-was-rotated-90-degrees-clockwise'
									)
								);
							}}
							symbol="rotate"
							title={Liferay.Language.get(
								'rotate-90-degrees-clockwise'
							)}
						/>

						<ClayButtonWithIcon
							aria-label={Liferay.Language.get(
								'flip-horizontally'
							)}
							borderless
							className="editor-bar-button"
							displayType="secondary"
							onClick={() => {
								dispatch({type: 'flip-horizontal'});
								onAnnounce(
									Liferay.Language.get(
										'the-image-was-flipped-horizontally'
									)
								);
							}}
							symbol="flip-horizontal"
							title={Liferay.Language.get('flip-horizontally')}
						/>
					</>
				)}

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('undo')}
					borderless
					className="editor-bar-button"
					disabled={!canUndo}
					displayType="secondary"
					onClick={onUndo}
					symbol="undo"
					title={Liferay.Language.get('undo')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('redo')}
					borderless
					className="editor-bar-button"
					disabled={!canRedo}
					displayType="secondary"
					onClick={onRedo}
					symbol="redo"
					title={Liferay.Language.get('redo')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('keyboard-shortcuts')}
					borderless
					className="editor-bar-button"
					displayType="secondary"
					onClick={onShowShortcuts}
					symbol="question-circle"
					title={Liferay.Language.get('keyboard-shortcuts')}
				/>
			</div>

			<div className="editor-bar-group editor-bar-middle">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('zoom-out')}
					borderless
					className="editor-bar-button"
					displayType="secondary"
					onClick={() => onZoom(-1)}
					symbol="minus-circle"
					title={Liferay.Language.get('zoom-out')}
				/>

				<span className="editor-zoom-level">
					{sub(
						Liferay.Language.get('x-percent'),
						Math.round(zoom * 100)
					)}
				</span>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('zoom-in')}
					borderless
					className="editor-bar-button"
					displayType="secondary"
					onClick={() => onZoom(1)}
					symbol="plus-circle-full"
					title={Liferay.Language.get('zoom-in')}
				/>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('fit-image-to-window')}
					borderless
					className="editor-bar-button"
					displayType="secondary"
					onClick={onZoomFit}
					symbol="autosize"
					title={Liferay.Language.get('fit-image-to-window')}
				/>
			</div>

			<div
				aria-busy={saving}
				className="editor-bar-group editor-bar-last"
			>
				<ClayButton
					disabled={saving}
					displayType="secondary"
					onClick={onCancel}
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>

				<ClayButton
					disabled={saving}
					displayType="primary"
					onClick={onSave}
				>
					{saving
						? Liferay.Language.get('saving')
						: Liferay.Language.get('save')}
				</ClayButton>
			</div>
		</div>
	);
}
