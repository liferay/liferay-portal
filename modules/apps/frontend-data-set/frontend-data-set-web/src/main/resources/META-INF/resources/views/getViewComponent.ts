/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '../utils/types';
import Cards from './cards/Cards';
import EmailsList from './emails_list/EmailsList';
import List from './list/List';
import SelectableTable from './selectable_table/SelectableTable';
import Table from './table/Table';
import Timeline from './timeline/Timeline';

const VIEWS = {
	cards: Cards,
	emailsList: EmailsList,
	list: List,
	selectableTable: SelectableTable,
	table: Table,
	timeline: Timeline,
};

const getViewComponent = (view: IView) => {
	return view.component || VIEWS[view.contentRenderer as keyof typeof VIEWS];
};

export default getViewComponent;
