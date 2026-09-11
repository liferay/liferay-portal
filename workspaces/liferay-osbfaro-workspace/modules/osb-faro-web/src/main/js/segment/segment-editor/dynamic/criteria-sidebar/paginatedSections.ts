import {getRemoteCriterionTypeByPropertyKey} from '../criterion-types/registry';
import {PaginatedSource} from '../criterion-types/RemoteCriterionType';
import {searchTermPagination} from './searchTermPagination';

// Sections that get the sidebar's search-box-plus-pagination treatment
// without joining the `RemoteCriterionType` registry — see
// `searchTermPagination.ts` for why that registry doesn't fit here.

const PAGINATED_SIDEBAR_SECTIONS: Record<string, PaginatedSource> = {
	'search-term': searchTermPagination,
};

export const getPaginatedSection = (
	propertyKey: string | null
): PaginatedSource | undefined =>
	getRemoteCriterionTypeByPropertyKey(propertyKey) ??
	(propertyKey ? PAGINATED_SIDEBAR_SECTIONS[propertyKey] : undefined);
