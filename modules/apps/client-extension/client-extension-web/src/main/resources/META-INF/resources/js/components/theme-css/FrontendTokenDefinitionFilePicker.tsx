/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert, {DisplayType} from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import classNames from 'classnames';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import {fetch, objectToFormData} from 'frontend-js-web';
import React, {ChangeEvent, useEffect, useRef, useState} from 'react';

import {disableFormSubmitButton} from '../../clientExtensionUtil';

type TFeedback = {displayType: DisplayType; message: string};

const EMPTY_FEEDBACK: TFeedback = {
	displayType: 'info',
	message: '',
};

const readInputFile = (file: File) => {
	const fileReader = new FileReader();

	return new Promise<string>((resolve) => {
		fileReader.onload = () => {
			resolve(fileReader.result as string);
		};

		fileReader.readAsText(file);
	});
};

const EMPTY_JSON_STRING = '{}';

interface IProps {
	disabled: boolean;
	frontendTokenDefinitionJSON: string;
	learnResources: object;
	portletNamespace: string;
}

const FrontendTokenDefinitionFilePicker = ({
	disabled,
	frontendTokenDefinitionJSON: initialFrontendTokenDefinitionJSON,
	learnResources,
	portletNamespace,
}: IProps) => {
	const [feedback, setFeedback] = useState(EMPTY_FEEDBACK);
	const [alertRole, setAlertRole] = useState<'alert' | null>(null);
	const [isValidatingJSON, setIsValidatingJSON] = useState(false);
	const [frontendTokenDefinitionJSON, setFrontendTokenDefinitionJSON] =
		useState<string | undefined>(
			initialFrontendTokenDefinitionJSON !== ''
				? initialFrontendTokenDefinitionJSON
				: undefined
		);

	const fileInputRef = useRef<HTMLInputElement>();
	const selectFileButtonRef = useRef<HTMLButtonElement>();

	const clearSelection = () => {
		setFeedback(EMPTY_FEEDBACK);

		setFrontendTokenDefinitionJSON('');

		disableFormSubmitButton(false, portletNamespace);

		selectFileButtonRef.current?.focus();

		if (fileInputRef.current) {
			fileInputRef.current.value = '';
		}
	};

	const validateFrontendTokenDefinitionFile = async (
		file: File,
		disableSubmitButtonOnError: boolean = true
	) => {
		return await fetch('/o/frontend-token-definition/validate-file', {
			body: objectToFormData({file}),
			method: 'POST',
		})
			.then(async (response) => {
				const data = await response.json();

				if (response.ok) {
					setFeedback({
						displayType: 'success',
						message: data.message,
					});

					disableFormSubmitButton(false, portletNamespace);
				}
				else {
					throw data.message;
				}
			})
			.catch((error) => {
				if (disableSubmitButtonOnError) {
					disableFormSubmitButton(true, portletNamespace);
				}

				setFeedback({
					displayType: 'danger',
					message:
						typeof error === 'string'
							? error
							: Liferay.Language.get(
									'your-upload-failed-to-complete'
								),
				});

				if (fileInputRef.current) {
					fileInputRef.current.value = '';
				}
			});
	};

	const handleFileInputChange = async ({
		target,
	}: ChangeEvent<HTMLInputElement>) => {
		setIsValidatingJSON(true);

		setFrontendTokenDefinitionJSON('');

		if (!alertRole) {
			setAlertRole('alert');
		}

		setFeedback({
			displayType: 'info',
			message: Liferay.Language.get(
				'the-frontend-token-definition-json-file-is-being-uploaded-and-validated'
			),
		});

		const filePath = target.value;

		if (!filePath.endsWith('.json')) {
			setFeedback({
				displayType: 'danger',
				message: Liferay.Language.get(
					'the-format-is-invalid.-please-upload-a-valid-frontend-token-definition-json-file'
				),
			});

			disableFormSubmitButton(true, portletNamespace);
		}
		else if (target.files === null) {
			setFeedback({
				displayType: 'danger',
				message: Liferay.Language.get('your-upload-failed-to-complete'),
			});

			disableFormSubmitButton(true, portletNamespace);
		}
		else {
			const file = target.files[0];

			await validateFrontendTokenDefinitionFile(file);

			setFrontendTokenDefinitionJSON(
				(await readInputFile(file)) || EMPTY_JSON_STRING
			);
		}

		setIsValidatingJSON(false);
	};

	useEffect(() => {
		if (frontendTokenDefinitionJSON) {
			validateFrontendTokenDefinitionFile(
				new File(
					[frontendTokenDefinitionJSON],
					'frontend-token-definition.json',
					{
						type: 'application/json',
					}
				),
				false
			);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const fileInputId = `${portletNamespace}file`;
	const frontendTokenDefinitionDescriptionId = `${portletNamespace}frontendTokenDefinitionDescription`;

	return (
		<LearnResourcesContext.Provider value={learnResources}>
			<div
				className={disabled ? 'disabled' : ''}
				id="frontendTokenDefinitionFilePicker"
			>
				<label
					aria-describedby={frontendTokenDefinitionDescriptionId}
					aria-disabled={disabled}
					className={classNames('d-block', {disabled})}
					htmlFor={fileInputId}
					tabIndex={0}
				>
					{Liferay.Language.get(
						'frontend-token-definition-json-file-upload'
					)}
				</label>

				<Text
					as="p"
					color="secondary"
					id={frontendTokenDefinitionDescriptionId}
					size={3}
				>
					{`${Liferay.Language.get(
						'the-frontend-token-definition-is-a-json-file-that-allows-you-to-contribute-your-own-frontend-tokens'
					)} `}

					<LearnMessage
						resource="client-extension-web"
						resourceKey="frontend-token-definitions"
					/>
				</Text>

				<ClayInput
					accept=".json"
					className="d-none"
					disabled={disabled}
					id={fileInputId}
					name={fileInputId}
					onChange={handleFileInputChange}

					// @ts-ignore

					ref={fileInputRef}
					type="file"
				/>

				<ClayInput
					id={`${portletNamespace}frontendTokenDefinitionJSON`}
					name={`${portletNamespace}frontendTokenDefinitionJSON`}
					type="hidden"
					value={frontendTokenDefinitionJSON}
				/>

				<div className="my-2">
					<ClayButton
						disabled={isValidatingJSON || disabled}
						displayType="secondary"
						onClick={() => fileInputRef.current?.click()}

						// @ts-ignore

						ref={selectFileButtonRef}
					>
						{!frontendTokenDefinitionJSON
							? Liferay.Language.get('select-file')
							: Liferay.Language.get('replace-file')}
					</ClayButton>

					<div className="inline-item">
						{!frontendTokenDefinitionJSON && !isValidatingJSON && (
							<small className="inline-item inline-item-after">
								<strong>
									{Liferay.Language.get(
										'no-file-is-selected'
									)}
								</strong>
							</small>
						)}

						{frontendTokenDefinitionJSON && (

							// @ts-ignore

							<ClayButtonWithIcon
								borderless
								className="ml-2"
								disabled={disabled}
								displayType="secondary"
								monospaced
								onClick={clearSelection}
								symbol="times-circle-full"
								title={Liferay.Language.get('unselect-file')}
							/>
						)}
					</div>
				</div>

				<ClayAlert
					displayType={feedback.displayType}
					role={alertRole}
					style={{display: feedback.message ? 'block' : 'none'}}
					title={feedback.message}
					variant="feedback"
				/>
			</div>
		</LearnResourcesContext.Provider>
	);
};

export default FrontendTokenDefinitionFilePicker;
