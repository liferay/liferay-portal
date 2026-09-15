/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {
	IDataSet,
	getDataSetResourceURL,
} from '@liferay/frontend-data-set-admin-web';
import {DEFAULT_FETCH_HEADERS} from '@liferay/frontend-data-set-web';
import {useId} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import './DataSetConfigurationFields.scss';
import DataSetSelector from './components/DataSetSelector';
import EntitySelectorRow from './components/EntitySelectorRow';
import LiteralInput from './components/LiteralInput';
import {
	EMappingMode,
	IdentifierField,
	TokenMapping,
	getMappingMode,
	isAutoResolved,
	isContentMapped,
	isContextMapped,
	isMappedTokenValue,
} from './tokenMapping';

interface IItemSelectorValue {
	className?: string;
	classPK?: number;
	externalReferenceCode?: string;
}

function getItemSelectorValue(
	className: string | undefined,
	dataSet: Partial<IDataSet>
): IItemSelectorValue {
	const {externalReferenceCode, id} = dataSet;

	if (!externalReferenceCode) {
		return {};
	}

	if (!className) {
		if (process.env.NODE_ENV === 'development') {
			console.error(
				'Unable to get the data set class name. The selection will not survive a page export.'
			);
		}

		return {externalReferenceCode};
	}

	return {
		className,
		classPK: Number(id),
		externalReferenceCode,
	};
}

function isOnDisplayPageTemplate(): boolean {
	return !!document.getElementById('infoItemSelectorContainer');
}

interface IConfigurationField {
	onValueSelect: (name: string, value: any) => void;
	values: {
		apiURLTokenMappings: string;
		autoResolvedTokenNames: string;
		itemSelector: IItemSelectorValue;
	};
}

export default function DataSetConfigurationFields({
	onValueSelect,
	values,
}: IConfigurationField) {
	const [apiURLTokenMappings, setApiURLTokenMappings] = useState<
		Record<string, TokenMapping>
	>(() => {
		try {
			return JSON.parse(values?.apiURLTokenMappings || '{}');
		}
		catch (error) {
			return {};
		}
	});

	const tokenSelectId = useId();
	const mappingSelectId = useId();
	const fieldInputId = useId();

	const className = values.itemSelector?.className;
	const externalReferenceCode = values.itemSelector?.externalReferenceCode;

	const [dataSet, setDataSet] = useState<Partial<IDataSet>>({});

	useEffect(() => {
		if (!externalReferenceCode) {
			setDataSet({});

			return;
		}

		if (dataSet.externalReferenceCode === externalReferenceCode) {
			return;
		}

		let cancelled = false;

		const getDataSet = async () => {
			try {
				const response = await fetch(
					getDataSetResourceURL({dataSetERC: externalReferenceCode}),
					{headers: DEFAULT_FETCH_HEADERS}
				);

				const responseJSON = await response.json();

				if (!cancelled && responseJSON?.id) {
					setDataSet(responseJSON);
				}
			}
			catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error(error);
				}
			}
		};

		getDataSet();

		return () => {
			cancelled = true;
		};
	}, [dataSet.externalReferenceCode, externalReferenceCode]);

	const apiURL =
		(dataSet.restEndpoint || '') +
		(dataSet.additionalAPIURLParameters || '');

	const tokenKeys = useMemo(() => {
		const matches = apiURL.match(/{(.*?)}/g) ?? [];

		return matches.map((token) => token.replaceAll(/[{}]/g, ''));
	}, [apiURL]);

	const [selectedTokenKey, setSelectedTokenKey] = useState('');

	useEffect(() => {
		if (tokenKeys.length === 1) {
			if (selectedTokenKey !== tokenKeys[0]) {
				setSelectedTokenKey(tokenKeys[0]);
			}
		}
		else if (selectedTokenKey && !tokenKeys.includes(selectedTokenKey)) {
			setSelectedTokenKey('');
		}
	}, [tokenKeys, selectedTokenKey]);

	const autoResolvedTokenNames = useMemo(
		() =>
			new Set<string>(JSON.parse(values.autoResolvedTokenNames || '[]')),
		[values.autoResolvedTokenNames]
	);

	const isCurrentTokenAutoResolved =
		autoResolvedTokenNames.has(selectedTokenKey);

	const isTokenMapped = useCallback(
		(tokenKey: string): boolean => {
			const mapping = apiURLTokenMappings[tokenKey];

			if (mapping === undefined) {
				return autoResolvedTokenNames.has(tokenKey);
			}

			if (typeof mapping === 'string') {
				return !!mapping.trim().length;
			}

			if (isAutoResolved(mapping)) {
				return true;
			}

			if (!mapping.fieldId) {
				return false;
			}

			if (isContextMapped(mapping)) {
				return true;
			}

			return mapping.fieldId === 'externalReferenceCode'
				? !!mapping.externalReferenceCode
				: !!mapping.classPK;
		},
		[apiURLTokenMappings, autoResolvedTokenNames]
	);

	const isCompleted = useMemo(
		() => tokenKeys.every(isTokenMapped),
		[tokenKeys, isTokenMapped]
	);

	const [isTokenDropdownActive, setIsTokenDropdownActive] = useState(false);

	const currentTokenMapping: TokenMapping = useMemo(
		() =>
			apiURLTokenMappings[selectedTokenKey] ??
			(isCurrentTokenAutoResolved
				? {mappingMode: EMappingMode.AUTO_RESOLVED}
				: ''),
		[apiURLTokenMappings, isCurrentTokenAutoResolved, selectedTokenKey]
	);

	const currentMappingMode = getMappingMode(currentTokenMapping);

	const displayPageTemplate = isOnDisplayPageTemplate();

	const mappingOptions = [
		{
			label: Liferay.Language.get('input-token-value'),
			value: EMappingMode.LITERAL,
		},
		{
			label: Liferay.Language.get('map-to-selected-entity'),
			value: EMappingMode.CONTENT,
		},
		...(displayPageTemplate
			? [
					{
						label: Liferay.Language.get(
							'map-to-page-context-entity'
						),
						value: EMappingMode.CONTEXT,
					},
				]
			: []),
		...(isCurrentTokenAutoResolved
			? [
					{
						label: Liferay.Language.get('resolve-automatically'),
						value: EMappingMode.AUTO_RESOLVED,
					},
				]
			: []),
	];

	const updateTokenMapping = useCallback(
		(tokenKey: string, tokenMapping: TokenMapping) => {
			const newTokenMappings = {
				...apiURLTokenMappings,
				[tokenKey]: tokenMapping,
			};

			setApiURLTokenMappings(newTokenMappings);

			onValueSelect(
				'apiURLTokenMappings',
				JSON.stringify(newTokenMappings)
			);
		},
		[apiURLTokenMappings, onValueSelect]
	);

	const changeMappingMode = useCallback(
		(mappingMode: EMappingMode) => {
			if (mappingMode === currentMappingMode) {
				return;
			}

			if (mappingMode === EMappingMode.LITERAL) {
				updateTokenMapping(selectedTokenKey, '');

				return;
			}

			if (mappingMode === EMappingMode.AUTO_RESOLVED) {
				updateTokenMapping(selectedTokenKey, {
					mappingMode: EMappingMode.AUTO_RESOLVED,
				});

				return;
			}

			const existingFieldId =
				isMappedTokenValue(currentTokenMapping) &&
				!isAutoResolved(currentTokenMapping)
					? currentTokenMapping.fieldId
					: '';

			if (mappingMode === EMappingMode.CONTEXT) {
				updateTokenMapping(selectedTokenKey, {
					fieldId: existingFieldId,
					mappingMode: EMappingMode.CONTEXT,
				});

				return;
			}

			updateTokenMapping(selectedTokenKey, {
				className: '',
				classPK: '',
				externalReferenceCode: '',
				fieldId: '',
				mappingMode: EMappingMode.CONTENT,
			});
		},
		[
			currentTokenMapping,
			currentMappingMode,
			selectedTokenKey,
			updateTokenMapping,
		]
	);

	return (
		<>
			<DataSetSelector
				onChange={(selectedDataSet) => {
					setDataSet(selectedDataSet);

					onValueSelect(
						'itemSelector',
						getItemSelectorValue(className, selectedDataSet)
					);
				}}
				value={dataSet}
			/>

			{Liferay.FeatureFlags['LPD-38564'] && !!tokenKeys.length && (
				<ClayPanel
					className="w-100"
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get('url-token-mapping')}
					displayType="unstyled"
					showCollapseIcon
				>
					<ClayPanel.Body>
						<ClayForm.Group className="align-items-center d-flex justify-content-between">
							<label className="mb-0">
								{Liferay.Language.get('status')}
							</label>

							<ClayLabel
								className="text-uppercase"
								displayType={
									isCompleted ? 'success' : 'warning'
								}
							>
								{isCompleted
									? Liferay.Language.get('completed')
									: Liferay.Language.get('incomplete')}
							</ClayLabel>
						</ClayForm.Group>

						<ClayForm.Group>
							<label htmlFor={tokenSelectId}>
								{Liferay.Language.get('token')}
							</label>

							<ClayDropDown
								active={isTokenDropdownActive}
								onActiveChange={setIsTokenDropdownActive}
								trigger={
									<ClayButton
										className="align-items-center btn-sm d-flex justify-content-between w-100"
										displayType="secondary"
										id={tokenSelectId}
									>
										{selectedTokenKey ? (
											<>
												<span className="data-set-token-name text-truncate">{`{${selectedTokenKey}}`}</span>

												<span className="align-items-center d-flex data-set-token-status">
													<ClayLabel
														className="text-uppercase"
														displayType={
															isTokenMapped(
																selectedTokenKey
															)
																? 'success'
																: 'info'
														}
													>
														{isTokenMapped(
															selectedTokenKey
														)
															? Liferay.Language.get(
																	'mapped'
																)
															: Liferay.Language.get(
																	'unmapped'
																)}
													</ClayLabel>

													<ClayIcon
														className="ml-2"
														symbol="caret-bottom"
													/>
												</span>
											</>
										) : (
											<>
												<span>
													{Liferay.Language.get(
														'select-an-option'
													)}
												</span>

												<ClayIcon symbol="caret-bottom" />
											</>
										)}
									</ClayButton>
								}
							>
								<ClayDropDown.ItemList>
									{tokenKeys.map((key) => {
										const mapped = isTokenMapped(key);

										return (
											<ClayDropDown.Item
												active={
													key === selectedTokenKey
												}
												className="p-2"
												key={key}
												onClick={() => {
													setSelectedTokenKey(key);
													setIsTokenDropdownActive(
														false
													);
												}}
											>
												<span className="align-items-center d-flex justify-content-between">
													<span className="data-set-token-name text-truncate">{`{${key}}`}</span>

													<ClayLabel
														className="data-set-token-status ml-2 mt-0 text-uppercase"
														displayType={
															mapped
																? 'success'
																: 'info'
														}
													>
														{mapped
															? Liferay.Language.get(
																	'mapped'
																)
															: Liferay.Language.get(
																	'unmapped'
																)}
													</ClayLabel>
												</span>
											</ClayDropDown.Item>
										);
									})}
								</ClayDropDown.ItemList>
							</ClayDropDown>
						</ClayForm.Group>

						{selectedTokenKey && (
							<>
								<ClayForm.Group>
									<label htmlFor={mappingSelectId}>
										{Liferay.Language.get(
											'mapping[content]'
										)}
									</label>

									<ClaySelectWithOption
										id={mappingSelectId}
										onChange={(event) =>
											changeMappingMode(
												event.target
													.value as EMappingMode
											)
										}
										options={mappingOptions}
										sizing="sm"
										value={currentMappingMode}
									/>
								</ClayForm.Group>

								{isContentMapped(currentTokenMapping) && (
									<EntitySelectorRow
										entity={currentTokenMapping}
										onEntityChange={(entity) =>
											updateTokenMapping(
												selectedTokenKey,
												entity
											)
										}
										onEntityRemove={() =>
											updateTokenMapping(
												selectedTokenKey,
												{
													className: '',
													classPK: '',
													externalReferenceCode: '',
													fieldId: '',
													mappingMode:
														EMappingMode.CONTENT,
												}
											)
										}
									/>
								)}

								{currentMappingMode !==
									EMappingMode.AUTO_RESOLVED && (
									<ClayForm.Group>
										<label htmlFor={fieldInputId}>
											{currentMappingMode ===
											EMappingMode.LITERAL
												? Liferay.Language.get(
														'token-value'
													)
												: Liferay.Language.get('field')}
										</label>

										{currentMappingMode ===
										EMappingMode.LITERAL ? (
											<LiteralInput
												inputId={fieldInputId}
												onCommit={(value) =>
													updateTokenMapping(
														selectedTokenKey,
														value
													)
												}
												value={
													typeof currentTokenMapping ===
													'string'
														? currentTokenMapping
														: ''
												}
											/>
										) : (
											<ClaySelectWithOption
												disabled={
													isContentMapped(
														currentTokenMapping
													) &&
													!currentTokenMapping.className
												}
												id={fieldInputId}
												onChange={(event) => {
													if (
														!isMappedTokenValue(
															currentTokenMapping
														) ||
														isAutoResolved(
															currentTokenMapping
														)
													) {
														return;
													}

													updateTokenMapping(
														selectedTokenKey,
														{
															...currentTokenMapping,
															fieldId: event
																.target
																.value as
																| IdentifierField
																| '',
														}
													);
												}}
												options={[
													{
														label: `-- ${Liferay.Language.get(
															'unmapped'
														)} --`,
														value: '',
													},
													{
														label: Liferay.Language.get(
															'id'
														),
														value: 'classPK',
													},
													{
														label: Liferay.Language.get(
															'external-reference-code'
														),
														value: 'externalReferenceCode',
													},
												]}
												sizing="sm"
												value={
													isMappedTokenValue(
														currentTokenMapping
													) &&
													!isAutoResolved(
														currentTokenMapping
													) &&
													!(
														isContentMapped(
															currentTokenMapping
														) &&
														!currentTokenMapping.className
													)
														? currentTokenMapping.fieldId
														: ''
												}
											/>
										)}
									</ClayForm.Group>
								)}
							</>
						)}
					</ClayPanel.Body>
				</ClayPanel>
			)}
		</>
	);
}
