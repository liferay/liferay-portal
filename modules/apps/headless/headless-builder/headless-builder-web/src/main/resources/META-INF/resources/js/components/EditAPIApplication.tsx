/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayCard from '@clayui/card';
import {Heading} from '@clayui/core';
import ClayLayout from '@clayui/layout';
import ClayModal from '@clayui/modal';
import ClayNavigationBar from '@clayui/navigation-bar';
import {openModal, openToast} from 'frontend-js-components-web';
import {localStorage} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import EndpointsContent from '../components/EndpointsContent';
import SchemasContent from '../components/SchemasContent';
import {APIApplicationManagementToolbar} from './APIApplicationManagementToolbar';
import {EditAPIApplicationContext} from './EditAPIApplicationContext';
import BaseAPIApplicationField from './baseComponents/BaseAPIApplicationFields';
import {CancelEditAPIApplicationModalContent} from './modals/CancelEditAPIApplicationModalContent';
import {hasDataChanged, resetToFetched} from './utils/dataUtils';
import {fetchJSON, updateData} from './utils/fetchUtil';
import {
	getCurrentNavFromURL,
	getCurrentURLParamValue,
	updateHistory,
} from './utils/urlUtil';

import '../../css/main.scss';

interface EditAPIApplicationProps {
	apiURLPaths: APIURLPaths;
	basePath: string;
	portletId: string;
}

export default function EditAPIApplication({
	apiURLPaths,
	basePath,
	portletId,
}: EditAPIApplicationProps) {
	const currentAPIApplicationId = getCurrentURLParamValue({
		paramSufix: 'apiApplicationId',
		portletId,
	});

	const [activeNav, setActiveNav] = useState<ActiveNav>(
		getCurrentNavFromURL({
			portletId,
		})
	);

	const [displayError, setDisplayError] = useState<ApplicationDataError>({
		baseURL: false,
		title: false,
	});

	const [fetchedData, setFetchedData] = useState<FetchedData>({});

	const [hideManagementButtons, setHideManagementButtons] =
		useState<boolean>(false);

	const [isDataUnsaved, setIsDataUnsaved] = useState<boolean>(false);

	const [localUIData, setLocalUIData] = useState<APIApplicationUIData>({
		baseURL: '',
		description: '',
		title: '',
	});

	const defaultButtonProps = {onClick: () => {}, visible: true};
	const [managementButtonsProps, setManagementButtonsProps] =
		useState<ManagementButtonsProps>({
			cancel: defaultButtonProps,
			publish: defaultButtonProps,
			save: defaultButtonProps,
		});

	const [title, setTitle] = useState<string>('');
	const [status, setStatus] = useState<ApplicationStatusKeys>('unpublished');

	const fetchAPIApplication = () => {
		fetchJSON<APIApplicationItem>({
			input: apiURLPaths.applications + currentAPIApplicationId,
		}).then((response) => {
			if (response.id.toString() === currentAPIApplicationId) {
				setFetchedData((previous) => ({
					...previous,
					apiApplication: response,
				}));
				setLocalUIData({
					baseURL: response.baseURL,
					description: response.description,
					title: response.title,
				});
				setStatus(response.applicationStatus.key);
				setTitle(response.title);
			}
		});
	};

	const handleCancel = () => {
		if (
			fetchedData?.apiApplication &&
			hasDataChanged({
				fetchedEntityData: fetchedData.apiApplication,
				localUIData,
			})
		) {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CancelEditAPIApplicationModalContent({
						closeModal,
						onConfirm: () => history.back(),
					}),
				id: 'confirmCancelEditModal',
				size: 'md',
				status: 'warning',
			});
		}
		else {
			history.back();
		}
	};

	const handleUpdate = useCallback(
		({
			applicationStatusKey,
			successMessage,
		}: {
			applicationStatusKey: 'published' | 'unpublished';
			successMessage: string;
		}) => {
			const isDataValid = validateData();

			if (isDataValid && localUIData && fetchedData.apiApplication) {
				updateData<APIApplicationItem>({
					dataToUpdate: {
						applicationStatus: {key: applicationStatusKey},
						baseURL: localUIData.baseURL,
						description: localUIData.description,
						title: localUIData.title,
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
						setIsDataUnsaved(false);
						setTitle(responseJSON.title);
						setStatus(responseJSON.applicationStatus.key);
						openToast({
							message: successMessage,
							type: 'success',
						});
					},
					url: fetchedData.apiApplication.actions.update.href,
				});
			}
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[localUIData]
	);

	const updateManagementButtons = () => {
		setManagementButtonsProps({
			cancel: {onClick: handleCancel, visible: true},
			publish: {
				onClick: () =>
					handleUpdate({
						applicationStatusKey: 'published',
						successMessage: Liferay.Language.get(
							'api-application-was-published'
						),
					}),
				visible: true,
			},
			save: {
				onClick: () =>
					handleUpdate({
						applicationStatusKey: 'unpublished',
						successMessage: Liferay.Language.get(
							'api-application-changes-were-saved'
						),
					}),
				visible:
					fetchedData?.apiApplication?.applicationStatus?.key ===
					'unpublished',
			},
		});
	};

	const handleNavigate = (nav: ActiveNav) => {
		const navigate = () => {
			if (nav === 'details') {
				setHideManagementButtons(false);
				updateManagementButtons();
			}
			updateHistory({navState: nav, portletId});
			setActiveNav(nav);
		};

		if (nav !== activeNav && isDataUnsaved) {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CancelEditAPIApplicationModalContent({
						closeModal,
						onConfirm: () => {
							if (fetchedData.apiApplication) {
								setLocalUIData(
									resetToFetched<
										APIApplicationItem,
										APIApplicationUIData
									>({
										fetchedEntityData:
											fetchedData.apiApplication,
										localUIData,
									})
								);
							}

							setIsDataUnsaved(false);
							navigate();
						},
					}),
				id: 'confirmCancelEditModal',
				size: 'md',
				status: 'warning',
			});
		}
		else {
			navigate();
		}
	};

	useEffect(() => {
		if (fetchedData.apiApplication) {
			setIsDataUnsaved(
				hasDataChanged({
					fetchedEntityData: fetchedData.apiApplication,
					localUIData,
				})
			);
		}

		for (const key in localUIData) {
			if (localUIData[key as keyof APIApplicationUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}

		updateManagementButtons();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [localUIData]);

	useEffect(() => {
		updateManagementButtons();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [fetchedData.apiApplication]);

	useEffect(() => {
		fetchAPIApplication();

		if (
			localStorage.getItem('justCreated', localStorage.TYPES.FUNCTIONAL)
		) {
			openToast({
				message: Liferay.Language.get(
					'new-api-application-was-created'
				),
				type: 'success',
			});
			localStorage.removeItem('justCreated');
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	function validateData() {
		let isDataValid = true;
		const mandatoryFields = ['baseURL', 'title'];

		if (!Object.keys(localUIData).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as ApplicationDataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData[field as keyof APIApplicationUIData]) {
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

	return fetchedData && currentAPIApplicationId && managementButtonsProps ? (
		<EditAPIApplicationContext.Provider
			value={{
				fetchedData,
				isDataUnsaved,
				setFetchedData,
				setHideManagementButtons,
				setIsDataUnsaved,
			}}
		>
			<>
				<APIApplicationManagementToolbar
					applicationStatusKey={status}
					hideManagementButtons={hideManagementButtons}
					managementButtonsProps={managementButtonsProps}
					title={title}
				/>
				<ClayNavigationBar triggerLabel={activeNav as string}>
					<ClayNavigationBar.Item active={activeNav === 'details'}>
						<ClayButton onClick={() => handleNavigate('details')}>
							{Liferay.Language.get('details')}
						</ClayButton>
					</ClayNavigationBar.Item>

					<ClayNavigationBar.Item active={activeNav === 'endpoints'}>
						<ClayButton onClick={() => handleNavigate('endpoints')}>
							{Liferay.Language.get('endpoints')}
						</ClayButton>
					</ClayNavigationBar.Item>

					<ClayNavigationBar.Item active={activeNav === 'schemas'}>
						<ClayButton onClick={() => handleNavigate('schemas')}>
							{Liferay.Language.get('schemas')}
						</ClayButton>
					</ClayNavigationBar.Item>
				</ClayNavigationBar>
				{activeNav === 'details' && (
					<ClayLayout.Container className="api-app-details mt-5">
						<ClayCard className="pt-2">
							<ClayModal.Header withTitle={false}>
								<Heading
									fontSize={5}
									level={3}
									weight="semi-bold"
								>
									{Liferay.Language.get('details')}
								</Heading>
							</ClayModal.Header>

							<ClayCard.Body>
								<div className="application-fields-card-body">
									<BaseAPIApplicationField
										basePath={basePath}
										data={localUIData}
										disableURLAutoFill
										displayError={displayError}
										setData={setLocalUIData}
									/>
								</div>
							</ClayCard.Body>
						</ClayCard>
					</ClayLayout.Container>
				)}
				{activeNav === 'endpoints' && (
					<EndpointsContent
						apiApplicationData={localUIData}
						apiURLPaths={apiURLPaths}
						basePath={basePath}
						currentAPIApplicationId={currentAPIApplicationId}
						portletId={portletId}
						setManagementButtonsProps={setManagementButtonsProps}
						setStatus={setStatus}
						setTitle={setTitle}
					/>
				)}
				{activeNav === 'schemas' && (
					<SchemasContent
						apiURLPaths={apiURLPaths}
						currentAPIApplicationId={currentAPIApplicationId}
						portletId={portletId}
						setManagementButtonsProps={setManagementButtonsProps}
						setStatus={setStatus}
						setTitle={setTitle}
					/>
				)}
			</>
		</EditAPIApplicationContext.Provider>
	) : null;
}
