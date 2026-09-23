/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	Dispatch,
	ReactNode,
	SetStateAction,
	createContext,
	useContext,
	useState,
} from 'react';

import {Option, Options, Picklist} from '../../common/types/Picklist';
import buildLocalizedValue from '../../common/utils/buildLocalizedValue';
import {DefaultLanguageLabels} from '../../common/utils/defaultLanguageLabels';
import getRandomId from '../utils/getRandomId';
import normalizeI18nValue from '../utils/normalizeI18nValue';

const noop = () => null;

const INITIAL_STATE: State = {
	defaultLanguageLabels: {labels: {}, locale: ''},
	deletedOptions: false,
	erc: '',
	id: null,
	name: {},
	options: new Map(),
	setDeletedOptions: noop,
	setErc: noop,
	setId: noop,
	setName: noop,
	setOptions: noop,
};

function getInitialState(defaultLanguageLabels: DefaultLanguageLabels): State {
	return {
		...INITIAL_STATE,
		defaultLanguageLabels,
		erc: getRandomId(),
		name: buildLocalizedValue({
			defaultLanguageLabels,
			key: 'untitled-picklist',
		}),
	};
}

export type State = {
	defaultLanguageLabels: DefaultLanguageLabels;
	deletedOptions: boolean;
	erc: string;
	id: number | null;
	name: Liferay.Language.LocalizedValue<string>;
	options: Options;
	setDeletedOptions: Dispatch<SetStateAction<boolean>>;
	setErc: Dispatch<SetStateAction<string>>;
	setId: Dispatch<SetStateAction<number | null>>;
	setName: Dispatch<SetStateAction<Liferay.Language.LocalizedValue<string>>>;
	setOptions: Dispatch<SetStateAction<Options>>;
};

const PicklistBuilderContext = createContext<State>(INITIAL_STATE);

export default function PicklistBuilderContextProvider({
	children,
	initialState,
}: {
	children: ReactNode;
	initialState: State;
}) {
	const {defaultLanguageLabels} = initialState;

	const [deletedOptions, setDeletedOptions] = useState<boolean>(false);
	const [erc, setErc] = useState<string>(initialState.erc);
	const [id, setId] = useState<number | null>(initialState.id);
	const [name, setName] = useState<Liferay.Language.LocalizedValue<string>>(
		initialState.name
	);
	const [options, setOptions] = useState<Options>(initialState.options);

	return (
		<PicklistBuilderContext.Provider
			value={{
				defaultLanguageLabels,
				deletedOptions,
				erc,
				id,
				name,
				options,
				setDeletedOptions,
				setErc,
				setId,
				setName,
				setOptions,
			}}
		>
			{children}
		</PicklistBuilderContext.Provider>
	);
}

const buildState = (
	picklist: Picklist,
	defaultLanguageLabels: DefaultLanguageLabels
): State => {
	if (!picklist) {
		return getInitialState(defaultLanguageLabels);
	}

	return {
		...getInitialState(defaultLanguageLabels),
		erc: picklist.externalReferenceCode,
		id: picklist.id,
		name: normalizeI18nValue(picklist.name_i18n),
		options: new Map(
			picklist.listTypeEntries.map((option) => [
				option.externalReferenceCode,
				{
					key: option.key,
					name: normalizeI18nValue(option.name_i18n),
				},
			])
		),
	};
};

const useAddOption = () => {
	const {setOptions} = useContext(PicklistBuilderContext);

	return ({erc, key, name}: Option) => {
		if (!erc || !key || !name) {
			return;
		}

		return setOptions((previousOptions) => {
			const options = new Map(previousOptions);

			options.set(erc, {key, name});

			return options;
		});
	};
};

const useDefaultLanguageLabels = () =>
	useContext(PicklistBuilderContext).defaultLanguageLabels;

const useDeletedOptions = () =>
	useContext(PicklistBuilderContext).deletedOptions;

const useErc = () => useContext(PicklistBuilderContext).erc;

const useId = () => useContext(PicklistBuilderContext).id;

const useName = () => useContext(PicklistBuilderContext).name;

const useSetErc = () => useContext(PicklistBuilderContext).setErc;

const useSetId = () => useContext(PicklistBuilderContext).setId;

const useSetName = () => useContext(PicklistBuilderContext).setName;

const useOptions = () => useContext(PicklistBuilderContext).options;

const useRemoveOptions = () => {
	const {setDeletedOptions, setOptions} = useContext(PicklistBuilderContext);

	return (ercs: string[]) => {
		setDeletedOptions(true);

		setOptions((options) => {
			const newOptions = new Map(options);

			ercs.forEach((erc) => newOptions.delete(erc));

			return newOptions;
		});
	};
};

export {
	INITIAL_STATE,
	PicklistBuilderContext,
	PicklistBuilderContextProvider,
	buildState,
	useAddOption,
	useDefaultLanguageLabels,
	useDeletedOptions,
	useErc,
	useId,
	useName,
	useOptions,
	useRemoveOptions,
	useSetErc,
	useSetId,
	useSetName,
};
