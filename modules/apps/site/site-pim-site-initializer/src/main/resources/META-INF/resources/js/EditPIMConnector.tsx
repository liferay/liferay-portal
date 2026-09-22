/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {
	ClayInput,
	ClaySelectWithOption,
	ClayToggle,
} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import {RequiredMark, Toolbar} from '@liferay/site-cms-site-initializer';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

interface IPIMConnector {
	key: string;
	name: string;
}

interface IPIMConnectorData {
	active: boolean;
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
	const [key, setKey] = useState(pimConnector?.key || '');
	const [name, setName] = useState(pimConnector?.name || '');

	useEffect(() => {
		if (!isNew && !pimConnector) {
			navigate(backURL);
		}
	}, [backURL, isNew, pimConnector]);

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault();

		try {
			const response = await fetch(
				isNew ? apiURL : `${apiURL}/${objectEntryId}`,
				{
					body: JSON.stringify({
						active,
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
						form="pimConnectorForm"
						size="sm"
						type="submit"
					>
						{Liferay.Language.get('save')}
					</ClayButton>
				</Toolbar.Item>
			</Toolbar>

			<div className="container-fluid container-fluid-max-md p-0 p-md-4">
				<ClayForm id="pimConnectorForm" onSubmit={handleSubmit}>
					<ClayPanel
						aria-label="basic-info"
						className="mb-4"
						collapsable={false}
						displayType="secondary"
						role="group"
					>
						<div className="c-gap-4 d-flex flex-column p-4">
							<h2 className="mb-0 py-2 text-6 text-dark">
								{Liferay.Language.get('basic-info')}
							</h2>

							<ClayForm.Group className="mb-0">
								<label htmlFor="pimConnectorName">
									{Liferay.Language.get('name')}

									<RequiredMark />
								</label>

								<ClayInput
									id="pimConnectorName"
									onChange={(event) =>
										setName(event.target.value)
									}
									required
									type="text"
									value={name}
								/>
							</ClayForm.Group>

							<ClayForm.Group className="mb-0">
								<label htmlFor="pimConnectorKey">
									{Liferay.Language.get('connector')}

									<RequiredMark />
								</label>

								<ClaySelectWithOption
									id="pimConnectorKey"
									onChange={(event) =>
										setKey(event.target.value)
									}
									options={[
										{
											disabled: true,
											label: Liferay.Language.get(
												'select-a-connector'
											),
											value: '',
										},
										...pimConnectors.map(
											(pimConnector) => ({
												label: pimConnector.name,
												value: pimConnector.key,
											})
										),
									]}
									required
									value={key}
								/>
							</ClayForm.Group>

							<ClayForm.Group className="mb-0">
								<ClayToggle
									label={Liferay.Language.get('active')}
									onToggle={setActive}
									toggled={active}
								/>
							</ClayForm.Group>
						</div>
					</ClayPanel>
				</ClayForm>
			</div>
		</>
	);
}
