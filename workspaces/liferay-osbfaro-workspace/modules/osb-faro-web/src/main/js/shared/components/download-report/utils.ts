import moment from 'moment';
import {buildOrderByFields} from 'shared/util/pagination';
import {DEFAULT_DATE_FORMAT} from 'shared/util/date';
import {DEFAULT_RANGE_SELECTORS} from 'shared/hooks/useQueryRangeSelectors';
import {INDIVIDUALS} from 'shared/util/router';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';

export function formatDate(date: string | Date) {
	return moment(date).format(DEFAULT_DATE_FORMAT);
}

export enum CSVType {
	Asset = 'asset',
	Blog = 'blog',
	Document = 'document',
	Event = 'event',
	Form = 'form',
	Individual = 'individual',
	Journal = 'journal',
	Membership = 'membership',
	Page = 'page',
	SearchTerms = 'search-terms',
}

export function useDownloadCSV({
	assetId,
	assetType,
	filter,
	individualId,
	objectType,
	segmentId,
	type,
}: {
	assetId?: string;
	assetType?: string;
	filter?: string;
	individualId?: string;
	objectType?: string;
	segmentId?: string;
	type: CSVType;
}) {
	const {channelId, groupId, title} = useParams();

	return (
		rangeSelectors: RangeSelectors = DEFAULT_RANGE_SELECTORS as unknown as RangeSelectors,
		overrides: {filter?: string; query?: string} = {}
	) => {
		const searchParams = new URLSearchParams(location.search);

		const field = searchParams.get('field');
		const query = overrides.query ?? searchParams.get('query');
		const sortOrder = searchParams.get('sortOrder');
		const resolvedFilter = overrides.filter ?? filter;

		let url = `/o/faro/main/${groupId}/reports/export/csv/${type}?channelId=${channelId}`;

		if (rangeSelectors.rangeKey === RangeKeyTimeRanges.CustomRange) {
			url += '&rangeKey=CUSTOM';
			url += `&fromDate=${formatDate(rangeSelectors.rangeStart ?? '')}`;
			url += `&toDate=${formatDate(rangeSelectors.rangeEnd ?? '')}`;
		}
		else {
			url += `&rangeKey=${rangeSelectors.rangeKey}`;
		}

		const optionalParams = {
			assetId: assetId && encodeURIComponent(assetId),
			assetTitle: title,
			assetType,
			filter: resolvedFilter && encodeURIComponent(resolvedFilter),
			individualId,
			objectType,
			orderByFields:
				field && sortOrder
					? encodeURIComponent(
							JSON.stringify(
								buildOrderByFields(
									{field, sortOrder},
									INDIVIDUALS
								)
							)
						)
					: null,
			query,
			segmentId,
		};

		Object.entries(optionalParams).forEach(([key, value]) => {
			if (value) {
				url += `&${key}=${value}`;
			}
		});

		return url;
	};
}

export const useMutationObserver = () => {
	const [loadingCount, setLoadingCount] = useState(0);

	useEffect(() => {
		const observer = new MutationObserver(() => {
			const loadingElement = document.querySelectorAll(
				'.page-container .loading-animation'
			);

			setLoadingCount(loadingElement.length);
		});

		observer.observe(document.body, {
			attributes: true,
			characterData: true,
			childList: true,
			subtree: true,
		});

		return () => observer.disconnect();
	}, []);

	return {loadingCount};
};

export const MAX_CSV_ENTRIES = 10000;
