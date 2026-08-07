/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {openModal} from 'frontend-js-components-web';
import React, {useState} from 'react';

type Status = 'danger' | 'info' | 'warning';

type Props = {
	buttonLabel: string;
	cancelButtonLabel?: string;
	center?: boolean;
	hideCancel?: boolean;
	onCancel?: () => Promise<void>;
	onCloseFocusElement?: HTMLButtonElement | null;
	onConfirm?: () => Promise<void>;
	optOutConfig?: {label?: string; sessionKey: string};
	status?: Status;
	text?: string;
	title: string;
};

export default function openConfirmModal(props: Props) {
	if (props.optOutConfig) {
		return openOptOutConfirmModal(props);
	}
	else {
		return openStandardConfirmModal(props);
	}
}

function openStandardConfirmModal({
	buttonLabel,
	cancelButtonLabel,
	center,
	hideCancel,
	onCancel = () => Promise.resolve(),
	onCloseFocusElement,
	onConfirm = () => Promise.resolve(),
	status,
	text,
	title,
}: Props) {
	return new Promise((resolve) => {
		const buttons = [];

		if (!hideCancel) {
			buttons.push({
				autoFocus: true,
				displayType: 'secondary' as const,
				label: cancelButtonLabel || Liferay.Language.get('cancel'),
				onClick: ({processClose} = {processClose: () => {}}) => {
					processClose();

					onCancel().then(() => resolve(false));
				},
				type: 'cancel' as const,
			});
		}

		buttons.push({
			displayType: status,
			label: buttonLabel,
			onClick: ({processClose} = {processClose: () => {}}) => {
				processClose();

				onConfirm().then(() => resolve(true));
			},
		});

		openModal({
			bodyHTML: text && `<div class="text-secondary">${text}</div>`,
			buttons,
			center,
			onClose: () => {
				if (onCloseFocusElement) {
					onCloseFocusElement.focus();
				}
			},
			status,
			title,
		});
	});
}

async function openOptOutConfirmModal({
	buttonLabel,
	cancelButtonLabel,
	center,
	onCancel = () => Promise.resolve(),
	onCloseFocusElement,
	onConfirm = () => Promise.resolve(),
	optOutConfig,
	status,
	text,
	title,
}: Props) {
	const {label: optOutLabel, sessionKey} = optOutConfig!;

	if (await isOptedOut(sessionKey)) {
		return true;
	}

	return new Promise((resolve) => {
		openModal({
			center,
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				ModalContent({
					body: text,
					buttonLabel,
					cancelButtonLabel,
					onCancel: () => {
						closeModal();

						onCancel().then(() => resolve(false));
					},
					onConfirm: () => {
						closeModal();

						onConfirm().then(() => resolve(true));
					},

					optOutLabel,
					sessionKey,
					status,
					title,
				}),
			onClose: () => {
				if (onCloseFocusElement) {
					onCloseFocusElement.focus();
				}
			},
			status,
		});
	});
}

async function isOptedOut(key: string) {
	const value = await Liferay.Util.Session.get(key);

	return value === 'true';
}

function optOut(key: string) {
	Liferay.Util.Session.set(key, 'true');
}

function ModalContent({
	body,
	buttonLabel,
	cancelButtonLabel,
	onCancel,
	onConfirm,
	optOutLabel = Liferay.Language.get('do-not-show-me-this-again'),
	sessionKey,
	status,
	title,
}: {
	body?: string;
	buttonLabel: string;
	cancelButtonLabel?: string;
	onCancel: () => void;
	onConfirm: () => void;
	optOutLabel?: string;
	sessionKey: string;
	status?: Status;
	title: string;
}) {
	const [disable, setDisable] = useState(false);

	return (
		<>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>
				{body ? <p className="mb-0 text-secondary">{body}</p> : null}
			</ClayModal.Body>

			<ClayModal.Footer
				first={
					<ClayCheckbox
						checked={disable}
						label={optOutLabel}
						onChange={({target: {checked}}) => setDisable(checked)}
					/>
				}
				last={
					<ClayButton.Group spaced>
						<ClayButton
							autoFocus
							displayType="secondary"
							onClick={() => {
								onCancel();

								if (disable) {
									optOut(sessionKey);
								}
							}}
						>
							{cancelButtonLabel ||
								Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType={status}
							onClick={() => {
								onConfirm();

								if (disable) {
									optOut(sessionKey);
								}
							}}
						>
							{buttonLabel}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
