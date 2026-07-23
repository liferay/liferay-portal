/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';

import CategorizationSuggestionService from '../../common/services/CategorizationSuggestionService';
import {
	AUTO_CATEGORIZE_AGENT,
	CATEGORIZE_EVENT,
	COMMIT_EVENT,
	CategorizationAction,
	CategorizationCommitPayload,
	CategorizeEventPayload,
	GENERATE_TAGS_AGENT,
	REQUEST_CATEGORIZE_EVENT,
	RequestCategorizePayload,
} from '../../main_view/info_panel/components/categorizationAgentEvents';
import ObjectEntryService from '../../main_view/info_panel/services/ObjectEntryService';
import getEditedContent from '../utils/getEditedContent';
import {
	CategorizationFields,
	UpdateCategorizationProps,
} from './ContentEditorSidePanel';

export default function useAssistantCategorization({
	assetLibraryId,
	categorizationFields,
	cmsGroupId,
	contentAPIURL,
	onUpdateCategorization,
	panel,
}: {
	assetLibraryId: string;
	categorizationFields: CategorizationFields | null;
	cmsGroupId: string;
	contentAPIURL: string;
	onUpdateCategorization: (props: UpdateCategorizationProps) => void;
	panel: React.Key | null;
}) {
	const categorizationFieldsRef = useRef(categorizationFields);
	const panelRef = useRef(panel);

	useEffect(() => {
		categorizationFieldsRef.current = categorizationFields;
	}, [categorizationFields]);

	useEffect(() => {
		panelRef.current = panel;
	}, [panel]);

	useEffect(() => {
		const dispatchCategorizeEvents = async (
			actions: CategorizationAction[]
		) => {
			const {data, error} =
				await ObjectEntryService.getObjectEntry(contentAPIURL);

			if (!data) {
				if (error) {
					console.error(error);
				}

				return;
			}

			const editedContent = await getEditedContent(
				data.systemProperties?.objectDefinitionBrief
					?.externalReferenceCode
			);

			actions.forEach((action) => {
				const agent =
					action.agent === 'categorize'
						? AUTO_CATEGORIZE_AGENT
						: GENERATE_TAGS_AGENT;

				const payload: CategorizeEventPayload = {
					agent,
					cmsGroupId,
					content: editedContent || data.contentRawText || '',
					count: action.count,
					scopeId:
						agent === AUTO_CATEGORIZE_AGENT
							? data.scopeId
							: data.scopeId || assetLibraryId || cmsGroupId,
					suppressUserMessage: true,
					targets: action.targets,
				};

				if (agent === AUTO_CATEGORIZE_AGENT) {
					payload.classNameId =
						data.systemProperties?.objectDefinitionBrief
							?.classNameId ?? -1;
					payload.currentCategoryIds = (
						data.taxonomyCategoryBriefs || []
					).map((brief) => brief.taxonomyCategoryId);
				}
				else {
					payload.currentTagNames = data.keywords || [];
				}

				Liferay.fire(CATEGORIZE_EVENT, payload);
			});
		};

		const handleRequestCategorize = (payload: RequestCategorizePayload) => {
			dispatchCategorizeEvents(payload.actions);
		};

		const applyCommittedSuggestions = async ({
			agent,
			scopeId,
			suggestions,
		}: CategorizationCommitPayload) => {
			const fields = categorizationFieldsRef.current;

			if (panelRef.current === 'categorization' || !fields) {
				return;
			}

			if (agent === AUTO_CATEGORIZE_AGENT) {
				const currentBriefs = fields.assetCategoryIds.value;

				const briefs =
					await CategorizationSuggestionService.resolveNewCategoryBriefs(
						suggestions,
						currentBriefs.map(
							({taxonomyCategoryId}) => taxonomyCategoryId
						)
					);

				if (!briefs.length) {
					return;
				}

				const value = [...currentBriefs, ...briefs];

				onUpdateCategorization([
					'assetCategoryIds',
					{
						serverValue: value
							.map(({taxonomyCategoryId}) => taxonomyCategoryId)
							.join(','),
						value,
					},
				]);

				openToast({
					message: sub(
						Liferay.Language.get(
							'x-categories-have-been-successfully-added-to-the-selected-content'
						),
						`${briefs.length}`
					),
					type: 'success',
				});
			}
			else if (agent === GENERATE_TAGS_AGENT) {
				const names =
					await CategorizationSuggestionService.createTagNames(
						suggestions,
						{
							assetLibraryId:
								scopeId || assetLibraryId || cmsGroupId,
							cmsGroupId,
						}
					);

				const currentNames = fields.assetTagNames.value;

				const newNames = [
					...new Set(
						names.filter((name) => !currentNames.includes(name))
					),
				];

				if (!newNames.length) {
					return;
				}

				const keywords = [...currentNames, ...newNames];

				onUpdateCategorization([
					'assetTagNames',
					{serverValue: keywords.join(','), value: keywords},
				]);

				openToast({
					message: sub(
						Liferay.Language.get(
							'x-tags-have-been-successfully-added-to-the-selected-content'
						),
						`${newNames.length}`
					),
					type: 'success',
				});
			}
		};

		Liferay.on(COMMIT_EVENT, applyCommittedSuggestions);
		Liferay.on(REQUEST_CATEGORIZE_EVENT, handleRequestCategorize);

		return () => {
			Liferay.detach(COMMIT_EVENT, applyCommittedSuggestions);
			Liferay.detach(REQUEST_CATEGORIZE_EVENT, handleRequestCategorize);
		};
	}, [assetLibraryId, cmsGroupId, contentAPIURL, onUpdateCategorization]);
}
