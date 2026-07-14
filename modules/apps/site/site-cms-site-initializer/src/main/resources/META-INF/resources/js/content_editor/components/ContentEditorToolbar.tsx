/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/content_editor/ContentEditorToolbar.scss';

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {AIAssistantChat} from '@liferay/ai-hub-cell-js-components-web';
import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {openToast, useSessionState} from 'frontend-js-components-web';
import {sessionStorage, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useId, useRef, useState} from 'react';
import {flushSync} from 'react-dom';

import Toolbar from '../../common/components/Toolbar';
import {toMomentDate} from './ScheduleField';
import SchedulePublicationModal from './SchedulePublicationModal';
import PreviewModal from './preview/PreviewModal';
import {
	PREVIEW_CACHED_EXTERNAL_URL_SESSION_KEY,
	PREVIEW_CHANNEL_SESSION_KEY,
	PREVIEW_DISPLAY_PAGE_SESSION_KEY,
	PREVIEW_EXTERNAL_URL_SESSION_KEY,
	PREVIEW_VISIBLE_SESSION_KEY,
	PREVIEW_WIDTH_SESSION_KEY,
} from './preview/sessionKeys';
import useLocalizationLanguageId from './useLocalizationLanguageId';

export const EVENT_CLOSE_PREVIEW = 'contentEditor:closePreview';

export const EVENT_HANDLE_PREVIEW = 'contentEditor:handlePreview';

export const EVENT_VALIDATE_FORM = 'contentEditor:validateForm';

const STATUS_DRAFT_CODE = 2;

const SUCCESS_MESSAGE_SESSION_KEY =
	'com.liferay.site.cms.site.initializer.successMessage';

export default function ContentEditorToolbar({
	backURL,
	defaultLanguageId,
	displayDate: initialDisplayDate,
	getPreviewDataURL,
	groupId,
	hasWorkflow,
	headerTitle,
	isNew,
	title,
	type,
}: {
	backURL: string;
	defaultLanguageId: Liferay.Language.Locale;
	displayDate: string;
	getPreviewDataURL: string;
	groupId: number;
	hasWorkflow: boolean;
	headerTitle: string;
	isNew: boolean;
	title: string;
	type: string;
}) {
	const [displayDate, setDisplayDate] = useState<string>('');
	const [formId, setFormId] = useState<string | undefined>();
	const [redirect, setRedirect] = useState<string>(backURL);
	const [showModal, setShowModal] = useState<boolean>(false);
	const [showPreview, setShowPreview] = useSessionState<boolean>(
		PREVIEW_VISIBLE_SESSION_KEY,
		false
	);
	const [showPreviewModal, setShowPreviewModal] = useState<boolean>(false);

	const localizationLanguageId = useLocalizationLanguageId(defaultLanguageId);
	const previewButtonRef = useRef<HTMLButtonElement>(null);

	const optionsTitle = hasWorkflow
		? Liferay.Language.get('submit-for-workflow-options')
		: Liferay.Language.get('publish-options');
	const submitLabelId = useId();
	const submitTitle = getSubmitTitle(
		hasWorkflow
			? sub(Liferay.Language.get('submit-x-for-workflow'), type)
			: sub(Liferay.Language.get('publish-x'), type)
	);

	const getForm = useCallback((): HTMLFormElement => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		return form as HTMLFormElement;
	}, []);

	const setSuccessMessage = useCallback(
		(message: string) => {
			const form = getForm();

			if (form?.checkValidity?.()) {
				const defaultLanguageId =
					Liferay.ThemeDisplay.getDefaultLanguageId();

				const defaultTitleInput = form.querySelector(
					`[name="ObjectField_title_${defaultLanguageId}"]`
				) as HTMLInputElement | null;

				const currentTitleInput = form.querySelector(
					'[name="ObjectField_title"]'
				) as HTMLInputElement | null;

				const titleInput = defaultTitleInput ?? currentTitleInput;

				const value = titleInput?.value.trim() || headerTitle;

				sessionStorage.setItem(
					SUCCESS_MESSAGE_SESSION_KEY,
					sub(message, `<strong>${value}</strong>`),
					sessionStorage.TYPES.NECESSARY
				);
			}
		},
		[getForm, headerTitle]
	);

	const handleSaveAsDraftClick = useCallback(() => {
		flushSync(() => setRedirect(window.location.href));

		setSuccessMessage(
			Liferay.Language.get('the-draft-was-saved-successfully')
		);
	}, [setSuccessMessage]);

	const handlePublishClick = useCallback(() => {
		flushSync(() => setRedirect(backURL));

		clearSessionStates();

		setSuccessMessage(
			hasWorkflow
				? Liferay.Language.get('x-was-submitted-for-workflow')
				: isNew
					? Liferay.Language.get('x-was-created-successfully')
					: Liferay.Language.get('x-was-updated-successfully')
		);
	}, [backURL, hasWorkflow, isNew, setSuccessMessage]);

	useEffect(() => {
		const form = getForm();

		if (form) {
			setFormId(form.id);

			const handlePublishShortcut = (event: KeyboardEvent) => {
				if (
					event.altKey &&
					event.key === 'Enter' &&
					isCtrlOrMeta(event)
				) {
					handlePublishClick();

					form.submit();
				}
			};

			window.addEventListener('keydown', handlePublishShortcut);

			return () =>
				window.removeEventListener('keydown', handlePublishShortcut);
		}
	}, [getForm, handlePublishClick]);

	useEffect(() => {
		const closePreview = () => {
			setShowPreview(false);

			previewButtonRef.current?.focus();
		};

		Liferay.on(EVENT_CLOSE_PREVIEW, closePreview);

		return () => Liferay.detach(EVENT_CLOSE_PREVIEW, closePreview);
	}, [setShowPreview]);

	useEffect(() => {
		const message = sessionStorage.getItem(
			SUCCESS_MESSAGE_SESSION_KEY,
			sessionStorage.TYPES.NECESSARY
		);

		if (!message) {
			return;
		}

		sessionStorage.removeItem(SUCCESS_MESSAGE_SESSION_KEY);

		if (getForm()?.querySelector('.alert-danger, .form-group.has-error')) {
			return;
		}

		openToast({message, type: 'success'});
	}, [getForm]);

	return (
		<Toolbar
			backURL={backURL}
			className="content-editor__toolbar position-fixed"
			onBackClick={clearSessionStates}
			title={headerTitle}
		>
			{Liferay.FeatureFlags['LPD-62272'] && (
				<>
					<Toolbar.Item>
						<AIAssistantChat
							context={{groupId}}
							hideTriggerLabel
							instructionDefinitionScope="cms"
							triggerRound
						/>
					</Toolbar.Item>

					<div className="ai-assistant__separator" />
				</>
			)}

			<Toolbar.Item className="nav-divider-end">
				<ClayButton
					aria-label={
						showPreview
							? Liferay.Language.get('close-preview')
							: Liferay.Language.get('open-preview')
					}
					aria-pressed={showPreview}
					borderless
					className={classNames('d-lg-block d-none', {
						active: showPreview,
					})}
					displayType="secondary"
					onClick={() => {
						const nextShowPreview = !showPreview;

						setShowPreview(nextShowPreview);

						Liferay.fire(EVENT_HANDLE_PREVIEW, {
							showPreview: nextShowPreview,
						});
					}}
					ref={previewButtonRef}
					size="sm"
				>
					<ClayIcon
						className="inline-item inline-item-before"
						symbol="view"
					/>

					{Liferay.Language.get('preview')}
				</ClayButton>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('preview')}
					borderless
					className="c-mr-1 d-lg-none"
					displayType="secondary"
					onClick={() => setShowPreviewModal(true)}
					size="sm"
					symbol="view"
					title={Liferay.Language.get('preview')}
				/>
			</Toolbar.Item>

			<Toolbar.Item className="c-pl-0">
				<ClayLink
					aria-label={Liferay.Language.get('cancel')}
					borderless
					button
					className="d-none d-sm-flex"
					displayType="secondary"
					href={backURL}
					onClick={clearSessionStates}
					small
				>
					{Liferay.Language.get('cancel')}
				</ClayLink>
			</Toolbar.Item>

			<Toolbar.Item>
				<ClayButton
					className="d-md-flex d-none"
					displayType="secondary"
					form={formId}
					name="status"
					onClick={handleSaveAsDraftClick}
					size="sm"
					type="submit"
					value={STATUS_DRAFT_CODE}
				>
					{Liferay.Language.get('save-as-draft')}
				</ClayButton>

				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('save-as-draft')}
					className="d-md-none"
					displayType="secondary"
					size="sm"
					symbol="disk"
					title={Liferay.Language.get('save-as-draft')}
				/>
			</Toolbar.Item>

			<Toolbar.Item>
				<ClayButton.Group>
					<ClayButton
						aria-labelledby={submitLabelId}
						data-title={submitTitle}
						data-title-set-as-html
						form={formId}
						onClick={(event) => {
							handlePublishClick();

							Liferay.fire(EVENT_VALIDATE_FORM, {event});
						}}
						size="sm"
						type="submit"
					>
						{hasWorkflow
							? Liferay.Language.get('submit-for-workflow')
							: Liferay.Language.get('publish')}
					</ClayButton>

					<span
						className="sr-only"
						dangerouslySetInnerHTML={{__html: submitTitle}}
						id={submitLabelId}
					/>

					<ClayDropDownWithItems
						className="btn-group"
						items={[
							{
								label: hasWorkflow
									? Liferay.Language.get(
											'schedule-publication-and-submit-for-workflow'
										)
									: Liferay.Language.get(
											'schedule-publication'
										),
								onClick: () => setShowModal(true),
								symbolLeft: 'date-time',
							},
						]}
						trigger={
							<ClayButtonWithIcon
								aria-label={optionsTitle}
								size="sm"
								symbol="caret-bottom"
								title={optionsTitle}
							/>
						}
					/>
				</ClayButton.Group>

				<ClayInput
					form={formId}
					name="redirect"
					type="hidden"
					value={redirect}
				/>

				{displayDate && (
					<ClayInput
						form={formId}
						name="ObjectEntry_displayDate"
						type="hidden"
						value={displayDate}
					/>
				)}
			</Toolbar.Item>

			{showModal ? (
				<SchedulePublicationModal
					date={toMomentDate(displayDate || initialDisplayDate)}
					formId={formId!}
					hasWorkflow={hasWorkflow}
					onCloseModal={() => setShowModal(false)}
					onUpdateDate={setDisplayDate}
					type={type}
				/>
			) : null}

			{showPreviewModal ? (
				<PreviewModal
					getPreviewDataURL={getPreviewDataURL}
					languageId={localizationLanguageId}
					onCloseModal={() => {
						setShowPreviewModal(false);
						clearSessionStates();
					}}
					title={title}
				/>
			) : null}
		</Toolbar>
	);
}

function getSubmitTitle(title: string) {
	const isMac = Liferay.Browser?.isMac();

	return `
		<span class="d-block">
			${title}
		</span>
		<kbd class="c-kbd c-kbd-dark mt-1">
			<kbd class="c-kbd">${isMac ? '⌘' : 'Ctrl'}</kbd>
			<span class="c-kbd-separator"> + </span>
			<kbd class="c-kbd">${isMac ? '⌥' : 'Alt'}</kbd>
			<span class="c-kbd-separator"> + </span>
			<kbd class="c-kbd">${Liferay.Language.get('enter')}</kbd>
		</kbd>`
		.replaceAll('\n', '')
		.replaceAll('\t', '');
}

function clearSessionStates() {
	[
		PREVIEW_VISIBLE_SESSION_KEY,
		PREVIEW_CHANNEL_SESSION_KEY,
		PREVIEW_DISPLAY_PAGE_SESSION_KEY,
		PREVIEW_EXTERNAL_URL_SESSION_KEY,
		PREVIEW_CACHED_EXTERNAL_URL_SESSION_KEY,
		PREVIEW_WIDTH_SESSION_KEY,
	].forEach((key) => sessionStorage.removeItem(key));
}
