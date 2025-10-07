/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {usePrevious} from '@liferay/frontend-js-react-web';
import {ReactFieldBase as FieldBase} from 'dynamic-data-mapping-form-field-type/api';
import {openSelectionModal} from 'frontend-js-components-web';
import {addParams, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {useSyncValue} from '../hooks/useSyncValue.es';

const defaultValue = {description: '', title: '', url: ''};

const ImagePicker = ({
	accessibleProps,
	editingLanguageId,
	id,
	inputValue,
	itemSelectorURL,
	message,
	name,
	onBlur,
	onClearClick,
	onDescriptionChange,
	onFieldChanged,
	onFocus,
	portletNamespace,
	readOnly,
}) => {
	const [imageValues, setImageValues] = useSyncValue(inputValue);
	const [modalVisible, setModalVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setModalVisible(false),
	});

	const [selectedImageId, setSelectedImageId] = useState(
		inputValue?.fileEntryId
	);

	const dispatchValue = ({clear, value}, callback = () => {}) =>
		setImageValues((oldValues) => {
			let mergedValues = {...oldValues, ...value};
			mergedValues = clear ? {} : mergedValues;
			mergedValues.alt = mergedValues.description || '';

			callback(mergedValues);

			return mergedValues;
		});

	const handleFieldChanged = (selectedItem) => {
		if (selectedItem?.value) {
			const selectedImage = new Image();
			const selectedItemValue = JSON.parse(selectedItem.value);

			setSelectedImageId(selectedItemValue.fileEntryId);

			selectedImage.addEventListener('load', (event) => {
				const {
					target: {height, width},
				} = event;

				const imageData = {
					...{
						description: '',
						event,
						height,
						title: '',
						url: '',
						width,
					},
					...selectedItemValue,
				};

				dispatchValue({value: imageData}, (mergedValues) =>
					onFieldChanged(mergedValues)
				);
			});

			selectedImage.addEventListener('error', (event) => {
				const imageData = {
					...{
						description: '',
						event,
						height: 0,
						title: '',
						url: '',
						width: 0,
					},
					...selectedItemValue,
				};

				dispatchValue({value: imageData}, (mergedValues) =>
					onFieldChanged(mergedValues)
				);
			});

			selectedImage.src = selectedItemValue.url;
		}
	};

	const handleItemSelectorTriggerClick = (event) => {
		event.preventDefault();

		onFocus(event);

		let url = itemSelectorURL;

		if (Liferay.FeatureFlags['LPS-153332']) {
			url = addParams(
				`selectedItemIds=${selectedImageId}`,
				itemSelectorURL
			);
		}

		openSelectionModal({
			onClose: () => {
				setTimeout(() => onBlur(event), 100);
			},
			onSelect: handleFieldChanged,
			selectEventName: `${portletNamespace}selectDocumentLibrary`,
			title: sub(
				Liferay.Language.get('select-x'),
				Liferay.Language.get('image')
			),
			url,
		});
	};

	const placeholder = readOnly
		? ''
		: Liferay.Language.get('add-image-description');

	return (
		<>
			<ClayForm.Group style={{marginBottom: '0.5rem'}}>
				<input
					name={name}
					type="hidden"
					value={JSON.stringify(imageValues)}
				/>

				<ClayInput.Group>
					<ClayInput.GroupItem className="d-none d-sm-block" prepend>
						<ClayInput
							{...accessibleProps}
							className="field"
							dir={Liferay.Language.direction[editingLanguageId]}
							disabled={readOnly}
							id={id}
							lang={editingLanguageId}
							onClick={handleItemSelectorTriggerClick}
							type="text"
							value={imageValues.title || ''}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem append shrink>
						<ClayButton
							disabled={readOnly}
							displayType="secondary"
							onClick={handleItemSelectorTriggerClick}
							type="button"
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayInput.GroupItem>

					{imageValues.url && (
						<ClayInput.GroupItem shrink>
							<ClayButton
								disabled={readOnly}
								displayType="secondary"
								onClick={(event) =>
									dispatchValue(
										{
											clear: true,
											value: {
												description: '',
												event,
												title: '',
												url: '',
											},
										},
										(mergedValues) =>
											onClearClick(mergedValues)
									)
								}
								type="button"
							>
								{Liferay.Language.get('clear')}
							</ClayButton>
						</ClayInput.GroupItem>
					)}
				</ClayInput.Group>

				{message && <div className="form-feedback-item">{message}</div>}
			</ClayForm.Group>

			{imageValues.url && modalVisible ? (
				<ClayModal
					className="image-picker-preview-modal"
					observer={observer}
					size="full-screen"
				>
					<ClayModal.Header />

					<ClayModal.Body>
						<img
							alt={imageValues.description}
							className="d-block img-fluid mb-2 mx-auto rounded"
							onClick={onClose}
							src={imageValues.url}
							style={{cursor: 'zoom-out', maxHeight: '95%'}}
						/>

						<p
							className="font-weight-light text-center"
							style={{color: '#FFFFFF'}}
						>
							{imageValues.description}
						</p>
					</ClayModal.Body>
				</ClayModal>
			) : (
				imageValues.url && (
					<>
						<div className="image-picker-preview">
							<img
								alt={imageValues.description}
								className="d-block img-fluid mb-2 rounded"
								onClick={() => setModalVisible(true)}
								onError={(event) =>
									event.currentTarget.classList.add('hide')
								}
								onLoad={(event) =>
									event.currentTarget.classList.remove('hide')
								}
								src={imageValues.url}
								style={{
									cursor: 'pointer',
								}}
							/>
						</div>

						<ClayForm.Group>
							<ClayInput
								dir={
									Liferay.Language.direction[
										editingLanguageId
									]
								}
								disabled={readOnly}
								lang={editingLanguageId}
								name={`${name}-description`}
								onBlur={onBlur}
								onChange={({event, target: {value}}) =>
									dispatchValue(
										{value: {description: value, event}},
										(mergedValues) =>
											onDescriptionChange(mergedValues)
									)
								}
								placeholder={placeholder}
								type="text"
								value={imageValues.description}
							/>
						</ClayForm.Group>
					</>
				)
			)}
		</>
	);
};

const Main = ({
	defaultLanguageId,
	displayErrors,
	editingLanguageId,
	errorMessage,
	id,
	inputValue,
	itemSelectorURL,
	localizable,
	localizedValue,
	message,
	name,
	onBlur,
	onChange,
	onFocus,
	portletNamespace,
	readOnly,
	valid,
	value,
	...otherProps
}) => {
	const prevEditingLanguageId = usePrevious(editingLanguageId);

	if (prevEditingLanguageId !== editingLanguageId && localizable) {
		value =
			localizedValue[editingLanguageId] ??
			localizedValue[defaultLanguageId];
	}

	const getErrorMessages = (errorMessage, isSignedIn) => {
		const errorMessages = [errorMessage];

		if (!isSignedIn) {
			errorMessages.push(
				Liferay.Language.get(
					'you-need-to-be-signed-in-to-edit-this-field'
				)
			);
		}

		return errorMessages.join(' ');
	};

	const isSignedIn = Liferay.ThemeDisplay.isSignedIn();

	const transformValue = (sourceValue) => {
		if (sourceValue) {
			if (typeof sourceValue === 'string') {
				return JSON.parse(sourceValue);
			}
			else if (typeof sourceValue === 'object') {
				return sourceValue;
			}
		}

		return null;
	};

	return (
		<FieldBase
			{...otherProps}
			displayErrors={isSignedIn ? displayErrors : true}
			errorMessage={getErrorMessages(errorMessage, isSignedIn)}
			id={id}
			localizedValue={localizedValue}
			name={name}
			readOnly={isSignedIn ? readOnly : true}
			valid={isSignedIn ? valid : false}
		>
			<ImagePicker
				accessibleProps={{
					...((otherProps.errorMessage || otherProps.tip) && {
						'aria-describedby': `${otherProps.id ?? name}_fieldFeedback`,
					}),
					...(displayErrors && !valid && {'aria-invalid': true}),
					'aria-required': otherProps.required,
				}}
				editingLanguageId={editingLanguageId}
				id={id ?? name}
				inputValue={
					transformValue(inputValue) ??
					transformValue(value) ??
					defaultValue
				}
				itemSelectorURL={itemSelectorURL}
				message={message}
				name={name}
				onBlur={onBlur}
				onClearClick={({event, ...data}) => onChange(event, data)}
				onDescriptionChange={({event, ...data}) =>
					onChange(event, data)
				}
				onFieldChanged={({event, ...data}) => onChange(event, data)}
				onFocus={onFocus}
				portletNamespace={portletNamespace}
				readOnly={isSignedIn ? readOnly : true}
			/>
		</FieldBase>
	);
};

Main.displayName = 'ImagePicker';

export default Main;
