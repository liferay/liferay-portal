/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch, localStorage} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import BaseAPIApplicationField from '../baseComponents/BaseAPIApplicationFields';
import {headers} from '../utils/fetchUtil';
import {openEditURL} from '../utils/urlUtil';

interface HandleCreateInModal {
	apiApplicationsURLPath: string;
	basePath: string;
	closeModal: voidReturn;
	editURL: string;
	loadData: voidReturn;
	portletId: string;
}

export function CreateAPIApplicationModalContent({
	apiApplicationsURLPath,
	basePath,
	closeModal,
	editURL,
	loadData,
	portletId,
}: HandleCreateInModal) {
	const [localUIData, setLocalUIData] = useState<APIApplicationUIData>({
		baseURL: '',
		description: '',
		title: '',
	});
	const [displayError, setDisplayError] = useState<ApplicationDataError>({
		baseURL: false,
		title: false,
	});

	useEffect(() => {
		for (const key in localUIData) {
			if (localUIData[key as keyof APIApplicationUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}
	}, [localUIData]);

	async function postData() {
		fetch(apiApplicationsURLPath, {
			body: JSON.stringify({
				...localUIData,
				applicationStatus: {key: 'unpublished'},
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
				openEditURL({editURL, id: responseJSON.id, portletId});
				localStorage.setItem(
					'justCreated',
					true,
					localStorage.TYPES.FUNCTIONAL
				);
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
			<ClayModal.Header>
				{Liferay.Language.get('new-api-application')}
			</ClayModal.Header>

			<div className="modal-body">
				<BaseAPIApplicationField
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
