/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayLink from '@clayui/link';
import {AIAssistantTriggerButton} from '@liferay/ai-hub-cell-js-components-web';
import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {ApiHelper, Toolbar} from '@liferay/site-cms-site-initializer';
import {escapeHTML, navigate, sessionStorage, sub} from 'frontend-js-web';
import React, {useEffect, useId, useRef, useState} from 'react';

import {getFormattedLabel} from '../utils/getFormattedText';

export default function EditorToolbar({
	backURL,
	discardURL,
	formSubmitURL,
	groupId,
	hasUpdatePermission,
	isNew,
	parentCMPTask,
	title,
	viewURL,
}: {
	backURL: string;
	discardURL?: string;
	formSubmitURL?: string;
	groupId: number;
	hasUpdatePermission: boolean;
	isNew: boolean;
	parentCMPTask?: {
		isDone: boolean;
		saveAndAddAnotherURL?: string;
		title: string;
	};
	title: string;
	viewURL?: string;
}) {
	const [formId, setFormId] = useState<string | undefined>();
	const [showDoneAlert, setShowDoneAlert] = useState(
		isNew && parentCMPTask?.isDone
	);

	const discardingRef = useRef(false);
	const redirectInputRef = useRef<HTMLInputElement>(null);
	const submitLabelId = useId();
	const submitTitle = getSubmitTitle(
		sub(Liferay.Language.get('save-x'), escapeHTML(title))
	);

	function discardDraft(event: React.MouseEvent) {
		if (!discardURL) {
			return;
		}

		event.preventDefault();

		if (discardingRef.current) {
			return;
		}

		discardingRef.current = true;

		ApiHelper.delete(discardURL).finally(() => navigate(backURL));
	}

	function setSuccessMessage() {
		const form = getForm();

		if (!form || !form.checkValidity()) {
			return;
		}

		const {value} = form.querySelector(
			'[name^="ObjectField_title"]'
		) as HTMLInputElement;

		let successMessage = sub(
			isNew
				? Liferay.Language.get('x-was-created-successfully')
				: Liferay.Language.get('x-was-updated-successfully'),
			getFormattedLabel(value)
		);

		if (isNew && parentCMPTask) {
			successMessage = sub(
				Liferay.Language.get('the-subtask-x-was-successfully-created'),
				`<a href="${viewURL}">${escapeHTML(value)}</a>`
			);
		}

		sessionStorage.setItem(
			'com.liferay.content.marketing.platform.web.successMessage',
			successMessage,
			sessionStorage.TYPES.NECESSARY
		);
	}

	function getForm(): HTMLFormElement {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		return form as HTMLFormElement;
	}

	useEffect(() => {
		const form = getForm();

		if (form) {
			setFormId(form.id);

			const handlePublishShortcut = (event: KeyboardEvent) => {
				const isShortcut =
					event.key === 'Enter' &&
					event.altKey &&
					isCtrlOrMeta(event);

				if (
					event.key === 'Enter' &&
					(event.target as HTMLElement).tagName === 'INPUT' &&
					!isShortcut
				) {
					event.preventDefault();
				}

				if (isShortcut && hasUpdatePermission) {
					(form as HTMLFormElement).submit();
				}
			};

			window.addEventListener('keydown', handlePublishShortcut);

			return () =>
				window.removeEventListener('keydown', handlePublishShortcut);
		}
	}, [hasUpdatePermission]);

	return (
		<>
			<Toolbar
				backURL={backURL}
				className="content-editor__toolbar position-fixed"
				onBackClick={discardDraft}
				title={title}
			>
				{Liferay.FeatureFlags['LPD-62272'] && (
					<Toolbar.Item className="nav-divider-end">
						<AIAssistantTriggerButton
							context={{groupId}}
							hideLabel
							instructionDefinitionScope="cms"
							presentation="dropdown"
							round
						/>
					</Toolbar.Item>
				)}

				<Toolbar.Item>
					<ClayLink
						aria-label={Liferay.Language.get('cancel')}
						borderless
						button
						displayType="secondary"
						href={backURL}
						onClick={discardDraft}
						small
					>
						{Liferay.Language.get('cancel')}
					</ClayLink>
				</Toolbar.Item>

				{parentCMPTask?.saveAndAddAnotherURL && (
					<Toolbar.Item>
						<ClayButton
							disabled={!hasUpdatePermission}
							displayType="secondary"
							form={formId}
							onClick={() => {
								if (
									getForm()?.checkValidity() &&
									redirectInputRef.current
								) {
									redirectInputRef.current.value =
										parentCMPTask.saveAndAddAnotherURL!;
								}

								setSuccessMessage();
							}}
							size="sm"
							type="submit"
						>
							{Liferay.Language.get('save-and-add-another')}
						</ClayButton>
					</Toolbar.Item>
				)}

				<Toolbar.Item>
					<ClayButton
						aria-labelledby={submitLabelId}
						data-title={submitTitle}
						data-title-set-as-html
						disabled={!hasUpdatePermission}
						form={formId}
						onClick={() => setSuccessMessage()}
						size="sm"
						type="submit"
					>
						{Liferay.Language.get('save')}
					</ClayButton>

					<span
						className="sr-only"
						dangerouslySetInnerHTML={{__html: submitTitle}}
						id={submitLabelId}
					/>

					<ClayInput
						defaultValue={formSubmitURL || backURL}
						form={formId}
						name="redirect"
						ref={redirectInputRef}
						type="hidden"
					/>
				</Toolbar.Item>
			</Toolbar>

			{showDoneAlert && parentCMPTask && (
				<ClayAlert
					containerClassName="mw-100"
					displayType="info"
					onClose={() => setShowDoneAlert(false)}
					title={`${Liferay.Language.get('info')}:`}
					variant="stripe"
				>
					{sub(
						Liferay.Language.get(
							'the-parent-task-x-is-done-adding-this-subtask-will-not-change-its-status'
						),
						parentCMPTask.title
					)}
				</ClayAlert>
			)}
		</>
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
