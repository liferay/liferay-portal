/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {openModal} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useState} from 'react';

import '../css/DataSets.scss';
import RequiredMark from './components/RequiredMark';
import ValidationFeedback from './components/ValidationFeedback';
import RESTApplicationDropdownItem from './components/rest/RESTApplicationDropdownItem';
import RESTApplicationDropdownMenu from './components/rest/RESTApplicationDropdownMenu';
import RESTEndpointDropdownMenu from './components/rest/RESTEndpointDropdownMenu';
import RESTSchemaDropdownMenu from './components/rest/RESTSchemaDropdownMenu';
import {
	ALLOWED_ENDPOINTS_PARAMETERS,
	DEFAULT_FETCH_HEADERS,
	FDS_DEFAULT_PROPS,
} from './utils/constants';
import getAPIExplorerURL from './utils/getAPIExplorerURL';
import getDataSetResourceURL from './utils/getDataSetResourceURL';
import openDefaultFailureToast from './utils/openDefaultFailureToast';
import openDefaultSuccessToast from './utils/openDefaultSuccessToast';
import {IDataSet, ISystemDataSet} from './utils/types';

const LIST_OF_ITEMS_PER_PAGE = '4, 8, 20, 40, 60';
const DEFAULT_ITEMS_PER_PAGE = 20;

const LabelInput = ({
	labelValidationError,
	namespace,
	onBlur,
	onChange,
	value,
}: {
	labelValidationError: boolean;
	namespace: string;
	onBlur: () => void;
	onChange: Function;
	value: string;
}) => (
	<ClayForm.Group
		className={classNames({
			'has-error': labelValidationError,
		})}
	>
		<label htmlFor={`${namespace}labelInput`}>
			{Liferay.Language.get('name')}

			<RequiredMark />
		</label>

		<ClayInput
			id={`${namespace}labelInput`}
			onBlur={onBlur}
			onChange={(event) => onChange(event.target.value)}
			type="text"
			value={value}
		/>

		{labelValidationError && <ValidationFeedback />}
	</ClayForm.Group>
);

const NewDataSetModalContent = ({
	closeModal,
	loadData,
	namespace,
	resolvedRESTSchemas,
	restApplications,
}: {
	closeModal: Function;
	loadData: Function;
	namespace: string;
	resolvedRESTSchemas?: Array<string>;
	restApplications?: Array<string>;
}) => {
	const [label, setLabel] = useState('');
	const [saveButtonDisabled, setSaveButtonDisabled] = useState(false);
	const [labelValidationError, setLabelValidationError] = useState(false);
	const [
		requiredRESTApplicationValidationError,
		setRequiredRESTApplicationValidationError,
	] = useState(false);
	const [
		noEnpointsRESTApplicationValidationError,
		setNoEnpointsRESTApplicationValidationError,
	] = useState(false);
	const [restSchemaValidationError, setRESTSchemaValidationError] =
		useState(false);
	const [restEndpointValidationError, setRESTEndpointValidationError] =
		useState(false);
	const [restSchemaEndpoints, setRESTSchemaEndpoints] = useState<
		Map<string, Array<string>>
	>(new Map());
	const [selectedRESTApplication, setSelectedRESTApplication] = useState<
		string | null
	>();
	const [selectedRESTSchema, setSelectedRESTSchema] = useState<
		string | null
	>();
	const [selectedRESTEndpoint, setSelectedRESTEndpoint] = useState<
		string | null
	>();

	const saveDataSet = async () => {
		if (!selectedRESTApplication) {
			return;
		}

		selectedRESTApplication;

		const body = {
			defaultItemsPerPage: DEFAULT_ITEMS_PER_PAGE,
			label,
			listOfItemsPerPage: LIST_OF_ITEMS_PER_PAGE,
			restApplication: selectedRESTApplication,
			restEndpoint: selectedRESTEndpoint,
			restSchema: selectedRESTSchema,
		};

		const response = await fetch(getDataSetResourceURL({}), {
			body: JSON.stringify(body),
			headers: DEFAULT_FETCH_HEADERS,
			method: 'POST',
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const dataSet: IDataSet = await response.json();

		if (dataSet?.id) {
			closeModal();

			openDefaultSuccessToast();

			loadData();
		}
		else {
			setSaveButtonDisabled(false);

			openDefaultFailureToast();
		}
	};

	const isPathValid = (
		path: string,
		allowedParameters: string[]
	): boolean => {
		if (Liferay.FeatureFlags['LPD-38564']) {
			return true;
		}

		const paramsMatcher = RegExp('{(.*?)}', 'g');
		let matches;

		while ((matches = paramsMatcher.exec(path)) !== null) {
			if (!allowedParameters.includes(matches[1])) {
				return false;
			}
		}

		return true;
	};

	const getRESTSchemas = async (
		restApplication: string,
		resolvedRESTSchemas: Array<string> = []
	) => {
		if (!restApplication) {
			return;
		}

		const response = await fetch(`/o${restApplication}/openapi.json`, {
			headers: DEFAULT_FETCH_HEADERS,
		});

		if (!response.ok) {
			openDefaultFailureToast();

			return;
		}

		const responseJson = await response.json();

		const paths = Object.keys(responseJson.paths ?? []);
		const schemaNames = Object.keys(responseJson.components?.schemas ?? []);

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

		if (schemaEndpoints.size === 0) {
			setSelectedRESTSchema(null);

			setSelectedRESTEndpoint(null);

			setNoEnpointsRESTApplicationValidationError(true);
		}
		else if (schemaEndpoints.size === 1) {
			const schema = Array.from(schemaEndpoints.keys())[0];

			setSelectedRESTSchema(schema);

			const paths = schemaEndpoints.get(schema);

			if (paths?.length === 1) {
				setSelectedRESTEndpoint(paths[0]);
			}

			setNoEnpointsRESTApplicationValidationError(false);
		}
		else {
			setSelectedRESTSchema(null);

			setSelectedRESTEndpoint(null);

			setNoEnpointsRESTApplicationValidationError(false);
		}

		setRESTSchemaEndpoints(schemaEndpoints);
	};

	const validate = () => {
		if (!label) {
			setLabelValidationError(true);

			return false;
		}

		if (!selectedRESTApplication) {
			setRequiredRESTApplicationValidationError(true);

			return false;
		}

		if (noEnpointsRESTApplicationValidationError) {
			return false;
		}

		if (!selectedRESTSchema) {
			setRESTSchemaValidationError(true);

			return false;
		}

		if (!selectedRESTEndpoint) {
			setRESTEndpointValidationError(true);

			return false;
		}

		return true;
	};

	const RestApplicationDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className: 'fds-entries-dropdown-menu',
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
					setSelectedRESTApplication(item);

					setRequiredRESTApplicationValidationError(false);

					getRESTSchemas(item, resolvedRESTSchemas);
				}}
				restApplications={restApplications!}
			/>
		</ClayDropDown>
	);

	const RestSchemaDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className: 'fds-entries-dropdown-menu',
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

					const endpoints = restSchemaEndpoints.get(item);

					if (endpoints?.length === 1) {
						setSelectedRESTEndpoint(endpoints[0]);
					}
					else {
						setSelectedRESTEndpoint(null);
					}

					setRESTSchemaValidationError(false);
				}}
				restSchemas={Array.from(restSchemaEndpoints.keys())}
			/>
		</ClayDropDown>
	);

	const RestEndpointDropdown = () => (
		<ClayDropDown
			menuElementAttrs={{
				className: 'fds-entries-dropdown-menu',
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

					setRESTEndpointValidationError(false);
				}}
				restEndpoints={
					restSchemaEndpoints.get(selectedRESTSchema ?? '') ?? []
				}
			/>
		</ClayDropDown>
	);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('new-data-set')}
			</ClayModal.Header>

			<ClayModal.Body>
				<LabelInput
					labelValidationError={labelValidationError}
					namespace={namespace}
					onBlur={() => {
						setLabelValidationError(!label);
					}}
					onChange={setLabel}
					value={label}
				/>

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

				{restSchemaEndpoints.size > 0 && (
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
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							disabled={saveButtonDisabled}
							onClick={() => {
								setSaveButtonDisabled(true);

								const success = validate();

								if (success) {
									saveDataSet();
								}
								else {
									setSaveButtonDisabled(false);
								}
							}}
						>
							{Liferay.Language.get('save')}
						</ClayButton>

						<ClayButton
							displayType="secondary"
							onClick={() => closeModal()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
};

const CustomDataSets = ({
	editDataSetURL,
	hasAddDataSetObjectEntryPermission,
	namespace,
	permissionsURL,
	resolvedRESTSchemas,
	restApplications,
	systemDataSets,
}: {
	editDataSetURL: string;
	hasAddDataSetObjectEntryPermission: boolean;
	namespace: string;
	permissionsURL: string;
	resolvedRESTSchemas: Array<string>;
	restApplications: Array<string>;
	systemDataSets: Array<ISystemDataSet>;
}) => {
	const getAPIURL = () => {
		let params;

		if (systemDataSets.length) {
			const systemDataSetNames: string = systemDataSets
				.map((systemDataSet) => `'${systemDataSet.name}'`)
				.join(',');

			params = {
				filter: `not (externalReferenceCode in (${systemDataSetNames}))`,
			};
		}

		return getDataSetResourceURL({
			params,
		});
	};

	const getEditURL = (itemData: IDataSet) => {
		const url = new URL(editDataSetURL);

		url.searchParams.set(
			`${namespace}dataSetERC`,
			itemData.externalReferenceCode
		);
		url.searchParams.set(`${namespace}dataSetLabel`, itemData.label);

		return url;
	};

	const onDeleteClick = ({
		itemData,
		loadData,
	}: {
		itemData: IDataSet;
		loadData: Function;
	}) => {
		openModal({
			bodyHTML: Liferay.Language.get(
				'deleting-a-data-set-is-an-action-that-cannot-be-reversed'
			),
			buttons: [
				{
					autoFocus: true,
					displayType: 'secondary',
					label: Liferay.Language.get('cancel'),
					type: 'cancel',
				},
				{
					displayType: 'danger',
					label: Liferay.Language.get('delete'),
					onClick: ({processClose}: {processClose: Function}) => {
						processClose();

						fetch(itemData.actions.delete.href, {
							headers: DEFAULT_FETCH_HEADERS,
							method: itemData.actions.delete.method,
						})
							.then(() => {
								openDefaultSuccessToast();

								loadData();
							})
							.catch(openDefaultFailureToast);
					},
				},
			],
			status: 'danger',
			title: Liferay.Language.get('delete-data-set'),
		});
	};

	const restApplicationRenderer = function ({
		itemData,
	}: {
		itemData: IDataSet;
	}) {
		const apiExplorerURL = getAPIExplorerURL(itemData.restApplication);

		return (
			<ClayTooltipProvider>
				<ClayLink
					data-tooltip-align="top"
					decoration="underline"
					displayType="tertiary"
					href={apiExplorerURL}
					rel="noopener noreferrer"
					target="_blank"
					title={apiExplorerURL}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon
							className="mr-1 text-2 text-secondary"
							symbol="shortcut"
						/>
					</span>

					{itemData.restApplication}
				</ClayLink>
			</ClayTooltipProvider>
		);
	};

	const creationMenu = {
		primaryItems: [
			{
				label: Liferay.Language.get('new-data-set'),
				onClick: ({loadData}: {loadData: Function}) => {
					openModal({
						contentComponent: ({
							closeModal,
						}: {
							closeModal: Function;
						}) => (
							<NewDataSetModalContent
								closeModal={closeModal}
								loadData={loadData}
								namespace={namespace}
								resolvedRESTSchemas={resolvedRESTSchemas}
								restApplications={restApplications}
							/>
						),
					});
				},
			},
		],
	};

	const views = [
		{
			contentRenderer: 'table',
			name: 'table',
			schema: {
				fields: [
					{
						actionId: 'edit',
						contentRenderer: 'actionLink',
						fieldName: 'label',
						label: Liferay.Language.get('name'),
						sortable: true,
					},
					{
						contentRenderer: 'restApplicationRenderer',
						fieldName: 'restApplication',
						label: Liferay.Language.get('rest-application'),
						sortable: true,
					},
					{
						fieldName: 'restSchema',
						label: Liferay.Language.get('rest-schema'),
						sortable: true,
					},
					{
						fieldName: 'restEndpoint',
						label: Liferay.Language.get('rest-endpoint'),
						sortable: true,
					},
					{
						contentRenderer: 'dateTime',
						fieldName: 'dateModified',
						label: Liferay.Language.get('modified-date'),
						sortable: true,
					},
				],
			},
		},
	];

	return (
		<div className="custom-data-sets data-sets">
			<FrontendDataSet
				{...FDS_DEFAULT_PROPS}
				apiURL={getAPIURL()}
				creationMenu={
					hasAddDataSetObjectEntryPermission
						? creationMenu
						: undefined
				}
				customDataRenderers={{restApplicationRenderer}}
				emptyState={{
					description: Liferay.Language.get(
						'start-creating-one-to-show-your-data'
					),
					image: '/states/empty_state.svg',
					title: Liferay.Language.get('no-data-sets-created'),
				}}
				id={`${namespace}CustomDataSets`}
				itemsActions={[
					{
						data: {
							id: 'edit',
							permissionKey: 'update',
						},
						icon: 'pencil',
						label: Liferay.Language.get('edit'),
						onClick: ({itemData}: {itemData: IDataSet}) => {
							navigate(getEditURL(itemData));
						},
					},
					{
						data: {
							permissionKey: 'permissions',
							size: 'full-screen',
							title: Liferay.Language.get('permissions'),
						},
						href: permissionsURL,
						icon: 'password-policies',
						label: Liferay.Language.get('permissions'),
						target: 'modal-permissions',
					},
					{
						data: {
							permissionKey: 'delete',
						},
						icon: 'trash',
						label: Liferay.Language.get('delete'),
						onClick: onDeleteClick,
					},
				]}
				sorts={[{direction: 'desc', key: 'dateCreated'}]}
				views={views}
			/>
		</div>
	);
};

export default CustomDataSets;
