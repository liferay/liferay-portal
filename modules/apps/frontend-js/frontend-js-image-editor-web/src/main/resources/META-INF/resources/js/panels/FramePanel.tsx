/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {memo} from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {ColorField, CommitSlider} from '../chrome/fields';
import {useEditorId} from '../chrome/instance';
import {FrameShape} from '../imaging/frameShapes';
import {LoadedImage} from '../imaging/loadImage';
import {EditorAction} from '../state/editorReducer';
import {Frame, FrameKind} from '../state/types';
import {PRESET_THUMB_BOX, PresetGallery, PresetThumb} from './PresetGallery';

const FRAME_LABELS: Record<FrameKind, string> = {
	bevel: Liferay.Language.get('bevel'),
	corners: Liferay.Language.get('corners'),
	dashed: Liferay.Language.get('dashed'),
	double: Liferay.Language.get('double-line'),
	inset: Liferay.Language.get('inset'),
	line: Liferay.Language.get('line'),
	mat: Liferay.Language.get('mat'),
	none: Liferay.Language.get('none'),
	polaroid: Liferay.Language.get('print[noun]'),
	ticks: Liferay.Language.get('ticks'),
};

const SLIDERS: {key: 'offset' | 'size'; label: string; max: number}[] = [
	{key: 'size', label: Liferay.Language.get('frame-size'), max: 20},
	{key: 'offset', label: Liferay.Language.get('frame-offset'), max: 15},
];

interface Props {
	dispatch: (action: EditorAction) => void;
	frame: Frame;
	image: LoadedImage;
	onAnnounce: (message: string) => void;

	presets: FrameKind[];
}

function FramePanelCards({dispatch, frame, image, onAnnounce, presets}: Props) {
	const eid = useEditorId();

	return (
		<EditorSection
			title={Liferay.Language.get('frame')}
			titleId={eid('frame-panel-title')}
		>
			<PresetGallery
				idPrefix={eid('frame')}
				items={presets}
				label={(kind) => FRAME_LABELS[kind]}
				legend={Liferay.Language.get('frame')}
				onSelect={(kind) => {
					dispatch({frame: {kind}, type: 'set-frame'});

					onAnnounce(
						sub(
							Liferay.Language.get('frame-set-to-x'),
							FRAME_LABELS[kind]
						)
					);
				}}
				preview={(kind) => (
					<PresetThumb thumbUrl={image.thumbUrl}>
						<FrameShape
							crop={PRESET_THUMB_BOX}
							frame={{...frame, kind}}
						/>
					</PresetThumb>
				)}
				selected={frame.kind}
			/>

			{/*
			 * The options only exist once there is something to configure,
			 * and "None" is not a frame with a thin white border.
			 */}

			{frame.kind !== 'none' && (
				<>
					<ColorField
						fill
						id={eid('frame-color')}
						label={Liferay.Language.get('frame-color')}
						onCommit={(color) => {
							dispatch({frame: {color}, type: 'set-frame'});

							onAnnounce(Liferay.Language.get('frame-color-set'));
						}}
						onPreview={(color) =>
							dispatch({
								frame: {color},
								transient: true,
								type: 'set-frame',
							})
						}
						value={frame.color}
					/>

					{SLIDERS.map(({key, label, max}) => (
						<CommitSlider
							id={eid(`frame-${key}`)}
							key={key}
							label={label}
							max={max}
							min={0}
							onCancel={() => dispatch({type: 'cancel-gesture'})}
							onCommit={(value) => {
								dispatch({
									frame: {[key]: value},
									type: 'set-frame',
								});

								onAnnounce(
									sub(
										Liferay.Language.get(
											'x-set-to-x-percent'
										),
										label,
										value
									)
								);
							}}
							onPreview={(value) =>
								dispatch({
									frame: {[key]: value},
									transient: true,
									type: 'set-frame',
								})
							}
							shiftStep={5}
							value={frame[key]}
							valueLabel={sub(
								Liferay.Language.get('x-percent'),
								frame[key]
							)}
						/>
					))}
				</>
			)}
		</EditorSection>
	);
}

/*
 * The cards are the most expensive thing in the sidebar, and none of them
 * change while a crop or an annotation is being dragged: memoized, they
 * are drawn once per actual change instead of once per pointer move.
 */

export const FramePanel = memo(FramePanelCards);
