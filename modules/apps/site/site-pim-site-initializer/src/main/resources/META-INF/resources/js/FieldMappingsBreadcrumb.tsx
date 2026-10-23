/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Breadcrumb} from '@liferay/site-cms-site-initializer';
import React, {ComponentProps, useEffect, useState} from 'react';

import {EVENT_FIELD_MAPPINGS_CHANGE} from './FieldMappingsFDSPropsTransformer';

type MappingStatus = 'complete' | 'incomplete';

interface IActionItem {
	id?: string;
	[key: string]: any;
}

export default function FieldMappingsBreadcrumb({
	actionItems = [],
	mappingStatus: initialMappingStatus,
	...props
}: Omit<ComponentProps<typeof Breadcrumb>, 'actionItems'> & {
	actionItems?: IActionItem[];
	mappingStatus: MappingStatus;
}) {
	const [mappingStatus, setMappingStatus] = useState(initialMappingStatus);

	useEffect(() => {
		const handleFieldMappingsChange = ({
			mappingStatus,
		}: {
			mappingStatus: MappingStatus;
		}) => setMappingStatus(mappingStatus);

		Liferay.on(EVENT_FIELD_MAPPINGS_CHANGE, handleFieldMappingsChange);

		return () =>
			Liferay.detach(
				EVENT_FIELD_MAPPINGS_CHANGE,
				handleFieldMappingsChange
			);
	}, []);

	return (
		<Breadcrumb
			{...props}
			actionItems={
				actionItems
					.filter(
						({id}) =>
							mappingStatus === 'complete' || id !== 'execute'
					)
					.map(
						({id: _id, ...actionItem}) => actionItem
					) as ComponentProps<typeof Breadcrumb>['actionItems']
			}
		/>
	);
}
