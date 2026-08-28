/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import AIAssistantHost from './AIAssistantHost';
import {ChatContext} from './api';

export type AIAssistantPresentation = 'dropdown' | 'sidebar';

export interface AIAssistantOpenCommand {
	anchorId?: string;
	chatbotExternalReferenceCode?: string;
	context?: ChatContext;
	enableFreeFormCategorization?: boolean;
	getContext?: () => ChatContext;
	initialMessage?: string;
	instructionDefinitionScope: string;
	presentation?: AIAssistantPresentation;
	pushContainer?: string;
	quickActions?: string[];
	sidebarBehavior?: 'overlay' | 'push';
	triggerId: string;
}

interface AIAssistantState {
	command: AIAssistantOpenCommand | null;
}

type Listener = (state: AIAssistantState) => void;

interface AIAssistantSingleton {
	eventBound: boolean;
	hostMounted: boolean;
	listeners: Set<Listener>;
	state: AIAssistantState;
}

const HOST_CONTAINER_ID = 'ai-assistant-host-root';

const GLOBAL_KEY = '__LIFERAY_AI_ASSISTANT_SINGLETON__';

// The singleton state lives on a global, not in module scope, so that every
// copy of this module shares it. A page loads the module more than once -- the
// main bundle for imported triggers and the renderAIAssistantTrigger submodule
// chunk for fragment triggers -- and each copy would otherwise have its own
// state, host, and listeners. The global collapses them into one.

function getSingleton(): AIAssistantSingleton {
	const globalScope = window as unknown as Record<
		string,
		AIAssistantSingleton | undefined
	>;

	let singleton = globalScope[GLOBAL_KEY];

	if (!singleton) {
		singleton = {
			eventBound: false,
			hostMounted: false,
			listeners: new Set(),
			state: {command: null},
		};

		globalScope[GLOBAL_KEY] = singleton;
	}

	return singleton;
}

function emit(): void {
	const {listeners, state} = getSingleton();

	listeners.forEach((listener) => listener(state));
}

export function ensureHost(): void {
	const singleton = getSingleton();

	let container = document.getElementById(HOST_CONTAINER_ID);

	if (container && singleton.hostMounted) {
		return;
	}

	if (!container) {
		container = document.createElement('div');

		container.id = HOST_CONTAINER_ID;

		document.body.appendChild(container);
	}

	singleton.hostMounted = true;

	render(AIAssistantHost, {}, container);
}

export function releaseHost(): void {
	const singleton = getSingleton();

	singleton.hostMounted = false;
	singleton.state = {command: null};
}

export function close(): void {
	const singleton = getSingleton();

	if (!singleton.state.command) {
		return;
	}

	singleton.state = {command: null};

	emit();
}

export function getState(): AIAssistantState {
	return getSingleton().state;
}

export function open(command: AIAssistantOpenCommand): void {
	const singleton = getSingleton();

	singleton.state = {command};

	ensureHost();

	emit();
}

export function subscribe(listener: Listener): () => void {
	const {listeners} = getSingleton();

	listeners.add(listener);

	return () => {
		listeners.delete(listener);
	};
}

const singleton = getSingleton();

if (!singleton.eventBound) {
	singleton.eventBound = true;

	Liferay.on('aiAssistant:command', (command: AIAssistantOpenCommand) => {
		open(command);
	});
}

export default {close, ensureHost, getState, open, subscribe};
