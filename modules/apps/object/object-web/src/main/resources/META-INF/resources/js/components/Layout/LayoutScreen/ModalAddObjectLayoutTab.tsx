/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import ClayForm from '@clayui/form';
import ClayLabel from '@clayui/label';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {
	FormError,
	Input,
	constantsUtils,
	stringUtils,
	useForm,
} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {FieldBase} from 'frontend-js-components-web';
import React, {useMemo, useState} from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {TYPES as EVENT_TYPES, useLayoutContext} from '../objectLayoutContext';
import {TObjectLayoutTab} from '../types';

import './ModalAddObjectLayoutTab.scss';

import type {Observer} from '@clayui/modal/src/types';

type TTabTypes = {
	[key: string]: {
		active: boolean;
		description: string;
		label: string;
	};
};

type TLabelInfo = {
	displayType: 'info' | 'secondary' | 'success';
	labelContent: string;
};

const TYPES = {
	FIELDS: 'fields',
	RELATIONSHIPS: 'relationships',
};

const types: TTabTypes = {
	[TYPES.FIELDS]: {
		active: true,
		description: Liferay.Language.get(
			'display-fields-and-one-to-one-relationships'
		),
		label: Liferay.Language.get('fields'),
	},
	[TYPES.RELATIONSHIPS]: {
		active: false,
		description: Liferay.Language.get('display-multiple-relationships'),
		label: Liferay.Language.get('relationships'),
	},
};

interface ModalAddObjectLayoutTabProps
	extends React.HTMLAttributes<HTMLElement> {
	observer: Observer;
	onClose: () => void;
}

interface TabTypeProps extends React.HTMLAttributes<HTMLElement> {
	description: string;
	disabled?: boolean;
	disabledMessage?: string;
	label: string;
	onChangeType: (type: string) => void;
	selected: string;
	type: string;
}

function TabType({
	description,
	disabled = false,
	label,
	onChangeType,
	selected,
	type,
}: TabTypeProps) {
	const tabProps = {
		'data-tooltip-align': 'top',
		'onClick': () => {},
		'title': Liferay.Language.get(
			'the-first-tab-in-the-layout-cannot-be-a-relationship-tab'
		),
	};

	return (
		<ClayTooltipProvider>
			<div
				className={classNames('layout-tab__tab-types', {
					active: selected === type,
					disabled,
				})}
				key={type}
				onClick={() => onChangeType(type)}
				{...(disabled && tabProps)}
			>
				<div className="h4 layout-tab__tab-types__title">{label}</div>

				<span className="tab__tab-types__description">
					{description}
				</span>
			</div>
		</ClayTooltipProvider>
	);
}

function getRelationshipInfo(reverse: boolean): TLabelInfo {
	return {
		displayType: reverse ? 'info' : 'success',
		labelContent: reverse
			? Liferay.Language.get('child')
			: Liferay.Language.get('parent'),
	};
}

export function ModalAddObjectLayoutTab({
	observer,
	onClose,
}: ModalAddObjectLayoutTabProps) {
	const [
		{
			creationLanguageId,
			objectDefinitionExternalReferenceCode,
			objectLayout: {objectLayoutTabs},
			objectRelationships,
		},
		dispatch,
	] = useLayoutContext();

	const [networkStatus, setNetworkStatus] = useState(4);
	const [selectedType, setSelectedType] = useState(TYPES.FIELDS);
	const [search, setSearch] = useState<string>('');

	const {
		resource,
	}: {
		resource: {
			items: ObjectRelationship[];
		};
	} = useResource({
		fetchOptions: {
			credentials: 'include',
			headers: new Headers({'x-csrf-token': Liferay.authToken}),
			method: 'GET',
		},
		link: `${Liferay.ThemeDisplay.getPortalURL()}${Liferay.ThemeDisplay.getPathContext()}/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinitionExternalReferenceCode}/object-relationships`,
		onNetworkStatusChange: setNetworkStatus,
		variables: {
			filter: 'edge eq false',
			search,
		},
	});

	const availableObjectRelationshipIds = useMemo(() => {
		const set = new Set<string>();

		objectRelationships.forEach(({id, inLayout}) => {
			if (!inLayout) {
				set.add(id.toString());
			}
		});

		return set;
	}, [objectRelationships]);

	const filteredRelationshipItems = useMemo(() => {
		if (!resource?.items) {
			return [];
		}

		return resource.items.filter((item) =>
			availableObjectRelationshipIds.has(item.id.toString())
		);
	}, [availableObjectRelationshipIds, resource?.items]);

	const onSubmit = (values: TObjectLayoutTab) => {
		dispatch({
			payload: {
				name: {
					[defaultLanguageId]: values.name[defaultLanguageId],
				},
				objectRelationshipId: values.objectRelationshipId,
			},
			type: EVENT_TYPES.ADD_OBJECT_LAYOUT_TAB,
		});

		onClose();
	};

	const onValidate = (values: Partial<TObjectLayoutTab>) => {
		const errors: FormError<TObjectLayoutTab> = {};

		if (
			!stringUtils.getLocalizableLabel({
				fallbackLanguageId: creationLanguageId,
				labels: values.name,
			})
		) {
			errors.name = constantsUtils.REQUIRED_MSG;
		}

		if (
			!values.objectRelationshipId &&
			selectedType === TYPES.RELATIONSHIPS
		) {
			errors.objectRelationshipId = constantsUtils.REQUIRED_MSG;
		}

		return errors;
	};

	const {errors, handleSubmit, setValues, values} = useForm<TObjectLayoutTab>(
		{
			initialValues: {},
			onSubmit,
			validate: onValidate,
		}
	);

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('add-tab')}
				</ClayModal.Header>

				<ClayModal.Body>
					<Input
						error={errors.name}
						id="inputName"
						label={Liferay.Language.get('label')}
						name="name"
						onChange={({target: {value}}) => {
							setValues({
								name: {
									[defaultLanguageId]: value,
								},
							});
						}}
						required
						value={stringUtils.getLocalizableLabel({
							fallbackLanguageId: creationLanguageId,
							labels: values.name,
						})}
					/>

					<ClayForm.Group>
						<label className="mb-2">
							{Liferay.Language.get('type')}
						</label>

						{Object.keys(types).map((key) => {
							const {description, label} = types[key];

							return (
								<TabType
									description={description}
									disabled={
										!objectLayoutTabs.length &&
										key === TYPES.RELATIONSHIPS
									}
									key={key}
									label={label}
									onChangeType={setSelectedType}
									selected={selectedType}
									type={key}
								/>
							);
						})}
					</ClayForm.Group>

					{selectedType === TYPES.RELATIONSHIPS && (
						<FieldBase
							errorMessage={errors.objectRelationshipId}
							id="relationshipAutocomplete"
							label={Liferay.Language.get('relationship')}
							required
						>
							<Autocomplete
								filterKey="label"
								id="relationshipAutocomplete"
								items={filteredRelationshipItems}
								loadingState={networkStatus}
								menuTrigger="focus"
								messages={{
									loading: Liferay.Language.get('loading...'),
									notFound:
										Liferay.Language.get(
											'no-results-found'
										),
								}}
								onChange={(item) => {
									setSearch(item);
								}}
								onItemsChange={() => {}}
								value={search}
							>
								{(item) => {
									const label =
										stringUtils.getLocalizableLabel({
											fallbackLabel: item.name,
											fallbackLanguageId:
												creationLanguageId,
											labels: item.label,
										});

									const relationshipInfo =
										getRelationshipInfo(
											item.reverse ?? false
										);

									return (
										<Autocomplete.Item
											key={item.id}
											onClick={() => {
												setValues({
													objectRelationshipId:
														item.id,
												});
											}}
											textValue={label}
										>
											<div className="d-flex justify-content-between">
												<div>{label}</div>

												<div className="object-web-relationship-item-label">
													<ClayLabel
														displayType={
															relationshipInfo.displayType
														}
													>
														{
															relationshipInfo.labelContent
														}
													</ClayLabel>
												</div>
											</div>
										</Autocomplete.Item>
									);
								}}
							</Autocomplete>
						</FieldBase>
					)}
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton type="submit">
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}
