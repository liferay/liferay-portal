/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Provider} from '@clayui/core';
const spritemap = require('@clayui/css/src/images/icons/icons.svg');
import React, {useMemo, useState} from 'react';

import ClayAlert, {ClayToaster, createToastStore} from '../src';

export default {
	argTypes: {
		autoFocus: {
			control: {type: 'boolean'},
		},
		displayType: {
			control: {type: 'select'},
			options: ['danger', 'info', 'secondary', 'success', 'warning'],
		},
		variant: {
			control: {type: 'select'},
			options: ['feedback', 'stripe', undefined],
		},
	},
	component: ClayAlert,
	title: 'Design System/Components/Alert',
};
export function Default(args: any) {
	return (
		<ClayAlert
			autoFocus={args.autoFocus}
			displayType={args.displayType}
			title={args.title}
			variant={args.variant}
		>
			{args.content}
		</ClayAlert>
	);
}
export function Feedback() {
	return (
		<>
			<ClayAlert displayType="warning" variant="feedback">
				Data is temporarily unavailable
			</ClayAlert>
			<ClayAlert displayType="danger" variant="feedback">
				Error loading data
			</ClayAlert>
			<ClayAlert displayType="info" variant="feedback">
				News on the{' '}

				<a className="alert-link" href="#">
					blog
				</a>
			</ClayAlert>
			<ClayAlert displayType="success" variant="feedback">
				Data is saved
			</ClayAlert>
			<ClayAlert displayType="secondary" variant="feedback">
				Due to permission restrictions, this content cannot be
				displayed.
			</ClayAlert>
		</>
	);
}
export function Toast() {
	return (
		<ClayAlert.ToastContainer>
			<ClayAlert
				actions={
					<ClayButton alert displayType="warning" small>
						Refresh
					</ClayButton>
				}
				displayType="warning"
				onClose={() => {}}
				title="Warning"
			>
				Data is temporarily unavailable
			</ClayAlert>

			<ClayAlert
				actions={
					<ClayButton alert displayType="danger" small>
						Retry
					</ClayButton>
				}
				displayType="danger"
				onClose={() => {}}
				title="Error"
				variant="inline"
			>
				Error loading data
			</ClayAlert>

			<ClayAlert displayType="info" onClose={() => {}}>
				News on the{' '}

				<a className="alert-link" href="#">
					blog
				</a>
			</ClayAlert>

			<ClayAlert displayType="success" onClose={() => {}} title="Success">
				Data is saved
			</ClayAlert>

			<ClayAlert displayType="success" onClose={() => {}} title="Success">
				Strong kopi-luwak half and half single origin single shot, half
				and half instant latte brewed latte. At, decaffeinated, blue
				mountain viennese barista, spoon carajillo shop viennese dark.
				And ristretto caffeine, plunger pot black, café au lait galão
				flavour foam strong. Macchiato roast, breve fair trade seasonal
				et cultivar.
			</ClayAlert>

			<ClayAlert displayType="secondary" onClose={() => {}}>
				Due to permission restrictions, this content cannot be
				displayed.
			</ClayAlert>

			<ClayAlert
				displayType="secondary"
				onClose={() => {}}
				title="Success"
			>
				Due to permission restrictions, this content cannot be
				displayed.
			</ClayAlert>
		</ClayAlert.ToastContainer>
	);
}

export function ToastWithPersistentAlerts() {
	const [visible, setVisible] = useState(true);

	return (
		<Provider persistentAlerts spritemap={spritemap}>
			{visible && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						displayType="info"
						onClose={() => setVisible(false)}
						title="Info"
					>
						This alert stays until it is dismissed.
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}
		</Provider>
	);
}

export function Toaster() {
	const store = useMemo(() => createToastStore(), []);

	return (
		<Provider spritemap={spritemap}>
			<ClayButton.Group spaced>
				<ClayButton
					displayType="secondary"
					onClick={() =>
						store.add({
							children: 'Data is saved',
							displayType: 'success',
							onClose: () => {},
							title: 'Success',
						})
					}
				>
					Add single line
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() =>
						store.add({
							children:
								'Strong kopi-luwak half and half single origin single shot, half and half instant latte brewed latte. At, decaffeinated, blue mountain viennese barista, spoon carajillo shop viennese dark. And ristretto caffeine, plunger pot black, café au lait galão flavour foam strong.',
							displayType: 'info',
							onClose: () => {},
							title: 'Info',
						})
					}
				>
					Add multiline
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() =>
						store.add({
							actions: (
								<ClayButton alert displayType="danger" small>
									Retry
								</ClayButton>
							),
							children: 'Error loading data',
							displayType: 'danger',
							onClose: () => {},
							title: 'Error',
							variant: 'inline',
						})
					}
				>
					Add with actions
				</ClayButton>
			</ClayButton.Group>

			<ClayToaster store={store} />
		</Provider>
	);
}

Default.args = {
	autoFocus: false,
	content: 'This is an alert!',
	displayType: 'info',
	title: 'Info',
	variant: undefined,
};
