/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import {Form, FormikProvider, useFormik, useFormikContext} from 'formik';
import {openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {FormField} from '../forms/FormField';
import {FormSection} from '../forms/FormSection';
import {getDataMask} from '../services/getDataMask';
import {patchDataMask} from '../services/patchDataMask';
import {postDataMask} from '../services/postDataMask';
import {DataMask, DataMaskFormValues, DataMaskPayload} from '../types';
import {isSystemMask} from '../utils';
import {DataMaskRegexTester} from './DataMaskRegexTester';

interface EditDataMaskProps {
	backURL: string;
	dataMaskId: number;
}

export default function EditDataMask({backURL, dataMaskId}: EditDataMaskProps) {
	const [dataMask, setDataMask] = useState<DataMask | null>(null);
	const [loading, setLoading] = useState(dataMaskId > 0);

	useEffect(() => {
		if (!dataMaskId || Number(dataMaskId) <= 0) {
			return;
		}

		let active = true;

		getDataMask(dataMaskId).then(({data, error}) => {
			if (!active) {
				return;
			}

			if (error || !data) {
				openToast({
					message: error
						? Liferay.Util.escapeHTML(error)
						: Liferay.Language.get('an-unexpected-error-occurred'),
					type: 'danger',
				});

				navigate(backURL);

				return;
			}

			setDataMask(data);
			setLoading(false);
		});

		return () => {
			active = false;
		};
	}, [backURL, dataMaskId]);

	if (loading) {
		return null;
	}

	return <EditDataMaskView backURL={backURL} dataMask={dataMask} />;
}

interface EditDataMaskViewProps {
	backURL: string;
	dataMask: DataMask | null;
}

function EditDataMaskView({backURL, dataMask}: EditDataMaskViewProps) {
	const readOnly = isSystemMask(dataMask);

	const formik = useFormik<DataMaskFormValues>({
		initialValues: {
			description: dataMask?.description ?? '',
			detectionRegex: dataMask?.detectionRegex ?? '',
			name: dataMask?.name ?? '',
			replacementRegex: dataMask?.replacementRegex ?? '',
			replacementValue: dataMask?.replacementValue ?? '',
		},
		onSubmit: async (values) => {
			const payload: DataMaskPayload = {
				description: values.description,
				detectionRegex: values.detectionRegex,
				maskType: {key: dataMask?.maskType?.key ?? 'custom'},
				name: values.name,
				replacementRegex: values.replacementRegex,
				replacementValue: values.replacementValue,
			};

			const {data: saved, error} = dataMask?.id
				? await patchDataMask(dataMask.id, payload)
				: await postDataMask(payload);

			if (error) {
				openToast({
					message: Liferay.Util.escapeHTML(error),
					type: 'danger',
				});

				return;
			}

			if (saved) {
				openToast({
					message: Liferay.Util.sub(
						Liferay.Language.get('x-was-saved-successfully'),
						Liferay.Util.escapeHTML(saved.name)
					),
					type: 'success',
				});

				navigate(backURL);
			}
		},
	});

	return (
		<FormikProvider value={formik}>
			<Form className="data-mask-form" noValidate>
				{readOnly && (
					<ClayAlert
						displayType="info"
						title={Liferay.Language.get('info')}
					>
						{Liferay.Language.get(
							'system-masks-are-read-only-and-cannot-be-edited'
						)}
					</ClayAlert>
				)}

				<MaskInformationSection readOnly={readOnly} />

				<DetectionConfigurationSection readOnly={readOnly} />

				<ReplacementConfigurationSection readOnly={readOnly} />

				<ClayLayout.SheetFooter>
					<ClayButton
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
							disabled={formik.isSubmitting}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					)}
				</ClayLayout.SheetFooter>
			</Form>
		</FormikProvider>
	);
}

function MaskInformationSection({readOnly}: {readOnly: boolean}) {
	return (
		<FormSection title={Liferay.Language.get('mask-information')}>
			<FormField
				disabled={readOnly}
				id="dataMaskName"
				label={Liferay.Language.get('name')}
				name="name"
				required
			/>

			<FormField
				component="textarea"
				disabled={readOnly}
				id="dataMaskDescription"
				label={Liferay.Language.get('description')}
				name="description"
			/>
		</FormSection>
	);
}

function DetectionConfigurationSection({readOnly}: {readOnly: boolean}) {
	return (
		<FormSection
			className="mt-4"
			title={Liferay.Language.get('detection-configuration')}
		>
			<FormField
				disabled={readOnly}
				helpMessage={Liferay.Language.get(
					'use-a-standard-regular-expression-named-capture-groups-are-supported'
				)}
				id="dataMaskRegexPattern"
				label={Liferay.Language.get('regex-pattern')}
				name="detectionRegex"
				required
			/>
		</FormSection>
	);
}

function ReplacementConfigurationSection({readOnly}: {readOnly: boolean}) {
	const {values} = useFormikContext<{
		detectionRegex: string;
		replacementRegex: string;
		replacementValue: string;
	}>();

	return (
		<FormSection
			className="mt-4"
			title={Liferay.Language.get('replacement-configuration')}
		>
			<FormField
				disabled={readOnly}
				helpMessage={Liferay.Language.get(
					'leave-empty-to-replace-the-entire-detected-value-with-the-replacement-token'
				)}
				id="dataMaskMatchPattern"
				label={Liferay.Language.get('match-pattern')}
				name="replacementRegex"
			/>

			<FormField
				disabled={readOnly}
				id="dataMaskReplacement"
				label={Liferay.Language.get('replacement')}
				name="replacementValue"
				required
			/>

			<DataMaskRegexTester
				detectionRegex={values.detectionRegex}
				replacementRegex={values.replacementRegex}
				replacementValue={values.replacementValue}
			/>
		</FormSection>
	);
}
