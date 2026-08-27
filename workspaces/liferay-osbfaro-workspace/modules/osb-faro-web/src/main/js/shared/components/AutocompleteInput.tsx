import ClayAutocomplete from '@clayui/autocomplete';
import getCN from 'classnames';
import React, {useEffect, useState} from 'react';
import {DocumentNode, useQuery} from '@apollo/client';

import {NetworkState} from 'shared/util/constants';
import {useDebounce} from 'shared/hooks/useDebounce';

import {useRequest} from 'shared/hooks/useRequest';

type TMappedData = {
	data: string[];
	total: number;
};

type GraphqlQuery = {
	mapResultsToProps: (data: any) => TMappedData;
	variables: object;
	query: DocumentNode;
};

interface IAutocompleteProps {
	className?: string;
	dataSourceFn?: (query?: string) => Promise<string[]>;

	/**
	 * Extra request identity for `dataSourceFn`, for a caller whose data
	 * source depends on something other than the typed query (e.g. which
	 * field the values are read from). The suggestions are refetched
	 * whenever it changes; without it, only the query invalidates them.
	 */
	dataSourceKey?: string;
	disabled?: boolean;
	graphqlQuery?: GraphqlQuery;
	placeholder?: string;
	testId?: string;
	value: string;
	onBlur?: React.FocusEventHandler<HTMLInputElement>;
	onChange?: (value: string) => void;
}

const DEBOUNCE_DELAY = 250;

const AutocompleteInput: React.FC<IAutocompleteProps> = ({
	className,
	dataSourceFn,
	dataSourceKey,
	disabled = false,
	graphqlQuery,
	onBlur,
	onChange,
	placeholder,
	value,
}) => {
	const [networkState, setNetworkState] = useState(NetworkState.Unused);

	let response;

	if (graphqlQuery) {
		const {
			mapResultsToProps = (value) => value,
			query,
			variables,
		} = graphqlQuery;
		const debouncedInputValue = useDebounce(value, DEBOUNCE_DELAY);

		response = useQuery(query, {
			fetchPolicy: 'network-only',
			variables: {
				...variables,
				keywords: debouncedInputValue,
			},
		});

		response = {
			...response,
			...mapResultsToProps(response.data),
		};
	}
	else {
		response = useRequest({
			dataSourceFn: ({value}) => dataSourceFn?.(value),
			debounceDelay: DEBOUNCE_DELAY,
			initialState: {
				data: [],
				error: false,
				loading: false,
			},
			variables: {dataSourceKey, value},
		});
	}

	const {data: items = [], loading} = response;

	useEffect(() => {
		setNetworkState(loading ? NetworkState.Loading : NetworkState.Unused);
	}, [loading]);

	return (
		<ClayAutocomplete
			allowsCustomValue
			aria-labelledby="clay-autocomplete-label-1"
			className={getCN('select-input-root', className)}
			data-testid="attribute-value-string-input"
			disabled={disabled}
			id="clay-autocomplete-1"
			items={items as string[]}
			loadingState={networkState}
			menuTrigger="focus"
			messages={{
				loading: Liferay.Language.get('loading'),
				notFound: Liferay.Language.get('no-results-were-found'),
			}}
			onBlur={onBlur}
			onChange={onChange}
			placeholder={placeholder}
			value={value}
		>
			{(item) => (
				<ClayAutocomplete.Item key={item}>{item}</ClayAutocomplete.Item>
			)}
		</ClayAutocomplete>
	);
};

export default AutocompleteInput;
