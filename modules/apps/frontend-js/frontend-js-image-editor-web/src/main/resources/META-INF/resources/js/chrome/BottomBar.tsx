/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayModal from '@clayui/modal';
import React from 'react';

import {t} from '../i18n';
import {EditorAction} from '../state/editorReducer';

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
	saving: boolean;
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
	saving,
	zoom,
}: Props) {
	return (
		<ClayModal.Footer
			className="editor-bottom-bar"
			first={
				<div className="editor-bar-group">
					<ClayButtonWithIcon
						aria-label={t('rotate-90')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => {
							dispatch({type: 'rotate-90'});
							onAnnounce(t('rotated-90'));
						}}
						symbol="rotate"
						title={t('rotate-90')}
					/>

					<ClayButtonWithIcon
						aria-label={t('flip-horizontal')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => {
							dispatch({type: 'flip-horizontal'});
							onAnnounce(t('flipped-horizontal'));
						}}
						symbol="flip-horizontal"
						title={t('flip-horizontal')}
					/>

					<ClayButtonWithIcon
						aria-label={t('undo')}
						borderless
						className="editor-bar-button"
						disabled={!canUndo}
						displayType="secondary"
						onClick={onUndo}
						symbol="undo"
						title={t('undo')}
					/>

					<ClayButtonWithIcon
						aria-label={t('redo')}
						borderless
						className="editor-bar-button"
						disabled={!canRedo}
						displayType="secondary"
						onClick={onRedo}
						symbol="redo"
						title={t('redo')}
					/>

					<ClayButtonWithIcon
						aria-label={t('keyboard-shortcuts')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={onShowShortcuts}
						symbol="question-circle"
						title={t('keyboard-shortcuts')}
					/>
				</div>
			}
			last={
				<div aria-busy={saving} className="editor-bar-group">
					<ClayButton
						disabled={saving}
						displayType="secondary"
						onClick={onCancel}
					>
						{t('cancel')}
					</ClayButton>

					<ClayButton
						disabled={saving}
						displayType="primary"
						onClick={onSave}
					>
						{saving ? t('saving') : t('save')}
					</ClayButton>
				</div>
			}
			middle={
				<div className="editor-bar-group">
					<ClayButtonWithIcon
						aria-label={t('zoom-out')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => onZoom(-1)}
						symbol="minus-circle"
						title={t('zoom-out')}
					/>

					<span className="editor-zoom-level">
						{t('zoom-percent', Math.round(zoom * 100))}
					</span>

					<ClayButtonWithIcon
						aria-label={t('zoom-in')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={() => onZoom(1)}
						symbol="plus-circle-full"
						title={t('zoom-in')}
					/>

					<ClayButtonWithIcon
						aria-label={t('zoom-fit')}
						borderless
						className="editor-bar-button"
						displayType="secondary"
						onClick={onZoomFit}
						symbol="autosize"
						title={t('zoom-fit')}
					/>
				</div>
			}
		/>
	);
}
