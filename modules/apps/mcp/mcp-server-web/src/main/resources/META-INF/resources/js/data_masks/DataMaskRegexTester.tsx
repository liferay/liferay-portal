/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import {FieldBase} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {
	ValidationResult,
	postValidateDataMask,
} from '../services/postValidateDataMask';

interface DataMaskRegexTesterProps {
	detectionRegex: string;
	replacementRegex: string;
	replacementValue: string;
}

export function DataMaskRegexTester({
	detectionRegex,
	replacementRegex,
	replacementValue,
}: DataMaskRegexTesterProps) {
	const [result, setResult] = useState<ValidationResult | null>(null);
	const [sampleText, setSampleText] = useState('');
	const [testing, setTesting] = useState(false);

	const canTest = Boolean(detectionRegex && sampleText) && !testing;

	const handleTest = async () => {
		setTesting(true);

		const {data, error} = await postValidateDataMask({
			detectionRegex,
			replacementRegex,
			replacementValue,
			text: sampleText,
		});

		setResult(data ?? {error: error ?? '', output: ''});

		setTesting(false);
	};

	return (
		<div className="data-mask-test mt-4">
			<h4 className="sheet-tertiary-title">
				{Liferay.Language.get('test-this-mask')}
			</h4>

			<p className="text-secondary">
				{Liferay.Language.get(
					'enter-a-sample-value-to-preview-how-this-mask-would-transform-it'
				)}
			</p>

			<FieldBase
				id="dataMaskSampleValue"
				label={Liferay.Language.get('sample')}
			>
				<ClayInput
					id="dataMaskSampleValue"
					onChange={(event) => setSampleText(event.target.value)}
					placeholder={Liferay.Language.get('enter-a-sample-value')}
					type="text"
					value={sampleText}
				/>
			</FieldBase>

			<FieldBase
				errorMessage={result?.error}
				id="dataMaskOutput"
				label={Liferay.Language.get('output')}
			>
				<ClayInput
					id="dataMaskOutput"
					readOnly
					type="text"
					value={result && !result.error ? result.output : ''}
				/>
			</FieldBase>

			<ClayButton
				disabled={!canTest}
				displayType="secondary"
				onClick={handleTest}
				type="button"
			>
				{Liferay.Language.get('test')}
			</ClayButton>
		</div>
	);
}
