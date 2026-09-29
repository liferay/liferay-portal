/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidebarCategory} from '@liferay/object-js-components-web';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React, {ElementType} from 'react';

import {DEFAULT_VALUE_SUPPORTED_BUSINESS_TYPES} from '../../../../utils/constants';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';
import {AutocompleteContainer} from './AutocompleteContainer';
import {BlockedDomainsContainer} from './BlockedDomainsContainer';
import {DefaultValueContainer} from './DefaultValueContainer';
import {ReadOnlyContainer} from './ReadOnlyContainer';

interface AdvancedTabProps {
	ckEditor5Config?: object;
	containerWrapper: ElementType;
	creationLanguageId: Liferay.Language.Locale;
	decimalSeparator: string;
	defaultValueSidebarElements: SidebarCategory[];
	errors: ObjectFieldErrors;
	googleMapsAPIKey?: string;
	isDefaultStorageType: boolean;
	isRootDescendantNode: boolean;
	learnResources: ILearnResourceContext;
	mapProviderKey?: string;
	modelBuilder?: boolean;
	onSubmit?: () => void;
	readOnlySidebarElements: SidebarCategory[];
	setValues: (value: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

export function AdvancedTab({
	ckEditor5Config,
	containerWrapper: ContainerWrapper,
	creationLanguageId,
	decimalSeparator,
	defaultValueSidebarElements,
	errors,
	googleMapsAPIKey,
	isDefaultStorageType,
	isRootDescendantNode,
	learnResources,
	mapProviderKey,
	modelBuilder = false,
	onSubmit,
	readOnlySidebarElements,
	setValues,
	values,
}: AdvancedTabProps) {
	const disabledReadyOnly =
		values.businessType === 'Aggregation' ||
		values.businessType === 'AutoIncrement' ||
		values.businessType === 'Formula' ||
		(values.businessType === 'Relationship' && isRootDescendantNode) ||
		values.required ||
		values.system;
	const hasDefaultValue =
		values.businessType &&
		DEFAULT_VALUE_SUPPORTED_BUSINESS_TYPES.includes(values.businessType);

	return (
		<>
			{isDefaultStorageType && (
				<ContainerWrapper
					collapsable
					defaultExpanded
					disabled={disabledReadyOnly}
					displayTitle={Liferay.Language.get('read-only')}
					displayType="unstyled"
					title={Liferay.Language.get('read-only')}
				>
					<ReadOnlyContainer
						disabled={disabledReadyOnly}
						modelBuilder={modelBuilder}
						onSubmit={onSubmit}
						readOnlySidebarElements={readOnlySidebarElements}
						requiredField={values.required as boolean}
						setValues={setValues}
						values={values}
					/>
				</ContainerWrapper>
			)}

			{hasDefaultValue && (
				<ContainerWrapper
					collapsable
					defaultExpanded
					disabled={false}
					displayTitle={Liferay.Language.get('default-value')}
					displayType="unstyled"
					title={Liferay.Language.get('default-value')}
				>
					<DefaultValueContainer
						ckEditor5Config={ckEditor5Config}
						creationLanguageId={creationLanguageId}
						decimalSeparator={decimalSeparator}
						defaultValueSidebarElements={
							defaultValueSidebarElements
						}
						errors={errors}
						googleMapsAPIKey={googleMapsAPIKey}
						learnResources={learnResources}
						mapProviderKey={mapProviderKey}
						modelBuilder={modelBuilder}
						onSubmit={onSubmit}
						setValues={setValues}
						values={values}
					/>
				</ContainerWrapper>
			)}

			{values.businessType === 'EmailAddress' && (
				<>
					<ContainerWrapper
						collapsable
						defaultExpanded
						disabled={false}
						displayTitle={Liferay.Language.get('blocked-domains')}
						displayType="unstyled"
						title={Liferay.Language.get('blocked-domains')}
					>
						<BlockedDomainsContainer
							onSubmit={onSubmit}
							setValues={setValues}
							values={values}
						/>
					</ContainerWrapper>

					<ContainerWrapper
						collapsable
						defaultExpanded
						disabled={false}
						displayTitle={Liferay.Language.get('autocomplete')}
						displayType="unstyled"
						title={Liferay.Language.get('autocomplete')}
					>
						<AutocompleteContainer
							onSubmit={onSubmit}
							setValues={setValues}
							values={values}
						/>
					</ContainerWrapper>
				</>
			)}
		</>
	);
}
