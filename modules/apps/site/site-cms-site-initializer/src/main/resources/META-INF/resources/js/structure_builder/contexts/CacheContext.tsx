/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	ReactNode,
	RefObject,
	createContext,
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import PicklistService from '../../services/PicklistService';
import SpaceService from '../../services/SpaceService';
import {Picklist} from '../../types/Picklist';
import {Space} from '../../types/Space';
import StructureService from '../services/StructureService';
import {Structures} from '../types/Structure';

export type CacheKey = 'picklists' | 'spaces' | 'structures';
export type CacheStatus = 'idle' | 'saving' | 'saved' | 'stale';

export type Cache = {
	picklists: {
		data: Picklist[];
		fetcher: () => Promise<Picklist[]>;
		status: CacheStatus;
	};
	spaces: {
		data: Space[];
		fetcher: () => Promise<Space[]>;
		status: CacheStatus;
	};
	structures: {
		data: Structures;
		fetcher: () => Promise<Structures>;
		status: CacheStatus;
	};
};

const INITIAL_CACHE: Cache = {
	picklists: {
		data: [],
		fetcher: PicklistService.getPicklists,
		status: 'idle',
	},
	spaces: {
		data: [],
		fetcher: SpaceService.getSpaces,
		status: 'idle',
	},
	structures: {
		data: new Map(),
		fetcher: StructureService.getStructures,
		status: 'idle',
	},
};

const CacheContext = createContext<{
	broadcastRef: RefObject<BroadcastChannel>;
	cache: Cache;
	update: <T extends CacheKey>(key: T, partial: Partial<Cache[T]>) => void;
}>({
	broadcastRef: {current: null},
	cache: INITIAL_CACHE,
	update: () => {},
});

function CacheContextProvider({children}: {children: ReactNode}) {
	const broadcastRef = useRef(new BroadcastChannel('update-cache'));
	const [cache, setCache] = useState(INITIAL_CACHE);

	const update = <T extends CacheKey>(key: T, partial: Partial<Cache[T]>) => {
		setCache((current) => ({
			...current,
			[key]: {
				...current[key],
				...partial,
			},
		}));
	};

	return (
		<CacheContext.Provider value={{broadcastRef, cache, update}}>
			{children}
		</CacheContext.Provider>
	);
}

function useCache<T extends CacheKey>(
	key: T
): Cache[T] & {load: () => Promise<void>} {
	const {broadcastRef, cache, update} = useContext(CacheContext);

	const item = cache[key];

	const load = useCallback(async () => {
		update(key, {status: 'saving'} as Partial<Cache[T]>);

		try {
			const response = await item.fetcher();

			update(key, {data: response, status: 'saved'} as Partial<Cache[T]>);
		}
		catch {
			update(key, {status: 'stale'} as Partial<Cache[T]>);
		}
	}, [item, key, update]);

	useEffect(() => {
		if (item.status !== 'idle') {
			return;
		}

		load();
	}, [item, load]);

	useEffect(() => {
		const broadcast = broadcastRef.current;

		const staleCache = ({data}: MessageEvent) => {
			if (data.type !== 'staleCache' || data.key !== key) {
				return;
			}

			update(key, {status: 'stale'} as Partial<Cache[T]>);
		};

		broadcast?.addEventListener('message', staleCache);

		return () => {
			broadcast?.removeEventListener('message', staleCache);
		};
	}, [broadcastRef, item, update, key]);

	return {...item, load};
}

function useStaleCache() {
	const {broadcastRef} = useContext(CacheContext);

	return (key: CacheKey) => {
		broadcastRef.current?.postMessage({key, type: 'staleCache'});
	};
}
export default CacheContextProvider;

export {CacheContext, useCache, useStaleCache};
