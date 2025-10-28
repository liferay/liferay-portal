/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {Dispatch, SetStateAction, useEffect, useState} from 'react';

import BaseAPIEndpointFields from '../baseComponents/BaseAPIEndpointFields';
import {HTTP_METHODS, RETRIEVE_TYPES, STR_BLANK} from '../utils/constants';
import {headers} from '../utils/fetchUtil';
import {beginStringWithForwardSlash} from '../utils/string';

interface CreateAPIEndpointModalProps {
	apiApplicationBaseURL: string;
	apiEndpointsURLPath: string;
	basePath: string;
	closeModal: voidReturn;
	currentAPIApplicationId: string | null;
	loadData: voidReturn;
	setMainEndpointNav: Dispatch<SetStateAction<MainNav>>;
}

export function CreateAPIEndpointModalContent({
	apiApplicationBaseURL,
	apiEndpointsURLPath,
	basePath,
	closeModal,
	currentAPIApplicationId,
	loadData,
	setMainEndpointNav,
}: CreateAPIEndpointModalProps) {
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

	useEffect(() => {
		for (const key in localUIData) {
			if (localUIData[key as keyof APIEndpointUIData] !== STR_BLANK) {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}
	}, [localUIData]);

	async function postData() {
		let parameter: string | undefined = STR_BLANK;

		if (
			localUIData.httpMethod?.key === HTTP_METHODS.GET &&
			localUIData.retrieveType?.key === RETRIEVE_TYPES.SINGLE_ELEMENT
		) {
			parameter = localUIData.parameter;
		}

		fetch(apiEndpointsURLPath, {
			body: JSON.stringify({
				...localUIData,
				applicationStatus: {key: 'unpublished'},
				httpMethod: {
					key: localUIData.httpMethod?.key!,
					name: localUIData.httpMethod?.key!,
				},
				name: localUIData.path,
				...(localUIData.path && {
					path: beginStringWithForwardSlash(
						localUIData.path +
							beginStringWithForwardSlash(parameter)
					),
				}),
				r_apiApplicationToAPIEndpoints_l_apiApplicationId:
					currentAPIApplicationId,
				...(localUIData.scope?.key && {
					scope: {key: localUIData.scope.key},
				}),
				version: '1.0',
			}),
			headers,
			method: 'POST',
		})
			.then((response) => {
				if (response.ok) {
					return response.json();
				}
				else {
					throw response.json();
				}
			})
			.then((responseJSON) => {
				loadData();
				closeModal();
				setMainEndpointNav({edit: responseJSON.id});
				openToast({
					message: Liferay.Language.get(
						'new-api-application-endpoint-was-created'
					),
					type: 'success',
				});
			})
			.catch((error) => {
				error.then((response: {message: string; title: string}) => {
					{
						openToast({
							message: response.title ?? response.message,
							type: 'danger',
						});
					}
				});
			});
	}

	function validateData() {
		let isDataValid = true;
		const mandatoryFields = ['httpMethod', 'path', 'retrieveType', 'scope'];

		if (
			localUIData.httpMethod?.key === HTTP_METHODS.GET &&
			localUIData.retrieveType?.key === RETRIEVE_TYPES.SINGLE_ELEMENT
		) {
			mandatoryFields.push('parameter');
		}

		if (!Object.keys(localUIData).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as EndpointDataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData[field as keyof APIEndpointUIData]) {
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

	const handleCreate = () => {
		const isDataValid = validateData();

		if (isDataValid) {
			postData();
		}
		else {
			return;
		}
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('new-api-endpoint')}
			</ClayModal.Header>

			<div className="modal-body">
				<BaseAPIEndpointFields
					apiApplicationBaseURL={apiApplicationBaseURL}
					basePath={basePath}
					data={localUIData}
					displayError={displayError}
					setData={setLocalUIData}
				/>
			</div>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							id="modalCancelButton"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							id="modalCreateButton"
							onClick={handleCreate}
							type="button"
						>
							{Liferay.Language.get('create')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
