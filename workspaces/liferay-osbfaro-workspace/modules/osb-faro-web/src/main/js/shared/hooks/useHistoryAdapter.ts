import {Location, useLocation, useNavigate} from 'react-router-dom';
import {useMemo, useRef} from 'react';

export interface IHistoryAdapter {
	goBack: () => void;
	location: Location;
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

export function useHistoryAdapter(): IHistoryAdapter {
	const navigate = useNavigate();
	const locationRef = useRef<Location>();

	locationRef.current = useLocation();

	return useMemo<IHistoryAdapter>(
		() => ({
			goBack: () => navigate(-1),
			get location() {
				return locationRef.current as Location;
			},
			push: (to, state) => navigate(...toNavigateArgs(to, state, false)),
			replace: (to, state) =>
				navigate(...toNavigateArgs(to, state, true)),
		}),
		[navigate]
	);
}
