import {List} from 'immutable';
import {PaginatedSource} from '../criterion-types/RemoteCriterionType';
import {Property} from 'shared/util/records';
import {useDebounce} from 'shared/hooks/useDebounce';
import {useEffect, useState} from 'react';

export const SEARCH_DEBOUNCE_DELAY = 300;

interface IUsePaginatedPropertiesParams<T> {
	channelId: string;
	enabled?: boolean;
	groupId: string;
	keywords?: string;
	pageSize: number;
	source?: PaginatedSource<T>;
}

export function usePaginatedProperties<T>({
	channelId,
	enabled = true,
	groupId,
	keywords = '',
	pageSize,
	source,
}: IUsePaginatedPropertiesParams<T>) {
	const [items, setItems] = useState<List<Property>>(List());
	const [loading, setLoading] = useState(false);
	const [totalCount, setTotalCount] = useState(0);

	// `keywords` and `page` move together so that a new search resets to the
	// first page in a single update. Kept apart, the fetch below would fire
	// once for the stale page before the reset landed.

	const [query, setQuery] = useState<{keywords: string; page: number}>({
		keywords,
		page: 1,
	});

	const debouncedKeywords = useDebounce(keywords, SEARCH_DEBOUNCE_DELAY);

	useEffect(() => {
		setQuery((currentQuery) =>
			currentQuery.keywords === debouncedKeywords
				? currentQuery
				: {keywords: debouncedKeywords, page: 1}
		);
	}, [debouncedKeywords]);

	// Adjusted while rendering rather than in an effect: an effect would run
	// after the fetch below had already read the previous section's page and
	// requested it against the new source.

	const [previousSource, setPreviousSource] = useState(source);

	if (previousSource !== source) {
		setPreviousSource(source);
		setItems(List());
		setTotalCount(0);
		setQuery((currentQuery) =>
			currentQuery.page === 1 ? currentQuery : {...currentQuery, page: 1}
		);
	}

	useEffect(() => {
		if (!enabled || !source) {
			return;
		}

		let cancelled = false;

		setLoading(true);

		source
			.api({
				channelId,
				groupId,
				keywords: query.keywords,
				page: query.page,
				pageSize,
			})
			.then((result) => {
				if (cancelled) {
					return;
				}

				setItems(List((result.items ?? []).map(source.createProperty)));
				setTotalCount(result.totalCount ?? 0);
			})
			.finally(() => {
				if (!cancelled) {
					setLoading(false);
				}
			});

		return () => {
			cancelled = true;
		};
	}, [channelId, enabled, groupId, pageSize, query, source]);

	const setPage = (page: number) =>
		setQuery((currentQuery) => ({...currentQuery, page}));

	return {
		items,
		keywords: query.keywords,
		loading,
		page: query.page,
		setPage,
		totalPages: Math.ceil(totalCount / pageSize),
	};
}
