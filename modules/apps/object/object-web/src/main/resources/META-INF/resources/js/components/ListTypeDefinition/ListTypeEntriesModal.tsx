/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {
	API,
	Input,
	constantsUtils,
	invalidateRequired,
} from '@liferay/object-js-components-web';
import {InputLocalized, openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {specialCharactersInString, toCamelCase} from '../../utils/string';
import {ObjectValidationErrors} from './ListTypeFormBase';
import {fixLocaleKeys} from './utils';

export interface IModalState extends Partial<ListTypeEntry> {
	header?: string;
	itemExternalReferenceCode?: string;
	itemId?: number;
	itemKey?: string;
	modalType?: 'add' | 'edit';
	pickListId?: number;
	readOnly?: boolean;
	reloadIframeWindow?: () => void;
	system?: boolean;
}

function ListTypeEntriesModal() {
	const [
		{
			header,
			itemExternalReferenceCode,
			itemId,
			itemKey,
			modalType,
			name_i18n,
			pickListId,
			readOnly,
			reloadIframeWindow,
			system,
		},
		setState,
	] = useState<IModalState>({});

	const [keyChanged, setKeyChanged] = useState(false);
	const [APIError, setAPIError] = useState<string>('');

	const handleExternalReferenceCodeChange = (value: string) => {
		setState((previousValues) => ({
			...previousValues,
			itemExternalReferenceCode: value,
		}));
	};

	const handleKeyChange = (value: string) => {
		if (keyChanged === false) {
			setKeyChanged(true);
		}
		setState((previousValues) => ({
			...previousValues,
			itemKey: toCamelCase(value, false, true),
		}));
	};

	const handleNameChange = (newName_i18n: LocalizedValue<string>) => {
		let newItemKey = itemKey;

		if (modalType !== 'edit' && keyChanged === false) {
			newItemKey = toCamelCase(
				newName_i18n[defaultLanguageId] as string,
				true,
				true
			);
		}

		setState((previousValues) => ({
			...previousValues,
			itemKey: newItemKey,
			name_i18n: newName_i18n,
		}));
	};

	const [errors, setErrors] = useState<{
		externalReferenceCode?: string;
		key?: string;
		name?: string;
		name_i18n?: string;
	}>({
		externalReferenceCode: '',
		key: '',
		name: '',
		name_i18n: '',
	});

	const resetModal = () => {
		setAPIError('');
		setState({});
		setErrors({});
		setKeyChanged(false);
	};

	const {observer, onClose} = useModal({
		onClose: resetModal,
	});

	useEffect(() => {
		const openModal = (modalProps: Partial<IModalState>) => {
			const newModalProps = {...modalProps};

			if (newModalProps.name_i18n) {
				newModalProps.name_i18n = fixLocaleKeys(
					newModalProps.name_i18n
				);
			}
			setState(newModalProps);
		};

		Liferay.on('openListTypeEntriesModal', openModal);

		return () =>
			Liferay.detach('openListTypeEntriesModal', openModal as () => void);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (APIError) {
			openToast({
				message: APIError,
				type: 'danger',
			});
		}
		setAPIError('');
	}, [APIError]);

	const validate = (
		entry: Partial<ListTypeEntry>
	): ObjectValidationErrors => {
		const errors: ObjectValidationErrors = {};
		const externalReferenceCode = entry.externalReferenceCode;
		const key = entry.key;
		const name_i18n = entry.name_i18n?.[defaultLanguageId];

		if (invalidateRequired(name_i18n)) {
			errors.name_i18n = constantsUtils.REQUIRED_MSG;
		}

		if (invalidateRequired(key)) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}

		if (key && specialCharactersInString(key)) {
			errors.key = Liferay.Language.get(
				'key-must-only-contain-letters-and-digits'
			);
		}

		if (modalType === 'edit' && invalidateRequired(externalReferenceCode)) {
			errors.externalReferenceCode = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const handleSave = async () => {
		const errors: ObjectValidationErrors = validate({
			externalReferenceCode: itemExternalReferenceCode,
			key: itemKey,
			name_i18n,
		});

		if (Object.keys(errors).length) {
			setErrors(errors);
		}
		else {
			setErrors({});
			try {
				if (modalType === 'add') {
					await API.postListTypeEntry({
						key: itemKey,
						listTypeDefinitionId: pickListId,
						name_i18n,
					});
					openToast({
						message: Liferay.Language.get(
							'the-picklist-item-was-created-successfully'
						),
						type: 'success',
					});
				}
				else if (modalType === 'edit') {
					await API.putListTypeEntry({
						externalReferenceCode: itemExternalReferenceCode,
						id: itemId,
						name_i18n,
					});
					openToast({
						message: Liferay.Language.get(
							'the-picklist-item-was-updated-successfully'
						),
						type: 'success',
					});
				}
				onClose();
				if (reloadIframeWindow) {
					reloadIframeWindow();
				}
			}
			catch (error) {
				setAPIError((error as Error).message);
			}
		}
	};

	return header ? (
		<ClayModal observer={observer}>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{header}
			</ClayModal.Header>

			<ClayModal.Body>
				{errors.key && (
					<ClayAlert displayType="danger">{errors.key}</ClayAlert>
				)}

				<InputLocalized
					aria-label={Liferay.Language.get('item-name')}
					disabled={readOnly}
					error={errors.name_i18n}
					id="locale"
					label={Liferay.Language.get('name')}
					onChange={handleNameChange}
					required
					translations={name_i18n ?? {[defaultLanguageId]: ''}}
				/>

				<Input
					aria-label={Liferay.Language.get('item-key')}
					disabled={modalType === 'edit'}
					error={errors.name}
					id="listTypeEntriesModalKeyInputField"
					label={Liferay.Language.get('key')}
					name="name"
					onChange={({target}) => handleKeyChange(target.value)}
					required
					value={itemKey ?? ''}
				/>

				{modalType === 'edit' && (
					<Input
						disabled={system}
						error={errors.externalReferenceCode}
						id="externalReferenceCodeInput"
						label={Liferay.Language.get('external-reference-code')}
						name="externalReferenceCode"
						onChange={({target}) =>
							handleExternalReferenceCodeChange(target.value)
						}
						required
						value={itemExternalReferenceCode ?? ''}
					/>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onClose()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={readOnly}
							displayType="primary"
							onClick={handleSave}
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
}

export default ListTypeEntriesModal;
