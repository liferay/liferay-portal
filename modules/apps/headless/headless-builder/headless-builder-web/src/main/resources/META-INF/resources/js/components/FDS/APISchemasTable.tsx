/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openModal} from 'frontend-js-components-web';
import React, {Dispatch, SetStateAction, useContext, useEffect} from 'react';

import {EditAPIApplicationContext} from '../EditAPIApplicationContext';
import {CreateAPISchemaModalContent} from '../modals/CreateAPISchemaModalContent';
import {DeleteAPISchemaModalContent} from '../modals/DeleteAPISchemaModalContent';
import {getFilterRelatedItemURL} from '../utils/urlUtil';
import {getAPISchemasFDSProps} from './fdsUtils/schemasFDSProps';

interface APIApplicationsTableProps {
	apiURLPaths: APIURLPaths;
	currentAPIApplicationId: string | null;
	portletId: string;
	setMainSchemaNav: Dispatch<SetStateAction<MainNav>>;
}

export default function APISchemasTable({
	apiURLPaths,
	currentAPIApplicationId,
	portletId,
	setMainSchemaNav,
}: APIApplicationsTableProps) {
	const {setHideManagementButtons} = useContext(EditAPIApplicationContext);

	const createAPISchema = {
		label: Liferay.Language.get('add-new-schema'),
		onClick: ({loadData}: {loadData: voidReturn}) => {
			openModal({
				center: true,
				contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
					CreateAPISchemaModalContent({
						apiSchemasURLPath: apiURLPaths.schemas,
						closeModal,
						currentAPIApplicationId,
						loadData,
						setMainSchemaNav,
					}),
				id: 'createAPISchemaModal',
				size: 'md',
			});
		},
	};

	const schemaAPIURLPath = getFilterRelatedItemURL({
		apiURLPath: apiURLPaths.schemas,
		filterQuery: `r_apiApplicationToAPISchemas_l_apiApplicationId eq '${currentAPIApplicationId}'`,
	});

	const deleteAPISchema = (itemData: APISchemaItem, loadData: voidReturn) => {
		openModal({
			center: true,
			contentComponent: ({closeModal}: {closeModal: voidReturn}) =>
				DeleteAPISchemaModalContent({
					closeModal,
					itemData,
					loadData,
				}),
			id: 'deleteAPISchemaModal',
			size: 'md',
			status: 'danger',
		});
	};

	function onActionDropdownItemClick({
		action,
		itemData,
		loadData,
	}: FDSItem<APISchemaItem>) {
		if (action.id === 'deleteAPISchema') {
			deleteAPISchema(itemData, loadData);
		}

		if (action.id === 'editAPISchema') {
			setMainSchemaNav({edit: itemData.id});
		}
	}

	useEffect(() => {
		setHideManagementButtons(true);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<FrontendDataSet
			{...getAPISchemasFDSProps(
				schemaAPIURLPath,
				portletId,
				setMainSchemaNav
			)}
			creationMenu={{
				primaryItems: [createAPISchema],
			}}
			onActionDropdownItemClick={onActionDropdownItemClick}
		/>
	);
}
