export const actionTypes = {
	COLLAPSE_SIDEBAR: 'COLLAPSE_SIDEBAR',
};

/**
 * Collapses the whole sidebar, or one of its sections when `sectionKey` is
 * given.
 */

export function collapseSidebar(payload) {
	return {
		payload,
		type: actionTypes.COLLAPSE_SIDEBAR,
	};
}
