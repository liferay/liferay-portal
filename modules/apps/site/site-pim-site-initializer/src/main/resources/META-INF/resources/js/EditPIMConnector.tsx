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

import PIMConnectorSettingsField, {
	IPIMConnectorSettingsField,
	PIMConnectorSettingsValue,
} from './edit_connector/PIMConnectorSettingsField';

const TEMP_OBFUSCATION_VALUE = 'TEMP_OBFUSCATION_VALUE';

interface IPIMConnector {
	key: string;
	name: string;
	settings?: IPIMConnectorSettings | null;
}

interface IPIMConnectorData {
	active: boolean;
	externalReferenceCode: string;
	key: string;
	name: string;
}

interface IPIMConnectorSettings {
	apiURL: string;
	fields: IPIMConnectorSettingsField[];
	title: string;
}

interface IPIMConnectorSettingsData {
	externalReferenceCode: string;
	values: {[name: string]: PIMConnectorSettingsValue | null};
}

interface IProps {
	apiURL: string;
	backURL: string;
	objectEntryId: number;
	pimConnector: IPIMConnectorData | null;
	pimConnectors: IPIMConnector[];
	settings: IPIMConnectorSettingsData | null;
	title: string;
}

function isEmpty(value?: PIMConnectorSettingsValue | null) {
	return (
		value === undefined ||
		value === null ||
		value === '' ||
		(Array.isArray(value) && !value.length)
	);
}

function toRequestValue(
	field: IPIMConnectorSettingsField,
	value: PIMConnectorSettingsValue
) {
	if (
		field.businessType === 'Decimal' ||
		field.businessType === 'Integer' ||
		field.businessType === 'LongInteger' ||
		field.businessType === 'PrecisionDecimal'
	) {
		return Number(value);
	}

	if (field.businessType === 'DateTime') {
		return new Date(String(value)).toISOString();
	}

	return value;
}

export default function EditPIMConnector({
	apiURL,
	backURL,
	objectEntryId,
	pimConnector,
	pimConnectors = [],
	settings,
	title,
}: IProps) {
	const isNew = Number(objectEntryId) === 0;

	const [active, setActive] = useState(Boolean(pimConnector?.active));
	const [key, setKey] = useState(pimConnector?.key || '');
	const [name, setName] = useState(pimConnector?.name || '');
	const [settingsValues, setSettingsValues] = useState<{
		[name: string]: PIMConnectorSettingsValue | null;
	}>(settings?.values ?? {});

	const pimConnectorSettings = pimConnectors.find(
		(curPIMConnector) => curPIMConnector.key === key
	)?.settings;

	useEffect(() => {
		if (!isNew && !pimConnector) {
			navigate(backURL);
		}
	}, [backURL, isNew, pimConnector]);

	const saveSettings = async (externalReferenceCode: string) => {
		if (!pimConnectorSettings) {
			return;
		}

		const body: {[name: string]: unknown} = {};

		for (const field of pimConnectorSettings.fields) {
			const value = settingsValues[field.name];

			if (
				field.businessType === 'Encrypted' &&
				value === TEMP_OBFUSCATION_VALUE
			) {
				continue;
			}

			body[field.name] = isEmpty(value)
				? null
				: toRequestValue(field, value as PIMConnectorSettingsValue);
		}

		const response = await fetch(
			`${pimConnectorSettings.apiURL}/by-external-reference-code/${externalReferenceCode}`,
			{
				body: JSON.stringify(body),
				headers: {
					'Content-Type': 'application/json',
				},
				method: settings ? 'PATCH' : 'PUT',
			}
		);

		if (!response.ok) {
			throw new Error();
		}
	};

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

			const {externalReferenceCode} = await response.json();

			await saveSettings(externalReferenceCode);

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

	const settingsComplete = (pimConnectorSettings?.fields ?? []).every(
		(field) =>
			!field.required ||
			field.businessType === 'Boolean' ||
			!isEmpty(settingsValues[field.name])
	);

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
						disabled={!name.trim() || !key || !settingsComplete}
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
						aria-label={Liferay.Language.get('general')}
						className="mb-4"
						collapsable={false}
						displayType="secondary"
						role="group"
					>
						<div className="c-gap-4 d-flex flex-column p-4">
							<h2 className="mb-0 py-2 text-6 text-dark">
								{Liferay.Language.get('general')}
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
									aria-describedby={
										isNew
											? undefined
											: 'pimConnectorKeyHelp'
									}
									disabled={!isNew}
									id="pimConnectorKey"
									onChange={(event) => {
										setKey(event.target.value);
										setSettingsValues({});
									}}
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

								{!isNew && (
									<ClayForm.FeedbackGroup>
										<ClayForm.Text id="pimConnectorKeyHelp">
											{Liferay.Language.get(
												'the-connector-type-cannot-be-changed-after-the-connector-is-created'
											)}
										</ClayForm.Text>
									</ClayForm.FeedbackGroup>
								)}
							</ClayForm.Group>

							<ClayForm.Group className="mb-0">
								<ClayToggle
									label={Liferay.Language.get('enable')}
									onToggle={setActive}
									toggled={active}
								/>
							</ClayForm.Group>
						</div>
					</ClayPanel>

					{pimConnectorSettings &&
						!!pimConnectorSettings.fields.length && (
							<ClayPanel
								aria-label={pimConnectorSettings.title}
								className="mb-4"
								collapsable={false}
								displayType="secondary"
								role="group"
							>
								<div className="c-gap-4 d-flex flex-column p-4">
									<h2 className="mb-0 py-2 text-6 text-dark">
										{pimConnectorSettings.title}
									</h2>

									{pimConnectorSettings.fields.map(
										(field) => (
											<PIMConnectorSettingsField
												field={field}
												key={field.name}
												onChange={(value) =>
													setSettingsValues(
														(
															previousSettingsValues
														) => ({
															...previousSettingsValues,
															[field.name]: value,
														})
													)
												}
												value={
													settingsValues[field.name]
												}
											/>
										)
									)}
								</div>
							</ClayPanel>
						)}
				</ClayForm>
			</div>
		</>
	);
}
