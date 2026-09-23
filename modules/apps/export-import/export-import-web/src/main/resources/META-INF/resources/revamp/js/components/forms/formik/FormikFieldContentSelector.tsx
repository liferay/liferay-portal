/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useField, useFormikContext} from 'formik';
import React, {useEffect, useState} from 'react';

import {ExportImportProcess} from '../../../types/exportImportProcess';
import {PreviewPortletDataHandlerSection} from '../../../types/portletDataHandler';
import {getFullDataSelection} from '../../../utils/contentSelection';
import {PageTreeModalConfiguration} from '../../PageTreeModal';
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

	// The selection starts out full once the preview arrives, and only once.
	// Whether it has been seeded is tracked here rather than read from
	// `meta.touched`: Formik drops a field whose value is `undefined` from
	// `values`, and on submit it rebuilds `touched` from `values`, so a
	// deselected field reads as untouched again right after the submit.

	const [seeded, setSeeded] = useState(false);

	const shouldSeed =
		!seeded &&
		!!previewPortletDataHandlerSections.length &&
		field.value === undefined;

	const defaultContentSelection = shouldSeed
		? getFullDataSelection(previewPortletDataHandlerSections, {
				commentsAndRatingsEnabled,
				lookAndFeelEnabled,
				showDeletions,
			})
		: undefined;

	useEffect(() => {
		if (!defaultContentSelection) {
			return;
		}

		setSeeded(true);

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
