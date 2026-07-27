/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {
	ClayCheckbox,
	ClayInput,
	ClaySelectWithOption,
} from '@clayui/form';
import {Toolbar} from '@liferay/site-cms-site-initializer';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

interface IPIMConnector {
	key: string;
	name: string;
}

interface IPIMConnectorData {
	active: boolean;
	apiSchema: string;
	key: string;
	name: string;
}

interface IProps {
	apiURL: string;
	backURL: string;
	objectEntryId: number;
	pimConnector: IPIMConnectorData | null;
	pimConnectors: IPIMConnector[];
	title: string;
}

export default function EditPIMConnector({
	apiURL,
	backURL,
	objectEntryId,
	pimConnector,
	pimConnectors = [],
	title,
}: IProps) {
	const isNew = Number(objectEntryId) === 0;

	const [active, setActive] = useState(Boolean(pimConnector?.active));
	const [apiSchema, setApiSchema] = useState(pimConnector?.apiSchema || '');
	const [key, setKey] = useState(pimConnector?.key || '');
	const [name, setName] = useState(pimConnector?.name || '');

	useEffect(() => {
		if (!isNew && !pimConnector) {
			navigate(backURL);
		}
	}, [backURL, isNew, pimConnector]);

	const handleSave = async () => {
		try {
			const response = await fetch(
				isNew ? apiURL : `${apiURL}/${objectEntryId}`,
				{
					body: JSON.stringify({
						active,
						apiSchema,
						key,
						name,
					}),
					headers: {
						'Content-Type': 'application/json',
					},
					method: isNew ? 'POST' : 'PUT',
				}
			);

			if (!response.ok) {
				throw new Error();
			}

			Liferay.Util.openToast({
				message: sub(
					isNew
						? Liferay.Language.get('x-was-published-successfully')
						: Liferay.Language.get('x-was-updated-successfully'),
					name
				),
				type: 'success',
			});

			navigate(backURL);
		}
		catch (error) {
			Liferay.Util.openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<>
			<Toolbar backURL={backURL} title={title}>
				<Toolbar.Item>
					<ClayButton
						displayType="secondary"
						onClick={() => navigate(backURL)}
						size="sm"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton
						className="inline-item-after"
						disabled={!name.trim() || !key}
						displayType="primary"
						onClick={handleSave}
						size="sm"
					>
						{Liferay.Language.get('save')}
					</ClayButton>
				</Toolbar.Item>
			</Toolbar>

			<div className="container-fluid container-fluid-max-xl">
				<ClayForm>
					<ClayForm.Group>
						<label htmlFor="pimConnectorName">
							{Liferay.Language.get('name')}
						</label>

						<ClayInput
							id="pimConnectorName"
							onChange={(event) => setName(event.target.value)}
							required
							type="text"
							value={name}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="pimConnectorKey">
							{Liferay.Language.get('connector')}
						</label>

						<ClaySelectWithOption
							id="pimConnectorKey"
							onChange={(event) => setKey(event.target.value)}
							options={[
								{
									disabled: true,
									label: Liferay.Language.get(
										'select-a-connector'
									),
									value: '',
								},
								...pimConnectors.map((pimConnector) => ({
									label: pimConnector.name,
									value: pimConnector.key,
								})),
							]}
							required
							value={key}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="pimConnectorAPISchema">
							{Liferay.Language.get('api-schema')}
						</label>

						<textarea
							className="form-control"
							id="pimConnectorAPISchema"
							onChange={(event) =>
								setApiSchema(event.target.value)
							}
							rows={12}
							value={apiSchema}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<ClayCheckbox
							checked={active}
							label={Liferay.Language.get('active')}
							onChange={() =>
								setActive((previousActive) => !previousActive)
							}
						/>
					</ClayForm.Group>
				</ClayForm>
			</div>
		</>
	);
}
