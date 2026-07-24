/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Dispatch, SetStateAction, useEffect, useRef, useState} from 'react';

import {putAgentInstanceResume} from './api';
import {TranslateContentMessageBalloonProps} from './types';
import {getPageContext} from './utils/getPageContext';
import {getTranslatedLanguageIds} from './utils/getTranslatedLanguageIds';

export default function useTranslateContentAgent({
	agentInstanceId,
	availableLanguageIds,
	requestedLanguageIds,
	results,
	setIsGenerating,
	sourceLanguageIdRef,
}: TranslateContentMessageBalloonProps) {
	const appliedRef = useRef<boolean>(false);

	const [overwriteLanguageIds, setOverwriteLanguageIds] = useState<string[]>(
		[]
	);
	const [selectedLanguageIds, setSelectedLanguageIds] = useState<string[]>(
		() =>
			(requestedLanguageIds ?? []).filter((languageId) =>
				(availableLanguageIds ?? []).includes(languageId)
			)
	);
	const [step, setStep] = useState<'confirm' | 'review' | 'select'>('select');
	const [submitted, setSubmitted] = useState<boolean>(false);
	const [translatedLanguageIds, setTranslatedLanguageIds] = useState<
		string[]
	>([]);
	const [value, setValue] = useState<string>('');

	const submit = (targetLanguageIds: string[]) => {
		setIsGenerating(true);
		setSubmitted(true);

		const sourceLanguageId = sourceLanguageIdRef.current;

		const {fields, html} = getPageContext(sourceLanguageId);

		putAgentInstanceResume({
			agentInstanceId,
			context: {
				fields: JSON.stringify(fields),
				html: JSON.stringify(html),
				sourceLanguageId,
				targetLanguageIds: JSON.stringify(targetLanguageIds),
			},
		}).catch(() => setIsGenerating(false));
	};

	const toggleLanguageId = (
		setLanguageIds: Dispatch<SetStateAction<string[]>>,
		languageId: string
	) => {
		setLanguageIds((previousLanguageIds) =>
			previousLanguageIds.includes(languageId)
				? previousLanguageIds.filter(
						(previousLanguageId) =>
							previousLanguageId !== languageId
					)
				: [...previousLanguageIds, languageId]
		);
	};

	const onTranslate = () => {
		const translatedLanguageIds =
			getTranslatedLanguageIds(selectedLanguageIds);

		if (!translatedLanguageIds.length) {
			submit(selectedLanguageIds);

			return;
		}

		setStep('confirm');
		setTranslatedLanguageIds(translatedLanguageIds);
	};

	const review = () => {
		setOverwriteLanguageIds(translatedLanguageIds);
		setStep('review');
	};

	const overwriteAll = () => submit(selectedLanguageIds);

	const overwriteTargetLanguageIds = selectedLanguageIds.filter(
		(languageId) =>
			!translatedLanguageIds.includes(languageId) ||
			overwriteLanguageIds.includes(languageId)
	);

	const overwrite = () => submit(overwriteTargetLanguageIds);

	useEffect(() => {
		if (appliedRef.current || !results?.length) {
			return;
		}

		appliedRef.current = true;

		for (const result of results) {
			const fields: Record<string, string> = {};

			for (const [name, value] of Object.entries(result.fields ?? {})) {
				fields[name] = Liferay.Util.unescapeHTML(value);
			}

			if (!Object.keys(fields).length) {
				continue;
			}

			Liferay.fire('localizationSelect:autoTranslate', {
				fields,
				languageId: result.targetLanguageId,
			});
		}
	}, [results]);

	return {
		confirmDisabled: submitted || step === 'review',
		onTranslate,
		overwrite,
		overwriteAll,
		overwriteDisabled: submitted || !overwriteTargetLanguageIds.length,
		overwriteLanguageIds,
		review,
		selectDisabled: submitted || step !== 'select',
		selectedLanguageIds,
		setSelectedLanguageIds,
		setValue,
		showConfirm: step === 'confirm' || step === 'review',
		showReview: step === 'review',
		submitted,
		toggleOverwriteLanguageId: (languageId: string) =>
			toggleLanguageId(setOverwriteLanguageIds, languageId),
		toggleSelectedLanguageId: (languageId: string) =>
			toggleLanguageId(setSelectedLanguageIds, languageId),
		translatedLanguageIds,
		value,
	};
}
