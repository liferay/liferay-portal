/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Text} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {Dispatch, SetStateAction, useEffect, useState} from 'react';

import {Select} from '../fieldComponents/Select';
import {HTTP_METHODS, RETRIEVE_TYPES, STR_BLANK} from '../utils/constants';
import {fetchJSON} from '../utils/fetchUtil';
import {
	makeURLPathParameterString,
	makeURLPathStringWithForwardSlashes,
	removeLeadingForwardSlash,
	stringBetweenCurlyBraces,
} from '../utils/string';

interface BaseAPIApplicationFieldsProps {
	apiApplicationBaseURL: string;
	basePath: string;
	data: Partial<APIEndpointUIData>;
	displayError: EndpointDataError;
	editing?: Boolean;
	setData: Dispatch<SetStateAction<Partial<APIEndpointUIData>>>;
}

export default function BaseAPIEndpointFields({
	apiApplicationBaseURL,
	basePath,
	data,
	displayError,
	editing,
	setData,
}: BaseAPIApplicationFieldsProps) {
	const [httpMethodOptions, setHttpMethodOptions] = useState<SelectOption[]>(
		[]
	);

	const [pathErrorMessage, setPathErrorMessage] = useState<string>(STR_BLANK);
	const [pathHasErrors, setPathHasErrors] = useState<boolean>(false);

	const [retrieveTypeOptions, setRetrieveTypeOptions] = useState<
		SelectOption[]
	>([]);
	const [scopeOptions, setScopeOptions] = useState<SelectOption[]>([]);

	const [selectedHttpMethod, setSelectedHttpMethod] =
		useState<SelectOption>();
	const [selectedRetrieveType, setSelectedRetrieveType] =
		useState<SelectOption>();
	const [selectedScope, setSelectedScope] = useState<SelectOption>();

	useEffect(() => {
		setPathHasErrors(
			displayError.path ||
				(displayError.parameter &&
					selectedRetrieveType?.value ===
						RETRIEVE_TYPES.SINGLE_ELEMENT)
		);

		if (
			displayError.path &&
			displayError.parameter &&
			selectedRetrieveType?.value === RETRIEVE_TYPES.SINGLE_ELEMENT
		) {
			setPathErrorMessage(
				Liferay.Language.get('please-enter-a-path-and-a-parameter')
			);
		}
		else if (displayError.parameter) {
			setPathErrorMessage(
				Liferay.Language.get('please-enter-a-parameter')
			);
		}
		else if (displayError.path) {
			setPathErrorMessage(Liferay.Language.get('please-enter-a-path'));
		}
	}, [
		displayError.path,
		displayError.parameter,
		selectedRetrieveType?.value,
	]);

	useEffect(() => {
		fetchJSON<FetchedListType>({
			input: '/o/headless-admin-list-type/v1.0/list-type-definitions/by-external-reference-code/L_API_ENDPOINT_SCOPES',
		}).then((response) => {
			const options = response.listTypeEntries
				? response.listTypeEntries.map((entry) => ({
						label:
							entry.key === 'site'
								? Liferay.Language.get('site')
								: Liferay.Language.get('company'),
						value: entry.key,
					}))
				: [];

			if (options.length) {
				setScopeOptions(options);
			}
		});

		fetchJSON<FetchedListType>({
			input: '/o/headless-admin-list-type/v1.0/list-type-definitions/by-external-reference-code/L_API_ENDPOINT_RETRIEVE_TYPES',
		}).then((response) => {
			const options = response.listTypeEntries
				? response.listTypeEntries.map((entry) => ({
						label:
							entry.key === RETRIEVE_TYPES.SINGLE_ELEMENT
								? Liferay.Language.get('single-element')
								: Liferay.Language.get('collection'),
						value: entry.key,
					}))
				: [];

			if (options.length) {
				setRetrieveTypeOptions(options);
			}
		});

		fetchJSON<FetchedListType>({
			input: '/o/headless-admin-list-type/v1.0/list-type-definitions/by-external-reference-code/L_API_ENDPOINT_HTTP_METHODS',
		}).then((response) => {
			const options = response.listTypeEntries
				? response.listTypeEntries.map((entry) => ({
						label: Liferay.Language.get(entry.key).toUpperCase(),
						value: entry.key,
					}))
				: [];

			if (options.length) {
				setHttpMethodOptions(options);
			}
		});
	}, []);

	useEffect(() => {
		displayError.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId &&
			openToast({
				message: Liferay.Language.get(
					'there-are-errors-on-the-form-please-check-if-any-mandatory-fields-have-not-been-completed'
				),
				type: 'danger',
			});
	}, [displayError.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId]);

	useEffect(() => {
		if (data.retrieveType?.key && retrieveTypeOptions.length) {
			setSelectedRetrieveType(
				retrieveTypeOptions.find(
					(option) => option.value === data.retrieveType?.key
				)
			);
		}

		if (data.retrieveType?.key === RETRIEVE_TYPES.COLLECTION) {
			setData((previousValue) => {
				delete previousValue.parameter;
				delete previousValue.pathParameter;
				delete previousValue.pathParameterDescription;

				return previousValue;
			});
		}
	}, [data.retrieveType, retrieveTypeOptions, setData]);

	useEffect(() => {
		if (data.scope?.key && scopeOptions.length) {
			setSelectedScope(
				scopeOptions.find((option) => option.value === data.scope?.key)
			);
		}
	}, [data, scopeOptions]);

	useEffect(() => {
		if (data.httpMethod?.key && httpMethodOptions.length) {
			setSelectedHttpMethod(
				httpMethodOptions.find(
					(option) => option.value === data.httpMethod?.key
				)
			);
		}

		if (data.httpMethod?.key === HTTP_METHODS.POST) {
			setData((previousValue) => {
				delete previousValue.parameter;
				delete previousValue.pathParameter;
				delete previousValue.pathParameterDescription;

				return {
					...previousValue,
					retrieveType: {
						key: RETRIEVE_TYPES.SINGLE_ELEMENT,
						value: STR_BLANK,
					},
				};
			});
		}
		else if (data.httpMethod?.key === HTTP_METHODS.GET) {
			setData((previousValue) => {
				delete previousValue.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId;

				return {
					...previousValue,
					...(!previousValue.parameter && {parameter: STR_BLANK}),
					...(!previousValue.pathParameter && {
						pathParameter: STR_BLANK,
					}),
					...(!previousValue.pathParameterDescription && {
						pathParameterDescription: STR_BLANK,
					}),
				};
			});
		}
	}, [data.httpMethod, httpMethodOptions, setData]);

	const handleDropdownChange = (
		itemKey: string,
		value: string,
		options: SelectOption[],
		onChangeFn: Dispatch<SetStateAction<SelectOption | undefined>>
	) => {
		setData((previousValue) => ({
			...previousValue,
			[itemKey]: {key: value, value: STR_BLANK},
		}));

		onChangeFn({
			label: options.find((option) => option.value === value)?.label!,
			value,
		});
	};

	const endpointDescriptionLabel = Liferay.Language.get(
		'add-a-short-description-that-describes-this-endpoint'
	);

	const endpointPathHostTextPreview =
		selectedScope?.value === 'site'
			? `${window.location.origin}${basePath}${apiApplicationBaseURL}/scopes/{scopeKey}`
			: `${window.location.origin}${basePath}${apiApplicationBaseURL}`;
	const endpointPathLabel = Liferay.Language.get('enter-path');
	const endpointParameterLabel = `{${Liferay.Language.get(
		'enter-parameter'
	)}}`;

	return (
		<ClayForm>
			<ClayForm.Group
				className={classNames({
					'has-error': displayError.httpMethod,
				})}
			>
				<label htmlFor="selectTrigger">
					{Liferay.Language.get('method')}

					<span className="ml-1 reference-mark text-warning">
						<ClayIcon symbol="asterisk" />
					</span>
				</label>

				<Select
					disabled={!!editing}
					invalid={displayError.httpMethod}
					onClick={(value) =>
						handleDropdownChange(
							'httpMethod',
							value,
							httpMethodOptions,
							setSelectedHttpMethod
						)
					}
					options={httpMethodOptions}
					placeholder={Liferay.Language.get('select-method')}
					required
					searchable={false}
					selectedOption={selectedHttpMethod}
				/>

				{displayError.httpMethod && (
					<ClayAlert
						className="mt-2"
						displayType="danger"
						title={Liferay.Language.get('please-select-a-method')}
						variant="feedback"
					></ClayAlert>
				)}
			</ClayForm.Group>

			{selectedHttpMethod?.value === HTTP_METHODS.GET && (
				<ClayForm.Group
					className={classNames({
						'has-error': displayError.retrieveType,
					})}
				>
					<label htmlFor="selectTrigger">
						{Liferay.Language.get('retrieve-type')}

						<span className="ml-1 reference-mark text-warning">
							<ClayIcon symbol="asterisk" />
						</span>
					</label>

					<Select
						dropDownSearchAriaLabel={Liferay.Language.get(
							'search-for-an-object-definition-or-use-the-arrow-keys-to-navigate-and-select-an-object-definition-from-the-list'
						)}
						invalid={displayError.retrieveType}
						onClick={(value) =>
							handleDropdownChange(
								'retrieveType',
								value,
								retrieveTypeOptions,
								setSelectedRetrieveType
							)
						}
						options={retrieveTypeOptions}
						placeholder={Liferay.Language.get('select-type')}
						required
						searchable={false}
						selectedOption={selectedRetrieveType}
						triggerAriaLabel={
							!selectedRetrieveType
								? Liferay.Language.get(
										Liferay.Language.get('select-type')
									)
								: sub(
										Liferay.Language.get(
											'type-x-is-selected'
										),
										selectedRetrieveType.label
									)
						}
					/>

					{displayError.retrieveType && (
						<ClayAlert
							className="mt-2"
							displayType="danger"
							title={Liferay.Language.get(
								'please-select-a-retrieve-type'
							)}
							variant="feedback"
						></ClayAlert>
					)}
				</ClayForm.Group>
			)}

			<ClayForm.Group
				className={classNames({
					'has-error': displayError.scope,
				})}
			>
				<label htmlFor="selectTrigger">
					{Liferay.Language.get('scope')}

					<span className="ml-1 reference-mark text-warning">
						<ClayIcon symbol="asterisk" />
					</span>
				</label>

				<Select
					disabled={false}
					dropDownSearchAriaLabel={Liferay.Language.get(
						'search-for-an-object-definition-or-use-the-arrow-keys-to-navigate-and-select-an-object-definition-from-the-list'
					)}
					invalid={displayError.scope}
					onClick={(value) =>
						handleDropdownChange(
							'scope',
							value,
							scopeOptions,
							setSelectedScope
						)
					}
					options={scopeOptions}
					placeholder={Liferay.Language.get('select-scope')}
					required
					searchable={false}
					selectedOption={selectedScope}
					triggerAriaLabel={
						!selectedScope
							? Liferay.Language.get(
									Liferay.Language.get('select-scope')
								)
							: sub(
									Liferay.Language.get('scope-x-is-selected'),
									selectedScope.label
								)
					}
				/>

				{displayError.scope && (
					<ClayAlert
						className="mt-2"
						displayType="danger"
						title={Liferay.Language.get('please-select-a-scope')}
						variant="feedback"
					></ClayAlert>
				)}
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="endpointPathField">
					{Liferay.Language.get('path')}

					<span className="ml-1 reference-mark text-warning">
						<ClayIcon symbol="asterisk" />
					</span>
				</label>

				<Text as="p" color="secondary" id="hostTextPreview" size={3}>
					{endpointPathHostTextPreview}
				</Text>

				<ClayInput.Group>
					<ClayInput.GroupItem prepend shrink>
						<ClayInput.GroupText>/</ClayInput.GroupText>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem
						append
						className={classNames({
							'has-error': displayError.path,
						})}
					>
						<ClayInput
							aria-label={endpointPathLabel}
							id="endpointPathField"
							onChange={({target: {value}}) =>
								setData((previousData) => ({
									...previousData,
									path: makeURLPathStringWithForwardSlashes(
										value
									),
								}))
							}
							placeholder={endpointPathLabel}
							type="text"
							value={
								data.path
									? removeLeadingForwardSlash(data.path)
									: STR_BLANK
							}
						/>
					</ClayInput.GroupItem>

					{selectedHttpMethod?.value === HTTP_METHODS.GET &&
						selectedRetrieveType?.value ===
							RETRIEVE_TYPES.SINGLE_ELEMENT && (
							<>
								<ClayInput.GroupItem
									prepend
									shrink
									style={{marginLeft: 0}}
								>
									<ClayInput.GroupText>/</ClayInput.GroupText>
								</ClayInput.GroupItem>

								<ClayInput.GroupItem
									append
									className={classNames({
										'has-error': displayError.parameter,
									})}
								>
									<ClayInput
										aria-label={endpointParameterLabel}
										id="endpointParameterField"
										onBlur={() =>
											setData((previousData) => ({
												...previousData,
												parameter:
													stringBetweenCurlyBraces(
														removeLeadingForwardSlash(
															previousData.parameter!
														)
													),
											}))
										}
										onChange={({target: {value}}) =>
											setData((previousData) => ({
												...previousData,
												parameter:
													makeURLPathParameterString(
														value
													),
											}))
										}
										placeholder={endpointParameterLabel}
										type="text"
										value={
											data.parameter
												? removeLeadingForwardSlash(
														data.parameter
													)
												: STR_BLANK
										}
									/>
								</ClayInput.GroupItem>
							</>
						)}
				</ClayInput.Group>

				{pathHasErrors && (
					<ClayAlert
						className="mt-2"
						displayType="danger"
						title={pathErrorMessage}
						variant="feedback"
					></ClayAlert>
				)}
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="endpointDescriptionField">
					{Liferay.Language.get('description')}
				</label>

				<textarea
					aria-label={endpointDescriptionLabel}
					autoComplete="off"
					className="form-control"
					id="endpointDescriptionField"
					onChange={({target: {value}}) =>
						setData((previousData) => {
							if (value === '' && previousData.description) {
								delete previousData.description;

								return {...previousData};
							}

							return {
								...previousData,
								description: value,
							};
						})
					}
					placeholder={endpointDescriptionLabel}
					value={data.description}
				/>
			</ClayForm.Group>

			<div aria-live="assertive" className="sr-only">
				{(displayError.httpMethod ||
					displayError.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId ||
					displayError.retrieveType ||
					displayError.scope ||
					pathHasErrors) && (
					<span>
						{Liferay.Language.get(
							'there-are-errors-on-the-form-please-check-if-any-mandatory-fields-have-not-been-completed'
						)}
					</span>
				)}
			</div>
		</ClayForm>
	);
}
