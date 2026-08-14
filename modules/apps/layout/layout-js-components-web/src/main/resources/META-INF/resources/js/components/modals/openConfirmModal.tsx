/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {openModal, openToast} from 'frontend-js-components-web';
import React, {useRef, useState} from 'react';

type Status = 'danger' | 'info' | 'warning';

type Props = {
	blocking?: boolean;
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
	if (props.blocking || props.optOutConfig) {
		return openStatefulConfirmModal(props);
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

async function openStatefulConfirmModal({
	blocking,
	buttonLabel,
	cancelButtonLabel,
	center,
	hideCancel,
	onCancel = () => Promise.resolve(),
	onCloseFocusElement,
	onConfirm = () => Promise.resolve(),
	optOutConfig,
	status,
	text,
	title,
}: Props) {
	if (optOutConfig && (await isOptedOut(optOutConfig.sessionKey))) {
		return true;
	}

	return new Promise((resolve) => {
		openModal({
			center,
			contentComponent: ({closeModal}: {closeModal: () => void}) =>
				StatefulModalContent({
					blocking,
					body: text,
					buttonLabel,
					cancelButtonLabel,
					hideCancel,
					onCancel: () => {
						closeModal();

						onCancel().then(() => resolve(false));
					},
					onConfirm: async () => {
						if (blocking) {
							try {
								await onConfirm();
							}
							catch (error) {
								openToast({
									message: Liferay.Language.get(
										'an-unexpected-error-occurred'
									),
									type: 'danger',
								});
							}

							closeModal();
						}
						else {
							closeModal();

							await onConfirm();
						}

						resolve(true);
					},
					optOutConfig,
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

function StatefulModalContent({
	blocking,
	body,
	buttonLabel,
	cancelButtonLabel,
	hideCancel,
	onCancel,
	onConfirm,
	optOutConfig,
	status,
	title,
}: {
	blocking?: boolean;
	body?: string;
	buttonLabel: string;
	cancelButtonLabel?: string;
	hideCancel?: boolean;
	onCancel: () => void;
	onConfirm: () => Promise<void>;
	optOutConfig?: {label?: string; sessionKey: string};
	status?: Status;
	title: string;
}) {
	const {
		label: optOutLabel = Liferay.Language.get('do-not-show-me-this-again'),
	} = optOutConfig ?? {};

	const [disable, setDisable] = useState(false);
	const [pending, setPending] = useState(false);

	const pendingRef = useRef(false);

	return (
		<>
			<ClayModal.Header>{title}</ClayModal.Header>

			<ClayModal.Body>
				{body ? (
					<div
						className="text-secondary"
						dangerouslySetInnerHTML={{__html: body}}
					/>
				) : null}
			</ClayModal.Body>

			<ClayModal.Footer
				first={
					optOutConfig ? (
						<ClayCheckbox
							checked={disable}
							label={optOutLabel}
							onChange={({target: {checked}}) =>
								setDisable(checked)
							}
						/>
					) : undefined
				}
				last={
					<ClayButton.Group spaced>
						{hideCancel ? null : (
							<ClayButton
								autoFocus
								disabled={pending}
								displayType="secondary"
								onClick={() => {
									onCancel();

									if (disable && optOutConfig) {
										optOut(optOutConfig.sessionKey);
									}
								}}
							>
								{cancelButtonLabel ||
									Liferay.Language.get('cancel')}
							</ClayButton>
						)}

						<ClayButton
							disabled={pending}
							displayType={status}
							onClick={async () => {
								if (disable && optOutConfig) {
									optOut(optOutConfig.sessionKey);
								}

								if (!blocking) {
									await onConfirm();

									return;
								}

								if (pendingRef.current) {
									return;
								}

								pendingRef.current = true;

								setPending(true);

								try {
									await onConfirm();
								}
								finally {
									pendingRef.current = false;

									setPending(false);
								}
							}}
						>
							{pending ? (
								<span className="inline-item inline-item-before">
									<ClayLoadingIndicator
										displayType="light"
										size="sm"
									/>
								</span>
							) : null}

							{buttonLabel}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
