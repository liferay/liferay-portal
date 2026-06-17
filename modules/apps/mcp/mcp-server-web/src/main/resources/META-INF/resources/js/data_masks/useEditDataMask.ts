/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {FormEvent, useState} from 'react';

import {patchDataMask} from '../services/patchDataMask';
import {postDataMask} from '../services/postDataMask';
import {DataMask} from '../types';

interface Options {
	dataMask: DataMask | null;
	onSaved: (saved: DataMask) => void;
}

function regexError(value: string): string {
	if (!value) {
		return '';
	}

	try {
		new RegExp(value);

		return '';
	}
	catch (error) {
		return Liferay.Language.get(
			'patterns-must-be-valid-regular-expressions'
		);
	}
}

export function useEditDataMask({dataMask, onSaved}: Options) {
	const isSystemMask = dataMask?.maskType?.key === 'system';

	const [name, setName] = useState(dataMask?.name ?? '');
	const [description, setDescription] = useState(dataMask?.description ?? '');
	const [detectionRegex, setDetectionRegex] = useState(
		dataMask?.detectionRegex ?? ''
	);
	const [replacementRegex, setReplacementRegex] = useState(
		dataMask?.replacementRegex ?? ''
	);
	const [replacementValue, setReplacementValue] = useState(
		dataMask?.replacementValue ?? ''
	);
	const [submitting, setSubmitting] = useState(false);

	const detectionRegexError = regexError(detectionRegex);
	const replacementRegexError = regexError(replacementRegex);

	const handleSubmit = async (event: FormEvent) => {
		event.preventDefault();

		if (detectionRegexError || replacementRegexError) {
			return;
		}

		setSubmitting(true);

		const payload = {
			description,
			detectionRegex,
			maskType: {key: dataMask?.maskType?.key ?? 'custom'},
			name,
			replacementRegex,
			replacementValue,
		};

		const {data: saved, error} = dataMask?.id
			? await patchDataMask(dataMask.id, payload)
			: await postDataMask(payload);

		setSubmitting(false);

		if (error) {
			openToast({
				message: error,
				type: 'danger',
			});

			return;
		}

		if (saved) {
			onSaved(saved);
		}
	};

	return {
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
	};
}
