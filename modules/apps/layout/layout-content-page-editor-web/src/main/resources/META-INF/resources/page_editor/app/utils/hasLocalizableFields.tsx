/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FormLayoutDataItem} from '../../types/layout_data/FormLayoutDataItem';
import {ObjectFields} from '../contexts/ObjectDataContext';
import {State} from '../reducers';
import selectFormConfiguration from '../selectors/selectFormConfiguration';
import FormService from '../services/FormService';
import {CACHE_KEYS, getCacheItem, getCacheKey} from './cache';
import {findSelectedFormFields} from './findSelectedFormFields';

export async function hasLocalizableFields(
	state: State,
	formId: FormLayoutDataItem['itemId']
): Promise<boolean> {
	const selectedFormFields = findSelectedFormFields(state, formId);

	const {classNameId, classTypeId} = selectFormConfiguration(
		state.layoutData.items[formId],
		state.layoutData
	);

	if (!classNameId) {
		return false;
	}

	const cacheKey = getCacheKey([
		CACHE_KEYS.formFields,
		classNameId,
		classTypeId,
	]);

	const {data: fields} = getCacheItem<ObjectFields>(cacheKey);

	const promise = fields
		? Promise.resolve(fields)
		: FormService.getFormFields({
				classNameId,
				classTypeId,
			});

	const formFields = await promise;

	return formFields
		.flatMap((field) => ('fields' in field ? field.fields : []))
		.filter(
			(field) => 'key' in field && selectedFormFields.includes(field.key)
		)
		.some((field) => 'localizable' in field && field.localizable);
}
