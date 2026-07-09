import React from 'react';
import {Attribute} from 'event-analysis/utils/types';
import {
	Conjunctions,
	CustomFunctionOperators,
	FunctionalOperators,
	NotOperators,
	RelationalOperators,
} from './constants';
import {CustomValue, Property} from 'shared/util/records';

export type Context = {
	lastNodeWasGroup?: boolean;
	oDataASTNode: ODataASTNode;
	prevConjunction?: string;
};

export type CriterionGroup = {
	conjunctionName: string;
	criteriaGroupId: string;
	items: (CriterionGroup | Criterion)[];
};

export type Criterion = {
	defaultValue?: string;
	operatorName?: Conjunctions &
		CustomFunctionOperators &
		FunctionalOperators &
		NotOperators &
		RelationalOperators;
	propertyName?: string;
	rowId?: string;
	touched?: boolean | object;
	type?: string;
	valid?: boolean | object;
	value?: any;
};

export type Criteria = Criterion | CriterionGroup;

export type AttributeFilterState = {
	attribute: boolean;
	attributeValue: boolean;
};

export type AttributeConjunctionChangeParams = {
	attribute?: Attribute;
	criterion: Criterion;
	touched: AttributeFilterState;
	valid: AttributeFilterState;
};

export type OnCriterionAdd = (index: number, criterion: Criterion) => void;

export type ODataASTNode = {
	next?: number;
	position?: number;
	raw?: string;
	type: string;
	value: any;
};

export type OnMove = (
	startGroupId: string,
	startIndex: number,
	destGroupId: string,
	destIndex: number,
	criterion: Criterion | CriterionGroup,
	replace?: boolean
) => void;

export type Operator = {
	key: string;
	label: string;
	name: string;
};

export interface ISegmentEditorInputBase {
	channelId?: string;
	className?: string;
	displayValue?: string;
	groupId?: string;
	id?: string;
	onChange: (params: Criterion | Criterion[]) => void;
	operatorRenderer: React.ElementType;
	property: Property;
	timeZoneId?: string;
	touched?: boolean | object;
	valid?: boolean | object;
	value: string | number | CustomValue;
}

export interface ISegmentEditorCustomInputBase extends ISegmentEditorInputBase {
	property: Property;
	value: CustomValue;
}
