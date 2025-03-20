/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Field, FieldType} from './utils/dataConverter';

export {EVENT_TYPES} from './core/actions/eventTypes';
export {FieldFeedback} from './core/components/FieldFeedback';
export {EVENT_TYPES as FORM_EVENT_TYPES} from './custom/form/eventTypes';
export {
	getDDMFormFieldSettingsContext,
	FieldType,
	FieldTypeName,
} from './utils/dataConverter';

export function convertToFormData(body: unknown): unknown;

export function makeFetch({
	body,
	headers,
	method,
	url,
	...otherProps
}: {
	body: unknown;
	headers?:
		| {
				Accept: string;
		  }
		| undefined;
	method?: string | undefined;
	url: unknown;
	[x: string]: unknown;
}): unknown;

export function useConfig(): {
	dataEngineModule: string;
	displayChartAsTable: boolean;
	fieldTypes: FieldType[];
	formReportDataURL: string;
	portletNamespace: string;
};

export function useForm(): ({
	payload,
	type,
}: {
	payload?: unknown;
	type: string;
}) => void;

export function useFormState<T extends {[key: string]: unknown}>(): T;

export const FormReport: React.FC<{
	data?: string;
	dataEngineModule: string;
	displayChartAsTable: boolean;
	fields: unknown;
	formReportRecordsFieldValuesURL: string;
	portletNamespace: string;
}>;

export const FormView: React.FC<{
	children?: React.ReactNode;
}>;

export const PartialResults: React.FC<{
	dataEngineModule: string;
	displayChartAsTable: boolean;
	reportDataURL: string;
}>;

export class PagesVisitor {
	constructor(pages: unknown);
	mapFields: (
		mapper: (field: Field) => void,
		merge: boolean,
		includeNestedFields: boolean
	) => void;
}
