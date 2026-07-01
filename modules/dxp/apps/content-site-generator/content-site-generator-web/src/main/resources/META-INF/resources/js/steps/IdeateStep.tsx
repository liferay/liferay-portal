/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {useState} from 'react';

import {EXAMPLES} from '../constants/examples';

interface IProps {
	error?: string;
	loading: boolean;
	onAnalyze: (prompt: string) => void;
}

export default function IdeateStep({error, loading, onAnalyze}: IProps) {
	const [prompt, setPrompt] = useState('');

	const hasText = !!prompt.trim().length;

	return (
		<div className="content-site-generator__ideate">
			<div className="content-site-generator__title">
				<h2>{Liferay.Language.get('what-do-you-want-to-create')}</h2>

				<p className="text-secondary">
					{Liferay.Language.get(
						'describe-the-content-you-want-to-generate'
					)}
				</p>
			</div>

			{error && (
				<ClayAlert
					displayType="danger"
					title={Liferay.Language.get('error')}
				>
					{error}
				</ClayAlert>
			)}

			<textarea
				aria-label={Liferay.Language.get('describe-your-content')}
				className="content-site-generator__textarea form-control"
				onChange={(event) => setPrompt(event.target.value)}
				placeholder={Liferay.Language.get(
					'describe-your-content-and-add-any-reference-materials-to-get-started'
				)}
				rows={5}
				value={prompt}
			/>

			<div className="content-site-generator__actions">
				<ClayButton
					disabled={!hasText || loading}
					displayType="primary"
					onClick={() => onAnalyze(prompt.trim())}
				>
					{Liferay.Language.get('analyze-and-configure')}

					{loading ? (
						<span
							aria-hidden="true"
							className="loading-animation loading-animation-sm ml-2"
						/>
					) : (
						<ClayIcon
							className="ml-2"
							spritemap={Liferay.Icons.spritemap}
							symbol="magic"
						/>
					)}
				</ClayButton>
			</div>

			<div className="content-site-generator__examples">
				<p className="font-weight-semi-bold">
					{Liferay.Language.get('try-an-example')}
				</p>

				<div className="list-group">
					{EXAMPLES.map((example) => (
						<button
							className="content-site-generator__example font-weight-semi-bold list-group-item list-group-item-action text-left"
							key={example.label}
							onClick={() => setPrompt(example.label)}
							type="button"
						>
							<ClayIcon
								className="mr-2 text-secondary"
								spritemap={Liferay.Icons.spritemap}
								symbol={example.icon}
							/>

							{example.label}
						</button>
					))}
				</div>
			</div>
		</div>
	);
}
