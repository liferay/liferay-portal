/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import AgentDefinitionListItemTitleRenderer from './AgentDefinitionListItemTitleRenderer';

import type {
	IInternalRenderer,
	IListSchema,
	IView,
} from '@liferay/frontend-data-set-web';

export default function propsTransformer({itemsActions, ...otherProps}: any) {
	const customListTitleRenderer: IInternalRenderer = {
		component: AgentDefinitionListItemTitleRenderer,
		name: 'customListTitleRenderer',
		type: 'internal',
	};

	const views: Array<IView> = otherProps.views;

	const listView = views.find((view) => view.name === 'list')!;

	const listSchema = listView.schema as IListSchema;

	listView.setItemComponentProps = ({props}: {props: any}) => {
		const updatedProps = {
			...props,
			schema: {
				...listSchema,
				titleRenderer: 'customListTitleRenderer',
			},
		};

		return updatedProps;
	};

	return {
		...otherProps,
		customRenderers: {
			listSection: [customListTitleRenderer],
		},
		itemsActions,
		views,
	};
}
