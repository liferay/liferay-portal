/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';

const SHORTCUTS: Array<{description: string; keys: string}> = [
	{
		description: Liferay.Language.get(
			'move-or-adjust-the-focused-control-by-1'
		),
		keys: 'Arrow keys',
	},
	{
		description: Liferay.Language.get(
			'move-or-adjust-the-focused-control-by-10'
		),
		keys: 'Shift + Arrow keys',
	},
	{
		description: Liferay.Language.get(
			'keep-the-proportions-while-resizing-the-crop-or-a-box-annotation'
		),
		keys: 'Shift + drag',
	},
	{
		description: Liferay.Language.get(
			'resize-the-crop-from-its-center-while-dragging'
		),
		keys: 'Alt + drag',
	},
	{
		description: Liferay.Language.get(
			'zoom-in-and-out-while-the-workspace-has-focus-towards-the-pointer-when-it-is-over-the-image-and-towards-the-center-of-the-view-otherwise'
		),
		keys: '+ / -',
	},
	{
		description: Liferay.Language.get(
			'fit-the-image-to-the-window-while-the-workspace-has-focus'
		),
		keys: '0',
	},
	{
		description: Liferay.Language.get('zoom-to-actual-size'),
		keys: '1',
	},
	{
		description: Liferay.Language.get('fit-the-crop-area-to-the-window'),
		keys: '2',
	},
	{
		description: Liferay.Language.get(
			'add-or-remove-an-annotation-from-a-group-that-moves-and-deletes-together'
		),
		keys: 'Shift + click',
	},
	{
		description: Liferay.Language.get('copy-the-focused-annotation'),
		keys: 'Ctrl/Cmd + C',
	},
	{
		description: Liferay.Language.get('paste-the-copied-annotation'),
		keys: 'Ctrl/Cmd + V',
	},
	{
		description: Liferay.Language.get('undo-the-last-change'),
		keys: 'Ctrl/Cmd + Z',
	},
	{
		description: Liferay.Language.get('redo-the-last-undone-change'),
		keys: 'Ctrl/Cmd + Shift + Z',
	},
	{
		description: Liferay.Language.get(
			'close-the-editor-or-the-open-dialog'
		),
		keys: 'Esc',
	},
];

interface Props {
	onOpenChange: (open: boolean) => void;
	open: boolean;
}

export function ShortcutsDialog({onOpenChange, open}: Props) {
	const {observer} = useModal({onClose: () => onOpenChange(false)});

	if (!open) {
		return null;
	}

	return (
		<ClayModal observer={observer}>
			<div
				onKeyDown={(event: React.KeyboardEvent) => {
					if (event.key !== 'Escape') {
						event.stopPropagation();
					}
				}}
			>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
					withTitle
				>
					{Liferay.Language.get('keyboard-shortcuts')}
				</ClayModal.Header>

				<ClayModal.Body>
					<dl className="editor-shortcut-list small">
						{SHORTCUTS.map(({description, keys}) => (
							<React.Fragment key={keys}>
								<dt>
									<kbd>{keys}</kbd>
								</dt>

								<dd>{description}</dd>
							</React.Fragment>
						))}
					</dl>
				</ClayModal.Body>
			</div>
		</ClayModal>
	);
}
