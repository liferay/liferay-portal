import TimeZone from './TimeZone';
import {List, Map, Record} from 'immutable';

interface IProject {
	accountKey: string;
	accountName: string;
	corpProjectName: string;
	corpProjectUuid: string;
	faroSubscription: Map<string, any>;
	friendlyURL: string;
	groupId: number;
	incidentReportEmailAddresses: List<string>;
	name: string;
	ownerEmailAddress: string;
	recommendationsEnabled: boolean;
	salesforceAccountId: string;
	serverLocation: string;
	state: string;
	stateStartDate: number;
	timeZone: TimeZone;
	userId: number;
}

export default class Project
	extends Record({
		accountKey: null,
		accountName: '',
		corpProjectName: '',
		corpProjectUuid: null,
		faroSubscription: Map(),
		friendlyURL: null,
		groupId: null,
		incidentReportEmailAddresses: List(),
		name: '',
		ownerEmailAddress: '',
		recommendationsEnabled: false,
		salesforceAccountId: null,
		serverLocation: null,
		state: null,
		stateStartDate: null,
		timeZone: new TimeZone(),
		userId: null,
	})
	implements IProject
{
	declare accountKey: string;
	declare accountName: string;
	declare corpProjectName: string;
	declare corpProjectUuid: string;
	declare faroSubscription: Map<string, any>;
	declare friendlyURL: string;
	declare groupId: number;
	declare incidentReportEmailAddresses: List<string>;
	declare name: string;
	declare ownerEmailAddress: string;
	declare recommendationsEnabled: boolean;
	declare salesforceAccountId: string;
	declare serverLocation: string;
	declare state: string;
	declare stateStartDate: number;
	declare timeZone: TimeZone;
	declare userId: number;

	constructor(props = {}) {
		super(props);
	}
}
