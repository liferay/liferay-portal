/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import {useResource} from '@clayui/data-provider';
import ClayLabel from '@clayui/label';
import {stringUtils} from '@liferay/object-js-components-web';
import {FieldBase} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import './SelectObjectDefinition.scss';

interface SelectObjectDefinitionProps {
	disabled?: boolean;
	error?: string;
	initialValue?: string;
	label?: string;
	objectDefinition1?: Partial<ObjectDefinition>;
	reverseOrder: boolean;
	setValues: (values: Partial<ObjectRelationship>) => void;
}

export default function SelectObjectDefinition({
	disabled,
	error,
	initialValue,
	label,
	objectDefinition1,
	reverseOrder,
	setValues,
}: SelectObjectDefinitionProps) {
	const [networkStatus, setNetworkStatus] = useState(4);
	const [value, setValue] = useState(initialValue ?? '');
	const [search, setSearch] = useState('');

	const {
		resource,
	}: {
		resource: {
			items: Partial<ObjectDefinition>[];
		};
	} = useResource({
		fetchOptions: {
			credentials: 'include',
			headers: new Headers({'x-csrf-token': Liferay.authToken}),
			method: 'GET',
		},
		link: `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/object-admin/v1.0/object-definitions`,
		onNetworkStatusChange: setNetworkStatus,
		variables: {
			search,
		},
	});

	const objectDefinitions = useMemo(
		() =>
			resource?.items
				? resource.items.filter(
						({modifiable, parameterRequired, storageType}) =>
							(objectDefinition1?.modifiable || modifiable) &&
							(!Liferay.FeatureFlags['LPS-135430'] ||
								storageType === 'default') &&
							!parameterRequired
					)
				: [],
		[resource, objectDefinition1]
	);

	useEffect(() => {
		setValue(initialValue ?? '');
	}, [initialValue]);

	return (
		<FieldBase
			disabled={disabled}
			errorMessage={error}
			id="objectRelationshipSelectObjectDefinition"
			label={label}
			required
		>
			<Autocomplete
				aria-label={label}
				disabled={disabled}
				filterKey="name"
				items={objectDefinitions}
				loadingState={networkStatus}
				menuTrigger="focus"
				messages={{
					loading: Liferay.Language.get('loading...'),
					notFound: Liferay.Language.get('no-results-found'),
				}}
				onChange={(newValue) => {
					setValue(newValue);
					setSearch(newValue);
				}}
				onItemsChange={() => {}}
				placeholder={Liferay.Language.get(
					'search-for-an-object-definition'
				)}
				value={value}
			>
				{(item) => {
					const label = stringUtils.getLocalizableLabel({
						fallbackLabel: item.name,
						fallbackLanguageId: item.defaultLanguageId,
						labels: item.label,
					});

					return (
						<Autocomplete.Item
							key={item.externalReferenceCode}
							onClick={() => {
								if (!reverseOrder) {
									setValues({
										objectDefinitionExternalReferenceCode2:
											item.externalReferenceCode,
										objectDefinitionId2: item.id,
										objectDefinitionName2: item.name,
									});
								}
								else {
									setValues({
										objectDefinitionExternalReferenceCode1:
											item.externalReferenceCode,
										objectDefinitionId1: item.id,
									});
								}

								setValue(label);
								setSearch('');
							}}
							textValue={label}
						>
							<div className="lfr-objects__select-object-definition-option">
								<div>{label}</div>

								<ClayLabel
									displayType={
										item.system ? 'info' : 'warning'
									}
								>
									{item.system
										? Liferay.Language.get('system')
										: Liferay.Language.get('custom')}
								</ClayLabel>
							</div>
						</Autocomplete.Item>
					);
				}}
			</Autocomplete>
		</FieldBase>
	);
}
