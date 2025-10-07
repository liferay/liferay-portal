/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Option} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import {FormError, SingleSelect} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {Scope} from './EditObjectDetails';

const SCOPE_OPTIONS = [
	{
		label: Liferay.Language.get('company'),
		value: 'company',
	},
	{
		label: Liferay.Language.get('site'),
		value: 'site',
	},
];

interface ScopeContainerProps {
	className?: string;
	companies: Scope[];
	errors: FormError<ObjectDefinition>;
	hasUpdateObjectDefinitionPermission: boolean;
	isApproved: boolean;
	isLinkedObjectDefinition?: boolean;
	onSubmit?: (editedObjectDefinition?: Partial<ObjectDefinition>) => void;
	setValues: (values: Partial<ObjectDefinition>) => void;
	sites: Scope[];
	values: Partial<ObjectDefinition>;
}

export function ScopeContainer({
	className,
	companies,
	errors,
	hasUpdateObjectDefinitionPermission,
	isApproved,
	isLinkedObjectDefinition,
	onSubmit,
	setValues,
	sites,
	values,
}: ScopeContainerProps) {
	const [selectedPanelCategoryValue, setSelectedPanelCategoryValue] =
		useState('');

	const setPanelCategoryKey = (
		sites: Scope[],
		panelCategoryValue: string
	) => {
		let selectedPanelCategory: string = '';

		sites.find(({items}) =>
			items.find(({value}) => {
				if (value === panelCategoryValue) {
					selectedPanelCategory = value;

					return true;
				}
			})
		);

		setSelectedPanelCategoryValue(selectedPanelCategory);
	};

	useEffect(() => {
		setPanelCategoryKey(
			values.scope === 'company' ? companies : sites,
			values.panelCategoryKey as string
		);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [values.id, values.scope, companies, sites]);

	return (
		<>
			<SingleSelect<LabelValueObject>
				className={className}
				disabled={
					isApproved ||
					!hasUpdateObjectDefinitionPermission ||
					values.storageType === 'salesforce' ||
					isLinkedObjectDefinition
				}
				error={errors.titleObjectFieldId}
				id="lfr-objects__object-scope-container-scope"
				items={SCOPE_OPTIONS}
				label={Liferay.Language.get('scope')}
				onSelectionChange={(value) => {
					setValues({
						panelCategoryKey: '',
						scope: value as string,
					});

					if (onSubmit) {
						onSubmit({
							...values,
							panelCategoryKey: '',
							scope: value as string,
						});
					}

					setSelectedPanelCategoryValue('');
				}}
				selectedKey={values.scope}
			/>

			<SingleSelect
				className={className}
				disabled={
					(!values.modifiable && values.system) ||
					!hasUpdateObjectDefinitionPermission ||
					isLinkedObjectDefinition
				}
				error={errors.titleObjectFieldId}
				id="lfr-objects__object-scope-container-panel-link"
				items={values.scope === 'company' ? companies : sites}
				label={Liferay.Language.get('panel-link')}
				onSelectionChange={(value) => {
					setValues({
						panelCategoryKey: value as string,
					});

					if (onSubmit) {
						onSubmit({
							...values,
							panelCategoryKey: value as string,
						});
					}

					setSelectedPanelCategoryValue(value as string);
				}}
				selectedKey={selectedPanelCategoryValue}
			>
				{(group) => (
					<ClayDropDown.Group
						header={group.label}
						items={group.items}
					>
						{(item) => (
							<Option key={item.value} textValue={item.label}>
								{item.label}
							</Option>
						)}
					</ClayDropDown.Group>
				)}
			</SingleSelect>
		</>
	);
}
