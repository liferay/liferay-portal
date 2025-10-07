/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import {useId} from 'frontend-js-components-web';
import React from 'react';

import {useUpdateItemLocalConfig} from '../../../../../../app/contexts/LocalConfigContext';
import {useDispatch} from '../../../../../../app/contexts/StoreContext';
import useFormRelationshipFieldSets from '../../../../../../app/hooks/useFormRelationshipFieldSets';
import updateItemConfig from '../../../../../../app/thunks/updateItemConfig';
import {FormRelationshipLayoutDataItem} from '../../../../../../types/layout_data/FormRelationshipLayoutDataItem';

export default function FormRelationshipMappingOptions({
	item,
	showLabel = true,
}: {
	item: FormRelationshipLayoutDataItem;
	showLabel?: boolean;
}) {
	const updateItemLocalConfig = useUpdateItemLocalConfig();

	const dispatch = useDispatch();

	const fieldSets = useFormRelationshipFieldSets(item);

	const selectId = useId();

	return (
		<ClayForm.Group>
			{showLabel ? (
				<label htmlFor={selectId}>
					{Liferay.Language.get('content-type')}
				</label>
			) : null}

			<ClaySelectWithOption
				aria-label={
					showLabel
						? ''
						: Liferay.Language.get('select-a-content-type')
				}
				id={selectId}
				onChange={(event) => {
					updateItemLocalConfig(item.itemId, {
						loading: true,
					});

					dispatch(
						updateItemConfig({
							itemConfig: {
								...item.config,
								contentType: event.target.value || null,
							},
							itemIds: [item.itemId],
						})
					).then(() =>
						updateItemLocalConfig(item.itemId, {
							loading: false,
						})
					);
				}}
				options={[
					{
						label: Liferay.Language.get('none'),
						value: '',
					},
					...fieldSets.map(({label, name}) => ({
						label,
						value: name,
					})),
				]}
				sizing="sm"
				value={item.config.contentType || ''}
			/>
		</ClayForm.Group>
	);
}
