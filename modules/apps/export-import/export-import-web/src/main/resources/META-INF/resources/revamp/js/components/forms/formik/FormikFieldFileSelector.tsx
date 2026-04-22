/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {RequestResult} from '../../../services/ApiHelper';
import FileSelector from '../../FileSelector';
import {FormikWrapper} from './FormikWrapper';
import {ValidationResponse, useFileSelector} from './hooks/useFileSelector';

export function FormikFieldFileSelector({
	'aria-describedby': ariaDescribedby,
	'aria-labelledby': ariaLabelledby,
	name,
	progress,
	uploadRequest,
	validExtensions,
	...otherProps
}: {
	'aria-describedby'?: string;
	'aria-labelledby'?: string;
	'name': string;
	'progress'?: number;
	'uploadRequest': (
		file: File,
		signal?: AbortSignal
	) => Promise<RequestResult<ValidationResponse>>;
	'validExtensions'?: string;
}) {
	const {
		handleRejection,
		handleRemove,
		handleUpload,
		selectedFile,
		status,
		validate,
	} = useFileSelector(name, uploadRequest);

	return (
		<FormikWrapper name={name} validate={validate}>
			{({errorMessage}) => {
				const isActualError =
					errorMessage && (status === 'IDLE' || status === 'ERROR');

				return (
					<FileSelector
						{...otherProps}
						aria-describedby={
							[
								ariaDescribedby,
								isActualError && `${name}-error-message`,
							]
								.filter(Boolean)
								.join(' ') || undefined
						}
						aria-labelledby={ariaLabelledby}
						error={errorMessage}
						file={selectedFile}
						handleRejection={handleRejection}
						handleUpload={handleUpload}
						name={name}
						onRemove={handleRemove}
						progress={progress}
						status={status}
						validExtensions={validExtensions}
					/>
				);
			}}
		</FormikWrapper>
	);
}
