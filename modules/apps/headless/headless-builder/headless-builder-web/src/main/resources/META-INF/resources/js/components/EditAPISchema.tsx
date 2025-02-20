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

import {
	EditAPIApplicationContext,
	EditSchemaContext,
} from './EditAPIApplicationContext';
import EditAPISchemaProperties from './EditAPISchemaProperties';
import BaseAPISchemaFields from './baseComponents/BaseAPISchemaFields';
import {CancelEditAPIApplicationModalContent} from './modals/CancelEditAPIApplicationModalContent';
import Sidebar from './sidebar/Sidebar';
import {
	AddObjectFieldsDataToProperties,
	hasDataChanged,
	hasPropertiesDataChanged,
	resetToFetched,
} from './utils/dataUtils';
import {fetchJSON, getAllItems, updateData} from './utils/fetchUtil';

import '../../css/main.scss';

interface EditAPISchemaProps {
	apiURLPaths: APIURLPaths;
	currentAPIApplicationId: string;
	schemaId: number;
	setMainSchemaNav: Dispatch<SetStateAction<MainNav>>;
	setManagementButtonsProps: Dispatch<SetStateAction<ManagementButtonsProps>>;
	setStatus: Dispatch<SetStateAction<ApplicationStatusKeys>>;
	setTitle: Dispatch<SetStateAction<string>>;
}

export default function EditAPISchema({
	apiURLPaths,
	currentAPIApplicationId,
	schemaId,
	setMainSchemaNav,
	setManagementButtonsProps,
	setStatus,
	setTitle,
}: EditAPISchemaProps) {
	const {
		fetchedData,
		isDataUnsaved,
		setFetchedData,
		setHideManagementButtons,
		setIsDataUnsaved,
	} = useContext(EditAPIApplicationContext);

	const [activeTab, setActiveTab] = useState(0);

	const [displayError, setDisplayError] = useState<SchemaDataError>({
		description: false,
		mainObjectDefinitionERC: false,
		name: false,
	});

	const [fetchedSchemaData, setFetchedSchemaData] =
		useState<FetchedSchemaData>({});

	const [localUIData, setLocalUIData] = useState<APISchemaUIData>({
		description: '',
		mainObjectDefinitionERC: '',
		name: '',
		schemaProperties: [],
	});

	const fetchAPISchema = () => {
		fetchJSON<APISchemaItem>({
			input: apiURLPaths.schemas + schemaId,
		}).then((response) => {
			if (response.id === schemaId) {
				setFetchedSchemaData((previous) => ({
					...previous,
					apiSchema: response,
				}));

				setLocalUIData((previous) => ({
					...previous,
					description: response.description,
					mainObjectDefinitionERC: response.mainObjectDefinitionERC,
					name: response.name,
				}));
			}
		});
	};

	const fetchAPISchemaProperties = () => {
		getAllItems<APISchemaPropertyItem>({
			url: `/o/headless-builder/schemas/${schemaId}/apiSchemaToAPIProperties`,
		}).then((response) => {
			setFetchedSchemaData((previous) => ({
				...previous,
				schemaProperties: response.length ? response : [],
			}));

			if (response.length) {
				getAllItems<ObjectDefinition>({
					url: '/o/object-admin/v1.0/object-definitions',
				}).then((objectDefinitionsResponse) => {
					if (response.length && fetchedSchemaData.apiSchema) {
						setLocalUIData((previous) => ({
							...previous,
							schemaProperties: AddObjectFieldsDataToProperties({
								apiSchema: fetchedSchemaData.apiSchema!,
								objectDefinitions: objectDefinitionsResponse,
								schemaProperties: response,
							}),
						}));
					}
				});
			}
		});
	};

	const resetLocalUIData = useCallback(() => {
		if (fetchedData.apiSchema) {
			setLocalUIData(
				resetToFetched<APISchemaItem, APISchemaUIData>({
					fetchedEntityData: fetchedData.apiSchema,
					localUIData,
				})
			);
		}
	}, [fetchedData.apiSchema, localUIData]);

	function validateData() {
		let isDataValid = true;
		const mandatoryFields = ['name'];

		if (!Object.keys(localUIData!).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as SchemaDataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData![field as keyof APISchemaUIData]) {
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

	const handlePublish = useCallback(
		({successMessage}: {successMessage: string}) => {
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
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[localUIData]
	);

	const handleCancel = useCallback(() => {
		if (isDataUnsaved) {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CancelEditAPIApplicationModalContent({
						closeModal,
						onConfirm: () => {
							resetLocalUIData();
							setIsDataUnsaved(false);
							setMainSchemaNav('list');
						},
					}),
				id: 'confirmCancelEditModal',
				size: 'md',
				status: 'warning',
			});
		}
		else {
			setMainSchemaNav('list');
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fetchedSchemaData, isDataUnsaved, localUIData, resetLocalUIData]);

	const handleUpdate = useCallback(
		({successMessage}: {successMessage: string}) => {
			const isDataValid = validateData();

			if (
				localUIData?.schemaProperties &&
				isDataValid &&
				fetchedSchemaData.apiSchema
			) {
				updateData<APISchemaItem>({
					dataToUpdate: {
						description: localUIData.description,
						name: localUIData.name,
						...(localUIData.schemaProperties.length && {
							apiSchemaToAPIProperties:
								localUIData.schemaProperties.map(
									(property) => ({
										...(!!property.description && {
											description: property.description,
										}),
										externalReferenceCode:
											property.externalReferenceCode,
										name: property.name,
										objectFieldERC: property.objectFieldERC,
										r_apiPropertyToAPIProperties_l_apiPropertyId:
											property.r_apiPropertyToAPIProperties_l_apiPropertyId,
										r_apiSchemaToAPIProperties_l_apiSchemaId:
											property.r_apiSchemaToAPIProperties_l_apiSchemaId,
										...(property.objectRelationshipNames && {
											objectRelationshipNames:
												property.objectRelationshipNames,
										}),
									})
								),
						}),
						...(!localUIData.schemaProperties.length && {
							apiSchemaToAPIProperties: [],
						}),
						mainObjectDefinitionERC:
							localUIData.mainObjectDefinitionERC,
					},
					method: localUIData.schemaProperties.length
						? 'PATCH'
						: 'PUT',
					onError: (error: string) => {
						openToast({
							message: error,
							type: 'danger',
						});
					},
					onSuccess: () => {
						openToast({
							message: successMessage,
							type: 'success',
						});
						fetchAPISchema();
						fetchAPISchemaProperties();
						setIsDataUnsaved(false);
					},
					url: fetchedSchemaData.apiSchema.actions.update.href,
				});
			}
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[localUIData]
	);

	useEffect(() => {
		setHideManagementButtons(false);
		fetchAPISchema();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
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
							'api-schema-changes-were-saved'
						),
					});
				},
				visible: true,
			},
			save: {
				onClick: () =>
					handleUpdate({
						successMessage: Liferay.Language.get(
							'api-schema-changes-were-saved'
						),
					}),
				visible:
					fetchedData.apiApplication?.applicationStatus.key ===
					'unpublished',
			},
		});

		for (const key in localUIData) {
			if (localUIData[key as keyof APISchemaUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [isDataUnsaved, localUIData]);

	useEffect(() => {
		setIsDataUnsaved(
			!!(
				fetchedSchemaData.apiSchema &&
				(hasDataChanged({
					fetchedEntityData: fetchedSchemaData.apiSchema,
					localUIData: {
						description: localUIData.description,
						mainObjectDefinitionERC:
							localUIData.mainObjectDefinitionERC,
						name: localUIData.name,
					},
				}) ||
					(fetchedSchemaData.schemaProperties &&
						localUIData.schemaProperties &&
						hasPropertiesDataChanged({
							fetchedPropertiesData:
								fetchedSchemaData.schemaProperties,
							propertiesUIData: localUIData.schemaProperties,
						})))
			)
		);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fetchedSchemaData, localUIData]);

	return (
		<EditSchemaContext.Provider
			value={{
				apiSchemaId: schemaId,
				fetchedSchemaData,
				objectDefinitionBasePath:
					'/o/object-admin/v1.0/object-definitions/by-external-reference-code/',
				setFetchedSchemaData,
			}}
		>
			<div className="main-container">
				<div className="edit-schema">
					<div className="container-fluid container-fluid-max-xl edit-schema-child mt-3">
						<ClayBreadcrumb
							className="api-builder-navigation-breadcrumb"
							items={[
								{
									label: Liferay.Language.get('schemas'),
									onClick: () => {
										handleCancel();
									},
								},
								{
									active: true,
									label:
										fetchedData.apiSchema?.name ??
										localUIData.name,
								},
							]}
						/>

						<ClayCard className="mt-3 pt-2">
							<ClayTabs
								active={activeTab}
								className="mt-3"
								fade
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
									{Liferay.Language.get('properties')}
								</ClayTabs.Item>
							</ClayTabs>

							<ClayTabs.Content activeIndex={activeTab} fade>
								{activeTab === 0 && (
									<ClayTabs.TabPane
										aria-label={Liferay.Language.get(
											'information-tab'
										)}
										className="schema-tabs"
									>
										<ClayCard.Body>
											<div className="schema-fields-card-body">
												<BaseAPISchemaFields
													data={localUIData}
													disableObjectSelect
													displayError={displayError}
													setData={setLocalUIData}
												/>
											</div>
										</ClayCard.Body>
									</ClayTabs.TabPane>
								)}

								{activeTab === 1 && (
									<ClayTabs.TabPane
										aria-label={Liferay.Language.get(
											'properties-tab'
										)}
										className="schema-tabs"
									>
										<ClayCard.Body>
											<div className="schema-properties-card-body">
												{fetchedSchemaData.apiSchema &&
													fetchedSchemaData.objectDefinitions && (
														<EditAPISchemaProperties
															fetchedSchemaData={
																fetchedSchemaData
															}
															schemaId={schemaId}
															schemaUIData={
																localUIData
															}
															setFetchedSchemaData={
																setFetchedSchemaData
															}
															setSchemaUIData={
																setLocalUIData
															}
														/>
													)}
											</div>
										</ClayCard.Body>
									</ClayTabs.TabPane>
								)}
							</ClayTabs.Content>
						</ClayCard>
					</div>

					{activeTab === 1 && (
						<Sidebar
							mainObjectDefinitionERC={
								localUIData.mainObjectDefinitionERC
							}
							objectDefinitions={
								fetchedSchemaData.objectDefinitions
							}
							schemaUIData={localUIData}
							setSchemaUIData={setLocalUIData}
						/>
					)}
				</div>
			</div>
		</EditSchemaContext.Provider>
	);
}
