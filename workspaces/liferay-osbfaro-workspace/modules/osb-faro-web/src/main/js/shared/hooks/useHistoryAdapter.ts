import {useNavigate} from 'react-router-dom';
import {useMemo} from 'react';

export interface IHistoryAdapter {
	goBack: () => void;
	push: (to: any, state?: unknown) => void;
	replace: (to: any, state?: unknown) => void;
}

function toNavigateArgs(
	to: any,
	state: unknown,
	replace: boolean
): [any, {replace: boolean; state?: unknown}] {
	if (to && typeof to === 'object') {
		const {state: locationState, ...path} = to;

		return [path, {replace, state: locationState ?? state}];
	}

	return [to, {replace, state}];
}

/**
 * A v5-`history`-shaped adapter (`push`/`replace`/`goBack`) built on
 * `useNavigate`, with a stable identity that survives navigations. It
 * deliberately does NOT read `useLocation`: `useNavigate` is subscription-free
 * under the data router, so the ~18 imperative consumers (e.g. Toolbar,
 * Breadcrumbs) do not re-render on every navigation. Callers that need the
 * current location call `useLocation` themselves.
 */
export function useHistoryAdapter(): IHistoryAdapter {
	const navigate = useNavigate();

	return useMemo<IHistoryAdapter>(
		() => ({
			goBack: () => navigate(-1),
			push: (to, state) => navigate(...toNavigateArgs(to, state, false)),
			replace: (to, state) =>
				navigate(...toNavigateArgs(to, state, true)),
		}),
		[navigate]
	);
}
