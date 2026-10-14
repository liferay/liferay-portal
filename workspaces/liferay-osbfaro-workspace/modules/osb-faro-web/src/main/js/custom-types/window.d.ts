import {ChannelPermissionTypes} from 'shared/util/constants';
import {compose} from 'redux';

export {};

declare global {
	interface Window {
		faroConstants: {
			channelPermissionTypes: {[key: string]: ChannelPermissionTypes};
			faroURL: string;
			pagination: {
				cur: number;
				delta: number;
				deltaValues: number[];
				orderAscending: string;
				orderDefault: string;
				orderDescending: string;
			};
			projectLocations: {[key: string]: string};
		};
		hbspt: {
			forms: {
				create: Function;
			};
		};
		jQuery: object;
		__REDUX_DEVTOOLS_EXTENSION_COMPOSE__: compose;
	}
}
