/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	MutableRefObject,
	useCallback,
	useLayoutEffect,
	useMemo,
	useRef,
} from 'react';

import {EViewsActionTypes} from '../views/viewsReducer';
import {readConfigFromURL, writeConfigInURL} from './configInURL';
import {
	EConfigInURLBehavior,
	IConfigInURL,
	IConfigInURLGetter,
	IConfigInURLUpdaterThunk,
	IConfigReader,
	IConfigWriter,
} from './types';

type configWriters = Record<
	string,
	Partial<{
		[key in keyof IConfigInURL]: MutableRefObject<IConfigWriter<key>>;
	}>
>;

const CONFIG_WRITERS: configWriters = {};

function registerConfigWriter<K extends keyof IConfigInURL>({
	configWriterRef,
	id,
	key,
}: {
	configWriterRef: MutableRefObject<IConfigWriter<K>>;
	id: string;
	key: K;
}): void {
	if (!CONFIG_WRITERS[id]) {
		CONFIG_WRITERS[id] = {};
	}

	(
		CONFIG_WRITERS[id] as Record<
			keyof IConfigInURL,
			MutableRefObject<IConfigWriter<any>>
		>
	)[key] = configWriterRef;
}

function updateConfig({
	config,
	configInURLSettings,
	id,
}: {
	config: Partial<IConfigInURL>;
	configInURLSettings: EConfigInURLBehavior;
	id: string;
}) {
	const updatedConfig: Partial<IConfigInURL> = {};

	Object.keys(config).forEach((key: string) => {
		const configWriterRef = (
			CONFIG_WRITERS[id] as Record<
				keyof IConfigInURL,
				MutableRefObject<IConfigWriter<any>>
			>
		)[key as keyof IConfigInURL];

		updatedConfig[key as keyof IConfigInURL] = configWriterRef.current?.(
			config[key as keyof IConfigInURL]
		);
	});

	writeConfigInURL(id, updatedConfig, configInURLSettings);
}

export function useUpdateConfig({
	configInURLSettings,
	id,
}: {
	configInURLSettings: EConfigInURLBehavior;

	id: string;
}): Function {
	return useCallback(
		(config: Partial<IConfigInURL>) =>
			updateConfig({config, configInURLSettings, id}),
		[id, configInURLSettings]
	);
}

function useConfigInURL<K extends keyof IConfigInURL>({
	additionalStateDispatchers = [],
	configInURLSettings,
	configReader,
	configWriter = (value: IConfigInURL[K]) => value,
	id,
	stateDispatcher,
}: {
	additionalStateDispatchers?: {
		key: keyof IConfigInURL;
		type: EViewsActionTypes;
		value: any;
	}[];
	configInURLSettings: EConfigInURLBehavior;
	configReader: IConfigReader<K>;
	configWriter?: IConfigWriter<K>;
	id: string;
	stateDispatcher: {
		key: K;
		type: EViewsActionTypes;
	};
}): [IConfigInURLGetter<K>, IConfigInURLUpdaterThunk<K>] {
	const {key, type} = stateDispatcher;

	return [
		useGetter({configReader, id, key}),
		useUpdaterThunk({
			additionalStateDispatchers,
			configInURLSettings,
			configWriter,
			id,
			key,

			type,
		}),
	];
}

function useGetter<K extends keyof IConfigInURL>({
	configReader,
	id,
	key,
}: {
	configReader: IConfigReader<K>;

	id: string;
	key: K;
}): IConfigInURLGetter<K> {
	return useCallback((): IConfigInURL[K] | undefined => {
		const config: Partial<IConfigInURL> | null = readConfigFromURL(id);

		let value: IConfigInURL[K] | undefined = undefined;

		if (config && config[key]) {
			value = config[key] as IConfigInURL[K];
		}

		return configReader(value);
	}, [id, configReader, key]);
}

function useUpdaterThunk<K extends keyof IConfigInURL>({
	additionalStateDispatchers = [],

	configInURLSettings,
	configWriter = (value: IConfigInURL[K]) => value,
	id,
	key,
	type,
}: {
	additionalStateDispatchers?: {
		key: keyof IConfigInURL;
		type: EViewsActionTypes;
		value: any;
	}[];

	configInURLSettings: EConfigInURLBehavior;
	configWriter?: IConfigWriter<K>;
	id: string;
	key: K;
	type: EViewsActionTypes;
}): IConfigInURLUpdaterThunk<K> {
	const additionalStateDispatchersKey = JSON.stringify(
		additionalStateDispatchers
	);

	const memoizedAdditionalStateDispatchers: {
		key: keyof IConfigInURL;
		type: EViewsActionTypes;
		value: any;
	}[] = useMemo(
		() => JSON.parse(additionalStateDispatchersKey),
		[additionalStateDispatchersKey]
	);

	const configWriterRef = useRef(configWriter);

	useLayoutEffect(() => {
		configWriterRef.current = configWriter;
	});

	registerConfigWriter({configWriterRef, id, key});

	const updateConfig = useUpdateConfig({configInURLSettings, id});

	return useCallback(
		(value: IConfigInURL[K]) => {
			return (viewsDispatch: Function) => {
				const newConfig: Partial<IConfigInURL> = {
					[key]: value,
				};

				if (
					!memoizedAdditionalStateDispatchers ||
					!memoizedAdditionalStateDispatchers.length
				) {
					viewsDispatch({
						type,
						value,
					});
				}
				else {
					const stateUpdates: Array<{
						type: EViewsActionTypes;
						value: IConfigInURL[keyof IConfigInURL];
					}> = [];

					stateUpdates.push({
						type,
						value,
					});

					memoizedAdditionalStateDispatchers.forEach(
						(stateDispatcher) => {
							stateUpdates.push({
								type: stateDispatcher.type,
								value: stateDispatcher.value,
							});

							newConfig[stateDispatcher.key] =
								stateDispatcher.value;
						}
					);

					viewsDispatch({
						type: EViewsActionTypes.BATCH_UPDATE,
						value: stateUpdates,
					});
				}

				updateConfig(newConfig);
			};
		},
		[key, memoizedAdditionalStateDispatchers, type, updateConfig]
	);
}

export default useConfigInURL;
