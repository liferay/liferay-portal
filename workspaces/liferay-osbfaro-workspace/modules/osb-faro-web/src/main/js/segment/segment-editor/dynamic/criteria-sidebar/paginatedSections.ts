import {getRemoteCriterionTypeByPropertyKey} from '../criterion-types/registry';
import {Property} from 'shared/util/records';
import {
	RemoteCriterionSearchParams,
	RemoteCriterionSearchResult,
} from '../criterion-types/RemoteCriterionType';
import {searchTermPagination} from './searchTermPagination';

export interface IPaginatedSidebarSection {
	api: (
		params: RemoteCriterionSearchParams
	) => Promise<RemoteCriterionSearchResult>;
	createProperty: (data: {id: string; name: string}) => Property;
}

// Sections that get the sidebar's search-box-plus-pagination treatment
// without joining the `RemoteCriterionType` registry — see
// `searchTermPagination.ts` for why that registry doesn't fit here.

const PAGINATED_SIDEBAR_SECTIONS: Record<string, IPaginatedSidebarSection> = {
	'search-term': searchTermPagination,
};

export const getPaginatedSection = (
	propertyKey: string | null
): IPaginatedSidebarSection | undefined =>
	getRemoteCriterionTypeByPropertyKey(propertyKey) ??
	(propertyKey ? PAGINATED_SIDEBAR_SECTIONS[propertyKey] : undefined);
