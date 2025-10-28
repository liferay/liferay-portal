/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {Dispatch, SetStateAction, useEffect, useState} from 'react';

import BaseAPISchemaFields from '../baseComponents/BaseAPISchemaFields';
import {headers} from '../utils/fetchUtil';

interface CreateAPISchemaModalProps {
	apiSchemasURLPath: string;
	closeModal: voidReturn;
	currentAPIApplicationId: string | null;
	loadData: voidReturn;
	setMainSchemaNav: Dispatch<SetStateAction<MainNav>>;
}

export function CreateAPISchemaModalContent({
	apiSchemasURLPath,
	closeModal,
	currentAPIApplicationId,
	loadData,
	setMainSchemaNav,
}: CreateAPISchemaModalProps) {
	const [localUIData, setLocalUIData] = useState<APISchemaUIData>({
		description: '',
		mainObjectDefinitionERC: '',
		name: '',
	});
	const [displayError, setDisplayError] = useState<SchemaDataError>({
		description: false,
		mainObjectDefinitionERC: false,
		name: false,
	});

	useEffect(() => {
		for (const key in localUIData) {
			if (localUIData[key as keyof APISchemaUIData] !== '') {
				setDisplayError((previousErrors) => ({
					...previousErrors,
					[key]: false,
				}));
			}
		}
	}, [localUIData]);

	async function postData() {
		fetch(apiSchemasURLPath, {
			body: JSON.stringify({
				...localUIData,
				applicationStatus: {key: 'unpublished'},
				r_apiApplicationToAPISchemas_l_apiApplicationId:
					currentAPIApplicationId,
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
				setMainSchemaNav({edit: responseJSON.id});
				openToast({
					message: Liferay.Language.get(
						'new-api-application-schema-was-created'
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
		const mandatoryFields = ['mainObjectDefinitionERC', 'name'];

		if (!Object.keys(localUIData).length) {
			const errors = mandatoryFields.reduce(
				(errors, field) => ({...errors, [field]: true}),
				{}
			);
			setDisplayError(errors as SchemaDataError);

			isDataValid = false;
		}
		else {
			mandatoryFields.forEach((field) => {
				if (localUIData[field as keyof APISchemaUIData]) {
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
				{Liferay.Language.get('new-schema')}
			</ClayModal.Header>

			<div className="modal-body">
				<BaseAPISchemaFields
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
