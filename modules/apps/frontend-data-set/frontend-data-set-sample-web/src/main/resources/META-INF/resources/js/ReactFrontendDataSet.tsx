/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayBreadCrumb from '@clayui/breadcrumb';
import ClayButton from '@clayui/button';
import {
	DisplayType,
	FrontendDataSet,
	IFrontendDataSetProps,
	IInlineNotificationComponent,
} from '@liferay/frontend-data-set-web';
import React from 'react';

enum EComponentType {
	ALERT = 'alert',
	BREADCRUMB = 'breadcrumb',
	SPAN = 'span',
}

const ReactFrontendDataSet = (initialProps: IFrontendDataSetProps) => {
	const cardsView = {
		_key: 'cards',
		contentRenderer: 'cards',
		default: false,
		label: 'Cards',
		name: 'cards',
		schema: {
			description: 'description',
			href: '',
			image: '',
			labels: [
				{
					displayType: DisplayType.INFO,
					value: 'color',
				},
				{
					displayTypeKey: 'status.label',
					displayTypeValues: {
						approved: DisplayType.SUCCESS,
						expired: DisplayType.DANGER,
					},
					value: 'status.label_i18n',
				},
			],
			sticker: '',
			symbol: '',
			title: 'title',
		},
		thumbnail: 'cards2',
	};

	const listView = {
		contentRenderer: 'list',
		default: false,
		label: 'List',
		name: 'list',
		schema: {
			description: 'description',
			image: '',
			sticker: '',
			symbol: '',
			title: 'title',
		},
		thumbnail: 'list',
	};

	const props: IFrontendDataSetProps = {
		...initialProps,
		creationMenu: {
			primaryItems: [
				{
					data: {id: 'addSample'},
					icon: 'plus',
					label: 'Add Sample',
					onClick: () => alert('Add Sample clicked!'),
				},
				{
					data: {id: 'templates'},
					items: [
						{
							data: {id: 'blankSample'},
							label: 'Blank Sample',
							onClick: () => alert('Blank Sample clicked!'),
						},
						{
							data: {id: 'sampleFromTemplate'},
							label: 'Sample from Template',
							onClick: () =>
								alert('Sample from Template clicked!'),
						},
					],
					label: 'Templates',
					separator: true,
					type: 'group',
				},
				{
					data: {id: 'importSamples'},
					icon: 'upload',
					items: [
						{
							data: {id: 'importFromFile'},
							label: 'From File',
							onClick: () => alert('From File clicked!'),
						},
						{
							data: {id: 'importFromURL'},
							label: 'From URL',
							onClick: () => alert('From URL clicked!'),
						},
					],
					label: 'Import Samples',
					type: 'contextual',
				},
			],
		},
		sorts: [
			{
				active: true,
				direction: 'asc',
				key: 'title',
				label: 'By Title',
			},
		],
	};

	props.views.push(cardsView, listView);

	const [componentType, setComponentType] =
		React.useState<EComponentType | null>(null);
	const [selectedItems, setSelectedItems] = React.useState<any[]>([]);

	const AlertInlineNotificationComponent = ({
		context,
	}: {
		context: IInlineNotificationComponent['context'];
	}) => {
		return (
			<ClayAlert
				displayType="info"
				onClose={() => setComponentType(null)}
				variant="stripe"
			>
				{context?.selectedItems?.length
					? `${selectedItems.length} items selected`
					: 'No items selected'}

				<ClayButton.Group className="pl-3" spaced>
					<ClayButton
						displayType="info"
						onClick={() => {
							const newSort = {
								active: true,
								direction: 'desc' as const,
								key: 'dateCreated',
								label: 'By Date',
							};

							let updatedSorts: Array<any> = [];

							updatedSorts = updatedSorts
								.concat(
									context?.sorts?.map((sort) => {
										sort.active = false;

										return sort;
									})
								)
								.filter((sort) => sort.key !== 'dateCreated');

							updatedSorts.push(newSort);

							context && context.forceSortsUpdate(updatedSorts);

							setComponentType(null);
						}}
						size="sm"
					>
						{Liferay.Language.get('reload')}
					</ClayButton>

					<ClayButton
						displayType="danger"
						onClick={() => {
							context &&
								context.updateAdditionalAPIURLParameters(
									`sort=dateCreated:desc&t=${Date.now()}`
								);

							setComponentType(null);
						}}
						size="sm"
					>
						{Liferay.Language.get('refresh')}
					</ClayButton>

					<ClayButton
						alert
						onClick={() => setComponentType(null)}
						size="sm"
					>
						{Liferay.Language.get('dismiss')}
					</ClayButton>
				</ClayButton.Group>
			</ClayAlert>
		);
	};

	const BreadCrumbInlineNotificationComponent = () => {
		return (
			<ClayBreadCrumb
				items={[
					{
						href: '#1',
						label: 'Home',
					},
					{
						href: '#2',
						label: 'About',
					},
					{
						href: '#3',
						label: 'Contact',
					},
				]}
			/>
		);
	};

	const SpanInlineNotificationComponent = ({
		context,
	}: {
		context: IInlineNotificationComponent['context'];
	}) => {
		return (
			<span>
				This is a notification message with {context?.selectedItems}
			</span>
		);
	};

	return (
		<>
			<ClayButton.Group spaced>
				<ClayButton
					displayType="primary"
					onClick={() => setSelectedItems([])}
				>
					Clear
				</ClayButton>

				<ClayButton
					displayType="info"
					onClick={() => {
						setComponentType(EComponentType.ALERT);
					}}
				>
					Show info message
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() => {
						setComponentType(EComponentType.BREADCRUMB);
					}}
				>
					Show breadcrumb
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() => {
						setComponentType(EComponentType.SPAN);
					}}
				>
					Show text
				</ClayButton>
			</ClayButton.Group>

			<FrontendDataSet
				{...props}
				inlineNotificationComponent={({context}) => {
					if (componentType === EComponentType.ALERT) {
						return (
							<AlertInlineNotificationComponent
								context={context}
							/>
						);
					}
					else if (componentType === EComponentType.BREADCRUMB) {
						return <BreadCrumbInlineNotificationComponent />;
					}
					else if (componentType === EComponentType.SPAN) {
						return (
							<SpanInlineNotificationComponent
								context={context}
							/>
						);
					}
					else {
						return null;
					}
				}}
				onSelectedItemsChange={setSelectedItems}
				selectedItems={selectedItems}
				selectionType="multiple"
			/>
		</>
	);
};

export default ReactFrontendDataSet;
