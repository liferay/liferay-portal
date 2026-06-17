/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {FieldBase, openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {getDataMask} from '../services/getDataMask';
import {DataMask} from '../types';
import {DataMaskRegexTester} from './DataMaskRegexTester';
import {useEditDataMask} from './useEditDataMask';

interface EditDataMaskProps {
	backURL: string;
	dataMaskId: number;
}

export default function EditDataMask({backURL, dataMaskId}: EditDataMaskProps) {
	const [dataMask, setDataMask] = useState<DataMask | null>(null);
	const [loading, setLoading] = useState(dataMaskId > 0);

	useEffect(() => {
		if (!dataMaskId) {
			return;
		}

		let active = true;

		getDataMask(dataMaskId).then(({data}) => {
			if (!active) {
				return;
			}

			setDataMask(data);
			setLoading(false);
		});

		return () => {
			active = false;
		};
	}, [dataMaskId]);

	if (loading) {
		return null;
	}

	return (
		<EditDataMaskView
			backURL={backURL}
			dataMask={dataMask}
			readOnly={dataMask?.maskType?.key === 'system'}
		/>
	);
}

interface EditDataMaskViewProps {
	backURL: string;
	dataMask: DataMask | null;
	readOnly: boolean;
}

function EditDataMaskView({
	backURL,
	dataMask,
	readOnly,
}: EditDataMaskViewProps) {
	const {
		description,
		detectionRegex,
		detectionRegexError,
		handleSubmit,
		isSystemMask,
		name,
		replacementRegex,
		replacementRegexError,
		replacementValue,
		setDescription,
		setDetectionRegex,
		setName,
		setReplacementRegex,
		setReplacementValue,
		submitting,
	} = useEditDataMask({
		dataMask,
		onSaved: (saved) => {
			openToast({
				message: Liferay.Util.sub(
					Liferay.Language.get('x-was-saved-successfully'),
					saved.name
				),
				type: 'success',
			});

			navigate(backURL);
		},
	});

	const headerTitle = readOnly
		? Liferay.Language.get('view-data-mask')
		: dataMask
			? Liferay.Language.get('edit-data-mask')
			: Liferay.Language.get('new-data-mask');

	return (
		<ClayForm className="data-mask-form" onSubmit={handleSubmit}>
			<h2 className="mb-4">{headerTitle}</h2>

			{isSystemMask && (
				<ClayAlert
					displayType="info"
					title={Liferay.Language.get('info')}
				>
					{Liferay.Language.get(
						'system-masks-are-read-only-and-cannot-be-edited'
					)}
				</ClayAlert>
			)}

			<MaskInformationSection
				description={description}
				name={name}
				readOnly={readOnly}
				setDescription={setDescription}
				setName={setName}
			/>

			<DetectionConfigurationSection
				detectionRegex={detectionRegex}
				detectionRegexError={detectionRegexError}
				readOnly={readOnly}
				setDetectionRegex={setDetectionRegex}
			/>

			<ReplacementConfigurationSection
				detectionRegex={detectionRegex}
				readOnly={readOnly}
				replacementRegex={replacementRegex}
				replacementRegexError={replacementRegexError}
				replacementValue={replacementValue}
				setReplacementRegex={setReplacementRegex}
				setReplacementValue={setReplacementValue}
			/>

			<div className="d-flex">
				<ClayButton
					className="mr-3"
					displayType="secondary"
					onClick={() => navigate(backURL)}
					type="button"
				>
					{readOnly
						? Liferay.Language.get('close')
						: Liferay.Language.get('cancel')}
				</ClayButton>

				{!readOnly && (
					<ClayButton
						disabled={submitting}
						displayType="primary"
						type="submit"
					>
						{Liferay.Language.get('save')}
					</ClayButton>
				)}
			</div>
		</ClayForm>
	);
}

interface MaskInformationSectionProps {
	description: string;
	name: string;
	readOnly: boolean;
	setDescription: (value: string) => void;
	setName: (value: string) => void;
}

function MaskInformationSection({
	description,
	name,
	readOnly,
	setDescription,
	setName,
}: MaskInformationSectionProps) {
	return (
		<ClayLayout.Sheet className="mb-4">
			<h3 className="sheet-title">
				{Liferay.Language.get('mask-information')}
			</h3>

			<FieldBase
				disabled={readOnly}
				id="dataMaskName"
				label={Liferay.Language.get('name')}
				required
			>
				<ClayInput
					disabled={readOnly}
					id="dataMaskName"
					onChange={(event) => setName(event.target.value)}
					required
					type="text"
					value={name}
				/>
			</FieldBase>

			<FieldBase
				disabled={readOnly}
				id="dataMaskDescription"
				label={Liferay.Language.get('description')}
			>
				<ClayInput
					component="textarea"
					disabled={readOnly}
					id="dataMaskDescription"
					onChange={(event) => setDescription(event.target.value)}
					value={description}
				/>
			</FieldBase>
		</ClayLayout.Sheet>
	);
}

interface DetectionConfigurationSectionProps {
	detectionRegex: string;
	detectionRegexError: string;
	readOnly: boolean;
	setDetectionRegex: (value: string) => void;
}

function DetectionConfigurationSection({
	detectionRegex,
	detectionRegexError,
	readOnly,
	setDetectionRegex,
}: DetectionConfigurationSectionProps) {
	return (
		<ClayLayout.Sheet className="mb-4">
			<h3 className="sheet-title">
				{Liferay.Language.get('detection-configuration')}
			</h3>

			<FieldBase
				disabled={readOnly}
				errorMessage={detectionRegexError}
				helpMessage={Liferay.Language.get(
					'use-a-standard-regular-expression-named-capture-groups-are-supported'
				)}
				id="dataMaskRegexPattern"
				label={Liferay.Language.get('regex-pattern')}
				required
			>
				<ClayInput
					disabled={readOnly}
					id="dataMaskRegexPattern"
					onChange={(event) => setDetectionRegex(event.target.value)}
					required
					type="text"
					value={detectionRegex}
				/>
			</FieldBase>
		</ClayLayout.Sheet>
	);
}

interface ReplacementConfigurationSectionProps {
	detectionRegex: string;
	readOnly: boolean;
	replacementRegex: string;
	replacementRegexError: string;
	replacementValue: string;
	setReplacementRegex: (value: string) => void;
	setReplacementValue: (value: string) => void;
}

function ReplacementConfigurationSection({
	detectionRegex,
	readOnly,
	replacementRegex,
	replacementRegexError,
	replacementValue,
	setReplacementRegex,
	setReplacementValue,
}: ReplacementConfigurationSectionProps) {
	return (
		<ClayLayout.Sheet className="mb-4">
			<h3 className="sheet-title">
				{Liferay.Language.get('replacement-configuration')}
			</h3>

			<FieldBase
				disabled={readOnly}
				errorMessage={replacementRegexError}
				helpMessage={Liferay.Language.get(
					'leave-empty-to-replace-the-entire-detected-value-with-the-replacement-token'
				)}
				id="dataMaskMatchPattern"
				label={Liferay.Language.get('match-pattern')}
			>
				<ClayInput
					disabled={readOnly}
					id="dataMaskMatchPattern"
					onChange={(event) =>
						setReplacementRegex(event.target.value)
					}
					type="text"
					value={replacementRegex}
				/>
			</FieldBase>

			<FieldBase
				disabled={readOnly}
				id="dataMaskReplacement"
				label={Liferay.Language.get('replacement')}
				required
			>
				<ClayInput
					disabled={readOnly}
					id="dataMaskReplacement"
					onChange={(event) =>
						setReplacementValue(event.target.value)
					}
					required
					type="text"
					value={replacementValue}
				/>
			</FieldBase>

			<DataMaskRegexTester
				detectionRegex={detectionRegex}
				replacementRegex={replacementRegex}
				replacementValue={replacementValue}
			/>
		</ClayLayout.Sheet>
	);
}
