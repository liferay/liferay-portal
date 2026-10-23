/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Breadcrumb} from '@liferay/site-cms-site-initializer';
import React, {ComponentProps, useEffect, useState} from 'react';

import {EVENT_FIELD_MAPPINGS_CHANGE} from './constants';

interface IActionItem {
	id?: string;
	[key: string]: any;
}

export default function FieldMappingsBreadcrumb({
	actionItems = [],
	mappingComplete: initialMappingComplete,
	...props
}: Omit<ComponentProps<typeof Breadcrumb>, 'actionItems'> & {
	actionItems?: IActionItem[];
	mappingComplete: boolean;
}) {
	const [mappingComplete, setMappingComplete] = useState(
		initialMappingComplete
	);

	useEffect(() => {
		const handleFieldMappingsChange = ({
			mappingComplete,
		}: {
			mappingComplete: boolean;
		}) => setMappingComplete(mappingComplete);

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
					.filter(({id}) => mappingComplete || id !== 'execute')
					.map(
						({id: _id, ...actionItem}) => actionItem
					) as ComponentProps<typeof Breadcrumb>['actionItems']
			}
		/>
	);
}
