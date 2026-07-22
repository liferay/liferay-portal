import ErrorPage from 'shared/pages/ErrorPage';
import React from 'react';

/**
 * Renders the 404 page. Kept as a thin alias for the remaining call sites that
 * render it as a not-found fallback; new code should render `ErrorPage` (or a
 * catch-all `<Route path="*">`) directly.
 */
export default () => <ErrorPage />;
