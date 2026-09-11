/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {sub} from 'frontend-js-web';
import React from 'react';

import {EditorSection} from '../chrome/EditorSection';
import {CommitSlider} from '../chrome/fields';
import {useEditorId} from '../chrome/instance';
import {EditorAction} from '../state/editorReducer';
import {Adjustments} from '../state/types';

const SLIDERS: Array<{key: keyof Adjustments; label: string}> = [
	{key: 'brightness', label: Liferay.Language.get('brightness')},
	{key: 'contrast', label: Liferay.Language.get('contrast')},
	{key: 'saturation', label: Liferay.Language.get('saturation')},
	{key: 'shadows', label: Liferay.Language.get('shadows')},
	{key: 'highlights', label: Liferay.Language.get('highlights')},
];

interface Props {
	adjustments: Adjustments;
	dispatch: (action: EditorAction) => void;
	onAnnounce: (message: string) => void;
}

export function AdjustPanel({adjustments, dispatch, onAnnounce}: Props) {
	const eid = useEditorId();

	const hasAdjustments = SLIDERS.some(({key}) => adjustments[key] !== 0);

	return (
		<EditorSection
			title={Liferay.Language.get('adjustments')}
			titleId={eid('adjust-panel-title')}
		>
			{SLIDERS.map(({key, label}) => {
				const value = adjustments[key];

				return (
					<CommitSlider
						id={eid(`adjust-${key}`)}
						key={key}
						label={label}
						max={100}
						min={-100}
						onCancel={() => dispatch({type: 'cancel-gesture'})}
						onCommit={(next) => {
							dispatch({
								key,
								type: 'set-adjustment',
								value: next,
							});

							onAnnounce(
								sub(
									Liferay.Language.get('x-set-to-x'),
									label,
									next
								)
							);
						}}
						onPreview={(next) =>
							dispatch({
								key,
								transient: true,
								type: 'set-adjustment',
								value: next,
							})
						}
						shiftStep={10}
						value={value}
						valueLabel={String(value)}
					>
						<ClayButtonWithIcon
							aria-label={sub(
								Liferay.Language.get('reset-x'),
								label
							)}
							borderless
							className="editor-slider-reset"
							disabled={value === 0}
							displayType="secondary"
							onClick={() => {
								dispatch({
									key,
									type: 'set-adjustment',
									value: 0,
								});

								onAnnounce(
									sub(
										Liferay.Language.get('x-set-to-x'),
										label,
										0
									)
								);
							}}
							size="xs"
							symbol="restore"
							title={sub(Liferay.Language.get('reset-x'), label)}
						/>
					</CommitSlider>
				);
			})}

			{hasAdjustments && (
				<div className="editor-panel-actions">
					<ClayButton
						displayType="secondary"
						onClick={() => {
							dispatch({type: 'reset-adjustments'});

							onAnnounce(
								Liferay.Language.get(
									'the-adjustments-were-reset'
								)
							);

							// This button disappears once everything is back
							// to zero: hand focus to the adjacent slider so
							// it is never dropped.

							window.setTimeout(
								() =>
									document
										.getElementById(
											eid(
												`adjust-${SLIDERS[SLIDERS.length - 1].key}`
											)
										)
										?.focus(),
								0
							);
						}}
						size="xs"
					>
						{Liferay.Language.get('reset-all')}
					</ClayButton>
				</div>
			)}
		</EditorSection>
	);
}
