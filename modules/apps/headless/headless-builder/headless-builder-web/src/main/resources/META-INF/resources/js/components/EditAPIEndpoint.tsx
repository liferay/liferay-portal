/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import ClayCard from '@clayui/card';
import ClayTabs from '@clayui/tabs';
import {openModal, openToast} from 'frontend-js-components-web';
import React, {
	Dispatch,
	SetStateAction,
	useCallback,
	useContext,
	useEffect,
	useState,
} from 'react';

import {EditAPIApplicationContext} from './EditAPIApplicationContext';
import EditEndpointConfiguration from './EditEndpointConfiguration';
import BaseAPIEndpointFields from './baseComponents/BaseAPIEndpointFields';
import {CancelEditAPIApplicationModalContent} from './modals/CancelEditAPIApplicationModalContent';
import {hasEndpointDataChanged} from './utils/dataUtils';
import {deleteData, fetchJSON, postData, updateData} from './utils/fetchUtil';

import '../../css/main.scss';
import {HTTP_METHODS, RETRIEVE_TYPES, STR_BLANK} from './utils/constants';
import {
	beginStringWithForwardSlash,
	getAllButLastParameterFromPath,
	getLastParameterFromPath,
} from './utils/string';

interface EditAPIEndpointProps {
	apiApplicationBaseURL: string;
	apiURLPaths: APIURLPaths;
	basePath: string;
	currentAPIApplicationId: string;
	endpointId: number;
	setMainEndpointNav: Dispatch<SetStateAction<MainNav>>;
	setManagementButtonsProps: Dispatch<SetStateAction<ManagementButtonsProps>>;
	setStatus: Dispatch<SetStateAction<ApplicationStatusKeys>>;
	setTitle: Dispatch<SetStateAction<string>>;
}

export default function EditAPIEndpoint({
	apiApplicationBaseURL,
	apiURLPaths,
	basePath,
	currentAPIApplicationId,
	endpointId,
	setMainEndpointNav,
	setManagementButtonsProps,
	setStatus,
	setTitle,
}: EditAPIEndpointProps) {
	const {
		fetchedData,
		isDataUnsaved,
		setFetchedData,
		setHideManagementButtons,
		setIsDataUnsaved,
	} = useContext(EditAPIApplicationContext);
	const [activeTab, setActiveTab] = useState(0);
	const [localUIData, setLocalUIData] = useState<Partial<APIEndpointUIData>>(
		{}
	);
	const [displayError, setDisplayError] = useState<EndpointDataError>({
		httpMethod: false,
		parameter: false,
		path: false,
		pathParameter: false,
		r_requestAPISchemaToAPIEndpoints_l_apiSchemaId: false,
		retrieveType: false,
		scope: false,
	});

	const fetchAPIEndpoint = () => {
		fetchJSON<APIEndpointItem>({
			input:
				apiURLPaths.endpoints +
				endpointId +
				'?nestedFields=apiEndpointToAPIFilters, apiEndpointToAPISorts',
		}).then((response) => {
			setFetchedData((previous) => ({
				...previous,
				apiEndpoint: response,
			}));

			setLocalUIData({
				...(response.apiEndpointToAPIFilters?.length && {
					apiEndpointToAPIFilters: response.apiEndpointToAPIFilters,
				}),
				...(response.apiEndpointToAPISorts?.length && {
					apiEndpointToAPISorts: response.apiEndpointToAPISorts,
				}),
				...(response.description && {
					description: response.description,
				}),
				httpMethod: response.httpMethod,
				parameter: getLastParameterFromPath(response.path),
				path: getAllButLastParameterFromPath(response.path),
				...(response.pathParameter && {
					pathParameter: response.pathParameter,
				}),
				...(response.pathParameterDescription && {
					pathParameterDescription: response.pathParameterDescription,
				}),
				...(response.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId && {
					r_requestAPISchemaToAPIEndpoints_l_apiSchemaId:
						response.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId,
				}),
				...(response.r_responseAPISchemaToAPIEndpoints_l_apiSchemaId && {
					r_responseAPISchemaToAPIEndpoints_l_apiSchemaId:
						response.r_responseAPISchemaToAPIEndpoints_l_apiSchemaId,
				}),
				retrieveType: response.retrieveType,
				scope: response.scope,
			});
		});
	};

	function validateData() {
		let isDataValid = true;

		const mandatoryFields = ['httpMethod', 'path', 'retrieveType', 'scope'];

		if (
			localUIData.httpMethod?.key === HTTP_METHODS.GET &&
			localUIData.retrieveType?.key === RETRIEVE_TYPES.SINGLE_ELEMENT
		) {
			mandatoryFields.push('parameter');

			if (localUIData.r_responseAPISchemaToAPIEndpoints_l_apiSchemaId) {
				mandatoryFields.push('pathParameter');
			}
		}

		if (localUIData.httpMethod?.key === HTTP_METHODS.POST) {
			mandatoryFields.push(
				'r_requestAPISchemaToAPIEndpoints_l_apiSchemaId'
			);
		}

		if (!Object.keys(localUIData!).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as EndpointDataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData![field as keyof APIEndpointUIData]) {
					setDisplayError((previousErrors) => ({
						...previousErrors,
						[field]: false,
					}));
				}
				else {
					setDisplayError((previousErrors) => ({
						...previousErrors,
						[field]: true,
					}));

					isDataValid = false;
				}
			});
		}

		return isDataValid;
	}

	const handleUpdate = useCallback(
		({successMessage}: {successMessage: string}) => {
			const isDataValid = validateData();

			if (
				fetchedData?.apiEndpoint &&
				Object.keys(localUIData).length &&
				isDataValid
			) {
				if (
					localUIData.retrieveType?.key !==
					RETRIEVE_TYPES.SINGLE_ELEMENT
				) {
					handleModifyODataFields({
						deleteSuccessMessage: Liferay.Language.get(
							'the-filter-was-deleted'
						),
						fieldKey: 'Filter',
						postSuccessMessage: Liferay.Language.get(
							'the-filter-was-created'
						),
						updateSuccessMessage: Liferay.Language.get(
							'the-filter-was-updated'
						),
					});

					handleModifyODataFields({
						deleteSuccessMessage: Liferay.Language.get(
							'the-sort-was-deleted'
						),
						fieldKey: 'Sort',
						postSuccessMessage: Liferay.Language.get(
							'the-sort-was-created'
						),
						updateSuccessMessage: Liferay.Language.get(
							'the-sort-was-updated'
						),
					});
				}

				let parameter: string | undefined = STR_BLANK;

				if (
					localUIData.httpMethod?.key === HTTP_METHODS.GET &&
					localUIData.retrieveType?.key ===
						RETRIEVE_TYPES.SINGLE_ELEMENT
				) {
					parameter = localUIData.parameter;
				}

				updateData<APIEndpointItem>({
					dataToUpdate: {
						description: localUIData.description
							? localUIData.description
							: STR_BLANK,
						...(localUIData.path && {
							path: beginStringWithForwardSlash(
								localUIData.path +
									beginStringWithForwardSlash(parameter)
							),
						}),
						httpMethod: {
							key: localUIData.httpMethod?.key!,
							name: localUIData.httpMethod?.name!,
						},
						pathParameter: localUIData.pathParameter
							? localUIData.pathParameter
							: STR_BLANK,
						pathParameterDescription:
							localUIData.pathParameterDescription
								? localUIData.pathParameterDescription
								: STR_BLANK,
						...(localUIData.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId && {
							r_requestAPISchemaToAPIEndpoints_l_apiSchemaId:
								localUIData.r_requestAPISchemaToAPIEndpoints_l_apiSchemaId,
						}),
						r_responseAPISchemaToAPIEndpoints_l_apiSchemaId:
							localUIData.r_responseAPISchemaToAPIEndpoints_l_apiSchemaId,
						retrieveType: localUIData.retrieveType,
						scope: localUIData.scope,
					},
					method: 'PATCH',
					onError: (error: string) => {
						openToast({
							message: error,
							type: 'danger',
						});
					},
					onSuccess: (responseJSON) => {
						setFetchedData((previous) => ({
							...previous,
							apiEndpoint: {
								...responseJSON,
								...(previous.apiEndpoint
									?.apiEndpointToAPIFilters?.length && {
									apiEndpointToAPIFilters:
										previous.apiEndpoint
											.apiEndpointToAPIFilters,
								}),
								...(previous.apiEndpoint?.apiEndpointToAPISorts
									?.length && {
									apiEndpointToAPISorts:
										previous.apiEndpoint
											.apiEndpointToAPISorts,
								}),
							},
						}));
						openToast({
							message: successMessage,
							type: 'success',
						});
					},
					url: fetchedData.apiEndpoint.actions.update.href,
				});
			}
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[localUIData]
	);

	async function handleModifyODataFields({
		deleteSuccessMessage,
		fieldKey,
		postSuccessMessage,
		updateSuccessMessage,
	}: {
		deleteSuccessMessage: string;
		fieldKey: 'Filter' | 'Sort';
		postSuccessMessage: string;
		updateSuccessMessage: string;
	}) {
		if (
			fetchedData.apiEndpoint?.[`apiEndpointToAPI${fieldKey}s`] &&
			!fetchedData.apiEndpoint[`apiEndpointToAPI${fieldKey}s`].length &&
			(fieldKey === 'Filter'
				? localUIData[`apiEndpointToAPI${fieldKey}s`]?.[0]?.[
						`oData${fieldKey}` as keyof APIEndpointFilter
					]
				: localUIData[`apiEndpointToAPI${fieldKey}s`]?.[0]?.[
						`oData${fieldKey}` as keyof APIEndpointSort
					])
		) {
			postData<APIEndpointFilter | APIEndpointSort>({
				data: {
					[`oData${fieldKey}`]:
						fieldKey === 'Filter'
							? localUIData[`apiEndpointToAPIFilters`]?.[0][
									`oData${fieldKey}`
								]
							: localUIData[`apiEndpointToAPISorts`]?.[0][
									`oData${fieldKey}`
								],
					[`r_apiEndpointToAPI${fieldKey}s_l_apiEndpointId`]:
						fetchedData.apiEndpoint.id,
				},
				onError: (error: string) => {
					openToast({
						message: error,
						type: 'danger',
					});
				},
				onSuccess: (responseJSON) => {
					setFetchedData((previous) => ({
						...previous,
						apiEndpoint: {
							...previous.apiEndpoint!,
							[`apiEndpointToAPI${fieldKey}s`]: [responseJSON],
						},
					}));
					openToast({
						message: postSuccessMessage,
						type: 'success',
					});
				},
				url: apiURLPaths[
					`${fieldKey.toLocaleLowerCase()}s` as keyof APIURLPaths
				],
			});
		}
		else if (
			(fieldKey === 'Filter'
				? fetchedData.apiEndpoint?.[`apiEndpointToAPI${fieldKey}s`][0][
						`oData${fieldKey}` as keyof APIEndpointFilter
					]
				: fetchedData.apiEndpoint?.[`apiEndpointToAPI${fieldKey}s`][0][
						`oData${fieldKey}` as keyof APIEndpointSort
					]) &&
			(fieldKey === 'Filter'
				? localUIData[`apiEndpointToAPI${fieldKey}s`]?.[0]?.[
						`oData${fieldKey}` as keyof APIEndpointFilter
					]
				: localUIData[`apiEndpointToAPI${fieldKey}s`]?.[0]?.[
						`oData${fieldKey}` as keyof APIEndpointSort
					])
		) {
			updateData<APIEndpointFilter | APIEndpointSort>({
				dataToUpdate: {
					[`oData${fieldKey}`]:
						fieldKey === 'Filter'
							? localUIData[
									`apiEndpointToAPI${fieldKey}s`
								]?.[0]?.[
									`oData${fieldKey}` as keyof APIEndpointFilter
								]
							: localUIData[
									`apiEndpointToAPI${fieldKey}s`
								]?.[0]?.[
									`oData${fieldKey}` as keyof APIEndpointSort
								],
				},
				method: 'PATCH',
				onError: (error: string) => {
					openToast({
						message: error,
						type: 'danger',
					});
				},
				onSuccess: (responseJSON) => {
					setFetchedData((previous) => ({
						...previous,
						apiEndpoint: {
							...previous.apiEndpoint!,
							[`apiEndpointToAPI${fieldKey}s`]: [responseJSON],
						},
					}));
					openToast({
						message: updateSuccessMessage,
						type: 'success',
					});
				},
				url:
					apiURLPaths[
						`${fieldKey.toLocaleLowerCase()}s` as keyof APIURLPaths
					] +
					fetchedData.apiEndpoint?.[`apiEndpointToAPI${fieldKey}s`][0]
						.id,
			});
		}
		else if (
			localUIData[`apiEndpointToAPI${fieldKey}s`] &&
			fetchedData.apiEndpoint?.[`apiEndpointToAPI${fieldKey}s`] &&
			fetchedData.apiEndpoint[`apiEndpointToAPI${fieldKey}s`].length !==
				localUIData[`apiEndpointToAPI${fieldKey}s`]?.length
		) {
			deleteData({
				onError: (error: string) => {
					openToast({
						message: error,
						type: 'danger',
					});
				},
				onSuccess: () => {
					setFetchedData((previous) => ({
						...previous,
						apiEndpoint: {
							...previous.apiEndpoint!,
							[`apiEndpointToAPI${fieldKey}s`]: [],
						},
					}));
					openToast({
						message: deleteSuccessMessage,
						type: 'success',
					});
				},
				url:
					apiURLPaths[
						`${fieldKey.toLocaleLowerCase()}s` as keyof APIURLPaths
					] +
					fetchedData.apiEndpoint[`apiEndpointToAPI${fieldKey}s`][0]
						.id,
			});
		}
	}

	const handlePublish = ({successMessage}: {successMessage: string}) => {
		const isDataValid = validateData();
		if (localUIData && isDataValid) {
			updateData<APIApplicationItem>({
				dataToUpdate: {
					applicationStatus: {key: 'published'},
				},
				method: 'PATCH',
				onError: (error: string) => {
					openToast({
						message: error,
						type: 'danger',
					});
				},
				onSuccess: (responseJSON: APIApplicationItem) => {
					setFetchedData((previous) => ({
						...previous,
						apiApplication: responseJSON,
					}));
					setStatus(responseJSON.applicationStatus.key);
					setTitle(responseJSON.title);
					openToast({
						message: successMessage,
						type: 'success',
					});
				},
				url: apiURLPaths.applications + currentAPIApplicationId,
			});
		}
	};

	const handleCancel = useCallback(() => {
		if (isDataUnsaved) {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CancelEditAPIApplicationModalContent({
						closeModal,
						onConfirm: () => {
							setIsDataUnsaved(false);
							setMainEndpointNav('list');
						},
					}),
				id: 'confirmCancelEditModal',
				size: 'md',
				status: 'warning',
			});
		}
		else {
			setMainEndpointNav('list');
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isDataUnsaved]);

	useEffect(() => {
		setHideManagementButtons(false);

		fetchAPIEndpoint();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		if (fetchedData.apiEndpoint) {
			setIsDataUnsaved(
				hasEndpointDataChanged({
					fetchedEndpointData: fetchedData.apiEndpoint,
					localUIData,
				})
			);
		}

		setManagementButtonsProps({
			cancel: {onClick: handleCancel, visible: true},
			publish: {
				onClick: () => {
					handlePublish({
						successMessage: Liferay.Language.get(
							'api-application-was-published'
						),
					});
					handleUpdate({
						successMessage: Liferay.Language.get(
							'api-endpoint-changes-were-saved'
						),
					});
				},
				visible: true,
			},
			save: {
				onClick: () =>
					handleUpdate({
						successMessage: Liferay.Language.get(
							'api-endpoint-changes-were-saved'
						),
					}),
				visible:
					fetchedData.apiApplication?.applicationStatus.key ===
					'unpublished',
			},
		});

		for (const key in localUIData) {
			if (localUIData[key as keyof APIEndpointUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fetchedData, isDataUnsaved, localUIData]);

	const APIEndpointHttpMethodName =
		fetchedData.apiEndpoint?.httpMethod.name?.toUpperCase();
	const editAPIEndpointBreadcrumbLabel = `${
		APIEndpointHttpMethodName ?? APIEndpointHttpMethodName
	} ${fetchedData.apiEndpoint?.path ?? fetchedData.apiEndpoint?.path}`;

	return (
		<div className="container-fluid container-fluid-max-xl mt-3">
			<ClayBreadcrumb
				className="api-builder-navigation-breadcrumb"
				items={[
					{
						label: Liferay.Language.get('endpoints'),
						onClick: () => handleCancel(),
					},
					{
						active: true,
						label: editAPIEndpointBreadcrumbLabel,
					},
				]}
			/>

			<ClayCard className="mt-3 pt-2">
				<ClayTabs
					active={activeTab}
					className="mt-3"
					onActiveChange={setActiveTab}
				>
					<ClayTabs.Item
						innerProps={{
							'aria-controls': 'tabpanel-1',
						}}
					>
						{Liferay.Language.get('info')}
					</ClayTabs.Item>

					<ClayTabs.Item
						innerProps={{
							'aria-controls': 'tabpanel-2',
						}}
					>
						{Liferay.Language.get('configuration')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTab} fade>
					{activeTab === 0 && (
						<ClayTabs.TabPane
							aria-label={Liferay.Language.get('information-tab')}
							className="info-tab"
						>
							<ClayCard.Body>
								<div className="endpoints-fields-card-body">
									<BaseAPIEndpointFields
										apiApplicationBaseURL={
											apiApplicationBaseURL
										}
										basePath={basePath}
										data={localUIData}
										displayError={displayError}
										editing
										setData={setLocalUIData}
									/>
								</div>
							</ClayCard.Body>
						</ClayTabs.TabPane>
					)}

					{activeTab === 1 && (
						<ClayTabs.TabPane
							aria-label={Liferay.Language.get(
								'configuration-tab'
							)}
							className="info-tab"
						>
							<ClayCard.Body>
								<div className="endpoints-fields-card-body">
									<EditEndpointConfiguration
										currentAPIApplicationId={
											currentAPIApplicationId
										}
										data={localUIData}
										displayError={displayError}
										schemaAPIURLPath={apiURLPaths.schemas}
										setData={setLocalUIData}
									/>
								</div>
							</ClayCard.Body>
						</ClayTabs.TabPane>
					)}
				</ClayTabs.Content>
			</ClayCard>
		</div>
	);
}
