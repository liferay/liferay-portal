/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayNavigationBar from '@clayui/navigation-bar';
import {IClientExtensionRenderer} from '@liferay/frontend-data-set-web';
import {
	ILearnResourceContext,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {DEFAULT_FETCH_HEADERS} from '../utils/constants';
import getDataSetResourceURL from '../utils/getDataSetResourceURL';
import getFields from '../utils/getFields';
import openDefaultFailureToast from '../utils/openDefaultFailureToast';
import {IDataSet, IFieldTreeItem} from '../utils/types';
import Actions from './actions/Actions';
import Details from './details/Details';
import Filters from './filters/Filters';
import Pagination from './pagination/Pagination';
import Settings from './settings/Settings';
import Sorting from './sorting/Sorting';
import VisualizationModes from './visualization_modes/VisualizationModes';

const NAVIGATION_BAR_ITEMS = [
	{
		Component: Details,
		label: Liferay.Language.get('details'),
	},
	{
		Component: VisualizationModes,
		label: Liferay.Language.get('visualization-modes'),
	},
	{
		Component: Filters,
		label: Liferay.Language.get('filters'),
	},
	{
		Component: Sorting,
		label: Liferay.Language.get('sorting'),
	},
	{
		Component: Actions,
		label: Liferay.Language.get('actions'),
	},
	{
		Component: Pagination,
		label: Liferay.Language.get('pagination'),
	},
	{
		Component: Settings,
		label: Liferay.Language.get('settings'),
	},
];

export interface IDataSetSectionProps {
	backURL: string;
	cellClientExtensionRenderers: IClientExtensionRenderer[];
	dataSet: IDataSet;
	fieldTreeItems: Array<IFieldTreeItem>;
	filterClientExtensionRenderers: IClientExtensionRenderer[];
	namespace: string;
	onActiveSectionChange: (section: number) => void;
	onDataSetUpdate: (data: IDataSet) => void;
	resolvedRESTSchemas: string[];
	restApplications: string[];
	saveDataSetSortURL: string;
	saveDataSetTableSectionsURL: string;
	spritemap: string;
}

const DataSet = ({
	backURL,
	dataSetERC,
	cellClientExtensionRenderers,
	fdsViewId,
	filterClientExtensionRenderers,
	learnResources,
	namespace,
	resolvedRESTSchemas = [],
	restApplications,
	saveDataSetSortURL,
	saveDataSetTableSectionsURL,
	spritemap,
}: {
	backURL: string;
	cellClientExtensionRenderers: IClientExtensionRenderer[];
	dataSetERC: string;
	fdsViewId: string;
	filterClientExtensionRenderers: IClientExtensionRenderer[];
	learnResources: ILearnResourceContext;
	namespace: string;
	resolvedRESTSchemas: string[];
	restApplications: string[];
	saveDataSetSortURL: string;
	saveDataSetTableSectionsURL: string;
	spritemap: string;
}) => {
	const [activeIndex, setActiveIndex] = useState(0);
	const [dataSet, setDataSet] = useState<IDataSet>();
	const [fieldTreeItems, setFieldTreeItems] = useState<Array<IFieldTreeItem>>(
		[]
	);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const getDataSet = async () => {
			const url = getDataSetResourceURL({
				dataSetERC,
			});

			const response = await fetch(url, {
				headers: DEFAULT_FETCH_HEADERS,
			});

			const responseJSON = await response.json();

			if (responseJSON?.id) {
				setDataSet(responseJSON);

				const {restApplication, restSchema} = responseJSON;

				getFields({restApplication, restSchema}).then((fields) => {
					setFieldTreeItems(fields);

					setLoading(false);
				});
			}
			else {
				openDefaultFailureToast();
			}
		};

		getDataSet();
	}, [dataSetERC, fdsViewId]);

	const Content = NAVIGATION_BAR_ITEMS[activeIndex].Component;

	return (
		<LearnResourcesContext.Provider value={learnResources}>
			<div className="cadmin fds-view">
				<ClayNavigationBar
					triggerLabel={NAVIGATION_BAR_ITEMS[activeIndex].label}
				>
					{NAVIGATION_BAR_ITEMS.map((item, index) => (
						<ClayNavigationBar.Item
							active={index === activeIndex}
							key={index}
						>
							<ClayButton onClick={() => setActiveIndex(index)}>
								{item.label}
							</ClayButton>
						</ClayNavigationBar.Item>
					))}
				</ClayNavigationBar>

				{loading ? (
					<ClayLoadingIndicator />
				) : (
					dataSet && (
						<Content
							backURL={backURL}
							cellClientExtensionRenderers={
								cellClientExtensionRenderers
							}
							dataSet={dataSet}
							fieldTreeItems={fieldTreeItems}
							filterClientExtensionRenderers={
								filterClientExtensionRenderers
							}
							namespace={namespace}
							onActiveSectionChange={(tab) => setActiveIndex(tab)}
							onDataSetUpdate={(updatedDataSet) => {
								setDataSet({...dataSet, ...updatedDataSet});
							}}
							resolvedRESTSchemas={resolvedRESTSchemas}
							restApplications={restApplications}
							saveDataSetSortURL={saveDataSetSortURL}
							saveDataSetTableSectionsURL={
								saveDataSetTableSectionsURL
							}
							spritemap={spritemap}
						/>
					)
				)}
			</div>
		</LearnResourcesContext.Provider>
	);
};

export default DataSet;
