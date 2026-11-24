import ClayAutocomplete from '@clayui/autocomplete';
import getCN from 'classnames';
import React, {useEffect, useState} from 'react';
import {DocumentNode, useQuery} from '@apollo/client';

import {NetworkState} from 'shared/util/constants';
import {useDebounce} from 'shared/hooks/useDebounce';

import {
	DEFAULT_PAGE_SIZE,
	PaginatedDataSourceFn,
	usePaginatedRequest,
} from 'shared/hooks/usePaginatedRequest';

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
	'aria-invalid'?: boolean;
	'className'?: string;

	/**
	 * Extra request identity for `paginatedDataSourceFn`, for a caller whose data
	 * source depends on something other than the typed query (e.g. which
	 * field the values are read from). The suggestions are refetched
	 * whenever it changes; without it, only the query invalidates them.
	 */
	'dataSourceKey'?: string;
	'disabled'?: boolean;
	'graphqlQuery'?: GraphqlQuery;
	'pageSize'?: number;
	'paginatedDataSourceFn'?: PaginatedDataSourceFn;
	'placeholder'?: string;
	'testId'?: string;
	'value': string;
	'onBlur'?: React.FocusEventHandler<HTMLInputElement>;
	'onChange'?: (value: string) => void;
}

const DEBOUNCE_DELAY = 250;

const PaginatedAutocompleteInput: React.FC<
	IAutocompleteProps &
		Required<Pick<IAutocompleteProps, 'paginatedDataSourceFn'>>
> = ({
	dataSourceKey,
	pageSize = DEFAULT_PAGE_SIZE,
	paginatedDataSourceFn,
	value,
	...otherProps
}) => {
	const {items, networkState, onLoadMore} = usePaginatedRequest({
		dataSourceFn: paginatedDataSourceFn,
		dataSourceKey,
		debounceDelay: DEBOUNCE_DELAY,
		pageSize,
		query: value,
	});

	return (
		<BaseAutocomplete
			{...otherProps}
			items={items}
			loadingState={networkState}
			onLoadMore={onLoadMore}
			value={value}
		/>
	);
};

const GraphqlAutocompleteInput: React.FC<
	IAutocompleteProps & Required<Pick<IAutocompleteProps, 'graphqlQuery'>>
> = ({graphqlQuery, value, ...otherProps}) => {
	const [networkState, setNetworkState] = useState(NetworkState.Unused);

	const {
		mapResultsToProps = (value) => value,
		query,
		variables,
	} = graphqlQuery;

	const debouncedInputValue = useDebounce(value, DEBOUNCE_DELAY);

	const response = useQuery(query, {
		fetchPolicy: 'network-only',
		variables: {
			...variables,
			keywords: debouncedInputValue,
		},
	});

	const {data: items = [], loading} = {
		...response,
		...mapResultsToProps(response.data),
	};

	useEffect(() => {
		setNetworkState(loading ? NetworkState.Loading : NetworkState.Unused);
	}, [loading]);

	return (
		<BaseAutocomplete
			{...otherProps}
			items={items as string[]}
			loadingState={networkState}
			value={value}
		/>
	);
};

interface IBaseAutocompleteProps
	extends Pick<
		IAutocompleteProps,
		| 'aria-invalid'
		| 'className'
		| 'disabled'
		| 'onBlur'
		| 'onChange'
		| 'placeholder'
		| 'value'
	> {
	items: string[];
	loadingState: NetworkState;
	onLoadMore?: () => Promise<any> | null;
}

const BaseAutocomplete: React.FC<IBaseAutocompleteProps> = ({
	'aria-invalid': ariaInvalid,
	className,
	disabled = false,
	items,
	loadingState,
	onBlur,
	onChange,
	onLoadMore,
	placeholder,
	value,
}) => (
	<ClayAutocomplete
		allowsCustomValue
		aria-invalid={ariaInvalid}
		aria-labelledby="clay-autocomplete-label-1"
		className={getCN('select-input-root', className)}
		data-testid="attribute-value-string-input"
		disabled={disabled}
		id="clay-autocomplete-1"
		items={items}
		loadingState={loadingState}
		menuTrigger="focus"
		messages={{
			loading: Liferay.Language.get('loading'),
			notFound: Liferay.Language.get('no-results-were-found'),
		}}
		onBlur={onBlur}
		onChange={onChange}
		onLoadMore={onLoadMore}
		placeholder={placeholder}
		value={value}
	>
		{(item) => (
			<ClayAutocomplete.Item key={item}>{item}</ClayAutocomplete.Item>
		)}
	</ClayAutocomplete>
);

const AutocompleteInput: React.FC<IAutocompleteProps> = ({
	graphqlQuery,
	paginatedDataSourceFn,
	...otherProps
}) => {
	if (paginatedDataSourceFn) {
		return (
			<PaginatedAutocompleteInput
				{...otherProps}
				paginatedDataSourceFn={paginatedDataSourceFn}
			/>
		);
	}

	if (graphqlQuery) {
		return (
			<GraphqlAutocompleteInput
				{...otherProps}
				graphqlQuery={graphqlQuery}
			/>
		);
	}

	return (
		<BaseAutocomplete
			{...otherProps}
			items={[]}
			loadingState={NetworkState.Unused}
		/>
	);
};

export default AutocompleteInput;
