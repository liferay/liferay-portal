/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EventSource} from 'eventsource';
import {useCallback, useEffect, useRef, useState} from 'react';

import {
	createCategorizationEventSource,
	postCategorizationAgentInstance,
} from './api';
import {
	CategorizationContext,
	CategorizationStatus,
	ECategorizationAgent,
	Suggestion,
} from './types';
import {parseSuggestions} from './utils/parseSuggestions';

const DEFAULT_COUNT = 3;

function toRequestContext(
	agent: ECategorizationAgent,
	context: CategorizationContext
): Record<string, unknown> {
	const requestContext: Record<string, unknown> = {
		content: context.content,
		count: context.count ?? DEFAULT_COUNT,
	};

	if (agent === ECategorizationAgent.AUTO_CATEGORIZE) {
		requestContext.candidateCategories = JSON.stringify(
			context.candidateCategories ?? []
		);
	}
	else {
		requestContext.existingTags = JSON.stringify(
			context.existingTags ?? []
		);
	}

	return requestContext;
}

function resolveTargetSuggestions(
	agent: ECategorizationAgent,
	context: CategorizationContext,
	targets: string[]
): Suggestion[] {
	const suggestions: Suggestion[] = [];

	if (agent === ECategorizationAgent.AUTO_CATEGORIZE) {
		const candidateCategories = context.candidateCategories ?? [];
		const seen = new Set<number>();

		targets.forEach((target) => {
			const name = target.trim().toLowerCase();

			const candidateCategory = candidateCategories.find(
				(candidate) => candidate.name.trim().toLowerCase() === name
			);

			if (candidateCategory && !seen.has(candidateCategory.id)) {
				seen.add(candidateCategory.id);

				suggestions.push({
					id: candidateCategory.id,
					name: candidateCategory.name,
				});
			}
		});

		return suggestions;
	}

	const existingTagsByLowerCase = new Map<string, string>();

	(context.existingTags ?? []).forEach((existingTag) =>
		existingTagsByLowerCase.set(existingTag.toLowerCase(), existingTag)
	);

	const seen = new Set<string>();

	targets.forEach((target) => {
		const name = target.trim();
		const lowerCaseName = name.toLowerCase();

		if (!name || seen.has(lowerCaseName)) {
			return;
		}

		seen.add(lowerCaseName);

		const existingTag = existingTagsByLowerCase.get(lowerCaseName);

		suggestions.push({
			isNew: existingTag === undefined,
			name: existingTag ?? name,
		});
	});

	return suggestions;
}

export default function useCategorizationAgent(agent: ECategorizationAgent) {
	const [error, setError] = useState<string>();
	const [status, setStatus] = useState<CategorizationStatus>('idle');
	const [suggestions, setSuggestions] = useState<Suggestion[]>([]);

	const connectingRef = useRef<boolean>(false);
	const eventSourceRef = useRef<EventSource | null>(null);
	const lastContextRef = useRef<CategorizationContext | null>(null);
	const lastTargetsRef = useRef<string[] | null>(null);
	const mountedRef = useRef<boolean>(true);
	const pendingRef = useRef<boolean>(false);
	const sseEventSinkKeyRef = useRef<string | null>(null);
	const stoppedRef = useRef<boolean>(false);

	const closeEventSource = useCallback(() => {
		eventSourceRef.current?.close();
		eventSourceRef.current = null;
		sseEventSinkKeyRef.current = null;
	}, []);

	const invoke = useCallback(
		async (context: CategorizationContext) => {
			try {
				await postCategorizationAgentInstance({
					agent,
					context: toRequestContext(agent, context),
					sseEventSinkKey: sseEventSinkKeyRef.current as string,
				});
			}
			catch {
				if (stoppedRef.current) {
					return;
				}

				setError(Liferay.Language.get('an-unexpected-error-occurred'));
				setStatus('error');

				closeEventSource();
			}
		},
		[agent, closeEventSource]
	);

	const connect = useCallback(() => {
		if (eventSourceRef.current || connectingRef.current) {
			return;
		}

		connectingRef.current = true;

		createCategorizationEventSource()
			.then((eventSource) => {
				connectingRef.current = false;

				if (!mountedRef.current || stoppedRef.current) {
					eventSource?.close();

					return;
				}

				if (!eventSource) {
					pendingRef.current = false;

					setError(
						Liferay.Language.get('an-unexpected-error-occurred')
					);
					setStatus('error');

					return;
				}

				eventSourceRef.current = eventSource;

				eventSource.addEventListener('Subscribe', (event) => {
					sseEventSinkKeyRef.current = event.data;

					if (pendingRef.current && lastContextRef.current) {
						pendingRef.current = false;

						invoke(lastContextRef.current);
					}
				});

				eventSource.addEventListener(agent, (event) => {
					try {
						const dataJSON = JSON.parse(event.data);

						const parsed = parseSuggestions(
							agent,
							dataJSON.data ?? '',
							lastContextRef.current ?? {content: ''}
						);

						setSuggestions(parsed);
						setStatus(parsed.length ? 'ready' : 'empty');
					}
					catch {
						setError(
							Liferay.Language.get('an-unexpected-error-occurred')
						);
						setStatus('error');
					}

					closeEventSource();
				});

				eventSource.addEventListener(
					'Agent Invocation Failed',
					(event) => {
						let text = '';

						try {
							text = JSON.parse(event.data).data;
						}
						catch {
							text = '';
						}

						setError(
							text ||
								Liferay.Language.get(
									'an-unexpected-error-occurred'
								)
						);
						setStatus('error');

						closeEventSource();
					}
				);

				eventSource.addEventListener('error', () => {
					if (stoppedRef.current) {
						return;
					}

					setError(
						Liferay.Language.get('an-unexpected-error-occurred')
					);
					setStatus('error');

					closeEventSource();
				});
			})
			.catch(() => {
				connectingRef.current = false;
				pendingRef.current = false;

				if (!mountedRef.current) {
					return;
				}

				setError(Liferay.Language.get('an-unexpected-error-occurred'));
				setStatus('error');
			});
	}, [agent, closeEventSource, invoke]);

	const run = useCallback(
		(context: CategorizationContext) => {
			if (stoppedRef.current) {
				return;
			}

			lastContextRef.current = context;
			lastTargetsRef.current = null;

			setError(undefined);
			setSuggestions([]);
			setStatus('loading');

			if (sseEventSinkKeyRef.current) {
				invoke(context);
			}
			else {
				pendingRef.current = true;

				connect();
			}
		},
		[connect, invoke]
	);

	const resolveTargets = useCallback(
		(context: CategorizationContext, targets: string[]) => {
			if (stoppedRef.current) {
				return;
			}

			lastContextRef.current = context;
			lastTargetsRef.current = targets;

			setError(undefined);

			const resolved = resolveTargetSuggestions(agent, context, targets);

			setSuggestions(resolved);
			setStatus(resolved.length ? 'ready' : 'empty');
		},
		[agent]
	);

	const regenerate = useCallback(() => {
		if (!lastContextRef.current) {
			return;
		}

		if (lastTargetsRef.current) {
			resolveTargets(lastContextRef.current, lastTargetsRef.current);
		}
		else {
			run(lastContextRef.current);
		}
	}, [resolveTargets, run]);

	const stop = useCallback(() => {
		stoppedRef.current = true;

		closeEventSource();

		setStatus('stopped');
	}, [closeEventSource]);

	const reset = useCallback(() => {
		stoppedRef.current = false;

		setError(undefined);
		setSuggestions([]);
		setStatus('idle');
	}, []);

	useEffect(() => {
		mountedRef.current = true;

		return () => {
			mountedRef.current = false;
		};
	}, []);

	useEffect(() => {
		return () => {
			pendingRef.current = false;

			closeEventSource();
		};
	}, [agent, closeEventSource]);

	return {
		error,
		regenerate,
		reset,
		resolveTargets,
		run,
		status,
		stop,
		suggestions,
	};
}
