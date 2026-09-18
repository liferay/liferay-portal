export const actionTypes = {
	COLLAPSE_SIDEBAR: 'COLLAPSE_SIDEBAR',
	SET_SIDEBAR_SECTION_EXPANDED: 'SET_SIDEBAR_SECTION_EXPANDED',
};

export function collapseSidebar(payload) {
	return {
		payload,
		type: actionTypes.COLLAPSE_SIDEBAR,
	};
}

export function setSidebarSectionExpanded(payload) {
	return {
		payload,
		type: actionTypes.SET_SIDEBAR_SECTION_EXPANDED,
	};
}
