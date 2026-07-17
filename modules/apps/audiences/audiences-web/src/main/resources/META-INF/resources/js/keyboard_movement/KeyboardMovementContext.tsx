/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScreenReaderAnnouncer} from '@liferay/layout-js-components-web';
import React, {useCallback, useContext, useRef, useState} from 'react';

import {DropPosition} from '../constants/dropPositions';
import {AudiencesCriteria} from '../types';

export interface MovementSource {
	audiencesCriteria?: AudiencesCriteria;
	icon: string;
	name: string;
	ruleId?: string;
}

export interface MovementTarget {
	nodeId: string | null;
	position: DropPosition | null;
}

interface KeyboardMovementContextValue {
	setSource: (source: MovementSource | null) => void;
	setTarget: (target: MovementTarget) => void;
	setText: (text: string) => void;
	source: MovementSource | null;
	target: MovementTarget;
}

const INITIAL_TARGET: MovementTarget = {
	nodeId: null,
	position: null,
};

const KeyboardMovementContext =
	React.createContext<KeyboardMovementContextValue>({
		setSource: () => {},
		setTarget: () => {},
		setText: () => {},
		source: null,
		target: INITIAL_TARGET,
	});

export function KeyboardMovementContextProvider({
	children,
}: {
	children: React.ReactNode;
}) {
	const [source, setSource] = useState<MovementSource | null>(null);
	const [target, setTarget] = useState<MovementTarget>(INITIAL_TARGET);

	const screenReaderAnnouncerRef = useRef<{
		sendMessage: (message: string) => void;
	}>(null);

	const setText = useCallback((text: string) => {
		screenReaderAnnouncerRef.current?.sendMessage(text);
	}, []);

	return (
		<KeyboardMovementContext.Provider
			value={{setSource, setTarget, setText, source, target}}
		>
			<ScreenReaderAnnouncer
				aria-live="assertive"
				ref={screenReaderAnnouncerRef}
			/>

			{children}
		</KeyboardMovementContext.Provider>
	);
}

export function useDisableKeyboardMovement() {
	const {setSource, setTarget} = useContext(KeyboardMovementContext);

	return useCallback(() => {
		setSource(null);

		setTarget(INITIAL_TARGET);
	}, [setSource, setTarget]);
}

export function useMovementSource() {
	return useContext(KeyboardMovementContext).source;
}

export function useMovementTarget() {
	return useContext(KeyboardMovementContext).target;
}

export function useSetMovementSource() {
	return useContext(KeyboardMovementContext).setSource;
}

export function useSetMovementTarget() {
	return useContext(KeyboardMovementContext).setTarget;
}

export function useSetMovementText() {
	return useContext(KeyboardMovementContext).setText;
}
