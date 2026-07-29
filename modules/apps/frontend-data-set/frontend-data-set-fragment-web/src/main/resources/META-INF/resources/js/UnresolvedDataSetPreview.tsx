/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import './UnresolvedDataSetPreview.scss';

const SKELETON_COLUMNS = [1, 2, 3, 4];

const SKELETON_ROWS = [1, 2, 3, 4, 5];

interface IUnresolvedDataSetPreview {
	apiURL: string;
	hasUnmappedTokens?: boolean;
	hasUnresolvedContextTokens?: boolean;
}

export default function UnresolvedDataSetPreview({
	apiURL,
	hasUnmappedTokens = false,
	hasUnresolvedContextTokens = false,
}: IUnresolvedDataSetPreview) {
	const [showAlert, setShowAlert] = useState([true, true]);

	const messages: string[] = [];

	if (hasUnresolvedContextTokens) {
		messages.push(
			sub(
				Liferay.Language.get('unresolved-context-url-help-x'),
				Liferay.Language.get('preview-with')
			)
		);
	}

	if (hasUnmappedTokens || !messages.length) {
		messages.push(Liferay.Language.get('unmapped-url-help'));
	}

	return (
		<div className="unresolved-data-set-preview">
			{messages.map((message, messageIndex) => (
				<>
					{showAlert[messageIndex] && (
						<ClayAlert
							displayType="info"
							key={messageIndex}
							onClose={() =>
								setShowAlert(() =>
									showAlert.map(
										(value: boolean, alertIndex) =>
											alertIndex === messageIndex
												? false
												: value
									)
								)
							}
						>
							<p className="mb-0">{message}</p>
						</ClayAlert>
					)}
				</>
			))}

			<div className="border p-2 pl-3 rounded text-break">
				{(apiURL || '')
					.split(/(\{[^}]*\})/)
					.map((part, index) =>
						index % 2 ? (
							<strong key={index}>{part}</strong>
						) : (
							<React.Fragment key={index}>{part}</React.Fragment>
						)
					)}
			</div>

			<div className="border mt-3 rounded">
				<div className="align-items-center bg-light d-flex data-set-skeleton-management-bar p-3">
					<span className="data-set-skeleton-bar mr-3" />

					<span className="data-set-skeleton-bar flex-grow-1 mr-3" />

					<span className="data-set-skeleton-bar mr-3" />

					<span className="data-set-skeleton-bar" />
				</div>

				<table className="data-set-skeleton-table mb-0 table">
					<tbody>
						{SKELETON_ROWS.map((row) => (
							<tr key={row}>
								<td>
									<span className="data-set-skeleton-bar" />
								</td>

								{SKELETON_COLUMNS.map((column) => (
									<td key={column}>
										<span className="data-set-skeleton-bar" />
									</td>
								))}
							</tr>
						))}
					</tbody>
				</table>
			</div>
		</div>
	);
}
