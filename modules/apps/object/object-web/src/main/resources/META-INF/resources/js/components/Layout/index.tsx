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
import {TabsVisitor} from '../../utils/visitor';
import InfoScreen from './InfoScreen/InfoScreen';
import LayoutScreen from './LayoutScreen/LayoutScreen';
import {
	LayoutContextProvider,
	TYPES,
	useLayoutContext,
} from './objectLayoutContext';
import {
	TObjectField,
	TObjectLayout,
	TObjectLayoutTab,
	TObjectRelationship,
} from './types';

const TABS = [
	{
		Component: InfoScreen,
		label: Liferay.Language.get('info'),
	},
	{
		Component: LayoutScreen,
		label: Liferay.Language.get('layout'),
	},
];

type TNormalizeObjectFields = ({
	objectFields,
	objectLayout,
}: {
	objectFields: TObjectField[];
	objectLayout: TObjectLayout;
}) => TObjectField[];

const normalizeObjectFields: TNormalizeObjectFields = ({
	objectFields,
	objectLayout,
}) => {
	const visitor = new TabsVisitor(objectLayout);

	const objectFieldNames = objectFields.map(({name}) => name);

	const normalizedObjectFields = [...objectFields];

	visitor.mapFields((field) => {
		const objectFieldIndex = objectFieldNames.indexOf(
			field.objectFieldName
		);
		normalizedObjectFields[objectFieldIndex].inLayout = true;
	});

	return normalizedObjectFields;
};

type TNormalizeObjectRelationships = ({
	objectLayoutTabs,
	objectRelationships,
}: {
	objectLayoutTabs: TObjectLayoutTab[];
	objectRelationships: TObjectRelationship[];
}) => TObjectRelationship[];

const normalizeObjectRelationships: TNormalizeObjectRelationships = ({
	objectLayoutTabs,
	objectRelationships,
}) => {
	const objectRelationshipIds = objectRelationships.map(({id}) => id);

	const normalizedObjectRelationships = [...objectRelationships];

	objectLayoutTabs.forEach(({objectRelationshipId}) => {
		if (objectRelationshipId) {
			const objectRelationshipIndex =
				objectRelationshipIds.indexOf(objectRelationshipId);

			normalizedObjectRelationships[objectRelationshipIndex].inLayout =
				true;
		}
	});

	return normalizedObjectRelationships;
};

const Layout: React.FC<React.HTMLAttributes<HTMLElement>> = () => {
	const [{isViewOnly, objectFields, objectLayout, objectLayoutId}, dispatch] =
		useLayoutContext();
	const [activeIndex, setActiveIndex] = useState<number>(0);
	const [loading, setLoading] = useState<boolean>(true);

	useEffect(() => {
		const makeFetch = async () => {
			const {
				defaultObjectLayout,
				name,
				objectDefinitionExternalReferenceCode,
				objectLayoutTabs,
			} = await API.fetchJSON<TObjectLayout>(
				`/o/object-admin/v1.0/object-layouts/${objectLayoutId}`
			);

			const objectDefinition =
				await API.getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode
				);

			const objectFields =
				await getNonInheritanceObjectRelationshipFields(
					objectDefinition
				);

			const objectRelationships =
				await API.getObjectDefinitionByExternalReferenceCodeObjectRelationships(
					objectDefinitionExternalReferenceCode,
					{filter: 'edge eq false'}
				);

			const objectLayout = {
				defaultObjectLayout,
				name,
				objectDefinitionExternalReferenceCode,
				objectLayoutTabs,
			};

			dispatch({
				payload: {
					creationLanguageId: objectDefinition.defaultLanguageId,
					enableCategorization: objectDefinition.enableCategorization,
					objectLayout,
					objectRelationships: normalizeObjectRelationships({
						objectLayoutTabs,
						objectRelationships,
					}),
				},
				type: TYPES.ADD_OBJECT_LAYOUT,
			});

			dispatch({
				payload: {
					objectFields: normalizeObjectFields({
						objectFields,
						objectLayout,
					}),
				},
				type: TYPES.ADD_OBJECT_FIELDS,
			});

			setLoading(false);
		};

		makeFetch();
	}, [objectLayoutId, dispatch]);

	const saveObjectLayout = async () => {
		const hasFieldsInLayout = objectFields.some(
			(objectField) => objectField.inLayout
		);

		if (invalidateRequired(objectLayout.name[defaultLanguageId])) {
			openToast({
				message: Liferay.Language.get('a-name-is-required'),
				type: 'danger',
			});

			return;
		}

		if (!hasFieldsInLayout) {
			openToast({
				message: Liferay.Language.get('please-add-at-least-one-field'),
				type: 'danger',
			});

			return;
		}

		if (objectLayout.objectLayoutTabs[0].objectRelationshipId > 0) {
			openToast({
				message: Liferay.Language.get(
					'the-layouts-first-tab-must-be-a-field-tab'
				),
				type: 'danger',
			});

			return;
		}

		try {
			await API.save({
				item: objectLayout,
				url: `/o/object-admin/v1.0/object-layouts/${objectLayoutId}`,
			});
			saveAndReload();
			openToast({
				message: Liferay.Language.get(
					'the-object-layout-was-updated-successfully'
				),
			});
		}
		catch (error: unknown) {
			const {message} = error as Error;

			openToast({message, type: 'danger'});
		}
	};

	return (
		<SidePanelContent
			onSave={saveObjectLayout}
			readOnly={isViewOnly || loading}
			title={Liferay.Language.get('layout')}
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

interface ILayoutWrapperProps extends React.HTMLAttributes<HTMLElement> {
	isViewOnly: boolean;
	objectFieldBusinessTypes: ObjectFieldBusinessType[];
	objectLayoutId: string;
}

export default function LayoutWrapper({
	isViewOnly,
	objectFieldBusinessTypes,
	objectLayoutId,
}: ILayoutWrapperProps) {
	return (
		<LayoutContextProvider
			value={{
				isViewOnly,
				objectFieldBusinessTypes,
				objectLayoutId,
			}}
		>
			<Layout />
		</LayoutContextProvider>
	);
}
