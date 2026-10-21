/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useFormikContext} from 'formik';
import React, {useEffect, useRef, useState} from 'react';

import SectionHeader from '../../../components/SectionHeader';
import {FormikFieldText} from '../../../components/forms/formik';
import {FormikFieldFileSelector} from '../../../components/forms/formik/FormikFieldFileSelector';
import {postImportPreview} from '../../../services/postImportPreview';
import {ImportPreview} from '../../../types/exportImportPreview';

export default function FileSelectionStep({
	importPreviewAPIURL,
	setImportPreview,
}: {
	importPreviewAPIURL: string;
	setImportPreview: (importPreview?: ImportPreview) => void;
}) {
	const [progress, setProgress] = useState<number>();

	const {setFieldTouched, setFieldValue, values} = useFormikContext<{
		fileSelector?: File;
		name: string;
	}>();
	const autoFilledFileRef = useRef<File | undefined>(undefined);

	useEffect(() => {
		const currentFile = values.fileSelector;

		if (currentFile === autoFilledFileRef.current) {
			return;
		}

		autoFilledFileRef.current = currentFile;

		if (currentFile instanceof File && !values.name) {
			setFieldValue('name', currentFile.name.replace(/\.lar$/i, ''));
		}

		if (!currentFile) {
			setFieldValue('contentSelection', undefined);
			setFieldTouched('contentSelection', false, false);
			setFieldValue('siteExternalReferenceCodes', []);
			setFieldTouched('siteExternalReferenceCodes', false, false);
			setImportPreview(undefined);
		}
	}, [
		values.fileSelector,
		values.name,
		setFieldTouched,
		setFieldValue,
		setImportPreview,
	]);

	const handleUpload = async (file: File, signal?: AbortSignal) => {
		const result = await postImportPreview({
			file,
			onProgress: setProgress,
			signal,
			url: importPreviewAPIURL,
		});

		if (result.data) {
			setImportPreview(result.data);
		}

		return result;
	};

	return (
		<>
			<SectionHeader title={Liferay.Language.get('import-details')} />

			<FormikFieldText
				label={Liferay.Language.get('name')}
				name="name"
				required
			/>

			<SectionHeader
				id="fileSelector-label"
				subtitle={Liferay.Language.get(
					'select-and-upload-your-prepared-file'
				)}
				subtitleId="fileSelector-description"
				title={Liferay.Language.get('file-upload')}
			/>

			<FormikFieldFileSelector
				aria-describedby="fileSelector-description"
				aria-labelledby="fileSelector-label"
				name="fileSelector"
				progress={progress}
				uploadRequest={handleUpload}
				validExtensions=".lar"
			/>
		</>
	);
}
