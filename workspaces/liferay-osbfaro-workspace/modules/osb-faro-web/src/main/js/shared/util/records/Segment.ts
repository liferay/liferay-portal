import {EntityTypes, SegmentCategories, SegmentTypes} from '../constants';
import {fromJS, Map, Record} from 'immutable';
import {SegmentActivationDetails} from 'segment/components/SegmentActivationCard';

interface ISegment {
	accountCount: number;
	activation: SegmentActivationDetails;
	activeIndividualCount: number;
	activitiesCount: number;
	anonymousIndividualCount: number;
	channelId: string;
	criteriaString?: string; // "filter" has been renamed to criteriaString to avoid clashing with ImmutableMaps filter method.
	dateCreated: number;
	dateModified: number;
	externalReferenceCode: string;
	id: string;
	includeAnonymousUsers: boolean;
	individualCount: number;
	knownIndividualCount: number;
	lastActivityDate: number;
	name: string;
	properties: Map<string, any>;
	referencedObjects?: Map<string, any>;
	segmentCategory: SegmentCategories | null;
	segmentType: SegmentTypes | null;
	sequential: boolean;
	state: string;
	status: string;
	type: EntityTypes.IndividualsSegment;
	userName: string;
}

export default class Segment
	extends Record({
		accountCount: 0,
		activation: null,
		activeIndividualCount: 0,
		activitiesCount: 0,
		anonymousIndividualCount: 0,
		channelId: null,
		criteriaString: '',
		dateCreated: null,
		dateModified: null,
		externalReferenceCode: '',
		id: '',
		includeAnonymousUsers: false,
		individualCount: 0,
		knownIndividualCount: 0,
		lastActivityDate: null,
		name: '',
		properties: Map(),
		referencedObjects: Map({
			assets: Map(),
			attributes: Map(),
			events: Map(),
			fieldMappings: Map(),
		}),
		segmentCategory: null,
		segmentType: null,
		sequential: false,
		state: '',
		status: null,
		type: EntityTypes.IndividualsSegment,
		userName: null,
	})
	implements ISegment
{
	declare accountCount: number;
	declare activation: SegmentActivationDetails;
	declare activeIndividualCount: number;
	declare activitiesCount: number;
	declare anonymousIndividualCount: number;
	declare channelId: string;
	declare criteriaString?: string;
	declare dateCreated: number;
	declare dateModified: number;
	declare externalReferenceCode: string;
	declare id: string;
	declare includeAnonymousUsers: boolean;
	declare individualCount: number;
	declare knownIndividualCount: number;
	declare lastActivityDate: number;
	declare name: string;
	declare properties: Map<string, any>;
	declare referencedObjects?: Map<string, any>;
	declare segmentCategory: SegmentCategories | null;
	declare segmentType: SegmentTypes | null;
	declare sequential: boolean;
	declare state: string;
	declare status: string;
	declare type: EntityTypes.IndividualsSegment;
	declare userName: string;

	constructor(props = {}) {
		super(fromJS(props));
	}
}
