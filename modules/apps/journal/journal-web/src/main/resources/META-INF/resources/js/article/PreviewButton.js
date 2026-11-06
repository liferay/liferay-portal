/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {openModal, openToast} from 'frontend-js-components-web';
import React, {useEffect, useRef, useState} from 'react';

export default function PreviewButton({
	disabled,
	getPreviewURL,
	hasFormChanges,
	markFormSaved,
	namespace,
	newArticle,
	saveAsDraftURL,
}) {
	const [articleId, setArticleId] = useState(
		document.getElementById(`${namespace}articleId`).value
	);

	const lockRef = useRef(null);

	useEffect(() => {
		const updateArticleId = ({articleId}) => {
			setArticleId(articleId);

			document.getElementById(
				`${namespace}jakarta-portlet-action`
			).value = '/journal/update_article';
		};

		Liferay.on('asyncFormSubmission', updateArticleId);

		Liferay.componentReady(`${namespace}publishing`).then((publishLock) => {
			lockRef.current = publishLock;
		});

		return () => {
			Liferay.detach('asyncFormSubmission', updateArticleId);
		};
	}, [namespace]);

	const enableDraft = !!articleId;

	return (
		<ClayButton
			aria-label={Liferay.Language.get(
				'a-draft-will-be-saved-before-displaying-the-preview'
			)}
			disabled={disabled || !enableDraft}
			displayType="secondary"
			onClick={() => {
				if (lockRef.current?.isLocked()) {
					return;
				}

				lockRef.current?.lock();

				const futureDate = new Date(new Date().getTime() + 1000);

				updateJournalInput({
					name: 'formDate',
					namespace,
					value: futureDate.getTime(),
				});

				const form = document.getElementById(`${namespace}fm1`);

				const formData = new FormData(form);

				let cmd = 'update';

				if (newArticle && !articleId) {
					cmd = 'add';
				}
				else if (!hasFormChanges()) {
					cmd = 'preview';
				}

				formData.append(`${namespace}cmd`, cmd);

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

							markFormSaved();

							openModal({
								title: Liferay.Language.get('preview'),
								url: getPreviewURL(response),
							});
						}
					})
					.catch((error) => {
						console.error(error);

						openToast({
							message: Liferay.Language.get(
								'web-content-could-not-be-previewed-due-to-an-unexpected-error-while-generating-the-draft'
							),
							title: Liferay.Language.get('error'),
							type: 'danger',
						});
					})
					.finally(() => {
						lockRef.current?.unlock();
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
