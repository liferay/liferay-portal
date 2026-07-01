/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import StepLayout from './components/StepLayout';
import {
	createGeneration,
	getGeneration,
	getGenerationItems,
} from './services/generations';
import IdeateStep from './steps/IdeateStep';
import RefineStep from './steps/RefineStep';

import type {Generation} from './types/Generation';
import type {GenerationItem} from './types/GenerationItem';

const MAX_POLL_ATTEMPTS = 100;

const POLL_INTERVAL = 3000;

const STEP_IDEATE = 0;
const STEP_REFINE = 1;
const STEP_REVIEW = 2;

interface IProps {
	apiURL: string;
	generationId?: number;
	generationsURL: string;
}

export default function ContentSiteGeneratorWizard({
	apiURL,
	generationId,
	generationsURL,
}: IProps) {
	const [activeStep, setActiveStep] = useState(STEP_IDEATE);
	const [error, setError] = useState<string>();
	const [generation, setGeneration] = useState<Generation>();
	const [items, setItems] = useState<GenerationItem[]>([]);
	const [loading, setLoading] = useState(!!generationId);

	const pollAttemptsRef = useRef(0);

	const handleCancel = useCallback(() => {
		Liferay.Util.navigate(generationsURL);
	}, [generationsURL]);

	const refresh = useCallback(
		async (currentGenerationId: number) => {
			const [newGeneration, newItems] = await Promise.all([
				getGeneration(apiURL, currentGenerationId),
				getGenerationItems(apiURL, currentGenerationId),
			]);

			setGeneration(newGeneration);
			setItems(newItems);

			return newGeneration;
		},
		[apiURL]
	);

	useEffect(() => {
		if (!generationId) {
			return;
		}

		refresh(generationId)
			.then((newGeneration) => {
				const statusKey = newGeneration.generationStatus.key;

				setActiveStep(
					statusKey === 'committed' || statusKey === 'ready'
						? STEP_REVIEW
						: STEP_REFINE
				);
			})
			.catch((newError: Error) => setError(newError.message))
			.finally(() => setLoading(false));
	}, [generationId, refresh]);

	const activeGenerationId = generation?.id;
	const generationStatusKey = generation?.generationStatus.key;
	const polling =
		generationStatusKey === 'generating' ||
		generationStatusKey === 'refining';

	useEffect(() => {
		if (!polling || activeGenerationId === undefined) {
			pollAttemptsRef.current = 0;

			return;
		}

		let cancelled = false;
		let timeoutId: ReturnType<typeof setTimeout>;

		const poll = async () => {
			pollAttemptsRef.current += 1;

			try {
				await refresh(activeGenerationId);
			}
			catch (pollError) {
				if (!cancelled) {
					setError(
						pollError instanceof Error
							? pollError.message
							: String(pollError)
					);
				}

				return;
			}

			if (cancelled) {
				return;
			}

			if (pollAttemptsRef.current >= MAX_POLL_ATTEMPTS) {
				setError(
					Liferay.Language.get(
						'it-looks-like-this-is-taking-longer-than-expected'
					)
				);

				return;
			}

			timeoutId = setTimeout(poll, POLL_INTERVAL);
		};

		timeoutId = setTimeout(poll, POLL_INTERVAL);

		return () => {
			cancelled = true;

			clearTimeout(timeoutId);
		};
	}, [activeGenerationId, polling, refresh]);

	const handleAnalyze = async (prompt: string) => {
		setError(undefined);
		setLoading(true);

		try {
			const newGeneration = await createGeneration(apiURL, {
				prompt,
				title: prompt.split('\n')[0].slice(0, 75),
			});

			setGeneration(newGeneration);
			setItems([]);
			setActiveStep(STEP_REFINE);
		}
		catch (newError) {
			setError(
				newError instanceof Error ? newError.message : String(newError)
			);
		}
		finally {
			setLoading(false);
		}
	};

	if (loading && !generation) {
		return <ClayLoadingIndicator displayType="secondary" size="md" />;
	}

	return (
		<div className="content-site-generator">
			{activeStep === STEP_IDEATE && (
				<StepLayout activeStep={STEP_IDEATE}>
					<IdeateStep
						error={error}
						loading={loading}
						onAnalyze={handleAnalyze}
					/>
				</StepLayout>
			)}

			{activeStep === STEP_REFINE && generation && (
				<StepLayout activeStep={STEP_REFINE}>
					<RefineStep
						generation={generation}
						items={items}
						onBack={() => setActiveStep(STEP_IDEATE)}
						onCancel={handleCancel}
						onContinue={() => setActiveStep(STEP_REVIEW)}
					/>
				</StepLayout>
			)}
		</div>
	);
}
