/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useState} from 'react';

import {ClayInputGroupAI} from '../src';
import ClayForm from '../src/Form';

export default {
	argTypes: {
		aiState: {
			control: {type: 'select'},
			options: [
				undefined,
				'focused',
				'result',
				'result-readonly',
				'working',
			],
		},
	},
	title: 'Design System/Components/InputGroupAI',
};

export function Default(args: any) {
	const [value, setValue] = useState('');

	return (
		<div className="sheet">
			<ClayForm
				onSubmit={(event) => {
					event.preventDefault();

					setValue('');
				}}
			>
				<ClayInputGroupAI
					aiState={args.aiState}
					onChange={(event) => setValue(event.target.value)}
					placeholder="Ask me anything..."
					value={value}
				/>
			</ClayForm>
		</div>
	);
}

Default.args = {
	aiState: undefined,
};

export function Result() {
	const [value, setValue] = useState('A generated suggestion');

	return (
		<div className="sheet">
			<ClayForm onSubmit={(event) => event.preventDefault()}>
				<ClayInputGroupAI
					aiState="result"
					onChange={(event) => setValue(event.target.value)}
					onRetryClick={() =>
						setValue('Another generated suggestion')
					}
					value={value}
				/>

				<ClayForm.FeedbackGroup>
					<ClayForm.Text>
						<ClayForm.FeedbackIndicator symbol="stars" />
						Suggestion
					</ClayForm.Text>
				</ClayForm.FeedbackGroup>
			</ClayForm>
		</div>
	);
}

export function Working() {
	return (
		<div className="sheet">
			<ClayForm onSubmit={(event) => event.preventDefault()}>
				<ClayInputGroupAI
					aiState="working"
					onChange={() => {}}
					value=""
				/>
			</ClayForm>
		</div>
	);
}
