/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';

import '../../../css/content_editor/ContentEditorSidePanel.scss';

import {VerticalBar} from '@clayui/core';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {datetimeUtils} from '@liferay/object-js-components-web';
import {LiferayEditorConfig} from 'frontend-editor-ckeditor-web';
import {openToast} from 'frontend-js-components-web';
import {fetch, objectToFormData} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import VocabularyService from '../../common/services/VocabularyService';
import {IAssetObjectEntry} from '../../common/types/AssetType';
import focusInvalidElement from '../../common/utils/focusInvalidElement';
import ObjectEntryService from '../../main_view/info_panel/services/ObjectEntryService';
import {Comment} from '../services/CommentService';
import {EVENT_VALIDATE_FORM} from './ContentEditorToolbar';
import {dateConfig, toMomentDate, toServerISOFormat} from './ScheduleField';
import CategorizationPanel from './panels/CategorizationPanel';
import CommentsPanel from './panels/CommentsPanel';
import GeneralPanel from './panels/GeneralPanel';
import ProjectsPanel from './panels/ProjectsPanel';
import SchedulePanel from './panels/SchedulePanel';

type Props = {
	addCommentURL: string;
	assetLibraryId: string;
	assetType: number;
	cmpProjectLinkObjectDefinitionId?: number | null;
	cmpProjectObjectDefinitionId?: number | null;
	cmpProjectViewURL?: string;
	cmpTaskObjectDefinitionId?: number | null;
	cmpTaskViewURL?: string;
	cmsGroupId: string;
	comments: Comment[];
	contentAPIURL: string;
	deleteCommentURL: string;
	editCommentURL: string;
	editorConfig: LiferayEditorConfig;
	entryClassName: string;
	entryExternalReferenceCode?: string;
	entryGroupExternalReferenceCode?: string;
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
	assetKeywords?: string[];
	categorizationFields: CategorizationFields | null;
	dateConfig: datetimeUtils.DateConfig;
	onUpdateCategorization: (props: UpdateCategorizationProps) => void;
	onUpdateSchedule: (props: UpdateScheduleProps) => void;
	requiredVocabularyIds: number[] | null;
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
		error?: string;
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

export type UpdateCategorizationProps = {
	[K in keyof CategorizationFields]: [K, Partial<CategorizationFields[K]>];
}[keyof CategorizationFields];

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
		title: Liferay.Language.get('schedule[noun]'),
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
	{
		component: ProjectsPanel,
		icon: 'archive',
		id: 'projects',
		title: Liferay.Language.get('projects'),
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
		useState<CategorizationFields | null>(null);
	const [requiredVocabularyIds, setRequiredVocabularyIds] = useState<
		number[] | null
	>(null);

	const isMounted = useIsMounted();

	const onUpdateCategorization = useCallback(
		([name, value]: UpdateCategorizationProps) => {
			setCategorizationFields((fields) =>
				fields
					? {...fields, [name]: {...fields[name], ...value}}
					: fields
			);
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
		ObjectEntryService.getObjectEntry(props.contentAPIURL).then(
			({data, error}) => {
				if (!isMounted()) {
					return;
				}

				if (data) {
					setCategorizationFields((prevState) => {

						// Only populate the categorization fields if they are
						// empty. If they are not empty, it means that the
						// categorization panel has already been opened and the
						// data has been fetched by the AssetCategorization
						// component.

						if (prevState) {
							return prevState;
						}

						return {
							assetCategoryIds: {
								serverValue: (data.taxonomyCategoryBriefs || [])
									.map(({taxonomyCategoryId: id}) => id)
									.join(','),
								value: data.taxonomyCategoryBriefs || [],
							},
							assetTagNames: {
								serverValue: (data.keywords || []).join(','),
								value: data.keywords || [],
							},
						};
					});
				}
				else if (error) {
					console.error(error);
				}
			}
		);
	}, [isMounted, props.contentAPIURL]);

	useEffect(() => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		if (form) {
			setFormId(form.id);
		}
	}, []);

	useEffect(() => {
		VocabularyService.getRequiredVocabularies({
			assetLibraryId: props.assetLibraryId,
			assetTypeId: props.assetType,
			siteId: props.cmsGroupId,
		})
			.then((vocabularies) => {
				if (!isMounted()) {
					return;
				}

				setRequiredVocabularyIds(
					vocabularies
						.map(({id}) => id)
						.filter(
							(id): id is number =>
								id !== undefined && id !== null
						)
				);
			})
			.catch((error) => {
				console.error(error);

				if (isMounted()) {
					setRequiredVocabularyIds([]);
				}
			});
	}, [isMounted, props.assetLibraryId, props.assetType, props.cmsGroupId]);

	return (
		<>
			<SidePanel
				{...props}
				assetKeywords={categorizationFields?.assetTagNames?.value}
				categorizationFields={categorizationFields}
				dateConfig={dateConfig}
				onUpdateCategorization={onUpdateCategorization}
				onUpdateSchedule={onUpdateSchedule}
				requiredVocabularyIds={requiredVocabularyIds}
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

			{categorizationFields &&
				Object.entries(categorizationFields).map(
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

	const showErrorInPanel = useCallback((panelId: React.Key) => {
		setPanel(panelId);
		setHasError(true);
	}, []);

	useEffect(() => {
		const validateScheduleFields = ({event}: {event: MouseEvent}) => {
			const hasError = Object.values(props.scheduleFields).some(
				(field) => field.error && field.serverValue
			);

			if (hasError) {
				event.preventDefault();

				showErrorInPanel('schedule');
			}
		};

		Liferay.on(EVENT_VALIDATE_FORM, validateScheduleFields);

		return () => {
			Liferay.detach(EVENT_VALIDATE_FORM, validateScheduleFields);
		};
	}, [props.scheduleFields, showErrorInPanel]);

	const {onUpdateCategorization} = props;

	useEffect(() => {
		if (
			props.categorizationFields?.assetCategoryIds?.error &&
			props.requiredVocabularyIds &&
			!hasMissingRequiredVocabulary(
				props.categorizationFields,
				props.requiredVocabularyIds
			)
		) {
			onUpdateCategorization(['assetCategoryIds', {error: ''}]);
		}

		const validateCategorizationFields = ({event}: {event: MouseEvent}) => {
			if (props.requiredVocabularyIds === null) {
				event.preventDefault();

				return;
			}

			if (
				hasMissingRequiredVocabulary(
					props.categorizationFields,
					props.requiredVocabularyIds
				)
			) {
				event.preventDefault();

				onUpdateCategorization([
					'assetCategoryIds',
					{
						error: Liferay.Language.get(
							'please-enter-at-least-one-category-for-all-mandatory-vocabularies'
						),
					},
				]);

				showErrorInPanel('categorization');
			}
		};

		Liferay.on(EVENT_VALIDATE_FORM, validateCategorizationFields);

		return () => {
			Liferay.detach(EVENT_VALIDATE_FORM, validateCategorizationFields);
		};
	}, [
		onUpdateCategorization,
		props.categorizationFields,
		props.requiredVocabularyIds,
		showErrorInPanel,
	]);

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
						<VerticalBar.Panel key={item.id}>
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
					<VerticalBar.Item divider={item.divider} key={item.id}>
						<ClayButtonWithIcon
							aria-label={item.title}
							data-canonical-name={item.title}
							data-tooltip-align="left"
							displayType={null}
							ref={panel === item.id ? buttonRef : null}
							symbol={item.icon}
							title={item.title}
						/>
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

function hasMissingRequiredVocabulary(
	categorizationFields: CategorizationFields | null,
	requiredVocabularyIds: number[]
) {
	const selectedVocabularyIds = new Set(
		(categorizationFields?.assetCategoryIds?.value || []).map(
			({embeddedTaxonomyCategory}) =>
				embeddedTaxonomyCategory?.taxonomyVocabularyId
		)
	);

	return requiredVocabularyIds.some((id) => !selectedVocabularyIds.has(id));
}
