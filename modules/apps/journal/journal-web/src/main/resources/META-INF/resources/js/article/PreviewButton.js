/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {openModal, openToast} from 'frontend-js-web';
import React from 'react';

export default function PreviewButton({
	disabled,
	getPreviewURL,
	namespace,
	newArticle,
	saveAsDraftURL,
}) {
	return (
		<ClayButton
			aria-label={Liferay.Language.get(
				'a-draft-will-be-saved-before-displaying-the-preview'
			)}
			disabled={disabled}
			displayType="secondary"
			onClick={() => {
				const futureDate = new Date(new Date().getTime() + 1000);

				updateJournalInput({
					name: 'formDate',
					namespace,
					value: futureDate.getTime(),
				});

				const form = document.getElementById(`${namespace}fm1`);

				const formData = new FormData(form);

				const articleId = document.getElementById(
					`${namespace}articleId`
				);

				formData.append(
					`${namespace}cmd`,
					newArticle && !articleId.value ? 'add' : 'update'
				);

				return Liferay.Util.fetch(saveAsDraftURL, {
					body: formData,
					method: form.method,
				})
					.then((response) => response.json())
					.then((response) => {
						const {articleId, error, friendlyUrlMap, version} =
							response;

						if (error) {
							openToast({
								message: Liferay.Language.get(
									'web-content-could-not-be-previewed-due-to-an-unexpected-error-while-generating-the-draft'
								),
								title: Liferay.Language.get('error'),
								type: 'danger',
							});
						}
						else {
							updateJournalInput({
								name: 'formDate',
								namespace,
								value: Date.now().toString(),
							});

							updateJournalInput({
								name: 'articleId',
								namespace,
								value: articleId,
							});

							updateJournalInput({
								name: 'version',
								namespace,
								value: version,
							});

							Object.entries(friendlyUrlMap).forEach(
								([languageId, value]) => {
									updateJournalInput({
										name: `friendlyURL_${languageId}`,
										namespace,
										value,
									});
								}
							);

							openModal({
								title: Liferay.Language.get('preview'),
								url: getPreviewURL(response),
							});
						}
					})
					.catch(() => {
						openToast({
							message: Liferay.Language.get(
								'web-content-could-not-be-previewed-due-to-an-unexpected-error-while-generating-the-draft'
							),
							title: Liferay.Language.get('error'),
							type: 'danger',
						});
					});
			}}
			title={
				!disabled &&
				Liferay.Language.get(
					'a-draft-will-be-saved-before-displaying-the-preview'
				)
			}
		>
			{Liferay.Language.get('preview')}
		</ClayButton>
	);
}

function updateJournalInput({name, namespace, value}) {
	const input = document.getElementById(`${namespace}${name}`);

	if (input) {
		input.value = value;
	}
}
