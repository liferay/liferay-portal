/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {useFormikContext} from 'formik';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef, useState} from 'react';

import {getValidateLarFileEndpoint} from '../../../common/utils/getValidateLarFileEndpoint';
import {FormikFieldText} from '../../../components/forms/formik';
import {FormikFieldFileSelector} from '../../../components/forms/formik/FormikFieldFileSelector';
import {useWizard} from '../NewImport';

interface FileSelectionValues {
	fileSelector?: File;
	name: string;
}

export default function FileSelectionStep() {
	const [progress, setProgress] = useState<number>();
	const {isCompanyGroup} = useWizard();

	const {setFieldValue, values} = useFormikContext<FileSelectionValues>();
	const previousFileRef = useRef<File | undefined>(undefined);
	const nameRef = useRef(values.name);

	useEffect(() => {
		nameRef.current = values.name;
	}, [values.name]);

	useEffect(() => {
		const currentFile = values.fileSelector;
		const previousFile = previousFileRef.current;

		previousFileRef.current = currentFile;

		if (
			currentFile instanceof File &&
			currentFile !== previousFile &&
			!nameRef.current
		) {
			setFieldValue('name', currentFile.name);
		}
	}, [values.fileSelector, setFieldValue]);

	const handleUpload = (file: File, signal?: AbortSignal) =>
		getValidateLarFileEndpoint({
			file,
			isCompanyGroup,
			onProgress: setProgress,
			signal,
		});

	return (
		<>
			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader className="mb-1">
					<div className="mb-2 sheet-title">
						{sub(
							Liferay.Language.get('x-details'),
							Liferay.Language.get('import')
						)}
					</div>

					<div className="sheet-text text-3">
						{Liferay.Language.get(
							'provide-a-descriptive-name-for-your-import'
						)}
					</div>
				</ClayLayout.SheetHeader>

				<FormikFieldText
					label={Liferay.Language.get('name')}
					name="name"
					required
				/>
			</ClayLayout.Sheet>

			<ClayLayout.Sheet>
				<ClayLayout.SheetHeader className="mb-1">
					<div className="mb-2 sheet-title" id="fileSelector-label">
						{Liferay.Language.get('file-upload')}
					</div>

					<div
						className="sheet-text text-3"
						id="fileSelector-description"
					>
						{Liferay.Language.get(
							'select-and-upload-your-prepared-file'
						)}
					</div>
				</ClayLayout.SheetHeader>

				<FormikFieldFileSelector
					aria-describedby="fileSelector-description"
					aria-labelledby="fileSelector-label"
					name="fileSelector"
					progress={progress}
					uploadRequest={handleUpload}
					validExtensions=".lar"
				/>
			</ClayLayout.Sheet>
		</>
	);
}
