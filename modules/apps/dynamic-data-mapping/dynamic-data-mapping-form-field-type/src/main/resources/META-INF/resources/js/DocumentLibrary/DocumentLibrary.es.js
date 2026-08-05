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
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

const MIME_TO_EXTENSION = {
	'application/msword': 'doc',
	'application/vnd.ms-excel': 'xls',
	'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': 'xlsx',
	'application/vnd.openxmlformats-officedocument.wordprocessingml.document':
		'docx',
	'text/plain': 'txt',
};

const SUBMIT_EVENT = 'paginationControlsSubmitButtonClicked';

const getFileEntryId = (fileEntryValue) => {
	if (!fileEntryValue) {
		return null;
	}

	if (typeof fileEntryValue === 'string') {
		try {
			const fileEntry = JSON.parse(fileEntryValue);

			return fileEntry.fileEntryId || null;
		}
		catch (error) {
			console.error('Unable to parse JSON', fileEntryValue);

			return null;
		}
	}

	return fileEntryValue.fileEntryId || null;
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

const hasInvalidExtension = (value, acceptedExtensions) => {
	if (!value || !acceptedExtensions) {
		return false;
	}

	let fileEntryJSON;

	try {
		fileEntryJSON = JSON.parse(value);
	}
	catch (error) {
		return false;
	}

	let fileExtension = fileEntryJSON.extension?.toLowerCase();

	if (!fileExtension && fileEntryJSON.mimeType) {
		fileExtension =
			MIME_TO_EXTENSION[fileEntryJSON.mimeType.toLowerCase()] ||
			fileEntryJSON.mimeType.split('/')[1]?.toLowerCase();
	}

	if (!fileExtension) {
		return false;
	}

	const supportedExtensions = acceptedExtensions
		.split(',')
		.map((ext) => ext.trim().toLowerCase());

	return !supportedExtensions.includes(fileExtension);
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
	strings,
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
							aria-label={
								strings?.fileLabel ??
								Liferay.Language.get('file')
							}
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
								{strings?.selectLabel ??
									Liferay.Language.get('select')}
							</span>
						</ClayButton>
					</ClayInput.GroupItem>

					{transformedFileEntryTitle && (
						<ClayInput.GroupItem shrink>
							<ClayButton
								aria-label={
									strings?.unselectFileLabel ??
									Liferay.Language.get('unselect-file')
								}
								displayType="secondary"
								onClick={onClearButtonClicked}
								type="button"
							>
								{strings?.clearLabel ??
									Liferay.Language.get('clear')}
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
	strings,
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
						{strings?.selectLabel ?? Liferay.Language.get('select')}
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
							aria-label={
								strings?.unselectFileLabel ??
								Liferay.Language.get('unselect-file')
							}
							displayType="secondary"
							onClick={onClearButtonClicked}
							type="button"
						>
							{strings?.clearLabel ??
								Liferay.Language.get('clear')}
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

			{progress !== 0 && (
				<ClayProgressBar
					messages={{
						ariaLabelAttention:
							strings?.attentionValueIsAtLabel ??
							Liferay.Language.get('attention-value-is-at-x'),
						ariaLabelComplete:
							strings?.completeLabel ??
							Liferay.Language.get('complete'),
						ariaLabelInProgress:
							strings?.progressLabel ??
							Liferay.Language.get('progress-x'),
					}}
					value={progress}
				/>
			)}

			{message && <div className="form-feedback-item">{message}</div>}
		</div>
	);
};

function useFileLifecycle({
	currentValue,
	fileEntryDeleteURL,
	portletNamespace,
	readOnly,
	value,
}) {
	const originalFileEntryIdRef = useRef(null);
	const pendingDeletionsRef = useRef([]);
	const submitButtonClickedRef = useRef(false);

	const deleteFileEntry = useCallback(
		(fileEntryId, {beacon = false} = {}) => {
			if (!fileEntryId || !fileEntryDeleteURL) {
				return;
			}

			const formData = convertToFormData({
				[`${portletNamespace}oldFileEntryId`]: fileEntryId,
			});

			// Use sendBeacon on unload because async XHRs are cancelled when
			// the page tears down; foreground paths keep XHR for parity with
			// the rest of the field.

			if (beacon && navigator.sendBeacon) {
				navigator.sendBeacon(fileEntryDeleteURL, formData);

				return;
			}

			const request = new XMLHttpRequest();

			request.open('POST', fileEntryDeleteURL);
			request.send(formData);
		},
		[fileEntryDeleteURL, portletNamespace]
	);

	const stagePendingDeletion = (fileEntryId) => {
		if (!fileEntryId || pendingDeletionsRef.current.includes(fileEntryId)) {
			return;
		}

		pendingDeletionsRef.current = [
			...pendingDeletionsRef.current,
			fileEntryId,
		];
	};

	useEffect(() => {

		// Capture the file entry that was attached when the page loaded.
		// The unload handler later compares this against the *current* value
		// to decide whether the user replaced the file without saving — so it
		// must reflect the page-load state, not the latest edit. That is why
		// the deps array is empty.

		originalFileEntryIdRef.current = getFileEntryId(value);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		const handleBeforeUnload = () => {
			if (readOnly || submitButtonClickedRef.current) {
				return;
			}

			const currentFileEntryId = getFileEntryId(currentValue);
			const originalFileEntryId = originalFileEntryIdRef.current;

			if (
				currentFileEntryId &&
				currentFileEntryId !== originalFileEntryId
			) {
				deleteFileEntry(currentFileEntryId, {beacon: true});
			}

			pendingDeletionsRef.current.forEach((fileEntryId) => {
				if (fileEntryId !== originalFileEntryId) {
					deleteFileEntry(fileEntryId, {beacon: true});
				}
			});
		};

		window.addEventListener('beforeunload', handleBeforeUnload);
		Liferay.on('beforeNavigate', handleBeforeUnload);

		return () => {
			window.removeEventListener('beforeunload', handleBeforeUnload);
			Liferay.detach('beforeNavigate', handleBeforeUnload);
		};
	}, [currentValue, deleteFileEntry, readOnly]);

	useEffect(() => {
		const onSubmit = () => {
			pendingDeletionsRef.current.forEach((fileEntryId) =>
				deleteFileEntry(fileEntryId)
			);

			pendingDeletionsRef.current = [];

			submitButtonClickedRef.current = true;
		};

		Liferay.on(SUBMIT_EVENT, onSubmit);

		return () => {
			Liferay.detach(SUBMIT_EVENT, onSubmit);
		};
	}, [deleteFileEntry]);

	return {stagePendingDeletion};
}

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
	strings,
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

	const {stagePendingDeletion} = useFileLifecycle({
		currentValue,
		fileEntryDeleteURL,
		portletNamespace,
		readOnly,
		value,
	});

	const isSignedIn = Liferay.ThemeDisplay.isSignedIn();

	const hasCustomError =
		(!isSignedIn && !allowGuestUsers) ||
		maximumSubmissionLimitReached ||
		showUploadPermissionMessage;

	const getErrorMessages = (
		errorMessage,
		isSignedIn,
		objectFieldInvalidExtension
	) => {
		const errorMessages = [errorMessage];

		if (!allowGuestUsers && !isSignedIn) {
			errorMessages.push(
				strings?.signInRequiredErrorMessage ??
					Liferay.Language.get(
						'you-need-to-be-signed-in-to-edit-this-field'
					)
			);
		}
		else if (maximumSubmissionLimitReached) {
			errorMessages.push(
				strings?.maximumSubmissionLimitReachedErrorMessage ??
					Liferay.Language.get(
						'the-maximum-number-of-submissions-allowed-for-this-form-has-been-reached'
					)
			);
		}
		else if (showUploadPermissionMessage) {
			errorMessages.push(
				strings?.uploadPermissionErrorMessage ??
					Liferay.Language.get(
						'you-need-to-be-assigned-to-the-same-site-where-the-form-was-created-to-use-this-field'
					)
			);
		}
		else if (objectFieldInvalidExtension) {
			errorMessages.push(
				Liferay.Util.sub(
					strings?.invalidExtensionErrorMessage ??
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
		const invalidExtension = hasInvalidExtension(
			value,
			objectFieldAcceptedFileExtensions
		);

		setCurrentValue(invalidExtension ? null : value);
		setDisplayErrors(invalidExtension ? true : initialDisplayErrors);
		setErrorMessage(
			getErrorMessages(initialErrorMessage, isSignedIn, invalidExtension)
		);
		setValid(invalidExtension ? false : initialValid);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [
		initialDisplayErrors,
		initialErrorMessage,
		initialValid,
		objectFieldAcceptedFileExtensions,
		value,
	]);

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

	const isExceededUploadRequestSizeLimit = (fileSize) => {
		const uploadRequestSizeLimit =
			Liferay.PropsValues.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE;

		if (fileSize <= uploadRequestSizeLimit) {
			return false;
		}

		const errorMessage = sub(
			strings?.invalidFileSizeErrorMessage ??
				Liferay.Language.get(
					'please-enter-a-file-with-a-valid-file-size-no-larger-than-x'
				),
			[formatStorage(uploadRequestSizeLimit)]
		);

		handleGuestUploadFileChanged(errorMessage, {}, null);

		return true;
	};

	const handleGuestUploadFileChanged = (errorMessage, event, value) => {
		configureErrorMessage(errorMessage);

		setCurrentValue(value);

		onChange(event, value ? value : '{}');
	};

	const handleFieldChanged = (selectedItem) => {
		if (selectedItem?.value) {
			setCurrentValue(selectedItem.value);

			onChange(selectedItem, selectedItem.value);
		}
	};

	const handleSelectButtonClicked = (event) => {
		onFocus(event);

		openSelectionModal({
			onClose: () => onBlur(event),
			onSelect: handleFieldChanged,
			selectEventName: `${portletNamespace}selectDocumentLibrary`,
			title: sub(
				strings?.selectXLabel ?? Liferay.Language.get('select-x'),
				strings?.documentLabel ?? Liferay.Language.get('document')
			),
			url: itemSelectorURL,
		});
	};

	const handleOnClearButtonClicked = (event, isSignedIn) => {
		onFocus(event);

		stagePendingDeletion(getFileEntryId(currentValue));

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
		if (!event.target.files?.length) {
			return;
		}

		onFocus(event);

		stagePendingDeletion(getFileEntryId(currentValue));

		uploadFileEntry(event);
	};

	const uploadFileEntry = (event) => {
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
				[`${portletNamespace}oldFileEntryId`]: 0,
			})
		);
	};

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
					strings={strings}
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
					onSelectButtonClicked={handleSelectButtonClicked}
					placeholder={placeholder}
					readOnly={hasCustomError ? true : readOnly}
					strings={strings}
					value={currentValue || ''}
				/>
			)}
		</FieldBase>
	);
};

Main.displayName = 'DocumentLibrary';

export default Main;
