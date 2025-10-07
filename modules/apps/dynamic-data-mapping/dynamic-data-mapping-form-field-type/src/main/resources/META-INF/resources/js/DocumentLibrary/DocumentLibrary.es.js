/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayProgressBar from '@clayui/progress-bar';
import {
	PagesVisitor,
	convertToFormData,
	useConfig,
	useFormState,
} from 'data-engine-js-components-web';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import {openSelectionModal} from 'frontend-js-components-web';
import {formatStorage, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

const CardItem = ({fileEntryTitle, fileEntryURL}) => {
	return (
		<ClayCard horizontal>
			<ClayCard.Body>
				<div className="card-col-content card-col-gutters">
					<div className="h4 text-truncate" title={fileEntryTitle}>
						{fileEntryTitle}
					</div>
				</div>

				<div className="card-col-field">
					<a download={fileEntryTitle} href={fileEntryURL}>
						<ClayIcon symbol="download" />
					</a>
				</div>
			</ClayCard.Body>
		</ClayCard>
	);
};

const getValue = (value) => {
	if (!value) {
		return '';
	}

	if (typeof value === 'string') {
		return value;
	}

	return JSON.stringify(value);
};

function transformFileEntryProperties({fileEntryTitle, value}) {
	if (value && typeof value === 'string') {
		try {
			const fileEntry = JSON.parse(value);

			fileEntryTitle = fileEntry.title;
		}
		catch (error) {
			console.warn('Unable to parse JSON', value);
		}
	}

	return value && fileEntryTitle !== ''
		? [fileEntryTitle]
		: fileEntryTitle === ''
			? [value.title]
			: [];
}

const DocumentLibrary = ({
	accessibleProps,
	editingLanguageId,
	fileEntryTitle = '',
	fileEntryURL = '',
	id,
	message,
	name,
	onClearButtonClicked,
	onSelectButtonClicked,
	placeholder,
	readOnly,
	value,
}) => {
	const [transformedFileEntryTitle] = useMemo(
		() =>
			transformFileEntryProperties({
				fileEntryTitle,
				value,
			}),
		[fileEntryTitle, value]
	);

	return (
		<div className="liferay-ddm-form-field-document-library">
			{transformedFileEntryTitle && readOnly ? (
				<CardItem
					fileEntryTitle={transformedFileEntryTitle}
					fileEntryURL={fileEntryURL}
				/>
			) : (
				<ClayInput.Group>
					<ClayInput.GroupItem prepend>
						<ClayInput
							aria-label={Liferay.Language.get('file')}
							className="bg-light field"
							dir={Liferay.Language.direction[editingLanguageId]}
							disabled={readOnly}
							id={`${name}inputFile`}
							lang={editingLanguageId}
							onClick={onSelectButtonClicked}
							readonly="true"
							tabindex="-1"
							value={transformedFileEntryTitle || ''}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ClayButton
							{...accessibleProps}
							className="select-button"
							disabled={readOnly}
							displayType="secondary"
							id={name}
							onClick={onSelectButtonClicked}
						>
							<span className="lfr-btn-label">
								{Liferay.Language.get('select')}
							</span>
						</ClayButton>
					</ClayInput.GroupItem>

					{transformedFileEntryTitle && (
						<ClayInput.GroupItem shrink>
							<ClayButton
								aria-label={Liferay.Language.get(
									'unselect-file'
								)}
								displayType="secondary"
								onClick={onClearButtonClicked}
								type="button"
							>
								{Liferay.Language.get('clear')}
							</ClayButton>
						</ClayInput.GroupItem>
					)}
				</ClayInput.Group>
			)}

			<input
				id={id}
				name={name}
				placeholder={placeholder}
				type="hidden"
				value={getValue(value)}
			/>

			{message && <div className="form-feedback-item">{message}</div>}
		</div>
	);
};

const GuestUploadFile = ({
	fileEntryTitle = '',
	id,
	message,
	name,
	onClearButtonClicked,
	onUploadSelectButtonClicked,
	placeholder,
	progress,
	readOnly,
	value,
}) => {
	const [transformedFileEntryTitle] = useMemo(
		() =>
			transformFileEntryProperties({
				fileEntryTitle,
				value,
			}),
		[fileEntryTitle, value]
	);

	return (
		<div className="liferay-ddm-form-field-document-library">
			<ClayInput.Group>
				<ClayInput.GroupItem prepend>
					<ClayInput
						className="bg-light"
						disabled={readOnly}
						id={name}
						onClick={onUploadSelectButtonClicked}
						type="text"
						value={transformedFileEntryTitle || ''}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem append shrink>
					<label
						className={
							'btn btn-secondary select-button' +
							(transformedFileEntryTitle
								? ' clear-button-upload-on'
								: '') +
							(readOnly ? ' disabled' : '')
						}
						htmlFor={`${name}inputFileGuestUpload`}
					>
						{Liferay.Language.get('select')}
					</label>

					<input
						className="input-file"
						disabled={readOnly}
						id={`${name}inputFileGuestUpload`}
						onChange={onUploadSelectButtonClicked}
						type="file"
					/>
				</ClayInput.GroupItem>

				{transformedFileEntryTitle && (
					<ClayInput.GroupItem shrink>
						<ClayButton
							aria-label={Liferay.Language.get('unselect-file')}
							displayType="secondary"
							onClick={onClearButtonClicked}
							type="button"
						>
							{Liferay.Language.get('clear')}
						</ClayButton>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>

			<input
				id={id}
				name={name}
				placeholder={placeholder}
				type="hidden"
				value={getValue(value)}
			/>

			{progress !== 0 && <ClayProgressBar value={progress} />}

			{message && <div className="form-feedback-item">{message}</div>}
		</div>
	);
};

const Main = ({
	_onBlur,
	_onFocus,
	allowGuestUsers,
	displayErrors: initialDisplayErrors = false,
	editingLanguageId,
	errorMessage: initialErrorMessage,
	fieldName,
	fileEntryDeleteURL,
	fileEntryTitle,
	fileEntryURL,
	guestUploadURL,
	id,
	itemSelectorURL,
	maximumRepetitions,
	maximumSubmissionLimitReached,
	message,
	name,
	objectFieldAcceptedFileExtensions,
	onBlur,
	onChange,
	onFocus,
	placeholder,
	readOnly,
	showUploadPermissionMessage,
	valid: initialValid,
	value = '{}',
	...otherProps
}) => {
	const {portletNamespace} = useConfig();
	const {pages} = useFormState();

	const [currentValue, setCurrentValue] = useState(value);
	const [errorMessage, setErrorMessage] = useState(initialErrorMessage);
	const [displayErrors, setDisplayErrors] = useState(initialDisplayErrors);
	const [valid, setValid] = useState(initialValid);
	const [progress, setProgress] = useState(0);
	const [submitButtonClicked, setSubmitButtonClicked] = useState(false);

	const isSignedIn = Liferay.ThemeDisplay.isSignedIn();

	const getErrorMessages = (
		errorMessage,
		isSignedIn,
		objectFieldInvalidExtension
	) => {
		const errorMessages = [errorMessage];

		if (!allowGuestUsers && !isSignedIn) {
			errorMessages.push(
				Liferay.Language.get(
					'you-need-to-be-signed-in-to-edit-this-field'
				)
			);
		}
		else if (maximumSubmissionLimitReached) {
			errorMessages.push(
				Liferay.Language.get(
					'the-maximum-number-of-submissions-allowed-for-this-form-has-been-reached'
				)
			);
		}
		else if (showUploadPermissionMessage) {
			errorMessages.push(
				Liferay.Language.get(
					'you-need-to-be-assigned-to-the-same-site-where-the-form-was-created-to-use-this-field'
				)
			);
		}
		else if (objectFieldInvalidExtension) {
			errorMessages.push(
				Liferay.Util.sub(
					Liferay.Language.get(
						'please-enter-a-file-with-a-valid-extension-x'
					),
					objectFieldAcceptedFileExtensions
				)
			);
		}

		return errorMessages.join(' ');
	};

	useEffect(() => {
		if ((!allowGuestUsers && !isSignedIn) || showUploadPermissionMessage) {
			const ddmFormUploadPermissionMessage = document.querySelector(
				`.ddm-form-upload-permission-message`
			);

			if (ddmFormUploadPermissionMessage) {
				ddmFormUploadPermissionMessage.classList.remove('hide');
			}
		}
	}, [allowGuestUsers, isSignedIn, showUploadPermissionMessage]);

	useEffect(() => {
		const objectFieldInvalidExtension =
			isObjectFieldInvalidExtension(value);

		setCurrentValue(objectFieldInvalidExtension ? null : value);
		setDisplayErrors(
			objectFieldInvalidExtension ? true : initialDisplayErrors
		);
		setErrorMessage(
			getErrorMessages(
				initialErrorMessage,
				isSignedIn,
				objectFieldInvalidExtension
			)
		);
		setValid(objectFieldInvalidExtension ? false : initialValid);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [initialDisplayErrors, initialErrorMessage, initialValid, value]);

	const checkMaximumRepetitions = () => {
		const visitor = new PagesVisitor(pages);

		let repetitionsCounter = 0;

		visitor.mapFields(
			(field) => {
				if (fieldName === field.fieldName) {
					repetitionsCounter++;
				}
			},
			true,
			true
		);

		return repetitionsCounter === maximumRepetitions;
	};

	const handleFieldChanged = (selectedItem) => {
		if (selectedItem?.value) {
			setCurrentValue(selectedItem.value);

			onChange(selectedItem, selectedItem.value);
		}
	};

	const handleSelectButtonClicked = ({portletNamespace}, event) => {
		onFocus(event);

		openSelectionModal({
			onClose: () => onBlur(event),
			onSelect: handleFieldChanged,
			selectEventName: `${portletNamespace}selectDocumentLibrary`,
			title: sub(
				Liferay.Language.get('select-x'),
				Liferay.Language.get('document')
			),
			url: itemSelectorURL,
		});
	};

	const configureErrorMessage = (message) => {
		setErrorMessage(message);

		const enable = message ? true : false;

		setDisplayErrors(enable);
		setValid(!enable);
	};

	const disableSubmitButton = (disable = true) => {
		const ddmFormSubmitButton = document.getElementById('ddm-form-submit');

		if (ddmFormSubmitButton) {
			ddmFormSubmitButton.disabled = disable;
		}
	};

	const handleGuestUploadFileChanged = (errorMessage, event, value) => {
		configureErrorMessage(errorMessage);

		setCurrentValue(value);

		onChange(event, value ? value : '{}');
	};

	const isExceededUploadRequestSizeLimit = (fileSize) => {
		const uploadRequestSizeLimit =
			Liferay.PropsValues.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE;

		if (fileSize <= uploadRequestSizeLimit) {
			return false;
		}

		const errorMessage = sub(
			Liferay.Language.get(
				'please-enter-a-file-with-a-valid-file-size-no-larger-than-x'
			),
			[formatStorage(uploadRequestSizeLimit)]
		);

		handleGuestUploadFileChanged(errorMessage, {}, null);

		return true;
	};

	const isObjectFieldInvalidExtension = (value) => {
		if (!value || !objectFieldAcceptedFileExtensions) {
			return false;
		}

		const fileEntryJSON = JSON.parse(value);

		let fileExtension = fileEntryJSON.extension?.toLowerCase();

		if (!fileExtension && fileEntryJSON.mimeType) {
			const mimeToExt = {
				'application/msword': 'doc',
				'application/vnd.ms-excel': 'xls',
				'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet':
					'xlsx',
				'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
					'docx',
				'text/plain': 'txt',
			};

			fileExtension =
				mimeToExt[fileEntryJSON.mimeType.toLowerCase()] ||
				fileEntryJSON.mimeType.split('/')[1]?.toLowerCase();
		}

		if (!fileExtension) {
			return false;
		}

		const supportedExtensions = objectFieldAcceptedFileExtensions
			.split(', ')
			.map((ext) => ext.trim().toLowerCase());

		return !supportedExtensions.includes(fileExtension);
	};

	const deleteFileEntry = useCallback(() => {
		const request = new XMLHttpRequest();

		let oldFileEntryId = 0;

		try {
			const fileEntry = JSON.parse(value);

			oldFileEntryId = fileEntry.fileEntryId;
		}
		catch (error) {
			console.error('Unable to parse JSON', value);
		}

		request.open('POST', fileEntryDeleteURL);
		request.send(
			convertToFormData({
				[`${portletNamespace}oldFileEntryId`]: oldFileEntryId,
			})
		);
	}, [fileEntryDeleteURL, portletNamespace, value]);

	const handleOnClearButtonClicked = (event, isSignedIn) => {
		onFocus(event);

		deleteFileEntry();

		setCurrentValue(null);

		onChange(event, '{}');

		if (!isSignedIn) {
			const guestUploadInput = document.getElementById(
				`${name}inputFileGuestUpload`
			);

			if (guestUploadInput) {
				guestUploadInput.value = '';
			}

			onBlur(event);
		}
	};

	const handleUploadSelectButtonClicked = (event, currentValue) => {
		onFocus(event);

		let oldFileEntryId = 0;

		if (currentValue) {
			try {
				const fileEntry = JSON.parse(currentValue);

				oldFileEntryId = fileEntry.fileEntryId;

				uploadFileEntry(event, oldFileEntryId);
			}
			catch (error) {
				console.error('Unable to parse JSON', currentValue);
			}
		}
		else {
			uploadFileEntry(event, oldFileEntryId);
		}
	};

	const uploadFileEntry = (event, oldFileEntryId) => {
		const file = event.target.files[0];

		if (isExceededUploadRequestSizeLimit(file.size)) {
			onBlur(event);

			return;
		}

		const request = new XMLHttpRequest();

		request.upload.addEventListener('progress', (event) => {
			disableSubmitButton();

			setCurrentValue(null);

			setProgress(Math.round((event.loaded * 100) / event.total));
		});
		request.addEventListener('readystatechange', (event) => {
			if (request.readyState === 4) {
				disableSubmitButton(false);

				let response;

				try {
					response = JSON.parse(request.responseText);
				}
				catch (error) {
					response = request.responseText;
				}

				if (response.success) {
					handleGuestUploadFileChanged(
						'',
						event,
						JSON.stringify(response.file)
					);
				}
				else {
					handleGuestUploadFileChanged(
						response.error.message,
						event,
						null
					);
				}

				setProgress(0);
			}
		});

		request.open('POST', guestUploadURL);
		request.send(
			convertToFormData({
				[`${portletNamespace}file`]: file,
				[`${portletNamespace}oldFileEntryId`]: oldFileEntryId,
			})
		);
	};

	const hasCustomError =
		(!isSignedIn && !allowGuestUsers) ||
		maximumSubmissionLimitReached ||
		showUploadPermissionMessage;

	useEffect(() => {
		window.onbeforeunload = function () {
			if (!submitButtonClicked) {
				deleteFileEntry();
			}
		};

		return () => {
			window.onbeforeunload = null;
		};
	}, [deleteFileEntry, submitButtonClicked]);

	useEffect(() => {
		Liferay.on(
			'paginationControlsSubmitButtonClicked',

			() => {
				setSubmitButtonClicked(true);
			}
		);

		return () => {
			Liferay.detach('paginationControlsSubmitButtonClicked');
		};
	}, []);

	return (
		<FieldBase
			{...otherProps}
			displayErrors={hasCustomError ? true : displayErrors}
			errorMessage={errorMessage}
			fieldName={fieldName}
			id={id}
			name={name}
			overMaximumRepetitionsLimit={
				maximumRepetitions > 0 ? checkMaximumRepetitions() : false
			}
			readOnly={hasCustomError ? true : readOnly}
			valid={hasCustomError ? false : valid}
		>
			{allowGuestUsers && !isSignedIn ? (
				<GuestUploadFile
					fileEntryTitle={fileEntryTitle}
					id={id}
					message={message}
					name={name}
					onBlur={onBlur}
					onClearButtonClicked={(event) => {
						handleOnClearButtonClicked(event, value, isSignedIn);
					}}
					onFocus={onFocus}
					onUploadSelectButtonClicked={(event) =>
						handleUploadSelectButtonClicked(event, currentValue)
					}
					placeholder={placeholder}
					progress={progress}
					readOnly={hasCustomError ? true : readOnly}
					value={currentValue || ''}
				/>
			) : (
				<DocumentLibrary
					accessibleProps={{
						...((errorMessage || otherProps.tip) && {
							'aria-describedby': `${id ?? name}_fieldFeedback`,
						}),
						...(displayErrors && !valid && {'aria-invalid': true}),
						'aria-required': otherProps.required,
					}}
					editingLanguageId={editingLanguageId}
					fileEntryTitle={fileEntryTitle}
					fileEntryURL={fileEntryURL}
					id={id}
					message={message}
					name={name}
					onClearButtonClicked={(event) => {
						handleOnClearButtonClicked(event, value, isSignedIn);
					}}
					onSelectButtonClicked={(event) =>
						handleSelectButtonClicked(
							{
								itemSelectorURL,
								portletNamespace,
							},
							event
						)
					}
					placeholder={placeholder}
					readOnly={hasCustomError ? true : readOnly}
					value={currentValue || ''}
				/>
			)}
		</FieldBase>
	);
};

Main.displayName = 'DocumentLibrary';

export default Main;
