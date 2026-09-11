import {ComponentType} from 'react';
import {CustomFunctionOperators, NotOperators} from '../utils/constants';
import {IDisplayComponentProps} from 'segment/components/criteria-card/types';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Property} from 'shared/util/records';

export interface PaginatedSourceParams {
	channelId: string;
	groupId: string;
	keywords?: string;
	page: number;
	pageSize: number;
}

export interface PaginatedSourceResult<T> {
	items: T[];
	totalCount: number;
}

/**
 * One page-at-a-time list behind a sidebar section. Implemented by the
 * remote criterion types below and by the sections that only need paging
 * (see `criteria-sidebar/paginatedSections.ts`).
 */
export interface PaginatedSource<T = {id: string; name: string}> {
	api: (params: PaginatedSourceParams) => Promise<PaginatedSourceResult<T>>;
	createProperty: (item: T) => Property;
}

export interface RemoteCriterionType
	extends PaginatedSource<{id: string; name: string}> {
	DisplayComponent: ComponentType<IDisplayComponentProps>;
	InputComponent: ComponentType<ISegmentEditorCustomInputBase>;
	idProperty: string;
	nameProperty: string;
	negativeOperator: NotOperators;
	operators: ReadonlySet<CustomFunctionOperators | NotOperators>;
	positiveOperator: CustomFunctionOperators;
	propertyKey: 'tag' | 'vocabulary';
	supportsCategories: boolean;
}
