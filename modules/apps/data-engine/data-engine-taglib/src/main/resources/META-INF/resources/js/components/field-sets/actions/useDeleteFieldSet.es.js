/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useForm, useFormState} from 'data-engine-js-components-web';

import {EVENT_TYPES} from '../../../eventTypes';
import {deleteItem} from '../../../utils/client.es';
import {errorToast, successToast} from '../../../utils/toast.es';

const useDeleteFieldSet = () => {
	const dispatch = useForm();
	const {fieldSets} = useFormState();

	return async (fieldSet) => {
		const endpoint = '/o/data-engine/v2.0/data-definitions/';

		try {
			const {ok} = await deleteItem(`${endpoint}${fieldSet.id}`);

			if (!ok) {
				throw new Error();
			}

			dispatch({
				payload: {
					fieldSets: fieldSets.filter(({id}) => id !== fieldSet.id),
				},
				type: EVENT_TYPES.FIELD_SET.UPDATE_LIST,
			});

			successToast(
				Liferay.Language.get('the-item-was-deleted-successfully')
			);

			return true;
		}
		catch (error) {
			errorToast(Liferay.Language.get('the-item-could-not-be-deleted'));

			return false;
		}
	};
};

export default useDeleteFieldSet;
