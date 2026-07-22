import React from 'react';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';

/**
 * Injects a v5-`history`-shaped adapter (see `useHistoryAdapter`) so the
 * existing `withHistory` consumers keep calling `history.push(...)` unchanged.
 * Tech debt: consumers should migrate to `useNavigate` directly, after which
 * this HOC can be deleted.
 */
export default WrappedComponent => props => {
	const history = useHistoryAdapter();

	return <WrappedComponent history={history} {...props} />;
};
