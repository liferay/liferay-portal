/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ListView, {ListViewProps} from '../../../components/ListView';
import {TableProps} from '../../../components/Table';
import {ListViewContextProviderProps} from '../../../context/ListViewContext';
import {FormModal} from '../../../hooks/useFormModal';
import i18n from '../../../i18n';
import {Action} from '../../../types';

type RoutineListViewProps = {
	actions?: Action[];
	formModal?: FormModal;
	projectId?: number | string;
	resource: string;
	variables?: any;
} & {
	listViewProps?: Partial<ListViewProps> & {
		initialContext?: Partial<ListViewContextProviderProps>;
	};
	tableProps?: Partial<TableProps>;
};

const RoutineListView: React.FC<RoutineListViewProps> = ({
	actions,
	formModal,
	listViewProps,
	resource,
	tableProps,
	variables,
}) => {
	return (
		<ListView
			forceRefetch={formModal?.forceRefetch}
			managementToolbarProps={{
				applyFilters: false,
				title: i18n.translate('routines'),
			}}
			resource={resource}
			tableProps={{
				actions,
				columns: [
					{
						clickable: true,
						key: 'testrayRoutineName',
						size: 'md',
						sorteable: true,
						value: i18n.translate('routine'),
					},
				],
				navigateTo: ({id}) => id?.toString(),
				...tableProps,
			}}
			variables={variables}
			{...listViewProps}
		/>
	);
};

export default RoutineListView;
