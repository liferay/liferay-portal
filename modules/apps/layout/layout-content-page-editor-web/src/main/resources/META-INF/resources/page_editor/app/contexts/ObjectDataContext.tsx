/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	Dispatch,
	ReactNode,
	SetStateAction,
	useCallback,
	useContext,
	useState,
} from 'react';

import {config} from '../config/index';
import FormService from '../services/FormService';
import {CACHE_KEYS} from '../utils/cache';
import useCache from '../utils/useCache';

type FieldType =
	| 'boolean'
	| 'date'
	| 'datetime'
	| 'decimal'
	| 'friendly-url'
	| 'integer'
	| 'long-text'
	| 'multiselect'
	| 'rich-text'
	| 'single-select'
	| 'text'
	| 'upload';

export type ObjectField = {
	key: string;
	label: string;
	localizable: boolean;
	name: string;
	required: boolean;
	type: FieldType;
};

export type ObjectFieldSet = {
	fields: ObjectFields;
	label: string;
	name: string;
	relationship: boolean;
};

export type ObjectFields = Array<ObjectField | ObjectFieldSet>;

type ObjectDataMap = Record<string, {fields: ObjectFields; label?: string}>;

const ObjectDataContext = React.createContext<{
	map: ObjectDataMap;
	setMap: Dispatch<SetStateAction<ObjectDataMap>>;
}>({
	map: {},
	setMap: () => {},
});

function ObjectDataContextProvider({children}: {children: ReactNode}) {
	const [map, setMap] = useState({});

	return (
		<ObjectDataContext.Provider value={{map, setMap}}>
			{children}
		</ObjectDataContext.Provider>
	);
}

type Props =
	| {classNameId: string; classTypeId: string; name?: never}
	| {classNameId?: never; classTypeId?: never; name: string};

function useObjectFields(props: Props) {
	const map = useContext(ObjectDataContext).map;

	const entry = map[buildKey(props)];

	if (entry) {
		return entry.fields;
	}

	return [];
}

function useObjectLabel(props: Props) {
	const map = useContext(ObjectDataContext).map;

	const entry = map[buildKey(props)];

	if (entry) {
		return entry.label;
	}

	return null;
}

function useSaveObjectFields({
	classNameId,
	classTypeId,
}: {
	classNameId: string;
	classTypeId: string;
}) {
	const {setMap} = useContext(ObjectDataContext);

	const fields = useCache({
		fetcher: () => FormService.getFormFields({classNameId, classTypeId}),
		key: [CACHE_KEYS.formFields, classNameId, classTypeId],
	});

	return useCallback(() => {
		if (!fields) {
			return;
		}

		const label = config.formTypes.find(
			({value}) => value === classNameId
		)?.label;

		setMap((currentMap) =>
			buildMap({classNameId, classTypeId, currentMap, fields, label})
		);
	}, [classNameId, classTypeId, fields, setMap]);
}

function buildKey(props: Props): string {
	if ('name' in props && props.name) {
		return props.name;
	}

	return `${props.classNameId}_${props.classTypeId}`;
}

function buildMap({
	currentMap,
	fields,
	label,
	...props
}: Props & {
	currentMap: ObjectDataMap;
	fields: ObjectFields;
	label?: string;
}): ObjectDataMap {
	let map = {...currentMap};

	const key = buildKey(props);

	if (!(key in map)) {
		map[key] = {fields, label};
	}

	for (const field of fields) {
		if ('fields' in field) {
			if (!(field.name in map)) {
				map = {
					...map,
					...buildMap({
						currentMap: map,
						fields: field.fields,
						label: field.label,
						name: field.name,
					}),
				};
			}
		}
	}

	return map;
}

export {
	ObjectDataContextProvider,
	useObjectFields,
	useObjectLabel,
	useSaveObjectFields,
};
