/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {Container} from '@clayui/layout';
import classNames from 'classnames';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import {fetch, getOpener} from 'frontend-js-web';
import React, {FormEvent, useState} from 'react';

import {ErrorMessage} from './ErrorMessage';
import {FormFooter} from './FormFooter';
import {FormImage} from './FormImage';
import {ImagesResult} from './ImagesResult';
import {LoadingMessage} from './LoadingMessage';

interface Props {
	eventName?: string;
	getGenerationsURL: string;
	imageSizes: string[];
	learnResources: AICreatorModalLearnResources;
	portletNamespace: string;
	uploadGenerationsURL: string;
}

type AICreatorModalLearnResources = {
	'ai-creator-openai-web': {
		general: {
			[key: string]: {
				message: string;
				url: string;
			};
		};
	};
};

type Generation = {
	expirationTime: number;
	signature: string;
	url: string;
};

type RequestStatus =
	| {type: 'adding'}
	| {type: 'idle'}
	| {type: 'loading'}
	| {errorMessage: string; type: 'error'};

function getBlob(dataURL: string) {
	const [header, data] = dataURL.split(',');

	return new Blob(
		[Uint8Array.from(atob(data), (character) => character.charCodeAt(0))],
		{type: header.slice('data:'.length, header.indexOf(';'))}
	);
}

export default function AICreatorImageModal({
	eventName,
	getGenerationsURL,
	imageSizes,
	learnResources,
	portletNamespace,
	uploadGenerationsURL,
}: Props) {
	const closeModal = () => {
		getOpener().Liferay.fire('closeModal');
	};

	const [status, setStatus] = useState<RequestStatus>({type: 'idle'});
	const [generations, setGenerations] = useState<Generation[] | null>(null);

	const [selectedImages, setSelectedImages] = useState<number[]>([]);

	const setErrorStatus = (
		errorMessage = Liferay.Language.get('an-unexpected-error-occurred')
	) => {
		setStatus({
			errorMessage,
			type: 'error',
		});
	};

	const onAdd = () => {
		if (selectedImages.length) {
			setStatus({type: 'adding'});

			const addedImages: string[] = [];

			Promise.all(
				selectedImages.map((index) => {
					const generation = generations![index];

					const formData = new FormData();

					formData.append(
						`${portletNamespace}expirationTime`,
						String(generation.expirationTime)
					);
					formData.append(
						`${portletNamespace}image`,
						getBlob(generation.url),
						'image.png'
					);
					formData.append(
						`${portletNamespace}signature`,
						generation.signature
					);

					return fetch(uploadGenerationsURL, {
						body: formData,
						method: 'POST',
					})
						.then((response) => response.json())
						.then((json) => {
							if (json.success) {
								addedImages.push(generation.signature);
							}
							else {
								setErrorStatus(json.error?.message);
							}
						})
						.catch((error) => {
							if (process.env.NODE_ENV === 'development') {
								console.error(error);
							}

							setErrorStatus();
						});
				})
			).then(() => {
				setStatus({type: 'idle'});

				getOpener().Liferay.fire(eventName, {
					selectedItems: addedImages,
				});
			});
		}
	};

	const onSelectedChange = (index: number) => {
		const newSelectedImages = [...selectedImages];

		if (newSelectedImages.includes(index)) {
			newSelectedImages.splice(newSelectedImages.indexOf(index), 1);
		}
		else {
			newSelectedImages.push(index);
		}

		setSelectedImages(newSelectedImages);
	};

	const onSubmit = (event: FormEvent) => {
		event.preventDefault();
		setStatus({type: 'loading'});

		const formData = new FormData(event.target as HTMLFormElement);
		const url = new URL(window.location.href);

		formData.append(
			`${portletNamespace}languageId`,
			url.searchParams.get(`${portletNamespace}languageId`)!
		);

		fetch(getGenerationsURL, {
			body: formData,
			method: 'POST',
		})
			.then((response) => response.json())
			.then((json) => {
				if (json.error) {
					setErrorStatus(json.error.message);
				}
				else if (json.generations?.content) {
					setGenerations(json.generations.content);
					setSelectedImages([]);
					setStatus({type: 'idle'});
				}
				else {
					setErrorStatus();
				}
			})
			.catch((error) => {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}

				setErrorStatus();
			});
	};

	return (
		<div className="h-100" tabIndex={-1}>
			{status.type === 'loading' ? <LoadingMessage /> : null}

			<ClayForm
				className={classNames('h-100', {
					'sr-only': status.type === 'loading',
				})}
				onSubmit={onSubmit}
			>
				<fieldset
					className="d-flex flex-column h-100"
					disabled={status.type === 'loading'}
				>
					{status.type === 'error' ? (
						<ErrorMessage message={status.errorMessage} />
					) : null}

					<Container
						className="c-p-4 flex-grow-1 overflow-auto"
						fluid
					>
						<FormImage
							imageSizes={imageSizes}
							portletNamespace={portletNamespace}
						/>

						{generations && (
							<ImagesResult
								imagesURL={generations.map(
									(generation) => generation.url
								)}
								onSelectedChange={onSelectedChange}
								selectedImages={selectedImages}
							/>
						)}

						<ClayForm.Group className="c-mb-0">
							<LearnResourcesContext.Provider
								value={learnResources}
							>
								<LearnMessage
									resource="ai-creator-openai-web"
									resourceKey="general"
								/>
							</LearnResourcesContext.Provider>
						</ClayForm.Group>
					</Container>

					<div className="d-flex flex-column flex-shrink-0">
						<FormFooter
							addButtonLabel={Liferay.Language.get(
								'add-selected'
							)}
							disableAddButton={Boolean(
								!selectedImages?.length ||
									status.type === 'adding'
							)}
							disableRetryButton={Boolean(
								status.type === 'adding'
							)}
							onAdd={onAdd}
							onClose={closeModal}
							showAddButton={Boolean(generations?.length)}
							showCreateButton={!generations}
							showRetryButton={Boolean(generations?.length)}
						/>
					</div>
				</fieldset>
			</ClayForm>
		</div>
	);
}
