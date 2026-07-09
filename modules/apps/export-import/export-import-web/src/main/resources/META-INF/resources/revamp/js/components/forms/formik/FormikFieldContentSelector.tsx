/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useField, useFormikContext} from 'formik';
import React, {useEffect, useRef} from 'react';

import {PageTreeModalConfiguration} from '../../../pages/export/components/PageTreeModal';
import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PreviewPortletDataHandlerSection} from '../../../types/portletDataHandler';
import {getFullDataSelection} from '../../../utils/contentSelection';
import ContentSelector, {
	ContentSelection,
} from '../content_selector/ContentSelector';

interface FormikFieldContentSelectorProps {
	'aria-labelledby'?: string;
	'commentsAndRatingsEnabled'?: boolean;
	'lookAndFeelEnabled'?: boolean;
	'name': string;
	'pageTreeModalConfiguration'?: PageTreeModalConfiguration;
	'previewPortletDataHandlerSections': PreviewPortletDataHandlerSection[];
	'process'?: ExportImportProcess;
}

export function FormikFieldContentSelector({
	'aria-labelledby': ariaLabelledby,
	commentsAndRatingsEnabled = false,
	lookAndFeelEnabled = false,
	name,
	pageTreeModalConfiguration,
	previewPortletDataHandlerSections,
	process = 'export',
}: FormikFieldContentSelectorProps) {
	const [field, meta, helpers] = useField<ContentSelection | undefined>(name);
	const [{value: deletions}] = useField<boolean | undefined>('deletions');
	const {setFieldTouched, setFieldValue} = useFormikContext();

	const showDeletions = !!deletions;

	const shouldSeed =
		!!previewPortletDataHandlerSections.length &&
		field.value === undefined &&
		!meta.touched;

	const defaultContentSelection = shouldSeed
		? getFullDataSelection(previewPortletDataHandlerSections, {
				commentsAndRatingsEnabled,
				lookAndFeelEnabled,
				showDeletions,
			})
		: undefined;

	const hasSeededRef = useRef(false);

	useEffect(() => {
		if (hasSeededRef.current || !defaultContentSelection) {
			return;
		}

		hasSeededRef.current = true;

		setFieldValue(name, defaultContentSelection);
	}, [name, defaultContentSelection, setFieldValue]);

	return (
		<ContentSelector
			aria-labelledby={ariaLabelledby}
			commentsAndRatingsEnabled={commentsAndRatingsEnabled}
			contentSelection={field.value ?? defaultContentSelection}
			errorMessage={meta.touched && meta.error ? meta.error : undefined}
			lookAndFeelEnabled={lookAndFeelEnabled}
			name={name}
			onChange={(newValue) => {
				helpers.setValue(newValue);
				setFieldTouched(name, true, false);
			}}
			pageTreeModalConfiguration={pageTreeModalConfiguration}
			previewPortletDataHandlerSections={
				previewPortletDataHandlerSections
			}
			process={process}
			showDeletions={showDeletions}
		/>
	);
}
