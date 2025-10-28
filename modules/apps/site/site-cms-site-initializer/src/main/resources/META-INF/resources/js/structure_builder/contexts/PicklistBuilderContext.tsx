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
import getRandomId from '../utils/getRandomId';
import normalizeI18n from '../utils/normalizeI18n';

const noop = () => null;

const DEFAULT_PICKLIST_NAME = Liferay.Language.get('untitled-picklist');

const INITIAL_STATE = {
	deletedOptions: false,
	erc: getRandomId(),
	id: null,
	name: {
		[Liferay.ThemeDisplay.getDefaultLanguageId()]: DEFAULT_PICKLIST_NAME,
	},
	options: new Map(),
	setDeletedOptions: noop,
	setErc: noop,
	setId: noop,
	setName: noop,
	setOptions: noop,
};

export type State = {
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

const buildState = (picklist: Picklist): State => {
	if (!picklist) {
		return INITIAL_STATE;
	}

	return {
		...INITIAL_STATE,
		erc: picklist.externalReferenceCode,
		id: picklist.id,
		name: normalizeI18n(picklist.name_i18n),
		options: new Map(
			picklist.listTypeEntries.map((option) => [
				option.externalReferenceCode,
				{
					key: option.key,
					name: normalizeI18n(option.name_i18n),
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
