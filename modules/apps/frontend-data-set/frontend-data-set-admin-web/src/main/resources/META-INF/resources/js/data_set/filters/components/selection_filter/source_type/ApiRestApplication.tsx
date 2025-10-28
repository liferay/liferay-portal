/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import {TItem} from '@clayui/form/lib/SelectBox';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {fetch} from 'frontend-js-web';
import fuzzy from 'fuzzy';
import React, {useEffect, useState} from 'react';

import RequiredMark from '../../../../../components/RequiredMark';
import ValidationFeedback from '../../../../../components/ValidationFeedback';
import RESTApplicationDropdownItem from '../../../../../components/rest/RESTApplicationDropdownItem';
import RESTApplicationDropdownMenu from '../../../../../components/rest/RESTApplicationDropdownMenu';
import RESTEndpointDropdownMenu from '../../../../../components/rest/RESTEndpointDropdownMenu';
import RESTSchemaDropdownMenu from '../../../../../components/rest/RESTSchemaDropdownMenu';
import {
	ALLOWED_ENDPOINTS_PARAMETERS,
	FUZZY_OPTIONS,
} from '../../../../../utils/constants';
import getFields, {
	ISchemas,
	getValidFields,
} from '../../../../../utils/getFields';
import openDefaultFailureToast from '../../../../../utils/openDefaultFailureToast';
import {IField, ISelectionFilter} from '../../../../../utils/types';

interface IApiRestApplicationModalContentProps {
	filter?: ISelectionFilter;
	itemKeyValidationError: boolean;
	itemLabelValidationError: boolean;
	namespace: string;
	onChange: ({
		selectedItemKey,
		selectedItemLabel,
		selectedRESTApplication,
		selectedRESTEndpoint,
		selectedRESTSchema,
		sourceItems,
	}: {
		selectedItemKey: string;
		selectedItemLabel: string;
		selectedRESTApplication: string | null;
		selectedRESTEndpoint: string | null;
		selectedRESTSchema: string | null;
		sourceItems: TItem[];
	}) => void;
	preselectedValueInput: string;
	requiredRESTApplicationValidationError: boolean;
	resolvedRESTSchemas: string[];
	restApplications: string[];
	restEndpointValidationError: boolean;
	restSchemaValidationError: boolean;
	source: string;
}

function ApiRestApplication({
	filter,
	itemKeyValidationError,
	itemLabelValidationError,
	namespace,
	onChange,
	preselectedValueInput,
	requiredRESTApplicationValidationError,
	resolvedRESTSchemas,
	restApplications,
	restEndpointValidationError,
	restSchemaValidationError,
	source,
}: IApiRestApplicationModalContentProps) {
	const [fields, setFields] = useState<IField[]>([]);
	const [selectedItemKey, setSelectedItemKey] = useState<string>('');
	const [selectedItemLabel, setSelectedItemLabel] = useState<string>('');
	const [
		noEnpointsRESTApplicationValidationError,
		setNoEnpointsRESTApplicationValidationError,
	] = useState(false);
	const [restSchemaEndpoints, setRESTSchemaEndpoints] = useState<
		Map<string, Array<string>>
	>(new Map());
	const [selectedRESTApplication, setSelectedRESTApplication] = useState<
		string | null
	>(null);
	const [selectedRESTSchema, setSelectedRESTSchema] = useState<string | null>(
		null
	);
	const [selectedRESTEndpoint, setSelectedRESTEndpoint] = useState<
		string | null
	>(null);
	const [sourceItems, setSourceItems] = useState<TItem[]>([]);

	const itemKeyFormElementId = `${namespace}ItemKey`;
	const itemLabelFormElementId = `${namespace}ItemLabel`;

	const isPathValid = (
		path: string,
		allowedParameters: string[]
	): boolean => {
		const paramsMatcher = RegExp('{(.*?)}', 'g');
		let matches;

		while ((matches = paramsMatcher.exec(path)) !== null) {
			if (!allowedParameters.includes(matches[1])) {
				return false;
			}
		}

		return true;
	};

	async function getSourceItems({
		preselectedValueInput,
		selectedItemKey,
		selectedItemLabel,
		source,
	}: {
		preselectedValueInput: string;
		selectedItemKey: string;
		selectedItemLabel: string;
		source: string | null;
	}) {
		const isValidSource =
			source && !(source as string).match(/\{[A-Za-z0-9]+\}/g);

		if (!isValidSource || !selectedItemKey || !selectedItemLabel) {
			return [];
		}

		const sourceItems = await fetch(source as string)
			.then((response) => {
				if (!response.ok) {
					return [];
				}

				const responseJSON = response.json();

				return responseJSON;
			})
			.then((apiValues) => {
				return !apiValues?.items?.length
					? []
					: apiValues.items
							.filter((item: any) => {
								return fuzzy.match(
									preselectedValueInput,
									String(item[selectedItemLabel])
								);
							})
							.map((item: any) => {
								return {
									label: String(item[selectedItemLabel]),
									value: String(item[selectedItemKey]),
								};
							});
			});

		return sourceItems;
	}

	const getRESTSchemas = async (
		restApplication: string,
		resolvedRESTSchemas: string[]
	) => {
		if (!restApplication) {
			return;
		}
		const response = await fetch(`/o${restApplication}/openapi.json`);

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const responseJson = await response.json();

		const paths = Object.keys(responseJson.paths ?? []);
		const schemas = responseJson.components?.schemas;
		const schemaNames = Object.keys(schemas ?? []);

		const schemaEndpoints: Map<string, Array<string>> = new Map();

		schemaNames.forEach((schemaName) => {
			paths.forEach((path: string) => {
				if (
					!isPathValid(path, ALLOWED_ENDPOINTS_PARAMETERS) &&
					!resolvedRESTSchemas.includes(schemaName)
				) {
					return;
				}

				if (
					responseJson.paths[path]?.get?.responses.default.content[
						'application/json'
					]?.schema?.$ref?.endsWith(`/Page${schemaName}`)
				) {
					const endpoints = schemaEndpoints.get(schemaName) ?? [];

					endpoints.push(path);

					if (endpoints.length === 1) {
						schemaEndpoints.set(schemaName, endpoints);
					}
				}
			});
		});

		return {schemaEndpoints, schemas};
	};

	const loadFields = ({
		restSchema,
		schemas,
	}: {
		restSchema: string;
		schemas: ISchemas;
	}) => {
		const fields = getValidFields({
			contextPath: '',
			schemaName: restSchema,
			schemas,
			visitedFields: [],
		});

		setFields(
			fields.filter(
				(field) => field.type !== 'array' && field.type !== 'object'
			)
		);
	};

	const RestApplicationDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className:
					'fds-entries-dropdown-menu fds-filter-rest-application-menu',
			}}
			trigger={
				<ClayButton
					aria-labelledby={`${namespace}restApplicationsLabel`}
					className="form-control form-control-select form-control-select-secondary"
					displayType="secondary"
					id={`${namespace}restApplicationsSelect`}
				>
					{selectedRESTApplication ? (
						<RESTApplicationDropdownItem
							query=""
							restApplication={selectedRESTApplication}
						/>
					) : (
						Liferay.Language.get('choose-an-option')
					)}
				</ClayButton>
			}
		>
			<RESTApplicationDropdownMenu
				onItemClick={(item: string) => {
					const restApplication = item;
					let restSchema: string | null = null;
					let restEndpoint: string | null = null;

					getRESTSchemas(restApplication, resolvedRESTSchemas)
						.then((result) => {
							const schemas = result?.schemas;
							const schemaEndpoints = result?.schemaEndpoints;

							if (schemaEndpoints) {
								setNoEnpointsRESTApplicationValidationError(
									schemaEndpoints.size === 0
								);
								if (schemaEndpoints.size === 1) {
									restSchema = schemaEndpoints
										.keys()
										.next().value;

									const paths = schemaEndpoints.get(
										restSchema ?? ''
									);

									if (paths?.length === 1) {
										restEndpoint = paths[0];
									}

									if (restSchema) {
										loadFields({restSchema, schemas});
									}
								}

								setRESTSchemaEndpoints(schemaEndpoints);
							}
						})
						.finally(() => {
							setSelectedRESTApplication(restApplication);
							setSelectedRESTSchema(restSchema);
							setSelectedRESTEndpoint(restEndpoint);
							setSelectedItemKey('');
							setSelectedItemLabel('');
							setSourceItems([]);

							onChange({
								selectedItemKey: '',
								selectedItemLabel: '',
								selectedRESTApplication: restApplication,
								selectedRESTEndpoint: restEndpoint,
								selectedRESTSchema: restSchema,
								sourceItems: [],
							});
						});
				}}
				restApplications={restApplications}
			/>
		</ClayDropDown>
	);

	const RestSchemaDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className:
					'fds-entries-dropdown-menu fds-filter-rest-schema-menu',
			}}
			trigger={
				<ClayButton
					aria-labelledby={`${namespace}restSchema`}
					className="form-control form-control-select form-control-select-secondary"
					displayType="secondary"
					id={`${namespace}restSchemaSelect`}
				>
					{selectedRESTSchema ||
						Liferay.Language.get('choose-an-option')}
				</ClayButton>
			}
		>
			<RESTSchemaDropdownMenu
				onItemClick={(item: string) => {
					setSelectedRESTSchema(item);

					setSelectedItemKey('');
					setSelectedItemLabel('');
					setSourceItems([]);

					const endpoints = restSchemaEndpoints.get(item);
					let endpoint;

					if (endpoints?.length === 1) {
						endpoint = endpoints[0];
						setSelectedRESTEndpoint(endpoint);
					}
					else {
						endpoint = null;
					}

					onChange({
						selectedItemKey: '',
						selectedItemLabel: '',
						selectedRESTApplication,
						selectedRESTEndpoint:
							endpoints?.length === 1
								? endpoint
								: selectedRESTEndpoint,
						selectedRESTSchema: item,
						sourceItems: [],
					});

					if (selectedRESTApplication && item) {
						getFields({
							restApplication: selectedRESTApplication,
							restSchema: item,
						}).then((fields: IField[]) => {
							if (fields) {
								setFields(
									fields.filter(
										(field) =>
											field.type !== 'array' &&
											field.type !== 'object'
									)
								);
							}
						});
					}
				}}
				restSchemas={Array.from(restSchemaEndpoints.keys())}
			/>
		</ClayDropDown>
	);

	const RestEndpointDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className:
					'fds-entries-dropdown-menu fds-filter-rest-endpoint-menu',
			}}
			trigger={
				<ClayButton
					aria-labelledby={`${namespace}restEndpoint`}
					className="form-control form-control-select form-control-select-secondary"
					displayType="secondary"
					id={`${namespace}restEndpointSelect`}
				>
					{selectedRESTEndpoint ||
						Liferay.Language.get('choose-an-option')}
				</ClayButton>
			}
		>
			<RESTEndpointDropdownMenu
				onItemClick={(item: string) => {
					setSelectedRESTEndpoint(item);

					setSelectedItemKey('');
					setSelectedItemLabel('');
					setSourceItems([]);

					onChange({
						selectedItemKey: '',
						selectedItemLabel: '',
						selectedRESTApplication,
						selectedRESTEndpoint: item,
						selectedRESTSchema,
						sourceItems: [],
					});
				}}
				restEndpoints={
					restSchemaEndpoints.get(selectedRESTSchema ?? '') ?? []
				}
			/>
		</ClayDropDown>
	);

	const ItemKeyDropdownMenu = ({
		itemKeys: initialItemKeys,
		onItemClick,
	}: {
		itemKeys: (string | undefined)[];
		onItemClick: Function;
	}) => {
		const [itemKeys, setItemKeys] =
			useState<(string | undefined)[]>(initialItemKeys);
		const [query, setQuery] = useState('');

		const onSearch = (query: string) => {
			setQuery(query);

			const regexp = new RegExp(query, 'i');
			setItemKeys(
				query
					? initialItemKeys.filter((itemKey) => {
							return itemKey?.match(regexp);
						}) || []
					: initialItemKeys
			);
		};

		return (
			<>
				<ClayDropDown.Search
					aria-label={Liferay.Language.get('search')}
					onChange={onSearch}
					value={query}
				/>

				<ClayDropDown.ItemList items={itemKeys} role="listbox">
					{

						// @ts-ignore

						(item: string) => {
							const fuzzymatch = fuzzy.match(
								query,
								item,
								FUZZY_OPTIONS
							);

							return (
								<ClayDropDown.Item
									key={item}
									onClick={() => onItemClick(item)}
									roleItem="option"
								>
									{fuzzymatch ? (
										<span
											dangerouslySetInnerHTML={{
												__html: fuzzymatch.rendered,
											}}
										/>
									) : (
										item
									)}
								</ClayDropDown.Item>
							);
						}
					}
				</ClayDropDown.ItemList>
			</>
		);
	};

	const ItemLabelDropdownMenu = ({
		itemLabels: initialItemLabels = [],
		onItemClick,
	}: {
		itemLabels: (string | undefined)[];
		onItemClick: Function;
	}) => {
		const [itemLabels, setItemLabels] =
			useState<(string | undefined)[]>(initialItemLabels);
		const [query, setQuery] = useState('');

		const onSearch = (query: string) => {
			setQuery(query);

			const regexp = new RegExp(query, 'i');
			setItemLabels(
				query
					? initialItemLabels.filter((itemLabel) => {
							return itemLabel?.match(regexp);
						}) || []
					: initialItemLabels
			);
		};

		return (
			<>
				<ClayDropDown.Search
					aria-label={Liferay.Language.get('search')}
					onChange={onSearch}
					value={query}
				/>

				<ClayDropDown.ItemList items={itemLabels} role="listbox">
					{

						// @ts-ignore

						(item: string) => {
							const fuzzymatch = fuzzy.match(
								query,
								item,
								FUZZY_OPTIONS
							);

							return (
								<ClayDropDown.Item
									key={item}
									onClick={() => onItemClick(item)}
									roleItem="option"
								>
									{fuzzymatch ? (
										<span
											dangerouslySetInnerHTML={{
												__html: fuzzymatch.rendered,
											}}
										/>
									) : (
										item
									)}
								</ClayDropDown.Item>
							);
						}
					}
				</ClayDropDown.ItemList>
			</>
		);
	};

	useEffect(() => {
		if (!filter) {
			return;
		}
		setSelectedItemKey(filter.itemKey);
		setSelectedItemLabel(filter.itemLabel);
		setSelectedRESTApplication(filter.restApplication);
		setSelectedRESTSchema(filter.restSchema);
		setSelectedRESTEndpoint(filter.restEndpoint);
		let initialSourceItems: TItem[] = [];
		getRESTSchemas(filter.restApplication, resolvedRESTSchemas)
			.then((result) => {
				const schemas = result?.schemas;
				const schemaEndpoints = result?.schemaEndpoints;

				if (schemaEndpoints) {
					setRESTSchemaEndpoints(schemaEndpoints);
					loadFields({
						restSchema: filter.restSchema,
						schemas,
					});
				}
			})
			.then(() =>
				getSourceItems({
					preselectedValueInput,
					selectedItemKey: filter.itemKey,
					selectedItemLabel: filter.itemLabel,
					source: filter.source,
				})
			)
			.then((sourceItems) => {
				initialSourceItems = sourceItems;
				setSourceItems(sourceItems);
			})
			.finally(() =>
				onChange({
					selectedItemKey: filter.itemKey,
					selectedItemLabel: filter.itemLabel,
					selectedRESTApplication: filter.restApplication,
					selectedRESTEndpoint: filter.restEndpoint,
					selectedRESTSchema: filter.restSchema,
					sourceItems: initialSourceItems,
				})
			);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<>
			{restApplications && (
				<ClayForm.Group
					className={classNames({
						'has-error':
							requiredRESTApplicationValidationError ||
							noEnpointsRESTApplicationValidationError,
					})}
				>
					<label
						htmlFor={`${namespace}restApplicationsSelect`}
						id={`${namespace}restApplicationsLabel`}
					>
						{Liferay.Language.get('rest-application')}

						<RequiredMark />
					</label>

					<RestApplicationDropdown />

					{requiredRESTApplicationValidationError && (
						<ValidationFeedback />
					)}

					{noEnpointsRESTApplicationValidationError && (
						<ValidationFeedback
							message={Liferay.Language.get(
								'there-are-no-usable-endpoints'
							)}
						/>
					)}
				</ClayForm.Group>
			)}

			{selectedRESTApplication &&
				!noEnpointsRESTApplicationValidationError && (
					<ClayForm.Group
						className={classNames({
							'has-error': restSchemaValidationError,
						})}
					>
						<label
							htmlFor={`${namespace}restSchemaSelect`}
							id={`${namespace}restSchema`}
						>
							{Liferay.Language.get('rest-schema')}

							<RequiredMark />
						</label>

						<RestSchemaDropdown />

						{restSchemaValidationError && <ValidationFeedback />}
					</ClayForm.Group>
				)}

			{selectedRESTSchema && (
				<ClayForm.Group
					className={classNames({
						'has-error': restEndpointValidationError,
					})}
				>
					<label
						htmlFor={`${namespace}restEndpointSelect`}
						id={`${namespace}restEndpoint`}
					>
						{Liferay.Language.get('rest-endpoint')}

						<RequiredMark />
					</label>

					<RestEndpointDropdown />

					{restEndpointValidationError && <ValidationFeedback />}
				</ClayForm.Group>
			)}

			{selectedRESTSchema && (
				<>
					<ClayForm.Group
						className={classNames('form-group-autofit', {
							'has-error':
								itemKeyValidationError ||
								itemLabelValidationError,
						})}
					>
						<div className="form-group-item">
							<label htmlFor={itemKeyFormElementId}>
								{Liferay.Language.get('item-key')}

								<RequiredMark />

								<span
									className="label-icon lfr-portal-tooltip ml-2"
									title={Liferay.Language.get(
										'this-field-provides-the-values-to-filter-elements'
									)}
								>
									<ClayIcon symbol="question-circle-full" />
								</span>
							</label>

							<ClayDropDown
								className="fds-filter-item-key"
								menuElementAttrs={{
									className: 'fds-entries-dropdown-menu',
								}}
								trigger={
									<ClayButton
										className="form-control form-control-select form-control-select-secondary"
										displayType="secondary"
										name={itemKeyFormElementId}
									>
										{selectedItemKey ? (
											<RESTApplicationDropdownItem
												query=""
												restApplication={
													selectedItemKey
												}
											/>
										) : (
											Liferay.Language.get(
												'choose-an-option'
											)
										)}
									</ClayButton>
								}
							>
								<ItemKeyDropdownMenu
									itemKeys={fields.map((field) => field.name)}
									onItemClick={(itemKey: string) => {
										setSelectedItemKey(itemKey);

										let currentSourceItems: TItem[] =
											sourceItems;

										getSourceItems({
											preselectedValueInput,
											selectedItemKey: itemKey,
											selectedItemLabel,
											source,
										})
											.then((sourceItems) => {
												currentSourceItems =
													sourceItems;
												setSourceItems(sourceItems);
											})
											.finally(() => {
												onChange({
													selectedItemKey: itemKey,
													selectedItemLabel,
													selectedRESTApplication,
													selectedRESTEndpoint,
													selectedRESTSchema,
													sourceItems:
														currentSourceItems,
												});
											});
									}}
								/>
							</ClayDropDown>

							{itemKeyValidationError && <ValidationFeedback />}
						</div>

						<div className="form-group-item">
							<label htmlFor={itemLabelFormElementId}>
								{Liferay.Language.get('item-label')}

								<RequiredMark />

								<span
									className="label-icon lfr-portal-tooltip ml-2"
									title={Liferay.Language.get(
										'this-field-provides-the-labels-to-show-in-the-filter'
									)}
								>
									<ClayIcon symbol="question-circle-full" />
								</span>
							</label>

							<ClayDropDown
								className="fds-filter-item-label"
								menuElementAttrs={{
									className: 'fds-entries-dropdown-menu',
								}}
								trigger={
									<ClayButton
										className="form-control form-control-select form-control-select-secondary"
										displayType="secondary"
										name={itemLabelFormElementId}
									>
										{selectedItemLabel ? (
											<RESTApplicationDropdownItem
												query=""
												restApplication={
													selectedItemLabel
												}
											/>
										) : (
											Liferay.Language.get(
												'choose-an-option'
											)
										)}
									</ClayButton>
								}
							>
								<ItemLabelDropdownMenu
									itemLabels={fields.map(
										(field) => field.label
									)}
									onItemClick={(itemLabel: string) => {
										setSelectedItemLabel(itemLabel);

										let currentSourceItems: TItem[] =
											sourceItems;

										getSourceItems({
											preselectedValueInput,
											selectedItemKey,
											selectedItemLabel: itemLabel,
											source,
										})
											.then((sourceItems) => {
												currentSourceItems =
													sourceItems;
												setSourceItems(sourceItems);
											})
											.finally(() => {
												onChange({
													selectedItemKey,
													selectedItemLabel:
														itemLabel,
													selectedRESTApplication,
													selectedRESTEndpoint,
													selectedRESTSchema,
													sourceItems:
														currentSourceItems,
												});
											});
									}}
								/>
							</ClayDropDown>

							{itemLabelValidationError && <ValidationFeedback />}
						</div>
					</ClayForm.Group>
				</>
			)}
		</>
	);
}

export default ApiRestApplication;
