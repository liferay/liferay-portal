/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelectWithOption} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useEffect, useRef, useState} from 'react';

import {ColorField} from '../chrome/fields';
import {useEditorId} from '../chrome/instance';
import {nextId} from '../state/ids';
import {TextOverlay} from '../state/types';
import {FONT_FAMILIES} from './textFonts';

export const TEXT_DIALOG_OPEN_MS = 350;

export const TEXT_DIALOG_CLOSE_MS = 450;

interface Props {
	onAdd: (overlay: Omit<TextOverlay, 'x' | 'y'>) => void;
	onOpenChange: (open: boolean) => void;
	open: boolean;
}

export function TextDialog({onAdd, onOpenChange, open}: Props) {
	const eid = useEditorId();

	const {observer} = useModal({onClose: () => onOpenChange(false)});

	const [text, setText] = useState('');
	const [fontFamily, setFontFamily] = useState('sans-serif');
	const [fontSize, setFontSize] = useState('64');
	const [color, setColor] = useState('#ffffff');

	const inputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		if (!open) {
			return;
		}

		const id = window.setTimeout(() => {
			const active = document.activeElement;

			if (
				!active ||
				active === document.body ||
				active.classList.contains('modal-content')
			) {
				inputRef.current?.focus();
			}
		}, TEXT_DIALOG_OPEN_MS);

		return () => window.clearTimeout(id);
	}, [open]);

	if (!open) {
		return null;
	}

	const submit = (event: React.FormEvent) => {
		event.preventDefault();

		if (!text.trim()) {
			return;
		}

		onAdd({
			color,
			fontFamily,
			fontSize: Math.max(Number.parseInt(fontSize, 10) || 64, 8),
			id: nextId('text'),
			kind: 'text',
			text: text.trim(),
		});

		onOpenChange(false);
	};

	return (
		<ClayModal observer={observer} size="sm">
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
					{Liferay.Language.get('add-text')}
				</ClayModal.Header>

				<ClayModal.Body>
					<form onSubmit={submit}>
						<ClayForm.Group>
							<label htmlFor={eid('text')}>
								{Liferay.Language.get('text')}
							</label>

							<ClayInput
								id={eid('text')}
								onChange={(event) =>
									setText(event.target.value)
								}
								ref={inputRef}
								type="text"
								value={text}
							/>
						</ClayForm.Group>

						<ClayForm.Group>
							<label htmlFor={eid('text-font-family')}>
								{Liferay.Language.get('font-family')}
							</label>

							<ClaySelectWithOption
								id={eid('text-font-family')}
								onChange={(event) =>
									setFontFamily(event.target.value)
								}
								options={FONT_FAMILIES}
								value={fontFamily}
							/>
						</ClayForm.Group>

						<ClayForm.Group>
							<label htmlFor={eid('text-font-size')}>
								{Liferay.Language.get('font-size')}
							</label>

							<ClayInput
								id={eid('text-font-size')}
								min={8}
								onChange={(event) =>
									setFontSize(event.target.value)
								}
								type="number"
								value={fontSize}
							/>
						</ClayForm.Group>

						<ColorField
							fill
							id={eid('text-color')}
							label={Liferay.Language.get('text-color')}
							onCommit={setColor}
							onPreview={setColor}
							value={color}
						/>

						<ClayButton
							disabled={!text.trim()}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('add')}
						</ClayButton>
					</form>
				</ClayModal.Body>
			</div>
		</ClayModal>
	);
}
