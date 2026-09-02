/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import {FormError, Input, Toggle} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {ChangeEventHandler, useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';

interface ObjectDataContainerProps {
	dbTableName: string | undefined;
	errors: FormError<ObjectDefinition>;
	handleChange: ChangeEventHandler<HTMLInputElement>;
	hasUpdateObjectDefinitionPermission: boolean;
	isApproved: boolean;
	isLinkedObjectDefinition?: boolean;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	values: Partial<ObjectDefinition>;
}

export function ObjectDataContainer({
	dbTableName,
	errors,
	handleChange,
	hasUpdateObjectDefinitionPermission,
	isApproved,
	isLinkedObjectDefinition,
	onSubmit,
	setValues,
	values,
}: ObjectDataContainerProps) {
	const [selectedLocale, setSelectedLocale] =
		useState<Liferay.Language.Locale>(defaultLanguageId);

	const isReadOnly = !values.modifiable && values.system;

	const noPermissionOrLinked =
		!hasUpdateObjectDefinitionPermission || isLinkedObjectDefinition;

	return (
		<>
			<Input
				disabled={isApproved || noPermissionOrLinked}
				error={errors.name}
				id="lfr-objects__object-data-container-name"
				label={Liferay.Language.get('name')}
				name="name"
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={handleChange}
				required
				value={values.name}
			/>

			<InputLocalized
				disabled={isReadOnly || noPermissionOrLinked}
				error={errors.label}
				id="lfr-objects__object-data-container-label"
				label={Liferay.Language.get('label')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={(label) => setValues({label})}
				onSelectedLocaleChange={setSelectedLocale}
				required
				selectedLocale={selectedLocale}
				translations={values.label as LocalizedValue<string>}
			/>

			<InputLocalized
				disabled={isReadOnly || noPermissionOrLinked}
				error={errors.pluralLabel}
				id="lfr-objects__object-data-container-plural-label"
				label={Liferay.Language.get('plural-label')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={(pluralLabel) => setValues({pluralLabel})}
				onSelectedLocaleChange={setSelectedLocale}
				required
				selectedLocale={selectedLocale}
				translations={values.pluralLabel as LocalizedValue<string>}
			/>

			{Liferay.FeatureFlags['LPD-80279'] && !isReadOnly && (
				<InputLocalized
					component="textarea"
					disabled={noPermissionOrLinked}
					error={errors.description}
					helpMessage={Liferay.Language.get(
						'provide-descriptive-text-used-only-by-ai-agents-and-api-consumers'
					)}
					id="lfr-objects__object-data-container-description"
					label={Liferay.Language.get('description')}
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onChange={(description) => setValues({description})}
					onSelectedLocaleChange={setSelectedLocale}
					placeholder=""
					selectedLocale={selectedLocale}
					translations={
						(values.description ?? {}) as LocalizedValue<string>
					}
				/>
			)}

			<Input
				disabled
				id="lfr-objects__object-data-container-table-name"
				label={Liferay.Language.get('object-definition-table-name')}
				name="name"
				value={dbTableName}
			/>

			<ClayForm.Group>
				<Toggle
					disabled={!isApproved || isReadOnly || noPermissionOrLinked}
					label={sub(
						Liferay.Language.get('activate-x'),
						Liferay.Language.get('object')
					)}
					name="active"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onToggle={() => setValues({active: !values.active})}
					toggled={values.active}
				/>
			</ClayForm.Group>
		</>
	);
}
