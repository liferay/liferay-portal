/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	MutableRefObject,
	ReactNode,
	createContext,
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import PicklistService from '../../common/services/PicklistService';
import SpaceService from '../../common/services/SpaceService';
import {getWorkflowDefinitions} from '../../common/services/WorkflowService';
import {Picklist} from '../../common/types/Picklist';
import {Space} from '../../common/types/Space';
import {Workflow} from '../../common/types/Workflow';
import ObjectDefinitionService from '../services/ObjectDefinitionService';
import {ObjectDefinitions} from '../types/ObjectDefinition';

export type CacheKey =
	| 'object-definitions'
	| 'picklists'
	| 'spaces'
	| 'workflows';

export type CacheStatus = 'idle' | 'saving' | 'saved' | 'stale';

export type Cache = {
	'object-definitions': {
		data: ObjectDefinitions;
		fetcher: () => Promise<ObjectDefinitions>;
		status: CacheStatus;
	};
	'picklists': {
		data: Picklist[];
		fetcher: () => Promise<Picklist[]>;
		status: CacheStatus;
	};
	'spaces': {
		data: Space[];
		fetcher: () => Promise<Space[]>;
		status: CacheStatus;
	};
	'workflows': {
		data: Workflow[];
		fetcher: () => Promise<Workflow[]>;
		status: CacheStatus;
	};
};

type Promises = {
	[K in CacheKey]: Promise<Cache[K]['data']>;
};

type InitialData = Partial<{
	[K in CacheKey]: Cache[K]['data'];
}>;

function getInitialCache(initialData: InitialData = {}): Cache {
	return {
		'object-definitions': {
			data: initialData['object-definitions'] ?? {},
			fetcher: ObjectDefinitionService.getObjectDefinitions,
			status: initialData['object-definitions'] ? 'saved' : 'idle',
		},
		'picklists': {
			data: initialData['picklists'] ?? [],
			fetcher: PicklistService.getPicklists,
			status: initialData['picklists'] ? 'saved' : 'idle',
		},
		'spaces': {
			data: initialData['spaces'] ?? [],
			fetcher: SpaceService.getSpaces,
			status: initialData['spaces'] ? 'saved' : 'idle',
		},
		'workflows': {
			data: initialData['workflows'] ?? [],
			fetcher: getWorkflowDefinitions,
			status: initialData['workflows'] ? 'saved' : 'idle',
		},
	};
}

const CacheContext = createContext<{
	cache: Cache;
	promisesRef: MutableRefObject<Partial<Promises>>;
	update: <T extends CacheKey>(key: T, partial: Partial<Cache[T]>) => void;
}>({
	cache: getInitialCache(),
	promisesRef: {current: {}},
	update: () => {},
});

function CacheContextProvider({
	children,
	initialData,
}: {
	children: ReactNode;
	initialData: InitialData;
}) {
	const promisesRef = useRef<Partial<Promises>>({});

	const [cache, setCache] = useState(getInitialCache(initialData));

	const update = <T extends CacheKey>(key: T, partial: Partial<Cache[T]>) => {
		setCache((current) => ({
			...current,
			[key]: {
				...current[key],
				...partial,
			},
		}));
	};

	useEffect(() => {
		const broadcast = new BroadcastChannel('update-cache');

		return () => {
			broadcast.close();
		};
	}, []);

	return (
		<CacheContext.Provider value={{cache, promisesRef, update}}>
			{children}
		</CacheContext.Provider>
	);
}

function useCache<T extends CacheKey>(
	key: T
): Cache[T] & {load: () => Promise<Cache[T]['data']>} {
	const {cache, promisesRef, update} = useContext(CacheContext);

	const item = cache[key];

	const promises = promisesRef.current;

	const load = useCallback(async () => {
		const existingPromise = promises[key] as Promise<Cache[T]['data']>;

		if (existingPromise) {
			await existingPromise;

			return cache[key].data;
		}

		const fetchData = async (): Promise<Cache[T]['data']> => {
			update(key, {status: 'saving'} as Partial<Cache[T]>);

			try {
				const response = await item.fetcher();

				update(key, {data: response, status: 'saved'} as Partial<
					Cache[T]
				>);

				return response;
			}
			catch (error) {
				update(key, {status: 'stale'} as Partial<Cache[T]>);

				return Promise.reject(error);
			}
			finally {
				delete promises[key];
			}
		};

		const promise = fetchData();

		promises[key] = promise as Promises[T];

		return promise;
	}, [item, key, promises, update, cache]);

	useEffect(() => {
		if (item.status !== 'idle') {
			return;
		}

		load();
	}, [item, load]);

	useEffect(() => {
		const broadcast = new BroadcastChannel('update-cache');

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
	}, [item, update, key]);

	return {...item, load};
}

function useStaleCache() {
	const broadcast = new BroadcastChannel('update-cache');

	return (key: CacheKey) => {
		broadcast.postMessage({key, type: 'staleCache'});
	};
}

export default CacheContextProvider;

export {CacheContext, useCache, useStaleCache};
