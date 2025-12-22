/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';

import '../../../css/content_editor/ContentEditorSidePanel.scss';

import {Button, VerticalBar} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {LiferayEditorConfig} from 'frontend-editor-ckeditor-web';
import {openToast} from 'frontend-js-components-web';
import {fetch, objectToFormData} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {IAssetObjectEntry} from '../../common/types/AssetType';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import {Comment} from '../services/CommentService';
import {EVENT_VALIDATE_FORM} from './ContentEditorToolbar';
import {dateConfig, toMomentDate, toServerISOFormat} from './ScheduleField';
import CategorizationPanel from './panels/CategorizationPanel';
import CommentsPanel from './panels/CommentsPanel';
import GeneralPanel from './panels/GeneralPanel';
import SchedulePanel from './panels/SchedulePanel';

type Props = {
	addCommentURL: string;
	assetLibraryId: string;
	assetType: number;
	cmsGroupId: string;
	comments: Comment[];
	contentAPIURL: string;
	deleteCommentURL: string;
	editCommentURL: string;
	editorConfig: LiferayEditorConfig;
	entryClassName: string;
	expirationDate: string;
	getCommentsURL: string;
	hasUpdatePermission: boolean;
	id: string;
	isSubscribed: boolean;
	reviewDate: string;
	subscribeURL: string;
	type: string;
	version: string;
};

type SidePanelProps = Props & {
	categorizationFields: CategorizationFields;
	dateConfig: datetimeUtils.DateConfig;
	onUpdateCategorization: (props: UpdateCategorizationProps) => void;
	onUpdateSchedule: (props: UpdateScheduleProps) => void;
	scheduleFields: ScheduleFields;
};

type Item = {
	component: React.ComponentType<SidePanelProps>;
	divider?: boolean;
	icon: string;
	id: string;
	title: string;
};

type BaseScheduleData = {
	error: string;
	neverCheckbox: {label: string; value: boolean};
	value: string;
};

export type CategorizationFields = {
	assetCategoryIds: {
		serverValue: string;
		value: IAssetObjectEntry['taxonomyCategoryBriefs'];
	};
	assetTagNames: {
		serverValue: string;
		value: IAssetObjectEntry['keywords'];
	};
};

type ScheduleFieldData = BaseScheduleData & {
	serverValue: string;
};

export type ScheduleFields = {
	expirationDate: ScheduleFieldData;
	reviewDate: ScheduleFieldData;
};

export type UpdateCategorizationProps = [
	keyof CategorizationFields,
	CategorizationFields[keyof CategorizationFields],
];

export type UpdateScheduleProps = BaseScheduleData & {
	name: keyof ScheduleFields;
};

const items: Item[] = [
	{
		component: GeneralPanel,
		icon: 'info-circle',
		id: 'general',
		title: Liferay.Language.get('general'),
	},
	{
		component: SchedulePanel,
		icon: 'date-time',
		id: 'schedule',
		title: Liferay.Language.get('schedule'),
	},
	{
		component: CategorizationPanel,
		icon: 'categories',
		id: 'categorization',
		title: Liferay.Language.get('categorization'),
	},
	{
		component: CommentsPanel,
		icon: 'comments',
		id: 'comments',
		title: Liferay.Language.get('comments'),
	},
];

export default function ContentEditorSidePanel(props: Props) {
	const [formId, setFormId] = useState<string | undefined>();
	const [scheduleFields, setScheduleFields] = useState<ScheduleFields>({
		expirationDate: {
			error: '',
			neverCheckbox: {
				label: Liferay.Language.get('never-expire'),
				value: !props.expirationDate,
			},
			serverValue: props.expirationDate,
			value: toMomentDate(props.expirationDate),
		},
		reviewDate: {
			error: '',
			neverCheckbox: {
				label: Liferay.Language.get('never-review'),
				value: !props.reviewDate,
			},
			serverValue: props.reviewDate,
			value: toMomentDate(props.reviewDate),
		},
	});
	const [categorizationFields, setCategorizationFields] =
		useState<CategorizationFields>({
			assetCategoryIds: {serverValue: '', value: []},
			assetTagNames: {serverValue: '', value: []},
		});

	const onUpdateCategorization = useCallback(
		([name, value]: UpdateCategorizationProps) => {
			setCategorizationFields((fields) => ({
				...fields,
				[name]: value,
			}));
		},
		[]
	);

	const onUpdateSchedule = ({
		error,
		name,
		neverCheckbox,
		value,
	}: UpdateScheduleProps) => {
		const values = neverCheckbox
			? {serverValue: ''}
			: {
					serverValue: toServerISOFormat(value),
					value,
				};

		setScheduleFields((fields) => ({
			...fields,
			[name]: {
				...fields[name],
				...values,
				error,
			},
		}));
	};

	useEffect(() => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		if (form) {
			setFormId(form.id);
		}
	}, []);

	return (
		<>
			<SidePanel
				{...props}
				categorizationFields={categorizationFields}
				dateConfig={dateConfig}
				onUpdateCategorization={onUpdateCategorization}
				onUpdateSchedule={onUpdateSchedule}
				scheduleFields={scheduleFields}
			/>
			{Object.entries(scheduleFields).map(([name, {serverValue}]) => (
				<input
					form={formId}
					key={name}
					name={`ObjectEntry_${name}`}
					type="hidden"
					value={serverValue}
				/>
			))}

			{Object.entries(categorizationFields).map(
				([name, {serverValue}]) => (
					<input
						form={formId}
						key={name}
						name={name}
						type="hidden"
						value={serverValue}
					/>
				)
			)}
		</>
	);
}

function SidePanel(props: SidePanelProps) {
	const buttonRef = useRef<HTMLButtonElement>(null);
	const [hasError, setHasError] = useState<boolean>(false);
	const [panel, setPanel] = useState<React.Key | null>(null);

	useEffect(() => {
		const validateScheduleFields = ({event}: {event: MouseEvent}) => {
			const hasError = Object.values(props.scheduleFields).some(
				(field) => field.error && field.serverValue
			);

			if (hasError) {
				event.preventDefault();

				setPanel(Liferay.Language.get('schedule'));
				setHasError(true);
			}
		};

		Liferay.on(EVENT_VALIDATE_FORM, validateScheduleFields);

		return () => {
			Liferay.detach(EVENT_VALIDATE_FORM, validateScheduleFields);
		};
	}, [props.scheduleFields]);

	useEffect(() => {
		if (hasError) {
			focusInvalidElement();
			setHasError(false);
		}
	}, [hasError]);

	return (
		<VerticalBar
			active={panel}
			className="content-editor__side-panel"
			onActiveChange={setPanel}
			panelWidth={280}
		>
			<VerticalBar.Content items={items}>
				{(item) => {
					const Component = item.component;

					return (
						<VerticalBar.Panel key={item.title}>
							<div className="align-items-center d-flex justify-content-between pl-3 sidebar-header">
								<div className="component-title">
									{item.title}
								</div>

								<div>
									{item.id === 'comments' ? (
										<SubscribeButton
											isSubscribed={props.isSubscribed}
											subscribeURL={props.subscribeURL}
										/>
									) : null}

									<ClayButtonWithIcon
										aria-label={Liferay.Language.get(
											'close'
										)}
										borderless
										displayType="secondary"
										monospaced
										onClick={() => {
											setPanel(null);

											buttonRef.current?.focus();
										}}
										size="sm"
										symbol="times"
										title={Liferay.Language.get('close')}
									/>
								</div>
							</div>

							<Component {...props} />
						</VerticalBar.Panel>
					);
				}}
			</VerticalBar.Content>

			<VerticalBar.Bar displayType="light" items={items}>
				{(item) => (
					<VerticalBar.Item divider={item.divider} key={item.title}>
						<Button
							aria-label={item.title}
							data-tooltip-align="left"
							displayType={null}
							ref={panel === item.title ? buttonRef : null}
							title={item.title}
						>
							<ClayIcon symbol={item.icon} />
						</Button>
					</VerticalBar.Item>
				)}
			</VerticalBar.Bar>
		</VerticalBar>
	);
}

function SubscribeButton({
	isSubscribed,
	subscribeURL,
}: {
	isSubscribed: boolean;
	subscribeURL: string;
}) {
	const [disabled, setDisabled] = useState<boolean>(false);
	const [subscribed, setSubscribed] = useState<boolean>(isSubscribed);

	const title = subscribed
		? Liferay.Language.get('unsubscribe')
		: Liferay.Language.get('subscribe');

	return (
		<ClayButtonWithIcon
			aria-label={title}
			borderless
			disabled={disabled}
			displayType="secondary"
			monospaced
			onClick={async () => {
				setDisabled(true);

				const response = await fetch(subscribeURL, {
					body: objectToFormData({
						cmd: !subscribed ? 'subscribe' : 'unsubscribe',
					}),
					method: 'POST',
				});

				const subscription = await response.json();

				setDisabled(false);

				if (subscription.error) {
					openToast({
						message:
							subscription.error ||
							Liferay.Language.get(
								'an-unexpected-system-error-occurred'
							),
						type: 'danger',
					});
				}
				else {
					setSubscribed((subscribed) => !subscribed);

					openToast({
						message: subscribed
							? Liferay.Language.get(
									'you-have-successfully-unsubscribed-from-comments'
								)
							: Liferay.Language.get(
									'you-have-successfully-subscribed-to-comments'
								),
						type: 'success',
					});
				}
			}}
			size="sm"
			symbol={subscribed ? 'bell-off' : 'bell-on'}
			title={title}
		/>
	);
}
