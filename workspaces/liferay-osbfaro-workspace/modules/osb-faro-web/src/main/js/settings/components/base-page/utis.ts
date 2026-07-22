import {IHistoryAdapter} from 'shared/hooks/useHistoryAdapter';

export function updateSearchParams(
	history: IHistoryAdapter,
	key: string,
	value: any
) {
	const params = new URLSearchParams(window.location.search);
	params.set(key, String(value));

	history.push({
		pathname: window.location.pathname,
		search: params.toString(),
	});
}
