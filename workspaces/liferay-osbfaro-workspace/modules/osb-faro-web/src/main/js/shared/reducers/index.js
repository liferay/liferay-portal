import accounts from './accounts';
import alerts from './alerts';
import currentUser from './current-user';
import dataSources from './data-sources';
import distributions from './distributions';
import individuals from './individuals';
import interests from './interests';
import maintenanceSeen from './maintenance-seen';
import modals from './modals';
import normalizer from './normalizer';
import preferences from './preferences';
import projects from './projects';
import segments from './segments';
import sidebar from './sidebar';
import store from './store';
import users from './users';
import {combineReducers} from 'redux-immutable';
import {composeReducers} from 'redux-toolbox';

export default composeReducers(
	normalizer,
	store,
	combineReducers({
		accounts,
		alerts,
		currentUser,
		dataSources,
		distributions,
		individuals,
		interests,
		maintenanceSeen,
		modals,
		preferences,
		projects,
		segments,
		sidebar,
		users,
	})
);
