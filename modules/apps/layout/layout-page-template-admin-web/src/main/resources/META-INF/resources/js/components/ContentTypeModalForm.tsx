/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React, {
	ReactNode,
	RefObject,
	useCallback,
	useEffect,
	useRef,
	useState,
} from 'react';

import {MODAL_TYPES, ModalType} from '../constants/modalTypes';
import {MappingSubtype, MappingType} from '../types/MappingTypes';
import {ValidationError} from '../types/ValidationError';

interface Props {
	displayPageName: string;
	error: ValidationError;
	formRef: RefObject<HTMLFormElement>;
	mappingTypes: MappingType[];
	namespace: string;
	onSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
	selectedSubtype?: string;
	selectedType?: string;
	type: ModalType;
}

export default function ContentTypeModalForm({
	displayPageName,
	error: initialError,
	formRef,
	mappingTypes,
	namespace,
	onSubmit,
	selectedSubtype,
	selectedType,
	type,
}: Props) {
	const [error, setError] = useState<ValidationError>(initialError);

	const nameInputRef = useRef<HTMLInputElement>(null);

	useEffect(() => {
		if (nameInputRef.current) {
			nameInputRef.current.focus();
		}
	}, []);

	useEffect(() => {
		setError(initialError);
	}, [initialError]);

	if (type === MODAL_TYPES.edit) {
		return (
			<form onSubmit={onSubmit} ref={formRef}>
				<MappingTypeSelector
					error={error}
					mappingTypes={mappingTypes}
					namespace={namespace}
					selectedSubtype={selectedSubtype}
					selectedType={selectedType}
					setError={setError}
				/>
			</form>
		);
	}

	return (
		<form onSubmit={onSubmit} ref={formRef}>
			<FormField
				error={error.name}
				id={`${namespace}name`}
				name={Liferay.Language.get('name')}
			>
				<input
					className="form-control"
					defaultValue={displayPageName}
					id={`${namespace}name`}
					name={`${namespace}name`}
					onChange={() => setError({...error, name: ''})}
					ref={nameInputRef}
				/>
			</FormField>

			<MappingTypeSelector
				error={error}
				mappingTypes={mappingTypes}
				namespace={namespace}
				setError={setError}
			/>
		</form>
	);
}

interface MappingTypesSelectorProps {
	error: ValidationError;
	mappingTypes: MappingType[];
	namespace: string;
	selectedSubtype?: string;
	selectedType?: string;
	setError: (error: ValidationError) => void;
}

function MappingTypeSelector({
	error,
	mappingTypes,
	namespace,
	selectedSubtype,
	selectedType,
	setError,
}: MappingTypesSelectorProps) {
	const [subtypes, setSubtypes] = useState<MappingSubtype[]>(() => {
		const mappingType = mappingTypes.find(({id}) => id === selectedType);

		if (mappingType) {
			return mappingType.subtypes;
		}

		return [];
	});

	const onChange = useCallback(
		(event: any) => {
			setError({...error, classNameId: '', classTypeId: ''});

			const select = event.target;
			const selectedMappingId =
				select.options[select.selectedIndex].value;

			const mappingType = mappingTypes.find(
				(mappingType) => mappingType.id === selectedMappingId
			);

			if (mappingType) {
				setSubtypes(mappingType.subtypes);
			}
			else {
				setSubtypes([]);
			}
		},
		[error, mappingTypes, setError]
	);

	if (!Array.isArray(mappingTypes) || !mappingTypes.length) {
		return null;
	}

	return (
		<fieldset>
			<FormField
				error={error.classNameId}
				id={`${namespace}classNameId`}
				name={Liferay.Language.get('content-type')}
			>
				<select
					className="form-control"
					defaultValue={selectedType}
					id={`${namespace}classNameId`}
					name={`${namespace}classNameId`}
					onChange={onChange}
				>
					<option value="">
						{`-- ${Liferay.Language.get('not-selected')} --`}
					</option>

					{mappingTypes.map(({id, label}) => (
						<option key={id} value={id}>
							{label}
						</option>
					))}
				</select>
			</FormField>

			{Array.isArray(subtypes) && Boolean(subtypes.length) && (
				<FormField
					error={error.classTypeId}
					id={`${namespace}classTypeId`}
					name={Liferay.Language.get('subtype')}
				>
					<select
						className="form-control"
						defaultValue={selectedSubtype}
						id={`${namespace}classTypeId`}
						name={`${namespace}classTypeId`}
						onChange={() =>
							setError({
								...error,
								classTypeId: '',
							})
						}
					>
						<option value="">
							{`-- ${Liferay.Language.get('not-selected')} --`}
						</option>

						{subtypes.map(({id, label}) => (
							<option key={id} value={id}>
								{label}
							</option>
						))}
					</select>
				</FormField>
			)}
		</fieldset>
	);
}

interface FormFieldProps {
	children: ReactNode;
	error?: string;
	id: string;
	name: string;
}

function FormField({children, error, id, name}: FormFieldProps) {
	const hasError = Boolean(error);

	return (
		<div
			className={classNames({'form-group': true, 'has-error': hasError})}
		>
			<label htmlFor={id}>
				{name}

				<span className="reference-mark">
					<ClayIcon symbol="asterisk" />
				</span>
			</label>

			{children}

			{hasError && (
				<div className="form-feedback-item">
					<span className="form-feedback-indicator mr-1">
						<ClayIcon symbol="exclamation-full" />
					</span>

					{error}
				</div>
			)}
		</div>
	);
}

export function validateForm(form: any, namespace: string): ValidationError {
	const {elements} = form;
	const error: ValidationError = {};

	const errorMessage = Liferay.Language.get('this-field-is-required');

	const nameField = elements[`${namespace}name`];

	if (nameField && !nameField.value) {
		error.name = errorMessage;
	}

	const classNameIdField = elements[`${namespace}classNameId`];

	if (classNameIdField && classNameIdField.selectedIndex === 0) {
		error.classNameId = errorMessage;
	}

	const classTypeIdField = elements[`${namespace}classTypeId`];

	if (classTypeIdField && classTypeIdField.selectedIndex === 0) {
		error.classTypeId = errorMessage;
	}

	return error;
}
