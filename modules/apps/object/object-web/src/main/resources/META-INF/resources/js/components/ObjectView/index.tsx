/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTabs from '@clayui/tabs';
import {
	API,
	SidePanelContent,
	invalidateRequired,
	openToast,
	saveAndReload,
} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {getNonInheritanceObjectRelationshipFields} from '../../utils/getNonInheritanceObjectRelationshipFields';
import BasicInfoScreen from './BasicInfoScreen/BasicInfoScreen';
import {DefaultSortScreen} from './DefaultSortScreen/DefaultSortScreen';
import {FilterScreen} from './FilterScreen/FilterScreen';
import ViewBuilderScreen from './ViewBuilderScreen/ViewBuilderScreen';
import {TYPES, ViewContextProvider, useViewContext} from './objectViewContext';
import {TObjectView, TWorkflowStatus} from './types';

const TABS = [
	{
		Component: BasicInfoScreen,
		label: Liferay.Language.get('basic-info'),
	},
	{
		Component: ViewBuilderScreen,
		label: Liferay.Language.get('view-builder'),
	},
	{
		Component: DefaultSortScreen,
		label: Liferay.Language.get('default-sort'),
	},
	{
		Component: FilterScreen,
		label: Liferay.Language.get('filters'),
	},
];

const CustomView: React.FC<
	{children?: React.ReactNode | undefined} & React.HTMLAttributes<HTMLElement>
> = () => {
	const [
		{
			isViewOnly,
			objectDefinitionExternalReferenceCode,
			objectView,
			objectViewId,
		},
		dispatch,
	] = useViewContext();

	const [activeIndex, setActiveIndex] = useState<number>(0);
	const [loading, setLoading] = useState<boolean>(true);

	useEffect(() => {
		const makeFetch = async () => {
			const {
				defaultObjectView,
				name,
				objectDefinitionId,
				objectViewColumns,
				objectViewFilterColumns,
				objectViewSortColumns,
			} = await API.fetchJSON<TObjectView>(
				`/o/object-admin/v1.0/object-views/${objectViewId}`
			);

			const objectDefinition =
				await API.getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode
				);

			const objectFields =
				await getNonInheritanceObjectRelationshipFields(
					objectDefinition
				);

			const objectView = {
				defaultObjectView,
				name,
				objectDefinitionId,
				objectViewColumns,
				objectViewFilterColumns,
				objectViewSortColumns,
			};

			dispatch({
				payload: {
					creationLanguageId: objectDefinition.defaultLanguageId,
					objectFields,
					objectView,
				},
				type: TYPES.ADD_OBJECT_VIEW,
			});

			setLoading(false);
		};

		makeFetch();
	}, [objectDefinitionExternalReferenceCode, objectViewId, dispatch]);

	const removeUnnecessaryPropertiesFromObjectView = (
		objectView: TObjectView
	) => {
		const {
			objectViewColumns,
			objectViewFilterColumns,
			objectViewSortColumns,
		} = objectView;

		const newObjectViewColumns = objectViewColumns.map((viewColumn) => {
			return {
				label: viewColumn.label,
				objectFieldName: viewColumn.objectFieldName,
				priority: viewColumn.priority,
			};
		});

		const newObjectViewFilterColumns = objectViewFilterColumns.map(
			(filterColumn) => {
				return {
					filterType: filterColumn.filterType,
					json: JSON.stringify(filterColumn.definition),
					objectFieldName: filterColumn.objectFieldName,
				};
			}
		);

		const newObjectViewSortColumns = objectViewSortColumns.map(
			(sortColumn) => {
				return {
					objectFieldName: sortColumn.objectFieldName,
					priority: sortColumn.priority,
					sortOrder: sortColumn.sortOrder,
				};
			}
		);

		const newObjectView = {
			...objectView,
			objectViewColumns: newObjectViewColumns,
			objectViewFilterColumns: newObjectViewFilterColumns,
			objectViewSortColumns: newObjectViewSortColumns,
		};

		return newObjectView;
	};

	const handleSaveObjectView = async () => {
		const newObjectView =
			removeUnnecessaryPropertiesFromObjectView(objectView);

		const {objectViewColumns} = newObjectView;

		if (invalidateRequired(objectView.name[defaultLanguageId])) {
			openToast({
				message: Liferay.Language.get('a-name-is-required'),
				type: 'danger',
			});

			return;
		}

		if (!objectView.defaultObjectView || objectViewColumns.length !== 0) {
			try {
				await API.save({
					item: newObjectView,
					url: `/o/object-admin/v1.0/object-views/${objectViewId}`,
				});
				saveAndReload();

				openToast({
					message: Liferay.Language.get(
						'modifications-saved-successfully'
					),
				});
			}
			catch (error) {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			}
		}
		else {
			openToast({
				message: Liferay.Language.get(
					'default-view-must-have-at-least-one-column'
				),
				type: 'danger',
			});
		}
	};

	return (
		<SidePanelContent
			onSave={handleSaveObjectView}
			readOnly={isViewOnly || loading}
			title={Liferay.Language.get('custom-view')}
		>
			<ClayTabs className="side-panel-iframe__tabs">
				{TABS.map(({label}, index) => (
					<ClayTabs.Item
						active={activeIndex === index}
						key={index}
						onClick={() => setActiveIndex(index)}
					>
						{label}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content activeIndex={activeIndex} fade>
				{TABS.map(({Component}, index) => (
					<ClayTabs.TabPane key={index}>
						{!loading && <Component />}
					</ClayTabs.TabPane>
				))}
			</ClayTabs.Content>
		</SidePanelContent>
	);
};
interface ICustomViewWrapperProps extends React.HTMLAttributes<HTMLElement> {
	filterOperators: TFilterOperators;
	isViewOnly: boolean;
	objectDefinitionExternalReferenceCode: string;
	objectViewId: string;
	workflowStatuses: TWorkflowStatus[];
}

const CustomViewWrapper: React.FC<
	{children?: React.ReactNode | undefined} & ICustomViewWrapperProps
> = ({
	filterOperators,
	isViewOnly,
	objectDefinitionExternalReferenceCode,
	objectViewId,
	workflowStatuses,
}) => {
	return (
		<ViewContextProvider
			value={{
				filterOperators,
				isViewOnly,
				objectDefinitionExternalReferenceCode,
				objectViewId,
				workflowStatuses,
			}}
		>
			<CustomView />
		</ViewContextProvider>
	);
};

export default CustomViewWrapper;
